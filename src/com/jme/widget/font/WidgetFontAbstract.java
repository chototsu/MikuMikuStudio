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
package com.jme.widget.font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetFontAbstract implements WidgetFont {
    private static String FONT_DIRECTORY = "data/Font/";
    private static String FONT_EXT = "glf";

    protected WidgetFontHeader header;
    protected Texture texture;

    private String name;

    public WidgetFontAbstract(String name) {
        this.name = name;

        String filename = FONT_DIRECTORY + name + "." + FONT_EXT;

        create(filename);
    }

    public void create(String filename) {
        try {
            int numChars, numTexBytes, cnt;
            WidgetFontChar fontChar;

            header = new WidgetFontHeader();
            texture = new Texture();

            File data = new File(filename);

            ReadableByteChannel channel = new FileInputStream(data).getChannel();
            ByteBuffer buf = ByteBuffer.allocateDirect((int) data.length());

            buf.order(ByteOrder.LITTLE_ENDIAN);

            buf.rewind();
            channel.read(buf);
            channel.close();

            buf.rewind();

            header.setTex(buf.getInt());
            header.setTexWidth(buf.getInt());
            header.setTexHeight(buf.getInt());

            header.setStartChar(buf.getInt());
            header.setEndChar(buf.getInt());

            //header.chars
            buf.getInt();

            float newHeight = 0;
            float height = 0;

            numChars = header.getEndChar() - header.getStartChar() + 1;
            for (cnt = 0; cnt < numChars; cnt++) {
                fontChar = new WidgetFontChar();

                fontChar.setDx(buf.getFloat());
                fontChar.setDy(buf.getFloat());

                fontChar.setTx1(buf.getFloat());
                fontChar.setTy1(buf.getFloat());

                fontChar.setTx2(buf.getFloat());
                fontChar.setTy2(buf.getFloat());

                header.addChar(fontChar);

                newHeight = fontChar.getDy() * header.getTexHeight();
                if (newHeight > height)
                    height = newHeight;

            }

            Image textureImage = new Image();
            textureImage.setType(Image.RA88);
            textureImage.setWidth(header.getTexWidth());
            textureImage.setHeight(header.getTexHeight());

            textureImage.setData(buf);

            texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
            texture.setFilter(Texture.MM_NEAREST);
            texture.setImage(textureImage);
            texture.setMipmapState(Texture.MM_NONE);
            texture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Calculate size of a string
    public Vector2f getStringSize(String text) {
        Vector2f ret = new Vector2f();
        int i;
        char c;
        WidgetFontChar fontChar;
        float width = 0, height = 0; //, newHeight = 0;

        fontChar = header.getChar(header.getStartChar());
        ret.y = fontChar.getDy() * header.getTexHeight();

        //Calculate width of string
        width = 0;
        for (i = 0; i < text.length(); i++) {
            //Make sure character is in range
            c = text.charAt(i);
            if (c < header.getStartChar() || c > header.getEndChar())
                continue;

            //Get pointer to glFont character
            fontChar = header.getChar(c - header.getStartChar());

            //Get width and height
            width += fontChar.getDx() * header.getTexWidth();
        }

        ret.x = width;
        return ret;
    }

    public Texture getTexture() {
        return texture;
    }

    public String getName() {
        return name;
    }

    public abstract void renderString(
        String text,
        float x,
        float y,
        float scalar,
        ColorRGBA topColor,
        ColorRGBA bottomColor);

    public String toString() {
        return "[" + name + "]";
    }
}
