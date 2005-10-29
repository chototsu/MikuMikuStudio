package com.jme.input.joystick;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;

/**
 * Each {@link JoystickInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link JoystickInput} to receive joystick events and forward them to the joystick triggers.
 */
class TriggersJoystickInputListener implements JoystickInputListener {

    public TriggersJoystickInputListener() {
    }

    public void activate() {
        JoystickInput.get().addListener( this );
    }

    public void deactivate() {
        JoystickInput.get().removeListener( this );
    }

    private ArrayList buttonTriggers = new ArrayList();
    private ArrayList axisTriggers = new ArrayList();

    public void onAxis( Joystick controller, int axis, float axisValue ) {
        float pos = axisValue;
        float delta = Float.NaN;
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) axisTriggers.get( i );
            trigger.checkActivation( '\0', axis, pos, delta, false, controller );
        }
    }

    public void onButton( Joystick controller, int button, boolean pressed ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) buttonTriggers.get( i );
            trigger.checkActivation( '\0', button, Float.NaN, Float.NaN, pressed, controller );
        }

    }

    void add( JoystickInputHandlerDevice.JoystickButtonTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( JoystickInputHandlerDevice.JoystickButtonTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }

    void add( JoystickInputHandlerDevice.JoystickAxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    void remove( JoystickInputHandlerDevice.JoystickAxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }
}
