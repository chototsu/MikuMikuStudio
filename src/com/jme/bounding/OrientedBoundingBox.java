package com.jme.bounding;

import com.jme.scene.shape.OrientedBox;
import com.jme.math.*;

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
    static private final Vector3f tempVc=new Vector3f();
    static private final Vector3f tempVd=new Vector3f();
    static private final Vector3f tempVe=new Vector3f();
    static private final Vector3f tempVf=new Vector3f();
    static private final Vector3f tempVg=new Vector3f();
    static private final Vector3f tempVh=new Vector3f();
    static private final Vector3f tempVi=new Vector3f();
    static private final Vector3f tempVj=new Vector3f();
    static private final Matrix3f tempMa=new Matrix3f();
    static private final Quaternion tempQa=new Quaternion();
    static private final Quaternion tempQb=new Quaternion();


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
        float fRadius=FastMath.abs(extent.x*(plane.getNormal().dot(xAxis)))+
                FastMath.abs(extent.y*(plane.getNormal().dot(yAxis)))+
                FastMath.abs(extent.z*(plane.getNormal().dot(zAxis)));
        float fDistance=plane.pseudoDistance(center);
        if (fDistance <=-fRadius)
            return Plane.NEGATIVE_SIDE;
        else
            return Plane.POSITIVE_SIDE;
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
        if (volume instanceof OrientedBoundingBox){
            return mergeOBB((OrientedBoundingBox)volume);
        } else if (volume instanceof BoundingBox){
            return mergeAABB((BoundingBox)volume);
        } else if (volume instanceof BoundingSphere){
            return mergeSphere((BoundingSphere)volume);
        } else
            return null;
    }

    private BoundingVolume mergeSphere(BoundingSphere volume) {
        BoundingSphere mergeSphere=(BoundingSphere) volume;
        if (!correctCorners) this.computeCorners();
        Vector3f[] mergeArray=new Vector3f[16];
        for (int i=0;i<vectorStore.length;i++){
            mergeArray[i]=this    .vectorStore[i];
        }
        mergeArray[8]=tempVc.set(mergeSphere.center).addLocal(
                mergeSphere.radius, mergeSphere.radius, mergeSphere.radius
        );
        mergeArray[9]=tempVd.set(mergeSphere.center).addLocal(
                -mergeSphere.radius, mergeSphere.radius, mergeSphere.radius
        );
        mergeArray[10]=tempVe.set(mergeSphere.center).addLocal(
                mergeSphere.radius, -mergeSphere.radius, mergeSphere.radius
        );
        mergeArray[11]=tempVf.set(mergeSphere.center).addLocal(
                mergeSphere.radius, mergeSphere.radius, -mergeSphere.radius
        );
        mergeArray[12]=tempVg.set(mergeSphere.center).addLocal(
                -mergeSphere.radius, -mergeSphere.radius, mergeSphere.radius
        );
        mergeArray[13]=tempVh.set(mergeSphere.center).addLocal(
                -mergeSphere.radius, mergeSphere.radius, -mergeSphere.radius
        );
        mergeArray[14]=tempVi.set(mergeSphere.center).addLocal(
                mergeSphere.radius, -mergeSphere.radius, -mergeSphere.radius
        );
        mergeArray[15]=tempVj.set(mergeSphere.center).addLocal(
                -mergeSphere.radius, -mergeSphere.radius, -mergeSphere.radius
        );
        containAABB(mergeArray);
        correctCorners=false;
        return this;
    }

    private BoundingVolume mergeAABB(BoundingBox volume) {
        BoundingBox mergeBox=(BoundingBox) volume;
        if (!correctCorners) this.computeCorners();
        Vector3f[] mergeArray=new Vector3f[16];
        for (int i=0;i<vectorStore.length;i++){
            mergeArray[i]=this    .vectorStore[i];
        }
        mergeArray[8]=tempVc.set(mergeBox.center).addLocal(
                mergeBox.xExtent, mergeBox.yExtent, mergeBox.zExtent
        );
        mergeArray[9]=tempVd.set(mergeBox.center).addLocal(
                -mergeBox.xExtent, mergeBox.yExtent, mergeBox.zExtent
        );
        mergeArray[10]=tempVe.set(mergeBox.center).addLocal(
                mergeBox.xExtent, -mergeBox.yExtent, mergeBox.zExtent
        );
        mergeArray[11]=tempVf.set(mergeBox.center).addLocal(
                mergeBox.xExtent, mergeBox.yExtent, -mergeBox.zExtent
        );
        mergeArray[12]=tempVg.set(mergeBox.center).addLocal(
                -mergeBox.xExtent, -mergeBox.yExtent, mergeBox.zExtent
        );
        mergeArray[13]=tempVh.set(mergeBox.center).addLocal(
                -mergeBox.xExtent, mergeBox.yExtent, -mergeBox.zExtent
        );
        mergeArray[14]=tempVi.set(mergeBox.center).addLocal(
                mergeBox.xExtent, -mergeBox.yExtent, -mergeBox.zExtent
        );
        mergeArray[15]=tempVj.set(mergeBox.center).addLocal(
                -mergeBox.xExtent, -mergeBox.yExtent, -mergeBox.zExtent
        );
        containAABB(mergeArray);
        correctCorners=false;
        return this;

    }

    private BoundingVolume mergeOBB(OrientedBoundingBox volume) {
//        OrientedBoundingBox mergeBox=(OrientedBoundingBox) volume;
//        if (!correctCorners) this.computeCorners();
//        if (!mergeBox.correctCorners) mergeBox.computeCorners();
//        Vector3f[] mergeArray=new Vector3f[16];
//        for (int i=0;i<vectorStore.length;i++){
//            mergeArray[i*2+0]=this    .vectorStore[i];
//            mergeArray[i*2+1]=mergeBox.vectorStore[i];
//        }
//        containAABB(mergeArray);
//        correctCorners=false;
//        return this;
        // construct a box that contains the input boxes
//        Box3<Real> kBox;
        OrientedBoundingBox rkBox0=this;
        OrientedBoundingBox rkBox1=volume;

        // The first guess at the box center.  This value will be updated later
        // after the input box vertices are projected onto axes determined by an
        // average of box axes.
        Vector3f kBoxCenter = (rkBox0.getCenter().add(rkBox1.getCenter(),tempVd)).multLocal(.5f);

        // A box's axes, when viewed as the columns of a matrix, form a rotation
        // matrix.  The input box axes are converted to quaternions.  The average
        // quaternion is computed, then normalized to unit length.  The result is
        // the slerp of the two input quaternions with t-value of 1/2.  The result
        // is converted back to a rotation matrix and its columns are selected as
        // the merged box axes.
        Quaternion kQ0=tempQa, kQ1=tempQb;

        tempMa.setColumn(0,rkBox0.getxAxis());
        tempMa.setColumn(1,rkBox0.getyAxis());
        tempMa.setColumn(2,rkBox0.getzAxis());
        kQ0.fromRotationMatrix(tempMa);

        tempMa.setColumn(0,rkBox1.getxAxis());
        tempMa.setColumn(1,rkBox1.getyAxis());
        tempMa.setColumn(2,rkBox1.getzAxis());
        kQ1.fromRotationMatrix(tempMa);

        if ( kQ0.dot(kQ1) < 0.0f )
            kQ1.negate();

        Quaternion kQ = kQ0.addLocal(kQ1);
        float fInvLength = FastMath.invSqrt(kQ.dot(kQ));
        kQ.multLocal(fInvLength);
//        kQ = fInvLength*kQ;
        Matrix3f kBoxaxis=kQ.toRotationMatrix(tempMa);

        // Project the input box vertices onto the merged-box axes.  Each axis
        // D[i] containing the current center C has a minimum projected value
        // pmin[i] and a maximum projected value pmax[i].  The corresponding end
        // points on the axes are C+pmin[i]*D[i] and C+pmax[i]*D[i].  The point C
        // is not necessarily the midpoint for any of the intervals.  The actual
        // box center will be adjusted from C to a point C' that is the midpoint
        // of each interval,
        //   C' = C + sum_{i=0}^2 0.5*(pmin[i]+pmax[i])*D[i]
        // The box extents are
        //   e[i] = 0.5*(pmax[i]-pmin[i])

        int i;
        float fDot;
//        Vector3f akVertex[8], kDiff;
        Vector3f kDiff=tempVa;
//        Vector3f kMin = new Vector3f();
//        Vector3f kMax = new Vector3f();
//        float[] kMax=new float[3];
//        float[] kMin=new float[3];
        Vector3f kMin=tempVb;
        Vector3f kMax=tempVc;

        Vector3f kBoxXaxis=kBoxaxis.getColumn(0,tempVe);
        Vector3f kBoxYaxis=kBoxaxis.getColumn(1,tempVf);
        Vector3f kBoxZaxis=kBoxaxis.getColumn(2,tempVg);

//        rkBox0.ComputeVertices(akVertex);
        if (!rkBox0.correctCorners)
            rkBox0.computeCorners();
        for (i = 0; i < 8; i++)
        {
//            kDiff = akVertex[i] - kBox.Center();
            rkBox0.vectorStore[i].subtract(kBoxCenter,kDiff);

            fDot = kDiff.dot(kBoxXaxis);
            if ( fDot > kMax.x )
                kMax.x = fDot;
            else if ( fDot < kMin.x )
                kMin.x = fDot;

            fDot = kDiff.dot(kBoxYaxis);
            if ( fDot > kMax.y )
                kMax.y = fDot;
            else if ( fDot < kMin.y )
                kMin.y = fDot;

            fDot = kDiff.dot(kBoxZaxis);
            if ( fDot > kMax.z )
                kMax.z = fDot;
            else if ( fDot < kMin.z )
                kMin.z = fDot;

        }

        if (!rkBox1.correctCorners) rkBox1.computeCorners();
        for (i = 0; i < 8; i++)
        {
//            kDiff = akVertex[i] - kBox.Center();
            rkBox1.vectorStore[i].subtract(kBoxCenter,kDiff);

            fDot = kDiff.dot(kBoxXaxis);
            if ( fDot > kMax.x )
                kMax.x = fDot;
            else if ( fDot < kMin.x )
                kMin.x = fDot;

            fDot = kDiff.dot(kBoxYaxis);
            if ( fDot > kMax.y )
                kMax.y = fDot;
            else if ( fDot < kMin.y )
                kMin.y = fDot;

            fDot = kDiff.dot(kBoxZaxis);
            if ( fDot > kMax.z )
                kMax.z = fDot;
            else if ( fDot < kMin.z )
                kMin.z = fDot;
        }

        xAxis.set(kBoxXaxis);
        yAxis.set(kBoxYaxis);
        zAxis.set(kBoxZaxis);

        this.extent.x=.5f*(kMax.x-kMin.x);
        kBoxCenter.addLocal(this.xAxis.mult(.5f*(kMax.x+kMin.x),tempVe));

        this.extent.y=.5f*(kMax.y-kMin.y);
        kBoxCenter.addLocal(this.yAxis.mult(.5f*(kMax.y+kMin.y),tempVe));

        this.extent.z=.5f*(kMax.z-kMin.z);
        kBoxCenter.addLocal(this.zAxis.mult(.5f*(kMax.z+kMin.z),tempVe));

        this.center.set(kBoxCenter);

        this.correctCorners=false;
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