/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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

import java.net.URL;

import com.jme.app.VariableTimestepGame;
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
import com.jme.scene.TriMesh;
import com.jme.scene.lod.ClodMesh;
import com.jme.scene.model.Model;
import com.jme.scene.model.ase.ASEModel;
//import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.shape.Disk;
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
 * <code>TestClodMesh</code> shows off the use of the ClodMesh in jME.
 *
 * keys:
 * +,-  Change level of detail
 * L    Toggle lights
 * T    Toggle Wireframe mode
 * M    Toggle Model or Disc
 *
 * @author Joshua Slack
 * @version $Id: TestClodMesh.java,v 1.10 2004-04-19 20:44:52 renanse Exp $
 */

public class TestClodMesh extends VariableTimestepGame {
  private Camera cam;
  private Node root, scene;
  private InputHandler input;
  private Timer timer;
  private Model model;

  private ClodMesh iNode, iNode2;
  private Node fpsNode;
  private Text fps;
  private long lastPress = 0;
  private WireframeState wireState;
  private LightState lightState;
  private boolean useModel = true;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestClodMesh app = new TestClodMesh();
    app.setDialogBehaviour(VariableTimestepGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * Not used in this test.
   * @see com.jme.app.SimpleGame#update()
   */
  protected void update(float interpolation) {
    input.update(timer.getTimePerFrame());
    fps.print("FPS: " + (int) timer.getFrameRate() + " - " +
              display.getRenderer().getStatistics());
    scene.updateGeometricState(interpolation, true);

    if (System.currentTimeMillis() - lastPress > 100) {
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("detail_down")) {
        if (useModel)
          iNode2.setTargetRecord( (iNode2.getTargetRecord()) + 10);
        else
          iNode.setTargetRecord( (iNode.getTargetRecord()) + 25);
        lastPress = System.currentTimeMillis();
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("detail_up")) {
        if (useModel)
          iNode2.setTargetRecord( (iNode2.getTargetRecord()) - 10);
        else
          iNode.setTargetRecord( (iNode.getTargetRecord()) - 25);
        lastPress = System.currentTimeMillis();
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("toggle_wire")) {
        wireState.setEnabled(!wireState.isEnabled());
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("toggle_lights")) {
        lightState.setEnabled(!lightState.isEnabled());
        root.updateRenderState();
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("switch_models")) {
        useModel = !useModel;
        iNode.setForceCull(useModel);
        iNode2.setForceCull(!useModel);
      }
    }
  }

  /**
   * clears the buffers and then draws the TriMesh.
   * @see com.jme.app.SimpleGame#render()
   */
  protected void render(float interpolation) {
    display.getRenderer().clearStatistics();
    display.getRenderer().clearBuffers();
    display.getRenderer().draw(root);
    display.getRenderer().draw(fpsNode);
  }

  /**
   * creates the displays and sets up the viewport.
   * @see com.jme.app.SimpleGame#initSystem()
   */
  protected void initSystem() {
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

    ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
    display.getRenderer().setBackgroundColor(blackColor);

    // setup our camera
    cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
    Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
    Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f);
    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
    cam.setFrame(loc, left, up, dir);
    display.getRenderer().setCamera(cam);

    // Setup the input controller and timer
    input = new FirstPersonHandler(this, cam, "LWJGL");
    input.setKeySpeed(10f);
    input.setMouseSpeed(1f);
    timer = Timer.getTimer("LWJGL");

    display.setTitle("Imposter Test");
    display.getRenderer().enableStatistics(true);

    KeyBindingManager.getKeyBindingManager().set(
        "detail_up",
        KeyInput.KEY_ADD);
    KeyBindingManager.getKeyBindingManager().set(
        "detail_down",
        KeyInput.KEY_SUBTRACT);
    KeyBindingManager.getKeyBindingManager().set(
        "toggle_wire",
        KeyInput.KEY_T);
    KeyBindingManager.getKeyBindingManager().set(
        "toggle_lights",
        KeyInput.KEY_L);
    KeyBindingManager.getKeyBindingManager().set(
        "switch_models",
        KeyInput.KEY_M);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void initGame() {

    scene = new Node("3D Scene Node");
    root = new Node("Root Scene Node");
    root.attachChild(scene);

    model = new ASEModel("Statue of Liberty");
    URL data = TestClodMesh.class.getClassLoader()
        .getResource("jmetest/data/model/Statue.ase");
    model.load(data, "jmetest/data/model/");

//    model = new Md2Model("Dr Freak");
//    model.load(TestClodMesh.class.getClassLoader()
//               .getResource("jmetest/data/model/drfreak.md2"));

    model.updateGeometricState(0, true);

    // Setup our params for the depth buffer
    ZBufferState buf = display.getRenderer().getZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.CF_LEQUAL);

    scene.setRenderState(buf);

    iNode = new ClodMesh("model", new Disk("disc", 50, 50, 8), null);
    iNode.setForceCull(true);

    TriMesh child = (TriMesh)model.getChild(0);
    iNode2 = new ClodMesh("model", child, null);
    iNode2.setForceCull(false);

    wireState = display.getRenderer().getWireframeState();
    wireState.setEnabled(false);
    scene.setRenderState(wireState);

    scene.attachChild(iNode);
    scene.attachChild(iNode2);

    //This code is all for the FPS display...
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
        TestClodMesh.class.getClassLoader().getResource(
        "jmetest/data/font/font.png"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    font.setEnabled(true);

    // Then our font Text object.
    fps = new Text("FPS label", "");
    fps.setRenderState(font);
    fps.setRenderState(as1);
    fps.setForceView(true);

    // Finally, a stand alone node (not attached to root on purpose)
    fpsNode = new Node("FPS node");
    fpsNode.attachChild(fps);
    fpsNode.setForceView(true);

    // add some light...

    PointLight am = new PointLight();
    am.setDiffuse(new ColorRGBA(1f, 1.0f, 1.0f, 1.0f));
    am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    am.setLocation(new Vector3f(15, 15, 15));
    am.setEnabled(true);

    lightState = display.getRenderer().getLightState();
    lightState.setEnabled(true);
    lightState.attach(am);
    scene.setRenderState(lightState);

    cam.update();
    scene.updateGeometricState(0.0f, true);
    fpsNode.updateGeometricState(0.0f, true);
    scene.updateRenderState();
    fpsNode.updateRenderState();
  }

  /**
   * not used.
   * @see com.jme.app.SimpleGame#reinit()
   */
  protected void reinit() {

  }

  /**
   * Not used.
   * @see com.jme.app.SimpleGame#cleanup()
   */
  protected void cleanup() {

  }

}
