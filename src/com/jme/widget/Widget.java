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
package com.jme.widget;

import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewport;
import com.jme.widget.input.mouse.WidgetMouseHandlerInterface;
import com.jme.widget.util.WidgetNotifier;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Widget extends WidgetMouseHandlerInterface {
    public abstract void doParentLayout();
    public abstract WidgetBorder getBorder();
    public abstract void setBorder(WidgetBorder border);
    public abstract void setLocation(Vector2f at);
    public abstract void setLocation(int x, int y);
    public abstract Vector2f getLocation();
    public abstract void setSize(Vector2f size);
    public abstract void setSize(int width, int height);
    public abstract Vector2f getSize();
    public abstract Vector2f getPreferredSize();
    public abstract void setPreferredSize(Vector2f size);
    public abstract void setPreferredSize(int width, int height);
    public abstract int getX();
    public abstract void setX(int x);
    public abstract int getY();
    public abstract void setY(int y);
    public abstract int getWidth();
    public abstract void setWidth(int width);
    public abstract int getHeight();
    public abstract void setHeight(int height);
	public WidgetBoundingRectangle getExtents();
    public abstract void setVisible(boolean b);
    public abstract boolean isVisible();
    public abstract boolean isOpaque();
    public abstract void setWidgetParent(WidgetContainerAbstract parent);
    public abstract WidgetContainerAbstract getWidgetParent();
    public abstract Vector2f getAbsoluteLocation();
    public abstract void setApplyOffsetX(boolean b);
    public abstract boolean isApplyOffsetX();
    public abstract int getXOffset();
	public abstract void setApplyOffsetY(boolean b);
	public abstract boolean isApplyOffsetY();
    public abstract int getYOffset();
    public abstract int getZOrder();
    public abstract void setZOrder(int i);
    public abstract boolean isMouseInWidget();
    public abstract WidgetAlignmentType getAlignment();
    public abstract void setAlignment(WidgetAlignmentType alignment);
    public abstract void doAlignment(Vector2f size, WidgetInsets insets);
    public abstract void close();
    public abstract boolean canClose();
    public abstract ColorRGBA getBgColor();
    public abstract void setBgColor(ColorRGBA colorRGBA);
    public abstract ColorRGBA getFgColor();
    public abstract void setFgColor(ColorRGBA colorRGBA);
    public abstract boolean isCantOwnMouse();
    public abstract void setCantOwnMouse(boolean b);
    public abstract WidgetViewport getViewport();
    public abstract void setViewport(WidgetViewport viewport);
    public abstract Widget getOwner();
    public abstract void setOwner(Widget owner);
    public abstract WidgetNotifier getNotifierMouseButtonDown();
    public abstract WidgetNotifier getNotifierMouseButtonUp();
    public abstract WidgetNotifier getNotifierMouseDrag();
    public abstract WidgetNotifier getNotifierMouseEnter();
    public abstract WidgetNotifier getNotifierMouseExit();
    public abstract WidgetNotifier getNotifierMouseMove();
    public abstract Widget getMouseOwner();
    public abstract void setMouseOwner(Widget widget);


}