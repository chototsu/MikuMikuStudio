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


/**
 * <code>StencilState</code>
 * @author Mark Powell
 * @version $id$
 */
public abstract class StencilState extends RenderState {
    
    public static final int SF_NEVER = 0;
    public static final int SF_LESS = 1;
    public static final int SF_LEQUAL = 2;
    public static final int SF_GREATER = 3;
    public static final int SF_GEQUAL = 4;
    public static final int SF_EQUAL = 5;
    public static final int SF_NOTEQUAL = 6;
    public static final int SF_ALWAYS = 7;
    
    public static final int SO_KEEP = 0;
    public static final int SO_ZERO = 1;
    public static final int SO_REPLACE = 2;
    public static final int SO_INCR = 3;
    public static final int SO_DECR = 4;
    public static final int SO_INVERT = 5;
    
    private int stencilFunc;
    private int stencilRef;
    private int stencilMask;
    private int stencilOpFail;
    private int stencilOpZFail;
    private int stencilOpZPass;

    /* (non-Javadoc)
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_STENCIL;
    }
    
    public void setStencilFunc(int func) {
        this.stencilFunc = func;
    }
    
    public int getStencilFunc() {
        return stencilFunc;
    }
    
    public void setStencilRef(int ref) {
        this.stencilRef = ref;
    }
    
    public int getStencilRef() {
        return stencilRef;
    }
    
    public void setStencilMask(int mask) {
        this.stencilMask = mask;
    }
    
    public int getStencilMask() {
        return stencilMask;
    }
    
    public void setStencilOpFail(int op) {
        this.stencilOpFail = op;
    }
    
    public int getStencilOpFail() {
        return stencilOpFail;
    }
    
    public void setStencilOpZFail(int op) {
        this.stencilOpZFail = op;
    }
    
    public int getStencilOpZFail() {
        return stencilOpZFail;
    }
    
    public void setStencilOpZPass(int op) {
        this.stencilOpZPass = op;
    }
    
    public int getStencilOpZPass() {
        return stencilOpZPass;
    }
}
