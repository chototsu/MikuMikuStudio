/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

import com.jme.intersection.Intersection;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.SortUtil;

/**
 * CollisionTree defines a well balanced red black tree used for triangle 
 * accurate collision detection. The CollisionTree supports three types: 
 * Oriented Bounding Box, Axis-Aligned Bounding Box and Sphere. The tree 
 * is composed of a heirarchy of nodes, all but leaf nodes have two children, 
 * a left and a right, where the children contain half of the triangles of
 * the parent. This "half split" is executed down the tree until the node is
 * maintaining a set maximum of triangles. This node is called the leaf node. 
 * 
 * Intersection checks are handled as follows:<br>
 * 1. The bounds of the node is checked for intersection. If no intersection
 * occurs here, no further processing is needed, the children (nodes or triangles)
 * do not intersect.<br>
 * 2a. If an intersection occurs and we have children left/right nodes, pass
 * the intersection information to the children.<br>
 * 2b. If an intersection occurs and we are a leaf node, pass each triangle 
 * individually for intersection checking.<br>
 * 
 * Optionally, during creation of the collision tree, sorting can be applied. 
 * Sorting will attempt to optimize the order of the triangles in such a way
 * as to best split for left and right sub-trees. This function can lead to faster
 * intersection tests, but increases the creation time for the tree.
 * 
 * The number of triangles a leaf node is responsible for is defined in 
 * CollisionTreeManager. It is actually recommended to allow CollisionTreeManager
 * to maintain the collision trees for a scene.
 * 
 * @author Mark Powell
 * @see com.jme.bounding.CollisionTreeManager
 */
public class CollisionTree implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * defines a CollisionTree as using Oriented Bounding Boxes.
	 */
	public static final int OBB_TREE = 0;

	/**
	 * defines a CollisionTree as using Axis-Aligned Bounding Boxes.
	 */
	public static final int AABB_TREE = 1;

	/**
	 * defines a CollisionTree as using Bounding Spheres.
	 */
	public static final int SPHERE_TREE = 2;
	
	//Default tree is axis-aligned
	private int type = AABB_TREE;

	//children trees
	private CollisionTree left;
	private CollisionTree right;

	//bounding volumes that contain the triangles that the node is 
	//handling
	private BoundingVolume bounds;
	private BoundingVolume worldBounds;

	//the list of triangle indices that compose the tree. This list
	//contains all the triangles of the batch and is shared between
	//all nodes of this tree.
	private int[] triIndex;

	//Defines the pointers into the triIndex array that this node is
	//directly responsible for.
	private int start, end;

	//Required Spatial information
	private TriMesh parent;
	private TriangleBatch batch;

	// static variables to contain information for ray intersection
	static private final Vector3f tempVa = new Vector3f();
	static private final Vector3f tempVb = new Vector3f();
	static private final Vector3f tempVc = new Vector3f();
	static private final Vector3f tempVd = new Vector3f();
	static private final Vector3f tempVe = new Vector3f();
	static private final Vector3f tempVf = new Vector3f();

	static private Vector3f[] verts = new Vector3f[3];
	static private Vector3f[] target = new Vector3f[3];

	//Comparator used to sort triangle indices
	protected static TreeComparator comparator = new TreeComparator();
	
	/**
	 * Constructor creates a new instance of CollisionTree. The type of tree
	 * is provided as a parameter with valid options being:
	 * AABB_TREE, OBB_TREE, SPHERE_TREE.
	 * @param type the type of collision tree to make
	 */
	public CollisionTree(int type) {
		this.type = type;
	}

	/**
	 * Recreate this Collision Tree for the given TriMesh and batch
	 * index.
	 * 
	 * @param batchIndex the index of the batch to generate the tree for.
	 * @param parent
	 *            The trimesh that this OBBTree should represent.
	 * @param doSort true to sort triangles during creation, false otherwise
	 */
	public void construct(int batchIndex, TriMesh parent, boolean doSort) {
		this.parent = parent;
		this.batch = parent.getBatch(batchIndex);
		triIndex = parent.getBatch(batchIndex).getTriangleIndices(triIndex);
		createTree(0, triIndex.length, doSort);
	}

	/**
	 * Recreate this Collision Tree for the given TriMesh and batch.
	 * 
	 * @param batch the batch to generate the tree for.
	 * @param parent
	 *            The trimesh that this OBBTree should represent.
	 * @param doSort true to sort triangles during creation, false otherwise
	 */
	public void construct(TriangleBatch batch, TriMesh parent, boolean doSort) {
		this.parent = parent;
		this.batch = batch;
		triIndex = batch.getTriangleIndices(triIndex);
		createTree(0, triIndex.length, doSort);
	}

	/**
	 * Creates a Collision Tree by recursively creating children nodes, splitting
	 * the triangles this node is responsible for in half until the desired
	 * triangle count is reached.
	 * 
	 * @param start
	 *            The start index of the tris array, inclusive.
	 * @param end
	 *            The end index of the tris array, exclusive.
	 * @param doSort
	 * 			  True if the triangles should be sorted at each level,
	 * 			false otherwise.
	 */
	public void createTree(int start, int end, boolean doSort) {
		this.start = start;
		this.end = end;
		
		if (parent == null || triIndex == null) {
			return;
		}

		createBounds();

		// the bounds at this level should contain all the triangles this level
		// is reponsible for.
		bounds.computeFromTris(triIndex, batch, start, end);

		// check to see if we are a leaf, if the number of triangles we
		// reference is less than or equal to the maximum defined by the
		// CollisionTreeManager we are done.
		if (end - start + 1 <= CollisionTreeManager.getInstance()
				.getMaxTrisPerLeaf()) {
			return;
		}

		// if doSort is set we need to attempt to optimize the referenced
		// triangles.
		// optimizing the sorting of the triangles will help group them
		// spatially
		// in the left/right children better.
		if (doSort) {
			sortTris();
		}

		// create the left child, it will reference many of our values (index,
		// parent, batch)
		if (left == null) {
			left = new CollisionTree(type);
		}

		left.triIndex = this.triIndex;
		left.parent = this.parent;
		left.batch = this.batch;
		left.createTree(start, (start + end) / 2, doSort);

		// create the right child, it will reference many of our values (index,
		// parent, batch)
		if (right == null) {
			right = new CollisionTree(type);
		}
		right.triIndex = this.triIndex;
		right.parent = this.parent;
		right.batch = this.batch;
		right.createTree((start + end) / 2, end, doSort);
	}
	
	/**
	 * Tests if the world bounds of the node at this level interesects a 
	 * provided bounding volume. If an intersection occurs, true is 
	 * returned, otherwise false is returned. If the provided volume is
	 * invalid, false is returned.
	 * @param volume the volume to intersect with.
	 * @return true if there is an intersect, false otherwise.
	 */
	public boolean intersectsBounding(BoundingVolume volume) {
		switch(volume.getType()) {
		case BoundingVolume.BOUNDING_BOX:
			return worldBounds.intersectsBoundingBox((BoundingBox)volume);
		case BoundingVolume.BOUNDING_OBB:
			return worldBounds.intersectsOrientedBoundingBox((OrientedBoundingBox)volume);
		case BoundingVolume.BOUNDING_SPHERE:
			return worldBounds.intersectsSphere((BoundingSphere)volume);
		default:
			return false;
		}
		
	}
	
	/**
	 * Determines if this Collision Tree intersects the given CollisionTree. If
	 * a collision occurs, true is returned, otherwise false is returned. If
	 * the provided collisionTree is invalid, false is returned.
	 * 
	 * @param collisionTree
	 *            The Tree to test.
	 * @return True if they intersect, false otherwise.
	 */
	public boolean intersect(CollisionTree collisionTree) {
		if (collisionTree == null) {
			return false;
		}

		collisionTree.bounds
				.transform(collisionTree.parent.getWorldRotation(),
						collisionTree.parent.getWorldTranslation(),
						collisionTree.parent.getWorldScale(),
						collisionTree.worldBounds);

		// our two collision bounds do not intersect, therefore, our triangles must
		// not intersect. Return false.
		if (!intersectsBounding(collisionTree.worldBounds)) {
			return false;
		}

		// check children
		if (left != null) { // This is not a leaf
			if (collisionTree.intersect(left)) {
				return true;
			}
			if (collisionTree.intersect(right)) {
				return true;
			}
			return false;
		}

		// This is a leaf
		if (collisionTree.left != null) { // but collision isn't
			if (intersect(collisionTree.left)) {
				return true;
			}
			if (intersect(collisionTree.right)) {
				return true;
			}
			return false;
		}

		// both are leaves
		Quaternion roti = parent.getWorldRotation();
		Vector3f scalei = parent.getWorldScale();
		Vector3f transi = parent.getWorldTranslation();

		Quaternion rotj = collisionTree.parent.getWorldRotation();
		Vector3f scalej = collisionTree.parent.getWorldScale();
		Vector3f transj = collisionTree.parent.getWorldTranslation();

		//for every triangle to compare, put them into world space and check
		//for intersections
		for (int i = start; i < end; i++) {
			batch.getTriangle(triIndex[i], verts);
			roti.mult(verts[0], tempVa).multLocal(scalei).addLocal(transi);
			roti.mult(verts[1], tempVb).multLocal(scalei).addLocal(transi);
			roti.mult(verts[2], tempVc).multLocal(scalei).addLocal(transi);
			for (int j = collisionTree.start; j < collisionTree.end; j++) {
				collisionTree.batch.getTriangle(collisionTree.triIndex[j],
						target);
				rotj.mult(target[0], tempVd).multLocal(scalej).addLocal(transj);
				rotj.mult(target[1], tempVe).multLocal(scalej).addLocal(transj);
				rotj.mult(target[2], tempVf).multLocal(scalej).addLocal(transj);
				if (Intersection.intersection(tempVa, tempVb, tempVc, tempVd,
						tempVe, tempVf))
					return true;
			}
		}
		return false;
	}

	/**
	 * Determines if this Collision Tree intersects the given CollisionTree. If
	 * a collision occurs, true is returned, otherwise false is returned. If
	 * the provided collisionTree is invalid, false is returned. All collisions
	 * that occur are stored in lists as an integer index into the mesh's 
	 * triangle buffer. where aList is the triangles for this mesh and bList 
	 * is the triangles for the test tree.
	 * 
	 * @param collisionTree
	 *            The Tree to test.
	 * @param aList a list to contain the colliding triangles of this mesh.
	 * @param bList a list to contain the colliding triangles of the testing mesh.
	 * @return True if they intersect, false otherwise.
	 */
	public boolean intersect(CollisionTree collisionTree, ArrayList<Integer> aList,
			ArrayList<Integer> bList) {
		
		if (collisionTree == null) {
			return false;
		}
		
		// our two collision bounds do not intersect, therefore, our triangles must
		// not intersect. Return false.
		collisionTree.bounds
				.transform(collisionTree.parent.getWorldRotation(),
						collisionTree.parent.getWorldTranslation(),
						collisionTree.parent.getWorldScale(),
						collisionTree.worldBounds);
		
		if (!intersectsBounding(collisionTree.worldBounds)) {
			return false;
		}
		
		//if our node is not a leaf send the children (both left and right) to
		// the test tree.
		if (left != null) { // This is not a leaf
			boolean test = collisionTree.intersect(left, bList, aList);
			test = collisionTree.intersect(right, bList, aList) || test;
			return test;
		}

		// This node is a leaf, but the testing tree node is not. Therefore,
		// continue processing the testing tree until we find its leaves.
		if (collisionTree.left != null) {
			boolean test = intersect(collisionTree.left, aList, bList);
			test = intersect(collisionTree.right, aList, bList) || test;
			return test;
		}

		// both this node and the testing node are leaves. Therefore, we can
		// switch to checking the contained triangles with each other. Any 
		// that are found to intersect are placed in the appropriate list.
		Quaternion roti = parent.getWorldRotation();
		Vector3f scalei = parent.getWorldScale();
		Vector3f transi = parent.getWorldTranslation();

		Quaternion rotj = collisionTree.parent.getWorldRotation();
		Vector3f scalej = collisionTree.parent.getWorldScale();
		Vector3f transj = collisionTree.parent.getWorldTranslation();
		
		boolean test = false;
		
		for (int i = start; i < end; i++) {
			batch.getTriangle(triIndex[i], verts);
			roti.mult(verts[0], tempVa).multLocal(scalei).addLocal(transi);
			roti.mult(verts[1], tempVb).multLocal(scalei).addLocal(transi);
			roti.mult(verts[2], tempVc).multLocal(scalei).addLocal(transi);
			for (int j = collisionTree.start; j < collisionTree.end; j++) {
				collisionTree.batch.getTriangle(collisionTree.triIndex[j],
						target);
				rotj.mult(target[0], tempVd).multLocal(scalej).addLocal(transj);
				rotj.mult(target[1], tempVe).multLocal(scalej).addLocal(transj);
				rotj.mult(target[2], tempVf).multLocal(scalej).addLocal(transj);
				if (Intersection.intersection(tempVa, tempVb, tempVc, tempVd,
						tempVe, tempVf)) {
					test = true;
					aList.add(triIndex[i]);
					bList.add(collisionTree.triIndex[j]);
				}
			}
		}
		return test;

	}

	/**
	 * intersect checks for collisions between this collision tree and a 
	 * provided Ray. Any collisions are stored in a provided list. The ray
	 * is assumed to have a normalized direction for accurate calculations.
	 * @param ray the ray to test for intersections.
	 * @param triList the list to store instersections with.
	 */
	public void intersect(Ray ray, ArrayList<Integer> triList) {

		// if our ray doesn't hit the bounds, then it must not hit a triangle.
		if (!worldBounds.intersects(ray)) {
			return;
		}

		//This is not a leaf node, therefore, check each child (left/right) for
		//intersection with the ray.
		if (left != null) {
			left.bounds.transform(parent.getWorldRotation(), parent
					.getWorldTranslation(), parent.getWorldScale(),
					left.worldBounds);
			left.intersect(ray, triList);
		}

		if (right != null) {
			right.bounds.transform(parent.getWorldRotation(), parent
					.getWorldTranslation(), parent.getWorldScale(),
					right.worldBounds);
			right.intersect(ray, triList);
		} else if (left == null) {
			//This is a leaf node. We can therfore, check each triangle this
			//node contains. If an intersection occurs, place it in the 
			//list.
			
			for (int i = start; i < end; i++) {
				batch.getTriangle(this.triIndex[i], verts);
                parent.localToWorld( verts[0], tempVa );
                parent.localToWorld( verts[1], tempVb );
                parent.localToWorld( verts[2], tempVc );
				if (ray.intersect(tempVa, tempVb, tempVc)) {
					triList.add(triIndex[i]);
                }
			}
		}
	}

	/**
	 * Returns the bounding volume for this tree node in local space.
	 * @return the bounding volume for this tree node in local space.
	 */
	public BoundingVolume getBounds() {
		return bounds;
	}

	/**
	 * Returns the bounding volume for this tree node in world space.
	 * @return the bounding volume for this tree node in world space.
	 */
	public BoundingVolume getWorldBounds() {
		return worldBounds;
	}

	/**
	 * recursively sets the parent of the tree. 
	 * @param parent the parent to set.
	 */
	public void setParent(TriMesh parent) {
		this.parent = parent;
		if (left != null) {
			left.setParent(parent);
		}

		if (right != null) {
			right.setParent(parent);
		}
	}

	
	/**
	 * creates the appropriate bounding volume based on the type set
	 * during construction.
	 */
	private void createBounds() {
		switch(type) {
		case AABB_TREE:
			bounds = new BoundingBox();
			worldBounds = new BoundingBox();
			break;
		case OBB_TREE:
			bounds = new OrientedBoundingBox();
			worldBounds = new OrientedBoundingBox();
			break;
		case SPHERE_TREE:
			bounds = new BoundingSphere();
			worldBounds = new BoundingSphere();
			break;
		default:
			break;
		}
	}
	
	/**
	 * sortTris attempts to optimize the ordering of the subsection of the array
	 * of triangles this node is responsible for. The sorting is based on the
	 * most efficient method along an axis. Using the TreeComparator and quick
	 * sort, the subsection of the array is sorted.
	 */
	public void sortTris() {
		switch (type) {
		case AABB_TREE:
			//determine the longest length of the box, this axis will be best
			//for sorting.
			if (((BoundingBox) bounds).xExtent > ((BoundingBox) bounds).yExtent) {
				if (((BoundingBox) bounds).xExtent > ((BoundingBox) bounds).zExtent) {
					comparator.setAxis(TreeComparator.X_AXIS);
				} else {
					comparator.setAxis(TreeComparator.Z_AXIS);
				}
			} else {
				if (((BoundingBox) bounds).yExtent > ((BoundingBox) bounds).zExtent) {
					comparator.setAxis(TreeComparator.Y_AXIS);
				} else {
					comparator.setAxis(TreeComparator.Z_AXIS);
				}
			}
			break;
		case OBB_TREE:
			//determine the longest length of the box, this axis will be best
			//for sorting.
			if (((OrientedBoundingBox) bounds).extent.x > ((OrientedBoundingBox) bounds).extent.y) {
				if (((OrientedBoundingBox) bounds).extent.x > ((OrientedBoundingBox) bounds).extent.z) {
					comparator.setAxis(TreeComparator.X_AXIS);
				} else {
					comparator.setAxis(TreeComparator.Z_AXIS);
				}
			} else {
				if (((OrientedBoundingBox) bounds).extent.y > ((OrientedBoundingBox) bounds).extent.z) {
					comparator.setAxis(TreeComparator.Y_AXIS);
				} else {
					comparator.setAxis(TreeComparator.Z_AXIS);
				}
			}
			break;
		case SPHERE_TREE:
			//sort any axis, X is fine.
			comparator.setAxis(TreeComparator.X_AXIS);
			break;
		default:
			break;
		}

		comparator.setCenter(bounds.center);
		comparator.setBatch(batch);
		SortUtil.qsort(triIndex, start, end - 1, comparator);
	}
}
