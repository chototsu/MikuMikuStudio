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
import com.jme.scene.Box;
import com.jme.scene.BoundingBox;
import com.jme.scene.BoundingSphere;
import com.jme.scene.BoundingVolume;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of some objects. All the methods are static to allow for quick
 * and easy calls.
 * @author Mark Powell
 * @version $Id: Intersection.java,v 1.7 2004-03-09 18:09:10 renanse Exp $
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

    /**
     *
     * <code>intersection</code> determines if a ray has intersected a box.
     * @param ray the ray to test.
     * @param box the box to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingBox box) {
        float absRay[] = new float[3];
        float absDiff[] = new float[3];

        Vector3f diff = ray.origin.subtract(box.center);

        absRay[0] = Math.abs(ray.direction.x);
        absDiff[0] = Math.abs(diff.x);
        if ( absDiff[0] > box.xExtent && diff.x*ray.direction.x >= 0.0f )
            return false;

        absRay[1] = Math.abs(ray.direction.y);
        absDiff[1] = Math.abs(diff.y);
        if ( absDiff[1] > box.yExtent && diff.y*ray.direction.y >= 0.0f )
            return false;

        absRay[2] = Math.abs(ray.direction.z);
        absDiff[2] = Math.abs(diff.z);
        if ( absDiff[2] > box.zExtent && diff.z*ray.direction.z >= 0.0f )
            return false;

        Vector3f rayXdiff = ray.direction.cross(diff);

        float check;
        check = box.yExtent*absRay[2] + box.zExtent*absRay[1];
        if ( Math.abs(rayXdiff.x) > check )
            return false;

        check = box.xExtent*absRay[2] + box.zExtent*absRay[0];
        if ( Math.abs(rayXdiff.y) > check )
            return false;

        check = box.xExtent*absRay[1] + box.yExtent*absRay[0];
        if ( Math.abs(rayXdiff.z) > check )
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
                return intersection(
                    (BoundingBox) vol1,
                    (BoundingBox) vol2);
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

        // compute difference of box centers, D = C1-C0
        Vector3f centDiff = box2.center.subtract(box1.center);

        float fR0, fR1, fR;   // interval radii and distance between centers
        float fR01;           // = R0 + R1

        // axis C0+t*A0
        fR = Math.abs(centDiff.x);
        fR1 = box2.xExtent*1+box2.yExtent*2+box2.zExtent*2;
        fR01 = box1.xExtent + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1
        fR = Math.abs(centDiff.y);
        fR1 = box2.xExtent*2+box2.yExtent*1+box2.zExtent*2;
        fR01 = box1.yExtent + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2
        fR = Math.abs(centDiff.z);
        fR1 = box2.xExtent*2+box2.yExtent*2+box2.zExtent*1;
        fR01 = box1.zExtent + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B0
        fR = Math.abs(centDiff.x);
        fR0 = box1.xExtent*1+box1.yExtent*2+box1.zExtent*2;
        fR01 = fR0 + box2.xExtent;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B1
        fR = Math.abs(centDiff.y);
        fR0 = box1.xExtent*2+box1.yExtent*1+box1.zExtent*2;
        fR01 = fR0 + box2.yExtent;
        if ( fR > fR01 )
            return false;

        // axis C0+t*B2
        fR = Math.abs(centDiff.z);
        fR0 = box1.xExtent*2+box1.yExtent*2+box1.zExtent*1;
        fR01 = fR0 + box2.zExtent;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB0
        fR = Math.abs(centDiff.z*2-centDiff.y*2);
        fR0 = box1.yExtent*2 + box1.zExtent*2;
        fR1 = box2.yExtent*2 + box2.zExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB1
        fR = Math.abs(centDiff.z*1-centDiff.y*2);
        fR0 = box1.yExtent*2 + box1.zExtent*1;
        fR1 = box2.xExtent*2 + box2.zExtent*1;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A0xB2
        fR = Math.abs(centDiff.z*2-centDiff.y*1);
        fR0 = box1.yExtent*1 + box1.zExtent*2;
        fR1 = box2.xExtent*2 + box2.yExtent*1;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB0
        fR = Math.abs(centDiff.x*2-centDiff.z*1);
        fR0 = box1.xExtent*2 + box1.zExtent*1;
        fR1 = box2.yExtent*2 + box2.zExtent*1;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB1
        fR = Math.abs(centDiff.x*2-centDiff.z*2);
        fR0 = box1.xExtent*2 + box1.zExtent*2;
        fR1 = box2.xExtent*2 + box2.zExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A1xB2
        fR = Math.abs(centDiff.x*1-centDiff.z*2);
        fR0 = box1.xExtent*1 + box1.zExtent*2;
        fR1 = box2.xExtent*1 + box2.yExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB0
        fR = Math.abs(centDiff.y*1-centDiff.x*2);
        fR0 = box1.xExtent*2 + box1.yExtent*1;
        fR1 = box2.yExtent*1 + box2.zExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB1
        fR = Math.abs(centDiff.y*2-centDiff.x*1);
        fR0 = box1.xExtent*1 + box1.yExtent*2;
        fR1 = box2.xExtent*1 + box2.zExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
            return false;

        // axis C0+t*A2xB2
        fR = Math.abs(centDiff.y*2-centDiff.x*2);
        fR0 = box1.xExtent*2 + box1.yExtent*2;
        fR1 = box2.xExtent*2 + box2.yExtent*2;
        fR01 = fR0 + fR1;
        if ( fR > fR01 )
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
