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

package com.jme.scene.lod;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.TriMesh;
import com.jme.util.AreaUtils;
import java.util.logging.Level;
import com.jme.util.LoggingSystem;
import com.jme.system.DisplaySystem;

/**
 * <code>ClodMesh</code>
 * originally ported from David Eberly's c++, modifications and
 * enhancements made from there.
 * @author Joshua Slack
 * @version $Id: AreaClodMesh.java,v 1.9 2004-07-21 22:12:02 guurk Exp $
 */
public class AreaClodMesh extends ClodMesh {
  float trisPerPixel = 1f;
  float distTolerance = 1f;
  float lastDistance = 0f;

  /**
   * Empty Constructor to be used internally only.
   */
  public AreaClodMesh() {}

  public AreaClodMesh(String name) {
      super(name);
  }
  public AreaClodMesh(
      String name,
      TriMesh data,
      CollapseRecord[] records) {

    super(name, data, records);
  }

  public AreaClodMesh(
      String name,
      Vector3f[] vertices,
      Vector3f[] normal,
      ColorRGBA[] color,
      Vector2f[] texture,
      int[] indices, CollapseRecord[] records) {

    super(name, vertices, normal, color, texture, indices, records);
    
  }

  public int chooseTargetRecord(Renderer r) {
    if (getWorldBound() == null) {
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "AreaClodMesh found with no Bounds.");
      return 0;
    }
    
    float newDistance = getWorldBound().distanceTo(r.getCamera().getLocation());
    if (Math.abs(newDistance - lastDistance) <= distTolerance)
      return targetRecord; // we haven't moved relative to the model, send the old measurement back.
    if (lastDistance > newDistance && targetRecord == 0)
      return targetRecord; // we're already at the lowest setting and we just got closer to the model, no need to keep trying.
    if (lastDistance < newDistance && targetRecord == records.length-1)
      return targetRecord; // we're already at the highest setting and we just got further from the model, no need to keep trying.

    lastDistance = newDistance;

    // estimate area of polygon via bounding volume
    float area = AreaUtils.calcScreenArea(getWorldBound(), lastDistance, DisplaySystem.getDisplaySystem().getWidth());
    float trisToDraw = area * trisPerPixel;
    if (records == null || records.length == 0) {
      LoggingSystem.getLogger().log(Level.WARNING,
                                    "Records was null.");
      return 0;
    }
    
    targetRecord = records.length - 1;
    for (int i = records.length; --i >= 0; ) {
      if (trisToDraw - records[i].numbTriangles < 0) break;
      targetRecord = i;
    }
    //System.err.println("choosing record: "+targetRecord);
    return targetRecord;
  }

  public void setTargetRecord(int target) {
    // ignore;
  }

  public float getTrisPerPixel() {
    return trisPerPixel;
  }

  public void setTrisPerPixel(float trisPerPixel) {
    this.trisPerPixel = trisPerPixel;
  }

  public float getDistanceTolerance() {
    return distTolerance;
  }

  public void setDistanceTolerance(float tolerance) {
    this.distTolerance = tolerance;
  }
}
