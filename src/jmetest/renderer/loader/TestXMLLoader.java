package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.model.XMLparser.SAXReader;
import com.jme.scene.model.XMLparser.XMLWriter;
import com.jme.scene.Node;

import java.io.*;


/**
 *
 * Test class for XML loading/writting with jME
 * Started Date: May 30, 2004
 * @author Jack Lindamood
 */
public class TestXMLLoader extends SimpleGame{


    public static void main(String[] args){
        new TestXMLLoader().start();
    }

    protected void simpleInitGame() {
        lightState.get(0).setSpecular(new ColorRGBA(1,1,1,1));

        SAXReader r=new SAXReader();
        Node mi1=null;
        try {
            mi1=r.loadXML(new File("data/XML docs/newSampleScene.xml").toURL().openStream());
        } catch (IOException e) {
            System.out.println("bad File exception" + e.getCause() + "*" + e.getMessage());
        }
//        rootNode.attachChild(mi1);

        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        XMLWriter rr=new XMLWriter(BO);
        try {
            rr.writeScene(mi1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(BO);
        r.loadXML(new ByteArrayInputStream(BO.toByteArray()));
        Node mi2=r.fetchCopy();
        rootNode.attachChild(mi2);

    }
}
