package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.FastMath;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;



/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * This class test the ability to use OBB.
 *
 * @author Jack Lindamood
 */
public class TestOrientedBox extends SimpleGame{
    public static void main(String[] args) {

        TestOrientedBox app=new TestOrientedBox();
        app.setDialogBehaviour(SimpleGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
        app.start();
    }

    Node AABBnode=new Node("AABBNode");
    Node OBBnode=new Node("OBBNode");

    protected void simpleInitGame() {
        {
            Cylinder c=new Cylinder("cylinder",20,20,1,10);
            c.setModelBound(new BoundingBox());
            c.updateModelBound();
            AABBnode.attachChild(c);
        }
        {
            Cylinder c2=new Cylinder("cylinder2",20,20,1,10);
            c2.setLocalTranslation(new Vector3f(5,10,0));
            c2.setModelBound(new OrientedBoundingBox());
            c2.updateModelBound();
            OBBnode.attachChild(c2);
        }
        AABBnode.updateGeometricState(0,true);
        AABBnode.updateRenderState();
        OBBnode.updateGeometricState(0,true);
        OBBnode.updateRenderState();

        smallrotation=new Quaternion();
        smallrotation.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(0,1,0));

        Quaternion upright=new Quaternion();
        upright.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(1,0,0));
        OBBnode.setLocalRotation(new Quaternion(upright));
        AABBnode.setLocalRotation(new Quaternion(upright));
    }
    Quaternion smallrotation;
    protected void simpleUpdate(){
        Quaternion q=new Quaternion();
        q.slerp(smallrotation,tpf);
        AABBnode.getLocalRotation().multLocal(
                q);
        OBBnode.getLocalRotation().multLocal(
                q);


        AABBnode.updateGeometricState(tpf,true);
        OBBnode.updateGeometricState(tpf,true);

    }
    protected void simpleRender(){
        display.getRenderer().draw(AABBnode);
        display.getRenderer().draw(OBBnode);
        if (showBounds){
            display.getRenderer().drawBounds(AABBnode);
            display.getRenderer().drawBounds(OBBnode);
        }
    }
}