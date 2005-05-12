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
package com.jmex.ui;

import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.scene.*;

/**
 * A rectangular screen area for handling mouse input and checking hit
 * tests.
 * 
 * @author schustej
 *
 */
public class UIActiveArea extends Node {
   
    protected int _x;
    protected int _y;
    protected int _width;
    protected int _height;

    protected InputHandler _inputHandler = null;
    
    /**
     * Constructor, enter the x and y position (starting in lower left corner)
     * and width and height of the rectangle. The InputHandler should have
     * a mouse associated with it.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     * @param inputHandler
     */
    public UIActiveArea( String name, int x, int y, int width, int height, InputHandler inputHandler) {
        super( name);
        
        _x = x;
        _y = y;
        _width = width;
        _height = height;
        _inputHandler = inputHandler;
    }
    
    /**
     * Checks if the hot spot of the mouse is in the rectangle
     * area. This method will always return false if the inputhandler
     * is null
     * @return true if the hot spot of the mouse of the input handler is within the rectangle
     */
    public boolean hitTest() {
        
        if( _inputHandler == null) {
            return false;
        }
        
	    Vector3f mouseloc = _inputHandler.getMouse().getHotSpotPosition();
		if (mouseloc.x >= _x
				&& mouseloc.x <= _x + _width
				&& mouseloc.y  >= _y
				&& mouseloc.y  <= _y + _height) {
			return true;
		} else
			return false;
    }
    
    /**
     * Allows you to switch out the inputHandler being used for the hitTest
     * @param inputHandler
     */
    public void setInputHandler( InputHandler inputHandler) {
        _inputHandler = inputHandler;
    }
    
    /**
     * Allows the developer to gain access to the inputHander that's being
     * used for the hitTest
     * @return
     */
    public InputHandler getInputHandler() {
        return _inputHandler;
    }
}
