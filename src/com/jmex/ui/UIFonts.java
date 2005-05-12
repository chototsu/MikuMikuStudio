/*
 * Created on Nov 8, 2004
 *
 */
package com.jmex.ui;

import com.jme.image.Texture;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import java.util.*;
import com.jme.system.JmeException;

/**
 * @author schustej
 *
 */
public class UIFonts {
    static final int CHAR_OFFSET = 16; 

    UICharacter[] _chars = new UICharacter[256];
    HashMap _textureStates = new HashMap();

    public UIFonts( String[] fontNames, String[] fontFileNames) {
        this( fontNames, fontFileNames, true);
    }

    public UIFonts( String[] fontNames, String[] fontFileNames, boolean useClassLoader) {

        if( fontNames.length != fontFileNames.length) {
            throw new JmeException( "Font names array and font file names array must be equal");
        }
        
        for( int n = 0; n < fontFileNames.length; n++) {
        
	        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	        ts.setEnabled(true);

	        if (useClassLoader) {
	            ts.setTexture(TextureManager.loadTexture(UIText.class.getClassLoader().getResource(fontFileNames[n]),
	                    Texture.MM_NEAREST, Texture.FM_NEAREST));
	        } else {
	            ts.setTexture(TextureManager.loadTexture(fontFileNames[n], Texture.MM_NEAREST, Texture.FM_NEAREST));
	        }
	        ts.apply();
	        _textureStates.put( fontNames[n], ts);
        }
    
        for( int fx = 0; fx < 16; fx++) {
            for( int fy = 0; fy < 16; fy++) {
                _chars[ (fy * 16) + fx] = new UICharacter( "UIC" + fx + fy,
                        									(float) fx / 16.0f,
                        									(float) fy / 16.0f,
                        									(float) (fx + 1) / 16.0f,
                        									(float) (fy + 1) / 16.0f);
            }
        }
    }
    
    public UICharacter createCharacter( String name, char c, String fontname,
            int x, int y, int width, int height, 
            float scale, UIColorScheme scheme) {
        
        int charval = ((int) c) + CHAR_OFFSET;
        int row = charval / 16;
        int charnum = charval % 16;
        
        UICharacter uichar = new UICharacter( name , _chars[ (256 - (16 * row)) + charnum ], x, y, width, height, scale, scheme);
        //uichar.setLocalTranslation( new Vector3f( x, y, 0));
        uichar.setRenderState( (TextureState) _textureStates.get( fontname));

        return uichar;
    }
    
    public TextureState getFontTexture( String name) {
        return (TextureState) _textureStates.get( name);
    }
    
}
