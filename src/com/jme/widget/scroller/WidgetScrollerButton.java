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
package com.jme.widget.scroller;

import com.jme.math.Vector2f;
import com.jme.widget.WidgetExpander;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.renderer.WidgetRendererFactory;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetScrollerButton extends WidgetButton {

    private static final long serialVersionUID = 1L;

	public final static int DEFAULT_SCROLLER_BUTTON_SIZE = 16;

    protected WidgetExpander expander = new WidgetExpander();

    public WidgetScrollerButton() {
        super();

        setWidgetRenderer(WidgetRendererFactory.getFactory().getRenderer(this));
    }

    public Vector2f getPreferredSize() {
        Vector2f d = new Vector2f();

		d.x = (DEFAULT_SCROLLER_BUTTON_SIZE + expander.getLeft() + expander.getRight());
		d.y = (DEFAULT_SCROLLER_BUTTON_SIZE + expander.getTop() + expander.getBottom());

        return d;
    }

    public int getExpandTop() {
        return expander.getTop();
    }

    public void setExpandTop(int top) {
        expander.setTop(top);
    }

    public int getExpandLeft() {
        return expander.getLeft();
    }

    public void setExpandLeft(int left) {
        expander.setLeft(left);
    }

    public int getExpandBottom() {
        return expander.getBottom();
    }

    public void setExpandBottom(int bottom) {
        expander.setLeft(bottom);
    }

    public int getExpandRight() {
        return expander.getRight();
    }

    public void setExpandRight(int right) {
        expander.setRight(right);
    }

    public WidgetExpander getExpander() {
        return new WidgetExpander(expander);
    }

    public void setExpander(WidgetExpander expander) {
        this.expander.set(expander);
    }

}
