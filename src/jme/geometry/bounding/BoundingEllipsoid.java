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
import jme.math.Matrix;
import jme.math.Vector;

/**
 * <code>BoundingEllipsoid</code> is defined as x^2/a^2 + y^2/b^2 + z^2/c^2 = 1
 * with a center of (0, 0, 0). The ellipsoid can also be expressed as the 
 * center matrix form (and will be in this class) as: (X-C)^T A (X-C) = 1.
 * @author Mark Powell
 * @version $Id: BoundingEllipsoid.java,v 1.4 2003-09-10 20:32:59 mojomonkey Exp $
 *
 */
public class BoundingEllipsoid implements BoundingVolume {
    private Vector center;
    private Matrix a;
    private Matrix inverseA;
    private float collisionBuffer;

    /**
     * @return
     */
    public Matrix getA() {
        return a;
    }

    /**
     * @param a
     */
    public void setA(Matrix a) {
        this.a = a;
    }

    /**
     * @return
     */
    public Vector getCenter() {
        return center;
    }

    /**
     * @param center
     */
    public void setCenter(Vector center) {
        this.center = center;
    }

    /**
     * Constructor instantiates a new <code>BoundingEllipsoid</code> with default
     * attributes.
     *
     */
    public BoundingEllipsoid() {
        center = new Vector();
        a = new Matrix();
        inverseA = new Matrix();
    }

    /**
     * Constructor instantiates a new <code>BoundingEllipsoid</code> with 
     * given parameters.
     * @param center the center of the ellipsoid.
     * @param a the matrix A from (X-C)^T A (X-C) = 1.
     * @param inverseA the inverse of A.
     */
    public BoundingEllipsoid(Vector center, Matrix a, Matrix inverseA) {
        this.center = center;
        this.a = a;
        this.inverseA = inverseA;
    }

    /**
     * <code>gaussianDistribution</code> creates an ellipsoid using the 
     * mean of the points for the center and the eigenvectors for the
     * axes.
     * @param points the points to contain.
     */
    public void gaussianDistribution(Vector[] points) {
        Vector[] axis = new Vector[3];
        for(int i = 0; i < 3; i++) {
            axis[i] = new Vector();
        }
        float[] d = new float[3];
        Approximation.gaussPointsFit(points, center, axis, d);
        float maxValue = 0.0f;
        for (int i = 0; i < points.length; i++) {
            Vector diff = points[i].subtract(center);
            float[] u = {
                    axis[0].dot(diff),
                    axis[1].dot(diff),
                    axis[2].dot(diff)
            };

            float value =
                d[0] * u[0] * u[0]
                    + d[1] * u[1] * u[1]
                    + d[2] * u[2] * u[2];

            if (value > maxValue) {
                maxValue = value;
            }
        }

        Matrix[] tensor = new Matrix[3];
        for(int i = 0; i < 3; i++) {
            tensor[i] = new Matrix();
        }
        tensor[0].tensorProduct(axis[0], axis[0]);
        tensor[1].tensorProduct(axis[1], axis[1]);
        tensor[2].tensorProduct(axis[2], axis[2]);

        float inv = 1.0f / maxValue;
        tensor[0].multiply(inv * d[0]);
        tensor[1].multiply(inv * d[1]);
        tensor[2].multiply(inv * d[2]);
        tensor[1].add(tensor[2]);
        tensor[0].add(tensor[1]);
        a = tensor[0];

    }
    
    /**
     * <code>setCollisionBuffer</code> sets the value that must be reached to
     * consider bounding volumes colliding. By default this value is 0.
     * @param buffer the collision buffer.
     */
    public void setCollisionBuffer(float buffer) {
        collisionBuffer = buffer;
    }
    
    /**
     * <code>hasCollision</code> will determine if this volume is colliding
     * (touching in any way) with another volume.
     * @param sourceOffset defines the position of the entity containing
     *      this volume, if null it is ignored.
     * @param volume the bounding volume to compare.
     * @param targetOffset defines the position of the entity containing
     *      the target volume, if null it is ignored.
     * @return true if there is a collision, false otherwise.
     */
    public boolean hasCollision(Vector sourceOffset, BoundingVolume volume, 
            Vector targetOffset) {
        return false;
    }

    public float distance(Vector sourceOffset, BoundingVolume volume, 
            Vector targetOffset) {
        return -1.0f;
    }
    
    public boolean isVisible(Vector offsetPosition, Frustum frustum) {
        return true;
    }
}
