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
package com.jme.input;

import com.jme.app.AbstractGame;
import com.jme.input.action.*;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;

/**
 * <code>NodeHandler</code> defines an InputHandler that sets
 * a node that can be controlled via keyboard and mouse inputs. By default the
 * commands are, WSAD moves the node forward, backward and strafes. The
 * arrow keys rotate and tilt the node and the mouse also rotates and tilts
 * the node.
 * @author Mark Powell
 * @version $Id: NodeHandler.java,v 1.4 2004-10-14 01:23:07 mojomonkey Exp $
 */
public class NodeHandler extends InputHandler {

    /**
     * Constructor instantiates a new <code>NodeHandler</code> object. The
     * application is set for the use of the exit action. The node is set to
     * control, while the api defines which input api is to be used.
     * @param app the app using the controller, for the exit action.
     * @param node the node to control.
     * @param api the api to use for input.
     */
    public NodeHandler(AbstractGame app, Spatial node, String api) {

        setKeyBindings(api);
        setUpMouse(node);
        setActions(node, app);

    }

    /**
     *
     * <code>setKeyBindings</code> binds the keys to use for the actions.
     * @param api the api to use for the input.
     */
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
        keyboard.set("exit", KeyInput.KEY_ESCAPE);
        keyboard.set("screenshot", KeyInput.KEY_F12);

        setKeyBindingManager(keyboard);

    }

    /**
     *
     * <code>setUpMouse</code> sets the mouse look object.
     * @param node the node to use for rotations.
     */
    private void setUpMouse(Spatial node) {
        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.setMouseInput(InputSystem.getMouseInput());
        setMouse(mouse);

        NodeMouseLook mouseLook = new NodeMouseLook(mouse, node, 0.1f);
        mouseLook.setKey("mouselook");
        mouseLook.setLockAxis(new Vector3f(node.getLocalRotation().getRotationColumn(1).x,
                node.getLocalRotation().getRotationColumn(1).y,
                node.getLocalRotation().getRotationColumn(1).z));
        addAction(mouseLook);
    }

    /**
     *
     * <code>setActions</code> sets the keyboard actions with the corresponding
     * key command.
     * @param node the node to control.
     * @param app the app to use exit with.
     */
    private void setActions(Spatial node, AbstractGame app) {
        KeyExitAction exit = new KeyExitAction(app);
        exit.setKey("exit");
        addAction(exit);
        KeyNodeForwardAction forward = new KeyNodeForwardAction(node, 0.5f);
        forward.setKey("forward");
        addAction(forward);
        KeyNodeBackwardAction backward = new KeyNodeBackwardAction(node, 0.5f);
        backward.setKey("backward");
        addAction(backward);
        KeyNodeStrafeLeftAction strafeLeft = new KeyNodeStrafeLeftAction(node, 0.5f);
        strafeLeft.setKey("strafeLeft");
        addAction(strafeLeft);
        KeyNodeStrafeRightAction strafeRight = new KeyNodeStrafeRightAction(node, 0.5f);
        strafeRight.setKey("strafeRight");
        addAction(strafeRight);
        KeyNodeLookUpAction lookUp = new KeyNodeLookUpAction(node, 0.01f);
        lookUp.setKey("lookUp");
        addAction(lookUp);
        KeyNodeLookDownAction lookDown = new KeyNodeLookDownAction(node, 0.01f);
        lookDown.setKey("lookDown");
        addAction(lookDown);
        KeyNodeRotateRightAction rotateRight = new KeyNodeRotateRightAction(node, 0.01f);
        rotateRight.setKey("turnRight");
        rotateRight.setLockAxis(node.getLocalRotation().getRotationColumn(1));
        addAction(rotateRight);
        KeyNodeRotateLeftAction rotateLeft = new KeyNodeRotateLeftAction(node, 0.01f);
        rotateLeft.setKey("turnLeft");
        rotateLeft.setLockAxis(node.getLocalRotation().getRotationColumn(1));
        addAction(rotateLeft);
        KeyScreenShotAction screenshot = new KeyScreenShotAction();
        screenshot.setKey("screenshot");
        addAction(screenshot);
    }
}
