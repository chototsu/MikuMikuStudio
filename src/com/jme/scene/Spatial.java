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
import java.util.*;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * <code>Spatial</code> defines the base class for scene graph nodes. It
 * maintains a link to a parent, it's local transforms and the world's
 * transforms. All other nodes, such as <code>Node</code> and
 * <code>Geometry</code> are subclasses of <code>Spatial</code>.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Spatial.java,v 1.96 2006-02-20 23:28:47 llama Exp $
 */
public abstract class Spatial implements Serializable {

	public static final int NODE = 1;
	public static final int GEOMETRY = 2;
	public static final int TRIMESH = 4;
	public static final int SHARED_MESH = 8;
	public static final int SKY_BOX = 16;
	public static final int TERRAIN_BLOCK = 32;
	public static final int TERRAIN_PAGE = 64;
    public static final int COMPOSITE_MESH = 128;

    public static final int CULL_INHERIT = 0;
    public static final int CULL_DYNAMIC = 1;
    public static final int CULL_ALWAYS = 2;
    public static final int CULL_NEVER = 3;

    public static final int LOCKED_BOUNDS = 1;
    public static final int LOCKED_MESH_DATA = 2;
    public static final int LOCKED_TRANSFORMS = 4;

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

    /**
     * A flag indicating if scene culling should be done on this object by
     * inheritance, dynamically, never, or always.
     */
    private int cullMode = CULL_INHERIT;

    /** Spatial's bounding volume relative to the world. */
    protected BoundingVolume worldBound;

    /** Spatial's parent, or null if it has none. */
    protected transient Node parent;

    /** List of default states all spatials take if none is set. */
    public final static RenderState[] defaultStateList = new RenderState[RenderState.RS_MAX_STATE];

    /** List of states that override any set states on a spatial if not null. */
    public final static RenderState[] enforcedStateList = new RenderState[RenderState.RS_MAX_STATE];

    /** RenderStates a Spatial contains during rendering. */
    protected final static RenderState[] currentStates = new RenderState[RenderState.RS_MAX_STATE];

    /** The render states of this spatial. */
    private RenderState[] renderStateList;

    protected int renderQueueMode = Renderer.QUEUE_INHERIT;

    /** Used to determine draw order for ortho mode rendering. */
    protected int zOrder = 0;
    
    /**
     * Used to indicate this spatial (and any below it in the case of Node) is
     * locked against certain changes.
     */
    protected int lockedMode = 0;

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

    // scale values
    protected int frustrumIntersects = Camera.INTERSECTS_FRUSTUM;
    
    /** Defines if this spatial will be used in intersection operations or not. Default is true*/
    protected boolean isCollidable = true;

    private static final Vector3f compVecA = new Vector3f();
    private static final Quaternion compQuat = new Quaternion();

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
     * Returns the name of this spatial.
     *
     * @return This spatial's name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets if this Spatial is to be used in intersection (collision and picking) calculations.
     * By default this is true.
     * @param isCollidable true if this Spatial is to be used in intersection calculations, false otherwise.
     */
    public void setIsCollidable(boolean isCollidable) {
        this.isCollidable = isCollidable;
    }
    
    /**
     * Defines if this Spatial is to be used in intersection (collision and picking) calculations.
     * By default this is true.
     * @return true if this Spatial is to be used in intersection calculations, false otherwise.
     */
    public boolean isCollidable() {
        return this.isCollidable;
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
     * <code>onDraw</code> checks the spatial with the camera to see if it should
     * be culled, if not, the node's draw method is called.
     *
     * @param r
     *            the renderer used for display.
     */
    public void onDraw(Renderer r) {
        int cm = getCullMode();
        if (cm == CULL_ALWAYS) {
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();

        // check to see if we can cull this node
        frustrumIntersects = (parent != null ? parent.frustrumIntersects
                : Camera.INTERSECTS_FRUSTUM);
        if (cm == CULL_DYNAMIC && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
            frustrumIntersects = camera.contains(worldBound);
        }

        if (cm == CULL_NEVER || frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
            draw(r);
        }
        camera.setPlaneState(state);
    }

    /**
     * <code>getType</code> returns an int representing the class type
     * of this Spatial.  This allows avoidance of instanceof.  Comparisons
     * are to be done via bitwise & allowing checking of superclass instance.
     */
    public abstract int getType();

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
     * <code>setCullMode</code> sets how scene culling should work on this
     * spatial during drawing.
     *
     * CULL_DYNAMIC: Determine via the defined Camera planes whether or not this
     * Spatial should be culled.
     *
     * CULL_ALWAYS: Always throw away this object and any children during draw
     * commands.
     *
     * CULL_NEVER: Never throw away this object (always draw it)
     *
     * CULL_INHERIT: Look for a non-inherit parent and use its cull mode.
     *
     * NOTE: You must set this AFTER attaching to a parent or it will be reset
     * with the parent's cullMode value.
     *
     * @param mode
     *            one of CULL_DYNAMIC, CULL_ALWAYS, CULL_INHERIT or CULL_NEVER
     */
    public void setCullMode(int mode) {
        cullMode = mode;
    }


    /**
     * @see #setCullMode(int)
     *
     * @return the cull mode of this spatial
     */
    public int getCullMode() {
        if (cullMode != CULL_INHERIT)
            return cullMode;
        else if (parent != null)
            return parent.getCullMode();
        else return CULL_DYNAMIC;
    }

    /**
     * <code>rotateUpTo</code> is a util function that alters the
     * localrotation to point the Y axis in the direction given by newUp.
     * 
     * @param newUp the up vector to use - assumed to be a unit vector.
     */
    public void rotateUpTo(Vector3f newUp) {
        //First figure out the current up vector.
        Vector3f upY = compVecA.set(Vector3f.UNIT_Y);
        localRotation.multLocal(upY);

        // get angle between vectors
        float angle = upY.angleBetween(newUp);

        //figure out rotation axis by taking cross product
        Vector3f rotAxis = upY.crossLocal(newUp);

        // Build a rotation quat and apply current local rotation.
        Quaternion q = compQuat;
        q.fromAngleAxis(angle, rotAxis);
        q.mult(localRotation, localRotation);
    }


    /**
     * <code>lookAt</code> is a convienence method for auto-setting the
     * local rotation based on a position and an up vector. It computes
     * the rotation to transform the z-axis to point onto 'position'
     * and the y-axis to 'up'. Unlike {@link Quaternion#lookAt} this method
     * takes a world position to look at not a relative direction.
     *
     * @param position
     *            where to look at in terms of world coordinates
     * @param upVector
     *            a vector indicating the (local) up direction.
     *            (typically {0, 1, 0} in jME.)
     */
    public void lookAt(Vector3f position, Vector3f upVector) {
        compVecA.set( position ).subtractLocal( getWorldTranslation() );
        getLocalRotation().lookAt( compVecA, upVector );
    }

    /**
     * Calling this method tells the scenegraph that it is not necessary to
     * update bounds from this point in the scenegraph on down to the leaves.
     * This is useful for performance gains where you have scene items that do
     * not move (at all) or change shape and thus do not need constant
     * re-calculation of boundaries.
     * 
     * When you call lock, the bounds are first updated to ensure current bounds
     * are accurate.
     * 
     * @see #unlockBounds()
     */
    public void lockBounds() {
        updateGeometricState(0, true);
        lockedMode |= LOCKED_BOUNDS;
    }
    
    /**
     * Flags this spatial and those below it in the scenegraph to not
     * recalculate world transforms such as translation, rotation and scale on
     * every update.
     * 
     * This is useful for efficiency when you have scene items that stay in one
     * place all the time as it avoids needless recalculation of transforms.
     * 
     * @see #unlockTransforms()
     */
    public void lockTransforms() {
        updateWorldVectors();
        lockedMode |= LOCKED_TRANSFORMS;
    }
    
    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data.
     * 
     * Calling this method does not provide a guarentee that data changes will
     * not be allowed or will/won't show up in the scene. It is merely a hint to
     * the engine.
     * 
     * @param r A renderer to lock against.
     * @see #unlockMeshes(Renderer)
     */
    public void lockMeshes(Renderer r) {
        updateRenderState();
        lockedMode |= LOCKED_MESH_DATA;
    }
    
    /**
     * Flags this spatial and those below it that any meshes in the specified
     * scenegraph location or lower will not have changes in vertex, texcoord,
     * normal or color data. This allows optimizations by the engine such as
     * creating display lists from the data.
     * 
     * Calling this method does not provide a guarentee that data changes will
     * not be allowed or will/won't show up in the scene. It is merely a hint to
     * the engine.
     * 
     * Calls lockMeshes(Renderer) with the current display system's renderer.
     * 
     * @see #lockMeshes(Renderer)
     */
    public void lockMeshes() {
        lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }
    
    /**
     * Convienence function for locking all aspects of a Spatial.
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes(Renderer)
     */
    public void lock(Renderer r) {
        lockBounds();
        lockTransforms();
        lockMeshes(r);
    }
    
    /**
     * Convienence function for locking all aspects of a Spatial. For lockMeshes
     * it calls:
     * <code>lockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #lockBounds()
     * @see #lockTransforms()
     * @see #lockMeshes(Renderer)
     */
    public void lock() {
        lockBounds();
        lockTransforms();
        lockMeshes();
    }
    
    /**
     * Flags this spatial and those below it to allow for bounds updating (the
     * default).
     * 
     * @see #lockBounds()
     */
    public void unlockBounds() {
        lockedMode &= ~LOCKED_BOUNDS;
    }
    
    /**
     * Flags this spatial and those below it to allow for transform updating (the
     * default).
     * 
     * @see #lockTransforms()
     */
    public void unlockTransforms() {
        lockedMode &= ~LOCKED_TRANSFORMS;
    }
    
    /**
     * Flags this spatial and those below it to allow for mesh updating (the
     * default). Generally this means that any display lists setup will be
     * erased and released.
     * 
     * @param r The renderer used to lock against.
     * @see #lockMeshes(Renderer)
     */
    public void unlockMeshes(Renderer r) {
        lockedMode &= ~LOCKED_MESH_DATA;
    }

    /**
     * Convienence function for unlocking all aspects of a Spatial.
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes(Renderer)
     */
    public void unlock(Renderer r) {
        unlockBounds();
        unlockTransforms();
        unlockMeshes(r);
    }
    
    /**
     * Convienence function for unlocking all aspects of a Spatial. For
     * unlockMeshes it calls:
     * <code>unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());</code>
     * 
     * @see #unlockBounds()
     * @see #unlockTransforms()
     * @see #unlockMeshes(Renderer)
     */
    public void unlock() {
        unlockBounds();
        unlockTransforms();
        unlockMeshes(DisplaySystem.getDisplaySystem().getRenderer());
    }

    /**
     * @return a bitwise combination of the current locks established on this
     *         Spatial.
     */
    public int getLocks() {
        return lockedMode;
    }
    
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
        if ((lockedMode & LOCKED_BOUNDS) == 0) {
            updateWorldBound();
            if (initiator) {
                propagateBoundToRoot();
            }
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
    public void updateWorldData(float time) {
        // update spatial state via controllers
        Object controller;
        for (int i = 0, gSize = geometricalControllers.size(); i < gSize; i++) {
            try {
                controller = geometricalControllers.get( i );
            } catch ( IndexOutOfBoundsException e ) {
                // a controller was removed in Controller.update (note: this may skip one controller)
                break;
            }
            if ( controller != null ) {
                ( (Controller) controller ).update( time );
            }
        }

        updateWorldVectors();
    }

    public void updateWorldVectors() {
        if ((lockedMode & LOCKED_TRANSFORMS) == 0) {
            updateWorldScale();
            updateWorldRotation();
            updateWorldTranslation();
        }
    }

    private void updateWorldTranslation() {
        if (parent != null) {
            worldTranslation = parent.localToWorld( localTranslation, worldTranslation );
        } else {
            worldTranslation.set(localTranslation);
        }
    }

    /**
     * Convert a vector (in) from this spatials local coordinate space to world coordinate space.
     * @param in vector to read from
     * @param store where to write the result (null to create a new vector, may be same as in)
     * @return the result (store)
     */
    public Vector3f localToWorld( final Vector3f in, final Vector3f store ) {
        return getWorldRotation().mult(in,
                store ).multLocal( getWorldScale())
                .addLocal( getWorldTranslation());
    }

    private void updateWorldRotation() {
        if (parent != null) {
            parent.getWorldRotation().mult(localRotation, worldRotation);
        } else {
            worldRotation.set(localRotation);
        }
    }

    private void updateWorldScale() {
        if (parent != null) {
            worldScale.set(parent.getWorldScale()).multLocal(localScale);
        } else {
            worldScale.set(localScale);
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
                if (getRenderState(x) != null)
                    parentStates[x].push(getRenderState(x));
            }
        }

        applyRenderState(parentStates);

        // restore previous if we are not the initiator
        if (!initiator) {
            for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
                if (getRenderState(x) != null)
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
            if (getRenderState(x) != null)
                states[x].push(getRenderState(x));
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
     * Called by {@link Node#attachChild(Spatial)} and {@link Node#detachChild(Spatial)} - don't call directly.
     * <code>setParent</code> sets the parent of this node.
     *
     * @param parent
     *            the parent of this node.
     */
    protected void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * <code>removeFromParent</code> removes this Spatial from it's parent.
     *
     * @return true if it has a parent and performed the remove.
     */
    public boolean removeFromParent() {
        if (parent != null) {
            parent.detachChild(this);
            return true;
        }
        return false;
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
        this.worldRotation.set(this.localRotation);
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
        this.worldRotation.set(this.localRotation);
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
        this.worldScale.set(this.localScale);
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     *
     * @param localScale
     *            the new local scale.
     */
    public void setLocalScale(Vector3f localScale) {
        this.localScale = localScale;
        this.worldScale.set(this.localScale);
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
        this.worldTranslation.set(this.localTranslation);
    }

    /**
     *
     * <code>setRenderState</code> sets a render state for this node. Note,
     * there can only be one render state per type per node. That is, there can
     * only be a single AlphaState a single TextureState, etc. If there is
     * already a render state for a type set the old render state will be
     * returned. Otherwise, null is returned.
     *
     * @param rs
     *            the render state to add.
     * @return the old render state.
     */
    public RenderState setRenderState(RenderState rs) {
        if ( renderStateList == null )
        {
            renderStateList = new RenderState[RenderState.RS_MAX_STATE];
        }
        RenderState oldState = renderStateList[rs.getType()];
        renderStateList[rs.getType()] = rs;
        return oldState;
    }

    /**
     * Returns the requested RenderState that this Spatial currently has set or
     * null if none is set.
     * 
     * @param type
     *            the renderstate type to retrieve
     * @return a renderstate at the given position or null
     */
    public RenderState getRenderState( int type ) {
        return renderStateList != null ? renderStateList[type] : null;
    }

    /**
     * Clears a given render state index by setting it to null.
     *
     * @param renderStateType
     *            The index of a RenderState to clear
     * @see com.jme.scene.state.RenderState#getType()
     */
    public void clearRenderState(int renderStateType) {
        if ( renderStateList != null )
        {
            renderStateList[renderStateType] = null;
        }
    }

    /**
     * Enforce a particular state. In other words, the given state will override
     * any state of the same type set on a scene object. Remember to clear the
     * state when done enforcing. Very useful for multipass techniques where
     * multiple sets of states need to be applied to a scenegraph drawn multiple
     * times.
     * 
     * @param state
     *            state to enforce
     */
    public static void enforceState(RenderState state) {
        Spatial.enforcedStateList[state.getType()] = state;
    }

    /**
     * Clears an enforced render state index by setting it to null. This allows
     * object specific states to be used.
     * 
     * @param renderStateType
     *            The type of RenderState to clear enforcement on.
     */
    public static void clearEnforcedState(int renderStateType) {
        if ( enforcedStateList != null )
        {
            enforcedStateList[renderStateType] = null;
        }
    }

    /**
     * sets all enforced states to null.
     * 
     * @see com.jme.scene.Spatial#clearEnforcedState(int)
     */
    public static void clearEnforcedStates() {
        for (int i = 0; i < enforcedStateList.length; i++)
            enforcedStateList[i] = null;
    }

    /**
	 * <code>setRenderQueueMode</code> determines at what phase of the
	 * rendering proces this Spatial will rendered. There are 4 different
	 * phases:
	 * 
	 * QUEUE_SKIP - The spatial will be drawn as soon as possible, before the
	 * other phases of rendering.
	 * 
	 * QUEUE_OPAQUE - The renderer will try to find the optimal order for
	 * rendering all objects using this mode. You should use this mode for most
	 * normal objects, except transparant ones, as it could give a nice
	 * performance boost to your application.
	 * 
	 * QUEUE_TRANSPARENT - This is the mode you should use for object with
	 * transparancy in them. It will ensure the objects furthest away are
	 * rendered first. That ensures when another transparent object is drawn on
	 * top of previously drawn objects, you can see those (and the object drawn
	 * using SKIP and OPAQUE) through the tranparant parts of the newly drawn
	 * object.
	 * 
	 * QUEUE_ORTHO - This is a special mode, for drawing 2D object without
	 * prespective (such as GUI or HUD parts)
	 * 
	 * Lastly, there is a special mode, QUEUE_INHERIT, that will ensure that
	 * this spatial uses the same mode as the parent Node does.
	 * 
	 * @param renderQueueMode 
	 *            The mode to use for this Spatial.
	 */
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

    /**
     * Returns this spatial's last frustum intersection result. This int is set
     * when a check is made to determine if the bounds of the object fall inside
     * a camera's frustum. If a parent is found to fall outside the frustum, the
     * value for this spatial will not be updated.
     *
     * Possible values include: Camera.OUTSIDE_FRUSTUM,
     * Camera.INTERSECTS_FRUSTUM, and Camera.INSIDE_FRUSTUM
     *
     * @return The spatial's last frustum intersection result.
     */
    public int getLastFrustumIntersection() {
        return frustrumIntersects;
    }

    /**
     * Overrides the last intersection result.  This is useful for
     * operations that want to start rendering at the middle of a
     * scene tree and don't want the parent of that node to
     * influence culling.  (See texture renderer code for example.)
     *
     * Possible values include: Camera.OUTSIDE_FRUSTUM,
     * Camera.INTERSECTS_FRUSTUM, and Camera.INSIDE_FRUSTUM

     * @param intersects the new value, one of those given above.
     */
    public void setLastFrustumIntersection(int intersects) {
        frustrumIntersects = intersects;
    }

    /**
     * sets all current states to null, and therefore forces the use of the
     * default states.
     *
     */
    public static void clearCurrentStates() {
        for (int i = 0; i < currentStates.length; i++)
            currentStates[i] = null;
    }

    /**
     * clears the specified state. The state is referenced by it's int value,
     * and therefore should be called via RenderState's constant list. For
     * example, RenderState.RS_ALPHA.
     *
     * @param state
     *            the state to clear.
     */
    public static void clearCurrentState(int state) {
        currentStates[state] = null;
    }

    public static RenderState getCurrentState(int state) {
        return currentStates[state];
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
     *
     * <code>calculateCollisions</code> calls findCollisions to populate the
     * CollisionResults object then processes the collision results.
     *
     * @param scene
     *            the scene to test against.
     * @param results
     *            the results object.
     */
    public void calculateCollisions(Spatial scene, CollisionResults results) {
        findCollisions(scene, results);
        results.processCollisions();
    }

    /**
     * checks this spatial against a second spatial, any collisions are stored
     * in the results object.
     *
     * @param scene
     *            the scene to test against.
     * @param results
     *            the results of the collisions.
     */
    public abstract void findCollisions(Spatial scene, CollisionResults results);

    public abstract boolean hasCollision(Spatial scene, boolean checkTriangles);

    public void calculatePick(Ray ray, PickResults results) {
        findPick(ray, results);
        results.processPick();
    }

    public abstract void findPick(Ray toTest, PickResults results);
    
    

    /**
     * This method updates the exact bounding tree of any this Spatial. If this
     * spatial has children, the function is called recursivly on its children.
     * Spatial objects, such as text, which don't make sense to have an exact
     * bounds are ignored.
     */
    public void updateCollisionTree() {
    }

    /**
     * Returns the Spatial's name followed by the class of the spatial <br>
     * Example: "MyNode (com.jme.scene.Spatial)
     *
     * @return Spatial's name followed by the class of the Spatial
     */
    public String toString() {
        return name + " (" + this.getClass().getName() + ')';
    }

    public Spatial putClone(Spatial store, CloneCreator properties) {
        if (store == null)
            return null;
        store.renderQueueMode = this.renderQueueMode;
        store.setLocalTranslation(new Vector3f(getLocalTranslation()));
        store.setLocalRotation(new Quaternion(getLocalRotation()));
        store.setLocalScale(new Vector3f(getLocalScale()));
        if ( renderStateList != null )
        {
            if (store.renderStateList == null)
                store.renderStateList = new RenderState[RenderState.RS_MAX_STATE];

            System.arraycopy( renderStateList, 0, store.renderStateList, 0, renderStateList.length );
        }
        Iterator I = geometricalControllers.iterator();
        while (I.hasNext()) {
            Controller c = (Controller) I.next();
            Controller toAdd = c.putClone(null, properties);
            if (toAdd != null)
                store.addController(toAdd);
        }
        properties.originalToCopy.put(this, store);
        return store;
    }
}