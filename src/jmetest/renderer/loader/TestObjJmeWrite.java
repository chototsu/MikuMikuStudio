package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.ObjToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.Node;

import java.net.URL;
import java.io.*;

/**
 * Started Date: Jul 17, 2004<br><br>
 *
 * Test the ability to read and write obj files.
 * 
 * @author Jack Lindamood
 */
public class TestObjJmeWrite extends SimpleGame{
    public static void main(String[] args) {
        TestObjJmeWrite app=new TestObjJmeWrite();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }
    protected void simpleInitGame() {
        ObjToJme converter=new ObjToJme();
        try {
//            URL objFile=new File("obj/ninja.obj").toURL();
            URL objFile=TestObjJmeWrite.class.getClassLoader().getResource("jmetest/data/model/ninja.obj");
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            converter.setProperty("sillycolors","true");
            converter.convert(objFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            BinaryToXML btx=new BinaryToXML();
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),new PrintWriter(System.out));
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            rootNode.attachChild(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lightState.detachAll();
    }
}
