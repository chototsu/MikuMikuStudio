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
package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.renderer.Renderer;
import com.jme.scene.state.MaterialState;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.LightState;
import com.jme.light.AmbientLight;
import com.jme.light.DirectionalLight;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;

/**
 * <code>TestRenderQueue</code>
 * @author Joshua Slack
 * @version $Id: TestRenderQueue.java,v 1.2 2004-06-17 16:45:18 renanse Exp $
 */
public class TestRenderQueue extends SimpleGame {
  private boolean useQueue = false;
  protected Node opaques, transps;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestRenderQueue app = new TestRenderQueue();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();

  }

  protected void simpleUpdate() {

    if (KeyBindingManager.getKeyBindingManager().isValidCommand("queue", false)) {
        if (useQueue) {
          display.setTitle("Test Render Queue - off - hit Q to toggle Queue mode");
          transps.setRenderQueueMode(Renderer.QUEUE_INHERIT);
          opaques.setRenderQueueMode(Renderer.QUEUE_INHERIT);
        } else {
          display.setTitle("Test Render Queue - on - hit Q to toggle Queue mode");
          transps.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
          opaques.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        }
        useQueue = !useQueue;
    }

  }

  protected void simpleInitGame() {
    display.setTitle("Test Render Queue - off - hit Q to toggle Queue mode");
    KeyBindingManager.getKeyBindingManager().set("queue", KeyInput.KEY_Q);
    cam.setLocation(new Vector3f(10, 0, 50));
    cam.update();

    Vector3f max = new Vector3f(5, 5, 5);
    Vector3f min = new Vector3f( -5, -5, -5);

    opaques = new Node("Opaques");
    transps = new Node("Transps");
    rootNode.attachChild(transps);
    rootNode.attachChild(opaques);

    Box b1 = new Box("Box", min, max);
    b1.setModelBound(new BoundingBox());
    b1.updateModelBound();
    b1.setLocalTranslation(new Vector3f(0, 0, -15));
    opaques.attachChild(b1);

    Box b2 = new Box("Box", min, max);
    b2.setModelBound(new BoundingBox());
    b2.updateModelBound();
    b2.setLocalTranslation(new Vector3f(0, 0, -30));
    opaques.attachChild(b2);

    Box b3 = new Box("Box", min, max);
    b3.setModelBound(new BoundingBox());
    b3.updateModelBound();
    b3.setLocalTranslation(new Vector3f(0, -15, -15));
    opaques.attachChild(b3);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestRenderQueue.class.getClassLoader().getResource(
        "jmetest/data/images/Monkey.tga"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    opaques.setRenderState(ts);

    LightState ls = display.getRenderer().getLightState();
    ls.setEnabled(true);
    DirectionalLight dLight = new DirectionalLight();
    dLight.setEnabled(true);
    dLight.setDiffuse(new ColorRGBA(1,1,1,1));
    dLight.setDirection(new Vector3f(-1,-1,-1));
    ls.attach(dLight);
    DirectionalLight dLight2 = new DirectionalLight();
    dLight2.setEnabled(true);
    dLight2.setDiffuse(new ColorRGBA(1,1,1,1));
    dLight2.setDirection(new Vector3f(1,1,1));
    ls.attach(dLight2);
    ls.setTwoSidedLighting(false);

    Box tb1 = new Box("TBox Blue", min, max);
    tb1.setModelBound(new BoundingBox());
    tb1.updateModelBound();
    tb1.setLocalTranslation(new Vector3f(0, 15, 15));
    transps.attachChild(tb1);
    MaterialState ms1 = display.getRenderer().getMaterialState();
    ms1.setEnabled(true);
    ms1.setDiffuse(new ColorRGBA(0,0,1,.75f));
    ms1.setShininess(128);
    tb1.setRenderState(ls);
    tb1.setRenderState(ms1);
    tb1.setLightCombineMode(LightState.REPLACE);

    Box tb2 = new Box("TBox Green", min, max);
    tb2.setModelBound(new BoundingBox());
    tb2.updateModelBound();
    tb2.setLocalTranslation(new Vector3f(0, 0, 30));
    transps.attachChild(tb2);
    MaterialState ms2 = display.getRenderer().getMaterialState();
    ms2.setEnabled(true);
    ms2.setDiffuse(new ColorRGBA(0,1,0,.75f));
    ms2.setShininess(128);
    tb2.setRenderState(ls);
    tb2.setRenderState(ms2);
    tb2.setLightCombineMode(LightState.REPLACE);

    Box tb3 = new Box("TBox Red", min, max);
    tb3.setModelBound(new BoundingBox());
    tb3.updateModelBound();
    tb3.setLocalTranslation(new Vector3f(0, 0, 15));
    transps.attachChild(tb3);
    MaterialState ms3 = display.getRenderer().getMaterialState();
    ms3.setEnabled(true);
    ms3.setDiffuse(new ColorRGBA(1,0,0,.75f));
    ms3.setShininess(128);
    tb3.setRenderState(ms3);
    tb3.setRenderState(ls);
    tb3.setLightCombineMode(LightState.REPLACE);

//    ZBufferState zs = display.getRenderer().getAlphaState();
//    zs.setEnabled(true);
//    zs.setWritable(false);
//    zs.setFunction(ZBufferState.CF_LESS);
//    transps.setRenderState(zs);
//
    AlphaState as = display.getRenderer().getAlphaState();
    as.setEnabled(true);
    as.setBlendEnabled(true);
    as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
    transps.setRenderState(as);
  }
}
