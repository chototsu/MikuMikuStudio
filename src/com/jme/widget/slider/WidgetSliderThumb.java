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
package com.jme.widget.slider;

import com.jme.math.Vector2f;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.WidgetTextureCoords;
import com.jme.widget.renderer.WidgetAbstractRenderer;
import com.jme.widget.renderer.WidgetRendererFactory;
import com.jme.widget.scroller.WidgetScrollerThumb;

/**
 * <code>WidgetSliderThumb</code>
 * @author Gregg Patton
 * @version $Id: WidgetSliderThumb.java,v 1.3 2004-09-14 21:52:26 mojomonkey Exp $
 */
public class WidgetSliderThumb extends WidgetScrollerThumb {

    private static final long serialVersionUID = 1L;
	private WidgetOrientationType orientationType = WidgetOrientationType.NONE;

    public WidgetSliderThumb() {
        super();
    }

    /**
     *
     */
    public WidgetSliderThumb(WidgetOrientationType type) {
        super();

        this.orientationType = type;

        setBorder(null);

        float p = WidgetAbstractRenderer.LOOK_AND_FEEL_PIXEL_SIZE;

        if (this.orientationType == WidgetOrientationType.DOWN) {

            setTextureCoords(new WidgetTextureCoords(p * 17f, p * 33f, p * 32f, p * 63f));

            setSize(new Vector2f(15, 30));
            setPreferredSize(getSize());

        } else if (this.orientationType == WidgetOrientationType.UP) {

            setTextureCoords(new WidgetTextureCoords(p * 1f, p * 33f, p * 16f, p * 63f));

            setSize(new Vector2f(15, 30));
            setPreferredSize(getSize());

        } else if (this.orientationType == WidgetOrientationType.LEFT) {

            setTextureCoords(new WidgetTextureCoords(p * 33f, p * 32f, p * 63f, p * 47f));

            setSize(new Vector2f(30, 15));
            setPreferredSize(getSize());

        } else if (this.orientationType == WidgetOrientationType.RIGHT) {

            setTextureCoords(new WidgetTextureCoords(p * 33f, p * 48f, p * 63f, p * 63f));

            setSize(new Vector2f(30, 15));
            setPreferredSize(getSize());

        }


    }

    /** <code>initWidgetRenderer</code>
     *
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {
        setWidgetRenderer(WidgetRendererFactory.getFactory().getRenderer(this));
    }

    /**
     * <code>getOrientationType</code>
     * @return
     */
    public WidgetOrientationType getOrientationType() {
        return orientationType;
    }

    /**
     * <code>setOrientationType</code>
     * @param type
     */
    public void setOrientationType(WidgetOrientationType type) {
        this.orientationType = type;
    }

}
