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

import java.util.Observer;
import java.util.logging.Level;

import com.jme.input.InputControllerAbstract;
import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingVolume;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.LoggingSystem;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewport;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.layout.WidgetLayoutManager;
import com.jme.widget.layout.WidgetLayoutManager2;
import com.jme.widget.util.WidgetNotifier;

/**
 * @author pattogo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetContainerAbstract extends Node implements Widget {
    protected Vector2f panOffset = new Vector2f();

    protected WidgetLayoutManager layout;

    protected WidgetImpl widgetImpl = new WidgetImpl();

    public final static int DEFAULT_INSET_SIZE = 0;
    protected WidgetInsets insets = new WidgetInsets(DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE);

    public WidgetContainerAbstract() {
        super();
        init();
    }

    public WidgetContainerAbstract(int width, int height) {
        super();
        widgetImpl.setSize(width, height);
        init();
    }

    private void init() {
        setVisible(true);
    }

    private void update() {
        calcViewport(this);
        updateChildrenViewports();
        updateWorldBound();
    }

    public void add(Widget w) {
        w.setZOrder(getWidgetCount());

        if (w instanceof Spatial) {

            super.attachChild((Spatial) w);

            w.setWidgetParent(this);

            if (layout != null)
                layout.addLayoutWidget(w);

        } else {
            LoggingSystem.getLogger().log(Level.WARNING, "Children must be of type Spatial.");
        }

    }

    public void add(Widget w, Object constraints) {
        w.setZOrder(getWidgetCount());

        Spatial child = null;

        if (w instanceof Spatial) {

            super.attachChild((Spatial) w);

            w.setWidgetParent(this);

            if (layout != null)
                 ((WidgetLayoutManager2) layout).addLayoutWidget(w, constraints);

        } else {
            LoggingSystem.getLogger().log(Level.WARNING, "WidgetContainerAbstract:  Children must be of type Spatial.");
        }

    }

    public int getWidgetCount() {
        return super.getQuantity();
    }

    public WidgetBoundingRectangle getExtents() {

        super.updateWorldBound();

        WidgetBoundingRectangle rect = (WidgetBoundingRectangle) super.getWorldBound();

        rect.subtractMinX(insets.left + widgetImpl.getBorder().left);
        rect.addWidth(insets.left + insets.right + widgetImpl.getBorder().left + widgetImpl.getBorder().right);

        rect.subtractMinY(insets.bottom + widgetImpl.getBorder().bottom);
        rect.addHeight(insets.top + insets.bottom + widgetImpl.getBorder().top + widgetImpl.getBorder().bottom);

        return rect;
    }

    public Widget getWidget(int n) {
        if (n < super.getQuantity())
            return (Widget) super.getChild(n);
        else
            return null;
    }

    public Vector2f getPreferredSize() {
        if (layout == null)
            setLayout(new WidgetFlowLayout());

        return layout.preferredLayoutSize(this);
    }

    public void setLayout(WidgetLayoutManager layout) {
        this.layout = layout;
    }

    public WidgetLayoutManager getLayout() {
        return layout;
    }

    public void setInsets(WidgetInsets insets) {
        this.insets.set(insets);
    }

    public WidgetInsets getInsets() {
        return new WidgetInsets(insets);
    }

    public void doLayout() {
        if (layout == null)
            setLayout(new WidgetFlowLayout());

        layout.layoutContainer(this);

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);

            if (w instanceof WidgetContainerAbstract)
                 ((WidgetContainerAbstract) w).doLayout();

            w.doAlignment(getSize(), getInsets());
        }

        updateChildrenViewports();
        updateWorldBound();

    }

    public void updateChildrenViewports() {
        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            calcViewport(w);

            if (w instanceof WidgetContainerAbstract) {
                ((WidgetContainerAbstract) w).updateChildrenViewports();
            }
        }

    }

    public void pack() {
        setSize(getPreferredSize());
    }

    public void remove(int w) {
        if (w > 0 && w < super.getQuantity()) {
            super.detachChildAt(w);
            doLayout();
        }
    }

    public void remove(Widget w) {
        if (w instanceof Spatial) {

            super.detachChild((Spatial) w);

        } else {
            LoggingSystem.getLogger().log(Level.WARNING, "WidgetContainerAbstract:  Children must be of type Spatial.");
        }

    }

    public void removeAll() {
        while (super.getQuantity() != 0)
            super.detachChildAt(0);
    }

    public void dispose() {
        removeAll();
    }

    protected void calcViewport(Widget w) {
        if (layout != null)
            layout.calcViewport(w);
    }

    public Widget getMouseOwner() {
        return widgetImpl.getMouseOwner();
    }

    public void setMouseOwner(Widget widget) {
        widgetImpl.setMouseOwner(widget);
    }

    /**
     * @see com.jme.widget.Widget#getMouseInput()
     */
    public MouseInput getMouseInput() {
        return widgetImpl.getMouseInput();
    }

    /**
     * @see com.jme.widget.Widget#getInputController()
     */
    public InputControllerAbstract getInputController() {
        return widgetImpl.getInputController();
    }

    /** 
     * @see com.jme.widget.Widget#setInputController(com.jme.input.InputController)
     */
    public void setInputController(InputControllerAbstract controller) {
        widgetImpl.setInputController(controller);
        
    }

    public void handleMouseButtonDown() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseButtonDown();

        if (getMouseOwner() == widgetImpl) {
            setMouseOwner(this);
        }

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseButtonDown();
        }

    }

    public void handleMouseButtonUp() {
        if (isVisible() == false)
            return;

        Widget mouseOwner = getMouseOwner();

        widgetImpl.handleMouseButtonUp();

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseButtonUp();
        }

    }

    public void handleMouseMove() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseMove();

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseMove();
        }
    }

    public void handleMouseDrag() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseDrag();

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseDrag();
        }
    }

    public void handleMouseEnter() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseEnter();

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseEnter();
        }
    }

    public void handleMouseExit() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseExit();

        for (int i = 0; i < getQuantity(); i++) {
            Widget w = (Widget) getChild(i);
            w.handleMouseExit();
        }

    }

    public Vector2f getPanOffset() {
        Vector2f ret = new Vector2f();

        ret.x = panOffset.x;
        ret.y = panOffset.y;

        return ret;
    }

    public float getPanOffsetX() {
        return panOffset.x;
    }

    public float getPanOffsetY() {
        return panOffset.y;
    }

    public void setPanOffset(Vector2f l) {
        setPanOffset((int) l.x, (int) l.y);
    }

    public void setPanOffset(int x, int y) {
        panOffset.x = x;
        panOffset.y = y;
        updateChildrenViewports();
    }

    public void setPanXOffset(int x) {
        panOffset.x = x;
        updateChildrenViewports();
    }

    public void setPanYOffset(int y) {
        panOffset.y = y;
        updateChildrenViewports();
    }

    public boolean contains(Widget w) {
        boolean ret = false;

        for (int i = 0; ret == false && i < getQuantity(); i++) {
            Widget c = (Widget) getChild(i);
            ret = (w == c);
        }

        return ret;
    }

    /*
     **************************************************************
     * Widget2d implementation - start
     **************************************************************
     */

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getBorder()
     */
    public WidgetBorder getBorder() {
        return widgetImpl.getBorder();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setBorder(jme.widget.border.Border)
     */
    public void setBorder(WidgetBorder border) {
        widgetImpl.setBorder(border);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setLocation(jme.math.point.Point2)
     */
    public void setLocation(Vector2f at) {
        widgetImpl.setLocation(at);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setLocation(int, int)
     */
    public void setLocation(int x, int y) {
        widgetImpl.setLocation(x, y);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getLocation()
     */
    public Vector2f getLocation() {
        return widgetImpl.getLocation();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setSize(jme.math.point.Point2)
     */
    public void setSize(Vector2f size) {
        widgetImpl.setSize(size);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setSize(int, int)
     */
    public void setSize(int width, int height) {
        widgetImpl.setSize(width, height);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getSize()
     */
    public Vector2f getSize() {
        return widgetImpl.getSize();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setPreferredSize(jme.math.point.Point2)
     */
    public void setPreferredSize(Vector2f size) {
        widgetImpl.setPreferredSize(size);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setPreferredSize(int, int)
     */
    public void setPreferredSize(int width, int height) {
        widgetImpl.setPreferredSize(width, height);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getX()
     */
    public int getX() {
        return widgetImpl.getX();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setX(int)
     */
    public void setX(int x) {
        widgetImpl.setX(x);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getY()
     */
    public int getY() {
        return widgetImpl.getY();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setY(int)
     */
    public void setY(int y) {
        widgetImpl.setY(y);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getWidth()
     */
    public int getWidth() {
        return widgetImpl.getWidth();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setWidth(int)
     */
    public void setWidth(int width) {
        widgetImpl.setWidth(width);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getHeight()
     */
    public int getHeight() {
        return widgetImpl.getHeight();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setHeight(int)
     */
    public void setHeight(int height) {
        widgetImpl.setHeight(height);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        widgetImpl.setVisible(visible);
        setForceCull(!visible);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#isVisible()
     */
    public boolean isVisible() {
        return widgetImpl.isVisible();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#isOpaque()
     */
    public boolean isOpaque() {
        return widgetImpl.isOpaque();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#doParentLayout()
     */
    public void doParentLayout() {
        widgetImpl.doParentLayout();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setWidgetParent(jme.widget.WidgetContainerAbstract)
     */
    public void setWidgetParent(WidgetContainerAbstract parent) {
        widgetImpl.setWidgetParent(parent);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getWidgetParent()
     */
    public WidgetContainerAbstract getWidgetParent() {
        return widgetImpl.getWidgetParent();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getAbsoluteLocation()
     */
    public Vector2f getAbsoluteLocation() {
        return widgetImpl.getAbsoluteLocation();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setApplyOffsetX(boolean)
     */
    public void setApplyOffsetX(boolean b) {
        widgetImpl.setApplyOffsetX(b);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isApplyOffsetX()
     */
    public boolean isApplyOffsetX() {
        return widgetImpl.isApplyOffsetX();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getXOffset()
     */
    public int getXOffset() {
        return widgetImpl.getXOffset();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setApplyOffsetY(boolean)
     */
    public void setApplyOffsetY(boolean b) {
        widgetImpl.setApplyOffsetY(b);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isApplyOffsetY()
     */
    public boolean isApplyOffsetY() {
        return widgetImpl.isApplyOffsetY();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getYOffset()
     */
    public int getYOffset() {
        return widgetImpl.getYOffset();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getZOrder()
     */
    public int getZOrder() {
        return widgetImpl.getZOrder();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setZOrder(int)
     */
    public void setZOrder(int i) {
        widgetImpl.setZOrder(i);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#isMouseInWidget()
     */
    public boolean isMouseInWidget() {
        return widgetImpl.isMouseInWidget();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getAlignment()
     */
    public WidgetAlignmentType getAlignment() {
        return widgetImpl.getAlignment();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setAlignment(jme.widget.AlignmentType)
     */
    public void setAlignment(WidgetAlignmentType alignment) {
        widgetImpl.setAlignment(alignment);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#doAlignment(jme.math.point.Point2, jme.widget.Insets)
     */
    public void doAlignment(Vector2f size, WidgetInsets insets) {
        widgetImpl.doAlignment(size, insets);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#close()
     */
    public void close() {
        widgetImpl.close();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#canClose()
     */
    public boolean canClose() {
        return widgetImpl.canClose();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getBgColor()
     */
    public ColorRGBA getBgColor() {
        return widgetImpl.getBgColor();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setBgColor(jme.color.ColorRGBA)
     */
    public void setBgColor(ColorRGBA colorRGBA) {
        widgetImpl.setBgColor(colorRGBA);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getFgColor()
     */
    public ColorRGBA getFgColor() {
        return widgetImpl.getFgColor();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setFgColor(jme.color.ColorRGBA)
     */
    public void setFgColor(ColorRGBA colorRGBA) {
        widgetImpl.setFgColor(colorRGBA);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#isCantOwnMouse()
     */
    public boolean isCantOwnMouse() {
        return widgetImpl.isCantOwnMouse();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setCantOwnMouse(boolean)
     */
    public void setCantOwnMouse(boolean b) {
        widgetImpl.setCantOwnMouse(b);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getViewport()
     */
    public WidgetViewport getViewport() {
        return widgetImpl.getViewport();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#setViewport(jme.scene.bounds.Viewport)
     */
    public void setViewport(WidgetViewport viewport) {
        widgetImpl.setViewport(viewport);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getOwner()
     */
    public Widget getOwner() {
        return widgetImpl.getOwner();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setOwner(jme.widget.Widget)
     */
    public void setOwner(Widget owner) {
        widgetImpl.setOwner(owner);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseButtonDown()
     */
    public WidgetNotifier getNotifierMouseButtonDown() {
        return widgetImpl.getNotifierMouseButtonDown();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseButtonUp()
     */
    public WidgetNotifier getNotifierMouseButtonUp() {
        return widgetImpl.getNotifierMouseButtonUp();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseDrag()
     */
    public WidgetNotifier getNotifierMouseDrag() {
        return widgetImpl.getNotifierMouseDrag();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseEnter()
     */
    public WidgetNotifier getNotifierMouseEnter() {
        return widgetImpl.getNotifierMouseEnter();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseExit()
     */
    public WidgetNotifier getNotifierMouseExit() {
        return widgetImpl.getNotifierMouseExit();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget2d#getNotifierMouseMove()
     */
    public WidgetNotifier getNotifierMouseMove() {
        return widgetImpl.getNotifierMouseMove();
    }

    /*
     **************************************************************
     * Widget2d implementation - end
     **************************************************************
     */

    /*
     **************************************************************
     * MouseHandlerInterface implementation - start
     **************************************************************
     */

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseButtonDownObserver(java.util.Observer)
     */
    public void addMouseButtonDownObserver(Observer o) {
        widgetImpl.addMouseButtonDownObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseButtonDownObserver(java.util.Observer)
     */
    public void deleteMouseButtonDownObserver(Observer o) {
        widgetImpl.deleteMouseButtonDownObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseButtonDown()
     */
    public void doMouseButtonDown() {
        widgetImpl.doMouseButtonDown();
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseButtonUpObserver(java.util.Observer)
     */
    public void addMouseButtonUpObserver(Observer o) {
        widgetImpl.addMouseButtonUpObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseButtonUpObserver(java.util.Observer)
     */
    public void deleteMouseButtonUpObserver(Observer o) {
        widgetImpl.deleteMouseButtonUpObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseButtonUp()
     */
    public void doMouseButtonUp() {
        widgetImpl.doMouseButtonUp();
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseMoveObserver(java.util.Observer)
     */
    public void addMouseMoveObserver(Observer o) {
        widgetImpl.addMouseMoveObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseMoveObserver(java.util.Observer)
     */
    public void deleteMouseMoveObserver(Observer o) {
        widgetImpl.deleteMouseMoveObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseMove()
     */
    public void doMouseMove() {
        widgetImpl.doMouseMove();
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseDragObserver(java.util.Observer)
     */
    public void addMouseDragObserver(Observer o) {
        widgetImpl.addMouseDragObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseDragObserver(java.util.Observer)
     */
    public void deleteMouseDragObserver(Observer o) {
        widgetImpl.deleteMouseDragObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseDrag()
     */
    public void doMouseDrag() {
        widgetImpl.doMouseDrag();
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseEnterObserver(java.util.Observer)
     */
    public void addMouseEnterObserver(Observer o) {
        widgetImpl.addMouseEnterObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseEnterObserver(java.util.Observer)
     */
    public void deleteMouseEnterObserver(Observer o) {
        widgetImpl.deleteMouseEnterObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseEnter()
     */
    public void doMouseEnter() {
        widgetImpl.doMouseEnter();
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#addMouseExitObserver(java.util.Observer)
     */
    public void addMouseExitObserver(Observer o) {
        widgetImpl.addMouseExitObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#deleteMouseExitObserver(java.util.Observer)
     */
    public void deleteMouseExitObserver(Observer o) {
        widgetImpl.deleteMouseExitObserver(o);
    }

    /* (non-Javadoc)
     * @see jme.input.mouse.MouseHandlerInterface#doMouseExit()
     */
    public void doMouseExit() {
        widgetImpl.doMouseExit();
    }

    /*
     **************************************************************
     * MouseHandlerInterface implementation - end
     **************************************************************
     */

    /*
     **************************************************************
     * Spatial implementation - start
     **************************************************************
     */

    /* (non-Javadoc)
     * @see jme.scene.Spatial#getWorldBound()
     */
    public BoundingVolume getWorldBound() {
        return widgetImpl.getWorldBound();
    }

    /* (non-Javadoc)
     * @see jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        widgetImpl.updateWorldBound();
        super.updateWorldBound();
        super.setWorldBound(widgetImpl.getWorldBound().merge(super.getWorldBound()));
    }

    /*
     **************************************************************
     * Spatial implementation - end
     **************************************************************
     */

    public String toString() {
        return "[worldBound=" + getWorldBound() + "]";
    }

}
