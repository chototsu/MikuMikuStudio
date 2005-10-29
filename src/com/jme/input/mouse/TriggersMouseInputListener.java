package com.jme.input.mouse;

import java.util.ArrayList;

import com.jme.input.ActionTrigger;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.system.DisplaySystem;

/**
 * Each {@link MouseInputHandlerDevice} has an instance of this class which is subscribed at the
 * {@link com.jme.input.MouseInput} to receive mouse events and forward them to the mouse triggers.
 */
class TriggersMouseInputListener implements MouseInputListener {
    private float maxX = DisplaySystem.getDisplaySystem().getWidth();
    private float maxY = DisplaySystem.getDisplaySystem().getHeight();
    private float maxWheel = 120;
    public static final boolean DO_CLAMP = false;

    public TriggersMouseInputListener() {
    }

    public void activate() {
        MouseInput.get().addListener( this );
    }

    public void deactivate() {
        MouseInput.get().removeListener( this );
    }

    private ArrayList buttonTriggers = new ArrayList();
    private ArrayList axisTriggers = new ArrayList();

    // javadoc copied from overwritten method
    public void onButton( int button, boolean pressed, int x, int y ) {
        for ( int i = buttonTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) buttonTriggers.get( i );
            trigger.checkActivation( '\0', button, Float.NaN, Float.NaN, pressed, null );
        }
    }

    // javadoc copied from overwritten method
    public void onWheel( int wheelDelta, int x, int y ) {
        float pos = clamp( MouseInput.get().getWheelRotation() / maxWheel );
        float delta = clamp( wheelDelta / maxWheel );
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) axisTriggers.get( i );
            trigger.checkActivation( '\0', 2, pos, delta, false, null );
        }
    }

    // javadoc copied from overwritten method
    public void onMove( int xDelta, int yDelta, int newX, int newY ) {
        float posX = clamp( newX / maxX );
        float posY = clamp( newY / maxY );
        float deltaX = clamp( xDelta / maxX );
        float deltaY = clamp( yDelta / maxY );
        for ( int i = axisTriggers.size() - 1; i >= 0; i-- ) {
            final ActionTrigger trigger = (ActionTrigger) axisTriggers.get( i );
            if ( xDelta != 0 ) {
                trigger.checkActivation( '\0', 0, posX, deltaX, DO_CLAMP, null );
            }
            if ( yDelta != 0 ) {
                trigger.checkActivation( '\0', 1, posY, deltaY, DO_CLAMP, null );
            }
        }
    }

    void add( MouseInputHandlerDevice.MouseButtonTrigger trigger ) {
        buttonTriggers.add( trigger );
    }

    void remove( MouseInputHandlerDevice.MouseButtonTrigger trigger ) {
        buttonTriggers.remove( trigger );
    }

    void add( MouseInputHandlerDevice.MouseAxisTrigger trigger ) {
        axisTriggers.add( trigger );
    }

    void remove( MouseInputHandlerDevice.MouseAxisTrigger trigger ) {
        axisTriggers.remove( trigger );
    }

    /**
     * @param value any float value
     * @return float value clamped to [-1;1] if {@link #DO_CLAMP} is true, otherwise returns value
     */
    private static float clamp( float value ) {
        if ( DO_CLAMP ) {
            if ( value > 1 ) {
                return 1;
            }
            if ( value < -1 ) {
                return -1;
            }
        }
        return value;
    }
}
