package com.jme.bounding;

import com.jme.scene.shape.OrientedBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.math.Plane;
import com.jme.math.Matrix3f;

/**
 * Started Date: Aug 24, 2004<br><br>
 *
 * This class is liked BoundingBox, but can correctly rotate to fit its bounds.
 * 
 * @author Jack Lindamood
 */
public class OrientedBoundingBox extends OrientedBox implements BoundingVolume{

    static private final Vector3f tempVa=new Vector3f();
    static private final Vector3f tempVb=new Vector3f();
    static private final Matrix3f tempMa=new Matrix3f();

    public int[] checkPlanes = new int[6];

    /**
     * Creates a new OrientedBounding box.
     */
    public OrientedBoundingBox() {
        super("obb");
        initCheckPlanes();
    }

    public BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale) {
        return transform(rotate,translate, scale,new OrientedBoundingBox());
    }

    public BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale, BoundingVolume store) {
        if (store==null)
            store=new OrientedBoundingBox();
        OrientedBoundingBox toReturn=(OrientedBoundingBox) store;
        toReturn.extent.set(extent.x*scale.x, extent.y*scale.y,extent.z*scale.z);
        rotate.toRotationMatrix(tempMa);
        tempMa.mult(xAxis,toReturn.xAxis);
        tempMa.mult(yAxis,toReturn.yAxis);
        tempMa.mult(zAxis,toReturn.zAxis);
        tempMa.mult(center,toReturn.center);
        toReturn.center.multLocal(scale).addLocal(translate);
        toReturn.correctCorners=false;
        return toReturn;
    }

    public int whichSide(Plane plane) {
        if (!correctCorners)
            computeCorners();
        boolean posSide=false;
        boolean negSide=false;
        for (int i=0;i<vectorStore.length;i++){
            if (plane.pseudoDistance(vectorStore[i])<0){
                if (posSide==true)
                    return Plane.NO_SIDE;
                negSide=true;
            } else{
                if (negSide==true)
                    return Plane.NO_SIDE;
                posSide=true;
            }
        }
        if (posSide)
            return Plane.POSITIVE_SIDE;
        else
            return Plane.NEGATIVE_SIDE;
    }

    public void computeFromPoints(Vector3f[] points) {
        containAABB(points);
//        computeCorners();
        correctCorners=false;
    }

    /**
     * Calculates an AABB of the given point values for this OBB.
     * @param points The points this OBB should contain.
     */
    private void containAABB(Vector3f[] points) {
       if(points.length <= 0) {
            return;
        }

        Vector3f min = tempVa.set(points[0]);
        Vector3f max = tempVb.set(min);

        for (int i = 1; i < points.length; i++) {
            if (points[i].x < min.x)
                min.x = points[i].x;
            else if (points[i].x > max.x)
                max.x = points[i].x;

            if (points[i].y < min.y)
                min.y = points[i].y;
            else if (points[i].y > max.y)
                max.y = points[i].y;

            if (points[i].z < min.z)
                min.z = points[i].z;
            else if (points[i].z > max.z)
                max.z = points[i].z;
        }

        center.set(min.addLocal(max));
        center.multLocal(0.5f);

        extent.set(
            max.x - center.x,
            max.y - center.y,
            max.z - center.z
        );

        xAxis.set(1,0,0);

        yAxis.set(0,1,0);

        zAxis.set(0,0,1);
    }

    public BoundingVolume merge(BoundingVolume volume) {
        return new OrientedBoundingBox().mergeLocal(volume);
    }

    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume==null) return this;
        OrientedBoundingBox mergeBox=(OrientedBoundingBox) volume;
        if (!correctCorners) this.computeCorners();
        if (!mergeBox.correctCorners) mergeBox.computeCorners();
        Vector3f[] mergeArray=new Vector3f[16];
        for (int i=0;i<vectorStore.length;i++){
            mergeArray[i*2+0]=this    .vectorStore[i];
            mergeArray[i*2+1]=mergeBox.vectorStore[i];
        }
        containAABB(mergeArray);
        correctCorners=false;
        return this;
    }

    public Object clone(BoundingVolume store) {
        if (store==null)
            store=new OrientedBoundingBox();
        OrientedBoundingBox toReturn=(OrientedBoundingBox) store;
        toReturn.extent.set(extent);
        toReturn.xAxis.set(xAxis);
        toReturn.yAxis.set(yAxis);
        toReturn.zAxis.set(zAxis);
        toReturn.center.set(center);
        for (int i=0;i<checkPlanes.length;i++)
            toReturn.checkPlanes[i]=checkPlanes[i];
        toReturn.correctCorners=false;
        return toReturn;
    }


    public void initCheckPlanes() {
        checkPlanes[0] = 0;
        checkPlanes[1] = 1;
        checkPlanes[2] = 2;
        checkPlanes[3] = 3;
        checkPlanes[4] = 4;
        checkPlanes[5] = 5;
    }

    public int getCheckPlane(int index) {
        return checkPlanes[index];
    }

    public void setCheckPlane(int index, int value) {
        checkPlanes[index] = value;
    }

    public void recomputeMesh() {
        computeInformation();
    }

    public float distanceTo(Vector3f point) {
        return point.distance(getCenter(tempVa));
    }

    public Vector3f getCenter(Vector3f store) {
        return store.set(super.getCenter());
    }
}