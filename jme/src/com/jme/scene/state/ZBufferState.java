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
 * <code>ZBufferState</code> maintains how the use of the depth buffer is to
 * occur. Depth buffer comparisons are used to evaluate what incoming fragment
 * will be used. This buffer is based on z depth, or distance between the 
 * pixel source and the eye.
 * @author Mark Powell
 * @version $Id: ZBufferState.java,v 1.1.1.1 2003-10-29 10:56:41 Anakan Exp $
 */
public abstract class ZBufferState extends RenderState {
    /**
     * Depth comparison never passes.
     */
    public static final int CF_NEVER = 0;
    /**
     * Passes if the incoming value is less than the stored value.
     */
    public static final int CF_LESS = 1;
    /**
     * Passes if the incoming value is the same as the stored value.
     */
    public static final int CF_EQUAL = 2;
    /**
     * Passes if the incoming value is less than or equal to the stored value.
     */
    public static final int CF_LEQUAL = 3;
    /**
     * Passes if the incoming value is greater than the stored value.
     */
    public static final int CF_GREATER = 4;
    /**
     * Passes if the incoming value is not equal to the stored value.
     */
    public static final int CF_NOTEQUAL = 5;
    /**
     * Passes if the incoming value is greater than or equal to the stored value.
     */
    public static final int CF_GEQUAL = 6;
    /**
     * Depth comparison always passes.
     */
    public static final int CF_ALWAYS = 7;
    
    //depth function
    protected int function;
    //depth mask is writable or not.
    protected boolean writable;
    
    /**
     * Constructor instantiates a new <code>ZBufferState</code> object. The
     * initial values are CF_LESS and depth writing on.
     *
     */
    public ZBufferState() {
        function = CF_LESS;
        writable = true;
    }

    /**
     * <code>getFunction</code> returns the current depth function.
     * @return the depth function currently used.
     */
    public int getFunction() {
        return function;
    }

    /**
     * <code>setFunction</code> sets the depth function. If an invalid value is
     * passed, CF_LESS is used.
     * @param function the depth function.
     */
    public void setFunction(int function) {
        if(function < 0 || function > 7) {
            function = CF_LESS;
        }
        this.function = function;
    }

    /**
     * <code>isWritable</code> returns if the depth mask is writable or not.
     * @return true if the depth mask is writable, false otherwise.
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * <code>setWritable</code> sets the depth mask writable or not.
     * @param writable true to turn on depth writing, false otherwise.
     */
    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    /**
     * <code>getType</code> returns the type of renderstate this is.
     * (RS_ZBUFFER).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_ZBUFFER;
    }
}
