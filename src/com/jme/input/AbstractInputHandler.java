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

/*
 * EDIT:  04/05/2004 - Added check to see if we already have an input system initialized. GOP
 */

package com.jme.input;

import java.util.ArrayList;

import com.jme.app.AbstractGame;
import com.jme.input.action.InputAction;
import com.jme.input.action.MouseInputAction;
import com.jme.renderer.Camera;
import com.jme.renderer.RendererType;
import com.jme.system.DisplaySystem;

/**
 * <code>AbstractInputHandler</code> defines a super abstract class for
 * input controlling. It maintains a list of actions and mouse actions. These
 * actions are then processed during every update cycle. Subclasses are required
 * to defined to setMouse and setActions methods for custom InputControllers.
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: AbstractInputHandler.java,v 1.3 2004-04-22 22:26:29 renanse Exp $
 */
public abstract class AbstractInputHandler {

    protected AbstractGame app;
    protected Camera camera;
    protected ArrayList actions;
    protected ArrayList mouseActions;
    protected KeyBindingManager keyboard;
    protected Mouse mouse;

    protected boolean updateKeyboardActionsEnabled = true;
    protected boolean updateMouseActionsEnabled = true;

    /**
     * Constructor creates a default <code>AbstractInputHandler</code>.
     *
     */
    public AbstractInputHandler() {
        init(null, null);
    }

    /**
     * Constructor instantiates a new <code>AbstractInputHandler</code>
     * defining the camera that defines the viewing.
     * @param camera the camera that defines the viewport frame.
     */
    public AbstractInputHandler(Camera camera) {
        init(null, camera);
    }

    /**
     *
     * @param app
     */
    public AbstractInputHandler(AbstractGame app) {
        init(app, null);
    }

    public AbstractInputHandler(AbstractGame app, Camera camera) {

        init(app, camera);
    }

    private void init(AbstractGame app, Camera camera) {
        this.app = app;

        this.camera = camera;

        actions = new ArrayList();
        mouseActions = new ArrayList();

        setKeyBindings(DisplaySystem.getDisplaySystem().getRendererType());
        setMouse();
        setActions();
    }

    /**
     * <code>getApp</code>
     * @return
     */
    public AbstractGame getApp() {
        return app;
    }

    /**
     * <code>setApp</code>
     * @param game
     */
    public void setApp(AbstractGame game) {
        app = game;
    }

    /**
     * <code>getCamera</code>
     * @return
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setCamera</code>
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setKeyBindingManager(KeyBindingManager keyboard) {
        this.keyboard = keyboard;
    }

    public KeyBindingManager getKeyBindingManager() {
        return keyboard;
    }

    public void setMouse(Mouse mouse) {
        this.mouse = mouse;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public void setKeySpeed(float speed) {
        for(int i = 0; i < actions.size(); i++) {
            ((InputAction)actions.get(i)).setSpeed(speed);
        }
    }

    public void setMouseSpeed(float speed) {
        for(int i = 0; i < mouseActions.size(); i++) {
            ((MouseInputAction)mouseActions.get(i)).setSpeed(speed);
        }
    }

    public void addAction(InputAction inputAction) {
        actions.add(inputAction);
    }

    public void addAction(MouseInputAction mouseAction) {
        mouseActions.add(mouseAction);
    }

    public void removeAction(InputAction inputAction) {
        actions.remove(inputAction);
    }

    public void removeAction(MouseInputAction mouseAction) {
        mouseActions.remove(mouseAction);
    }

    public void update(float time) {
        update(true, true, time);
    }

    public void update(boolean updateMouseState, boolean updateKeyboard, float time) {
        if (keyboard != null) {

            if (updateKeyboard)
                keyboard.update();

            if (updateKeyboardActionsEnabled) {
                for (int i = 0; i < actions.size(); i++) {
                    if (keyboard
                        .isValidCommand(((InputAction) actions.get(i)).getKey())) {
                        ((InputAction) actions.get(i)).performAction(time);
                    }
                }
            }
        }

        if(mouse != null) {

            mouse.update(updateMouseState);

            if (updateMouseActionsEnabled) {
                for(int i = 0; i < mouseActions.size(); i++) {
                    ((MouseInputAction)mouseActions.get(i)).performAction(time);
                }
            }
        }
    }

    protected void setKeyBindings(RendererType rendererType) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        //Check to see if we already have an input system, we only need one.
        if (InputSystem.getKeyInput() == null)
            InputSystem.createInputSystem(rendererType.getName());

        keyboard.setKeyInput(InputSystem.getKeyInput());

        setKeyBindingManager(keyboard);
    }

    public boolean isUpdateKeyboardActionsEnabled() {
        return updateKeyboardActionsEnabled;
    }

    public void setUpdateKeyboardActionsEnabled(boolean b) {
        updateKeyboardActionsEnabled = b;
    }

    public boolean isUpdateMouseActionsEnabled() {
        return updateMouseActionsEnabled;
    }

    public void setUpdateMouseActionsEnabled(boolean b) {
        updateMouseActionsEnabled = b;
    }

    protected abstract void setMouse();

    protected abstract void setActions();

}
