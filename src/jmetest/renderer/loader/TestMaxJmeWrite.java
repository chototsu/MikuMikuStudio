package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.MaxToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.model.XMLparser.BinaryToXML;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.light.DirectionalLight;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.image.Texture;


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
//            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/sphere.3DS");
            URL maxFile = new File("3dsmodels/cubeColoured.3DS").toURI().toURL();
            globalLoad=C1.convert(maxFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            BinaryToXML btx=new BinaryToXML();
            StringWriter SW=new StringWriter();
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),SW);
            System.out.println(SW);
            globalLoad=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            globalLoad.setLocalScale(.01f);
            DirectionalLight DL=new DirectionalLight();
            DL.setAmbient(new ColorRGBA(.1f,.1f,.1f,.1f));
            DL.setDiffuse(new ColorRGBA(1,1,1,1));
            DL.setSpecular(new ColorRGBA(1,1,1,1));
            DL.setEnabled(true);
            DL.setDirection(new Vector3f(1,1,-1));
            rootNode.attachChild(globalLoad);
            lightState.detachAll();
            lightState.attach(DL);
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e);
            e.printStackTrace();
        }
//        doBox();

//        drawAxis();
    }

    private void doBox(){
        Pyramid b=new Pyramid("Mesh Object",4,4);
        b.setModelBound(new BoundingSphere());
        b.updateModelBound();
        Node o=new Node("box01");
        Node i=new Node("3ds editable object");
        Node j=new Node("3ds scene");
        Node k=new Node("XML Scene");
//        rootNode.attachChild(k);
        k.attachChild(j);
        j.attachChild(i);
        i.attachChild(o);
        o.attachChild(b);
        MaterialState m=display.getRenderer().getMaterialState();
//        TextureState ts=display.getRenderer().getTextureState();
//        ts.setTexture(TextureManager.loadTexture("ASHSEN_2.GIF",Texture.MM_LINEAR,Texture.FM_LINEAR,false));
//        ts.setEnabled(true);

        m.setDiffuse(new ColorRGBA(.8f,.9f,.2f,1));
        m.setAmbient(new ColorRGBA(.89f,.89f,.89f,1));
        m.setEmissive(new ColorRGBA(0,0,0,1));
        m.setSpecular(new ColorRGBA(0f,0f,0f,1));
        m.setShininess(12.8f);
        m.setAlpha(0);

//        b.setTextureCombineMode(TextureState.REPLACE);
        m.setEnabled(true);

        b.setRenderState(m);
        b.setSolidColor(new ColorRGBA(.1f,.1f,.1f,.1f));

        JmeBinaryWriter jbw=new JmeBinaryWriter();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            jbw.writeScene(k,BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            BinaryToXML btx=new BinaryToXML();
            StringWriter SW=new StringWriter();
            btx.sendBinarytoXML(new ByteArrayInputStream(BO.toByteArray()),SW);
            System.out.println(SW);
            Node binaryLoad=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
//            rootNode.attachChild(binaryLoad);
//            rootNode.attachChild(k);
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

