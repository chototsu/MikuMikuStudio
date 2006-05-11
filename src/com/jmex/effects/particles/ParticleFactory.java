package com.jmex.effects.particles;

public class ParticleFactory {

    public static ParticleMesh buildParticles(String name, int number) {
        ParticleMesh particleMesh = new ParticleMesh(name, number);
        ParticleController particleController = new ParticleController(particleMesh);
        particleMesh.addController(particleController);
        return particleMesh;
    }
}
