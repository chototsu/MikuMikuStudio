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

import java.nio.IntBuffer;

import com.jme.renderer.Renderer;

/**
 * <code>Clone</code> defines a scene node that takes it's geometry data from
 * a set <code>TriMesh</code> from a parent <code>CloneNode</code>.
 * <code>Clone</code> has no real data of it's own, only a position, orientation
 * and render states. This allows for a more efficient system for reusing loaded
 * model data. It is intended to be a child of the <code>CloneNode</code>, and
 * this is where it obtains the relevant information needed to render. The
 * clone builds it's bounding volume by transforming the cloned geometry's
 * model bounds by it's current position, orientation and scale.
 * @author Mark Powell
 * @version $Id: Clone.java,v 1.7 2004-07-20 19:47:48 Mojomonkey Exp $
 */
public class Clone extends Spatial {

    private IntBuffer indexBuffer;

    /**
     * Constructor instantiates a new <code>Clone</code> object. The name of
     * the object is supplied during construction.
     * @param name the name of the cloned object.
     */
    public Clone(String name) {
        super(name);
    }

    /** <code>draw</code> render's this clone. It is assumed, maintained by
     * a <code>CloneNode</code> that the relevent Geometry data is already
     * supplied to the graphics card, and a call to render the data will
     * produce the desired results. Using <code>Clone</code> without
     * <code>CloneNode</code> will produce unpredictable results.
     * @param r the renderer used to draw this object.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        r.draw(this);
    }

    /**
     * <code>drawBounds</code> calls super to set the render state then passes itself
     * to the renderer.
     * @param r the renderer to display
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(this);
    }

    /** <code>updateWorldBound</code> updates the bounding volume of this
     * object. The geometry maintained by <code>CloneNode</code> is transformed
     * by this object's rotation, translation and scale. If the parent is
     * not a <code>CloneNode</code> the worldBound will not be updated.
     *
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        Node parent = this.getParent();
        if(parent instanceof CloneNode) {
            worldBound = ((CloneNode)parent).getGeometry().getModelBound().
                transform(worldRotation, worldTranslation, worldScale, worldBound);
        }
    }

    /**
     *
     * <code>setIndexBuffer</code> sets the indices that define how the
     * currently set geometry should be displayed.
     * @param indexBuffer the indices of the geometry.
     */
    public void setIndexBuffer(IntBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }
    
    public void setForceView(boolean value) {
    	forceView = value;
    }

    /**
     *
     * <code>getIndexBuffer</code> returns the indices of the geometry.
     * @return the indices of the geometry.
     */
    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

}
