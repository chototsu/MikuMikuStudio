package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Torus;
import com.jme.scene.shape.Sphere;
import com.jme.scene.*;
import com.jme.scene.model.XMLparser.Converters.Md2ToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.math.Vector3f;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jme.system.JmeException;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.renderer.ColorRGBA;

import java.io.*;

import jmetest.renderer.state.TestTextureState;
import jmetest.curve.TestBezierCurve;

import javax.swing.*;

/**
 * Started Date: Jul 5, 2004<br><br>
 * Test the Serializability of jME's scenegraph.
 * 
 * @author Jack Lindamood
 */
public class TestSerial extends SimpleGame{

    ByteArrayOutputStream skybox;
    ByteArrayOutputStream freaky;
    ByteArrayOutputStream curve;
    Node mainNode=new Node("blarg");


    public static void main(String[] args){
        TestSerial app=new TestSerial();
        JOptionPane.showMessageDialog(null,"This will take a while to load.\nPress U to load Dr.Freak, Press O to load skybox, Press I to load curve");
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_freak", false)) {

            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(freaky.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_sky", false)) {
            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(skybox.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("load_curve", false)) {
            try {
                rootNode.detachChild(mainNode);
                mainNode=(Node) new ObjectInputStream(new ByteArrayInputStream(curve.toByteArray())).readObject();
                rootNode.attachChild(mainNode);
                rootNode.updateRenderState();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

    protected void simpleInitGame() {
        System.out.println("Requesting skybox");
        skybox=getSkyBox();
        try {
            System.out.println("requesting drfreak");
            freaky=getFreaky();
            System.out.println("Requesting curve");
            curve=getCurve();
        } catch (IOException e) {
            throw new JmeException("damn");
        }
        KeyBindingManager.getKeyBindingManager().set("load_sky",KeyInput.KEY_O);
        KeyBindingManager.getKeyBindingManager().set("load_freak",KeyInput.KEY_U);
        KeyBindingManager.getKeyBindingManager().set("load_curve",KeyInput.KEY_I);
        rootNode.attachChild(mainNode);
    }

    private ByteArrayOutputStream getSkyBox() {
        Node toReturn=new Node("sky");
        Skybox m_skybox;
        Torus t = new Torus("Torus", 20, 20, 5, 10);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();
        t.setLocalTranslation(new Vector3f(-40, 0, 10));
        t.setVBOVertexEnabled(true);
        t.setVBOTextureEnabled(true);
        t.setVBONormalEnabled(true);
        t.setVBOColorEnabled(true);
        toReturn.attachChild(t);

        Sphere s = new Sphere("Sphere", 20, 20, 25);
        s.setModelBound(new BoundingBox());
        s.updateModelBound();
        s.setLocalTranslation(new Vector3f(40, 0, -10));
        toReturn.attachChild(s);
        s.setVBOVertexEnabled(true);
        s.setVBOTextureEnabled(true);
        s.setVBONormalEnabled(true);
        s.setVBOColorEnabled(true);

        Box b = new Box("box", new Vector3f(-25, 70, -45), 20, 20, 20);
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        b.setVBOVertexEnabled(true);
        b.setVBOTextureEnabled(true);
        b.setVBONormalEnabled(true);
        b.setVBOColorEnabled(true);
        toReturn.attachChild(b);


        // Create a skybox
        // we pick 570 because our clip plane is at 1000 -- see SimpleGame
        // (570^2 + 570^2 + 570^2)^.5 = ~988 so it won't get clipped.
        // If our scene has stuff larger than will fit in the box, we'll
        // need to increase max clip.
        m_skybox = new Skybox("skybox", 570, 570, 570);

        Texture north = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/north.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);
        Texture south = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/south.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);
        Texture east = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/east.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);
        Texture west = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/west.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);
        Texture up = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/top.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);
        Texture down = TextureManager.loadTexture(
            TestSkybox.class.getClassLoader().getResource(
            "jmetest/data/texture/bottom.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true);

        m_skybox.setTexture(Skybox.NORTH, north);
        m_skybox.setTexture(Skybox.WEST, west);
        m_skybox.setTexture(Skybox.SOUTH, south);
        m_skybox.setTexture(Skybox.EAST, east);
        m_skybox.setTexture(Skybox.UP, up);
        m_skybox.setTexture(Skybox.DOWN, down);
        toReturn.attachChild(m_skybox);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(toReturn);
            return BO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ByteArrayOutputStream getFreaky() throws IOException {
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestTextureState.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        Md2ToJme mtj=new Md2ToJme();
        ByteArrayOutputStream BO2=new ByteArrayOutputStream();

        mtj.convert(TestSerial.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2").openStream(),BO2);
        JmeBinaryReader jbr=new JmeBinaryReader();
        Node it=jbr.loadBinaryFormat(new ByteArrayInputStream(BO2.toByteArray()));
        it.getChild(0).getController(0).setSpeed(10);
        it.setRenderState(ts);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(it);
            return BO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ByteArrayOutputStream getCurve(){
        Vector3f up = new Vector3f(0, 1, 0);
        //create control Points
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f( -4, 0, 0);
        points[1] = new Vector3f( -2, 3, 2);
        points[2] = new Vector3f(2, -3, -2);
        points[3] = new Vector3f(4, 0, 0);

        BezierCurve curve = new BezierCurve("Curve", points);
        ColorRGBA[] colors = new ColorRGBA[4];
        colors[0] = new ColorRGBA(0, 1, 0, 1);
        colors[1] = new ColorRGBA(1, 0, 0, 1);
        colors[2] = new ColorRGBA(1, 1, 0, 1);
        colors[3] = new ColorRGBA(0, 0, 1, 1);
        curve.setColors(colors);

        Vector3f min = new Vector3f( -0.1f, -0.1f, -0.1f);
        Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        TriMesh t = new Box("Control 1", min, max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(points[0]);

        TriMesh t2 = new Box("Control 2", min, max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(points[1]);

        TriMesh t3 = new Box("Control 3", min, max);
        t3.setModelBound(new BoundingSphere());
        t3.updateModelBound();

        t3.setLocalTranslation(points[2]);

        TriMesh t4 = new Box("Control 4", min, max);
        t4.setModelBound(new BoundingSphere());
        t4.updateModelBound();

        t4.setLocalTranslation(points[3]);

        TriMesh box = new Box("Controlled Box", min.mult(5), max.mult(5));
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();

        box.setLocalTranslation(points[0]);

        CurveController cc = new CurveController(curve, box);
        box.addController(cc);
        cc.setRepeatType(Controller.RT_CYCLE);
        cc.setUpVector(up);
        cc.setSpeed(0.5f);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
            TestBezierCurve.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MM_LINEAR,
            Texture.FM_LINEAR,
            true));
        box.setRenderState(ts);

        Node it=new Node("blargggg");

        it.setRenderState(buf);
        it.attachChild(t);
        it.attachChild(t2);
        it.attachChild(t3);
        it.attachChild(t4);
        it.attachChild(box);
        it.attachChild(curve);
        it.setLocalScale(10);
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos=new ObjectOutputStream(BO);
            oos.writeObject(it);
            return BO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
