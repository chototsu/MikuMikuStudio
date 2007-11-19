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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Spatial</code> defines the base class for scene graph nodes. It
 * maintains a link to a parent, it's local transforms and the world's
 * transforms. All other nodes, such as <code>Node</code> and
 * <code>Geometry</code> are subclasses of <code>Spatial</code>.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Spatial.java,v 1.106 2006-06-01 15:05:39 nca Exp $
 */
public abstract class Spatial extends SceneElement implements Serializable, Savable {

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

    /** Spatial's parent, or null if it has none. */
    protected transient Node parent;

    /** ArrayList of controllers for this spatial. */
    protected ArrayList<Controller> geometricalControllers;

    private static final Vector3f compVecA = new Vector3f();
    private static final Quaternion compQuat = new Quaternion();
    
    /**
     * Empty Constructor to be used internally only.
     */
    public Spatial() {
        localRotation = new Quaternion();
        worldRotation = new Quaternion();
        localTranslation = new Vector3f();
        worldTranslation = new Vector3f();
        localScale = new Vector3f(1.0f, 1.0f, 1.0f);
        worldScale = new Vector3f(1.0f, 1.0f, 1.0f);
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
     * Adds a Controller to this Spatial's list of controllers.
     *
     * @param controller
     *            The Controller to add
     * @see com.jme.scene.Controller
     */
    public void addController(Controller controller) {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>();
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
            geometricalControllers = new ArrayList<Controller>();
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
            geometricalControllers = new ArrayList<Controller>();
        }
        return geometricalControllers.get(i);
    }

    /**
     * Returns the ArrayList that contains this spatial's Controllers.
     *
     * @return This spatial's geometricalControllers.
     */
    public ArrayList getControllers() {
        if (geometricalControllers == null) {
            geometricalControllers = new ArrayList<Controller>();
        }
        return geometricalControllers;
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
        if (cm == SceneElement.CULL_ALWAYS) {
            return;
        } else if (cm == SceneElement.CULL_NEVER) {
            draw(r);
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();

        // check to see if we can cull this node
        frustrumIntersects = (parent != null ? parent.frustrumIntersects
                : Camera.INTERSECTS_FRUSTUM);
        
        
        if (cm == SceneElement.CULL_DYNAMIC && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
            frustrumIntersects = camera.contains(worldBound);
        }
        
        if (frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
            draw(r);
        }
        camera.setPlaneState(state);
    }

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
    

    @Override
    public void lockTransforms() {
        updateWorldVectors();
        lockedMode |= LOCKED_TRANSFORMS;
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
        if ((lockedMode & SceneElement.LOCKED_BOUNDS) == 0) {
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
        if(geometricalControllers != null) {
            Controller controller;
            for (int i = 0, gSize = geometricalControllers.size(); i < gSize; i++) {
                try {
                    controller = geometricalControllers.get( i );
                } catch ( IndexOutOfBoundsException e ) {
                    // a controller was removed in Controller.update (note: this may skip one controller)
                    break;
                }
                if ( controller != null ) {
                    controller.update( time );
                }
            }
        }

        updateWorldVectors();
    }

    public void updateWorldVectors() {
        if (((lockedMode & SceneElement.LOCKED_TRANSFORMS) == 0)) {
            updateWorldScale();
            updateWorldRotation();
            updateWorldTranslation();
        }
    }

    protected void updateWorldTranslation() {
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

    public Vector3f worldToLocal(final Vector3f in, final Vector3f store) {
        in.subtract(getWorldTranslation(), store).divideLocal(getWorldScale());
        getWorldRotation().inverse().mult(store, store);
        return store;
    }

    protected void updateWorldRotation() {
        if (parent != null) {
            parent.getWorldRotation().mult(localRotation, worldRotation);
        } else {
            worldRotation.set(localRotation);
        }
    }

    protected void updateWorldScale() {
        if (parent != null) {
            worldScale.set(parent.getWorldScale()).multLocal(localScale);
        } else {
            worldScale.set(localScale);
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
     * @see #setCullMode(int)
     *
     * @return the cull mode of this spatial, or if set to INHERIT, the cullmode of it's parent.
     */
    public int getCullMode() {
        if (cullMode != CULL_INHERIT)
            return cullMode;
        else if (parent != null)
            return parent.getCullMode();
        else return CULL_DYNAMIC;
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
    
    public int getRenderQueueMode() {
        if (renderQueueMode != Renderer.QUEUE_INHERIT)
            return renderQueueMode;
        else if (parent != null)
            return parent.getRenderQueueMode();
        else
            return Renderer.QUEUE_SKIP;
    }
    
    public int getNormalsMode() {
        if (normalsMode != NM_INHERIT)
            return normalsMode;
        else if (parent != null)
            return parent.getNormalsMode();
        else
            return NM_GL_NORMALIZE_IF_SCALED;
    }

    /**
     * Called during updateRenderState(Stack[]), this function goes up the scene
     * graph tree until the parent is null and pushes RenderStates onto the
     * states Stack array.
     *
     * @param states
     *            The Stack[] to push states onto.
     */
    @SuppressWarnings("unchecked")
    public void propagateStatesFromRoot(Stack[] states) {
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

    public void write(JMEExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        
        capsule.write(localRotation, "localRotation", Quaternion.IDENTITY);
        capsule.write(localTranslation, "localTranslation", Vector3f.ZERO);
        capsule.write(localScale, "localScale", Vector3f.UNIT_XYZ);
        
        capsule.writeSavableArrayList(geometricalControllers, "geometricalControllers", (ArrayList<Savable>)null);
   }

    @SuppressWarnings("unchecked")
	public void read(JMEImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);

        localRotation = (Quaternion)capsule.readSavable("localRotation", Quaternion.IDENTITY);
        localTranslation = (Vector3f)capsule.readSavable("localTranslation", Vector3f.ZERO);
        localScale = (Vector3f)capsule.readSavable("localScale", Vector3f.UNIT_XYZ);

        geometricalControllers = capsule.readSavableArrayList("geometricalControllers", null);
        
        worldRotation = new Quaternion();
        worldTranslation = new Vector3f();
        worldScale = new Vector3f(1.0f, 1.0f, 1.0f);
    }
}
