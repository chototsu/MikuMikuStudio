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

package com.jme.scene;

import java.util.logging.Level;

import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;

/**
 * <code>BoundingSphere</code> defines a sphere that defines a container 
 * for a group of vertices of a particular piece of geometry. This sphere 
 * defines a radius and a center. 
 * <br><br>
 * A typical usage is to allow the class define the center and radius
 * by calling either <code>containAABB</code> or <code>averagePoints</code>.
 * A call to <code>computeFramePoint</code> in turn calls 
 * <code>containAABB</code>.
 * 
 * @author Mark Powell
 * @version $Id: BoundingSphere.java,v 1.6 2004-01-26 21:44:37 mojomonkey Exp $
 */
public class BoundingSphere implements BoundingVolume {
    private float radius;
    private Vector3f center;

    /**
     * Default contstructor instantiates a new <code>BoundingSphere</code>
     * object. 
     */
    public BoundingSphere() {
        center = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * @param radius the radius of the sphere.
     * @param center the center of the sphere.
     */
    public BoundingSphere(float radius, Vector3f center) {
        if (null == center) {
            this.center = new Vector3f();
        } else {
            this.center = center;
        }
        this.radius = radius;
    }

    /**
     * <code>getRadius</code> returns the radius of the bounding sphere.
     * @return the radius of the bounding sphere.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * <code>getCenter</code> returns the center of the bounding sphere.
     * @return the center of the bounding sphere.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * <code>setRadius</code> sets the radius of this bounding sphere.
     * @param radius the new radius of the bounding sphere.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * <code>setCenter</code> sets the center of the bounding sphere.
     * @param center the new center of the bounding sphere.
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Sphere from
     * a given set of points. It uses the <code>containAABB</code> method
     * as default.
     * @param points the points to contain.
     */
    public void computeFromPoints(Vector3f[] points) {
        containAABB(points);
    }

    /**
     * <code>containAABB</code> creates a minimum-volume axis-aligned
     * bounding box of the points, then selects the smallest 
     * enclosing sphere of the box with the sphere centered at the
     * boxes center.
     * @param points the list of points.
     */
    public void containAABB(Vector3f[] points) {
       if(points.length <= 0) {
            return;
        }
    
        Vector3f min = new Vector3f(points[0].x, points[0].y, points[0].z);
        Vector3f max = new Vector3f(min.x, min.y, min.z);
        for (int i = 1; i < points.length; i++) {
            if (points[i].x < min.x)
                min.x = points[i].x;
            else if (points[i].x > max.x)
                max.x = points[i].x;

            if (points[i].y < min.y)
                min.y = points[i].y;
            else if (points[i].y > max.y)
                max.y = points[i].y;

            if (points[i].z < min.z)
                min.z = points[i].z;
            else if (points[i].z > max.z)
                max.z = points[i].z;
        }

        center = max.add(min);
        center = center.mult(0.5f);

        Vector3f halfDiagonal = max.subtract(min);
        halfDiagonal = halfDiagonal.mult(0.5f);
        radius = halfDiagonal.length();
    }

    /**
     * <code>averagePoints</code> selects the sphere center to be
     * the average of the points and the sphere radius to be the 
     * smallest value to enclose all points.
     * @param points the list of points to contain.
     */
    public void averagePoints(Vector3f[] points) {
        LoggingSystem.getLogger().log(Level.INFO, "Bounding Sphere calculated " +
                    "using average points.");
        center = points[0];

        for (int i = 1; i < points.length; i++)
            center = center.add(points[i]);
        float quantity = 1.0f / points.length;
        center = center.mult(quantity);

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
     * @param rotate the rotation change.
     * @param translate the translation change.
     * @param scale the size change.
     */
    public BoundingVolume transform(
        Matrix3f rotate,
        Vector3f translate,
        float scale) {
            
        Vector3f newCenter = ((rotate.mult(center)).mult(scale)).add(translate);
        return new BoundingSphere(scale * radius, newCenter);
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on. 
     * @param plane the plane to check against.
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
     * @param volume the sphere to combine with this sphere.
     */
    public BoundingVolume merge(BoundingVolume volume) {
        if(volume == null) {
            return null;
        }
        if (!(volume instanceof BoundingSphere)) {
            return null;
        } else {
            BoundingSphere sphere = (BoundingSphere) volume;
            Vector3f diff = sphere.getCenter().subtract(center);
            float lengthSquared = diff.lengthSquared();
            float radiusDiff = sphere.getRadius() - radius;
            float diffSquared = radiusDiff * radiusDiff;

            if (diffSquared >= lengthSquared) {
                return (radiusDiff >= 0.0 ? volume : this);
            }

            float length = (float) Math.sqrt(lengthSquared);
            float tolerance = 1e-06f;
            BoundingSphere newSphere = new BoundingSphere();

            if (length > tolerance) {
                float coeff = (length + radiusDiff) / (2.0f * length);
                newSphere.setCenter(center.add(diff.mult(coeff)));
            } else {
                newSphere.setCenter(center);
            }

            newSphere.setRadius(0.5f * (length + radius + sphere.getRadius()));
            
            return newSphere;
        }
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: XXX.YYYY Center: <Vector>".
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingSphere [Radius: " + radius + " Center: " 
                + center +"]";
    }
}
