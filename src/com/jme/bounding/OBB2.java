package com.jme.bounding;

import com.jme.math.*;

/**
 * Started Date: Sep 5, 2004<br><br>
 * This class is like an OrientedBoundingBox, but lacks the overhead of extending TriMesh.
 * @author Jack Lindamood
 */
public class OBB2 implements BoundingVolume{

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

    private final int[] checkPlanes = new int[6];

    private static final float[] fWdU = new float[3];
    private static final float[] fAWdU = new float[3];
    private static final float[] fDdU = new float[3];
    private static final float[] fADdU = new float[3];
    private static final float[] fAWxDdU = new float[3];
    private static final float[] tempFa = new float[3];
    private static final float[] tempFb = new float[3];


    /** Center of the Oriented Box. */
    public final Vector3f center=new Vector3f(0,0,0);
    /** X axis of the Oriented Box.*/
    public final Vector3f xAxis=new Vector3f(1,0,0);
    /** Y axis of the Oriented Box.*/
    public final Vector3f yAxis=new Vector3f(0,1,0);
    /** Z axis of the Oriented Box.*/
    public final Vector3f zAxis=new Vector3f(0,0,1);
    /** Extents of the box along the x,y,z axis.*/
    public final Vector3f extent=new Vector3f(0,0,0);
    /** Vector array used to store the array of 8 corners the box has.*/
    private final Vector3f [] vectorStore=new Vector3f[8];
    /** If true, the box's vectorStore array correctly represnts the box's corners.*/
    private boolean correctCorners=false;


    public BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale) {
        return transform(rotate,translate, scale,new OBB2());
    }

    public BoundingVolume transform(Quaternion rotate, Vector3f translate, Vector3f scale, BoundingVolume store) {
        if (store==null)
            store=new OBB2();
        OBB2 toReturn=(OBB2) store;
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

    public BoundingVolume transform(Matrix3f rotate, Vector3f translate, Vector3f scale, BoundingVolume store) {
        if (store==null)
            store=new OBB2();
        OBB2 toReturn=(OBB2) store;
        toReturn.extent.set(extent.x*scale.x, extent.y*scale.y,extent.z*scale.z);
        rotate.mult(xAxis,toReturn.xAxis);
        rotate.mult(yAxis,toReturn.yAxis);
        rotate.mult(zAxis,toReturn.zAxis);
        rotate.mult(center,toReturn.center);
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
        return new OBB2().mergeLocal(volume);
    }

    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume==null) return this;
        if (volume instanceof OBB2){
            return mergeOBB((OBB2)volume);
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

    private BoundingVolume mergeOBB(OBB2 volume) {
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
        OBB2 rkBox0=this;
        OBB2 rkBox1=volume;

        // The first guess at the box center.  This value will be updated later
        // after the input box vertices are projected onto axes determined by an
        // average of box axes.
        Vector3f kBoxCenter = (rkBox0.center.add(rkBox1.center,tempVd)).multLocal(.5f);

        // A box's axes, when viewed as the columns of a matrix, form a rotation
        // matrix.  The input box axes are converted to quaternions.  The average
        // quaternion is computed, then normalized to unit length.  The result is
        // the slerp of the two input quaternions with t-value of 1/2.  The result
        // is converted back to a rotation matrix and its columns are selected as
        // the merged box axes.
        Quaternion kQ0=tempQa, kQ1=tempQb;

        tempMa.setColumn(0,rkBox0.xAxis);
        tempMa.setColumn(1,rkBox0.yAxis);
        tempMa.setColumn(2,rkBox0.zAxis);
        kQ0.fromRotationMatrix(tempMa);

        tempMa.setColumn(0,rkBox1.xAxis);
        tempMa.setColumn(1,rkBox1.yAxis);
        tempMa.setColumn(2,rkBox1.zAxis);
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
            store=new OBB2();
        OBB2 toReturn=(OBB2) store;
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
        if (!correctCorners) computeCorners();
    }

    public float distanceTo(Vector3f point) {
        return point.distance(center);
    }

    public Vector3f getCenter(Vector3f store) {
        return store.set(center);
    }


    /**
     * Sets the vectorStore information to the 8 corners of the box.
     */
    public void computeCorners() {
        correctCorners=true;
        float xDotYcrossZ=xAxis.dot(yAxis.cross(zAxis,tempVa));
        Vector3f yCrossZmulX=yAxis.cross(zAxis,tempVa).multLocal(extent.x);
        Vector3f zCrossXmulY=zAxis.cross(xAxis,tempVb).multLocal(extent.y);
        Vector3f xCrossYmulZ=xAxis.cross(yAxis,tempVc).multLocal(extent.z);

        vectorStore[0].set(
                ((yCrossZmulX.x + zCrossXmulY.x + xCrossYmulZ.x)/xDotYcrossZ)+center.x,
                ((yCrossZmulX.y + zCrossXmulY.y + xCrossYmulZ.y)/xDotYcrossZ)+center.y,
                ((yCrossZmulX.z + zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ)+center.z
        );
        vectorStore[1].set(
                (-yCrossZmulX.x + zCrossXmulY.x + xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (-yCrossZmulX.y + zCrossXmulY.y + xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (-yCrossZmulX.z + zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
        );

        vectorStore[2].set(
                (yCrossZmulX.x + -zCrossXmulY.x + xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (yCrossZmulX.y + -zCrossXmulY.y + xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (yCrossZmulX.z + -zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
        );

        vectorStore[3].set(
                (yCrossZmulX.x + zCrossXmulY.x + -xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (yCrossZmulX.y + zCrossXmulY.y + -xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (yCrossZmulX.z + zCrossXmulY.z + -xCrossYmulZ.z)/xDotYcrossZ+center.z
        );

        vectorStore[4].set(
                (-yCrossZmulX.x + -zCrossXmulY.x + xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (-yCrossZmulX.y + -zCrossXmulY.y + xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (-yCrossZmulX.z + -zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
        );

        vectorStore[5].set(
                (-yCrossZmulX.x + zCrossXmulY.x + -xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (-yCrossZmulX.y + zCrossXmulY.y + -xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (-yCrossZmulX.z + zCrossXmulY.z + -xCrossYmulZ.z)/xDotYcrossZ+center.z
        );
        vectorStore[6].set(
                (yCrossZmulX.x + -zCrossXmulY.x + -xCrossYmulZ.x)/xDotYcrossZ+center.x,
                (yCrossZmulX.y + -zCrossXmulY.y + -xCrossYmulZ.y)/xDotYcrossZ+center.y,
                (yCrossZmulX.z + -zCrossXmulY.z + -xCrossYmulZ.z)/xDotYcrossZ+center.z
        );

        vectorStore[7].set(
                -(yCrossZmulX.x + zCrossXmulY.x + xCrossYmulZ.x)/xDotYcrossZ+center.x,
                -(yCrossZmulX.y + zCrossXmulY.y + xCrossYmulZ.y)/xDotYcrossZ+center.y,
                -(yCrossZmulX.z + zCrossXmulY.z + xCrossYmulZ.z)/xDotYcrossZ+center.z
        );
    }

    public void computeFromTris(OBBTree.TreeTriangle[] tris, int start, int end) {
        if(end-start <= 0) {
             return;
         }

         Vector3f min = tempVa.set(tris[start].a);
         Vector3f max = tempVb.set(min);
         Vector3f point;
         for (int i = start; i < end; i++) {

             point=tris[i].a;
             if (point.x < min.x)
                 min.x = point.x;
             else if (point.x > max.x)
                 max.x = point.x;
             if (point.y < min.y)
                 min.y = point.y;
             else if (point.y > max.y)
                 max.y = point.y;
             if (point.z < min.z)
                 min.z = point.z;
             else if (point.z > max.z)
                 max.z = point.z;


             point=tris[i].b;
             if (point.x < min.x)
                 min.x = point.x;
             else if (point.x > max.x)
                 max.x = point.x;
             if (point.y < min.y)
                 min.y = point.y;
             else if (point.y > max.y)
                 max.y = point.y;
             if (point.z < min.z)
                 min.z = point.z;
             else if (point.z > max.z)
                 max.z = point.z;

             point=tris[i].c;
             if (point.x < min.x)
                 min.x = point.x;
             else if (point.x > max.x)
                 max.x = point.x;

             if (point.y < min.y)
                 min.y = point.y;
             else if (point.y > max.y)
                 max.y = point.y;

             if (point.z < min.z)
                 min.z = point.z;
             else if (point.z > max.z)
                 max.z = point.z;
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

    public boolean intersection(OBB2 box1) {
        // Cutoff for cosine of angles between box axes.  This is used to catch
        // the cases when at least one pair of axes are parallel.  If this happens,
        // there is no need to test for separation along the Cross(A[i],B[j])
        // directions.
        OBB2 box0=this;
        float cutoff = 0.999999f;
        boolean parallelPairExists = false;
        int i;

        // convenience variables
        Vector3f akA[] = new Vector3f[]{
            box0.xAxis,
            box0.yAxis,
            box0.zAxis
        };
        Vector3f[] akB = new Vector3f[]{
            box1.xAxis,
            box1.yAxis,
            box1.zAxis
        };
        Vector3f afEA = box0.extent;
        Vector3f afEB = box1.extent;

        // compute difference of box centers, D = C1-C0
        Vector3f kD = box1.center.subtract(box0.center, tempVa);

        float[][] aafC = {
            fWdU, fAWdU, fDdU
        };

        float[][] aafAbsC = {
            fADdU, fAWxDdU, tempFa
        };

        float[] afAD = tempFb;
        float fR0, fR1, fR;   // interval radii and distance between centers
        float fR01;           // = R0 + R1

        // axis C0+t*A0
        for (i = 0; i < 3; i++) {
            aafC[0][i] = akA[0].dot(akB[i]);
            aafAbsC[0][i] = FastMath.abs(aafC[0][i]);
            if (aafAbsC[0][i] > cutoff) {
                parallelPairExists = true;
            }
        }
        afAD[0] = akA[0].dot(kD);
        fR = FastMath.abs(afAD[0]);
        fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z * aafAbsC[0][2];
        fR01 = afEA.x + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A1
        for (i = 0; i < 3; i++) {
            aafC[1][i] = akA[1].dot(akB[i]);
            aafAbsC[1][i] = FastMath.abs(aafC[1][i]);
            if (aafAbsC[1][i] > cutoff) {
                parallelPairExists = true;
            }
        }
        afAD[1] = akA[1].dot(kD);
        fR = FastMath.abs(afAD[1]);
        fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z * aafAbsC[1][2];
        fR01 = afEA.y + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A2
        for (i = 0; i < 3; i++) {
            aafC[2][i] = akA[2].dot(akB[i]);
            aafAbsC[2][i] = FastMath.abs(aafC[2][i]);
            if (aafAbsC[2][i] > cutoff) {
                parallelPairExists = true;
            }
        }
        afAD[2] = akA[2].dot(kD);
        fR = FastMath.abs(afAD[2]);
        fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z * aafAbsC[2][2];
        fR01 = afEA.z + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B0
        fR = FastMath.abs(akB[0].dot(kD));
        fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z * aafAbsC[2][0];
        fR01 = fR0 + afEB.x;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B1
        fR = FastMath.abs(akB[1].dot(kD));
        fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z * aafAbsC[2][1];
        fR01 = fR0 + afEB.y;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B2
        fR = FastMath.abs(akB[2].dot(kD));
        fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z * aafAbsC[2][2];
        fR01 = fR0 + afEB.z;
        if (fR > fR01) {
            return false;
        }

        // At least one pair of box axes was parallel, so the separation is
        // effectively in 2D where checking the "edge" normals is sufficient for
        // the separation of the boxes.
        if (parallelPairExists) {
            return true;
        }

        // axis C0+t*A0xB0
        fR = FastMath.abs(afAD[2] * aafC[1][0] - afAD[1] * aafC[2][0]);
        fR0 = afEA.y * aafAbsC[2][0] + afEA.z * aafAbsC[1][0];
        fR1 = afEB.y * aafAbsC[0][2] + afEB.z * aafAbsC[0][1];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A0xB1
        fR = FastMath.abs(afAD[2] * aafC[1][1] - afAD[1] * aafC[2][1]);
        fR0 = afEA.y * aafAbsC[2][1] + afEA.z * aafAbsC[1][1];
        fR1 = afEB.x * aafAbsC[0][2] + afEB.z * aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A0xB2
        fR = FastMath.abs(afAD[2] * aafC[1][2] - afAD[1] * aafC[2][2]);
        fR0 = afEA.y * aafAbsC[2][2] + afEA.z * aafAbsC[1][2];
        fR1 = afEB.x * aafAbsC[0][1] + afEB.y * aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A1xB0
        fR = FastMath.abs(afAD[0] * aafC[2][0] - afAD[2] * aafC[0][0]);
        fR0 = afEA.x * aafAbsC[2][0] + afEA.z * aafAbsC[0][0];
        fR1 = afEB.y * aafAbsC[1][2] + afEB.z * aafAbsC[1][1];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A1xB1
        fR = FastMath.abs(afAD[0] * aafC[2][1] - afAD[2] * aafC[0][1]);
        fR0 = afEA.x * aafAbsC[2][1] + afEA.z * aafAbsC[0][1];
        fR1 = afEB.x * aafAbsC[1][2] + afEB.z * aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A1xB2
        fR = FastMath.abs(afAD[0] * aafC[2][2] - afAD[2] * aafC[0][2]);
        fR0 = afEA.x * aafAbsC[2][2] + afEA.z * aafAbsC[0][2];
        fR1 = afEB.x * aafAbsC[1][1] + afEB.y * aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A2xB0
        fR = FastMath.abs(afAD[1] * aafC[0][0] - afAD[0] * aafC[1][0]);
        fR0 = afEA.x * aafAbsC[1][0] + afEA.y * aafAbsC[0][0];
        fR1 = afEB.y * aafAbsC[2][2] + afEB.z * aafAbsC[2][1];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A2xB1
        fR = FastMath.abs(afAD[1] * aafC[0][1] - afAD[0] * aafC[1][1]);
        fR0 = afEA.x * aafAbsC[1][1] + afEA.y * aafAbsC[0][1];
        fR1 = afEB.x * aafAbsC[2][2] + afEB.z * aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*A2xB2
        fR = FastMath.abs(afAD[1] * aafC[0][2] - afAD[0] * aafC[1][2]);
        fR0 = afEA.x * aafAbsC[1][2] + afEA.y * aafAbsC[0][2];
        fR1 = afEB.x * aafAbsC[2][1] + afEB.y * aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if (fR > fR01) {
            return false;
        }

        return true;
    }
}