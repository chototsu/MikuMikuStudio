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
package com.jme.widget.bounds;

import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingVolume;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetBoundingRectangle implements BoundingVolume {

    protected Vector2f min = new Vector2f();
    protected Vector2f max = new Vector2f();
    protected Vector2f center = new Vector2f();

    protected Vector2f[] points = { min, max };

    protected boolean lockMin = false;

    /**
     * Constructor instantiates a new <code>BoundingBox</code> object.
     *
     */
    public WidgetBoundingRectangle() {}

    public WidgetBoundingRectangle(boolean lockMin) {
        setLockMin(lockMin);
        //calcVisiblity();
    }

    public WidgetBoundingRectangle(WidgetBoundingRectangle r) {
        set(r);
        //calcVisiblity();
    }

    public WidgetBoundingRectangle(WidgetBoundingRectangle r, boolean lockMin) {
        set(r);
        setLockMin(lockMin);
    }

    /**
     * Constructor instantiates a new <code>BoundingBox</code> object. The
     * box is defined by a minimum and maximum point.
     * @param center the center of the box.
     * @param min the minimum point of the box.
     * @param max the maximum point of the box.
     */
    protected WidgetBoundingRectangle(Vector2f min, Vector2f max, Vector2f center) {
        setMin(min);
        setMax(max);
    }

    public void set(WidgetBoundingRectangle r) {
        setMin(r.min);
        setMax(r.max);
    }

    public Vector2f getMin() {
        return min;
    }

    public float getMinX() {
        return min.x;
    }

    public void setMinX(float x) {
        min.x = x;
        computeFromPoints();
    }

    public float getMinY() {
        return min.y;
    }

    public void setMinY(float y) {
        min.y = y;
        computeFromPoints();
    }

    public Vector2f getMax() {
        return max;
    }

    public void setMaxX(float x) {
        max.x = x;
        computeFromPoints();
    }

    public float getMaxX() {
        return max.x;
    }

    public float getMaxY() {
        return max.y;
    }

    public void setMaxY(float y) {
        max.y = y;
        computeFromPoints();
    }

    public Vector2f getCenter() {
        return center;
    }

    /**
     * <code>computeFromPoints</code> creates a new BoundingBox from
     * a given set of points. It uses the <code>axisAligned</code> method
     * as default.
     * @param points the points to contain.
     */
    public void computeFromPoints(Vector2f[] points) {
        axisAligned(points);
    }

    /**
     * <code>axisAligned</code> creates a minimal box around all
     * supplied points. The orientation is always aligned with the
     * local entity's coordinate system and therefore is axis
     * aligned.
     * @param points the list of points to contain.
     */
    public void axisAligned(Vector2f[] points) {
        float minX = points[0].x;
        float minY = points[0].y;
        float maxX = minX;
        float maxY = minY;

        for (int i = 1; i < points.length; i++) {
            if (points[i].x < minX)
                minX = points[i].x;
            else if (points[i].x > maxX)
                maxX = points[i].x;

            if (points[i].y < minY)
                minY = points[i].y;
            else if (points[i].y > maxY)
                maxY = points[i].y;

        }

        if (lockMin == false) {
            min.x = minX;
            min.y = minY;
        }

        max.x = maxX;
        max.y = maxY;

        center.x = (maxX + minX) / 2;
        center.y = (maxY + minY) / 2;
    }

    public boolean contains(WidgetBoundingRectangle rect) {
        //if this min is less than or equal to rect's min and
        //if this max is greater than or equal to rect's max
        if ((min.x <= rect.getMin().x && min.y <= rect.getMin().y) && (max.x >= rect.getMax().x && max.y >= rect.getMax().y)) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#merge(com.jme.scene.BoundingVolume)
     */
    public BoundingVolume merge(BoundingVolume bound) {
        if (!(bound instanceof WidgetBoundingRectangle)) {
            return this;
        }

        WidgetBoundingRectangle testBox = (WidgetBoundingRectangle) bound;
        if (contains(testBox)) {
            return this;
        } else if (testBox.contains(this)) {
            return testBox;
        }

        Vector2f newMin = new Vector2f();
        Vector2f newMax = new Vector2f();
        //otherwise:
        //new sphere is the average of the two centers
        //smallest min and largest max.
        if (min.x < testBox.getMin().x) {
            newMin.x = min.x;
        } else {
            newMin.x = testBox.getMin().x;
        }

        //find min point
        if (min.y < testBox.getMin().y) {
            newMin.y = min.y;
        } else {
            newMin.y = testBox.getMin().y;
        }

        if (max.x > testBox.getMax().x) {
            newMax.x = max.x;
        } else {
            newMax.x = testBox.getMax().x;
        }

        //find max point
        if (max.y > testBox.getMax().y) {
            newMax.y = max.y;
        } else {
            newMax.y = testBox.getMax().y;
        }

        Vector2f newCenter = new Vector2f();
        newCenter.x = (newMax.x + newMin.x) / 2;
        newCenter.y = (newMax.y + newMin.y) / 2;

        return new WidgetBoundingRectangle(newMin, newMax, newCenter);
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#merge(com.jme.scene.BoundingVolume)
     */
    public BoundingVolume mergeLocal(BoundingVolume bound) {
        if (!(bound instanceof WidgetBoundingRectangle)) {
            return this;
        }

        WidgetBoundingRectangle testBox = (WidgetBoundingRectangle) bound;
        if (contains(testBox)) {
            return this;
        } else if (testBox.contains(this)) {
            return testBox;
        }

        //otherwise:
        //new sphere setting is the average of the two centers
        //smallest min and largest max.
        if (min.x > testBox.getMin().x) {
            min.x = testBox.getMin().x;
        }

        //find min point
        if (min.y > testBox.getMin().y) {
            min.y = testBox.getMin().y;
        }

        if (max.x < testBox.getMax().x) {
            max.x = testBox.getMax().x;
        }

        //find max point
        if (max.y < testBox.getMax().y) {
            max.y = testBox.getMax().y;
        }

        center.x = (max.x + min.x) / 2;
        center.y = (max.y + min.y) / 2;
        return this;
    }

    /**
     * <code>clone</code> creates a new BoundingSphere object containing the same
     * data as this one.
     * @return the new BoundingSphere
     */
    public Object clone() {
        WidgetBoundingRectangle rVal = new WidgetBoundingRectangle();
        rVal.min = (Vector2f)min.clone();
        rVal.max = (Vector2f)max.clone();
        rVal.center = (Vector2f)center.clone();
        rVal.points = new Vector2f[] { rVal.min, rVal.max };
        rVal.lockMin = lockMin;
        return rVal;
    }

    public void setMin(Vector2f min) {
        setMin(min.x, min.y);
    }

    public void setMin(float x, float y) {
        this.min.x = x;
        this.min.y = y;

        if (this.max.x < this.min.x)
            this.max.x = this.min.x;

        if (this.max.y < this.min.y)
            this.max.y = this.min.y;

        computeFromPoints();
    }

    public void setMinPreserveSize(float x, float y) {
        float w = max.x - min.x;
        float h = max.y - min.y;

        min.x = x;
        min.y = y;

        max.x = min.x + w;
        max.y = min.y + h;

        computeFromPoints();

    }

    public void setMinPreserveSize(Vector2f at) {
        setMinPreserveSize(at.x, at.y);
    }

    public void setMinXPreserveSize(float x) {
        float w = max.x - min.x;

        min.x = x;

        max.x = min.x + w;

        computeFromPoints();
    }

    public void setMinYPreserveSize(float y) {
        float h = max.y - min.y;

        min.y = y;

        max.y = min.y + h;

        computeFromPoints();
    }

    public void addMinX(float x) {
        this.min.x += x;
        computeFromPoints();
    }

    public void subtractMinX(float x) {
        addMinX(-x);
    }

    public void addMinY(float y) {
        this.min.y += y;
        computeFromPoints();
    }

    public void subtractMinY(float y) {
        addMinY(-y);
    }

    public void setMax(Vector2f max) {
        setMax(max.x, max.y);
        computeFromPoints();
    }

    public void setMax(float x, float y) {
        this.max.x = x;
        this.max.y = y;

        if (this.min.x > this.max.x)
            this.min.x = this.max.x;

        if (this.min.y > this.max.y)
            this.min.y = this.max.y;

        computeFromPoints();
    }

    public void addMaxX(float x) {
        this.max.x += x;
        computeFromPoints();
    }

    public void subtractMaxX(float x) {
        addMaxX(-x);
    }

    public void addMaxY(float y) {
        this.max.y += y;
        computeFromPoints();
    }

    public void subtractMaxY(float y) {
        addMaxY(-y);
    }

    public void setSize(Vector2f size) {
        setWidthHeight(size.x, size.y);
    }

    public Vector2f getSize() {
        return new Vector2f(getWidth(), getHeight());
    }

    public void setWidthHeight(float width, float height) {
        max.x = min.x + width;
        max.y = min.y + height;
        computeFromPoints();
    }

    public void setWidth(float width) {
        max.x = min.x + width;
        computeFromPoints();
    }

    public float getWidth() {
        return max.x - min.x;
    }

    public void setHeight(float height) {
        max.y = min.y + height;
        computeFromPoints();
    }

    public float getHeight() {
        return max.y - min.y;
    }

    public void addWidth(float width) {
        max.x += width;
        computeFromPoints();
    }

    public void subtractWidth(float width) {
        addWidth(-width);
    }

    public void addHeight(float height) {
        max.y += height;
        computeFromPoints();
    }

    public void subtractHeight(float height) {
        addHeight(-height);
    }

    public boolean inside(int x, int y) {
        return (insideX(x) && insideY(y));
    }

    public boolean insideX(float x) {
        return (x >= min.x && x <= max.x);
    }

    public boolean insideY(float y) {
        return (y >= min.y && y <= max.y);
    }

    protected void computeFromPoints() {
        computeFromPoints(points);
    }

    public static boolean intersects(WidgetBoundingRectangle r1, WidgetBoundingRectangle r2) {
        boolean ret;

        ret = r1.insideX(r2.min.x) || r1.insideX(r2.max.x) || r2.insideX(r1.min.x) || r2.insideX(r1.max.x);

        if (ret) {
            ret = r1.insideY(r2.min.y) || r1.insideY(r2.max.y) || r2.insideY(r1.min.y) || r2.insideY(r1.max.y);
        }

        return ret;
    }

    public static WidgetBoundingRectangle clip(WidgetBoundingRectangle clip, WidgetBoundingRectangle clipTo) {
        WidgetBoundingRectangle clipped = new WidgetBoundingRectangle(clip.min, clip.max, clip.center);

        if (clipped.min.x < clipTo.min.x) {
            clipped.min.x = clipTo.min.x;
        }

        if (clipped.max.x > clipTo.max.x) {
            clipped.max.x = clipTo.max.x;
        }

        if (clipped.min.y < clipTo.min.y) {
            clipped.min.y = clipTo.min.y;
        }

        if (clipped.max.y > clipTo.max.y) {
            clipped.max.y = clipTo.max.y;
        }

        clipped.computeFromPoints();

        return clipped;
    }

    public boolean isLockMin() {
        return lockMin;
    }

    public void setLockMin(boolean b) {
        lockMin = b;
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#transform(com.jme.math.Matrix3f, com.jme.math.Vector3f, float)
     */
    public BoundingVolume transform(Matrix3f rotate, Vector3f translate, float scale) {
        // TODO Auto-generated method stub
        return null;
    }

    public BoundingVolume transform(Matrix3f rotate, Vector3f translate, float scale, BoundingVolume bv) {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#whichSide(com.jme.math.Plane)
     */
    public int whichSide(Plane plane) {
        // TODO Auto-generated method stub
        return Plane.NO_SIDE;
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#computeFromPoints(com.jme.math.Vector3f[])
     */
    public void computeFromPoints(Vector3f[] points) {
        // TODO Auto-generated method stub

    }

    public String toString() {
        return "[min=" + min + "\nmax=" + max + "\ncenter=" + center + "\nwidth=" + getWidth() + "\nheight=" + getHeight() + "]";
    }

}
