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
package com.jme.widget.image;

import com.jme.image.Image;
import com.jme.math.Vector2f;
import com.jme.renderer.Renderer;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractImpl;
import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetRenderer;
import com.jme.widget.impl.lwjgl.WidgetLWJGLImage;


/**
 * <code>ImageWidget</code>
 * @author Mike Kienenberger
 *
 * <code>ImageWidget</code> is a widget that draws a 2d image.
 *
 * The image may be scaled by four methods:
 *   SCALE_MODE_NONE - The image will not be scaled.
 *   SCALE_MODE_SIZE_TO_FIT - The image will be scaled to fill the size of the widget.  Alignment is unused.
 *   SCALE_MODE_ABSOLUTE - The image is scaled vertically and horizontally by a percentage of the original size of the image.
 *   SCALE_MODE_RELATIVE - The image is scaled vertically and horizontally by a percentage of the size of the widget.
 *
 * KNOWN ISSUES:
 *  Drawing isn't clipped to the widget bounds.
 *  Alignment code doesn't work properly.
 *
 * @since 0.6
 * @version $$Id: WidgetImage.java,v 1.7 2004-07-20 19:47:47 Mojomonkey Exp $$
 */
public class WidgetImage extends WidgetAbstractImpl {

    public final static int SCALE_MODE_NONE = 0;
    public final static int SCALE_MODE_SIZE_TO_FIT = 1;
    public final static int SCALE_MODE_ABSOLUTE = 2;
    public final static int SCALE_MODE_RELATIVE = 3;

    private int scaleMode = SCALE_MODE_SIZE_TO_FIT;
    private float scaleHorizontal = 1f;
    private float scaleVertical = 1f;

    private Image image;

    public WidgetImage() {
        this(null, WidgetAlignmentType.ALIGN_NONE, SCALE_MODE_SIZE_TO_FIT);
    }

    public WidgetImage(Image image) {
        this(image, WidgetAlignmentType.ALIGN_NONE, SCALE_MODE_SIZE_TO_FIT);
    }

    public WidgetImage(Image image, WidgetAlignmentType alignment, int scaleMode) {
        super();

        setImage(image);
        setAlignment(alignment);
        setScaleMode(scaleMode);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void onDraw(Renderer r) {
        super.onDraw(r);
    }

    public void draw(Renderer r) {
        r.draw(getWidgetRenderer());
    }

    public void drawBounds(Renderer r) {
        //ignore
    }

    public float getHorizontalScale() {
        return scaleHorizontal;
    }

    public void setHorizontalScale(float f) {
        scaleHorizontal = f;
    }

    public float getVerticalScale() {
        return scaleVertical;
    }

    public void setVerticalScale(float f) {
        scaleVertical = f;
    }

    public int getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(int mode) {
        scaleMode = mode;
    }

    public Vector2f getPreferredSize() {
       return new Vector2f( image.getWidth(), image.getHeight());
    }

    public void doMouseButtonDown() {
    }

    public void doMouseButtonUp() {
    }

    public void setSize(Vector2f size) {
        updateWorldBound();
    }

    public void setSize(int width, int height) {
        updateWorldBound();
    }
    
    public void setForceView(boolean value) {
    	forceView = value;
    }

    public void setPreferredSize(Vector2f size) {
    }

    // TODO: Fix alignment code for non-SCALE_MODE_SIZE_TO_FIT
    protected void alignCenter(Vector2f size, WidgetInsets insets) {
        if (SCALE_MODE_SIZE_TO_FIT == scaleMode)  return;
        else if (SCALE_MODE_ABSOLUTE == scaleMode)
        {
            int x = getX() + (int) ((size.x / 2) - (getWidth() * getHorizontalScale() / 2));
            int y = getY() + (int) ((size.y / 2) - (getHeight() * getVerticalScale() / 2));
            setLocation(x, y);
        }
        else super.alignCenter(size, insets);
    }

//    protected void alignWest(Vector2f size, WidgetInsets insets) {
//       setX(insets.left);
//   }
//
//   protected void alignEast(Vector2f size, WidgetInsets insets) {
//       setX((int) (size.x - getWidth() - insets.right));
//   }
//
//   protected void alignNorth(Vector2f size, WidgetInsets insets) {
//       setY((int) (size.y - getHeight() - insets.top));
//   }
//
//   protected void alignSouth(Vector2f size, WidgetInsets insets) {
//       setY(insets.bottom);
//   }
//
   protected void alignWest(Vector2f size, WidgetInsets insets) {
        if (SCALE_MODE_SIZE_TO_FIT == scaleMode)  return;

        super.alignWest(size, insets);
    }

    protected void alignEast(Vector2f size, WidgetInsets insets) {
        if (SCALE_MODE_SIZE_TO_FIT == scaleMode)  return;

        super.alignEast(size, insets);
    }

    protected void alignNorth(Vector2f size, WidgetInsets insets) {
        if (SCALE_MODE_SIZE_TO_FIT == scaleMode)  return;

        super.alignNorth(size, insets);
    }

    protected void alignSouth(Vector2f size, WidgetInsets insets) {
        if (SCALE_MODE_SIZE_TO_FIT == scaleMode)  return;

        super.alignSouth(size, insets);
    }

    public void setLocation(int x, int y) {
        float w = localBound.getWidth();
        float h = localBound.getHeight();

        super.setLocation(x, y);

        localBound.setWidthHeight(w, h);
        updateWorldBound();
    }

    public void setLocation(Vector2f at) {
        float w = localBound.getWidth();
        float h = localBound.getHeight();

        super.setLocation(at);

        localBound.setWidthHeight(w, h);
        updateWorldBound();
    }

    public void setX(int x) {
        float w = localBound.getWidth();
        super.setX(x);
        localBound.setWidth(w);
        updateWorldBound();
    }

    public void setY(int y) {
        float h = localBound.getHeight();
        super.setY(y);
        localBound.setHeight(h);
        updateWorldBound();
    }

    // TODO: handle in WidgetRendererFactory
    public void initWidgetRenderer() {
        WidgetRenderer renderer = null;

        renderer = new WidgetLWJGLImage(this);

        setWidgetRenderer(renderer);
    }

}
