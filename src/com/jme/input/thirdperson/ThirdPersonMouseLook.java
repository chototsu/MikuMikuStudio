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

package com.jme.input.thirdperson;

import java.util.HashMap;

import com.jme.input.ChaseCamera;
import com.jme.input.InputHandler;
import com.jme.input.Mouse;
import com.jme.input.RelativeMouse;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

public class ThirdPersonMouseLook extends MouseInputAction {
    
    public static final String PROP_MAXASCENT = "maxAscent";
    public static final String PROP_MAXROLLOUT = "maxRollOut";
    public static final String PROP_MINROLLOUT = "minRollOut";
    public static final String PROP_MOUSEXMULT = "maxAscent";
    public static final String PROP_MOUSEYMULT = "maxRollOut";
    public static final String PROP_MOUSEROLLMULT = "minRollOut";
    public static final String PROP_INVERTEDY = "invertedY";

    public static final float DEFAULT_MOUSEXMULT = 1f / 50f;
    public static final float DEFAULT_MOUSEYMULT = 10f / 50f;
    public static final float DEFAULT_MOUSEROLLMULT = 60f / 50f;
    public static final float DEFAULT_MAXASCENT = 45 * FastMath.DEG_TO_RAD;
    public static final float DEFAULT_MAXROLLOUT = 240;
    public static final float DEFAULT_MINROLLOUT = 20;
    public static final boolean DEFAULT_INVERTEDY = false;

    protected float maxAscent = DEFAULT_MAXASCENT;
    protected float maxRollOut = DEFAULT_MAXROLLOUT;
    protected float minRollOut = DEFAULT_MINROLLOUT;
    protected float mouseXMultiplier = DEFAULT_MOUSEXMULT;
    protected float mouseYMultiplier = DEFAULT_MOUSEYMULT;
    protected float mouseRollMultiplier = DEFAULT_MOUSEROLLMULT;
    protected float mouseXSpeed;
    protected float mouseYSpeed;
    protected float rollInSpeed;
    protected ChaseCamera camera;
    protected Spatial target;
    protected boolean updated;
    protected boolean invertedY = DEFAULT_INVERTEDY;
    protected Vector3f difTemp = new Vector3f();
    protected Vector3f sphereTemp = new Vector3f();
    protected Vector3f rightTemp = new Vector3f();

    /**
     * Constructor creates a new <code>MouseLook</code> object. It takes the
     * mouse, camera and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param camera
     *            the camera to move.
     */
    public ThirdPersonMouseLook(Mouse mouse, ChaseCamera camera, Spatial target) {
        this.mouse = (RelativeMouse) mouse;
        this.camera = camera;
        this.target = target;

        setSpeed(1);
    }

    /**
     * 
     * <code>updateProperties</code>
     * @param props
     */
    public void updateProperties(HashMap props) {
        maxAscent = InputHandler.getFloatProp(props, PROP_MAXASCENT, DEFAULT_MAXASCENT);
        maxRollOut = InputHandler.getFloatProp(props, PROP_MAXROLLOUT, DEFAULT_MAXROLLOUT);
        minRollOut = InputHandler.getFloatProp(props, PROP_MINROLLOUT, DEFAULT_MINROLLOUT);
        setMouseXMultiplier(InputHandler.getFloatProp(props, PROP_MOUSEXMULT, DEFAULT_MOUSEXMULT));
        setMouseYMultiplier(InputHandler.getFloatProp(props, PROP_MOUSEYMULT, DEFAULT_MOUSEYMULT));
        setMouseRollMultiplier(InputHandler.getFloatProp(props, PROP_MAXROLLOUT, DEFAULT_MOUSEROLLMULT));
        invertedY = InputHandler.getBooleanProp(props, PROP_INVERTEDY, DEFAULT_INVERTEDY);
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * 
     * @param speed
     *            the speed of the mouse look.
     */
    public void setSpeed(float speed) {
        super.setSpeed( speed );
        mouseXSpeed = mouseXMultiplier * speed;
        mouseYSpeed = mouseYMultiplier * speed;
        rollInSpeed = mouseRollMultiplier * speed;
    }

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.MouseInputAction#performAction
     */
    public void performAction(InputActionEvent event) {
        float time = event.getTime();
        if (mouse.getLocalTranslation().x != 0) {
            float amount = time * mouse.getLocalTranslation().x;
            rotateRight(amount);
            updated = true;
        }
        if (mouse.getLocalTranslation().y != 0) {
            float amount = time * mouse.getLocalTranslation().y;
            rotateUp(amount);
            updated = true;
        }
        int wdelta = MouseInput.get().getWheelDelta();
        if (wdelta != 0) {
            float amount = time * -wdelta;
            rollIn(amount);
            updated = true;
        }

        if (updated)
            camera.getCamera().onFrameChange();
    }

    /**
     * <code>rotateRight</code> updates the azimuth values of the camera's
     * spherical coordinates.
     * 
     * @param amount
     */
    private void rotateRight(float amount) {
        Vector3f camPos = camera.getCamera().getLocation();
        Vector3f targetPos = target.getWorldTranslation();

        float azimuthAccel = (amount * mouseXSpeed);
        difTemp.set(camPos).subtractLocal(targetPos);
        FastMath.cartesianToSpherical(difTemp, sphereTemp);
        sphereTemp.y = FastMath.normalize(sphereTemp.y + (azimuthAccel),
                -FastMath.TWO_PI, FastMath.TWO_PI);
        FastMath.sphericalToCartesian(sphereTemp, rightTemp);
        rightTemp.addLocal(targetPos);
        camPos.set(rightTemp);
    }

    /**
     * <code>rotateRight</code> updates the altitude values of the camera's
     * spherical coordinates.
     * 
     * @param amount
     */
    private void rotateUp(float amount) {
        if (invertedY)
            amount *= -1;
        Vector3f camPos = camera.getCamera().getLocation();
        Vector3f targetPos = target.getWorldTranslation();

        float thetaAccel = (amount * mouseYSpeed);
        difTemp.set(camPos).subtractLocal(targetPos).subtractLocal(
                camera.getTargetOffset());
        FastMath.cartesianToSpherical(difTemp, sphereTemp);
        camera.getIdealSphereCoords().z = clampUpAngle(sphereTemp.z
                + (thetaAccel));
    }

    private void rollIn(float amount) {
        camera.getIdealSphereCoords().x = clampRollIn(camera
                .getIdealSphereCoords().x
                + (amount * rollInSpeed));
    }

    /**
     * normalizeUpAngle
     * 
     * @param r
     *            float
     * @return float
     */
    private float clampUpAngle(float r) {
        if (Float.isInfinite(r) || Float.isNaN(r))
            return r;
        if (r > maxAscent)
            r = maxAscent;
        else if (r < -maxAscent)
            r = -maxAscent;
        return r;
    }

    /**
     * normalizeUpAngle
     * 
     * @param r
     *            float
     * @return float
     */
    private float clampRollIn(float r) {
        if (Float.isInfinite(r) || Float.isNaN(r))
            return 100f;
        if (r > maxRollOut)
            r = maxRollOut;
        else if (r < minRollOut)
            r = minRollOut;
        return r;
    }

    /**
     * 
     * @param invertY
     *            boolean
     */
    public void setInvertedY(boolean invertY) {
        this.invertedY = invertY;
    }

    /**
     * 
     * @return boolean
     */
    public boolean isInvertedY() {
        return invertedY;
    }

    /**
     * @return Returns the maxAscent.
     */
    public float getMaxAscent() {
        return maxAscent;
    }

    /**
     * @param maxAscent
     *            The maxAscent to set.
     */
    public void setMaxAscent(float maxAscent) {
        this.maxAscent = maxAscent;
    }

    /**
     * @return Returns the maxRollOut.
     */
    public float getMaxRollOut() {
        return maxRollOut;
    }

    /**
     * @param maxRollOut
     *            The maxRollOut to set.
     */
    public void setMaxRollOut(float maxRollOut) {
        this.maxRollOut = maxRollOut;
    }

    /**
     * @return Returns the minRollOut.
     */
    public float getMinRollOut() {
        return minRollOut;
    }

    /**
     * @param minRollOut The minRollOut to set.
     */
    public void setMinRollOut(float minRollOut) {
        this.minRollOut = minRollOut;
    }

    /**
     * @return Returns the mouseXMultiplier.
     */
    public float getMouseXMultiplier() {
        return mouseXMultiplier;
    }

    /**
     * @param mouseXMultiplier The mouseXMultiplier to set.  Updates mouseXSpeed as well.
     */
    public void setMouseXMultiplier(float mouseXMultiplier) {
        if (this.mouseXMultiplier != 0) {
            float speed = mouseXSpeed / this.mouseXMultiplier;
            mouseXSpeed = speed * mouseXMultiplier;
        }
        this.mouseXMultiplier = mouseXMultiplier;
    }

    /**
     * @return Returns the mouseYMultiplier.
     */
    public float getMouseYMultiplier() {
        return mouseYMultiplier;
    }

    /**
     * @param mouseYMultiplier The mouseYMultiplier to set.  Updates mouseYSpeed as well.
     */
    public void setMouseYMultiplier(float mouseYMultiplier) {
        if (this.mouseYMultiplier != 0) {
            float speed = mouseYSpeed / this.mouseYMultiplier;
            mouseYSpeed = speed * mouseYMultiplier;
        }
        this.mouseYMultiplier = mouseYMultiplier;
    }

    /**
     * @return Returns the mouseRollMultiplier.
     */
    public float getMouseRollMultiplier() {
        return mouseRollMultiplier;
    }

    /**
     * @param mouseRollMultiplier The mouseRollMultiplier to set.  Updates rollInSpeed as well.
     */
    public void setMouseRollMultiplier(float mouseRollMultiplier) {
        if (this.mouseRollMultiplier != 0) {
            float speed = rollInSpeed / this.mouseRollMultiplier;
            rollInSpeed = speed * mouseRollMultiplier;
        }
        this.mouseRollMultiplier = mouseRollMultiplier;
    }
}
