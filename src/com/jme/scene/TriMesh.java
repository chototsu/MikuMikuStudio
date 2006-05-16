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
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating triangles on every
 * three points.
 * 
 * @author Mark Powell
 * @version $Id: TriMesh.java,v 1.60 2006-05-16 16:09:33 nca Exp $
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
        batch.setParentGeom(this);
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
        batch.getTriangle(i, storage);
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
        TriangleBatch batch = getBatch(batchIndex);
        batch.getTriangle(i, vertices);
    }

    /**
     * Returns the number of triangles contained in this mesh. This is a
     * summation of the triangle count for each batch that is contained in this
     * mesh.
     */
    public int getTotalTriangles() {
        int count = 0;

        TriangleBatch batch;
        for (int i = 0; i < getBatchCount(); i++) {
            batch = getBatch(i);
            if (batch != null && batch.isEnabled())
                count += batch.getTriangleCount();
        }

        return count;
    }

    public int getType() {
        return (SceneElement.GEOMETRY | SceneElement.TRIMESH);
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
        for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
            batch =  getBatch(i);
            if (batch != null && batch.isEnabled())
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
            if (tb != null)
                tb.updateCollisionTree(doSort);
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
            if ((scene.getType() & SceneElement.NODE) != 0) {
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
            if ((scene.getType() & SceneElement.NODE) != 0) {
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
        TriangleBatch a,b;
        for (int x = 0; x < getBatchCount(); x++) {
            a = getBatch(x);
            if (a == null || !a.isEnabled()) continue;
            for (int y = 0; y < toCheck.getBatchCount(); y++) {
                b = toCheck.getBatch(y);
                if (b == null || !b.isEnabled()) continue;
                if (hasTriangleCollision(toCheck, x, y))
                    return true;
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

        OBBTree myTree = getBatch(batchIndex1).getCollisionTree();
        OBBTree otherTree = toCheck.getBatch(batchIndex2).getCollisionTree();

        if (myTree == null || otherTree == null)
            return;
        else {
            myTree.bounds.transform(
                    worldRotation, worldTranslation, worldScale,
                    myTree.worldBounds);
            myTree.intersect(
                    otherTree, thisIndex,
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
        TriangleBatch triBatch = getBatch(batchIndex);
        if (triBatch == null) return;
        
        triBatch.findTrianglePick(toTest, results);
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
        if (batch != null) return batch.getMeshAsTrianglesVertices(verts);
        else return verts;
    }

    public Triangle[] getMeshAsTriangles(int batchIndex, Triangle[] tris) {
        TriangleBatch batch = getBatch(batchIndex);
        if (batch != null) return batch.getMeshAsTriangles(tris);
        else return tris;
    }

    public TriangleBatch getBatch(int index) {
        return (TriangleBatch) batchList.get(index);
    }
}