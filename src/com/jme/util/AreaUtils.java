package com.jme.util;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.renderer.Camera;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.FastMath;
import com.jme.system.DisplaySystem;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AreaUtils {

  /**
   * calcScreenArea -- in Pixels
   *
   * @param bound BoundingVolume
   * @param cam Camera
   * @return float
   */
  public static float calcScreenArea(BoundingVolume bound, float distance, float screenWidth) {
    if (bound instanceof BoundingSphere)
      return calcScreenArea((BoundingSphere)bound, distance, screenWidth);
    else if (bound instanceof BoundingBox)
      return calcScreenArea((BoundingBox)bound, distance, screenWidth);
    return 0.0f;
  }

  /**
   * calcScreenArea -- in Pixels
   *
   * @param bound BoundingSphere
   * @param cam Camera
   * @return float
   */
  private static float calcScreenArea(BoundingSphere bound, float distance, float screenWidth) {
    // Where is the center point and a radius point that lies in a plan parallel to the view plane?
//    // Calc radius based on these two points and plug into circle area formula.
//    Vector2f centerSP = null;
//    Vector2f outerSP = null;
//    float radiusSq = centerSP.subtract(outerSP).lengthSquared();
    float radius = (bound.radius * screenWidth) / (distance * 2);
    return radius * radius * FastMath.PI;
  }

  /**
   * calcScreenArea -- in Pixels
   *
   * @param bound BoundingBox
   * @param cam Camera
   * @return float
   */
  private static float calcScreenArea(BoundingBox bound, float distance, float screenWidth) {
    // Calc as if we are a BoundingSphere for now...
    Vector3f radVect = new Vector3f(bound.xExtent, bound.yExtent, bound.zExtent);
    Vector3f tempCenter = bound.getCenter();
    BoundingSphere sphere = new BoundingSphere(radVect.length(), tempCenter);
    return calcScreenArea(sphere, distance, screenWidth);
  }

}
