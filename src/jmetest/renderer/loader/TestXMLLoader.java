package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.model.XMLparser.SAXReader;
import com.jme.scene.model.XMLparser.XMLWriter;
import com.jme.scene.Node;

import java.io.*;
import java.net.URL;


/**
 *
 * Started Date: May 30, 2004 <br><br>
 * Test class for XML loading/writting with jME.  I load an XML doc, write the node I loaded, then load that write and
 * finally attach that to the SceneGraph
 *
 * @deprecated soon to go byebye
 * @author Jack Lindamood
 */
public class TestXMLLoader extends SimpleGame{


    public static void main(String[] args){
        new TestXMLLoader().start();
    }

    protected void simpleInitGame() {
        /*
        lightState.get(0).setSpecular(new ColorRGBA(1,1,1,1));

        SAXReader r=new SAXReader();
        URL xmldoc=TestXMLLoader.class.getClassLoader().getResource("jmetest/data/XML docs/newSampleScene.xml");
        if (xmldoc==null){
            System.out.println("Error locating XML document.  Try including jmetest-data.jar in classpath");
            System.exit(0);
        }
        Node mi1=null;
        try {
            mi1=r.loadXML(xmldoc.openStream());
        } catch (IOException e) {
            System.out.println("bad File exception" + e.getCause() + "*" + e.getMessage());
        }

        StringWriter BO=new StringWriter();
        XMLWriter rr=new XMLWriter(BO);
        try {
            rr.writeScene(mi1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(BO);
        r.loadXML(new ByteArrayInputStream(BO.toString().getBytes()));
        Node mi2=r.fetchCopy();
        rootNode.attachChild(mi2);
        */
    }
}
