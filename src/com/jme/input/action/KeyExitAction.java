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
package com.jme.input.action;

import com.jme.app.AbstractGame;

/**
 * <code>KeyExitAction</code> is used to call <code>finish</code> on the
 * application. This action can be assigned to an event, such as the escape
 * key being pressed, or a exit button being chosen.
 * @author Mark Powell
 * @version $Id: KeyExitAction.java,v 1.4 2004-04-22 22:26:31 renanse Exp $
 */
public class KeyExitAction implements InputAction{
    private AbstractGame app;
    private String key;

    public void setSpeed(float speed) {

    }

    /**
     * Constructor instantiates a new <code>KeyExitAction</code> object.
     * @param app the reference to the application.
     */
    public KeyExitAction(AbstractGame app) {
        this.app = app;
    }

    /**
     * <code>performAction</code> calls the <code>finish</code> method of the
     * provided application.
     * @see com.jme.input.action.InputAction#performAction(float)
     */
    public void performAction(float time) {
        app.finish();
    }

    /**
     * <code>getKey</code> retrieves the key associated with this action.
     * @see com.jme.input.action.InputAction#getKey()
     */
    public String getKey() {
        return key;
    }

    /**
     * <code>setKey</code> sets the key associated with this action.
     * @see com.jme.input.action.InputAction#setKey(java.lang.String)
     */
    public void setKey(String key) {
        this.key = key;

    }

}
