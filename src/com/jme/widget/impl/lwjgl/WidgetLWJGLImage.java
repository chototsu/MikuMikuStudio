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
package com.jme.widget.impl.lwjgl;

import org.lwjgl.opengl.GL11;

import com.jme.image.Image;
import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.image.WidgetImage;

/**
 * <code>WidgetLWJGLImage</code>
 * @author Mike Kienenberger
 *
 * @since 0.6
 * @version $$Id: WidgetLWJGLImage.java,v 1.3 2004-04-24 15:26:48 mojomonkey Exp $$
 */
public class WidgetLWJGLImage extends WidgetLWJGLAbstractRenderer {

    /**
     * @param w widget
     */
    public WidgetLWJGLImage(Widget w) {
        super(w);
    }

    /** <code>render</code>
     *
     * @see com.jme.widget.WidgetRenderer#render()
     */
    public void render() {
        WidgetImage wt = (WidgetImage) getWidget();

        initWidgetProjection(wt);

        // TODO: clip drawing to widget bounds.

        Image image = wt.getImage();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int widgetWidth = wt.getWidth();
        int widgetHeight = wt.getHeight();

        int newImageWidth = -1;
        int newImageHeight = -1;

        boolean needsAlignment = (wt.getAlignment() != WidgetAlignmentType.ALIGN_NONE);
        
        if (WidgetImage.SCALE_MODE_SIZE_TO_FIT == wt.getScaleMode())
        {
            float xfactor = ((float)widgetWidth) / ((float)imageWidth);
            float yfactor = ((float)widgetHeight) / ((float)imageHeight);
            GL11.glPixelZoom(xfactor, yfactor);
            newImageWidth = widgetWidth;
            newImageHeight = widgetHeight;
        }
        else if (WidgetImage.SCALE_MODE_ABSOLUTE == wt.getScaleMode())
        {
            GL11.glPixelZoom(wt.getHorizontalScale(), wt.getVerticalScale());
            newImageWidth = (int)(imageWidth * wt.getHorizontalScale());
            newImageHeight = (int)(imageHeight * wt.getVerticalScale());
        }
        else if (WidgetImage.SCALE_MODE_NONE == wt.getScaleMode())
        {
            GL11.glPixelZoom(1f, 1f);
            newImageWidth = imageWidth;
            newImageHeight = imageHeight;
        }
        else if (WidgetImage.SCALE_MODE_RELATIVE == wt.getScaleMode())
        {
            float xfactor = ((float)widgetWidth) / ((float)imageWidth) * wt.getHorizontalScale();
            float yfactor = ((float)widgetHeight) / ((float)imageHeight) * wt.getVerticalScale();
            GL11.glPixelZoom(xfactor, yfactor);
            newImageWidth = (int)(imageWidth * xfactor);
            newImageHeight = (int)(imageHeight * yfactor);
        }
            
        needsAlignment = needsAlignment && ((newImageWidth < widgetWidth) || (newImageHeight < widgetHeight));
        
        if (needsAlignment)
        {
            int xAdjustment = 0;
            int yAdjustment = 0;
            
            if (WidgetAlignmentType.ALIGN_NORTHWEST == wt.getAlignment())
            {
                xAdjustment = 0;
                yAdjustment = widgetHeight - newImageHeight;
            }
            else if (WidgetAlignmentType.ALIGN_NORTH == wt.getAlignment())
            {
                xAdjustment = (int) ((widgetWidth - newImageWidth) / 2);
                yAdjustment = widgetHeight - newImageHeight;
            }
            else if (WidgetAlignmentType.ALIGN_NORTHEAST == wt.getAlignment())
            {
                xAdjustment = widgetWidth - newImageWidth;
                yAdjustment = widgetHeight - newImageHeight;
            }
            else if (WidgetAlignmentType.ALIGN_WEST == wt.getAlignment())
            {
                xAdjustment = 0;
                yAdjustment = (int) ((widgetHeight - newImageHeight) / 2);
            }
            if (WidgetAlignmentType.ALIGN_CENTER == wt.getAlignment())
            {
                xAdjustment = (int) ((widgetWidth - newImageWidth) / 2);
                yAdjustment = (int) ((widgetHeight - newImageHeight) / 2);
            }
            else if (WidgetAlignmentType.ALIGN_EAST == wt.getAlignment())
            {
                xAdjustment = widgetWidth - newImageWidth;
                yAdjustment = (int) ((widgetHeight - newImageHeight) / 2);
            }
            else if (WidgetAlignmentType.ALIGN_SOUTHWEST == wt.getAlignment())
            {
                xAdjustment = 0;
                yAdjustment = 0;
            }
            if (WidgetAlignmentType.ALIGN_SOUTH == wt.getAlignment())
            {
                xAdjustment = (int) ((widgetWidth - newImageWidth) / 2);
                yAdjustment = 0;
            }
            else if (WidgetAlignmentType.ALIGN_SOUTHEAST == wt.getAlignment())
            {
                xAdjustment = widgetWidth - newImageWidth;
                yAdjustment = 0;
            }
            else if (WidgetAlignmentType.ALIGN_NONE == wt.getAlignment())
            {
                // should never get here
            }
            GL11.glRasterPos2i(wt.getX() + xAdjustment, wt.getY() + yAdjustment);
        }
        else
        {
            GL11.glRasterPos2i(wt.getX(), wt.getY());
        }

         GL11.glDrawPixels(imageWidth, imageHeight, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image.getData());
        
        // reset pixel zoom
        GL11.glPixelZoom(1f, 1f);

        resetWidgetProjection();

    }

}
