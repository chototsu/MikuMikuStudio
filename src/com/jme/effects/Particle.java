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
 * <code>Particle</code> defines a single Particle of a Particle system.
 * Generally, you would not interact with this class directly.
 *
 * @author Joshua Slack
 * @version $Id: Particle.java,v 1.9 2004-03-28 03:15:33 renanse Exp $
 */
public class Particle {

  Vector3f verts[];
  Vector3f initialLocation, location;
  ColorRGBA color;

  private float currentSize;
  private float lifeSpan;
  private float lifeRatio;
  private int currentAge, halfAge;
  private Vector3f speed;
  private Vector3f randomPoint;
  private ParticleManager parent;
  private Vector3f bbX, bbY;
  public int status;

  /** Particle is dead -- not in play. */
  public static final int DEAD = 0;
  /** Particle is currently active. */
  public static final int ALIVE = 1;
  /** Particle is available for spawning. */
  public static final int AVAILABLE = 2;

  /**
   * Particle constructor
   * @param parent ParticleManager parent of this particle
   * @param speed initial velocity of the particle in the x,y and z directions
   * @param iLocation initial location of the particle
   * @param lifeSpan how long the particle should live for
   */
  public Particle(ParticleManager parent, Vector3f speed,
                     Vector3f iLocation, float lifeSpan) {
    this.lifeSpan = lifeSpan;
    this.speed = (Vector3f) speed.clone();
    this.initialLocation = (Vector3f) iLocation.clone();
    this.location = new Vector3f();
    this.parent = parent;

    color = (ColorRGBA) parent.getStartColor().clone();
    randomPoint = new Vector3f();
    currentAge = 0;
    status = AVAILABLE;
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
    this.speed.set(speed);

    this.color.set(parent.getStartColor().r, parent.getStartColor().g,
                   parent.getStartColor().b, parent.getStartColor().a);
    currentSize = parent.getStartSize();
    currentAge = 0;
    status = AVAILABLE;
  }

  /**
   * Update the vertices for this particle, taking size, direction of viewer
   * and current location into account.
   */
  public void updateVerts() {
    Camera cam = parent.getCamera();

    bbX.set(cam.getLeft()).multLocal(currentSize);
    bbY.set(cam.getUp()).multLocal(currentSize);

    location.add(bbX, verts[1]).subtractLocal(bbY);
    location.add(bbX, verts[2]).addLocal(bbY);
    location.subtract(bbX, verts[3]).addLocal(bbY);
    location.subtract(bbX, verts[0]).subtractLocal(bbY);
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
   * @return true if this particle is not ALIVE
   */
  public boolean updateAndCheck(float secondsPassed) {

    if (status != ALIVE) {
      return true;
    }
    currentAge += secondsPassed * 1000; // add 10ms to age
    halfAge = currentAge >> 1;
    if (currentAge > lifeSpan) {
      status = DEAD;
      color.a = 0;
      return true;
    }
    location.x = currentAge * ((halfAge*parent.getGravityForce().x) + speed.x) + initialLocation.x;
    location.y = currentAge * ((halfAge*parent.getGravityForce().y) + speed.y) + initialLocation.y;
    location.z = currentAge * ((halfAge*parent.getGravityForce().z) + speed.z) + initialLocation.z;

    if (parent.getRandomMod() != 0.0f) {
      randomPoint.set(parent.getRandomMod() *
                      2*( FastMath.nextRandomFloat() - .5f),
                      0.0f,
                      parent.getRandomMod() *
                      2*( FastMath.nextRandomFloat() - .5f));
      location.addLocal(randomPoint);
    }

    lifeRatio = currentAge / lifeSpan;

    // update the size, currently, the size
    // updates both the x and y values. So you always
    // get a square
    currentSize = parent.getStartSize();
    currentSize -=
        ( (currentSize - parent.getEndSize()) * lifeRatio);

    // interpolate colors
    color.set(parent.getStartColor());
    color.r -=
        ( (color.r - parent.getEndColor().r) * lifeRatio);
    color.g -=
        ( (color.g - parent.getEndColor().g) * lifeRatio);
    color.b -=
        ( (color.b - parent.getEndColor().b) * lifeRatio);
    color.a -=
        ( (color.a - parent.getEndColor().a) * lifeRatio);

    updateVerts();

    return false;
  }


  /**
   * Resets current age to 0
   */
  public void resetAge() {
    currentAge = 0;
  }

}
