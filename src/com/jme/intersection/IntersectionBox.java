/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

import com.jme.bounding.BoundingBox;
import com.jme.math.Ray;
import com.jme.math.Vector3f;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of box objects. All the methods are static to allow for quick
 * and easy calls.
 * 
 * @author Mark Powell
 * @version $Id: IntersectionBox.java,v 1.7 2004-04-02 17:08:01 mojomonkey Exp $
 */
public class IntersectionBox {

    /**
     * EPSILON represents the error buffer used to denote a hit.
     */
    public static final double EPSILON = 1e-12;

    /**
     * 
     * <code>intersection</code> determines if a ray has intersected a box.
     * 
     * @param ray
     *            the ray to test.
     * @param box
     *            the box to test.
     * @return true if they intersect, false otherwise.
     */
    public static boolean intersection(Ray ray, BoundingBox box) {
        Vector3f diff = ray.origin.subtract(box.center);
        // convert ray to box coordinates
        Vector3f origin = new Vector3f(diff.x, diff.y, diff.z);
        Vector3f direction = new Vector3f(ray.direction.x, ray.direction.y,
                ray.direction.z);
        float[] t = new float[2];
        t[0] = 0f;
        t[1] = Float.POSITIVE_INFINITY;
        float[] extents = { box.xExtent, box.yExtent, box.zExtent};
        return findIntersection(origin, direction, extents, t);
    }

    /**
     * 
     * <code>intersection</code> compares two static spheres for intersection.
     * If any part of the two spheres touch, true is returned, otherwise false
     * will return.
     * 
     * @param box1
     *            the first box to test.
     * @param box2
     *            the second box to test.
     * @return true if the spheres are intersecting, false otherwise.
     */
    public static boolean intersection(BoundingBox box1, BoundingBox box2) {
        if (box1.center.x + box1.xExtent < box2.center.x - box2.xExtent
                || box1.center.x - box1.xExtent > box2.center.x + box2.xExtent)
            return false;
        else if (box1.center.y + box1.yExtent < box2.center.y - box2.yExtent
                || box1.center.y - box1.yExtent > box2.center.y + box2.yExtent)
            return false;
        else if (box1.center.z + box1.zExtent < box2.center.z - box2.zExtent
                || box1.center.z - box1.zExtent > box2.center.z + box2.zExtent)
            return false;
        else
            return true;
    }

    /**
     * 
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
    private static boolean clip(float denom, float numer, float[] t) {
        // Return value is 'true' if line segment intersects the current test
        // plane. Otherwise 'false' is returned in which case the line segment
        // is entirely clipped.
        if (denom > 0.0f) {
            if (numer > denom * t[1]) return false;
            if (numer > denom * t[0]) t[0] = numer / denom;
            return true;
        } else if (denom < 0.0f) {
            if (numer > denom * t[0]) return false;
            if (numer > denom * t[1]) t[1] = numer / denom;
            return true;
        } else {
            return numer <= 0.0;
        }
    }

    /**
     * 
     * <code>findIntersection</code> determines if any of the planes of the box are
     * intersected by a ray (defined by an origin and direction). 
     * 
     * @param origin the origin of the ray.
     * @param direction the direction of the ray.
     * @param extent the extents of the box.
     * @param t the plane intersection values of the box.
     * @return true if an intersection occurs, false otherwise.
     */
    private static boolean findIntersection(Vector3f origin,
            Vector3f direction, float[] extent, float[] t) {
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