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
package com.jme.scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

/**
 * <code>Spatial</code> defines the base class for scene graph nodes. It
 * maintains a link to a parent, it's local transforms and the world's
 * transforms. All other nodes, such as <code>Node</code> and
 * <code>Geometry</code> are subclasses of <code>Spatial</code>.
 * 
 * @author Mark Powell
 * @version $Id: Spatial.java,v 1.50 2004-09-10 22:36:10 mojomonkey Exp $
 */
public abstract class Spatial implements Serializable {
	/** Spatial's rotation relative to its parent. */
	protected Quaternion localRotation;

	/** Spatial's world absolute rotation. */
	protected Quaternion worldRotation;

	/** Spatial's translation relative to its parent. */
	protected Vector3f localTranslation;

	/** Spatial's world absolute translation. */
	protected Vector3f worldTranslation;

	/** Spatial's scale relative to its parent. */
	protected Vector3f localScale;

	/** Spatial's world absolute scale. */
	protected Vector3f worldScale;

	/** If true, spatial and all children are culled from the scene graph. */
	protected boolean forceCull;

	/** If true, spatial and all children are always rendered in the scene graph. */
	protected boolean forceView;

	/** Spatial's bounding volume relative to the world. */
	protected BoundingVolume worldBound;

	/** Spatial's parent, or null if it has none. */
	protected transient Node parent;

	/** If true, spatial is a root node. */
	protected boolean isRoot;

	/** List of default states all spatials take if none is set. */
	public static RenderState[] defaultStateList = new RenderState[RenderState.RS_MAX_STATE];

	/** RenderStates a Spatial contains during rendering. */
	protected static RenderState[] currentStates = new RenderState[RenderState.RS_MAX_STATE];

	/** The render states of this spatial. */
	protected RenderState[] renderStateList;

	protected int renderQueueMode = Renderer.QUEUE_INHERIT;

	protected int zOrder = 0;

	public transient float queueDistance = Float.NEGATIVE_INFINITY;

	/**
	 * Flag signaling how lights are combined for this node. By default set to
	 * INHERIT.
	 */
	protected int lightCombineMode = LightState.INHERIT;

	/**
	 * Flag signaling how textures are combined for this node. By default set to
	 * INHERIT.
	 */
	protected int textureCombineMode = TextureState.INHERIT;

	/** ArrayList of controllers for this spatial. */
	protected ArrayList geometricalControllers = new ArrayList();

	/** This spatial's name. */
	protected String name;

	//scale values
	protected int frustrumIntersects = Camera.INTERSECTS_FRUSTUM;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public Spatial() {
	}

	/**
	 * Constructor instantiates a new <code>Spatial</code> object setting the
	 * rotation, translation and scale value to defaults.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public Spatial(String name) {
		this.name = name;
		renderStateList = new RenderState[RenderState.RS_MAX_STATE];
		localRotation = new Quaternion();
		worldRotation = new Quaternion();
		localTranslation = new Vector3f();
		worldTranslation = new Vector3f();
		localScale = new Vector3f(1.0f, 1.0f, 1.0f);
		worldScale = new Vector3f(1.0f, 1.0f, 1.0f);
	}

	/**
	 * Sets the name of this spatial.
	 * 
	 * @param name
	 *            The spatial's new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets if this spatial is a root spatial.
	 * 
	 * @param value
	 *            If true, flag this spatial as a root.
	 */
	public void setIsRoot(boolean value) {
		isRoot = value;
	}

	/**
	 * Returns the flag value for this spatial's root settting.
	 * 
	 * @return True if this spatial is flagged as a root.
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * Returns the name of this spatial.
	 * 
	 * @return This spatial's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds a Controller to this Spatial's list of controllers.
	 * 
	 * @param controller
	 *            The Controller to add
	 * @see com.jme.scene.Controller
	 */
	public void addController(Controller controller) {
		if (geometricalControllers == null) {
			geometricalControllers = new ArrayList();
		}
		geometricalControllers.add(controller);
	}

	/**
	 * Removes a Controller to this Spatial's list of controllers, if it exist.
	 * 
	 * @param controller
	 *            The Controller to remove
	 * @return True if the Controller was in the list to remove.
	 * @see com.jme.scene.Controller
	 */
	public boolean removeController(Controller controller) {
		if (geometricalControllers == null) {
			geometricalControllers = new ArrayList();
		}
		return geometricalControllers.remove(controller);
	}

	/**
	 * Returns the controller in this list of controllers at index i.
	 * 
	 * @param i
	 *            The index to get a controller from.
	 * @return The controller at index i.
	 * @see com.jme.scene.Controller
	 */
	public Controller getController(int i) {
		if (geometricalControllers == null) {
			geometricalControllers = new ArrayList();
		}
		return (Controller) geometricalControllers.get(i);
	}

	/**
	 * Returns the ArrayList that contains this spatial's Controllers.
	 * 
	 * @return This spatial's geometricalControllers.
	 */
	public ArrayList getControllers() {
		if (geometricalControllers == null) {
			geometricalControllers = new ArrayList();
		}
		return geometricalControllers;
	}

	/**
	 * 
	 * <code>getWorldBound</code> retrieves the world bound at this node
	 * level.
	 * 
	 * @return the world bound at this level.
	 */
	public BoundingVolume getWorldBound() {
		return worldBound;
	}

	/**
	 * 
	 * <code>setWorldBound</code> sets the world bound for this node level.
	 * This function should only be used in rare situations. In most cases,
	 * users will let jME's engine set the world bound and will instead call
	 * setModelBound on the leaf nodes.
	 * 
	 * @param worldBound
	 *            the world bound at this level.
	 * @see com.jme.scene.Geometry#setModelBound(com.jme.bounding.BoundingVolume)
	 */
	public void setWorldBound(BoundingVolume worldBound) {
		this.worldBound = worldBound;
	}

	/**
	 * 
	 * <code>onDraw</code> checks the node with the camera to see if it should
	 * be culled, if not, the node's draw method is called.
	 * 
	 * @param r
	 *            the renderer used for display.
	 */
	public void onDraw(Renderer r) {
		if (forceCull) {
			return;
		}

		Camera camera = r.getCamera();
		int state = camera.getPlaneState();
		//check to see if we can cull this node
		frustrumIntersects = (parent != null ? parent.frustrumIntersects
				: Camera.INTERSECTS_FRUSTUM);
		if (!forceView && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
			frustrumIntersects = camera.contains(worldBound);
		}

		if (forceView || frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
			draw(r);
		}
		camera.setPlaneState(state);
	}

	/**
	 * 
	 * <code>onDrawBounds</code> checks the node with the camera to see if it
	 * should be culled, if not, the node's draw method is called.
	 * 
	 * @param r
	 *            the renderer used for display.
	 */
	public void onDrawBounds(Renderer r) {
		if (forceCull) {
			return;
		}

		Camera camera = r.getCamera();
		int state = camera.getPlaneState();
		//check to see if we can cull this node
		frustrumIntersects = (parent != null ? parent.frustrumIntersects
				: Camera.INTERSECTS_FRUSTUM);
		if (!forceView && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
			frustrumIntersects = camera.contains(worldBound);
		}

		if (forceView || frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
			drawBounds(r);
		}
		camera.setPlaneState(state);
	}

	/**
	 * 
	 * <code>draw</code> abstract method that handles drawing data to the
	 * renderer if it is geometry and passing the call to it's children if it is
	 * a node.
	 * 
	 * @param r
	 *            the renderer used for display.
	 */
	public abstract void draw(Renderer r);

	/**
	 * 
	 * <code>drawBounds</code> abstract method that handles drawing bounds
	 * data to the renderer if it is geometry and passing the call to it's
	 * children if it is a node.
	 * 
	 * @param r
	 *            the renderer used for display.
	 */
	public abstract void drawBounds(Renderer r);

	/**
	 * 
	 * <code>getWorldRotation</code> retrieves the absolute rotation of the
	 * Spatial.
	 * 
	 * @return the Spatial's world rotation matrix.
	 */
	public Quaternion getWorldRotation() {
		return worldRotation;
	}

	/**
	 * 
	 * <code>getWorldTranslation</code> retrieves the absolute translation of
	 * the spatial.
	 * 
	 * @return the world's tranlsation vector.
	 */
	public Vector3f getWorldTranslation() {
		return worldTranslation;
	}

	/**
	 * 
	 * <code>getWorldScale</code> retrieves the absolute scale factor of the
	 * spatial.
	 * 
	 * @return the world's scale factor.
	 */
	public Vector3f getWorldScale() {
		return worldScale;
	}

	/**
	 * 
	 * <code>isForceCulled</code> reports if this node should always be culled
	 * or not. If true, this node will not be displayed.
	 * 
	 * @return true if this node should never be displayed, false otherwise.
	 */
	public boolean isForceCulled() {
		return forceCull;
	}

	/**
	 * 
	 * <code>isForceView</code> returns true if the node will be rendered
	 * whether it's in the camera frustum or not.
	 * 
	 * @return true if viewing is forced, false otherwise.
	 */
	public boolean isForceView() {
		return forceView;
	}

	/**
	 * 
	 * <code>setForceCull</code> sets if this node should always be culled or
	 * not. True will always cull the node, false will allow proper culling to
	 * take place.
	 * 
	 * @param forceCull
	 *            the value for forcing a culling.
	 */
	public void setForceCull(boolean forceCull) {
		this.forceCull = forceCull;
	}

	/**
	 * 
	 * <code>setForceView</code> will force the node to be rendered whether
	 * it's in the camera frustum or not.
	 * 
	 * @param value
	 *            true to force viewing, false otherwise.
	 */
	public abstract void setForceView(boolean value);

	/**
	 * 
	 * <code>updateGeometricState</code> updates all the geometry information
	 * for the node.
	 * 
	 * @param time
	 *            the frame time.
	 * @param initiator
	 *            true if this node started the update process.
	 */
	public void updateGeometricState(float time, boolean initiator) {
		updateWorldData(time);
		updateWorldBound();
		if (initiator) {
			propagateBoundToRoot();
		}
	}

	/**
	 * 
	 * <code>updateWorldData</code> updates the world transforms from the
	 * parent down to the leaf.
	 * 
	 * @param time
	 *            the frame time.
	 */
	protected void updateWorldData(float time) {
		//update spatial state via controllers
		Object controller;
		for (int i = 0, gSize = geometricalControllers.size(); i < gSize; i++) {
			if ((controller = geometricalControllers.get(i)) != null) {
				((Controller) controller).update(time);
			}
		}

		//update render state via controllers
		//    Controller[] controls;
		RenderState rs;
		for (int i = renderStateList.length; --i >= 0;) {
			rs = renderStateList[i];
			if (rs != null) {
				ArrayList controls = rs.getControllers();
				if (controls == null)
					continue;
				for (int j = controls.size(); --j >= 0;) {
					Controller tempC = (Controller) controls.get(j);
					if (tempC != null) {
						tempC.update(time);
					}
				}
			}
		}

		// update spatial controllers
		boolean computesWorldTransform = false;

		// update world transforms
		if (!computesWorldTransform) {
			if (parent != null) {
				worldScale.set(parent.getWorldScale()).multLocal(localScale);
				parent.getWorldRotation().mult(localRotation, worldRotation);
				worldTranslation = parent.getWorldRotation().mult(
						localTranslation, worldTranslation).multLocal(
						parent.getWorldScale()).addLocal(
						parent.getWorldTranslation());
			} else {
				worldScale = localScale;
				worldRotation = localRotation;
				worldTranslation = localTranslation;
			}
		}
	}

	/**
	 * 
	 * <code>updateWorldBound</code> updates the bounding volume of the world.
	 * Abstract, geometry transforms the bound while node merges the children's
	 * bound. In most cases, users will want to call updateModelBound() and let
	 * this function be called automatically during updateGeometricState().
	 *  
	 */
	public abstract void updateWorldBound();

	/**
	 * Updates the render state values of this Spatial and and children it has.
	 * Should be called whenever render states change.
	 */
	public void updateRenderState() {
		updateRenderState(null);
	}

	/**
	 * Called internally. Updates the render states of this Spatial. The stack
	 * contains parent render states.
	 * 
	 * @param parentStates
	 *            The list of parent renderstates.
	 */
	protected void updateRenderState(Stack[] parentStates) {
		boolean initiator = (parentStates == null);

		// first we need to get all the states from parent to us.
		if (initiator) {
			// grab all states from root to here.
			parentStates = new Stack[RenderState.RS_MAX_STATE];
			for (int x = 0; x < parentStates.length; x++)
				parentStates[x] = new Stack();
			propagateStatesFromRoot(parentStates);
		} else {
			for (int x = 0; x < RenderState.RS_MAX_STATE; x++) {
				if (renderStateList[x] != null)
					parentStates[x].push(renderStateList[x]);
			}
		}

		// now set these onto this spatial and below
		applyRenderState(parentStates);

		// restore previous if we are not the initiator
		if (!initiator) {
			for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
				if (renderStateList[x] != null)
					parentStates[x].pop();
		}

	}

	/**
	 * Called during updateRenderState(Stack[]), this function determines how
	 * the render states are actually applied to the spatial and any children it
	 * may have. By default, this function does nothing.
	 * 
	 * @param states
	 *            An array of stacks for each state.
	 */
	protected void applyRenderState(Stack[] states) {
	}

	/**
	 * Called during updateRenderState(Stack[]), this function goes up the scene
	 * graph tree until the parent is null and pushes RenderStates onto the
	 * states Stack array.
	 * 
	 * @param states
	 *            The Stack[] to push states onto.
	 */
	protected void propagateStatesFromRoot(Stack[] states) {
		// traverse to root to allow downward state propagation
		if (parent != null)
			parent.propagateStatesFromRoot(states);

		// push states onto current render state stack
		for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
			if (renderStateList[x] != null)
				states[x].push(renderStateList[x]);
	}

	/**
	 * 
	 * <code>propagateBoundToRoot</code> passes the new world bound up the
	 * tree to the root.
	 *  
	 */
	public void propagateBoundToRoot() {
		if (parent != null) {
			parent.updateWorldBound();
			parent.propagateBoundToRoot();
		}
	}

	/**
	 * <code>getParent</code> retrieve's this node's parent. If the parent is
	 * null this is the root node.
	 * 
	 * @return the parent of this node.
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * <code>setParent</code> sets the parent of this node.
	 * 
	 * @param parent
	 *            the parent of this node.
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * <code>getLocalRotation</code> retrieves the local rotation of this
	 * node.
	 * 
	 * @return the local rotation of this node.
	 */
	public Quaternion getLocalRotation() {
		return localRotation;
	}

	/**
	 * <code>setLocalRotation</code> sets the local rotation of this node.
	 * 
	 * @param rotation
	 *            the new local rotation.
	 */
	public void setLocalRotation(Matrix3f rotation) {
		if (localRotation == null)
			localRotation = new Quaternion();
		localRotation.fromRotationMatrix(rotation);
	}

	/**
	 * 
	 * <code>setLocalRotation</code> sets the local rotation of this node,
	 * using a quaterion to build the matrix.
	 * 
	 * @param quaternion
	 *            the quaternion that defines the matrix.
	 */
	public void setLocalRotation(Quaternion quaternion) {
		localRotation = quaternion;
	}

	/**
	 * <code>getLocalScale</code> retrieves the local scale of this node.
	 * 
	 * @return the local scale of this node.
	 */
	public Vector3f getLocalScale() {
		return localScale;
	}

	/**
	 * <code>setLocalScale</code> sets the local scale of this node.
	 * 
	 * @param localScale
	 *            the new local scale, applied to x, y and z
	 */
	public void setLocalScale(float localScale) {
		this.localScale.x = localScale;
		this.localScale.y = localScale;
		this.localScale.z = localScale;
	}

	/**
	 * <code>setLocalScale</code> sets the local scale of this node.
	 * 
	 * @param localScale
	 *            the new local scale.
	 */
	public void setLocalScale(Vector3f localScale) {
		this.localScale = localScale;
	}

	/**
	 * <code>getLocalTranslation</code> retrieves the local translation of
	 * this node.
	 * 
	 * @return the local translation of this node.
	 */
	public Vector3f getLocalTranslation() {
		return localTranslation;
	}

	/**
	 * <code>setLocalTranslation</code> sets the local translation of this
	 * node.
	 * 
	 * @param localTranslation
	 *            the local translation of this node.
	 */
	public void setLocalTranslation(Vector3f localTranslation) {
		this.localTranslation = localTranslation;
	}

	/**
	 * 
	 * <code>setRenderState</code> sets a render state for this node. Note,
	 * there can only be one render state per type per node. That is, there can
	 * only be a single AlphaState a single TextureState, etc. If there is
	 * already a render state for a type set the old render state will be
	 * rendered. Otherwise, null is returned.
	 * 
	 * @param rs
	 *            the render state to add.
	 * @return the old render state.
	 */
	public RenderState setRenderState(RenderState rs) {
		RenderState oldState = renderStateList[rs.getType()];
		renderStateList[rs.getType()] = rs;
		return oldState;
	}

	/**
	 * Returns the array of RenerState that this Spatial currently has.
	 * 
	 * @return This spatial's state array.
	 */
	public RenderState[] getRenderStateList() {
		return renderStateList;
	}

	/**
	 * Clears a given render state index by setting it to 0.
	 * 
	 * @param renderStateType
	 *            The index of a RenderState to clear
	 * @see com.jme.scene.state.RenderState#getType()
	 */
	public void clearRenderState(int renderStateType) {
		renderStateList[renderStateType] = null;
	}

	public void setRenderQueueMode(int renderQueueMode) {
		this.renderQueueMode = renderQueueMode;
	}

	public int getRenderQueueMode() {
		if (renderQueueMode != Renderer.QUEUE_INHERIT)
			return renderQueueMode;
		else if (parent != null)
			return parent.getRenderQueueMode();
		else
			return Renderer.QUEUE_SKIP;
	}

	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	public int getZOrder() {
		return zOrder;
	}

	/**
	 * Sets how lights from parents should be combined for this spatial.
	 * 
	 * @param lightCombineMode
	 *            The light combine mode for this spatial
	 * @see com.jme.scene.state.LightState#COMBINE_CLOSEST
	 * @see com.jme.scene.state.LightState#COMBINE_FIRST
	 * @see com.jme.scene.state.LightState#COMBINE_RECENT_ENABLED
	 * @see com.jme.scene.state.LightState#INHERIT
	 * @see com.jme.scene.state.LightState#OFF
	 * @see com.jme.scene.state.LightState#REPLACE
	 */
	public void setLightCombineMode(int lightCombineMode) {
		this.lightCombineMode = lightCombineMode;
	}

	/**
	 * Returns this spatial's light combine mode. If the mode is set to inherit,
	 * then the spatial gets its combine mode from its parent.
	 * 
	 * @return The spatial's light current combine mode.
	 */
	public int getLightCombineMode() {
		if (lightCombineMode != LightState.INHERIT)
			return lightCombineMode;
		else if (parent != null)
			return parent.getLightCombineMode();
		else
			return LightState.COMBINE_FIRST;
	}

	/**
	 * Sets how textures from parents should be combined for this Spatial.
	 * 
	 * @param textureCombineMode
	 *            The new texture combine mode for this spatial.
	 * @see com.jme.scene.state.TextureState#COMBINE_CLOSEST
	 * @see com.jme.scene.state.TextureState#COMBINE_FIRST
	 * @see com.jme.scene.state.TextureState#COMBINE_RECENT_ENABLED
	 * @see com.jme.scene.state.TextureState#INHERIT
	 * @see com.jme.scene.state.TextureState#OFF
	 * @see com.jme.scene.state.TextureState#REPLACE
	 */
	public void setTextureCombineMode(int textureCombineMode) {
		this.textureCombineMode = textureCombineMode;
	}

	/**
	 * Returns this spatial's texture combine mode. If the mode is set to
	 * inherit, then the spatial gets its combine mode from its parent.
	 * 
	 * @return The spatial's texture current combine mode.
	 */
	public int getTextureCombineMode() {
		if (textureCombineMode != TextureState.INHERIT)
			return textureCombineMode;
		else if (parent != null)
			return parent.getTextureCombineMode();
		else
			return TextureState.COMBINE_CLOSEST;
	}

	public static void clearCurrentStates() {
		for (int i = 0; i < currentStates.length; i++)
			currentStates[i] = null;
	}

	/**
	 * All non null default states are applied to the renderer.
	 */
	public static void applyDefaultStates() {
		for (int i = 0; i < defaultStateList.length; i++) {
			if (defaultStateList[i] != null)
				defaultStateList[i].apply();
		}
	}

	/**
	 * checks this spatial against a second spatial, any collisions are 
	 * stored in the results object.
	 * @param scene the scene to test against.
	 * @param results the results of the collisions.
	 */
	public abstract void hasCollision(Spatial scene, CollisionResults results);

	/**
	 * Returns the Spatial's name followed by the class of the spatial <br>
	 * Example: "MyNode (com.jme.scene.Spatial)
	 * 
	 * @return Spatial's name followed by the class of the Spatial
	 */
	public String toString() {
		return name + " (" + this.getClass().getName() + ')';
	}
}