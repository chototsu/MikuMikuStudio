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

import com.jme.math.Quaternion;
import com.jme.math.Plane;
import com.jme.math.Vector3f;

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
 * @version $Id: BoundingBox.java,v 1.15 2004-03-17 23:16:53 renanse Exp $
 */
public class BoundingBox extends Box implements BoundingVolume {

    public int[] checkPlanes = new int[6];

    private Vector3f minPnt = new Vector3f();
    private Vector3f maxPnt = new Vector3f();

    private float oldXExtent, oldYExtent, oldZExtent;
    private Vector3f oldCenter = new Vector3f();

    /**
     * Default contstructor instantiates a new <code>BoundingBox</code>
     * object.
     */
    public BoundingBox() {
        super("aabb", new Vector3f(0,0,0), 1, 1, 1);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name) {
        super(name, new Vector3f(0,0,0), 1, 1, 1);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(Vector3f center, float xExtent, float yExtent, float zExtent) {
        super("aabb", new Vector3f(0,0,0), xExtent, yExtent, zExtent);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name, Vector3f center, float xExtent, float yExtent, float zExtent) {
        super(name, new Vector3f(0,0,0), xExtent, yExtent, zExtent);
        initCheckPlanes();
    }

    public void initCheckPlanes() {
        checkPlanes[0] = 0;
        checkPlanes[1] = 1;
        checkPlanes[2] = 2;
        checkPlanes[3] = 3;
        checkPlanes[4] = 4;
        checkPlanes[5] = 5;
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
        } else if (distance >= radius) {
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
        if (volume instanceof BoundingBox) {
          BoundingBox vBox = (BoundingBox)volume;
          return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent, new BoundingBox(new Vector3f(0,0,0), 0, 0, 0));
        } else if (volume instanceof BoundingSphere) {
          BoundingSphere vSphere = (BoundingSphere)volume;
          return merge(vSphere.center, vSphere.radius, vSphere.radius, vSphere.radius, new BoundingBox(new Vector3f(0,0,0), 0, 0, 0));
        } else {
          return null;
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
        if (volume instanceof BoundingBox) {
          BoundingBox vBox = (BoundingBox)volume;
          return merge(vBox.center, vBox.xExtent, vBox.yExtent, vBox.zExtent, this);
        } else if (volume instanceof BoundingSphere) {
          BoundingSphere vSphere = (BoundingSphere)volume;
          return merge(vSphere.center, vSphere.radius, vSphere.radius, vSphere.radius, this);
        } else {
          return null;
        }
    }

    private BoundingBox merge(Vector3f boxCenter, float boxX, float boxY, float boxZ, BoundingBox rVal) {
        minPnt.x = center.x-xExtent;
        if (minPnt.x > boxCenter.x-boxX) minPnt.x = boxCenter.x-boxX;
        minPnt.y = center.y-yExtent;
        if (minPnt.y > boxCenter.y-boxY) minPnt.y = boxCenter.y-boxY;
        minPnt.z = center.z-zExtent;
        if (minPnt.z > boxCenter.z-boxZ) minPnt.z = boxCenter.z-boxZ;

        maxPnt.x = center.x+xExtent;
        if (maxPnt.x < boxCenter.x+boxX) maxPnt.x = boxCenter.x+boxX;
        maxPnt.y = center.y+yExtent;
        if (maxPnt.y < boxCenter.y+boxY) maxPnt.y = boxCenter.y+boxY;
        maxPnt.z = center.z+zExtent;
        if (maxPnt.z < boxCenter.z+boxZ) maxPnt.z = boxCenter.z+boxZ;

        maxPnt.subtractLocal(minPnt).multLocal(0.5f);
        rVal.xExtent = maxPnt.x;
        rVal.yExtent = maxPnt.y;
        rVal.zExtent = maxPnt.z;

        rVal.center.x = minPnt.x + rVal.xExtent;
        rVal.center.y = minPnt.y + rVal.yExtent;
        rVal.center.z = minPnt.z + rVal.zExtent;

        return rVal;
    }

    /**
     * <code>clone</code> creates a new BoundingBox object containing the same
     * data as this one.
     * @param store where to store the cloned information.  if null or wrong class, a new store is created.
     * @return the new BoundingBox
     */
    public Object clone(BoundingVolume store) {
        if (store != null && store instanceof BoundingBox) {
            BoundingBox rVal = (BoundingBox)store;
            rVal.center.x = center.x;
            rVal.center.y = center.y;
            rVal.center.z = center.z;
            rVal.xExtent = xExtent;
            rVal.yExtent = yExtent;
            rVal.zExtent = zExtent;
            rVal.checkPlanes[0] = checkPlanes[0];
            rVal.checkPlanes[1] = checkPlanes[1];
            rVal.checkPlanes[2] = checkPlanes[2];
            rVal.checkPlanes[3] = checkPlanes[3];
            rVal.checkPlanes[4] = checkPlanes[4];
            rVal.checkPlanes[5] = checkPlanes[5];
            return rVal;
        } else
            return new BoundingBox(name+"_clone", (Vector3f)center.clone(), xExtent, yExtent, zExtent);
    }

    public int getCheckPlane(int index) {
        return checkPlanes[index];
    }

    public void setCheckPlane(int index, int value) {
        checkPlanes[index] = value;
    }

    public void recomputeMesh() {
        if (!center.equals(oldCenter) || xExtent != oldXExtent || yExtent != oldYExtent || zExtent != oldZExtent) {
            setData(null, xExtent, yExtent, zExtent);
            oldXExtent = xExtent;
            oldYExtent = yExtent;
            oldZExtent = zExtent;
            oldCenter.set(center.x, center.y, center.z);
        }
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: XXX.YYYY Center: <Vector>".
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingBox [Center: "
                + center
                +"  xExtent: "+xExtent
                +"  yExtent: "+yExtent
                +"  zExtent: "+zExtent+"]";
    }
}
