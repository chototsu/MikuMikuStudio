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

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyRotateLeftAction</code>
 * @author Mark Powell
 * @version 
 */
public class KeyRotateLeftAction implements InputAction {
    private Matrix3f incr;
    private Camera camera;
    private float speed;
    private String key;
    private Vector3f lockAxis;
    
    public KeyRotateLeftAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
        incr = new Matrix3f();
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public void setLockAxis(Vector3f lockAxis) {
        this.lockAxis = lockAxis;
    }

    /* (non-Javadoc)
     * @see com.jme.input.action.InputAction#performAction(float)
     */
    public void performAction(float time) {
        incr.loadIdentity();
        if(lockAxis == null) {
            incr.fromAxisAngle(camera.getUp(), speed * time);
        } else {
            incr.fromAxisAngle(lockAxis, speed * time);
        }
        
        camera.setUp(incr.mult(camera.getUp()));
        
        camera.setLeft(incr.mult(camera.getLeft()));
        camera.setDirection(incr.mult(camera.getDirection()));
        camera.update();

    }

    /* (non-Javadoc)
     * @see com.jme.input.action.InputAction#getKey()
     */
    public String getKey() {
        return key;
    }

    /* (non-Javadoc)
     * @see com.jme.input.action.InputAction#setKey(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;
    }

}
