/*
 * Created on Jun 1, 2004
 */
package jmetest.effects;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.effects.Shake;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.NodeHandler;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.model.Loader;
import com.jme.scene.model.ms3d.MilkLoader;


/**
 * @author Ahmed
 */
public class TestShake extends SimpleGame {
    
    Shake camShake;
    
    protected void simpleUpdate() {
        if (KeyBindingManager.getKeyBindingManager().isValidCommand("Shake", false)) {
            camShake.setActive(!camShake.isActive());
        }
    }
    
    protected void simpleInitGame() {
        display.setTitle("CameraShake");
        
        CameraNode camNode = new CameraNode("camNode", cam);
        
        input = new NodeHandler(this, camNode, properties.getRenderer());
        KeyBindingManager.getKeyBindingManager().set("Shake", KeyInput.KEY_SPACE);
        input.setKeySpeed(10f);
        input.setMouseSpeed(1.5f);
        
        Loader milk = new MilkLoader();
        milk.setLoadFlag(Loader.LOAD_CONTROLLERS);
        milk.setLoadFlag(Loader.PRECOMPUTE_BOUNDS);
        
        URL milkshapeURL = TestShake.class.getClassLoader().getResource("jmetest/data/model/msascii/run.ms3d");
        MilkLoader loader = new MilkLoader();
        Node milkshapeModel = loader.load(milkshapeURL);
        milkshapeModel.setLocalScale(0.1f);
        milkshapeModel.getController(0).setSpeed(10f);
        
        camShake = new Shake(camNode, 1f);
        camShake.setType(Shake.RANDOMISE);
        camNode.addController(camShake);
        
        rootNode.attachChild(milkshapeModel);
        rootNode.attachChild(camNode);
    }

    public static void main(String[] args) {
        TestShake app = new TestShake();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
