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

package jmetest.input;

import java.io.IOException;

import com.jme.input.*;
import com.jme.input.joystick.Joystick;
import com.jme.input.joystick.JoystickInput;
import com.jme.input.joystick.JoystickInputListener;

/**
 * 
 */
public class TestJoystick {
    public static void main( String[] args ) throws IOException, InterruptedException {
        JoystickInput.setProvider(InputSystem.INPUT_SYSTEM_LWJGL);
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
