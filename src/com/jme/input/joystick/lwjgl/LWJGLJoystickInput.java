package com.jme.input.joystick.lwjgl;

import java.util.ArrayList;

import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInputListener;
import com.jme.input.joystick.JoystickInput;
import com.jme.util.LoggingSystem;
import org.lwjgl.input.Controllers;
import org.lwjgl.LWJGLException;

/**
 * LWJGL Implementation of {@link JoystickInput}.
 */
public class LWJGLJoystickInput extends JoystickInput {
    private ArrayList joysticks;
    private DummyJoystick dummyJoystick;


    protected LWJGLJoystickInput() {
        try {
            Controllers.create();
            updateJoystickList();
        } catch ( LWJGLException e ) {
            LoggingSystem.getLogger().warning( "Initalizing joystick support failed: " + e );
        }
    }

    private void updateJoystickList() {
        joysticks = new ArrayList();
        for ( int i = 0; i < Controllers.getControllerCount(); i++ ) {
            joysticks.add( new LWJGLJoystick( Controllers.getController( i ) ) );
        }
    }


    public void update() {
        Controllers.poll();
        while ( Controllers.next() ) {
            if ( listeners != null && listeners.size() > 0 ) {
                Joystick joystick = getJoystick( Controllers.getEventSource().getIndex() );
                int controlIndex = Controllers.getEventControlIndex();
                if ( Controllers.isEventButton() ) {
                    boolean buttonPressed = joystick.isButtonPressed( controlIndex );
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        JoystickInputListener listener = (JoystickInputListener) listeners.get( i );
                        listener.onButton( joystick, controlIndex, buttonPressed );
                    }
                }
                else if ( Controllers.isEventAxis() ) {
                    float axisValue = joystick.getAxisValue( controlIndex );
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        JoystickInputListener listener = (JoystickInputListener) listeners.get( i );
                        listener.onAxis( joystick, controlIndex, axisValue );
                    }
                }
            }
        }
    }

    public int getJoystickCount() {
        int numJoysticks = joysticks.size();
        if ( numJoysticks != Controllers.getControllerCount() )
        {
            updateJoystickList();
        }
        return numJoysticks;
    }

    public Joystick getJoystick( int index ) {
        return (Joystick) joysticks.get( index );
    }

    public Joystick getDefaultJoystick() {
        if ( getJoystickCount() > 0 )
        {
            return getJoystick( getJoystickCount()-1 );
        }
        else
        {
            if ( dummyJoystick == null )
            {
                dummyJoystick = new DummyJoystick();
            }
            return dummyJoystick;
        }
    }

    protected void destroy() {
        Controllers.destroy();
    }

    private static class DummyJoystick implements Joystick {
        public void rumble( int axis, float intensity ) {
        }

        public String[] getAxisNames() {
            return new String[0];
        }

        public int getAxisCount() {
            return 0;
        }

        public float getAxisValue( int axis ) {
            return 0;
        }

        public int getButtonCount() {
            return 0;
        }

        public boolean isButtonPressed( int button ) {
            return false;
        }

        public String getName() {
            return "Dummy";
        }

        public void setDeadZone( int axis, float value ) {

        }
    }
}
