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
package com.jme.input.action;

import com.jme.input.Mouse;
import com.jme.input.RelativeMouse;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * <code>MouseLook</code> defines a mouse action that detects mouse movement
 * and converts it into camera rotations and camera tilts. 
 * @author Mark Powell
 * @version $Id: NodeMouseLook.java,v 1.1 2003-12-11 23:21:07 mojomonkey Exp $
 */
public class NodeMouseLook implements MouseInputAction {
    private RelativeMouse mouse;
    private KeyNodeLookDownAction lookDown;
    private KeyNodeLookUpAction lookUp;
    private KeyNodeRotateLeftAction rotateLeft;
    private KeyNodeRotateRightAction rotateRight;

    private Vector3f lockAxis;

    private float speed;
    private Spatial node;

    /**
     * Constructor creates a new <code>MouseLook</code> object. It takes the
     * mouse, camera and speed of the looking.
     * @param mouse the mouse to calculate view changes.
     * @param camera the camera to move.
     * @param speed the speed at which to alter the camera.
     */
    public NodeMouseLook(Mouse mouse, Spatial node, float speed) {
        this.mouse = (RelativeMouse)mouse;
        this.speed = speed;
        this.node = node;

        lookDown = new KeyNodeLookDownAction(this.node, speed);
        lookUp = new KeyNodeLookUpAction(this.node, speed);
        rotateLeft = new KeyNodeRotateLeftAction(this.node, speed);
        rotateRight = new KeyNodeRotateRightAction(this.node, speed);
    }

    /**
     * 
     * <code>setLockAxis</code> sets the axis that should be locked down. This
     * prevents "rolling" about a particular axis. Typically, this is set to 
     * the mouse's up vector.
     * @param lockAxis the axis that should be locked down to prevent rolling.
     */
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
        rotateLeft.setLockAxis(lockAxis);
        rotateRight.setLockAxis(lockAxis);
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed of the mouse look.
     * @param speed the speed of the mouse look.
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
     * @return the speed of the mouse look.
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * <code>performAction</code> checks for any movement of the mouse, and
     * calls the appropriate method to alter the camera's orientation when
     * applicable.
     * @see com.jme.input.action.MouseInputAction#performAction(float)
     */
    public void performAction(float time) {
        time *= speed;
        if (mouse.getLocalTranslation().x > 0) {
            rotateRight.performAction(
                time * mouse.getLocalTranslation().x);
        } else if (mouse.getLocalTranslation().x < 0) {
            rotateLeft.performAction(
                time * mouse.getLocalTranslation().x * -1);
        }
        if (mouse.getLocalTranslation().y > 0) {
            lookUp.performAction(
                time * mouse.getLocalTranslation().y);
        } else if (mouse.getLocalTranslation().y < 0) {
            lookDown.performAction(
                time * mouse.getLocalTranslation().y * -1);
        }

    }
    /**
     * <code>setMouse</code> sets the mouse used to check for movement.
     * @see com.jme.input.action.MouseInputAction#setMouse(com.jme.input.Mouse)
     */
    public void setMouse(Mouse mouse) {
        this.mouse = (RelativeMouse)mouse;
    }

}
