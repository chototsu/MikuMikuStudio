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
import com.jme.renderer.Camera;

/**
 * <code>KeyLookDownAction</code> tilts a camera down a given angle. This angle
 * should be represented as a radian.
 * @author Mark Powell
 * @version $Id: KeyLookDownAction.java,v 1.4 2004-02-06 03:57:14 mojomonkey Exp $
 */
public class KeyLookDownAction implements InputAction {

    private Matrix3f incr;
    private Camera camera;
    private float speed;
    private String key;
    
    /**
     * Constructor instantiates a new <code>KeyLookDownAction</code> object.
     * @param camera the camera to tilt.
     * @param speed the speed at which the camera tilts.
     */
    public KeyLookDownAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
        incr = new Matrix3f();
    }
    
    /**
     * 
     * <code>setSpeed</code> sets the speed at which the camera is allowed to
     * tilt.
     * @param speed the speed the camera is allowed to tilt.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }


    /**
     * <code>performAction</code> adjusts the view of the camera to tilt down
     * a given angle. This angle is determined by the camera's speed and the
     * time which has passed.
     * @see com.jme.input.action.InputAction#performAction(float)
     */
    public void performAction(float time) {
        incr.loadIdentity();
        incr.fromAxisAngle(camera.getLeft(), speed * time);
        incr.mult(camera.getLeft(), camera.getLeft());
        incr.mult(camera.getDirection(), camera.getDirection());
        incr.mult(camera.getUp(), camera.getUp());
        camera.update();

    }

    /**
     * <code>getKey</code> retrieves the key associated with this action.
     * @see com.jme.input.action.InputAction#getKey()
     */
    public String getKey() {
        return key;
    }

    /**
     * <code>setKey</code> sets the key associated with this action.
     * @see com.jme.input.action.InputAction#setKey(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;

    }
}
