/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.effects.particles;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;

/**
 * <code>SimpleParticleForceFactory</code>
 * @author Joshua Slack
 * @version $Id: SimpleParticleForceFactory.java,v 1.1 2006-06-14 03:42:17 renanse Exp $
 */
public final class SimpleParticleForceFactory {

    /**
     * Not used.
     */
    private SimpleParticleForceFactory() {
    }

    /**
     * Creates a basic wind that always blows in a single direction.
     *
     * @param windStr Max strength of wind.
     * @param windDir Direction wind should blow.
     * @param addRandom randomly alter the strength of the wind by 0-100%
     * @return ParticleForce
     */
    public static ParticleForce createBasicWind(final float windStr, final Vector3f windDir, final boolean addRandom) {
        return new ParticleForce() {
            private final float strength = windStr;
            private final Vector3f windDirection = windDir;
            private final boolean random = addRandom;

            public void apply(float dt, Particle p) {
                float tStr = (random ? FastMath.nextRandomFloat() * strength : strength);
                p.getVelocity().addLocal(windDirection.x * tStr * dt,
                                                                             windDirection.y * tStr * dt,
                                                                             windDirection.z * tStr * dt);
            }
        };
    }

    /**
     * Create a basic gravitational force.
     *
     * @return ParticleForce
     */
    public static ParticleForce createBasicGravity(final Vector3f gravForce) {
        return new ParticleForce() {
            private Vector3f gravity = new Vector3f(gravForce);

            public void apply(float dt, Particle p) {
                p.getVelocity().addLocal(gravity.x * dt, gravity.y * dt,
                                                gravity.z * dt);
            }
        };
    }

    /**
     * Create a basic drag force that will use the given drag coefficient.
     * Drag is determined by figuring the current velocity and reversing it, then
     * multiplying by the drag coefficient and dividing by the particle mass.
     *
     * @param dragCoef Should be positive.  Larger values mean more drag but possibly more instability.
     * @return ParticleForce
     */
    public static ParticleForce createBasicDrag(final float dragCoef) {
        return new ParticleForce() {
            private Vector3f velocity = new Vector3f();
            private float dragCoefficient = dragCoef;

            public void apply(float dt, Particle p) {
                // viscous drag
                velocity.set(p.getVelocity());
                p.getVelocity().addLocal(velocity.multLocal(-dragCoefficient * dt * p.getInvMass()));
            }
        };
    }
}
