/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

import java.io.Serializable;
import java.util.ArrayList;

import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;

/**
 * <code>Pass</code> encapsulates logic necessary for rendering one or more
 * steps in a multipass technique.
 * 
 * Rendering:
 *  
 *      When renderPass is called, a check is first made to see if the
 *      pass isEnabled(). Then any states set on this pass are enforced via
 *      Spatial.enforceState(RenderState). This is useful for doing things such
 *      as causing this pass to be blended to a previous pass via enforcing an
 *      BlendState, etc.  Next, doRender(Renderer) is called to do the actual
 *      rendering work.  Finally, any enforced states set before this pass was
 *      run are restored.
 *      
 * @author Joshua Slack
 * @version $Id: Pass.java,v 1.9 2007/08/14 13:41:40 rherlitz Exp $
 */
public abstract class Pass implements Serializable {

    /** list of spatials registered with this pass. */
    protected ArrayList<Spatial> spatials = new ArrayList<Spatial>();
    
    /** if false, pass will not be updated or rendered. */
    protected boolean enabled = true;

    /** offset params to use to differentiate multiple passes of the same scene in the zbuffer. */
    protected float zFactor;
    protected float zOffset;
    
    /**
     * RenderStates registered with this pass - if a given state is not null it
     * overrides the corresponding state set during rendering.
     */
    protected RenderState[] passStates = new RenderState[RenderState.RS_MAX_STATE];

    /** a place to internally save previous states setup before rendering this pass */
    protected RenderState[] savedStates = new RenderState[RenderState.RS_MAX_STATE];

    protected RenderContext context = null;
    
    /** if enabled, set the states for this pass and then render. */
    public final void renderPass(Renderer r) {
        if (!enabled) return;
        context  = DisplaySystem.getDisplaySystem().getCurrentContext();
        applyPassStates();
        r.setPolygonOffset(zFactor, zOffset);
        doRender(r);
        r.clearPolygonOffset();
        resetOldStates();
        context = null;
    }

    /**
     * Enforce a particular state. In other words, the given state will override
     * any state of the same type set on a scene object. Remember to clear the
     * state when done enforcing. Very useful for multipass techniques where
     * multiple sets of states need to be applied to a scenegraph drawn multiple
     * times.
     * 
     * @param state
     *            state to enforce
     */
    public void setPassState(RenderState state) {
        passStates[state.getType()] = state;
    }

    /**
     * Clears an enforced render state index by setting it to null. This allows
     * object specific states to be used.
     * 
     * @param renderStateType
     *            The type of RenderState to clear enforcement on.
     */
    public void clearPassState(int renderStateType) {
        passStates[renderStateType] = null;
    }

    /**
     * sets all enforced states to null.
     * 
     * @see RenderContext#clearEnforcedState(int)
     */
    public void clearPassStates() {
        for (int i = 0; i < passStates.length; i++)
            passStates[i] = null;
    }

    protected void applyPassStates() {
        for (int x = RenderState.RS_MAX_STATE; --x >= 0; ) {
            if (passStates[x] != null) {
                savedStates[x] = context.enforcedStateList[x];
                context.enforcedStateList[x] = passStates[x];
            }
        }
    }
    
    protected abstract void doRender(Renderer r);

    protected void resetOldStates() {
        for (int x = RenderState.RS_MAX_STATE; --x >= 0; ) {
            if (passStates[x] != null) {
                context.enforcedStateList[x] = savedStates[x];
            }
        }
    }

    /** if enabled, call doUpdate to update information for this pass. */
    public final void updatePass(float tpf) {
        if (!enabled) return;
        doUpdate(tpf);
    }
    
    protected void doUpdate(float tpf) {
        ; // nothing to do.
    }

    
    public void add(Spatial toAdd) {
        spatials.add(toAdd);
    }
    
    public Spatial get(int index) {
        return spatials.get(index);
    }

    public boolean contains(Spatial s) {
        return spatials.contains(s);
    }
    
    public boolean remove(Spatial toRemove) {
        return spatials.remove(toRemove);
    }
    
    public int size() {
        return spatials.size();
    }
    
    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return Returns the zFactor.
     */
    public float getZFactor() {
        return zFactor;
    }

    /**
     * Sets the polygon offset param - factor - for this Pass.
     * 
     * @param factor
     *            The zFactor to set.
     */
    public void setZFactor(float factor) {
        zFactor = factor;
    }

    /**
     * @return Returns the zOffset.
     */
    public float getZOffset() {
        return zOffset;
    }

    /**
     * Sets the polygon offset param - offset - for this Pass.
     * 
     * @param offset
     *            The zOffset to set.
     */
    public void setZOffset(float offset) {
        zOffset = offset;
    }

    public void cleanUp() {
    }
}
