package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.model.XMLparser.Converters.FormatConverter;
import com.jme.scene.model.XMLparser.Converters.ObjToJme;
import com.jme.scene.model.XMLparser.JmeBinaryReader;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.state.RenderState;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.curve.CurveController;
import com.jme.curve.BezierCurve;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyExitAction;
import com.jme.util.MemPool;

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

        // Clear the keyboard commands that can move the camera.
        input.clearKeyboardActions();
        input.clearMouseActions();
        // Insert a keyboard command that can exit the application.
        input.addKeyboardAction("exit",KeyInput.KEY_ESCAPE,new KeyExitAction(this));

        // The path the camera will take.
        Vector3f[]cameraPoints=new Vector3f[]{
            new Vector3f(0,5,20),
            new Vector3f(0,20,90),
            new Vector3f(0,30,200),
            new Vector3f(0,100,300),
            new Vector3f(0,150,400),
        };
        // Create a path for the camera.
        BezierCurve bc=new BezierCurve("camera path",cameraPoints);

        // Create a camera node to move along that path.
        cn=new CameraNode("camera node",cam);

        // Create a curve controller to move the CameraNode along the path
        CurveController cc=new CurveController(bc,cn);

        // Set an up vector for the controller.
        cc.setUpVector(new Vector3f(0,1,0));

        // Cycle the animation.
        cc.setRepeatType(Controller.RT_CYCLE);

        // Slow down the curve controller a bit
        cc.setSpeed(.25f);

        // Add the controller to the node.  Notice I do NOT add the node to rootNode.
        cn.addController(cc);
    }

    protected void simpleUpdate(){
        // Update the node's geometric state, which will update its controll.er
        cn.updateGeometricState(tpf,true);
        // Get the center of root's bound.
        Vector3f objectCenter=rootNode.getWorldBound().getCenter(MemPool.v3a);

        // My direction is the place I want to look minus the location of the camera.
        Vector3f lookAtObject=new Vector3f(objectCenter).subtractLocal(cam.getLocation()).normalizeLocal();

        // Set my camera to look at the object
        cam.setFrame(cam.getLocation(),
                new Vector3f(0,1,0).crossLocal(lookAtObject).normalizeLocal(),
                new Vector3f(1,0,0).crossLocal(lookAtObject).normalizeLocal(),
                lookAtObject);
    }
}