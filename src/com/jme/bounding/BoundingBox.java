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

package com.jme.bounding;

import com.jme.scene.shape.*;
import com.jme.math.*;
import com.jme.util.MemPool;

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
 * @version $Id: BoundingBox.java,v 1.13 2004-08-20 02:42:54 cep21 Exp $
 */
public class BoundingBox extends Box implements BoundingVolume {

    public int[] checkPlanes = new int[6];

    private Vector3f minPnt = new Vector3f();
    private Vector3f maxPnt = new Vector3f();

    private float oldXExtent, oldYExtent, oldZExtent;
    private Vector3f oldCenter = new Vector3f();

    private Vector3f origCenter = new Vector3f();
    private Vector3f origExtent = new Vector3f();

    /**
     * Default contstructor instantiates a new <code>BoundingBox</code>
     * object.
     */
    public BoundingBox() {
        super("aabb");
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name) {
        super(name);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(Vector3f center, float xExtent, float yExtent, float zExtent) {
        super("aabb", center, xExtent, yExtent, zExtent);
        initCheckPlanes();
    }

    /**
     * Contstructor instantiates a new <code>BoundingBox</code> object with given specs.
     */
    public BoundingBox(String name, Vector3f center, float xExtent, float yExtent, float zExtent) {
        super(name, center, xExtent, yExtent, zExtent);
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

        center.set(max.add(min));
        center.multLocal(0.5f);

        origExtent.x = xExtent = max.x - center.x;
        origExtent.y = yExtent = max.y - center.y;
        origExtent.z = zExtent = max.z - center.z;
        origCenter.set(center);
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
        Vector3f scale) {
        return this.transform(rotate,translate,scale,null);
    }

    int DEBUG=0;
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
        Vector3f scale,
        BoundingVolume store) {



        BoundingBox box = (BoundingBox)store;
        if (box == null) box = new BoundingBox(new Vector3f(0,0,0), 1,1,1);

        box.origCenter.set(origCenter);
        rotate.mult(origCenter, box.center);
        box.center.multLocal(scale).addLocal(translate);

        Matrix3f transMatrix=MemPool.m3a;
        transMatrix.set(rotate);
        // Make the rotation matrix all positive to get the maximum x/y/z extent
        if (transMatrix.m00<0) transMatrix.m00*=-1;
        if (transMatrix.m01<0) transMatrix.m01*=-1;
        if (transMatrix.m02<0) transMatrix.m02*=-1;
        if (transMatrix.m10<0) transMatrix.m10*=-1;
        if (transMatrix.m11<0) transMatrix.m11*=-1;
        if (transMatrix.m12<0) transMatrix.m12*=-1;
        if (transMatrix.m20<0) transMatrix.m20*=-1;
        if (transMatrix.m21<0) transMatrix.m21*=-1;
        if (transMatrix.m22<0) transMatrix.m22*=-1;

        // (ab)use origExtent to do the multiplication resulting in the biggest extent
        // values for a rotation.
        transMatrix.mult(origExtent,box.origExtent);
        // Assign the biggest rotations after scales.
        box.xExtent=box.origExtent.x*scale.x;
        box.yExtent=box.origExtent.y*scale.y;
        box.zExtent=box.origExtent.z*scale.z;
        // reset origExtent back to what it should be.
        box.origExtent.set(origExtent);

        return box;
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * @param plane the plane to check against.
     */
    public int whichSide(Plane plane) {
        float radius = FastMath.abs(xExtent*plane.normal.x) +
                       FastMath.abs(yExtent*plane.normal.y) +
                       FastMath.abs(zExtent*plane.normal.z);

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

        rVal.setData(minPnt, maxPnt, false);

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
            rVal.center.set(center);
            rVal.xExtent = xExtent;
            rVal.yExtent = yExtent;
            rVal.zExtent = zExtent;
            rVal.checkPlanes[0] = checkPlanes[0];
            rVal.checkPlanes[1] = checkPlanes[1];
            rVal.checkPlanes[2] = checkPlanes[2];
            rVal.checkPlanes[3] = checkPlanes[3];
            rVal.checkPlanes[4] = checkPlanes[4];
            rVal.checkPlanes[5] = checkPlanes[5];
            rVal.origCenter.set(origCenter);
            rVal.origExtent.set(origExtent);
            return rVal;
        } else{
            BoundingBox rVal=new BoundingBox(name+"_clone", (center != null ? (Vector3f)center.clone() : null), xExtent, yExtent, zExtent);
            rVal.origCenter.set(origCenter);
            rVal.origExtent.set(origExtent);
            return rVal;
        }
    }

    /**
     * <code>getCheckPlane</code> returns a specific check plane. This plane identitifies the
     * previous value of the visibility check.
     */
    public int getCheckPlane(int index) {
        return checkPlanes[index];
    }

    /**
     * <code>setCheckPlane</code> indentifies the value of one of the spheres
     * checked planes. That is what plane of the view frustum has been checked
     * for intersection.
     */
    public void setCheckPlane(int index, int value) {
        checkPlanes[index] = value;
    }

    /**
     * <code>recomputeMesh</code> regenerates the <code>BoundingBox</code>
     * based on new model information.
     */
    public void recomputeMesh() {
        if (!center.equals(oldCenter) || xExtent != oldXExtent || yExtent != oldYExtent || zExtent != oldZExtent) {
            setData(center, xExtent, yExtent, zExtent, true);
            oldXExtent = xExtent;
            oldYExtent = yExtent;
            oldZExtent = zExtent;
            oldCenter.set(center.x, center.y, center.z);
        }
    }

    /**
     * Find the distance from the center of this Bounding Volume to the given point.
     *
     * @param point The point to get the distance to
     * @return distance
     */
    public float distanceTo(Vector3f point) {
      return center.distance(point);
    }


    /**
     * Stores the current center of this BoundingBox into the store vector.
     * @param store The vector to store the center into.
     * @return The store vector, after setting it's contents to the center
     */ 
    public Vector3f getCenter(Vector3f store) {
        store.set(center);
        return store;
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: RRR.SSSS Center: <Vector>".
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
