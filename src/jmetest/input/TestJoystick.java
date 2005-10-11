package jmetest.input;

import java.io.IOException;

import com.jme.input.joystick.JoystickInput;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInputListener;

/**
 * 
 */
public class TestJoystick {
    public static void main( String[] args ) throws IOException, InterruptedException {

        JoystickInput input = JoystickInput.get();
        System.out.println( "Number of joysticks: " + input.getJoystickCount() );
        Joystick joystick = input.getDefaultJoystick();

        String name = joystick.getAxisNames().length > 0 ? joystick.getAxisNames()[0] : "";
        System.out.println( "Testing joystick '" + joystick.getName() + "' axis '" + name + "'" );

        input.update();
        System.out.println( "Value: " + joystick.getAxisValue( 0 ) );
        Thread.sleep( 1000 );
        input.update();
        System.out.println( "Value: " + joystick.getAxisValue( 0 ) );
        Thread.sleep( 1000 );
        input.update();
        System.out.println( "Value: " + joystick.getAxisValue( 0 ) );

        input.addListener( new JoystickInputListener() {
            public void onButton( Joystick controller, int button, boolean pressed ) {
                System.out.println( "Button Event: " + button + ", " + pressed );
            }

            public void onAxis( Joystick controller, int axis, float axisValue ) {
                System.out.println( "Axis Event: " + axis + ", " + controller.getAxisNames()[axis] + ", " + axisValue );
            }
        } );

        System.out.println( "Polling manually:" );
        input.update();
        Thread.sleep( 1000 );
        input.update();
        Thread.sleep( 1000 );
        input.update();

//        System.out.println( "Polling in extra thread - hit enter to leave:" );
//
//        input.startUpdateThread( 5 );
//
//        System.in.read();
    }
}
