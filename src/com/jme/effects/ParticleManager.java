/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
import com.jme.math.Line;
import com.jme.math.Matrix3f;
import com.jme.math.Rectangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;

/**
 * <code>ParticleManager</code>
 *
 * Example usage:
 * <code>
 *   ParticleManager manager = new ParticleManager(300, display.getRenderer().getCamera());
 *   manager.getParticles().addController(manager);
 *   someNode.attachChild(manager.getParticles());
 * </code>
 *
 * See the method comments for more usage information.
 *
 * note: The idea of using one TriMesh to control particles and much of the code
 *       related to picking starting angles was kindly donated by Java Cool Dude.
 *
 * @author Joshua Slack
 * @version $Id: ParticleManager.java,v 1.20 2005-03-17 21:41:42 renanse Exp $
 *
 * TODO Points and Lines (not just quads)
 * TODO Particles stretched based on historical path
 * TODO Particle motion models
 */
public class ParticleManager extends Controller {
    
    private static final long serialVersionUID = 1L;
    
    public static final int GS_POINT = 0;
    public static final int GS_LINE = 1;
    public static final int GS_RECTANGLE = 2;
    public static final int GS_MESH = 3;
    
    private final static Vector2f sharedTextureData[] = {
        new Vector2f(0.0f, 0.0f), new Vector2f(1.0f, 0.0f),
        new Vector2f(1.0f, 1.0f), new Vector2f(0.0f, 1.0f)
    };
    
    private Vector3f particleSpeed;
    private TriMesh particlesGeometry;
    private int noParticles;
    private int releaseRate; // particles per second
    private int released;
    private int particlesToCreate = 0;
    private Vector3f upVector;
    private Vector3f gravityForce;
    private Vector3f emissionDirection;
    private Vector3f originCenter;
    private Vector3f invScale; 
    private Matrix3f rotMatrix;
    private Particle particles[];
    private Vector3f geometryCoordinates[];
    private ColorRGBA appearanceColors[];
    private ColorRGBA startColor;
    private ColorRGBA endColor;
    private float releaseVariance;
    private float initialVelocity;
    private float particleSpinSpeed;
    private float minimumLifeTime;
    private float maximumAngle;
    private float startSize, endSize;
    private float randomMod;
    private float currentTime;
    private float prevTime;
    private float releaseTime;
    private float timePassed;
    private float precision;
    private boolean controlFlow;
    
    private int geoToUse = GS_POINT;
    private Line psLine;
    private Rectangle psRect;
    private Geometry psMesh;
    
    private Camera camera;
    private int iterations;
    
    /**
     * ParticleManager constructor
     *
     * @param noParticles Desired number of particles in this system.
     * @param cam The camera to have the billboarded particles face.
     */
    public ParticleManager(int noParticles, Camera cam) {
        camera = cam;
        this.noParticles = noParticles;
        
//      init non-null, non-zero field members
        rotMatrix = new Matrix3f();
        originCenter = new Vector3f();
        upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        gravityForce = new Vector3f(0.0f, 0.0f, 0.0f);
        emissionDirection = new Vector3f(0.0f, 1.0f, 0.0f);
        startColor = new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f);
        endColor = new ColorRGBA(1.0f, 1.0f, 0.0f, 0.0f);
        particleSpeed = new Vector3f();
        
        setMinTime(0);
        setMaxTime(Float.MAX_VALUE);
        setRepeatType(Controller.RT_WRAP);
        setSpeed(1.0f);
        
        initialVelocity = 1.0f;
        minimumLifeTime = 2500f;
        maximumAngle = 0.7853982f;
        startSize = 20f;
        endSize = 4f;
        randomMod = 1.0f;
        releaseRate = noParticles;
        releaseVariance = 0;
        particleSpinSpeed = 0;
        controlFlow = false;
        precision = .01f; // 10ms
        
        geometryCoordinates = new Vector3f[noParticles << 2];
        int[] indices = new int[noParticles * 6];
        for (int j = 0; j < noParticles; j++) {
            indices[0 + j * 6] = j * 4 + 2;
            indices[1 + j * 6] = j * 4 + 1;
            indices[2 + j * 6] = j * 4 + 0;
            indices[3 + j * 6] = j * 4 + 3;
            indices[4 + j * 6] = j * 4 + 2;
            indices[5 + j * 6] = j * 4 + 0;
        }
        
        appearanceColors = new ColorRGBA[noParticles << 2];
        particles = new Particle[noParticles];
        
        // overriding the worldRotation allows the programmer to attach the particles
        // to things which have changing rotation without ruining the particle bill-
        // boarding.
        particlesGeometry = new TriMesh("particles") {
            private static final long serialVersionUID = 1L;
            public void updateGeometricState(float time, boolean initiator) {
                super.updateGeometricState(time, initiator);
                if (geoToUse == GS_MESH) {
                    psMesh.getWorldRotation().mult(emissionDirection, worldEmit);
                } else
                    getWorldRotation().mult(emissionDirection, worldEmit);
                getWorldRotation().set(0,0,0,1);
            }
        };
        particlesGeometry.setVertices(new Vector3f[noParticles << 2]);
        particlesGeometry.setTextures(new Vector2f[noParticles << 2], 0);
        particlesGeometry.setIndices(indices);
        particlesGeometry.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        particlesGeometry.setLightCombineMode(LightState.OFF);
        particlesGeometry.setTextureCombineMode(TextureState.REPLACE);
        
        invScale = new Vector3f();
        
        Vector3f speed = new Vector3f();
        Vector3f location = new Vector3f();
        updateRotationMatrix();
        for (int k = 0; k < noParticles; k++) {
            float life = getRandomLifeSpan();
            getRandomSpeed(speed);
            particles[k] = new Particle(this, speed, location, life);
            for (int a = 3; a >= 0; a--) {
                particlesGeometry.setTextureCoord(0, (k << 2) + a, sharedTextureData[a]);
                geometryCoordinates[ (k << 2) + a] = particles[k].verts[a];
                appearanceColors[ (k << 2) + a] = particles[k].color;
            }
            
        }
        particlesGeometry.updateTextureBuffer();
        
        warmUp(60);
    }
    
    /**
     * Update the particles managed by this manager.  If any particles are "dead"
     * recreate them at the origin position (which may be a point, line or
     * rectangle.)
     * See com.jme.scene.Controller.update(float)
     * @param secondsPassed float
     */
    public void update(float secondsPassed) {
        if (isActive()) {
            secondsPassed *= getSpeed();
            currentTime += secondsPassed;
            timePassed = currentTime - prevTime;
            if (timePassed < precision)
                return;
            
            prevTime = currentTime;

            // Update the current rotation matrix if needed.
            updateRotationMatrix();
            
            if (currentTime >= getMinTime() && currentTime <= getMaxTime()) {
                
                if (controlFlow) {
                    if (currentTime - releaseTime > 1.0f) {
                        released = 0;
                        releaseTime = currentTime;
                    }
                    particlesToCreate = (int) ( (float) releaseRate * timePassed *
                            (1.0f +
                                    releaseVariance *
                                    (FastMath.nextRandomFloat() - 0.5f)));
                    if (particlesToCreate <= 0)
                        particlesToCreate = 1;
                    if (releaseRate - released <= 0)
                        particlesToCreate = 0;
                }
                
                
                invScale.set(particlesGeometry.getLocalScale());
                invScale.set(1f/invScale.x,1f/invScale.y,1f/invScale.z);
                int i = 0;
                boolean dead = true;
                while (i < noParticles) {
                    if (particles[i].updateAndCheck(timePassed) &&
                            (!controlFlow || particlesToCreate > 0)) {
                        if (particles[i].status == Particle.DEAD &&
                                getRepeatType() == RT_CLAMP) {
                            ;
                        } else {
                            dead = false;
                            if (controlFlow) {
                                released++;
                                particlesToCreate--;
                            }
                            getRandomSpeed(particleSpeed);
                            particles[i].recreateParticle(particleSpeed, getRandomLifeSpan());
                            particles[i].status = Particle.ALIVE;
                            
                            switch (getGeometry()) {
                            case GS_LINE:
                                particles[i].location.set(getLine().random());
                                break;
                            case GS_RECTANGLE:
                                particles[i].location.set(getRectangle().random());
                                break;
                            case GS_MESH:
                                particles[i].location.set(getGeoMesh().randomVertice());
                                break;
                            case GS_POINT:
                            default:
                                particles[i].location.set(originCenter);
                            break;
                            }
                            particles[i].location.multLocal(invScale);
                            particles[i].updateVerts();
                        }
                    } else dead = false;
                    i++;
                }
                
                particlesGeometry.setVertices(geometryCoordinates);
                particlesGeometry.setColors(appearanceColors);
                if (dead) setActive(false);
            }
            if (particlesGeometry.getModelBound() != null) {
                particlesGeometry.getModelBound().computeFromPoints(particlesGeometry.getVertices());
            }
        }
    }
    
    /**
     * Force all dead particles back to life.
     */
    public void forceRespawn() {
        for (int i = noParticles; --i >= 0; ) {
            particles[i].status = Particle.AVAILABLE;
        }
        setActive(true);
    }
    
    /**
     * Setup the rotation matrix used to determine initial particle velocity
     * based on emission angle and emission direction.
     *
     * called automatically by the set* methods for those parameters.
     */
    private Vector3f oldEmit = new Vector3f(Float.NaN,Float.NaN,Float.NaN);
    private Vector3f worldEmit = new Vector3f();
    private float matData[][] = new float[3][3];
    public void updateRotationMatrix() {
        
        if (oldEmit.equals(worldEmit)) return;

        float upDotEmit = upVector.dot(worldEmit);
        if ( ( (double) FastMath.abs(upDotEmit)) > 1.0d - FastMath.DBL_EPSILON) {
            Vector3f absUpVector = new Vector3f();
            Vector3f abUpMinUp = new Vector3f();
            absUpVector.x = upVector.x <= 0.0f ? -upVector.x : upVector.x;
            absUpVector.y = upVector.y <= 0.0f ? -upVector.y : upVector.y;
            absUpVector.z = upVector.z <= 0.0f ? -upVector.z : upVector.z;
            if (absUpVector.x < absUpVector.y) {
                if (absUpVector.x < absUpVector.z) {
                    absUpVector.x = 1.0f;
                    absUpVector.y = absUpVector.z = 0.0f;
                } else {
                    absUpVector.z = 1.0f;
                    absUpVector.x = absUpVector.y = 0.0f;
                }
            } else
                if (absUpVector.y < absUpVector.z) {
                    absUpVector.y = 1.0f;
                    absUpVector.x = absUpVector.z = 0.0f;
                } else {
                    absUpVector.z = 1.0f;
                    absUpVector.x = absUpVector.y = 0.0f;
                }
            abUpMinUp = absUpVector.subtract(upVector);
            Vector3f upXemit = absUpVector.subtract(worldEmit);
            float f4 = 2.0f / abUpMinUp.dot(abUpMinUp);
            float f6 = 2.0f / upXemit.dot(upXemit);
            float f8 = f4 * f6 * abUpMinUp.dot(upXemit);
            float af1[] = {
                    abUpMinUp.x, abUpMinUp.y, abUpMinUp.z
            };
            float af2[] = {
                    upXemit.x, upXemit.y, upXemit.z
            };
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matData[i][j] = ( -f4 * af1[i] * af1[j] - f6 * af2[i] * af2[j]) +
                    f8 * af2[i] * af1[j];
                    
                }
                matData[i][i]++;
            }
            
        } else {
            Vector3f upXemit = upVector.cross(worldEmit);
            float f2 = 1.0f / (1.0f + upDotEmit);
            float f5 = f2 * upXemit.x;
            float f7 = f2 * upXemit.z;
            float f9 = f5 * upXemit.y;
            float f10 = f5 * upXemit.z;
            float f11 = f7 * upXemit.y;
            matData[0][0] = upDotEmit + f5 * upXemit.x;
            matData[0][1] = f9 - upXemit.z;
            matData[0][2] = f10 + upXemit.y;
            matData[1][0] = f9 + upXemit.z;
            matData[1][1] = upDotEmit + f2 * upXemit.y * upXemit.y;
            matData[1][2] = f11 - upXemit.x;
            matData[2][0] = f10 - upXemit.y;
            matData[2][1] = f11 + upXemit.x;
            matData[2][2] = upDotEmit + f7 * upXemit.z;
        }
        rotMatrix.set(matData);
        oldEmit.set(worldEmit);
    }
    
    /**
     * Generate a random velocity within the parameters of max angle and
     * the rotation matrix.
     *
     * @param pSpeed a vector to store the results in.
     */
    private void getRandomSpeed(Vector3f pSpeed) {
        float randDir = FastMath.TWO_PI * FastMath.nextRandomFloat();
        float clampAngle = clampToMaxAngle(FastMath.PI * FastMath.nextRandomFloat());
        pSpeed.x = (float) (FastMath.cos(randDir) * FastMath.sin(clampAngle));
        pSpeed.y = (float) FastMath.cos(clampAngle);
        pSpeed.z = (float) (FastMath.sin(randDir) * FastMath.sin(clampAngle));
        rotateVectorSpeed(pSpeed);
        pSpeed.multLocal(initialVelocity);
    }
    
    /**
     * Make sure the given angle is less than or equal to the max angle.
     *
     * @param angle the angle to check
     * @return the angle or max angle if the supplied angle was greater.
     */
    private float clampToMaxAngle(float angle) {
        if (angle > maximumAngle) {
            return maximumAngle;
        } else {
            return angle;
        }
    }
    
    /**
     * generate a random lifespan between 100% and 150% of the min life span.
     *
     * @return the generated lifespan value
     */
    private float getRandomLifeSpan() {
        float life = minimumLifeTime * (0.5f + FastMath.nextRandomFloat());
        if (life <= minimumLifeTime) {
            return minimumLifeTime;
        } else {
            return life;
        }
    }
    
    /**
     * Apply the rotation matrix to a given vector representing a particle velocity.
     *
     * @param pSpeed the velocity vector to be modified.
     */
    private void rotateVectorSpeed(Vector3f pSpeed) {
        
        float x = pSpeed.x,
        y = pSpeed.y,
        z = pSpeed.z;
        
        pSpeed.x = -1 * ( (rotMatrix.m00 * x) +
                (rotMatrix.m10 * y) +
                (rotMatrix.m20 * z));
        
        pSpeed.y = (rotMatrix.m01 * x) +
        (rotMatrix.m11 * y) +
        (rotMatrix.m21 * z);
        
        pSpeed.z = -1 * ( (rotMatrix.m02 * x) +
                (rotMatrix.m12 * y) +
                (rotMatrix.m22 * z));
    }
    
    /**
     * Set the origin for any new particles created (or recreated) by this manager.
     * This is applicable only to managers generating from a point (not a line,
     * rectangle, etc..)
     *
     * @param origin new origin position
     */
    public void setParticlesOrigin(Vector3f origin) {
        originCenter.set(origin);
    }
    
    /**
     * Get the origin point set in this manager.
     *
     * @return origin
     */
    public Vector3f getParticlesOrigin() {
        return originCenter;
    }
    
    /**
     * Set the acceleration for any new particles created (or recreated) by this manager.
     *
     * @param velocity particle v0
     */
    public void setInitialVelocity(float velocity) {
        this.initialVelocity = velocity;
    }
    
    /**
     * Get the acceleration set in this manager.
     *
     * @return The initialVelocity
     */
    public float getInitialVelocity() {
        return initialVelocity;
    }
    
    /**
     * Set the start color for particles.  This is the base color of the quad.
     *
     * @param color The start color.
     */
    public void setStartColor(ColorRGBA color) {
        this.startColor = color;
    }
    
    /**
     * <code>getStartColor</code> returns the starting color.
     *
     * @return ColorRGBA The begining color.
     */
    public ColorRGBA getStartColor() {
        return startColor;
    }
    
    /**
     * Set the end color for particles.  The base color of the quad will linearly
     * approach this color from the start color over the lifetime of the particle.
     *
     * @param color ColorRGBA The ending color.
     */
    public void setEndColor(ColorRGBA color) {
        this.endColor = color;
    }
    
    /**
     * getEndColor returns the ending color.
     *
     * @return The ending color
     */
    public ColorRGBA getEndColor() {
        return endColor;
    }
    
    /**
     * Set the Camera whose position should be used to determine billboard
     * facing direction.
     *
     * @param cam Camera
     */
    public void setCamera(Camera cam) {
        camera = cam;
    }
    
    /**
     * Get the currently set Camera used for billboad calcs.
     *
     * @return Camera
     */
    public Camera getCamera() {
        return camera;
    }
    
    /**
     * Set a vector describing the force of gravity on a particle.
     * Generally, the values should be less than .01f
     *
     * @param force Vector3f
     */
    public void setGravityForce(Vector3f force) {
        gravityForce.set(force);
    }
    
    /**
     * getGravityForce returns the gravity force.
     *
     * @return The gravity force vector.
     */
    public Vector3f getGravityForce() {
        return gravityForce;
    }
    
    /**
     * Set the size of the new particles generated by this manager.
     * If a value less than zero is given, zero is used.
     *
     * @param size Start size.
     */
    public void setStartSize(float size) {
        startSize = size >= 0.0f ? size : 0.0f;
    }
    
    /**
     * getStartSize returns the start size.
     *
     * @return The start size.
     */
    public float getStartSize() {
        return startSize;
    }
    
    /**
     * Set the size particles will approach as they age.
     * If a value less than zero is given, zero is used.
     *
     * @param size The ending size.
     */
    public void setEndSize(float size) {
        endSize = size >= 0.0f ? size : 0.0f;
    }
    
    /**
     * getEndSize returns the end size.
     *
     * @return The end size.
     */
    public float getEndSize() {
        return endSize;
    }
    
    /**
     * Set the general direction that particles are emitted in.
     * This will be modified by the emission angle.  You need to use
     * a non-zero normalized direction.
     *
     * @param direction Vector3f
     */
    public void setEmissionDirection(Vector3f direction) {
        emissionDirection.set(direction);
        updateRotationMatrix();
    }
    
    /**
     * getEmissionDirection returns the emission direction.
     *
     * @return The emission direction.
     */
    public Vector3f getEmissionDirection() {
        return emissionDirection;
    }
    
    /**
     * Set the maximum angle (in radians) that particles can be emitted away from
     * the emission direction.  Any angle less than 0 is trimmed to 0.
     *
     * @param f The new emission maximum angle.
     */
    public void setEmissionMaximumAngle(float f) {
        maximumAngle = f >= 0.0f ? f : 0.0f;
    }
    
    /**
     * getEmissionMaximumAngle returns the maximum emission angle.
     *
     * @return The maximum emission angle.
     */
    public float getEmissionMaximumAngle() {
        return maximumAngle;
    }
    
    /**
     * Set the minimum lifespan of new particles (or recreated) managed by this
     * manager.
     *
     * if a value less than zero is given, 1.0f is used.
     *
     * @param lifeSpan float
     */
    public void setParticlesMinimumLifeTime(float lifeSpan) {
        minimumLifeTime = lifeSpan >= 0.0f ? lifeSpan : 1.0f;
    }
    
    /**
     * getParticlesMinimumLifeTime returns the minimum life time of a particle.
     *
     * @return The current minimum life time.
     */
    public float getParticlesMinimumLifeTime() {
        return minimumLifeTime;
    }
    
    /**
     * Set the spinSpeed of new particles managed by this manager.
     * Setting it to 0 means no spin.
     * @param speed float
     */
    public void setParticleSpinSpeed(float speed) {
        particleSpinSpeed = speed;
    }
    
    /**
     * getParticleSpinSpeed returns the current spin speed of particles.
     *
     * @return current spin speed of particles.
     */
    public float getParticleSpinSpeed() {
        return particleSpinSpeed;
    }
    
    /**
     * Set the "randomness" modifier.  0 = not random
     *
     * @param mod The new randomness of particle information.
     */
    public void setRandomMod(float mod) {
        randomMod = mod;
    }
    
    /**
     * getRandomFactor returns the current randomness of particles.
     *
     * @return float The current randomness.
     */
    public float getRandomMod() {
        return randomMod;
    }
    
    /**
     * Get the number of particles managed by this manager.
     *
     * @return The number of particles managed by this manager.
     */
    public int getParticlesNumber() {
        return noParticles;
    }
    
    /**
     * Get the number of particles the manager should release per second.
     *
     * @return The number of particles that should be released per second.
     */
    public int getReleaseRate() {
        return releaseRate;
    }
    
    /**
     * Set the number of particles the manager should release per second.
     *
     * @param particlesPerSecond number of particles per second
     */
    public void setReleaseRate(int particlesPerSecond) {
        this.releaseRate = particlesPerSecond;
    }
    
    /**
     * Get how soon after the last update the manager will send updates to the particles.
     *
     * @return The precision.
     */
    public float getPrecision() {
        return precision;
    }
    
    /**
     * Set how soon after the last update the manager will send updates to the particles.
     * Defaults to .01f (10ms)<br><br>
     * This means that if an update is called every 2ms (e.g. running at 500 FPS)
     * the particles position and stats will be updated every fifth frame with the
     * elapsed time (in this case, 10ms) since previous update.
     *
     * @param precision in seconds
     */
    public void setPrecision(float precision) {
        this.precision = precision;
    }
    
    /**
     * Get the variance possible on the release rate.
     * 0.0f = no variance
     * 0.5f = between releaseRate / 2f and  1.5f * releaseRate
     *
     * @return release variance as a percent.
     */
    public float getReleaseVariance() {
        return releaseVariance;
    }
    
    /**
     * Set the variance possible on the release rate.
     *
     * @param variance release rate +/- variance as a percent  (eg. .5 = 50%)
     */
    public void setReleaseVariance(float variance) {
        this.releaseVariance = variance;
    }
    
    /**
     * Does this manager regulate the particle flow?
     *
     * @return true if this manager regulates how many particles per sec are emitted.
     */
    public boolean getControlFlow() {
        return controlFlow;
    }
    
    /**
     * Set the regulate flow property on the manager.
     *
     * @param regulate regulate particle flow.
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
        return particlesGeometry;
    }
    
    /**
     * Get which Geometry method is being used by the underlying system.
     * 0 = point
     * 1 = line
     * 2 = rectangle
     * 3 = trimesh
     *
     * @return An int representing hte current geometry method being used.
     */
    public int getGeometry() {
        return geoToUse;
    }
    
    /**
     * Set which Geometry method is being used by the underlying system.
     * This is already done by setGeometry(Line) and setGeometry(Rectangle)
     * You should not need to use this method unless you are switching between
     * geometry already set by those methods.
     *
     * @param type Geometry type to use
     */
    public void setGeometry(int type) {
        geoToUse = type;
    }
    
    /**
     * Set a line segment to be used as the "emittor".
     *
     * @param line New emittor line segment.
     */
    public void setGeometry(Line line) {
        psLine = line;
        geoToUse = GS_LINE;
    }
    
    /**
     * Set a rectangular patch to be used as the "emittor".
     *
     * @param rect New rectangular patch.
     */
    public void setGeometry(Rectangle rect) {
        psRect = rect;
        geoToUse = GS_RECTANGLE;
    }
    
    /**
     * Set a Geometry's verts to be the random emission points
     *
     * @param mesh The new geometry random verts.
     */
    public void setGeometry(Geometry mesh) {
        psMesh = mesh;
        geoToUse = GS_MESH;
    }
    
    /**
     * getLine returns the currently set line segment.
     *
     * @return Current line segment.
     */
    public Line getLine() {
        return psLine;
    }
    
    /**
     * getRectangle returns the currently set rectangle segment.
     *
     * @return Current rectangle segment.
     */
    public Rectangle getRectangle() {
        return psRect;
    }
    
    /**
     * getGeoMesh returns the currently set geometry mesh.
     *
     * @return Current geometry mesh.
     */
    public Geometry getGeoMesh() {
        return psMesh;
    }
    
    /**
     *
     * Return the number this manager has warmed up
     * @return int
     */
    public int getIterations() {
        return iterations;
    }
    
    /**
     * Sets the iterations for the warmup and calls
     * warmUp with the number of iterations as the argument
     * @param iterations
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    /**
     * Runs the update method of this particle manager for iteration seconds
     * with an update every .1 seconds (IE <code>iterations</code> * 10
     * update(.1f) calls).  This is used to "warm up" and get the particle
     * manager going.
     *
     * @param iterations The number of iterations to warm up.
     */
    public void warmUp(int iterations) {
        iterations *= 10;
        for (int i = iterations; --i>= 0; )
            update(.1f);
    }
    
    /**
     * Clones every aspect of this manager into a new manager
     */
    public Object clone() {
        ParticleManager manager = new ParticleManager(getParticlesNumber(),
                getCamera());
        manager.setControlFlow(getControlFlow());
        manager.setEmissionDirection( (Vector3f) getEmissionDirection().clone());
        manager.setEmissionMaximumAngle(getEmissionMaximumAngle());
        manager.setEndColor( (ColorRGBA) getEndColor().clone());
        manager.setEndSize(getEndSize());
        manager.setGeometry(getGeoMesh());
        manager.setGeometry(getGeometry());
        manager.setGravityForce( (Vector3f) getGravityForce().clone());
        manager.setInitialVelocity(getInitialVelocity());
        manager.setParticlesMinimumLifeTime(getParticlesMinimumLifeTime());
        manager.setParticlesOrigin( (Vector3f) getParticlesOrigin().clone());
        manager.setParticleSpinSpeed(getParticleSpinSpeed());
        manager.setPrecision(getPrecision());
        manager.setRandomMod(getRandomMod());
        manager.setReleaseRate(getReleaseRate());
        manager.setReleaseVariance(getReleaseVariance());
        manager.setSpeed(getSpeed());
        manager.setStartColor( (ColorRGBA) getStartColor().clone());
        manager.setStartSize(getStartSize());
        manager.setIterations(getIterations());
        manager.setRepeatType(getRepeatType());
        
        manager.getParticles().addController(manager);
        
        for (int i = 0; i < getParticles().getRenderStateList().length; i++) {
            if (getParticles().getRenderStateList()[i] != null) {
                manager.getParticles().setRenderState(getParticles()
                        .getRenderStateList()[i]);
            }
        }
        
        manager.getParticles().setModelBound(getParticles().getModelBound());
        manager.getParticles().updateModelBound();
        manager.warmUp(manager.getIterations());
        
        return manager;
    }
}
