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
package com.jme.widget.layout;

import com.jme.math.Vector2f;

import com.jme.widget.Widget;
import com.jme.widget.WidgetContainerAbstract;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewport;

//import java.awt.LayoutManager2;
//import java.awt.BorderLayout;
/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetBorderLayout extends WidgetLayoutManager2 {

    private static final int CENTER = 0;
    private static final int EAST = 1;
    private static final int NORTH = 2;
    private static final int SOUTH = 3;
    private static final int WEST = 4;

    // for Western, top-to-bottom, left-to-right orientations

    private int hgap;
    private int vgap;

    private Widget centerWidget;
    private Vector2f centerDim;

    private Widget eastWidget;
    private Vector2f eastDim;

    private Widget northWidget;
    private Vector2f northDim;

    private Widget southWidget;
    private Vector2f southDim;

    private Widget westWidget;
    private Vector2f westDim;

    private DistributedSize dsWCE = new DistributedSize();
    private DistributedSize dsNCS = new DistributedSize();

    private class DistributedSize {
        int startSize, centerSize, endSize;
        void set(int startSize, int centerSize, int endSize) {
            this.startSize = startSize;
            this.centerSize = centerSize;
            this.endSize = endSize;
        }
    }

    public WidgetBorderLayout() {}

    public WidgetBorderLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    public void addLayoutWidget(Widget widget, Object constraints) {
        WidgetBorderLayoutConstraint c = (WidgetBorderLayoutConstraint) constraints;

        if (c == WidgetBorderLayoutConstraint.CENTER) {
            centerWidget = widget;
        } else if (c == WidgetBorderLayoutConstraint.EAST) {
            eastWidget = widget;
        } else if (c == WidgetBorderLayoutConstraint.NORTH) {
            northWidget = widget;
        } else if (c == WidgetBorderLayoutConstraint.SOUTH) {
            southWidget = widget;
        } else if (c == WidgetBorderLayoutConstraint.WEST) {
            westWidget = widget;
        }

    }

    public void addLayoutWidget(Widget w) {}

    public void removeLayoutWidget(Widget w) {}

    private void setupDimensions() {

        centerDim = centerWidget != null && centerWidget.isVisible() ? centerWidget.getPreferredSize() : new Vector2f();
        eastDim = eastWidget != null && eastWidget.isVisible() ? eastWidget.getPreferredSize() : new Vector2f();
        northDim = northWidget != null && northWidget.isVisible() ? northWidget.getPreferredSize() : new Vector2f();
        southDim = southWidget != null && southWidget.isVisible() ? southWidget.getPreferredSize() : new Vector2f();
        westDim = westWidget != null && westWidget.isVisible() ? westWidget.getPreferredSize() : new Vector2f();

    }

    public Vector2f preferredLayoutSize(WidgetContainerAbstract parent) {
        if (parent.isVisible() == false)
            return new Vector2f();

        setupDimensions();

        int minWidth = (int) (northDim.x > southDim.x ? northDim.x : southDim.x);

        int width =
            getSizeSum(
                (int) westDim.x,
                (int) centerDim.x,
                (int) eastDim.x,
                minWidth,
                this.hgap,
                westWidget != null,
                centerWidget != null,
                eastWidget != null);

        int minHeight = (int) (westDim.y > eastDim.y ? westDim.y : eastDim.y);

        int height =
            getSizeSum(
                (int) southDim.y,
                (int) centerDim.y,
                (int) northDim.y,
                minHeight,
                this.vgap,
                southWidget != null,
                centerWidget != null,
                northWidget != null);

        WidgetInsets insets = parent.getInsets();

        WidgetBorder border = parent.getBorder();

        Vector2f ret = new Vector2f();
        ret.x = width + insets.getLeft() + insets.getRight() + border.getLeft() + border.getRight();
        ret.y = height + insets.getTop() + insets.getBottom() + border.getTop() + border.getBottom();

        return ret;

    }

    public Vector2f minimumLayoutSize(WidgetContainerAbstract parent) {
        return preferredLayoutSize(parent);
    }

    public Vector2f maximumLayoutSize(WidgetContainerAbstract target) {
        Vector2f ret = new Vector2f();
        ret.x = ret.y = 3000;
        return ret;
    }

    public void layoutContainer(WidgetContainerAbstract parent) {
        if (parent.isVisible() == false)
            return;

        setupDimensions();

        int x = 0, y = 0;
        WidgetInsets insets = parent.getInsets();

        Vector2f parentDim = new Vector2f();
        parentDim.x = parent.getWidth() - insets.getLeft() + insets.getRight();
        parentDim.y = parent.getHeight() - insets.getTop() + insets.getBottom();

        x += insets.getLeft();
        y += insets.getTop();

        dsWCE.set((int) westDim.x, (int) centerDim.x, (int) eastDim.x);
        distributeSizes(dsWCE, (int) parentDim.x, this.hgap, westWidget != null, centerWidget != null, eastWidget != null);
        westDim.x = dsWCE.startSize;
        centerDim.x = dsWCE.centerSize;
        eastDim.x = dsWCE.endSize;

        dsNCS.set((int) southDim.y, (int) centerDim.y, (int) northDim.y);
        distributeSizes(this.dsNCS, (int) parentDim.y, this.vgap, southWidget != null, centerWidget != null, northWidget != null);
        southDim.y = dsNCS.startSize;
        centerDim.y = dsNCS.centerSize;
        northDim.y = dsNCS.endSize;

        eastDim.y = centerDim.y;
        westDim.y = centerDim.y;

        northDim.x = parentDim.x;
        southDim.x = parentDim.x;

        if (centerWidget != null && centerWidget.isVisible()) {
            centerWidget.setLocation((int) (x + westDim.x + horzGap(westWidget)), (int) (y + southDim.y + vertGap(southWidget)));
            centerWidget.setSize(centerDim);
        }

        if (eastWidget != null && eastWidget.isVisible()) {
            eastWidget.setLocation(
                (int) (x + westDim.x + centerDim.x + horzGap(westWidget) + horzGap(centerWidget)),
                (int) (y + southDim.y + vertGap(southWidget)));
            eastWidget.setSize(eastDim);
        }

        if (southWidget != null && southWidget.isVisible()) {
            southWidget.setLocation(x, y);
            southWidget.setSize(southDim);
        }

        if (northWidget != null && northWidget.isVisible()) {
            northWidget.setLocation(x, (int) (y + southDim.y + centerDim.y + vertGap(southWidget) + vertGap(centerWidget)));
            northWidget.setSize(northDim);
        }

        if (westWidget != null && westWidget.isVisible()) {
            westWidget.setLocation(x, (int) (y + southDim.y + vertGap(southWidget)));
            westWidget.setSize(westDim);
        }

    }

    private void distributeSizes(DistributedSize ds, int totalSize, int gap, boolean hasStart, boolean hasCenter, boolean hasEnd) {
        if (hasStart && hasCenter)
            totalSize -= gap;
        if (hasCenter && hasEnd)
            totalSize -= gap;
        if (hasEnd && hasStart && !hasCenter)
            totalSize -= gap;

        if (ds.startSize + ds.centerSize + ds.endSize <= totalSize)
            ds.centerSize = totalSize - ds.startSize - ds.endSize;
        else {
            float sum = ds.startSize + ds.endSize;

            if (sum < totalSize) {

                ds.centerSize = (int) (totalSize - sum);

            } else {

                ds.startSize = (int) (totalSize * (ds.startSize / sum));
                ds.centerSize = (int) (totalSize * (ds.centerSize / sum));

                ds.endSize = totalSize - ds.startSize - ds.centerSize;
            }

        }
    }

    private int getSizeSum(int left, int center, int right, int min, int gap, boolean hasLeft, boolean hasCenter, boolean hasRight) {
        int sum = left + center + right;

        if (hasLeft && hasCenter)
            sum += gap;
        if (hasCenter && hasRight)
            sum += gap;
        if (hasRight && hasLeft && !hasCenter)
            sum += gap;

        return sum > min ? sum : min;
    }

    private int horzGap(Widget w) {
        if (w != null)
            return hgap;
        else
            return 0;
    }

    private int vertGap(Widget w) {
        if (w != null)
            return vgap;
        else
            return 0;
    }

    public int getLayoutAlignmentX(WidgetContainerAbstract target) {
        return 0;
    }

    public int getLayoutAlignmentY(WidgetContainerAbstract target) {
        return 0;
    }

    public void invalidateLayout(WidgetContainerAbstract target) {}

    protected WidgetBoundingRectangle calcVisiblityRect(Widget w) {
        WidgetBoundingRectangle r = null;

        Widget p = w.getWidgetParent();

        WidgetViewport vp = null;

        if (p != null) {
            vp = p.getViewport();

            r = new WidgetBoundingRectangle(true);

            Vector2f d = null;
            Vector2f l = w.getAbsoluteLocation();

            if (centerWidget == w && centerDim != null) {
                d = centerDim;
            } else if (eastWidget == w && eastDim != null) {
                d = eastDim;
            } else if (southWidget == w && southDim != null) {
                d = southDim;
            } else if (northWidget == w && northDim != null) {
                d = northDim;
            } else if (westWidget == w && westDim != null) {
                d = westDim;
            }

            if (d != null) {
                r.setMin(l);
                r.setWidthHeight(d.x, d.y);
                r = WidgetBoundingRectangle.clip(r, vp);
            }
        }

        return r;
    }

}
