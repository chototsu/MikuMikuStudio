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
package com.jme.scene;

import java.util.logging.Level;

import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.scene.state.RenderState;
import java.util.Stack;

/**
 * <code>CloneNode</code> is a node that maintains a list of only Clone
 * children, and a TriMesh geometry.  Each Clone child will have the information
 * of the geometry, but may have its own position, orientation, and render states.
 *
 * @author Mark Powell
 * @version $Id: CloneNode.java,v 1.7 2004-08-01 02:56:12 cep21 Exp $
 */
public class CloneNode extends Node {
    private TriMesh geometry;
    RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

    /**
     * Creates a new CloneNode with the given name.
     * @param name The name of the CloneNode.
     */
    public CloneNode(String name) {
        super(name);
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained
     * by this node.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r the renderer to draw to.
     */
    public void draw(Renderer r) {
      if (!r.isProcessingQueue()) {
        if (r.checkAndAdd(this))
          return;
      }
      applyStates();
      r.draw(this);
      super.draw(r);
    }

    /**
     * Applies the stack of render states to each child by calling updateRenderState(states) on
     * each child.
     * @param states The Stack[] of render states to apply to each child.
     */
    protected void applyRenderState(Stack[] states) {
      for (int x = 0; x < states.length; x++) {
        if (states[x].size() > 0) {
          this.states[x] = ((RenderState) states[x].peek()).extract(states[x], this);
        } else {
          this.states[x] = (RenderState) defaultStateList[x];
        }
      }
    }

    /**
     *
     * <code>setStates</code> applies all the render states for this
     * particular geometry.
     *
     */
    public void applyStates() {
      if (parent == null || isRoot)
        Spatial.clearCurrentStates();
      for (int i = 0; i < states.length; i++) {
        if (states[i] != currentStates[i]) {
          states[i].apply();
          currentStates[i] = states[i];
        }
      }
    }

    /**
     * <code>draw</code> calls the onDraw method for each child maintained
     * by this node.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r the renderer to draw to.
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(getWorldBound());
        for (int i = 0, cSize = children.size(); i < cSize ; i++) {
            Spatial child = (Spatial) children.get(i);
            if (child != null)
                child.onDrawBounds(r);
        }
    }

    /**
     *
     * <code>attachChild</code> attaches a child to this node. This node
     * becomes the child's parent. The current number of children maintained
     * is returned.
     * @param child the child to attach to this node.
     * @return the number of children maintained by this node.
     */
    public int attachChild(Spatial child) {
        if(!(child instanceof Clone)) {
            throw new JmeException("Child of CloneNode must be a Clone.");
        }
        if(child == null) {
            return children.size();
        }
        if(!children.contains(child)) {
            child.setParent(this);
            children.add(child);
            if(geometry != null) {
                ((Clone)child).setIndexBuffer(geometry.getIndexAsBuffer());
            }
        }
        LoggingSystem.getLogger().log(Level.INFO, "Child (" + child.getName() + ") attached to this" +
            " node (" + name + ")");
        return children.size();
    }

    /**
     * Sets the geometry that defines how each child of this CloneNode will look.  This
     * may be called before or after children are added.
     * @param geometry The new geometry of each child.
     */
    public void setGeometry(TriMesh geometry) {
        this.geometry = geometry;
        for (int i = 0; i < this.getQuantity(); i++) {
            ((Clone)this.getChild(i)).setIndexBuffer(this.geometry.getIndexAsBuffer());
        }
    }

    /**
     * Returns the current geometry that defines how this CloneNode's children
     * will look.
     * @return The CloneNode's current geometry.
     */
    public TriMesh getGeometry() {
        return geometry;
    }
}
