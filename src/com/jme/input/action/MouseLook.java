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

import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>MouseLook</code>
 * @author Mark Powell
 * @version 
 */
public class MouseLook implements MouseInputAction{
    public static final int MOUSE_BUFFER = 1;
    private MouseInput mouse;
    private KeyLookDownAction lookDown;
    private KeyLookUpAction lookUp;
    private KeyRotateLeftAction rotateLeft;
    private KeyRotateRightAction rotateRight;
    
    private Vector3f lockAxis;
    
    private float speed;
    private Camera camera;
    
    public MouseLook(MouseInput mouse, Camera camera, float speed) {
        this.mouse = mouse;
        this.speed = speed;
        this.camera = camera;
        
        lookDown = new KeyLookDownAction(camera, speed);
        lookUp = new KeyLookUpAction(camera, speed);
        rotateLeft = new KeyRotateLeftAction(camera, speed);
        rotateRight = new KeyRotateRightAction(camera, speed);
    }
    
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
        rotateLeft.setLockAxis(lockAxis);
        rotateRight.setLockAxis(lockAxis);
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
        lookDown.setSpeed(speed);
        lookUp.setSpeed(speed);
        rotateRight.setSpeed(speed);
        rotateLeft.setSpeed(speed);
        
    }
    
    public float getSpeed() {
        return speed;
    }
    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#performAction(float)
     */
    public void performAction(float time) {
        if(mouse.getXDelta() > 0) {
            rotateRight.performAction(time * ((float)mouse.getXDelta()/MOUSE_BUFFER));
        } else if(mouse.getXDelta() < 0) {
            rotateLeft.performAction(time * ((float)mouse.getXDelta()/MOUSE_BUFFER));
        }
        if(mouse.getYDelta() > 0) {
            lookUp.performAction(time * ((float)mouse.getYDelta()/MOUSE_BUFFER));
        } else if(mouse.getYDelta() < 0) {
            lookDown.performAction(time * ((float)mouse.getYDelta()/MOUSE_BUFFER));
        }
        
    }
    /* (non-Javadoc)
     * @see com.jme.input.action.MouseInputAction#setMouse(com.jme.input.Mouse)
     */
    public void setMouse(MouseInput mouse) {
        this.mouse = mouse;
    }
    
    
}
