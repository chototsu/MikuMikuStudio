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

import com.jme.input.MouseInput;
import com.jme.math.Vector2f;
import com.jme.widget.WidgetAbstractImpl;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.renderer.WidgetRendererFactory;
import com.jme.widget.scroller.WidgetScrollerThumbTray;

/**
 * <code>WidgetSliderThumbTray</code>
 * @author Gregg Patton
 * @version $Id: WidgetSliderThumbTray.java,v 1.2 2004-04-22 22:27:26 renanse Exp $
 */
public class WidgetSliderThumbTray extends WidgetScrollerThumbTray {

    /**
     * @param type
     */
    public WidgetSliderThumbTray(WidgetOrientationType type) {
        super(type);

        setBgColor(WidgetAbstractImpl.defaultBgColor);
    }

    /** <code>calcThumbSize</code>
     *
     * @see com.jme.widget.scroller.WidgetScrollerThumbTray#calcThumbSize()
     */
    protected void calcThumbSize() {
        if (this.type == WidgetOrientationType.HORIZONTAL) {

            this.thumbSize = this.thumb.getWidth();

        } else if (this.type == WidgetOrientationType.VERTICAL) {

            this.thumbSize = this.thumb.getHeight();

        }

    }

    /** <code>initExtents</code>
     *
     * @see com.jme.widget.scroller.WidgetScrollerThumbTray#initExtents()
     */
    protected void initExtents() {

        if (type == WidgetOrientationType.VERTICAL) {
            size = getHeight();

            if (size > 0) {

                calcThumbSize();

                setPanYOffset((int) - thumbPos);
            }

        } else if (type == WidgetOrientationType.HORIZONTAL) {
            size = getWidth();

            if (size > 0) {

                calcThumbSize();

                setPanXOffset((int) thumbPos);
            }
        }

        doLayout();
    }

    /** <code>doMouseButtonDown</code>
     *
     * @see com.jme.widget.input.mouse.WidgetMouseHandlerInterface#doMouseButtonDown()
     */
    public void doMouseButtonDown() {

        MouseInput mi = getMouseInput();

        Vector2f l = thumb.getAbsoluteLocation();
        Vector2f size = thumb.getSize();

        int mx = mi.getXAbsolute();
        int my = mi.getYAbsolute();
        int delta;

        if (type == WidgetOrientationType.VERTICAL) {

            delta = (int) ((my - l.y) - (size.y / 2));
            setThumbPos(thumbPos - delta);

            setMouseOwner(thumb);

            getNotifierMouseDrag().notifyObservers(this);

        } else if (type == WidgetOrientationType.HORIZONTAL) {

            delta = (int) ((mx - l.x) - (size.x / 2));
            setThumbPos(thumbPos + delta);

            setMouseOwner(thumb);

            getNotifierMouseDrag().notifyObservers(this);

        }
    }

    /** <code>initWidgetRenderer</code>
     *
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {
        setWidgetRenderer(WidgetRendererFactory.getFactory().getRenderer(this));
    }

    /** <code>calcThumbPos</code>
     *
     * @see com.jme.widget.scroller.WidgetScrollerThumbTray#calcThumbPos()
     */
    protected void calcThumbPos() {
        thumbPos = offset;
        clampThumbPos();
    }

    /** <code>clampOffset</code>
     *
     * @see com.jme.widget.scroller.WidgetScrollerThumbTray#clampOffset()
     */
    protected void clampOffset() {}

    /** <code>calcOffset</code>
     *
     * @see com.jme.widget.scroller.WidgetScrollerThumbTray#calcOffset()
     */
    protected void calcOffset() {}

}
