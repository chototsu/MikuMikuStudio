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
package jme.geometry.bounding;

import jme.entity.camera.Frustum;
import jme.math.Approximation;
import jme.math.Distance;
import jme.math.Line;
import jme.math.Vector;

/**
 * <code>BoundingCylinder</code> defines a cylinder as a set of all points a
 * distance r from a line P + tD where D is unit length. We are defining the
 * cylinder as a subset of this line and therefore finite. All points can be
 * also defined by the equation Xi = P + RYi where R = [U V D], and U V causes
 * R to be orthonormal.
 * @author Mark Powell
 * @version $Id: BoundingCylinder.java,v 1.3 2003-09-08 20:29:28 mojomonkey Exp $
 */
public class BoundingCylinder implements BoundingVolume {
    private Vector center; //P
    private Vector direction; //D
    private Vector u;
    private Vector v;
    private float height;
    private float radius;

    /**
     * Constructor instantiates a new <code>BoundingCylinder</code> with
     * default (zero) attributes.
     *
     */
    public BoundingCylinder() {

        center = new Vector();
        direction = new Vector();
        u = new Vector();
        v = new Vector();
    }

    /**
     * Constructor instantiates a new <code>BoundingCylinder</code> with
     * set attributes.
     * @param center the center of the cylinder.
     * @param direction the orientation of the cylinder.
     * @param u the U unit vector to make [u v direction] orthonormal.
     * @param v the V unit vector to make [u v direction] orthonormal.
     * @param height the height of the cylinder.
     * @param radius the radius of the cylinder.
     */
    public BoundingCylinder(
        Vector center,
        Vector direction,
        Vector u,
        Vector v,
        float height,
        float radius) {

        this.center = center;
        this.direction = direction;
        this.u = u;
        this.v = v;
        this.height = height;
        this.radius = radius;
    }

    /**
     * <code>generateCoordinateSystem</code> calculates the matrix 
     * R = [U V D]. 
     *
     */
    public void generateCoordinateSystem() {
        Vector.generateOrthonormalBasis(u, v, direction, true);
    }

    /**
     * <code>leastSquaresFit</code> builds a cylinder from a collection of 
     * points where the radius is the distance from a line approximation to the
     * furthest point and the height is the direction(max) - direction(min). It
     * is assumed that the matrix [U V D] has been predefined either through
     * construction or calling <code>generateCoordinateSystem</code>.
     * @param points the collection of points that the cylinder contains.
     */
    public void leastSquaresFit(Vector[] points) {

        Line line = Approximation.orthogonalLineFit(points);

        float maxRadiusSquared = 0.0f;
        for (int i = 0; i < points.length; i++) {
            float radiusSquared = Distance.distancePointLineSquared(points[i], line);
            if (radiusSquared > maxRadiusSquared) {
                maxRadiusSquared = radiusSquared;
            }
        }

        Vector diff = points[0].subtract(line.getOrigin());
        float wMin = line.getDirection().dot(diff);
        float wMax = wMin;
        for (int i = 1; i < points.length; i++) {
            diff = points[i].subtract(line.getOrigin());
            float w = line.getDirection().dot(diff);
            if (w < wMin) {
                wMin = w;
            } else if (w > wMax)
                wMax = w;
        }

        center =
            line.getOrigin().add(
                line.getDirection().mult(0.5f * (wMax + wMin)));
        direction = line.getDirection();
        radius = (float) Math.sqrt(maxRadiusSquared);
        height = wMax - wMin;

    }
    
    public boolean hasCollision(BoundingVolume volume) {
        return false;
    }

    public float distance(BoundingVolume volume) {
        return -1.0f;
    }
    
    public boolean isVisible(Frustum frustum) {
        return true;
    }

}
