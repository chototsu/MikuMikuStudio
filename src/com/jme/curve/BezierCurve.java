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
 * <code>BezierCurve</code>
 * @author Mark Powell
 * @version 
 */
public class BezierCurve extends Curve {
    
    public BezierCurve() {
        super();
    }
    
    public BezierCurve(Vector3f[] controlPoints) {
        super(controlPoints);
    }

    /* (non-Javadoc)
     * @see com.jme.curve.Curve#getPoint(float)
     */
    public Vector3f getPoint(float time) {
        Vector3f point = new Vector3f();
        
        float muk = 1;
        float munk = (float)Math.pow(1-time, controlPoints.length-1);
        
        for(int i = 0; i < controlPoints.length; i++) {
            int count = controlPoints.length-1;
            int iCount = i;
            int diff = count - iCount;
            float blend = muk * munk;
            muk *= time;
            munk /= (1 - time);
            while(count >= 1) {
                blend *= count;
                count--;
                if(iCount > 1) {
                    blend /= iCount;
                    iCount--;
                }
                
                if(diff > 1) {
                    blend /= diff;
                    diff--;
                }
            }
            point.x += controlPoints[i].x * blend;
            point.y += controlPoints[i].y * blend;
            point.z += controlPoints[i].z * blend;
        }
        
        return point;
    }

}
