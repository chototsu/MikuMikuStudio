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
 * @author schustej
 *  
 */
public class UIEditBox extends Node {

    protected UIActiveArea _hitArea = null;
    UIText _text = null;
    protected InputHandler _inputHandler = null;

    boolean _active = false;
    boolean _activeateOnHover = true;
    
    int _cursorPos = 0;
    String _curText = "";

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
                    KeyPress( this.key);
            }
        });
    }

    public void setActive(boolean active) {
        _active = active;
    }

    public void setActivateOnHover(boolean onHover) {
        _activeateOnHover = onHover;
    }

    protected void KeyPress( String key) {

        int val = _inputHandler.getKeyBindingManager().getKeyInput().getKeyIndex( key);
        
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
        	default: {
        	    _curText = _curText.substring(0, _cursorPos) + key + _curText.substring( _cursorPos);
        	    _cursorPos++;
        	    break;
        	}
        }
        checkPos();
    }
    
    private void checkPos() {
        if( _cursorPos < 0) {
            _cursorPos = 0;
        }
        if( _cursorPos > _curText.length()) {
            _cursorPos = _curText.length();
        }
    }
    
    public boolean update() {

        boolean retval = false;

        if (_hitArea.hitTest()) {
            if (_activeateOnHover) {
                _active = true;
            }
        } else {
            if (_activeateOnHover) {
                _active = false;
            }
        }

        if (_active) {
    	    _text.setText( _curText);
        }

        return retval;
    }
}