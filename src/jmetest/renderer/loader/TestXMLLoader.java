package jmetest.renderer.loader;

import com.jme.scene.model.SAXReader;
import com.jme.app.SimpleGame;
import com.jme.renderer.ColorRGBA;

import java.io.File;
import java.io.IOException;


/**
 *
 * Test class for XML loading with jME
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
        try {
            r.loadXML(new File("CVS root/data/XML docs/SampleXMLScene.xml").toURL().openStream());
        } catch (IOException e) {
            System.out.println("bad File exception" + e.getCause() + "*" + e.getMessage());
        }
        rootNode.attachChild(r.fetchCopy());

    }
}
