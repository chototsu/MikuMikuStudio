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

import java.util.logging.Level;

import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.InputSystem;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>SimpleHeadlessApp</code> provides the simplest possible implementation
 * of a main game loop. Interpolation is used between frames for varying framerates.
 *
 * @author Joshua Slack, (javadoc by cep21)
 * @version $Id: SimpleHeadlessApp.java,v 1.3 2005-02-10 21:48:24 renanse Exp $
 */
public abstract class SimpleHeadlessApp extends BaseHeadlessApp {

  /** The camera that we see through. */
  protected Camera cam;
    /** The root of our normal scene graph. */
  protected Node rootNode;
    /** Handles our mouse/keyboard input. */
  protected InputHandler input;
    /** High resolution timer for jME. */
  protected Timer timer;
    /** The root node of our text. */
  protected Node fpsNode;
    /** Displays all the lovely information at the bottom. */
  protected Text fps;
    /** Simply an easy way to get at timer.getTimePerFrame().  Also saves time so you don't call it more than once per frame. */
  protected float tpf;

    /** A lightstate to turn on and off for the rootNode */
  protected LightState lightState;

    /** Location of the font for jME's text at the bottom */
  public static String fontLocation = "com/jme/app/defaultfont.tga";

  /** This is used to display print text. */
  protected StringBuffer updateBuffer=new StringBuffer(30);
  /** This is used to recieve getStatistics calls.*/
  protected StringBuffer tempBuffer=new StringBuffer();

  /**
   * This is called every frame in BaseGame.start()
   * @param interpolation unused in this implementation
   * @see AbstractGame#update(float interpolation)
   */
  protected final void update(float interpolation) {
      /** Recalculate the framerate. */
    timer.update();
      /** Update tpf to time per frame according to the Timer. */
    tpf = timer.getTimePerFrame();

      /** Call simpleUpdate in any derived classes of SimpleHeadlessApp. */
    simpleUpdate();

      /** Update controllers/render states/transforms/bounds for rootNode. */
    rootNode.updateGeometricState(tpf, true);

		/** Print the FPS info **/
		updateBuffer.setLength(0);
		updateBuffer.append("UPS: ").append((int)timer.getFrameRate()).append(" - ");
		updateBuffer.append(display.getRenderer().getStatistics(tempBuffer));

		fps.print(updateBuffer);

  }

  /**
   * This is called every frame in BaseGame.start(), after update()
   * @param interpolation unused in this implementation
   * @see AbstractGame#render(float interpolation)
   */
  protected final void render(float interpolation) {
      /** Reset display's tracking information for number of triangles/vertexes */
    display.getRenderer().clearStatistics();
      /** Clears the previously rendered information. */
    display.getRenderer().clearBuffers();
      /** Draw the rootNode and all its children. */
    display.getRenderer().draw(rootNode);
      /** Draw the fps node to show the fancy information at the bottom. */
    display.getRenderer().draw(fpsNode);
      /** Call simpleRender() in any derived classes. */
    simpleRender();
  }

  /**
   * Creates display, sets up camera, and binds keys.  Called in BaseGame.start() directly after
   * the dialog box.
   * @see AbstractGame#initSystem()
   */
  protected final void initSystem() {
    try {
        /** Get a DisplaySystem acording to the renderer selected in the startup box. */
      display = DisplaySystem.getDisplaySystem(properties.getRenderer());
       /** Create a window with the startup box's information. */
      display.createHeadlessWindow(
          properties.getWidth(),
          properties.getHeight(),
          properties.getDepth());
         /** Create a camera specific to the DisplaySystem that works with
          * the display's width and height*/
      cam =
          display.getRenderer().createCamera(
          display.getWidth(),
          display.getHeight());

    }
    catch (JmeException e) {
        /** If the displaysystem can't be initialized correctly, exit instantly. */
      e.printStackTrace();
      System.exit(1);
    }

      /** Set a black background.*/
    display.getRenderer().setBackgroundColor(ColorRGBA.black);

    /** Set up how our camera sees. */
    cam.setFrustumPerspective(45.0f,
                              (float) display.getWidth() /
                              (float) display.getHeight(), 1, 1000);
    Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
    Vector3f left = new Vector3f( -1.0f, 0.0f, 0.0f);
    Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
      /** Move our camera to a correct place and orientation. */
    cam.setFrame(loc, left, up, dir);
      /** Signal that we've changed our camera's location/frustum. */
    cam.update();
      /** Assign the camera to this renderer.*/
    display.getRenderer().setCamera(cam);

    /** Create a basic input controller. */
    input = new FirstPersonHandler(this, cam, properties.getRenderer());
      /** Signal to all key inputs they should work 10x faster. */
    input.setKeySpeed(10f);
    input.setMouseSpeed(1f);

      /** Get a high resolution timer for FPS updates. */
    timer = Timer.getTimer(properties.getRenderer());

      /** Sets the title of our display. */
      display.setTitle("SimpleHeadlessApp");
      /** Signal to the renderer that it should keep track of rendering information. */
    display.getRenderer().enableStatistics(true);
  }

  /**
   * Creates rootNode, lighting, statistic text, and other basic render states.
   * Called in BaseGame.start() after initSystem().
   * @see AbstractGame#initGame()
   */
  protected final void initGame() {
      /** Create rootNode */
    rootNode = new Node("rootNode");

    /** Create a ZBuffer to display pixels closest to the camera above farther ones.  */
    ZBufferState buf = display.getRenderer().createZBufferState();
    buf.setEnabled(true);
    buf.setFunction(ZBufferState.CF_LEQUAL);

    rootNode.setRenderState(buf);

    // -- FPS DISPLAY
    // First setup alpha state
      /** This allows correct blending of text and what is already rendered below it*/
    AlphaState as1 = display.getRenderer().createAlphaState();
    as1.setBlendEnabled(true);
    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
    as1.setDstFunction(AlphaState.DB_ONE);
    as1.setTestEnabled(true);
    as1.setTestFunction(AlphaState.TF_GREATER);
    as1.setEnabled(true);

    // Now setup font texture
    TextureState font = display.getRenderer().createTextureState();
      /** The texture is loaded from fontLocation */
    font.setTexture(
        TextureManager.loadTexture(
        SimpleHeadlessApp.class.getClassLoader().getResource(
        fontLocation),
        Texture.MM_LINEAR,
        Texture.FM_LINEAR));
    font.setEnabled(true);

    // Then our font Text object.
      /** This is what will actually have the text at the bottom. */
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
      /** Set up a basic, default light. */
    PointLight light = new PointLight();
    light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
    light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
    light.setLocation(new Vector3f(100, 100, 100));
    light.setEnabled(true);

      /** Attach the light to a lightState and the lightState to rootNode. */
    lightState = display.getRenderer().createLightState();
    lightState.setEnabled(true);
    lightState.attach(light);
    rootNode.setRenderState(lightState);

      /** Let derived classes initialize. */
    simpleInitGame();

      /** Update geometric and rendering information for both the rootNode and fpsNode. */
    rootNode.updateGeometricState(0.0f, true);
    rootNode.updateRenderState();
    fpsNode.updateGeometricState(0.0f, true);
    fpsNode.updateRenderState();
  }

  /**
   * Called near end of initGame(). Must be defined by derived classes.
   */
  protected abstract void simpleInitGame();

    /**
     * Can be defined in derived classes for custom updating.
     * Called every frame in update.
     */
  protected void simpleUpdate() {}

    /**
     * Can be defined in derived classes for custom rendering.
     * Called every frame in render.
     */
  protected void simpleRender() {}

  /**
   * unused
   * @see AbstractGame#reinit()
   */
  protected void reinit() {
  }

  /**
   * Cleans up the keyboard.
   * @see AbstractGame#cleanup()
   */
  protected void cleanup() {
    LoggingSystem.getLogger().log(Level.INFO, "Cleaning up resources.");

    if (InputSystem.getKeyInput() != null)
      InputSystem.getKeyInput().destroy();
    if (InputSystem.getMouseInput() != null)
      InputSystem.getMouseInput().destroy();
  }
}
