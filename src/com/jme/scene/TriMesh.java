/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.bounding.OBBTree;
import com.jme.intersection.CollisionResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating triangles on every
 * three points.
 * 
 * @author Mark Powell
 * @version $Id: TriMesh.java,v 1.45 2005-10-17 16:34:01 Mojomonkey Exp $
 */
public class TriMesh extends Geometry implements Serializable {

    private static final long serialVersionUID = 2L;

    protected transient IntBuffer indexBuffer;

    protected int triangleQuantity = -1;

    /** This tree is only built on calls too updateCollisionTree. */
    private OBBTree collisionTree;

    /**
     * Empty Constructor to be used internally only.
     */
    public TriMesh() {
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public TriMesh(String name) {
        super(name);

    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object. Provided
     * are the attributes that make up the mesh all attributes may be null,
     * except for vertices and indices.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param vertices
     *            the vertices of the geometry.
     * @param normal
     *            the normals of the geometry.
     * @param color
     *            the colors of the geometry.
     * @param texture
     *            the texture coordinates of the mesh.
     * @param indices
     *            the indices of the vertex array.
     */
    public TriMesh(String name, FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, FloatBuffer texture, IntBuffer indices) {

        super(name, vertices, normal, color, texture);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        this.indexBuffer = indices;
        triangleQuantity = indices.capacity() / 3;
        LoggingSystem.getLogger().log(Level.INFO, "TriMesh created.");
    }

    /**
     * Recreates the geometric information of this TriMesh from scratch. The
     * index and vertex array must not be null, but the others may be. Every 3
     * indices define an index in the <code>vertices</code> array that
     * refrences a vertex of a triangle.
     * 
     * @param vertices
     *            The vertex information for this TriMesh.
     * @param normal
     *            The normal information for this TriMesh.
     * @param color
     *            The color information for this TriMesh.
     * @param texture
     *            The texture information for this TriMesh.
     * @param indices
     *            The index information for this TriMesh.
     */
    public void reconstruct(FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, FloatBuffer texture, IntBuffer indices) {

        super.reconstruct(vertices, normal, color, texture);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        this.indexBuffer = indices;
        triangleQuantity = indices.capacity() / 3;
    }

    /**
     * 
     * <code>getIndexAsBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

    /**
     * 
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>TriMesh</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices) {
        this.indexBuffer = indices;
        if (indices == null) triangleQuantity = 0;
        else triangleQuantity = indices.capacity() / 3;
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
        if (i < triangleQuantity && storage.length >= 3) {

            int iBase = 3 * i;
            storage[0] = indexBuffer.get(iBase++);
            storage[1] = indexBuffer.get(iBase++);
            storage[2] = indexBuffer.get(iBase);
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
        // System.out.println(i + ", " + triangleQuantity);
        if (i < triangleQuantity && i >= 0) {
            int iBase = 3 * i;
            for (int x = 0; x < 3; x++) {
                vertices[x] = new Vector3f();   // we could reuse existing, but it may affect current users.
                BufferUtils.populateFromBuffer(vertices[x], vertBuf, indexBuffer.get(iBase++));
            }
        }
    }

    /**
     * Returns the number of triangles this TriMesh contains.
     * 
     * @return The current number of triangles.
     */
    public int getTriangleQuantity() {
        return triangleQuantity;
    }
    
    public int getType() {
    	return (Spatial.GEOMETRY | Spatial.TRIMESH);
    }

    /**
     * <code>draw</code> calls super to set the render state then passes
     * itself to the renderer.
     * 
     * LOGIC: 1. If we're not RenderQueue calling draw goto 2, if we are, goto 3
     * 2. If we are supposed to use queue, add to queue and RETURN, else 3 3.
     * call super draw 4. tell renderer to draw me.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
                return;
        }
        super.draw(r);
        r.draw(this);
    }

    /**
     * Clears the buffers of this TriMesh. The buffers include its indexBuffer only.
     */
    public void clearBuffers() {
        super.clearBuffers();
        indexBuffer = null;
    }

    /**
     * This function creates a collision tree from the TriMesh's current
     * information. If the information changes, the tree needs to be updated.
     */
    public void updateCollisionTree() {
        if (collisionTree == null)
            collisionTree = new OBBTree();
        collisionTree.construct(this);
    }

    /**
     * determines if a collision between this trimesh and a given spatial occurs
     * if it has true is returned, otherwise false is returned.
     * 
     */
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        if (this == scene || !isCollidable || !scene.isCollidable()) {
            return false;
        }
        if (getWorldBound().intersects(scene.getWorldBound())) {
            if ((scene.getType() & Spatial.NODE) != 0) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    if (hasCollision(parent.getChild(i), checkTriangles)) {
                        return true;
                    }
                }

                return false;
            } else {
                if (!checkTriangles) {
                    return true;
                } else {
                    return hasTriangleCollision((TriMesh) scene);
                }
            }
        } else {
            return false;
        }
    }

    /**
     * determines if this TriMesh has made contact with the give scene. The
     * scene is recursively transversed until a trimesh is found, at which time
     * the two trimesh OBBTrees are then compared to find the triangles that
     * hit.
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        if (this == scene || !isCollidable || !scene.isCollidable()) {
            return;
        }

        if (getWorldBound().intersects(scene.getWorldBound())) {
        	if ((scene.getType() & Spatial.NODE) != 0) {
                Node parent = (Node) scene;
                for (int i = 0; i < parent.getQuantity(); i++) {
                    findCollisions(parent.getChild(i), results);
                }
            } else {
                results.addCollision(this, (Geometry) scene);
            }
        }
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck) {
        if (collisionTree == null || toCheck.collisionTree == null || !isCollidable || !toCheck.isCollidable())
            return false;
        else {
            collisionTree.bounds.transform(worldRotation, worldTranslation,
                    worldScale, collisionTree.worldBounds);
            return collisionTree.intersect(toCheck.collisionTree);
        }
    }

    /**
     * This function finds all intersections between this trimesh and the
     * checking one. The intersections are stored as Integer objects of Triangle
     * indexes in each of the parameters.
     * 
     * @param toCheck
     *            The TriMesh to check.
     * @param thisIndex
     *            The array of triangle indexes intersecting in this mesh.
     * @param otherIndex
     *            The array of triangle indexes intersecting in the given mesh.
     */
    public void findTriangleCollision(TriMesh toCheck, ArrayList thisIndex,
            ArrayList otherIndex) {
        if (collisionTree == null || toCheck.collisionTree == null)
            return;
        else {
            collisionTree.bounds.transform(worldRotation, worldTranslation,
                    worldScale, collisionTree.worldBounds);
            collisionTree.intersect(toCheck.collisionTree, thisIndex,
                    otherIndex);
        }
    }

    /**
     * 
     * <code>findTrianglePick</code> determines the triangles of this trimesh
     * that are being touched by the ray. The indices of the triangles are
     * stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test.
     * @param results
     *            the indices to the triangles.
     */
    public void findTrianglePick(Ray toTest, ArrayList results) {
        if (worldBound == null || !isCollidable) {
            return;
        }
        if (worldBound.intersects(toTest)) {
            if (collisionTree == null) {
                updateCollisionTree();
            }
            collisionTree.bounds.transform(worldRotation, worldTranslation,
                    worldScale, collisionTree.worldBounds);
            collisionTree.intersect(toTest, results);
        }
    }

    /**
     * sets the attributes of this TriMesh into a given spatial. What is to be
     * stored is contained in the properties parameter.
     * 
     * @param store
     *            the Spatial to clone to.
     * @param properties
     *            the CloneCreator object that defines what is to be cloned.
     */
    public Spatial putClone(Spatial store, CloneCreator properties) {
        TriMesh toStore;
        if (store == null) {
            toStore = new TriMesh(this.getName() + "copy");
        } else {
            toStore = (TriMesh) store;
        }
        super.putClone(toStore, properties);


		if (properties.isSet("indices")) {
			toStore.setIndexBuffer(indexBuffer);
		} else {
		    if (indexBuffer != null) {
			    toStore.setIndexBuffer(BufferUtils.createIntBuffer(indexBuffer.capacity()));
			    toStore.indexBuffer.rewind();
			    indexBuffer.rewind();
			    toStore.indexBuffer.put(indexBuffer);
			    toStore.setIndexBuffer(toStore.indexBuffer); // pick up triangleQuantity
		    } else toStore.setIndexBuffer(null);
		}

        if (properties.isSet("obbtree")) {
            toStore.collisionTree = this.collisionTree;
        }

        return toStore;
    }

    /**
     * Return this mesh object as triangles. Every 3 vertices returned compose
     * single triangle. Vertices are returned by reference for efficiency, so it
     * is required that they won't be modified by caller.
     * 
     * @return view of current mesh as group of triangle vertices
     */
    public Vector3f[] getMeshAsTriangles() {
        Vector3f[] vertex = BufferUtils.getVector3Array(vertBuf); // FIXME: UGLY if done often!
        Vector3f[] triangles = new Vector3f[indexBuffer.capacity()];
        indexBuffer.rewind();
        for (int i = 0; i < triangles.length; i++) {
            triangles[i] = vertex[indexBuffer.get(i)];
        }
        return triangles;
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
        if (indexBuffer == null)
            s.writeInt(0);
        else {
            s.writeInt(indexBuffer.capacity());
            indexBuffer.rewind();
            for (int x = 0, len = indexBuffer.capacity(); x < len; x++)
                s.writeInt(indexBuffer.get());
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
}