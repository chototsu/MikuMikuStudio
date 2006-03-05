/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

package com.jme.input.util;

import java.util.ArrayList;

import com.jme.input.InputHandler;
import com.jme.input.ActionTrigger;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * This class can be used to create synthetic buttons for {@link InputHandler}s. As an example see {@link TwoButtonAxis}.
 */
public abstract class SyntheticButton {
    /**
     * name of this button.
     */
    protected final String name;
    /**
     * store the index in util device
     */
    private int index;
    /**
     * list of triggers
     */
    private ArrayList buttonTriggers = new ArrayList();

    public SyntheticButton( String name ) {
        this.name = name;
        UtilInputHandlerDevice.get().addButton( this );
    }

    /**
     * @return the name of this button
     */
    public String getName() {
        return name;
    }

    /**
     * @return index of this button (used when registering with InputHandler)
     * @see #getDeviceName()
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * setter for field button
     */
    void setIndex(final int value) {
        this.index = value;
    }

    /**
     * @return name of the virtual device this button is attached to (used when registering with InputHandler)
     * @see #getIndex()
     */
    public final String getDeviceName() {
        return UtilInputHandlerDevice.DEVICE_UTIL;
    }

    protected void createTrigger( InputHandler inputHandler, InputAction action, boolean allowRepeats ) {
        new ButtonTrigger( inputHandler, action, allowRepeats );
    }

    /**
     * @param trigger what to add to list of triggers
     */
    private void add( ButtonTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    /**
     * @param trigger what to remove from list of triggers
     */
    private void remove( ButtonTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }

    /**
     * check all triggers
     * @see com.jme.input.ActionTrigger#checkActivation(char, int, float, float, boolean, Object)
     */
    protected void trigger( float delta, char character, float value, boolean pressed, Object data ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) buttonTriggers.get( i );
            trigger.checkActivation( character, getIndex(), value, delta, pressed, data );
        }
    }

    /**
     * trigger for simulating button
     */
    protected class ButtonTrigger extends ActionTrigger {

        public ButtonTrigger( InputHandler handler, InputAction action, boolean allowRepeats ) {
            super( handler, SyntheticButton.this.getName(), action, allowRepeats );
            SyntheticButton.this.add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            SyntheticButton.this.remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event, int invocationIndex ) {
            super.putTriggerInfo( event, invocationIndex );
            event.setTriggerIndex( getIndex() );
            event.setTriggerDelta( delta );
            event.setTriggerPosition( position );
        }

        protected final String getDeviceName() {
            return UtilInputHandlerDevice.DEVICE_UTIL;
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( buttonIndex == getIndex() ) {
                this.delta = delta;
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
}
