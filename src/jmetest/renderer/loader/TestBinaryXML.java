package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.*;
import com.jme.scene.Node;

import java.net.URL;
import java.io.*;

/**
 * Started Date: Jun 23, 2004<br><br>
 *
 * This class test XMLtoBinary, JmeBinaryReader, and JmeBinaryWriter.  
 *
 * @author Jack Lindamood
 */
public class TestBinaryXML extends SimpleGame{
    public static void main(String[] args) {
        TestBinaryXML app=new TestBinaryXML();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        try {
            doSimple();
        } catch (IOException e) {
            System.out.println("Send error: " + e.getMessage());
            System.exit(0);
        }
    }

    private void doSimple() throws IOException{

        // Send XML file to jME binary
        XMLtoBinary c1=new XMLtoBinary();
        URL xmldoc=TestBinaryXML.class.getClassLoader().getResource("jmetest/data/XML documents/newSampleScene.xml");
        ByteArrayOutputStream BO1=new ByteArrayOutputStream();
        c1.sendXMLtoBinary(xmldoc.openStream(),BO1);

        // Send jME binary to XML
        BinaryToXML btx=new BinaryToXML();
        btx.sendBinarytoXML(new ByteArrayInputStream(BO1.toByteArray()),new PrintWriter(System.out));

        // Send jME binary to a jME Scene Graph
        JmeBinaryReader jbr=new JmeBinaryReader();
        jbr.setProperty("texclasspath","jmetest/data/images/");
        Node fileScene=jbr.loadBinaryFormat(new ByteArrayInputStream(BO1.toByteArray()));

        // Send a jME SceneGraph to jME Binary
        JmeBinaryWriter jbw=new JmeBinaryWriter();
        ByteArrayOutputStream BO2=new ByteArrayOutputStream();
        long time=System.currentTimeMillis();
        jbw.writeScene(fileScene,BO2);
        System.out.println("Finished Writting time:" + (System.currentTimeMillis()-time));

        // Send the new jME binary to a jME SceneGraph and attach it.
        fileScene=jbr.loadBinaryFormat(new ByteArrayInputStream(BO2.toByteArray()));
        rootNode.attachChild(fileScene);
    }
}
