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
 * @version $Id: Particle.java,v 1.3 2006-06-14 03:42:16 renanse Exp $
 */
public class Particle implements Savable {

    /** Particle is dead -- not in play. */
    public static final int DEAD = 0;

    /** Particle is currently active. */
    public static final int ALIVE = 1;

    /** Particle is available for spawning. */
    public static final int AVAILABLE = 2;


    private int[] verts;
    private Vector3f position;
    private ColorRGBA startColor = new ColorRGBA(ColorRGBA.black);
    private ColorRGBA currColor = new ColorRGBA(ColorRGBA.black);;
    private int status = AVAILABLE;
    private float currentSize;
    private float lifeSpan;
    private float lifeRatio;
    private float spinAngle;
    private float mass = 1;
    private float invMass = 1;
    private int currentAge;
    private ParticleMesh parent;
    private Vector3f velocity;
    private Vector3f bbX = new Vector3f(), bbY = new Vector3f();

    // colors
    private float rChange, gChange, bChange, aChange;
    
    private static Vector3f tempVec = new Vector3f();

    
    
    public Particle() {}
    
    public Particle(ParticleMesh parent) {
        this.parent = parent;
        
    }
    
    public void init() {
        this.lifeSpan = getRandomLifeSpan();
        this.velocity = new Vector3f();
        getRandomSpeed(this.velocity);
        this.position = new Vector3f();

        startColor = (ColorRGBA) parent.getStartColor().clone();
        currColor = new ColorRGBA(startColor);
        currentAge = 0;
        status = AVAILABLE;
        currentSize = parent.getStartSize();
        verts = new int[4];
    }
    
    public void init(Vector3f speed, Vector3f iLocation,
            float lifeSpan) {
        this.lifeSpan = lifeSpan;
        this.velocity = (Vector3f) speed.clone();
        this.position = (Vector3f) iLocation.clone();
        
        startColor = (ColorRGBA) parent.getStartColor().clone();
        currColor = new ColorRGBA(startColor);
        currentAge = 0;
        status = AVAILABLE;
        currentSize = parent.getStartSize();
        verts = new int[4];
    }
    
    /**
     * Reset particle conditions. Besides the passed in speeds and lifespan, we
     * also reset color and size to their starting values (as given by parent.)
     * 
     * @param speed
     *            initial velocity of recreated particle
     * @param lifeSpan
     *            the recreated particle's new lifespan
     */
    public void recreateParticle(Vector3f speed, float lifeSpan) {
        this.lifeSpan = lifeSpan;
        this.velocity.set(speed);

        startColor.set(parent.getStartColor());
        currColor.set(startColor);
        rChange = startColor.r - parent.getEndColor().r;
        gChange = startColor.g - parent.getEndColor().g;
        bChange = startColor.b - parent.getEndColor().b;
        aChange = startColor.a - parent.getEndColor().a;
        for (int x = 0; x < 4; x++)
            BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), verts[x]);
        currentSize = parent.getStartSize();
        currentAge = 0;
        spinAngle = 0;
        status = AVAILABLE;
    }

    /**
     * Update the vertices for this particle, taking size, direction of viewer
     * and current location into account.
     */
    public void updateVerts(Camera cam) {

        if (spinAngle == 0) {
            bbX.set(cam.getLeft()).multLocal(currentSize);
            bbY.set(cam.getUp()).multLocal(currentSize);
        } else {
            float cA = FastMath.cos(spinAngle) * currentSize;
            float sA = FastMath.sin(spinAngle) * currentSize;
            bbX.set(cam.getLeft()).multLocal(cA).addLocal(cam.getUp().x * sA,
                    cam.getUp().y * sA, cam.getUp().z * sA);
            bbY.set(cam.getLeft()).multLocal(-sA).addLocal(cam.getUp().x * cA,
                    cam.getUp().y * cA, cam.getUp().z * cA);
        }

        position.add(bbX, tempVec).subtractLocal(bbY); // Q4
        BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), verts[1]);
        
        position.add(bbX, tempVec).addLocal(bbY); // Q1
        BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), verts[2]);

        position.subtract(bbX, tempVec).addLocal(bbY); // Q2
        BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), verts[3]);

        position.subtract(bbX, tempVec).subtractLocal(bbY); // Q3
        BufferUtils.setInBuffer(tempVec, parent.getVertexBuffer(0), verts[0]);
    }

    /**
     * update position (using current location, speed and gravity), color
     * (interpolating between start and end color), size (interpolating between
     * start and end size) and current age of particle.
     * 
     * After updating the above, <code>updateVerts()</code> is called.
     * 
     * if this particle's age is greater than its lifespan, it is considered
     * dead.
     * 
     * @param secondsPassed
     *            number of seconds passed since last update.
     * @return true if this particle is not ALIVE
     */
    public boolean updateAndCheck(float secondsPassed) {

        if (status != ALIVE) {
            return true;
        }
        currentAge += secondsPassed * 1000; // add time to age
        if (currentAge > lifeSpan) {
            status = DEAD;
            currColor.a = 0;
            for (int x = 0; x < 4; x++)
                BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), verts[x]);
            return true;
        }
        
        position.scaleAdd(secondsPassed * 1000f, velocity, position);
        spinAngle = spinAngle + parent.getParticleSpinSpeed() * secondsPassed
                * 100f;

        if (parent.getRandomMod() != 0.0f) {
            position.addLocal(parent.getRandomMod() * 2
                    * (FastMath.nextRandomFloat() - .5f), 0.0f, parent
                    .getRandomMod()
                    * 2 * (FastMath.nextRandomFloat() - .5f));
        }

        lifeRatio = currentAge / lifeSpan;

        // update the size, currently, the size
        // updates both the x and y values. So you always
        // get a square
        currentSize = parent.getStartSize();
        currentSize -= ((currentSize - parent.getEndSize()) * lifeRatio);

        // interpolate colors
        currColor.set(startColor);
        currColor.r -= rChange * lifeRatio;
        currColor.g -= gChange * lifeRatio;
        currColor.b -= bChange * lifeRatio;
        currColor.a -= aChange * lifeRatio;
        for (int x = 0; x < 4; x++)
            BufferUtils.setInBuffer(currColor, parent.getColorBuffer(0), verts[x]);

        return false;
    }

    /**
     * Resets current age to 0
     */
    public void resetAge() {
        currentAge = 0;
    }
    
    public int getCurrentAge() {
        return currentAge;
    }

    public Vector3f getPosition() {
        return position;
    }
    
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f speed) {
        this.velocity.set(speed);
    }

    public ColorRGBA getCurrentColor() {
        return currColor;
    }

    public void setCurrentColor(ColorRGBA color) {
        currColor.set(color);
    }

    public int getVertIndex(int corner) {
        return verts[corner];
    }
    
    public void setVertIndex(int corner, int ind) {
        this.verts[corner] = ind;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        if (mass == 0)
            invMass = Float.POSITIVE_INFINITY;
        else if (mass == Float.POSITIVE_INFINITY)
            invMass = 0;
        else if (mass == Float.POSITIVE_INFINITY)
            invMass = -0;
        else invMass = 1f / mass;
    }
    
    /**
     * for use in setting large quantities of particles to same mass/invmass
     * skipping recomp of invmass for each particle.
     */
    public void setMasses(float mass, float invMass) {
        this.mass = mass;
        this.invMass = invMass;
    }

    public float getInvMass() {
        return invMass;
    }

    /**
     * Returns a random angle between the min and max angles.
     *
     * @return the random angle.
     */
    public float getRandomAngle() {
        return parent.getMinimumAngle() +
            FastMath.nextRandomFloat() * (parent.getMaximumAngle() - parent.getMinimumAngle());
    }
    
    /**
     * generate a random lifespan between the min and max lifespan of the particle system.
     *
     * @return the generated lifespan value
     */
    public float getRandomLifeSpan() {
        return parent.getMinimumLifeTime() + ((parent.getMaximumLifeTime() - parent.getMinimumLifeTime()) * FastMath.nextRandomFloat());
    }
    
    /**
     * Generate a random velocity within the parameters of max angle and
     * the rotation matrix.
     *
     * @param pSpeed a vector to store the results in.
     */
    public void getRandomSpeed(Vector3f pSpeed) {
        float randDir = FastMath.TWO_PI * FastMath.nextRandomFloat();
        float randAngle = getRandomAngle();
        pSpeed.x = FastMath.cos(randDir) * FastMath.sin(randAngle);
        pSpeed.y = FastMath.cos(randAngle);
        pSpeed.z = FastMath.sin(randDir) * FastMath.sin(randAngle);
        rotateVectorSpeed(pSpeed);
        pSpeed.multLocal(parent.getInitialVelocity());
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
        pSpeed.x = -1 * ( (parent.getRotMatrix().m00 * x) +
                (parent.getRotMatrix().m10 * y) +
                (parent.getRotMatrix().m20 * z));
        
        pSpeed.y = (parent.getRotMatrix().m01 * x) +
        (parent.getRotMatrix().m11 * y) +
        (parent.getRotMatrix().m21 * z);
        
        pSpeed.z = -1 * ( (parent.getRotMatrix().m02 * x) +
                (parent.getRotMatrix().m12 * y) +
                (parent.getRotMatrix().m22 * z));
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(verts, "verts", new int[4]);
        capsule.write(position, "position", Vector3f.ZERO);
        capsule.write(startColor, "startColor", ColorRGBA.black);
        capsule.write(currColor, "currColor", ColorRGBA.black);
        capsule.write(status, "status", AVAILABLE);
        capsule.write(currentSize, "currentSize", 0);
        capsule.write(lifeSpan, "lifeSpan", 0);
        capsule.write(lifeRatio, "lifeRatio", 0);
        capsule.write(spinAngle, "spinAngle", 0);
        capsule.write(currentAge, "currentAge", 0);
        capsule.write(parent, "parent", null);
        capsule.write(velocity, "velocity", Vector3f.UNIT_XYZ);
        capsule.write(rChange, "rChange", 0);
        capsule.write(gChange, "gChange", 0);
        capsule.write(bChange, "bChange", 0);
        capsule.write(aChange, "aChange", 0);
        capsule.write(mass, "mass", 1);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        verts = capsule.readIntArray("verts", new int[4]);
        position = (Vector3f)capsule.readSavable("position", new Vector3f(Vector3f.ZERO));
        startColor = (ColorRGBA)capsule.readSavable("startColor", new ColorRGBA(ColorRGBA.black));
        currColor = (ColorRGBA)capsule.readSavable("currColor", new ColorRGBA(ColorRGBA.black));
        status = capsule.readInt("status", AVAILABLE);
        currentSize = capsule.readFloat("currentSize", 0);
        lifeSpan = capsule.readFloat("lifeSpan", 0);
        lifeRatio = capsule.readFloat("lifeRatio", 0);
        spinAngle = capsule.readFloat("spinAngle", 0);
        currentAge = capsule.readInt("currentAge", 0);
        parent = (ParticleMesh)capsule.readSavable("parent", null);
        velocity = (Vector3f)capsule.readSavable("velocity", new Vector3f(Vector3f.UNIT_XYZ));
        rChange = capsule.readFloat("rChange", 0);
        gChange = capsule.readFloat("gChange", 0);
        bChange = capsule.readFloat("bChange", 0);
        aChange = capsule.readFloat("aChange", 0);
        setMass(capsule.readFloat("mass", 1));
    }

    public Class getClassTag() {
        return this.getClass();
    }
}
