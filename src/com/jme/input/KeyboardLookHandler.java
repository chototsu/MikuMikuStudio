package com.jme.input;

import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.renderer.Camera;

/**
 * <code>KeyboardLookHandler</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera.
 */
public class KeyboardLookHandler extends InputHandler {
    public KeyboardLookHandler( Camera cam ) {
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set( "forward", KeyInput.KEY_W );
        keyboard.set( "backward", KeyInput.KEY_S );
        keyboard.set( "strafeLeft", KeyInput.KEY_A );
        keyboard.set( "strafeRight", KeyInput.KEY_D );
        keyboard.set( "lookUp", KeyInput.KEY_UP );
        keyboard.set( "lookDown", KeyInput.KEY_DOWN );
        keyboard.set( "turnRight", KeyInput.KEY_RIGHT );
        keyboard.set( "turnLeft", KeyInput.KEY_LEFT );

        addAction( new KeyForwardAction( cam, 0.5f ), "forward", true );
        addAction( new KeyBackwardAction( cam, 0.5f ), "backward", true );
        addAction( new KeyStrafeLeftAction( cam, 0.5f ), "strafeLeft", true );
        addAction( new KeyStrafeRightAction( cam, 0.5f ), "strafeRight", true );
        addAction( new KeyLookUpAction( cam, 0.01f ), "lookUp", true );
        addAction( new KeyLookDownAction( cam, 0.01f ), "lookDown", true );
        addAction( new KeyRotateRightAction( cam, 0.01f ), "turnRight", true );
        addAction( new KeyRotateLeftAction( cam, 0.01f ), "turnLeft", true );
    }
}
