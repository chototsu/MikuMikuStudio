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
import com.jme.renderer.Renderer;

/**
 * UICharacter is used by UIText to create text strings on screen.
 * Each UICharacter is unique and a part of the whole font file texture.
 *
 * @author schustej
 *
 */
public class UICharacter extends UIObject {

    private static final long serialVersionUID = 1L;

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
    public UICharacter(String name, 
            float tx, float ty, float tx2, float ty2) {
        super( name, 0, 0, 0, 0, null, UIObject.TEXTURE);
        
        setup();

        Vector2f[] texCoords = new Vector2f[4];

        texCoords[0] = new Vector2f(tx, ty2);
        texCoords[1] = new Vector2f(tx, ty);
        texCoords[2] = new Vector2f(tx2, ty);
        texCoords[3] = new Vector2f(tx2, ty2);

        _quad.setTextures( texCoords);
    }

    /**
     * Copy contstructor, this is used when UIText needs to make a copy of a character
     * for actual rendering. The only difference b/t one character and another will be the
     * location.
     * We should convert this to using Clones if it makes sense.
     * @param tmp
     */
    public UICharacter(String name, UICharacter tmp, 
            int x, int y, int width, int height,
            float scale, UIColorScheme scheme) {
        super( name + tmp.getName(), x, y, width, height, scheme, UIObject.TEXTURE);

        this._textureStates = null;
        this._width = width;
        this._height = height;
        this._x = x;
        this._y = y;
        
        Vector2f[] texCoords = tmp._quad.getTextures();
        
        super.setup();

        //System.out.println( "x: " + _x + " y: " + _y + " w: " + _width + " h:" + _height);
        //System.out.println( _quad.getLocalTranslation());
        
        _quad.setTextures( texCoords);
        
        //getLocalScale().x = _xscale;
        //getLocalScale().y = _yscale;
        
        _quad.setSolidColor( scheme._foregroundcolor);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        
        this.updateGeometricState(0.0f, true);
        this.updateRenderState();
    }

    /**
     * Empty
     */
    public boolean update() {
        return false;
    }
}
