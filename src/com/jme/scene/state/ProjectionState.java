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
package com.jme.scene.state;

import com.jme.math.Vector2f;


/**
 * <code>ProjectionState</code>
 * @author Mark Powell
 * @version $id$
 */
public abstract class ProjectionState extends RenderState {
    public static final int PS_PERSPECTIVE = 0;
    public static final int PS_ORTHOGRAPHIC = 1;
    
    protected static boolean isInOrthoMode = false;
    
    protected int projection;
    
    protected Vector2f leftBottom;
    protected Vector2f rightTop;

    /* (non-Javadoc)
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_PROJECTION;
    }
    
    public void setProjection(int mode) {
        projection = mode;
    }
    
    public int getProjection() {
        return projection;
    }
    
    public void setLeftBottom(Vector2f leftBottom) {
        this.leftBottom = leftBottom;
    }
    
    public void setRightTop(Vector2f rightTop) {
        this.rightTop = rightTop;
    }
}
