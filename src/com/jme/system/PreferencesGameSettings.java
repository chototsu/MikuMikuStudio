/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.util.prefs.*;

/**
 * <code>PreferencesGameSettings</code> uses the Preferences system in Java
 * and implements the <code>GameSettings</code> interface.
 * 
 * @author Matthew D. Hicks
 * 
 * @see GameSettings
 */
public class PreferencesGameSettings implements GameSettings {
    private static final String DEFAULT_RENDERER = PropertiesIO.DEFAULT_RENDERER;
    private static final int DEFAULT_WIDTH = PropertiesIO.DEFAULT_WIDTH;
    private static final int DEFAULT_HEIGHT = PropertiesIO.DEFAULT_HEIGHT;
    private static final int DEFAULT_DEPTH = PropertiesIO.DEFAULT_DEPTH;
    private static final int DEFAULT_FREQUENCY = PropertiesIO.DEFAULT_FREQ;
    private static final boolean DEFAULT_FULLSCREEN = false; //PropertiesIO.DEFAULT_FULLSCREEN;
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
    
    public int getWidth() {
        return preferences.getInt("GameWidth", DEFAULT_WIDTH);
    }
    
    public int getHeight() {
        return preferences.getInt("GameHeight", DEFAULT_HEIGHT);
    }
    
    public int getDepth() {
        return preferences.getInt("GameDepth", DEFAULT_DEPTH);
    }
    
    public int getFrequency() {
        return preferences.getInt("GameFrequency", DEFAULT_FREQUENCY);
    }
    
    public boolean isFullscreen() {
        return preferences.getBoolean("GameFullscreen", DEFAULT_FULLSCREEN);
    }
    
    public int getDepthBits() {
        return preferences.getInt("GameDepthBits", DEFAULT_DEPTH_BITS);
    }
    
    public int getAlphaBits() {
        return preferences.getInt("GameAlphaBits", DEFAULT_ALPHA_BITS);
    }
    
    public int getStencilBits() {
        return preferences.getInt("GameStencilBits", DEFAULT_STENCIL_BITS);
    }
    
    public int getSamples() {
        return preferences.getInt("GameSamples", DEFAULT_SAMPLES);
    }
    
    public boolean isMusic() {
        return preferences.getBoolean("GameMusic", DEFAULT_MUSIC);
    }
    
    public boolean isSFX() {
        return preferences.getBoolean("GameSFX", DEFAULT_SFX);
    }
    
    public int getFramerate() {
        return preferences.getInt("GameFramerate", DEFAULT_FRAMERATE);
    }
}
