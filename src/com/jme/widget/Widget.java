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
package com.jme.widget;

import com.jme.input.AbstractInputController;
import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.input.mouse.WidgetMouseHandlerInterface;
import com.jme.widget.util.WidgetNotifier;

/**
 * <code>Widget</code>
 * @author Gregg Patton
 * @version $Id: Widget.java,v 1.3 2004-02-09 12:24:46 greggpatton Exp $
 */
public interface Widget extends WidgetMouseHandlerInterface {
    
    /**
     * <code>doParentLayout</code>
     * 
     */
    public abstract void doParentLayout();
    
    /**
     * <code>getBorder</code>
     * @return
     */
    public abstract WidgetBorder getBorder();
    
    /**
     * <code>setBorder</code>
     * @param border
     */
    public abstract void setBorder(WidgetBorder border);
    
    /**
     * <code>setLocation</code>
     * @param at
     */
    public abstract void setLocation(Vector2f at);
    
    /**
     * <code>setLocation</code>
     * @param x
     * @param y
     */
    public abstract void setLocation(int x, int y);
    
    /**
     * <code>getLocation</code>
     * @return
     */
    public abstract Vector2f getLocation();
    
    /**
     * <code>setSize</code>
     * @param size
     */
    public abstract void setSize(Vector2f size);
    
    /**
     * <code>setSize</code>
     * @param width
     * @param height
     */
    public abstract void setSize(int width, int height);
    
    /**
     * <code>getSize</code>
     * @return
     */
    public abstract Vector2f getSize();
    
    /**
     * <code>getPreferredSize</code>
     * @return
     */
    public abstract Vector2f getPreferredSize();
    
    /**
     * <code>setPreferredSize</code>
     * @param size
     */
    public abstract void setPreferredSize(Vector2f size);
    
    /**
     * <code>setPreferredSize</code>
     * @param width
     * @param height
     */
    public abstract void setPreferredSize(int width, int height);
    
    /**
     * <code>getX</code>
     * @return
     */
    public abstract int getX();
    
    /**
     * <code>setX</code>
     * @param x
     */
    public abstract void setX(int x);
    
    /**
     * <code>getY</code>
     * @return
     */
    public abstract int getY();
    
    /**
     * <code>setY</code>
     * @param y
     */
    public abstract void setY(int y);
    
    /**
     * <code>getWidth</code>
     * @return
     */
    public abstract int getWidth();
    
    /**
     * <code>setWidth</code>
     * @param width
     */
    public abstract void setWidth(int width);
    
    /**
     * <code>getHeight</code>
     * @return
     */
    public abstract int getHeight();
    
    /**
     * <code>setHeight</code>
     * @param height
     */
    public abstract void setHeight(int height);
    
	/**
     * <code>getExtents</code>
     * @return
     */
    public abstract WidgetBoundingRectangle getExtents();
    
    /**
     * <code>setVisible</code>
     * @param b
     */
    public abstract void setVisible(boolean b);
    
    /**
     * <code>isVisible</code>
     * @return
     */
    public abstract boolean isVisible();
    
    /**
     * <code>isOpaque</code>
     * @return
     */
    public abstract boolean isOpaque();
    
    /**
     * <code>setWidgetParent</code>
     * @param parent
     */
    public abstract void setWidgetParent(WidgetAbstractContainer parent);
    
    /**
     * <code>getWidgetParent</code>
     * @return
     */
    public abstract WidgetAbstractContainer getWidgetParent();
    
    /**
     * <code>getAbsoluteLocation</code>
     * @return
     */
    public abstract Vector2f getAbsoluteLocation();
    
    /**
     * <code>setApplyOffsetX</code>
     * @param b
     */
    public abstract void setApplyOffsetX(boolean b);
    
    /**
     * <code>isApplyOffsetX</code>
     * @return
     */
    public abstract boolean isApplyOffsetX();
    
    /**
     * <code>getXOffset</code>
     * @return
     */
    public abstract int getXOffset();
    
	/**
     * <code>setApplyOffsetY</code>
     * @param b
     */
    public abstract void setApplyOffsetY(boolean b);
    
	/**
     * <code>isApplyOffsetY</code>
     * @return
     */
    public abstract boolean isApplyOffsetY();
    
    /**
     * <code>getYOffset</code>
     * @return
     */
    public abstract int getYOffset();
    
    /**
     * <code>getZOrder</code>
     * @return
     */
    public abstract int getZOrder();
    
    /**
     * <code>setZOrder</code>
     * @param i
     */
    public abstract void setZOrder(int i);
    
    /**
     * <code>isMouseInWidget</code>
     * @return
     */
    public abstract boolean isMouseInWidget();
    
    /**
     * <code>getAlignment</code>
     * @return
     */
    public abstract WidgetAlignmentType getAlignment();
    
    /**
     * <code>setAlignment</code>
     * @param alignment
     */
    public abstract void setAlignment(WidgetAlignmentType alignment);
    
    /**
     * <code>doAlignment</code>
     * @param size
     * @param insets
     */
    public abstract void doAlignment(Vector2f size, WidgetInsets insets);
    
    /**
     * <code>close</code>
     * 
     */
    public abstract void close();
    
    /**
     * <code>canClose</code>
     * @return
     */
    public abstract boolean canClose();
    
    /**
     * <code>getBgColor</code>
     * @return
     */
    public abstract ColorRGBA getBgColor();
    
    /**
     * <code>setBgColor</code>
     * @param colorRGBA
     */
    public abstract void setBgColor(ColorRGBA colorRGBA);
    
    /**
     * <code>getFgColor</code>
     * @return
     */
    public abstract ColorRGBA getFgColor();
    
    /**
     * <code>setFgColor</code>
     * @param colorRGBA
     */
    public abstract void setFgColor(ColorRGBA colorRGBA);
    
    /**
     * <code>isCantOwnMouse</code>
     * @return
     */
    public abstract boolean isCantOwnMouse();
    
    /**
     * <code>setCantOwnMouse</code>
     * @param b
     */
    public abstract void setCantOwnMouse(boolean b);
    
    /**
     * <code>getViewRectangle</code>
     * @return
     */
    public abstract WidgetViewRectangle getViewRectangle();
    
    /**
     * <code>setViewRectangle</code>
     * @param viewRectangle
     */
    public abstract void setViewRectangle(WidgetViewRectangle viewRectangle);
    
    /**
     * <code>getOwner</code>
     * @return
     */
    public abstract Widget getOwner();
    
    /**
     * <code>setOwner</code>
     * @param owner
     */
    public abstract void setOwner(Widget owner);
    
    /**
     * <code>getNotifierMouseButtonDown</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseButtonDown();
    
    /**
     * <code>getNotifierMouseButtonUp</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseButtonUp();
    
    /**
     * <code>getNotifierMouseDrag</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseDrag();
    
    /**
     * <code>getNotifierMouseEnter</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseEnter();
    
    /**
     * <code>getNotifierMouseExit</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseExit();
    
    /**
     * <code>getNotifierMouseMove</code>
     * @return
     */
    public abstract WidgetNotifier getNotifierMouseMove();
    
    /**
     * <code>getMouseOwner</code>
     * @return
     */
    public abstract Widget getMouseOwner();
    
    /**
     * <code>setMouseOwner</code>
     * @param widget
     */
    public abstract void setMouseOwner(Widget widget);
    
    /**
     * <code>getWidgetUnderMouse</code>
     * @return
     */
    public abstract Widget getWidgetUnderMouse();

    /**
     * <code>setWidgetUnderMouse</code>
     * @param widget
     */
    public abstract void setWidgetUnderMouse(Widget widget);

    /**
     * <code>getLastWidgetUnderMouse</code>
     * @return
     */
    public abstract Widget getLastWidgetUnderMouse();

    /**
     * <code>setLastWidgetUnderMouse</code>
     * @param widget
     */
    public abstract void setLastWidgetUnderMouse(Widget widget);

    /**
     * <code>getMouseInput</code>
     * @return
     */
    public abstract MouseInput getMouseInput();
    
    /**
     * <code>getInputController</code>
     * @return
     */
    public abstract AbstractInputController getInputController();
    
    /**
     * <code>setInputController</code>
     * @param controller
     */
    public abstract void setInputController(AbstractInputController controller);
    
}