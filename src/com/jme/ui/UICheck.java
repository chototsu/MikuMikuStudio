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
import com.jme.input.MouseButtonStateType;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.input.*;
import com.jme.math.Vector3f;

/**
 * UIObject derrived object that adds another state beyond the UIButton, a
 * checked state.
 *
 * The UICheck may be Up, Over, Down or Checked. When the state is Checked, then
 * the other states are disabled. When UnChecked the Up, Over and Down states
 * apply.
 *
 * @author schustej
 *
 */
public class UICheck extends UIActiveObject {

    protected boolean _selected = false;
    
    UIText _text = null;

    public UICheck( String name, 
            int x, int y, int width, int height,
            InputHandler inputHandler, UIColorScheme scheme, int flags) {
        this( name, x, y, width, height, inputHandler, scheme, null, null, null, null, flags, false );
    /**
     * Constructor, give unique name, inputhandler with the mouse attached to check for
     * hit tests, the files for the different states and location and scale. This will default
     * to using the classloader to get the resources by name
     */
    }
    
    public UICheck(String name, int x, int y, int width, int height,
        	InputHandler inputHandler,
        	String upfile, String overfile, String downfile, String selectedfile,
            int flags) {
        this( name, x, y, width, height, inputHandler, null, upfile, overfile, downfile, selectedfile, flags, true);
    }
    /**
     * Alternate constructer allow developer to load directly from file system
     */
    public UICheck(String name, int x, int y, int width, int height, 
            	InputHandler inputHandler,
            	UIColorScheme scheme,
            	String upfile, String overfile, String downfile, String selectedfile,
                int flags, boolean useClassLoader) {
        super(name, x, y, width, height, inputHandler, scheme, flags);

        if( upfile != null && overfile != null && downfile != null && selectedfile != null) {
	        
	        TextureState[] textureStates = new TextureState[4];
	
	        for (int i = 0; i < 4; i++) {
	            textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	            textureStates[i].setEnabled(true);
	        }
	
	        if( useClassLoader) {
		        textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
		                upfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		        textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
		                overfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		        textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
		                downfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		        textureStates[3].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
		                selectedfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
	        } else {
	            textureStates[0].setTexture(TextureManager.loadTexture(upfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	            textureStates[1].setTexture(TextureManager.loadTexture(overfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	            textureStates[2].setTexture(TextureManager.loadTexture(downfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	            textureStates[3].setTexture(TextureManager.loadTexture(selectedfile, Texture.MM_NEAREST,
	                    Texture.FM_NEAREST, true));
	        }
	        textureStates[0].apply();
	        textureStates[1].apply();
	        textureStates[2].apply();
	        textureStates[3].apply();
        
	        _textureStates.add( textureStates[0]);
	        _textureStates.add( textureStates[1]);
	        _textureStates.add( textureStates[2]);
	        _textureStates.add( textureStates[3]);
        }
        
        setup();
    }

    /**
     * Checks the state of the mouse against the state of the control.
     */
    public boolean update( float time) {

        boolean retval = false;

        if (hitTest()) {

            if (_inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {

                // button is down
                if (_state != DOWN) {
                    if (!_selected) {
	                    if( usingTexture()) {
	                        if( (DRAW_DOWN & _flags) == DRAW_DOWN) {
	                            _quad.setRenderState(((TextureState)_textureStates.elementAt(DOWN)));
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
                    }
                    _state = DOWN;
                }
            } else {
                // button is up
                if (_state == DOWN) {
                    // Fire action. here
                    retval = true;
                    if (!_selected) {
                        if( usingTexture()) {
                            _quad.setRenderState(((TextureState)_textureStates.elementAt(SELECTED)));
                            _quad.updateRenderState();
                        }
                        _selected = true;
                        _state = OVER;
                    } else {
                        _selected = false;
                    }
                }
                if (!_selected && _state != OVER) {
                    if( usingTexture()) {
                        if( (DRAW_OVER & _flags) == DRAW_OVER) {
                            _quad.setRenderState(((TextureState)_textureStates.elementAt(OVER)));
                            _quad.updateRenderState();
                        }
                    }
                    if( usingBorders()) {
                        setBaseBorderColors();
                        if( (DRAW_OVER & _flags) == DRAW_OVER) {
                            setHighlightColors();
                            if( _text != null && _state == DOWN) {
                                _text.setLocalTranslation( new Vector3f( -1.0f, 1.0f, 0.0f));
                                _text.updateGeometricState(0.0f, true);
                                _text.updateRenderState();
                            }

                        }
                    }
                    _state = OVER;
                }
            }
        } else {
            if (!_selected && _state != UP) {
                if( usingTexture()) {
                    _quad.setRenderState(((TextureState)_textureStates.elementAt(UP)));
                    _quad.updateRenderState();
                }
                if( usingBorders()) {
                    setBaseBorderColors();
                    if( (DRAW_OVER & _flags) == DRAW_OVER) {
                        setBaseColors();
                    }
                }
                _state = UP;
            }
        }
        return retval;
    }

    /**
     * Override of the UIObject getState() which returns also the checked or selected
     * state.
     */
    public int getState() {
        if (_selected) {
            return SELECTED;
        } else {
            return _state;
        }
    }

    /**
     * sets the current state of the button.
     */
    public void setSelected(boolean sel) {
      int newState = sel ? SELECTED : UP;
      _selected = sel;
      if (_state != newState) {
        _state = newState;
        this.setRenderState( (TextureState) _textureStates.elementAt(_state));
        this.updateRenderState();
      }
    }
    
    public void setText( UIFonts fonts, String fontName, String text) {
        _text = new UIText( name+"text", fonts, fontName, text,
                _x, _y, 65.0f, 0.0f, _height, _width, _scheme, 0);
        
        _text.setLocalTranslation( new Vector3f( (this.getWidth() - _text.getWidth()) / 2,
                (this.getHeight() - _text.getHeight()) / 2, 0.0f ));
        
        this.attachChild( _text);
    }
    
}
