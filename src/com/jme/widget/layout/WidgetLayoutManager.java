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
package com.jme.widget.layout;

import com.jme.math.Vector2f;
import com.jme.scene.Spatial;

import com.jme.widget.Widget;
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.bounds.WidgetBoundingRectangle;
import com.jme.widget.bounds.WidgetViewRectangle;

/**
 * @author pattogo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class WidgetLayoutManager {

    protected Vector2f maximumSize = new Vector2f();

    public abstract Vector2f preferredLayoutSize(WidgetAbstractContainer parent);

    public abstract void layoutContainer(WidgetAbstractContainer parent);

    public Vector2f getMaximumSize() {
        return this.maximumSize;
    }

	public void setMaximumWidth(float w) {
		this.maximumSize.x = w;
	}

	public void setMaximumHeight(float h) {
		this.maximumSize.y = h;
	}

    protected WidgetBoundingRectangle calcVisiblityRect(Widget w) {

        Widget p = w.getWidgetParent();

        if (p != null) {
            return p.getViewRectangle();
        } else {
            return null;
        }
    }

    public void calcViewport(Widget w) {
        WidgetViewRectangle vp = new WidgetViewRectangle();

        if (!w.isVisible()) {
            w.setViewRectangle(vp);
            return;
        }

        WidgetBoundingRectangle visiRect = calcVisiblityRect(w);

        ((Spatial) w).updateWorldBound();

        WidgetBoundingRectangle r =
            new WidgetBoundingRectangle((WidgetBoundingRectangle) ((Spatial) w).getWorldBound(), true);

        if (visiRect != null) {

            if (visiRect.contains(r) || WidgetBoundingRectangle.intersects(visiRect, r)) {

                WidgetBoundingRectangle clipped = WidgetBoundingRectangle.clip(r, visiRect);

                vp.set(clipped);

                if (w.isApplyOffsetX() && r.getMinX() < visiRect.getMinX()) {

//					vp.setMinX(clipped.getMinX());
//					vp.setMaxX(clipped.getMaxX());

					vp.setOffsetX(r.getMinX() - visiRect.getMinX());

                } else {

//					vp.setMinX(r.getMinX());
//					vp.setMaxX(r.getMaxX());

                }

                if (w.isApplyOffsetY() && r.getMinY() < visiRect.getMinY()) {

//					vp.setMinY(clipped.getMinY());
//					vp.setMaxY(clipped.getMaxY());

					vp.setOffsetY(r.getMinY() - visiRect.getMinY());
				} else {

//					vp.setMinY(r.getMinY());
//					vp.setMaxY(r.getMaxY());

				}

            }

        } else {

            vp.set(r);

        }

        w.setViewRectangle(vp);

    }

}
