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

/**
 * <code>SingleCurve</code>
 * @author Mark Powell
 * @version $Id: SingleCurve.java,v 1.2 2004-01-06 04:06:46 mojomonkey Exp $
 */
public abstract class SingleCurve extends Curve {

    public SingleCurve(float minTime, float maxTime) {
        super(minTime, maxTime);
    }

    /* (non-Javadoc)
     * @see com.jme.curve.Curve#getLength(float, float)
     */
    public float getLength(float time0, float time1) {
        return integrate(time0, time1);
    }

    /* (non-Javadoc)
     * @see com.jme.curve.Curve#getTime(float, int, float)
     */
    public float getTime(float length, int iterations, float tolerance) {
        if (length <= 0.0f)
            return minTime;

        if (length >= getTotalLength())
            return maxTime;

        // initial guess for Newton's method
        float fRatio = length / getTotalLength();
        float fOmRatio = 1.0f - fRatio;
        float fTime = fOmRatio * minTime + fRatio * maxTime;

        for (int i = 0; i < iterations; i++) {
            float fDifference = getLength(minTime, fTime) - length;
            if (Math.abs(fDifference) < tolerance)
                return fTime;

            fTime -= fDifference / getSpeed(fTime);
        }

        // Newton's method failed.  If this happens, increase iterations or
        // tolerance or integration accuracy.
        return Float.MAX_VALUE;
    }

    protected float getSpeedWithData(float time, Curve data) {
        return data.getSpeed(time);
    }

    private float integrate(float a, float b) {
        return 0;
    }
}
