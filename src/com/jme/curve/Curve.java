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
package com.jme.curve;

import com.jme.math.Vector3f;

/**
 * <code>Curve</code>
 * @author Mark Powell
 * @version $Id: Curve.java,v 1.1 2004-01-05 01:44:31 mojomonkey Exp $
 */
public abstract class Curve {
    protected static final float EPSILON = 0.0000001f;
    protected float minTime;
    protected float maxTime;

    public Curve() {
        minTime = 0;
        maxTime = 0;
    }

    public Curve(float minTime, float maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public void setMinTime(float minTime) {
        this.minTime = minTime;
    }

    public float getMinTime() {
        return minTime;
    }

    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    public void setTimeInterval(float minTime, float maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    public abstract Vector3f getPosition(float time);
    public abstract Vector3f getFirstDerivative(float time);
    public abstract Vector3f getSecondDerivative(float time);
    public abstract Vector3f getThirdDerivative(float time);

    public float getSpeed(float time) {
        Vector3f velocity = getFirstDerivative(time);
        return velocity.length();
    }

    public abstract float getLength(float time0, float time1);

    public float getTotalLength() {
        return getLength(minTime, maxTime);
    }

    public Vector3f getTangent(float time) {
        Vector3f velocity = getFirstDerivative(time);
        return velocity.normalize();
    }

    public void getFrame(
        float time,
        Vector3f position,
        Vector3f tangent,
        Vector3f normal,
        Vector3f binormal) {

        position = getPosition(time);
        Vector3f kVelocity = getFirstDerivative(time);
        Vector3f kAcceleration = getSecondDerivative(time);
        float fVDotV = kVelocity.dot(kVelocity);
        float fVDotA = kVelocity.dot(kAcceleration);
        normal = kAcceleration.mult(fVDotV).subtract(kVelocity.mult(fVDotA));
        normal.normalize();
        tangent = kVelocity;
        tangent.normalize();
        binormal = tangent.cross(normal);

    }

    public float getCurvature(float time) {
        Vector3f kVelocity = getFirstDerivative(time);
        float fSpeedSqr = kVelocity.lengthSquared();

        if (fSpeedSqr >= EPSILON) {
            Vector3f kAcceleration = getSecondDerivative(time);
            Vector3f kCross = kVelocity.cross(kAcceleration);
            float fNumer = kCross.length();
            float fDenom = (float) Math.pow(fSpeedSqr, 1.5f);
            return fNumer / fDenom;
        } else {
            // curvature is indeterminate, just return 0
            return 0;
        }
    }

    public float getTorsion(float time) {
        Vector3f kVelocity = getFirstDerivative(time);
        Vector3f kAcceleration = getSecondDerivative(time);
        Vector3f kCross = kVelocity.cross(kAcceleration);
        float fDenom = kCross.lengthSquared();

        if (fDenom >= EPSILON) {
            Vector3f kJerk = getThirdDerivative(time);
            float fNumer = kCross.dot(kJerk);
            return fNumer / fDenom;
        } else {
            // torsion is indeterminate, just return 0
            return 0;
        }
    }

    public abstract float getTime(
        float length,
        int iterations,
        float tolerance);

    public void subdivideByTime(int numPoints, Vector3f[] point) {
        point = new Vector3f[numPoints];

        float delta = (maxTime - minTime) / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            float time = minTime + delta * i;
            point[i] = getPosition(time);
        }
    }

    public void subdivideByLength(int numPoints, Vector3f[] point) {

        point = new Vector3f[numPoints];

        float delta = getTotalLength() / (numPoints - 1);

        for (int i = 0; i < numPoints; i++) {
            float length = delta * i;
            float time = getTime(length, 32, 0.0000001f);
            point[i] = getPosition(time);
        }
    }

    public abstract float getVariation(
        float time0,
        float time1,
        Vector3f point0,
        Vector3f point1);

    protected int subdivideByVariation(
        float fT0,
        Vector3f rkP0,
        float fT1,
        Vector3f rkP1,
        float fMinVariation,
        int uiLevel,
        int iNumPoints,
        PointList rpkList) {
        if (uiLevel > 0
            && getVariation(fT0, fT1, rkP0, rkP1) > fMinVariation) {
            // too much variation, subdivide interval
            uiLevel--;
            float fTMid = 0.5f * (fT0 + fT1);
            Vector3f kPMid = getPosition(fTMid);

            iNumPoints =
                subdivideByVariation(
                    fT0,
                    rkP0,
                    fTMid,
                    kPMid,
                    fMinVariation,
                    uiLevel,
                    iNumPoints,
                    rpkList);

            iNumPoints =
                subdivideByVariation(
                    fTMid,
                    kPMid,
                    fT1,
                    rkP1,
                    fMinVariation,
                    uiLevel,
                    iNumPoints,
                    rpkList);
        } else {
            // add right end point, left end point was added by neighbor
            rpkList = new PointList(rkP1);
            iNumPoints++;
        }

        return iNumPoints;
    }

    public void subdivideByVariation(
        float fMinVariation,
        int uiMaxLevel,
        Vector3f[] rakPoint) {

        //          compute end points of curve
        Vector3f kPMin = getPosition(minTime);
        Vector3f kPMax = getPosition(maxTime);

        // add left end point to list
        PointList pkList = new PointList(kPMin);
        int iNumPoints = 1;
        // binary subdivision, leaf nodes add right end point of subinterval
        iNumPoints =
            subdivideByVariation(
                minTime,
                kPMin,
                maxTime,
                kPMax,
                fMinVariation,
                uiMaxLevel,
                iNumPoints,
                pkList.m_kNext);

        // repackage points in an array
        rakPoint = new Vector3f[iNumPoints];
        for (int i = 0; i < rakPoint.length; i++) {
            rakPoint[i] = pkList.m_kPoint;
            pkList = pkList.m_kNext;
        }
    }

    class PointList {
        Vector3f m_kPoint;
        PointList m_kNext;
        public PointList(Vector3f rkPoint) {
            m_kPoint = rkPoint;
            m_kNext = null;
        }

    }

}
