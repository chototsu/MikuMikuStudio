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
package com.jme.widget.renderer;

import java.util.logging.Level;

import com.jme.renderer.RendererType;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.widget.WidgetRenderer;
import com.jme.widget.image.WidgetImage;
import com.jme.widget.impl.lwjgl.WidgetLWJGLImage;
import com.jme.widget.impl.lwjgl.WidgetLWJGLPanelRenderer;
import com.jme.widget.impl.lwjgl.WidgetLWJGLScrollerButtonRenderer;
import com.jme.widget.impl.lwjgl.WidgetLWJGLSliderThumbRenderer;
import com.jme.widget.impl.lwjgl.WidgetLWJGLSliderThumbTrayRenderer;
import com.jme.widget.impl.lwjgl.WidgetLWJGLText;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.scroller.WidgetScrollerButton;
import com.jme.widget.slider.WidgetSliderThumb;
import com.jme.widget.slider.WidgetSliderThumbTray;
import com.jme.widget.text.WidgetText;
/**
 * <code>WidgetRendererFactory</code>
 * @author Gregg Patton
 * @version $Id: WidgetRendererFactory.java,v 1.3 2004-04-25 16:58:22 mojomonkey Exp $
 */
public final class WidgetRendererFactory {
    private static String UNKNOWN_RENDERER_TYPE = "Unknown RendererType";
    private static RendererType rendererType;

    private static WidgetRendererFactory factory;

    private WidgetRendererFactory() {}

    /**
     * <code>getFactory</code>
     * @return
     */
    public static WidgetRendererFactory getFactory() {

        if (factory == null) {
            factory = new WidgetRendererFactory();

            WidgetRendererFactory.rendererType = DisplaySystem.getDisplaySystem().getRendererType();
        }

        return factory;
    }

    /**
     * <code>getRenderer</code>
     * @param wp
     * @return
     */
    public WidgetRenderer getRenderer(WidgetPanel wp) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLPanelRenderer(wp);
            renderer.setWidget(wp);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }

    /**
     * <code>getRenderer</code>
     * @param text
     * @return
     */
    public WidgetRenderer getRenderer(WidgetText text) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLText(text);
            renderer.setWidget(text);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }

    /**
     * <code>getRenderer</code>
     * @param wsb
     * @return
     */
    public WidgetRenderer getRenderer(WidgetScrollerButton wsb) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLScrollerButtonRenderer(wsb);
            renderer.setWidget(wsb);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }

    /**
     * <code>getRenderer</code>
     * @param wsb
     * @return
     */
    public WidgetRenderer getRenderer(WidgetSliderThumb wst) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLSliderThumbRenderer(wst);
            renderer.setWidget(wst);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }

    /**
     * <code>getRenderer</code>
     * @param wp
     * @return
     */
    public WidgetRenderer getRenderer(WidgetSliderThumbTray wst) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLSliderThumbTrayRenderer(wst);
            renderer.setWidget(wst);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }
    
    /**
     * <code>getRenderer</code>
     * @param wi
     * @return
     */
    public WidgetRenderer getRenderer(WidgetImage wi) {
        WidgetRenderer renderer = null;

        if (rendererType == RendererType.LWJGL) {
            renderer = new WidgetLWJGLImage(wi);
            renderer.setWidget(wi);
        } else {
            LoggingSystem.getLogger().log(Level.SEVERE, UNKNOWN_RENDERER_TYPE);
            throw new JmeException(UNKNOWN_RENDERER_TYPE);
        }

        return renderer;
    }

}
