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
package com.jme.renderer;

import java.util.Comparator;
import java.util.TreeSet;

import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import java.util.Iterator;

public class RenderQueue {

  private TreeSet opaqueBucket, transparentBucket, orthoBucket;
  private Renderer renderer;

  public RenderQueue(Renderer r) {
    this.renderer = r;
    setupBuckets();
  }

  private void setupBuckets() {
    opaqueBucket = new TreeSet(new OpaqueComp());
    transparentBucket = new TreeSet(new TransparentComp());
    orthoBucket = new TreeSet(new OrthoComp());
  }

  /**
   * Add a given Spatial to the RenderQueue
   * @param s Spatial
   * @param bucket int
   */
  public void addToQueue(Spatial s, int bucket) {
    switch (bucket) {
      case Renderer.QUEUE_OPAQUE:
        opaqueBucket.add(s);
        break;
      case Renderer.QUEUE_TRANSPARENT:
        transparentBucket.add(s);
        break;
      case Renderer.QUEUE_ORTHO:
        orthoBucket.add(s);
        break;
    }
  }

  private float distanceToCam(Spatial spat) {
    if (spat.queueDistance != Float.NEGATIVE_INFINITY) return spat.queueDistance;
    Camera cam = renderer.getCamera();
    float rVal = 0;
    if (Vector3f.isValidVector(cam.getLocation()) &&
        Vector3f.isValidVector(spat.getWorldTranslation()))
      rVal = cam.getLocation().distance(spat.getWorldTranslation());
    spat.queueDistance = rVal;
    return rVal;
  }

  public void renderBuckets() {
    renderOpaqueBucket();
    renderTransparentBucket();
    renderOrthoBucket();
  }

  private void renderOpaqueBucket() {
//    System.err.println("drawing opaq. items: "+opaqueBucket.size());
    Iterator it = opaqueBucket.iterator();
    while (it.hasNext()) {
      Spatial spat = (Spatial)it.next();
//      System.err.println("draw: "+spat.getName());
      spat.onDraw(renderer);
      spat.queueDistance = Float.NEGATIVE_INFINITY;
    }
    opaqueBucket.clear();
  }

  private void renderTransparentBucket() {
//    System.err.println("drawing transp. items: "+transparentBucket.size());
    Iterator it = transparentBucket.iterator();
    while (it.hasNext()) {
      Spatial spat = (Spatial)it.next();
//      System.err.println("draw: "+spat.getName());
      spat.onDraw(renderer);
      spat.queueDistance = Float.NEGATIVE_INFINITY;
    }
    transparentBucket.clear();
  }

  private void renderOrthoBucket() {
//    System.err.println("drawing ortho. items: "+orthoBucket.size());
    Iterator it = orthoBucket.iterator();
    while (it.hasNext()) {
      Spatial spat = (Spatial)it.next();
//      System.err.println("draw: "+spat.getName());
      spat.onDraw(renderer);
    }
    orthoBucket.clear();
  }

  class OpaqueComp implements Comparator {
    public int compare(Object o1, Object o2) {
      float d1 = distanceToCam((Spatial)o1);
      float d2 = distanceToCam((Spatial)o2);
      if (d1 <= d2)
        return -1;
      else return 1;
    }
  }

  class TransparentComp implements Comparator {
    public int compare(Object o1, Object o2) {
      float d1 = distanceToCam((Spatial)o1);
      float d2 = distanceToCam((Spatial)o2);
      if (d1 <= d2)
        return 1;
      else return -1;
    }
  }

  class OrthoComp implements Comparator {
    public int compare(Object o1, Object o2) {
      Spatial s1 = (Spatial)o1;
      Spatial s2 = (Spatial)o2;
      if (s1.getZOrder() < s2.getZOrder()) return 1;
      else return -1;
    }
  }

}
