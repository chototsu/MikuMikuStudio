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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 */
public class TestRenderToTexture extends SimpleGame {
    private Box realBox, monkeyBox;
    private Camera cam;
    private Node root, scene;
    private Node fakeScene;
    private InputHandler input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private Quaternion rotMBQuat;
    private float angle = 0;
    private float angle2 = 0;
    private Vector3f axis;

    TextureRenderer tRenderer;
    Texture fakeTex;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestRenderToTexture app = new TestRenderToTexture();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        if(timer.getTimePerFrame() < 1) {
            angle = angle + (timer.getTimePerFrame() * -.25f);
            angle2 = angle2 + (timer.getTimePerFrame() * 1);
            if(angle < 0) {
                angle = 360-.25f;
            }
            if(angle2 >= 360) {
                angle2 = 0;
            }
        }
        rotQuat.fromAngleAxis(angle, axis);
        rotMBQuat.fromAngleAxis(angle2, axis);
        timer.update();
        input.update(timer.getTimePerFrame());
        display.setTitle("Render to Texture - FPS:"+(int)timer.getFrameRate()+" - "+display.getRenderer().getStatistics());

        realBox.setLocalRotation(rotQuat);
        monkeyBox.setLocalRotation(rotMBQuat);
        scene.updateGeometricState(0.0f, true);
        fakeScene.updateGeometricState(0.0f, true);
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearStatistics();
        tRenderer.render(fakeScene, fakeTex);
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

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);

        // setup our camera
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        // Setup the input controller and timer
        input = new FirstPersonHandler(this, cam, "LWJGL");
        input.setKeySpeed(10f);
        input.setMouseSpeed(1f);
        timer = Timer.getTimer("LWJGL");

        rotQuat = new Quaternion();
        rotMBQuat = new Quaternion();
        axis = new Vector3f(1,1,0.5f);
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

        // Setup dimensions for a box
        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);

        // Make the real world box -- you'll see this spinning around..  woo...
        realBox = new Box("Box", min,max);
        realBox.setModelBound(new BoundingSphere());
        realBox.updateModelBound();
        realBox.setLocalTranslation(new Vector3f(0,0,0));

        scene.attachChild(realBox);
        root.attachChild(scene);

        // Make a monkey box -- some geometry that will be rendered onto a flat texture.
        // First, we'd like the box to be bigger, so...
        min.multLocal(3); max.multLocal(3);
        monkeyBox = new Box("Fake Monkey Box", min,max);
        monkeyBox.setModelBound(new BoundingSphere());
        monkeyBox.updateModelBound();
        monkeyBox.setLocalTranslation(new Vector3f(0,0,0));

        // add the monkey box to a node.  This node is a root node, not part of the "real world" tree.
        fakeScene = new Node("Fake node");
        fakeScene.attachChild(monkeyBox);

        // Setup our params for the depth buffer
        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        fakeScene.setRenderState(buf);

        // Add a directional light to the "real world" scene.
        DirectionalLight am = new DirectionalLight();
        am.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, -25));

        LightState state = display.getRenderer().getLightState();
        state.attach(am);
        state.setEnabled(true);
        am.setEnabled(true);
        scene.setRenderState(state);

        // Lets add a monkey texture to the geometry we are going to rendertotexture...
        TextureState ts = display.getRenderer().getTextureState();
            ts.setEnabled(true);
            ts.setTexture(
                TextureManager.loadTexture(
                    TestRenderToTexture.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                    Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR,
                    true));
        fakeScene.setRenderState(ts);

        // Ok, now lets create the Texture object that our monkey cube will be rendered to.
        tRenderer = display.createTextureRenderer(512, 512, false, true, false, false, TextureRenderer.RENDER_TEXTURE_2D, 0);
        tRenderer.setBackgroundColor(new ColorRGBA(.667f, .667f, .851f, 1f));
        fakeTex = tRenderer.setupTexture();
        tRenderer.getCamera().setLocation(new Vector3f(0,0,75f));
        tRenderer.getCamera().update();

        // Now add that texture to the "real" cube.
        ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(fakeTex, 0);

        // Heck, while we're at it, why not add another texture to blend with.
        ts.setTexture(
                TextureManager.loadTexture(
                    TestRenderToTexture.class.getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                    Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR,
                    true), 1);
        scene.setRenderState(ts);

        // Since we have 2 textures, the geometry needs to know how to split up the coords.
        Vector2f[] dirtCoords = realBox.getTextures(0);
        realBox.setTextures(dirtCoords, 1);

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
