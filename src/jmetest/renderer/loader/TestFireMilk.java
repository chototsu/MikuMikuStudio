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
package jmetest.renderer.loader;

import java.net.URL;

import com.jme.animation.DeformationJointController;
import com.jme.app.SimpleGame;
import com.jme.effects.ParticleManager;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Geometry;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.renderer.Renderer;

/**
 * <code>TestFireMilk</code>
 * @author Joshua Slack
 * @version
 */
public class TestFireMilk extends SimpleGame {
  private Model model;

  public static void main(String[] args) {
    TestFireMilk app = new TestFireMilk();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * set up the scene
   * @see com.jme.app.AbstractGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Joint Animation");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 200.0f));
    cam.update();
    input.setKeySpeed(100);

    URL modelURL = null;
    model = new MilkshapeASCIIModel("Milkshape Model");
    modelURL = TestFireMilk.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.txt");
    model.load(modelURL, "jmetest/data/model/msascii/");
    model.getAnimationController().setSpeed(25.0f);
    model.getAnimationController().setRepeatType(Controller.RT_CYCLE);
    ( (DeformationJointController) model.getAnimationController()).
        setUpdateModelBounds(true);
    model.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
    rootNode.attachChild(model);

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
        TestFireMilk.class.getClassLoader().getResource(
        "jmetest/data/texture/flaresmall.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts.setEnabled(true);

    ParticleManager manager = new ParticleManager(200, display.getRenderer().getCamera());
    manager.setGravityForce(new Vector3f(0.0f, 0.0f, 0.0f));
    manager.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
    manager.setEmissionMaximumAngle(0.20943952f);
    manager.setSpeed(1.0f);
    manager.setParticlesMinimumLifeTime(150.0f);
    manager.setStartSize(10.0f);
    manager.setEndSize(6.0f);
    manager.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
    manager.setEndColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 0.0f));
    manager.setRandomMod(4.5f);
    manager.setControlFlow(false);
    manager.setInitialVelocity(0.12f);
    manager.setGeometry((Geometry)model.getChild(0));

    manager.warmUp(60);
    manager.getParticles().addController(manager);
    manager.getParticles().setRenderState(ts);
    manager.getParticles().setRenderState(as1);
    manager.getParticles().setLightCombineMode(LightState.OFF);
    manager.getParticles().setRenderState(display.getRenderer().getZBufferState());
    rootNode.attachChild(manager.getParticles());
  }
}
