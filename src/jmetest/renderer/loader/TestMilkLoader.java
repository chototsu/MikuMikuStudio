package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.scene.Node;
import com.jme.math.Vector3f;
import com.jme.scene.model.ms3d.MilkLoader;
import com.jme.scene.model.ms3d.MilkAnimation;
import com.jme.scene.model.Loader;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.animation.DeformationJointController;

import java.net.URL;


/**
 * Class used to test MilkLoader
 *
 * @author Jack Lindamood
 */
public class TestMilkLoader extends SimpleGame{
    public static void main(String[] args) {
        TestMilkLoader app=new TestMilkLoader();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    // The new loader
    protected void simpleInitGame1() {
        //  Idealy this could be replaced by
        // Loader mi=new AutoCadLoader(); ect
        Loader mi=new MilkLoader();
        mi.setLoadFlag(Loader.LOAD_CONTROLLERS);
        mi.setLoadFlag(Loader.PRECOMPUTE_BOUNDS);
        URL MSFile=TestMilkLoader.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.ms3d");
        mi.setBase(TestMilkLoader.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/"));
        Node mi1=mi.load(MSFile);
        mi1.setLocalScale(.1f);
        mi1.getController(0).setSpeed(5f);
        ((MilkAnimation) mi1.getController(0)).setSkipRate(.01f);
        rootNode.attachChild(mi1);

        Node mi2=mi.fetchCopy();
        mi2.setLocalTranslation(new Vector3f(3,3,-200));
        ((MilkAnimation) mi2.getController(0)).setSkipRate(.05f);
        mi2.setLocalScale(.5f);
        rootNode.attachChild(mi2);
    }

    // The old loader
    protected void simpleInitGame2() {

        MilkshapeASCIIModel mi1=new MilkshapeASCIIModel("runguy");
        mi1.load("models/run.txt","models");
       ( (DeformationJointController) mi1.getAnimationController()).
            setUpdateModelBounds(true);
        mi1.setLocalScale(.1f);
        rootNode.attachChild(mi1);

//        MilkshapeASCIIModel mi2=new MilkshapeASCIIModel("ninja");
//        mi2.load("models/run.txt","models");
//       ( (DeformationJointController) mi2.getAnimationController()).
//            setUpdateModelBounds(true);
//        mi2.setLocalTranslation(new Vector3f(3,3,3));
//        mi2.setLocalScale(.5f);
//        rootNode.attachChild(mi2);
    }

    protected void simpleInitGame() {
        long time=System.currentTimeMillis();
        simpleInitGame1();
        rootNode.attachChild(new Box("axisX",new Vector3f(5,0,0),5f,.1f,.1f));
        rootNode.attachChild(new Box("axisY",new Vector3f(0,5,0),.1f,5f,.1f));
        rootNode.attachChild(new Box("axisZ",new Vector3f(0,0,5),.1f,.1f,5f));
        System.out.println("TIME:" + (System.currentTimeMillis()-time));
    }
}