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
package com.jme.input;

import java.util.logging.Level;

import org.lwjgl.input.Mouse;

import com.jme.util.LoggingSystem;

/**
 * <code>LWJGLMouseInput</code> handles mouse input via the LWJGL Input API. 
 * 
 * @author Mark Powell
 * @version $Id: LWJGLMouseInput.java,v 1.2 2003-10-26 17:56:36 mojomonkey Exp $
 */
public class LWJGLMouseInput implements MouseInput {
    
    /**
     * Constructor creates a new <code>LWJGLMouseInput</code> object. A call
     * to the LWJGL creation method is made, if any problems occur during
     * this creation, it is logged.
     *
     */
    public LWJGLMouseInput() {
        try {
            Mouse.create();
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Problem during " +                "creation of Mouse.");
        }
    }
    /**
     * <code>destroy</code> cleans up the native mouse reference.
     * @see com.jme.input.MouseInput#destroy()
     */
    public void destroy() {
        Mouse.destroy();
        
    }

    /**
     * <code>getButtonIndex</code> returns the index of a given button name.
     * @see com.jme.input.MouseInput#getButtonIndex(java.lang.String)
     */
    public int getButtonIndex(String buttonName) {
        return Mouse.getButtonIndex(buttonName);
    }

    /**
     * <code>getButtonName</code> returns the name of a given button index.
     * @see com.jme.input.MouseInput#getButtonName(int)
     */
    public String getButtonName(int buttonIndex) {
        return Mouse.getButtonName(buttonIndex);
    }
    
    /**
     * <code>isButtonDown</code> tests if a given button is pressed or not.
     * @see com.jme.input.MouseInput#isButtonDown(int)
     */
    public boolean isButtonDown(int buttonCode) {
        return Mouse.isButtonDown(buttonCode);
    }

    /**
     * <code>isCreated</code> returns false if the mouse is created, false
     * otherwise.
     * @see com.jme.input.MouseInput#isCreated()
     */
    public boolean isCreated() {
        return Mouse.isCreated();
    }

    /**
     * <code>poll</code> gets the current state of the mouse.
     * @see com.jme.input.MouseInput#poll()
     */
    public void poll() {
        Mouse.poll();
    }
    
    /**
     * <code>getWheelDelta</code> retrieves the change of the mouse wheel,
     * if any.
     * @see com.jme.input.MouseInput#getWheelDelta()
     */
    public int getWheelDelta() {
        return Mouse.dwheel;
    }
    /**
     * <code>getXDelta</code> retrieves the change of the x position, if any.
     * @see com.jme.input.MouseInput#getXDelta()
     */
    public int getXDelta() {
        return Mouse.dx;
    }
    /**
     * <code>getYDelta</code> retrieves the change of the y position, if any.
     * @see com.jme.input.MouseInput#getYDelta()
     */
    public int getYDelta() {
        return Mouse.dy;
    }
    
    

}
