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

import com.jme.renderer.RenderContext;
import com.jme.scene.state.CullState;
import com.jme.scene.state.lwjgl.records.CullStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLCullState</code>
 * 
 * @author Mark Powell
 * @author Tijl Houtbeckers (added flipped culling mode)
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLCullState.java,v 1.10 2006-11-16 19:18:02 nca Exp $
 */
public class LWJGLCullState extends CullState {

    private static final long serialVersionUID = 1L;

    /**
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        CullStateRecord record = (CullStateRecord) context
                .getStateRecord(RS_CULL);
        context.currentStates[RS_CULL] = this;

        if (isEnabled()) {
            int useCullMode = getCullMode();

            // check if we should flip the culling mode before applying.
            // FIXME: REMOVE THIS FLIP STUFF WHEN WE GET A POLYGON WIND STATE.
            if (CullState.isFlippedCulling()) {
                if (useCullMode == CullState.CS_BACK)
                    useCullMode = CullState.CS_FRONT;
                else if (useCullMode == CullState.CS_FRONT)
                    useCullMode = CullState.CS_BACK;
            }

            switch (useCullMode) {
                case CS_FRONT:
                    setCull(GL11.GL_FRONT, record);
                    setCullEnabled(true, record);
                    break;
                case CS_BACK:
                    setCull(GL11.GL_BACK, record);
                    setCullEnabled(true, record);
                    break;
                case CS_NONE:
                default:
                    setCullEnabled(false, record);
                    break;
            }
        } else {
            setCullEnabled(false, record);
        }

    }

    private void setCullEnabled(boolean enable, CullStateRecord record) {
        if (record.enabled != enable) {
            if (enable)
                GL11.glEnable(GL11.GL_CULL_FACE);
            else
                GL11.glDisable(GL11.GL_CULL_FACE);
            record.enabled = enable;
        }
    }

    private void setCull(int face, CullStateRecord record) {
        if (record.face != face) {
            GL11.glCullFace(face);
            record.face = face;
        }
    }

    @Override
    public CullStateRecord createStateRecord() {
        return new CullStateRecord();
    }
}