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

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.LightNode;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jme.scene.Text;
import com.jme.util.TextureManager;
import com.jme.scene.state.AlphaState;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 */
public class TestCameraMan extends SimpleGame {
    private Model model;
    private Camera cam;
    private Node root, scene, monitorNode;
    private InputHandler input;
    private Timer timer;
    private CameraNode camNode;

    private TextureRenderer tRenderer;
    private Texture fakeTex;

    private Node fpsNode;
    private Text fps;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestCameraMan app = new TestCameraMan();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        timer.update();
        fps.print("FPS: " + (int) timer.getFrameRate() + " - " +
                  display.getRenderer().getStatistics());
        input.update(timer.getTimePerFrame());
        scene.updateGeometricState(0.0f, true);
        monitorNode.updateGeometricState(0.0f, true);
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearStatistics();
        //render to texture
        tRenderer.render(model, fakeTex);
        //display scene
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(root);
        display.getRenderer().draw(monitorNode);
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

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);

        // setup our camera
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 50.0f, 100.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);
        tRenderer = display.createTextureRenderer(256, 256, false, true, false, false, TextureRenderer.RENDER_TEXTURE_2D, 0);
        camNode = new CameraNode("Camera Node", tRenderer.getCamera());
        camNode.setLocalTranslation(new Vector3f(0,50,-50));
        camNode.updateGeometricState(0,true);
        // Setup the input controller and timer
        //input = new FirstPersonHandler(this, cam, "LWJGL");
        input = new NodeHandler(this, camNode, "LWJGL");
        input.setKeySpeed(10f);
        input.setMouseSpeed(1f);
        timer = Timer.getTimer("LWJGL");

        display.setTitle("Camera Man");
        display.getRenderer().enableStatistics(true);
    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {

        scene = new Node("3D Scene Node");
        root = new Node("Root Scene Node");

       DirectionalLight am = new DirectionalLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(1, 0, 0));


        LightState state = display.getRenderer().getLightState();
        state.setEnabled(true);
        state.attach(am);
        am.setEnabled(true);

        SpotLight sl = new SpotLight();
        sl.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        sl.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));
        sl.setDirection(new Vector3f(0, 0, 1));
        sl.setLocation(new Vector3f(0, 0, 0));
        sl.setAngle(25);

        LightState cameraLightState = display.getRenderer().getLightState();
        cameraLightState.setEnabled(true);

        sl.setEnabled(true);

        scene.setRenderState(state);

        model = new MilkshapeASCIIModel("Milkshape Model");
        URL modelURL = TestCameraMan.class.getClassLoader().getResource("jmetest/data/model/msascii/run.txt");
        model.load(modelURL, "jmetest/data/model/msascii/");
        model.getAnimationController().setActive(false);
        scene.attachChild(model);
        root.attachChild(scene);

        CullState cs = display.getRenderer().getCullState();
        cs.setCullMode(CullState.CS_BACK);
        cs.setEnabled(true);
        scene.setRenderState(cs);
        model.setRenderState(cs);

        LightNode cameraLight = new LightNode("Camera Light", cameraLightState);
        cameraLight.setLight(sl);
        cameraLight.setTarget(model);

        Model camBox = new MilkshapeASCIIModel("Camera Box");
        URL camBoxUrl = TestCameraMan.class.getClassLoader().getResource("jmetest/data/model/msascii/camera.txt");
        camBox.load(camBoxUrl, "jmetest/data/model/msascii/");
        camNode.attachChild(camBox);
        camNode.attachChild(cameraLight);


        monitorNode = new Node("Monitor Node");
        Quad quad = new Quad("Monitor");
        quad.initialize(3,3);
        quad.setLocalTranslation(new Vector3f(3.75f,52.5f,90));

        Quad quad2 = new Quad("Monitor");
        quad2.initialize(3.4f,3.4f);
        quad2.setLocalTranslation(new Vector3f(3.95f,52.6f,89.5f));

        // Setup our params for the depth buffer
        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        model.setRenderState(buf);
        scene.setRenderState(buf);
        monitorNode.setRenderState(buf);
        monitorNode.attachChild(quad);
        monitorNode.attachChild(quad2);
        scene.attachChild(camNode);


        // Ok, now lets create the Texture object that our monkey cube will be rendered to.

//        tRenderer.setBackgroundColor(new ColorRGBA(.667f, .667f, .851f, 1f));
        tRenderer.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        fakeTex = tRenderer.setupTexture();
        TextureState screen = display.getRenderer().getTextureState();
        screen.setTexture(fakeTex);
        screen.setEnabled(true);
        quad.setRenderState(screen);

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);

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
            TestImposterNode.class.getClassLoader().getResource(
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
        monitorNode.updateGeometricState(0.0f, true);
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
