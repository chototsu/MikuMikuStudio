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
package com.jme.widget.bounds;

import com.jme.math.Vector2f;

/**
 * <code>WidgetViewRectangle</code>
 * @author Gregg Patton
 * @version
 */
public class WidgetViewRectangle extends WidgetBoundingRectangle {

    protected Vector2f offset = new Vector2f();

    public WidgetViewRectangle() {
        super();
    }

    public WidgetViewRectangle(WidgetBoundingRectangle br) {
        super(br);
    }

    public WidgetViewRectangle(Vector2f min, Vector2f max, Vector2f center) {
        super(min, max, center);
    }

    public WidgetViewRectangle(int x, int y, int width, int height) {
        set(x, y, width, height);
    }

    public WidgetViewRectangle(WidgetViewRectangle v) {
        set(v);
    }

    public void set(WidgetViewRectangle v) {
        setMin(v.min);
        setMax(v.max);
        setOffset(v.offset);
    }

    public void set(int x, int y, int width, int height) {
        min.x = x;
        min.y = y;

        setWidthHeight(width, height);
    }

    public Vector2f getOffset() {
        Vector2f ret = new Vector2f();

        ret.x = offset.x;
        ret.y = offset.y;

        return ret;
    }

    public float getOffsetX() {
        return offset.x;
    }

    public float getOffsetY() {
        return offset.y;
    }

    public void setOffset(Vector2f o) {
        setOffset(o.x, o.y);
    }

    public void setOffset(float x, float y) {
        offset.x = x;
        offset.y = y;
    }

    public void setOffsetX(float x) {
        offset.x = x;
    }

    public void setOffsetY(float y) {
        offset.y = y;
    }

    public String toString() {
        return super.toString() + "offset=" + offset;
    }

}
