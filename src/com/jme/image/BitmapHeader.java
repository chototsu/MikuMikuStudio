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

import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

/**
 * <code>BitmapHeader</code> defines header information about a bitmap (BMP) image
 * file format.
 * @author Mark Powell
 * @version $Id: BitmapHeader.java,v 1.4 2004-02-22 20:43:46 mojomonkey Exp $
 */
public class BitmapHeader {
    public int size;
    public int bisize;
    public int width;
    public int height;
    public int planes;
    public int bitcount;
    public int compression;
    public int sizeimage;
    public int xpm;
    public int ypm;
    public int clrused;
    public int clrimp;

    public java.awt.Image readMap32(byte[] data, BitmapHeader bh) {
        java.awt.Image image;
        int xwidth = bh.sizeimage / bh.height;
        int ndata[] = new int[bh.height * bh.width];
        byte brgb[] = new byte[bh.width * 4 * bh.height];
        
        for(int i = 0; i < bh.width * 4 * bh.height; i++) {
                    brgb[i] = data[i + 54];
                }
        int nindex = 0;

        for (int j = 0; j < bh.height; j++) {
            for (int i = 0; i < bh.width; i++) {
                ndata[bh.width * (bh.height - j - 1) + i] =
                    constructInt3(brgb, nindex);
                System.out.println(ndata[bh.width * (bh.height - j - 1) + i]);
                nindex += 4;
            }
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(bh.width, bh.height, ndata, 0, bh.width));
        return (image);
    }

    public java.awt.Image readMap24(byte[] data, BitmapHeader bh)
        throws IOException {
        java.awt.Image image;
        int npad = (bh.sizeimage / bh.height) - bh.width * 3;
        int ndata[] = new int[bh.height * bh.width];
        byte brgb[] = new byte[(bh.width + npad) * 3 * bh.height];
        for(int i = 0; i < (bh.width + npad) * 3 * bh.height; i++) {
            brgb[i] = data[i + 54];
        }
        
        int nindex = 0;

        for (int j = 0; j < bh.height; j++) {
            for (int i = 0; i < bh.width; i++) {
                ndata[bh.width * (bh.height - j - 1) + i] =
                    constructInt3(brgb, nindex);
                nindex += 3;
            }
            nindex += npad;
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(bh.width, bh.height, ndata, 0, bh.width));
        return image;
    }

    public java.awt.Image readMap8(byte[] data, BitmapHeader bh) {
         java.awt.Image image;
        int nNumColors = 0;

        if (bh.clrused > 0) {
            nNumColors = bh.clrused;
        } else {
            nNumColors = (1 & 0xff) << bh.bitcount;
        }

        if (bh.sizeimage == 0) {
            bh.sizeimage = ((((bh.width * bh.bitcount) + 31) & ~31) >> 3);
            bh.sizeimage *= bh.height;
        }

        int npalette[] = new int[nNumColors];
        byte bpalette[] = new byte[nNumColors * 4];
        System.out.println(bpalette.length);
        
        for(int i = 0; i < nNumColors * 4; i++) {
            bpalette[i] = data[i + 54];
            System.out.println(bpalette[i]);
        }
        
        int nindex8 = 0;

        for (int n = 0; n < nNumColors; n++) {
            npalette[n] = constructInt3(bpalette, nindex8);
            System.out.println(npalette[n]);
            nindex8 += 4;
        }

        int npad8 = (bh.sizeimage / bh.height) - bh.width;
        int ndata8[] = new int[bh.width * bh.height];
        byte bdata[] = new byte[(bh.width + npad8) * bh.height];
        for(int i = 0; i < bdata.length; i++) {
            bdata[i] = data[i + bpalette.length + 54];
        }
        nindex8 = 0;

        for (int j8 = 0; j8 < bh.height; j8++) {
            for (int i8 = 0; i8 < bh.width; i8++) {
                ndata8[bh.width * (bh.height - j8 - 1) + i8] =
                    npalette[(bdata[nindex8] & 0xff)];
                nindex8++;
            }

            nindex8 += npad8;
        }

        image =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(
                    bh.width,
                    bh.height,
                    ndata8,
                    0,
                    bh.width));

        return image;
    }

    /* Builds an int from a byte array - convert little to big endian.
         */

    private int constructInt(byte[] in, int offset) {
        int ret = (in[offset + 3] & 0xff);
        ret = (ret << 8) | (in[offset + 2] & 0xff);
        ret = (ret << 8) | (in[offset + 1] & 0xff);
        ret = (ret << 8) | (in[offset + 0] & 0xff);
        return (ret);
    }

    /* Builds an int from a byte array - convert little to big endian 
     * set high order bytes to 0xfff.
     */

    private int constructInt3(byte[] in, int offset) {
        int ret = 0xff;
        ret = (ret << 8) | (in[offset + 2] & 0xff);
        ret = (ret << 8) | (in[offset + 1] & 0xff);
        ret = (ret << 8) | (in[offset + 0] & 0xff);
        return (ret);
    }

    /* Builds an int from a byte array - convert little to big endian.
     */

    private long constructLong(byte[] in, int offset) {
        long ret = ((long) in[offset + 7] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 6] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 5] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 4] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 3] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 2] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 1] & 0xff);
        ret |= (ret << 8) | ((long) in[offset + 0] & 0xff);
        return (ret);
    }

    /* Builds an double from a byte array - convert little to big endian.
     */

    private double constructDouble(byte[] in, int offset) {
        long ret = constructLong(in, offset);
        return (Double.longBitsToDouble(ret));
    }

    /* Builds an short from a byte array - convert little to big endian.
     */

    private short constructShort(byte[] in, int offset) {
        short ret = (short) (in[offset + 1] & 0xff);
        ret = (short) ((ret << 8) | (short) (in[offset + 0] & 0xff));
        return (ret);
    }

    public final void read(byte[] data){
        final int bflen = 14;
        byte bf[] = new byte[bflen];
        for(int i = 0; i < bf.length; i++) {
            bf[i] = data[i];
        }
        final int bilen = 40;
        byte bi[] = new byte[bilen];
        for(int i = 0; i < bi.length; i++) {
            bi[i] = data[i + 14];
        }

        size = constructInt(bf, 2);
        bisize = constructInt(bi, 2);
        width = constructInt(bi, 4);
        height = constructInt(bi, 8);
        planes = constructShort(bi, 12);
        bitcount = constructShort(bi, 14);
        compression = constructInt(bi, 16);
        sizeimage = constructInt(bi, 20);
        xpm = constructInt(bi, 24);
        ypm = constructInt(bi, 28);
        clrused = constructInt(bi, 32);
        clrimp = constructInt(bi, 36);
        
    }
}
