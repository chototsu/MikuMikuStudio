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
 * Created on Jun 16, 2004
 *
 */
package com.jme.ui;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * UIObject based class that displays an image on the screen in a Orthogonal
 * way. Also has a couple of convienience methods to center the image in the
 * screen and to change the width and height on the screen.
 * 
 * The image file that is specified still needs to be sized by the standard
 * texture power of 2 rule.
 * 
 * @author schustej
 *  
 */
public class UIBillboard extends UIObject {

    /**
     * Specific the image file to be shown. Just like all objects in jME, name
     * it with a unique name.
     *  
     */
    public UIBillboard(String name, int x, int y, int width, int height, String imgfile) {
        this(name, x, y, width, height, null, imgfile, UIObject.TEXTURE, true);
    }

    public UIBillboard(String name, int x, int y, int width, int height, UIColorScheme scheme) {
        this(name, x, y, width, height, scheme, null, UIObject.BORDER, true);
    }

    /**
     */
    public UIBillboard(String name, int x, int y, int width, int height, UIColorScheme scheme,
            String imgfile, int flags, boolean useClassLoader) {
        super(name, x, y, width, height, scheme, flags);

        if ((TEXTURE & _flags) == TEXTURE) {
            TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
            ts.setEnabled(true);

            if (useClassLoader) {
                ts.setTexture(TextureManager.loadTexture(
                        UIObject.class.getClassLoader().getResource(imgfile), Texture.MM_NEAREST,
                        Texture.FM_NEAREST, true));
            } else {
                ts.setTexture(TextureManager.loadTexture(imgfile, Texture.MM_NEAREST, Texture.FM_NEAREST,
                        true));
            }
            ts.apply();

            _textureStates.add(ts);
        }

        setup();

    }

    /**
     * Easy method that will center the image within the display.
     *  
     */
    public void center() {
        _x = DisplaySystem.getDisplaySystem().getWidth() / 2 - _width / 2;
        _y = DisplaySystem.getDisplaySystem().getHeight() / 2 - _height / 2;
        setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2, 0.0f));
    }

    /**
     * Used to set the wrapping parameters. Use Texture.WM_* wrapping
     * parameters.
     */
    public void setWrap(int wrap) {
        if ((TEXTURE & _flags) == TEXTURE)
            ((TextureState) _textureStates.elementAt(0)).getTexture().setWrap(wrap);
    }

    /**
     * Non-functionaly method since there is no user interaction with this
     * object
     * @param scale - used for both x and y scale
     */
    public boolean update() {
        return false;
    }
}
