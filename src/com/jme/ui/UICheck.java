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
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.input.*;

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
public class UICheck extends UIObject {

    private static final long serialVersionUID = 1L;
	protected boolean _selected = false;

    /**
     * Constructor, give unique name, inputhandler with the mouse attached to check for
     * hit tests, the files for the different states and location and scale. This will default
     * to using the classloader to get the resources by name
     * @param name
     * @param inputHandler
     * @param upfile
     * @param overfile
     * @param downfile
     * @param selectedfile
     * @param x
     * @param y
     * @param scale - used for both x and y scale
     */
    public UICheck(String name, InputHandler inputHandler, String upfile, String overfile, String downfile,
            String selectedfile, int x, int y, float scale) {
        this( name, inputHandler, upfile, overfile, downfile, selectedfile, x, y, scale, scale, true);
    }

    /**
     * Alternate constructer allow developer to load directly from file system
     * @param name
     * @param inputHandler
     * @param upfile
     * @param overfile
     * @param downfile
     * @param selectedfile
     * @param x
     * @param y
     * @param scale
     * @param useClassLoader
     */
    public UICheck(String name, InputHandler inputHandler, String upfile, String overfile, String downfile,
                String selectedfile, int x, int y, float xscale, float yscale, boolean useClassLoader) {


        super(name, inputHandler, x, y, xscale, yscale);

        _textureStates = new TextureState[4];

        for (int i = 0; i < 4; i++) {
            _textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
            _textureStates[i].setEnabled(true);
        }

        if( useClassLoader) {
	        _textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
	                upfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
	        _textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
	                overfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
	        _textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
	                downfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
	        _textureStates[3].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader().getResource(
	                selectedfile), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
        } else {
            _textureStates[0].setTexture(TextureManager.loadTexture(upfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
            _textureStates[1].setTexture(TextureManager.loadTexture(overfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
            _textureStates[2].setTexture(TextureManager.loadTexture(downfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
            _textureStates[3].setTexture(TextureManager.loadTexture(selectedfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
        }
        _textureStates[0].apply();
        _textureStates[1].apply();
        _textureStates[2].apply();
        _textureStates[3].apply();
        this.setRenderState(_textureStates[0]);

        setup();
    }

    /**
     * Checks the state of the mouse against the state of the control.
     */
    public boolean update() {

        boolean retval = false;

        if (hitTest()) {

            if (_inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {

                // button is down
                if (_state != DOWN) {
                    if (!_selected) {
                        this.setRenderState(_textureStates[DOWN]);
                        this.updateRenderState();
                    }
                    _state = DOWN;
                }
            } else {
                // button is up
                if (_state == DOWN) {
                    // Fire action. here
                    retval = true;
                    if (!_selected) {
                        this.setRenderState(_textureStates[3]);
                        this.updateRenderState();
                        _selected = true;
                        _state = OVER;
                    } else {
                        _selected = false;
                    }
                }
                if (!_selected && _state != OVER) {
                    this.setRenderState(_textureStates[OVER]);
                    this.updateRenderState();
                    _state = OVER;
                }
            }
        } else {
            if (!_selected && _state != UP) {
                this.setRenderState(_textureStates[UP]);
                this.updateRenderState();
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
        this.setRenderState(_textureStates[_state]);
        this.updateRenderState();
      }
    }
}
