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
import com.jme.math.Quaternion;
import com.jme.math.Triangle;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Particle</code> defines a single Particle of a Particle system.
 * Generally, you would not interact with this class directly.
 * 
 * @author Joshua Slack
 * @version $Id: Particle.java,v 1.5 2006-06-23 22:31:54 nca Exp $
 */
public class Particle implements Savable {

    /** Particle is dead -- not in play. */
    public static final int DEAD = 0;

    /** Particle is currently active. */
    public static final int ALIVE = 1;

    /** Particle is available for spawning. */
    public static final int AVAILABLE = 2;


    private int startIndex;
    private Vector3f position;
    private ColorRGBA startColor = new ColorRGBA(ColorRGBA.black);
    private ColorRGBA currColor = new ColorRGBA(ColorRGBA.black);;
    private int status = AVAILABLE;
    private float currentSize;
    private float lifeSpan;
    private float spinAngle;
    private float mass = 1;
    private float invMass = 1;
    private int currentAge;
    private ParticleGeometry parent;
    private Vector3f velocity;
    private Vector3f bbX = new Vector3f(), bbY = new Vector3f();

    // colors
    private float rChange, gChange, bChange, aChange;

    private int type = ParticleGeometry.PT_QUAD;

    private Triangle triModel;
    
    // static variable for use in calculations to eliminate object creation
    private static Vector3f tempVec = new Vector3f();
    private static Quaternion tempQuat = new Quaternion();

    /**
     * Empty constructor - mostly for use with Savable interface
     */
    public Particle() {}

    /**
     * Normal use constructor. Sets up the parent and particle type for this
     * particle.
     * 
     * @param parent
     *            the particle collection this particle belongs to
     */
    public Particle(ParticleGeometry parent) {
        this.parent = parent;
        this.type = parent.getParticleType();
    }

    /**
     * Cause this particle to reset it's lifespan, velocity, color, age and size
     * per the parent's settings. status is set to AVAILABLE and location is set
     * to 0,0,0. Actual geometry data is not affected by this call, only
     * particle params.
     */
    public void init() {
        init(parent.getRandomVelocity(null), new Vector3f(), parent.getRandomLifeSpan());
    }

    /**
     * Cause this particle to reset it's color, age and size per the parent's
     * settings. status is set to AVAILABLE. Location, velocity and lifespan are
     * set as given. Actual geometry data is not affected by this call, only
     * particle params.
     * 
     * @param velocity
     *            new initial particle velocity
     * @param position
     *            new initial particle position
     * @param lifeSpan
     *            new particle lifespan in ms
     */
    public void init(Vector3f velocity, Vector3f position,
            float lifeSpan) {
        this.lifeSpan = lifeSpan;
        this.velocity = (Vector3f) velocity.clone();
        this.position = (Vector3f) position.clone();

        startColor = (ColorRGBA) parent.getStartColor().clone();
        currColor = new ColorRGBA(startColor);
        currentAge = 0;
        status = AVAILABLE;
        currentSize = parent.getStartSize();
    }

    /**
     * Reset particle conditions. Besides the passed lifespan, we also reset
     * color, size, and spin angle to their starting values (as given by
     * parent.) Status is set to AVAILABLE.
     * 
     * @param lifeSpan
     *            the recreated particle's new lifespan
     */
    public void recreateParticle(float lifeSpan) {
        this.lifeSpan = lifeSpan;

        int verts = ParticleGeometry.getVertsForParticleType(type);
        startColor.set(parent.getStartColor());
        currColor.set(startColor);
        rChange = startColor.r - parent.getEndColor().r;
        gChange = startColor.g - parent.getEndColor().g;
        bChange = startColor.b - parent.getEndColor().b;
        aChange = startColor.a - parent.getEndColor().a;
        for (int x = 0; x < verts; x++)
            BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), startIndex+x);
        currentSize = parent.getStartSize();
        currentAge = 0;
        spinAngle = 0;
        status = AVAILABLE;
    }

    /**
     * Update the vertices for this particle, taking size, spin and viewer into
     * consideration. In the case of particle type PT_GEOMBATCH, the original
     * triangle normal is maintained rather than rotating it to face the camera
     * or parent vectors.
     * 
     * @param cam
     *            Camera to use in determining viewer aspect. If null, or if
     *            parent is not set to camera facing, parent's left and up
     *            vectors are used.
     */
    public void updateVerts(Camera cam) {
        float orient = parent.getParticleOrientation() + spinAngle;
        
        if (type == ParticleGeometry.PT_GEOMBATCH || type == ParticleGeometry.PT_POINT) {
            ; // nothing to do
        } else if (cam != null && parent.isCameraFacing()) {
            if (orient == 0) {
                bbX.set(cam.getLeft()).multLocal(currentSize);
                bbY.set(cam.getUp()).multLocal(currentSize);
            } else {
                float cA = FastMath.cos(orient) * currentSize;
                float sA = FastMath.sin(orient) * currentSize;
                bbX.set(cam.getLeft()).multLocal(cA).addLocal(cam.getUp().x * sA,
                        cam.getUp().y * sA, cam.getUp().z * sA);
                bbY.set(cam.getLeft()).multLocal(-sA).addLocal(cam.getUp().x * cA,
                        cam.getUp().y * cA, cam.getUp().z * cA);
            }
        } else {
            bbX.set(parent.getLeftVector()).multLocal(currentSize);
            bbY.set(parent.getUpVector()).multLocal(currentSize);
        }

        switch (type) {
            case ParticleGeometry.PT_QUAD: {
                position.add(bbX, tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex);
                
                position.add(bbX, tempVec).addLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+1);

                position.subtract(bbX, tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+2);

                position.subtract(bbX, tempVec).addLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+3);
                break;
            }
            case ParticleGeometry.PT_GEOMBATCH: {
                Vector3f norm = triModel.getNormal();
                if (orient != 0)
                    tempQuat.fromAngleNormalAxis(orient, norm);

                for (int x = 0; x < 3; x++) {
                    if (orient != 0)
                        tempQuat.mult(triModel.get(x), tempVec);
                    else
                        tempVec.set(triModel.get(x));
                    tempVec.multLocal(currentSize).addLocal(position);
                    BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex + x);
                }
                break;
            }
            case ParticleGeometry.PT_TRIANGLE: {
                position.add(bbX, tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex);
                
                position.add(bbX, tempVec).addLocal(3*bbY.x, 3*bbY.y, 3*bbY.z);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+1);

                position.subtract(bbX.multLocal(3), tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+2);
                break;
            }
            case ParticleGeometry.PT_LINE: {
                position.subtract(bbX, tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex);

                position.add(bbX, tempVec).subtractLocal(bbY);
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+1);
                break;
            }
            case ParticleGeometry.PT_POINT: {
                BufferUtils.setInBuffer(position, parent.getVertexBuffer(0), startIndex);
                break;
            }
        }
    }

    /**
     * <p>
     * update position (using current position and velocity), color
     * (interpolating between start and end color), size (interpolating between
     * start and end size), spin (using parent's spin speed) and current age of
     * particle. If this particle's age is greater than its lifespan, it is set
     * to status DEAD.
     * </p>
     * <p>
     * Note that this only changes the parameters of the Particle, not the
     * geometry the particle is associated with.
     * </p>
     * 
     * @param secondsPassed
     *            number of seconds passed since last update.
     * @return true if this particle is not ALIVE (in other words, if it is
     *         ready to be reused.)
     */
    public boolean updateAndCheck(float secondsPassed) {
        int verts = ParticleGeometry.getVertsForParticleType(type);
        if (status != ALIVE) {
            return true;
        }
        currentAge += secondsPassed * 1000; // add ms time to age
        if (currentAge > lifeSpan) {
            status = DEAD;
            currColor.a = 0;
            
            BufferUtils.populateFromBuffer(tempVec, parent.getVertexBuffer(0), startIndex);
            for (int x = 0; x < verts; x++) {
                BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), startIndex+x);
                BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), startIndex+x);
            }
            return true;
        }
        
        position.scaleAdd(secondsPassed * 1000f, velocity, position);
        spinAngle = spinAngle + parent.getParticleSpinSpeed() * secondsPassed;

        if (parent.getRandomMod() != 0.0f) {
            position.addLocal(parent.getRandomMod() * 2
                    * (FastMath.nextRandomFloat() - .5f), 0.0f, parent
                    .getRandomMod()
                    * 2 * (FastMath.nextRandomFloat() - .5f));
        }

        float lifeRatio = currentAge / lifeSpan;

        // update the size
        currentSize = parent.getStartSize();
        currentSize -= ((currentSize - parent.getEndSize()) * lifeRatio);
        
        // interpolate colors
        currColor.set(startColor);
        currColor.r -= rChange * lifeRatio;
        currColor.g -= gChange * lifeRatio;
        currColor.b -= bChange * lifeRatio;
        currColor.a -= aChange * lifeRatio;
        for (int x = 0; x < verts; x++)
            BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), startIndex+x);

        return false;
    }

    /**
     * Resets current age to 0
     */
    public void resetAge() {
        currentAge = 0;
    }
    
    /**
     * @return the current age of the particle in ms
     */
    public int getCurrentAge() {
        return currentAge;
    }

    /**
     * @return the current position of the particle in space
     */
    public Vector3f getPosition() {
        return position;
    }
    
    /**
     * Set the position of the particle in space.
     * 
     * @param position
     *            the new position in world coordinates
     */
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * @return the current status of this particle, one of DEAD, ALIVE,
     *         AVAILABLE
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the status of this particle.
     * 
     * @param status
     *            new status of this particle, one of DEAD, ALIVE, AVAILABLE
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the current velocity of this particle
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Set the current velocity of this particle
     * 
     * @param velocity
     *            the new velocity
     */
    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
    }

    /**
     * @return the current color applied to this particle
     */
    public ColorRGBA getCurrentColor() {
        return currColor;
    }

    /**
     * @return the start index of this particle in relation to where it exists
     *         in its parent's geometry data.
     */
    public int getStartIndex() {
        return startIndex;
    }
    
    /**
     * Set the starting index where this particle is represented in its parent's
     * geometry data
     * 
     * @param index
     */
    public void setStartIndex(int index) {
        this.startIndex = index;
    }

    /**
     * @return the mass of this particle. Only used by ParticleInfluences such
     *         as drag.
     */
    public float getMass() {
        return mass;
    }

    /**
     * Set the current mass of a particle - a value used only in particle
     * influences. This method also automatically calculates the inverse mass of
     * the particle. If the mass is 0, the inverse mass is considered to be
     * positive infinity. Conversely, if the mass is positive infinity, the
     * inverse is 0. The inverse of negative infinity is considered to be -0.
     * 
     * @param mass
     *            the new mass of the particle.
     */
    public void setMass(float mass) {
        this.mass = mass;
        if (mass == 0)
            invMass = Float.POSITIVE_INFINITY;
        else if (mass == Float.POSITIVE_INFINITY)
            invMass = 0;
        else if (mass == Float.NEGATIVE_INFINITY)
            invMass = -0;
        else invMass = 1f / mass;
    }
    
    /**
     * For use in setting large quantities of particles to same mass/invmass
     * skipping recomp of invmass for each particle.
     * 
     * @see #setMass(float)
     */
    public void setMasses(float mass, float invMass) {
        this.mass = mass;
        this.invMass = invMass;
    }

    /**
     * @return the inverse mass of this particle. Often useful for skipping
     *         constant division by mass calculations.
     */
    public float getInvMass() {
        return invMass;
    }

    /**
     * Sets a triangle model to use for particle calculations when using
     * particle type PT_GEOMBATCH. The particle will maintain the triangle's
     * ratio and plane of orientation. It will spin (if applicable) around the
     * triangle's normal axis. The triangle should already have its center and
     * normal fields calculated before calling this method.
     * 
     * @param t
     *            the triangle to model this particle after.
     */
    public void setTriangleModel(Triangle t) {
        this.triModel = t;
    }

    /**
     * @return the triangle model used by this particle
     * @see #setTriangleModel(Triangle)
     */
    public Triangle getTriangleModel() {
        return this.triModel;
    }

    
    ///////
    // Savable interface methods
    ///////
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(startIndex, "startIndex", 0);
        capsule.write(position, "position", Vector3f.ZERO);
        capsule.write(startColor, "startColor", ColorRGBA.black);
        capsule.write(currColor, "currColor", ColorRGBA.black);
        capsule.write(status, "status", AVAILABLE);
        capsule.write(currentSize, "currentSize", 0);
        capsule.write(lifeSpan, "lifeSpan", 0);
        capsule.write(currentAge, "currentAge", 0);
        capsule.write(parent, "parent", null);
        capsule.write(velocity, "velocity", Vector3f.UNIT_XYZ);
        capsule.write(rChange, "rChange", 0);
        capsule.write(gChange, "gChange", 0);
        capsule.write(bChange, "bChange", 0);
        capsule.write(aChange, "aChange", 0);
        capsule.write(mass, "mass", 1);
        capsule.write(type, "type", ParticleGeometry.PT_QUAD);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        startIndex = capsule.readInt("startIndex", 0);
        position = (Vector3f)capsule.readSavable("position", new Vector3f(Vector3f.ZERO));
        startColor = (ColorRGBA)capsule.readSavable("startColor", new ColorRGBA(ColorRGBA.black));
        currColor = (ColorRGBA)capsule.readSavable("currColor", new ColorRGBA(ColorRGBA.black));
        status = capsule.readInt("status", AVAILABLE);
        currentSize = capsule.readFloat("currentSize", 0);
        lifeSpan = capsule.readFloat("lifeSpan", 0);
        currentAge = capsule.readInt("currentAge", 0);
        parent = (ParticleGeometry)capsule.readSavable("parent", null);
        velocity = (Vector3f)capsule.readSavable("velocity", new Vector3f(Vector3f.UNIT_XYZ));
        rChange = capsule.readFloat("rChange", 0);
        gChange = capsule.readFloat("gChange", 0);
        bChange = capsule.readFloat("bChange", 0);
        aChange = capsule.readFloat("aChange", 0);
        setMass(capsule.readFloat("mass", 1));
        type = capsule.readInt("type", ParticleGeometry.PT_QUAD);
    }

    public Class getClassTag() {
        return this.getClass();
    }
}
