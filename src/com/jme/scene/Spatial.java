/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;

/**
 * <code>Spatial</code> defines the base class for scene graph nodes. It
 * maintains a link to a parent, it's local transforms and the world's 
 * transforms. All other nodes, such as <code>Node</code> and 
 * <code>Geometry</code> are subclasses of <code>Spatial</code>.
 * @author Mark Powell
 * @version $Id: Spatial.java,v 1.1 2003-10-02 15:01:17 mojomonkey Exp $
 */
public abstract class Spatial {
    //rotation matrices
    private Matrix3f localRotation;
    protected Matrix3f worldRotation;

    //translation vertex
    private Vector3f localTranslation;
    protected Vector3f worldTranslation;

    //scale values
    private float localScale;
    protected float worldScale;

    //flag to cull node
    private boolean forceCull;

    //bounding volume of the world.
    protected BoundingVolume worldBound;

    //reference to the parent node.
    private Node parent;

    /**
     * Constructor instantiates a new <code>Spatial</code> object setting 
     * the rotation, translation and scale value to defaults.
     *
     */
    public Spatial() {
        localRotation = new Matrix3f();
        worldRotation = new Matrix3f();
        localTranslation = new Vector3f();
        worldTranslation = new Vector3f();
        localScale = 1.0f;
        worldScale = 1.0f;
    }

    /**
     * 
     * <code>getWorldBound</code> retrieves the world bound at this node level.
     * @return the world bound at this level.
     */
    public BoundingVolume getWorldBound() {
        return worldBound;
    }

    /**
     * 
     * <code>setWorldBound</code> sets the world bound for this node level.
     * @param worldBound the world bound at this level.
     */
    public void setWorldBound(BoundingVolume worldBound) {
        this.worldBound = worldBound;
    }

    /**
     * 
     * <code>onDraw</code> checks the node with the camera to see if it
     * should be culled, if not, the node's draw method is called.
     * @param r the renderer used for display.
     */
    public void onDraw(Renderer r) {
        if (forceCull) {
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();
        //check to see if we can cull this node
        if (!camera.culled(worldBound)) {
            draw(r);
        }

        camera.setPlaneState(state);
    }

    /**
     * 
     * <code>draw</code> abstract method that handles drawing data to the
     * renderer if it is geometry and passing the call to it's children if it
     * is a node.
     * @param r the renderer used for display.
     */
    public abstract void draw(Renderer r);

    /**
     * 
     * <code>getWorldRotation</code> retrieves the rotation matrix of the 
     * world.
     * @return the world's rotation matrix.
     */
    public Matrix3f getWorldRotation() {
        return worldRotation;
    }

    /**
     * 
     * <code>getWorldTranslation</code> retrieves the translation vector of
     * the world.
     * @return the world's tranlsation vector.
     */
    public Vector3f getWorldTranslation() {
        return worldTranslation;
    }

    /**
     * 
     * <code>getWorldScale</code> retrieves the scale factor of the world.
     * @return the world's scale factor.
     */
    public float getWorldScale() {
        return worldScale;
    }

    /**
     * 
     * <code>isForceCulled</code> reports if this node should always be 
     * culled or not. If true, this node will not be displayed.
     * @return true if this node should never be displayed, false otherwise.
     */
    public boolean isForceCulled() {
        return forceCull;
    }

    /**
     * 
     * <code>setForceCull</code> sets if this node should always be culled or
     * not. True will always cull the node, false will allow proper culling to
     * take place.
     * @param forceCull the value for forcing a culling.
     */
    public void setForceCull(boolean forceCull) {
        this.forceCull = forceCull;
    }

    /**
     * 
     * <code>updateGeometricState</code> updates all the geometry information
     * for the node.
     * @param time the frame time.
     * @param initiator true if this node started the update process.
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
     * @param time the frame time.
     */
    public void updateWorldData(float time) {
        // update spatial controllers
        boolean computesWorldTransform = false;

        // update world transforms
        if (!computesWorldTransform) {
            if (parent != null) {
                worldScale = parent.getWorldScale() * localScale;
                worldRotation = parent.getWorldRotation().mult(localRotation);
                worldTranslation =
                    parent.getWorldTranslation().add(
                        (
                            parent.getWorldRotation().mult(
                                localTranslation)).mult(
                            parent.getWorldScale()));

            } else {
                worldScale = localScale;
                worldRotation = localRotation;
                worldTranslation = localTranslation;
            }
        }
    }

    /**
     * 
     * <code>updateWorldBound</code> updates the bounding volume of the 
     * world. Abstract, geometry transforms the bound while node merges 
     * the children's bound.
     *
     */
    public abstract void updateWorldBound();

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
     * @return the parent of this node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * <code>setParent</code> sets the parent of this node.
     * @param parent the parent of this node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * <code>getLocalRotation</code> retrieves the local rotation of this
     * node.
     * @return the local rotation of this node.
     */
    public Matrix3f getLocalRotation() {
        return localRotation;
    }

    /**
     * <code>setLocalRotation</code> sets the local rotation of this node.
     * @param localRotation the new local rotation.
     */
    public void setLocalRotation(Matrix3f localRotation) {
        this.localRotation = localRotation;
    }

    /**
     * <code>getLocalScale</code> retrieves the local scale of this node.
     * @return the local scale of this node.
     */
    public float getLocalScale() {
        return localScale;
    }

    /**
     * <code>setLocalScale</code> sets the local scale of this node.
     * @param localScale the new local scale.
     */
    public void setLocalScale(float localScale) {
        this.localScale = localScale;
    }

    /**
     * <code>getLocalTranslation</code> retrieves the local translation of 
     * this node.
     * @return the local translation of this node.
     */
    public Vector3f getLocalTranslation() {
        return localTranslation;
    }

    /**
     * <code>setLocalTranslation</code> sets the local translation of this
     * node.
     * @param localTranslation the local translation of this node.
     */
    public void setLocalTranslation(Vector3f localTranslation) {
        this.localTranslation = localTranslation;
    }
}
