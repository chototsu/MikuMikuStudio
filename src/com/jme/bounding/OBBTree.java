package com.jme.bounding;

import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.intersection.Intersection;
import com.jme.scene.TriMesh;

import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Started Date: Sep 5, 2004 <br>
 * <br>
 * This class is used exclusivly by the TriMesh object. There is no need for
 * users to call functions on this class directly. It is a tree of OBB objects
 * that represent a model's bound.
 * 
 * @author Jack Lindamood
 */
public class OBBTree {

    /** The max number of triangles in a leaf. */
    public static int maxPerLeaf = 5;

    /** Left tree. */
    private OBBTree left;

    /** Right tree. */
    private OBBTree right;

    /** Untransformed bounds of this tree. */
    public OBB2 bounds;

    /** This tree's bounds after transformation. */
    public OBB2 worldBounds;

    /** Array of triangles this tree is indexing. */
    private TreeTriangle[] tris;

    /** Start and end triangle indexes that this node contains. */
    private int myStart, myEnd;

    /** The mesh that built this node. */
    private TriMesh myParent;

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
    public void construct(TriMesh parent) {
        this.myParent = parent;
        Vector3f[] triangles = parent.getMeshAsTriangles(); 
        tris = new TreeTriangle[triangles.length / 3]; 
        for (int i = 0; i < tris.length; i++) {
        	tris[i] = new TreeTriangle(triangles[i * 3 + 0], 
        		triangles[i * 3 + 1], triangles[i * 3 + 2]); 
            tris[i].putCentriod();
            tris[i].index = i;
        }
        createTree(0, tris.length);
        for (int i = 0; i < tris.length; i++) {
            tris[i].centroid = null;
        }
    }

    /**
     * Creates an OBB tree recursivly from the tris's array of triangles.
     * 
     * @param start
     *            The start index of the tris array, inclusive.
     * @param end
     *            The end index of the tris array, exclusive.
     */
    public void createTree(int start, int end) {
        myStart = start;
        myEnd = end;
        bounds = new OBB2();
        worldBounds = new OBB2();
        bounds.computeFromTris(tris, start, end);
        if (myEnd - myStart + 1 <= maxPerLeaf) {
            return;
        } else {
            splitTris(start, end);
            this.left = new OBBTree();
            this.left.tris = this.tris;
            this.left.myParent = this.myParent;
            this.left.createTree(start, (start + end) / 2);

            this.right = new OBBTree();
            this.right.tris = this.tris;
            this.right.myParent = this.myParent;
            this.right.createTree((start + end) / 2 + 1, end);
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
                collisionTree.myParent.findWorldRotMat(),
                collisionTree.myParent.getWorldTranslation(),
                collisionTree.myParent.getWorldScale(),
                collisionTree.worldBounds);
        if (!worldBounds.intersection(collisionTree.worldBounds)) return false;
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
                Matrix3f roti = this.myParent.findWorldRotMat();
                Vector3f scalei = this.myParent.getWorldScale();
                Vector3f transi = this.myParent.getWorldTranslation();

                Matrix3f rotj = collisionTree.myParent.findWorldRotMat();
                Vector3f scalej = collisionTree.myParent.getWorldScale();
                Vector3f transj = collisionTree.myParent.getWorldTranslation();
                for (int i = myStart; i < myEnd; i++) {
                    roti.mult(tris[i].a, tempVa).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].b, tempVb).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].c, tempVc).multLocal(scalei).addLocal(
                            transi);
                    for (int j = collisionTree.myStart; j < collisionTree.myEnd; j++) {
                        rotj.mult(collisionTree.tris[j].a, tempVd).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].b, tempVe).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].c, tempVf).multLocal(
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
                collisionTree.myParent.findWorldRotMat(),
                collisionTree.myParent.getWorldTranslation(),
                collisionTree.myParent.getWorldScale(),
                collisionTree.worldBounds);
        if (!worldBounds.intersection(collisionTree.worldBounds)) return false;
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
                Matrix3f roti = this.myParent.findWorldRotMat();
                Vector3f scalei = this.myParent.getWorldScale();
                Vector3f transi = this.myParent.getWorldTranslation();

                Matrix3f rotj = collisionTree.myParent.findWorldRotMat();
                Vector3f scalej = collisionTree.myParent.getWorldScale();
                Vector3f transj = collisionTree.myParent.getWorldTranslation();
                boolean test = false;
                for (int i = myStart; i < myEnd; i++) {
                    roti.mult(tris[i].a, tempVa).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].b, tempVb).multLocal(scalei).addLocal(
                            transi);
                    roti.mult(tris[i].c, tempVc).multLocal(scalei).addLocal(
                            transi);
                    for (int j = collisionTree.myStart; j < collisionTree.myEnd; j++) {
                        rotj.mult(collisionTree.tris[j].a, tempVd).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].b, tempVe).multLocal(
                                scalej).addLocal(transj);
                        rotj.mult(collisionTree.tris[j].c, tempVf).multLocal(
                                scalej).addLocal(transj);
                        if (Intersection.intersection(tempVa, tempVb, tempVc,
                                tempVd, tempVe, tempVf)) {
                            test = true;
                            aList.add(new Integer(tris[i].index));
                            bList.add(new Integer(collisionTree.tris[j].index));
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
            left.bounds.transform(myParent.findWorldRotMat(), myParent
                    .getWorldTranslation(), myParent.getWorldScale(),
                    left.worldBounds);
            left.intersect(toTest, triList);
        }

        if (right != null) {
            right.bounds.transform(myParent.findWorldRotMat(), myParent
                    .getWorldTranslation(), myParent.getWorldScale(),
                    right.worldBounds);
            right.intersect(toTest, triList);
        } else if (left == null) {
            Matrix3f roti = this.myParent.findWorldRotMat();
            Vector3f scalei = this.myParent.getWorldScale();
            Vector3f transi = this.myParent.getWorldTranslation();

            TreeTriangle tempt;
            for (int i = myStart; i < myEnd; i++) {
                tempt = tris[i];
                roti.mult(tempt.a, tempVa).multLocal(scalei).addLocal(transi);
                roti.mult(tempt.b, tempVb).multLocal(scalei).addLocal(transi);
                roti.mult(tempt.c, tempVc).multLocal(scalei).addLocal(transi);
                if (toTest.intersect(tempVa, tempVb, tempVc))
                        triList.add(new Integer(tris[i].index));
            }
        }
    }

    /**
     * Splits the root obb acording to the largest bounds extent.
     * 
     * @param start
     *            Start index in the tris array, inclusive, that is the OBB to
     *            split.
     * @param end
     *            End index in the tris array, exclusive, that is the OBB to
     *            split.
     */
    private void splitTris(int start, int end) {
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
            tris[i].centroid.subtract(bounds.center, tempVa);
            tris[i].projection = bounds.zAxis.dot(tempVa);
        }
        Arrays.sort(tris, start, end, new TreeCompare());
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
            tris[i].centroid.subtract(bounds.center, tempVa);
            tris[i].projection = bounds.yAxis.dot(tempVa);
        }
        Arrays.sort(tris, start, end, new TreeCompare());
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
            tris[i].centroid.subtract(bounds.center, tempVa);
            tris[i].projection = bounds.xAxis.dot(tempVa);
        }
        Arrays.sort(tris, start, end, new TreeCompare());
    }

    /**
     * This class is simply a container for a triangle.
     */
    static class TreeTriangle {

        Vector3f a, b, c;

        float projection;

        int index;

        Vector3f centroid;

        public TreeTriangle(Vector3f a, Vector3f b, Vector3f c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public void putCentriod() {
            centroid = new Vector3f(a);
            centroid.addLocal(b).addLocal(c).multLocal(1.0f / 3.0f);
        }
    }

    /**
     * Class to sort TreeTriangle acording to projection.
     */
    static class TreeCompare implements Comparator {

        public int compare(Object o1, Object o2) {
            TreeTriangle a = (TreeTriangle) o1;
            TreeTriangle b = (TreeTriangle) o1;
            if (a.projection < b.projection) { return -1; }
            if (a.projection > b.projection) { return 1; }
            return 0;
        }
    }
}