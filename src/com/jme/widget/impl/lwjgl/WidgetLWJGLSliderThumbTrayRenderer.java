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
import com.jme.widget.WidgetAbstractImpl;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.slider.WidgetSliderThumbTray;

/**
 * <code>WidgetLWJGLSliderThumbTrayRenderer</code>
 * @author Gregg Patton
 * @version $Id: WidgetLWJGLSliderThumbTrayRenderer.java,v 1.1 2004-03-04 03:28:42 greggpatton Exp $
 */
public class WidgetLWJGLSliderThumbTrayRenderer extends WidgetLWJGLAbstractRenderer {

    /**
     * @param w
     */
    public WidgetLWJGLSliderThumbTrayRenderer(Widget w) {
        super(w);
    }

    /** <code>render</code> 
     * 
     * @see com.jme.widget.WidgetRenderer#render()
     */
    public void render() {

        WidgetSliderThumbTray wstt = (WidgetSliderThumbTray) getWidget();

        //Draw the slider thumb track
        initWidgetProjection(wstt);

        int trackInset = (int) (wstt.getThumbSize() - 4);

        int left = wstt.getX() + wstt.getXOffset();
        int bottom = wstt.getY() + wstt.getYOffset();

        int right = left + wstt.getWidth();
        int top = bottom + wstt.getHeight();

        left += trackInset;
        bottom += trackInset;
        right -= trackInset;
        top -= trackInset;

        WidgetBorder border = new WidgetBorder(1, 1, 1, 1, WidgetBorderType.LOWERED);
        
        drawBox2d(top, left, bottom, right, border, WidgetAbstractImpl.defaultBgColor, null);
        drawLoweredBoxBorder2d(top, left, bottom, right, border);

        resetWidgetProjection();
    }

}
