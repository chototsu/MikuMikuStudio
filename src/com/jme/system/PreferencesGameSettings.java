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
package com.jme.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * <code>PreferencesGameSettings</code> uses the Preferences system in Java
 * and implements the <code>GameSettings</code> interface.
 * 
 * @author Matthew D. Hicks
 * 
 * @see GameSettings
 */
public class PreferencesGameSettings implements GameSettings {
    private static final Logger logger = Logger
            .getLogger(PreferencesGameSettings.class.getName());
    
    private static final String DEFAULT_RENDERER = PropertiesIO.DEFAULT_RENDERER;
    private static final int DEFAULT_WIDTH = PropertiesIO.DEFAULT_WIDTH;
    private static final int DEFAULT_HEIGHT = PropertiesIO.DEFAULT_HEIGHT;
    private static final int DEFAULT_DEPTH = PropertiesIO.DEFAULT_DEPTH;
    private static final int DEFAULT_FREQUENCY = PropertiesIO.DEFAULT_FREQ;
    private static final boolean DEFAULT_VERTICAL_SYNC = true;
    private static final boolean DEFAULT_FULLSCREEN = false; // PropertiesIO.DEFAULT_FULLSCREEN;
    private static final int DEFAULT_DEPTH_BITS = 8;
    private static final int DEFAULT_ALPHA_BITS = 0;
    private static final int DEFAULT_STENCIL_BITS = 0;
    private static final int DEFAULT_SAMPLES = 0;
    private static final boolean DEFAULT_MUSIC = true;
    private static final boolean DEFAULT_SFX = true;
    private static final int DEFAULT_FRAMERATE = -1;

    private Preferences preferences;

    public PreferencesGameSettings(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getRenderer() {
        return preferences.get("GameRenderer", DEFAULT_RENDERER);
    }

    public void setRenderer(String renderer) {
        preferences.put("GameRenderer", renderer);
    }

    public int getWidth() {
        return preferences.getInt("GameWidth", DEFAULT_WIDTH);
    }

    public void setWidth(int width) {
        preferences.putInt("GameWidth", width);
    }

    public int getHeight() {
        return preferences.getInt("GameHeight", DEFAULT_HEIGHT);
    }

    public void setHeight(int height) {
        preferences.putInt("GameHeight", height);
    }

    public int getDepth() {
        return preferences.getInt("GameDepth", DEFAULT_DEPTH);
    }

    public void setDepth(int depth) {
        preferences.putInt("GameDepth", depth);
    }

    public int getFrequency() {
        return preferences.getInt("GameFrequency", DEFAULT_FREQUENCY);
    }

    public void setFrequency(int frequency) {
        preferences.putInt("GameFrequency", frequency);
    }
    
    public boolean isVerticalSync() {
        return preferences.getBoolean("GameVerticalSync", DEFAULT_VERTICAL_SYNC);
    }
    
    public void setVerticalSync(boolean vsync) {
        preferences.putBoolean("GameVerticalSync", vsync);
    }

    public boolean isFullscreen() {
        return preferences.getBoolean("GameFullscreen", DEFAULT_FULLSCREEN);
    }

    public void setFullscreen(boolean fullscreen) {
        preferences.putBoolean("GameFullscreen", fullscreen);
    }

    public int getDepthBits() {
        return preferences.getInt("GameDepthBits", DEFAULT_DEPTH_BITS);
    }

    public void setDepthBits(int depthBits) {
        preferences.putInt("GameDepthBits", depthBits);
    }

    public int getAlphaBits() {
        return preferences.getInt("GameAlphaBits", DEFAULT_ALPHA_BITS);
    }

    public void setAlphaBits(int alphaBits) {
        preferences.putInt("GameAlphaBits", alphaBits);
    }

    public int getStencilBits() {
        return preferences.getInt("GameStencilBits", DEFAULT_STENCIL_BITS);
    }

    public void setStencilBits(int stencilBits) {
        preferences.putInt("GameStencilBits", stencilBits);
    }

    public int getSamples() {
        return preferences.getInt("GameSamples", DEFAULT_SAMPLES);
    }

    public void setSamples(int samples) {
        preferences.putInt("GameSamples", samples);
    }

    public boolean isMusic() {
        return preferences.getBoolean("GameMusic", DEFAULT_MUSIC);
    }

    public void setMusic(boolean musicEnabled) {
        preferences.putBoolean("GameMusic", musicEnabled);
    }

    public boolean isSFX() {
        return preferences.getBoolean("GameSFX", DEFAULT_SFX);
    }

    public void setSFX(boolean sfxEnabled) {
        preferences.putBoolean("GameSFX", sfxEnabled);
    }

    public int getFramerate() {
        return preferences.getInt("GameFramerate", DEFAULT_FRAMERATE);
    }

    public void setFramerate(int framerate) {
        preferences.putInt("GameFramerate", framerate);
    }
    
    public void clear() throws Exception {
        preferences.clear();
    }

    public String get(String name, String defaultValue) {
        return preferences.get(name, defaultValue);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return preferences.getBoolean(name, defaultValue);
    }

    public double getDouble(String name, double defaultValue) {
        return preferences.getDouble(name, defaultValue);
    }

    public float getFloat(String name, float defaultValue) {
        return preferences.getFloat(name, defaultValue);
    }

    public int getInt(String name, int defaultValue) {
        return preferences.getInt(name, defaultValue);
    }

    public long getLong(String name, long defaultValue) {
        return preferences.getLong(name, defaultValue);
    }

    public byte[] getByteArray(String name, byte[] defaultValue) {
    	return preferences.getByteArray(name, defaultValue);
    }
    
    public Object getObject(String name, Object defaultValue) {
    	try {
	    	byte[] bytes = preferences.getByteArray(name, null);
	    	if (bytes == null) return defaultValue;
	    	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	    	ObjectInputStream ois = new ObjectInputStream(bais);
	    	return ois.readObject();
    	} catch(Exception exc) {
    		logger.logp(Level.SEVERE, this.getClass().toString(),
                    "getObject(String, Object)", "Exception", exc);
    	}
    	return null;
    }
    
    public void set(String name, String value) {
        if (value == null) {
    		remove(name);
    	} else {
    		preferences.put(name, value);
    	}
    }

    public void setBoolean(String name, boolean value) {
        preferences.putBoolean(name, value);
    }

    public void setDouble(String name, double value) {
        preferences.putDouble(name, value);
    }

    public void setFloat(String name, float value) {
        preferences.putFloat(name, value);
    }

    public void setInt(String name, int value) {
        preferences.putInt(name, value);
    }

    public void setLong(String name, long value) {
        preferences.putLong(name, value);
    }

    public void setByteArray(String name, byte[] value) {
    	preferences.putByteArray(name, value);
    }
    
    public void setObject(String name, Object value) {
    	try {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	ObjectOutputStream oos = new ObjectOutputStream(baos);
	    	oos.writeObject(value);
	    	byte[] bytes = baos.toByteArray();
	    	preferences.putByteArray(name, bytes);
    	} catch(Exception exc) {
    		logger.logp(Level.SEVERE, this.getClass().toString(),
                    "setObject(String, Object)", "Exception", exc);
    	}
    }

    public void remove(String name) {
    	preferences.remove(name);
    }
}