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

package com.jme.terrain.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>RawHeightMap</code> creates a height map from a RAW image file. The
 * greyscale image denotes height based on the value of the pixel for each
 * point. Where pure black denotes 0 and pure white denotes 255.
 * 
 * @author Mark Powell
 * @version $Id: RawHeightMap.java,v 1.2 2004-04-13 02:08:26 mojomonkey Exp $
 */
public class RawHeightMap extends AbstractHeightMap {
    private String filename;

    /**
     * Constructor creates a new <code>RawHeightMap</code> object and
     * loads a RAW image file to use as a height
     * field. The greyscale image denotes the height of the terrain, where
     * dark is low point and bright is high point. The values of the RAW
     * correspond directly with the RAW values or 0 - 255. 
     * 
     * @param filename the RAW file to use as the heightmap.
     * @param size the size of the RAW (must be square).
     * 
     * @throws MonkeyRuntimeException if the filename is null or not RAW, and
     *      if the size is 0 or less.
     */
    public RawHeightMap(String filename, int size) {
        //varify that filename and size are valid.
        if (null == filename || size <= 0) {
            throw new JmeException(
                "Must supply valid filename and " + "size (> 0)");
        }

        //make sure it's a raw file.
        if (!filename.endsWith(".raw")) {
            throw new JmeException("Height data must be RAW format");
        }

        this.filename = filename;
        this.size = size;
        load();
    }

    /**
     * <code>load</code> fills the height data array with the appropriate
     * data from the set RAW image. If the RAW image has not been set a 
     * JmeException will be thrown.
     * 
     * @return true if the load is successfull, false otherwise.
     */
    public boolean load() {
        //confirm data has been set. Redundant check...
        if (null == filename || size <= 0) {
            throw new JmeException(
                "Must supply valid filename and " + "size (> 0)");
        }
        
        //clean up
        if (null != heightData) {
            unloadHeightMap();
        }

        //initialize the height data attributes
        heightData = new int[size*size];

        //attempt to connect to the supplied file.
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filename);
            
            DataInputStream dis = new DataInputStream(fis);
            if(heightData.length != dis.available()) {
                LoggingSystem.getLogger().log(Level.WARNING, "Incorrect map size. Aborting raw load.");
            }
            //read in each byte from the raw file.
            for (int i = 0; i < size; i++) {
                for(int j = 0; j < size; j++) {
                    heightData[i + (j*size)] = dis.readUnsignedByte();
                }
            }

            dis.close();
            fis.close();

        } catch (FileNotFoundException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Heightmap file" + filename + " not found.");
            return false;
        } catch (IOException e1) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Error reading data from " + filename);
            return false;
        }

        LoggingSystem.getLogger().log(
            Level.WARNING,
            "Successfully loaded " + filename);
        return true;
    }

    /**
     * <code>setFilename</code> sets the file to use for the RAW data. A
     * call to <code>load</code> is required to put the changes into effect.
     *
     * @param filename the new file to use for the height data.
     * 
     * @throws JmeException if the file is null or not RAW.
     */
    public void setFilename(String filename) {
        if (null == filename) {
            throw new JmeException("Must supply valid filename");
        }

        if (null == filename || size <= 0) {
            throw new JmeException(
                "Must supply valid filename and " + "size (> 0)");
        }

        this.filename = filename;
    }
}
