/*
 * Created on Nov 8, 2004
 *
 */
package com.jme.ui;

import com.jme.renderer.ColorRGBA;

/**
 * @author schustej
 *
 */
public class UIColorScheme {
    /**
     * The background color of the ui component
     */
    public ColorRGBA _backgroundcolor = ColorRGBA.lightGray;
    
    /**
     * The color of the text
     */
    public ColorRGBA _foregroundcolor = ColorRGBA.black;
    
    /**
     * The color of the background when the mouse is hovering over the component
     */
    public ColorRGBA _highlightbackgroundcolor = new ColorRGBA( 0.9f, 0.9f, 0.9f, 0.5f);
    
    /**
     * The color of the background when the mouse is hovering the component
     */
    public ColorRGBA _highlightforegroundcolor = ColorRGBA.white;
    
    /**
     * The color of the light part of the border
     */
    public ColorRGBA _borderlightcolor = ColorRGBA.white;
    
    /**
     * The color of the dark part of the border
     */
    public ColorRGBA _borderdarkcolor = ColorRGBA.darkGray;
}
