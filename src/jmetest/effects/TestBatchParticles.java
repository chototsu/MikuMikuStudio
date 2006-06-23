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

package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleInfluence;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

/**
 * This test shows off using an existing mesh as the actual geometry for your
 * particle system.
 * 
 * @author Joshua Slack
 * @version $Id: TestBatchParticles.java,v 1.1 2006-06-23 22:31:58 nca Exp $
 */
public class TestBatchParticles extends SimpleGame {

    private ParticleMesh pMesh;
    private ParticleInfluence wind;

    public static void main(String[] args) {
        TestBatchParticles app = new TestBatchParticles();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {

    }

    protected void simpleInitGame() {
        display.setTitle("Particle System - Batch Particles");
        
        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture tex = TextureManager.loadTexture(
            TestBatchParticles.class.getClassLoader().getResource(
            "jmetest/data/images/Monkey.jpg"),
            Texture.MM_LINEAR_LINEAR,
            Texture.FM_LINEAR);
        ts.setTexture(tex);

        Sphere sphere = new Sphere("sp", 16, 16, 2);
        sphere.setModelBound(new BoundingBox());
        sphere.updateModelBound();
        sphere.setSolidColor(ColorRGBA.white);
        sphere.setRenderState(ts);
        rootNode.attachChild(sphere);
        
        pMesh = ParticleFactory.buildBatchParticles("particles", sphere.getBatch(0));
        pMesh.setEmissionDirection(new Vector3f(1, 1, 1));
        pMesh.setOriginOffset(new Vector3f(0, 0, 0));
        pMesh.setInitialVelocity(.002f);
        pMesh.setStartSize(1);
        pMesh.setEndSize(1f);
        pMesh.setMinimumLifeTime(1000f);
        pMesh.setMaximumLifeTime(3000f);
        pMesh.setStartColor(new ColorRGBA(1, 1, 1, 1));
        pMesh.setEndColor(new ColorRGBA(1, 1, 1, 0));
        pMesh.setMaximumAngle(0f * FastMath.DEG_TO_RAD);
        pMesh.setRandomMod(0f);
        pMesh.setParticleSpinSpeed(180 * FastMath.DEG_TO_RAD);
        wind = SimpleParticleInfluenceFactory.createBasicWind(.008f, new Vector3f(-1, 0, 0), true);
        wind.setEnabled(true);
        pMesh.addInfluence(wind);
        pMesh.forceRespawn();

        AlphaState as1 = display.getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as1.setEnabled(true);
        rootNode.setRenderState(as1);
        
        pMesh.setRenderState(ts);

        pMesh.setModelBound(new BoundingBox());
        pMesh.updateModelBound();

        rootNode.attachChild(pMesh);
    }
}
