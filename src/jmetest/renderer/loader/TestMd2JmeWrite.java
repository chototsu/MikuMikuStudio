package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.*;
import com.jme.scene.model.XMLparser.Converters.Md2ToJme;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.animation.KeyframeController;

import javax.swing.*;
import java.io.*;
import java.net.URL;

/**
 * Started Date: Jun 14, 2004<br><br>
 * Test class to test the ability to load and save .md2 files to jME binary format
 * 
 * @author Jack Lindamood
 */
public class TestMd2JmeWrite extends SimpleGame{
    float totalFPS;
    long totalCounts;
    private KeyframeController kc;
    private static final String helpMessage="Fun with KeyframeController and md2 models.  Keys are:\n" +
            "R: Make drFreak run\n" +
            "H: Make drFreak attack\n" +
            "Z: Toggle repeat type wrap and cycle\n" +
            "E: Do a quick transform to the begining\n" +
            "B: Do a smooth transform to the begining\n" +
            "Q: Do a smooth transform to drfreak's death\n";

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null,helpMessage);
        TestMd2JmeWrite app=new TestMd2JmeWrite();
        app.setDialogBehaviour(TestMd2JmeWrite.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }
    protected void simpleInitGame() {

        Md2ToJme converter=new Md2ToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        URL textu=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        URL freak=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
        Node freakmd2=null;

        try {
            long time = System.currentTimeMillis();
            converter.writeFiletoStream(freak,BO);
            System.out.println("Time to convert from md2 to .jme:"+ ( System.currentTimeMillis()-time));
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e.getMessage());
        }
        JmeBinaryReader jbr=new JmeBinaryReader();
        try {
            long time=System.currentTimeMillis();
            freakmd2=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            System.out.println("Time to convert from .jme to SceneGraph:"+ ( System.currentTimeMillis()-time));
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e.getMessage());
        }

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true));
        freakmd2.setRenderState(ts);
        freakmd2.setLocalTranslation(new Vector3f(0,0,-20));
        freakmd2.setLocalScale(.5f);
        kc=(KeyframeController) freakmd2.getChild(0).getController(0);
        kc.setSpeed(10);
        // Note: W S A D Left Down Up Right F12 ESC T L B C Already used
        KeyBindingManager.getKeyBindingManager().set("start_run",KeyInput.KEY_R);
        KeyBindingManager.getKeyBindingManager().set("start_hit",KeyInput.KEY_H);
        KeyBindingManager.getKeyBindingManager().set("toggle_wrap",KeyInput.KEY_Z);
        KeyBindingManager.getKeyBindingManager().set("start_end",KeyInput.KEY_E);
        KeyBindingManager.getKeyBindingManager().set("start_smoothbegin",KeyInput.KEY_B);
        KeyBindingManager.getKeyBindingManager().set("start_smoothdeath",KeyInput.KEY_Q);
        rootNode.attachChild(freakmd2);
     }
    protected void simpleUpdate(){
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_run", false)) {
            kc.setNewAnimationTimes(39,44);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_hit", false)) {
            kc.setNewAnimationTimes(45,52);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_end", false)) {
            kc.setNewAnimationTimes(0,196);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_smoothbegin", false)) {
            kc.setSmoothTranslation(0,25,0,196);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_smoothdeath", false)) {
            kc.setSmoothTranslation(175,25,175,182);
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
 }