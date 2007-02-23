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

package com.jme.renderer.pass;

import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;

import java.util.ArrayList;

/**
 * <code>LightMaskedRenderPass</code> renders the spatials attached to it with
 * all light states masked as defined by a given mask - default mask is 0 or no
 * mask. This is useful for doing things like an ambient pass of a scene. Any
 * masks set on the light states prior to rendering this pass are replaced after
 * the pass is run.
 * 
 * @author Joshua Slack
 * @version $Id: LightMaskedRenderPass.java,v 1.6 2007-02-23 17:08:08 irrisor Exp $
 */
public class LightMaskedRenderPass extends Pass {
    
    private static final long serialVersionUID = 1L;
    protected ArrayList<LightState> lightStates = new ArrayList<LightState>();
    protected int mask = 0;

    public void doRender(Renderer r) {
        for (int i = 0, sSize = spatials.size(); i < sSize; i++) {
            Spatial s = spatials.get(i);
            maskLightStates(s);
            r.draw(s);
        }
        r.renderQueue();
        unmaskLightStates();
        lightStates.clear();
    }

    private void maskLightStates(Spatial s) {
        if ((s.getType() & SceneElement.GEOMETRY) != 0) {
            Geometry g = (Geometry)s;
            for (int x = 0; x < g.getBatchCount(); x++) {
                LightState ls = (LightState) g.getBatch(x).states[RenderState.RS_LIGHT];
                if (ls != null && !lightStates.contains(ls)) {
                    lightStates.add(ls);
                    ls.pushLightMask();
                    ls.setLightMask(mask);
                }
            }
        }
        if ((s.getType() & SceneElement.NODE) != 0) {
            Node n = (Node)s;
            ArrayList children = n.getChildren();
            if (children != null) {
                for (int i = children.size(); --i >= 0; ) {
                    Spatial child = (Spatial)children.get(i);
                    maskLightStates(child);
                }
            }
        }
    }

    private void unmaskLightStates() {
        for (int i = lightStates.size(); --i >= 0; ) {
            LightState ls = lightStates.get(i);
            ls.popLightMask();
        }
    }

    /**
     * @return Returns the mask.
     */
    public int getMask() {
        return mask;
    }

    /**
     * @param mask The mask to set.
     */
    public void setMask(int mask) {
        this.mask = mask;
    }
}
