package com.jme.input.keyboard;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * Creates InputHandler triggers for keyboard support.
 */
public class KeyboardInputHandlerDevice extends InputHandlerDevice {
    public KeyboardInputHandlerDevice() {
        super( InputHandler.DEVICE_KEYBOARD );
    }

    protected void createTriggers( InputAction action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {
        if ( button == InputHandler.BUTTON_ALL ) {
            new KeyTrigger( inputHandler, "key", action, button, allowRepeats );
        }
        else if ( button != InputHandler.BUTTON_NONE ) {
            inputHandler.addAction( action, "key code " + button, button, allowRepeats );
        }
        if ( axis != InputHandler.AXIS_NONE ) {
//            LoggingSystem.getLogger().warning( "addAction was called with an axis specified for keyboard!" );
        }

    }

    private TriggersKeyboardInputListener keyboardListener;

    public synchronized TriggersKeyboardInputListener getKeyboardListener() {
        if ( keyboardListener == null ) {
            keyboardListener = new TriggersKeyboardInputListener();
            keyboardListener.activate();
        }
        return keyboardListener;
    }

    protected class KeyTrigger extends ActionTrigger {
        private int keyCode;

        private char lastChar;
        private int lastKeyCode;
        private boolean pressed;

        public KeyTrigger( InputHandler handler, String triggerName, InputAction action, int keyCode, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.keyCode = keyCode;
            getKeyboardListener().add( this );
        }

        protected void remove() {
            super.remove();
            getKeyboardListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( lastKeyCode );
            event.setTriggerPressed( pressed );
            event.setTriggerCharacter( lastChar );
        }

        protected String getDeviceName() {
            return InputHandler.DEVICE_KEYBOARD;
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( buttonIndex == this.keyCode || this.keyCode == InputHandler.BUTTON_ALL ) {
                lastChar = character;
                lastKeyCode = buttonIndex;
                if ( allowRepeats ) {
                    if ( pressed ) {
                        this.pressed = true;
                        activate();
                    }
                    else {
                        deactivate();
                    }
                } else {
                    this.pressed = pressed;
                    activate();
                }
            }
        }
    }
}
