package jmetest.intersection;

import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;
import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;

import java.util.ArrayList;

/**
 * Started Date: Sep 6, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class TestOBBTree extends SimpleGame {
    public static void main(String[] args) {
        TestOBBTree app = new TestOBBTree();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
    TriMesh s,r;
    protected void simpleInitGame() {
        s=new Sphere("sphere",10,10,1);
        s.updateCollisionTree();
        s.setModelBound(new BoundingBox());
        s.updateModelBound();

//        r=new Sphere("Sphere",10,10,1);
        r=new PQTorus("tort",5,4,2f,.5f,128,16);
        r.updateCollisionTree();
        r.setLocalTranslation(new Vector3f(0,0,0));
        r.setModelBound(new BoundingBox());
        r.updateModelBound();

        SpatialTransformer st=new SpatialTransformer(1);
        st.setObject(r,0,-1);
//        st.setSpeed(.2f);
        st.setPosition(0,0,new Vector3f(10,10,0));
        st.setPosition(0,4,new Vector3f(-10,-10,0));
        st.setPosition(0,8,new Vector3f(10,10,0));
        st.interpolateMissing();
        r.addController(st);

        rootNode.attachChild(r);
        rootNode.attachChild(s);

        lightState.detachAll();
    }

    int count=0;
    protected void simpleUpdate(){
        count++;
        if (count!=10) return;
        count=0;
        ArrayList a=new ArrayList();
        ArrayList b=new ArrayList();
        s.findIntersection(r,a,b);
        {
        ColorRGBA[] colors=new ColorRGBA[s.getVertices().length];
        for (int i=0;i<colors.length;i++){
            if (i%3==0)
                colors[i]=ColorRGBA.white;
            else if (i%3==1)
                colors[i]=ColorRGBA.green;
            else
                colors[i]=ColorRGBA.gray;
        }
        if (a.size()!=0){
            int[] indices=s.getIndices();
            for (int i=0;i<a.size();i++){
                int triIndex=((Integer)a.get(i)).intValue();
                colors[indices[triIndex*3+0]]=ColorRGBA.red;
                colors[indices[triIndex*3+1]]=ColorRGBA.red;
                colors[indices[triIndex*3+2]]=ColorRGBA.red;
            }
        }
        s.setColors(colors);
        }
        {
        ColorRGBA[] colors2=new ColorRGBA[r.getVertices().length];
        for (int i=0;i<colors2.length;i++){
            if (i%3==0)
                colors2[i]=ColorRGBA.white;
            else if (i%3==1)
                colors2[i]=ColorRGBA.green;
            else
                colors2[i]=ColorRGBA.gray;
        }
        if (b.size()!=0){
            int[] indices=r.getIndices();
            for (int i=0;i<b.size();i++){
                int triIndex=((Integer)b.get(i)).intValue();
                colors2[indices[triIndex*3+0]]=ColorRGBA.blue;
                colors2[indices[triIndex*3+1]]=ColorRGBA.blue;
                colors2[indices[triIndex*3+2]]=ColorRGBA.blue;
            }
        }
        r.setColors(colors2);
        }
    }
}