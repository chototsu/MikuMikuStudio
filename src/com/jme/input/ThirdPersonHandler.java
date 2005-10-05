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

import com.jme.input.thirdperson.MovementPermitter;
import com.jme.input.thirdperson.ThirdPersonBackwardAction;
import com.jme.input.thirdperson.ThirdPersonForwardAction;
import com.jme.input.thirdperson.ThirdPersonLeftAction;
import com.jme.input.thirdperson.ThirdPersonRightAction;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

/**
 * <code>ThirdPersonHandler</code> defines an InputHandler that sets input to
 * be controlled similar to games such as Zelda Windwaker and Mario 64, etc.
 * 
 * @author <a href="mailto:josh@renanse.com">Joshua Slack</a>
 * @version $Revision: 1.10 $
 */

public class ThirdPersonHandler extends InputHandler {
    public static final String PROP_TURNSPEED = "turnSpeed";
    public static final String PROP_DOGRADUAL = "doGradual";
    public static final String PROP_PERMITTER = "permitter";
    public static final String PROP_UPVECTOR = "upVector";
    public static final String PROP_LOCKBACKWARDS = "lockBackwards";
    public static final String PROP_CAMERAALIGNEDMOVE = "cameraAlignedMovement";

    public static final String PROP_KEY_FORWARD = "fwdKey";
    public static final String PROP_KEY_BACKWARD = "backKey";
    public static final String PROP_KEY_LEFT = "leftKey";
    public static final String PROP_KEY_RIGHT = "rightKey";
    public static final String PROP_KEY_STRAFELEFT = "strfLeftKey";
    public static final String PROP_KEY_STRAFERIGHT = "strfRightKey";
    
    /** Default character turn speed is 1.5pi per sec. */
    public static final float DEFAULT_TURNSPEED = 1.5f * FastMath.PI;
    
    /** The node we are controlling with this handler. */
    protected Node node;

    /**
     * The previous location of the node... used to maintain where the node is
     * before actions are run. This allows a comparison to see where the node
     * wants to be taken.
     */
    protected Vector3f prevLoc = new Vector3f();

    /**
     * Stores the new location of the node after actions. used internally by
     * update method
     */
    protected Vector3f loc = new Vector3f();

    /**
     * The current facing direction of the controlled target in radians in terms
     * of relationship to the world.
     */
    protected float faceAngle;

    /**
     * How fast the character can turn per second. Used when doGradualRotation
     * is set to true.
     */
    protected float turnSpeed = DEFAULT_TURNSPEED;

    /**
     * When true, the controlled target will do turns by moving forward and
     * turning at the same time. When false, a turn will cause immediate
     * rotation to the given angle.
     */
    protected boolean doGradualRotation = true;

    /**
     * When not null, gives a means for denying movement to the controller. See
     * MovementPermitter javadoc for more.
     */
    protected MovementPermitter permitter;

    /** World up vector.  Currently 0,1,0 is the only guarenteed value to work. */
    protected Vector3f upVector = new Vector3f(0, 1, 0);

    /** An internal vector used for calculations to prevent object creation. */
    protected Vector3f calcVector = new Vector3f();

    /** The camera this handler uses for determining action movement. */
    protected Camera camera;
    
    /**
     * if true, backwards movement will not cause the target to rotate around to
     * point backwards. (useful for vehicle movement) Default is false.
     */
    protected boolean lockBackwards;
    
    /**
     * if true, movements of the character are in relation to the current camera
     * view. If false, they are in relation to the current target's facing
     * vector. Default is true.
     */
    protected boolean cameraAlignedMovement;
    
    /**
     * if true, backwards movement will not cause the target to rotate around to
     * point backwards. (useful for vehicle movement) Default is false.
     */
    protected boolean walkingBackwards;

    /**
     * Basic constructor for the ThirdPersonHandler. Sets all non specified args
     * to their defaults.
     * 
     * @param node
     *            the target to move
     * @param cam
     *            the camera for movements to be in relation to
     * @param api
     *            the api to use for the underlying input system
     */
    public ThirdPersonHandler(Node node, Camera cam, String api) {
        this(node, cam, null, api);
    }
    
    /**
     * Full constructor for the ThirdPersonHandler. Properties in the props arg
     * will be used to set handler fields if set, otherwise default values are
     * used.
     * 
     * @param node
     *            the target to move
     * @param cam
     *            the camera for movements to be in relation to
     * @param props
     *            a hashmap of properties used to set handler characteristics
     *            where the key is one of this class's static PROP_XXXX fields.
     * @param api
     *            the api to use for the underlying input system
     */
    public ThirdPersonHandler(Node node, Camera cam, HashMap props, String api) {
        this.node = node;
        this.camera = cam;
        setupKeyboard(api);
        setActions();
        updateProperties(props);
    }

    /**
     * 
     * <code>setProperties</code> sets up class fields from the given hashmap.
     * It also calls updateKeyBindings for you.
     * 
     * @param props
     */
    private void updateProperties(HashMap props) {
        turnSpeed = getFloatProp(props, PROP_TURNSPEED, DEFAULT_TURNSPEED);
        doGradualRotation = getBooleanProp(props, PROP_DOGRADUAL, true);
        lockBackwards = getBooleanProp(props, PROP_LOCKBACKWARDS, false);
        cameraAlignedMovement = getBooleanProp(props, PROP_CAMERAALIGNEDMOVE, true);
        permitter = (MovementPermitter)getObjectProp(props, PROP_PERMITTER, null);
        upVector = (Vector3f)getObjectProp(props, PROP_UPVECTOR, new Vector3f(Vector3f.UNIT_Y));
        updateKeyBindings(props);
    }

    /**
     * 
     * <code>setupKeyboard</code>
     * @param api
     */
    protected void setupKeyboard(String api) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        InputSystem.createInputSystem(api);

        keyboard.setKeyInput(InputSystem.getKeyInput());
        setKeyBindingManager(keyboard);
    }

    /**
     * 
     * <code>updateKeyBindings</code> allows a user to update the keys mapped to the various actions.
     * 
     * @param props
     */
    public void updateKeyBindings(HashMap props) {
        keyboard.set(PROP_KEY_FORWARD, getIntProp(props, PROP_KEY_FORWARD, KeyInput.KEY_W));
        keyboard.set(PROP_KEY_BACKWARD, getIntProp(props, PROP_KEY_BACKWARD, KeyInput.KEY_S));
        keyboard.set(PROP_KEY_LEFT, getIntProp(props, PROP_KEY_LEFT, KeyInput.KEY_A));
        keyboard.set(PROP_KEY_RIGHT, getIntProp(props, PROP_KEY_RIGHT, KeyInput.KEY_D));
        keyboard.set(PROP_KEY_STRAFELEFT, getIntProp(props, PROP_KEY_STRAFELEFT, KeyInput.KEY_Q));
        keyboard.set(PROP_KEY_STRAFERIGHT, getIntProp(props, PROP_KEY_STRAFERIGHT, KeyInput.KEY_E));        
    }

    /**
     * 
     * <code>setActions</code> sets the keyboard actions with the
     * corresponding key command.
     * 
     * @param cam
     */
    protected void setActions() {
        ThirdPersonForwardAction forward = new ThirdPersonForwardAction(this, 0.5f);
        forward.setKey(PROP_KEY_FORWARD);
        addAction(forward);
        ThirdPersonBackwardAction backward = new ThirdPersonBackwardAction(this, 0.5f);
        backward.setKey(PROP_KEY_BACKWARD);
        addAction(backward);
        ThirdPersonRightAction right = new ThirdPersonRightAction(this, 1f);
        right.setKey(PROP_KEY_RIGHT);
        addAction(right);
        ThirdPersonLeftAction left = new ThirdPersonLeftAction(this, 1f);
        left.setKey(PROP_KEY_LEFT);
        addAction(left);
    }

    /**
     * <code>update</code> updates the position and rotation of the target
     * based on the movement requested by the user.
     * 
     * @param time
     * @see com.jme.input.InputHandler#update(float)
     */
    public void update(float time) {
        prevLoc.set(node.getLocalTranslation());
        loc.set(prevLoc);
        super.update(time);
        loc.subtractLocal(node.getLocalTranslation());
        if (!loc.equals(Vector3f.ZERO)) {
            float distance = loc.length();
            if (distance != 0)
                loc.divideLocal(distance); // this == normalizeLocal.
            
            loc.negateLocal();
            
            float actAngle = 0;
            if (upVector.y == 1) {
                actAngle = FastMath.atan2(loc.z, loc.x);
            } else if (upVector.x == 1) {
                actAngle = FastMath.atan2(loc.z, loc.y);
            } else if (upVector.z == 1) {
                actAngle = FastMath.atan2(loc.y, loc.x);
            }
            
            actAngle = FastMath.normalize(actAngle, -FastMath.TWO_PI, FastMath.TWO_PI);

            System.err.println("actAngle: "+actAngle);
            
            calcFaceAngle(actAngle, time);

            node.getLocalTranslation().set(prevLoc);
            node.getLocalRotation().fromAngleNormalAxis(-faceAngle, upVector);
            node.getLocalRotation().getRotationColumn(0, calcVector).multLocal(distance);
            if (lockBackwards && walkingBackwards) {
                node.getLocalTranslation().subtractLocal(calcVector);
                System.err.println("WALKING BACKWARDS");
                walkingBackwards = false;
            } else
                node.getLocalTranslation().addLocal(calcVector);
        }
    }

    /**
     * <code>calcFaceAngle</code>
     * @param actAngle
     * @param time
     */
    private void calcFaceAngle(float actAngle, float time) {
        if (doGradualRotation) {
            faceAngle = FastMath.normalize(faceAngle, -FastMath.TWO_PI, FastMath.TWO_PI);

            // Check the difference between action angle and current facing angle.
            actAngle -= faceAngle;
            if (actAngle > FastMath.PI)
                actAngle -= FastMath.TWO_PI;
            else if (actAngle < -FastMath.PI)
                actAngle += FastMath.TWO_PI;

            if (lockBackwards && walkingBackwards) {
                // update faceangle rotation towards action angle
                if (actAngle > 0)
                    faceAngle -= time * turnSpeed;
                else if (actAngle <= 0)
                    faceAngle += time * turnSpeed;
            } else {
                // update faceangle rotation towards action angle
                if (actAngle > 0)
                    faceAngle += time * turnSpeed;
                else if (actAngle <= 0)
                    faceAngle -= time * turnSpeed;
            }
        } else {
            if (lockBackwards && walkingBackwards)
                faceAngle = FastMath.PI + actAngle;
            else
                faceAngle = actAngle;
        }
    }

    /**
     * @return Returns the turnSpeed.
     */
    public float getTurnSpeed() {
        return turnSpeed;
    }

    /**
     * @param turnSpeed
     *            The turnSpeed to set.
     */
    public void setTurnSpeed(float turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    /**
     * @return Returns the upAngle.
     */
    public Vector3f getUpVector() {
        return upVector;
    }

    /**
     * @param upAngle
     *            The upAngle to set (as copy)
     */
    public void setUpVector(Vector3f upAngle) {
        this.upVector.set(upAngle);
    }

    /**
     * @return Returns the faceAngle (in radians)
     */
    public float getFaceAngle() {
        return faceAngle;
    }

    /**
     * @return Returns the doGradualRotation.
     */
    public boolean isDoGradualRotation() {
        return doGradualRotation;
    }

    /**
     * @param doGradualRotation
     *            The doGradualRotation to set.
     */
    public void setDoGradualRotation(boolean doGradualRotation) {
        this.doGradualRotation = doGradualRotation;
    }

    public MovementPermitter getPermitter() {
        return permitter;
    }

    public Node getTarget() {
        return node;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setLockBackwards(boolean b) {
        lockBackwards = b;
    }

    public boolean isLockBackwards() {
        return lockBackwards;
    }

    public void setCameraAlignedMovement(boolean b) {
        cameraAlignedMovement = b;
    }

    public boolean isCameraAlignedMovement() {
        return cameraAlignedMovement;
    }

    /**
     * Internal method used to let the handler know that the target is currently
     * moving backwards (via use of the back key.)
     * 
     * @param backwards
     */
    public void setGoingBackwards(boolean backwards) {
        walkingBackwards = backwards;
    }
}
