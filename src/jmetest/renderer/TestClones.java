package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.CloneCreator;
import com.jme.scene.Spatial;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingBox;

/**
 * Started Date: Sep 16, 2004 <br>
 * <br>
 * 
 * @author Jack Lindamood
 */
public class TestClones extends SimpleGame {

    public static void main(String[] args) {
        TestClones app = new TestClones();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    CloneCreator cc1;

    CloneCreator cc2;

    protected void simpleInitGame() {
        //        Box b=new Box("box",new Vector3f(0,0,0),new Vector3f(1,1,1));
        Sphere b = new Sphere("my sphere", 25, 25, 2);
        b.setRandomColors();
        BoundingSphere.useExactBounds = true;
        b.setModelBound(new BoundingSphere());
        b.updateModelBound();
        rootNode.attachChild(b);

        cc1 = new CloneCreator(b);
        cc1.addProperty("vertices");
        cc1.addProperty("normals");
        cc1.addProperty("colors");
        cc1.addProperty("texcoords");
        cc1.addProperty("indices");

        Box c = new Box("my box", new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        c.setRandomColors();
        c.setModelBound(new BoundingBox());
        c.updateModelBound();
        cc2 = new CloneCreator(c);
        cc2.addProperty("vertices");
        cc2.addProperty("normals");
        cc2.addProperty("colors");
        cc2.addProperty("texcoords");
        cc2.addProperty("indices");
        for (int i = 0; i < 45; i++) {
            addRandom();
        }
    }

    private void addRandom() {
        Spatial s1 = cc1.createCopy();
        s1.setLocalTranslation(new Vector3f(
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25));
        rootNode.attachChild(s1);

        Spatial s2 = cc2.createCopy();
        s2.setLocalTranslation(new Vector3f(
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25));
        rootNode.attachChild(s2);
    }
}