package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;

/**
 * Started Date: Jul 12, 2004<br><br>
 *
 * Test Spatial Transformer animation.
 *
 * @author Jack Lindamood
 */
public class TestSpatialTransformer extends SimpleGame{
    public static void main(String[] args) {
        new TestSpatialTransformer().start();
    }
    protected void simpleInitGame() {
        Box b=new Box("box",new Vector3f(-1,-1,-1),new Vector3f(1,1,1));
        SpatialTransformer st=new SpatialTransformer(1);
        st.setObject(b,0,-1);
        Quaternion x0=new Quaternion();
        x0.fromAngleAxis(0,new Vector3f(1,0,0));
        Quaternion x90=new Quaternion();
        x90.fromAngleAxis((float) (Math.PI/2),new Vector3f(1,0,0));
        Quaternion x180=new Quaternion();
        x180.fromAngleAxis((float) (Math.PI),new Vector3f(1,0,0));
        Quaternion x270=new Quaternion();
        x270.fromAngleAxis((float) (3*Math.PI/2),new Vector3f(1,0,0));
        st.setScale(0,0,new Vector3f(1,1,1));
        st.setScale(0,1f,new Vector3f(2,2,2));
        st.setScale(0,2,new Vector3f(1,1,1));

        st.setRotation(0,0,x0);
        st.setRotation(0,1,x90);
        st.setRotation(0,2,x180);
        st.setRotation(0,3,x270);
        st.interpolateMissing();
        b.addController(st);
        b.setModelBound(new BoundingBox());
        rootNode.attachChild(b);
    }
}