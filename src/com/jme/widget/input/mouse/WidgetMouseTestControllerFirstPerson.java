/*
 * Copyright (c) 2003-2004, Gregg Patton
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.widget.input.mouse;

import com.jme.app.AbstractGame;
import com.jme.input.AbstractInputHandler;
import com.jme.input.InputSystem;
import com.jme.input.KeyInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyExitAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.input.action.MouseLook;
import com.jme.renderer.Camera;
import com.jme.renderer.RendererType;

/**
 * <code>WidgetMouseTestControllerFirstPerson</code>
 * @author Gregg Patton
 * @version $Id: WidgetMouseTestControllerFirstPerson.java,v 1.6 2004-04-22 22:27:20 renanse Exp $
 */
public class WidgetMouseTestControllerFirstPerson extends AbstractInputHandler {

    /**
     * @param camera
     */
    public WidgetMouseTestControllerFirstPerson(Camera camera) {
        super(camera);
    }

    /**
     * @param app
     * @param camera
     */
    public WidgetMouseTestControllerFirstPerson(AbstractGame app, Camera camera) {
        super(app, camera);
    }

    /* (non-Javadoc)
     * @see com.jme.input.AbstractInputHandler#setKeyBindings(java.lang.String)
     */
    protected void setKeyBindings(RendererType rendererType) {
        super.setKeyBindings(rendererType);

        keyboard.set("forward", KeyInput.KEY_W);
        keyboard.set("backward", KeyInput.KEY_S);
        keyboard.set("strafeLeft", KeyInput.KEY_A);
        keyboard.set("strafeRight", KeyInput.KEY_D);
        keyboard.set("lookUp", KeyInput.KEY_UP);
        keyboard.set("lookDown", KeyInput.KEY_DOWN);
        keyboard.set("turnRight", KeyInput.KEY_RIGHT);
        keyboard.set("turnLeft", KeyInput.KEY_LEFT);
        keyboard.set("exit", KeyInput.KEY_ESCAPE);

    }

    /* (non-Javadoc)
     * @see com.jme.input.AbstractInputHandler#setMouse(com.jme.renderer.Camera)
     */
    protected void setMouse() {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());
        setMouse(mouse);

        MouseLook mouseLook = new MouseLook(mouse, camera, 0.1f);
        mouseLook.setLockAxis(camera.getUp());
        addAction(mouseLook);

    }

    /* (non-Javadoc)
     * @see com.jme.input.AbstractInputHandler#setActions(com.jme.renderer.Camera, com.jme.app.AbstractGame)
     */
    protected void setActions() {

        if (app != null) {
        KeyExitAction exit = new KeyExitAction(app);
        exit.setKey("exit");
        addAction(exit);
        }

        KeyForwardAction forward = new KeyForwardAction(camera, 0.5f);
        forward.setKey("forward");
        addAction(forward);
        KeyBackwardAction backward = new KeyBackwardAction(camera, 0.5f);
        backward.setKey("backward");
        addAction(backward);
        KeyStrafeLeftAction strafeLeft = new KeyStrafeLeftAction(camera, 0.5f);
        strafeLeft.setKey("strafeLeft");
        addAction(strafeLeft);
        KeyStrafeRightAction strafeRight = new KeyStrafeRightAction(camera, 0.5f);
        strafeRight.setKey("strafeRight");
        addAction(strafeRight);
        KeyLookUpAction lookUp = new KeyLookUpAction(camera, 0.01f);
        lookUp.setKey("lookUp");
        addAction(lookUp);
        KeyLookDownAction lookDown = new KeyLookDownAction(camera, 0.01f);
        lookDown.setKey("lookDown");
        addAction(lookDown);
        KeyRotateRightAction rotateRight = new KeyRotateRightAction(camera, 0.01f);
        rotateRight.setKey("turnRight");
        addAction(rotateRight);
        KeyRotateLeftAction rotateLeft = new KeyRotateLeftAction(camera, 0.01f);
        rotateLeft.setKey("turnLeft");
        addAction(rotateLeft);
    }

}
