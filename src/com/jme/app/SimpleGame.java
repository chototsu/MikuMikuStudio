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

package com.jme.app;

import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>DemoGame</code> provides the simplest possible implementation
 * of a main game loop. Both logic and graphics are updated as quickly as
 * possible, with no interpolation to account for shifting frame rates.
 * It is suggested that a more complex variant of AbstractGame be used
 * in almost all cases.
 *
 * @author Joshua Slack
 * @version $Id: SimpleGame.java,v 1.19 2004-05-10 23:09:37 mojomonkey Exp $
 */
public abstract class SimpleGame extends BaseGame {

  protected Camera cam;
  protected Node rootNode;
  protected InputHandler input;
  protected Timer timer;
  protected Node fpsNode;
  protected Text fps;
  protected float tpf;
  protected boolean showBounds = false;

  protected WireframeState wireState;
  protected LightState lightState;

  public static String fontLocation = "jmetest/data/font/font.png";

  /**
   * @param interpolation unused in this implementation
   * @see AbstractGame#update(float interpolation)
   */
  protected final void update(float interpolation) {
    timer.update();
    tpf = timer.getTimePerFrame();
    input.update(tpf);
    fps.print("FPS: " + (int) timer.getFrameRate() + " - " +
              display.getRenderer().getStatistics());

    simpleUpdate();

    rootNode.updateGeometricState(tpf, true);

    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("toggle_wire", false)) {
      wireState.setEnabled(!wireState.isEnabled());
      rootNode.updateRenderState();
    }
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("toggle_lights", false)) {
      lightState.setEnabled(!lightState.isEnabled());
      rootNode.updateRenderState();
    }
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("toggle_bounds", false)) {
      showBounds = !showBounds;
    }
    if (KeyBindingManager
        .getKeyBindingManager()
        .isValidCommand("camera_out", false)) {
      System.err.println("Camera at: "+display.getRenderer().getCamera().getLocation());
    }

  }

  /**
   * @param interpolation unused in this implementation
   * @see AbstractGame#render(float interpolation)
   */
  protected final void render(float interpolation) {
    display.getRenderer().clearStatistics();
    display.getRenderer().clearBuffers();
    display.getRenderer().draw(rootNode);
    if (showBounds)
      display.getRenderer().drawBounds(rootNode);
    display.getRenderer().draw(fpsNode);
    simpleRender();
  }

  /**
   * @see AbstractGame#initSystem()
   */
  protected final void initSystem() {
    try {
      display = DisplaySystem.getDisplaySystem(properties.getRenderer());
      display.createWindow(
          properties.getWidth(),
          properties.getHeight(),
          properties.getDepth(),
          properties.getFreq(),
          properties.getFullscreen());
      cam =
          display.getRenderer().getCamera(
          properties.getWidth(),
          properties.getHeight());

    }
    catch (JmeException e) {
      e.printStackTrace();
      System.exit(1);
    }

    display.getRenderer().setBackgroundColor(ColorRGBA.black);

    // setup our camera
    cam.setFrustumPerspective(45.0f,(float)display.getWidth()/(float)display.getHeight(), 1,1000);
    Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
    Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f);
    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
    cam.setFrame(loc, left, up, dir);
    display.getRenderer().setCamera(cam);

    // Setup the input controller and timer
    input = new FirstPersonHandler(this, cam, properties.getRenderer());
    input.setKeySpeed(10f);
    input.setMouseSpeed(1f);
    timer = Timer.getTimer(properties.getRenderer());

    display.setTitle("SimpleGame");
    display.getRenderer().enableStatistics(true);

    KeyBindingManager.getKeyBindingManager().set(
        "toggle_wire",
        KeyInput.KEY_T);
    KeyBindingManager.getKeyBindingManager().set(
        "toggle_lights",
        KeyInput.KEY_L);
    KeyBindingManager.getKeyBindingManager().set(
        "toggle_bounds",
        KeyInput.KEY_B);
    KeyBindingManager.getKeyBindingManager().set(
        "camera_out",
        KeyInput.KEY_C);
  }

  /**
   * @see AbstractGame#initGame()
   */
  protected final void initGame() {
     rootNode = new Node("rootNode");

     // -- WIRESTATE
     wireState = display.getRenderer().getWireframeState();
     wireState.setEnabled(false);
     rootNode.setRenderState(wireState);

     // -- ZBUFFER
     ZBufferState buf = display.getRenderer().getZBufferState();
     buf.setEnabled(true);
     buf.setFunction(ZBufferState.CF_LEQUAL);

     rootNode.setRenderState(buf);

     // -- FPS DISPLAY
     // First setup alpha state
     AlphaState as1 = display.getRenderer().getAlphaState();
     as1.setBlendEnabled(true);
     as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
     as1.setDstFunction(AlphaState.DB_ONE);
     as1.setTestEnabled(true);
     as1.setTestFunction(AlphaState.TF_GREATER);
     as1.setEnabled(true);

     // Now setup font texture
     TextureState font = display.getRenderer().getTextureState();
     font.setTexture(
         TextureManager.loadTexture(
         SimpleGame.class.getClassLoader().getResource(
         fontLocation),
         Texture.MM_LINEAR,
         Texture.FM_LINEAR,
         true));
     font.setEnabled(true);

     // Then our font Text object.
     fps = new Text("FPS label", "");
     fps.setForceView(true);
     fps.setTextureCombineMode(TextureState.REPLACE);

     // Finally, a stand alone node (not attached to root on purpose)
     fpsNode = new Node("FPS node");
     fpsNode.attachChild(fps);
     fpsNode.setRenderState(font);
     fpsNode.setRenderState(as1);
     fpsNode.setForceView(true);

     // ---- LIGHTS
     PointLight light = new PointLight();
     light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
     light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
     light.setLocation(new Vector3f(100, 100, 100));
     light.setEnabled(true);

     lightState = display.getRenderer().getLightState();
     lightState.setEnabled(true);
     lightState.attach(light);
     rootNode.setRenderState(lightState);

     simpleInitGame();

     rootNode.updateGeometricState(0.0f, true);
     rootNode.updateRenderState();
     fpsNode.updateGeometricState(0.0f, true);
     fpsNode.updateRenderState();
  }

  protected abstract void simpleInitGame();

  protected void simpleUpdate() {}
  protected void simpleRender() {}

  /**
   * unused
   * @see AbstractGame#reinit()
   */
  protected void reinit() {
  }

  /**
   * unused -- recommend you kill the mouse and keyboard...
   * @see AbstractGame#cleanup()
   */
  protected void cleanup() {
  }
}
