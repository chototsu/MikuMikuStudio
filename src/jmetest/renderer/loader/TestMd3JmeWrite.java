package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.Md3ToJme;
import com.jme.scene.model.XMLparser.Converters.Md2ToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.Node;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.MalformedURLException;

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
        URL laura=null;
        try {
            laura=new File("data/model/lara/lara_upper.md3").toURL();
//            laura=new File("3dsmodels/box.md3").toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            converter.convert(laura.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
//            r.setLocalScale(.1f);
            rootNode.attachChild(r);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}