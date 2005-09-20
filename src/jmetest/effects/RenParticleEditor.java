/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

import java.awt.Canvas;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jme.app.VariableTimestepGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jmex.effects.ParticleManager;

/**
 * @author Joshua Slack
 * @version $Id: RenParticleEditor.java,v 1.15 2005-09-20 21:51:40 renanse Exp $
 */
public class RenParticleEditor extends VariableTimestepGame {

  public static ParticleManager manager;
  public static boolean quit = false;

  public static Node root;
  private Node main;

  private Camera cam;

  private Timer timer;
  private InputHandler input;

  private Text fps;

  private Node fpsNode;
  private static RenParticleControlFrame controlFrame;
  public static boolean noUpdate = false;

  private Canvas displayCanvas;
  public static File newTexture = null;

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    Thread glThread = new Thread() {
      public void run() {
        RenParticleEditor app = new RenParticleEditor();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
      }
    };
    glThread.setPriority(Thread.NORM_PRIORITY);
    glThread.start();
    controlFrame = new RenParticleControlFrame();
  }

  protected void update(float interpolation) {
    if (quit) finish();
    if (noUpdate) return;
    
    if (newTexture != null) {
        loadApplyTexture();
    }

    fps.print("FPS: " + (int) timer.getFrameRate() + " - " +
              display.getRenderer().getStatistics());
    main.updateGeometricState(interpolation, true);
  }

  private void loadApplyTexture() {
      TextureState ts = (TextureState)root.getRenderStateList()[RenderState.RS_TEXTURE];
      ts.setTexture(
              TextureManager.loadTexture(
                      newTexture.getAbsolutePath(),
                      Texture.MM_LINEAR,
                      Texture.FM_LINEAR));
      ts.setEnabled(true);
      root.setRenderState(ts);
      root.updateRenderState();
      newTexture = null;
  }

protected void render(float interpolation) {
    if (noUpdate) return;
    display.getRenderer().clearStatistics();
    display.getRenderer().clearBuffers();
    display.getRenderer().draw(main);
    display.getRenderer().draw(fpsNode);
  }

  protected void initSystem() {
    LoggingSystem.getLogger().setLevel(java.util.logging.Level.WARNING);
    controlFrame.setVisible(true);
    try {
      display = DisplaySystem.getDisplaySystem(properties.getRenderer());
      if (properties.getFullscreen())
        JOptionPane.showMessageDialog(null, "Sorry, this application does not run in full screen...  Using windowed mode.", "Sorry", JOptionPane.WARNING_MESSAGE);
      display.createWindow(
          properties.getWidth(),
          properties.getHeight(),
          properties.getDepth(),
          properties.getFreq(),
          false);

      cam =
          display.getRenderer().createCamera(
          properties.getWidth(),
          properties.getHeight());
    }
    catch (JmeException e) {
      e.printStackTrace();
      System.exit(1);
    }

    display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 1));

    cam.setFrustum(1f, 1000F, -0.55f, 0.55f, 0.4125f, -0.4125f);

    Vector3f loc = new Vector3f(0, 0, -850);
    Vector3f left = new Vector3f(1, 0, 0);
    Vector3f up = new Vector3f(0, 1, 0f);
    Vector3f dir = new Vector3f(0, 0, 1);
    cam.setFrame(loc, left, up, dir);

    display.getRenderer().setCamera(cam);

    timer = Timer.getTimer(properties.getRenderer());
    input = new FirstPersonHandler(this, cam, properties.getRenderer());
    input.setMouseSpeed(0.2f);
    input.setKeySpeed(10f);

    InputSystem.createInputSystem(properties.getRenderer());
    display.setTitle("Particle System");
    display.getRenderer().enableStatistics(true);
  }

  protected void initGame() {
    root = new Node("Scene graph root");
    root.setCullMode(Spatial.CULL_NEVER);
    main = new Node("Main node");
    main.setCullMode(Spatial.CULL_NEVER);

    AlphaState as1 = display.getRenderer().createAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);

    TextureState ts = display.getRenderer().createTextureState();
    ts.setTexture(
        TextureManager.loadTexture(
        RenParticleEditor.class.getClassLoader().getResource(
        "jmetest/data/texture/flaresmall.jpg"),
        Texture.MM_LINEAR_LINEAR,
        Texture.FM_LINEAR));
    ts.setEnabled(true);

    TextureState font = display.getRenderer().createTextureState();
    font.setTexture(
        TextureManager.loadTexture(
        RenParticleEditor.class.getClassLoader().getResource(
        "jmetest/data/font/font.png"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR));
    font.setEnabled(true);

    fps = new Text("FPS label", "");
    fps.setRenderState(font);
    fps.setRenderState(as1);
    fps.setCullMode(Spatial.CULL_NEVER);

    fpsNode = new Node("FPS node");
    fpsNode.attachChild(fps);
    fpsNode.setCullMode(Spatial.CULL_NEVER);

    manager = new ParticleManager(300);
    manager.setGravityForce(new Vector3f(0.0f, -0.0040f, 0.0f));
    manager.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
    manager.setEmissionMaximumAngle(0.2268928f);
    manager.setSpeed(1.0f);
    manager.setParticlesMinimumLifeTime(2000.0f);
    manager.setStartSize(10.0f);
    manager.setEndSize(10.0f);
    manager.setStartColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 1.0f));
    manager.setEndColor(new ColorRGBA(0.0f, 0.0625f, 1.0f, 0.0f));
    manager.setRandomMod(1.0f);
    manager.warmUp(120);

    root.setRenderState(ts);
    main.setRenderState(as1);
    manager.getParticles().addController(manager);
    root.attachChild(manager.getParticles());
    main.attachChild(root);
    root.updateGeometricState(0.0f, true);
    main.updateRenderState();
    fpsNode.updateGeometricState(0.0f, true);
    fpsNode.updateRenderState();
//    displayCanvas = new GUIFrame(input);
    controlFrame.updateFromManager();
  }

  protected void reinit() {
  }

  protected void cleanup() {
  }

}
