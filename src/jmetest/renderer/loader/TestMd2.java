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
package jmetest.renderer.loader;

import com.jme.animation.VertexKeyframeController;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.model.md2.Md2KeyframeSelector;
import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestBackwardAction</code>
 *
 * @author Mark Powell
 * @version $Id: TestMd2.java,v 1.16 2004-08-14 00:50:04 cep21 Exp $
 */
public class TestMd2 extends SimpleGame {
  TextureState ts = null;
  private Md2Model model;
  private String FILE_NAME = "data/model/drfreak.md2";
  private String TEXTURE_NAME = "data/model/drfreak.jpg";
  private Md2KeyframeSelector keyframeSelector;
  private int animCounter = 0;
  private float lastTime = 10;

  public static void main(String[] args) {
    TestMd2 app = new TestMd2();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleUpdate() {
    model.updateWorldData(timer.getTimePerFrame() * 10);
    lastTime += timer.getTimePerFrame();
    if (lastTime > 0.12) {
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("selectAnimation", false)) {
        animCounter++;
        if (animCounter >= keyframeSelector.getNumberOfAnimations()) {
          animCounter = 0;
        }
        keyframeSelector.setAnimation(animCounter);
      }
      lastTime = 0;
    }
  }

  /**
   * set up the scene
   *
   * @see com.jme.app.AbstractGame#initGame()
   */
  protected void simpleInitGame() {
    KeyBindingManager.getKeyBindingManager().set(
        "selectAnimation",
        KeyInput.KEY_F1);

    display.setTitle("MD2 Animation");
    cam.setLocation(new Vector3f(0, 0, 200));
    cam.update();

    input.setKeySpeed(50);

    PointLight am = new PointLight();
    am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
    am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am.setLocation(new Vector3f(25, 10, 10));

    PointLight am2 = new PointLight();
    am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
    am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am2.setLocation(new Vector3f( -25, 10, 0));

    DirectionalLight dr = new DirectionalLight();
    dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    dr.setAmbient(new ColorRGBA(0.25f, 0.25f, 0.25f, 1.0f));
    dr.setDirection(new Vector3f(0, 0, -150));

    lightState.detachAll();
    lightState.attach(am);
    lightState.attach(dr);
    lightState.attach(am2);
    am.setEnabled(true);
    am2.setEnabled(true);
    dr.setEnabled(true);

    model = new Md2Model("Dr Freak");
    model.load(TestMd2.class.getClassLoader().getResource("jmetest/" +
        FILE_NAME));
    rootNode.attachChild(model);

    ts = display.getRenderer().createTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestMd2.class.getClassLoader().getResource("jmetest/" + TEXTURE_NAME),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR,
        true));

    model.setRenderState(ts);

    model.getAnimationController().setRepeatType(Controller.RT_WRAP);

    keyframeSelector = new Md2KeyframeSelector( (VertexKeyframeController)
                                               model.getAnimationController());
  }
}
