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
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.system.JmeException;
import com.jme.system.lwjgl.LWJGLDisplaySystem;

/**
 * Test of the point renderer for the LWJGL renderer.
 * @author Mark Powell
 * @version $Id: TestLWJGLRendererPoint.java,v 1.5 2004-04-02 23:29:20 mojomonkey Exp $
 */
public class TestLWJGLRendererPoint extends SimpleGame {

    private Point p;
    private Camera cam;
    private Node scene;

    public static void main(String[] args) {
        TestLWJGLRendererPoint app = new TestLWJGLRendererPoint();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    /**
     * not used.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {

    }

    /**
     * renders the scene graph
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);

    }

    /**
     * sets up the display and camera
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        try {
            if("LWJGL".equalsIgnoreCase(properties.getRenderer())) {
                display = new LWJGLDisplaySystem();
                display.createWindow(properties.getWidth(), properties.getHeight(),
                                properties.getDepth(), properties.getFreq(),
                                properties.getFullscreen());
                cam = display.getRenderer().getCamera(properties.getWidth(),properties.getHeight());
            }
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
        cam.setFrustum(1.0f,1000.0f,-0.55f,0.55f,0.4125f,-0.4125f);
        Vector3f loc = new Vector3f(4.0f,0.0f,0.0f);
        Vector3f left = new Vector3f(0.0f,-1.0f,0.0f);
        Vector3f up = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f dir = new Vector3f(-1.0f,0f,0.0f);
        cam.setFrame(loc,left,up,dir);
        display.getRenderer().setCamera(cam);


    }

    /**
     * creates scene
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        Vector3f[] vertex = new Vector3f[1000];
        ColorRGBA[] color = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex[i] = new Vector3f();
            vertex[i].x = (float) Math.random()* -100 - 50;
            vertex[i].y = (float) Math.random() * 50 - 25;
            vertex[i].z = (float) Math.random() * 50 - 25;

            color[i] = new ColorRGBA();
            color[i].r = (float) Math.random();
            color[i].g = (float) Math.random();
            color[i].b = (float) Math.random();
            color[i].a = 1.0f;
        }

        p = new Point("Point Group",vertex, null, color, null);
        p.setModelBound(new BoundingSphere());
        p.updateModelBound();
        System.out.println(p.getModelBound());

        scene = new Node("3D Scene Node");
        scene.attachChild(p);
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
     * not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {

    }

}