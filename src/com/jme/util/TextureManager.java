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

package com.jme.util;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;

import com.jme.image.BitmapHeader;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;

/**
 * 
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 * @author Mark Powell
 * @version $Id: TextureManager.java,v 1.1 2003-10-13 18:30:09 mojomonkey Exp $
 */
public class TextureManager {
   
    // For TGA loading
    private static final int NO_TRANSPARENCY = 255;
    private static final int FULL_TRANSPARENCY = 0;

    private static short idLength;
    private static short colorMapType;
    private static short imageType;
    private static int cMapStart;
    private static int cMapLength;
    private static short cMapDepth;
    private static int xOffset;
    private static int yOffset;
    private static int width;
    private static int height;
    private static short pixelDepth;
    private static short imageDescriptor;
    private static DirectColorModel cm;
    private static int[] pixels;

    
    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used
     * to define the filtering of the texture. Whether the texture is to be
     * mipmapped or not is denoted by the isMipmapped boolean flag. If there
     * is an error loading the file, null is returned.
     * 
     * @param file the filename of the texture image.
     * @param minFilter the filter for the near values.
     * @param magFilter the filter for the far values.
     * @param isMipmapped determines if we will load the texture mipmapped
     *      or not. True load the texture mipmapped, false do not.
     * @param flipped true flips the bits of the image, false does not. True
     *      by default.
     * 
     * @return the loaded texture. If there is a problem
     *      loading the texture, null is returned.
     */
    public static com.jme.image.Texture loadTexture(
        String file,
        int minFilter,
        int magFilter,
        boolean isMipMapped) {
        return loadTexture(file, minFilter, magFilter, isMipMapped, true);
    }

    /**
     * <code>loadTexture</code> loads a new texture defined by the parameter
     * string. Filter parameters are used
     * to define the filtering of the texture. Whether the texture is to be
     * mipmapped or not is denoted by the isMipmapped boolean flag. If there
     * is an error loading the file, null is returned.
     * 
     * @param file the filename of the texture image.
     * @param minFilter the filter for the near values.
     * @param magFilter the filter for the far values.
     * @param isMipmapped determines if we will load the texture mipmapped
     *      or not. True load the texture mipmapped, false do not.
     * 
     * @return the loaded texture. If there is a problem
     *      loading the texture, null is returned.
     */
    public static com.jme.image.Texture loadTexture(
        String file,
        int minFilter,
        int magFilter,
        boolean isMipmapped,
        boolean flipped) {

        java.awt.Image image = null;

        if (".TGA".equalsIgnoreCase(file.substring(file.indexOf('.')))) {
            //Load the TGA file
            image = loadTGAImage(file);
        } else if (
            ".BMP".equalsIgnoreCase(file.substring(file.indexOf('.')))) {
            image = loadBMPImage(file);
        } else {
            //Load the new image.
            image = (new javax.swing.ImageIcon(file)).getImage();
        }

        if (null == image) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Could not load" + file);
            return null;
        }

        com.jme.image.Image imageData =
            loadImage(file, image, flipped);
        Texture texture = new Texture();
        texture.setApply(Texture.AM_MODULATE);
        texture.setBlendColor(new ColorRGBA(1, 1, 1, 1));
        texture.setCorrection(Texture.CM_PERSPECTIVE);
        texture.setFilter(magFilter);
        texture.setImage(imageData);
        texture.setMipmapState(minFilter);
        texture.setWrap(Texture.WM_CLAMP_S_CLAMP_T);
        return texture;
    }

    /**
     * 
     * <code>loadImage</code> sets the image data
     * 
     * @param file The name of the texture (filename usually).
     * @param image The image data.
     * @param minFilter minimum filter
     * @param magFilter maximum filter
     * @param isMipmapped whether the texture should be mipmapped or not.
     * @return the loaded image.
     */
    public static com.jme.image.Image loadImage(
        String file,
        java.awt.Image image,
        boolean flipImage) {
        //      Obtain the image data.
        BufferedImage tex = null;
        try {
            tex =
                new BufferedImage(
                    image.getWidth(null),
                    image.getHeight(null),
                    BufferedImage.TYPE_3BYTE_BGR);
        } catch (IllegalArgumentException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Could not load image file " + file);
            return null;
        }
        Graphics2D g = (Graphics2D) tex.getGraphics();
        g.drawImage(image, null, null);
        g.dispose();

        if (flipImage) {
            //Flip the image
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -image.getHeight(null));
            AffineTransformOp op =
                new AffineTransformOp(
                    tx,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            tex = op.filter(tex, null);
        }

        //Get a pointer to the image memory
        ByteBuffer scratch =
            ByteBuffer.allocateDirect(4 * tex.getWidth() * tex.getHeight());
        byte data[] =
            (byte[]) tex.getRaster().getDataElements(
                0,
                0,
                tex.getWidth(),
                tex.getHeight(),
                null);
        scratch.clear();
        scratch.put(data);
        scratch.rewind();

        com.jme.image.Image textureImage = new com.jme.image.Image();
        textureImage.setType(com.jme.image.Image.RGB888);
        textureImage.setWidth(tex.getWidth());
        textureImage.setHeight(tex.getHeight());
        textureImage.setData(scratch);
        return textureImage;

    }

    /**
    * <code>loadBMPImage</code> because bitmap is not directly supported by
    * Java, we must load it manually. The requires opening a stream to the
    * file and reading in each byte. After the image data is read, it is
    * used to create a new <code>Image</code> object. This object is
    * returned to be used for normal use.
    * 
    * @param file the name of the bitmap file.
    * 
    * @return <code>Image</code> object that contains the bitmap information.
    */
    private static java.awt.Image loadBMPImage(String file) {

        try {
            FileInputStream fs = new FileInputStream(file);
            BitmapHeader bh = new BitmapHeader();
            bh.read(fs);

            if (bh.bitcount == 24)
                return (bh.readMap24(fs, bh));

            if (bh.bitcount == 32)
                return (bh.readMap32(fs, bh));

            if (bh.bitcount == 8)
                return (bh.readMap8(fs, bh));

            fs.close();
        } catch (IOException e) {
            System.err.println("Error while loading " + file);
            System.exit(1);
        }

        return null;
    }

    /**
     * <code>loadTGAImage</code> because targa is not directly supported by
     * Java, we must load it manually. The requires opening a stream to the
     * file and reading in each byte. After the image data is read, it is
     * used to create a new <code>Image</code> object. This object is
     * returned to be used for normal use.
     * 
     * @param file the name of the targa file.
     * 
     * @return <code>Image</code> object that contains the targa information.
     */
    private static java.awt.Image loadTGAImage(String file) {
        try {
            int red = 0;
            int green = 0;
            int blue = 0;
            int srcLine = 0;
            int alpha = FULL_TRANSPARENCY;

            //open a stream to the file
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis, 8192);
            DataInputStream dis = new DataInputStream(bis);

            //Read the TGA header
            idLength = (short) dis.read();
            colorMapType = (short) dis.read();
            imageType = (short) dis.read();
            cMapStart = flipEndian(dis.readShort());
            cMapLength = flipEndian(dis.readShort());
            cMapDepth = (short) dis.read();
            xOffset = flipEndian(dis.readShort());
            yOffset = flipEndian(dis.readShort());
            width = flipEndian(dis.readShort());
            height = flipEndian(dis.readShort());
            pixelDepth = (short) dis.read();

            if (pixelDepth == 24) {
                cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
            } else if (pixelDepth == 32) {
                cm =
                    new DirectColorModel(
                        32,
                        0xFF000000,
                        0xFF0000,
                        0xFF00,
                        0xFF);
            }

            imageDescriptor = (short) dis.read();

            //Skip image ID
            if (idLength > 0) {
                bis.skip(idLength);
            }

            //create the buffer for the image data.
            pixels = new int[width * height];

            //read the pixel data.
            for (int i = (height - 1); i >= 0; i--) {
                srcLine = i * width;

                for (int j = 0; j < width; j++) {
                    blue = bis.read() & 0xFF;
                    green = bis.read() & 0xFF;
                    red = bis.read() & 0xFF;

                    if (pixelDepth == 32) {
                        alpha = bis.read() & 0xFF;
                        pixels[srcLine + j] =
                            alpha << 24 | red << 16 | green << 8 | blue;
                    } else {
                        pixels[srcLine + j] = red << 16 | green << 8 | blue;
                    }
                }
            }

            //Close the file, we are done.
            fis.close();
        } catch (IOException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Unable to load " + file);
        }

        //create the Image object and return it.
        return Toolkit.getDefaultToolkit().createImage(
            new MemoryImageSource(width, height, cm, pixels, 0, width));
    }

    /**
     * <code>flipEndian</code> is used to flip the endian bit of the header file.
     * @param signedShort the bit to flip.
     * 
     * @return the flipped bit.
     */
    private static short flipEndian(short signedShort) {
        int input = signedShort & 0xFFFF;
        return (short) (input << 8 | (input & 0xFF00) >>> 8);
    }

}