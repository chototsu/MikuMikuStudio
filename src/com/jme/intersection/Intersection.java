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
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingSphere;
import com.jme.scene.BoundingVolume;

/**
 * <code>Intersection</code>
 * @author Mark Powell
 * @version $Id: Intersection.java,v 1.1 2003-12-04 20:39:49 mojomonkey Exp $
 */
public class Intersection {

    public static final double EPSILON = 1e-12;

    public static boolean intersection(Ray ray, BoundingVolume volume) {
        if (volume instanceof BoundingSphere) {
            return intersection(ray, (BoundingSphere) volume);
        }
        return false;
    }

    public static boolean intersection(Ray ray, BoundingSphere sphere) {
        //      set up quadratic Q(t) = a*t^2 + 2*b*t + c
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

    public static boolean intersection(Ray ray, Triangle triangle) {
        return Distance.distance(ray, triangle) <= EPSILON;
    }
}
