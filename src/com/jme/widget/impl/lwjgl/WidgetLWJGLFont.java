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
package com.jme.widget.impl.lwjgl;

import org.lwjgl.opengl.GL;

import com.jme.renderer.ColorRGBA;
import com.jme.widget.font.WidgetAbstractFont;
import com.jme.widget.font.WidgetFontChar;

/**
 * @author pattogo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetLWJGLFont extends WidgetAbstractFont {

    public WidgetLWJGLFont(String name) {
        super(name);
    }

    public void renderString(String text, float x, float y, float scalar, ColorRGBA topColor, ColorRGBA bottomColor) {
        int i;
        int c;
        WidgetFontChar fontChar;
        float width, height;

        //Begin rendering quads
        GL.glBegin(GL.GL_QUADS);

        //Loop through characters
        for (i = 0; i < text.length(); i++) {
            //Make sure character is in range
            c = text.charAt(i);
            if (c < header.getStartChar() || c > header.getEndChar())
                continue;

            //Get pointer to glFont character
            fontChar = header.getChar(c - header.getStartChar());

            //Get width and height
            width = (fontChar.getDx() * header.getTexWidth()) * scalar;
            height = (fontChar.getDy() * header.getTexHeight()) * scalar;

            //Specify vertices and texture coordinates
            GL.glColor3f(topColor.r, topColor.g, topColor.b);

            GL.glTexCoord2f(fontChar.getTx1(), fontChar.getTy1());
            GL.glVertex2f(x, y);
            GL.glTexCoord2f(fontChar.getTx1(), fontChar.getTy2());
            GL.glVertex2f(x, y - height);

            GL.glColor3f(bottomColor.r, bottomColor.g, bottomColor.b);
            GL.glTexCoord2f(fontChar.getTx2(), fontChar.getTy2());
            GL.glVertex2f(x + width, y - height);
            GL.glTexCoord2f(fontChar.getTx2(), fontChar.getTy1());
            GL.glVertex2f(x + width, y);

            //Move to next character
            x += width;
        }

        //Stop rendering quads
        GL.glEnd();
    }

}
