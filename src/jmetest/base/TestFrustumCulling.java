package jmetest.base;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

/**
 * @author Irrisor
 */
public class TestFrustumCulling extends SimpleGame {
    protected void simpleInitGame() {
        for ( int x = 0; x < 10; x++ ) {
            for ( int y = 0; y < 10; y++ ) {
                Box box = new Box( x + ", " + y, new Vector3f(), 0.5f, 0.5f, 0.5f );
                box.setModelBound( new BoundingBox() );
                box.updateModelBound();
                box.getLocalTranslation().set( x, 0, y );
                rootNode.attachChild( box );
            }
        }

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                // zoom in
                cam.setFrustum( cam.getFrustumNear(), cam.getFrustumFar(),
                        cam.getFrustumLeft()*0.99f, cam.getFrustumRight()*0.99f,
                        cam.getFrustumTop()*0.99f, cam.getFrustumBottom()*0.99f );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_Q, InputHandler.AXIS_NONE, true );

        input.addAction( new InputAction() {
            public void performAction( InputActionEvent evt ) {
                // zoom in
                cam.setFrustum( cam.getFrustumNear(), cam.getFrustumFar(),
                        cam.getFrustumLeft()*1.01f, cam.getFrustumRight()*1.01f,
                        cam.getFrustumTop()*1.01f, cam.getFrustumBottom()*1.01f );
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_E, InputHandler.AXIS_NONE, true );
    }

    public static void main( String[] args ) {
        new TestFrustumCulling().start();
    }
}
