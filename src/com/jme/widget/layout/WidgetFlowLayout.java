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

import java.util.ArrayList;

import com.jme.math.Vector2f;

import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetContainerAbstract;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;

/**
 * @author pattogo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetFlowLayout extends WidgetLayoutManager {

    private int hgap;
    private int vgap;

    private boolean wrap = true;

    private WidgetAlignmentType alignmentType = WidgetAlignmentType.ALIGN_CENTER;
    private WidgetFillType fillType = WidgetFillType.NONE;

    public WidgetFlowLayout() {
        super();
    }

    public WidgetFlowLayout(WidgetAlignmentType type) {
        this.alignmentType = type;
    }

    public WidgetFlowLayout(WidgetFillType type) {
        this.fillType = type;
    }

    public WidgetFlowLayout(WidgetAlignmentType alignmentType, WidgetFillType fillType) {
        this.alignmentType = alignmentType;
        this.fillType = fillType;
    }

    public WidgetFlowLayout(WidgetAlignmentType type, int hgap, int vgap) {
        this.alignmentType = type;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    public WidgetFlowLayout(WidgetAlignmentType alignmentType, WidgetFillType fillType, int hgap, int vgap) {
        this.alignmentType = alignmentType;
        this.fillType = fillType;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    protected Vector2f calcLayout(WidgetContainerAbstract parent, boolean setPosSize) {
        Vector2f ret = new Vector2f();

        if (parent.isVisible() == false)
            return ret;

        int totalChildren = parent.getWidgetCount();

        if (totalChildren > 0) {

            Widget child;
            Vector2f size;

            boolean pastWidth;
            boolean pastMaxWidth;
            boolean pastParentWidth;
            boolean lastChild;
            boolean atRowStart;

            WidgetInsets insets = parent.getInsets();
            WidgetBorder border = parent.getBorder();

            float parentWidth = parent.getWidth();
            float parentHeight = parent.getHeight();

            float widthStart = 0;
            float curWidth = widthStart;

            float heightStart = parentHeight - (border.getTop() + insets.getTop());
            float maxRowHeight = 0;

            float xPos = widthStart;
            float yPos = heightStart;

            int idx;
            int rowStartIdx;

            ArrayList preferredSizes = new ArrayList();
            ArrayList newBounds = new ArrayList();

            WidgetBoundingRectangle r;

            for (idx = 0; idx < totalChildren; idx++) {

                child = parent.getWidget(idx);

                size = child.getPreferredSize();

                if (setPosSize == true) {

                    if (fillType == WidgetFillType.HORIZONTAL) {
                        size.x = parentWidth;
                    } else if (fillType == WidgetFillType.VERTICAL) {
                        size.y = parentHeight;
                    } else if (fillType == WidgetFillType.VERTICAL_HORIZONTAL) {
                        size.x = parentWidth;
                        size.y = parentHeight;
                    }
                }

                preferredSizes.add(size);

                r = new WidgetBoundingRectangle();

                r.setSize(size);

                newBounds.add(r);
            }

            for (idx = rowStartIdx = 0; idx < totalChildren; idx++) {

                if (curWidth != widthStart) {
                    curWidth += hgap;
                }

                child = parent.getWidget(idx);

                if (child.isVisible()) {

                    size = (Vector2f) preferredSizes.get(idx);

                    r = (WidgetBoundingRectangle) newBounds.get(idx);

                    curWidth += size.x;

                    pastMaxWidth = (maximumSize.x > 0 && maximumSize.x < curWidth);
                    pastParentWidth = (!pastMaxWidth && wrap && parentWidth > 0 && parentWidth < curWidth);
                    
                    pastWidth = pastMaxWidth || pastParentWidth;
                    
                    lastChild = (idx + 1 == totalChildren);
                    atRowStart = (xPos == widthStart);

                    if ((!pastWidth || atRowStart) && size.y > maxRowHeight) {
                        maxRowHeight = size.y;
                    }

                   
                    if (pastWidth || lastChild) {
                        
                        /* 
                         * Passed max width 
                         * OR
                         * Passe width of the parent
                         * OR
                         * Last child
                         */

                        if ((!atRowStart && pastWidth)) {
                            /* 
                             * Not the first child on a row
                             * AND
                             * Not the last child
                             * SO
                             * process this child again
                             */
                            idx--;

                        } else if (atRowStart || lastChild) {
                            /*
                             * First child on a row
                             * OR
                             * Last child
                             */

                            r.setMinXPreserveSize(xPos); //Set the x location (left)

                        }

                        /*
                         * Set height and y position
                         */

                        yPos -= maxRowHeight;

                        for (int i = rowStartIdx; i <= idx; i++) {

                            r = (WidgetBoundingRectangle) newBounds.get(i);

                            r.setMinYPreserveSize(yPos); //Set y location (bottom)

                            /*
                             * Set size to preferred width and max row height
                             */
                            r.setHeight(maxRowHeight);
                        }

                        if (idx + 1 != totalChildren) {
                            /*
                             * NOT the last child
                             * SO
                             * Setup for a new row
                             */
                            xPos = curWidth = widthStart;

                            if (yPos != heightStart) { //NOT first row
                                yPos -= vgap; //Add vertical gap
                            }

                            rowStartIdx = idx + 1;
                            maxRowHeight = 0;
                        } else {
                            /*
                             * Last child
                             * SO
                             * Stop
                             */
                            break;
                        }

                    } else {

                        /*
                         * NOT passed max width
                         * AND
                         * NOT last child
                         */

                        r.setMinXPreserveSize(xPos); //Set the x location (left)

                        if (xPos != widthStart) { //NOT first in row
                            xPos += hgap; //Add horizontal gap
                        }

                        xPos += size.x;
                    }
                }
            }

            WidgetBoundingRectangle bounds = new WidgetBoundingRectangle((WidgetBoundingRectangle) newBounds.get(0));

            for (idx = 0; idx < totalChildren; idx++) {

                child = parent.getWidget(idx);

                if (child.isVisible()) {

                    r = (WidgetBoundingRectangle) newBounds.get(idx);

                    bounds = (WidgetBoundingRectangle) bounds.merge(r);

                    if (setPosSize == true) {
                        Vector2f l = r.getMin();
                        Vector2f s = r.getSize();
                        
                        // TODO:  Handle alignment here    
//                        if (this.alignmentType == WidgetAlignmentType.ALIGN_CENTER)
//                            l.x = (int) (l.x + (parentWidth.x - rWidth) / 2);
//                        else if (this.alignmentType == WidgetAlignmentType.ALIGN_EAST)
//                            leftPos = (int) (x + (parentDim.x - rWidth));
                        
                        child.setLocation(l);
                        child.setSize(s);
                    }
                }

                ret.x = bounds.getWidth();
                ret.y = bounds.getHeight();

            }
        }

        return ret;
    }

    public Vector2f preferredLayoutSize(WidgetContainerAbstract parent) {
        if (parent.isVisible() == false)
            return new Vector2f();

        Vector2f ret = calcLayout(parent, false);

        WidgetInsets insets = parent.getInsets();
        WidgetBorder border = parent.getBorder();

        ret.x += border.getLeft() + border.getRight() + insets.getLeft() + insets.getRight();
        ret.y += border.getTop() + border.getBottom() + insets.getBottom() + insets.getTop();

        return ret;
    }

    public void layoutContainer(WidgetContainerAbstract parent) {
        calcLayout(parent, true);
    }

    //    public void layoutContainer(WidgetContainerAbstract parent) {
    //        if (parent.isVisible() == false)
    //            return;
    //
    //        Widget w;
    //
    //        int childCnt = parent.getWidgetCount();
    //
    //        if (childCnt == 0)
    //            return;
    //
    //        WidgetInsets insets = parent.getInsets();
    //        WidgetBorder border = parent.getBorder();
    //
    //
    //        Vector2f parentDim = new Vector2f();
    //        parentDim.x = parent.getWidth() - (insets.getLeft() + insets.getRight() + border.getLeft() + border.getRight());
    //        parentDim.y =
    //            parent.getHeight() - (insets.getTop() + insets.getBottom() + border.getTop() + border.getBottom());
    //
    //        int x = 0, y = (int) parentDim.y;
    //
    //        x += insets.getLeft();
    //        y += insets.getBottom();
    //
    //        int childIdx = 0;
    //        int curRow = 0;
    //        Vector2f[] dims = new Vector2f[childCnt];
    //
    //        while (childIdx < childCnt) {
    //            int rWidth = 0;
    //            int column = 0;
    //            int childIdx1 = childIdx;
    //            int maxH = 0;
    //
    //            while (childIdx < childCnt) {
    //
    //                w = parent.getWidget(childIdx);
    //                Vector2f prefDim = w.isVisible() ? w.getPreferredSize() : new Vector2f();
    //
    //                if (fillType == WidgetFillType.HORIZONTAL) {
    //                    prefDim.x = parentDim.x;
    //                } else if (fillType == WidgetFillType.VERTICAL) {
    //                    prefDim.y = parentDim.y;
    //                } else if (fillType == WidgetFillType.VERTICAL_HORIZONTAL) {
    //                    prefDim.x = parentDim.x;
    //                    prefDim.y = parentDim.y;
    //                }
    //
    //                if (column != 0)
    //                    rWidth += hgap;
    //
    //                dims[childIdx - childIdx1] = prefDim;
    //
    //                rWidth += prefDim.x;
    //
    //                if (column > 0 && rWidth + prefDim.x > parentDim.x)
    //                    break;
    //
    //
    //                if (prefDim.y > maxH)
    //                    maxH = (int) prefDim.y;
    //
    //                childIdx++;
    //                column++;
    //
    //                if (rWidth + prefDim.x > parentDim.x)
    //                    break;
    //
    //            }
    //
    //            if (column == 0)
    //                break;
    //
    //            int leftPos = x;
    //
    //            if (this.alignmentType == WidgetAlignmentType.ALIGN_CENTER)
    //                leftPos = (int) (x + (parentDim.x - rWidth) / 2);
    //            else if (this.alignmentType == WidgetAlignmentType.ALIGN_EAST)
    //                leftPos = (int) (x + (parentDim.x - rWidth));
    //
    //            column = 0;
    //            int rowStart = childIdx1;
    //
    //            if (curRow != 0) {
    //                y -= vgap;
    //            }
    //
    //            while (childIdx1 < childIdx) {
    //                Vector2f dim = dims[childIdx1 - rowStart];
    //
    //                if (column != 0)
    //                    leftPos += hgap;
    //
    //                int yPos = (int) (y - ((maxH - dim.y) / 2) - maxH);
    //
    //                w = parent.getWidget(childIdx1);
    //
    //                w.setLocation(leftPos, yPos);
    //                w.setSize(dim);
    //
    //                leftPos += dim.x;
    //                ++column;
    //                ++childIdx1;
    //            }
    //
    //            y -= maxH;
    //            ++curRow;
    //
    //        }
    //
    //    }

    public int getHgap() {
        return hgap;
    }

    public void setHgap(int i) {
        hgap = i;
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int i) {
        vgap = i;
    }

    public WidgetAlignmentType getAlignmentType() {
        return alignmentType;
    }

    public void setAlignmentType(WidgetAlignmentType type) {
        alignmentType = type;
    }

    public WidgetFillType getFillType() {
        return fillType;
    }

    public void setFillType(WidgetFillType type) {
        fillType = type;
    }

    public void addLayoutWidget(Widget w) {}

    public void removeLayoutWidget(Widget w) {}

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean b) {
        wrap = b;
    }

}
