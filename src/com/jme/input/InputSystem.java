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

import java.util.logging.Level;

import com.jme.input.lwjgl.LWJGLKeyInput;
import com.jme.input.lwjgl.LWJGLMouseInput;
import com.jme.util.LoggingSystem;

/**
 * <code>InputSystem</code> creates the required input objects (mouse and
 * keyboard) depending on the API desired for the handling of the input. This
 * will allow the client application to only deal with <code>KeyInput</code>
 * and <code>MouseInput</code> not having to worry about the API specifics.
 * 
 * @see com.jme.input.KeyInput
 * @see com.jme.input.MouseInput
 * @author Mark Powell
 * @version $Id: InputSystem.java,v 1.8 2005-10-03 20:25:11 renanse Exp $
 */
public class InputSystem {
    // the input devices.
    private static KeyInput keyInput;

    private static MouseInput mouseInput;

    private static boolean inited = false;
    
    public static final String INPUT_SYSTEM_LWJGL = "LWJGL";

    /**
     * 
     * <code>createInputSystem</code> initializes the input devices using the
     * provided API string.
     * 
     * @param system
     *            the input API to use, e.g. "LWJGL" or "JInput".
     */
    public static void createInputSystem(String system) {
        createInputSystem(system, false);
    }

    /**
     * 
     * <code>createInputSystem</code> initializes the input devices using the
     * provided API string.
     * 
     * @param system
     *            the input API to use, e.g. "LWJGL" or "JInput".
     * @param forceNew
     *            true if we should force creation of the inputSystem regardless
     *            of whether it was previously inited.
     */
    public static void createInputSystem(String system, boolean forceNew) {
        if (inited && !forceNew) {
            LoggingSystem
            .getLogger()
            .log(Level.INFO,
                    "InputSystem is already created.");
            return;
        }
            
        if (INPUT_SYSTEM_LWJGL.equalsIgnoreCase(system)) {
            keyInput = new LWJGLKeyInput();
            mouseInput = new LWJGLMouseInput();
        }
        inited = true;
    }

    /**
     * <code>isInited</code> returns true if createInputSystem was previously called.
     * 
     * @return inited
     */
    public static boolean isInited() {
        return inited;
    }
    
    /**
     * 
     * <code>getKeyInput</code> retrieves the key input device.
     * 
     * @return the key input device.
     */
    public static KeyInput getKeyInput() {
        if (keyInput == null) {
            LoggingSystem
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "KeyInput is null,"
                                    + " insure that a call to createInputSystem was made before"
                                    + " getting the devices.");
        }
        return keyInput;
    }

    /**
     * 
     * <code>getMouseInput</code> retrieves the mouse input device.
     * 
     * @return the mouse input device.
     */
    public static MouseInput getMouseInput() {
        if (mouseInput == null) {
            LoggingSystem
                    .getLogger()
                    .log(
                            Level.WARNING,
                            "MouseInput is null,"
                                    + " insure that a call to createInputSystem was made before"
                                    + " getting the devices.");
        }
        return mouseInput;

    }
}
