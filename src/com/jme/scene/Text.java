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

import com.jme.renderer.Renderer;


/**
 * 
 * <code>Text</code>
 * @author Mark Powell
 * @version $Id: Text.java,v 1.3 2004-02-20 20:17:49 mojomonkey Exp $
 */
public class Text extends Geometry {
    private StringBuffer text;

    /**
     * Constructor takes a path to the texture to use of the font base. This
     * image format must be compatible with <code>TextureManager</code>'s 
     * image types. 
     * 
     * @see jme.texture.TextureManager
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param texture the path to the image that defines the fonts.
     */
    public Text(String name,String text) {
        super(name);
        setForceView(true);
        this.text = new StringBuffer(text);
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
        super.draw(r);
        r.draw(this);
    }
    
}
