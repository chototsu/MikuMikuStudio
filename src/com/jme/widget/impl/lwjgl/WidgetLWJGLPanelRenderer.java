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
package com.jme.widget.impl.lwjgl;

import com.jme.widget.Widget;
import com.jme.widget.panel.WidgetPanel;

/**
 * <code>WidgetLWJGLPanelRenderer</code>
 * @author Gregg Patton
 * @version $Id: WidgetLWJGLPanelRenderer.java,v 1.2 2004-04-16 20:47:23 renanse Exp $
 */
public class WidgetLWJGLPanelRenderer extends WidgetLWJGLAbstractRenderer {

    /**
     * @param s
     */
    public WidgetLWJGLPanelRenderer(Widget w) {
        super(w);
    }

    /** <code>render</code>
     *
     * @see com.jme.widget.WidgetRenderer#render()
     */
    public void render() {
        WidgetPanel wp = (WidgetPanel) getWidget();

        drawBox2d(wp, null);
        drawBoxBorder2d(wp);
    }

}
