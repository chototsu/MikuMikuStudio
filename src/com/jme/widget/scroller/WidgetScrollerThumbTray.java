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

import java.util.Observable;
import java.util.Observer;

import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.util.WidgetRepeater;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetScrollerThumbTray extends WidgetPanel implements Observer {

    private static final long serialVersionUID = 1L;

	protected WidgetRepeater repeat = new WidgetRepeater();

    protected boolean pagingUpLeft;
    protected boolean pagingDownRight;

    protected WidgetButton thumb = new WidgetScrollerThumb();
    protected WidgetOrientationType type;

    protected double buttonSize = WidgetScrollerButton.DEFAULT_SCROLLER_BUTTON_SIZE;

    protected double range;
    protected double visibleRange;
    protected double ratio;

    protected double offset;
    protected double offsetAdjust = 0;

    protected double thumbPos;
    protected double thumbSize;

    protected double size;

    public WidgetScrollerThumbTray(WidgetOrientationType type) {
        super();

        setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST));

        this.type = type;

        Vector2f size = new Vector2f();
        size.x = size.y = (float) buttonSize;
        thumb.setPreferredSize(size);

        add(thumb);

        thumb.addMouseDragObserver(this);

        setBgColor(new ColorRGBA(.7f, .7f, .7f, 1));

    }

    protected void calcRatio() {
        if (range != 0) {

            ratio = size / range;

        }
    }

    protected void calcOffset() {
        if (ratio != 0) {
            offset = (thumbPos + (offsetAdjust * (thumbPos / (size - buttonSize)))) / ratio;
        }
    }

    protected void calcThumbPos() {
        thumbPos = offset * ratio;
        clampThumbPos();
    }

    protected void calcThumbSize() {
        if (ratio != 0) {

            double ts = visibleRange * ratio;

            if (ts < buttonSize) {
                offsetAdjust = buttonSize - ts;
                thumbSize = (int) buttonSize;

            } else {

                thumbSize = (int) ts;
                offsetAdjust = 0;

            }
        }

    }

    protected void clampThumbPos() {
        if (type == WidgetOrientationType.VERTICAL) {

            if (thumbPos < 0) {
                thumbPos = 0;
            } else if (thumbPos + thumbSize > getHeight()) {
                thumbPos = getHeight() - thumbSize;
            }

        } else if (type == WidgetOrientationType.HORIZONTAL) {

            if (thumbPos < 0) {
                thumbPos = 0;
            } else if (thumbPos + thumbSize > getWidth()) {
                thumbPos = getWidth() - thumbSize;
            }
        }
    }

    protected void clampOffset() {

        if (offset < 0) {
            offset = 0;
        } else if (offset + visibleRange > range) {
            offset = range - visibleRange;
        }

    }

    protected void initExtents() {

        if (type == WidgetOrientationType.VERTICAL) {
            size = getHeight();

            if (size > 0) {

                calcThumbSize();

                calcThumbPos();

                clampThumbPos();

                thumb.setPreferredSize((int) buttonSize, (int) thumbSize);
                updatePanOffset();
            }

        } else if (type == WidgetOrientationType.HORIZONTAL) {
            size = getWidth();

            if (size > 0) {

                calcThumbSize();

                calcThumbPos();

                clampThumbPos();

                thumb.setPreferredSize((int) thumbSize, (int) buttonSize);
                updatePanOffset();
            }
        }

        doLayout();
    }

    public void update(Observable o, Object arg) {
        MouseInput mi = getMouseInput();

        if (type == WidgetOrientationType.VERTICAL) {

            thumbPos -= mi.getYDelta();

            clampThumbPos();

            calcOffset();
            updatePanOffset();

        } else if (type == WidgetOrientationType.HORIZONTAL) {

            thumbPos += mi.getXDelta();

            clampThumbPos();

            calcOffset();
            updatePanOffset();
        }

        getNotifierMouseDrag().notifyObservers(this);
    }

    public void setRangeExtents(float range, float visibleRange) {
        this.range = range;
        this.visibleRange = visibleRange;

        calcRatio();

        initExtents();

    }

    public int getRange() {
        return (int) range;
    }

    public int getVisibleRange() {
        return (int) visibleRange;
    }

    public int getOffset() {
        return (int) offset;

    }

    public void setOffset(int i) {
        offset = i < 0 ? 0 : i;
        initExtents();
    }

    public void decrement() {
        decrement(1);
    }

    public void pageUpLeft() {
        decrement((int) visibleRange);
    }

    public void pageDownRight() {
        increment((int) visibleRange);
    }

    public void decrement(int d) {
        offset -= d;

        clampOffset();

        calcThumbPos();

        updatePanOffset();

        getNotifierMouseDrag().notifyObservers(this);
    }

    public void increment() {
        increment(1);
    }

    public void increment(int i) {
        offset += i;

        calcThumbPos();

        clampOffset();

        updatePanOffset();

        getNotifierMouseDrag().notifyObservers(this);

    }

    protected void updatePanOffset() {
        if (type == WidgetOrientationType.VERTICAL) {

            setPanYOffset((int) - thumbPos);

        } else if (type == WidgetOrientationType.HORIZONTAL) {

            setPanXOffset((int) thumbPos);

        }
    }


    public void doMouseButtonDown() {
        MouseInput mi = getMouseInput();

        if (this.type == WidgetOrientationType.VERTICAL) {
            Vector2f l = thumb.getAbsoluteLocation();

            if (mi.getYAbsolute() > l.y + thumbSize) {

                pageUpLeft();
                this.pagingUpLeft = true;
                repeat.start();

            } else if (mi.getYAbsolute() < l.y) {
                pageDownRight();
                this.pagingDownRight = true;
                repeat.start();
            }

        } else if (this.type == WidgetOrientationType.HORIZONTAL) {
            Vector2f l = thumb.getAbsoluteLocation();

            if (mi.getXAbsolute() > l.x + thumbSize) {

                pageDownRight();
                this.pagingDownRight = true;
                repeat.start();

            } else if (mi.getXAbsolute() < l.x) {
                pageUpLeft();
                this.pagingUpLeft = true;
                repeat.start();
            }

        }
    }

    public void doMouseButtonUp() {
        pagingUpLeft = pagingDownRight = false;
    }

    public void draw(Renderer r) {
        if (isVisible() == false)
            return;

        if (pagingUpLeft) {
            if (repeat.doRepeat()) {
                pageUpLeft();
            }
        } else if (pagingDownRight) {
            if (repeat.doRepeat()) {
                pageDownRight();
            }
        }

        super.draw(r);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);

        if (type == WidgetOrientationType.VERTICAL) {

            size = getHeight();

        } else if (this.type == WidgetOrientationType.HORIZONTAL) {

            size = getWidth();

        }

        calcRatio();
        initExtents();
    }

    /** <code>setSize</code>
     * @param size
     * @see com.jme.widget.Widget#setSize(com.jme.math.Vector2f)
     */
    public void setSize(Vector2f size) {
        setSize((int) size.x, (int) size.y);
    }


    /**
     * <code>getThumb</code>
     * @return
     */
    public WidgetButton getThumb() {
        return thumb;
    }

    /**
     * <code>setThumb</code>
     * @param button
     */
    public void setThumb(WidgetButton button) {
        if (thumb != null) {
            remove(thumb);
        }

        thumb = button;
        add(thumb);
        thumb.addMouseDragObserver(this);
    }

    /**
     * <code>getThumbPos</code>
     * @return
     */
    public double getThumbPos() {
        return thumbPos;
    }

    /**
     * <code>setThumbPos</code>
     * @param d
     */
    public void setThumbPos(double d) {
        thumbPos = d;
        clampThumbPos();
        updatePanOffset();
    }

    /**
     * <code>getThumbSize</code>
     * @return
     */
    public double getThumbSize() {
        return thumbSize;
    }

}
