package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;

/**
 * Started Date: Jul 20, 2004<br><br>
 * Simple HelloWorld program for jME
 * 
 * @author Jack Lindamood
 */
public class HelloWorld extends SimpleGame{
    public static void main(String[] args) {
        HelloWorld app=new HelloWorld();    // Create Object
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        // Signal to show properties dialog
        app.start();    // Start the program
    }
    protected void simpleInitGame() {
        Box b=new Box("My box",new Vector3f(0,0,0),new Vector3f(1,1,1));    // Make a box
        rootNode.attachChild(b);    // Put it in the scene graph
    }
}