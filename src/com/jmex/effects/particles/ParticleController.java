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
import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>ParticleController</code> controls and maintains the parameters of a
 * ParticleGeometry particle system over time.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleController.java,v 1.10 2006-09-07 14:57:51 nca Exp $
 */
public class ParticleController extends Controller {

    private static final long serialVersionUID = 1L;

    private ParticleGeometry particles;
    private int particlesToCreate = 0;
    private float releaseVariance;
    private float currentTime;
    private float prevTime;
    private float releaseParticles;
    private float timePassed;
    private float precision;
    private boolean controlFlow;

    private int iterations;
    private ArrayList<ParticleInfluence> influences;

    public ParticleController() {}
    
    /**
     * ParticleManager constructor
     * 
     * @param numParticles
     *            Desired number of particles in this system.
     */
    public ParticleController(ParticleGeometry particleMesh) {
        this.particles = particleMesh;

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
            if (timePassed < precision * getSpeed()) {
                return;
            }
            prevTime = currentTime;

            // Update the current rotation matrix if needed.
            particles.updateRotationMatrix();
            if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {

                if (controlFlow) {
                    releaseParticles += (particles.getReleaseRate() *
                        timePassed * (1.0f + releaseVariance *
                            (FastMath.nextRandomFloat() - 0.5f)));
                    particlesToCreate = (int) releaseParticles;
                    if (particlesToCreate > 0)
                        releaseParticles -= particlesToCreate;
                    else
                        particlesToCreate = 0;
                }

                particles.updateInvScale();

                if (influences != null) {
                    for (ParticleInfluence influence : influences) {
                        influence.prepare(particles);
                    }
                }
                
                int i = 0;
                boolean dead = true;
                while (i < particles.getNumParticles()) {
                    Particle p = particles.getParticle(i);
                    
                    if (influences != null && p.getStatus() == Particle.ALIVE) {
                        for (int x = 0; x < influences.size(); x++) {
                            ParticleInfluence inf = influences.get(x);
                            if (inf.isEnabled())
                                inf.apply(timePassed, p, i);
                        }
                    }
                        
                    
                    if (p.updateAndCheck(timePassed)
                            && (!controlFlow || particlesToCreate > 0)) {
                        if (p.getStatus() == Particle.DEAD
                                && getRepeatType() == RT_CLAMP) {
                            ;
                        } else {
                            dead = false;
                            if (controlFlow) {
                                particlesToCreate--;
                            }
                            p.recreateParticle(particles.getRandomLifeSpan());
                            p.setStatus(Particle.ALIVE);
                            particles.initParticleLocation(i);
                            particles.resetParticleVelocity(i);
                            p.updateVerts(null);
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
            if (particles.getBatch(0).getModelBound() != null) {
                particles.updateModelBound();
                particles.updateWorldBoundManually();
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
    public boolean isControlFlow() {
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
     * Get the Spatial that holds all of the particle information for display.
     * 
     * @return Spatial holding particle information.
     */
    public Spatial getParticles() {
        return particles;
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
     * Add an external influence to this particle controller.
     * 
     * @param influence
     *            ParticleInfluence
     */
    public void addInfluence(ParticleInfluence influence) {
        if (influences == null) influences = new ArrayList<ParticleInfluence>(1);
        influences.add(influence);
    }

    /**
     * Remove an influence from this particle controller.
     * 
     * @param influence
     *            ParticleInfluence
     * @return true if found and removed.
     */
    public boolean removeInfluence(ParticleInfluence influence) {
        if (influences == null) return false;
        return influences.remove(influence);
    }
    
    /**
     * Returns the list of influences acting on this particle controller.
     * 
     * @return ArrayList
     */
    public ArrayList<ParticleInfluence> getInfluences() {
        return influences;
    }
    
    public void clearInfluences() {
        if (influences != null)
            influences.clear();
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
        capsule.write(particles, "particleMesh", null);
        capsule.write(releaseVariance, "releaseVariance", 0);
        capsule.write(precision, "precision", 0);
        capsule.write(controlFlow, "controlFlow", false);
        capsule.write(iterations, "iterations", 0);
        capsule.writeSavableArrayList(influences, "influences", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        particles = (ParticleGeometry)capsule.readSavable("particleMesh", null);
        releaseVariance = capsule.readFloat("releaseVariance", 0);
        precision = capsule.readFloat("precision", 0);
        controlFlow = capsule.readBoolean("controlFlow", false);
        iterations = capsule.readInt("iterations", 0);
        influences = capsule.readSavableArrayList("influences", null);
    }
}
