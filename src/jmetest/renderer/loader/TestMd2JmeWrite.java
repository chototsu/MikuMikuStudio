package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.*;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;

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

    public static void main(String[] args) {
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
            converter.writeFiletoStream(freak,BO);
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e.getMessage());
        }
        JmeBinaryReader jbr=new JmeBinaryReader();
        try {
            freakmd2=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
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

        rootNode.attachChild(freakmd2);
        KeyBindingManager.getKeyBindingManager().set(
            "smoothTrans",
            KeyInput.KEY_T);
     }
 }