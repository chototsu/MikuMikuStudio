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

package com.jme.input.action;

/**
 * <code>InputAction</code> defines an interface for creating input actions.
 * These actions can correspond to any event defined by a key value. The
 * <code>InputHandler</code> class typically maintains a collection of the
 * actions and when required calls the actions <code>performAction</code>
 * method.
 * 
 * @see com.jme.input.InputHandler
 * @author Mark Powell
 * @version $Id: KeyInputAction.java,v 1.2 2005-09-15 17:13:57 renanse Exp $
 */
public abstract class KeyInputAction implements InputAction {

    /**
     * If true, a single button press results in multiple calls to this class's
     * performAction(float)
     */
    protected boolean allowsRepeats = true;

    /** A speed value that, if desired, can change how actions are performed. */
    protected float speed = 0;

    /** A name that identifies this action. */
    protected String key;

    /** The char associated with this event. */
    protected char keyChar;

    /**
     * 
     * <code>setSpeed</code> defines the speed at which this action occurs.
     * 
     * @param speed
     *            the speed at which this action occurs.
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns the currently set speed. Speed is 0 by default.
     * 
     * @return The current speed.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * 
     * <code>getKey</code> retrieves the key associated with this action.
     * 
     * @return the key associated with the action.
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * <code>setKey</code> sets the key associated with this action.
     * 
     * @param key
     *            the key associated with the action.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     * <code>getKey</code> retrieves the key associated with this action.
     * 
     * @return the key associated with the action.
     */
    public char getKeyChar() {
        return keyChar;
    }

    /**
     * 
     * <code>setKey</code> sets the key associated with this action.
     * 
     * @param key
     *            the key associated with the action.
     */
    public void setKeyChar(char keyChar) {
        this.keyChar = keyChar;
    }

    /**
     * Returns true if a single key press is set to allow repeated performAction
     * calls.
     * 
     * @return The current repeat state.
     */
    public boolean allowsRepeats() {
        return allowsRepeats;
    }

    /**
     * Sets if a single key press allows repeated performAction calls.
     * 
     * @param allow
     *            If true, a single key press allows fo repeated performAction
     *            calls.
     */
    public void setAllowsRepeats(boolean allow) {
        allowsRepeats = allow;
    }
}