/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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

/*
 * Created on Jul 26, 2004
 *
 */
package com.jme.ui;

import com.jme.image.Texture;
import com.jme.scene.*;
import com.jme.system.DisplaySystem;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.math.*;

/**
 * A specialized Node that works like a UIObject and holds as many
 * UICharacters as are needed to create a string on screen
 * 
 * @author schustej
 *
 */
public class UIText extends UIObject {

    UIFonts _fonts = null;
    String _fontName = null;
    
    String _text = "";
    
    public float _texSizeX = 0.0f;
    public float _texSizeY = 0.0f;
    
    int _x = 0;
    int _y = 0;
    
    public float _xtrimFactor = 0.0f;
    public float _ytrimFactor = 0.0f;
    
    int _targheight = 0;
    int _targwidth = 0;
    
    /**
     */
    public UIText( String nodeName, UIFonts fonts,
        String fontName, String text,
        int x, int y, float xtrim, float ytrim,
        int targheight,int targwidth,
        UIColorScheme scheme, int flags ) {
        this( nodeName, fonts, fontName, text, x, y, xtrim, ytrim, targheight, targwidth, scheme, flags, true);
    }
    
    /**
     */
    public UIText( String nodeName, UIFonts fonts, String fontName, String text,
        int x, int y, float xtrim, float ytrim,
        int targheight, int targwidth, 
        UIColorScheme scheme, int flags, 
        boolean useClassLoader) {
        super( nodeName, x, y, 1, 1, scheme, flags);

        _xtrimFactor = xtrim / 100.0f;
        _ytrimFactor = ytrim / 100.0f;
        
        _x = x;
        _y = y;
        
        _targheight = targheight;
        _targwidth = targwidth;
        
        _fonts = fonts;
        _fontName = fontName;
        TextureState ts = _fonts.getFontTexture( fontName);
        
        _text = text;
        
        _texSizeX = ts.getTexture().getImage().getWidth()  / 16;
        _texSizeY = ts.getTexture().getImage().getHeight()  / 16;
        
        float scalefactor = _targheight / _texSizeX;
        
        _texSizeX *= scalefactor;
        _texSizeY *= scalefactor;
        
//        _xtrimFactor *= scalefactor;
//        _ytrimFactor *= scalefactor;
        
        if( _text.length() > 0) {
            setText( _text);
        }
        
    }
    
    /**
     * Called to set what text is rendered by UIText
     * @param text
     */
    public void setText( String text) {
        
        _text = text;
        
        this.detachAllChildren();

        if( _targwidth == 0) {
            _width = (int) (( _text.length() * ( _texSizeX * _xtrimFactor)) + _texSizeX / 2);
        } else {
            _width = _targwidth;
        }
        
        _height = (int) ( _texSizeY);
        
        setup();
        
        this.setLocalScale( 1.0f);
        
        for( int c = 0; c < _text.length(); c++) {
            
            int xloc = (int) (_x + ( c * ( _texSizeX * _xtrimFactor)));
            int yloc = (int) (_y + ( _texSizeY * _ytrimFactor));
            
            //System.out.println( xloc);
            
            UICharacter uichar = _fonts.createCharacter( name + Integer.toString( c),
                    _text.charAt(c),
                    _fontName,
                    xloc,
                    yloc,
                    (int) _texSizeX,
                    (int) _texSizeY,
                    1.0f,
                    _scheme);
            
            if( ( xloc + _texSizeX) > (_width + _x) ) {
                break;
            }
            
            this.attachChild( uichar);
        }

        this.updateGeometricState(0.0f, true);
        this.updateRenderState();
    }
    
    /**
     * Empty, just here to create consistent interface
     * @return
     */
    public boolean update() {
        return false;
    }
}
