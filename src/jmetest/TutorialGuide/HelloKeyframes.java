package jmetest.TutorialGuide;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.TriMesh;
import com.jme.scene.Controller;
import com.jme.scene.state.MaterialState;
import com.jme.renderer.ColorRGBA;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;
import com.jme.animation.KeyframeController;

/**
 * Started Date: Jul 23, 2004<br><br>
 *
 * Demonstrates making your own keyframe animations.
 *
 * @author Jack Lindamood
 */
public class HelloKeyframes extends SimpleGame {
    public static void main(String[] args) {
        HelloKeyframes app = new HelloKeyframes();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        // The box we start off looking like
        TriMesh startBox=new Sphere("begining box",15,15,3);
        // Null colors,normals,textures because they aren't being updated
        startBox.setColors(null);
        startBox.setNormals(null);
        startBox.setTextures(null);

        // The middle animation sphere
        TriMesh middleSphere=new Sphere("middleSphere sphere",15,15,3);
        middleSphere.setColors(null);
        middleSphere.setNormals(null);
        middleSphere.setTextures(null);

        // The end animation pyramid
        TriMesh endPyramid=new Sphere("End sphere",15,15,3);
        endPyramid.setColors(null);
        endPyramid.setNormals(null);
        endPyramid.setTextures(null);

        Vector3f[] boxVerts=startBox.getVertices();
        Vector3f[] sphereVerts=middleSphere.getVertices();
        Vector3f[] pyramidVerts=endPyramid.getVertices();

        for (int i=0;i<sphereVerts.length;i++){
            Vector3f boxPos=boxVerts[i];
            Vector3f spherePos=sphereVerts[i];
            Vector3f pyramidPos=pyramidVerts[i];

            // The box is the sign of the sphere coords * 5
            boxPos.x =FastMath.sign(spherePos.x)*4;
            boxPos.y =FastMath.sign(spherePos.y)*4;
            boxPos.z =FastMath.sign(spherePos.z)*4;

            if (boxPos.y<0){    // The bottom of the pyramid
                pyramidPos.x=boxPos.x;
                pyramidPos.y=-4;
                pyramidPos.z=boxPos.z;
            }
            else    // The top of the pyramid
                pyramidPos.set(0,4,0);
        }

        // The object that will actually be rendered
        TriMesh renderedObject=new Sphere("Rendered Object",15,15,3);
        renderedObject.setLocalScale(2);

        // Create my KeyframeController
        KeyframeController kc=new KeyframeController();
        // Assign the object I'll be changing
        kc.setMorphingMesh(renderedObject);
        // Assign for a time, what my renderedObject will look like
        kc.setKeyframe(0,startBox);
        kc.setKeyframe(.5f,startBox);
        kc.setKeyframe(2.75f,middleSphere);
        kc.setKeyframe(3.25f,middleSphere);
        kc.setKeyframe(5.5f,endPyramid);
        kc.setKeyframe(6,endPyramid);
        kc.setRepeatType(Controller.RT_CYCLE);

        // Give it a red material with a green tint
        MaterialState redgreen=display.getRenderer().getMaterialState();
        redgreen.setDiffuse(ColorRGBA.red);
        redgreen.setSpecular(ColorRGBA.green);
        // Make it very affected by the Specular color.
        redgreen.setShininess(10f);
        redgreen.setEnabled(true);
        renderedObject.setRenderState(redgreen);

        // Add the controller to my object
        renderedObject.addController(kc);
        rootNode.attachChild(renderedObject);
    }
}