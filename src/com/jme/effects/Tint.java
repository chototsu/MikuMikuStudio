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
 * <code>Tint</code> draws a tint over the entire render screen.  The color
 * of the tint is specified by the <code>tintColor</code> of the object.  An
 * alpha value can be used to 'weaken' the tint color.  If used for that purpose,
 * users should combine the Tint with an alpha state that will blend the alpha
 * values correctly.
 * @author Ahmed
 * @author Jack Lindamood (javadoc only)
 * @version $Id: Tint.java,v 1.7 2004-08-03 02:50:57 cep21 Exp $
 */
public class Tint extends Geometry {

    /** The tint color used by this Geometry. */
	private ColorRGBA tintColor;

    /**
     * Creates a new Tint object with the given name and starting tint color.
     * @param name The name of the Tint object.
     * @param c The starting tint color.
     */
	public Tint(String name, ColorRGBA c) {
		super(name);
		tintColor = c;
	}

    /**
     * Creates a new tint object with the given name and a default color
     * of 0,0,0,0.  This is basicly a call to this(name,new ColorRGBA(0,0,0,0))
     * @param name The name of the Tint.
     * @see #Tint(java.lang.String, com.jme.renderer.ColorRGBA)
     */
	public Tint(String name) {
        this(name,new ColorRGBA(0,0,0,0));
	}

    /**
     * Draws the tint.
     * @param r The renderer to draw it from.
     */
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

    /**
     * Returns the current tint color.
     * @return The current tint color.
     */
	public ColorRGBA getTintColor() {
		return tintColor;
	}

    /**
     * Sets the tint color to use.
     * @param c The new tint color.
     */
	public void setTintColor(ColorRGBA c) {
		tintColor = c;
	}
}