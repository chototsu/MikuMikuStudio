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

package com.jme.util;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;

/**
 * <code>AreaUtils</code>
 * @author Joshua Slack
 * @version $Id: AreaUtils.java,v 1.3 2004-04-22 22:27:09 renanse Exp $
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
