package com.jme.input.joystick;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * Creates InputHandler triggers for joystick support.
 */
public class JoystickInputHandlerDevice extends InputHandlerDevice {
    protected final Joystick joystick;

    public JoystickInputHandlerDevice( Joystick joystick ) {
        super( joystick.getName() );
        this.joystick = joystick;
    }

    private TriggersJoystickInputListener joystickListener;

    public synchronized TriggersJoystickInputListener getJoystickListener() {
        if ( joystickListener == null ) {
            joystickListener = new TriggersJoystickInputListener();
            joystickListener.activate();
        }
        return joystickListener;
    }

    protected void createTriggers( InputAction action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {

        if ( axis != InputHandler.AXIS_NONE && axis < joystick.getAxisCount() ) {
            String[] axisNames = joystick.getAxisNames();
            int minAxis = axis == InputHandler.AXIS_ALL ? 0 : axis;
            int maxAxis = axis == InputHandler.AXIS_ALL ? axisNames.length - 1 : axis;
            for ( int j = minAxis; j <= maxAxis; j++ ) {
                new JoystickAxisTrigger( inputHandler, axisNames[j], action, joystick, j, allowRepeats );
            }
        }
        if ( button != InputHandler.BUTTON_NONE && axis < joystick.getButtonCount() ) {
            int minButton = button == InputHandler.BUTTON_ALL ? 0 : button;
            int maxButton = button == InputHandler.BUTTON_ALL ? joystick.getButtonCount() - 1 : button;
            for ( int j = minButton; j <= maxButton; j++ ) {
                new JoystickButtonTrigger( inputHandler, "BUTTON" + j, action, joystick, j, allowRepeats );
            }
        }
    }

    protected class JoystickButtonTrigger extends ActionTrigger {
        private int button;
        private Joystick joystick;

        public JoystickButtonTrigger( InputHandler handler, String triggerName, InputAction action,
                                      Joystick joystick, int button, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.button = button;
            this.joystick = joystick;
            getJoystickListener().add( this );
        }

        protected void remove() {
            super.remove();
            getJoystickListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( button );
            event.setTriggerCharacter( (char) ( 'A' + button ) );
            event.setTriggerPressed( true );
        }

        protected String getDeviceName() {
            return joystick.getName();
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( data == joystick && buttonIndex == this.button ) {
                if ( pressed ) {
                    activate();
                }
                else {
                    deactivate();
                }
            }
        }
    }

    protected class JoystickAxisTrigger extends ActionTrigger {
        private Joystick joystick;
        private int axis;

        public JoystickAxisTrigger( InputHandler handler, String triggerName, InputAction action, Joystick joystick,
                                    int axis, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.joystick = joystick;
            this.axis = axis;
            getJoystickListener().add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            getJoystickListener().remove( this );
        }

        private float delta;
        private float position;

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( axis );
            event.setTriggerDelta( delta );
            event.setTriggerPosition( position );
        }

        protected String getDeviceName() {
            return joystick.getName();
        }

        public void checkActivation( char character, int axisIndex, float position, float delta, boolean pressed, Object data ) {
            if ( data == joystick && axisIndex == this.axis ) {
                this.delta = position - this.position; //delta is the position, too
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
}
