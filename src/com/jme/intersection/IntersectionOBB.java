package com.jme.intersection;

import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.bounding.BoundingVolume;

/**
 * Started Date: Aug 24, 2004<br><br>
 *
 * This class calculates various intersection test of Oriented Bounding Boxes.  Most
 * functions taken from www.magic-software.com  Converted to java by Jack Lindamood.
 * 
 * @author Jack Lindamood
 */
public class IntersectionOBB {
    private static final float [] fWdU=new float[3];
    private static final float [] fAWdU=new float[3];
    private static final float [] fDdU=new float[3];
    private static final float [] fADdU=new float[3];
    private static final float [] fAWxDdU=new float[3];

    private static final float[] tempFa=new float[3];
    private static final float[] tempFb=new float[3];
    private static final Vector3f tempVa=new Vector3f();
    private static final Vector3f tempVb=new Vector3f();

    private IntersectionOBB(){}

    /**
     * Test for the intersection of a ray and an oriented bounding box.
     * @param rkRay The ray attempting to intersect.
     * @param rkBox The box the ray is trying to hit.
     * @return True if the ray intersects with the box, false otherwise.
     */
    public static boolean intersection(Ray rkRay, OrientedBoundingBox rkBox) {
//        float fWdU[3], fAWdU[3], fDdU[3], fADdU[3], fAWxDdU[3], fRhs;
        float fRhs;


//        Vector3<Real> kDiff = rkRay.Origin() - rkBox.Center();
        Vector3f kDiff = rkRay.origin.subtract(rkBox.getCenter(tempVb),tempVa);

        fWdU[0] = rkRay.getDirection().dot(rkBox.getxAxis());
        fAWdU[0] = FastMath.abs(fWdU[0]);
        fDdU[0] = kDiff.dot(rkBox.getxAxis());
        fADdU[0] = FastMath.abs(fDdU[0]);
        if ( fADdU[0] > rkBox.getExtent().x && fDdU[0]*fWdU[0] >= 0.0 )
            return false;

        fWdU[1] = rkRay.getDirection().dot(rkBox.getyAxis());
        fAWdU[1] = FastMath.abs(fWdU[1]);
        fDdU[1] = kDiff.dot(rkBox.getyAxis());
        fADdU[1] = FastMath.abs(fDdU[1]);
        if ( fADdU[1] > rkBox.getExtent().y && fDdU[1]*fWdU[1] >= 0.0 )
            return false;

        fWdU[2] = rkRay.getDirection().dot(rkBox.getzAxis());
        fAWdU[2] = FastMath.abs(fWdU[2]);
        fDdU[2] = kDiff.dot(rkBox.getzAxis());
        fADdU[2] = FastMath.abs(fDdU[2]);
        if ( fADdU[2] > rkBox.getExtent().z && fDdU[2]*fWdU[2] >= 0.0 )
            return false;

        Vector3f kWxD = rkRay.getDirection().cross(kDiff,tempVb);

        fAWxDdU[0] = FastMath.abs(kWxD.dot(rkBox.getxAxis()));
        fRhs = rkBox.getExtent().y*fAWdU[2] + rkBox.getExtent().z*fAWdU[1];
        if ( fAWxDdU[0] > fRhs )
            return false;

        fAWxDdU[1] = FastMath.abs(kWxD.dot(rkBox.getyAxis()));
        fRhs = rkBox.getExtent().x*fAWdU[2] + rkBox.getExtent().z*fAWdU[0];
        if ( fAWxDdU[1] > fRhs )
            return false;

        fAWxDdU[2] = FastMath.abs(kWxD.dot(rkBox.getzAxis()));
        fRhs = rkBox.getExtent().x*fAWdU[1] + rkBox.getExtent().y*fAWdU[0];
        if ( fAWxDdU[2] > fRhs )
            return false;

        return true;
    }

    /**
     * This function test for the intersection of an oriented bounding box and a
     * bounding volume.  Only works for OBB to OBB intersections so far.
     * @param box The Oriented Bounding Box object.
     * @param vol2 The other bounding volume.
     * @return Returns true if the two intersect.
     */
    public static boolean intersection(OrientedBoundingBox box, BoundingVolume vol2) {
        if (vol2 instanceof OrientedBoundingBox)
            return intersection(box,(OrientedBoundingBox)vol2);
        else
            return false;
    }

    /**
     * Helper function of intersection. Returns true if two OBB intersect.
     * @param rkBox0 The first OBB.
     * @param rkBox1 The second OBB.
     * @return True if they intersect.
     */
    private static boolean intersection(OrientedBoundingBox rkBox0, OrientedBoundingBox rkBox1) {
        // Cutoff for cosine of angles between box axes.  This is used to catch
        // the cases when at least one pair of axes are parallel.  If this happens,
        // there is no need to test for separation along the Cross(A[i],B[j])
        // directions.
        float fCutoff = 0.999999f;
        boolean bExistsParallelPair = false;
        int i;

        // convenience variables
        Vector3f akA[] = new Vector3f[]{
            rkBox0.getxAxis(),
            rkBox0.getyAxis(),
            rkBox0.getzAxis()
        };
        Vector3f[] akB = new Vector3f[]{
            rkBox1.getxAxis(),
            rkBox1.getyAxis(),
            rkBox1.getzAxis()
        };
        Vector3f afEA = rkBox0.getExtent();
        Vector3f afEB = rkBox1.getExtent();

        // compute difference of box centers, D = C1-C0
        Vector3f kD = rkBox1.getCenter().subtract(rkBox0.getCenter(),tempVa);

//        float aafC[3][3];     // matrix C = A^T B, c_{ij} = Dot(A_i,B_j)
        float [][] aafC={
            fWdU,fAWdU,fDdU
        };
//        float aafAbsC[3][3];  // |c_{ij}|
        float [][] aafAbsC={
            fADdU,fAWxDdU,tempFa
        };
//        float afAD[3];        // Dot(A_i,D)
        float[] afAD=tempFb;
        float fR0, fR1, fR;   // interval radii and distance between centers
        float fR01;           // = R0 + R1

        // axis C0+t*A0
        for (i = 0; i < 3; i++)
        {
            aafC[0][i] = akA[0].dot(akB[i]);
            aafAbsC[0][i] = FastMath.abs(aafC[0][i]);
            if ( aafAbsC[0][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[0] = akA[0].dot(kD);
        fR = FastMath.abs(afAD[0]);
        fR1 = afEB.x *aafAbsC[0][0]+afEB.y*aafAbsC[0][1]+afEB.z*aafAbsC[0][2];
        fR01 = afEA.x + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1
        for (i = 0; i < 3; i++)
        {
            aafC[1][i] = akA[1].dot(akB[i]);
            aafAbsC[1][i] = FastMath.abs(aafC[1][i]);
            if ( aafAbsC[1][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[1] = akA[1].dot(kD);
        fR = FastMath.abs(afAD[1]);
        fR1 = afEB.x*aafAbsC[1][0]+afEB.y*aafAbsC[1][1]+afEB.z*aafAbsC[1][2];
        fR01 = afEA.y + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2
        for (i = 0; i < 3; i++)
        {
            aafC[2][i] = akA[2].dot(akB[i]);
            aafAbsC[2][i] = FastMath.abs(aafC[2][i]);
            if ( aafAbsC[2][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[2] = akA[2].dot(kD);
        fR = FastMath.abs(afAD[2]);
        fR1 = afEB.x*aafAbsC[2][0]+afEB.y*aafAbsC[2][1]+afEB.z*aafAbsC[2][2];
        fR01 = afEA.z + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B0
        fR = FastMath.abs(akB[0].dot(kD));
        fR0 = afEA.x*aafAbsC[0][0]+afEA.y*aafAbsC[1][0]+afEA.z*aafAbsC[2][0];
        fR01 = fR0 + afEB.x;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B1
        fR = FastMath.abs(akB[1].dot(kD));
        fR0 = afEA.x*aafAbsC[0][1]+afEA.y*aafAbsC[1][1]+afEA.z*aafAbsC[2][1];
        fR01 = fR0 + afEB.y;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B2
        fR = FastMath.abs(akB[2].dot(kD));
        fR0 = afEA.x*aafAbsC[0][2]+afEA.y*aafAbsC[1][2]+afEA.z*aafAbsC[2][2];
        fR01 = fR0 + afEB.z;
        if ( fR > fR01 )
            return false;

        // At least one pair of box axes was parallel, so the separation is
        // effectively in 2D where checking the "edge" normals is sufficient for
        // the separation of the boxes.
        if ( bExistsParallelPair )
            return true;

        // axis C0+t*A0xB0
        fR = FastMath.abs(afAD[2]*aafC[1][0]-afAD[1]*aafC[2][0]);
        fR0 = afEA.y*aafAbsC[2][0] + afEA.z*aafAbsC[1][0];
        fR1 = afEB.y*aafAbsC[0][2] + afEB.z*aafAbsC[0][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB1
        fR = FastMath.abs(afAD[2]*aafC[1][1]-afAD[1]*aafC[2][1]);
        fR0 = afEA.y*aafAbsC[2][1] + afEA.z*aafAbsC[1][1];
        fR1 = afEB.x*aafAbsC[0][2] + afEB.z*aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB2
        fR = FastMath.abs(afAD[2]*aafC[1][2]-afAD[1]*aafC[2][2]);
        fR0 = afEA.y*aafAbsC[2][2] + afEA.z*aafAbsC[1][2];
        fR1 = afEB.x*aafAbsC[0][1] + afEB.y*aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB0
        fR = FastMath.abs(afAD[0]*aafC[2][0]-afAD[2]*aafC[0][0]);
        fR0 = afEA.x*aafAbsC[2][0] + afEA.z*aafAbsC[0][0];
        fR1 = afEB.y*aafAbsC[1][2] + afEB.z*aafAbsC[1][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB1
        fR = FastMath.abs(afAD[0]*aafC[2][1]-afAD[2]*aafC[0][1]);
        fR0 = afEA.x*aafAbsC[2][1] + afEA.z*aafAbsC[0][1];
        fR1 = afEB.x*aafAbsC[1][2] + afEB.z*aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB2
        fR = FastMath.abs(afAD[0]*aafC[2][2]-afAD[2]*aafC[0][2]);
        fR0 = afEA.x*aafAbsC[2][2] + afEA.z*aafAbsC[0][2];
        fR1 = afEB.x*aafAbsC[1][1] + afEB.y*aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB0
        fR = FastMath.abs(afAD[1]*aafC[0][0]-afAD[0]*aafC[1][0]);
        fR0 = afEA.x*aafAbsC[1][0] + afEA.y*aafAbsC[0][0];
        fR1 = afEB.y*aafAbsC[2][2] + afEB.z*aafAbsC[2][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB1
        fR = FastMath.abs(afAD[1]*aafC[0][1]-afAD[0]*aafC[1][1]);
        fR0 = afEA.x*aafAbsC[1][1] + afEA.y*aafAbsC[0][1];
        fR1 = afEB.x*aafAbsC[2][2] + afEB.z*aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB2
        fR = FastMath.abs(afAD[1]*aafC[0][2]-afAD[0]*aafC[1][2]);
        fR0 = afEA.x*aafAbsC[1][2] + afEA.y*aafAbsC[0][2];
        fR1 = afEB.x*aafAbsC[2][1] + afEB.y*aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        return true;
    }
}