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
package jmetest.intersection;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestPick.java,v 1.12 2004-04-02 15:52:14 mojomonkey Exp $
 */
public class TestPick extends SimpleGame {
    private Camera cam;
    private Text text;
    private Node root;
    private Node scene;
    private InputHandler input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;

    private Model model;

    private boolean drawBounds = false;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestPick app = new TestPick();
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
        timer.update();
        input.update(timer.getTimePerFrame());
        scene.updateGeometricState(timer.getTimePerFrame(), true);

        if (KeyBindingManager
                .getKeyBindingManager()
                .isValidCommand("tog_bounds")) {
            drawBounds = !drawBounds;
        }
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(root);
        if (drawBounds)
            display.getRenderer().drawBounds(root);

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
        Vector3f loc = new Vector3f(0.0f, 50.0f, 100.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonHandler(this, cam, properties.getRenderer());
        input.setKeySpeed(15f);
        input.setMouseSpeed(1);

        timer = Timer.getTimer(properties.getRenderer());

        rotQuat = new Quaternion();
        axis = new Vector3f(1,0,0);

        display.setTitle("Mouse Pick");

        KeyBindingManager.getKeyBindingManager().set(
                "tog_bounds",
                KeyInput.KEY_B);
    }

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        text = new Text("Test Label","Hits: 0 Shots: 0");
        Text cross = new Text("Crosshairs", "+");
        text.setLocalTranslation(new Vector3f(1,60,0));
        cross.setLocalTranslation(new Vector3f((float)(display.getWidth()/2f)-8f, (float)(display.getHeight()/2f)-8f,0));
        TextureState textImage = display.getRenderer().getTextureState();
        textImage.setEnabled(true);
        textImage.setTexture(
            TextureManager.loadTexture(
                TestPick.class.getClassLoader().getResource("jmetest/data/font/font.png"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        text.setRenderState(textImage);
        cross.setRenderState(textImage);
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        as1.setEnabled(true);
        text.setRenderState(as1);
        cross.setRenderState(as1);
        scene = new Node("3D scene node");
        root = new Node("Scene Root");
        root.setForceView(true);

        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);

        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        scene.setWorldBound(new BoundingBox());
        cam.update();

        root.attachChild(text);
        root.attachChild(cross);

        model = new MilkshapeASCIIModel("Milkshape Model");
        URL modelURL = TestPick.class.getClassLoader().getResource("jmetest/data/model/msascii/run.txt");
        model.load(modelURL, "jmetest/data/model/msascii/");
        model.getAnimationController().setActive(false);


        Vector3f[] vertex = new Vector3f[1000];
        ColorRGBA[] color = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex[i] = new Vector3f();
            vertex[i].x = (float) Math.random() * -100 - 50;
            vertex[i].y = (float) Math.random() * 50 - 25;
            vertex[i].z = (float) Math.random() * 50 - 25;
            color[i] = new ColorRGBA();
            color[i].r = (float) Math.random();
            color[i].g = (float) Math.random();
            color[i].b = (float) Math.random();
            color[i].a = 1.0f;
        }

        Line l = new Line("Line Group",vertex, null, color, null);
        l.setModelBound(new BoundingBox());
        l.updateModelBound();

        scene.attachChild(l);


        scene.attachChild(model);
        scene.updateGeometricState(0.0f, true);

        MousePick pick = new MousePick(cam, scene, text);
        pick.setMouse(input.getMouse());
        input.addAction(pick);

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
