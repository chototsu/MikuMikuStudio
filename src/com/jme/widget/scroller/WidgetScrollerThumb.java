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
package com.jme.widget.scroller;

import com.jme.math.Vector2f;

import com.jme.widget.button.WidgetButton;
import com.jme.widget.button.WidgetButtonStateType;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetScrollerThumb extends WidgetButton {

	private Vector2f preferredSize = new Vector2f();
	
    /**
     * 
     */
    public WidgetScrollerThumb() {
        super();
    }

    public void doMouseButtonDown() {
        if (this.buttonState != WidgetButtonStateType.BUTTON_DOWN) {

            this.buttonState = WidgetButtonStateType.BUTTON_DOWN;
            getNotifierMouseButtonDown().notifyObservers(this);

        }
    }

    public void doMouseButtonUp() {
        if (this.buttonState != WidgetButtonStateType.BUTTON_UP) {

            this.buttonState = WidgetButtonStateType.BUTTON_UP;
            
            if (getMouseOwner() == this)
                getNotifierMouseButtonUp().notifyObservers(this);
        }
    }

    public void doMouseDrag() {
        getNotifierMouseDrag().notifyObservers(this);
    }

    public Vector2f getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(int width, int height) {
        preferredSize.x = width;
        preferredSize.y = height;
    }

    public void setPreferredSize(Vector2f size) {
        preferredSize.x = size.x;
        preferredSize.y = size.y;
   }

}
