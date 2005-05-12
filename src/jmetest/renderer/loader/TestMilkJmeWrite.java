package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MilkToJme;

import java.net.URL;
import java.io.*;

/**
 * Started Date: Jun 8, 2004
 * This class test the ability to correctly read and write .ms3d scenegraph files.
 * 
 * @author Jack Lindamood
 */
public class TestMilkJmeWrite extends SimpleGame{
    public static void main(String[] args) {
        new TestMilkJmeWrite().start();
    }

    protected void simpleInitGame() {

        MilkToJme converter=new MilkToJme();
        URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.ms3d");
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        try {
            converter.convert(MSFile.openStream(),BO);
        } catch (IOException e) {
            System.out.println("IO problem writting the file!!!");
            System.out.println(e.getMessage());
            System.exit(0);
        }
        JmeBinaryReader jbr=new JmeBinaryReader();
        URL TEXdir=TestMilkJmeWrite.class.getClassLoader().getResource(
                "jmetest/data/model/msascii/");
        jbr.setProperty("texurl",TEXdir);
        Node i=null;
        try {
            i=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            System.out.println("darn exceptions:" + e.getMessage());
        }
        i.setLocalScale(.1f);
        rootNode.attachChild(i);
    }

    private void drawAxis() {
        rootNode.attachChild(new Box("axisX",new Vector3f(5,0,0),5f,.1f,.1f));
        rootNode.attachChild(new Box("axisY",new Vector3f(0,5,0),.1f,5f,.1f));
        rootNode.attachChild(new Box("axisZ",new Vector3f(0,0,5),.1f,.1f,5f));
    }
}