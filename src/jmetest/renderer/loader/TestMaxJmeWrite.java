package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.MaxToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;


import java.io.*;
import java.net.URL;


/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * This class test the ability to save adn write .3ds files
 * 
 * @author Jack Lindamood
 */
public class TestMaxJmeWrite extends SimpleGame{
    public static void main(String[] args) {
        TestMaxJmeWrite app=new TestMaxJmeWrite();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        MaxToJme C1=new MaxToJme();
        try {
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/sphere.3DS");
//            URL maxFile = new File("3dsmodels/europe.3ds").toURI().toURL();
            C1.convert(maxFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            BinaryToXML btx=new BinaryToXML();
            StringWriter SW=new StringWriter();
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),SW);
            System.out.println(SW);
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            rootNode.attachChild(r);
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e);
            e.printStackTrace();
        }
        drawAxis();
    }
    private void drawAxis() {
        Box Xaxis=new Box("axisX",new Vector3f(5,0,0),5f,.1f,.1f);
        Xaxis.setModelBound(new BoundingSphere());
        Xaxis.updateModelBound();
        Xaxis.setSolidColor(ColorRGBA.blue);
        Box Yaxis=new Box("axisY",new Vector3f(0,5,0),.1f,5f,.1f);
        Yaxis.setModelBound(new BoundingSphere());
        Yaxis.updateModelBound();
        Yaxis.setSolidColor(ColorRGBA.red);
        Box Zaxis=new Box("axisZ",new Vector3f(0,0,5),.1f,.1f,5f);
        Zaxis.setSolidColor(ColorRGBA.green);
        Zaxis.setModelBound(new BoundingSphere());
        Zaxis.updateModelBound();
        rootNode.attachChild(Xaxis);
        rootNode.attachChild(Yaxis);
        rootNode.attachChild(Zaxis);
    }
}
