/*
 * Created on Jul 28, 2004
 *
 */
package com.jme.ui;

import com.jme.input.InputHandler;
import com.jme.math.Vector3f;

/**
 * @author schustej
 *
 */
public class UIActiveArea {
   
    protected int _x;
    protected int _y;
    protected int _width;
    protected int _height;

    protected InputHandler _inputHandler = null;
    
    public UIActiveArea( int x, int y, int width, int height, InputHandler inputHandler) {
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _inputHandler = inputHandler;
    }
    
    protected boolean hitTest() {
	    Vector3f mouseloc = _inputHandler.getMouse().getHotSpotPosition();
		if (mouseloc.x >= _x
				&& mouseloc.x <= _x + _width
				&& mouseloc.y  >= _y
				&& mouseloc.y  <= _y + _height) {
			return true;
		} else
			return false;
    }
}
