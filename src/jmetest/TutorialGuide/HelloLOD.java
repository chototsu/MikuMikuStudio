package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.FormatConverter;
import com.jme.scene.model.XMLparser.Converters.ObjToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.curve.CurveController;
import com.jme.curve.BezierCurve;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyExitAction;

import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * Started Date: Aug 16, 2004<br><br>
 *
 * This program teaches Complex Level of Detail mesh objects.  To use this program, move
 * the camera backwards and watch the model disappear.
 * 
 * @author Jack Lindamood
 */
public class HelloLOD extends SimpleGame {

    CameraNode cn;

    public static void main(String[] args) {
        HelloLOD app = new HelloLOD();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
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

        // Use an exact BoundingSphere bounds
        BoundingSphere.useExactBounds=true;

        Node meshParent=null;
        try {
            // Use the format converter to convert .obj to .jme
            converter.convert(model.openStream(), BO);

            // Load the binary .jme format into a scene graph
            Node maggie=jbr.loadBinaryFormat(new ByteArrayInputStream(BO.toByteArray()));
            meshParent=(Node) maggie.getChild(0);

        } catch (IOException e) {   // Just in case anything happens
            System.out.println("Damn exceptions!" + e);
            e.printStackTrace();
            System.exit(0);
        }

        // Create a node to hold my cLOD mesh objects
        Node clodNode=new Node("Clod node");
        // For each mesh in maggie
        for (int i=0;i<meshParent.getQuantity();i++){
            // Create an AreaClodMesh for that mesh.  Let it compute records automatically
            AreaClodMesh acm=new AreaClodMesh("part"+i,(TriMesh) meshParent.getChild(i),null);
            acm.setModelBound(new BoundingSphere());
            acm.updateModelBound();

            // Allow 1/2 of a triangle in every pixel on the screen in the bounds.
            acm.setTrisPerPixel(.5f);

            // Force a move of 2 units before updating the mesh geometry
            acm.setDistanceTolerance(2);

            // Give the clodMesh node the material state that the original had.
            acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]);

            // Attach clod node.
            clodNode.attachChild(acm);
        }
        // Attach the clod mesh at the origin.
        clodNode.setLocalScale(.1f);
        rootNode.attachChild(clodNode);

        // Attach the original at -15,0,0
        meshParent.setLocalScale(.1f);
        meshParent.setLocalTranslation(new Vector3f(-15,0,0));
        rootNode.attachChild(meshParent);
        input.clearKeyboardActions();
        input.clearMouseActions();
        input.addKeyboardAction("exit",KeyInput.KEY_ESCAPE,new KeyExitAction(this));

        Vector3f[]cameraPoints=new Vector3f[]{
            new Vector3f(0,5,20),
            new Vector3f(0,20,90),
            new Vector3f(0,30,200),
            new Vector3f(0,150,300)
        };
        BezierCurve bc=new BezierCurve("camera path",cameraPoints);
        cn=new CameraNode("camera node",display.getRenderer().getCamera());


        CurveController cc=new CurveController(bc,cn);
        cc.setActive(true);
        cc.setUpVector(new Vector3f(0,1,0));
        cc.setAutoRotation(false);
        cc.setRepeatType(Controller.RT_CYCLE);
        cc.setSpeed(.5f);

        cn.addController(cc);

        drawAxis();
//        rootNode.setForceView(true);
    }

    private void drawAxis() {
        Box xAxis=new Box("x axis",new Vector3f(10,0,0),10,.5f,.5f);
        MaterialState green=display.getRenderer().createMaterialState();
        green.setEmissive(ColorRGBA.green);
        xAxis.setRenderState(green);

        Box yAxis=new Box("y axis",new Vector3f(0,10,0),.5f,10,.5f);
        MaterialState blue=display.getRenderer().createMaterialState();
        blue.setEmissive(ColorRGBA.blue);
        yAxis.setRenderState(blue);

        Box zAxis=new Box("z axis",new Vector3f(0,0,10),.5f,.5f,10);
        MaterialState red=display.getRenderer().createMaterialState();
        red.setEmissive(ColorRGBA.red);
        zAxis.setRenderState(red);


        rootNode.attachChild(xAxis);
        rootNode.attachChild(yAxis);
        rootNode.attachChild(zAxis);
    }

    protected void simpleUpdate(){
        cn.updateGeometricState(tpf,true);
        Camera c=display.getRenderer().getCamera();
        Vector3f objectCenter=((BoundingSphere)rootNode.getWorldBound()).center;
        Vector3f lookAtObject=new Vector3f(objectCenter).subtractLocal(c.getLocation()).normalizeLocal();

//        c.setFrame(c.getLocation(),
//                lookAtObject.cross(new Vector3f(-1,0,0)).normalizeLocal(),
//                lookAtObject.cross(new Vector3f(0,1,0)).normalizeLocal(),
//                lookAtObject);
        c.setFrame(c.getLocation(),
                new Vector3f(-1,0,0).crossLocal(lookAtObject).normalizeLocal(),
                new Vector3f(0,1,0).crossLocal(lookAtObject).normalizeLocal(),
                lookAtObject);

        c.update();
    }
}