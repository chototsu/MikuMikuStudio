/**
 * 
 */
package com.jme.system;

/**
 * <code>GameSettings</code> offers an abstraction from the internals of getting/setting
 * settings for a game.
 * 
 * @author Matthew D. Hicks
 */
public interface GameSettings {
    public String getRenderer();
    
    public int getWidth();
    
    public int getHeight();
    
    public int getDepth();
    
    public int getFrequency();
    
    public boolean isFullscreen();
    
    public int getDepthBits();
    
    public int getAlphaBits();
    
    public int getStencilBits();
    
    public int getSamples();
    
    public boolean isMusic();
    
    public boolean isSFX();
    
    public int getFramerate();
}
