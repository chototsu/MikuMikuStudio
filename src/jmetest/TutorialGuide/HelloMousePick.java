package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.AlphaState;
import com.jme.math.Vector3f;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.bounding.BoundingBox;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputSystem;
import com.jme.input.MouseInput;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.intersection.Intersection;

import java.net.URL;

/**
 * Started Date: Jul 22, 2004<br><br>
 *
 * Demonstrates picking with the mouse.
 * 
 * @author Jack Lindamood
 */
public class HelloMousePick extends SimpleGame {
    // This will be my mouse
    AbsoluteMouse am;
    // This will be he box in the middle
    Box b;
    public static void main(String[] args) {
        HelloMousePick app = new HelloMousePick();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    // This is called every frame.  Do rendering here.
    protected void simpleRender(){
        // Draw the mouse
//        display.getRenderer().draw(am);
        am.onDraw(display.getRenderer());
    }

    // This is called every frame.  Do changing of values here.
    protected void simpleUpdate(){
        // Get the mouse input device from the jME mouse
        MouseInput thisMouse=am.getMouseInput();
        // Is button 0 down?  Button 0 is left click
        if (thisMouse.isButtonDown(0)){
            Vector2f screenPos=new Vector2f();
            // Get the position that the mouse is pointing
            screenPos.set(thisMouse.getXAbsolute(),thisMouse.getYAbsolute()+am.getImageHeight());
            // Get the world location of that X,Y value
            Vector3f worldCoords=display.getWorldCoordinates(screenPos,0);
            // Create a ray starting from the camera, and going in the direction of the mouse's location
            Ray mouseRay=new Ray(cam.getLocation(),worldCoords.subtractLocal(cam.getLocation()));
            // Does the mouse's ray intersect the box's world bounds?
            if (Intersection.intersection(mouseRay, b.getWorldBound()))
                b.setRandomColors();
        }
    }


    protected void simpleInitGame() {
        // Create a new mouse.  Restrict its movements to the display screen.
        am=new AbsoluteMouse("The Mouse",display.getWidth(),display.getHeight());

        // Get a picture for my mouse.
        TextureState ts=display.getRenderer().getTextureState();
        URL cursorLoc;
        cursorLoc=HelloMousePick.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png");
        Texture t=TextureManager.loadTexture(cursorLoc,Texture.MM_LINEAR, Texture.FM_LINEAR, true);
        ts.setTexture(t);
        ts.setEnabled(true);
        am.setRenderState(ts);

        // Make the mouse's background blend with what's already there
        AlphaState as=display.getRenderer().getAlphaState();
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as.setTestEnabled(true);
        as.setTestFunction(AlphaState.TF_GREATER);
        as.setEnabled(true);
        am.setRenderState(as);

        // Get the mouse input device and assign it to the AbsoluteMouse
        am.setMouseInput(InputSystem.getMouseInput());
        // Move the mouse to the middle of the screen to start with
        am.setLocalTranslation(new Vector3f(display.getWidth()/2,display.getHeight()/2,0));
        // Assign the mouse to an input handler
        input.setMouse(am);
        // Create the box in the middle.  Give it a bounds
        b=new Box("My Box", new Vector3f(-1,-1,-1),new Vector3f(1,1,1));
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        rootNode.attachChild(b);
        // Remove all the lightstates so we can see the per-vertex colors
        lightState.detachAll();

        // Update the mouse's newly added render states
        am.updateRenderState();
    }
}