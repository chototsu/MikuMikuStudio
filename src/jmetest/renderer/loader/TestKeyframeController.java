package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.model.XMLparser.KeyframeController;
import com.jme.renderer.ColorRGBA;


/**
 * Started Date: Jun 13, 2004<br><br>
 * Class to test use of KeyframeController
 * 
 * @author Jack Lindamood
 */
public class TestKeyframeController extends SimpleGame{
    public static void main(String[] args) {
        new TestKeyframeController().start();
    }

    protected void simpleInitGame() {
        Sphere small=new Sphere("small",9,15,1);
        small.setSolidColor(ColorRGBA.black);
        Sphere medium=new Sphere("med",9,15,4);
        medium.setSolidColor(ColorRGBA.red);
        Sphere big=new Sphere("big",9,15,10);
        big.setSolidColor(ColorRGBA.blue);
        Sphere thisone=new Sphere("blarg",9,15,1);
        KeyframeController kc=new KeyframeController();
        kc.setMorphingMesh(thisone);
        kc.setKeyframe(0,small);
        kc.setKeyframe(2.5f,medium);
        kc.setKeyframe(3,big);
        kc.setRepeatType(KeyframeController.RT_CYCLE);
        thisone.addController(kc);
        rootNode.attachChild(thisone);
        lightState.setEnabled(false);
    }
}