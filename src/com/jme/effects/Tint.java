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
package com.jme.effects;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;

/**
 * <code>Tint</code>
 * @author Ahmed
 * @version $Id: Tint.java,v 1.6 2004-04-22 22:26:25 renanse Exp $
 */
public class Tint extends Geometry {

	private ColorRGBA tintColor;

	public Tint(String name, ColorRGBA c) {
		super(name);
		tintColor = c;
	}

	public Tint(String name) {
		super(name);
		tintColor = new ColorRGBA(0, 0, 0, 0);
	}

	public void draw(Renderer r) {
		super.draw(r);
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

	public ColorRGBA getTintColor() {
		return tintColor;
	}

	public void setTintColor(ColorRGBA c) {
		tintColor = c;
	}
}
