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
package jme.math;

/**
 * <code>Distance</code> is a static class that provides commonly used math
 * functions.
 * 
 * <br><br>
 * <b>NOTE:</b> See 3D Game Engine Design. David H. Eberly.
 * @author Mark Powell
 *
 */
public class Distance {
    private final static float TOLERANCE = .00001f;

    /**
     * <code>distancePointPoint</code> calculates the distance between two points. These
     * points are described as a <code>Vector</code> object.
     * @param point1 the first point.
     * @param point2 the second point.
     * @return the distance between point1 and point2.
     */
    public static float distancePointPoint(Vector point1, Vector point2) {
        return (float) Math.sqrt(distancePointPointSquared(point1, point2));
    }

    /**
     * <code>distancePointPointSquared</code> calculates the distance squared
     * between two points. These points are described as a 
     * <code>Vector</code> object.
     * @param point1 the first point.
     * @param point2 the second point.
     * @return the distance squared between point1 and point2.
     */
    public static float distancePointPointSquared(
        Vector point1,
        Vector point2) {
        return ((point1.x - point2.x) * (point1.x - point2.x))
            + ((point1.y - point2.y) * (point1.y - point2.y))
            + ((point1.z - point2.z) * (point1.z - point2.z));
    }

    /**
     * <code>distancePointLineSquared</code> calculates the distance squared
     * between a point and a line. 
     * @param point the point to check.
     * @param line the line to check.
     * @return the distance squared between a point and line.
     */
    public static float distancePointLineSquared(Vector point, Line line) {
        Vector diff = point.subtract(line.getOrigin());
        float squareLen = line.getDirection().lengthSquared();
        float t = diff.dot(line.getDirection()) / squareLen;
        diff = diff.subtract(line.getDirection().mult(t));

        return diff.lengthSquared();
    }

    /**
     * <code>distancePointRaySquared</code> calculates the distance
     * squared between a point and a ray.
     * @param point the point to check.
     * @param ray the ray to check.
     * @return the distance between a point and ray.
     */
    public static float distancePointRaySquared(Vector point, Line ray) {
        Vector diff = point.subtract(ray.getOrigin());
        float t = diff.dot(ray.getDirection());

        if (t <= 0.0) {
            t = 0.0f;
        } else {
            t /= ray.getDirection().lengthSquared();
            diff = diff.subtract(ray.getDirection().mult(t));
        }

        return diff.lengthSquared();
    }

    /**
     * <code>distancePointSegmentSquared</code> calculates the distance
     * squared between a point and a line segment.
     * @param point the point to check.
     * @param seg the line segment to check.
     * @return the distance squared between a point and line segment.
     */
    public static float distancePointSegmentSquared(Vector point, Line seg) {
        Vector diff = point.subtract(seg.getOrigin());
        float t = diff.dot(seg.getDirection());

        if (t <= 0.0f) {
            t = 0.0f;
        } else {
            float lengthSquared = seg.getDirection().lengthSquared();
            if (t >= lengthSquared) {
                t = 1.0f;
                diff = diff.subtract(seg.getDirection());
            } else {
                t /= lengthSquared;
                diff = diff.subtract(seg.getDirection().mult(t));
            }
        }

        return diff.lengthSquared();
    }

    /**
     * <code>distancePointRectangle</code> calculates the distance squared
     * between a point and a rectangle.
     * @param point the point to check.
     * @param rect the rectangle to check.
     * @return the distance between the point and the rectangle.
     */
    public static float distancePointRectangle(Vector point, Rectangle rect) {
        Vector diff = rect.getOrigin().subtract(point);
        float a00 = rect.getFirstEdge().lengthSquared();
        float a11 = rect.getSecondEdge().lengthSquared();
        float b0 = diff.dot(rect.getFirstEdge());
        float b1 = diff.dot(rect.getSecondEdge());
        float s = -b0;
        float t = -b1;

        float distanceSquared = diff.lengthSquared();

        if (s < 0.0) {
            s = 0.0f;
        } else if (s <= a00) {
            s /= a00;
            distanceSquared += b0 * s;
        } else {
            s = 1.0f;
            distanceSquared += a00 + 2.0 * b0;
        }

        if (t < 0.0) {
            t = 0.0f;
        } else if (t <= a11) {
            t /= a11;
            distanceSquared += b1 * t;
        } else {
            t = 1.0f;
            distanceSquared += a11 + 2.0 * b1;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineLineSquared</code> calculates the distance squared
     * between two lines.
     * @param line1 the first line to check.
     * @param line2 the second line to check.
     * @return the distance squared between two lines.
     */
    public static float distanceLineLineSquared(Line line1, Line line2) {
        Vector diff = line1.getOrigin().subtract(line2.getOrigin());
        float a = line1.getDirection().lengthSquared();
        float b = -line1.getDirection().dot(line2.getDirection());
        float c = line2.getDirection().lengthSquared();
        float d = diff.dot(line1.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float distanceSquared;

        if (determinate >= TOLERANCE) {
            // lines are not parallel
            e = -diff.dot(line2.getDirection());
            float inverseDeterminate = 1.0f / determinate;
            s = (b * e - c * d) * inverseDeterminate;
            t = (b * d - a * e) * inverseDeterminate;
            distanceSquared =
                s * (a * s + b * t + 2.0f * d)
                    + t * (b * s + c * t + 2.0f * e)
                    + f;
        } else {
            // lines are parallel, select any closest pair of points
            s = -d / a;
            t = 0.0f;
            distanceSquared = d * s + f;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineRaySquared</code> calculates the squared distance
     * between a line and a ray. 
     * @param line the line to check.
     * @param ray the ray to check.
     * @return the distance between the line and the ray.
     */
    public static float distanceLineRaySquared(Line line, Line ray) {
        Vector diff = line.getOrigin().subtract(ray.getOrigin());
        float a = line.getDirection().lengthSquared();
        float b = -line.getDirection().dot(ray.getDirection());
        float c = ray.getDirection().lengthSquared();
        float d = diff.dot(line.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float distanceSquared;

        if (determinate >= TOLERANCE) {
            e = -diff.dot(ray.getDirection());
            t = b * d - a * e;

            if (t >= 0.0) {
                // two interior points are closest, one on line and one on ray
                float inverseDeterminate = 1.0f / determinate;
                s = (b * e - c * d) * inverseDeterminate;
                t *= inverseDeterminate;
                distanceSquared =
                    s * (a * s + b * t + 2.0f * d)
                        + t * (b * s + c * t + 2.0f * e)
                        + f;
            } else {
                // end point of ray and interior point of line are closest
                s = -d / a;
                t = 0.0f;
                distanceSquared = d * s + f;
            }
        } else {
            // lines are parallel, closest pair with one point at ray origin
            s = -d / a;
            t = 0.0f;
            distanceSquared = d * s + f;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineSegementSquared</code> calculates the distance
     * squared between a line and a line segment.
     * @param line the line to check.
     * @param seg the line segment to check.
     * @return the distance squared between a line and a line segment.
     */
    public static float distanceLineSegmentSquared(Line line, Line seg) {
        Vector diff = line.getOrigin().subtract(seg.getOrigin());
        float a = line.getDirection().lengthSquared();
        float b = -line.getDirection().dot(seg.getDirection());
        float c = seg.getDirection().lengthSquared();
        float d = diff.dot(line.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float squareDistance;

        if (determinate >= TOLERANCE) {
            e = -diff.dot(seg.getDirection());
            t = b * d - a * e;

            if (t >= 0.0) {
                if (t <= determinate) {
                    // two interior points are closest, one on line and one on
                    // segment
                    float fInvDet = 1.0f / determinate;
                    s = (b * e - c * d) * fInvDet;
                    t *= fInvDet;
                    squareDistance =
                        s * (a * s + b * t + 2.0f * d)
                            + t * (b * s + c * t + 2.0f * e)
                            + f;
                } else {
                    // end point e1 of segment and interior point of line are
                    // closest
                    float temp = b + d;
                    s = -temp / a;
                    t = 1.0f;
                    squareDistance = temp * s + c + 2.0f * e + f;
                }
            } else {
                // end point e0 of segment and interior point of line are closest
                s = -d / a;
                t = 0.0f;
                squareDistance = d * s + f;
            }
        } else {
            // lines are parallel, closest pair with one point at segment origin
            s = -d / a;
            t = 0.0f;
            squareDistance = d * s + f;
        }

        return Math.abs(squareDistance);
    }
    
    /**
     * <code>distanceRayRaySquared</code> calculates the distance squared
     * between two rays.
     * @param ray1 the first ray to check.
     * @param ray2 the second ray to check.
     * @return the distance squared between the two rays.
     */
    public static float distanceRayRaySquared(Line ray1, Line ray2) {
        Vector diff = ray1.getOrigin().subtract(ray2.getOrigin());
        float a = ray1.getDirection().lengthSquared();
        float b = -ray1.getDirection().dot(ray2.getDirection());
        float c = ray2.getDirection().lengthSquared();
        float d = diff.dot(ray1.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float distanceSquared;

        if (determinate >= TOLERANCE) {
            // rays are not parallel
            e = -diff.dot(ray2.getDirection());
            s = b * e - c * d;
            t = b * d - a * e;

            if (s >= 0.0) {
                if (t >= 0.0) // region 0 (interior)
                    {
                    // minimum at two interior points of rays
                    float inverseDeterminate = 1.0f / determinate;
                    s *= inverseDeterminate;
                    t *= inverseDeterminate;
                    distanceSquared =
                        s * (a * s + b * t + 2.0f * d)
                            + t * (b * s + c * t + 2.0f * e)
                            + f;
                } else // region 3 (side)
                    {
                    t = 0.0f;
                    if (d >= 0.0) {
                        s = 0.0f;
                        distanceSquared = f;
                    } else {
                        s = -d / a;
                        distanceSquared = d * s + f;
                    }
                }
            } else {
                if (t >= 0.0) // region 1 (side)
                    {
                    s = 0.0f;
                    if (e >= 0.0) {
                        t = 0.0f;
                        distanceSquared = f;
                    } else {
                        t = -e / c;
                        distanceSquared = e * t + f;
                    }
                } else // region 2 (corner)
                    {
                    if (d < 0.0) {
                        s = -d / a;
                        t = 0.0f;
                        distanceSquared = d * s + f;
                    } else {
                        s = 0.0f;
                        if (e >= 0.0) {
                            t = 0.0f;
                            distanceSquared = f;
                        } else {
                            t = -e / c;
                            distanceSquared = e * t + f;
                        }
                    }
                }
            }
        } else {
            // rays are parallel
            if (b > 0.0) {
                // opposite direction vectors
                t = 0.0f;
                if (d >= 0.0) {
                    s = 0.0f;
                    distanceSquared = f;
                } else {
                    s = -d / a;
                    distanceSquared = d * s + f;
                }
            } else {
                // same direction vectors
                if (d >= 0.0) {
                    e = -diff.dot(ray2.getDirection());
                    s = 0.0f;
                    t = -e / c;
                    distanceSquared = e * t + f;
                } else {
                    s = -d / a;
                    t = 0.0f;
                    distanceSquared = d * s + f;
                }
            }
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceRaySegmentSquared</code> calculates the distance
     * squared between a ray and a line segment. 
     * @param ray the ray to check.
     * @param seg the line segment to check.
     * @return the distance between the ray and the line segment.
     */
    public static float distanceRaySegmentSquared(Line ray, Line seg) {
        Vector diff = ray.getOrigin().subtract(seg.getOrigin());
        float a = ray.getDirection().lengthSquared();
        float b = -ray.getDirection().dot(seg.getDirection());
        float c = seg.getDirection().lengthSquared();
        float d = diff.dot(ray.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float distanceSquared;
        float temp;

        if (determinate >= TOLERANCE) {
            // ray and segment are not parallel
            e = -diff.dot(seg.getDirection());
            s = b * e - c * d;
            t = b * d - a * e;

            if (s >= 0.0) {
                if (t >= 0.0) {
                    if (t <= determinate) // region 0
                        {
                        // minimum at interior points of ray and segment
                        float inverseDeterminate = 1.0f / determinate;
                        s *= inverseDeterminate;
                        t *= inverseDeterminate;
                        distanceSquared =
                            s * (a * s + b * t + 2.0f * d)
                                + t * (b * s + c * t + 2.0f * e)
                                + f;
                    } else // region 1
                        {
                        t = 1.0f;
                        if (d >= -b) {
                            s = 0.0f;
                            distanceSquared = c + 2.0f * e + f;
                        } else {
                            temp = b + d;
                            s = -temp / a;
                            distanceSquared = temp * s + c + 2.0f * e + f;
                        }
                    }
                } else // region 5
                    {
                    t = 0.0f;
                    if (d >= 0.0) {
                        s = 0.0f;
                        distanceSquared = f;
                    } else {
                        s = -d / a;
                        distanceSquared = d * s + f;
                    }
                }
            } else {
                if (t <= 0.0) // region 4
                    {
                    if (d < 0.0) {
                        s = -d / a;
                        t = 0.0f;
                        distanceSquared = d * s + f;
                    } else {
                        s = 0.0f;
                        if (e >= 0.0) {
                            t = 0.0f;
                            distanceSquared = f;
                        } else if (-e >= c) {
                            t = 1.0f;
                            distanceSquared = c + 2.0f * e + f;
                        } else {
                            t = -e / c;
                            distanceSquared = e * t + f;
                        }
                    }
                } else if (t <= determinate) // region 3
                    {
                    s = 0.0f;
                    if (e >= 0.0) {
                        t = 0.0f;
                        distanceSquared = f;
                    } else if (-e >= c) {
                        t = 1.0f;
                        distanceSquared = c + 2.0f * e + f;
                    } else {
                        t = -e / c;
                        distanceSquared = e * t + f;
                    }
                } else // region 2
                    {
                    temp = b + d;
                    if (temp < 0.0) {
                        s = -temp / a;
                        t = 1.0f;
                        distanceSquared = temp * s + c + 2.0f * e + f;
                    } else {
                        s = 0.0f;
                        if (e >= 0.0) {
                            t = 0.0f;
                            distanceSquared = f;
                        } else if (-e >= c) {
                            t = 1.0f;
                            distanceSquared = c + 2 * e + f;
                        } else {
                            t = -e / c;
                            distanceSquared = e * t + f;
                        }
                    }
                }
            }
        } else {
            // ray and segment are parallel
            if (b > 0.0) {
                // opposite direction vectors
                t = 0.0f;
                if (d >= 0.0) {
                    s = 0.0f;
                    distanceSquared = f;
                } else {
                    s = -d / a;
                    distanceSquared = d * s + f;
                }
            } else {
                // same direction vectors
                e = -diff.dot(seg.getDirection());
                t = 1.0f;
                temp = b + d;
                if (temp >= 0.0) {
                    s = 0.0f;
                    distanceSquared = c + 2.0f * e + f;
                } else {
                    s = -temp / a;
                    distanceSquared = temp * s + c + 2.0f * e + f;
                }
            }
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceSegmentSegmentSquared</code> calculates the distance
     * squared between two line segments.
     * @param seg1 the first line segment to check.
     * @param seg2 the second line segment to check.
     * @return the distance between two line segments.
     */
    public static float distanceSegmentSegmentSquared(Line seg1, Line seg2) {
        Vector diff = seg1.getOrigin().subtract(seg2.getOrigin());
        float a = seg1.getDirection().lengthSquared();
        float b = -seg1.getDirection().dot(seg2.getDirection());
        float c = seg2.getDirection().lengthSquared();
        float d = diff.dot(seg1.getDirection());
        float f = diff.lengthSquared();
        float determinate = Math.abs(a * c - b * b);
        float e;
        float s;
        float t;
        float distanceSquared;
        float temp;

        if (determinate >= TOLERANCE) {
            // line segments are not parallel
            e = -diff.dot(seg2.getDirection());
            s = b * e - c * d;
            t = b * d - a * e;

            if (s >= 0.0) {
                if (s <= determinate) {
                    if (t >= 0.0) {
                        if (t <= determinate) // region 0 (interior)
                            {
                            // minimum at two interior points of 3D lines
                            float inverseDeterminate = 1.0f / determinate;
                            s *= inverseDeterminate;
                            t *= inverseDeterminate;
                            distanceSquared =
                                s * (a * s + b * t + 2.0f * d)
                                    + t * (b * s + c * t + 2.0f * e)
                                    + f;
                        } else // region 3 (side)
                            {
                            t = 1.0f;
                            temp = b + d;
                            if (temp >= 0.0f) {
                                s = 0.0f;
                                distanceSquared = c + 2.0f * e + f;
                            } else if (-temp >= a) {
                                s = 1.0f;
                                distanceSquared = a + c + f + 2.0f * (e + temp);
                            } else {
                                s = -temp / a;
                                distanceSquared = temp * s + c + 2.0f * e + f;
                            }
                        }
                    } else // region 7 (side)
                        {
                        t = 0.0f;
                        if (d >= 0.0f) {
                            s = 0.0f;
                            distanceSquared = f;
                        } else if (-d >= a) {
                            s = 1.0f;
                            distanceSquared = a + 2.0f * d + f;
                        } else {
                            s = -d / a;
                            distanceSquared = d * s + f;
                        }
                    }
                } else {
                    if (t >= 0.0f) {
                        if (t <= determinate) // region 1 (side)
                            {
                            s = 1.0f;
                            temp = b + e;
                            if (temp >= 0.0f) {
                                t = 0.0f;
                                distanceSquared = a + 2.0f * d + f;
                            } else if (-temp >= c) {
                                t = 1.0f;
                                distanceSquared = a + c + f + 2.0f * (d + temp);
                            } else {
                                t = -temp / c;
                                distanceSquared = temp * t + a + 2.0f * d + f;
                            }
                        } else // region 2 (corner)
                            {
                            temp = b + d;
                            if (-temp <= a) {
                                t = 1.0f;
                                if (temp >= 0.0f) {
                                    s = 0.0f;
                                    distanceSquared = c + 2.0f * e + f;
                                } else {
                                    s = -temp / a;
                                    distanceSquared =
                                        temp * s + c + 2.0f * e + f;
                                }
                            } else {
                                s = 1.0f;
                                temp = b + e;
                                if (temp >= 0.0f) {
                                    t = 0.0f;
                                    distanceSquared = a + 2.0f * d + f;
                                } else if (-temp >= c) {
                                    t = 1.0f;
                                    distanceSquared =
                                        a + c + f + 2.0f * (d + temp);
                                } else {
                                    t = -temp / c;
                                    distanceSquared =
                                        temp * t + a + 2.0f * d + f;
                                }
                            }
                        }
                    } else // region 8 (corner)
                        {
                        if (-d < a) {
                            t = 0.0f;
                            if (d >= 0.0f) {
                                s = 0.0f;
                                distanceSquared = f;
                            } else {
                                s = -d / a;
                                distanceSquared = d * s + f;
                            }
                        } else {
                            s = 1.0f;
                            temp = b + e;
                            if (temp >= 0.0f) {
                                t = 0.0f;
                                distanceSquared = a + 2.0f * d + f;
                            } else if (-temp >= c) {
                                t = 1.0f;
                                distanceSquared = a + c + f + 2.0f * (d + temp);
                            } else {
                                t = -temp / c;
                                distanceSquared = temp * t + a + 2.0f * d + f;
                            }
                        }
                    }
                }
            } else {
                if (t >= 0.0f) {
                    if (t <= determinate) // region 5 (side)
                        {
                        s = 0.0f;
                        if (e >= 0.0f) {
                            t = 0.0f;
                            distanceSquared = f;
                        } else if (-e >= c) {
                            t = 1.0f;
                            distanceSquared = c + 2.0f * e + f;
                        } else {
                            t = -e / c;
                            distanceSquared = e * t + f;
                        }
                    } else // region 4 (corner)
                        {
                        temp = b + d;
                        if (temp < 0.0f) {
                            t = 1.0f;
                            if (-temp >= a) {
                                s = 1.0f;
                                distanceSquared = a + c + f + 2.0f * (e + temp);
                            } else {
                                s = -temp / a;
                                distanceSquared = temp * s + c + 2.0f * e + f;
                            }
                        } else {
                            s = 0.0f;
                            if (e >= 0.0f) {
                                t = 0.0f;
                                distanceSquared = f;
                            } else if (-e >= c) {
                                t = 1.0f;
                                distanceSquared = c + 2.0f * e + f;
                            } else {
                                t = -e / c;
                                distanceSquared = e * t + f;
                            }
                        }
                    }
                } else // region 6 (corner)
                    {
                    if (d < 0.0f) {
                        t = 0.0f;
                        if (-d >= a) {
                            s = 1.0f;
                            distanceSquared = a + 2.0f * d + f;
                        } else {
                            s = -d / a;
                            distanceSquared = d * s + f;
                        }
                    } else {
                        s = 0.0f;
                        if (e >= 0.0f) {
                            t = 0.0f;
                            distanceSquared = f;
                        } else if (-e >= c) {
                            t = 1.0f;
                            distanceSquared = c + 2.0f * e + f;
                        } else {
                            t = -e / c;
                            distanceSquared = e * t + f;
                        }
                    }
                }
            }
        } else {
            // line segments are parallel
            if (b > 0.0f) {
                // direction vectors form an obtuse angle
                if (d >= 0.0f) {
                    s = 0.0f;
                    t = 0.0f;
                    distanceSquared = f;
                } else if (-d <= a) {
                    s = -d / a;
                    t = 0.0f;
                    distanceSquared = d * s + f;
                } else {
                    e = -diff.dot(seg2.getDirection());
                    s = 1.0f;
                    temp = a + d;
                    if (-temp >= b) {
                        t = 1.0f;
                        distanceSquared = a + c + f + 2.0f * (b + d + e);
                    } else {
                        t = -temp / b;
                        distanceSquared =
                            a + 2.0f * d + f + t * (c * t + 2.0f * (b + e));
                    }
                }
            } else {
                // direction vectors form an acute angle
                if (-d >= a) {
                    s = 1.0f;
                    t = 0.0f;
                    distanceSquared = a + 2.0f * d + f;
                } else if (d <= 0.0f) {
                    s = -d / a;
                    t = 0.0f;
                    distanceSquared = d * s + f;
                } else {
                    e = -diff.dot(seg2.getDirection());
                    s = 0.0f;
                    if (d >= -b) {
                        t = 1.0f;
                        distanceSquared = c + 2.0f * e + f;
                    } else {
                        t = -d / b;
                        distanceSquared = f + t * (2.0f * e + c * t);
                    }
                }
            }
        }

        return Math.abs(distanceSquared);
    }
}
