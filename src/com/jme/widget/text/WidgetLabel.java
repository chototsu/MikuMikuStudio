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
package com.jme.widget.text;

import com.jme.renderer.ColorRGBA;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.panel.WidgetPanel;

/**
 * <code>WidgetLabel</code>
 * @author Gregg Patton
 * @version
 */
public class WidgetLabel extends WidgetPanel {

    protected WidgetText text;

    public WidgetLabel(String title) {
        this(title, WidgetAlignmentType.ALIGN_SOUTHWEST);
    }

    public WidgetLabel(WidgetAlignmentType textAlignment) {
        this("", textAlignment);
    }

    public WidgetLabel(String title, WidgetAlignmentType textAlignment) {
        super();

        init(title, textAlignment);
    }

    private void init(String title, WidgetAlignmentType textAlignment) {
        text = new WidgetText(title);
        text.setAlignment(textAlignment);
        text.setCantOwnMouse(true);
        text.setFgColor(this.getFgColor());

        add(text);

    }

    public String getTitle() {
        return text.getText();
    }

    public void setTitle(String title) {
        text.setText(title);
    }

    public WidgetAlignmentType getTextAlignment() {
        return text.getAlignment();
    }

    public void setTextAlignment(WidgetAlignmentType alignment) {
        text.setAlignment(alignment);
    }

    public WidgetText getText() {
        return text;
    }

    public void setText(WidgetText text) {
        this.text = text;
    }

    public void setFgColor(ColorRGBA colorRGBA) {
        super.setFgColor(colorRGBA);
        if (text != null) {
            text.setFgColor(colorRGBA);
        }
    }


}
