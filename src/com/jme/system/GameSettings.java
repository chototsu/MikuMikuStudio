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

/**
 * <code>GameSettings</code> offers an abstraction from the internals of getting/setting
 * settings for a game.
 * 
 * @author Matthew D. Hicks
 */
public interface GameSettings {
    /**
     * Returns the stored rendering API name, or the default
     * 
     * @return
     *      String
     */
    public String getRenderer();
    
    /**
     * Sets the rendering API.
     * 
     * @param renderer
     */
    public void setRenderer(String renderer);
    
    /**
     * Returns the width for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    public int getWidth();
    
    /**
     * Sets the width for the screen.
     * 
     * @param width
     */
    public void setWidth(int width);
    
    /**
     * Returns the height for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    public int getHeight();
    
    /**
     * Sets the height for the screen.
     * 
     * @param height
     */
    public void setHeight(int height);
    
    /**
     * Returns the depth for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    public int getDepth();
    
    /**
     * Sets the depth for the screen.
     * 
     * @param depth
     */
    public void setDepth(int depth);
    
    /**
     * Returns the screen refresh frequency as stored or the default.
     * 
     * @return
     *      int
     */
    public int getFrequency();
    
    /**
     * Sets the screen refresh frequency.
     * 
     * @param frequency
     */
    public void setFrequency(int frequency);
    
    /**
     * Returns the current state of vertical synchronization. This synchronizes
     * the game update frequency to the monitor update frequency. This can help
     * provide a much smoother game experience and help with screen tearing.
     * 
     * @return
     *      boolean
     */
    public boolean isVerticalSync();
    
    /**
     * Sets the state of vertical synchronization. This synchronizes
     * the game update frequency to the monitor update frequency. This can help
     * provide a much smoother game experience and help with screen tearing.
     * 
     * @param vsync
     */
    public void setVerticalSync(boolean vsync);
    
    /**
     * Returns the screen's fullscreen status as stored or the default.
     * 
     * @return
     *      boolean
     */
    public boolean isFullscreen();
    
    /**
     * Sets the fullscreen status for the screen.
     * 
     * @param fullscreen
     */
    public void setFullscreen(boolean fullscreen);
    
    /**
     * Returns the depth bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    public int getDepthBits();
    
    /**
     * Sets the depth bits for use with the renderer.
     * 
     * @param depthBits
     */
    public void setDepthBits(int depthBits);
    
    /**
     * Returns the alpha bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    public int getAlphaBits();
    
    /**
     * Sets the alpha bits for use with the renderer.
     * 
     * @param alphaBits
     */
    public void setAlphaBits(int alphaBits);
    
    /**
     * Returns the stencil bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    public int getStencilBits();
    
    /**
     * Sets the stencil bits for use with the renderer.
     * 
     * @param stencilBits
     */
    public void setStencilBits(int stencilBits);
    
    /**
     * Returns the number of samples to use for the multisample buffer
     * as stored or the default.
     * 
     * @return
     *      int
     */
    public int getSamples();
    
    /**
     * Sets the number of samples to use for the multisample buffer.
     * 
     * @param samples
     */
    public void setSamples(int samples);
    
    /**
     * Returns the enabled status of music as stored or the default.
     * 
     * @return
     *      boolean
     */
    public boolean isMusic();
    
    /**
     * Sets the enabled status of music.
     * 
     * @param musicEnabled
     */
    public void setMusic(boolean musicEnabled);
    
    /**
     * Returns the enabled status of sound effects as stored or the default.
     * 
     * @return
     *      boolean
     */
    public boolean isSFX();
    
    /**
     * Sets the enabled status of sound effects.
     * 
     * @param sfxEnabled
     */
    public void setSFX(boolean sfxEnabled);
    
    /**
     * Returns the specified framerate or -1 if variable framerate is specified.
     * 
     * @return
     *      int
     */
    public int getFramerate();
    
    /**
     * Sets the framerate. Use -1 to specify variable framerate.
     * 
     * @param framerate
     */
    public void setFramerate(int framerate);
    
    /**
     * Clears all settings and reverts to default settings
     */
    public void clear() throws Exception;
    
    public void set(String name, String value);
    
    public void setBoolean(String name, boolean value);
    
    public void setInt(String name, int value);
    
    public void setLong(String name, long value);
    
    public void setFloat(String name, float value);
    
    public void setDouble(String name, double value);
    
    public void setByteArray(String name, byte[] bytes);
    
    public void setObject(String name, Object obj);
    
    public String get(String name, String defaultValue);
    
    public boolean getBoolean(String name, boolean defaultValue);
    
    public int getInt(String name, int defaultValue);
    
    public long getLong(String name, long defaultValue);
    
    public float getFloat(String name, float defaultValue);
    
    public double getDouble(String name, double defaultValue);
    
    public byte[] getByteArray(String name, byte[] bytes);
    
    public Object getObject(String name, Object obj);
}
