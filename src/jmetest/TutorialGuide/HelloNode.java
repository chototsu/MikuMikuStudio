package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Jul 20, 2004<br><br>
 *
 * Simple Node object with a few Geometry manipulators.
 * 
 * @author Jack Lindamood
 */
public class HelloNode extends SimpleGame {
    public static void main(String[] args) {
        HelloNode app = new HelloNode();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        Box b=new Box("My Box",new Vector3f(0,0,0),new Vector3f(1,1,1));
        // Give the box a bounds object to allow it to be culled
        b.setModelBound(new BoundingSphere());
        // Calculate the best bounds for the object you gave it
        b.updateModelBound();
        // Move the box 2 in the y direction up
        b.setLocalTranslation(new Vector3f(0,2,0));
        // Give the box a solid color of blue.
        b.setSolidColor(ColorRGBA.blue);

        Sphere s=new Sphere("My sphere",10,10,1f);
        // Do bounds for the sphere, but we'll use a BoundingBox this time
        s.setModelBound(new BoundingBox());
        s.updateModelBound();
        // Give the sphere random colors
        s.setRandomColors();

        // Make a node and give it children
        Node n=new Node("My Node");
        n.attachChild(b);
        n.attachChild(s);
        // Make the node and all its children 5 times larger.
        n.setLocalScale(5);

        // Remove  lighting for rootNode so that it will use our basic colors.
        lightState.detachAll();
        rootNode.attachChild(n);
    }
}