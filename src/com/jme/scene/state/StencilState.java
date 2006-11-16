/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene.state;

import java.io.IOException;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * The StencilState RenderState allows the user to set the attributes of the
 * stencil buffer of the renderer. The Stenciling is similar to
 * Z-Buffering in that it allows enabling and disabling drawing on a per pixel
 * basis. You can use the stencil plane to mask out portions of the rendering
 * to create special effects, such as outlining or planar shadows.
 * @author Mark Powell
 * @version $id$
 */
public abstract class StencilState extends RenderState {

    /** A stencil function that never passes. */
    public static final int SF_NEVER = 0;
    /** A stencil function that passes if (ref & mask) < (stencil & mask). */
    public static final int SF_LESS = 1;
    /** A stencil function that passes if (ref & max) <= (stencil & mask). */
    public static final int SF_LEQUAL = 2;
    /** A stencil function that passes if (ref & max) > (stencil & mask). */
    public static final int SF_GREATER = 3;
    /** A stencil function that passes if (ref & max) >= (stencil & mask). */
    public static final int SF_GEQUAL = 4;
    /** A stencil function that passes if (ref & max) == (stencil & mask). */
    public static final int SF_EQUAL = 5;
    /** A stencil function that passes if (ref & max) != (stencil & mask). */
    public static final int SF_NOTEQUAL = 6;
    /** A stencil function that always passes. */
    public static final int SF_ALWAYS = 7;

    /** A stencil function result that keeps the current value. */
    public static final int SO_KEEP = 0;
    /** A stencil function result that sets the stencil buffer value to 0. */
    public static final int SO_ZERO = 1;
    /**
     * A stencil function result that sets the stencil buffer value to ref, as
     * specified by stencil function.
     */
    public static final int SO_REPLACE = 2;
    /**
     * A stencil function result that increments the current stencil buffer
     * value.
     */
    public static final int SO_INCR = 3;
    /**
     * A stencil function result that decrements the current stencil buffer
     * value.
     */
    public static final int SO_DECR = 4;
    /**
     * A stencil function result that bitwise inverts the current stencil buffer
     * value.
     */
    public static final int SO_INVERT = 5;

    private int stencilFunc;
    private int stencilRef;
    private int stencilWriteMask;
    private int stencilFuncMask;
    private int stencilOpFail;
    private int stencilOpZFail;
    private int stencilOpZPass;

    /**
     * Returns RS_STENCIL
     * 
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_STENCIL;
    }

    /**
     * Sets the function that defines if a stencil test passes or not.
     * 
     * @param func
     *            The new stencil function.
     */
    public void setStencilFunc(int func) {
        this.stencilFunc = func;
        setNeedsRefresh(true);
    }

    /**
     * Returns the currently set stencil function.
     * 
     * @return The current stencil function.
     */
    public int getStencilFunc() {
        return stencilFunc;
    }

    /**
     * Sets the stencil reference to be used during the stencil function.
     * 
     * @param ref
     *            The new stencil reference.
     */
    public void setStencilRef(int ref) {
        this.stencilRef = ref;
        setNeedsRefresh(true);
    }

    /**
     * Returns the currently set stencil reference.
     * 
     * @return The current stencil reference.
     */
    public int getStencilRef() {
        return stencilRef;
    }

    /**
     * Convienence method for setting both types of stencil masks at once.
     * 
     * @param mask
     *            The new stencil write and func mask.
     */
    public void setStencilMask(int mask) {
        setStencilWriteMask(mask);
        setStencilFuncMask(mask);
    }

    /**
     * Controls which stencil bitplanes are written.
     * 
     * @param mask
     *            The new stencil write mask.
     */
    public void setStencilWriteMask(int mask) {
        this.stencilWriteMask = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil write mask.
     */
    public int getStencilWriteMask() {
        return stencilWriteMask;
    }

    /**
     * Sets the stencil mask to be used during stencil functions.
     * 
     * @param mask
     *            The new stencil function mask.
     */
    public void setStencilFuncMask(int mask) {
        this.stencilFuncMask = mask;
        setNeedsRefresh(true);
    }

    /**
     * @return The current stencil function mask.
     */
    public int getStencilFuncMask() {
        return stencilFuncMask;
    }

    /**
     * Specifies the aciton to take when the stencil test fails. One of SO_KEEP,
     * SO_ZERO, and so on.
     * 
     * @param op
     *            The new stencil operation.
     */
    public void setStencilOpFail(int op) {
        this.stencilOpFail = op;
        setNeedsRefresh(true);
    }

    /**
     * Returns the current stencil operation.
     * 
     * @return The current stencil operation.
     */
    public int getStencilOpFail() {
        return stencilOpFail;
    }

    /**
     * Specifies stencil action when the stencil test passes, but the depth test
     * fails. One of SO_KEEP, SO_ZERO, and so on.
     * 
     * @param op
     *            The Z test operation to set.
     */
    public void setStencilOpZFail(int op) {
        this.stencilOpZFail = op;
        setNeedsRefresh(true);
    }

    /**
     * Returns the current Z op fail function.
     * 
     * @return The current Z op fail function.
     */
    public int getStencilOpZFail() {
        return stencilOpZFail;
    }

    /**
     * Specifies stencil action when both the stencil test and the depth test
     * pass, or when the stencil test passes and either there is no depth buffer
     * or depth testing is not enabled. One of SO_KEEP, SO_ZERO, and so on.
     * 
     * @param op
     *            The new Z test pass operation to set.
     */
    public void setStencilOpZPass(int op) {
        this.stencilOpZPass = op;
        setNeedsRefresh(true);
    }

    /**
     * Returns the current Z op pass function.
     * 
     * @return The current Z op pass function.
     */
    public int getStencilOpZPass() {
        return stencilOpZPass;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(stencilFunc, "stencilFunc", SF_NEVER);
        capsule.write(stencilRef, "stencilRef", 0);
        capsule.write(stencilWriteMask, "stencilWriteMask", 0);
        capsule.write(stencilFuncMask, "stencilFuncMask", 0);
        capsule.write(stencilOpFail, "stencilOpFail", SO_KEEP);
        capsule.write(stencilOpZFail, "stencilOpZFail", SO_KEEP);
        capsule.write(stencilOpZPass, "stencilOpZPass", SO_KEEP);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        stencilFunc = capsule.readInt("stencilFunc", SF_NEVER);
        stencilRef = capsule.readInt("stencilRef", 0);
        stencilWriteMask = capsule.readInt("stencilWriteMask", 0);
        stencilFuncMask = capsule.readInt("stencilFuncMask", 0);
        stencilOpFail = capsule.readInt("stencilOpFail", SO_KEEP);
        stencilOpZFail = capsule.readInt("stencilOpZFail", SO_KEEP);
        stencilOpZPass = capsule.readInt("stencilOpZPass", SO_KEEP);
    }

    public Class getClassTag() {
        return StencilState.class;
    }
}
