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
 * Created on Jul 26, 2004
 *
 */
package com.jme.ui;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * UICharacter is used by UIText to create text strings on screen.
 * Each UICharacter is unique and a part of the whole font file texture.
 *
 * @author schustej
 *
 */
public class UICharacter extends UIObject {

    /**
     * Constructs a single character UIObject based on a sub-texture location
     * for the needed character. tx,ty and tx2,ty2 are the texture coordinates
     * of the corners of the quad
     * @param name unique
     * @param ts the texture state that contains the texture that has the characters
     * @param tx
     * @param ty
     * @param tx2
     * @param ty2
     * @param scale
     */
    public UICharacter(String name, TextureState ts, float tx, float ty, float tx2, float ty2, float scale) {
        super(name, null, 0, 0, scale);

        _textureStates = new TextureState[1];
        _textureStates[0] = ts;

        setup();

        Vector2f[] texCoords = new Vector2f[4];

        texCoords[0] = new Vector2f(tx, ty2);
        texCoords[1] = new Vector2f(tx, ty);
        texCoords[2] = new Vector2f(tx2, ty);
        texCoords[3] = new Vector2f(tx2, ty2);

        setTextures(texCoords);

        _xscale = scale;
        _yscale = scale;
    }

    /**
     * Copy contstructor, this is used when UIText needs to make a copy of a character
     * for actual rendering. The only difference b/t one character and another will be the
     * location.
     * We should convert this to using Clones if it makes sense.
     * @param tmp
     */
    public UICharacter(String name, UICharacter tmp) {
        super( name + tmp.getName(), null, 0, 0, tmp._xscale, tmp._yscale);

        this._textureStates = tmp._textureStates;
        this._width = tmp._width;
        this._height = tmp._height;
        this._x = tmp._x;
        this._y = tmp._y;
        this.renderStateList = tmp.renderStateList;

        setVertices(tmp.getVertices());
        setNormals(tmp.getNormals());
        setColors(tmp.getColors());
        setTextures(tmp.getTextures());
        setIndices(tmp.getIndices());

        getLocalScale().x = _xscale;
        getLocalScale().y = _yscale;
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Specialized override of UIObject setup that
     * accounts for the 1/16 size of the subtexture
     */
    protected void setup() {

        _width = ((TextureState) _textureStates[0]).getTexture().getImage().getWidth() / 16;
        _height = ((TextureState) _textureStates[0]).getTexture().getImage().getHeight() / 16;

        initialize(_width, _height);

        getLocalScale().x = _xscale;
        getLocalScale().y = _yscale;

        /*
         * doesn't seem to work right. It ends up being in the wrong place.
         */
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Empty
     */
    public boolean update() {
        return false;
    }
}
