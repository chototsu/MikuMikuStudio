package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.model.XMLparser.KeyframeController;
import com.jme.scene.Controller;
import com.jme.renderer.ColorRGBA;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;


/**
 * Started Date: Jun 13, 2004<br><br>
 * Class to test use of KeyframeController
 * 
 * @author Jack Lindamood
 */
public class TestKeyframeController extends SimpleGame{
    KeyframeController kc;
    public static void main(String[] args) {
        new TestKeyframeController().start();
    }

    protected void simpleUpdate(){
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_middle", false)) {
            kc.setNewAnimationTimes(.5f,2.75f);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_total", false)) {
            kc.setNewAnimationTimes(0,3);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_end", false)) {
            kc.setNewAnimationTimes(3,3);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("toggle_wrap", false)) {
            if (kc.getRepeatType()==KeyframeController.RT_CYCLE)
                kc.setRepeatType(KeyframeController.RT_WRAP);
            else
                kc.setRepeatType(KeyframeController.RT_CYCLE);
        }
    }

    protected void simpleInitGame() {
        Sphere small=new Sphere("small",9,15,1);
        small.setSolidColor(ColorRGBA.black);
        Sphere medium=new Sphere("med",9,15,4);
        medium.setSolidColor(ColorRGBA.red);
        Sphere big=new Sphere("big",9,15,10);
        big.setSolidColor(ColorRGBA.blue);
        Sphere thisone=new Sphere("blarg",9,15,1);
        kc=new KeyframeController();
        kc.setMorphingMesh(thisone);
        kc.setKeyframe(0,small);
        kc.setKeyframe(2.5f,medium);
        kc.setKeyframe(3,big);
        kc.setRepeatType(KeyframeController.RT_CYCLE);
        thisone.addController(kc);
        rootNode.attachChild(thisone);
        lightState.setEnabled(false);
        // Note: T L B C Already used
        KeyBindingManager.getKeyBindingManager().set("start_middle",KeyInput.KEY_Q);
        KeyBindingManager.getKeyBindingManager().set("start_total",KeyInput.KEY_A);
        KeyBindingManager.getKeyBindingManager().set("toggle_wrap",KeyInput.KEY_Z);
        KeyBindingManager.getKeyBindingManager().set("start_end",KeyInput.KEY_E);
    }
}