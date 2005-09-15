/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.bounding;

import java.nio.FloatBuffer;

import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.shape.OrientedBox;
import com.jme.util.geom.BufferUtils;

/**
 * Started Date: Aug 24, 2004 <br>
 * <br>
 * 
 * This class is liked BoundingBox, but can correctly rotate to fit its bounds.
 * 
 * @author Jack Lindamood
 */
public class OrientedBoundingBox extends OrientedBox implements BoundingVolume {
    private static final long serialVersionUID = 1L;

    private static final float[] fWdU = new float[3];

    private static final float[] fAWdU = new float[3];

    private static final float[] fDdU = new float[3];

    private static final float[] fADdU = new float[3];

    private static final float[] fAWxDdU = new float[3];

    private static final float[] tempFa = new float[3];

    private static final float[] tempFb = new float[3];

    static private final Vector3f tempVa = new Vector3f();

    static private final Vector3f tempVb = new Vector3f();

    static private final Vector3f tempVc = new Vector3f();

    static private final Vector3f tempVd = new Vector3f();

    static private final Vector3f tempVe = new Vector3f();

    static private final Vector3f tempVf = new Vector3f();

    static private final Vector3f tempVg = new Vector3f();

    static private final Vector3f tempVh = new Vector3f();

    static private final Vector3f tempVi = new Vector3f();

    static private final Vector3f tempVj = new Vector3f();

    static private final Matrix3f tempMa = new Matrix3f();

    static private final Quaternion tempQa = new Quaternion();

    static private final Quaternion tempQb = new Quaternion();

    static private final Vector3f tempVk = new Vector3f();

    static private final Vector3f tempForword = new Vector3f(0, 0, 1);

    static private final Vector3f tempLeft = new Vector3f(1, 0, 0);

    static private final Vector3f tempUp = new Vector3f(0, 1, 0);

    public int[] checkPlanes = new int[6];

    /**
     * Creates a new OrientedBounding box.
     */
    public OrientedBoundingBox() {
        this("obb");
    }

    public OrientedBoundingBox(String name) {
        super(name);
        initCheckPlanes();
    }

    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale) {
        return transform(rotate, translate, scale, new OrientedBoundingBox());
    }

    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale, BoundingVolume store) {
        if (store == null || !(store instanceof OrientedBoundingBox)) {
            store = new OrientedBoundingBox();
        }

        OrientedBoundingBox toReturn = (OrientedBoundingBox) store;
        toReturn.extent.set(extent.x * scale.x, extent.y * scale.y, extent.z
                * scale.z);
        rotate.toRotationMatrix(tempMa);
        tempMa.mult(xAxis, toReturn.xAxis);
        tempMa.mult(yAxis, toReturn.yAxis);
        tempMa.mult(zAxis, toReturn.zAxis);
        tempMa.mult(center, toReturn.center);
        toReturn.center.multLocal(scale).addLocal(translate);
        toReturn.correctCorners = false;
        return toReturn;
    }

    public int whichSide(Plane plane) {
        float fRadius = FastMath.abs(extent.x * (plane.getNormal().dot(xAxis)))
                + FastMath.abs(extent.y * (plane.getNormal().dot(yAxis)))
                + FastMath.abs(extent.z * (plane.getNormal().dot(zAxis)));
        float fDistance = plane.pseudoDistance(center);
        if (fDistance <= -fRadius)
            return Plane.NEGATIVE_SIDE;
        else
            return Plane.NO_SIDE;
    }

    public void computeFromPoints(FloatBuffer points) {
        containAABB(points);
        // computeCorners();
        correctCorners = false;
    }

    /**
     * Calculates an AABB of the given point values for this OBB.
     * 
     * @param points
     *            The points this OBB should contain.
     */
    private void containAABB(FloatBuffer points) {
        if (points == null || points.capacity() <= 2) { // we need at least a 3 float vector
            return;
        }

        BufferUtils.populateFromBuffer(tempVa, points, 0);
        float minX = tempVa.x, minY = tempVa.y, minZ = tempVa.z;
        float maxX = tempVa.x, maxY = tempVa.y, maxZ = tempVa.z;

        for (int i = 1, len = points.capacity() / 3; i < len; i++) {
            BufferUtils.populateFromBuffer(tempVa, points, i);
            
            if (tempVa.x < minX)
                minX = tempVa.x;
            else if (tempVa.x >maxX)
                maxX = tempVa.x;

            if (tempVa.y < minY)
                minY = tempVa.y;
            else if (tempVa.y > maxY)
                maxY = tempVa.y;

            if (tempVa.z < minZ)
                minZ = tempVa.z;
            else if (tempVa.z > maxZ)
                maxZ = tempVa.z;
        }

        center.set(minX+maxX, minY+maxY, minZ+maxZ);
        center.multLocal(0.5f);

        extent.set(maxX - center.x, maxY - center.y, maxZ - center.z);

        xAxis.set(1, 0, 0);

        yAxis.set(0, 1, 0);

        zAxis.set(0, 0, 1);
    }

    public BoundingVolume merge(BoundingVolume volume) {
        return new OrientedBoundingBox().mergeLocal(volume);
    }

    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume == null)
            return this;
        if (volume instanceof OrientedBoundingBox) {
            return mergeOBB((OrientedBoundingBox) volume);
        } else if (volume instanceof BoundingBox) {
            return mergeAABB((BoundingBox) volume);
        } else if (volume instanceof BoundingSphere) {
            return mergeSphere((BoundingSphere) volume);
        } else
            return null;
    }

    private BoundingVolume mergeSphere(BoundingSphere volume) {
        BoundingSphere mergeSphere = (BoundingSphere) volume;
        if (!correctCorners)
            this.computeCorners();

        FloatBuffer buf = BufferUtils.createVector3Buffer(16);
        for (int i = 0; i < 8; i++) {
            buf.put(vectorStore[i].x);
            buf.put(vectorStore[i].y);
            buf.put(vectorStore[i].z);
        }
        buf.put(center.x+mergeSphere.radius).put(center.y+mergeSphere.radius).put(center.z+mergeSphere.radius);
        buf.put(center.x-mergeSphere.radius).put(center.y+mergeSphere.radius).put(center.z+mergeSphere.radius);
        buf.put(center.x+mergeSphere.radius).put(center.y-mergeSphere.radius).put(center.z+mergeSphere.radius);
        buf.put(center.x+mergeSphere.radius).put(center.y+mergeSphere.radius).put(center.z-mergeSphere.radius);
        buf.put(center.x-mergeSphere.radius).put(center.y-mergeSphere.radius).put(center.z+mergeSphere.radius);
        buf.put(center.x-mergeSphere.radius).put(center.y+mergeSphere.radius).put(center.z-mergeSphere.radius);
        buf.put(center.x+mergeSphere.radius).put(center.y-mergeSphere.radius).put(center.z-mergeSphere.radius);
        buf.put(center.x-mergeSphere.radius).put(center.y-mergeSphere.radius).put(center.z-mergeSphere.radius);
        containAABB(buf);
        correctCorners = false;
        return this;
    }

    private BoundingVolume mergeAABB(BoundingBox volume) {
        BoundingBox mergeBox = (BoundingBox) volume;
        if (!correctCorners)
            this.computeCorners();

        FloatBuffer buf = BufferUtils.createVector3Buffer(16);
        for (int i = 0; i < 8; i++) {
            buf.put(vectorStore[i].x);
            buf.put(vectorStore[i].y);
            buf.put(vectorStore[i].z);
        }
        buf.put(center.x+mergeBox.xExtent).put(center.y+mergeBox.yExtent).put(center.z+mergeBox.zExtent);
        buf.put(center.x-mergeBox.xExtent).put(center.y+mergeBox.yExtent).put(center.z+mergeBox.zExtent);
        buf.put(center.x+mergeBox.xExtent).put(center.y-mergeBox.yExtent).put(center.z+mergeBox.zExtent);
        buf.put(center.x+mergeBox.xExtent).put(center.y+mergeBox.yExtent).put(center.z-mergeBox.zExtent);
        buf.put(center.x-mergeBox.xExtent).put(center.y-mergeBox.yExtent).put(center.z+mergeBox.zExtent);
        buf.put(center.x-mergeBox.xExtent).put(center.y+mergeBox.yExtent).put(center.z-mergeBox.zExtent);
        buf.put(center.x+mergeBox.xExtent).put(center.y-mergeBox.yExtent).put(center.z-mergeBox.zExtent);
        buf.put(center.x-mergeBox.xExtent).put(center.y-mergeBox.yExtent).put(center.z-mergeBox.zExtent);
        containAABB(buf);
        correctCorners = false;
        return this;
    }

    private BoundingVolume mergeOBB(OrientedBoundingBox volume) {

        OrientedBoundingBox rkBox0 = this;
        OrientedBoundingBox rkBox1 = volume;

        // The first guess at the box center. This value will be updated later
        // after the input box vertices are projected onto axes determined by an
        // average of box axes.
        Vector3f kBoxCenter = (rkBox0.getCenter().add(rkBox1.getCenter(),
                tempVd)).multLocal(.5f);

        // A box's axes, when viewed as the columns of a matrix, form a rotation
        // matrix. The input box axes are converted to quaternions. The average
        // quaternion is computed, then normalized to unit length. The result is
        // the slerp of the two input quaternions with t-value of 1/2. The
        // result
        // is converted back to a rotation matrix and its columns are selected
        // as
        // the merged box axes.
        Quaternion kQ0 = tempQa, kQ1 = tempQb;

        tempMa.setColumn(0, rkBox0.getxAxis());
        tempMa.setColumn(1, rkBox0.getyAxis());
        tempMa.setColumn(2, rkBox0.getzAxis());
        kQ0.fromRotationMatrix(tempMa);

        tempMa.setColumn(0, rkBox1.getxAxis());
        tempMa.setColumn(1, rkBox1.getyAxis());
        tempMa.setColumn(2, rkBox1.getzAxis());
        kQ1.fromRotationMatrix(tempMa);

        if (kQ0.dot(kQ1) < 0.0f)
            kQ1.negate();

        Quaternion kQ = kQ0.addLocal(kQ1);
        float fInvLength = FastMath.invSqrt(kQ.dot(kQ));
        kQ.multLocal(fInvLength);
        Matrix3f kBoxaxis = kQ.toRotationMatrix(tempMa);

        // Project the input box vertices onto the merged-box axes. Each axis
        // D[i] containing the current center C has a minimum projected value
        // pmin[i] and a maximum projected value pmax[i]. The corresponding end
        // points on the axes are C+pmin[i]*D[i] and C+pmax[i]*D[i]. The point C
        // is not necessarily the midpoint for any of the intervals. The actual
        // box center will be adjusted from C to a point C' that is the midpoint
        // of each interval,
        // C' = C + sum_{i=0}^2 0.5*(pmin[i]+pmax[i])*D[i]
        // The box extents are
        // e[i] = 0.5*(pmax[i]-pmin[i])

        int i;
        float fDot = 0;
        Vector3f kDiff = tempVa;
        Vector3f kMin = tempVb;
        Vector3f kMax = tempVc;

        kMin.zero();
        kMax.zero();

        Vector3f kBoxXaxis = kBoxaxis.getColumn(0, tempVe);
        Vector3f kBoxYaxis = kBoxaxis.getColumn(1, tempVf);
        Vector3f kBoxZaxis = kBoxaxis.getColumn(2, tempVg);

        if (!rkBox0.correctCorners)
            rkBox0.computeCorners();
        for (i = 0; i < 8; i++) {
            rkBox0.vectorStore[i].subtract(kBoxCenter, kDiff);

            fDot = kDiff.dot(kBoxXaxis);
            if (fDot > kMax.x)
                kMax.x = fDot;
            else if (fDot < kMin.x)
                kMin.x = fDot;

            fDot = kDiff.dot(kBoxYaxis);
            if (fDot > kMax.y)
                kMax.y = fDot;
            else if (fDot < kMin.y)
                kMin.y = fDot;

            fDot = kDiff.dot(kBoxZaxis);
            if (fDot > kMax.z)
                kMax.z = fDot;
            else if (fDot < kMin.z)
                kMin.z = fDot;

        }

        if (!rkBox1.correctCorners)
            rkBox1.computeCorners();
        for (i = 0; i < 8; i++) {
            rkBox1.vectorStore[i].subtract(kBoxCenter, kDiff);

            fDot = kDiff.dot(kBoxXaxis);
            if (fDot > kMax.x)
                kMax.x = fDot;
            else if (fDot < kMin.x)
                kMin.x = fDot;

            fDot = kDiff.dot(kBoxYaxis);
            if (fDot > kMax.y)
                kMax.y = fDot;
            else if (fDot < kMin.y)
                kMin.y = fDot;

            fDot = kDiff.dot(kBoxZaxis);
            if (fDot > kMax.z)
                kMax.z = fDot;
            else if (fDot < kMin.z)
                kMin.z = fDot;
        }

        xAxis.set(kBoxXaxis);
        yAxis.set(kBoxYaxis);
        zAxis.set(kBoxZaxis);

        this.extent.x = .5f * (kMax.x - kMin.x);
        kBoxCenter.addLocal(this.xAxis.mult(.5f * (kMax.x + kMin.x), tempVe));

        this.extent.y = .5f * (kMax.y - kMin.y);
        kBoxCenter.addLocal(this.yAxis.mult(.5f * (kMax.y + kMin.y), tempVe));

        this.extent.z = .5f * (kMax.z - kMin.z);
        kBoxCenter.addLocal(this.zAxis.mult(.5f * (kMax.z + kMin.z), tempVe));

        this.center.set(kBoxCenter);

        this.correctCorners = false;
        return this;
    }

    public Object clone(BoundingVolume store) {
        if (store == null)
            store = new OrientedBoundingBox();
        OrientedBoundingBox toReturn = (OrientedBoundingBox) store;
        toReturn.extent.set(extent);
        toReturn.xAxis.set(xAxis);
        toReturn.yAxis.set(yAxis);
        toReturn.zAxis.set(zAxis);
        toReturn.center.set(center);
        for (int i = 0; i < checkPlanes.length; i++)
            toReturn.checkPlanes[i] = checkPlanes[i];
        toReturn.correctCorners = false;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.bounding.BoundingVolume)
     */
    public boolean intersects(BoundingVolume bv) {
        if (bv == null)
            return false;
        else
            return bv.intersectsOrientedBoundingBox(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsSphere(com.jme.bounding.BoundingSphere)
     */
    public boolean intersectsSphere(BoundingSphere bs) {
        tempVa.set(bs.getCenter()).subtractLocal(getCenter());
        tempMa.fromAxes(getxAxis(), getyAxis(), getzAxis());

        tempMa.mult(tempVa, tempVb);

        if (FastMath.abs(tempVb.x) < bs.getRadius() + getExtent().x
                && FastMath.abs(tempVb.y) < bs.getRadius() + getExtent().y
                && FastMath.abs(tempVb.z) < bs.getRadius() + getExtent().z)
            return true;

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsBoundingBox(com.jme.bounding.BoundingBox)
     */
    public boolean intersectsBoundingBox(BoundingBox bb) {
        // Cutoff for cosine of angles between box axes. This is used to catch
        // the cases when at least one pair of axes are parallel. If this
        // happens,
        // there is no need to test for separation along the Cross(A[i],B[j])
        // directions.
        float cutoff = 0.999999f;
        boolean parallelPairExists = false;
        int i;

        // convenience variables
        Vector3f akA[] = new Vector3f[] { getxAxis(), getyAxis(), getzAxis() };
        Vector3f[] akB = new Vector3f[] { tempForword, tempLeft, tempUp };
        Vector3f afEA = getExtent();
        Vector3f afEB = tempVk.set(bb.xExtent, bb.yExtent, bb.zExtent);

        // compute difference of box centers, D = C1-C0
        Vector3f kD = bb.getCenter().subtract(getCenter(), tempVa);

        float[][] aafC = { fWdU, fAWdU, fDdU };

        float[][] aafAbsC = { fADdU, fAWxDdU, tempFa };

        float[] afAD = tempFb;
        float fR0, fR1, fR; // interval radii and distance between centers
        float fR01; // = R0 + R1

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
        fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z
                * aafAbsC[0][2];
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
        fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z
                * aafAbsC[1][2];
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
        fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z
                * aafAbsC[2][2];
        fR01 = afEA.z + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B0
        fR = FastMath.abs(akB[0].dot(kD));
        fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z
                * aafAbsC[2][0];
        fR01 = fR0 + afEB.x;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B1
        fR = FastMath.abs(akB[1].dot(kD));
        fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z
                * aafAbsC[2][1];
        fR01 = fR0 + afEB.y;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B2
        fR = FastMath.abs(akB[2].dot(kD));
        fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z
                * aafAbsC[2][2];
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

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
     */
    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
        // Cutoff for cosine of angles between box axes. This is used to catch
        // the cases when at least one pair of axes are parallel. If this
        // happens,
        // there is no need to test for separation along the Cross(A[i],B[j])
        // directions.
        float cutoff = 0.999999f;
        boolean parallelPairExists = false;
        int i;

        // convenience variables
        Vector3f akA[] = new Vector3f[] { getxAxis(), getyAxis(), getzAxis() };
        Vector3f[] akB = new Vector3f[] { obb.getxAxis(), obb.getyAxis(),
                obb.getzAxis() };
        Vector3f afEA = getExtent();
        Vector3f afEB = obb.getExtent();

        // compute difference of box centers, D = C1-C0
        Vector3f kD = obb.getCenter().subtract(getCenter(), tempVa);

        float[][] aafC = { fWdU, fAWdU, fDdU };

        float[][] aafAbsC = { fADdU, fAWxDdU, tempFa };

        float[] afAD = tempFb;
        float fR0, fR1, fR; // interval radii and distance between centers
        float fR01; // = R0 + R1

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
        fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z
                * aafAbsC[0][2];
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
        fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z
                * aafAbsC[1][2];
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
        fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z
                * aafAbsC[2][2];
        fR01 = afEA.z + fR1;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B0
        fR = FastMath.abs(akB[0].dot(kD));
        fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z
                * aafAbsC[2][0];
        fR01 = fR0 + afEB.x;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B1
        fR = FastMath.abs(akB[1].dot(kD));
        fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z
                * aafAbsC[2][1];
        fR01 = fR0 + afEB.y;
        if (fR > fR01) {
            return false;
        }

        // axis C0+t*B2
        fR = FastMath.abs(akB[2].dot(kD));
        fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z
                * aafAbsC[2][2];
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

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOBB2(com.jme.bounding.OBB2)
     */
    public boolean intersectsOBB2(OBB2 obb) {
        return obb.intersectsOrientedBoundingBox(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.math.Ray)
     */
    public boolean intersects(Ray ray) {
        float rhs;
        Vector3f diff = ray.origin.subtract(getCenter(tempVb), tempVa);

        fWdU[0] = ray.getDirection().dot(getxAxis());
        fAWdU[0] = FastMath.abs(fWdU[0]);
        fDdU[0] = diff.dot(getxAxis());
        fADdU[0] = FastMath.abs(fDdU[0]);
        if (fADdU[0] > getExtent().x && fDdU[0] * fWdU[0] >= 0.0) {
            return false;
        }

        fWdU[1] = ray.getDirection().dot(getyAxis());
        fAWdU[1] = FastMath.abs(fWdU[1]);
        fDdU[1] = diff.dot(getyAxis());
        fADdU[1] = FastMath.abs(fDdU[1]);
        if (fADdU[1] > getExtent().y && fDdU[1] * fWdU[1] >= 0.0) {
            return false;
        }

        fWdU[2] = ray.getDirection().dot(getzAxis());
        fAWdU[2] = FastMath.abs(fWdU[2]);
        fDdU[2] = diff.dot(getzAxis());
        fADdU[2] = FastMath.abs(fDdU[2]);
        if (fADdU[2] > getExtent().z && fDdU[2] * fWdU[2] >= 0.0) {
            return false;
        }

        Vector3f wCrossD = ray.getDirection().cross(diff, tempVb);

        fAWxDdU[0] = FastMath.abs(wCrossD.dot(getxAxis()));
        rhs = getExtent().y * fAWdU[2] + getExtent().z * fAWdU[1];
        if (fAWxDdU[0] > rhs) {
            return false;
        }

        fAWxDdU[1] = FastMath.abs(wCrossD.dot(getyAxis()));
        rhs = getExtent().x * fAWdU[2] + getExtent().z * fAWdU[0];
        if (fAWxDdU[1] > rhs) {
            return false;
        }

        fAWxDdU[2] = FastMath.abs(wCrossD.dot(getzAxis()));
        rhs = getExtent().x * fAWdU[1] + getExtent().y * fAWdU[0];
        if (fAWxDdU[2] > rhs) {
            return false;

        }

        return true;
    }
}