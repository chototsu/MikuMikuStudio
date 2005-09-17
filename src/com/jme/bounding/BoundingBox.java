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
import com.jme.scene.shape.Box;
import com.jme.util.geom.BufferUtils;

/**
 * <code>BoundingBox</code> defines an axis-aligned cube that defines a
 * container for a group of vertices of a particular piece of geometry. This box
 * defines a center and extents from that center along the x, y and z axis. <br>
 * <br>
 * A typical usage is to allow the class define the center and radius by calling
 * either <code>containAABB</code> or <code>averagePoints</code>. A call to
 * <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
 * 
 * @author Joshua Slack
 * @version $Id: BoundingBox.java,v 1.30 2005-09-17 15:09:34 renanse Exp $
 */
public class BoundingBox extends Box implements BoundingVolume {

    private static final long serialVersionUID = 1L;

    /** These define the array of planes that are check during view culling. */
    public int[] checkPlanes = new int[6];

    private Vector3f minPnt = new Vector3f();

    private Vector3f maxPnt = new Vector3f();

    private float oldXExtent, oldYExtent, oldZExtent;

    private Vector3f oldCenter = new Vector3f();

    private Vector3f origCenter = new Vector3f();

    private Vector3f origExtent = new Vector3f();

    private static final Matrix3f tempMat = new Matrix3f();

    private static final Vector3f tempVa = new Vector3f();

    private static final Vector3f tempVb = new Vector3f();

    /**
     * Default contstructor instantiates a new <code>BoundingBox</code>
     * object.
     */
    public BoundingBox() {
        super("aabb");
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with
     * given specs.
     */
    public BoundingBox(String name) {
        super(name);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with
     * given specs.
     */
    public BoundingBox(Vector3f center, float xExtent, float yExtent,
            float zExtent) {
        super("aabb", center, xExtent, yExtent, zExtent);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with
     * given specs.
     */
    public BoundingBox(String name, Vector3f center, float xExtent,
            float yExtent, float zExtent) {
        super(name, center, xExtent, yExtent, zExtent);
        initCheckPlanes();
    }
    
    public int getType() {
    	return BoundingVolume.BOUNDING_BOX;
    }

    /**
     * Not to be called by users. This function initializes the check planes for
     * the AABB.
     */
    public void initCheckPlanes() {
        checkPlanes[0] = 0;
        checkPlanes[1] = 1;
        checkPlanes[2] = 2;
        checkPlanes[3] = 3;
        checkPlanes[4] = 4;
        checkPlanes[5] = 5;
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Box from a given
     * set of points. It uses the <code>containAABB</code> method as default.
     * 
     * @param points
     *            the points to contain.
     */
    public void computeFromPoints(FloatBuffer points) {
        containAABB(points);
    }

    /**
     * <code>containAABB</code> creates a minimum-volume axis-aligned bounding
     * box of the points, then selects the smallest enclosing sphere of the box
     * with the sphere centered at the boxes center.
     * 
     * @param points
     *            the list of points.
     */
    public void containAABB(FloatBuffer points) {
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

        origExtent.x = xExtent = maxX - center.x;
        origExtent.y = yExtent = maxY - center.y;
        origExtent.z = zExtent = maxZ - center.z;
        origCenter.set(center);
    }

    /**
     * <code>transform</code> modifies the center of the box to reflect the
     * change made via a rotation, translation and scale.
     * 
     * @param rotate
     *            the rotation change.
     * @param translate
     *            the translation change.
     * @param scale
     *            the size change.
     */
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale) {
        return this.transform(rotate, translate, scale, null);
    }

    /**
     * <code>transform</code> modifies the center of the box to reflect the
     * change made via a rotation, translation and scale.
     * 
     * @param rotate
     *            the rotation change.
     * @param translate
     *            the translation change.
     * @param scale
     *            the size change.
     * @param store
     *            box to store result in
     */
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale, BoundingVolume store) {

        BoundingBox box;
        if (store == null || store.getType() != BoundingVolume.BOUNDING_BOX) {
            box = new BoundingBox(new Vector3f(0, 0, 0), 1, 1, 1);
        } else {
            box = (BoundingBox) store;
        }

        box.origCenter.set(origCenter);
        rotate.mult(origCenter, box.center);
        box.center.multLocal(scale).addLocal(translate);

        Matrix3f transMatrix = tempMat;
        transMatrix.set(rotate);
        // Make the rotation matrix all positive to get the maximum x/y/z extent
        if (transMatrix.m00 < 0)
            transMatrix.m00 *= -1;
        if (transMatrix.m01 < 0)
            transMatrix.m01 *= -1;
        if (transMatrix.m02 < 0)
            transMatrix.m02 *= -1;
        if (transMatrix.m10 < 0)
            transMatrix.m10 *= -1;
        if (transMatrix.m11 < 0)
            transMatrix.m11 *= -1;
        if (transMatrix.m12 < 0)
            transMatrix.m12 *= -1;
        if (transMatrix.m20 < 0)
            transMatrix.m20 *= -1;
        if (transMatrix.m21 < 0)
            transMatrix.m21 *= -1;
        if (transMatrix.m22 < 0)
            transMatrix.m22 *= -1;

        // (ab)use origExtent to do the multiplication resulting in the biggest
        // extent
        // values for a rotation.
        transMatrix.mult(origExtent, box.origExtent);
        // Assign the biggest rotations after scales.
        box.xExtent = box.origExtent.x * scale.x;
        box.yExtent = box.origExtent.y * scale.y;
        box.zExtent = box.origExtent.z * scale.z;
        // reset origExtent back to what it should be.
        box.origExtent.set(origExtent);

        return box;
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * 
     * @param plane
     *            the plane to check against.
     */
    public int whichSide(Plane plane) {
        float radius = FastMath.abs(xExtent * plane.normal.x)
                + FastMath.abs(yExtent * plane.normal.y)
                + FastMath.abs(zExtent * plane.normal.z);

        float distance = plane.pseudoDistance(center);

        if (distance <= -radius) {
            return Plane.NEGATIVE_SIDE;
        } else if (distance >= radius) {
            return Plane.POSITIVE_SIDE;
        } else {
            return Plane.NO_SIDE;
        }
    }

    /**
     * <code>merge</code> combines this sphere with a second bounding sphere.
     * This new sphere contains both bounding spheres and is returned.
     * 
     * @param volume
     *            the sphere to combine with this sphere.
     * @return the new sphere
     */
    public BoundingVolume merge(BoundingVolume volume) {
        if (volume == null) {
            return this;
        }
        
        switch (volume.getType()) {
        case BoundingVolume.BOUNDING_BOX: {
        	BoundingBox vBox = (BoundingBox) volume;
            return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent,
                    new BoundingBox(new Vector3f(0, 0, 0), 0, 0, 0));
        }
        
        case BoundingVolume.BOUNDING_SPHERE: {
        	BoundingSphere vSphere = (BoundingSphere) volume;
            return merge(vSphere.center, vSphere.radius, vSphere.radius,
                    vSphere.radius, new BoundingBox(new Vector3f(0, 0, 0), 0,
                            0, 0));
        }
        
        case BoundingVolume.BOUNDING_OBB: {
        	OrientedBoundingBox box = (OrientedBoundingBox) volume;
            BoundingBox rVal = (BoundingBox) this.clone(null);
            return rVal.mergeOBB(box);
        }
        
        default:
        	return null;
        }
    }

    /**
     * <code>mergeLocal</code> combines this sphere with a second bounding
     * sphere locally. Altering this sphere to contain both the original and the
     * additional sphere volumes;
     * 
     * @param volume
     *            the sphere to combine with this sphere.
     * @return this
     */
    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if (volume == null) {
            return this;
        }
        
        switch (volume.getType()) {
        case BoundingVolume.BOUNDING_BOX: {
        	BoundingBox vBox = (BoundingBox) volume;
            return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent,
                    this);
        }
        
        case BoundingVolume.BOUNDING_SPHERE: {
        	BoundingSphere vSphere = (BoundingSphere) volume;
            return merge(vSphere.center, vSphere.radius, vSphere.radius,
                    vSphere.radius, this);
        }
        
        case BoundingVolume.BOUNDING_OBB: {
        	return mergeOBB((OrientedBoundingBox) volume);
        }
        
        default:
        	return null;
        }
    }

    /**
     * Merges this AABB with the given OBB.
     * 
     * @param volume
     *            the OBB to merge this AABB with.
     * @return This AABB extended to fit the given OBB.
     */
    private BoundingBox mergeOBB(OrientedBoundingBox volume) {
        if (!volume.correctCorners)
            volume.computeCorners();

        Vector3f min = tempVa.set(center.x - xExtent, center.y - yExtent,
                center.z - zExtent);
        Vector3f max = tempVb.set(center.x + xExtent, center.y + yExtent,
                center.z + zExtent);

        for (int i = 1; i < volume.vectorStore.length; i++) {
            Vector3f temp = volume.vectorStore[i];
            if (temp.x < min.x)
                min.x = temp.x;
            else if (temp.x > max.x)
                max.x = temp.x;

            if (temp.y < min.y)
                min.y = temp.y;
            else if (temp.y > max.y)
                max.y = temp.y;

            if (temp.z < min.z)
                min.z = temp.z;
            else if (temp.z > max.z)
                max.z = temp.z;
        }

        center.set(min.addLocal(max));
        center.multLocal(0.5f);

        origExtent.x = xExtent = max.x - center.x;
        origExtent.y = yExtent = max.y - center.y;
        origExtent.z = zExtent = max.z - center.z;
        origCenter.set(center);
        return this;
    }

    /**
     * 
     * <code>merge</code> combines this bounding box with another box which is
     * defined by the center, x, y, z extents.
     * 
     * @param boxCenter
     *            the center of the box to merge with
     * @param boxX
     *            the x extent of the box to merge with.
     * @param boxY
     *            the y extent of the box to merge with.
     * @param boxZ
     *            the z extent of the box to merge with.
     * @param rVal
     *            the resulting merged box.
     * @return the resulting merged box.
     */
    private BoundingBox merge(Vector3f boxCenter, float boxX, float boxY,
            float boxZ, BoundingBox rVal) {

        minPnt.x = center.x - xExtent;
        if (minPnt.x > boxCenter.x - boxX)
            minPnt.x = boxCenter.x - boxX;
        minPnt.y = center.y - yExtent;
        if (minPnt.y > boxCenter.y - boxY)
            minPnt.y = boxCenter.y - boxY;
        minPnt.z = center.z - zExtent;
        if (minPnt.z > boxCenter.z - boxZ)
            minPnt.z = boxCenter.z - boxZ;

        maxPnt.x = center.x + xExtent;
        if (maxPnt.x < boxCenter.x + boxX)
            maxPnt.x = boxCenter.x + boxX;
        maxPnt.y = center.y + yExtent;
        if (maxPnt.y < boxCenter.y + boxY)
            maxPnt.y = boxCenter.y + boxY;
        maxPnt.z = center.z + zExtent;
        if (maxPnt.z < boxCenter.z + boxZ)
            maxPnt.z = boxCenter.z + boxZ;

        rVal.setData(minPnt, maxPnt, false);

        return rVal;
    }

    /**
     * <code>clone</code> creates a new BoundingBox object containing the same
     * data as this one.
     * 
     * @param store
     *            where to store the cloned information. if null or wrong class,
     *            a new store is created.
     * @return the new BoundingBox
     */
    public Object clone(BoundingVolume store) {
        if (store != null && store.getType() == BoundingVolume.BOUNDING_BOX) {
            BoundingBox rVal = (BoundingBox) store;
            rVal.center.set(center);
            rVal.xExtent = xExtent;
            rVal.yExtent = yExtent;
            rVal.zExtent = zExtent;
            rVal.checkPlanes[0] = checkPlanes[0];
            rVal.checkPlanes[1] = checkPlanes[1];
            rVal.checkPlanes[2] = checkPlanes[2];
            rVal.checkPlanes[3] = checkPlanes[3];
            rVal.checkPlanes[4] = checkPlanes[4];
            rVal.checkPlanes[5] = checkPlanes[5];
            rVal.origCenter.set(origCenter);
            rVal.origExtent.set(origExtent);
            return rVal;
        } else {
            BoundingBox rVal = new BoundingBox(getName() + "_clone",
                    (center != null ? (Vector3f) center.clone() : null),
                    xExtent, yExtent, zExtent);
            rVal.origCenter.set(origCenter);
            rVal.origExtent.set(origExtent);
            return rVal;
        }
    }

    /**
     * <code>getCheckPlane</code> returns a specific check plane. This plane
     * identitifies the previous value of the visibility check.
     */
    public int getCheckPlane(int index) {
        return checkPlanes[index];
    }

    /**
     * <code>setCheckPlane</code> indentifies the value of one of the spheres
     * checked planes. That is what plane of the view frustum has been checked
     * for intersection.
     */
    public void setCheckPlane(int index, int value) {
        checkPlanes[index] = value;
    }

    /**
     * <code>recomputeMesh</code> regenerates the <code>BoundingBox</code>
     * based on new model information.
     */
    public void recomputeMesh() {
        if (!center.equals(oldCenter) || xExtent != oldXExtent
                || yExtent != oldYExtent || zExtent != oldZExtent) {
            setData(center, xExtent, yExtent, zExtent, true);
            oldXExtent = xExtent;
            oldYExtent = yExtent;
            oldZExtent = zExtent;
            oldCenter.set(center.x, center.y, center.z);
        }
    }

    /**
     * Find the distance from the center of this Bounding Volume to the given
     * point.
     * 
     * @param point
     *            The point to get the distance to
     * @return distance
     */
    public float distanceTo(Vector3f point) {
        return center.distance(point);
    }

    /**
     * Stores the current center of this BoundingBox into the store vector.
     * 
     * @param store
     *            The vector to store the center into.
     * @return The store vector, after setting it's contents to the center
     */
    public Vector3f getCenter(Vector3f store) {
        store.set(center);
        return store;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: RRR.SSSS Center: <Vector>".
     * 
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingBox [Center: " + center + "  xExtent: "
                + xExtent + "  yExtent: " + yExtent + "  zExtent: " + zExtent
                + "]";
    }

    /**
     * Returns the original, unrotated center of the bounding box.
     * 
     * @return The box's original center.
     */
    public Vector3f getOrigCenter() {
        return origCenter;
    }

    /**
     * Sets the bounding box's original center. In most cases, users will simply
     * want to use computefrompoints
     * 
     * @param origCenter
     *            New original center
     * @see #computeFromPoints(com.jme.math.Vector3f[])
     */
    public void setOrigCenter(Vector3f origCenter) {
        this.origCenter = origCenter;
    }

    /**
     * Gets the original, unrotated extent of the box.
     * 
     * @return The box's original extent.
     */
    public Vector3f getOrigExtent() {
        return origExtent;
    }

    /**
     * Sets the box's original extent. In most cases, users will simply want to
     * use computefrompoints.
     * 
     * @param origExtent
     *            The new extent.
     * @see #computeFromPoints(com.jme.math.Vector3f[])
     */
    public void setOrigExtent(Vector3f origExtent) {
        this.origExtent = origExtent;
    }

    /**
     * intersects determines if this Bounding Box intersects with another given
     * bounding volume. If so, true is returned, otherwise, false is returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.bounding.BoundingVolume)
     */
    public boolean intersects(BoundingVolume bv) {
        if (bv == null)
            return false;
        else
            return bv.intersectsBoundingBox(this);
    }

    /**
     * determines if this bounding box intersects a given bounding sphere.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsSphere(com.jme.bounding.BoundingSphere)
     */
    public boolean intersectsSphere(BoundingSphere bs) {

        if (FastMath.abs(center.x - bs.getCenter().x) < bs.getRadius()
                + xExtent
                && FastMath.abs(center.y - bs.getCenter().y) < bs.getRadius()
                        + yExtent
                && FastMath.abs(center.z - bs.getCenter().z) < bs.getRadius()
                        + zExtent)
            return true;

        return false;
    }

    /**
     * determines if this bounding box intersects a given bounding box. If the
     * two boxes intersect in any way, true is returned. Otherwise, false is
     * returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsBoundingBox(com.jme.bounding.BoundingBox)
     */
    public boolean intersectsBoundingBox(BoundingBox bb) {
        if (center.x + xExtent < bb.center.x - bb.xExtent
                || center.x - xExtent > bb.center.x + bb.xExtent)
            return false;
        else if (center.y + yExtent < bb.center.y - bb.yExtent
                || center.y - yExtent > bb.center.y + bb.yExtent)
            return false;
        else if (center.z + zExtent < bb.center.z - bb.zExtent
                || center.z - zExtent > bb.center.z + bb.zExtent)
            return false;
        else
            return true;
    }

    /**
     * 
     * determines if this bounding box intersects with a given oriented bounding
     * box.
     * 
     * NOTE: Not currently supported, false always returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
     */
    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
        return obb.intersectsBoundingBox(this);
    }

    /**
     * determines if this bounding box intersects with a given OBB2 bounding.
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOBB2(com.jme.bounding.OBB2)
     */
    public boolean intersectsOBB2(OBB2 obb) {
        return obb.intersectsBoundingBox(this);
    }

    /**
     * determines if this bounding box intersects with a given ray object. If an
     * intersection has occurred, true is returned, otherwise false is returned.
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.math.Ray)
     */
    public boolean intersects(Ray ray) {
        Vector3f diff = tempVa.set(ray.origin).subtractLocal(center);
        // convert ray to box coordinates
        Vector3f direction = tempVb.set(ray.direction.x, ray.direction.y,
                ray.direction.z);
        float[] t = new float[2];
        t[0] = 0f;
        t[1] = Float.POSITIVE_INFINITY;
        float[] extents = { xExtent, yExtent, zExtent };
        return findIntersection(diff, direction, extents, t);
    }

    /**
     * <code>clip</code> determines if a line segment intersects the current
     * test plane.
     * 
     * @param denom
     *            the denominator of the line segment.
     * @param numer
     *            the numerator of the line segment.
     * @param t
     *            test values of the plane.
     * @return true if the line segment intersects the plane, false otherwise.
     */
    private boolean clip(float denom, float numer, float[] t) {
        // Return value is 'true' if line segment intersects the current test
        // plane. Otherwise 'false' is returned in which case the line segment
        // is entirely clipped.
        if (denom > 0.0f) {
            if (numer > denom * t[1])
                return false;
            if (numer > denom * t[0])
                t[0] = numer / denom;
            return true;
        } else if (denom < 0.0f) {
            if (numer > denom * t[0])
                return false;
            if (numer > denom * t[1])
                t[1] = numer / denom;
            return true;
        } else {
            return numer <= 0.0;
        }
    }

    /**
     * <code>findIntersection</code> determines if any of the planes of the
     * box are intersected by a ray (defined by an origin and direction).
     * 
     * @param origin
     *            the origin of the ray.
     * @param direction
     *            the direction of the ray.
     * @param extent
     *            the extents of the box.
     * @param t
     *            the plane intersection values of the box.
     * @return true if an intersection occurs, false otherwise.
     */
    private boolean findIntersection(Vector3f origin, Vector3f direction,
            float[] extent, float[] t) {
        float saveT0 = t[0], saveT1 = t[1];
        boolean notEntirelyClipped = clip(+direction.x, -origin.x - extent[0],
                t)
                && clip(-direction.x, +origin.x - extent[0], t)
                && clip(+direction.y, -origin.y - extent[1], t)
                && clip(-direction.y, +origin.y - extent[1], t)
                && clip(+direction.z, -origin.z - extent[2], t)
                && clip(-direction.z, +origin.z - extent[2], t);
        return notEntirelyClipped && (t[0] != saveT0 || t[1] != saveT1);
    }
}