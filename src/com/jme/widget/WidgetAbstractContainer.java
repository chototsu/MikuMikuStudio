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

/*
 * EDIT:  04/02/2004 - Renamed methods get/setInputController to get/setInputHandler. GOP
 */

package com.jme.widget;

import java.util.ArrayList;
import java.util.Observer;
import java.util.logging.Level;

import com.jme.bounding.BoundingVolume;
import com.jme.input.AbstractInputHandler;
import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.LoggingSystem;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewRectangle;
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
public abstract class WidgetAbstractContainer extends Node implements Widget {
    private class WidgetContainerImpl extends WidgetAbstractImpl {

        /** <code>initWidgetRenderer</code>
         *
         * @see com.jme.widget.Widget#initWidgetRenderer()
         */
        public void initWidgetRenderer() {}

		/** <code>drawBounds</code>
		 * @param r
		 * @see com.jme.scene.Spatial#drawBounds(com.jme.renderer.Renderer)
		 */
		public void drawBounds(Renderer r) {
			//do nothing

		}
    }

    protected Vector2f panOffset = new Vector2f();

    protected WidgetLayoutManager layout;

    protected WidgetContainerImpl widgetImpl = new WidgetContainerImpl();

    protected ArrayList widgetList = new ArrayList();

    public final static int DEFAULT_INSET_SIZE = 0;
    protected WidgetInsets insets =
        new WidgetInsets(DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE, DEFAULT_INSET_SIZE);

    public WidgetAbstractContainer() {
        super("");
        init();
    }

    public WidgetAbstractContainer(int width, int height) {
        super("");
        widgetImpl.setSize(width, height);
        init();
    }

    private void init() {
        initWidgetRenderer();
        setVisible(true);
    }

    protected void update() {
        calcViewport(this);
        updateChildrenViewports();
        updateWorldBound();
    }

    public void add(Widget w) {
        w.setZOrder(getWidgetCount());

        if (w instanceof Spatial) {

            super.attachChild((Spatial) w);

            widgetList.add(w);

            w.setWidgetParent(this);

        } else {
            LoggingSystem.getLogger().log(Level.WARNING, "Children must be of type Spatial.");
        }

    }

    public void add(Widget w, Object constraints) {
        w.setZOrder(getWidgetCount());

        Spatial child = null;

        if (w instanceof Spatial) {

            super.attachChild((Spatial) w);

            widgetList.add(w);

            w.setWidgetParent(this);

            if (layout != null)
                 ((WidgetLayoutManager2) layout).addLayoutWidget(w, constraints);

        } else {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "WidgetAbstractContainer:  Children must be of type Spatial.");
        }

    }

    public int getWidgetCount() {
        return widgetList.size();
    }

    public WidgetBoundingRectangle getExtents() {

        super.updateWorldBound();

        WidgetBoundingRectangle rect = (WidgetBoundingRectangle) super.getWorldBound();

        rect.subtractMinX(insets.left + widgetImpl.getBorder().left);
        rect.addWidth(
            insets.left + insets.right + widgetImpl.getBorder().left + widgetImpl.getBorder().right);

        rect.subtractMinY(insets.bottom + widgetImpl.getBorder().bottom);
        rect.addHeight(
            insets.top + insets.bottom + widgetImpl.getBorder().top + widgetImpl.getBorder().bottom);

        return rect;
    }

    public Widget getWidget(int n) {
        if (n < widgetList.size())
            return (Widget) widgetList.get(n);
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

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);

            if (w instanceof WidgetAbstractContainer)
                 ((WidgetAbstractContainer) w).doLayout();

            w.doAlignment(getSize(), getInsets());
        }

        updateChildrenViewports();
        updateWorldBound();

    }

    public void updateChildrenViewports() {
        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);

            calcViewport(w);

            if (w instanceof WidgetAbstractContainer) {
                ((WidgetAbstractContainer) w).updateChildrenViewports();
            }
        }

    }

    public void pack() {
        setSize(getPreferredSize());
    }

    public void remove(int w) {
        if (w > 0 && w < super.getQuantity()) {
            widgetList.remove(w);
            super.detachChildAt(w);
            doLayout();
        }
    }

    public void remove(Widget w) {
        widgetList.remove(w);
        if (w instanceof Spatial) {

            super.detachChild((Spatial) w);

        } else {
            LoggingSystem.getLogger().log(
                Level.WARNING,
                "WidgetAbstractContainer:  Children must be of type Spatial.");
        }

    }

    public void removeAll() {
        detachAllChildren();
        widgetList.clear();
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

    /** <code>getWidgetUnderMouse</code>
     * @return
     * @see com.jme.widget.Widget#getWidgetUnderMouse()
     */
    public Widget getWidgetUnderMouse() {
        return widgetImpl.getWidgetUnderMouse();
    }

    /** <code>setWidgetUnderMouse</code>
     * @param widget
     * @see com.jme.widget.Widget#setWidgetUnderMouse(com.jme.widget.Widget)
     */
    public void setWidgetUnderMouse(Widget widget) {
        widgetImpl.setWidgetUnderMouse(widget);
    }

    /** <code>getLastWidgetUnderMouse</code>
     * @return
     * @see com.jme.widget.Widget#getLastWidgetUnderMouse()
     */
    public Widget getLastWidgetUnderMouse() {
        return widgetImpl.getLastWidgetUnderMouse();
    }

    /** <code>setLastWidgetUnderMouse</code>
     * @param widget
     * @see com.jme.widget.Widget#setLastWidgetUnderMouse(com.jme.widget.Widget)
     */
    public void setLastWidgetUnderMouse(Widget widget) {
        widgetImpl.setLastWidgetUnderMouse(widget);
    }

    /** <code>getWidgetRenderer</code>
     * @return
     * @see com.jme.widget.Widget#getWidgetRenderer()
     */
    public WidgetRenderer getWidgetRenderer() {
        return widgetImpl.getWidgetRenderer();
    }

    /** <code>setWidgetRenderer</code>
     * @param widgetRenderer
     * @see com.jme.widget.Widget#setWidgetRenderer(com.jme.widget.WidgetRenderer)
     */
    public void setWidgetRenderer(WidgetRenderer widgetRenderer) {
        widgetImpl.setWidgetRenderer(widgetRenderer);
    }

    /**
     * @see com.jme.widget.Widget#getMouseInput()
     */
    public MouseInput getMouseInput() {
        return widgetImpl.getMouseInput();
    }

    /**
     * @see com.jme.widget.Widget#getInputHandler()
     */
    public AbstractInputHandler getInputHandler() {
        return widgetImpl.getInputHandler();
    }

    /**
     * @see com.jme.widget.Widget#setInputHandler(com.jme.input.InputHandler)
     */
    public void setInputHandler(AbstractInputHandler ih) {
        widgetImpl.setInputHandler(ih);

    }

    public void handleMouseButtonDown() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseButtonDown();

        if (getMouseOwner() == widgetImpl) {
            setMouseOwner(this);
        }

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.handleMouseButtonDown();
        }

    }

    public void handleMouseButtonUp() {
        if (isVisible() == false)
            return;

        Widget mouseOwner = getMouseOwner();

        widgetImpl.handleMouseButtonUp();

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.handleMouseButtonUp();
        }

    }

    public void handleMouseMove() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseMove();

        if (getWidgetUnderMouse() == widgetImpl) {
            setWidgetUnderMouse(this);
        }

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.handleMouseMove();
        }
    }

    public void handleMouseDrag() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseDrag();

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.handleMouseDrag();
        }
    }

    public void handleMouseEnter() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseEnter();

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
            w.handleMouseEnter();
        }
    }

    public void handleMouseExit() {
        if (isVisible() == false)
            return;

        widgetImpl.handleMouseExit();

        for (int i = 0; i < getWidgetCount(); i++) {
            Widget w = getWidget(i);
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
        return widgetList.contains(w);
    }

    /*
     **************************************************************
     * Widget implementation - start
     **************************************************************
     */

    /* (non-Javadoc)
     * @see jme.widget.Widget#getBorder()
     */
    public WidgetBorder getBorder() {
        return widgetImpl.getBorder();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setBorder(jme.widget.border.Border)
     */
    public void setBorder(WidgetBorder border) {
        widgetImpl.setBorder(border);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setLocation(jme.math.point.Point2)
     */
    public void setLocation(Vector2f at) {
        widgetImpl.setLocation(at);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setLocation(int, int)
     */
    public void setLocation(int x, int y) {
        widgetImpl.setLocation(x, y);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getLocation()
     */
    public Vector2f getLocation() {
        return widgetImpl.getLocation();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setSize(jme.math.point.Point2)
     */
    public void setSize(Vector2f size) {
        widgetImpl.setSize(size);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setSize(int, int)
     */
    public void setSize(int width, int height) {
        widgetImpl.setSize(width, height);
        update();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getSize()
     */
    public Vector2f getSize() {
        return widgetImpl.getSize();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setPreferredSize(jme.math.point.Point2)
     */
    public void setPreferredSize(Vector2f size) {
        widgetImpl.setPreferredSize(size);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setPreferredSize(int, int)
     */
    public void setPreferredSize(int width, int height) {
        widgetImpl.setPreferredSize(width, height);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getX()
     */
    public int getX() {
        return widgetImpl.getX();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setX(int)
     */
    public void setX(int x) {
        widgetImpl.setX(x);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getY()
     */
    public int getY() {
        return widgetImpl.getY();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setY(int)
     */
    public void setY(int y) {
        widgetImpl.setY(y);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getWidth()
     */
    public int getWidth() {
        return widgetImpl.getWidth();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setWidth(int)
     */
    public void setWidth(int width) {
        widgetImpl.setWidth(width);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getHeight()
     */
    public int getHeight() {
        return widgetImpl.getHeight();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setHeight(int)
     */
    public void setHeight(int height) {
        widgetImpl.setHeight(height);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        widgetImpl.setVisible(visible);
        setForceCull(!visible);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isVisible()
     */
    public boolean isVisible() {
        return widgetImpl.isVisible();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isOpaque()
     */
    public boolean isOpaque() {
        return widgetImpl.isOpaque();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#doParentLayout()
     */
    public void doParentLayout() {
        widgetImpl.doParentLayout();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setWidgetParent(jme.widget.WidgetAbstractContainer)
     */
    public void setWidgetParent(WidgetAbstractContainer parent) {
        widgetImpl.setWidgetParent(parent);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getWidgetParent()
     */
    public WidgetAbstractContainer getWidgetParent() {
        return widgetImpl.getWidgetParent();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getAbsoluteLocation()
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
     * @see jme.widget.Widget#getXOffset()
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
     * @see jme.widget.Widget#getYOffset()
     */
    public int getYOffset() {
        return widgetImpl.getYOffset();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getZOrder()
     */
    public int getZOrder() {
        return widgetImpl.getZOrder();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setZOrder(int)
     */
    public void setZOrder(int i) {
        widgetImpl.setZOrder(i);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isMouseInWidget()
     */
    public boolean isMouseInWidget() {
        return widgetImpl.isMouseInWidget();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getAlignment()
     */
    public WidgetAlignmentType getAlignment() {
        return widgetImpl.getAlignment();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setAlignment(jme.widget.AlignmentType)
     */
    public void setAlignment(WidgetAlignmentType alignment) {
        widgetImpl.setAlignment(alignment);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#doAlignment(jme.math.point.Point2, jme.widget.Insets)
     */
    public void doAlignment(Vector2f size, WidgetInsets insets) {
        widgetImpl.doAlignment(size, insets);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#close()
     */
    public void close() {
        widgetImpl.close();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#canClose()
     */
    public boolean canClose() {
        return widgetImpl.canClose();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getBgColor()
     */
    public ColorRGBA getBgColor() {
        return widgetImpl.getBgColor();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setBgColor(jme.color.ColorRGBA)
     */
    public void setBgColor(ColorRGBA colorRGBA) {
        widgetImpl.setBgColor(colorRGBA);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getFgColor()
     */
    public ColorRGBA getFgColor() {
        return widgetImpl.getFgColor();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setFgColor(jme.color.ColorRGBA)
     */
    public void setFgColor(ColorRGBA colorRGBA) {
        widgetImpl.setFgColor(colorRGBA);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#isCantOwnMouse()
     */
    public boolean isCantOwnMouse() {
        return widgetImpl.isCantOwnMouse();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setCantOwnMouse(boolean)
     */
    public void setCantOwnMouse(boolean b) {
        widgetImpl.setCantOwnMouse(b);
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getViewport()
     */
    public WidgetViewRectangle getViewRectangle() {
        return widgetImpl.getViewRectangle();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#setViewport(jme.scene.bounds.Viewport)
     */
    public void setViewRectangle(WidgetViewRectangle viewport) {
        widgetImpl.setViewRectangle(viewport);
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
     * @see jme.widget.Widget#getNotifierMouseButtonDown()
     */
    public WidgetNotifier getNotifierMouseButtonDown() {
        return widgetImpl.getNotifierMouseButtonDown();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getNotifierMouseButtonUp()
     */
    public WidgetNotifier getNotifierMouseButtonUp() {
        return widgetImpl.getNotifierMouseButtonUp();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getNotifierMouseDrag()
     */
    public WidgetNotifier getNotifierMouseDrag() {
        return widgetImpl.getNotifierMouseDrag();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getNotifierMouseEnter()
     */
    public WidgetNotifier getNotifierMouseEnter() {
        return widgetImpl.getNotifierMouseEnter();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getNotifierMouseExit()
     */
    public WidgetNotifier getNotifierMouseExit() {
        return widgetImpl.getNotifierMouseExit();
    }

    /* (non-Javadoc)
     * @see jme.widget.Widget#getNotifierMouseMove()
     */
    public WidgetNotifier getNotifierMouseMove() {
        return widgetImpl.getNotifierMouseMove();
    }

    /** <code>getTextureCoords</code>
     * @return
     * @see com.jme.widget.Widget#getTextureCoords()
     */
    public WidgetTextureCoords getTextureCoords() {
        return widgetImpl.getTextureCoords();
    }

    /** <code>setTextureCoords</code>
     * @param coords
     * @see com.jme.widget.Widget#setTextureCoords(com.jme.widget.WidgetTextureCoords)
     */
    public void setTextureCoords(WidgetTextureCoords coords) {
        widgetImpl.setTextureCoords(coords);
    }

    /*
     **************************************************************
     * Widget implementation - end
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

    /** <code>deleteMouseButtonDownObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseButtonDownObservers()
     */
    public void deleteMouseButtonDownObservers() {
        widgetImpl.deleteMouseButtonDownObservers();
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

    /** <code>deleteMouseButtonUpObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseButtonUpObservers()
     */
    public void deleteMouseButtonUpObservers() {
        widgetImpl.deleteMouseButtonUpObservers();
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

    /** <code>deleteMouseMoveObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseMoveObservers()
     */
    public void deleteMouseMoveObservers() {
        widgetImpl.deleteMouseMoveObservers();
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

    /** <code>deleteMouseDragObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseDragObservers()
     */
    public void deleteMouseDragObservers() {
        widgetImpl.deleteMouseDragObservers();
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

    /** <code>deleteMouseEnterObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseEnterObservers()
     */
    public void deleteMouseEnterObservers() {
        widgetImpl.deleteMouseEnterObservers();
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

    /** <code>deleteMouseExitObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseExitObservers()
     */
    public void deleteMouseExitObservers() {
        widgetImpl.deleteMouseExitObservers();
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

    /*
     **************************************************************
     * Node overrides - start
     **************************************************************
     */

    /*
     **************************************************************
     * Node overrides - end
     **************************************************************
     */

    public String toString() {
        return "[worldBound=" + getWorldBound() + "]";
    }

}
