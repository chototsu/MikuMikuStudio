package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.ms3d.MilkLoader;
import com.jme.scene.model.XMLparser.XMLWriter;
import com.jme.scene.model.XMLparser.SAXReader;
import com.jme.scene.Node;

import java.net.URL;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

/**
 * Started Date: Jun 5, 2004
 *
 * Class test the XMLWriter and SAXReader classes
 *
 * @author Jack Lindamood
 */
public class TestXMLWriter extends SimpleGame{
    public static void main(String[] args) {
        new TestXMLWriter().start();
    }
    protected void simpleInitGame() {
        MilkLoader mi=new MilkLoader();
        URL MSFile=TestMilkLoader.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.ms3d");
        mi.setBase(TestMilkLoader.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/"));
        Node mi1=mi.load(MSFile);
        mi1.setLocalScale(.1f);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        XMLWriter r=new XMLWriter(BO);
        try {
            r.writeScene(mi1);
        } catch (IOException e) {
            System.out.println("Error writting");
        }
        System.out.println(BO);
        SAXReader sr=new SAXReader();
        Node qq=sr.loadXML(new ByteArrayInputStream(BO.toByteArray()));
        rootNode.attachChild(qq);
        System.out.println(BO);
    }
}
