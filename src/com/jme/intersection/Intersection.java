/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Ray;

/**
 * <code>Intersection</code> provides functional methods for calculating the
 * intersection of some objects. All the methods are static to allow for quick
 * and easy calls. <code>Intersection</code> relays requests to specific classes
 * to handle the actual work. By providing checks to just <code>BoundingVolume</code>
 * the client application need not worry about what type of bounding volume is
 * being used.
 * @author Mark Powell
 * @version $Id: Intersection.java,v 1.14 2004-07-19 22:11:59 renanse Exp $
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
            return IntersectionSphere.intersection(ray, (BoundingSphere) volume);
        } else if (volume instanceof BoundingBox) {
            return IntersectionBox.intersection(ray, (BoundingBox) volume);
        }
        return false;
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
                return IntersectionSphere.intersection(
                    (BoundingSphere) vol1,
                    (BoundingSphere) vol2);
            } else {
                return false;
            }
        } else if (vol1 instanceof BoundingBox) {
            if (vol2 instanceof BoundingBox) {
                return IntersectionBox.intersection(
                    (BoundingBox) vol1,
                    (BoundingBox) vol2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


}
