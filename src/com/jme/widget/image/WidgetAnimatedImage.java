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

import java.util.List;

import com.jme.image.Image;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.renderer.WidgetRendererFactory;


/**
 * <code>WidgetAnimatedImage</code>
 * @author Mike Kienenberger
 *
 * <code>WidgetAnimatedImage</code> is a widget that draws an animated 2d image.
 *
 * The image may be scaled by four methods:
 *   SCALE_MODE_NONE - The image will not be scaled.
 *   SCALE_MODE_SIZE_TO_FIT - The image will be scaled to fill the size of the widget.
 *   SCALE_MODE_ABSOLUTE - The image is scaled vertically and horizontally by a percentage of the original size of the image.
 *   SCALE_MODE_RELATIVE - The image is scaled vertically and horizontally by a percentage of the size of the widget.
 *
 * @since 0.6
 * @version $$Id: WidgetAnimatedImage.java,v 1.2 2004-09-14 21:52:09 mojomonkey Exp $$
 */
public class WidgetAnimatedImage extends WidgetImage {
    private static final long serialVersionUID = 1L;
	protected List imageList = null;
    int imageIndex = 0;
    
    public WidgetAnimatedImage() {
        this(null, WidgetAlignmentType.ALIGN_NONE, SCALE_MODE_SIZE_TO_FIT);
    }

    public WidgetAnimatedImage(List imageList) {
        this(imageList, WidgetAlignmentType.ALIGN_NONE, SCALE_MODE_SIZE_TO_FIT);
    }

    public WidgetAnimatedImage(List imageList, WidgetAlignmentType alignment, int scaleMode) {
        super(null, alignment, scaleMode);

        if (imageList.size() < 1)  throw new RuntimeException("imageList must contain at least one item.");

        this.imageList = imageList;

        imageIndex = 0;
        Image image = (Image)imageList.get(imageIndex);
        this.setImage(image);
    }

    public void initWidgetRenderer() {
        setWidgetRenderer(WidgetRendererFactory.getFactory().getRenderer(this));
    }
    
    public void update()
    {
        imageIndex = imageIndex + 1;
        if (imageIndex >= imageList.size())  imageIndex = 0;
        
        Image image = (Image)imageList.get(imageIndex);
        this.setImage(image);
    }
    
    public void reset()
    {
        imageIndex = 0;
        Image image = (Image)imageList.get(imageIndex);
        this.setImage(image);
    }
    
    public void setImageIndexTo(int newIndex)
    {
        imageIndex = newIndex;
        Image image = (Image)imageList.get(imageIndex);
        this.setImage(image);
    }
}
