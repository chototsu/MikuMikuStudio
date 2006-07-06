package com.jmex.effects.particles;

import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;

public class WanderInfluence extends ParticleInfluence {

    private float wanderRadius = .03f;
    private float wanderDistance = .2f;
    private float wanderJitter = .005f;
    
    private ArrayList<Vector3f> wanderTargets = new ArrayList<Vector3f>(1);
    private Vector3f workVect = new Vector3f(); 
    
    @Override
    public void prepare(ParticleGeometry particleGeom) {
        if (wanderTargets.size() != particleGeom.getNumParticles()) {
            wanderTargets = new ArrayList<Vector3f>(particleGeom.getNumParticles());
            for (int x = particleGeom.getNumParticles(); --x >= 0; )
                wanderTargets.add(new Vector3f(particleGeom.getEmissionDirection()).normalizeLocal());
        }
    }
    
    @Override
    public void apply(float dt, Particle particle, int index) {
        if (wanderRadius == 0 && wanderDistance == 0 && wanderJitter == 0) return;
        
        Vector3f wanderTarget = wanderTargets.get(index);
        
        wanderTarget.addLocal(calcNewJitter(), calcNewJitter(), calcNewJitter());
        wanderTarget.normalizeLocal();
        wanderTarget.multLocal(wanderRadius);
        
        workVect.set(particle.getVelocity()).normalizeLocal().multLocal(wanderDistance);
        workVect.addLocal(wanderTarget).normalizeLocal();
        workVect.multLocal(particle.getVelocity().length());
        particle.getVelocity().set(workVect);
    }

    private float calcNewJitter() {
        return ((FastMath.nextRandomFloat()*2.0f)-1.0f) * wanderJitter;
    }

    public float getWanderDistance() {
        return wanderDistance;
    }

    public void setWanderDistance(float wanderDistance) {
        this.wanderDistance = wanderDistance;
    }

    public float getWanderJitter() {
        return wanderJitter;
    }

    public void setWanderJitter(float wanderJitter) {
        this.wanderJitter = wanderJitter;
    }

    public float getWanderRadius() {
        return wanderRadius;
    }

    public void setWanderRadius(float wanderRadius) {
        this.wanderRadius = wanderRadius;
    }
}
