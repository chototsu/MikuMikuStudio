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
package com.jme.image;

import java.nio.ByteBuffer;

/**
 * <code>Image</code> defines a data format for a graphical image. The image
 * is defined by a type, a height and width, and the image data. The type can
 * be any one of the following types: RGBA4444, RGB888, RGBA5551, RGBA8888. 
 * The width and height must be greater than 0. The data is contained in a 
 * byte buffer, and should be packed before creation of the image object.
 * @author Mark Powell
 * @version $Id: Image.java,v 1.1 2003-10-13 18:30:09 mojomonkey Exp $
 */
public class Image {
    /**
     * 16-bit RGBA with 4 bits for each component.
     */
    public static final int RGBA4444 = 0;
    /**
     * 24-bit RGB with 8 bits for each component.
     */
    public static final int RGB888 = 1;
    /**
     * 16-bit RGBA with 5 bits for color components and 1 bit for alpha.
     */
    public static final int RGBA5551 = 2;
    /**
     * 32-bit RGBA with 8 bits for each component.
     */
    public static final int RGBA8888 = 3;

    //image attributes
    private int type;
    private int width;
    private int height;
    private ByteBuffer data;
    
    /**
     * Constructor instantiates a new <code>Image</code> object. All values are
     * undefined.
     *
     */
    public Image() {
        
    }
    
    /**
     * Constructor instantiates a new <code>Image</code> object. The attributes
     * of the image are defined during construction.
     * @param type the type of image format.
     * @param width the width of the image.
     * @param height the height of the image.
     * @param data the image data.
     */
    public Image(int type, int width, int height, ByteBuffer data) {
        if(type < 0 || type > 3) {
            type = 0;
        }
        this.type = type;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    /**
     * <code>setData</code> sets the data that makes up the image. This data
     * is packed into a single <code>ByteBuffer</code>.
     * @param data the data that contains the image information.
     */ 
    public void setData(ByteBuffer data) {
        this.data = data;
    }

    /**
     * <code>setHeight</code> sets the height value of the image. It is 
     * typically a good idea to try to keep this as a multiple of 2.
     * @param height the height of the image.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * <code>setWidth</code> sets the width value of the image. It is 
     * typically a good idea to try to keep this as a multiple of 2.
     * @param width the width of the image.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 
     * <code>setType</code> sets the image format for this image. If an
     * invalid value is passed, the type defaults to RGBA4444.
     * @param type the image format.
     */
    public void setType(int type) {
        if(type < 0 || type > 3) {
            type = 0;
        }
        this.type = type;
    }
    
    /**
     * 
     * <code>getType</code> returns the image format for this image.
     * @return the image format.
     */
    public int getType() {
        return type;
    }

    /**
     * 
     * <code>getWidth</code> returns the width of this image.
     * @return the width of this image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * 
     * <code>getHeight</code> returns the height of this image.
     * @return the height of this image.
     */
    public int getHeight() {
        return height;
    }

    /**
     * 
     * <code>getData</code> returns the data for this image. If the data
     * is undefined, null will be returned.
     * @return the data for this image.
     */
    public ByteBuffer getData() {
        return data;
    }
}
