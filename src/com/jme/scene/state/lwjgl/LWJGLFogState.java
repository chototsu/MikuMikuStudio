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

package com.jme.scene.state.lwjgl;

import org.lwjgl.opengl.GL11;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.scene.state.FogState;
import com.jme.scene.state.lwjgl.records.FogStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLFogState</code> subclasses the fog state using the LWJGL API to
 * set the OpenGL fog state.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLFogState.java,v 1.12 2006-11-16 19:18:03 nca Exp $
 */
public class LWJGLFogState extends FogState {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor instantiates a new <code>LWJGLFogState</code> object with
	 * default values.
	 *  
	 */
	public LWJGLFogState() {
		super();
	}

	/**
	 * <code>set</code> sets the OpenGL fog values if the state is enabled.
	 * 
	 * @see com.jme.scene.state.RenderState#apply()
	 */
	public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        FogStateRecord record = (FogStateRecord) context
                .getStateRecord(RS_FOG);
        context.currentStates[RS_FOG] = this;

		if (isEnabled()) {
            enableFog(true, record);
            
            if (record.fogStart != start) {
                GL11.glFogf(GL11.GL_FOG_START, start);
                record.fogStart = start;
            }
            if (record.fogEnd != end) {
                GL11.glFogf(GL11.GL_FOG_END, end);
                record.fogEnd = end;
            }            
            if (record.density != density) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, density);
                record.density = density;
            }

            applyFogColor(getColor(), record);
            applyFogMode(densityFunction, record);
            applyFogHint(applyFunction, record);
		} else {
            enableFog(false, record);
		}
	}

    private void enableFog(boolean enable, FogStateRecord record) {
        if (enable && !record.enabled) {
            GL11.glEnable(GL11.GL_FOG);
            record.enabled = true;
        } else if (!enable && record.enabled) {
            GL11.glDisable(GL11.GL_FOG);
            record.enabled = false;
        }
    }

    private void applyFogColor(ColorRGBA color, FogStateRecord record) {
        if (!color.equals(record.fogColor)) {
            record.fogColor.set(color);
            record.colorBuff.clear();
            record.colorBuff.put(record.fogColor.r).put(record.fogColor.g).put(
                    record.fogColor.b).put(record.fogColor.a);
            record.colorBuff.flip();
            GL11.glFog(GL11.GL_FOG_COLOR, record.colorBuff);
        }
    }

    private void applyFogMode(int densityFunction, FogStateRecord record) {
        int glMode;
        switch (densityFunction) {
            case DF_LINEAR:
                glMode = GL11.GL_LINEAR;
                break;
            case DF_EXPSQR:
                glMode = GL11.GL_EXP2;
                break;
            case DF_EXP:
            default:
                glMode = GL11.GL_EXP;
                break;
        }
        
        if (record.fogMode != glMode) {
            GL11.glFogi(GL11.GL_FOG_MODE, glMode);
            record.fogMode = glMode;
        }
    }

    private void applyFogHint(int applyFunction, FogStateRecord record) {
        int glHint;
        switch (applyFunction) {
            case AF_PER_VERTEX:
                glHint = GL11.GL_FASTEST;
                break;
            case AF_PER_PIXEL:
            default:
                glHint = GL11.GL_NICEST;
                break;
        }
        
        if (record.fogHint != glHint) {
            GL11.glHint(GL11.GL_FOG_HINT, glHint);
            record.fogHint = glHint;
        }
    }

    @Override
    public FogStateRecord createStateRecord() {
        return new FogStateRecord();
    }
}