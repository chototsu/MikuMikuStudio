/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
 * <code>Clone</code>
 * @author Mark Powell
 * @version $Id: Clone.java,v 1.2 2004-02-27 23:51:42 renanse Exp $
 */
public class Clone extends Spatial {

    private IntBuffer indexBuffer;

    public Clone(String name) {
        super(name);
    }

    /** <code>draw</code>
     * @param r
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {
        r.draw(this);
    }

    /** <code>updateWorldBound</code>
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

    public void setIndexBuffer(IntBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public IntBuffer getIndexBuffer() {
        return indexBuffer;
    }

}
