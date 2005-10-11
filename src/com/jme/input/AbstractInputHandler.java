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

/*
 * EDIT:  04/05/2004 - Added check to see if we already have an input system initialized. GOP
 */

package com.jme.input;

import java.util.ArrayList;

import com.jme.app.AbstractGame;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.MouseInputAction;
import com.jme.renderer.Camera;
import com.jme.renderer.RendererType;
import com.jme.system.DisplaySystem;

/**
 * <code>AbstractInputHandler</code> defines a super abstract class for input
 * controlling. It maintains a list of actions and mouse actions. These actions
 * are then processed during every update cycle. Subclasses are required to
 * defined to setMouse and setActions methods for custom InputControllers.
 *
 * @author Mark Powell
 * @author Gregg Patton
 * @author Jack Lindamood - (Javadoc only)
 * @version $Id: AbstractInputHandler.java,v 1.10 2005-10-11 10:41:45 irrisor Exp $
 */
public abstract class AbstractInputHandler {

    /** Optional. The game controlling this InputHandler. */
    protected AbstractGame app;

    /** Optional. The camera controlled by this InputHandler. */
    protected Camera camera;

    /** List of keyboard actions. They are performed in update if valid. */
    protected ArrayList keyActions;

    /** List of mouse actions. They are performed in update. */
    protected ArrayList mouseActions;

    /** The keyboard where valid key actions are taken from in update. */
    protected KeyBindingManager keyboard;

    /** The mouse where valid mouse actions are taken from in update. */
    protected Mouse mouse;

    /** If false, keyboard actions are not done. */
    protected boolean updateKeyboardActionsEnabled = true;

    /** If false, mouse actions are not done. */
    protected boolean updateMouseActionsEnabled = true;

    /** the event to send to all actions in a single frame */
    private InputActionEvent event;

    /** list that holds the actions that will be executed this frame */
    private ArrayList actionList;

    /** list of the names of all actions that will be executed this frame */
    private ArrayList eventList;

    /**
     * Constructor creates a default <code>AbstractInputHandler</code>. It
     * has no set AbstractGame or Camera.
     *
     */
    public AbstractInputHandler() {
        init(null, null);
    }

    /**
     * Constructor instantiates a new <code>AbstractInputHandler</code>
     * defining the camera that defines the viewing. The AbstractGame app is by
     * default null.
     *
     * @param camera
     *            the camera that defines the viewport frame.
     */
    public AbstractInputHandler(Camera camera) {
        init(null, camera);
    }

    /**
     * Constructor instantiates a new <code>AbstractInputHandler</code>
     * defining the app that defines the application actions. The Camera cam is
     * by default null.
     *
     * @param app
     *            The AbstractGame that will take application actions.
     */
    public AbstractInputHandler(AbstractGame app) {
        init(app, null);
    }

    /**
     * Constructor instantiates a new <code>AbstractInputHandler</code>
     * defining the app that defines the application actions and the camera that
     * will define viewing.
     *
     * @param app
     *            The AbstractGame that will take application actions.
     * @param camera
     *            The camera that defines the viewport frame.
     */
    public AbstractInputHandler(AbstractGame app, Camera camera) {

        init(app, camera);
    }

    /**
     * Called internally after the constructor. Sets up mouse and key inputs to
     * be received by update.
     *
     * @param app
     *            The game controlling this handler.
     * @param camera
     *            The camera controlled by this handler.
     */
    private void init(AbstractGame app, Camera camera) {
        this.app = app;
        keyActions = new ArrayList();
        mouseActions = new ArrayList();
        event = new InputActionEvent();
        actionList = new ArrayList();
        eventList = new ArrayList();
        this.camera = camera;

        setKeyBindings(DisplaySystem.getDisplaySystem().getRendererType());
        setMouse();
        setActions();
    }

    /**
     * <code>getApp</code> returns the AbstractGame controlling this handler.
     *
     * @return This handler's AbstractGame.
     */
    public AbstractGame getApp() {
        return app;
    }

    /**
     * <code>setApp</code> sets the AbstractGame controlling this handler.
     *
     * @param game
     *            The game to set.
     */
    public void setApp(AbstractGame game) {
        app = game;
    }

    /**
     * <code>getCamera</code> returns the camera that defines the viewing for
     * this handler.
     *
     * @return This handler's camera.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * <code>setCamera</code> sets this handler's camera that will define
     * viewing.
     *
     * @param camera
     *            The new camera.
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Sets the keyboard that will receive key inputs by this handler.
     *
     * @param keyboard
     *            The keyboard to receive key inputs.
     */
    public void setKeyBindingManager(KeyBindingManager keyboard) {
        this.keyboard = keyboard;
    }

    /**
     * Returns the currently assigned keybard to receive key inputs.
     *
     * @return This handler's keyboard.
     */
    public KeyBindingManager getKeyBindingManager() {
        return keyboard;
    }

    /**
     * Sets the mouse to receive mouse inputs from.
     *
     * @param mouse
     *            This handler's new mouse.
     */
    public void setMouse(Mouse mouse) {
        this.mouse = mouse;
    }

    /**
     * Returns the mouse currently receiving inputs by this handler.
     *
     * @return This handler's mouse.
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Sets the speed of all key actions currently defined by this handler to
     * the given value.
     *
     * @param speed
     *            The new speed for all currently defined key actions.
     * @see com.jme.input.action.KeyInputAction#setSpeed(float)
     */
    public void setKeySpeed(float speed) {
        for (int i = 0; i < keyActions.size(); i++) {
            ((KeyInputAction) keyActions.get(i)).setSpeed(speed);
        }
    }

    /**
     * Sets the speed of all mouse actions currently defined by this handler to
     * the given value.
     *
     * @param speed
     *            The new speed for all currently defined mouse actions.
     * @see com.jme.input.action.MouseInputAction#setSpeed(float)
     */
    public void setMouseSpeed(float speed) {
        for (int i = 0; i < mouseActions.size(); i++) {
            ((MouseInputAction) mouseActions.get(i)).setSpeed(speed);
        }
    }

    /**
     * Adds a keyboard input action to be polled by this handler during update.
     *
     * @param inputAction
     *            The input action to be added
     */
    public void addAction(KeyInputAction inputAction) {
        keyActions.add(inputAction);
    }

    /**
     * Adds a mouse input action to be polled by this handler during update.
     *
     * @param mouseAction
     *            The input action to be added
     */
    public void addAction(MouseInputAction mouseAction) {
        mouseActions.add(mouseAction);
    }

    /**
     * Removes a keyboard input action from the list of keyActions that are
     * polled during update.
     *
     * @param inputAction
     *            The action to remove.
     */
    public void removeAction(KeyInputAction inputAction) {
        keyActions.remove(inputAction);
    }

    /**
     * Removes a mouse input action from the list of mouseActions that are
     * polled during update.
     *
     * @param mouseAction
     *            The action to remove.
     */
    public void removeAction(MouseInputAction mouseAction) {
        mouseActions.remove(mouseAction);
    }

    /**
     * Equivalent to <code>update(true,true,time)</code>
     *
     * @param time
     *            The time to pass to update.
     * @see #update(boolean, boolean, float)
     */
    public void update(float time) {
        update(true, true, time);
    }

    /**
     * Updates and calls actions on mice and keyboard inputs. If
     * <code>updateKeyboard</code> is true, the keyboard is updated for
     * inputs. If updating of keyboard actions is enabled, all key actions that
     * are active are performed. The same is true for mouse inputs.
     *
     * @param updateMouseState
     *            If true, the mouse state is updated.
     * @param updateKeyboard
     *            If true, the keyboard state is updated.
     * @param time
     *            The time value to pass to all performed actions.
     * @see com.jme.input.action.KeyInputAction#performAction(float)
     * @see com.jme.input.action.MouseInputAction#performAction(float)
     */
    public void update(boolean updateMouseState, boolean updateKeyboard,
            float time) {

        actionList.clear();
        eventList.clear();
        if (keyboard != null) {

            if (updateKeyboard) keyboard.update();

            if (updateKeyboardActionsEnabled) {
                for (int i = 0; i < keyActions.size(); i++) {
                    if (keyboard.isValidCommand(((InputAction) keyActions
                            .get(i)).getKey(), ((KeyInputAction) keyActions
                            .get(i)).allowsRepeats())) {
                        eventList.add(((InputAction) keyActions.get(i))
                                .getKey());
                        actionList.add(keyActions.get(i));
                    }
                }
            }
        }

        if (mouse != null) {

            mouse.update(updateMouseState);

            if (updateMouseActionsEnabled) {
                for (int i = 0; i < mouseActions.size(); i++) {
                    eventList
                            .add(((InputAction) mouseActions.get(i)).getKey());
                    actionList.add(mouseActions.get(i));
                }
            }
        }

        event.setTime(time);
        event.setKeys(keyboard.getKeyInput());
        event.setMouse(mouse.getMouseInput());
        event.setEventList(eventList);
        for (int i = 0; i < actionList.size(); i++) {
            ((InputAction) actionList.get(i)).performAction(event);
        }
    }

    /**
     * Sets up a keyboard to receive inputs acording to the RenderType.
     *
     * @param rendererType
     *            The render type to create a keyboard input from.
     * @see #setKeyBindingManager(com.jme.input.KeyBindingManager)
     */
    protected void setKeyBindings(RendererType rendererType) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.setKeyInput(KeyInput.get());

        setKeyBindingManager(keyboard);
    }

    /**
     * Returns if keyboard actions are performed during update.
     *
     * @return True if update will perform keyboard actions.
     * @see #update(boolean, boolean, float)
     */
    public boolean isUpdateKeyboardActionsEnabled() {
        return updateKeyboardActionsEnabled;
    }

    /**
     * Keyboard actions are performed during update only if this value is true.
     *
     * @param b
     *            If true, keyboard actions are performed during update.
     * @see #update(boolean, boolean, float)
     */
    public void setUpdateKeyboardActionsEnabled(boolean b) {
        updateKeyboardActionsEnabled = b;
    }

    /**
     * Returns if mouse actions are performed during update.
     *
     * @return True if update will perform mouse actions.
     * @see #update(boolean, boolean, float)
     */
    public boolean isUpdateMouseActionsEnabled() {
        return updateMouseActionsEnabled;
    }

    /**
     * Mouse actions are performed during update only if this value is true.
     *
     * @param b
     *            If true, mouse actions are performed during update.
     * @see #update(boolean, boolean, float)
     */
    public void setUpdateMouseActionsEnabled(boolean b) {
        updateMouseActionsEnabled = b;
    }

    /**
     * Defined by subclasses, this function should set the appropriate, default
     * mouse for this handler. <code>setMouse</code> is called after the
     * constructor.
     */
    protected abstract void setMouse();

    /**
     * Defined by subclasses, this function should set the appropriate, default
     * keyboard actions for this handler. <code>setActions</code> is called
     * after the constructor.
     */
    protected abstract void setActions();
}
