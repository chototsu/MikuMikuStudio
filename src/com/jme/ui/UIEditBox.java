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
 * Created on Jul 28, 2004
 *
 */
package com.jme.ui;

import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseButtonStateType;
import com.jme.input.action.*;
import com.jme.math.Vector3f;

/**
 * UIEditBox is a Node based aggrigation b/t a UIText and UIActiveArea
 *
 * @author schustej
 *
 */
public class UIEditBox extends UIActiveObject {

    UIText _text = null;
    UICharacter _cursor = null;
    
    boolean _focused = true;

    int _cursorPos = 0;
    
    String _textBuffer = "";
    
    long blinkint = 300;
    long lasttime = 0;
    boolean cursoron = false;
    
    protected InputHandler _bufferedInput;
    
    boolean buffignore = false;

    public abstract class EditBoxKeyInputAction extends KeyInputAction {
        public EditBoxKeyInputAction() {
            this.allowsRepeats = false;
        }
    };
    
    /**
     * Constructor.
     * @param name
     * @param fontFileName passed to the UIText object
     * @param charsDisWidth How many characters wide the editor should be
     * @param charWidth How many characters are allowed in the text total
     * @param inputHandler passed to the UIActiveArea
     * @param x passed to the UIText
     * @param y passed to the UIText
     * @param scale passed to the UIText
     * @param xtrim passed to the UIText
     * @param ytrim passed to the UIText
     */
    public UIEditBox( String name, int x, int y, int width, int height, 
            InputHandler inputHandler, InputHandler bufferedInputHandler,
            UIColorScheme scheme, UIFonts fonts,
            String fontName, String text, float xtrim, float ytrim,
            int flags) {
        super( name, x, y, width, height, inputHandler, scheme, flags);

        _inputHandler = inputHandler;
        _bufferedInput = bufferedInputHandler;

        _text = new UIText( name + "TEXT", fonts, fontName, text, x, y, xtrim, ytrim, height, width, scheme, 0);
        
        _textBuffer = text;

        _cursor = fonts.createCharacter( name + "cursor",
                    '|',
                    fontName,
                    (int) (_x - ( _text._texSizeX / 3)),
                    _y,
                    (int) _text._texSizeX,
                    (int) _text._texSizeY,
                    1.0f,
                    _scheme);
        
        _cursor.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_SKIP);

        setupKeyBindings();
        
        setup();
        this.attachChild( _text);
        this.attachChild( _cursor);
        
        setCursorLocation();
        
    }

    private void setupKeyBindings() {
        
        _inputHandler.addKeyboardAction( this.name + "backspc_action", KeyInput.KEY_BACK, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;
                
                if( _cursorPos > 0 && _textBuffer.length() > 0) {
	                String before = _textBuffer.substring( 0, _cursorPos - 1);
	                String after = _textBuffer.substring( _cursorPos);
	                _textBuffer = before + after;
	                _text.setText( _textBuffer);
	                _cursorPos -= 1;
	                setCursorLocation();
                }
                
            }
        });
        _inputHandler.addKeyboardAction( this.name + "delete_action", KeyInput.KEY_DELETE, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;

                if( _cursorPos < _textBuffer.length() && _textBuffer.length() > 0) {
	                String before = _textBuffer.substring( 0, _cursorPos);
	                String after = _textBuffer.substring( _cursorPos + 1);
	                _textBuffer = before + after;
	                _text.setText( _textBuffer);
                }
                
            }
        });
        _inputHandler.addKeyboardAction( this.name + "left_action", KeyInput.KEY_LEFT, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;

                _cursorPos -= 1;
                
                if( _cursorPos < 0) {
                    _cursorPos = 0;
                }

                setCursorLocation();
                
            }
        });
        _inputHandler.addKeyboardAction( this.name + "right_action", KeyInput.KEY_RIGHT, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;
                
                _cursorPos += 1;
                
                if( _cursorPos > _textBuffer.length()) {
                    _cursorPos = _textBuffer.length();
                }

                
                setCursorLocation();
                
            }
        });
        _inputHandler.addKeyboardAction( this.name + "end_action", KeyInput.KEY_END, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;
                
                _cursorPos = _textBuffer.length();
                setCursorLocation();

            }
        });
        
        _inputHandler.addKeyboardAction( this.name + "home_action", KeyInput.KEY_HOME, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;

                _cursorPos = 0;
                setCursorLocation();
            }
        });
        
        _inputHandler.addKeyboardAction( this.name + "space_action", KeyInput.KEY_SPACE, new EditBoxKeyInputAction() {
            public void performAction( InputActionEvent event) {
                buffignore = true;

                if( ( (_textBuffer.length() + 1) * ( _text._texSizeX * _text._xtrimFactor)) > _width ) {
                    return;
                }
                
                String before = _textBuffer.substring( 0, _cursorPos);
                String after = _textBuffer.substring( _cursorPos);
                before += ' ';
                _textBuffer = before + after;
                _text.setText( _textBuffer);
                _cursorPos += 1;
                setCursorLocation();
                
            }
        });
        
        _bufferedInput.addBufferedKeyAction(new KeyInputAction() {
            public void performAction(InputActionEvent event) {
                
                if( !buffignore) {
                    
                    if( ( (_textBuffer.length() + 1) * ( _text._texSizeX * _text._xtrimFactor)) > _width ) {
                        return;
                    }

                
	                String before = _textBuffer.substring( 0, _cursorPos);
	                String after = _textBuffer.substring( _cursorPos);
	                before += key;
	                _textBuffer = before + after;
	                _text.setText( _textBuffer);
	                _cursorPos += 1;
	                setCursorLocation();
                }
                
                buffignore = false;
                
            }
        });

    }
        
    private void setCursorLocation() {
        
        _cursor.getLocalTranslation().x = (_cursorPos * ( _text._texSizeX * _text._xtrimFactor));
        
        this.updateGeometricState(0.0f, true);
        this.updateRenderState();
    }
    

    /**
     * used to update the active flag and the text in the box to be rendered
     * @return
     */
    public boolean update( float time) {

        boolean retval = false;
        
        if (hitTest()) {
            if ( _inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {
                // button is down
                
                _focused = true;
                
                Vector3f mouseloc = _inputHandler.getMouse().getHotSpotPosition();
                
                mouseloc.x -= _x;
                mouseloc.y -= _y;
                
                _cursorPos = (int) (mouseloc.x / ( _text._texSizeX * _text._xtrimFactor));
                
                if( _cursorPos > _textBuffer.length()) {
                    _cursorPos = _textBuffer.length();
                }
                
                setCursorLocation();
                
            }
            
        } else {
            if (_inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {
                // button is down
                
                _focused = false;
                _cursor.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_SKIP);
            }
        }

        if( _focused) {
            if( System.currentTimeMillis() - lasttime > blinkint ) {
                cursoron = !cursoron;
                lasttime = System.currentTimeMillis();
        	}
            if( cursoron) {
                _cursor.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_ORTHO);
            } else {
                _cursor.setRenderQueueMode( com.jme.renderer.Renderer.QUEUE_SKIP);
            }
        }
        
        return retval;
    }
}
