/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

/*
 * Created on 25 janv. 2004
 *
 */
package com.jme.sound.fmod.scene;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.sound.fmod.SoundSystem;

/**
 * @author Arman Ozcelik
 * 
 */
public abstract class SoundSpatial extends Playable{

    private boolean forceCull;
    private SoundSpatial parent;
    protected boolean allowInterrupt = true;
    


    /**
     * <code>setParent</code> sets the parent of this node.
     * 
     * @param parent
     *            the parent of this node.
     */
    public void setParent(SoundSpatial node) {
        parent = node;
    }

    /**
     * <code>getParent</code> retrieve's this node's parent. If the parent is
     * null this is the root node.
     * 
     * @return the parent of this node.
     */
    public SoundSpatial getParent() {
        return parent;
    }

    /**
     * @param time
     */
    public void updateWorldData(float time) {
       
    }

    /**
     * 
     * <code>propagateBoundToRoot</code> passes the new world bound up the
     * tree to the root.
     * 
     */
    public void propagateBoundToRoot() {
        if (parent != null) {
            parent.propagateBoundToRoot();
        }
    }

    /**
     * @param time
     * @param b
     */
    public void updateGeometricState(float time, boolean initiator) {
        if (initiator) {
            propagateBoundToRoot();
        }

    }

    /**
     * 
     * <code>onDraw</code> checks the node with the camera to see if it should
     * be culled, if not, the node's draw method is called.
     * 
     * @param r
     *            the renderer used for display.
     */
    public void onDraw() {
        if (forceCull) {
            return;
        }
        draw();
    }

    public abstract void draw();

    /**
     * @param b
     */
    public void setForceCull(boolean b) {
        forceCull = b;
    }

    public void setAllowInterrupt(boolean allow) {
        allowInterrupt = allow;
    }

    public boolean allowsInterrupt() {
        return allowInterrupt;
    }

    public abstract boolean fireEvent(int event);
}
