/* 
* Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding 
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
package com.jme.widget.renderer;

import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.widget.Widget;
import com.jme.widget.WidgetRenderer;

/**
 * <code>WidgetAbstractRenderer</code>
 * @author Gregg Patton
 * @version $Id: WidgetAbstractRenderer.java,v 1.2 2004-03-05 11:38:14 greggpatton Exp $
 */
public abstract class WidgetAbstractRenderer implements WidgetRenderer {

    public static float LOOK_AND_FEEL_TEXTURE_SIZE = 64;
    public static float LOOK_AND_FEEL_PIXEL_SIZE = 1f / LOOK_AND_FEEL_TEXTURE_SIZE;

    private static String IMAGE_DIRECTORY = "jmetest/data/lookandfeel/";

    protected Widget widget;
    protected Renderer renderer;

    static protected TextureState textureState;
    static protected AlphaState alphaState;

    /**
     * 
     */
    public WidgetAbstractRenderer(Widget w) {
        super();
        widget = w;
        renderer = DisplaySystem.getDisplaySystem().getRenderer();

        if (textureState == null) {
            textureState = DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
            alphaState = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();

            alphaState.setBlendEnabled(true);

            Texture t =
                TextureManager.loadTexture(
                    WidgetAbstractRenderer.class.getClassLoader().getResource(IMAGE_DIRECTORY + "DefaultLookAndFeel.png"),
                    Texture.MM_NONE,
                    Texture.MM_NONE,
                    false);

            t.setApply(Texture.AM_REPLACE);
            textureState.setTexture(t);

        }

    }

    /** <code>setWidget</code> 
     * @param w
     * @see com.jme.widget.WidgetRenderer#setWidget(com.jme.widget.Widget)
     */
    public void setWidget(Widget w) {
        widget = w;
    }

    /** <code>getWidget</code> 
     * @return
     * @see com.jme.widget.WidgetRenderer#getWidget()
     */
    public Widget getWidget() {
        return widget;
    }

    /**
     * <code>updateCamera</code>
     * 
     */
    protected void updateCamera() {
        if (renderer != null && renderer.getCamera() != null) {
            renderer.getCamera().update();
        }
    }
}
