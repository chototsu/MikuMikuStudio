package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.ObjToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
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
            URL objFile=TestObjJmeWrite.class.getClassLoader().getResource("jmetest/data/model/maggie.obj");
            converter.setProperty("mtllib",objFile);
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            System.out.println("Starting to convert .obj to .jme");
            converter.convert(objFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("texurl",new File(".").toURL());
            System.out.println("Done converting, now watch how fast it loads!");
            long time=System.currentTimeMillis();
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            System.out.println("Finished loading time is "+(System.currentTimeMillis()-time));
            r.setLocalScale(.1f);
            rootNode.attachChild(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
