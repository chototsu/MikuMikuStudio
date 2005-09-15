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

import com.jme.app.AbstractGame;
import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyExitAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyScreenShotAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * <code>FirsPersonController</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera and the mouse also rotates and tilts
 * the camera.
 * @author Mark Powell
 * @version $Id: FirstPersonHandler.java,v 1.6 2005-09-15 17:13:06 renanse Exp $
 */
public class FirstPersonHandler extends InputHandler {

    /**
     * Creates a first person handler.  The app is used for the exit action, camera
     * is used for mouse look, and the api string is used to find the correct keyboard input.
     * @param app The application that is exited on an "exit" action.
     * @param cam The camera to move by this handler.
     * @param api The API to create a KeyBindingManager from.
     */
    public FirstPersonHandler(AbstractGame app, Camera cam, String api) {

        setKeyBindings(api);
        setMouse(cam);
        setActions(cam, app);

    }

    private void setKeyBindings(String api) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        InputSystem.createInputSystem(api);

        keyboard.setKeyInput(InputSystem.getKeyInput());
        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("strafeLeft", KeyInput.KEY_A);
        keyboard.set("strafeRight", KeyInput.KEY_D);
        keyboard.set("lookUp", KeyInput.KEY_UP);
        keyboard.set("lookDown", KeyInput.KEY_DOWN);
        keyboard.set("turnRight", KeyInput.KEY_RIGHT);
        keyboard.set("turnLeft", KeyInput.KEY_LEFT);
        keyboard.set("screenshot", KeyInput.KEY_F12);
        keyboard.set("exit", KeyInput.KEY_ESCAPE);

        setKeyBindingManager(keyboard);
    }

    private void setMouse(Camera cam) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());
        setMouse(mouse);

        MouseLook mouseLook = new MouseLook(mouse, cam, 1.0f);
        mouseLook.setKey("mouselook");
        mouseLook.setLockAxis(new Vector3f(cam.getUp().x, cam.getUp().y, 
                	cam.getUp().z));
        addAction(mouseLook);
    }

    private void setActions(Camera cam, AbstractGame app) {
        KeyExitAction exit = new KeyExitAction(app);
        exit.setKey("exit");
        addAction(exit);
        KeyScreenShotAction screen = new KeyScreenShotAction();
        screen.setKey("screenshot");
        addAction(screen);
        KeyForwardAction forward = new KeyForwardAction(cam, 0.5f);
        forward.setKey("forward");
        addAction(forward);
        KeyBackwardAction backward = new KeyBackwardAction(cam, 0.5f);
        backward.setKey("backward");
        addAction(backward);
        KeyStrafeLeftAction strafeLeft = new KeyStrafeLeftAction(cam, 0.5f);
        strafeLeft.setKey("strafeLeft");
        addAction(strafeLeft);
        KeyStrafeRightAction strafeRight = new KeyStrafeRightAction(cam, 0.5f);
        strafeRight.setKey("strafeRight");
        addAction(strafeRight);
        KeyLookUpAction lookUp = new KeyLookUpAction(cam, 0.01f);
        lookUp.setKey("lookUp");
        addAction(lookUp);
        KeyLookDownAction lookDown = new KeyLookDownAction(cam, 0.01f);
        lookDown.setKey("lookDown");
        addAction(lookDown);
        KeyRotateRightAction rotateRight = new KeyRotateRightAction(cam, 0.01f);
        rotateRight.setKey("turnRight");
        addAction(rotateRight);
        KeyRotateLeftAction rotateLeft = new KeyRotateLeftAction(cam, 0.01f);
        rotateLeft.setKey("turnLeft");
        addAction(rotateLeft);
    }
}
