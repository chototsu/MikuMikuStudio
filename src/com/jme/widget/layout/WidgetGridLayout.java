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
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;

/**
 * <code>WidgetGridLayout</code>
 * @author Gregg Patton
 * @version
 */
public class WidgetGridLayout extends WidgetLayoutManager {

    private int xSize = 1;
    private int ySize = 1;

    private int hgap;
    private int vgap;

    /**
     * @param xSize
     * @param ySize
     */
    public WidgetGridLayout(int xSize, int ySize) {

        this.xSize = xSize;
        this.ySize = ySize;

    }

    /**
     * @param xSize
     * @param ySize
     * @param horizontalGap
     * @param verticalGap
     */
    public WidgetGridLayout(int xSize, int ySize, int hgap, int vgap) {

        this.xSize = xSize;
        this.ySize = ySize;
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

		int totalGridSize = xSize * ySize;
        int totalChildren = parent.getWidgetCount();
        
        if (totalChildren > totalGridSize)
        	totalChildren = totalGridSize;
        
        float maxHeight = 0;
        float maxWidth = 0;

        ArrayList visibleChildren = new ArrayList();

        if (totalChildren > 0) {

            int idx;
            Widget child;
            Vector2f size;

            for (idx = 0; idx < totalChildren; idx++) {

                child = parent.getWidget(idx);

                if (child.isVisible()) {

                    size = child.getPreferredSize();

                    if (size.x > maxWidth)
                        maxWidth = size.x;
                    if (size.y > maxHeight)
                        maxHeight = size.y;
                        
                    visibleChildren.add(child);    

                }
            }
            
            ret.x = maxWidth * xSize;
            ret.y = maxHeight * ySize;
            
            if (setPosSize == true) {
                
                WidgetInsets insets = parent.getInsets();

				/*
				 * Get the width and height minus the parents insets
				 */
                int width = parent.getWidth() - (insets.getLeft() + insets.getRight());
                int height = parent.getHeight()- (insets.getTop() + insets.getBottom());
                
                /*
                 * Calculate the widget width.  
                 * Apply hgap if there will be more than one horizontal widget.  
                 */
                int widgetWidth = (int) ((width) / xSize) - (xSize > 1 ? hgap : 0);

                /*
                 * Calculate the widget height.  
                 * Apply vgap if there will be more than one vertical widget.
                 */
                int widgetHeight = (int) ((height ) / ySize) - (ySize > 1 ? vgap : 0);

				/*
				 * Calculate the starting point for the first widget in a row
				 * from left to right
				 */
                int xStart = insets.getLeft();

				/*
				 * Calculate the starting point for the first widget in a column
				 * from top to bottom
				 */
                int yStart = (int) (parent.getHeight() - (insets.getTop() + widgetHeight));

				/*
				 * Calculate the x increment
				 */
                int xChange = widgetWidth + hgap;
                
                /*
				 * Calculate the y increment
                 */
                int yChange = -(widgetHeight + vgap);

				/*
				 * Initialize the column counter
				 */
                int curX = 0;
                
                /*
                 * Initialize the row counter
                 */
                int curY = 0;

				/*
				 * Calculate the remaing space on the row from the width so
				 * it can be spread horizontally across the widgets.
				 */
				int widthErrorRemainder = calcWidthRemainder(xSize, width, widgetWidth);                

				/*
				 * Calculate the remaing space on the column from the height so
				 * it can be spread vertically across the widgets.
				 */
                int heightErrorRemainder = ySize > 0 && widgetHeight > 0 ? height % widgetHeight : 0;                

				/*
				 * Used to apply the width error
				 */
                int widthError;
                
                /*
                 * Used to apply the height error
                 */
                int heightError;                                

				/*
				 * Used to pad the last widget in the row so it
				 * will end on the correct point
				 */
				int widthPad;

				/*
				 * Used to pad the last widget in the column so it
				 * will end on the correct point
				 */
				int heightPad;                                

				/*
				 * Pixel position for the lower left corner
				 * of the current widget.
				 */
                int xPos = xStart;
                int yPos = yStart;
                
                Iterator i = visibleChildren.iterator();
                
                while (i.hasNext()) {
                    child = (Widget) i.next();

					/*
					 * If there is still some width error remaing
					 * then add one pixel to the widget width
					 */
                    widthError = (widthErrorRemainder > 0 ? 1 : 0);

					/*
					 * If there is still some height error remaing
					 * then add one pixel to the widget height
					 */
                    heightError = (heightErrorRemainder > 0 ? 1 : 0);

					/*
					 * If this is the last widget in the row, calclulate
					 * how much to pad the width to get it to reach the
					 * appropriate edge
					 */
					if (curX > 0 && curX + 1 >= xSize) {
						widthPad = (parent.getWidth() - insets.getRight()) - (xPos + widgetWidth + widthError); 
					} else {
						widthPad = 0;
					}
					
					/*
					 * If this is the last widget in the column, calclulate
					 * how much to pad the height to get it to reach the
					 * appropriate edge
					 */
					if (curY > 0 && curY + 1 >= ySize) {
						heightPad = (yPos - heightError) - (insets.getBottom()); 
					} else {
						heightPad = 0;
					}

                    child.setWidth(widgetWidth + widthError + widthPad);
                    
                    child.setHeight(widgetHeight + heightError + heightPad);
                             
                    child.setLocation(xPos, yPos - heightError - heightPad);
                    
                    curX ++;
                    

					/*
					 * If this is the last widget in the row
					 * reset to the beginning of the row and
					 * increment the y position
					 */
                    if (curX >= xSize) {
                        
                        xPos = xStart;
						curX = 0;       

						widthErrorRemainder = calcWidthRemainder(xSize, width, widgetWidth);                
                        
                        yPos += yChange - heightError;       

						curY ++;

                        heightErrorRemainder--;
                        
                    } else {
                        
                        xPos += xChange + widthError;

                        widthErrorRemainder--;
                        
                    }
                }
            }
        }
        
        return ret;
    }

	static private int calcWidthRemainder(int xSize, int width, int widgetWidth) {
		return xSize > 0 && widgetWidth > 0 ? width % widgetWidth : 0;
	}
    
    /**
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

    /**
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
     * <code>getXSize</code>
     * @return
     */
    public int getXSize() {
        return xSize;
    }

    /**
     * <code>setXSize</code>
     * @param i
     */
    public void setXSize(int i) {
        xSize = i;
    }

    /**
     * <code>getYSize</code>
     * @return
     */
    public int getYSize() {
        return ySize;
    }

    /**
     * <code>setYSize</code>
     * @param i
     */
    public void setYSize(int i) {
        ySize = i;
    }

}
