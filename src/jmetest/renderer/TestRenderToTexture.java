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

import com.jme.app.*;
import com.jme.image.*;
import com.jme.input.*;
import com.jme.light.*;
import com.jme.math.*;
import com.jme.renderer.*;
import com.jme.scene.*;
import com.jme.scene.state.*;
import com.jme.system.*;
import com.jme.util.*;
import org.lwjgl.opengl.*;
import java.nio.*;

/**
 * <code>TestRenderToTexture</code>
 * @author Joshua Slack
 */
public class TestRenderToTexture extends SimpleGame {
    private TriMesh t, t2;
    private Camera cam;
    private Node root;
    private Node scene, fake;
    private InputController input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private Quaternion rotQuat2;
    private float angle = 0;
    private float angle2 = 0;
    private Vector3f axis;

    LWJGLTextureRenderer tRenderer;
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
        rotQuat2.fromAngleAxis(angle2, axis);
        timer.update();
        input.update(timer.getTimePerFrame());
        display.setTitle("Render to Texture - FPS:"+(int)timer.getFrameRate()+" - "+display.getRenderer().getStatistics());

        t.setLocalRotation(rotQuat);
        t2.setLocalRotation(rotQuat2);
        scene.updateGeometricState(0.0f, true);
        fake.updateGeometricState(0.0f, true);
    }

    /**
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearStatistics();
        tRenderer.render(fake, fakeTex);
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
        Vector3f loc = new Vector3f(0.0f, 0.0f, 25.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonController(this, cam, "LWJGL");
        input.setKeySpeed(10f);
        input.setMouseSpeed(1f);
        timer = Timer.getTimer("LWJGL");

        rotQuat = new Quaternion();
        rotQuat2 = new Quaternion();
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
        fake = new Node("Fake node");

        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);

        t = new Box("Box", min,max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();
        t.setLocalTranslation(new Vector3f(0,0,0));

        scene.attachChild(t);
        root.attachChild(scene);

        t2 = new Box("Inner Box", min,max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();
        t2.setLocalTranslation(new Vector3f(0,0,0));

        fake.attachChild(t2);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        fake.setRenderState(buf);

        TextureState ts = display.getRenderer().getTextureState();
            ts.setEnabled(true);
            ts.setTexture(
                TextureManager.loadTexture(
                    TestRenderToTexture.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                    Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR,
                    true));
        fake.setRenderState(ts);

        tRenderer = new LWJGLTextureRenderer((LWJGLRenderer)display.getRenderer());
        tRenderer.setBackgroundColor(new ColorRGBA(.667f, .667f, .851f, 1f));
        fakeTex = tRenderer.setupTexture();

        ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(fakeTex);
        scene.setRenderState(ts);

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
