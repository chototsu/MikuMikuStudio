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

import com.jme.math.Line;
import com.jme.math.Plane;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingBox;
import com.jme.scene.BoundingSphere;
import com.jme.scene.BoundingVolume;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of some objects. All the methods are static to allow for quick
 * and easy calls.
 * @author Mark Powell
 * @version $Id: Intersection.java,v 1.5 2003-12-12 21:56:03 mojomonkey Exp $
 */
public class Intersection {
    /**
     * EPSILON represents the error buffer used to denote a hit.
     */
    public static final double EPSILON = 1e-12;

    /**
     * 
     * <code>intersection</code> determines if a ray has intersected a given
     * bounding volume. This method actually delegates the work to another
     * method depending on what type of bounding volume has been passed.
     * @param ray the ray to test.
     * @param volume the bounding volume to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingVolume volume) {
        if (volume instanceof BoundingSphere) {
            return intersection(ray, (BoundingSphere) volume);
        } else if (volume instanceof BoundingBox) {
            return intersection(ray, (BoundingBox) volume);
        }
        return false;
    }

    /**
     * 
     * <code>intersection</code> determines if a ray has intersected a sphere.
     * @param ray the ray to test.
     * @param sphere the sphere to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingSphere sphere) {
        Vector3f diff = ray.getOrigin().subtract(sphere.getCenter());
        float a = ray.getDirection().lengthSquared();
        float b = diff.dot(ray.getDirection());
        float c =
            diff.lengthSquared() - sphere.getRadius() * sphere.getRadius();

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

    public static boolean intersection(Ray ray, BoundingBox box) {
        float[] fWdU = new float[3];
        float[] fAWdU = new float[3];
        float[] fDdU = new float[3];
        float[] fADdU = new float[3];
        float[] fAWxDdU = new float[3];
        float fRhs;

        Vector3f axis0 = new Vector3f(1, 0, 0);
        Vector3f axis1 = new Vector3f(0, 1, 0);
        Vector3f axis2 = new Vector3f(0, 0, 1);
        float extent0 = (box.getMax().x - box.getMin().x) / 2;
        float extent1 = (box.getMax().y - box.getMin().y) / 2;
        float extent2 = (box.getMax().z - box.getMin().z) / 2;

        Vector3f kDiff = ray.getOrigin().subtract(box.getCenter());

        fWdU[0] = ray.getDirection().dot(axis0);
        fAWdU[0] = Math.abs(fWdU[0]);
        fDdU[0] = kDiff.dot(axis0);
        fADdU[0] = Math.abs(fDdU[0]);
        if (fADdU[0] > extent0 && fDdU[0] * fWdU[0] >= 0.0f)
            return false;

        fWdU[1] = ray.getDirection().dot(axis1);
        fAWdU[1] = Math.abs(fWdU[1]);
        fDdU[1] = kDiff.dot(axis1);
        fADdU[1] = Math.abs(fDdU[1]);
        if (fADdU[1] > extent1 && fDdU[1] * fWdU[1] >= 0.0f)
            return false;

        fWdU[2] = ray.getDirection().dot(axis2);
        fAWdU[2] = Math.abs(fWdU[2]);
        fDdU[2] = kDiff.dot(axis2);
        fADdU[2] = Math.abs(fDdU[2]);
        if (fADdU[2] > extent2 && fDdU[2] * fWdU[2] >= 0.0f)
            return false;

        Vector3f kWxD = ray.getDirection().cross(kDiff);

        fAWxDdU[0] = Math.abs(kWxD.dot(axis0));
        fRhs = extent1 * fAWdU[2] + extent2 * fAWdU[1];
        if (fAWxDdU[0] > fRhs)
            return false;

        fAWxDdU[1] = Math.abs(kWxD.dot(axis1));
        fRhs = extent0 * fAWdU[2] + extent2 * fAWdU[0];
        if (fAWxDdU[1] > fRhs)
            return false;

        fAWxDdU[2] = Math.abs(kWxD.dot(axis2));
        fRhs = extent0 * fAWdU[1] + extent1 * fAWdU[0];
        if (fAWxDdU[2] > fRhs)
            return false;

        return true;
    }

    /**
     * 
     * <code>intersection</code> compares a dynamic sphere to a stationary line.
     * The velocity of the sphere is given as well as the period of time for 
     * movement. If a collision occurs somewhere along this time period, true
     * is returned. False is returned otherwise.
     * @param line the stationary line to test against.
     * @param sphere the dynamic sphere to test.
     * @param velocity the velocity of the sphere.
     * @param time the time range to test.
     * @return true if intersection occurs, false otherwise.
     */
    public static boolean intersection(
        Line line,
        BoundingSphere sphere,
        Vector3f velocity,
        float time) {

        Vector3f e = sphere.getCenter().subtract(line.getOrigin());
        float dotDW = line.getDirection().dot(velocity);
        float dotDD = line.getDirection().dot(line.getDirection());
        float dotWW = velocity.dot(velocity);
        float dotWE = velocity.dot(e);
        float dotDE = line.getDirection().dot(e);
        float dotEE = e.dot(e);
        float ddr2 = dotDD * sphere.getRadius() * sphere.getRadius();

        float a = dotDD * dotWW - dotDW * dotDW;
        float b = dotDD * dotWE - dotDE * dotDW;
        float c = dotDD * dotEE - dotDE * dotDE;

        if (a > 0) {
            float t = -b / a;
            if (t < 0) {
                return c <= ddr2;
            } else if (t > time) {
                return time * (a * time + 2 * b) + c <= ddr2;
            } else {
                return t * (a * t + 2 * b) + c <= ddr2;
            }
        } else {
            return c <= ddr2;
        }
    }

    /**
     * 
     * <code>intersection</code> compares a dynamix sphere to a stationary plane.
     * The velocity of the sphere is given as well as the period of time for
     * movement. If a collision occurs somewhere along this time period, true is
     * returned. False is returned otherwise.
     * @param plane the stationary plane to test against.
     * @param sphere the dynamic sphere to test.
     * @param velocity the velocity of the sphere.
     * @param time the time range to test.
     * @return true if intersection occurs, false otherwise.
     */
    public static boolean intersection(
        Plane plane,
        BoundingSphere sphere,
        Vector3f velocity,
        float time) {

        float sdist =
            plane.getNormal().dot(sphere.getCenter()) - plane.getConstant();

        if (sdist > sphere.getRadius()) {
            float dotNW = plane.getNormal().dot(velocity);
            return (sphere.getRadius() - sdist / dotNW) < time;
        } else if (sdist < -sphere.getRadius()) {
            float dotNW = plane.getNormal().dot(velocity);
            return (- (sphere.getRadius() + sdist) / dotNW) < time;
        } else {
            return true;
        }
    }

    /**
     * 
     * <code>intersection</code> compares two bounding volumes for intersection.
     * If any part of the volumes touch, true is returned, otherwise false is
     * returned.
     * 
     * @param vol1 the first volume to check.
     * @param vol2 the second volume to check.
     * @return true if an intersection occurs, false otherwise.
     */
    public static boolean intersection(
        BoundingVolume vol1,
        BoundingVolume vol2) {
        if (vol1 instanceof BoundingSphere) {
            if (vol2 instanceof BoundingSphere) {
                return intersection(
                    (BoundingSphere) vol1,
                    (BoundingSphere) vol2);
            } else {
                return false;
            }
        } else if (vol1 instanceof BoundingBox) {
            if (vol2 instanceof BoundingBox) {
                return intersection((BoundingBox) vol1, (BoundingBox) vol2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 
     * <code>intersection</code> compares two static spheres for intersection. 
     * If any part of the two spheres touch, true is returned, otherwise false
     * will return.
     * @param sphere1 the first sphere to test.
     * @param sphere2 the second sphere to test.
     * @return true if the spheres are intersecting, false otherwise.
     */
    public static boolean intersection(
        BoundingSphere sphere1,
        BoundingSphere sphere2) {
        Vector3f diff = sphere1.getCenter().subtract(sphere2.getCenter());
        float rsum = sphere1.getRadius() + sphere2.getRadius();
        return (diff.dot(diff) <= rsum * rsum);
    }

    /**
     * 
     * <code>intersection</code>
     * @param box1
     * @param box2
     * @return
     */
    public static boolean intersection(BoundingBox box1, BoundingBox box2) {
        // convenience variables
        
        Vector3f axis0 = new Vector3f(1, 0, 0);
        Vector3f axis1 = new Vector3f(0, 1, 0);
        Vector3f axis2 = new Vector3f(0, 0, 1);
        float extentA0 = (box1.getMax().x - box1.getMin().x) / 2;
        float extentA1 = (box1.getMax().y - box1.getMin().y) / 2;
        float extentA2 = (box1.getMax().z - box1.getMin().z) / 2;
        
        float extentB0 = (box2.getMax().x - box2.getMin().x) / 2;
        float extentB1 = (box2.getMax().y - box2.getMin().y) / 2;
        float extentB2 = (box2.getMax().z - box2.getMin().z) / 2;

        // compute difference of box centers, D = C1-C0
        Vector3f kD = box2.getCenter().subtract(box1.getCenter());

        float[][] aafC = new float[3][3]; // matrix C = A^T B, c_{ij} = Dot(A_i,B_j)
        float[][] aafAbsC = new float[3][3]; // |c_{ij}|
        float[] afAD = new float[3]; // Dot(A_i,D)
        float fR0, fR1, fR; // interval radii and distance between centers
        float fR01; // = R0 + R1

        // axis C0+t*A0
        aafC[0][0] = axis0.dot(axis0);
        aafC[0][1] = axis0.dot(axis1);
        aafC[0][2] = axis0.dot(axis2);
        afAD[0] = axis0.dot(kD);
        aafAbsC[0][0] = Math.abs(aafC[0][0]);
        aafAbsC[0][1] = Math.abs(aafC[0][1]);
        aafAbsC[0][2] = Math.abs(aafC[0][2]);
        fR = Math.abs(afAD[0]);
        fR1 =
            extentB0 * aafAbsC[0][0]
                + extentB1 * aafAbsC[0][1]
                + extentB2 * aafAbsC[0][2];
        fR01 = extentA0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A1
        aafC[1][0] = axis1.dot(axis0);
        aafC[1][1] = axis1.dot(axis1);
        aafC[1][2] = axis1.dot(axis2);
        afAD[1] = axis1.dot(kD);
        aafAbsC[1][0] = Math.abs(aafC[1][0]);
        aafAbsC[1][1] = Math.abs(aafC[1][1]);
        aafAbsC[1][2] = Math.abs(aafC[1][2]);
        fR = Math.abs(afAD[1]);
        fR1 =
            extentB0 * aafAbsC[1][0]
                + extentB1 * aafAbsC[1][1]
                + extentB2 * aafAbsC[1][2];
        fR01 = extentA1 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A2
        aafC[2][0] = axis2.dot(axis0);
        aafC[2][1] = axis2.dot(axis1);
        aafC[2][2] = axis2.dot(axis2);
        afAD[2] = axis2.dot(kD);
        aafAbsC[2][0] = Math.abs(aafC[2][0]);
        aafAbsC[2][1] = Math.abs(aafC[2][1]);
        aafAbsC[2][2] = Math.abs(aafC[2][2]);
        fR = Math.abs(afAD[2]);
        fR1 =
            extentB0 * aafAbsC[2][0]
                + extentB1 * aafAbsC[2][1]
                + extentB2 * aafAbsC[2][2];
        fR01 = extentA2 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*B0
        fR = Math.abs(axis0.dot(kD));
        fR0 =
            extentA0 * aafAbsC[0][0]
                + extentA1 * aafAbsC[1][0]
                + extentA2 * aafAbsC[2][0];
        fR01 = fR0 + extentB0;
        if (fR > fR01)
            return false;

        // axis C0+t*B1
        fR = Math.abs(axis1.dot(kD));
        fR0 =
            extentA0 * aafAbsC[0][1]
                + extentA1 * aafAbsC[1][1]
                + extentA2 * aafAbsC[2][1];
        fR01 = fR0 + extentB1;
        if (fR > fR01)
            return false;

        // axis C0+t*B2
        fR = Math.abs(axis2.dot(kD));
        fR0 =
            extentA0 * aafAbsC[0][2]
                + extentA1 * aafAbsC[1][2]
                + extentA2 * aafAbsC[2][2];
        fR01 = fR0 + extentB2;
        if (fR > fR01)
            return false;

        // axis C0+t*A0xB0
        fR = Math.abs(afAD[2] * aafC[1][0] - afAD[1] * aafC[2][0]);
        fR0 = extentA1 * aafAbsC[2][0] + extentA2 * aafAbsC[1][0];
        fR1 = extentB1 * aafAbsC[0][2] + extentB2 * aafAbsC[0][1];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A0xB1
        fR = Math.abs(afAD[2] * aafC[1][1] - afAD[1] * aafC[2][1]);
        fR0 = extentA1 * aafAbsC[2][1] + extentA2 * aafAbsC[1][1];
        fR1 = extentB0 * aafAbsC[0][2] + extentB2 * aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A0xB2
        fR = Math.abs(afAD[2] * aafC[1][2] - afAD[1] * aafC[2][2]);
        fR0 = extentA1 * aafAbsC[2][2] + extentA2 * aafAbsC[1][2];
        fR1 = extentB0 * aafAbsC[0][1] + extentB1 * aafAbsC[0][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A1xB0
        fR = Math.abs(afAD[0] * aafC[2][0] - afAD[2] * aafC[0][0]);
        fR0 = extentA0 * aafAbsC[2][0] + extentA2 * aafAbsC[0][0];
        fR1 = extentB1 * aafAbsC[1][2] + extentB2 * aafAbsC[1][1];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A1xB1
        fR = Math.abs(afAD[0] * aafC[2][1] - afAD[2] * aafC[0][1]);
        fR0 = extentA0 * aafAbsC[2][1] + extentA2 * aafAbsC[0][1];
        fR1 = extentB0 * aafAbsC[1][2] + extentB2 * aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A1xB2
        fR = Math.abs(afAD[0] * aafC[2][2] - afAD[2] * aafC[0][2]);
        fR0 = extentA0 * aafAbsC[2][2] + extentA2 * aafAbsC[0][2];
        fR1 = extentB0 * aafAbsC[1][1] + extentB1 * aafAbsC[1][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A2xB0
        fR = Math.abs(afAD[1] * aafC[0][0] - afAD[0] * aafC[1][0]);
        fR0 = extentA0 * aafAbsC[1][0] + extentA1 * aafAbsC[0][0];
        fR1 = extentB1 * aafAbsC[2][2] + extentB2 * aafAbsC[2][1];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A2xB1
        fR = Math.abs(afAD[1] * aafC[0][1] - afAD[0] * aafC[1][1]);
        fR0 = extentA0 * aafAbsC[1][1] + extentA1 * aafAbsC[0][1];
        fR1 = extentB0 * aafAbsC[2][2] + extentB2 * aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        // axis C0+t*A2xB2
        fR = Math.abs(afAD[1] * aafC[0][2] - afAD[0] * aafC[1][2]);
        fR0 = extentA0 * aafAbsC[1][2] + extentA1 * aafAbsC[0][2];
        fR1 = extentB0 * aafAbsC[2][1] + extentB1 * aafAbsC[2][0];
        fR01 = fR0 + fR1;
        if (fR > fR01)
            return false;

        return true;
    }

    /**
     * 
     * <code>intersection</code> compares two dynamic spheres. Both sphers have
     * a velocity and a time is givin to check for. If these spheres will 
     * collide within the time alloted, true is returned, otherwise false is
     * returned.
     * @param sphere1 the first sphere to test. 
     * @param sphere2 the second sphere to test.
     * @param velocity1 the velocity of the first sphere.
     * @param velocity2 the velocity of the second sphere.
     * @param time the time frame to check.
     * @return true if a collision occurs, false otherwise.
     */
    public static boolean intersection(
        BoundingSphere sphere1,
        BoundingSphere sphere2,
        Vector3f velocity1,
        Vector3f velocity2,
        float time) {

        Vector3f velocityDiff = velocity2.subtract(velocity1);
        float a = velocityDiff.lengthSquared();
        Vector3f kCDiff = sphere2.getCenter().subtract(sphere1.getCenter());
        float c = kCDiff.lengthSquared();
        float radiusSum = sphere1.getRadius() + sphere2.getRadius();
        float radiusSumSquared = radiusSum * radiusSum;

        if (a > 0.0) {
            float b = kCDiff.dot(velocityDiff);
            if (b <= 0.0) {
                if (-time * a <= b)
                    return a * c - b * b <= a * radiusSumSquared;
                else
                    return time * (time * a + 2.0 * b) + c <= radiusSumSquared;
            }
        }

        return c <= radiusSumSquared;
    }
}
