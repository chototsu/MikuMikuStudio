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

package com.jme.scene;

import java.util.logging.Level;

import com.jme.math.Quaternion;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;

/**
 * <code>BoundingBox</code> defines an axis-aligned cube that defines a container
 * for a group of vertices of a particular piece of geometry. This box
 * defines a center and extents from that center along the x, y and z axis.
 * <br><br>
 * A typical usage is to allow the class define the center and radius
 * by calling either <code>containAABB</code> or <code>averagePoints</code>.
 * A call to <code>computeFramePoint</code> in turn calls
 * <code>containAABB</code>.
 *
 * @author Joshua Slack
 * @version $Id: BoundingBox.java,v 1.6 2004-03-09 00:06:22 renanse Exp $
 */
public class BoundingBox extends Box implements BoundingVolume {

    /**
     * Default contstructor instantiates a new <code>BoundingBox</code>
     * object.
     */
    public BoundingBox() {
        super("aabb", new Vector3f(0,0,0), 1, 1, 1);
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name) {
        super(name, new Vector3f(0,0,0), 1, 1, 1);
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(Vector3f center, float xExtent, float yExtent, float zExtent) {
        super("aabb", new Vector3f(0,0,0), xExtent, yExtent, zExtent);
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name, Vector3f center, float xExtent, float yExtent, float zExtent) {
        super(name, new Vector3f(0,0,0), xExtent, yExtent, zExtent);
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Box from
     * a given set of points. It uses the <code>containAABB</code> method
     * as default.
     * @param points the points to contain.
     */
    public void computeFromPoints(Vector3f[] points) {
        containAABB(points);
    }

    /**
     * <code>containAABB</code> creates a minimum-volume axis-aligned
     * bounding box of the points, then selects the smallest
     * enclosing sphere of the box with the sphere centered at the
     * boxes center.
     * @param points the list of points.
     */
    public void containAABB(Vector3f[] points) {
       if(points.length <= 0) {
            return;
        }

        Vector3f min = (Vector3f)points[0].clone();
        Vector3f max = (Vector3f)min.clone();

        for (int i = 1; i < points.length; i++) {
            if (points[i].x < min.x)
                min.x = points[i].x;
            else if (points[i].x > max.x)
                max.x = points[i].x;

            if (points[i].y < min.y)
                min.y = points[i].y;
            else if (points[i].y > max.y)
                max.y = points[i].y;

            if (points[i].z < min.z)
                min.z = points[i].z;
            else if (points[i].z > max.z)
                max.z = points[i].z;
        }

        center = max.add(min);
        center.multLocal(0.5f);

        xExtent = max.x - center.x;
        yExtent = max.y - center.y;
        zExtent = max.z - center.z;
    }

    /**
     * <code>transform</code> modifies the center of the box to reflect the
     * change made via a rotation, translation and scale.
     * @param rotate the rotation change.
     * @param translate the translation change.
     * @param scale the size change.
     */
    public BoundingVolume transform(
        Quaternion rotate,
        Vector3f translate,
        float scale) {
        Vector3f newCenter = rotate.mult(center).multLocal(scale).addLocal(translate);
        return new BoundingBox(newCenter, scale * xExtent, scale * yExtent, scale * zExtent);
    }

    /**
     * <code>transform</code> modifies the center of the box to reflect the
     * change made via a rotation, translation and scale.
     * @param rotate the rotation change.
     * @param translate the translation change.
     * @param scale the size change.
     * @param store box to store result in
     */
    public BoundingVolume transform(
        Quaternion rotate,
        Vector3f translate,
        float scale,
        BoundingVolume store) {

        BoundingBox box = (BoundingBox)store;
        if (box == null) box = new BoundingBox();
        rotate.mult(center, box.center);
        box.center.multLocal(scale).addLocal(translate);
        box.xExtent = scale*xExtent;
        box.yExtent = scale*yExtent;
        box.zExtent = scale*zExtent;
        return box;
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * @param plane the plane to check against.
     */
    public int whichSide(Plane plane) {

        float radius = Math.abs(xExtent*plane.normal.x) +
                       Math.abs(yExtent*plane.normal.y) +
                       Math.abs(zExtent*plane.normal.z);

        float distance = plane.pseudoDistance(center);

        if (distance <= -radius) {
            return Plane.NEGATIVE_SIDE;
        } else if (distance > radius) {
            return Plane.POSITIVE_SIDE;
        } else {
            return Plane.NO_SIDE;
        }
    }

    /**
     * <code>merge</code> combines this sphere with a second bounding sphere.
     * This new sphere contains both bounding spheres and is returned.
     * @param volume the sphere to combine with this sphere.
     * @return the new sphere
     */
    public BoundingVolume merge(BoundingVolume volume) {
        if(volume == null) {
            return this;
        }
        if (!(volume instanceof BoundingBox)) {
            return this;
        } else {
            BoundingBox vBox = (BoundingBox)volume;
            BoundingBox rVal = new BoundingBox(new Vector3f(0,0,0), 0, 0, 0);
            rVal.center.add(this.center).add(vBox.center).multLocal(.5f);

        // check first box for extents
            rVal.xExtent = Math.max(Math.abs(center.x - xExtent - rVal.center.x), rVal.xExtent);
            rVal.xExtent = Math.max(Math.abs(center.x + xExtent - rVal.center.x), rVal.xExtent);
            rVal.yExtent = Math.max(Math.abs(center.y - yExtent - rVal.center.y), rVal.yExtent);
            rVal.yExtent = Math.max(Math.abs(center.y + yExtent - rVal.center.y), rVal.yExtent);
            rVal.zExtent = Math.max(Math.abs(center.z - zExtent - rVal.center.z), rVal.zExtent);
            rVal.zExtent = Math.max(Math.abs(center.z + zExtent - rVal.center.z), rVal.zExtent);

        // check second box for extents
            rVal.xExtent = Math.max(Math.abs(vBox.center.x - vBox.xExtent - rVal.center.x), rVal.xExtent);
            rVal.xExtent = Math.max(Math.abs(vBox.center.x + vBox.xExtent - rVal.center.x), rVal.xExtent);
            rVal.yExtent = Math.max(Math.abs(vBox.center.y - vBox.yExtent - rVal.center.y), rVal.yExtent);
            rVal.yExtent = Math.max(Math.abs(vBox.center.y + vBox.yExtent - rVal.center.y), rVal.yExtent);
            rVal.zExtent = Math.max(Math.abs(vBox.center.z - vBox.zExtent - rVal.center.z), rVal.zExtent);
            rVal.zExtent = Math.max(Math.abs(vBox.center.z + vBox.zExtent - rVal.center.z), rVal.zExtent);

            return rVal;
        }
    }

    /**
     * <code>mergeLocal</code> combines this sphere with a second bounding sphere locally.
     * Altering this sphere to contain both the original and the additional sphere volumes;
     * @param volume the sphere to combine with this sphere.
     * @return this
     */
    public BoundingVolume mergeLocal(BoundingVolume volume) {
        if(volume == null) {
            return this;
        }
        if (!(volume instanceof BoundingBox)) {
            return this;
        } else {
            float oldcenterX = center.x;
            float oldcenterY = center.y;
            float oldcenterZ = center.z;
            BoundingBox vBox = (BoundingBox)volume;
            this.center.addLocal(vBox.center).multLocal(.5f);

        // check this box for new extents
            this.xExtent = Math.max(Math.abs(oldcenterX - xExtent - this.center.x), Math.abs(oldcenterX + xExtent - this.center.x));
            this.yExtent = Math.max(Math.abs(oldcenterY - yExtent - this.center.y), Math.abs(oldcenterY + yExtent - this.center.y));
            this.zExtent = Math.max(Math.abs(oldcenterZ - zExtent - this.center.z), Math.abs(oldcenterZ + zExtent - this.center.z));

        // check second box for new extents
            this.xExtent = Math.max(Math.abs(vBox.center.x - vBox.xExtent - this.center.x), this.xExtent);
            this.xExtent = Math.max(Math.abs(vBox.center.x + vBox.xExtent - this.center.x), this.xExtent);
            this.yExtent = Math.max(Math.abs(vBox.center.y - vBox.yExtent - this.center.y), this.yExtent);
            this.yExtent = Math.max(Math.abs(vBox.center.y + vBox.yExtent - this.center.y), this.yExtent);
            this.zExtent = Math.max(Math.abs(vBox.center.z - vBox.zExtent - this.center.z), this.zExtent);
            this.zExtent = Math.max(Math.abs(vBox.center.z + vBox.zExtent - this.center.z), this.zExtent);

            return this;
        }
    }

    /**
     * <code>clone</code> creates a new BoundingBox object containing the same
     * data as this one.
     * @return the new BoundingBox
     */
    public Object clone() {
        BoundingBox rVal = new BoundingBox();
        rVal.center = (Vector3f)center.clone();
        rVal.xExtent = xExtent;
        rVal.yExtent = yExtent;
        rVal.zExtent = zExtent;
        return rVal;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: XXX.YYYY Center: <Vector>".
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingBox [Center: "
                + center +"  Vertices: "+computeVertices()+"]";
    }
}
