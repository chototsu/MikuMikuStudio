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

package com.jme.renderer;

import com.jme.scene.state.RenderState;
import com.jme.scene.state.lwjgl.records.StateRecord;

/**
 * Represents the state of an individual context in OpenGL.
 * 
 * @author Joshua Slack
 * @version $Id: RenderContext.java,v 1.2 2007-04-11 18:27:35 nca Exp $
 */
public class RenderContext {

    /** List of states that override any set states on a spatial if not null. */
    public RenderState[] enforcedStateList = new RenderState[RenderState.RS_MAX_STATE];

    /** RenderStates a Spatial contains during rendering. */
    public RenderState[] currentStates = new RenderState[RenderState.RS_MAX_STATE];

    StateRecord[] records = new StateRecord[RenderState.RS_MAX_STATE];
    StateRecord lineRecord = null;
    
    public void setupRecords(Renderer r) {
        for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
            records[i] = r.createState(i).createStateRecord();
        }
        lineRecord = r.createLineRecord();
    }
    
    public void invalidateStates() {
        for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
            records[i].invalidate();
        }
        lineRecord.invalidate();
        
        clearCurrentStates();
    }
    
    public StateRecord getStateRecord(int state) {
        return records[state];
    }

    public StateRecord getLineRecord() {
        return lineRecord;
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
    public void enforceState(RenderState state) {
        enforcedStateList[state.getType()] = state;
    }

    /**
     * Clears an enforced render state index by setting it to null. This allows
     * object specific states to be used.
     * 
     * @param renderStateType
     *            The type of RenderState to clear enforcement on.
     */
    public void clearEnforcedState(int renderStateType) {
        if (enforcedStateList != null) {
            enforcedStateList[renderStateType] = null;
        }
    }

    /**
     * sets all enforced states to null.
     * 
     * @see com.jme.scene.Spatial#clearEnforcedState(int)
     */
    public void clearEnforcedStates() {
        for (int i = 0; i < enforcedStateList.length; i++)
            enforcedStateList[i] = null;
    }

    /**
     * sets all current states to null, and therefore forces the use of the
     * default states.
     *
     */
    public void clearCurrentStates() {
        for (int i = 0; i < currentStates.length; i++)
            currentStates[i] = null;
    }

    /**
     * clears the specified state. The state is referenced by it's int value,
     * and therefore should be called via RenderState's constant list. For
     * example, RenderState.RS_ALPHA.
     *
     * @param state
     *            the state to clear.
     */
    public void clearCurrentState(int state) {
        currentStates[state] = null;
    }

    public RenderState getCurrentState(int state) {
        return currentStates[state];
    }
}
