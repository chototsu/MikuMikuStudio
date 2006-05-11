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

package com.jme.scene;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.bounding.OBBTree;
import com.jme.intersection.CollisionResults;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
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
 * @version $Id: TriMesh.java,v 1.55 2006-05-11 19:39:20 nca Exp $
 */
public class TriMesh extends Geometry implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * Empty Constructor to be used internally only.
     */
    public TriMesh() {
        super();
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

        super(name);

        reconstruct(vertices, normal, color, texture);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        getBatch(0).setIndexBuffer(indices);
        getBatch(0).setTriangleQuantity(indices.capacity() / 3);
        LoggingSystem.getLogger().log(Level.INFO, "TriMesh created.");
    }

    protected void setupBatchList() {
        batchList = new ArrayList<GeomBatch>();
        TriangleBatch batch = new TriangleBatch();
        batch.parentGeom = this;
        batchList.add(batch);
        batchCount = 1;
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
        reconstruct(vertices, normal, color, texture, indices, 0);
    }

    public void reconstruct(FloatBuffer vertices, FloatBuffer normal,
            FloatBuffer color, FloatBuffer texture, IntBuffer indices,
            int batchIndex) {

        super.reconstruct(vertices, normal, color, texture, batchIndex);

        if (null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Indices may not be" + " null.");
            throw new JmeException("Indices may not be null.");
        }
        getBatch(batchIndex).setIndexBuffer(indices);
        getBatch(batchIndex)
                .setTriangleQuantity(indices.capacity() / 3);
    }

    /**
     * <code>getIndexAsBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer(int batchIndex) {
        return getBatch(batchIndex).getIndexBuffer();
    }

    /**
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>TriMesh</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(int batchIndex, IntBuffer indices) {
        getBatch(batchIndex).setIndexBuffer(indices);
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
        getTriangle(0, i, storage);
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
    public void getTriangle(int batchIndex, int i, int[] storage) {
        TriangleBatch batch = getBatch(batchIndex);
        if (i < batch.getTriangleCount() && storage.length >= 3) {

            int iBase = 3 * i;
            IntBuffer indices = batch.getIndexBuffer();
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
        getTriangle(i, vertices, 0);
    }
    public void getTriangle(int i, Vector3f[] vertices, int batchIndex) {
        TriangleBatch batch = getBatch(i);
        if (i < batch.getTriangleCount() && i >= 0) {
            int iBase = 3 * i;
            for (int x = 0; x < 3; x++) {
                vertices[x] = new Vector3f(); // we could reuse existing, but
                                                // it may affect current users.
                BufferUtils.populateFromBuffer(vertices[x], batch.getVertexBuffer(),
                        batch.getIndexBuffer().get(iBase++));
            }
        }
    }

    /**
     * Returns the number of triangles contained in this mesh. This is a
     * summation of the triangle count for each batch that is contained in this
     * mesh.
     */
    public int getTotalTriangles() {
        int count = 0;

        for (int i = 0; i < getBatchCount(); i++) {
            count += getBatch(i).getTriangleCount();
        }

        return count;
    }

    public int getType() {
        return (Spatial.GEOMETRY | Spatial.TRIMESH);
    }

    /**
     * <code>draw</code> calls super to set the render state then passes
     * itself to the renderer. LOGIC: 1. If we're not RenderQueue calling draw
     * goto 2, if we are, goto 3 2. If we are supposed to use queue, add to
     * queue and RETURN, else 3 3. call super draw 4. tell renderer to draw me.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        TriangleBatch batch;
        for (int i = 0, cSize = batchList.size(); i < cSize; i++) {
            batch =  getBatch(i);
            if (batch != null)
                batch.onDraw(r);
        }
    }

    /**
     * Clears the buffers of this TriMesh. The buffers include its indexBuffer
     * only.
     */
    public void clearBuffers() {
        super.clearBuffers();
        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).setIndexBuffer(null);
    }

    /**
     * This function creates a collision tree from the TriMesh's current
     * information. If the information changes, the tree needs to be updated.
     */
    public void updateCollisionTree() {
        updateCollisionTree(true);
    }

    /**
     * This function creates a collision tree from the TriMesh's current
     * information. If the information changes, the tree needs to be updated.
     */
    public void updateCollisionTree(boolean doSort) {
        for (int i = 0; i < getBatchCount(); i++) {
            TriangleBatch tb = getBatch(i); 
            if (tb.getCollisionTree() == null)
                tb.setCollisionTree(new OBBTree());
            tb.getCollisionTree().construct(i, this, doSort);
        }
    }

    /**
     * determines if a collision between this trimesh and a given spatial occurs
     * if it has true is returned, otherwise false is returned.
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
        for (int x = 0; x < getBatchCount(); x++) {
            for (int y = 0; y < toCheck.getBatchCount(); y++) {
                boolean result = hasTriangleCollision(toCheck, x, y);
                if (result) return true;
            }
        }
        return false;
    }

    /**
     * This function checks for intersection between this trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck, int thisBatch, int checkBatch) {
        if (getBatch(thisBatch).getCollisionTree() == null
                || toCheck.getBatch(checkBatch).getCollisionTree() == null
                || !isCollidable || !toCheck.isCollidable())
            return false;
        else {
            getBatch(thisBatch).getCollisionTree().bounds.transform(
                    worldRotation, worldTranslation, worldScale,
                    getBatch(thisBatch).getCollisionTree().worldBounds);
            return getBatch(thisBatch).getCollisionTree().intersect(
                    toCheck.getBatch(checkBatch).getCollisionTree());
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
    public void findTriangleCollision(TriMesh toCheck, int batchIndex1, int batchIndex2,
            ArrayList<Integer> thisIndex, ArrayList<Integer> otherIndex) {

        if (getBatch(batchIndex1).getCollisionTree() == null
                || (toCheck.getBatch(batchIndex2)).getCollisionTree() == null)
            return;
        else {
            getBatch(batchIndex1).getCollisionTree().bounds.transform(
                    worldRotation, worldTranslation, worldScale,
                    getBatch(batchIndex1).getCollisionTree().worldBounds);
            getBatch(batchIndex1).getCollisionTree().intersect(
                    (toCheck.getBatch(batchIndex2)).getCollisionTree(), thisIndex,
                    otherIndex);
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
    public void findTrianglePick(Ray toTest, ArrayList<Integer> results,
            int batchIndex) {
        if (worldBound == null || !isCollidable) {
            return;
        }

        if (worldBound.intersects(toTest)) {
            if (getBatch(batchIndex).getCollisionTree() == null) {
                updateCollisionTree();
            }
            TriangleBatch triBatch = getBatch(batchIndex);
            triBatch.getCollisionTree().bounds.transform(
                    worldRotation, worldTranslation, worldScale,
                    triBatch.getCollisionTree().worldBounds);
            triBatch.getCollisionTree().intersect(toTest, results);
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

        TriangleBatch batch = getBatch(0);
        TriangleBatch toBatch = toStore.getBatch(0);
        if (properties.isSet("indices")) {
            toBatch.setIndexBuffer(batch.getIndexBuffer());
        } else {
            if (batch.getIndexBuffer() != null) {
                toBatch.setIndexBuffer(BufferUtils
                        .createIntBuffer(batch.getIndexBuffer()
                                .capacity()));
                toBatch.getIndexBuffer().rewind();
                batch.getIndexBuffer().rewind();
                toBatch.getIndexBuffer().put(
                        batch.getIndexBuffer());
                toBatch.setTriangleQuantity(batch.getTriangleCount());
            } else
                toBatch.setIndexBuffer(null);
        }

        if (properties.isSet("obbtree")) {
            toBatch
                    .setCollisionTree(batch.getCollisionTree());
        }

        return toStore;
    }

    /**
     * Return this mesh object as triangles. Every 3 vertices returned compose a
     * single triangle.
     * 
     * @param verts
     *            a storage array to place the results in
     * @return view of current mesh as group of triangle vertices
     */
    public Vector3f[] getMeshAsTrianglesVertices(int batchIndex, Vector3f[] verts) {
        TriangleBatch batch = getBatch(batchIndex);
        if (verts == null
                || verts.length != batch.getIndexBuffer()
                        .capacity())
            verts = new Vector3f[batch.getIndexBuffer().capacity()];
        batch.getIndexBuffer().rewind();
        for (int i = 0; i < verts.length; i++) {
            if (verts[i] == null)
                verts[i] = new Vector3f();
            BufferUtils.populateFromBuffer(verts[i], batch.getVertexBuffer(),
                    batch.getIndexBuffer().get(i));
        }
        return verts;
    }

    public Triangle[] getMeshAsTriangles(int batchIndex, Triangle[] tris) {
        TriangleBatch batch = getBatch(batchIndex);
        TriangleBatch.setTriangles(getMeshAsTrianglesVertices(batchIndex, TriangleBatch
                .getTriangles()));
        if (tris == null
                || tris.length != (batch.getIndexBuffer()
                        .capacity() / 3))
            tris = new Triangle[batch.getIndexBuffer().capacity() / 3];

        for (int i = 0, tLength = tris.length; i < tLength; i++) {
            Triangle t = tris[i];
            if (t == null) {
                t = new Triangle(TriangleBatch.getTriangles()[i * 3 + 0],
                        TriangleBatch.getTriangles()[i * 3 + 1], TriangleBatch
                                .getTriangles()[i * 3 + 2]);
                tris[i] = t;
            } else {
                t.set(0, TriangleBatch.getTriangles()[i * 3 + 0]);
                t.set(1, TriangleBatch.getTriangles()[i * 3 + 1]);
                t.set(2, TriangleBatch.getTriangles()[i * 3 + 2]);
            }
            t.calculateCenter();
            t.setIndex(i);
        }
        return tris;
    }

    public TriangleBatch getBatch(int index) {
        return (TriangleBatch) batchList.get(index);
    }
}