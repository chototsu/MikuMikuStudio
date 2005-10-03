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
 * @version $Revision: 1.5 $
 */

public class ThirdPersonHandler extends InputHandler {
    public static final String KEY_TURNSPEED = "turnSpeed";
    public static final String KEY_DOGRADUAL = "doGradual";
    public static final String KEY_PERMITTER = "permitter";
    public static final String KEY_UPVECTOR = "upVector";

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

    public ThirdPersonHandler(Node node, Camera cam, String api) {
        this(node, cam, null, api);
    }
    
    public ThirdPersonHandler(Node node, Camera cam, HashMap props, String api) {
        this.node = node;
        setProperties(props);
        setKeyBindings(api, props);
        setActions(cam);
    }

    private void setProperties(HashMap props) {
        if (props == null) {
            return;
        }
        turnSpeed = getFloatProp(props, KEY_TURNSPEED, DEFAULT_TURNSPEED);
    }

    protected float getFloatProp(HashMap props, String key, float defaultVal) {
        if (props.get(key) == null)
            return defaultVal;
        else
            return Float.parseFloat(props.get(key).toString());
    }

    /**
     * 
     * <code>setKeyBindings</code> binds the keys to use for the actions.
     * 
     * @param api
     *            the api to use for the input.
     */
    protected void setKeyBindings(String api, HashMap props) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        InputSystem.createInputSystem(api);

        keyboard.setKeyInput(InputSystem.getKeyInput());
        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("left", KeyInput.KEY_A);
        keyboard.set("right", KeyInput.KEY_D);

        setKeyBindingManager(keyboard);
    }

    /**
     * 
     * <code>setActions</code> sets the keyboard actions with the
     * corresponding key command.
     * 
     * @param cam
     */
    protected void setActions(Camera cam) {
        ThirdPersonForwardAction forward = new ThirdPersonForwardAction(node,
                cam, permitter, 0.5f);
        forward.setKey("forward");
        addAction(forward);
        ThirdPersonBackwardAction backward = new ThirdPersonBackwardAction(
                node, cam, permitter, 0.5f);
        backward.setKey("backward");
        addAction(backward);
        ThirdPersonRightAction right = new ThirdPersonRightAction(node, cam,
                permitter, 1f);
        right.setKey("right");
        addAction(right);
        ThirdPersonLeftAction left = new ThirdPersonLeftAction(node, cam,
                permitter, 1f);
        left.setKey("left");
        addAction(left);
    }

    public void update(float time) {
        prevLoc.set(node.getLocalTranslation());
        loc.set(prevLoc);
        super.update(time);
        loc.subtractLocal(node.getLocalTranslation());
        if (!loc.equals(Vector3f.ZERO)) {
            float distance = loc.length();
            loc.normalizeLocal();
            float actAngle;
            if (loc.x < 0)
                actAngle = FastMath.atan(loc.z / loc.x);
            else if (loc.x > 0)
                actAngle = FastMath.PI + FastMath.atan(loc.z / loc.x);
            else if (loc.z > 0)
                actAngle = FastMath.PI;
            else
                actAngle = 0;
            if (doGradualRotation) {
                float oldAct = actAngle;
                actAngle -= faceAngle;

                // redo: THE FOLLOWING IS SUPPOSED TO CORRECT THE RAPID CHANGE
                // FROM ONE DIR TO ANOTHER... IT DOESN'T.
                if (actAngle == FastMath.PI) {
                    actAngle -= FastMath.PI / 8f;
                    oldAct -= FastMath.PI / 8f;
                }
                actAngle = FastMath.normalizeAngle(actAngle);

                // update rotation
                if (actAngle > 0) {
                    faceAngle += time * turnSpeed;
                    if (faceAngle > oldAct)
                        faceAngle = oldAct;
                } else if (actAngle < 0) {
                    faceAngle -= time * turnSpeed;
                    if (faceAngle < oldAct)
                        faceAngle = oldAct;
                }
            }

            node.getLocalRotation().fromAngleNormalAxis(-faceAngle, upVector);
            node.getLocalTranslation().set(prevLoc);
            node.getLocalRotation().getRotationColumn(0, calcVector).multLocal(
                    distance);
            node.getLocalTranslation().addLocal(calcVector);
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
}
