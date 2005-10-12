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

package com.jme.input.action;

import com.jme.input.Mouse;
import com.jme.input.RelativeMouse;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>MouseLook</code> defines a mouse action that detects mouse movement
 * and converts it into camera rotations and camera tilts.
 * 
 * @author Mark Powell
 * @version $Id: MouseLook.java,v 1.13 2005-10-12 04:00:32 renanse Exp $
 */
public class MouseLook implements MouseInputAction {
    //mouse that detects relative movements.
    private RelativeMouse mouse;
    //actions to handle looking up down left and right.
    private KeyLookDownAction lookDown;

    private KeyLookUpAction lookUp;

    private KeyRotateLeftAction rotateLeft;

    private KeyRotateRightAction rotateRight;
    //the axis to lock.
    private Vector3f lockAxis;
    //the event to distribute to the looking actions.
    private InputActionEvent event;
    //the speed to look
    private float speed;
    private String key;

    /**
     * Constructor creates a new <code>MouseLook</code> object. It takes the
     * mouse, camera and speed of the looking.
     * 
     * @param mouse
     *            the mouse to calculate view changes.
     * @param camera
     *            the camera to move.
     * @param speed
     *            the speed at which to alter the camera.
     */
    public MouseLook(Mouse mouse, Camera camera, float speed) {
        this.mouse = (RelativeMouse) mouse;
        this.speed = speed;

        lookDown = new KeyLookDownAction(camera, speed);
        lookUp = new KeyLookUpAction(camera, speed);
        rotateLeft = new KeyRotateLeftAction(camera, speed);
        rotateRight = new KeyRotateRightAction(camera, speed);

        event = new InputActionEvent();
    }

    /**
     * 
     * <code>setLockAxis</code> sets the axis that should be locked down. This
     * prevents "rolling" about a particular axis. Typically, this is set to the
     * mouse's up vector. Note this is only a shallow copy.
     * 
     * @param lockAxis
     *            the axis that should be locked down to prevent rolling.
     */
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
        rotateLeft.setLockAxis(lockAxis);
        rotateRight.setLockAxis(lockAxis);
    }

    /**
     * Returns the axis that is currently locked.
     * 
     * @return The currently locked axis
     * @see #setLockAxis(com.jme.math.Vector3f)
     */
    public Vector3f getLockAxis() {
        return lockAxis;
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * 
     * @param speed
     *            the speed of the mouse look.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        lookDown.setSpeed(speed);
        lookUp.setSpeed(speed);
        rotateRight.setSpeed(speed);
        rotateLeft.setSpeed(speed);

    }

    /**
     * 
     * <code>getSpeed</code> retrieves the speed of the mouse look.
     * 
     * @return the speed of the mouse look.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * 
     * @see com.jme.input.action.MouseInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
        float time = evt.getTime() * speed;

        if (mouse.getLocalTranslation().x > 0) {
            event.setTime(time * mouse.getLocalTranslation().x);
            rotateRight.performAction(event);
        } else if (mouse.getLocalTranslation().x < 0) {
            event.setTime(time * mouse.getLocalTranslation().x * -1);
            rotateLeft.performAction(event);
        }
        if (mouse.getLocalTranslation().y > 0) {
            event.setTime(time * mouse.getLocalTranslation().y);
            lookUp.performAction(event);
        } else if (mouse.getLocalTranslation().y < 0) {
            event.setTime(time * mouse.getLocalTranslation().y * -1);
            lookDown.performAction(event);
        }

    }

    /**
     * <code>setMouse</code> sets the mouse used to check for movement.
     * 
     * @see com.jme.input.action.MouseInputAction#setMouse(com.jme.input.Mouse)
     */
    public void setMouse(Mouse mouse) {
        this.mouse = (RelativeMouse) mouse;
    }

    /* (non-Javadoc)
     * @see com.jme.input.action.InputAction#setKey(java.lang.String)
     */
    public void setKey(String key) {
       this.key = key;
    }

    /* (non-Javadoc)
     * @see com.jme.input.action.InputAction#getKey()
     */
    public String getKey() {
        return key;
    }

}