package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.ObjToJme;
import com.jme.scene.model.XMLparser.Converters.FormatConverter;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.Node;
import com.jme.util.LoggingSystem;

import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

/**
 * Started Date: Jul 22, 2004<br><br>
 *
 * Demonstrates loading formats.
 * 
 * @author Jack Lindamood
 */
public class HelloModelLoading extends SimpleGame {
    public static void main(String[] args) {
        HelloModelLoading app = new HelloModelLoading();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        // Turn the logger off so we can see the XML later on
        LoggingSystem.getLogger().setLevel(Level.OFF);
        app.start();
    }

    protected void simpleInitGame() {
        // Point to a URL of my model
        URL model=HelloModelLoading.class.getClassLoader().getResource("jmetest/data/model/maggie.obj");

        // Create something to convert .obj format to .jme
        FormatConverter converter=new ObjToJme();
        // Point the converter to where it will find the .mtl file from
        converter.setProperty("mtllib",model);

        // This byte array will hold my .jme file
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        // This will read the .jme format and convert it into a scene graph
        JmeBinaryReader jbr=new JmeBinaryReader();
        // Use this to visualize the .obj file in XML
        BinaryToXML btx=new BinaryToXML();
        try {
            // Use the format converter to convert .obj to .jme
            converter.convert(model.openStream(), BO);
            // Send the .jme binary to System.out as XML
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),new OutputStreamWriter(System.out));

            // Tell the binary reader to use bounding boxes instead of bounding spheres
            jbr.setProperty("bound","box");
            
            // Load the binary .jme format into a scene graph
            Node maggie=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            // shrink this baby down some
            maggie.setLocalScale(.1f);
            // Put her on the scene graph
            rootNode.attachChild(maggie);
        } catch (IOException e) {   // Just in case anything happens
            System.out.println("Damn exceptions!" + e);
            e.printStackTrace();
            System.exit(0);
        }
    }
}
