/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
package jmetest.game.state;

import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.*;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

/**
 * @author Per Thulin
 *
 * This is basically the FirstPersonHandler but without the capability to 
 * exit the application.
 */
public class IngameHandler extends InputHandler {

    /**
     * Creates a first person handler.  The camera
     * is used for mouse look, and the api string is used to find the correct keyboard input.
     * @param cam The camera to move by this handler.
     * @param api The API to create a KeyBindingManager from.
     */
    public IngameHandler(Camera cam, String api) {
        setKeyBindings(api);
        setMouse(cam);
        setActions(cam);
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

    private void setActions(Camera cam) {
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

