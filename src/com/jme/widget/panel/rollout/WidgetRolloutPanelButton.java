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
package com.jme.widget.panel.rollout;

import com.jme.math.Vector2f;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.button.WidgetButton;

/**
 * @author pattogo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class WidgetRolloutPanelButton extends WidgetButton {

	private int rolloutPanelWidth;

    public WidgetRolloutPanelButton() {
        super();
        init();
    }

    public WidgetRolloutPanelButton(String title) {
        super(title);
		init();
    }

    public WidgetRolloutPanelButton(String title, WidgetAlignmentType textAlignment) {
        super(title, textAlignment);
		init();
    }

	private void init() {
		setApplyOffsetX(false);
		getText().setApplyOffsetX(false);
		getText().setForceView(true);
	}

    public int getRolloutPanelWidth() {
        return rolloutPanelWidth;
    }

    public void setRolloutPanelWidth(int i) {
        rolloutPanelWidth = i;
		super.setWidth(rolloutPanelWidth);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setSize(int, int)
     */
    public void setSize(int width, int height) {
        super.setSize(rolloutPanelWidth, height);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setSize(com.jme.math.Vector2f)
     */
    public void setSize(Vector2f size) {
        super.setSize(rolloutPanelWidth, (int) size.y);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setWidth(int)
     */
    public void setWidth(int width) {
        super.setWidth(rolloutPanelWidth);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setViewRectangle(jme.widget.bounds.WidgetViewRectangle)
     */
    public void setViewRectangle(WidgetViewRectangle viewRect) {
		WidgetViewRectangle v = new WidgetViewRectangle(viewRect);
		
		v.setWidth(getWidgetParent().getViewRectangle().getWidth());
		
        super.setViewRectangle(v);
    }

}
