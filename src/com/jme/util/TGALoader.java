/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.util;

import java.awt.image.DirectColorModel;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * <code>TextureManager</code> provides static methods for building a
 * <code>Texture</code> object. Typically, the information supplied is the
 * filename and the texture properties.
 *
 * @author Mark Powell
 * @version $Id: TGALoader.java,v 1.2 2005-09-15 17:12:58 renanse Exp $
 */
public final class TGALoader {

  // For TGA loading
  private static short colorMapType;
  private static short imageType;
  private static int cMapStart;
  private static int cMapLength;
  private static short cMapDepth;
  private static int xOffset;
  private static int yOffset;
  private static short imageDescriptor;
  private static DirectColorModel cm;
  private static int[] pixels;

  private static short idLength;
  private static int width;
  private static int height;
  private static short pixelDepth;

  private TGALoader() {
  }

  /**
   * <code>flipEndian</code> is used to flip the endian bit of the header
   * file.
   *
   * @param signedShort
   *            the bit to flip.
   *
   * @return the flipped bit.
   */
  private static short flipEndian(short signedShort) {
    int input = signedShort & 0xFFFF;
    return (short) (input << 8 | (input & 0xFF00) >>> 8);
  }

  /**
   * <code>loadImage</code> is a manual image loader which is entirely
   * independent of AWT.
   *
   * OUT: RGB8888 or RGBA8888 jme.image.Image object
   *
   * @param fis
   *            InputStream of an uncompressed 24b RGB or 32b RGBA TGA
   *
   * @return <code>com.jme.image.Image</code> object that contains the
   *         image, either as a RGB888 or RGBA8888
   */
  public static com.jme.image.Image loadImage(InputStream fis) throws
      IOException {
    byte red = 0;
    byte green = 0;
    byte blue = 0;
    byte alpha = 0;
    //open a stream to the file
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
    imageDescriptor = (short) dis.read();
    //Skip image ID
    if (idLength > 0)
      bis.skip(idLength);
      // Allocate image data array
    byte[] rawData = null;
    if (pixelDepth == 32)
      rawData = new byte[width * height * 4];
    else
      rawData = new byte[width * height * 3];
    int rawDataIndex = 0;
    // Faster than doing a 24-or-32 check on each individual pixel,
    // just make a seperate loop for each.
    if (pixelDepth == 24)
      for (int i = 0; i <= (height - 1); i++) {
        for (int j = 0; j < width; j++) {
          blue = dis.readByte();
          green = dis.readByte();
          red = dis.readByte();
          rawData[rawDataIndex++] = (byte) red;
          rawData[rawDataIndex++] = (byte) green;
          rawData[rawDataIndex++] = (byte) blue;
        }
      }
    else if (pixelDepth == 32)
      for (int i = 0; i <= (height - 1); i++) {
        for (int j = 0; j < width; j++) {
          blue = dis.readByte();
          green = dis.readByte();
          red = dis.readByte();
          alpha = dis.readByte();
          rawData[rawDataIndex++] = (byte) red;
          rawData[rawDataIndex++] = (byte) green;
          rawData[rawDataIndex++] = (byte) blue;
          rawData[rawDataIndex++] = (byte) alpha;
        }
      }
    fis.close();
    //Get a pointer to the image memory
    ByteBuffer scratch = ByteBuffer.allocateDirect(rawData.length);
    scratch.clear();
    scratch.put(rawData);
    scratch.rewind();
    // Create the jme.image.Image object
    com.jme.image.Image textureImage = new com.jme.image.Image();
    if (pixelDepth == 32)
      textureImage.setType(com.jme.image.Image.RGBA8888);
    else
      textureImage.setType(com.jme.image.Image.RGB888);
    textureImage.setWidth(width);
    textureImage.setHeight(height);
    textureImage.setData(scratch);
    return textureImage;
  }

}
