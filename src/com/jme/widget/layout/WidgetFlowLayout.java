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
package com.jme.widget.layout;

import java.util.ArrayList;
import java.util.Iterator;

import com.jme.math.Vector2f;

import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.bounds.WidgetBoundingRectangle;

/**
 * <code>WidgetFlowLayout</code>
 * @author Gregg Patton
 * @version $Id: WidgetFlowLayout.java,v 1.3 2004-02-09 12:34:43 greggpatton Exp $
 */
public class WidgetFlowLayout extends WidgetLayoutManager {

    private int hgap;
    private int vgap;

    private boolean wrap = true;

    private WidgetAlignmentType alignmentType = WidgetAlignmentType.ALIGN_CENTER;
    private WidgetFillType fillType = WidgetFillType.NONE;

    /**
     * <code>RowItem</code>
     */
    private class RowItem {
        Widget child;
        int childIdx;
        WidgetBoundingRectangle r;
    }

    /**
     * 
     */
    public WidgetFlowLayout() {
        super();
    }

    /**
     * @param type
     */
    public WidgetFlowLayout(WidgetAlignmentType type) {
        this.alignmentType = type;
    }

    /**
     * @param type
     */
    public WidgetFlowLayout(WidgetFillType type) {
        this.fillType = type;
    }

    /**
     * @param hgap
     * @param vgap
     */
    public WidgetFlowLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * @param alignmentType
     * @param fillType
     */
    public WidgetFlowLayout(WidgetAlignmentType alignmentType, WidgetFillType fillType) {
        this.alignmentType = alignmentType;
        this.fillType = fillType;
    }

    /**
     * @param type
     * @param hgap
     * @param vgap
     */
    public WidgetFlowLayout(WidgetAlignmentType type, int hgap, int vgap) {
        this.alignmentType = type;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * @param alignmentType
     * @param fillType
     * @param hgap
     * @param vgap
     */
    public WidgetFlowLayout(WidgetAlignmentType alignmentType, WidgetFillType fillType, int hgap, int vgap) {
        this.alignmentType = alignmentType;
        this.fillType = fillType;
        this.hgap = hgap;
        this.vgap = vgap;
    }

    /**
     * <code>calcLayout</code>
     * @param parent
     * @param setPosSize
     * @return
     */
    private Vector2f calcLayout(WidgetAbstractContainer parent, boolean setPosSize) {
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
            boolean fillHorizontal = (fillType == WidgetFillType.HORIZONTAL);

            WidgetInsets insets = parent.getInsets();

            float parentWidth = parent.getWidth();
            float parentHeight = parent.getHeight();

            float widthStart = insets.getLeft();
            float curWidth = widthStart;

            float heightStart = parentHeight - insets.getTop();
            float maxRowHeight = 0;

            float xPos = widthStart;
            float yPos = heightStart;

            int idx;
            int rowStartIdx;

            ArrayList preferredSizes = new ArrayList();
            ArrayList newBounds = new ArrayList();

            ArrayList rows = new ArrayList();
            ArrayList curRow = new ArrayList();

            WidgetBoundingRectangle r;

            for (idx = 0; idx < totalChildren; idx++) {

                child = parent.getWidget(idx);

                size = child.getPreferredSize();

                if (setPosSize == true) {

                    if (fillType == WidgetFillType.HORIZONTAL) {
                        size.x = parentWidth - (insets.getLeft() + insets.getRight());
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

                    pastMaxWidth = (maximumSize.x > 0 && maximumSize.x < curWidth || fillHorizontal);
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

                            if (!atRowStart)
                                xPos += hgap;

                            r.setMinXPreserveSize(xPos); //Set the x location (left)

                        }

                        /*
                         * Set height and y position
                         */

                        yPos -= maxRowHeight;

                        /*
                         * Initialize the current row array
                         */
                        curRow = new ArrayList();
                        
                        for (int i = rowStartIdx; i <= idx; i++) {

                            r = (WidgetBoundingRectangle) newBounds.get(i);

                            r.setMinYPreserveSize(yPos); //Set y location (bottom)

                            /*
                             * Set size to preferred width and max row height
                             */
                            r.setHeight(maxRowHeight);
                        
                            
                            /*
                             * Store RowItem in current row array
                             */
                            RowItem ri = new RowItem();
                            
                            ri.child = parent.getWidget(i);
                            ri.childIdx = i;         
                            ri.r = r;   
                                                
                            curRow.add(ri);
                        }

                        /*
                         * Add current row array to rows array
                         */
                        rows.add(curRow);

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

                        if (!atRowStart) { //NOT first in row
                            xPos += hgap; //Add horizontal gap
                        }

                        r.setMinXPreserveSize(xPos); //Set the x location (left)

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
                }

                ret.x = bounds.getWidth();
                ret.y = bounds.getHeight();

            }

            if (setPosSize == true) {

                float xOffset = 0;
                float innerXOffset = 0;
                float curRowWidth;

                RowItem ri;

                if (this.alignmentType == WidgetAlignmentType.ALIGN_CENTER)
                    xOffset = (int)(parentWidth - bounds.getWidth()) / 2f;
                else if (this.alignmentType == WidgetAlignmentType.ALIGN_EAST)
                    xOffset = (parentWidth - bounds.getWidth()) - (insets.getLeft() + insets.getRight());

                Iterator rowsI = rows.iterator();
                                    
                while (rowsI.hasNext()) {
                 
                    curRow = (ArrayList) rowsI.next();
                    
                    curRowWidth = calcRowWidth(curRow);

                    if (this.alignmentType == WidgetAlignmentType.ALIGN_CENTER) {
                        innerXOffset = (int)(bounds.getWidth() - curRowWidth) / 2f;
                    }

                    Iterator curRowI = curRow.iterator();
                    
                    while (curRowI.hasNext()) {
                 
                        ri = (RowItem) curRowI.next();    

                        Vector2f l = ri.r.getMin();
                        Vector2f s = ri.r.getSize();

                        l.x += xOffset + innerXOffset;

                        ri.child.setLocation(l);
                        ri.child.setSize(s);
                    }
                }
            }
        }

        return ret;
    }

    /**
     * <code>calcRowWidth</code>
     * @param curRow
     * @return
     */
    private float calcRowWidth(ArrayList curRow) {
        float ret = 0;
        
        
        RowItem ri;
        Iterator i = curRow.iterator();
        
        while (i.hasNext()) {
            ri = (RowItem) i.next();
            ret += ri.r.getWidth();
        }
        
        return ret;
    }

    /** <code>preferredLayoutSize</code> 
     * @param parent
     * @return
     * @see com.jme.widget.layout.WidgetLayoutManager#preferredLayoutSize(com.jme.widget.WidgetAbstractContainer)
     */
    public Vector2f preferredLayoutSize(WidgetAbstractContainer parent) {
        if (parent.isVisible() == false)
            return new Vector2f();

        Vector2f ret = calcLayout(parent, false);

        WidgetInsets insets = parent.getInsets();
        WidgetBorder border = parent.getBorder();

        ret.x += border.getLeft() + border.getRight() + insets.getLeft() + insets.getRight();
        ret.y += border.getTop() + border.getBottom() + insets.getBottom() + insets.getTop();

        return ret;
    }

    /** <code>layoutContainer</code> 
     * @param parent
     * @see com.jme.widget.layout.WidgetLayoutManager#layoutContainer(com.jme.widget.WidgetAbstractContainer)
     */
    public void layoutContainer(WidgetAbstractContainer parent) {
        calcLayout(parent, true);
    }

    /**
     * <code>getHgap</code>
     * @return
     */
    public int getHgap() {
        return hgap;
    }

    /**
     * <code>setHgap</code>
     * @param i
     */
    public void setHgap(int i) {
        hgap = i;
    }

    /**
     * <code>getVgap</code>
     * @return
     */
    public int getVgap() {
        return vgap;
    }

    /**
     * <code>setVgap</code>
     * @param i
     */
    public void setVgap(int i) {
        vgap = i;
    }

    /**
     * <code>getAlignmentType</code>
     * @return
     */
    public WidgetAlignmentType getAlignmentType() {
        return alignmentType;
    }

    /**
     * <code>setAlignmentType</code>
     * @param type
     */
    public void setAlignmentType(WidgetAlignmentType type) {
        alignmentType = type;
    }

    /**
     * <code>getFillType</code>
     * @return
     */
    public WidgetFillType getFillType() {
        return fillType;
    }

    /**
     * <code>setFillType</code>
     * @param type
     */
    public void setFillType(WidgetFillType type) {
        fillType = type;
    }

    /**
     * <code>isWrap</code>
     * @return
     */
    public boolean isWrap() {
        return wrap;
    }

    /**
     * <code>setWrap</code>
     * @param b
     */
    public void setWrap(boolean b) {
        wrap = b;
    }

}
