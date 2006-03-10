package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jmex.model.XMLparser.BinaryToXML;
import com.jmex.model.XMLparser.Converters.MilkToJme;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.JmeBinaryWriter;
import com.jmex.model.XMLparser.XMLtoBinary;
import com.jmex.terrain.TerrainPage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import jmetest.renderer.loader.TestMilkJmeWrite;

/*
 * TestXMLWriteRead.java
 * Created on January 31 2006, 13:19
 */

/**
 * @author Kai Rabien (hevee)
 */
public class TestXMLWriteRead extends SimpleGame{
    
    protected void simpleInitGame() {
        Node scene = new Node("scene");
        rootNode.attachChild(scene);
        
        Node torus = createLODTorus();
        scene.attachChild(torus);
        
        Node run = loadRun_ms3d();
        scene.attachChild(run);
        
        Text text = createText("Scene created. Writing to xml...");
        rootNode.attachChild(text);
        
        //create obb alphastate sphere
        Sphere sphere = new Sphere("sphere", new Vector3f(-10, 0, 0), 16,16,5);
        sphere.setLocalRotation(new Quaternion(new float[]{FastMath.DEG_TO_RAD * -90, 0, 0}));
        AlphaState as = (AlphaState) text.getRenderState(RenderState.RS_ALPHA);
        sphere.setRenderState(as);
        TextureState ts = (TextureState) text.getRenderState(RenderState.RS_TEXTURE);
        sphere.setRenderState(ts);
        sphere.setModelBound(new OrientedBoundingBox());
        sphere.updateModelBound();
        scene.attachChild(sphere);
        
        //create TerrainBlock
        int[] heightmap = new int[]{0,0,0,0,0,0,0,0,0,
                                    0,0,1,0,0,0,0,0,0,
                                    0,1,1,0,1,1,0,1,0,
                                    0,0,1,0,1,1,1,1,0,
                                    0,0,1,1,2,2,1,1,0,
                                    0,1,1,1,2,3,2,1,0,
                                    0,0,0,1,2,2,1,0,0,
                                    0,0,0,1,1,1,1,0,0,
                                    0,0,0,0,0,0,0,0,0};
        TerrainPage tp = new TerrainPage("terrainBlock", 3, 9, new Vector3f(10,3,10), heightmap, false);
        tp.setLocalTranslation(new Vector3f(0,-10,0));
        scene.attachChild(tp);
        
        File outfile = new File("out.xml");
        outputXML(scene, outfile);
        
        scene.detachAllChildren();
        
        Spatial s = loadFromXML(outfile);
        scene.attachChild(s);
        printtree(scene, "");
        text.getText().replace(0, text.getText().length(), "Scene successfully loaded from xml. Test complete.");
    }
    
    private void printtree(Node n, String indent){
        for(Object o : n.getChildren()){
            Spatial s = (Spatial)o;
            System.out.println(indent+s.getClass().getName()+" : "+s.getName());
            if(s instanceof Node){
                printtree((Node) s, indent+"  ");
            }
        }
    }
    
    private Node createLODTorus(){
        Node t = new Node("torus");
        PQTorus pqt = new PQTorus("pqtorus", 1, 3, 2, 1, 64, 8);
        t.attachChild(pqt);
        t = getClodNodeFromParent(t);
        t.setLocalTranslation(new Vector3f(10,0,0));
        return t;
    }
    
    public static void main(String[] args){
        TestXMLWriteRead app = new TestXMLWriteRead();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
    
    private Node getClodNodeFromParent(Node meshParent) {
        Node clodNode=new Node("Clod node");
        for (int i=0;i<meshParent.getQuantity();i++){
            AreaClodMesh acm=new AreaClodMesh("part"+i,(TriMesh) meshParent.getChild(i),null);
            acm.setModelBound(new BoundingSphere());
            acm.updateModelBound();
            acm.setTrisPerPixel(.5f);
            acm.setDistanceTolerance(2);
            clodNode.attachChild(acm);
        }
        return clodNode;
    }
    
    public static Spatial loadFromXML(File toLoad){
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        XMLtoBinary converter = new XMLtoBinary();
        try {
            converter.sendXMLtoBinary(new FileInputStream(toLoad),BO);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        JmeBinaryReader jbr=new JmeBinaryReader();
        URL TEXdir=TestMilkJmeWrite.class.getClassLoader().getResource("jmetest/data/model/msascii/");
        jbr.setProperty("texurl",TEXdir);
        try {
            return jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void outputXML(Spatial s, File writeTo){
        JmeBinaryWriter jbw = new JmeBinaryWriter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            jbw.writeScene(s, out);
            BinaryToXML b2x = new BinaryToXML();
            PrintWriter w;
            try {
                w = new PrintWriter(writeTo);
                b2x.sendBinarytoXML(new ByteArrayInputStream(out.toByteArray()), w);
                jbw.writeScene(s, new FileOutputStream("binary.jme"));
                w.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private Text createText(String textString){
        LightState ls = display.getRenderer().createLightState();
        ls.setEnabled(false);
        Text text = Text.createDefaultTextLabel("text", textString);
        text.setLocalTranslation(new Vector3f(1,60,0));
        text.setTextColor(ColorRGBA.green);
        text.setRenderState(ls);
        text.setLightCombineMode(ls.REPLACE);
        return text;
    }
    
    public static Node loadRun_ms3d(){
        MilkToJme converter=new MilkToJme();
        URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource("jmetest/data/model/msascii/run.ms3d");
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            converter.convert(MSFile.openStream(),BO);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        JmeBinaryReader jbr=new JmeBinaryReader();
        URL TEXdir=TestMilkJmeWrite.class.getClassLoader().getResource("jmetest/data/model/msascii/");
        jbr.setProperty("texurl",TEXdir);
        Node i=null;
        try {
            i=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        i.setLocalScale(.1f);
        return i;
    }
}