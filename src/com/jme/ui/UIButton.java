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
 * Created on Jun 9, 2004
 *
 */
package com.jme.ui;

import com.jme.image.Texture;
import com.jme.math.*;
import com.jme.input.InputHandler;
import com.jme.input.MouseButtonStateType;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * UIButton is a UIObject derived class that uses mouse input to change out the
 * texture that is shown on the button
 *
 * @author schustej
 *
 */
public class UIButton extends UIActiveObject {

    UIText _text = null;
    /**
     * Constructor requires the unique name, the inputhandler with the mouse
     * that will be monitored for hit tests and the 3 files for the states that
     * it will show.
     *
     * The states may be up, over and down. Up is the steady state. Over, is
     * used for when the mouse is over the button. Down is when the left mouse
     * button is down and the mouse is over the button.
     *
     * @param name
     * @param x
     * @param y
     * @param inputHandler
     * @param upfile
     * @param overfile
     * @param downfile
     */
    public UIButton(String name, int x, int y, int width, int height,
            InputHandler inputHandler,
            String upfile, String overfile, String downfile,
            int flags) {
        this( name, x, y, width, height, inputHandler, null, upfile, overfile, downfile, flags, true);
    }

    /**
     * Non-image based constructor, use only borders instead
     */
    public UIButton( String name, int x, int y, int width, int height, 
            InputHandler inputHandler,
            UIColorScheme scheme, int flags) {
        this( name, x, y, width, height, inputHandler, scheme, null, null, null, flags, true);
    }

    /**
     * Difference allows for the images to be loaded directly from the file
     * system instead of via the classloader
     */
    public UIButton(String name,
            int x, int y, int width, int height,
            InputHandler inputHandler, 
            UIColorScheme scheme,
            String upfile, String overfile, String downfile,
            int flags,
            boolean useClassloader ) {
        super(name, x, y, width, height, inputHandler, scheme, flags);
        
        if( upfile != null && overfile != null && downfile != null) {
            
        	TextureState[] textureStates = new TextureState[3];
        
	        for (int i = 0; i < 3; i++) {
	            textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	            textureStates[i].setEnabled(true);
	        }
	
	        if (useClassloader) {
	            textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
	                    .getResource(upfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
	            textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
	                    .getResource(overfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
	            textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
	                    .getResource(downfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
	        } else {
	            textureStates[0].setTexture(TextureManager.loadTexture(upfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	            textureStates[1].setTexture(TextureManager.loadTexture(overfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	            textureStates[2].setTexture(TextureManager.loadTexture(downfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	        }
	
	        textureStates[0].apply();
	        textureStates[1].apply();
	        textureStates[2].apply();
	        
	        _textureStates.add( textureStates[0]);
	        _textureStates.add( textureStates[1]);
	        _textureStates.add( textureStates[2]);
        }
        
	    setup();
    }

    /**
     * Needs to be called during the update cycle to allow for
     * the mouse to be checked for hit test and mouse button state.
     *
     */
    public boolean update( float time) {

        boolean retval = false;

        if (hitTest()) {

            if (_inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {
                // button is down
                if (_state != DOWN) {
                    if( usingTexture()) {
                        if( (DRAW_DOWN & _flags) == DRAW_DOWN) {
                            _quad.setRenderState( ((TextureState)_textureStates.elementAt(DOWN)));
                            _quad.updateRenderState();
                        }
                    }
                    if( usingBorders()) {
                        if( (DRAW_DOWN & _flags) == DRAW_DOWN) {
                            setAltBorderColors();
                            if( _text != null && _state == OVER) {
                                _text.setLocalTranslation( new Vector3f( 1.0f, -1.0f, 0.0f));
                                _text.updateGeometricState(0.0f, true);
                                _text.updateRenderState();
                            }
                        }
                    }
                    _state = DOWN;
                    fireActions();
                }
            } else {
                // button is up
                if (_state == DOWN) {
                    // Fire action. here
                    retval = true;
                }
                if (_state != OVER) {
                    if( usingTexture()) {
                        if( (DRAW_OVER & _flags) == DRAW_OVER) {
                            _quad.setRenderState( ((TextureState)_textureStates.elementAt(OVER)));
                            _quad.updateRenderState();
                        }
                    }
                    if( usingBorders()) {
                        setBaseBorderColors();
                        if( (DRAW_OVER & _flags) == DRAW_OVER) {
                            setHighlightColors();
                        }
                        if( _text != null && _state == DOWN) {
                            _text.setLocalTranslation( new Vector3f( -1.0f, 1.0f, 0.0f));
                            _text.updateGeometricState(0.0f, true);
                            _text.updateRenderState();
                        }
                    }
                    _state = OVER;
                    fireActions();
                }
            }
        } else {
            if (_state != UP) {
                if( usingTexture()) {
	                _quad.setRenderState( ((TextureState)_textureStates.elementAt(UP)));
	                _quad.updateRenderState();
                }
                if( usingBorders()) {
                    setBaseBorderColors();
                    if( (DRAW_OVER & _flags) == DRAW_OVER) {
                        setBaseColors();
                    }
                }
                _state = UP;
                fireActions();
            }
        }
        return retval;
    }

    public void setText( UIFonts fonts, String fontName, String text) {
        
        _text = new UIText( name+"text", fonts, fontName, text,
                _x, _y, 65.0f, 0.0f, _height, _width, _scheme, 0);
        
        _text.setLocalTranslation( new Vector3f( (_width - _text.getWidth()) / 2,
                (_height - _text.getHeight()) / 2, 0.0f ));
        
        this.attachChild( _text);
    }
}
