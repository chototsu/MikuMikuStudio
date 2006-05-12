/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene.batch;

import java.io.IOException;
import java.io.Serializable;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jme.bounding.OBBTree;
import com.jme.intersection.CollisionResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * TriangleBatch provides an extension of GeomBatch adding the capabilities for
 * triangle indices and OBBTree collisions. These batch elements are usually contained
 * in the TriMesh object, while Geometry contains GeomBatch elements.
 * @author Mark Powell
 *
 */
public class TriangleBatch extends GeomBatch implements Serializable, Savable {
	private static final long serialVersionUID = 1277007053054883892L;

    public static final int TRIANGLES = 1;

    public static final int TRIANGLE_STRIP = 2;

    public static final int TRIANGLE_FAN = 3;

    public static final int QUADS = 4;

    public static final int QUAD_STRIP = 5;
	
	protected transient IntBuffer indexBuffer;
	
    private OBBTree collisionTree;

    private static Vector3f[] triangles;

    private int mode = TRIANGLES;

    private int triangleQuantity;
    
    /**
     * Default constructor that creates a new TriangleBatch.
     *
     */
    public TriangleBatch() {
    	super();
    }
    
    public void setMode(int mode) {
    	this.mode = mode;
    }
    
    public int getMode() {
    	return mode;
    }

	public static Vector3f[] getTriangles() {
		return triangles;
	}

	public static void setTriangles(Vector3f[] triangles) {
		TriangleBatch.triangles = triangles;
	}

	public OBBTree getCollisionTree() {
		return collisionTree;
	}

	public void setCollisionTree(OBBTree collisionTree) {
		this.collisionTree = collisionTree;
	}

	public IntBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setIndexBuffer(IntBuffer indices) {
		this.indexBuffer = indices;
        if (indices == null) triangleQuantity = 0;
        else triangleQuantity = indices.capacity() / 3;
	}

	public int getTriangleCount() {
		return triangleQuantity;
	}

	public void setTriangleQuantity(int triangleQuantity) {
		this.triangleQuantity = triangleQuantity;
	}
    
    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getIndexBuffer() == null)
            s.writeInt(0);
        else {
            s.writeInt(getIndexBuffer().capacity());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().capacity(); x < len; x++)
                s.writeInt(getIndexBuffer().get());
        }
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        int len = s.readInt();
        if (len == 0) {
            setIndexBuffer(null);
        } else {
            IntBuffer buf = BufferUtils.createIntBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readInt());
            setIndexBuffer(buf);            
        }
    }

    public Vector3f randomPointOnTriangles(Vector3f fill, Vector3f work) {
        if (getVertexBuffer() == null || getIndexBuffer() == null)
            return null;
        int tri = (int) (FastMath.nextRandomFloat() * getTriangleCount());
        int pntA = getIndexBuffer().get(tri*3);
        int pntB = getIndexBuffer().get((tri*3)+1);
        int pntC = getIndexBuffer().get((tri*3)+2);
        
        float b = FastMath.nextRandomFloat();
        float c = FastMath.nextRandomFloat();
        
        if (b + c > 1) {
            b = 1 - b;
            c = 1 - c;
        }
        
        float a = 1 - b - c;

        if (fill == null) fill = new Vector3f();

        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntA);
        work.multLocal(a);
        fill.set(work);
        
        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntB);
        work.multLocal(b);
        fill.addLocal(work);
        
        BufferUtils.populateFromBuffer(work, getVertexBuffer(), pntC);
        work.multLocal(c);
        fill.addLocal(work);
        
        parentGeom.localToWorld(fill, fill);

        return fill;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(indexBuffer, "indexBuffer", null);
        capsule.write(mode, "mode", TRIANGLES);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
        if (indexBuffer == null) triangleQuantity = 0;
        else triangleQuantity = indexBuffer.capacity() / 3;
        mode = capsule.readInt("mode", TRIANGLES);
    }

    public void findCollisions(Spatial scene, CollisionResults results) {
        // TODO Auto-generated method stub
        
    }

    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getType() {
        return (SceneElement.GEOMETRY | SceneElement.GEOMBATCH | SceneElement.TRIANGLEBATCH);
    }
    

    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        super.draw(r);
        r.draw(this);
    }

    /**
     * Stores in the <code>storage</code> array the indices of triangle
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<3</code>, then nothing happens
     * 
     * @param i
     *            The index of the triangle to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getTriangle(int i, int[] storage) {
        if (i < getTriangleCount() && storage.length >= 3) {

            int iBase = 3 * i;
            IntBuffer indices = getIndexBuffer();
            storage[0] = indices.get(iBase++);
            storage[1] = indices.get(iBase++);
            storage[2] = indices.get(iBase);
        }
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle
     * <code>i</code>. If <code>i</code> is an invalid triangle index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getTriangle(int i, Vector3f[] vertices) {
        if (i < getTriangleCount() && i >= 0) {
            int iBase = 3 * i;
            for (int x = 0; x < 3; x++) {
                vertices[x] = new Vector3f(); // we could reuse existing, but
                                                // it may affect current users.
                BufferUtils.populateFromBuffer(vertices[x], getVertexBuffer(),
                        getIndexBuffer().get(iBase++));
            }
        }
    }

    /**
     * <code>findTrianglePick</code> determines the triangles of this trimesh
     * that are being touched by the ray. The indices of the triangles are
     * stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test.
     * @param results
     *            the indices to the triangles.
     */
    public void findTrianglePick(Ray toTest, ArrayList<Integer> results) {
        if (worldBound == null || !isCollidable) {
            return;
        }

        if (worldBound.intersects(toTest)) {
            if (getCollisionTree() == null) {
                getCollisionTree().construct(parentGeom.getBatchIndex(this), (TriMesh)parentGeom, true);
            }
            getCollisionTree().bounds.transform(
                    parentGeom.getWorldRotation(), parentGeom.getWorldTranslation(), parentGeom.getWorldScale(),
                    getCollisionTree().worldBounds);
            getCollisionTree().intersect(toTest, results);
        }
    }
}
