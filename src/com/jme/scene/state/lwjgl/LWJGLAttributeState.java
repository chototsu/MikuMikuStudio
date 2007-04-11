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
import com.jme.scene.state.AttributeState;
import com.jme.scene.state.lwjgl.records.AttributeStateRecord;
import com.jme.scene.state.lwjgl.records.StateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLAttributeState</code>
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLAttributeState.java,v 1.6 2004/09/08 17:06:47 mojomonkey
 *          Exp $
 */
public class LWJGLAttributeState extends AttributeState {
	private static final long serialVersionUID = 1L;

	/**
	 * <code>set</code>
	 * 
	 * @see com.jme.scene.state.RenderState#apply() ()
	 */
	public void apply() {
		RenderContext context = DisplaySystem.getDisplaySystem()
				.getCurrentContext();
		AttributeStateRecord record = (AttributeStateRecord) context
				.getStateRecord(RS_ATTRIBUTE);
        context.currentStates[RS_ATTRIBUTE] = this;

        if (isEnabled()) {
			switch(getMask()) {
				case ALL_ATTRIB_BIT:
					setMask(GL11.GL_ALL_ATTRIB_BITS, record);
					break;
				case ACCUM_BUFFER_BIT:
					setMask(GL11.GL_ACCUM_BUFFER_BIT, record);
					break;
				case COLOR_BUFFER_BIT:
					setMask(GL11.GL_COLOR_BUFFER_BIT, record);
					break;
				case CURRENT_BIT:
					setMask(GL11.GL_CURRENT_BIT, record);
					break;
				case DEPTH_BUFFER_BIT:
					setMask(GL11.GL_DEPTH_BUFFER_BIT, record);
					break;
				case ENABLE_BIT:
					setMask(GL11.GL_ENABLE_BIT, record);
					break;
				case EVAL_BIT:
					setMask(GL11.GL_EVAL_BIT, record);
					break;
				case FOG_BIT:
					setMask(GL11.GL_FOG_BIT, record);
					break;
				case HINT_BIT:
					setMask(GL11.GL_HINT_BIT, record);
					break;
				case LIGHTING_BIT:
					setMask(GL11.GL_LIGHTING_BIT, record);
					break;
				case LINE_BIT:
					setMask(GL11.GL_LINE_BIT, record);
					break;
				case LIST_BIT:
					setMask(GL11.GL_LIST_BIT, record);
					break;
				case PIXEL_MODE_BIT:
					setMask(GL11.GL_PIXEL_MODE_BIT, record);
					break;
				case POINT_BIT:
					setMask(GL11.GL_POINT_BIT, record);
					break;
				case POLYGON_BIT:
					setMask(GL11.GL_POLYGON_BIT, record);
					break;
				case POLYGON_STIPPLE_BIT:
					setMask(GL11.GL_POLYGON_STIPPLE_BIT, record);
					break;
				case SCISSOR_BIT:
					setMask(GL11.GL_SCISSOR_BIT, record);
					break;
				case STENCIL_BUFFER_BIT:
					setMask(GL11.GL_STENCIL_BUFFER_BIT, record);
					break;
				case TEXTURE_BIT:
					setMask(GL11.GL_TEXTURE_BIT, record);
					break;
				case TRANSFORM_BIT:
					setMask(GL11.GL_TRANSFORM_BIT, record);
					break;
				case VIEWPORT_BIT:
					setMask(GL11.GL_VIEWPORT_BIT, record);
					break;
				default:
					break;
			}
			
		} else if (level > 0) {
			GL11.glPopAttrib();
			level--;
		}
        
        if (!record.isValid())
            record.validate();
	}
	
	private void setMask(int mask, AttributeStateRecord record) {
		if(!record.isValid() || mask != record.getMask()) {
            // XXX: being in here means you can't push the same attrib twice.
            GL11.glPushAttrib(mask);
			record.setMask(mask);
			level++;
		}
	}

	@Override
	public StateRecord createStateRecord() {
		return new AttributeStateRecord();
	}
}