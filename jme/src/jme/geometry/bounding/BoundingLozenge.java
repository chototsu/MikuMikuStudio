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
import jme.math.Rectangle;
import jme.math.Vector;

/**
 * <code>BoundingLozenge</code> is a natural extension of a sphere based on
 * equaldistance (from Eberly). It is the set of all points that are a distance
 * r (where r > 0) from a rectangle with origin P and edge directions E1 and E2.
 * <br><br>
 * <b>NOTE:</b> See 3D Game Engine Design. David H. Eberly.
 * @author Mark Powell
 * @version $Id: BoundingLozenge.java,v 1.1.1.1 2003-10-29 10:57:30 Anakan Exp $
 */
public class BoundingLozenge implements BoundingVolume {
    private Rectangle rectangle;
    private float radius;
    private float collisionBuffer;

    /**
     * @return
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * @return
     */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
     * @param rectangle
     */
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * Constructor instantiates a new <code>BoundingLozenge</code> object. 
     */
    public BoundingLozenge() {
        rectangle = new Rectangle();
    }

    /**
     * Constructor instantiates a new <code>BoundingLozenge</code> object. The
     * rectangle and radius that defines the Lozenge is set during this 
     * construction.
     * @param rectangle the rectangle that defines the center are of the lozenge.
     * @param radius the radius from the rectangle.
     */
    public BoundingLozenge(Rectangle rectangle, float radius) {
        this.rectangle = rectangle;
        this.radius = radius;
    }

    /**
     * <code>gaussianDistribution</code> calculates the average of the points,
     * and a covariance matrix to determine the rectangle of the lozenge. The
     * radius is than calculated to best fit all the points.
     * @param points the list of points to contain.
     */
    public void gaussianDistribution(Vector[] points) {
        Vector center = new Vector();
        Vector[] axis = new Vector[3];
        for(int i = 0; i < axis.length; i++) {
            axis[i] = new Vector();
        }
        float[] extent = new float[3];
        Approximation.gaussPointsFit(points, center, axis, extent);

        Vector diff = points[0].subtract(center);
        float wMin = axis[0].dot(diff);
        float wMax = wMin;
        float w;

        for (int i = 1; i < points.length; i++) {
            diff = points[i].subtract(center);
            w = axis[0].dot(diff);
            if (w < wMin) {
                wMin = w;
            } else if (w > wMax) {
                wMax = w;
            }
        }

        float tempRadius = 0.5f * (wMax - wMin);
        float tempRadiusSquared = tempRadius * tempRadius;
        center = center.add((axis[0].mult(0.5f * (wMax + wMin))));

        float aMin = Float.MAX_VALUE;
        float aMax = Float.MIN_VALUE;
        float bMin = Float.MAX_VALUE;
        float bMax = Float.MIN_VALUE;

        float discr;
        float radical;
        float u;
        float v;
        float test;

        for (int i = 0; i < points.length; i++) {
            diff = points[i].subtract(center);
            u = axis[2].dot(diff);
            v = axis[1].dot(diff);
            w = axis[0].dot(diff);
            discr = tempRadiusSquared - w * w;
            radical = (float) Math.sqrt(Math.abs(discr));

            test = u + radical;
            if (test < aMin) {
                aMin = test;
            }

            test = u - radical;
            if (test > aMax) {
                aMax = test;
            }

            test = v + radical;
            if (test < bMin) {
                bMin = test;
            }

            test = v - radical;
            if (test > bMax) {
                bMax = test;
            }
        }

        // enclosing region might be a capsule or a sphere
        if (aMin >= aMax) {
            test = 0.5f * (aMin + aMax);
            aMin = test;
            aMax = test;
        }
        if (bMin >= bMax) {
            test = 0.5f * (bMin + bMax);
            bMin = test;
            bMax = test;
        }

        // Make correction for points inside mitered corner but outside quarter
        // sphere.
        for (int i = 0; i < points.length; i++) {
            diff = points[i].subtract(center);
            u = axis[2].dot(diff);
            v = axis[1].dot(diff);

            float aExtreme = 0;
            float bExtreme = 0;

            if (u > aMax) {
                if (v > bMax) {
                    aExtreme = aMax;
                    bExtreme = bMax;
                } else if (v < bMin) {
                    aExtreme = aMax;
                    bExtreme = bMin;
                }
            } else if (u < aMin) {
                if (v > bMax) {
                    aExtreme = aMin;
                    bExtreme = bMax;
                } else if (v < bMin) {
                    aExtreme = aMin;
                    bExtreme = bMin;
                }
            }

            if (aExtreme != 0) {
                float deltaU = u - aExtreme;
                float deltaV = v - bExtreme;
                float deltaSumSquared = deltaU * deltaU + deltaV * deltaV;
                w = axis[0].dot(diff);
                float wSquared = w * w;
                test = deltaSumSquared + wSquared;
                if (test > tempRadiusSquared) {
                    discr = (tempRadiusSquared - wSquared) / deltaSumSquared;
                    float t = (float) - Math.sqrt(Math.abs(discr));
                    aExtreme = u + t * deltaU;
                    bExtreme = v + t * deltaV;
                }
            }
        }

        if (aMin < aMax) {
            if (bMin < bMax) {
                rectangle.setOrigin(
                    center.add(axis[2].mult(aMin).add(axis[1].mult(bMin))));
                rectangle.setFirstEdge(axis[2].mult((aMax - aMin)));
                rectangle.setSecondEdge(axis[1].mult((bMax - bMin)));
            } else {
                // enclosing lozenge is really a capsule
                rectangle.setOrigin(
                    center.add(
                        axis[2].mult(aMin).add(
                            axis[1].mult((0.5f * (bMin + bMax))))));
                rectangle.setFirstEdge(axis[2].mult((aMax - aMin)));
                rectangle.setSecondEdge(new Vector());
            }
        } else {
            if (bMin < bMax) {
                // enclosing lozenge is really a capsule
                rectangle.setOrigin(
                    center.add(
                        axis[2].mult(0.5f * (aMin + aMax)).add(
                            axis[1].mult(bMin))));
                rectangle.setFirstEdge(new Vector());
                rectangle.setSecondEdge(axis[1].mult((bMax - bMin)));
            } else {
                // enclosing lozenge is really a sphere
                rectangle.setOrigin(
                    center.add(
                        axis[2].mult(0.5f * (aMin + aMax)).add(
                            axis[1].mult(0.5f * (bMin + bMax)))));
                rectangle.setFirstEdge(new Vector());
                rectangle.setSecondEdge(new Vector());
            }
        }

        radius = tempRadius;

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
