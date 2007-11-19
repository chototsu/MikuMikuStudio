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
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.lwjgl.records.AlphaStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLAlphaState</code> subclasses the AlphaState using the LWJGL API
 * to set OpenGL's alpha state.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLAlphaState.java,v 1.10 2007/04/11 18:27:36 nca Exp $
 */
public class LWJGLAlphaState extends AlphaState {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor instantiates a new <code>LWJGLAlphaState</code> object with
	 * default values.
	 *  
	 */
	public LWJGLAlphaState() {
		super();
	}

	/**
	 * <code>set</code> is called to set the alpha state. If blending is
	 * enabled, the blend function is set up and if alpha testing is enabled the
	 * alpha functions are set.
	 * 
	 * @see com.jme.scene.state.RenderState#apply()
	 */
	public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        AlphaStateRecord record = (AlphaStateRecord) context
                .getStateRecord(RS_ALPHA);
        context.currentStates[RS_ALPHA] = this;

        if (isEnabled()) {
            applyBlend(isBlendEnabled(), getSrcFunction(), getDstFunction(), record);
            applyTest(isTestEnabled(), getTestFunction(), getReference(), record);
        } else {
            applyBlend(false, -1, -1, record);
            applyTest(false, -1, -1, record);
        }
        
        if (!record.isValid())
            record.validate();
	}

    private void applyBlend(boolean enabled, int srcBlend, int dstBlend, AlphaStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                if (!record.blendEnabled) {
                    GL11.glEnable(GL11.GL_BLEND);
                    record.blendEnabled = true;
                }
                int glSrc = getGLSrcValue(srcBlend);
                int glDst = getGLDstValue(dstBlend);
                if (record.srcFactor != glSrc || record.dstFactor != glDst) {
                    GL11.glBlendFunc(glSrc, glDst);
                    record.srcFactor = glSrc;
                    record.dstFactor = glDst;
                }
            } else if (record.blendEnabled) {
                GL11.glDisable(GL11.GL_BLEND);
                record.blendEnabled = false;
            }
            
        } else {
            if (enabled) {
                GL11.glEnable(GL11.GL_BLEND);
                record.blendEnabled = true;
                int glSrc = getGLSrcValue(srcBlend);
                int glDst = getGLDstValue(dstBlend);
                GL11.glBlendFunc(glSrc, glDst);
                record.srcFactor = glSrc;
                record.dstFactor = glDst;
            } else {
                GL11.glDisable(GL11.GL_BLEND);
                record.blendEnabled = false;
            }
        }
    }
    
    private int getGLSrcValue(int srcBlend) {
        switch (srcBlend) {
            case SB_ZERO:
                return GL11.GL_ZERO;
            case SB_ONE:
                return GL11.GL_ONE;
            case SB_DST_COLOR:
                return GL11.GL_DST_COLOR;
            case SB_ONE_MINUS_DST_COLOR:
                return GL11.GL_ONE_MINUS_DST_COLOR;
            case SB_SRC_ALPHA:
                return GL11.GL_SRC_ALPHA;
            case SB_ONE_MINUS_SRC_ALPHA:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            case SB_DST_ALPHA:
                return GL11.GL_DST_ALPHA;
            case SB_ONE_MINUS_DST_ALPHA:
                return GL11.GL_ONE_MINUS_DST_ALPHA;
            case SB_SRC_ALPHA_SATURATE:
                return GL11.GL_SRC_ALPHA_SATURATE;
        }
        return GL11.GL_ONE; // default
    }

    private int getGLDstValue(int dstBlend) {
        switch (dstBlend) {
            case DB_ZERO:
                return GL11.GL_ZERO;
            case DB_ONE:
                return GL11.GL_ONE;
            case DB_SRC_COLOR:
                return GL11.GL_SRC_COLOR;
            case DB_ONE_MINUS_SRC_COLOR:
                return GL11.GL_ONE_MINUS_SRC_COLOR;
            case DB_SRC_ALPHA:
                return GL11.GL_SRC_ALPHA;
            case DB_ONE_MINUS_SRC_ALPHA:
                return GL11.GL_ONE_MINUS_SRC_ALPHA;
            case DB_DST_ALPHA:
                return GL11.GL_DST_ALPHA;
            case DB_ONE_MINUS_DST_ALPHA:
                return GL11.GL_ONE_MINUS_DST_ALPHA;
        }
        return GL11.GL_ZERO; // default
    }

    private void applyTest(boolean enabled, int test, float reference, AlphaStateRecord record) {
        if (record.isValid()) {
            if (enabled) {
                if (!record.testEnabled) {
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    record.testEnabled = true;
                }
                int glFunc = getGLFuncValue(test);
                if (record.alphaFunc != glFunc || record.alphaRef != reference) {
                    GL11.glAlphaFunc(glFunc, reference);
                    record.alphaFunc = glFunc;
                    record.alphaRef = reference;
                }
            } else if (record.testEnabled) {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                record.testEnabled = false;
            }
            
        } else {
            if (enabled) {
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                record.testEnabled = true;
                int glFunc = getGLFuncValue(test);
                GL11.glAlphaFunc(glFunc, reference);
                record.alphaFunc = glFunc;
                record.alphaRef = reference;
            } else {
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                record.testEnabled = false;
            }
        }
    }

    private int getGLFuncValue(int test) {
        switch (test) {
            case TF_NEVER:
                return GL11.GL_NEVER;
            case TF_LESS:
                return GL11.GL_LESS;
            case TF_EQUAL:
                return GL11.GL_EQUAL;
            case TF_LEQUAL:
                return GL11.GL_LEQUAL;
            case TF_GREATER:
                return GL11.GL_GREATER;
            case TF_NOTEQUAL:
                return GL11.GL_NOTEQUAL;
            case TF_GEQUAL:
                return GL11.GL_GEQUAL;
            case TF_ALWAYS:
            default:
                return GL11.GL_ALWAYS;
        }
    }

    @Override
    public AlphaStateRecord createStateRecord() {
        return new AlphaStateRecord();
    }
}