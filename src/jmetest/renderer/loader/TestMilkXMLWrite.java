package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.MilkToXML;
import com.jme.scene.model.XMLparser.SAXReader;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;

import java.net.URL;
import java.io.*;

/**
 * Started Date: Jun 8, 2004
 * This class test the ability to correctly read and write .ms3d scenegraph files.
 * 
 * @author Jack Lindamood
 */
public class TestMilkXMLWrite extends SimpleGame{
    public static void main(String[] args) {
        new TestMilkXMLWrite().start();
    }

    protected void simpleInitGame() {

        MilkToXML converter=new MilkToXML();
        URL MSFile=TestMilkXMLWrite.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.ms3d");
        ByteArrayOutputStream blah=new ByteArrayOutputStream();

        try {
            converter.writeFiletoStream(MSFile,blah);
        } catch (IOException e) {
            System.out.println("IO problem writting the file!!!");
            System.out.println(e.getMessage());
            System.exit(0);
        }
        System.out.println(blah);
        SAXReader toScreen=new SAXReader();
        URL TEXdir=TestMilkXMLWrite.class.getClassLoader().getResource(
                "jmetest/data/model/msascii/");
        toScreen.setProperty("texurl",TEXdir);
        Node mi=toScreen.loadXML(new ByteArrayInputStream(blah.toByteArray()));
        mi.setLocalScale(.1f);
        rootNode.attachChild(mi);
//        drawAxis();
    }

    private void drawAxis() {
        rootNode.attachChild(new Box("axisX",new Vector3f(5,0,0),5f,.1f,.1f));
        rootNode.attachChild(new Box("axisY",new Vector3f(0,5,0),.1f,5f,.1f));
        rootNode.attachChild(new Box("axisZ",new Vector3f(0,0,5),.1f,.1f,5f));
    }
}