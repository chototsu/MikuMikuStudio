package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.model.XMLparser.KeyframeController;
import com.jme.scene.model.XMLparser.XMLWriter;
import com.jme.renderer.ColorRGBA;

import java.io.*;

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
        Sphere small=new Sphere("small",4,9,1);
        small.setSolidColor(ColorRGBA.black);
        Sphere medium=new Sphere("med",4,9,4);
        medium.setSolidColor(ColorRGBA.red);
        Sphere big=new Sphere("big",4,9,10);
        big.setSolidColor(ColorRGBA.blue);
        Sphere thisone=new Sphere("blarg",4,9,1);
        KeyframeController kc=new KeyframeController();
        kc.setMorphingMesh(thisone);
        kc.setKeyframe(0,small);
        kc.setKeyframe(2.5f,medium);
        kc.setKeyframe(3,big);
        kc.setRepeatType(KeyframeController.RT_CYCLE);
        thisone.addController(kc);
        StringWriter BO=new StringWriter();
        XMLWriter xw=new XMLWriter(BO);
        try {
            xw.writeScene(thisone);
        } catch (IOException e) {
            System.out.println("Darn exceptions");
        }
        System.out.println(BO);
        rootNode.attachChild(thisone);
        lightState.setEnabled(false);
    }
}
