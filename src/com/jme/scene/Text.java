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

import com.jme.renderer.Renderer;
import com.jme.renderer.ColorRGBA;


/**
 *
 * <code>Text</code> allows text to be displayed on the screen.  The renderstate of this Geometry must be a valid
 * font texture.
 * @author Mark Powell
 * @version $Id: Text.java,v 1.11 2004-08-21 06:18:34 cep21 Exp $
 */
public class Text extends Geometry {
    private StringBuffer text;
    private ColorRGBA textColor = new ColorRGBA();

    /**
     * Creates a texture object that starts with the given text.
     *
     * @see com.jme.util.TextureManager
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param text The text to show.
     */
    public Text(String name,String text) {
        super(name);
        setForceView(true);
        this.text = new StringBuffer(text);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     *
     * <code>print</code> sets the text to be rendered on the next render
     * pass.
     * @param text the text to display.
     */
    public void print(String text) {
        this.text.replace(0, this.text.length(), text);
    }

    public void print(StringBuffer text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    /**
     *
     * <code>getText</code> retrieves the text string of this <code>Text</code>
     * object.
     * @return the text string of this object.
     */
    public StringBuffer getText() {
        return text;
    }

    /**
     * <code>draw</code> calls super to set the render state then calls the
     * renderer to display the text string.
     * @param r the renderer used to display the text.
     */
    public void draw(Renderer r) {
      if (!r.isProcessingQueue()) {
        if (r.checkAndAdd(this))
          return;
      }
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
     * Sets the color of the text.
     * @param color Color to set.
     */
    public void setTextColor(ColorRGBA color) {
      textColor.set(color);
    }

    /**
     * Returns the current text color.
     * @return Current text color.
     */
    public ColorRGBA getTextColor() {
      return textColor;
    }
}
