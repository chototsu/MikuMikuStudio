/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.widget.button;

import com.jme.renderer.Renderer;

import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.text.WidgetLabel;

/**
 * <code>WidgetButton</code>
 * @author Gregg Patton
 * @version $Id: WidgetButton.java,v 1.6 2004-03-04 03:27:05 greggpatton Exp $
 */
public class WidgetButton extends WidgetLabel {

    protected WidgetButtonStateType buttonState = WidgetButtonStateType.BUTTON_UP;

    public WidgetButton() {
        super("");

        init();
    }

    public WidgetButton(String title) {
        this(title, WidgetAlignmentType.ALIGN_SOUTHWEST);
    }

    public WidgetButton(String title, WidgetAlignmentType textAlignment) {
        super(title, textAlignment);

        init();

    }

    private void init() {
        setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST));

        setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.RAISED));
    }

    /* (non-Javadoc)
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {
        
        if (this.buttonState == WidgetButtonStateType.BUTTON_DOWN) {

            for (int i = 0; i < getQuantity(); i++) {
                Widget w = (Widget) getChild(i);

                w.setX(w.getX() + 1);
                w.setY(w.getY() - 1);
            }

        }

        super.onDraw(r);

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.doAlignment(getSize(), getInsets());
        }

    }

    public void doMouseButtonDown() {
        if (this.buttonState != WidgetButtonStateType.BUTTON_DOWN) {

            this.buttonState = WidgetButtonStateType.BUTTON_DOWN;

            if (getBorder() != null)
                setBorder(new WidgetBorder(getBorder(), WidgetBorderType.LOWERED));

            getNotifierMouseButtonDown().notifyObservers(this);

        }
    }

    public void doMouseButtonUp() {
        if (this.buttonState != WidgetButtonStateType.BUTTON_UP) {

            this.buttonState = WidgetButtonStateType.BUTTON_UP;

            if (getBorder() != null)
                setBorder(new WidgetBorder(getBorder(), WidgetBorderType.RAISED));

            if (getMouseOwner() == this)
                getNotifierMouseButtonUp().notifyObservers(this);
        }
    }

    public WidgetButtonStateType getButtonState() {
        return buttonState;
    }

    public void setButtonState(WidgetButtonStateType type) {
        buttonState = type;
    }

    public String toString() {
        return "[" + text.getText() + super.toString() + "]";
    }

}
