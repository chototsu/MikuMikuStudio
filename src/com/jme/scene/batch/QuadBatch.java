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

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.SceneElement;
import com.jme.system.JmeException;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * QuadBatch provides an extension of GeomBatch adding the capabilities for quad
 * indices. These batch elements are usually contained in the BatchMesh
 * object, while Geometry contains GeomBatch elements.
 * <br>
 * Unlike TriangleBatch is does not (yet) have OBBTree collision support.
 * 
 * @author Tijl Houtbeckers
 * @version $Id: QuadBatch.java,v 1.2 2007/03/06 15:15:11 nca Exp $
 *  
 */
public class QuadBatch extends GeomBatch implements Serializable, Savable {

	private static final long serialVersionUID = 8486155454487030827L;

	public static final int QUADS = 1;

    public static final int QUAD_STRIP = 2;

	protected transient IntBuffer indexBuffer;
	
//    private OBBTree collisionTree;

    private static Vector3f[] quads;

    private int mode = QUADS;

    private int quadQuantity;
    
    /**
     * Default constructor that creates a new TriangleBatch.
     *
     */
    public QuadBatch() {
    	super();
    }
    
    public void setMode(int mode) {
    	this.mode = mode;
    }
    
    public int getMode() {
    	return mode;
    }

	public static Vector3f[] getQuads() {
		return quads;
	}

	public static void setQuads(Vector3f[] quads) {
		QuadBatch.quads = quads;
	}

//	public OBBTree getCollisionTree() {
//		return collisionTree;
//	}
//
//	public void setCollisionTree(OBBTree collisionTree) {
//		this.collisionTree = collisionTree;
//	}

	public IntBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setIndexBuffer(IntBuffer indices) {
		this.indexBuffer = indices;
        recalcQuadQuantity();
	}

	protected void recalcQuadQuantity() {
        if (indexBuffer == null) {
            quadQuantity = 0;
            return;
        }
        
        switch (mode) {
            case QuadBatch.QUADS:
                quadQuantity = indexBuffer.limit() / 4;
                break;
            case QuadBatch.QUAD_STRIP:
            	quadQuantity = indexBuffer.limit() / 2 - 1;
                break;
        }
    }

    public int getQuadCount() {
		return quadQuantity;
	}

	public void setQuadQuantity(int quadQuantity) {
		this.quadQuantity = quadQuantity;
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
            s.writeInt(getIndexBuffer().limit());
            getIndexBuffer().rewind();
            for (int x = 0, len = getIndexBuffer().limit(); x < len; x++)
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
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(indexBuffer, "indexBuffer", null);
        capsule.write(mode, "mode", QUADS);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        indexBuffer = capsule.readIntBuffer("indexBuffer", null);
        recalcQuadQuantity();
        mode = capsule.readInt("mode", QUADS);
    }

    public int getType() {
        return SceneElement.GEOMBATCH | SceneElement.QUADBATCH;
    }    

    public void draw(Renderer r) {
        if(!isEnabled()) {
            return;
        }
        
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }

        super.draw(r);
        r.draw(this);
    }

    /**
     * Stores in the <code>storage</code> array the indices of quad
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<4</code>, then nothing happens
     * 
     * @param i
     *            The index of the quad to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getQuad(int i, int[] storage) {
        if (i < getQuadCount() && storage.length >= 4) {
            IntBuffer indices = getIndexBuffer();
            storage[0] = indices.get(getVertIndex(i, 0));
            storage[1] = indices.get(getVertIndex(i, 1));
            storage[2] = indices.get(getVertIndex(i, 2));
            storage[3] = indices.get(getVertIndex(i, 3));

        }
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of quad
     * <code>i</code>. If <code>i</code> is an invalid quad index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getQuad(int i, Vector3f[] vertices) {
        if (i < getQuadCount() && i >= 0) {
            for (int x = 0; x < 4; x++) {
                if (vertices[x] == null)
                    vertices[x] = new Vector3f();
                
                BufferUtils.populateFromBuffer(vertices[x], getVertexBuffer(),
                        getIndexBuffer().get(getVertIndex(i, x)));
            }
        }
    }

    /**
     * Return this mesh object as quads. Every 4 vertices returned compose a
     * single quad.
     * 
     * @param verts
     *            a storage array to place the results in
     * @return view of current mesh as group of quad vertices
     */
    public Vector3f[] getMeshAsQuadsVertices(Vector3f[] verts) {
        int maxCount = getQuadCount() * 4;
        if (verts == null
                || verts.length != maxCount)
            verts = new Vector3f[maxCount];
        getIndexBuffer().rewind();
        for (int i = 0; i < maxCount; i++) {
            if (verts[i] == null)
                verts[i] = new Vector3f();
            int index = getVertIndex(i/4, i%4);
            BufferUtils.populateFromBuffer(verts[i], getVertexBuffer(),
                    getIndexBuffer().get(index));
        }
        return verts;
    }
    
    protected int getVertIndex(int quad, int point) {
        switch (mode) {
            case QUADS:
                return quad * 4 + point;
            case QUAD_STRIP:
                return quad * 2 + point;
            default:
                throw new JmeException("mode is set to invalid type: "+mode);
        }
    }
    
    public int getMaxIndex() {
        if (indexBuffer == null) return -1;
        
        switch (mode) {
            case QuadBatch.QUADS:
                return quadQuantity * 4;
            case QuadBatch.QUAD_STRIP:
                return 2 + (quadQuantity * 2);
        }
        
        return 0;
    }    

    // TODO: possibly support OBB by "cutting quads in half" to provide triangles?
    //    public Triangle[] getMeshAsTriangles(Triangle[] tris) {
    //    }
}

	

