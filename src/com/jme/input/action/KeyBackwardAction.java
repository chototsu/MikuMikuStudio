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

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>KeyBackwardAction</code> defines an action for moving a camera along 
 * it's negative direction. How fast the camera moves in a single frame is
 * defined by the speed of the camera times the time between frames. The speed
 * of the camera can be thought of as how many units per second the camera
 * can travel.
 * @author Mark Powell
 * @version $Id: KeyBackwardAction.java,v 1.2 2003-10-30 20:41:24 mojomonkey Exp $
 */
public class KeyBackwardAction implements InputAction {
    private Camera camera;
    private float speed;
    private String key;
    
    /**
     * Constructor instantiates a new <code>KeyBackwardAction</code> object.
     * @param camera the camera that will be affected by this action.
     * @param speed the speed at which the camera can move.
     */
    public KeyBackwardAction(Camera camera, float speed) {
        this.camera = camera;
        this.speed = speed;
    }

    /**
     * 
     * <code>setSpeed</code> sets the speed in units/second that the 
     * camera can move.
     * @param movementSpeed the units/second of the camera.
     */
    public void setSpeed(float movementSpeed) {
        this.speed = movementSpeed;
    }
    
    /**
     * <code>performAction</code> moves the camera along it's negative
     * direction vector at a speed of movement speed * time. Where time is
     * the time between frames and 1 corresponds to 1 second.
     * @see com.jme.input.action.InputAction#performAction(float)
     */
    public void performAction(float time) {
        Vector3f loc = camera.getLocation();
        loc = loc.subtract(camera.getDirection().mult((speed*time)));
        camera.setLocation(loc);
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
