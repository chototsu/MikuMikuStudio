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

/*
 * EDIT:  02/09/2004 - Changed merge to return this instead of null. GOP
 */

package com.jme.scene;

import java.util.logging.Level;

import com.jme.math.Quaternion;
import com.jme.math.Plane;
import com.jme.math.Vector3f;
import com.jme.util.LoggingSystem;

/**
 * <code>BoundingSphere</code> defines a sphere that defines a container
 * for a group of vertices of a particular piece of geometry. This sphere
 * defines a radius and a center.
 * <br><br>
 * A typical usage is to allow the class define the center and radius
 * by calling either <code>containAABB</code> or <code>averagePoints</code>.
 * A call to <code>computeFramePoint</code> in turn calls
 * <code>containAABB</code>.
 *
 * @author Mark Powell
 * @version $Id: BoundingSphere.java,v 1.29 2004-03-31 17:56:55 renanse Exp $
 */
public class BoundingSphere extends Sphere implements BoundingVolume {

    public int[] checkPlanes = new int[6];
    private float oldRadius;
    private Vector3f oldCenter = new Vector3f();

    /**
     * Default contstructor instantiates a new <code>BoundingSphere</code>
     * object.
     */
    public BoundingSphere() {
        super("bsphere");
        initCheckPlanes();
    }

    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * @param radius the radius of the sphere.
     * @param center the center of the sphere.
     */
    public BoundingSphere(float radius, Vector3f center) {
        super("bsphere", center, 10, 10, 1);
        this.radius = radius;
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
     * <code>getRadius</code> returns the radius of the bounding sphere.
     * @return the radius of the bounding sphere.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * <code>getCenter</code> returns the center of the bounding sphere.
     * @return the center of the bounding sphere.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * <code>setRadius</code> sets the radius of this bounding sphere.
     * @param radius the new radius of the bounding sphere.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * <code>setCenter</code> sets the center of the bounding sphere.
     * @param center the new center of the bounding sphere.
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Sphere from
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

        Vector3f halfDiagonal = max.subtract(min).multLocal(0.5f);
        radius = halfDiagonal.length();
    }

    /**
     * <code>averagePoints</code> selects the sphere center to be
     * the average of the points and the sphere radius to be the
     * smallest value to enclose all points.
     * @param points the list of points to contain.
     */
    public void averagePoints(Vector3f[] points) {
        LoggingSystem.getLogger().log(Level.INFO, "Bounding Sphere calculated " +
                    "using average points.");
        center = points[0];

        for (int i = 1; i < points.length; i++)
            center.addLocal(points[i]);
        float quantity = 1.0f / points.length;
        center.multLocal(quantity);

        float maxRadiusSqr = 0;
        for (int i = 0; i < points.length; i++) {
            Vector3f diff = points[i].subtract(center);
            float radiusSqr = diff.lengthSquared();
            if (radiusSqr > maxRadiusSqr)
                maxRadiusSqr = radiusSqr;
        }

        radius = (float) Math.sqrt(maxRadiusSqr);

    }

  /**
   * <code>transform</code> modifies the center of the sphere to reflect the
   * change made via a rotation, translation and scale.
   *
   * @param rotate the rotation change.
   * @param translate the translation change.
   * @param scale the size change.
   * @return BoundingVolume
   */
  public BoundingVolume transform(
        Quaternion rotate,
        Vector3f translate,
        float scale) {
        Vector3f newCenter = rotate.mult(center).multLocal(scale).addLocal(translate);
        return new BoundingSphere(scale * radius, newCenter);
    }

  /**
   * <code>transform</code> modifies the center of the sphere to reflect the
   * change made via a rotation, translation and scale.
   *
   * @param rotate the rotation change.
   * @param translate the translation change.
   * @param scale the size change.
   * @param store sphere to store result in
   * @return BoundingVolume
   * @return ref
   */
  public BoundingVolume transform(
        Quaternion rotate,
        Vector3f translate,
        float scale,
        BoundingVolume store) {

        BoundingSphere sphere = (BoundingSphere)store;
        if (sphere == null) sphere = new BoundingSphere(1, new Vector3f(0,0,0));
        rotate.mult(center, sphere.center);
        sphere.center.multLocal(scale).addLocal(translate);
        sphere.radius = scale*radius;
        return sphere;
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view
     * frustum) to determine which side this bound is on.
     * @param plane the plane to check against.
     * @return side
     */
    public int whichSide(Plane plane) {
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
      if (volume == null) {
        return this;
      }
      if (volume instanceof BoundingSphere) {
        BoundingSphere sphere = (BoundingSphere) volume;
        float temp_radius = sphere.getRadius();
        Vector3f temp_center = sphere.getCenter();
        BoundingSphere rVal = new BoundingSphere();
        return merge(volume, temp_radius, temp_center, rVal);
      } else if (volume instanceof BoundingBox) {
        BoundingBox box = (BoundingBox)volume;
        Vector3f radVect = new Vector3f(box.xExtent, box.yExtent, box.zExtent);
        Vector3f temp_center = box.getCenter();
        BoundingSphere rVal = new BoundingSphere();
        return merge(volume, radVect.length(), temp_center, rVal);
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
      if (volume == null) {
        return this;
      }
      if (volume instanceof BoundingSphere) {
        BoundingSphere sphere = (BoundingSphere) volume;
        float temp_radius = sphere.getRadius();
        Vector3f temp_center = sphere.getCenter();
        return merge(volume, temp_radius, temp_center, this);
      } else if (volume instanceof BoundingBox) {
        BoundingBox box = (BoundingBox)volume;
        Vector3f radVect = new Vector3f(box.xExtent, box.yExtent, box.zExtent);
        Vector3f temp_center = box.getCenter();
        return merge(volume, radVect.length(), temp_center, this);
      } else {
        return null;
      }
    }

    private BoundingVolume merge(BoundingVolume volume, float temp_radius,
                                 Vector3f temp_center, BoundingSphere rVal) {
      Vector3f diff = temp_center.subtract(center);
      float lengthSquared = diff.lengthSquared();
      float radiusDiff = temp_radius - radius;
      float diffSquared = radiusDiff * radiusDiff;

      if (diffSquared >= lengthSquared) {
          return (radiusDiff >= 0.0 ? volume : this);
      }

      float length = (float) Math.sqrt(lengthSquared);
      float tolerance = 1e-06f;

      if (length > tolerance) {
          float coeff = (length + radiusDiff) / (2.0f * length);
          rVal.setCenter(center.addLocal(diff.multLocal(coeff)));
      } else {
          rVal.setCenter(center);
      }

      rVal.setRadius(0.5f * (length + radius + temp_radius));

      return rVal;
    }

    /**
     * <code>clone</code> creates a new BoundingSphere object containing the same
     * data as this one.
     * @param store where to store the cloned information.  if null or wrong class, a new store is created.
     * @return the new BoundingSphere
     */
    public Object clone(BoundingVolume store) {
        if (store != null && store instanceof BoundingSphere) {
            BoundingSphere rVal = (BoundingSphere)store;
            if (rVal.center == null) rVal.center = new Vector3f(center.x, center.y, center.z);
            else rVal.center.set(center.x, center.y, center.z);
            rVal.radius = radius;
            rVal.checkPlanes[0] = checkPlanes[0];
            rVal.checkPlanes[1] = checkPlanes[1];
            rVal.checkPlanes[2] = checkPlanes[2];
            rVal.checkPlanes[3] = checkPlanes[3];
            rVal.checkPlanes[4] = checkPlanes[4];
            rVal.checkPlanes[5] = checkPlanes[5];
            return rVal;
        } else
            return new BoundingSphere(radius, (Vector3f)center.clone());
    }

    public int getCheckPlane(int index) {
        return checkPlanes[index];
    }

    public void setCheckPlane(int index, int value) {
        checkPlanes[index] = value;
    }

    public void recomputeMesh() {
        if (radius != oldRadius || !center.equals(oldCenter)) {
            setData(center, 10, 10, radius);
            oldRadius = radius;
            oldCenter.set(center.x, center.y, center.z);
        }
    }

    /**
     * <code>toString</code> returns the string representation of this object.
     * The form is: "Radius: XXX.YYYY Center: <Vector>".
     * @return the string representation of this.
     */
    public String toString() {
        return "com.jme.scene.BoundingSphere [Radius: " + radius + " Center: "
                + center +"]";
    }
}
