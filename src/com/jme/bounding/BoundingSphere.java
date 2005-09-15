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
import java.util.logging.Level;

import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;
import com.jme.util.LoggingSystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>BoundingSphere</code> defines a sphere that defines a container for a
 * group of vertices of a particular piece of geometry. This sphere defines a
 * radius and a center. <br>
 * <br>
 * A typical usage is to allow the class define the center and radius by calling
 * either <code>containAABB</code> or <code>averagePoints</code>. A call to
 * <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
 * 
 * @author Mark Powell
 * @version $Id: BoundingSphere.java,v 1.28 2005-09-15 19:27:13 renanse Exp $
 */
public class BoundingSphere extends Sphere implements BoundingVolume {

    public int[] checkPlanes = new int[6];

    private float oldRadius;

    private Vector3f oldCenter = new Vector3f();

    // NOTE: To avoid numerical inaccuracies
    static final private float radiusEpsilon = 1 + 1e-5f;

    private static final long serialVersionUID = 1L;

    private static final Vector3f tempVeca = new Vector3f();

    /**
     * Default contstructor instantiates a new <code>BoundingSphere</code>
     * object.
     */
    public BoundingSphere() {
        this("bsphere");
    }

    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * 
     * @param radius
     *            the radius of the sphere.
     * @param center
     *            the center of the sphere.
     */
    public BoundingSphere(float radius, Vector3f center) {
        super("bsphere", center, 10, 10, radius);
        initCheckPlanes();
    }

    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * 
     * @param radius
     *            the radius of the sphere.
     * @param center
     *            the center of the sphere.
     */
    public BoundingSphere(String name, float radius, Vector3f center) {
        super(name, center, 10, 10, radius);
        initCheckPlanes();
    }

    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * 
     * @param name
     *            the name of this sphere object
     */
    public BoundingSphere(String name) {
        super(name);
        initCheckPlanes();
    }

    public void initCheckPlanes() {
        checkPlanes[0] = 0;
        checkPlanes[1] = 1;
        checkPlanes[2] = 2;
        checkPlanes[3] = 3;
        checkPlanes[4] = 4;
        checkPlanes[5] = 5;
    }

    /**
     * <code>getRadius</code> returns the radius of the bounding sphere.
     * 
     * @return the radius of the bounding sphere.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * <code>getCenter</code> returns the center of the bounding sphere.
     * 
     * @return the center of the bounding sphere.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * <code>setRadius</code> sets the radius of this bounding sphere.
     * 
     * @param radius
     *            the new radius of the bounding sphere.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * <code>setCenter</code> sets the center of the bounding sphere.
     * 
     * @param center
     *            the new center of the bounding sphere.
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Sphere from a
     * given set of points. It uses the <code>containAABB</code> method as
     * default.
     * 
     * @param points
     *            the points to contain.
     */
    public void computeFromPoints(FloatBuffer points) {
        calcWelzl(points);
    }

    /**
     * Calculates a minimum bounding sphere for the set of points. The algorithm
     * was originally found at
     * http://www.flipcode.com/cgi-bin/msg.cgi?showThread=COTD-SmallestEnclosingSpheres&forum=cotd&id=-1
     * in C++ and translated to java by Cep21
     * 
     * @param points
     *            The points to calculate the minimum bounds from.
     */
    public void calcWelzl(FloatBuffer points) {
        if (center == null)
            center = new Vector3f();
        FloatBuffer buf = BufferUtils.createFloatBuffer(points.capacity());
        points.rewind();
        buf.put(points);
        buf.flip();
        recurseMini(buf, buf.capacity() / 3, 0, 0);
    }

    private static Vector3f tempA = new Vector3f(), tempB = new Vector3f(), tempC = new Vector3f(), tempD = new Vector3f();
    /**
     * Used from calcWelzl. This function recurses to calculate a minimum
     * bounding sphere a few points at a time.
     * 
     * @param points
     *            The array of points to look through.
     * @param p
     *            The size of the list to be used.
     * @param b
     *            The number of points currently considering to include with the
     *            sphere.
     * @param ap
     *            A variable simulating pointer arithmatic from C++, and offset
     *            in <code>points</code>.
     */
    private void recurseMini(FloatBuffer points, int p, int b, int ap) {
        switch (b) {
        case 0:
            this.radius = 0;
            this.center.set(0, 0, 0);
            break;
        case 1:
            this.radius = 1 - radiusEpsilon;
            BufferUtils.populateFromBuffer(center, points, ap-1);
            break;
        case 2:
            BufferUtils.populateFromBuffer(tempA, points, ap-1);
            BufferUtils.populateFromBuffer(tempB, points, ap-2);
            setSphere(tempA, tempB);
            break;
        case 3:
            BufferUtils.populateFromBuffer(tempA, points, ap-1);
            BufferUtils.populateFromBuffer(tempB, points, ap-2);
            BufferUtils.populateFromBuffer(tempC, points, ap-3);
            setSphere(tempA, tempB, tempC);
            break;
        case 4:
            BufferUtils.populateFromBuffer(tempA, points, ap-1);
            BufferUtils.populateFromBuffer(tempB, points, ap-2);
            BufferUtils.populateFromBuffer(tempC, points, ap-3);
            BufferUtils.populateFromBuffer(tempD, points, ap-4);
            setSphere(tempA, tempB, tempC, tempD);
            return;
        }
        for (int i = 0; i < p; i++) {
            BufferUtils.populateFromBuffer(tempA, points, i+ap);
            if (tempA.distanceSquared(center) - radius * radius > radiusEpsilon - 1) {
                for (int j = i; j > 0; j--) {
                    BufferUtils.populateFromBuffer(tempB, points, j - 1 + ap);
                    BufferUtils.setInBuffer(tempB, points, j + ap);
                }
                BufferUtils.setInBuffer(tempA, points, ap);
                recurseMini(points, i, b + 1, ap + 1);
            }
        }
    }

    /**
     * Calculates the minimum bounding sphere of 4 points. Used in welzl's
     * algorithm.
     * 
     * @param O
     *            The 1st point inside the sphere.
     * @param A
     *            The 2nd point inside the sphere.
     * @param B
     *            The 3rd point inside the sphere.
     * @param C
     *            The 4th point inside the sphere.
     * @see #calcWelzl(com.jme.math.Vector3f[])
     */
    private void setSphere(Vector3f O, Vector3f A, Vector3f B, Vector3f C) {
        Vector3f a = A.subtract(O);
        Vector3f b = B.subtract(O);
        Vector3f c = C.subtract(O);

        float Denominator = 2.0f * (a.x * (b.y * c.z - c.y * b.z) - b.x
                * (a.y * c.z - c.y * a.z) + c.x * (a.y * b.z - b.y * a.z));
        if (Denominator == 0) {
            center.set(0, 0, 0);
            radius = 0;
        } else {
            Vector3f o = a.cross(b).multLocal(c.lengthSquared()).addLocal(
                    c.cross(a).multLocal(b.lengthSquared())).addLocal(
                    b.cross(c).multLocal(a.lengthSquared())).divideLocal(
                    Denominator);

            radius = o.length() * radiusEpsilon;
            O.add(o, center);
        }
    }

    /**
     * Calculates the minimum bounding sphere of 3 points. Used in welzl's
     * algorithm.
     * 
     * @param O
     *            The 1st point inside the sphere.
     * @param A
     *            The 2nd point inside the sphere.
     * @param B
     *            The 3rd point inside the sphere.
     * @see #calcWelzl(com.jme.math.Vector3f[])
     */
    private void setSphere(Vector3f O, Vector3f A, Vector3f B) {
        Vector3f a = A.subtract(O);
        Vector3f b = B.subtract(O);
        Vector3f acrossB = a.cross(b);

        float Denominator = 2.0f * acrossB.dot(acrossB);

        if (Denominator == 0) {
            center.set(0, 0, 0);
            radius = 0;
        } else {

            Vector3f o = acrossB.cross(a).multLocal(b.lengthSquared())
                    .addLocal(b.cross(acrossB).multLocal(a.lengthSquared()))
                    .divideLocal(Denominator);
            radius = o.length() * radiusEpsilon;
            O.add(o, center);
        }
    }

    /**
     * Calculates the minimum bounding sphere of 2 points. Used in welzl's
     * algorithm.
     * 
     * @param O
     *            The 1st point inside the sphere.
     * @param A
     *            The 2nd point inside the sphere.
     * @see #calcWelzl(com.jme.math.Vector3f[])
     */
    private void setSphere(Vector3f O, Vector3f A) {
        radius = FastMath.sqrt(((A.x - O.x) * (A.x - O.x) + (A.y - O.y)
                * (A.y - O.y) + (A.z - O.z) * (A.z - O.z)) / 4f);
        center.interpolate(O, A, .5f);
    }

    /**
     * <code>averagePoints</code> selects the sphere center to be the average
     * of the points and the sphere radius to be the smallest value to enclose
     * all points.
     * 
     * @param points
     *            the list of points to contain.
     */
    public void averagePoints(Vector3f[] points) {
        LoggingSystem.getLogger().log(Level.INFO,
                "Bounding Sphere calculated " + "using average points.");
        center = points[0];

        for (int i = 1; i < points.length; i++)
            center.addLocal(points[i]);
        float quantity = 1.0f / points.length;
        center.multLocal(quantity);

        float maxRadiusSqr = 0;
        for (int i = 0; i < points.length; i++) {
            Vector3f diff = points[i].subtract(center);
            float radiusSqr = diff.lengthSquared();
            if (radiusSqr > maxRadiusSqr)
                maxRadiusSqr = radiusSqr;
        }

        radius = (float) Math.sqrt(maxRadiusSqr);

    }

    /**
     * <code>transform</code> modifies the center of the sphere to reflect the
     * change made via a rotation, translation and scale.
     * 
     * @param rotate
     *            the rotation change.
     * @param translate
     *            the translation change.
     * @param scale
     *            the size change.
     * @return BoundingVolume
     */
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale) {
        Vector3f newCenter = rotate.mult(center).multLocal(scale).addLocal(
                translate);
        return new BoundingSphere(getMaxAxis(scale) * radius, newCenter);
    }

    /**
     * <code>transform</code> modifies the center of the sphere to reflect the
     * change made via a rotation, translation and scale.
     * 
     * @param rotate
     *            the rotation change.
     * @param translate
     *            the translation change.
     * @param scale
     *            the size change.
     * @param store
     *            sphere to store result in
     * @return BoundingVolume
     * @return ref
     */
    public BoundingVolume transform(Quaternion rotate, Vector3f translate,
            Vector3f scale, BoundingVolume store) {
        BoundingSphere sphere;
        if (store == null || !(store instanceof BoundingSphere)) {
            sphere = new BoundingSphere(1, new Vector3f(0, 0, 0));
        } else {
            sphere = (BoundingSphere) store;
        }

        rotate.mult(center, sphere.center);
        sphere.center.multLocal(scale).addLocal(translate);
        sphere.radius = getMaxAxis(scale) * radius;
        return sphere;
    }

    private float getMaxAxis(Vector3f scale) {
        if (scale.x >= scale.y) {
            if (scale.x >= scale.z)
                return scale.x;
            else
                return scale.z;
        } else {
            if (scale.y >= scale.z)
                return scale.y;
            else
                return scale.z;
        }
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * 
     * @param plane
     *            the plane to check against.
     * @return side
     */
    public int whichSide(Plane plane) {
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
        if (volume instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere) volume;
            float temp_radius = sphere.getRadius();
            Vector3f temp_center = sphere.getCenter();
            BoundingSphere rVal = new BoundingSphere();
            return merge(temp_radius, temp_center, rVal);
        } else if (volume instanceof BoundingBox) {
            BoundingBox box = (BoundingBox) volume;
            Vector3f radVect = new Vector3f(box.xExtent, box.yExtent,
                    box.zExtent);
            Vector3f temp_center = box.getCenter();
            BoundingSphere rVal = new BoundingSphere();
            return merge(radVect.length(), temp_center, rVal);
        } else if (volume instanceof OrientedBoundingBox) {
            OrientedBoundingBox box = (OrientedBoundingBox) volume;
            BoundingSphere rVal = (BoundingSphere) this.clone(null);
            return rVal.mergeOBB(box);
        } else {
            return null;
        }
    }

    private Vector3f tmpRadVect = new Vector3f();

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
        if (volume instanceof BoundingSphere) {
            BoundingSphere sphere = (BoundingSphere) volume;
            float temp_radius = sphere.getRadius();
            Vector3f temp_center = sphere.getCenter();
            return merge(temp_radius, temp_center, this);
        } else if (volume instanceof BoundingBox) {
            BoundingBox box = (BoundingBox) volume;
            Vector3f radVect = tmpRadVect;
            radVect.set(box.xExtent, box.yExtent, box.zExtent);
            Vector3f temp_center = box.getCenter();
            return merge(radVect.length(), temp_center, this);
        } else if (volume instanceof OrientedBoundingBox) {
            return mergeOBB((OrientedBoundingBox) volume);
        } else {
            return null;
        }
    }

    /**
     * Merges this sphere with the given OBB.
     * 
     * @param volume
     *            The OBB to merge.
     * @return This sphere, after merging.
     */
    private BoundingSphere mergeOBB(OrientedBoundingBox volume) {
        if (!volume.correctCorners)
            volume.computeCorners();
        FloatBuffer buf = BufferUtils.createVector3Buffer(16);
        for (int i = 0; i < 8; i++) {
            buf.put(volume.vectorStore[i].x);
            buf.put(volume.vectorStore[i].y);
            buf.put(volume.vectorStore[i].z);
        }
        buf.put(center.x+radius).put(center.y+radius).put(center.z+radius);
        buf.put(center.x-radius).put(center.y+radius).put(center.z+radius);
        buf.put(center.x+radius).put(center.y-radius).put(center.z+radius);
        buf.put(center.x+radius).put(center.y+radius).put(center.z-radius);
        buf.put(center.x-radius).put(center.y-radius).put(center.z+radius);
        buf.put(center.x-radius).put(center.y+radius).put(center.z-radius);
        buf.put(center.x+radius).put(center.y-radius).put(center.z-radius);
        buf.put(center.x-radius).put(center.y-radius).put(center.z-radius);
        computeFromPoints(buf);
        return this;
    }

    private BoundingVolume merge(float temp_radius, Vector3f temp_center,
            BoundingSphere rVal) {
        Vector3f diff = temp_center.subtract(center, tempVeca);
        float lengthSquared = diff.lengthSquared();
        float radiusDiff = temp_radius - radius;

        float fRDiffSqr = radiusDiff * radiusDiff;

        if (fRDiffSqr >= lengthSquared) {
            if (radiusDiff <= 0.0f) {
                return this;
            } else {
                rVal.setCenter(temp_center);
                rVal.setRadius(temp_radius);
                return rVal;
            }
        }

        float length = (float) Math.sqrt(lengthSquared);

        if (length > FastMath.FLT_EPSILON) {
            float coeff = (length + radiusDiff) / (2.0f * length);
            rVal.setCenter(center.addLocal(diff.multLocal(coeff)));
        } else {
            rVal.setCenter(center);
        }

        rVal.setRadius(0.5f * (length + radius + temp_radius));
        return rVal;
    }

    /**
     * <code>clone</code> creates a new BoundingSphere object containing the
     * same data as this one.
     * 
     * @param store
     *            where to store the cloned information. if null or wrong class,
     *            a new store is created.
     * @return the new BoundingSphere
     */
    public Object clone(BoundingVolume store) {
        if (store != null && store instanceof BoundingSphere) {
            BoundingSphere rVal = (BoundingSphere) store;
            if (null == rVal.center) {
                rVal.center = new Vector3f();
            }
            rVal.center.x = center.x;
            rVal.center.y = center.y;
            rVal.center.z = center.z;
            rVal.radius = radius;
            rVal.checkPlanes[0] = checkPlanes[0];
            rVal.checkPlanes[1] = checkPlanes[1];
            rVal.checkPlanes[2] = checkPlanes[2];
            rVal.checkPlanes[3] = checkPlanes[3];
            rVal.checkPlanes[4] = checkPlanes[4];
            rVal.checkPlanes[5] = checkPlanes[5];
            return rVal;
        } else
            return new BoundingSphere(radius,
                    (center != null ? (Vector3f) center.clone() : null));
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
     * <code>recomputeMesh</code> regenerates the <code>BoundingSphere</code>
     * based on new model information.
     */
    public void recomputeMesh() {
        if (radius != oldRadius || !center.equals(oldCenter)) {
            setData(center, 10, 10, radius, true);
            oldRadius = radius;
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
     * Stores the current center of this BoundingSphere into the store vector.
     * 
     * @param store
     *            The vector to store the center into.
     * @return The store vector, after setting it's contents to the center
     */
    public Vector3f getCenter(Vector3f store) {
        return store.set(center);
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: RRR.SSSS Center: <Vector>".
     * 
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingSphere [Radius: " + radius + " Center: "
                + center + "]";
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
            return bv.intersectsSphere(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsSphere(com.jme.bounding.BoundingSphere)
     */
    public boolean intersectsSphere(BoundingSphere bs) {
        Vector3f diff = getCenter().subtract(bs.getCenter());
        float rsum = getRadius() + bs.getRadius();
        return (diff.dot(diff) <= rsum * rsum);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsBoundingBox(com.jme.bounding.BoundingBox)
     */
    public boolean intersectsBoundingBox(BoundingBox bb) {
        if (FastMath.abs(bb.center.x - getCenter().x) < getRadius()
                + bb.xExtent
                && FastMath.abs(bb.center.y - getCenter().y) < getRadius()
                        + bb.yExtent
                && FastMath.abs(bb.center.z - getCenter().z) < getRadius()
                        + bb.zExtent)
            return true;

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOrientedBoundingBox(com.jme.bounding.OrientedBoundingBox)
     */
    public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb) {
        return obb.intersectsSphere(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersectsOBB2(com.jme.bounding.OBB2)
     */
    public boolean intersectsOBB2(OBB2 obb) {
        return obb.intersectsSphere(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.bounding.BoundingVolume#intersects(com.jme.math.Ray)
     */
    public boolean intersects(Ray ray) {
        Vector3f diff = tempVeca.set(ray.getOrigin())
                .subtractLocal(getCenter());
        float a = ray.getDirection().lengthSquared();
        float b = diff.dot(ray.getDirection());
        float c = diff.lengthSquared() - getRadius() * getRadius();

        float t[] = new float[2];
        float discr = b * b - a * c;
        if (discr < 0.0) {
            return false;
        } else if (discr > 0.0) {
            float root = (float) Math.sqrt(discr);
            float invA = 1.0f / a;
            t[0] = (-b - root) * invA;
            t[1] = (-b + root) * invA;

            if (t[0] >= 0.0) {
                return true;
            } else if (t[1] >= 0.0) {
                return true;
            } else {
                return false;
            }
        } else {
            t[0] = -b / a;
            if (t[0] >= 0.0) {
                return true;
            } else {
                return false;
            }
        }
    }
}