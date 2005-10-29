package com.jme.input.keyboard;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;

/**
 * Each {@link KeyboardInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link com.jme.input.KeyInput} to receive keyboard events and forward them to the keyboard triggers.
 */
class TriggersKeyboardInputListener implements KeyInputListener {

    public TriggersKeyboardInputListener() {
    }

    public void activate() {
        KeyInput.get().addListener( this );
    }

    public void deactivate() {
        KeyInput.get().removeListener( this );
    }

    private ArrayList buttonTriggers = new ArrayList();

    public void onKey( char character, int keyCode, boolean pressed ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) buttonTriggers.get( i );
            trigger.checkActivation( character, keyCode, Float.NaN, Float.NaN, pressed, null );
        }

    }

    void add( KeyboardInputHandlerDevice.KeyTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( KeyboardInputHandlerDevice.KeyTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }
}
