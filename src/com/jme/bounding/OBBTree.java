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

package com.jme.bounding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.jme.intersection.Intersection;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;

/**
 * Started Date: Sep 5, 2004 <br>
 * <br>
 * This class is used exclusivly by the TriMesh object. There is no need for
 * users to call functions on this class directly. It is a tree of OBB objects
 * that represent a model's bound.
 *
 * @author Jack Lindamood
 */
public class OBBTree implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The max number of triangles in a leaf. */
    public static int maxPerLeaf = 5;

    /** Left tree. */
    private OBBTree left;

    /** Right tree. */
    private OBBTree right;

    /** Untransformed bounds of this tree. */
    public OrientedBoundingBox bounds;

    /** This tree's bounds after transformation. */
    public OrientedBoundingBox worldBounds;

    /** Array of triangles this tree is indexing. */
    private Triangle[] tris;

    /** Start and end triangle indexes that this node contains. */
    private int myStart, myEnd;

    /** The mesh that built this node. */
    private transient TriMesh myParent;

    private static TreeCompare comparator = new TreeCompare();

    //static variables to contain information for ray intersection
    static private final Vector3f tempVa = new Vector3f();

    static private final Vector3f tempVb = new Vector3f();

    static private final Vector3f tempVc = new Vector3f();

    static private final Vector3f tempVd = new Vector3f();

    static private final Vector3f tempVe = new Vector3f();

    static private final Vector3f tempVf = new Vector3f();

    /**
     * Recreates this OBBTree's information for the given TriMesh.
     *
     * @param parent
     *            The trimesh that this OBBTree should represent.
     */
    public void construct(TriMesh parent, boolean doSort) {
        this.myParent = parent;
        tris = parent.getMeshAsTriangles(tris);
        createTree(0, tris.length, doSort);
    }

    /**
     * Creates an OBB tree recursivly from the tris's array of triangles.
     *
     * @param start
     *            The start index of the tris array, inclusive.
     * @param end
     *            The end index of the tris array, exclusive.
     */
    public void createTree(int start, int end, boolean doSort) {
        myStart = start;
        myEnd = end;
        if (bounds == null)
            bounds = new OrientedBoundingBox();
        if (worldBounds == null)
            worldBounds = new OrientedBoundingBox();
        bounds.computeFromTris(tris, start, end);
        if (myEnd - myStart + 1 <= maxPerLeaf) {
            return;
        } else {
            if (doSort) sortTris(start, end);
            if (this.left == null)
                this.left = new OBBTree();
            this.left.tris = this.tris;
            this.left.myParent = this.myParent;
            this.left.createTree(start, (start + end) / 2, doSort);

            if (this.right == null)
                this.right = new OBBTree();
            this.right.tris = this.tris;
            this.right.myParent = this.myParent;
            this.right.createTree((start + end) / 2, end, doSort);
        }
    }

    // Assume this is ok and param needs rotation.
    /**
     * Returns true if this OBBTree intersects the given OBBTree.
     *
     * @param collisionTree
     *            The Tree to test.
     * @return True if they intersect.
     */
    public boolean intersect(OBBTree collisionTree) {
        if (collisionTree == null) return false;
        collisionTree.bounds.transform(
                collisionTree.myParent.getWorldRotation(),
                collisionTree.myParent.getWorldTranslation(),
                collisionTree.myParent.getWorldScale(),
                collisionTree.worldBounds);
        if (!worldBounds.intersectsOrientedBoundingBox(collisionTree.worldBounds)) return false;
        if (left != null) { // This is not a leaf
            if (collisionTree.intersect(left)) { return true; }
            if (collisionTree.intersect(right)) { return true; }
            return false;
        } else { // This is a leaf
            if (collisionTree.left != null) { // but collision isn't
                if (this.intersect(collisionTree.left)) { return true; }
                if (this.intersect(collisionTree.right)) { return true; }
                return false;
            } else { // both are leaves
                Quaternion roti = this.myParent.getWorldRotation();
                Vector3f scalei = this.myParent.getWorldScale();
                Vector3f transi = this.myParent.getWorldTranslation();

                Quaternion rotj = collisionTree.myParent.getWorldRotation();
                Vector3f scalej = collisionTree.myParent.getWorldScale();
                Vector3f transj = collisionTree.myParent.getWorldTranslation();
                for (int i = myStart; i < myEnd; i++) {
                    roti.mult(tris[i].get(0), tempVa).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].get(1), tempVb).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].get(2), tempVc).multLocal(scalei).addLocal(
                            transi);
                    for (int j = collisionTree.myStart; j < collisionTree.myEnd; j++) {
                        rotj.mult(collisionTree.tris[j].get(0), tempVd).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].get(1), tempVe).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].get(2), tempVf).multLocal(
                                scalej).addLocal(transj);
                        if (Intersection.intersection(tempVa, tempVb, tempVc,
                                tempVd, tempVe, tempVf)) return true;
                    }
                }
                return false;
            }
        }
    }

    // Assume this is ok and param needs rotation.
    /**
     * Stores in the given array list all indexes of triangle intersection
     * between the two OBBTree.
     *
     * @param collisionTree
     *            The tree to test this one against.
     * @param aList
     *            The arraylist to hold indexes of this OBBTree's triangle
     *            intersections.
     * @param bList
     *            The arraylist to hold indexes of the testing OBBTree's
     *            triangle intersections.
     * @return True if there was an intersection.
     */
    public boolean intersect(OBBTree collisionTree, ArrayList aList,
            ArrayList bList) {
        if (collisionTree == null) return false;
        collisionTree.bounds.transform(
                collisionTree.myParent.getWorldRotation(),
                collisionTree.myParent.getWorldTranslation(),
                collisionTree.myParent.getWorldScale(),
                collisionTree.worldBounds);
        if (!worldBounds.intersectsOrientedBoundingBox(collisionTree.worldBounds)) return false;
        if (left != null) { // This is not a leaf
            boolean test = collisionTree.intersect(left, bList, aList);
            test = collisionTree.intersect(right, bList, aList) || test;
            return test;
        } else { // This is a leaf
            if (collisionTree.left != null) { // but collision isn't
                boolean test = this.intersect(collisionTree.left, aList, bList);
                test = this.intersect(collisionTree.right, aList, bList)
                        || test;
                return test;
            } else { // both are leaves
                Quaternion roti = this.myParent.getWorldRotation();
                Vector3f scalei = this.myParent.getWorldScale();
                Vector3f transi = this.myParent.getWorldTranslation();

                Quaternion rotj = collisionTree.myParent.getWorldRotation();
                Vector3f scalej = collisionTree.myParent.getWorldScale();
                Vector3f transj = collisionTree.myParent.getWorldTranslation();
                boolean test = false;
                for (int i = myStart; i < myEnd; i++) {
                    roti.mult(tris[i].get(0), tempVa).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].get(1), tempVb).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].get(2), tempVc).multLocal(scalei).addLocal(
                            transi);
                    for (int j = collisionTree.myStart; j < collisionTree.myEnd; j++) {
                        rotj.mult(collisionTree.tris[j].get(0), tempVd).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].get(1), tempVe).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].get(2), tempVf).multLocal(
                                scalej).addLocal(transj);
                        if (Intersection.intersection(tempVa, tempVb, tempVc,
                                tempVd, tempVe, tempVf)) {
                            test = true;
                            aList.add(new Integer(tris[i].getIndex()));
                            bList.add(new Integer(collisionTree.tris[j].getIndex()));
                        }
                    }
                }
                return test;
            }
        }
    }

    /**
     * Stores in the given array list all indexes of triangle intersection
     * between this tree and a given ray.
     *
     * @param ray
     *            The ray to test this tree against.
     * @param triList
     *            The arraylist to hold indexes of this OBBTree's triangle
     *            intersections.
     */
    public void intersect(Ray toTest, ArrayList triList) {
        if (!worldBounds.intersects(toTest)) return;

        if (left != null) {
            left.bounds.transform(myParent.getWorldRotation(), myParent
                    .getWorldTranslation(), myParent.getWorldScale(),
                    left.worldBounds);
            left.intersect(toTest, triList);
        }

        if (right != null) {
            right.bounds.transform(myParent.getWorldRotation(), myParent
                    .getWorldTranslation(), myParent.getWorldScale(),
                    right.worldBounds);
            right.intersect(toTest, triList);
        } else if (left == null) {
            Quaternion roti = this.myParent.getWorldRotation();
            Vector3f scalei = this.myParent.getWorldScale();
            Vector3f transi = this.myParent.getWorldTranslation();

            Triangle tempt;
            for (int i = myStart; i < myEnd; i++) {
                tempt = tris[i];
                roti.mult(tempt.get(0), tempVa).multLocal(scalei).addLocal(transi);
                roti.mult(tempt.get(1), tempVb).multLocal(scalei).addLocal(transi);
                roti.mult(tempt.get(2), tempVc).multLocal(scalei).addLocal(transi);
                if (toTest.intersect(tempVa, tempVb, tempVc))
                        triList.add(new Integer(tris[i].getIndex()));
            }
        }
    }
    
    /**
     * gets the left elements of the binary tree.
     * @return the left element of the binary tree.
     */
    public OBBTree getLeftTree() {
        return left;
    }
    
    /**
     * gets the right elements of the binary tree.
     * @return the right element of the binary tree.
     */
    public OBBTree getRightTree() {
        return right;
    }
        
    /**
     * gets the total number of triangles this tree is maintaining.
     * @return the total number of triangles in this tree.
     */
    public int getTriangleCount() {
        return myEnd - myStart;
    }
    
    /**
     * obtains a triangle from the tree specified by the index.
     * @param index the index of the triangle to obtain.
     * @return the triangle
     */       
    public Triangle getTriangle(int index) {
        return tris[index + myStart];
    }

    /**
     * Sorts the root obb acording to the largest bounds extent.
     *
     * @param start
     *            Start index in the tris array, inclusive, that is the OBB to
     *            split.
     * @param end
     *            End index in the tris array, exclusive, that is the OBB to
     *            split.
     */
    private void sortTris(int start, int end) {
        if (bounds.extent.x > bounds.extent.y) {
            if (bounds.extent.x > bounds.extent.z)
                sortX(start, end);
            else
                sortZ(start, end);
        } else {
            if (bounds.extent.y > bounds.extent.z)
                sortY(start, end);
            else
                sortZ(start, end);
        }
    }

    /**
     *
     * <code>sortZ</code> sorts the z bounds of the tree.
     *
     * @param start
     *            the start index of the triangle list.
     * @param end
     *            the end index of the triangle list.
     */
    private void sortZ(int start, int end) {
        for (int i = start; i < end; i++) {
            tris[i].getCenter().subtract(bounds.center, tempVa);
            tris[i].setProjection(bounds.zAxis.dot(tempVa));
        }
        Arrays.sort(tris, start, end, comparator);
    }

    /**
     *
     * <code>sortY</code> sorts the y bounds of the tree.
     *
     * @param start
     *            the start index of the triangle list.
     * @param end
     *            the end index of the triangle list.
     */
    private void sortY(int start, int end) {
        for (int i = start; i < end; i++) {
            tris[i].getCenter().subtract(bounds.center, tempVa);
            tris[i].setProjection(bounds.yAxis.dot(tempVa));
        }
        Arrays.sort(tris, start, end, comparator);
    }

    /**
     *
     * <code>sortX</code> sorts the x bounds of the tree.
     *
     * @param start
     *            the start index of the triangle list.
     * @param end
     *            the end index of the triangle list.
     */
    private void sortX(int start, int end) {
        for (int i = start; i < end; i++) {
            tris[i].getCenter().subtract(bounds.center, tempVa);
            tris[i].setProjection(bounds.xAxis.dot(tempVa));
        }
        Arrays.sort(tris, start, end, comparator );
    }

    /**
     * Class to sort Triangle acording to projection.
     */
    static class TreeCompare implements Comparator {

        public int compare(Object o1, Object o2) {
            Triangle a = (Triangle) o1;
            Triangle b = (Triangle) o2;
            if (a.getProjection() < b.getProjection()) { return -1; }
            if (a.getProjection() > b.getProjection()) { return 1; }
            return 0;
        }
    }
}
