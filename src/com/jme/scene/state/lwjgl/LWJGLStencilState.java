/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.GL11;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.StencilState;
import com.jme.scene.state.lwjgl.records.StencilStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLStencilState</code>
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLStencilState.java,v 1.9 2007/04/11 18:27:36 nca Exp $
 */
public class LWJGLStencilState extends StencilState {
	private static final long serialVersionUID = 2L;

    private static int[] stencilFunc = { GL11.GL_NEVER, GL11.GL_LESS,
            GL11.GL_LEQUAL, GL11.GL_GREATER, GL11.GL_GEQUAL, GL11.GL_EQUAL,
            GL11.GL_NOTEQUAL, GL11.GL_ALWAYS };

    private static int[] stencilOp = { GL11.GL_KEEP, GL11.GL_ZERO,
            GL11.GL_REPLACE, GL11.GL_INCR, GL11.GL_DECR, GL11.GL_INVERT };

    @Override
    public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        StencilStateRecord record = (StencilStateRecord) context
                .getStateRecord(RS_STENCIL);
        context.currentStates[RS_STENCIL] = this;

        setEnabled(isEnabled(), record);
        if (isEnabled()) {
            applyMask(getStencilWriteMask(), record);
            applyFunc(stencilFunc[getStencilFunc()], getStencilRef(),
                    getStencilFuncMask(), record);
            applyOp(stencilOp[getStencilOpFail()],
                    stencilOp[getStencilOpZFail()],
                    stencilOp[getStencilOpZPass()], record);
        }
        
        if (!record.isValid())
            record.validate();
    }

    private void setEnabled(boolean enable, StencilStateRecord record) {
        if (record.isValid()) {
            if (enable && !record.enabled)
                GL11.glEnable(GL11.GL_STENCIL_TEST);
            else if (!enable && record.enabled)
                GL11.glDisable(GL11.GL_STENCIL_TEST);
        } else {
            if (enable)
                GL11.glEnable(GL11.GL_STENCIL_TEST);
            else
                GL11.glDisable(GL11.GL_STENCIL_TEST);
        }
        
        record.enabled = enable;
    }

    private void applyMask(int writeMask, StencilStateRecord record) {
        if (!record.isValid() || writeMask != record.writeMask) {
            GL11.glStencilMask(writeMask);
            record.writeMask = writeMask;
        }
    }

    private void applyFunc(int glfunc, int stencilRef, int funcMask,
            StencilStateRecord record) {
        if (!record.isValid() || glfunc != record.func
                || stencilRef != record.ref || funcMask != record.funcMask) {
            GL11.glStencilFunc(glfunc, stencilRef, funcMask);
            record.func = glfunc;
            record.ref = stencilRef;
            record.funcMask = funcMask;
        }
    }

    private void applyOp(int fail, int zfail, int zpass,
            StencilStateRecord record) {
        if (!record.isValid() || fail != record.fail || zfail != record.zfail
                || zpass != record.zpass) {
            GL11.glStencilOp(fail, zfail, zpass);
            record.fail = fail;
            record.zfail = zfail;
            record.zpass = zpass;
        }
    }

    @Override
    public StencilStateRecord createStateRecord() {
        return new StencilStateRecord();
    }
}