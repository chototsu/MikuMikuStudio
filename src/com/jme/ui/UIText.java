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
public class UIText extends Node {

    private static final long serialVersionUID = 1L;
	UICharacter[] _chars = new UICharacter[256];
    String _text = "";
    
    float _texSizeX = 0.0f;
    float _texSizeY = 0.0f;
    
    int _x = 0;
    int _y = 0;
    
    float _xtrim = 0.0f;
    float _ytrim = 0.0f;
    
    static final int CHAR_OFFSET = 16; 
    
    /**
     * Constructor
     * @param nodeName unique name for the object
     * @param fontFileName The filename for the font, this will be useing the classloader to load as a resource
     * @param x location x
     * @param y location y
     * @param scale scale the UIObjects
     * @param xtrim how much to trim, in %, from the sides of the characters. This scruches them together horizontally.
     * @param ytrim how much to trim, in %, from the top and bottom of the characters. This scruches them together vertically
     */
    public UIText( String nodeName, String fontFileName, int x, int y, float scale, float xtrim, float ytrim) {
        this( nodeName, fontFileName, x, y, scale, xtrim, ytrim, true);
    }
    
    /**
     * Alternate constructor that allows the loading of the font file directly from the file system without using the classloader
     * @param nodeName
     * @param fontFileName
     * @param x
     * @param y
     * @param scale
     * @param xtrim
     * @param ytrim
     * @param useClassLoader
     */
    public UIText( String nodeName, String fontFileName, int x, int y, float scale, float xtrim, float ytrim, boolean useClassLoader) {
        super( nodeName);

        _xtrim = xtrim;
        _ytrim = ytrim;
        
        _x = x;
        _y = y;
        
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        ts.setEnabled(true);
	
        if( useClassLoader) {
        ts.setTexture(TextureManager.loadTexture(UIText.class
				.getClassLoader().getResource( fontFileName),
				Texture.MM_NEAREST, Texture.FM_NEAREST, true));
        } else {
	        ts.setTexture(TextureManager.loadTexture( fontFileName, Texture.MM_NEAREST,
	                Texture.FM_NEAREST, true));
		}        
        ts.apply();
        
        _texSizeX = (ts.getTexture().getImage().getWidth()  / 16) * scale;
        _texSizeX -= (_texSizeX * (_xtrim / 100.0f));
        
        _texSizeY = (ts.getTexture().getImage().getHeight()  / 16) * scale;
        _texSizeY -= (_texSizeY * (_ytrim / 100.0f));
        
        float diff = 1.0f / 16.0f;
        
        for( int fx = 0; fx < 16; fx++) {
            for( int fy = 0; fy < 16; fy++) {
                _chars[ (fy * 16) + fx] = new UICharacter( "UIC" + fx + fy,
                        									ts,
                        									(float) fx / 16.0f,
                        									(float) fy / 16.0f,
                        									(float) (fx + 1) / 16.0f,
                        									(float) (fy + 1) / 16.0f,
                        									scale);
            }
        }
        
        this.setRenderState( ts);
        
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);

		this.setRenderState(as1);
    }
    
    /**
     * simpler constructor without trim values
     * @param nodeName
     * @param fontFileName
     * @param x
     * @param y
     * @param scale
     */
    public UIText( String nodeName, String fontFileName, int x, int y, float scale) {
        this( nodeName, fontFileName, x, y, scale, 0.0f, 0.0f);
    }
    
    /**
     * Called to set what text is rendered by UIText
     * @param text
     */
    public void setText( String text) {
        
        _text = text;
        
        this.detachAllChildren();
        
        for( int c = 0; c < _text.length(); c++) {
            
            int charval = ((int) _text.charAt(c)) + CHAR_OFFSET;
            int row = charval / 16;
            int charnum = charval % 16;
            
            UICharacter uichar = new UICharacter( Integer.toString( c) , _chars[ (256 - (16 * row)) + charnum ]);
            uichar.setLocalTranslation( new Vector3f( _x + (c * _texSizeX) + (_texSizeX / 2), _y + (_texSizeY / 2), 0.0f ));
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
