/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.effects;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;

/**
 * <code>RenParticle</code> defines a single RenParticle of a RenParticle system.
 * Generally, you would not interact with this class directly.
 *
 * @author Joshua Slack
 * @version $Id: RenParticle.java,v 1.3 2004-03-24 18:45:18 renanse Exp $
 */
public class RenParticle {

  Vector3f verts[];
  Vector3f location;
  ColorRGBA color;

  private float currentSize;
  private float lifeSpan;
  private float currentAge;
  private Vector3f speed;
  private Vector3f randomPoint;
  private RenParticleManager parent;
  private Vector3f bbX, bbY;

  /**
   * RenParticle constructor
   * @param parent RenParticleManager parent of this particle
   * @param speed initial velocity of the particle in the x,y and z directions
   * @param location initial location of the particle
   * @param lifeSpan how long the particle should live for
   */
  public RenParticle(RenParticleManager parent, Vector3f speed,
                     Vector3f location, float lifeSpan) {
    this.lifeSpan = lifeSpan;
    this.speed = (Vector3f) speed.clone();
    this.location = (Vector3f) location.clone();
    this.parent = parent;

    color = (ColorRGBA) parent.getStartColor().clone();
    randomPoint = new Vector3f();
    currentAge = 0f;
    currentSize = parent.getStartSize();
    verts = new Vector3f[4];
    for (int i = 0; i < 4; i++) {
      verts[i] = new Vector3f();
    }
    bbX = new Vector3f();
    bbY = new Vector3f();
  }

  /**
   * Reset particle conditions.  Besides the passed in speed and lifespan,
   * we also reset color and size to their starting values (as given by parent.)
   *
   * @param speed initial velocity of recreated particle
   * @param lifeSpan the recreated particles new lifespan
   */
  public void recreateParticle(Vector3f speed, float lifeSpan) {
    this.lifeSpan = lifeSpan;
    this.speed.set(speed.x, speed.y, speed.z);

    this.color.set(parent.getStartColor().r, parent.getStartColor().g,
                   parent.getStartColor().b, parent.getStartColor().a);
    currentSize = parent.getStartSize();
    currentAge = 0f;
  }

  /**
   * Update the vertices for this particle, taking size, direction of viewer
   * and current location into account.
   */
  public void updateVerts() {
    Camera cam = parent.getCamera();

    bbX.set(cam.getLeft()).multLocal(currentSize);
    bbY.set(cam.getUp()).multLocal(currentSize);

    location.subtract(bbX, verts[0]).subtractLocal(bbY);
    location.add(bbX, verts[1]).subtractLocal(bbY);
    location.add(bbX, verts[2]).addLocal(bbY);
    location.subtract(bbX, verts[3]).addLocal(bbY);
  }

  /**
   * update position (using current location, speed and gravity), color
   * (interpolating between start and end color), size (interpolating between
   * start and end size) and current age of particle.
   *
   * After updating the above, <code>updateVerts()</code> is called.
   *
   * if this particle's age is greater than its lifespan, it is considered dead.
   *
   * @return true if this particle has "died"
   */
  public boolean updateAndCheck() {
    currentAge += 10f; // add 10ms to age
    if (currentAge > lifeSpan) {
      return true;
    }

    speed.scaleAdd(parent.getParticlesSpeed(), parent.getGravityForce(), speed);
    location.scaleAdd(parent.getParticlesSpeed(), speed, location);
    if (parent.getRandomMod() != 0.0f) {
      randomPoint.set(parent.getRandomMod() *
                      ( -FastMath.nextRandomFloat() + FastMath.nextRandomFloat()),
                      0.0f,
                      parent.getRandomMod() *
                      ( -FastMath.nextRandomFloat() + FastMath.nextRandomFloat()));
      location.addLocal(randomPoint);
    }

    float lifeRatio = currentAge / lifeSpan;

    // update the size, currently, the size
    // updates both the x and y values. So you always
    // get a square
    currentSize =
        parent.getStartSize() -
        ( (parent.getStartSize() - parent.getEndSize()) * lifeRatio);

    // interpolate colors
    color.r = parent.getStartColor().r -
        ( (parent.getStartColor().r - parent.getEndColor().r) * lifeRatio);
    color.g = parent.getStartColor().g -
        ( (parent.getStartColor().g - parent.getEndColor().g) * lifeRatio);
    color.b = parent.getStartColor().b -
        ( (parent.getStartColor().b - parent.getEndColor().b) * lifeRatio);
    color.a = parent.getStartColor().a -
        ( (parent.getStartColor().a - parent.getEndColor().a) * lifeRatio);

    updateVerts();

    return false;
  }

}
