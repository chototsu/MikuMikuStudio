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

import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.ImposterNode;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.scene.BoundingBox;
import com.jme.math.FastMath;
import com.jme.light.PointLight;
import com.jme.app.VariableTimestepGame;
import com.jme.scene.BillboardNode;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.animation.DeformationJointController;
import com.jme.scene.Controller;
import com.jme.scene.model.Model;
import java.net.URL;
import com.jme.scene.model.md2.Md2Model;

/**
 * <code>TestImposterNode</code>
 * @author Joshua Slack
 */
public class TestImposterNode extends VariableTimestepGame {
  private Camera cam;
  private Node root, scene;
  private Node fakeScene;
  private InputHandler input;
  private Timer timer;
  private Model model;

  private String FILE_NAME = "data/model/drfreak.md2";
  private String TEXTURE_NAME = "data/model/drfreak.jpg";

  ImposterNode iNode;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestImposterNode app = new TestImposterNode();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * Not used in this test.
   * @see com.jme.app.SimpleGame#update()
   */
  protected void update(float interpolation) {
    input.update(timer.getTimePerFrame());
    display.setTitle("Imposter Node - FPS:" + (int) timer.getFrameRate() +
                     " - " + display.getRenderer().getStatistics());
    scene.updateGeometricState(interpolation, true);
  }

  /**
   * clears the buffers and then draws the TriMesh.
   * @see com.jme.app.SimpleGame#render()
   */
  protected void render(float interpolation) {
    display.getRenderer().clearStatistics();
    display.getRenderer().clearBuffers();
    display.getRenderer().draw(root);
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

    ColorRGBA blackColor = new ColorRGBA(1, 1, 1, 1);
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

    display.setTitle("Render to Texture");
    display.getRenderer().enableStatistics(true);
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void initGame() {

    scene = new Node("3D Scene Node");
    root = new Node("Root Scene Node");
    root.attachChild(scene);

    fakeScene = new Node("Fake node");

    model = new Md2Model("Dr Freak");
    model.load(TestImposterNode.class.getClassLoader().getResource("jmetest/"+FILE_NAME));
    model.getAnimationController().setSpeed(10);
    model.getAnimationController().setRepeatType(Controller.RT_WRAP);
    fakeScene.attachChild(model);


    // Setup our params for the depth buffer
    ZBufferState buf = display.getRenderer().getZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.CF_LEQUAL);

    scene.setRenderState(buf);
    fakeScene.setRenderState(buf);

    TextureState ts = display.getRenderer().getTextureState();
    ts.setEnabled(true);
    ts.setTexture(
            TextureManager.loadTexture(
                TestImposterNode.class.getClassLoader().getResource("jmetest/"+TEXTURE_NAME),
                    Texture.MM_LINEAR,
                    Texture.FM_LINEAR,
                    true));
    fakeScene.setRenderState(ts);

    int tSize = 256;
    if (display.getHeight() > 512)
      tSize = 512;
    iNode = new ImposterNode("model imposter", 10, tSize, tSize);
    iNode.attachChild(fakeScene);
    iNode.setCameraDistance(100);
    iNode.setRedrawRate(.05f); // .05 = update texture 20 times a second on average
    BillboardNode bnode = new BillboardNode("imposter bbnode");
    bnode.setType(BillboardNode.SCREEN_ALIGNED);
    bnode.attachChild(iNode);
    scene.attachChild(bnode);

    cam.update();
    scene.updateGeometricState(0.0f, true);
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
