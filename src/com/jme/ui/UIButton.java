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
import com.jme.math.Vector3f;

/**
 * UIButton is a UIObject derived class that uses mouse input to change out the
 * texture that is shown on the button
 * 
 * @author schustej
 *  
 */
public class UIButton extends UIObject {

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
     * @param inputHandler
     * @param upfile
     * @param overfile
     * @param downfile
     * @param x
     * @param y
     * @param scale
     */
    public UIButton(String name, InputHandler inputHandler, String upfile, String overfile, String downfile,
            int x, int y, float scale) {
        this(name, inputHandler, upfile, overfile, downfile, x, y, scale, true);
    }

    /**
     * Difference allows for the images to be loaded directly from the file
     * system instead of via the classloader
     * 
     * @param name
     * @param inputHandler
     * @param upfile
     * @param overfile
     * @param downfile
     * @param x
     * @param y
     * @param scale
     * @param useClassloader
     */
    public UIButton(String name, InputHandler inputHandler, String upfile, String overfile, String downfile,
            int x, int y, float scale, boolean useClassloader) {
        super(name, inputHandler, x, y, scale);

        _textureStates = new TextureState[3];

        for (int i = 0; i < 3; i++) {
            _textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
            _textureStates[i].setEnabled(true);
        }

        if (useClassloader) {
            _textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
                    .getResource(upfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
            _textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
                    .getResource(overfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
            _textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class.getClassLoader()
                    .getResource(downfile), Texture.MM_NEAREST, Texture.FM_NEAREST, true));
        } else {
            _textureStates[0].setTexture(TextureManager.loadTexture(upfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
            _textureStates[1].setTexture(TextureManager.loadTexture(overfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
            _textureStates[2].setTexture(TextureManager.loadTexture(downfile, Texture.MM_NEAREST,
                    Texture.FM_NEAREST, true));
        }

        _textureStates[0].apply();
        _textureStates[1].apply();
        _textureStates[2].apply();

        this.setRenderState(_textureStates[0]);

        setup();
    }

    /**
     * Needs to be called during the update cycle to allow for
     * the mouse to be checked for hit test and mouse button state.
     * 
     */
    public boolean update() {

        boolean retval = false;

        if (hitTest()) {

            if (_inputHandler.getMouse().getMouseInput().getButtonState().equals(
                    MouseButtonStateType.MOUSE_BUTTON_1)) {
                // button is down
                if (_state != DOWN) {
                    this.setRenderState(_textureStates[DOWN]);
                    this.updateRenderState();
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
                    this.setRenderState(_textureStates[OVER]);
                    this.updateRenderState();
                    _state = OVER;
                    fireActions();
                }
            }
        } else {
            if (_state != UP) {
                this.setRenderState(_textureStates[UP]);
                this.updateRenderState();
                _state = UP;
                fireActions();
            }
        }
        return retval;
    }

}