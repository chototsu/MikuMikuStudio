package com.jmex.effects.particles;

import com.jme.scene.batch.TriangleBatch;

public class ParticleFactory {

    public static ParticleMesh buildParticles(String name, int number) {
        return buildParticles(name, number, ParticleGeometry.PT_QUAD);
    }

    public static ParticleMesh buildParticles(String name, int number, int particleType) {
        if (particleType != ParticleGeometry.PT_TRIANGLE && particleType != ParticleGeometry.PT_QUAD)
            throw new IllegalArgumentException("particleType should be either ParticleGeometry.PT_TRIANGLE or ParticleGeometry.PT_QUAD");
        ParticleMesh particleMesh = new ParticleMesh(name, number, particleType);
        ParticleController particleController = new ParticleController(particleMesh);
        particleMesh.addController(particleController);
        return particleMesh;
    }

    public static ParticleMesh buildBatchParticles(String name, TriangleBatch batch) {
        ParticleMesh particleMesh = new ParticleMesh(name, batch);
        ParticleController particleController = new ParticleController(particleMesh);
        particleMesh.addController(particleController);
        return particleMesh;
    }

    public static ParticlePoints buildPointParticles(String name, int number) {
        ParticlePoints p = new ParticlePoints(name, number);
        ParticleController particleController = new ParticleController(p);
        p.addController(particleController);
        return p;
    }

    public static ParticleLines buildLineParticles(String name, int number) {
        ParticleLines l = new ParticleLines(name, number);
        ParticleController particleController = new ParticleController(l);
        l.addController(particleController);
        return l;
    }
}
