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
 * functions. All tests contain a set of three methods. First is a method to
 * calculate the distance, second is to calculate the distance square and third
 * is to calculate the distance squared and keep a copy of the parameters 
 * defined for the distance algorithms.
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
        System.out.println("Checking: " + point1 + " " + point2);
        float value =
            (float) Math.sqrt(distancePointPointSquared(point1, point2));
        System.out.println("RETURNING " + value);
        return value;
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
     * <code>distancePointLine</code> calculates teh distance between a 
     * point and a line.
     * @param point the point to check.
     * @param line the line to check.
     * @return the distance between the point and the line.
     */
    public static float distancePointLine(Vector point, Line line) {
        return (float) Math.sqrt(distancePointLineSquared(point, line));
    }

    /**
     * <code>distancePointLineSquared</code> calculates the distance squared
     * between a point and a line.
     * @param point the point to check.
     * @param line the line to check.
     * @return the distance squared between a point and a line.
     */
    public static float distancePointLineSquared(Vector point, Line line) {
        return distancePointLineSquared(point, line, null);
    }

    /**
     * <code>distancePointLineSquared</code> calculates the distance squared
     * between a point and a line. 
     * @param point the point to check.
     * @param line the line to check.
     * @param lineParam container for t where L(t) = B + tM.
     * @return the distance squared between a point and line.
     */
    public static float distancePointLineSquared(
        Vector point,
        Line line,
        float[] lineParam) {

        Vector diff = point.subtract(line.getOrigin());
        float squareLen = line.getDirection().lengthSquared();
        float t = diff.dot(line.getDirection()) / squareLen;
        diff = diff.subtract(line.getDirection().mult(t));

        if (lineParam != null) {
            lineParam[0] = t;
        }

        return diff.lengthSquared();
    }

    /**
     * <code>distancePointRay</code> calculates the distance between a point
     * and a line.
     * @param point the point to check.
     * @param ray the ray to check.
     * @return the distance between a point and a ray.
     */
    public static float distancePointRay(Vector point, Line ray) {
        return (float) Math.sqrt(distancePointRaySquared(point, ray));
    }

    /**
     * <code>distancePointRaySquared</code> calculates the distance
     * squared between a point and a ray.
     * @param point the point to check.
     * @param ray the ray to check.
     * @return the distance between a point and ray.
     */
    public static float distancePointRaySquared(Vector point, Line ray) {
        return distancePointRaySquared(point, ray, null);
    }

    /**
     * <code>distancePointRaySquared</code> calculates the distance
     * squared between a point and a ray.
     * @param point the point to check.
     * @param ray the ray to check.
     * @param rayParam container for t where L(t) = B + tM and t >= 0.
     * @return the distance between a point and ray.
     */
    public static float distancePointRaySquared(
        Vector point,
        Line ray,
        float[] rayParam) {

        Vector diff = point.subtract(ray.getOrigin());
        float t = diff.dot(ray.getDirection());

        if (t <= 0.0) {
            t = 0.0f;
        } else {
            t /= ray.getDirection().lengthSquared();
            diff = diff.subtract(ray.getDirection().mult(t));
        }

        if (rayParam != null) {
            rayParam[0] = t;
        }

        return diff.lengthSquared();
    }

    /**
     * <code>distancePointSegment</code> calculates the distance between a
     * point and a line segment.
     * @param point the point to check.
     * @param seg the line segment to check.
     * @return the distance between a point and a line segment.
     */
    public static float distancePointSegment(Vector point, Line seg) {
        return (float) Math.sqrt(distancePointSegmentSquared(point, seg));
    }

    /**
     * <code>distancePointSegmentSquared</code> calculates the distance
     * squared between a point and a line segment.
     * @param point the point to check.
     * @param seg the line segment to check.
     * @return the distance squared between a point and line segment.
     */
    public static float distancePointSegmentSquared(Vector point, Line seg) {
        return distancePointSegmentSquared(point, seg, null);
    }

    /**
     * <code>distancePointSegmentSquared</code> calculates the distance
     * squared between a point and a line segment.
     * @param point the point to check.
     * @param seg the line segment to check.
     * @param lineParam storage for t where L(t) = B + tM and t is between (0,1).
     * @return the distance squared between a point and line segment.
     */
    public static float distancePointSegmentSquared(
        Vector point,
        Line seg,
        float[] lineParam) {

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
     * <code>distancePointRectangle</code> calculates the distance between a 
     * pointa and a rectangle.
     * @param point the point to check.
     * @param rect the rectangle to check.
     * @return the distance between the point and the rectangle.
     */
    public static float distancePointRectangle(Vector point, Rectangle rect) {
        return (float) Math.sqrt(distancePointRectangleSquared(point, rect));
    }

    /**
     * <code>distancePointRectangleSquared</code> calculates the distance squared
     * between a point and a rectangle.
     * @param point the point to check.
     * @param rect the rectangle to check.
     * @return the distance between the point and the rectangle.
     */
    public static float distancePointRectangleSquared(
        Vector point,
        Rectangle rect) {

        return distancePointRectangleSquared(point, rect, null, null);
    }

    /**
     * <code>distancePointRectangleSquared</code> calculates the distance squared
     * between a point and a rectangle.
     * @param point the point to check.
     * @param rect the rectangle to check.
     * @param sParam storage for the s value in the equation: Q(s,t) = |T(s,t) - P|.
     * @param tParam storage for the t value in the equation: Q(s,t) = |T(s,t) - P|.
     * @return the distance between the point and the rectangle.
     */
    public static float distancePointRectangleSquared(
        Vector point,
        Rectangle rect,
        float[] sParam,
        float[] tParam) {
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
        if (sParam != null)
            sParam[0] = s;

        if (tParam != null)
            tParam[0] = t;
        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineLine</code> calculates the distance between two lines.
     * @param line1 the first line to check.
     * @param line2 the second line to check.
     * @return the distance between two lines.
     */
    public static float distanceLineLine(Line line1, Line line2) {
        return (float) Math.sqrt(distanceLineLineSquared(line1, line2));
    }
    /**
     * <code>distanceLineLineSquared</code> calculates the distance squared
     * between two lines.
     * @param line1 the first line to check.
     * @param line2 the second line to check.
     * @return the distance squared between two lines.
     */
    public static float distanceLineLineSquared(Line line1, Line line2) {
        return distanceLineLineSquared(line1, line2, null, null);
    }
    /**
     * <code>distanceLineLineSquared</code> calculates the distance squared
     * between two lines.
     * @param line1 the first line to check.
     * @param line2 the second line to check.
     * @param sParam container for s in the first line where L0(s) = B0 + sM0.
     * @param tParam container for t in the second line where L1(t) = B1 + tM1.
     * @return the distance squared between two lines.
     */
    public static float distanceLineLineSquared(
        Line line1,
        Line line2,
        float[] sParam,
        float[] tParam) {
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

        if (tParam != null) {
            tParam[0] = t;
        }

        if (sParam != null) {
            sParam[0] = s;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineRay</code> calculates the distance between a line and
     * a ray.
     * @param line the line to check.
     * @param ray the ray to check.
     * @return the distance between a line and a ray.
     */
    public static float distanceLineRay(Line line, Line ray) {
        return (float) Math.sqrt(distanceLineRaySquared(line, ray));
    }

    /**
     * <code>distanceLineRaySquared</code> calculates the squared distance
     * between a line and a ray. 
     * @param line the line to check.
     * @param ray the ray to check.
     * @return the distance between the line and the ray.
     */
    public static float distanceLineRaySquared(Line line, Line ray) {
        return distanceLineRaySquared(line, ray, null, null);
    }

    /**
     * <code>distanceLineRaySquared</code> calculates the squared distance
     * between a line and a ray. 
     * @param line the line to check.
     * @param ray the ray to check.
     * @param sParam container for s where s is L0(s) = B0 + sM0.
     * @param tParam container for t where t is L1(t) = B1 + tM1.
     * @return the distance between the line and the ray.
     */
    public static float distanceLineRaySquared(
        Line line,
        Line ray,
        float[] sParam,
        float[] tParam) {
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

        if (tParam != null) {
            tParam[0] = t;
        }

        if (sParam != null) {
            sParam[0] = s;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceLineSegment</code> calculates the distance between a 
     * line and a line segment.
     * @param line the line to check.
     * @param seg the line segment to check.
     * @return the distance between a line and a line segment.
     */
    public static float distanceLineSegment(Line line, Line seg) {
        return (float) Math.sqrt(distanceLineSegmentSquared(line, seg));
    }

    /**
     * <code>distanceLineSegementSquared</code> calculates the distance
     * squared between a line and a line segment.
     * @param line the line to check.
     * @param seg the line segment to check.
     * @return the distance squared between a line and a line segment.
     */
    public static float distanceLineSegmentSquared(Line line, Line seg) {
        return distanceLineSegmentSquared(line, seg, null, null);
    }

    /**
     * <code>distanceLineSegementSquared</code> calculates the distance
     * squared between a line and a line segment.
     * @param line the line to check.
     * @param seg the line segment to check.
     * @param sParam container for s where s is L0(s) = B0 + sM0.
     * @param tParam container for t where t is L1(t) = B1 + tM1.
     * @return the distance squared between a line and a line segment.
     */
    public static float distanceLineSegmentSquared(
        Line line,
        Line seg,
        float[] sParam,
        float[] tParam) {

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
        if (null != sParam) {
            sParam[0] = s;
        }
        if (null != tParam) {
            tParam[0] = t;
        }

        return Math.abs(squareDistance);
    }

    /**
     * <code>distanceLineRectangle</code> calculates the distance between a 
     * line and a rectangle.
     * @param line the line to check.
     * @param rect the rectangle to check.
     * @return the distance between a line and a rectangle.
     */
    public static float distanceLineRectangle(Line line, Rectangle rect) {
        return (float) Math.sqrt(distanceLineRectangleSquared(line, rect));
    }

    /**
     * <code>distanceLineRectangleSquared</code> calculates the distance squared
     * between a line and a rectangle.
     * @param line the line to check.
     * @param rect the rectangle to check.
     * @return the distance squared.
     */
    public static float distanceLineRectangleSquared(
        Line line,
        Rectangle rect) {
        return distanceLineRectangleSquared(line, rect, null, null, null);
    }

    /**
     * <code>distanceLineRectangleSquared</code> calculates the distance squared
     * between a line and a rectangle.
     * @param line the line to check.
     * @param rect the rectangle to check.
     * @param rParam container for the Line's r parameter in L(r) = B + rM.
     * @param sParam container for the rectangle's s parameter in 
     *      R(s,t) = A + sE0 + tE1.
     * @param tParam container for the rectangle's t parameter in
     *      R(s,t) = A + sE0 + tE1.
     * @return the distance squared.
     */
    public static float distanceLineRectangleSquared(
        Line line,
        Rectangle rect,
        float[] rParam,
        float[] sParam,
        float[] tParam) {

        float[] r = new float[1];
        float[] s = new float[1];
        float[] t = new float[1];

        float[] r0 = new float[1];
        float[] s0 = new float[1];
        float[] t0 = new float[1];

        Vector diff = rect.getOrigin().subtract(line.getOrigin());
        float a00 = line.getDirection().lengthSquared();
        float a01 = -line.getDirection().dot(rect.getFirstEdge());
        float a02 = -line.getDirection().dot(rect.getSecondEdge());
        float a11 = rect.getFirstEdge().lengthSquared();
        float a22 = rect.getSecondEdge().lengthSquared();

        float b0 = -diff.dot(line.getDirection());
        float b1 = diff.dot(rect.getFirstEdge());
        float b2 = diff.dot(rect.getSecondEdge());

        float cof00 = a11 * a22;
        float cof01 = -a01 * a22;
        float cof02 = -a02 * a11;

        float determinate = a00 * cof00 + a01 * cof01 + a02 * cof02;

        Line tempSegment = new Line();
        Vector point;
        float distanceSquared;
        float distanceSquared0;

        if (Math.abs(determinate) >= TOLERANCE) {
            float cof11 = a00 * a22 - a02 * a02;
            float cof12 = a02 * a01;
            float cof22 = a00 * a11 - a01 * a01;
            float inverseDeterminate = 1.0f / determinate;
            float rhs0 = -b0 * inverseDeterminate;
            float rhs1 = -b1 * inverseDeterminate;
            float rhs2 = -b2 * inverseDeterminate;

            r[0] = cof00 * rhs0 + cof01 * rhs1 + cof02 * rhs2;
            s[0] = cof01 * rhs0 + cof11 * rhs1 + cof12 * rhs2;
            t[0] = cof02 * rhs0 + cof12 * rhs1 + cof22 * rhs2;

            if (s[0] < 0.0) {
                if (t[0] < 0.0) {
                    // min on face s=0 or t=0
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 0.0f;
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared0 =
                        distanceLineSegmentSquared(line, tempSegment, null, s0);
                    t0[0] = 0.0f;
                    if (distanceSquared0 < distanceSquared) {
                        distanceSquared = distanceSquared0;
                        s[0] = s0[0];
                        t[0] = t0[0];
                    }
                } else if (t[0] <= 1.0f) {
                    // min on face s=0
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 0.0f;
                } else {
                    // min on face s=0 or t=1
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 0.0f;
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getSecondEdge()));
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared0 =
                        distanceLineSegmentSquared(line, tempSegment, null, s0);
                    t0[0] = 1.0f;
                    if (distanceSquared0 < distanceSquared) {
                        distanceSquared = distanceSquared0;
                        s[0] = s0[0];
                        t[0] = t0[0];
                    }
                }
            } else if (s[0] <= 1.0f) {
                if (t[0] < 0.0f) {
                    // min on face t=0
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, s);
                    t[0] = 0.0f;
                } else if (t[0] <= 1.0f) {
                    // line intersects rectangle
                    distanceSquared = 0.0f;
                } else {
                    // min on face t=1
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getSecondEdge()));
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, s);
                    t[0] = 1.0f;
                }
            } else {
                if (t[0] < 0.0) {
                    // min on face s=1 or t=0
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getFirstEdge()));
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 1.0f;
                    tempSegment.setOrigin(rect.getOrigin());
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared0 =
                        distanceLineSegmentSquared(line, tempSegment, null, s0);
                    t0[0] = 0.0f;
                    if (distanceSquared0 < distanceSquared) {
                        distanceSquared = distanceSquared0;
                        s[0] = s0[0];
                        t[0] = t0[0];
                    }
                } else if (t[0] <= 1.0) {
                    // min on face s=1
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getFirstEdge()));
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 1.0f;
                } else {
                    // min on face s=1 or t=1
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getFirstEdge()));
                    tempSegment.setDirection(rect.getSecondEdge());
                    distanceSquared =
                        distanceLineSegmentSquared(line, tempSegment, null, t);
                    s[0] = 1.0f;
                    tempSegment.setOrigin(
                        rect.getOrigin().add(rect.getSecondEdge()));
                    tempSegment.setDirection(rect.getFirstEdge());
                    distanceSquared0 =
                        distanceLineSegmentSquared(line, tempSegment, null, s0);
                    t0[0] = 1.0f;
                    if (distanceSquared0 < distanceSquared) {
                        distanceSquared = distanceSquared0;
                        s[0] = s0[0];
                        t[0] = t0[0];
                    }
                }
            }
        } else {
            // line and rectangle are parallel
            tempSegment.setOrigin(rect.getOrigin());
            tempSegment.setDirection(rect.getFirstEdge());
            distanceSquared =
                distanceLineSegmentSquared(line, tempSegment, r, s);
            t[0] = 0.0f;

            tempSegment.setDirection(rect.getSecondEdge());
            distanceSquared0 =
                distanceLineSegmentSquared(line, tempSegment, r0, t0);
            s0[0] = 0.0f;
            if (distanceSquared0 < distanceSquared) {
                distanceSquared = distanceSquared0;
                r = r0;
                s = s0;
                t = t0;
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getSecondEdge()));
            tempSegment.setDirection(rect.getFirstEdge());
            distanceSquared0 =
                distanceLineSegmentSquared(line, tempSegment, r0, s0);
            t0[0] = 1.0f;
            if (distanceSquared0 < distanceSquared) {
                distanceSquared = distanceSquared0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getFirstEdge()));
            tempSegment.setDirection(rect.getSecondEdge());
            distanceSquared0 =
                distanceLineSegmentSquared(line, tempSegment, r0, t0);
            s0[0] = 1.0f;
            if (distanceSquared0 < distanceSquared) {
                distanceSquared = distanceSquared0;
                r = r0;
                s = s0;
                t = t0;
            }
        }

        if (rParam != null) {
            rParam[0] = r[0];
        }

        if (sParam != null) {
            sParam[0] = s[0];
        }

        if (tParam != null) {
            tParam[0] = t[0];
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceRayRay</code> returns the distance between two rays.
     * @param ray1 the first ray to check.
     * @param ray2 the second ray to check.
     * @return the distance between two rays.
     */
    public static float distanceRayRay(Line ray1, Line ray2) {
        return (float) Math.sqrt(distanceRayRaySquared(ray1, ray2));
    }

    /**
     * <code>distanceRayRaySquared</code> returns the distance squared between
     * two rays.
     * @param ray1 the first ray to check.
     * @param ray2 the second ray to check.
     * @return the distance squared between the two rays.
     */
    public static float distanceRayRaySquared(Line ray1, Line ray2) {
        return distanceRayRaySquared(ray1, ray2, null, null);
    }

    /**
     * <code>distanceRayRaySquared</code> calculates the distance squared
     * between two rays.
     * @param ray1 the first ray to check.
     * @param ray2 the second ray to check.
     * @param sParam container for s in L1(s) = B1 + sM1.
     * @param tParam container for t in L2(t) = B2 + tM2.
     * @return the distance squared between the two rays.
     */
    public static float distanceRayRaySquared(
        Line ray1,
        Line ray2,
        float[] sParam,
        float[] tParam) {
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
                if (t >= 0.0) {
                    // minimum at two interior points of rays
                    float inverseDeterminate = 1.0f / determinate;
                    s *= inverseDeterminate;
                    t *= inverseDeterminate;
                    distanceSquared =
                        s * (a * s + b * t + 2.0f * d)
                            + t * (b * s + c * t + 2.0f * e)
                            + f;
                } else {
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
                if (t >= 0.0) {
                    s = 0.0f;
                    if (e >= 0.0) {
                        t = 0.0f;
                        distanceSquared = f;
                    } else {
                        t = -e / c;
                        distanceSquared = e * t + f;
                    }
                } else {
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

        if (sParam != null) {
            sParam[0] = s;
        }

        if (tParam != null) {
            tParam[0] = t;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceRaySegment</code> calculates the distance between a ray
     * and a line segment.
     * @param ray the ray to check.
     * @param seg the line segment to check.
     * @return
     */
    public static float distanceRaySegment(Line ray, Line seg) {
        return (float) Math.sqrt(distanceRaySegmentSquared(ray, seg));
    }

    /**
    * <code>distanceRaySegmentSquared</code> calculates the distance
    * squared between a ray and a line segment. 
    * @param ray the ray to check.
    * @param seg the line segment to check.
    * @return the distance between the ray and the line segment.
    */
    public static float distanceRaySegmentSquared(Line ray, Line seg) {
        return distanceRaySegmentSquared(ray, seg, null, null);
    }

    /**
     * <code>distanceRaySegmentSquared</code> calculates the distance
     * squared between a ray and a line segment. 
     * @param ray the ray to check.
     * @param seg the line segment to check.
     * @param sParam container for s where L1(s) = B1 + sM1.
     * @param tParam container for t where L2(t) = B2 + tM2.
     * @return the distance between the ray and the line segment.
     */
    public static float distanceRaySegmentSquared(
        Line ray,
        Line seg,
        float[] sParam,
        float[] tParam) {

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

        if (sParam != null) {
            sParam[0] = s;
        }

        if (tParam != null) {
            tParam[0] = t;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceRayRectangle</code> calculates the distance between a 
     * ray and a rectangle.
     * @param ray the ray to check.
     * @param rect the rectangle to check.
     * @return the distance between a ray and a rectangle.
     */
    public static float distanceRayRectangle(Line ray, Rectangle rect) {
        return (float) Math.sqrt(distanceRayRectangleSquared(ray, rect));
    }

    /**
     * <code>distanceRayRectangleSquared</code> calculates the distance 
     * squared between a ray and a rectangle.
     * @param ray the ray to check.
     * @param rect the rectangle to check.
     * @return the distance squared between a ray and a rectangle.
     */
    public static float distanceRayRectangleSquared(Line ray, Rectangle rect) {
        return distanceRayRectangleSquared(ray, rect, null, null, null);
    }

    /**
     * <code>distanceRayRectangleSquared</code> calculates the distance 
     * squared between a ray and a rectangle.
     * @param ray the ray to check.
     * @param rect the rectangle to check.
     * @param rParam container for the Line's r parameter in L(r) = B + rM.
     * @param sParam container for the rectangle's s parameter in 
     *      R(s,t) = A + sE0 + tE1.
     * @param tParam container for the rectangle's t parameter in
     *      R(s,t) = A + sE0 + tE1.
     * 
     * @return the distance squared between a ray and a rectangle.
     */
    public static float distanceRayRectangleSquared(
        Line ray,
        Rectangle rect,
        float[] rParam,
        float[] sParam,
        float[] tParam) {
        Vector diff = rect.getOrigin().subtract(ray.getOrigin());
        float a00 = ray.getDirection().lengthSquared();
        float a01 = -ray.getDirection().dot(rect.getFirstEdge());
        float a02 = -ray.getDirection().dot(rect.getSecondEdge());
        float a11 = rect.getFirstEdge().lengthSquared();
        float a22 = rect.getSecondEdge().lengthSquared();
        float b0 = -diff.dot(ray.getDirection());
        float b1 = diff.dot(rect.getFirstEdge());
        float b2 = diff.dot(rect.getSecondEdge());
        float cof00 = a11 * a22;
        float cof01 = -a01 * a22;
        float cof02 = -a02 * a11;
        float determinate = a00 * cof00 + a01 * cof01 + a02 * cof02;

        Line tempSegment = new Line();
        Vector point = new Vector();
        float[] r = new float[1];
        float[] s = new float[1];
        float[] t = new float[1];
        float[] r0 = new float[1];
        float[] s0 = new float[1];
        float[] t0 = new float[1];
        float squaredDistance, squaredDistance0;

        if (Math.abs(determinate) >= TOLERANCE) {
            float cof11 = a00 * a22 - a02 * a02;
            float cof12 = a02 * a01;
            float cof22 = a00 * a11 - a01 * a01;
            float inverseDeterminate = 1.0f / determinate;
            float rhs0 = -b0 * inverseDeterminate;
            float rhs1 = -b1 * inverseDeterminate;
            float rhs2 = -b2 * inverseDeterminate;

            r[0] = cof00 * rhs0 + cof01 * rhs1 + cof02 * rhs2;
            s[0] = cof01 * rhs0 + cof11 * rhs1 + cof12 * rhs2;
            t[0] = cof02 * rhs0 + cof12 * rhs1 + cof22 * rhs2;

            if (r[0] <= 0.0) {
                if (s[0] < 0.0) {
                    if (t[0] < 0.0) {
                        // min on face s=0 or t=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(ray, tempSegment, r0, s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 0.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=0 or t=1 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(ray, tempSegment, r0, s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else if (s[0] <= 1.0) {
                    if (t[0] < 0.0) {
                        // min on face t=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, s);
                        t[0] = 0.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face r=0
                        squaredDistance =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s,
                                t);
                        r[0] = 0.0f;
                    } else {
                        // min on face t=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, s);
                        t[0] = 1.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else {
                    if (t[0] < 0.0) {
                        // min on face s=1 or t=0 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(ray, tempSegment, r0, s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 1.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=1 or t=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(ray, tempSegment, r, t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(ray, tempSegment, r0, s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                ray.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                }
            } else {
                if (s[0] < 0.0) {
                    if (t[0] < 0.0) {
                        // min on face s=0 or t=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 0.0f;
                    } else {
                        // min on face s=0 or t=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else if (s[0] <= 1.0) {
                    if (t[0] < 0.0) {
                        // min on face t=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s);
                        t[0] = 0.0f;
                    } else if (t[0] <= 1.0) {
                        // ray intersects the rectangle
                        squaredDistance = 0.0f;
                    } else {
                        // min on face t=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s);
                        t[0] = 1.0f;
                    }
                } else {
                    if (t[0] < 0.0) {
                        // min on face s=1 or t=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) // region 1p
                        {
                        // min on face s=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 1.0f;
                    } else {
                        // min on face s=1 or t=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceRaySegmentSquared(
                                ray,
                                tempSegment,
                                null,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                }
            }
        } else {
            // ray and rectangle are parallel
            tempSegment.setOrigin(rect.getOrigin());
            tempSegment.setDirection(rect.getFirstEdge());
            squaredDistance = distanceRaySegmentSquared(ray, tempSegment, r, s);
            t[0] = 0.0f;

            tempSegment.setDirection(rect.getSecondEdge());
            squaredDistance0 =
                distanceRaySegmentSquared(ray, tempSegment, r0, t0);
            s0[0] = 0.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getSecondEdge()));
            tempSegment.setDirection(rect.getFirstEdge());
            squaredDistance0 =
                distanceRaySegmentSquared(ray, tempSegment, r0, s0);
            t0[0] = 1.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getFirstEdge()));
            tempSegment.setDirection(rect.getSecondEdge());
            squaredDistance0 =
                distanceRaySegmentSquared(ray, tempSegment, r0, t0);
            s0[0] = 1.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            squaredDistance0 =
                distancePointRectangleSquared(ray.getOrigin(), rect, s0, t0);
            r0[0] = 0.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }
        }

        if (rParam != null) {
            rParam[0] = r[0];
        }

        if (sParam != null) {
            sParam[0] = s[0];
        }

        if (tParam != null) {
            tParam[0] = t[0];
        }

        return Math.abs(squaredDistance);
    }

    /**
     * <code>distanceSegmentSegment</code> calculates the distance between
     * two lien segments.
     * @param seg1 the first line segment to check.
     * @param seg2 the second line segment to check.
     * @return the distance between the two line segments.
     */
    public static float distanceSegmentSegment(Line seg1, Line seg2) {
        return (float) Math.sqrt(distanceSegmentSegment(seg1, seg2));
    }
    /**
     * <code>distanceSegmentSegmentSquared</code> calculates the distance
     * squared between two line segments.
     * @param seg1 the first line segment to check.
     * @param seg2 the second line segment to check.
     * @return the distance squared between two line segments.
     */
    public static float distanceSegmentSegmentSquared(Line seg1, Line seg2) {
        return distanceSegmentSegmentSquared(seg1, seg2, null, null);
    }

    /**
     * <code>distanceSegmentSegmentSquared</code> calculates the distance
     * squared between two line segments.
     * @param seg1 the first line segment to check.
     * @param seg2 the second line segment to check.
     * @param sParam container for s where s is L1(s) = B1 + sM1.
     * @param tParam container for t where t is L2(t) = B2 + tM2.
     * @return the distance squared between two line segments.
     */
    public static float distanceSegmentSegmentSquared(
        Line seg1,
        Line seg2,
        float[] sParam,
        float[] tParam) {
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
                        if (t <= determinate) {
                            // minimum at two interior points of 3D lines
                            float inverseDeterminate = 1.0f / determinate;
                            s *= inverseDeterminate;
                            t *= inverseDeterminate;
                            distanceSquared =
                                s * (a * s + b * t + 2.0f * d)
                                    + t * (b * s + c * t + 2.0f * e)
                                    + f;
                        } else {
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
                    } else {
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
                        if (t <= determinate) {
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
                        } else {
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
                    } else {
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
                    if (t <= determinate) {
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
                    } else {
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
                } else {
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

        if (sParam != null) {
            sParam[0] = s;
        }

        if (tParam != null) {
            tParam[0] = t;
        }

        return Math.abs(distanceSquared);
    }

    /**
     * <code>distanceSegmentRectangle</code> returns the distance between a
     * line segment and a rectangle. 
     * @param seg the line segment to check.
     * @param rect the rectangle to check.
     * @return the distance between a line segment and a rectangle.
     */
    public static float distanceSegmentRectangle(Line seg, Rectangle rect) {
        return (float) Math.sqrt(distanceSegmentRectangleSquared(seg, rect));
    }

    /**
     * <code>distanceSegmentRectangleSquared</code> calculates the distance
     * squared between a line segment and a rectangle.
     * @param seg the line segment to check.
     * @param rect the rectangle to check.
     * @return the distance squared between a line segment and rectangle.
     */
    public static float distanceSegmentRectangleSquared(
        Line seg,
        Rectangle rect) {
        return distanceSegmentRectangleSquared(seg, rect, null, null, null);
    }

    /**
     * <code>distanceSegmentRectangleSquared</code> calculates the distance
     * squared between a line segment and a rectangle.
     * @param seg the line segment to check.
     * @param rect the rectangle to check.
     * @param rParam container for the Line's r parameter in L(r) = B + rM.
     * @param sParam container for the rectangle's s parameter in 
     *      R(s,t) = A + sE0 + tE1.
     * @param tParam container for the rectangle's t parameter in
     *      R(s,t) = A + sE0 + tE1.
     * 
     * @return the distance squared between a line segment and rectangle.
     */
    public static float distanceSegmentRectangleSquared(
        Line seg,
        Rectangle rect,
        float[] rParam,
        float[] sParam,
        float[] tParam) {

        Vector diff = rect.getOrigin().subtract(seg.getOrigin());
        float a00 = seg.getDirection().lengthSquared();
        float a01 = -seg.getDirection().dot(rect.getFirstEdge());
        float a02 = -seg.getDirection().dot(rect.getSecondEdge());
        float a11 = rect.getFirstEdge().lengthSquared();
        float a22 = rect.getSecondEdge().lengthSquared();
        float b0 = -diff.dot(seg.getDirection());
        float b1 = diff.dot(rect.getFirstEdge());
        float b2 = diff.dot(rect.getSecondEdge());
        float cof00 = a11 * a22;
        float cof01 = -a01 * a22;
        float cof02 = -a02 * a11;
        float determinate = a00 * cof00 + a01 * cof01 + a02 * cof02;

        Line tempSegment = new Line();
        Vector point = new Vector();
        float[] r = new float[1];
        float[] s = new float[1];
        float[] t = new float[1];
        float[] r0 = new float[1];
        float[] s0 = new float[1];
        float[] t0 = new float[1];
        float squaredDistance, squaredDistance0;

        if (Math.abs(determinate) >= TOLERANCE) {
            float cof11 = a00 * a22 - a02 * a02;
            float cof12 = a02 * a01;
            float cof22 = a00 * a11 - a01 * a01;
            float inverseDeterminate = 1.0f / determinate;
            float rhs0 = -b0 * inverseDeterminate;
            float rhs1 = -b1 * inverseDeterminate;
            float rhs2 = -b2 * inverseDeterminate;

            r[0] = cof00 * rhs0 + cof01 * rhs1 + cof02 * rhs2;
            s[0] = cof01 * rhs0 + cof11 * rhs1 + cof12 * rhs2;
            t[0] = cof02 * rhs0 + cof12 * rhs1 + cof22 * rhs2;

            if (r[0] < 0.0) {
                if (s[0] < 0.0) {
                    if (t[0] < 0.0) {
                        // min on face s=0 or t=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setDirection(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=0 or t=1 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else if (s[0] <= 1.0) {
                    if (t[0] < 0.0) {
                        // min on face t=0 or r=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 0.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face r=0
                        squaredDistance =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s,
                                t);
                        r[0] = 0.0f;
                    } else {
                        // min on face t=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 1.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else {
                    if (t[0] < 0.0) {
                        // min on face s=1 or t=0 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=1 or t=1 or r=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        r0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                }
            } else if (r[0] <= 1.0) {
                if (s[0] < 0.0) {
                    if (t[0] < 0.0) {
                        // min on face s=0 or t=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1) {
                        // min on face s=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                    } else {
                        // min on face s=0 or t=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else if (s[0] <= 1.0) {
                    if (t[0] < 0.0) {
                        // min on face t=0
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 0.0f;
                    } else if (t[0] <= 1.0) {
                        // global minimum is interior
                        squaredDistance =
                            r[0]
                                * (a00 * r[0]
                                    + a01 * s[0]
                                    + a02 * t[0]
                                    + 2.0f * b0)
                                + s[0] * (a01 * r[0] + a11 * s[0] + 2.0f * b1)
                                + t[0] * (a02 * r[0] + a22 * t[0] + 2.0f * b2)
                                + diff.lengthSquared();
                    } else {
                        // min on face t=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 1.0f;
                    }
                } else {
                    if (t[0] < 0.0) {
                        // min on face s=1 or t=0
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                    } else {
                        // min on face s=1 or t=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                }
            } else {
                if (s[0] < 0.0) {
                    if (t[0] < 0.0) {
                        // min on face s=0 or t=0 or r=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=0 or r=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        squaredDistance0 =
                            distancePointRectangleSquared(
                                seg.getOrigin(),
                                rect,
                                s0,
                                t0);
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=0 or t=1 or r=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 0.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else if (s[0] <= 1.0) {
                    if (t[0] < 0.0) {
                        // min on face t=0 or r=1
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 0.0f;
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face r=1
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance =
                            distancePointRectangleSquared(point, rect, s, t);
                        r[0] = 1.0f;
                    } else {
                        // min on face t=1 or r=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                s);
                        t[0] = 1.0f;
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                } else {
                    if (t[0] < 0.0) {
                        // min on face s=1 or t=0 or r=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(rect.getOrigin());
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 0.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else if (t[0] <= 1.0) {
                        // min on face s=1 or r=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    } else {
                        // min on face s=1 or t=1 or r=1
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getFirstEdge()));
                        tempSegment.setOrigin(rect.getSecondEdge());
                        squaredDistance =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r,
                                t);
                        s[0] = 1.0f;
                        tempSegment.setOrigin(
                            rect.getOrigin().add(rect.getSecondEdge()));
                        tempSegment.setOrigin(rect.getFirstEdge());
                        squaredDistance0 =
                            distanceSegmentSegmentSquared(
                                seg,
                                tempSegment,
                                r0,
                                s0);
                        t0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                        point = seg.getOrigin().add(seg.getDirection());
                        squaredDistance0 =
                            distancePointRectangleSquared(point, rect, s0, t0);
                        r0[0] = 1.0f;
                        if (squaredDistance0 < squaredDistance) {
                            squaredDistance = squaredDistance0;
                            r[0] = r0[0];
                            s[0] = s0[0];
                            t[0] = t0[0];
                        }
                    }
                }
            }
        } else {
            // segment and rectangle are parallel
            tempSegment.setOrigin(rect.getOrigin());
            tempSegment.setOrigin(rect.getFirstEdge());
            squaredDistance =
                distanceSegmentSegmentSquared(seg, tempSegment, r, s);
            t[0] = 0.0f;

            tempSegment.setOrigin(rect.getSecondEdge());
            squaredDistance0 =
                distanceSegmentSegmentSquared(seg, tempSegment, r0, t0);
            s0[0] = 0.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getSecondEdge()));
            tempSegment.setOrigin(rect.getFirstEdge());
            squaredDistance0 =
                distanceSegmentSegmentSquared(seg, tempSegment, r0, s0);
            t0[0] = 1.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            tempSegment.setOrigin(rect.getOrigin().add(rect.getFirstEdge()));
            tempSegment.setOrigin(rect.getSecondEdge());
            squaredDistance0 =
                distanceSegmentSegmentSquared(seg, tempSegment, r0, t0);
            s0[0] = 1.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            squaredDistance0 =
                distancePointRectangleSquared(seg.getOrigin(), rect, s0, t0);
            r0[0] = 0.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }

            point = seg.getOrigin().add(seg.getDirection());
            squaredDistance0 =
                distancePointRectangleSquared(point, rect, s0, t0);
            r0[0] = 1.0f;
            if (squaredDistance0 < squaredDistance) {
                squaredDistance = squaredDistance0;
                r[0] = r0[0];
                s[0] = s0[0];
                t[0] = t0[0];
            }
        }

        if (rParam != null) {
            rParam[0] = r[0];
        }

        if (sParam != null) {
            sParam[0] = s[0];
        }

        if (tParam != null) {
            tParam[0] = t[0];
        }

        return Math.abs(squaredDistance);
    }

    /**
     * <code>distanceRectangleRectangle</code> calculates the distance between
     * two rectangles.
     * @param rect1 the first rectangle to check.
     * @param rect2 the second rectangle to check.
     * @return the distance between two rectangles.
     */
    public static float distanceRectangleRectangle(
        Rectangle rect1,
        Rectangle rect2) {
        return (float) Math.sqrt(
            distanceRectangleRectangleSquared(rect1, rect2));
    }

    /**
     * <code>distanceRectangleRectangleSquared</code> calculates the distance
     * squared between two rectangles.
     * @param rect1 the first rectangle to check.
     * @param rect2 the second rectangle to check.
     * @return the distance squared between these two rectangles.
     */
    public static float distanceRectangleRectangleSquared(
        Rectangle rect1,
        Rectangle rect2) {
        return distanceRectangleRectangleSquared(
            rect1,
            rect2,
            null,
            null,
            null,
            null);
    }

    /**
     * <code>distanceRectangleRectangleSquared</code> calculates the distance
     * squared between two rectangles.
     * @param rect1 the first rectangle to check.
     * @param rect2 the second rectangle to check.
     * @param sParam container for s where s is R1(s,t) = A + sE0 + tE1.
     * @param tParam container for t where t is R1(s,t) = A + sE0 + tE1.
     * @param uParam container for u where u is R2(u,v) = A + uE0 + vE1.
     * @param vParam container for v where v is R2(u,v) = A + uE0 + vE1.
     * @return the distance squared between these two rectangles.
     */
    public static float distanceRectangleRectangleSquared(
        Rectangle rect1,
        Rectangle rect2,
        float[] sParam,
        float[] tParam,
        float[] uParam,
        float[] vParam) {

        float[] s = new float[1];
        float[] t = new float[1];
        float[] u = new float[1];
        float[] v = new float[1];
        float[] t0 = new float[1];
        float[] u0 = new float[1];
        float[] v0 = new float[1];
        float[] s0 = new float[1];

        float distanceSquared, distanceSquared0;
        Line segment = new Line();

        // compare edges of rct0 against all of rct1
        segment.setOrigin(rect1.getOrigin());
        segment.setDirection(rect1.getFirstEdge());
        distanceSquared =
            distanceSegmentRectangleSquared(segment, rect2, s, u, v);
        t[0] = 0.0f;

        segment.setDirection(rect1.getSecondEdge());
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect2, t0, u0, v0);
        s0[0] = 0.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        segment.setOrigin(rect1.getOrigin().add(rect1.getFirstEdge()));
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect2, t0, u0, v0);
        s0[0] = 1.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        segment.setOrigin(rect1.getOrigin().add(rect1.getSecondEdge()));
        segment.setDirection(rect1.getFirstEdge());
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect2, s0, u0, v0);
        t0[0] = 1.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        // compare edges of pgm1 against all of pgm0
        segment.setOrigin(rect2.getOrigin());
        segment.setDirection(rect2.getFirstEdge());
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect1, u0, s0, t0);
        v0[0] = 0.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        segment.setDirection(rect2.getSecondEdge());
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect1, v0, s0, t0);
        u0[0] = 0.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        segment.setOrigin(rect2.getOrigin().add(rect2.getFirstEdge()));
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect1, v0, s0, t0);
        u0[0] = 1.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        segment.setOrigin(rect2.getOrigin().add(rect2.getSecondEdge()));
        segment.setDirection(rect2.getFirstEdge());
        distanceSquared0 =
            distanceSegmentRectangleSquared(segment, rect1, u0, s0, t0);
        v0[0] = 1.0f;
        if (distanceSquared0 < distanceSquared) {
            distanceSquared = distanceSquared0;
            s[0] = s0[0];
            t[0] = t0[0];
            u[0] = u0[0];
            v[0] = v0[0];
        }

        if (sParam != null) {
            sParam[0] = s[0];
        }

        if (tParam != null) {
            tParam[0] = t[0];
        }

        if (uParam != null) {
            uParam[0] = u[0];
        }

        if (vParam != null) {
            vParam[0] = v[0];
        }

        return Math.abs(distanceSquared);
    }
}
