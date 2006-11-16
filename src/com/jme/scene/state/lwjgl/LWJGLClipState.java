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

import java.nio.DoubleBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.jme.renderer.RenderContext;
import com.jme.scene.state.ClipState;
import com.jme.scene.state.lwjgl.records.ClipStateRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>LWJGLClipState</code>
 * @author Joshua Slack - reworked for StateRecords.
 */
public class LWJGLClipState extends ClipState {

    private static final long serialVersionUID = 1L;

    private transient DoubleBuffer buf;

    public LWJGLClipState() {
        buf = BufferUtils.createDoubleBuffer(4);
    }

    /**
     * <code>apply</code>
     * 
     * @see com.jme.scene.state.ClipState#apply()
     */
    public void apply() {
        // ask for the current state record
        RenderContext context = DisplaySystem.getDisplaySystem()
                .getCurrentContext();
        ClipStateRecord record = (ClipStateRecord) context
                .getStateRecord(RS_CLIP);
        context.currentStates[RS_CLIP] = this;

        if (isEnabled()) {
            for (int i = 0; i < MAX_CLIP_PLANES; i++) {
                enableClipPlane(i, enabledClipPlanes[i], record);
            }
        } else {
            for (int i = 0; i < MAX_CLIP_PLANES; i++) {
                enableClipPlane(i, false, record);
            }
        }
    }

    private void enableClipPlane(int planeIndex, boolean enable, ClipStateRecord record) {
        if (enable) {
            if (!record.planeEnabled[planeIndex]) {
                GL11.glEnable(GL11.GL_CLIP_PLANE0 + planeIndex);
                record.planeEnabled[planeIndex] = true;
            }
            if (!Arrays.equals(record.planeEq[planeIndex], planeEquations[planeIndex])) {
                buf.rewind();
                buf.put(planeEquations[planeIndex]);
                GL11.glClipPlane(GL11.GL_CLIP_PLANE0 + planeIndex, buf);
                System.arraycopy(planeEquations[planeIndex], 0, record.planeEq[planeIndex], 0, 4);
            }
        } else {
            if (record.planeEnabled[planeIndex]) {
                GL11.glDisable(planeIndex);
                record.planeEnabled[planeIndex] = false;
            }
        }

    }

    @Override
    public ClipStateRecord createStateRecord() {
        return new ClipStateRecord();
    }
}