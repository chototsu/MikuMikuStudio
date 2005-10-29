package com.jme.input.mouse;

import com.jme.input.ActionTrigger;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;

/**
 * Creates InputHandler triggers for mouse support.
 */
public class MouseInputHandlerDevice extends InputHandlerDevice {
    public MouseInputHandlerDevice() {
        super( InputHandler.DEVICE_MOUSE );
    }

    private TriggersMouseInputListener mouseListener;

    public synchronized TriggersMouseInputListener getMouseListener() {
        if ( mouseListener == null ) {
            mouseListener = new TriggersMouseInputListener();
            mouseListener.activate();
        }
        return mouseListener;
    }

    protected void createTriggers( InputAction action, int axis, int button, boolean allowRepeats, InputHandler inputHandler ) {
        if ( button != InputHandler.BUTTON_NONE ) {
            int minButton = button == InputHandler.BUTTON_ALL ? 0 : button;
            int maxButton = button == InputHandler.BUTTON_ALL ? MouseInput.get().getButtonCount() - 1 : button;
            for ( int i = minButton; i <= maxButton; i++ ) {
                new MouseButtonTrigger( inputHandler, MouseInput.get().getButtonName( i ),
                        action, i, allowRepeats );
            }
        }
        if ( axis != InputHandler.AXIS_NONE ) {
            int minAxis = axis == InputHandler.AXIS_ALL ? 0 : axis;
            int maxAxis = axis == InputHandler.AXIS_ALL ? 2 : axis;
            for ( int i = minAxis; i <= maxAxis; i++ ) {
                String axisName;
                switch ( i ) {
                    case 0:
                        axisName = "X Axis";
                        break;
                    case 1:
                        axisName = "Y Axis";
                        break;
                    case 2:
                        axisName = "Wheel";
                        break;
                    default:
                        axisName = null;
                }
                new MouseAxisTrigger( inputHandler, axisName, action, i, allowRepeats );
            }
        }
    }

    protected class MouseButtonTrigger extends ActionTrigger {
        private int button;

        public MouseButtonTrigger( InputHandler handler, String triggerName, InputAction action, int button, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.button = button;
            getMouseListener().add( this );
        }

        protected void remove() {
            super.remove();
            getMouseListener().remove( this );
        }

        protected void putTriggerInfo( InputActionEvent event ) {
            super.putTriggerInfo( event );
            event.setTriggerIndex( button );
            event.setTriggerPressed( true );
            final char buttonChar;
            switch ( button ) {
                case 0:
                    buttonChar = 'L';
                    break;
                case 1:
                    buttonChar = 'R';
                    break;
                case 2:
                    buttonChar = 'M';
                    break;
                default:
                    buttonChar = (char) ( 'a' + button - 3 );
            }
            event.setTriggerCharacter( buttonChar );
        }

        protected String getDeviceName() {
            return InputHandler.DEVICE_MOUSE;
        }

        public void checkActivation( char character, int buttonIndex, float position, float delta, boolean pressed, Object data ) {
            if ( buttonIndex == this.button ) {
                if ( pressed ) {
                    activate();
                }
                else {
                    deactivate();
                }
            }
        }
    }

    protected class MouseAxisTrigger extends ActionTrigger {
        private int axis;

        public MouseAxisTrigger( InputHandler handler, String triggerName, InputAction action, int axis, boolean allowRepeats ) {
            super( handler, triggerName, action, allowRepeats );
            this.axis = axis;
            getMouseListener().add( this );
            if ( allowRepeats ) {
                activate();
            }
        }

        protected void remove() {
            super.remove();
            getMouseListener().remove( this );
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
            return InputHandler.DEVICE_MOUSE;
        }

        public void checkActivation( char character, int axisIndex, float position, float delta, boolean pressed, Object data ) {
            if ( axisIndex == this.axis ) {
                this.delta = delta;
                this.position = position;
                if ( !allowRepeats ) {
                    activate();
                }
            }
        }
    }
}
