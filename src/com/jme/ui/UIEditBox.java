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

import com.jme.scene.Node;
import com.jme.input.InputHandler;
import com.jme.input.MouseButtonStateType;
import com.jme.input.action.*;
import com.jme.input.*;

/**
 * UIEditBox is a Node based aggrigation b/t a UIText and UIActiveArea
 *
 * @author schustej
 *
 */
public class UIEditBox extends Node {

    protected UIActiveArea _hitArea = null;
    UIText _text = null;
    protected InputHandler _inputHandler = null;

    boolean _active = false;
    boolean _activateOnHover = true;

    int _cursorPos = 0;
    String _curText = "";
    String _oldText = "";

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
    public UIEditBox(String name, String fontFileName, int charsDisWidth, int charWidth,
            InputHandler inputHandler, int x, int y, float scale, float xtrim, float ytrim) {
        super(name);

        _inputHandler = inputHandler;

        _text = new UIText(name + "TEXT", fontFileName, x, y, scale, xtrim, ytrim);
        _hitArea = new UIActiveArea(x, y, (int) (_text._texSizeX * charsDisWidth), (int) (_text._texSizeY),
                inputHandler);

        _text.setText( _curText);

        this.attachChild(_text);

        _inputHandler.addBufferedKeyAction( new AbstractInputAction() {
            public void performAction(float time) {
                if (_active)
                    keyPress(this);
            }
        });
    }

    /**
     * This determines if the input listener cares about key presses to fill the control
     * @param active
     */
    public void setActive(boolean active) {
        _active = active;
    }

    /**
     * Sets if the control will only be active when the mouse is over the control
     * @param onHover
     */
    public void setActivateOnHover(boolean onHover) {
        _activateOnHover = onHover;
    }

    /**
     * Method that is called via inputHandler when a key is pressed
     * @param key
     */
    protected void keyPress(AbstractInputAction keyAction) {

        int val = _inputHandler.getKeyBindingManager().getKeyInput().getKeyIndex(keyAction.getKey());

        _oldText = _curText;

        switch( val) {
        	case KeyInput.KEY_BACK: {
        	    if( _cursorPos > 0)
        	        _curText = _curText.substring( 0, _cursorPos - 1) + _curText.substring( _cursorPos);
        	    _cursorPos--;
        	    break;
        	}
        	case KeyInput.KEY_LEFT: {
        	    _cursorPos--;
        	    break;
        	}
                case KeyInput.KEY_RIGHT: {
                    _cursorPos++;
                    break;
                }
                case KeyInput.KEY_LCONTROL:
                case KeyInput.KEY_RCONTROL:
                case KeyInput.KEY_LSHIFT:
                case KeyInput.KEY_RSHIFT:
                case KeyInput.KEY_LWIN:
                case KeyInput.KEY_NUMLOCK:
                case KeyInput.KEY_CAPITAL:
                case KeyInput.KEY_ESCAPE:
                case KeyInput.KEY_PGDN:
                case KeyInput.KEY_PGUP:
                case KeyInput.KEY_SCROLL:
                case KeyInput.KEY_UP:
                case KeyInput.KEY_DOWN:
                    break;
        	default: {
        	    _curText = _curText.substring(0, _cursorPos) + keyAction.getKeyChar() + _curText.substring( _cursorPos);
        	    _cursorPos++;
        	    break;
        	}
        }
        checkPos();
    }

    /**
     * Checks the position of the cursor, making sure that it's not out-of-bounds
     *
     */
    private void checkPos() {
        if( _cursorPos < 0) {
            _cursorPos = 0;
        }
        if( _cursorPos > _curText.length()) {
            _cursorPos = _curText.length();
        }
    }

    /**
     * used to update the active flag and the text in the box to be rendered
     * @return
     */
    public boolean update() {

        boolean retval = false;

        if (_hitArea.hitTest()) {
            if (_activateOnHover) {
                _active = true;
            }
        } else {
            if (_activateOnHover) {
                _active = false;
            }
        }

        if (_active && !_oldText.equals(_curText)) {
          _oldText = _curText;
          _text.setText( _curText);
        }

        return retval;
    }
}
