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

/*
 * EDIT:  04/01/2004 - Check for null in getMouseInput(). GOP
 * EDIT:  04/02/2004 - Renamed methods get/setInputController to get/setInputHandler. GOP
 * EDIT:  04/02/2004 - Renamed attribute inputController to inputHandler. GOP
 */

package com.jme.widget;

import java.util.Observer;

import com.jme.input.AbstractInputHandler;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.util.WidgetNotifier;

/**
 * <code>WidgetAbstractImpl</code>
 * @author Gregg Patton
 * @version
 */
public abstract class WidgetAbstractImpl extends Spatial implements Widget {

    public final static ColorRGBA defaultFgColor = new ColorRGBA(0, 0, 0, 1);
    protected ColorRGBA fgColor = defaultFgColor;

    public final static ColorRGBA defaultBgColor = new ColorRGBA(.8f, .8f, .8f, 1);
    protected ColorRGBA bgColor = defaultBgColor;

    protected final static int DEFAULT_COMP_WIDTH = 0;
    protected final static int DEFAULT_COMP_HEIGHT = 0;

    protected final static int DEFAULT_BORDER_SIZE = 0;
    protected WidgetBorder border = new WidgetBorder(DEFAULT_BORDER_SIZE, DEFAULT_BORDER_SIZE, DEFAULT_BORDER_SIZE, DEFAULT_BORDER_SIZE);

    protected WidgetBoundingRectangle localBound = new WidgetBoundingRectangle(true);
    protected Vector2f preferredSize = new Vector2f(DEFAULT_COMP_WIDTH, DEFAULT_COMP_HEIGHT);

    protected WidgetTextureCoords textureCoords;

    protected int zOrder;

    protected boolean visible;
    protected boolean opaque = true;

    protected WidgetAlignmentType alignment = WidgetAlignmentType.ALIGN_NONE;

    protected boolean cantOwnMouse;

    protected WidgetNotifier notifierMouseButtonDown = new WidgetNotifier();
    protected WidgetNotifier notifierMouseButtonUp = new WidgetNotifier();
    protected WidgetNotifier notifierMouseDrag = new WidgetNotifier();
    protected WidgetNotifier notifierMouseEnter = new WidgetNotifier();
    protected WidgetNotifier notifierMouseExit = new WidgetNotifier();
    protected WidgetNotifier notifierMouseMove = new WidgetNotifier();

    protected WidgetViewRectangle viewRectangle = new WidgetViewRectangle();
    private boolean applyOffsetX = true;
    private boolean applyOffsetY = true;

    private static AbstractInputHandler inputHandler;

    private static Widget mouseOwner;
    private static Widget widgetUnderMouse;
    private static Widget lastWidgetUnderMouse;

    private Widget owner;

    private WidgetRenderer renderer;

    public WidgetAbstractImpl() {
        super("");
        init();
    }

    public WidgetAbstractImpl(int width, int height) {
        this();
        setSize(width, height);
        setPreferredSize(new Vector2f(width, height));
        init();
    }

    private void init() {
        initWidgetRenderer();
        setVisible(true);
        updateWorldBound();
    }

    public MouseInput getMouseInput() {
        MouseInput mi = null;
        Mouse m = inputHandler.getMouse();

        if (m != null) {
            mi = m.getMouseInput();
        }

        return mi;
    }

    public AbstractInputHandler getInputHandler() {
        return inputHandler;
    }

    public void setInputHandler(AbstractInputHandler ih) {
        inputHandler = ih;
    }


    public Widget getMouseOwner() {
        return WidgetAbstractImpl.mouseOwner;
    }

    public void setMouseOwner(Widget widget) {
        WidgetAbstractImpl.mouseOwner = widget;
    }

    /**
     * <code>getWidgetUnderMouse</code>
     * @return
     */
    public Widget getWidgetUnderMouse() {
        return widgetUnderMouse;
    }

    /**
     * <code>setWidgetUnderMouse</code>
     * @param widget
     */
    public void setWidgetUnderMouse(Widget widget) {
        widgetUnderMouse = widget;
    }

    /**
     * <code>getLastWidgetUnderMouse</code>
     * @return
     */
    public Widget getLastWidgetUnderMouse() {
        return lastWidgetUnderMouse;
    }

    /**
     * <code>setLastWidgetUnderMouse</code>
     * @param widget
     */
    public void setLastWidgetUnderMouse(Widget widget) {
        lastWidgetUnderMouse = widget;
    }

    /** <code>getWidgetRenderer</code>
     * @return
     * @see com.jme.widget.Widget#getWidgetRenderer()
     */
    public WidgetRenderer getWidgetRenderer() {
        return renderer;
    }

    /** <code>setWidgetRenderer</code>
     * @param widgetRenderer
     * @see com.jme.widget.Widget#setWidgetRenderer(com.jme.widget.WidgetRenderer)
     */
    public void setWidgetRenderer(WidgetRenderer widgetRenderer) {
        this.renderer = widgetRenderer;
    }

    public WidgetBorder getBorder() {
        if (border != null)
            return new WidgetBorder(border);
        else
            return null;
    }

    public void setBorder(WidgetBorder border) {
        if (border != null)
            this.border.set(border);
        else
            this.border = border;
    }

    public void setLocation(Vector2f at) {
        localBound.setMinPreserveSize(at);
        updateWorldBound();
    }

    public void setLocation(int x, int y) {
        localBound.setMinPreserveSize(x, y);
        updateWorldBound();
    }

    public Vector2f getLocation() {
        Vector2f ret = new Vector2f();

        ret.x = localBound.getMinX();
        ret.y = localBound.getMinY();

        return ret;
    }

    public void setSize(Vector2f size) {
        setSize((int) size.x, (int) size.y);
    }

    public void setSize(int width, int height) {
        localBound.setWidthHeight(width, height);
        updateWorldBound();
    }

    public Vector2f getSize() {
        return new Vector2f(localBound.getWidth(), localBound.getHeight());
    }

    public Vector2f getPreferredSize() {
        Vector2f ret = new Vector2f();

        ret.x = preferredSize.x;
        ret.y = preferredSize.y;

        return ret;
    }

    public void setPreferredSize(Vector2f size) {
        preferredSize.x = size.x;
        preferredSize.y = size.y;
    }

    public void setPreferredSize(int width, int height) {
        preferredSize.x = width;
        preferredSize.y = height;
    }

    public int getX() {
        return (int) this.localBound.getMinX();
    }

    public void setX(int x) {
        localBound.setMinXPreserveSize(x);
    }

    public int getY() {
        return (int) this.localBound.getMinY();
    }

    public void setY(int y) {
        localBound.setMinYPreserveSize(y);
    }

    public int getWidth() {
        return (int) this.localBound.getWidth();
    }

    public void setWidth(int width) {
        localBound.setWidth(width);
    }

    public int getHeight() {
        return (int) this.localBound.getHeight();
    }

    public void setHeight(int height) {
        localBound.setHeight(height);
    }

    public WidgetBoundingRectangle getExtents() {
        super.updateGeometricState(0.0f, true);

        WidgetBoundingRectangle rect = (WidgetBoundingRectangle) super.getWorldBound();

        rect.subtractMinX(border.left);
        rect.addWidth(border.left + border.right);

        rect.subtractMinY(border.bottom);
        rect.addHeight(border.top + border.bottom);

        return rect;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        setForceCull(!visible);
        updateWorldBound();
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public WidgetTextureCoords getTextureCoords() {
        return textureCoords;
    }

    public void setTextureCoords(WidgetTextureCoords coords) {
        textureCoords = coords;
    }

    public void doParentLayout() {
        WidgetAbstractContainer p = getWidgetParent();
        if (p != null)
            p.doLayout();
    }

    public void setWidgetParent(WidgetAbstractContainer parent) {
        super.setParent(parent);
    }

    public WidgetAbstractContainer getWidgetParent() {
        return (WidgetAbstractContainer) super.getParent();
    }

    public Vector2f getAbsoluteLocation() {
        Vector2f l = new Vector2f();
        l.x = localBound.getMinX();
        l.y = localBound.getMinY();

        WidgetAbstractContainer p = getWidgetParent();

        while (p != null) {

            l.x += p.getX();

            if (applyOffsetX == true)
                l.x += p.getPanOffsetX();

            l.y += p.getY();

            if (applyOffsetY == true)
                l.y += p.getPanOffsetY();

            p = p.getWidgetParent();
        }

        return l;
    }

    protected boolean isTopLevel() {
        return getParent() == null;
    }

    protected Widget getTopLevelComponent() {
        Widget parent = this;

        while (parent != null) {
            if (((Spatial) parent).getParent() == null)
                break;

            parent = parent.getWidgetParent();
        }

        return parent;

    }

    public void setApplyOffsetX(boolean b) {
        this.applyOffsetX = b;
    }

    public boolean isApplyOffsetX() {
        return this.applyOffsetX;
    }

    public int getXOffset() {
        WidgetAbstractContainer p = getWidgetParent();

        if (p != null && applyOffsetX == true) {
            return (int) (p.getViewRectangle().getOffsetX() + p.getPanOffsetX());
        } else {
            return 0;
        }
    }

    public void setApplyOffsetY(boolean b) {
        this.applyOffsetY = b;
    }

    public boolean isApplyOffsetY() {
        return this.applyOffsetY;
    }

    public int getYOffset() {

        WidgetAbstractContainer p = getWidgetParent();

        if (p != null && applyOffsetY == true) {
            return (int) (p.getViewRectangle().getOffsetY() + p.getPanOffsetY());
        } else {
            return 0;
        }
    }

    public int getZOrder() {
        return zOrder;
    }

    public void setZOrder(int i) {
        zOrder = i;
    }

    public boolean isMouseInWidget() {
        Widget mouseOwner = getMouseOwner();

        if (this.cantOwnMouse)
            return false;
        else if (mouseOwner == this)
            return true;
        else if (mouseOwner != null && (mouseOwner != getParent() && mouseOwner.getZOrder() > zOrder))
            return false;

        MouseInput mi = getMouseInput();

        return getViewRectangle().inside(mi.getXAbsolute(), mi.getYAbsolute());
    }

    public WidgetAlignmentType getAlignment() {
        return alignment;
    }

    public void setAlignment(WidgetAlignmentType alignment) {
        this.alignment = alignment;
    }

    protected void alignCenter(Vector2f size, WidgetInsets insets) {
        int x = (int) ((size.x / 2) - (getWidth() / 2));
        int y = (int) ((size.y / 2) - (getHeight() / 2));
        setLocation(x, y);
    }

    protected void alignWest(Vector2f size, WidgetInsets insets) {
        setX(insets.left);
    }

    protected void alignEast(Vector2f size, WidgetInsets insets) {
        setX((int) (size.x - getWidth() - insets.right));
    }

    protected void alignNorth(Vector2f size, WidgetInsets insets) {
        setY((int) (size.y - getHeight() - insets.top));
    }

    protected void alignSouth(Vector2f size, WidgetInsets insets) {
        setY(insets.bottom);
    }

    public void doAlignment(Vector2f size, WidgetInsets insets) {
        if (getAlignment() != WidgetAlignmentType.ALIGN_NONE) {

            if (getAlignment() == WidgetAlignmentType.ALIGN_CENTER) {
                alignCenter(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_WEST) {
                alignCenter(size, insets);
                alignWest(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_EAST) {
                alignCenter(size, insets);
                alignEast(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_NORTH) {
                alignCenter(size, insets);
                alignNorth(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_SOUTH) {
                alignCenter(size, insets);
                alignSouth(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_NORTHWEST) {
                alignNorth(size, insets);
                alignWest(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_SOUTHWEST) {
                alignSouth(size, insets);
                alignWest(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_NORTHEAST) {
                alignNorth(size, insets);
                alignEast(size, insets);
            } else if (getAlignment() == WidgetAlignmentType.ALIGN_SOUTHEAST) {
                alignSouth(size, insets);
                alignEast(size, insets);
            }

        }
    }

    public void close() {}

    public boolean canClose() {
        return true;
    }

    public ColorRGBA getBgColor() {
        return bgColor;
    }

    public void setBgColor(ColorRGBA colorRGBA) {
        bgColor = colorRGBA;
    }

    public ColorRGBA getFgColor() {
        return fgColor;
    }

    public void setFgColor(ColorRGBA colorRGBA) {
        fgColor = colorRGBA;
    }

    public void addMouseButtonDownObserver(Observer o) {
        this.notifierMouseButtonDown.addObserver(o);
    }

    public void deleteMouseButtonDownObserver(Observer o) {
        this.notifierMouseButtonDown.deleteObserver(o);
    }

    /** <code>deleteMouseButtonDownObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseButtonDownObservers()
     */
    public void deleteMouseButtonDownObservers() {
        this.notifierMouseButtonDown.deleteObservers();
    }

    public void doMouseButtonDown() {}

    public void handleMouseButtonDown() {
        if (isVisible() == false)
            return;

        boolean b = isMouseInWidget();

        if (b) {

            setMouseOwner(this);
        }

    }

    public void addMouseButtonUpObserver(Observer o) {
        this.notifierMouseButtonUp.addObserver(o);
    }

    public void deleteMouseButtonUpObserver(Observer o) {
        this.notifierMouseButtonUp.deleteObserver(o);
    }

    /** <code>deleteMouseButtonUpObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseButtonUpObservers()
     */
    public void deleteMouseButtonUpObservers() {
        this.notifierMouseButtonUp.deleteObservers();
    }

    public void doMouseButtonUp() {}

    public void handleMouseButtonUp() {
        if (isVisible() == false)
            return;

        boolean b = isMouseInWidget();

        Widget mouseOwner = getMouseOwner();

        if (mouseOwner != null) {

            mouseOwner.doMouseButtonUp();
            setMouseOwner(null);
        }

    }

    public void addMouseMoveObserver(Observer o) {
        this.notifierMouseMove.addObserver(o);
    }

    public void deleteMouseMoveObserver(Observer o) {
        this.notifierMouseMove.deleteObserver(o);
    }

    /** <code>deleteMouseMoveObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseMoveObservers()
     */
    public void deleteMouseMoveObservers() {
        this.notifierMouseMove.deleteObservers();
    }

    public void doMouseMove() {}

    public void handleMouseMove() {
        if (isVisible() == false)
            return;

        boolean b = isMouseInWidget();

        if (b) {

            setWidgetUnderMouse(this);
        }

    }

    public void addMouseDragObserver(Observer o) {
        this.notifierMouseDrag.addObserver(o);
    }

    public void deleteMouseDragObserver(Observer o) {
        this.notifierMouseDrag.deleteObserver(o);
    }

    /** <code>deleteMouseDragObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseDragObservers()
     */
    public void deleteMouseDragObservers() {
        this.notifierMouseDrag.deleteObservers();
    }

    public void doMouseDrag() {}

    public void handleMouseDrag() {
        if (isVisible() == false)
            return;
    }

    public void addMouseEnterObserver(Observer o) {
        this.notifierMouseEnter.addObserver(o);
    }

    public void deleteMouseEnterObserver(Observer o) {
        this.notifierMouseEnter.deleteObserver(o);
    }

    /** <code>deleteMouseEnterObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseEnterObservers()
     */
    public void deleteMouseEnterObservers() {
        this.notifierMouseEnter.deleteObservers();
    }

    public void doMouseEnter() {}

    public void handleMouseEnter() {
        if (isVisible() == false)
            return;
    }

    public void addMouseExitObserver(Observer o) {
        this.notifierMouseExit.addObserver(o);
    }

    public void deleteMouseExitObserver(Observer o) {
        this.notifierMouseExit.deleteObserver(o);
    }

    /** <code>deleteMouseExitObservers</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#deleteMouseExitObservers()
     */
    public void deleteMouseExitObservers() {
        this.notifierMouseExit.deleteObservers();
    }

    public void doMouseExit() {}

    public void handleMouseExit() {
        if (isVisible() == false)
            return;
    }

    public boolean isCantOwnMouse() {
        return cantOwnMouse;
    }

    public void setCantOwnMouse(boolean b) {
        cantOwnMouse = b;
    }

    public WidgetViewRectangle getViewRectangle() {
        return new WidgetViewRectangle(viewRectangle);
    }

    public void setViewRectangle(WidgetViewRectangle viewRectangle) {
        this.viewRectangle.set(viewRectangle);
    }

    public Widget getOwner() {
        return owner;
    }

    public void setOwner(Widget widget) {
        owner = widget;
    }

    public WidgetNotifier getNotifierMouseButtonDown() {
        return notifierMouseButtonDown;
    }

    public WidgetNotifier getNotifierMouseButtonUp() {
        return notifierMouseButtonUp;
    }

    public WidgetNotifier getNotifierMouseDrag() {
        return notifierMouseDrag;
    }

    public WidgetNotifier getNotifierMouseEnter() {
        return notifierMouseEnter;
    }

    public WidgetNotifier getNotifierMouseExit() {
        return notifierMouseExit;
    }

    public WidgetNotifier getNotifierMouseMove() {
        return notifierMouseMove;
    }

    /**
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {

        WidgetBoundingRectangle r = new WidgetBoundingRectangle(true);

        r.setMin(getAbsoluteLocation());

        r.setSize(getSize());

        setWorldBound(r);

    }

    protected boolean isCulled(WidgetBoundingRectangle bound) {
        float w = viewRectangle.getWidth();
        float h = viewRectangle.getHeight();

        if ((w > 0 && h > 0) == false)
            return true; //has no size so it's culled

        if (bound == null)
            return true;

        boolean ret = false;

        WidgetBoundingRectangle r = bound;

        ret = !viewRectangle.contains(bound);

        if (ret == true) {
            ret = !WidgetBoundingRectangle.intersects(viewRectangle, r);
        }

        return ret;
    }

    /**
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {
        if (forceCull) {
            return;
        }

        //check to see if we can cull this node
        if (!isCulled((WidgetBoundingRectangle) worldBound) || isForceView()) {
//            setStates();
            draw(r);
//            unsetStates();
        }

    }

    /**
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     */
    public void draw(Renderer r) {}

    public String toString() {
        return "[localBound=" + localBound + "]";
    }

}
