/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.intersection;

import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Box;
import com.jme.scene.BoundingBox;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of some objects. All the methods are static to allow for quick
 * and easy calls.
 * @author Mark Powell
 * @version $Id: IntersectionBox.java,v 1.2 2004-03-12 02:16:15 mojomonkey Exp $
 */
public class IntersectionBox {
    /**
     * EPSILON represents the error buffer used to denote a hit.
     */
    public static final double EPSILON = 1e-12;

    /**
     *
     * <code>intersection</code> determines if a ray has intersected a box.
     * @param ray the ray to test.
     * @param box the box to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingBox box) {

        Vector3f diff = ray.origin.subtract(box.center);
        // convert ray to box coordinates

        Vector3f kOrigin = new Vector3f(diff.x, diff.y, diff.z);
        Vector3f kDirection = new Vector3f(ray.direction.x, ray.direction.y, ray.direction.z);

        float[] fT = new float[2];
        fT[0] = 0f;
        fT[1] = Float.POSITIVE_INFINITY;
        float[] extents = { box.xExtent, box.yExtent, box.zExtent };
        boolean bIntersects = findIntersection(kOrigin,kDirection,extents,fT);

        return bIntersects;
    }

    /**
     *
     * <code>intersection</code> compares two static spheres for intersection.
     * If any part of the two spheres touch, true is returned, otherwise false
     * will return.
     * @param box1 the first box to test.
     * @param box2 the second box to test.
     * @return true if the spheres are intersecting, false otherwise.
     */
    public static boolean intersection(
        BoundingBox box1,
        BoundingBox box2) {
        // Cutoff for cosine of angles between box axes.  This is used to catch
        // the cases when at least one pair of axes are parallel.  If this happens,
        // there is no need to test for separation along the Cross(A[i],B[j])
        // directions.
        float fCutoff = (float)0.999999;
        boolean bExistsParallelPair = false;
        int i;

        // convenience variables
        Vector3f[] akB = {Box.AXIS_X, Box.AXIS_Y, Box.AXIS_Z};
        float[] afEA = {box1.xExtent, box1.yExtent, box1.zExtent};
        float[] afEB = {box2.xExtent, box2.yExtent, box2.zExtent};

        // compute difference of box centers, D = C1-C0
        Vector3f kD = box2.center.subtract(box1.center);

        float aafC[][] = new float[3][3];     // matrix C = A^T B, c_{ij} = Dot(A_i,B_j)
        float aafAbsC[][] = new float[3][3];  // |c_{ij}|
        float afAD[] = new float[3];        // Dot(A_i,D)
        float fR0, fR1, fR;   // interval radii and distance between centers
        float fR01;           // = R0 + R1

        // axis C0+t*A0
        for (i = 0; i < 3; i++)
        {
            aafC[0][i] = Box.AXIS_X.dot(akB[i]);
            aafAbsC[0][i] = Math.abs(aafC[0][i]);
            if ( aafAbsC[0][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[0] = Box.AXIS_X.dot(kD);
        fR = Math.abs(afAD[0]);
        fR1 = afEB[0]*aafAbsC[0][0]+afEB[1]*aafAbsC[0][1]+afEB[2]*aafAbsC[0][2];
        fR01 = afEA[0] + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1
        for (i = 0; i < 3; i++)
        {
            aafC[1][i] = Box.AXIS_Y.dot(akB[i]);
            aafAbsC[1][i] = Math.abs(aafC[1][i]);
            if ( aafAbsC[1][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[1] = Box.AXIS_Y.dot(kD);
        fR = Math.abs(afAD[1]);
        fR1 = afEB[0]*aafAbsC[1][0]+afEB[1]*aafAbsC[1][1]+afEB[2]*aafAbsC[1][2];
        fR01 = afEA[1] + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2
        for (i = 0; i < 3; i++)
        {
            aafC[2][i] = Box.AXIS_Z.dot(akB[i]);
            aafAbsC[2][i] = Math.abs(aafC[2][i]);
            if ( aafAbsC[2][i] > fCutoff )
                bExistsParallelPair = true;
        }
        afAD[2] = Box.AXIS_Z.dot(kD);
        fR = Math.abs(afAD[2]);
        fR1 = afEB[0]*aafAbsC[2][0]+afEB[1]*aafAbsC[2][1]+afEB[2]*aafAbsC[2][2];
        fR01 = afEA[2] + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B0
        fR = Math.abs(Box.AXIS_X.dot(kD));
        fR0 = afEA[0]*aafAbsC[0][0]+afEA[1]*aafAbsC[1][0]+afEA[2]*aafAbsC[2][0];
        fR01 = fR0 + afEB[0];
        if ( fR > fR01 )
            return false;

        // axis C0+t*B1
        fR = Math.abs(Box.AXIS_Y.dot(kD));
        fR0 = afEA[0]*aafAbsC[0][1]+afEA[1]*aafAbsC[1][1]+afEA[2]*aafAbsC[2][1];
        fR01 = fR0 + afEB[1];
        if ( fR > fR01 )
            return false;

        // axis C0+t*B2
        fR = Math.abs(Box.AXIS_Z.dot(kD));
        fR0 = afEA[0]*aafAbsC[0][2]+afEA[1]*aafAbsC[1][2]+afEA[2]*aafAbsC[2][2];
        fR01 = fR0 + afEB[2];
        if ( fR > fR01 )
            return false;

        // At least one pair of box axes was parallel, so the separation is
        // effectively in 2D where checking the "edge" normals is sufficient for
        // the separation of the boxes.
        if ( bExistsParallelPair )
            return true;

        // axis C0+t*A0xB0
        fR = Math.abs(afAD[2]*aafC[1][0]-afAD[1]*aafC[2][0]);
        fR0 = afEA[1]*aafAbsC[2][0] + afEA[2]*aafAbsC[1][0];
        fR1 = afEB[1]*aafAbsC[0][2] + afEB[2]*aafAbsC[0][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB1
        fR = Math.abs(afAD[2]*aafC[1][1]-afAD[1]*aafC[2][1]);
        fR0 = afEA[1]*aafAbsC[2][1] + afEA[2]*aafAbsC[1][1];
        fR1 = afEB[0]*aafAbsC[0][2] + afEB[2]*aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB2
        fR = Math.abs(afAD[2]*aafC[1][2]-afAD[1]*aafC[2][2]);
        fR0 = afEA[1]*aafAbsC[2][2] + afEA[2]*aafAbsC[1][2];
        fR1 = afEB[0]*aafAbsC[0][1] + afEB[1]*aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB0
        fR = Math.abs(afAD[0]*aafC[2][0]-afAD[2]*aafC[0][0]);
        fR0 = afEA[0]*aafAbsC[2][0] + afEA[2]*aafAbsC[0][0];
        fR1 = afEB[1]*aafAbsC[1][2] + afEB[2]*aafAbsC[1][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB1
        fR = Math.abs(afAD[0]*aafC[2][1]-afAD[2]*aafC[0][1]);
        fR0 = afEA[0]*aafAbsC[2][1] + afEA[2]*aafAbsC[0][1];
        fR1 = afEB[0]*aafAbsC[1][2] + afEB[2]*aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB2
        fR = Math.abs(afAD[0]*aafC[2][2]-afAD[2]*aafC[0][2]);
        fR0 = afEA[0]*aafAbsC[2][2] + afEA[2]*aafAbsC[0][2];
        fR1 = afEB[0]*aafAbsC[1][1] + afEB[1]*aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB0
        fR = Math.abs(afAD[1]*aafC[0][0]-afAD[0]*aafC[1][0]);
        fR0 = afEA[0]*aafAbsC[1][0] + afEA[1]*aafAbsC[0][0];
        fR1 = afEB[1]*aafAbsC[2][2] + afEB[2]*aafAbsC[2][1];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB1
        fR = Math.abs(afAD[1]*aafC[0][1]-afAD[0]*aafC[1][1]);
        fR0 = afEA[0]*aafAbsC[1][1] + afEA[1]*aafAbsC[0][1];
        fR1 = afEB[0]*aafAbsC[2][2] + afEB[2]*aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB2
        fR = Math.abs(afAD[1]*aafC[0][2]-afAD[0]*aafC[1][2]);
        fR0 = afEA[0]*aafAbsC[1][2] + afEA[1]*aafAbsC[0][2];
        fR1 = afEB[0]*aafAbsC[2][1] + afEB[1]*aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        return true;
    }

    private static boolean clip(float fDenom, float fNumer, float[] rfT)
    {
        // Return value is 'true' if line segment intersects the current test
        // plane.  Otherwise 'false' is returned in which case the line segment
        // is entirely clipped.

        if ( fDenom > 0.0f ) {
            if ( fNumer > fDenom*rfT[1] )
                return false;
            if ( fNumer > fDenom*rfT[0] )
                rfT[0] = fNumer/fDenom;
            return true;
        } else if ( fDenom < 0.0f ) {
            if ( fNumer > fDenom*rfT[0] )
                return false;
            if ( fNumer > fDenom*rfT[1] )
                rfT[1] = fNumer/fDenom;
            return true;
        } else {
            return fNumer <= 0.0;
        }
    }

    private static boolean findIntersection(Vector3f rkOrigin, Vector3f rkDirection, float[] afExtent, float[] rfT) {
        float fSaveT0 = rfT[0], fSaveT1 = rfT[1];

        boolean bNotEntirelyClipped =
            clip(+rkDirection.x,-rkOrigin.x-afExtent[0],rfT) &&
            clip(-rkDirection.x,+rkOrigin.x-afExtent[0],rfT) &&
            clip(+rkDirection.y,-rkOrigin.y-afExtent[1],rfT) &&
            clip(-rkDirection.y,+rkOrigin.y-afExtent[1],rfT) &&
            clip(+rkDirection.z,-rkOrigin.z-afExtent[2],rfT) &&
            clip(-rkDirection.z,+rkOrigin.z-afExtent[2],rfT);

        return bNotEntirelyClipped && ( rfT[0] != fSaveT0 || rfT[1] != fSaveT1 );
    }
}
