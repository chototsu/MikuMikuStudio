package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.Md3ToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.image.Texture;

import java.io.*;
import java.net.URL;

/**
 * Started Date: Jul 15, 2004<br><br>
 *
 * Test the ability to load MD3 files.
 * 
 * @author Jack Lindamood
 */
public class TestMd3JmeWrite extends SimpleGame{
    public static void main(String[] args) {
        TestMd3JmeWrite app=new TestMd3JmeWrite();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }
    protected void simpleInitGame() {
        Md3ToJme converter=new Md3ToJme();
        BinaryToXML btx=new BinaryToXML();
        URL laura=null;
        laura=TestMd3JmeWrite.class.getClassLoader().getResource("jmetest/data/model/lara/lara_lower.md3");
        URL tex=TestMd3JmeWrite.class.getClassLoader().getResource("jmetest/data/model/lara/default.bmp");
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            converter.convert(laura.openStream(),BO);
//            StringWriter SW=new StringWriter();
//            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),SW);
//            System.out.println(SW);
            JmeBinaryReader jbr=new JmeBinaryReader();
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            TextureState ts=display.getRenderer().createTextureState();
            ts.setTexture(TextureManager.loadTexture(tex,Texture.MM_LINEAR,Texture.FM_LINEAR));
            ts.setEnabled(true);
            r.setRenderState(ts);
//            r.setLocalScale(.1f);
            rootNode.attachChild(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}