/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

public class ParticleController extends Controller {

    private static final long serialVersionUID = 1L;

    private ParticleMesh particleMesh;
    private int released;
    private int particlesToCreate = 0;
    private float releaseVariance;
    private float currentTime;
    private float prevTime;
    private float releaseTime;
    private float timePassed;
    private float precision;
    private boolean controlFlow;

    private int iterations;

    public ParticleController() {}
    
    /**
     * ParticleManager constructor
     * 
     * @param numParticles
     *            Desired number of particles in this system.
     */
    public ParticleController(ParticleMesh particleMesh) {
        this.particleMesh = particleMesh;

        setMinTime(0);
        setMaxTime(Float.MAX_VALUE);
        setRepeatType(Controller.RT_WRAP);
        setSpeed(1.0f);

        releaseVariance = 0;
        controlFlow = false;
        precision = .01f; // 10ms

        particleMesh.updateRotationMatrix();
        warmUp(60);
    }

    /**
     * Update the particles managed by this manager. If any particles are "dead"
     * recreate them at the origin position (which may be a point, line or
     * rectangle.) See com.jme.scene.Controller.update(float)
     * 
     * @param secondsPassed
     *            float
     */
    public void update(float secondsPassed) {
        if (isActive()) {
            currentTime += secondsPassed * getSpeed();
            timePassed = currentTime - prevTime;
            if (timePassed < precision) {
                return;
            }
            prevTime = currentTime;

            // Update the current rotation matrix if needed.
            particleMesh.updateRotationMatrix();
            if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {

                if (controlFlow) {
                    if (currentTime - releaseTime > 1.0f) {
                        released = 0;
                        releaseTime = currentTime;
                    }
                    particlesToCreate = (int) ((float) particleMesh
                            .getReleaseRate()
                            * timePassed * (1.0f + releaseVariance
                            * (FastMath.nextRandomFloat() - 0.5f)));
                    if (particlesToCreate <= 0)
                        particlesToCreate = 1;
                    if (particleMesh.getReleaseRate() - released <= 0)
                        particlesToCreate = 0;
                }

                particleMesh.updateInvScale();

                int i = 0;
                boolean dead = true;
                while (i < particleMesh.getNumParticles()) {
                    if (particleMesh.particles[i]
                            .updateAndCheck(timePassed)
                            && (!controlFlow || particlesToCreate > 0)) {
                        if (particleMesh.particles[i].status == Particle.DEAD
                                && getRepeatType() == RT_CLAMP) {
                            ;
                        } else {
                            dead = false;
                            if (controlFlow) {
                                released++;
                                particlesToCreate--;
                            }
                            particleMesh.recreateParticle(i);
                            particleMesh.updateLocation(i);
                        }
                    } else {
                        dead = false;
                    }
                    i++;
                }
                if (dead) {
                    setActive(false);
                }
            }
            if (particleMesh.getBatch(0).getModelBound() != null) {
                particleMesh.updateModelBound();
                particleMesh.updateWorldBoundManually();
            }
        }
    }

    /**
     * Get how soon after the last update the manager will send updates to the
     * particles.
     * 
     * @return The precision.
     */
    public float getPrecision() {
        return precision;
    }

    /**
     * Set how soon after the last update the manager will send updates to the
     * particles. Defaults to .01f (10ms)<br>
     * <br>
     * This means that if an update is called every 2ms (e.g. running at 500
     * FPS) the particles position and stats will be updated every fifth frame
     * with the elapsed time (in this case, 10ms) since previous update.
     * 
     * @param precision
     *            in seconds
     */
    public void setPrecision(float precision) {
        this.precision = precision;
    }

    /**
     * Get the variance possible on the release rate. 0.0f = no variance 0.5f =
     * between releaseRate / 2f and 1.5f * releaseRate
     * 
     * @return release variance as a percent.
     */
    public float getReleaseVariance() {
        return releaseVariance;
    }

    /**
     * Set the variance possible on the release rate.
     * 
     * @param variance
     *            release rate +/- variance as a percent (eg. .5 = 50%)
     */
    public void setReleaseVariance(float variance) {
        this.releaseVariance = variance;
    }

    /**
     * Does this manager regulate the particle flow?
     * 
     * @return true if this manager regulates how many particles per sec are
     *         emitted.
     */
    public boolean getControlFlow() {
        return controlFlow;
    }

    /**
     * Set the regulate flow property on the manager.
     * 
     * @param regulate
     *            regulate particle flow.
     */
    public void setControlFlow(boolean regulate) {
        this.controlFlow = regulate;
    }

    /**
     * Get the TriMesh that holds all of the particle information for display.
     * 
     * @return TriMesh holding particle information.
     */
    public TriMesh getParticles() {
        return particleMesh;
    }

    /**
     * Return the number this manager has warmed up
     * 
     * @return int
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * Sets the iterations for the warmup and calls warmUp with the number of
     * iterations as the argument
     * 
     * @param iterations
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * Runs the update method of this particle manager for iteration seconds
     * with an update every .1 seconds (IE <code>iterations</code> * 10
     * update(.1f) calls). This is used to "warm up" and get the particle
     * manager going.
     * 
     * @param iterations
     *            The number of iterations to warm up.
     */
    public void warmUp(int iterations) {
        iterations *= 10;
        for (int i = iterations; --i >= 0;)
            update(.1f);
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(particleMesh, "particleMesh", null);
        capsule.write(releaseVariance, "releaseVariance", 0);
        capsule.write(precision, "precision", 0);
        capsule.write(controlFlow, "controlFlow", false);
        capsule.write(iterations, "iterations", 0);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        particleMesh = (ParticleMesh)capsule.readSavable("particleMesh", null);
        releaseVariance = capsule.readFloat("releaseVariance", 0);
        precision = capsule.readFloat("precision", 0);
        controlFlow = capsule.readBoolean("controlFlow", false);
        iterations = capsule.readInt("iterations", 0);
    }
}
