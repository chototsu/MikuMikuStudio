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

package com.jme.math;


/**
 * <code>Ray</code> defines a line segment which has an origin and a direction.
 * That is, a point and an infinite ray is cast from this point. The ray is
 * defined by the following equation: R(t) = origin + t*direction for t >= 0.
 * @author Mark Powell
 * @version $Id: Ray.java,v 1.12 2005-10-12 17:17:39 Mojomonkey Exp $
 */
public class Ray {
    /** The ray's begining point. */
    public Vector3f origin;
    /** The direction of the ray. */
    public Vector3f direction;
    protected static final Vector3f tempVa=new Vector3f();
    protected static final Vector3f tempVb=new Vector3f();
    protected static final Vector3f tempVc=new Vector3f();
    protected static final Vector3f tempVd=new Vector3f();

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
     * <code>intersect</code> determines if the Ray intersects a triangle.
     * @param t the Triangle to test against.
     * @return true if the ray collides.
     */
    public boolean intersect(Triangle t) {
        return intersect(t.get(0), t.get(1), t.get(2));
    }

    /**
     * <code>intersect</code> determines if the Ray intersects a triangle
     * defined by the specified points.
     * 
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @return true if the ray collides.
     */
    public boolean intersect(Vector3f v0,Vector3f v1,Vector3f v2){
        Vector3f edge1=v1.subtract(v0,tempVa);
        Vector3f edge2=v2.subtract(v0,tempVb);
        Vector3f pvec=direction.cross(edge2,tempVc);
        float det=edge1.dot(pvec);
        if (det > -FastMath.FLT_EPSILON && det < FastMath.FLT_EPSILON)
            return false;
        det=1f/det;
        Vector3f tvec=origin.subtract(v0,tempVd);
        float u=tvec.dot(pvec) *det;
        if (u < 0.0f || u > 1.0f)
            return false;
        Vector3f qvec=tvec.cross(edge1,tempVc);
        float v=direction.dot(qvec) * det;
        if (v < 0.0f || v + u > 1.0f)
            return false;
        return true;
    }

    /**
     * <code>intersectWhere</code> determines if the Ray intersects a triangle. It then
     * stores the point of intersection in the given loc vector
     * @param t the Triangle to test against.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides)
     * @return true if the ray collides.
     */
    public boolean intersectWhere(Triangle t, Vector3f loc) {
        return intersectWhere(t.get(0), t.get(1), t.get(2), loc);
    }

    /**
     * <code>intersectWhere</code> determines if the Ray intersects a triangle
     * defined by the specified points and if so it stores the point of
     * intersection in the given loc vector.
     * 
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides)
     * @return true if the ray collides.
     */
    public boolean intersectWhere(Vector3f v0, Vector3f v1, Vector3f v2,
            Vector3f loc) {
        Vector3f edge1 = v1.subtract(v0, tempVa);
        Vector3f edge2 = v2.subtract(v0, tempVb);
        Vector3f pvec = direction.cross(edge2, tempVc);
        float det = edge1.dot(pvec);
        if (det > -FastMath.FLT_EPSILON && det < FastMath.FLT_EPSILON)
            return false;
        det = 1f / det;
        Vector3f tvec = origin.subtract(v0, tempVd);
        float u = tvec.dot(pvec) * det;
        if (u < 0.0 || u > 1.0)
            return false;
        Vector3f qvec = tvec.cross(edge1, tempVc);
        float v = direction.dot(qvec) * det;
        if (v < 0.0 || v + u > 1.0)
            return false;
        float t = edge2.dot(qvec) * det;
        loc.set(origin).addLocal(direction.x * t, direction.y * t,
                direction.z * t);
        return true;
    }
    
    /**
     * <code>intersectWherePlanar</code> determines if the Ray intersects a
     * triangle and if so it stores the point of
     * intersection in the given loc vector as t, u, v where t is the distance
     * from the origin to the point of intersection and u,v is the intersection
     * point in terms of the triangle plane.
     * 
     * @param t the Triangle to test against.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides) as t, u, v
     * @return true if the ray collides.
     */
    public boolean intersectWherePlanar(Triangle t, Vector3f loc) {
        return intersectWhere(t.get(0), t.get(1), t.get(2), loc);
    }

    /**
     * <code>intersectWherePlanar</code> determines if the Ray intersects a
     * triangle defined by the specified points and if so it stores the point of
     * intersection in the given loc vector as t, u, v where t is the distance
     * from the origin to the point of intersection and u,v is the intersection
     * point in terms of the triangle plane.
     * 
     * @param v0
     *            first point of the triangle.
     * @param v1
     *            second point of the triangle.
     * @param v2
     *            third point of the triangle.
     * @param loc
     *            storage vector to save the collision point in (if the ray
     *            collides) as t, u, v
     * @return true if the ray collides.
     */
    public boolean intersectWherePlanar(Vector3f v0, Vector3f v1, Vector3f v2,
            Vector3f loc) {
        Vector3f edge1 = v1.subtract(v0, tempVa);
        Vector3f edge2 = v2.subtract(v0, tempVb);
        Vector3f pvec = direction.cross(edge2, tempVc);
        float det = edge1.dot(pvec);
        if (det > -FastMath.FLT_EPSILON && det < FastMath.FLT_EPSILON)
            return false;
        det = 1f / det;
        Vector3f tvec = origin.subtract(v0, tempVd);
        float u = tvec.dot(pvec) * det;
        if (u < 0.0 || u > 1.0)
            return false;
        Vector3f qvec = tvec.cross(edge1, tempVc);
        float v = direction.dot(qvec) * det;
        if (v < 0.0 || v + u > 1.0)
            return false;
        float t = edge2.dot(qvec) * det;
        loc.set(t, u, v);
        return true;
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
