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

import com.jme.app.VariableTimestepGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BillboardNode;
import com.jme.scene.Controller;
import com.jme.scene.lod.*;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.model.Model;
import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.scene.TriMesh;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Disk;

/**
 * <code>TestClodMesh</code> shows off the use of the ClodMesh in jME.
 * @author Joshua Slack
 * @version $Id: TestClodMesh.java,v 1.1 2004-04-06 22:13:19 renanse Exp $
 */
public class TestClodMesh extends VariableTimestepGame {
  private Camera cam;
  private Node root, scene;
  private InputHandler input;
  private Timer timer;
  private Model model;

  private String FILE_NAME = "data/model/drfreak.md2";
  private String TEXTURE_NAME = "data/model/drfreak.jpg";

  private ClodMesh iNode;
  private Node fpsNode;
  private Text fps;
  private long lastPress = 0;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestClodMesh app = new TestClodMesh();
    app.setDialogBehaviour(VariableTimestepGame.FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
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
        iNode.setTargetRecord( (iNode.getAutomatedTargetRecord()) - 1);
        lastPress = System.currentTimeMillis();
      }
      if (KeyBindingManager
          .getKeyBindingManager()
          .isValidCommand("detail_up")) {
        iNode.setTargetRecord( (iNode.getAutomatedTargetRecord()) + 1);
        lastPress = System.currentTimeMillis();
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
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void initGame() {

    scene = new Node("3D Scene Node");
    root = new Node("Root Scene Node");
    root.attachChild(scene);

    // setup the scene to be 'impostered'
    model = new Md2Model("Dr. Freak");
    model.load(TestClodMesh.class.getClassLoader().getResource("jmetest/" +
        FILE_NAME));
    model.getAnimationController().setSpeed(10);
    model.getAnimationController().setRepeatType(Controller.RT_WRAP);

    // apply the appropriate texture to the imposter scene
    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    ts.setTexture(
        TextureManager.loadTexture(
        TestClodMesh.class.getClassLoader().getResource("jmetest/" +
        TEXTURE_NAME),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));

    // Setup our params for the depth buffer
    ZBufferState buf = display.getRenderer().getZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.CF_LEQUAL);

    scene.setRenderState(buf);

    // setup the imposter node...
    // we first determine a good texture size (must be equal to or less than the display size)
    TriMesh child = (TriMesh)model.getChild(0);
    iNode = new ClodMesh("model", new Disk("disk", 10, 10, 8), null);
//    iNode = new ClodMesh("model", child, null);
    iNode.setRenderState(ts);

    scene.attachChild(iNode);

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

    cam.update();
    scene.updateGeometricState(0.0f, true);
    fpsNode.updateGeometricState(0.0f, true);
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
