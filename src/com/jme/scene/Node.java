/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

/*
 * EDIT:  02/05/2004 - Added detachAllChildren method. GOP
 * EDIT:  02/05/2004 - Added check for null on first child before setting
 *                     firstBound to true in updateWorldBound method.  GOP
 * EDIT: 02/14/2004 - Made children protected rather than private. MAP
 */

package com.jme.scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.bounding.*;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.renderer.Renderer;
import com.jme.util.LoggingSystem;
import java.util.Stack;

/**
 * <code>Node</code> defines an internal node of a scene graph. The internal
 * node maintains a collection of children and handles merging said children
 * into a single bound to allow for very fast culling of multiple nodes. Node
 * allows for any number of children to be attached.
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: Node.java,v 1.36 2005-01-13 04:12:08 mojomonkey Exp $
 */
public class Node extends Spatial implements Serializable {

    private static final long serialVersionUID = 1L;

    /** This node's children. */
    protected ArrayList children;

    /**
     * Empty Constructor to be used internally only.
     */
    public Node() {
    }

    /**
     * Constructor instantiates a new <code>Node</code> with a default empty
     * list for containing children.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public Node(String name) {
        super(name);
        children = new ArrayList();
        LoggingSystem.getLogger().log(Level.INFO, "Node created.");
    }

    /**
     * 
     * <code>getQuantity</code> returns the number of children this node
     * maintains.
     * 
     * @return the number of children this node maintains.
     */
    public int getQuantity() {
        return children.size();
    }

    /**
     * 
     * <code>attachChild</code> attaches a child to this node. This node
     * becomes the child's parent. The current number of children maintained is
     * returned.
     * 
     * @param child
     *            the child to attach to this node.
     * @return the number of children maintained by this node.
     */
    public int attachChild(Spatial child) {
        if (child == null) { return children.size(); }
        if (!children.contains(child)) {
            child.setParent(this);
            children.add(child);
            child.setForceCull(forceCull);
            child.setForceView(forceView);
        }
        LoggingSystem.getLogger().log(
                Level.INFO,
                "Child (" + child.getName() + ") attached to this" + " node ("
                        + name + ")");

        return children.size();
    }

    /**
     * 
     * <code>detachChild</code> removes a given child from the node's list.
     * This child will no longe be maintained.
     * 
     * @param child
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChild(Spatial child) {
        int index = children.indexOf(child);
        if (index != -1) {
            children.remove(index);
            LoggingSystem.getLogger().log(Level.INFO, "Child removed.");
        }
        return index;
    }

    /**
     * 
     * <code>detachChild</code> removes a given child from the node's list.
     * This child will no longe be maintained. Only the first child with a
     * matching name is removed.
     * 
     * @param childName
     *            the child to remove.
     * @return the index the child was at. -1 if the child was not in the list.
     */
    public int detachChildNamed(String childName) {
        if (childName == null) return -1;
        for (int x = 0, max = children.size(); x < max; x++) {
            Spatial child = (Spatial) children.get(x);
            if (childName.equals(child.getName())) {
                children.remove(x);
                LoggingSystem.getLogger().log(Level.INFO, "Child removed.");
                return x;
            }
        }
        return -1;
    }

    /**
     * 
     * <code>detachChildAt</code> removes a child at a given index. That child
     * is returned for saving purposes.
     * 
     * @param index
     *            the index of the child to be removed.
     * @return the child at the supplied index.
     */
    public Spatial detachChildAt(int index) {
        LoggingSystem.getLogger().log(Level.INFO, "Child removed.");
        return (Spatial) children.remove(index);
    }

    /**
     * 
     * <code>detachAllChildren</code> removes all children attached to this
     * node.
     */
    public void detachAllChildren() {
        LoggingSystem.getLogger().log(Level.INFO, "All children removed.");
        children.clear();
    }

    /**
     * 
     * <code>setChild</code> places a child at a given index. If a child is
     * already set to that index the old child is returned.
     * 
     * @param i
     *            the index to set the child to.
     * @param child
     *            the child to attach.
     * @return the old child at the index.
     */
    public Spatial setChild(int i, Spatial child) {
        Spatial old = (Spatial) children.get(i);
        children.add(i, child);
        LoggingSystem.getLogger().log(Level.INFO,
                "Child attached to this" + " node");
        return old;
    }

    /**
     * 
     * <code>getChild</code> returns a child at a given index.
     * 
     * @param i
     *            the index to retrieve the child from.
     * @return the child at a specified index.
     */
    public Spatial getChild(int i) {
        return (Spatial) children.get(i);
    }

    public void setForceView(boolean value) {
        forceView = value;

        for (int i = 0; i < children.size(); i++) {
            ((Spatial) children.get(i)).setForceView(value);
        }
    }
    
    /**
     * determines if the provide Spatial is contained in the children list of
     * this node.
     * @param spat the spatial object to check.
     * @return true if the object is contained, false otherwise.
     */
    public boolean hasChild(Spatial spat) { 

        if (children.contains(spat)) return true; 

       for (int i = 0; i < children.size(); i++) {
			Spatial child = (Spatial) children.get(i);
			if (child instanceof Node && ((Node) child).hasChild(spat))
				return true; 
       } 
        
        
        return false; 
     } 

    /**
     * <code>updateWorldData</code> updates all the children maintained by
     * this node.
     * 
     * @param time
     *            the frame time.
     */
    public void updateWorldData(float time) {
        super.updateWorldData(time);

        for (int i = 0; i < children.size(); i++) {
            Spatial child = (Spatial) children.get(i);
            if (child != null) {
                child.updateGeometricState(time, false);
            }
        }
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained by
     * this node.
     * 
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r
     *            the renderer to draw to.
     */
    public void draw(Renderer r) {
        Spatial child = null;
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            child = (Spatial) children.get(i);
            if (child != null) child.onDraw(r);
        }
    }

    /**
     * <code>drawBounds</code> calls super to set the render state then passes
     * itself to the renderer.
     * 
     * @param r
     *            the renderer to display
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(getWorldBound());
        Spatial child = null;
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            child = (Spatial) children.get(i);
            if (child != null) child.onDrawBounds(r);
        }
    }

    /**
     * Applies the stack of render states to each child by calling
     * updateRenderState(states) on each child.
     * 
     * @param states
     *            The Stack[] of render states to apply to each child.
     */
    protected void applyRenderState(Stack[] states) {
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial pkChild = getChild(i);
            if (pkChild != null) pkChild.updateRenderState(states);
        }
    }

    /**
     * <code>updateWorldBound</code> merges the bounds of all the children
     * maintained by this node. This will allow for faster culling operations.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {

        boolean foundFirstBound = false;
        for (int i = 0, cSize = children.size(); i < cSize; i++) {
            Spatial child = (Spatial) children.get(i);
            if (child != null) {
                if (foundFirstBound) {
                    // merge current world bound with child world bound
                    worldBound.mergeLocal(child.getWorldBound());

                } else {
                    // set world bound to first non-null child world bound
                    if (child.getWorldBound() != null) {
                        worldBound = (BoundingVolume) child.getWorldBound()
                                .clone(worldBound);
                        foundFirstBound = true;
                    }
                }
            }
        }
    }

    /**
     * @see Spatial#updateCollisionTree()
     */
    public void updateCollisionTree() {
        for (int i = children.size() - 1; i >= 0; i--) {
            ((Spatial) children.get(i)).updateCollisionTree();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
     *      com.jme.intersection.CollisionResults)
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        if(getWorldBound() != null) {
	        if (getWorldBound().intersects(scene.getWorldBound())) {
	            //further checking needed.
	            for (int i = 0; i < getQuantity(); i++) {
	                getChild(i).findCollisions(scene, results);
	            }
	        }
        }
    }

    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        if(getWorldBound() != null) {
	        if (getWorldBound().intersects(scene.getWorldBound())) {
	            //further checking needed.
	            for (int i = 0; i < getQuantity(); i++) {
	                if (getChild(i).hasCollision(scene, checkTriangles)) { return true; }
	            }
	        }
        }

        return false;
    }
    
    public void findPick(Ray toTest, PickResults results){
        if(getWorldBound() != null) {
			if(getWorldBound().intersects(toTest)) {
				//further checking needed.
				for(int i = 0; i < getQuantity(); i++) {
					((Spatial)children.get(i)).findPick(toTest, results);
				}
			}
        }
    }


    public Spatial putClone(Spatial store, CloneCreator properties) {
        Node toStore;
        if (store == null)
            toStore = new Node(getName() + "copy");
        else
            toStore = (Node) store;
        super.putClone(toStore, properties);
        for (int i = 0, size = children.size(); i < size; i++) {
            Spatial child = (Spatial) children.get(i);
            toStore.attachChild(child.putClone(null, properties));
        }
        return toStore;
    }
}