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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.SharedBatch;
import com.jme.scene.batch.TriangleBatch;

/**
 * CollisionTreeManager is an automated system for handling the creation and
 * deletion of CollisionTrees. The manager maintains a cache map of currently
 * generated collision trees. The collision system itself requests a collision
 * tree from the manager via the <code>getCollisionTree</code> method. The 
 * cache is checked for the tree, and if it is available, sent to the caller. 
 * If the tree is not in the cache, and generateTrees is true, a new CollisionTree
 * is generated on the fly and sent to the caller. When a new tree is created, the
 * cache size is compared to the maxElements value. If the cache is larger than
 * maxElements, the cache is sent to the CollisionTreeController for cleaning. 
 * 
 * There are a number of settings that can be used to control how trees are 
 * generated. First, generateTrees denotes whethere the manager should be 
 * creating trees at all. This is set to true by default. doSort defines if
 * the CollisionTree triangle array should be sorted as it is built. This is 
 * false by default. Sorting is beneficial for model data that is not well
 * ordered spatially. This occurance is rare, and sorting slows creation time. 
 * It is, therefore, only to be used when model data requires it. maxTrisPerLeaf
 * defines the number of triangles a leaf node in the collision tree should maintain.
 * The larger number of triangles maintained in a leaf node, the smaller the tree,
 * but the larger the number of triangle checks during a collision. By default,
 * this value is set to 16. maxElements defines the maximum number of trees that
 * will be maintained before clean-up is required. A collision tree is defined 
 * for each batch that is being collided with. The user should determine the 
 * optimal number of trees to maintain (a memory/performance tradeoff), based 
 * on the number of batches, their population density and their triangle size.
 * By default, this value is set to 25. The type of trees that will be generated
 * is defined by the treeType value, where valid options are define in 
 * CollisionTree as AABB_TREE, OBB_TREE and SPHERE_TREE.
 * 
 * You can set the functionality of how trees are removed from the cache by
 * providing the manager with a CollisionTreeController implementation. By
 * default, the manager will use the UsageTreeController for removing trees, 
 * but any other CollisionTreeController is acceptable. 
 * 
 * You can create protected tree manually. These are collision trees that you
 * request the manager to create and not allow them to be removed by the 
 * CollisionTreeController. 
 * 
 * @author Mark Powell
 * @see com.jme.bounding.CollisionTree
 * @see com.jme.bounding.CollisionTreeController
 */
public class CollisionTreeManager {
	/**
	 * defines the default maximum number of trees to maintain.
	 */
	public static final int DEFAULT_MAX_ELEMENTS = 25;
	/**
	 * defines the default maximum number of triangles in a tree leaf.
	 */
	public static final int DEFAULT_MAX_TRIS_PER_LEAF = 16;

	//the singleton instance of the manager
	private static CollisionTreeManager instance;
	
	//the cache and protected list for storing trees.
	private LinkedHashMap<TriangleBatch, CollisionTree> cache;
	private ArrayList<TriangleBatch> protectedList;

	private boolean generateTrees = true;
	private boolean doSort;

	private int treeType = CollisionTree.AABB_TREE;
	
	private int maxTrisPerLeaf = DEFAULT_MAX_TRIS_PER_LEAF;
	private int maxElements = DEFAULT_MAX_ELEMENTS;

	private CollisionTreeController treeRemover;

	/**
	 * private constructor for the Singleton. Initializes the cache.
	 *
	 */
	private CollisionTreeManager() {
		cache = new LinkedHashMap<TriangleBatch, CollisionTree>(1);
	}

	/**
	 * retrieves the singleton instance of the CollisionTreeManager.
	 * @return the singleton instance of the manager.
	 */
	public static CollisionTreeManager getInstance() {
		if (instance == null) {
			instance = new CollisionTreeManager();
			instance.setCollisionTreeController(new UsageTreeController());
		}
		return instance;
	}

	/**
	 * sets the CollisionTreeController used for cleaning the cache when the 
	 * maximum number of elements is reached.
	 * @param treeRemover the controller used to clean the cache.
	 */
	public void setCollisionTreeController(CollisionTreeController treeRemover) {
		this.treeRemover = treeRemover;
	}

	/**
	 * getCollisionTree obtains a collision tree that is assigned to a supplied
	 * TriangleBatch. The cache is checked for a pre-existing tree, if none is
	 * available and generateTrees is true, a new tree is created and returned.
	 *  
	 * @param batch the batch to use as the key for the tree to obtain.
	 * @return the tree assocated with a triangle batch
	 */
	public CollisionTree getCollisionTree(TriangleBatch batch) {
		CollisionTree toReturn = null;
		
		//If we have a shared batch, we want to use the tree of the target.
		//However, the tree requires world transform information, therefore,
		//set the parent of the tree to that of the shared batch's parent 
		//and return it.
		if (batch instanceof SharedBatch) {
			toReturn = cache.get(((SharedBatch) batch).getTarget());
			if (toReturn != null) {
				toReturn.setParent((TriMesh) batch.getParentGeom());
			}
		} else {
			//check cache
			toReturn = cache.get(batch);
		}

		//we didn't have it in the cache, create it if possible.
		if (toReturn == null) {
			if (generateTrees) {
				return generateCollisionTree(treeType, batch, false);
			} else {
				return null;
			}
		} else {
			//we had it in the cache, to keep the keyset in order, reinsert
			//this element
			cache.remove(batch);
			cache.put(batch, toReturn);
			return toReturn;
		}
	}

	/**
	 * creates a new collision tree for the provided spatial. If the spatial is
	 * a node, it recursively calls generateCollisionTree for each child. If
	 * it is a TriMesh, a call to generateCollisionTree is made for each batch. 
	 * If this tree(s) is to be protected, i.e. not deleted by the 
	 * CollisionTreeController, set protect to true.
	 * @param type the type of collision tree to generate.
	 * @param object the Spatial to generate tree(s) for.
	 * @param protect true to keep these trees from being removed, false otherwise.
	 */
	public void generateCollisionTree(int type, Spatial object, boolean protect) {
		if (object instanceof Node) {
			Node n = (Node) object;
			for (int i = n.getQuantity() - 1; i >= 0; i--) {
				generateCollisionTree(type, n.getChild(i), protect);
			}
		} else if (object instanceof TriMesh) {
			TriMesh t = (TriMesh) object;
			for (int i = 0; i < t.getBatchCount(); i++) {
				generateCollisionTree(type, t.getBatch(i), protect);
			}
		}
	}

	/**
	 * generates a new tree for the associated batch. The type is provided and
	 * a new tree is constructed of this type. The tree is placed in the cache.
	 * If the cache's size then becomes too large, the cache is sent to the
	 * CollisionTreeController for clean-up. If this tree is to be protected, 
	 * i.e. protected from the CollisionTreeController, set protect to true.
	 * 
	 * @param type the type of collision tree to generate.
	 * @param batch the batch to generate the tree fo.
	 * @param protect true if this tree is to be protected, false otherwise.
	 * @return the new collision tree.
	 */
	public CollisionTree generateCollisionTree(int type, TriangleBatch batch,
			boolean protect) {
		if (batch == null) {
			return null;
		}

		CollisionTree tree = new CollisionTree(type);

		return generateCollisionTree(tree, batch, protect);
	}

	/**
	 * generates a new tree for the associated batch. It is provided with a 
	 * pre-existing, non-null tree. The tree is placed in the cache.
	 * If the cache's size then becomes too large, the cache is sent to the
	 * CollisionTreeController for clean-up. If this tree is to be protected, 
	 * i.e. protected from the CollisionTreeController, set protect to true.
	 * 
	 * @param tree the tree to use for generation
	 * @param batch the batch to generate the tree fo.
	 * @param protect true if this tree is to be protected, false otherwise.
	 * @return the new collision tree.
	 */
	public CollisionTree generateCollisionTree(CollisionTree tree,
			TriangleBatch batch, boolean protect) {
		if (tree != null) {
			if (batch instanceof SharedBatch) {
				// we might already have the appropriate tree
				if (!cache.containsKey(((SharedBatch) batch).getTarget())) {
					tree.construct(((SharedBatch) batch).getTarget(),
							(TriMesh) ((SharedBatch) batch).getTarget()
									.getParentGeom(), doSort);
					cache.put(((SharedBatch) batch).getTarget(), tree);
					//This batch has been added by outside sources and labeled
					//as protected. Therefore, put it in the protected list
					//so it is not removed by a controller.
					if (protect) {
						if (protectedList == null) {
							protectedList = new ArrayList<TriangleBatch>(1);
						}
						protectedList.add(((SharedBatch) batch).getTarget());
					}
				}
                tree.setParent( (TriMesh) batch.getParentGeom() );
            } else {
				tree.construct(batch, (TriMesh) batch.getParentGeom(), doSort);
				cache.put(batch, tree);
				//This batch has been added by outside sources and labeled
				//as protected. Therefore, put it in the protected list
				//so it is not removed by a controller.
				if (protect) {
					if (protectedList == null) {
						protectedList = new ArrayList<TriangleBatch>(1);
					}
					protectedList.add(batch);
				}
			}

			// Are we over our max? Test
			if (cache.size() > maxElements && treeRemover != null) {
				treeRemover.clean(cache, protectedList, maxElements);
			}
		}
		return tree;
	}
    
    /**
     * removes a collision tree from the manager based on the batch supplied.
     * @param batch the batch to remove the corresponding collision tree.
     */
    public void removeCollisionTree(TriangleBatch batch) {
        cache.remove(batch);
    }
    
    /**
     * removes all collision trees associated with a Spatial object.
     * @param object the spatial to remove all collision trees from.
     */
    public void removeCollisionTree(Spatial object) {
        if(object instanceof Node) {
            Node n = (Node) object;
            for(int i = n.getQuantity() - 1; i >= 0; i--) {
                removeCollisionTree(n.getChild(i));
            }
        } else if(object instanceof TriMesh) {
            TriMesh t = (TriMesh) object;
            for (int i = t.getBatchCount() - 1; i >= 0; i--) {
                removeCollisionTree(t.getBatch(i));
            }
        }
    }

	/**
	 * updates the existing tree for a supplied batch. If this tree does
	 * not exist, the tree is not updated. If the tree is not in the cache,
	 * no further operations are handled.
	 * @param batch the batch key for the tree to update.
	 */
	public void updateCollisionTree(TriangleBatch batch) {
		CollisionTree ct = cache.get(batch);
		if (ct != null) {
			generateCollisionTree(ct, batch, protectedList != null
					&& protectedList.contains(batch));
		}
	}

	/**
	 * updates the existing tree(s) for a supplied spatial. If this tree does
	 * not exist, the tree is not updated. If the tree is not in the cache,
	 * no further operations are handled.

	 * @param object the object on which to update the tree.
	 */
	public void updateCollisionTree(Spatial object) {
		if (object instanceof Node) {
			Node n = (Node) object;
			for (int i = n.getQuantity() - 1; i >= 0; i--) {
				updateCollisionTree(n.getChild(i));
			}
		} else if (object instanceof TriMesh) {
			TriMesh t = (TriMesh) object;
			for (int i = 0; i < t.getBatchCount(); i++) {
				updateCollisionTree(t.getBatch(i));
			}
		}
	}

	
	/**
	 * returns true if the manager is set to sort new generated trees. False
	 * otherwise.
	 * @return true to sort tree, false otherwise.
	 */
	public boolean isDoSort() {
		return doSort;
	}

	/**
	 * set if this manager should have newly generated trees sort triangles.
	 * @param doSort true to sort trees, false otherwise.
	 */
	public void setDoSort(boolean doSort) {
		this.doSort = doSort;
	}

	/**
	 * returns true if the manager will automatically generate new trees as 
	 * needed, false otherwise.
	 * @return true if this manager is generating trees, false otherwise.
	 */
	public boolean isGenerateTrees() {
		return generateTrees;
	}

	/**
	 * set if this manager should generate new trees as needed.
	 * @param generateTrees true to generate trees, false otherwise.
	 */
	public void setGenerateTrees(boolean generateTrees) {
		this.generateTrees = generateTrees;
	}

	/**
	 * returns the type of collision trees this manager will create: AABB_TREE,
	 * OBB_TREE or SPHERE_TREE.
	 * @return the type of tree the manager will create.
	 */
	public int getTreeType() {
		return treeType;
	}

	/**
	 * set the type of collision tree this manager will create: AABB_TREE, 
	 * OBB_TREE or SPHERE_TREE.
	 * @param treeType the type of tree to create.
	 */
	public void setTreeType(int treeType) {
		this.treeType = treeType;
	}

	/**
	 * returns the maximum number of triangles a leaf of the collision tree
	 * will contain.
	 * @return the maximum number of triangles a leaf will contain.
	 */
	public int getMaxTrisPerLeaf() {
		return maxTrisPerLeaf;
	}

	/**
	 * set the maximum number of triangles a leaf of the collision tree will
	 * contain.
	 * @param maxTrisPerLeaf the maximum number of triangles a leaf will contain.
	 */
	public void setMaxTrisPerLeaf(int maxTrisPerLeaf) {
		this.maxTrisPerLeaf = maxTrisPerLeaf;
	}

}
