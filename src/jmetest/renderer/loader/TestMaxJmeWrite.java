package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.model.XMLparser.Converters.MaxToJme;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.FastMath;
import com.jme.bounding.BoundingBox;
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

    Node globalLoad=null;

    protected void simpleInitGame() {

        MaxToJme C1=new MaxToJme();

        try {
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
//            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/tpot.3ds");
//            URL maxFile=new File("3dsmodels/tpot.3ds").toURL();
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
//            jbr.setProperty("texulr",new File("3dsmodels").toURL());
            BinaryToXML btx=new BinaryToXML();
            StringWriter SW=new StringWriter();
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),SW);
            System.out.println(SW);

            jbr.setProperty("bound","box");
            Node r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            rootNode.setLocalRotation(temp);
            rootNode.attachChild(r);

        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
            e.printStackTrace();
        }

        drawAxis();
        TextureState ts=display.getRenderer().getTextureState();
        ts.setEnabled(true);
        rootNode.setRenderState(ts);

    }

    private void drawAxis() {
        Box Xaxis=new Box("axisX",new Vector3f(5,0,0),5f,.1f,.1f);
        Xaxis.setModelBound(new BoundingBox());
        Xaxis.updateModelBound();
        Xaxis.setSolidColor(ColorRGBA.red);
        MaterialState red=display.getRenderer().getMaterialState();
        red.setEmissive(ColorRGBA.red);
        red.setEnabled(true);
        Xaxis.setRenderState(red);

        Box Yaxis=new Box("axisY",new Vector3f(0,5,0),.1f,5f,.1f);
        Yaxis.setModelBound(new BoundingBox());
        Yaxis.updateModelBound();
        Yaxis.setSolidColor(ColorRGBA.green);
        MaterialState green=display.getRenderer().getMaterialState();
        green.setEmissive(ColorRGBA.green);
        green.setEnabled(true);
        Yaxis.setRenderState(green);

        Box Zaxis=new Box("axisZ",new Vector3f(0,0,5),.1f,.1f,5f);
        Zaxis.setSolidColor(ColorRGBA.blue);
        Zaxis.setModelBound(new BoundingBox());
        Zaxis.updateModelBound();
        MaterialState blue=display.getRenderer().getMaterialState();
        blue.setEmissive(ColorRGBA.blue);
        blue.setEnabled(true);
        Zaxis.setRenderState(blue);

        rootNode.attachChild(Xaxis);
        rootNode.attachChild(Yaxis);
        rootNode.attachChild(Zaxis);

    }
}