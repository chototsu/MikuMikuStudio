package com.jme.input.joystick.lwjgl;

import java.lang.reflect.Field;

import com.jme.input.joystick.Joystick;
import net.java.games.input.Rumbler;
import org.lwjgl.input.Controller;

/**
 * LWJGL Implementation of {@link Joystick}.
 */
class LWJGLJoystick implements Joystick {

    private Controller controller;
    private Rumbler[] rumblers;

    LWJGLJoystick( Controller controller ) {
        this.controller = controller;

        //fix me: dirty hack to obtain the rumblers:
        try {
            Field targetField = controller.getClass().getDeclaredField( "target" );
            targetField.setAccessible( true );
            net.java.games.input.Controller jinputController = (net.java.games.input.Controller) targetField.get( controller );
            Rumbler[] rumblers = jinputController.getRumblers();
            this.rumblers = new Rumbler[getAxisCount()];
            String[] axisNames = getAxisNames();
            for ( int i = 0; i < rumblers.length; i++ ) {
                Rumbler rumbler = rumblers[i];
                for ( int j = 0; j < axisNames.length; j++ ) {
                    String axisName = axisNames[j];
                    if ( axisName.equals( rumbler.getAxisName() ) ) {
                        this.rumblers[j] = rumbler;
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void rumble( int axis, float intensity ) {
        if ( rumblers != null && axis < rumblers.length ) {
            Rumbler rumbler = rumblers[axis];
            if ( rumbler != null ) {
                rumbler.rumble( intensity );
            }
        }
    }

    public String[] getAxisNames() {
        Controller c = controller;
        String[] axises = new String[c.getAxisCount()];
        for ( int i = 0; i < axises.length; i++ ) {
            axises[i] = c.getAxisName( i );
        }
        return axises;
    }

    public int getAxisCount() {
        return controller.getAxisCount();
    }

    public float getAxisValue( int axis ) {
        Controller c = controller;
        if ( axis < c.getAxisCount() ) {
            return c.getAxisValue( axis );
        }
        else {
            return 0;
        }
    }

    public int getButtonCount() {
        return controller.getButtonCount();
    }

    public boolean isButtonPressed( int button ) {
        Controller c = controller;
        if ( button < c.getButtonCount() ) {
            return c.isButtonPressed( button );
        }
        else {
            return false;
        }
    }

    public String getName() {
        return controller.getName();
    }
}
