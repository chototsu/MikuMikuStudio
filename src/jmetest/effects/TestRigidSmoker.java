/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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
package jmetest.effects;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.effects.ParticleManager;
import com.jme.image.Texture;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;

/**
 * <code>TestRigidSmoker</code>
 * @author Joshua Slack
 */
public class TestRigidSmoker extends SimpleGame {
  private Node smokeNode;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestRigidSmoker app = new TestRigidSmoker();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
    cam.update();

    smokeNode = new Node("Smoker Node");
    smokeNode.setLocalTranslation(new Vector3f(0, 50, -50));
    smokeNode.updateGeometricState(0, true);

    // Setup the input controller and timer
    input = new NodeHandler(this, smokeNode, "LWJGL");
    input.setKeySpeed(10f);
    input.setMouseSpeed(1f);

    display.setTitle("Rigid sSmoke box");

    // hijack the camera model for our own purposes
    Model camBox = new MilkshapeASCIIModel("Camera Box");
    URL camBoxUrl = TestRigidSmoker.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/camera.txt");
    camBox.load(camBoxUrl, "jmetest/data/model/msascii/");
    camBox.setLocalScale(5f);
    smokeNode.attachChild(camBox);
    rootNode.attachChild(smokeNode);

    AlphaState as1 = display.getRenderer().getAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setTexture(
        TextureManager.loadTexture(
        TestRigidSmoker.class.getClassLoader().getResource(
        "jmetest/data/texture/flaresmall.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts.setEnabled(true);

    ParticleManager manager = new ParticleManager(300, display.getRenderer().getCamera());
    manager.setGravityForce(new Vector3f(0.0f, 0.0f, 0.0f));
    manager.setEmissionDirection(new Vector3f(0f, 0f, 1f));
    manager.setEmissionMaximumAngle(0.0f);
    manager.setSpeed(2.0f);
    manager.setParticlesMinimumLifeTime(75.0f);
    manager.setStartSize(1.6f);
    manager.setEndSize(8.0f);
    manager.setStartColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    manager.setEndColor(new ColorRGBA(0.6f, 0.2f, 0.0f, 0.0f));
    manager.setRandomMod(3.5f);
    manager.setInitialVelocity(0.57f);

    manager.warmUp(60);
    manager.getParticles().addController(manager);

    ZBufferState zbuf = display.getRenderer().getZBufferState();
    zbuf.setWritable(false);
    zbuf.setEnabled(true);
    zbuf.setFunction(ZBufferState.CF_LEQUAL);

    manager.getParticles().setLocalTranslation(new Vector3f(0,3.75f,11f)); // this places the emitter in the "tail pipe"
    manager.getParticles().setRenderState(ts);
    manager.getParticles().setRenderState(as1);
    manager.getParticles().setRenderState(zbuf);
    smokeNode.attachChild(manager.getParticles());
  }

}
