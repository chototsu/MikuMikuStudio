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

import java.util.ArrayList;
import java.util.HashMap;

import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.MouseInputAction;

/**
 * <code>InputHandler</code> handles mouse and key inputs. Inputs are added
 * and whenever update is called whenever action needs to take place (usually
 * every frame). Mouse actions are performed every update call. Keyboard actions
 * are performed only if the correct key is pressed.
 *
 * @author Mark Powell
 * @author Jack Lindamood - (javadoc only)
 * @version $Id: InputHandler.java,v 1.25 2005-10-13 07:04:39 irrisor Exp $
 */
public class InputHandler extends AbstractInputHandler {

    /** List of keyboard actions. They are performed in update if valid. */
    protected ArrayList keyActions;

	/** List of mouse actions. They are performed in update. */
	protected ArrayList mouseActions;

    /** The mouse where valid mouse actions are taken from in update. */
    protected Mouse mouse;

    /** event that will be used to call each action this frame */
    protected InputActionEvent event;

    /** list of all actions that will be executed this frame */
    protected ArrayList actionList;

    /**
     * Creates a new input handler. By default, there are no keyboard actions or
     * mouse actions defined.
     */
    public InputHandler() {
        keyActions = new ArrayList();
        mouseActions = new ArrayList();
        event = new InputActionEvent();
        actionList = new ArrayList();
    }

    /**
     * @return keyboard manager.
     * @deprecated use {@link KeyBindingManager#getKeyBindingManager()}
     */
    public KeyBindingManager getKeyBindingManager() {
        //todo: remove this method in .11
        return KeyBindingManager.getKeyBindingManager();
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
     * Binds to the key an action and an identification string. The
     * identification maps to the key and the action will receive updates on the
     * key.
     *
     * @param keyIdent
     *            A string identifying this key/action purpose. IE "jump_key"
     * @param keyInputValue
     *            A key that will fire this action. IE KeyInput.KEY_SPACE
     * @param action
     *            An AbstractInputAction that is performed on the keyInputValue.
     */
    public void addKeyboardAction(String keyIdent, int keyInputValue,
                                  KeyInputAction action) {

            KeyBindingManager keyboard = KeyBindingManager
                    .getKeyBindingManager();

        keyboard.set(keyIdent, keyInputValue);
        action.setKey(keyIdent);
        addAction(action);
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
     * Clears all keyboard actions currently stored.
     */
    public void clearKeyboardActions() {
        keyActions.clear();
    }

    /**
     * Clears all mouse actions currently stored.
     */
    public void clearMouseActions() {
        mouseActions.clear();
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
     * Checks all key and mouse actions to see if they are valid commands. If
     * so, performAction is called on the command with the given time.
     *
     * @param time
     *            The time to pass to every key and mouse action that is active.
     */
    public void update(float time) {
        actionList.clear();

        for ( int i = 0; i < keyActions.size(); i++ ) {
            KeyInputAction keyInputAction = (KeyInputAction) keyActions.get( i );
            if ( KeyBindingManager.getKeyBindingManager().isValidCommand( keyInputAction.getKey(), keyInputAction.allowsRepeats() ) ) {
                actionList.add( keyInputAction );
            }
        }

        if (mouse != null) {
            mouse.update();
            for (int i = 0; i < mouseActions.size(); i++) {
                actionList.add(mouseActions.get(i));
            }
        }

        event.setTime(time);

        for (int i = 0; i < actionList.size(); i++) {
            ((InputAction) actionList.get(i)).performAction(event);
        }

    }

    //todo: provide a list of events that occur this frame (update call)

    public static float getFloatProp(HashMap props, String key, float defaultVal) {
        if (props == null || props.get(key) == null)
            return defaultVal;
        else
            return Float.parseFloat(props.get(key).toString());
    }

    public static int getIntProp(HashMap props, String key, int defaultVal) {
        if (props == null || props.get(key) == null)
            return defaultVal;
        else
            return Integer.parseInt(props.get(key).toString());
    }

    public static boolean getBooleanProp(HashMap props, String key, boolean defaultVal) {
        if (props == null || props.get(key) == null)
            return defaultVal;
        else
            return "true".equalsIgnoreCase(props.get(key).toString());
    }

    public static Object getObjectProp(HashMap props, String key, Object defaultVal) {
        if (props == null || props.get(key) == null)
            return defaultVal;
        else
            return props.get(key);
    }
}