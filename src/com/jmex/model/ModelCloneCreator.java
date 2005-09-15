/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jmex.model;

import java.util.Stack;

import com.jme.renderer.CloneCreator;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.system.JmeException;
import com.jmex.model.animation.JointController;
import com.jmex.model.animation.KeyframeController;

/**
 * <code>ModelCloneCreator</code>
 * 
 * @author Mark Powell
 * @version $id$
 */
public class ModelCloneCreator extends CloneCreator {

    /** Stack of to-be-processed KeyframeControllers Controllers. */
    private Stack kConts = new Stack();
    

    /** Stack of to-be-processed Joint Controllers. */
    private Stack jConts = new Stack();

    /**
     * @param toCopy
     */
    public ModelCloneCreator(Spatial toCopy) {
        super(toCopy);
    }
    
    /**
     * Creates a copy of the original and returns the copy.
     * 
     * @return
     */
    public Spatial createCopy() {
        setCreator = true;
        originalToCopy.clear();
        Spatial toReturn = toCopy.putClone(null, this);
        processJointStack();
        processSpatialStack();
        processKeyframeStack();
        return toReturn;
    }

    /**
     * Reasigns the original Spatial toCopy's KeyframeController to now animate
     * the copy.
     */
    private void processKeyframeStack() {
        while (!kConts.empty()) {
            KeyframeController kc = (KeyframeController) kConts.pop();
            TriMesh original = kc.getMorphMesh();
            TriMesh copy = (TriMesh) originalToCopy.get(original);
            if (copy == null)
                    throw new JmeException(
                            "Unable to match up copy for TriMesh "
                                    + original.getName());
            kc.shallowSetMorphMesh(copy);
        }
    }

    /**
     * Reasigns the original Spatial toCopy's JointController to now animate the
     * copy.
     */
    private void processJointStack() {
        while (!jConts.empty()) {
            JointController jc = (JointController) jConts.pop();
            for (int i = 0; i < jc.movingMeshes.size(); i++) {
                JointMesh original = (JointMesh) jc.movingMeshes.get(i);
                JointMesh copy = (JointMesh) originalToCopy.get(original);
                if (copy == null)
                        throw new JmeException(
                                "Unable to match up copy for JointMesh "
                                        + original.getName());
                jc.movingMeshes.set(i, copy);
            }

        }
    }

    /**
     * Signals that a JointController needs to be processed after the clone
     * happens. This should not be called by users directly. It is called by the
     * cloned JointController automatically.
     * 
     * @param jc
     *            The JointController that will later be processed.
     */
    public void queueJointController(JointController jc) {
        jConts.add(jc);
    }
    
    /**
     * Signals that a KeyframeController needs to be processed after the clone
     * happens. This should not be called by users directly. It is called by the
     * cloned KeyframeController automatically.
     * 
     * @param kc
     *            The KeyframeController that will later be processed.
     */
    public void queueKeyframeController(KeyframeController kc) {
        kConts.add(kc);
    }

}