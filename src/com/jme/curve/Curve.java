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
import com.jme.system.JmeException;

/**
 * <code>Curve</code>
 * @author Mark Powell
 * @version $Id: Curve.java,v 1.3 2004-01-06 15:19:39 mojomonkey Exp $
 */
public abstract class Curve {

    protected Vector3f[] controlPoints;

    public Curve() {
        controlPoints = new Vector3f[0];
    }

    public Curve(Vector3f[] controlPoints) {
        if (null == controlPoints) {
            throw new JmeException("Control Points may not be null.");
        }

        if (controlPoints.length < 2) {
            throw new JmeException("There must be at least two control points.");
        }

        this.controlPoints = controlPoints;
    }

    public void setControlPoints(Vector3f[] controlPoints) {
        if (null == controlPoints) {
            throw new JmeException("Control Points may not be null.");
        }

        if (controlPoints.length < 2) {
            throw new JmeException("There must be at least two control points.");
        }

        this.controlPoints = controlPoints;
    }
    
    public Vector3f[] getControlPoints() {
        return controlPoints;
    }

    public abstract Vector3f getPoint(float time);
}
