/*
 * Created on Jan 20, 2004
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

import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jme.app.VariableTimestepGame;
import com.jme.effects.ParticleManager;
import com.jme.image.Texture;
import com.jme.input.AbstractInputHandler;
import com.jme.input.InputSystem;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.input.mouse.WidgetMouseTestControllerFirstPerson;

/**
 * @author Joshua Slack
 * @version $Id: RenParticleEditor.java,v 1.1 2004-03-28 03:16:03 renanse Exp $
 */
public class RenParticleEditor extends VariableTimestepGame {

  public static ParticleManager manager;
  public static boolean quit = false;

  public static Node root;
  private Node main;

  private Camera cam;

  private Timer timer;
  private AbstractInputHandler input;

  private Text fps;

  private Node fpsNode;
  private static RenParticleControlFrame controlFrame;
  public static boolean noUpdate = false;

  private GUIFrame frame;

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

    frame.handleInput(interpolation*10f);

    fps.print("FPS: " + (int) timer.getFrameRate() + " - " +
              display.getRenderer().getStatistics());
    main.updateGeometricState(interpolation, true);
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
          display.getRenderer().getCamera(
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
    input = new WidgetMouseTestControllerFirstPerson(this, cam);
    input.setMouseSpeed(0.2f);
    input.setKeySpeed(1f);

    InputSystem.createInputSystem(properties.getRenderer());
    display.setTitle("Particle System");
    display.getRenderer().enableStatistics(true);
  }

  protected void initGame() {
    root = new Node("Scene graph root");
    root.setForceView(true);
    main = new Node("Main node");
    main.setForceView(true);

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
        RenParticleEditor.class.getClassLoader().getResource(
        "jmetest/data/texture/flaresmall.jpg"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    ts.setEnabled(true);

    TextureState font = display.getRenderer().getTextureState();
    font.setTexture(
        TextureManager.loadTexture(
        RenParticleEditor.class.getClassLoader().getResource(
        "jmetest/data/font/font.png"),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR,
        true));
    font.setEnabled(true);

    fps = new Text("FPS label", "");
    fps.setRenderState(font);
    fps.setRenderState(as1);
    fps.setForceView(true);

    fpsNode = new Node("FPS node");
    fpsNode.attachChild(fps);
    fpsNode.setForceView(true);

    manager = new ParticleManager(300, display.getRenderer().getCamera());
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
    manager.warmUp(1000);

    root.setRenderState(ts);
    main.setRenderState(as1);
    manager.getParticles().addController(manager);
    root.attachChild(manager.getParticles());
    main.attachChild(root);
    root.updateGeometricState(0.0f, true);
    fpsNode.updateGeometricState(0.0f, true);
    frame = new GUIFrame(input);
    controlFrame.updateFromManager();
  }

  protected void reinit() {
  }

  protected void cleanup() {
  }

  class GUIFrame extends WidgetAbstractFrame implements Observer {

    GUIFrame(AbstractInputHandler ic) {
      super(ic);
      doLayout();
      getMouseInput().setCursorVisible(true);
      input.setUpdateMouseActionsEnabled(false);
      input.setUpdateKeyboardActionsEnabled(false);
    }

    public void handleMouseButtonUp() {
        super.handleMouseButtonUp();

        if (getMouseInput().getPreviousButtonState() == WidgetMouseButtonType.MOUSE_BUTTON_2) {
            if (getMouseInput().isCursorVisible()) {
                getMouseInput().setCursorVisible(false);
                input.setUpdateMouseActionsEnabled(true);
                input.setUpdateKeyboardActionsEnabled(true);
            } else {
                getMouseInput().setCursorVisible(true);
                input.setUpdateMouseActionsEnabled(false);
                input.setUpdateKeyboardActionsEnabled(false);
            }
        }
    }

    public void handleMouseButtonDown() {

        if (isMouseCursorOn() && getMouseInput().getButtonState() != WidgetMouseButtonType.MOUSE_BUTTON_2) {
            super.handleMouseButtonDown();

        }
    }

    public boolean isMouseCursorOn() {
        return getMouseInput().isCursorVisible();
    }


  }
}
