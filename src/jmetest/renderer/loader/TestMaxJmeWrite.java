package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.scene.model.XMLparser.Converters.MaxToJme;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.light.DirectionalLight;


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

    Node globalLoad=null;

    protected void simpleInitGame() {
        MaxToJme C1=new MaxToJme();

        try {
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/sphere.3DS");
//            URL maxFile = new File("3dsmodels/tank.3ds").toURI().toURL();
            C1.convert(maxFile.openStream(),BO);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
        }
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

