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
 * @author schustej
 *
 */
public class UIText extends Node {

    UICharacter[] _chars = new UICharacter[256];
    String _text = "";
    
    float _texSizeX = 0.0f;
    float _texSizeY = 0.0f;
    
    int _x = 0;
    int _y = 0;
    
    float _xtrim = 0.0f;
    float _ytrim = 0.0f;
    
    static final int CHAR_OFFSET = 16; 
    
    public UIText( String nodeName, String fontFileName, int x, int y, float scale, float xtrim, float ytrim) {
        super( nodeName);
        
        _xtrim = xtrim;
        _ytrim = ytrim;
        
        _x = x;
        _y = y;
        
        TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        ts.setEnabled(true);
		
        ts.setTexture(TextureManager.loadTexture(UIText.class
				.getClassLoader().getResource( fontFileName),
				Texture.MM_NEAREST, Texture.FM_NEAREST, true));
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
        
		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);

		this.setRenderState(as1);
    }
    
    public UIText( String nodeName, String fontFileName, int x, int y, float scale) {
        this( nodeName, fontFileName, x, y, scale, 0.0f, 0.0f);
    }
    
    public void setText( String text) {
        
        _text = text;
        
        this.detachAllChildren();
        
        for( int c = 0; c < _text.length(); c++) {
            
            int charval = ((int) _text.charAt(c)) + CHAR_OFFSET;
            int row = charval / 16;
            int charnum = charval % 16;
            
            UICharacter uichar = new UICharacter( _chars[ (256 - (16 * row)) + charnum ]);
            uichar.setLocalTranslation( new Vector3f( _x + (c * _texSizeX) + (_texSizeX / 2), _y + (_texSizeY / 2), 0.0f ));
            this.attachChild( uichar);
        }
        
        this.updateGeometricState(0.0f, true);
        this.updateRenderState();

    }
}
