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
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.scene.BoundingBox;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestBoxColor.java,v 1.6 2004-03-30 01:22:02 renanse Exp $
 */
public class TestBoxColor extends SimpleGame {
    private TriMesh t;
    private Camera cam;
    private Node root;
    private Node scene;
    private InputHandler input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestBoxColor app = new TestBoxColor();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();

    }

    public void addSpatial(Spatial spatial) {
        scene.attachChild(spatial);
        scene.updateGeometricState(0.0f, true);
        System.out.println(scene.getQuantity());
    }

    /**
     * Not used in this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        if(timer.getTimePerFrame() < 1) {
            angle = angle + (timer.getTimePerFrame() * 1);
            if(angle > 360) {
                angle = 0;
            }
        }

        rotQuat.fromAngleAxis(angle, axis);
        timer.update();
        input.update(timer.getTimePerFrame());

        t.setLocalRotation(rotQuat);
        scene.updateGeometricState(0.0f, true);


    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
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
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 75.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonHandler(this, cam, "LWJGL");
        input.setKeySpeed(15f);
        input.setMouseSpeed(1);
        timer = Timer.getTimer("LWJGL");

        rotQuat = new Quaternion();
        axis = new Vector3f(1,1,0.5f);
        display.setTitle("Vertex Colors");

    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        TextureState textImage = display.getRenderer().getTextureState();
        textImage.setEnabled(true);
        textImage.setTexture(
            TextureManager.loadTexture(
                TestBoxColor.class.getClassLoader().getResource("jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));

        scene = new Node("3D Scene Node");
        root = new Node("Root Scene Node");

        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);

        t = new Box("Box", min, max);
        t.setModelBound(new BoundingBox());
        t.updateModelBound();

        t.setLocalTranslation(new Vector3f(0,0,0));

        scene.attachChild(t);
        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        cam.update();

        ColorRGBA[] colors = new ColorRGBA[24];
        for(int i = 0; i < 24; i++) {
            colors[i] = new ColorRGBA((float)Math.random(),
                    (float)Math.random(),
                    (float)Math.random(),1);
        }
        t.setColors(colors);

        TextureState ts = display.getRenderer().getTextureState();
                ts.setEnabled(true);
                ts.setTexture(
                    TextureManager.loadTexture(
                        TestBoxColor.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                        Texture.MM_LINEAR,
                        Texture.FM_LINEAR,
                        true));

        scene.setRenderState(ts);

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
