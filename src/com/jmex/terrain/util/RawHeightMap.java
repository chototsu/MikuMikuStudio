/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
package com.jmex.terrain.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import com.jme.math.FastMath;
import com.jme.system.JmeException;
import com.jme.util.LittleEndien;
import com.jme.util.LoggingSystem;

/**
 * <code>RawHeightMap</code> creates a height map from a RAW image file. The
 * greyscale image denotes height based on the value of the pixel for each
 * point. Where pure black the lowest point and pure white denotes the highest.
 * 
 * @author Mark Powell
 * @version $Id: RawHeightMap.java,v 1.7 2007-04-23 20:08:38 nca Exp $
 */
public class RawHeightMap extends AbstractHeightMap {

    /**
     * Format specification for 8 bit precision heightmaps
     */
    public static final int FORMAT_8BIT = 0;

    /**
     * Format specification for 16 bit little endian heightmaps
     */
    public static final int FORMAT_16BITLE = 1;

    /**
     * Format specification for 16 bit big endian heightmaps
     */
    public static final int FORMAT_16BITBE = 2;

    private String filename;
    private int format;
    private boolean swapxy;

    /**
     * Constructor creates a new <code>RawHeightMap</code> object and loads a
     * RAW image file to use as a height field. The greyscale image denotes the
     * height of the terrain, where dark is low point and bright is high point.
     * The values of the RAW correspond directly with the RAW values or 0 - 255.
     * 
     * @param filename
     *            the RAW file to use as the heightmap.
     * @param size
     *            the size of the RAW (must be square).
     * @throws JmeException
     *             if the filename is null or not RAW, and if the size is 0 or
     *             less.
     */
    public RawHeightMap(String filename, int size) {
        this(filename, size, FORMAT_8BIT, false);
    }

    public RawHeightMap(int heightData[]) {
        this.heightData = heightData;
        this.size = (int) FastMath.sqrt(heightData.length);
    }

    public RawHeightMap(String filename, int size, int format, boolean swapxy) {
        // varify that filename and size are valid.
        if (null == filename || size <= 0) {
            throw new JmeException("Must supply valid filename and "
                    + "size (> 0)");
        }

        this.filename = filename;
        this.size = size;
        this.format = format;
        this.swapxy = swapxy;
        load();
    }

    /**
     * <code>load</code> fills the height data array with the appropriate data
     * from the set RAW image. If the RAW image has not been set a JmeException
     * will be thrown.
     * 
     * @return true if the load is successfull, false otherwise.
     */
    @Override
    public boolean load() {
        // confirm data has been set. Redundant check...
        if (null == filename || size <= 0) {
            throw new JmeException("Must supply valid filename and "
                    + "size (> 0)");
        }

        // clean up
        if (null != heightData) {
            unloadHeightMap();
        }

        // initialize the height data attributes
        heightData = new int[size * size];

        // attempt to connect to the supplied file.
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filename);
            if (format == RawHeightMap.FORMAT_16BITLE) {
                LittleEndien dis = new LittleEndien(fis);
                int index;
                // read the raw file
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (swapxy) {
                            index = i + j * size;
                        } else {
                            index = (i * size) + j;
                        }
                        heightData[index] = dis.readUnsignedShort();
                    }
                }
                dis.close();
            } else {
                DataInputStream dis = new DataInputStream(fis);
                // read the raw file
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        int index;
                        if (swapxy) {
                            index = i + j * size;
                        } else {
                            index = (i * size) + j;
                        }
                        if (format == RawHeightMap.FORMAT_16BITBE) {
                            heightData[index] = dis.readUnsignedShort();
                        } else {
                            heightData[index] = dis.readUnsignedByte();
                        }
                    }
                }
                dis.close();
            }
            fis.close();
        } catch (FileNotFoundException e) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Heightmap file" + filename + " not found.");
            return false;
        } catch (IOException e1) {
            LoggingSystem.getLogger().log(Level.WARNING,
                    "Error reading data from " + filename);
            return false;
        }

        LoggingSystem.getLogger().log(Level.WARNING,
                "Successfully loaded " + filename);
        return true;
    }

    /**
     * <code>setFilename</code> sets the file to use for the RAW data. A call
     * to <code>load</code> is required to put the changes into effect.
     * 
     * @param filename
     *            the new file to use for the height data.
     * @throws JmeException
     *             if the file is null or not RAW.
     */
    public void setFilename(String filename) {
        if (null == filename) {
            throw new JmeException("Must supply valid filename");
        }

        if (null == filename || size <= 0) {
            throw new JmeException("Must supply valid filename and "
                    + "size (> 0)");
        }

        this.filename = filename;
    }
}
