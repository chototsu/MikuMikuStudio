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
package com.jme.math;

/**
 * <code>Ray</code> defines a line segment which has an origin and a direction.
 * That is, a point and an infinite ray is cast from this point. The ray is
 * defined by the following equation: R(t) = origin + t*direction for t >= 0.
 * @author Mark Powell
 * @version $Id: Ray.java,v 1.3 2004-03-09 07:16:15 renanse Exp $
 */
public class Ray {
    public Vector3f origin;
    public Vector3f direction;

    /**
     * Constructor instantiates a new <code>Ray</code> object. As default, the
     * origin is (0,0,0) and the direction is (0,0,0).
     *
     */
    public Ray() {
        origin = new Vector3f();
        direction = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>Ray</code> object. The origin and
     * direction are given.
     * @param origin the origin of the ray.
     * @param direction the direction the ray travels in.
     */
    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     *
     * <code>getOrigin</code> retrieves the origin point of the ray.
     * @return the origin of the ray.
     */
    public Vector3f getOrigin() {
        return origin;
    }

    /**
     *
     * <code>setOrigin</code> sets the origin of the ray.
     * @param origin the origin of the ray.
     */
    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    /**
     *
     * <code>getDirection</code> retrieves the direction vector of the ray.
     * @return the direction of the ray.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     *
     * <code>setDirection</code> sets the direction vector of the ray.
     * @param direction the direction of the ray.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
}
