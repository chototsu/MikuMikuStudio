/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Matrix3f;
import com.jme.math.Rectangle;
import com.jme.math.Ring;
import com.jme.math.TransformMatrix;
import com.jme.math.Triangle;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * ParticleGeometry is an abstract class representing a particle system. A
 * ParticleController must be attached for the effect to be complete.
 * 
 * @author Joshua Slack
 * @version $Id: ParticleGeometry.java,v 1.10 2007-04-03 14:30:20 nca Exp $
 */
public abstract class ParticleGeometry extends Geometry {

    protected static final long serialVersionUID = 2L;

    public static final int ET_POINT = 0;
    public static final int ET_LINE = 1;
    public static final int ET_RECTANGLE = 2;
    public static final int ET_GEOMBATCH = 3;
    public static final int ET_RING = 4;
    
    public static final int PT_QUAD = 0;
    public static final int PT_TRIANGLE = 1;
    public static final int PT_POINT = 2;
    public static final int PT_LINE = 3;
    public static final int PT_GEOMBATCH = 4; // NOT YET SUPPORTED
    
    protected static final float DEFAULT_END_SIZE = 4f;
    protected static final float DEFAULT_START_SIZE = 20f;
    protected static final float DEFAULT_MAX_ANGLE = 0.7853982f;
    protected static final float DEFAULT_MAX_LIFE = 3000f;
    protected static final float DEFAULT_MIN_LIFE = 2000f;

    protected static final ColorRGBA DEFAULT_START_COLOR = new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f);
    protected static final ColorRGBA DEFAULT_END_COLOR = new ColorRGBA(1.0f, 1.0f, 0.0f, 0.0f);

    protected int particleType;
    protected int emitType = ET_POINT;
    protected Line psLine;
    protected Rectangle psRect;
    protected GeomBatch psBatch;
    protected Ring psRing;
    protected boolean cameraFacing = true;
    protected boolean velocityAligned = false;
    
    protected float startSize, endSize;
    protected ColorRGBA startColor;
    protected ColorRGBA endColor;
    protected float initialVelocity;
    protected float minimumLifeTime, maximumLifeTime;
    protected float minimumAngle, maximumAngle;
    protected float particleSpinSpeed;
    protected Vector3f emissionDirection;
    protected TransformMatrix emitterTransform = new TransformMatrix();
    protected Vector3f worldEmit = new Vector3f();
    protected int numParticles;
    protected boolean rotateWithScene = false;
    protected Matrix3f rotMatrix;
    protected float particleOrientation;

    protected FloatBuffer geometryCoordinates;
    protected FloatBuffer appearanceColors;

    // vectors to prevent repeated object creation:
    protected Vector3f upXemit, absUpVector, abUpMinUp;
    protected Vector3f upVector;
    protected Vector3f leftVector;
    protected Vector3f invScale;

    protected Particle particles[];

    // protected Vector3f particleSpeed;
    protected int releaseRate; // particles per second
    protected Vector3f originOffset;
    protected Vector3f originCenter;
    protected static Vector2f workVect2 = new Vector2f();
    protected static Vector3f workVect3 = new Vector3f();

    protected ParticleController controller;

    public ParticleGeometry() { }
    
    public ParticleGeometry(String name, int numParticles) {
        this(name, numParticles, ParticleGeometry.PT_QUAD);
    }
    
    public ParticleGeometry(String name, int numParticles, int particleType) {
        super(name);
        this.numParticles = numParticles;
        this.particleType = particleType;
        emissionDirection = new Vector3f(0.0f, 1.0f, 0.0f);
        minimumLifeTime = DEFAULT_MIN_LIFE;
        maximumLifeTime = DEFAULT_MAX_LIFE;
        maximumAngle = DEFAULT_MAX_ANGLE;
        startSize = DEFAULT_START_SIZE;
        endSize = DEFAULT_END_SIZE;
        startColor = new ColorRGBA(DEFAULT_START_COLOR);
        endColor = new ColorRGBA(DEFAULT_END_COLOR);
        upVector = new Vector3f(Vector3f.UNIT_Y);
        leftVector = new Vector3f(-1, 0, 0);
        originCenter = new Vector3f();
        originOffset = new Vector3f();
        particleSpinSpeed = 0;
        releaseRate = numParticles;
        // init working vectors.. used to prevent additional object creation.
        upXemit = new Vector3f();
        absUpVector = new Vector3f();
        abUpMinUp = new Vector3f();
        initialVelocity = 1.0f;

        rotMatrix = new Matrix3f();
        initializeParticles(numParticles);
        getBatch(0).setCastsShadows(false);
    }

    protected abstract void initializeParticles(int numParticles);

    public static int getVertsForParticleType(int type) {
        switch (type) {
            case PT_TRIANGLE:
            case PT_GEOMBATCH:
                return 3;
            case PT_POINT:
                return 1;
            case PT_LINE:
                return 2;
            case PT_QUAD:
            default:
                return 4;
        }
    }

    public void forceRespawn() {
        for (int i = particles.length; --i >= 0;) {
            particles[i].recreateParticle(0);
            particles[i].setStatus(Particle.ALIVE);
            particles[i].updateAndCheck(1);
            particles[i].setStatus(Particle.AVAILABLE);
        }

        if (controller != null) {
            controller.setActive(true);
        }
    }

    /**
     * Setup the rotation matrix used to determine initial particle velocity
     * based on emission angle and emission direction. called automatically by
     * the set* methods for those parameters.
     */
    protected Vector3f oldEmit = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
    protected float matData[][] = new float[3][3];

    public void updateRotationMatrix() {

        if (oldEmit.equals(worldEmit))
            return;

        float upDotEmit = upVector.dot(worldEmit);
        if (FastMath.abs(upDotEmit) > 1.0d - FastMath.DBL_EPSILON) {
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
            } else if (absUpVector.y < absUpVector.z) {
                absUpVector.y = 1.0f;
                absUpVector.x = absUpVector.z = 0.0f;
            } else {
                absUpVector.z = 1.0f;
                absUpVector.x = absUpVector.y = 0.0f;
            }
            absUpVector.subtract(upVector, abUpMinUp);
            absUpVector.subtract(worldEmit, upXemit);
            float f4 = 2.0f / abUpMinUp.dot(abUpMinUp);
            float f6 = 2.0f / upXemit.dot(upXemit);
            float f8 = f4 * f6 * abUpMinUp.dot(upXemit);
            float af1[] = { abUpMinUp.x, abUpMinUp.y, abUpMinUp.z };
            float af2[] = { upXemit.x, upXemit.y, upXemit.z };
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matData[i][j] = (-f4 * af1[i] * af1[j] - f6 * af2[i]
                            * af2[j])
                            + f8 * af2[i] * af1[j];

                }
                matData[i][i]++;
            }

        } else {
            upVector.cross(worldEmit, upXemit);
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

    public ParticleController getParticleController() {
        return controller;
    }

    public void addController(Controller c) {
        super.addController(c);
        if (c instanceof ParticleController) {
            this.controller = (ParticleController) c;
        }
    }

    public Vector3f getEmissionDirection() {
        return emissionDirection;
    }

    public void setEmissionDirection(Vector3f emissionDirection) {
        this.emissionDirection = emissionDirection;
    }

    public float getEndSize() {
        return endSize;
    }

    public void setEndSize(float size) {
        endSize = size >= 0.0f ? size : 0.0f;
    }

    public float getStartSize() {
        return startSize;
    }

    public void setStartSize(float size) {
        startSize = size >= 0.0f ? size : 0.0f;
    }

    /**
     * Set the start color for particles. This is the base color of the quad.
     * 
     * @param color
     *            The start color.
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
     * Set the end color for particles. The base color of the quad will linearly
     * approach this color from the start color over the lifetime of the
     * particle.
     * 
     * @param color
     *            ColorRGBA The ending color.
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
     * Set the spinSpeed of new particles managed by this manager. Setting it to
     * 0 means no spin.
     * 
     * @param speed
     *            float
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

    public Vector3f getInvScale() {
        return invScale;
    }

    public void setInvScale(Vector3f invScale) {
        this.invScale = invScale;
    }

    public void updateInvScale() {
        invScale.set(worldScale);
        invScale.set(1f / invScale.x, 1f / invScale.y, 1f / invScale.z);
    }

    /**
     * Add an external influence to the particle controller for this mesh.
     * 
     * @param influence
     *            ParticleInfluence
     */
    public void addInfluence(ParticleInfluence influence) {
        controller.addInfluence(influence);
    }

    /**
     * Remove an influence from the particle controller for this mesh.
     * 
     * @param influence
     *            ParticleInfluence
     * @return true if found and removed.
     */
    public boolean removeInfluence(ParticleInfluence influence) {
        return controller.removeInfluence(influence);
    }
    
    /**
     * Returns the list of influences acting on this particle controller.
     *
     * @return ArrayList
     */
    public ArrayList<ParticleInfluence> getInfluences() {
        return controller.getInfluences();
    }
    
    public void clearInfluences() {
        controller.clearInfluences();
    }
    
    public void setParticleMass(float mass) {
        float invMass = 0;
        if (mass == 0)
            invMass = Float.POSITIVE_INFINITY;
        else if (mass == Float.POSITIVE_INFINITY)
            invMass = 0;
        else if (mass == Float.POSITIVE_INFINITY)
            invMass = -0;
        else invMass = 1f / mass;

        for (int x = particles.length; --x >= 0; )
            particles[x].setMasses(mass, invMass);
    }
    
    /**
     * Set the minimum angle (in radians) that particles can be emitted away
     * from the emission direction. Any angle less than 0 is trimmed to 0.
     * 
     * @param f
     *            The new emission minimum angle.
     */
    public void setMinimumAngle(float f) {
        minimumAngle = f >= 0.0f ? f : 0.0f;
    }

    /**
     * getEmissionMinimumAngle returns the minimum emission angle.
     * 
     * @return The minimum emission angle.
     */
    public float getMinimumAngle() {
        return minimumAngle;
    }

    /**
     * Set the maximum angle (in radians) that particles can be emitted away
     * from the emission direction. Any angle less than 0 is trimmed to 0.
     * 
     * @param f
     *            The new emission maximum angle.
     */
    public void setMaximumAngle(float f) {
        maximumAngle = f >= 0.0f ? f : 0.0f;
    }

    /**
     * getEmissionMaximumAngle returns the maximum emission angle.
     * 
     * @return The maximum emission angle.
     */
    public float getMaximumAngle() {
        return maximumAngle;
    }

    /**
     * Set the minimum lifespan of new particles (or recreated) managed by this
     * manager. if a value less than zero is given, 1.0f is used.
     * 
     * @param lifeSpan
     *            in ms
     */
    public void setMinimumLifeTime(float lifeSpan) {
        minimumLifeTime = lifeSpan >= 0.0f ? lifeSpan : 1.0f;
    }

    /**
     * getParticlesMinimumLifeTime returns the minimum life time of a particle.
     * 
     * @return The current minimum life time in ms.
     */
    public float getMinimumLifeTime() {
        return minimumLifeTime;
    }

    /**
     * Set the maximum lifespan of new particles (or recreated) managed by this
     * manager. if a value less than zero is given, 1.0f is used.
     * 
     * @param lifeSpan
     *            in ms
     */
    public void setMaximumLifeTime(float lifeSpan) {
        maximumLifeTime = lifeSpan >= 0.0f ? lifeSpan : 1.0f;
    }

    /**
     * getParticlesMaximumLifeTime returns the maximum life time of a particle.
     * 
     * @return The current maximum life time in ms.
     */
    public float getMaximumLifeTime() {
        return maximumLifeTime;
    }

    public Matrix3f getRotMatrix() {
        return rotMatrix;
    }

    public void setRotMatrix(Matrix3f rotMatrix) {
        this.rotMatrix = rotMatrix;
    }

    public TransformMatrix getEmitterTransform() {
        return emitterTransform;
    }
    
    public void setEmitterTransform(TransformMatrix emitterTransform) {
        this.emitterTransform = emitterTransform;
    }
    
    public float getParticleOrientation() {
        return particleOrientation;
    }

    public void setParticleOrientation(float orient) {
        this.particleOrientation = orient;
    }

    /**
     * Set the acceleration for any new particles created (or recreated) by this
     * manager.
     * 
     * @param velocity
     *            particle v0
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
     * Set the offset for any new particles created (or recreated) by this
     * manager. This is applicable only to managers generating from a point (not
     * a line, rectangle, etc..)
     * 
     * @param offset
     *            new offset position
     */
    public void setOriginOffset(Vector3f offset) {
        originOffset.set(offset);
    }

    /**
     * Get the offset point set in this manager.
     * 
     * @return origin
     */
    public Vector3f getOriginOffset() {
        return originOffset;
    }

    public Vector3f getWorldEmit() {
        return worldEmit;
    }

    public void setWorldEmit(Vector3f worldEmit) {
        this.worldEmit = worldEmit;
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
     * @param particlesPerSecond
     *            number of particles per second
     */
    public void setReleaseRate(int particlesPerSecond) {
        int oldRate = releaseRate;
        this.releaseRate = particlesPerSecond;
        if (controller != null && !controller.isActive() && controller.isControlFlow() && oldRate == 0) {
            controller.setActive(true);
        }
    }

    /**
     * Get which emittype method is being used by the underlying system.
     * One of ET_POINT, ET_LINE, ET_RECTANGLE, ET_GEOMBATCH, ET_RING
     * 
     * @return An int representing the current geometry method being used.
     */
    public int getEmitType() {
        return emitType;
    }

    /**
     * Set which emittype method is being used by the underlying system. This is
     * already done by setGeometry(Line) and setGeometry(Rectangle) You should
     * not need to use this method unless you are switching between geometry
     * already set by those methods.
     * 
     * @param type
     *            emit type to use
     */
    public void setEmitType(int type) {
        emitType = type;
    }

    /**
     * Get which emittype method is being used by the underlying system.
     * One of PT_QUAD, PT_TRIANGLE, PT_POINT, PT_LINE, PT_GEOMBATCH
     * 
     * @return An int representing the type of particle we are emitting.
     */
    public int getParticleType() {
        return particleType;
    }

    /**
     * Set what type of particle to emit from this sytem. Does not have an
     * effect unless recreate is called.
     * 
     * @param type
     *            particle type to use, should be one of PT_QUAD, PT_TRIANGLE,
     *            PT_POINT, PT_LINE, PT_GEOMBATCH
     */
    public void setParticleType(int type) {
        particleType = type;
    }

    /**
     * Set a line segment to be used as the "emittor".
     * 
     * @param line
     *            New emittor line segment.
     */
    public void setGeometry(Line line) {
        psLine = line;
        emitType = ET_LINE;
    }

    /**
     * Set a rectangular patch to be used as the "emittor".
     * 
     * @param rect
     *            New rectangular patch.
     */
    public void setGeometry(Rectangle rect) {
        psRect = rect;
        emitType = ET_RECTANGLE;
    }

    /**
     * Set a ring or disk to be used as the "emittor".
     *
     * @param ring
     *             The new ring area.
     */
    public void setGeometry(Ring ring) {
        psRing = ring;
        emitType = ET_RING;
    }
    
    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param batch
     *            The new geometry random verts.
     */
    public void setGeomBatch(GeomBatch batch) {
        psBatch = batch;
        emitType = ET_GEOMBATCH;
    }
    
    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param batch
     *            The new geometry random verts.
     */
    public void setGeometry(Geometry batch) {
        psBatch = batch.getBatch(0);
        emitType = ET_GEOMBATCH;
    }
    
    /**
     * Set a GeomBatch's verts to be the random emission points
     * 
     * @param batch
     *            The new geometry random verts.
     */
    public void setGeometry(Geometry batch, int batchIndex) {
        psBatch = batch.getBatch(batchIndex);
        emitType = ET_GEOMBATCH;
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
     * getRing returns the currently set ring emission area.
     *
     * @return Current ring.
     */
    public Ring getRing() {
        return psRing;
    }
    
    /**
     * getGeomBatch returns the currently set GeomBatch.
     * 
     * @return Current GeomBatch.
     */
    public GeomBatch getGeomBatch() {
        return psBatch;
    }

    public void initAllParticlesLocation() {
        for (int i = particles.length; --i >= 0; ) {
            initParticleLocation(i);
            particles[i].updateVerts(null);
        }
    }
    
    public void initParticleLocation(int index) {
        Particle p = particles[index];
        if (particleType == PT_GEOMBATCH) {
            // Update the triangle model on each new particle creation.
            Vector3f[] vertices = new Vector3f[3];
            ((TriangleBatch)psBatch).getTriangle(index, vertices);
            Triangle t = p.getTriangleModel();
            if (t == null)
                t = new Triangle(vertices[0],vertices[1],vertices[2]);
            else 
                for (int x = 0; x < 3; x++)
                    t.set(x, vertices[x]);
            t.calculateCenter();
            t.calculateNormal();
            // turn the triangle corners into vector offsets from center
            for (int x = 0; x < 3; x++) {
                vertices[x].subtract(t.getCenter(), vertices[x]);
                t.set(x, vertices[x]);
            }
            p.setTriangleModel(t);
            psBatch.getParentGeom().localToWorld(t.getCenter(), p.getPosition());
            p.getPosition().multLocal(getInvScale());
                
        } else if (getEmitType() == ET_GEOMBATCH) {
            if (getGeomBatch() != null && getGeomBatch() instanceof TriangleBatch)
                ((TriangleBatch)getGeomBatch()).randomPointOnTriangles(p.getPosition(), workVect3);
            else if (getGeomBatch() != null)
                getGeomBatch().randomVertex(p.getPosition());
            p.getPosition().multLocal(getInvScale());
                    
        } else {
            switch (getEmitType()) {
                case ET_LINE:
                    p.getPosition().set(getLine().random());
                    break;
                case ET_RECTANGLE:
                    p.getPosition().set(getRectangle().random());
                    break;
                case ET_RING:
                    p.getPosition().set(getRing().random());
                    break;
                case ET_POINT:
                default:
                    p.getPosition().set(originOffset);
                    break;
            }
            emitterTransform.multPoint(p.getPosition());
        }
    }

    public boolean isCameraFacing() {
        return cameraFacing;
    }

    public void setCameraFacing(boolean cameraFacing) {
        this.cameraFacing = cameraFacing;
    }
    
    public boolean isVelocityAligned() {
        return velocityAligned;
    }

    public void setVelocityAligned(boolean velocityAligned) {
        this.velocityAligned = velocityAligned;
    }
        
    public Particle getParticle(int i) {
        return particles[i];
    }

    public boolean isActive() {
        return controller.isActive();
    }

    public void setSpeed(float f) {
        controller.setSpeed(f);
    }

    public void setRepeatType(int type) {
        controller.setRepeatType(type);
    }

    public void setControlFlow(boolean b) {
        controller.setControlFlow(b);
    }

    public Vector3f getOriginCenter() {
        return originCenter;
    }

    public Vector3f getUpVector() {
        return upVector;
    }

    public void setUpVector(Vector3f upVector) {
        this.upVector = upVector;
    }

    public Vector3f getLeftVector() {
        return leftVector;
    }

    public void setLeftVector(Vector3f leftVector) {
        this.leftVector = leftVector;
    }

    public boolean isRotateWithScene() {
        return rotateWithScene;
    }

    public void setRotateWithScene(boolean rotate) {
        this.rotateWithScene = rotate;
    }

    public void resetParticleVelocity(int i) {
        getRandomVelocity(particles[i].getVelocity());
    }

    /**
     * Returns a random angle between the min and max angles.
     *
     * @return the random angle.
     */
    public float getRandomAngle() {
        return getMinimumAngle() +
            FastMath.nextRandomFloat() * (getMaximumAngle() - getMinimumAngle());
    }
    
    /**
     * generate a random lifespan between the min and max lifespan of the particle system.
     *
     * @return the generated lifespan value
     */
    public float getRandomLifeSpan() {
        return getMinimumLifeTime() + ((getMaximumLifeTime() - getMinimumLifeTime()) * FastMath.nextRandomFloat());
    }
    
    /**
     * Generate a random velocity within the parameters of max angle and
     * the rotation matrix.
     *
     * @param pSpeed a vector to store the results in.
     */
    protected Vector3f getRandomVelocity(Vector3f pSpeed) {
        float randDir = FastMath.TWO_PI * FastMath.nextRandomFloat();
        float randAngle = getRandomAngle();
        if (pSpeed == null)
            pSpeed = new Vector3f();
        pSpeed.x = FastMath.cos(randDir) * FastMath.sin(randAngle);
        pSpeed.y = FastMath.cos(randAngle);
        pSpeed.z = FastMath.sin(randDir) * FastMath.sin(randAngle);
        rotateVectorSpeed(pSpeed);
        pSpeed.multLocal(getInitialVelocity());
        return pSpeed;
    }
    
    /**
     * Apply the rotation matrix to a given vector representing a particle velocity.
     *
     * @param pSpeed the velocity vector to be modified.
     */
    protected void rotateVectorSpeed(Vector3f pSpeed) {
        
        float x = pSpeed.x, y = pSpeed.y, z = pSpeed.z;
        
        pSpeed.x = -1 * ( (rotMatrix.m00 * x) +
                (rotMatrix.m10 * y) +
                (rotMatrix.m20 * z));
        
        pSpeed.y = (getRotMatrix().m01 * x) +
                (rotMatrix.m11 * y) +
                (rotMatrix.m21 * z);
        
        pSpeed.z = -1 * ( (rotMatrix.m02 * x) +
                (rotMatrix.m12 * y) +
                (rotMatrix.m22 * z));
    }

    public void warmUp(int iterations) {
        if (controller != null) {
            controller.warmUp(iterations);
        }
    }

    public int getNumParticles() {
        return numParticles;
    }

    public void setNumParticles(int numParticles) {
        this.numParticles = numParticles;
    }

    public float getReleaseVariance() {
        if (controller != null) {
            return controller.getReleaseVariance();
        }
        return 0;
    }

    public void setReleaseVariance(float var) {
        if (controller != null) {
            controller.setReleaseVariance(var);
        }
    }

    /**
     * Changes the number of particles in this particle mesh.
     * 
     * @param count
     *            the desired number of particles to change to.
     */
    public void recreate(int count) {
        numParticles = count;
        initializeParticles(numParticles);
    }

    @Override
    public void updateWorldBound() {
        ; // ignore this since we want it to happen only when we say it can
            // happen due to world vectors not being used
    }
    public void updateWorldBoundManually() {
        super.updateWorldBound();
    }

    public void updateGeometricState(float time, boolean initiator) {
        super.updateGeometricState(time, initiator);
        if (isRotateWithScene()) {
            if (emitType == ET_GEOMBATCH && getGeomBatch() != null) {
                getGeomBatch().getParentGeom().getWorldRotation().mult(emissionDirection, worldEmit);
            } else {
                worldRotation.mult(emissionDirection, worldEmit);
            }
        } else worldEmit.set(emissionDirection);

        emitterTransform.set(worldRotation,
            worldTranslation.divideLocal(worldScale));
        
        originCenter.set(worldTranslation).addLocal(originOffset);

        getWorldTranslation().set(0,0,0);
        getWorldRotation().set(0, 0, 0, 1);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(emitType, "emitType", ET_POINT);
        capsule.write(particleType, "particleType", PT_QUAD);
        capsule.write(psLine, "psLine", null);
        capsule.write(psRect, "psRect", null);
        capsule.write(psRing, "psRing", null);
        capsule.write(psBatch, "psBatch", null);
        capsule.write(startSize, "startSize", DEFAULT_START_SIZE);
        capsule.write(endSize, "endSize", DEFAULT_END_SIZE);
        capsule.write(startColor, "startColor", DEFAULT_START_COLOR);
        capsule.write(endColor, "endColor", DEFAULT_END_COLOR);
        capsule.write(initialVelocity, "initialVelocity", 1);
        capsule.write(minimumLifeTime, "minimumLifeTime", DEFAULT_MIN_LIFE);
        capsule.write(maximumLifeTime, "maximumLifeTime", DEFAULT_MAX_LIFE);
        capsule.write(minimumAngle, "minimumAngle", 0);
        capsule.write(maximumAngle, "maximumAngle", DEFAULT_MAX_ANGLE);
        capsule.write(particleSpinSpeed, "particleSpinSpeed", 0);
        capsule.write(emissionDirection, "emissionDirection", Vector3f.UNIT_Y);
        capsule.write(worldEmit, "worldEmit", Vector3f.ZERO);
        capsule.write(upVector, "upVector", Vector3f.UNIT_Y);
        capsule.write(leftVector, "leftVector", new Vector3f(-1, 0, 0));
        capsule.write(numParticles, "numParticles", 0);
        capsule.write(particleOrientation, "particleOrientation", 0);
        capsule.write(rotateWithScene, "rotateWithScene", false);
        capsule.write(geometryCoordinates, "geometryCoordinates", null);
        capsule.write(appearanceColors, "appearanceColors", null);
        capsule.write(releaseRate, "releaseRate", numParticles);
        capsule.write(originCenter, "originCenter", Vector3f.ZERO);
        capsule.write(originOffset, "originOffset", Vector3f.ZERO);
        capsule.write(controller, "controller", null);
        capsule.write(cameraFacing, "cameraFacing", true);
        capsule.write(velocityAligned, "velocityAligned", false);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        emitType = capsule.readInt("emitType",ET_POINT);
        particleType = capsule.readInt("particleType", PT_QUAD);
        psLine = (Line)capsule.readSavable("psLine", null);
        psRect = (Rectangle)capsule.readSavable("psRect", null);
        psRing = (Ring)capsule.readSavable("psRing", null);
        psBatch = (GeomBatch)capsule.readSavable("psBatch", null);
        startSize = capsule.readFloat("startSize", DEFAULT_START_SIZE);
        endSize = capsule.readFloat("endSize", DEFAULT_END_SIZE);
        startColor = (ColorRGBA)capsule.readSavable("startColor", new ColorRGBA(DEFAULT_START_COLOR));
        endColor = (ColorRGBA)capsule.readSavable("endColor", new ColorRGBA(DEFAULT_END_COLOR));
        initialVelocity = capsule.readFloat("initialVelocity", 1);
        minimumLifeTime = capsule.readFloat("minimumLifeTime", DEFAULT_MIN_LIFE);
        maximumLifeTime = capsule.readFloat("maximumLifeTime", DEFAULT_MAX_LIFE);
        minimumAngle = capsule.readFloat("minimumAngle", 0);
        maximumAngle = capsule.readFloat("maximumAngle", DEFAULT_MAX_ANGLE);
        particleSpinSpeed = capsule.readFloat("particleSpinSpeed", 0);
        emissionDirection = (Vector3f)capsule.readSavable("emissionDirection", new Vector3f(Vector3f.UNIT_Y));
        worldEmit = (Vector3f)capsule.readSavable("worldEmit", new Vector3f(Vector3f.ZERO));
        upVector = (Vector3f)capsule.readSavable("upVector", new Vector3f(Vector3f.UNIT_Y));
        leftVector = (Vector3f)capsule.readSavable("leftVector", new Vector3f(-1, 0, 0));
        numParticles = capsule.readInt("numParticles", 0);
        rotateWithScene = capsule.readBoolean("rotateWithScene", false);
        geometryCoordinates = capsule.readFloatBuffer("geometryCoordinates", null);
        appearanceColors = capsule.readFloatBuffer("appearanceColors", null);
        
        releaseRate = capsule.readInt("releaseRate", numParticles);
        particleOrientation = capsule.readFloat("particleOrientation", 0);
        originCenter = (Vector3f)capsule.readSavable("originCenter", new Vector3f());
        originOffset = (Vector3f)capsule.readSavable("originOffset", new Vector3f());
        controller = (ParticleController)capsule.readSavable("controller", null);
        cameraFacing = capsule.readBoolean("cameraFacing", true);
        velocityAligned = capsule.readBoolean("velocityAligned", false);

        invScale = new Vector3f();
        upXemit = new Vector3f();
        absUpVector = new Vector3f();
        abUpMinUp = new Vector3f();
        rotMatrix = new Matrix3f();
        initializeParticles(numParticles);
    }

}
