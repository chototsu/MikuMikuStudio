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

package com.jme.system;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import com.jme.util.LoggingSystem;

/**
 * <code>PropertiesIO</code> handles loading and saving a properties file that
 * defines the display settings. A property file is identified during creation
 * of the object. The properties file should have the following format:<br><br>
 * FREQ=60<br>
 * RENDERER=LWJGL<br>
 * WIDTH=1280<br>
 * HEIGHT=1024<br>
 * DEPTH=32<br>
 * FULLSCREEN=false<br>
 * <br>
 * 
 * @author Mark Powell
 * @version $Id: PropertiesIO.java,v 1.1.1.1 2003-10-29 10:56:48 Anakan Exp $
 */
public class PropertiesIO {
    /**
     * The default width, used if there is a problem with the properties file.
     */
    public static final int DEFAULT_WIDTH = 640;
    /**
     * The default height, used if there is a problem with the properties file.
     */
    public static final int DEFAULT_HEIGHT = 480;
    /**
     * The default depth, used if there is a problem with the properties file.
     */
    public static final int DEFAULT_DEPTH = 16;
    /**
     * The default frequency, used if there is a problem with the properties
     * file.
     */
    public static final int DEFAULT_FREQ = 60;
    /**
     * The default fullscreen flag, used if there is a problem with the 
     * properties file.
     */
    public static final boolean DEFAULT_FULLSCREEN = true;
    /**
     * The default renderer flag, used if there is a problem with the 
     * properties file.
     */
    public static final String DEFAULT_RENDERER = "LWJGL";

    //property object
    private Properties prop;
    //the file that contains our properties.
    private String filename;

    /**
     * Constructor creates the <code>PropertiesIO</code> object for use.
     * 
     * @param filename the properties file to use.
     * @throws MonkeyRuntimeException if the filename is null.
     */
    public PropertiesIO(String filename) {
        if (null == filename) {
            throw new JmeException("Must give a valid filename");
        }

        this.filename = filename;
        prop = new Properties();

        LoggingSystem.getLogger().log(
            Level.INFO,
            "PropertiesIO created");
    }

    /**
     * <code>load</code> attempts to load the properties file defined during
     * instantiation and put all properties in the table. If there is a
     * problem loading or reading the file, false is returned. If all goes
     * well, true is returned.
     * 
     * @return the success of the load, true indicated success and false 
     *      indicates failure.
     */
    public boolean load() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Could not load properties: " + e.toString());
            return false;
        }

        try {
            if (fin != null) {
                prop.load(fin);
                fin.close();
            }
        } catch (IOException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Could not load properties: " + e.toString());
            return false;
        }

        //confirm that the properties file has all the data we need.
        if (null == prop.getProperty("WIDTH")
            || null == prop.getProperty("HEIGHT")
            || null == prop.getProperty("DEPTH")
            || null == prop.getProperty("FULLSCREEN")) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Properties file not complete.");
            return false;
        }

        LoggingSystem.getLogger().log(
            Level.INFO,
            "Read properties");
        return true;
    }

    /**
     * <code>save</code> overwrites the properties file with the given
     * parameters. 
     *  
     * @param width the width of the resolution.
     * @param height the height of the resolution.
     * @param depth the bits per pixel.
     * @param freq the frequency of the monitor.
     * @param fullscreen use fullscreen or not.
     * @return true if save was successful, false otherwise.
     */
    public boolean save(int width, int height, int depth, int freq, 
            boolean fullscreen, String renderer) {
        
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(filename);

            prop.clear();

            prop.put("WIDTH", "" + width);
            prop.put("HEIGHT", "" + height);
            prop.put("DEPTH", "" + depth);
            prop.put("FREQ", "" + freq);
            prop.put("FULLSCREEN", "" + fullscreen);
            prop.put("RENDERER", renderer);

            prop.store(fout, "Properties");

            fout.close();
        } catch (IOException e) {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "Could not save properties: " + e.toString());
            return false;
        }
        LoggingSystem.getLogger().log(
            Level.INFO,
            "Saved properties");
        return true;
    }

    /**
     * <code>getWidth</code> returns the width as read from the properties 
     * file. If the properties file does not contain width or was not read
     * properly, the default width is returned.
     * 
     * @return the width determined by the properties file, or the default.
     */
    public int getWidth() {
        String w = prop.getProperty("WIDTH");
        if (null == w) {
            return DEFAULT_WIDTH;
        } else {
            return Integer.parseInt(w);
        }
    }

    /**
     * <code>getHeight</code> returns the height as read from the properties 
     * file. If the properties file does not contain height or was not read
     * properly, the default height is returned.
     * 
     * @return the height determined by the properties file, or the default.
     */
    public int getHeight() {
        String h = prop.getProperty("HEIGHT");
        if (null == h) {
            return DEFAULT_HEIGHT;
        } else {
            return Integer.parseInt(h);
        }
    }

    /**
     * <code>getDepth</code> returns the depth as read from the properties 
     * file. If the properties file does not contain depth or was not read
     * properly, the default depth is returned.
     * 
     * @return the depth determined by the properties file, or the default.
     */
    public int getDepth() {
        String d = prop.getProperty("DEPTH");
        if (null == d) {
            return DEFAULT_DEPTH;
        } else {
            return Integer.parseInt(d);
        }
    }
    
    /**
     * <code>getFreq</code> returns the frequency of the monitor as read from
     * the properties file. If the properties file does not contain frequency
     * or was not read properly the default frequency is returned.
     * 
     * @return the frequency determined by the properties file, or the default.
     */
    public int getFreq() {
        String f = prop.getProperty("FREQ");
        if(null == f) {
            return DEFAULT_FREQ;
        } else {
            return Integer.parseInt(f);
        }
    }

    /**
     * <code>getFullscreen</code> returns the fullscreen flag as read from the 
     * properties file. If the properties file does not contain the fullscreen 
     * flag or was not read properly, the default fullscreen flag is returned.
     * 
     * @return the fullscreen flag determined by the properties file, or the 
     *      default.
     */
    public boolean getFullscreen() {
        String f = prop.getProperty("FULLSCREEN");
        if(null == f) {
            return DEFAULT_FULLSCREEN;
        } else {
            return new Boolean(prop.getProperty("FULLSCREEN")).booleanValue();
        }
    }
    
    /**
     * 
     * <code>getRenderer</code> returns the requested rendering API, or the
     * default.
     * @return the rendering API or the default.
     */
    public String getRenderer() {
        String renderer = prop.getProperty("RENDERER");
        if(null == renderer) {
            return DEFAULT_RENDERER;
        } else {
            return renderer;
        }
    }
    
    /**
     * <code>get</code> takes an arbitrary string as a key and returns any
     * value associated with it, null if none. 
     * @param key the key to use for data retrieval.
     * @return the string associated with the key, null if none.
     */
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    /**
     * <code>set</code> adds a key/value pair to the properties list.
     * @param key the key of the pair.
     * @param value the value of the pair.
     */
    public void set(String key, String value) {
        prop.setProperty(key, value);
    }
}
