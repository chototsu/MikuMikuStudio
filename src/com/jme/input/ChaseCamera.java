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

package com.jme.input;

import java.util.HashMap;

import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Spatial;

/**
 * <p>
 * Camera handler that will smoothly follow a set scene element, allowing for
 * rotation about and zoom on that element.
 * </p>
 * 
 * <p>
 * see the javadoc for update(float time) for information on how positioning
 * works.
 * </p>
 * 
 * @author <a href="mailto:josh@renanse.com">Joshua Slack</a>
 * @version $Revision: 1.8 $
 */

public class ChaseCamera extends InputHandler {
    public static final String PROP_INITIALSPHERECOORDS = "sphereCoords";
    public static final String PROP_DAMPINGK = "sphereCoords";
    public static final String PROP_SPRINGK = "sphereCoords";
    public static final String PROP_TARGETOFFSET = "targetOffset";
    public static final String PROP_WORLDUPVECTOR = "worldUpVec";
    public static final String PROP_DISABLESPRING = "disableSpring";

    public static final float DEFAULT_DAMPINGK = 12.0f;
    public static final float DEFAULT_SPRINGK = 36.0f;
    public static final boolean DEFAULT_DISABLESPRING = false;
    public static final Vector3f DEFAULT_WORLDUPVECTOR = new Vector3f(Vector3f.UNIT_Y);

    protected Vector3f idealSphereCoords;
    protected Vector3f idealPosition = new Vector3f();
    protected Camera cam;
    protected Vector3f velocity = new Vector3f();
    protected Spatial target;
    
    protected float dampingK;
    protected float springK;
    protected boolean disableSpring;

    protected Vector3f dirVec = new Vector3f();
    protected Vector3f worldUpVec = new Vector3f(DEFAULT_WORLDUPVECTOR);
    protected Vector3f upVec = new Vector3f();
    protected Vector3f leftVec = new Vector3f();
    protected Vector3f targetOffset = new Vector3f();
    protected Vector3f targetPos = new Vector3f();
    protected Vector3f oldCameraDir = new Vector3f();
    protected Vector3f compVect = new Vector3f();

    /** The ThirdPersonMouseLook action, kept as a field to allow easy access to setting speeds and y axis flipping. */
    protected ThirdPersonMouseLook mouseLook;

    /**
     * Simple constructor that accepts a Camera and a target and sets all
     * properties to their defaults.
     * 
     * @param cam
     *            Camera to control
     * @param target
     *            the target node to chase
     * @param api
     *            a string denoting the input api to use
     */
    public ChaseCamera(Camera cam, Spatial target, String api) {
        this(cam, target, null, api);
    }
    
    /**
     * More involved constructor allowing the setting of all member fields in
     * ChaseCamera and its associated ThirdPersonMouseLook object via a HashMap
     * of properties.
     * 
     * @param cam
     *            Camera to control
     * @param target
     *            the target node to chase
     * @param props
     *            a hashmap of properties to set this camera up with. keys are
     *            from the statics ChaseCamera.PROP_XXXX and
     *            ThirdPersonMouseLook.PROP_XXXX
     * @param api
     *            a string denoting the input api to use
     */
    public ChaseCamera(Camera cam, Spatial target, HashMap props, String api) {
        super();
        this.cam = cam;
        this.target = target;

        setupMouse(api);
        updateProperties(props);
    }

    /**
     * Set up a relative mouse and the ThirdPersonMouseLook used in this
     * camera's control.
     * 
     * @param api
     *            input system to use
     */
    private void setupMouse(String api) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(MouseInput.get());
        setMouse(mouse);

        if (mouseLook != null)
            removeAction(mouseLook);
        
        mouseLook = new ThirdPersonMouseLook(mouse, this, target);
        addAction(mouseLook);
    }

    /**
     * <code>updateProperties</code> allows you to update all properties of
     * this chase camera and its related mouse look class.
     * 
     * @param props
     */
    public void updateProperties(HashMap props) {
        mouseLook.updateProperties(props);
        
        if (idealSphereCoords == null)
            idealSphereCoords = ((Vector3f)getObjectProp(props, PROP_INITIALSPHERECOORDS, new Vector3f((mouseLook.getMaxRollOut()-mouseLook.getMinRollOut()) / 2f, 0, 0)));
        
        worldUpVec = (Vector3f)getObjectProp(props, PROP_WORLDUPVECTOR, DEFAULT_WORLDUPVECTOR);
        targetOffset = (Vector3f)getObjectProp(props, PROP_TARGETOFFSET, new Vector3f());

        dampingK = getFloatProp(props, PROP_DAMPINGK, DEFAULT_DAMPINGK);
        springK = getFloatProp(props, PROP_SPRINGK, DEFAULT_SPRINGK);
        
        disableSpring = getBooleanProp(props, PROP_DISABLESPRING, DEFAULT_DISABLESPRING);
    }

    public void setCamera(Camera cam) {
        this.cam = cam;
    }

    public Camera getCamera() {
        return cam;
    }

    /**
     * <p>
     * <code>update</code> repositions the camera based on the current
     * position and an ideal position using spherical coordinates.
     * </p>
     * 
     * <p>
     * The new position is determined by checking where the target has moved to
     * and getting an offset in relation to the XZ plane. Using this, the ideal
     * spherical coordiantes for the camera are updated for the angle around the
     * up axis (azimuth). Thus, ideal height and distance from the target are
     * still the same regardless of how far the target has moved.
     * </p>
     * 
     * <p>
     * Next, we using a spring system to move from the camera's current position
     * to the calculated "ideal position". This is done by accelerating towards
     * the ideal position using the amount of that distance and the springK
     * factor. This acceleration is damped by the dampingK factor amplified by
     * the magnitude of the current velocity of the camera.
     * </p>
     * 
     * <p>
     * The springK and dampingK factors can be expressed as a damping ratio:
     * </p>
     * 
     * <pre>
     * ratio = dampingK / (2 * sqrt(springK))
     * </pre>
     * 
     * <p>
     * Typically you want the ratio to come out equal to 1. Values less than 1
     * will ocillate before coming to rest. Values over 1 will take longer than
     * necessary to come to equilibrium.
     * </p>
     * 
     * <p>
     * Note that if disableSpring is true, the currentPosition is always set to the idealPosition.
     * </p>
     * 
     * <p>
     * <i>See Game programming Gems #4 pgs 303-307 for more in-depth information
     * on the technique.</i>
     * </p>
     * 
     * @param time
     *            amount of time since last update (in seconds)
     * @see com.jme.input.InputHandler#update(float)
     */
    public void update(float time) {
        super.update(time);
        Vector3f camPos = cam.getLocation();
        targetPos.set(target.getWorldTranslation());

        if (!Vector3f.isValidVector(camPos)
                || !Vector3f.isValidVector(targetPos))
            return;

        targetPos.addLocal(targetOffset);

        // update camera's ideal azimuth
        float offX = (camPos.x - targetPos.x);
        float offZ = (camPos.z - targetPos.z);
        if (worldUpVec.x == 1) {
            offX = (camPos.y - targetPos.y);
        } else if (worldUpVec.z == 1) {
            offZ = (camPos.y - targetPos.y);
        }
        idealSphereCoords.y = FastMath.atan2(offZ, offX);

        // determine ideal position in cartesian space
        FastMath.sphericalToCartesian(idealSphereCoords, idealPosition).addLocal(targetPos);

        if (disableSpring) {
            camPos.set(idealPosition);
        } else {
            // Determine displacement from current to ideal position
            // Use the spring constants to accelerate towards the ideal position
            Vector3f displace = compVect; 
            camPos.subtract(idealPosition, displace);
            displace.multLocal(-springK).subtractLocal(velocity.x * dampingK,
                    velocity.y * dampingK, velocity.z * dampingK);
    
            velocity.addLocal(displace.multLocal(time));
            camPos.addLocal(velocity.x * time, velocity.y * time, velocity.z
                            * time);
        }
        
        // Look at our target
        cam.lookAt(targetPos, worldUpVec);
    }

    public Vector3f getIdealSphereCoords() {
        return idealSphereCoords;
    }

    public Vector3f getIdealPosition() {
        return idealPosition;
    }

    /**
     * @return Returns the dampingK.
     */
    public float getDampingK() {
        return dampingK;
    }

    /**
     * @param dampingK The dampingK to set.
     */
    public void setDampingK(float dampingK) {
        this.dampingK = dampingK;
    }

    /**
     * @return Returns the springK.
     */
    public float getSpringK() {
        return springK;
    }

    /**
     * @param springK The springK to set.
     */
    public void setSpringK(float springK) {
        this.springK = springK;
    }

    /**
     * @return Returns the target.
     */
    public Spatial getTarget() {
        return target;
    }

    /**
     * @param target The target to set
     */
    public void setTarget(Spatial target) {
        this.target = target;
    }

    /**
     * @return Returns the targetOffset.
     */
    public Vector3f getTargetOffset() {
        return targetOffset;
    }

    /**
     * @param targetOffset The targetOffset to set (as copy)
     */
    public void setTargetOffset(Vector3f targetOffset) {
        this.targetOffset.set(targetOffset);
    }

    /**
     * @param worldUpVec The worldUpVec to set (as copy)
     */
    public void setWorldUpVec(Vector3f worldUpVec) {
        this.worldUpVec.set(worldUpVec);
    }

    /**
     * @return Returns the mouseLook.
     */
    public ThirdPersonMouseLook getMouseLook() {
        return mouseLook;
    }

    /**
     * @return Returns the disableSpring.
     */
    public boolean isDisableSpring() {
        return disableSpring;
    }

    /**
     * @param disableSpring The disableSpring to set.
     */
    public void setDisableSpring(boolean disableSpring) {
        this.disableSpring = disableSpring;
    }
}
