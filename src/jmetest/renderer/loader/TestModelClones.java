package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.CloneCreator;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.scene.model.XMLparser.Converters.MilkToJme;
import com.jme.scene.model.XMLparser.Converters.MaxToJme;
import com.jme.scene.model.XMLparser.Converters.Md2ToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.util.TextureManager;
import com.jme.image.Texture;

import java.net.URL;
import java.io.*;

/**
 * Started Date: Sep 18, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class TestModelClones extends SimpleGame {
    public static void main(String[] args) {
        FastMath.USE_FAST_TRIG = true;
        TestModelClones app = new TestModelClones();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }




    protected void simpleInitGame() {
        putMilkModels();
        putMaxModels();
        putMd2Models();
    }

    private void putMd2Models() {
        Md2ToJme converter=new Md2ToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();

        URL textu=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.jpg");
        URL freak=TestMd2JmeWrite.class.getClassLoader().getResource("jmetest/data/model/drfreak.md2");
        Node freakmd2=null;
        JmeBinaryReader jbr=new JmeBinaryReader();

        try {
            converter.convert(freak.openStream(),BO);
            freakmd2=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            System.out.println("damn exceptions:" + e.getMessage());
        }

        TextureState ts = display.getRenderer().createTextureState();
        ts.setTexture(
        TextureManager.loadTexture(
            textu,
            Texture.MM_LINEAR,
            Texture.FM_LINEAR));
        freakmd2.setRenderState(ts);

        freakmd2.setLocalScale(.1f);

        CloneCreator cc=new CloneCreator(freakmd2);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("indices");
        cc.addProperty("keyframecontroller");

        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(3*i,4*(i+1),i));
            Md2ToJme.findController((Node) s).setSpeed((i+1.0f)*4);
            Md2ToJme.findController((Node) s).setModelUpdate(false);
            s.setForceView(true);
            rootNode.attachChild(s);
        }
    }

    private void putMilkModels(){
        URL MSFile=TestMilkJmeWrite.class.getClassLoader().getResource(
            "jmetest/data/model/msascii/run.ms3d");
        MilkToJme mtj=new MilkToJme();
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        Node r=null;

        try {
            mtj.convert(MSFile.openStream(),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("texurl",MSFile);
            r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        r.setLocalScale(.1f);

        CloneCreator cc=new CloneCreator(r);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("indices");
        cc.addProperty("jointcontroller");

        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(3*i,0,i));
            MilkToJme.findController((Node) s).setSpeed((i+1.0f)/4);
            MilkToJme.findController((Node) s).setModelUpdate(false);
            s.setForceView(true);
            rootNode.attachChild(s);
        }
    }

    private void putMaxModels() {
        Node r=null;
        try {
            MaxToJme C1=new MaxToJme();
            ByteArrayOutputStream BO=new ByteArrayOutputStream();
            URL maxFile=TestMaxJmeWrite.class.getClassLoader().getResource("jmetest/data/model/Character.3DS");
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
            JmeBinaryReader jbr=new JmeBinaryReader();
            jbr.setProperty("bound","box");
            r=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            r.setLocalScale(.1f);
            if (r.getChild(0).getControllers().size()!=0)
                r.getChild(0).getController(0).setSpeed(20);
            Quaternion temp=new Quaternion();
            temp.fromAngleAxis(FastMath.PI/2,new Vector3f(-1,0,0));
            r.setLocalRotation(temp);
//            rootNode.attachChild(r);
        } catch (IOException e) {
            System.out.println("Damn exceptions:"+e);
            e.printStackTrace();
        }

        CloneCreator cc=new CloneCreator(r);
        cc.addProperty("colors");
        cc.addProperty("texcoords");
        cc.addProperty("vertices");
        cc.addProperty("normals");
        cc.addProperty("indices");
        cc.addProperty("spatialcontroller");
        for (int i=0;i<3;i++){
            Spatial s=cc.createCopy();
            s.setLocalTranslation(new Vector3f(0,0,i*3+1));
            MaxToJme.findController((Node) s).setSpeed(i*4);
            rootNode.attachChild(s);
        }
    }
}
