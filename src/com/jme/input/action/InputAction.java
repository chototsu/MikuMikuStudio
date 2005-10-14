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
 * <code>InputAction</code> defines an interface that sets the criteria for 
 * input actions, the speed of the action is definable and a single method 
 * (performAction) is supplied to execute the action itself.
 * @author Mark Powell
 * @version $Id: InputAction.java,v 1.9 2005-10-14 11:30:29 irrisor Exp $
 */
public abstract class InputAction {
    private boolean allowsRepeats = true;

    /**
     * 
     * <code>performAction</code> executes the action. The InputActionEvent
     * is supplied to define what keys are pressed, what other actions were
     * called and the time of the event.
     *
     * @param evt the event that triggered the perform action method.
     */
    public abstract void performAction(InputActionEvent evt);

    /** A speed value that, if desired, can change how actions are performed. */
    protected float speed = 0;

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

    private String key;

    /**
     * @deprecated InputHandler takes the command used to invoke this method as a parameter of
     * {@link com.jme.input.InputHandler#addAction(InputAction, String, boolean)}
     */
    public String getKey() {
        //todo: remove this method in .11
        return key;
    }

    /**
     * @deprecated InputHandler takes the command used to invoke this method as a parameter of
     * {@link com.jme.input.InputHandler#addAction(InputAction, String, boolean)}
     */
    public void setKey(String key) {
        //todo: remove this method in .11
        this.key = key;
    }

    /**
     * @deprecated InputHandler takes the allowsRepeats parameter in
     * {@link com.jme.input.InputHandler#addAction(com.jme.input.action.InputAction, String, boolean)}
     */
    public boolean allowsRepeats() {
        //todo: remove this method in .11
        return allowsRepeats;
    }

    /**
     * @deprecated InputHandler takes the allowsRepeats parameter in
     * {@link com.jme.input.InputHandler#addAction(com.jme.input.action.InputAction, String, boolean)}
     */
    public void setAllowsRepeats(boolean allow) {
        //todo: remove this method in .11
        allowsRepeats = allow;
    }
}
