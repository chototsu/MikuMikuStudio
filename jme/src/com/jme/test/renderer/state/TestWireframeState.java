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
package com.jme.test.renderer.state;

import com.jme.app.AbstractGame;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.WireframeState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestWireframeState</code>
 * @author Mark Powell
 * @version 
 */
public class TestWireframeState extends AbstractGame {
    private TriMesh t, t2;
    private Camera cam;
    private Node scene;

    /**
     * Entry point for the test, 
     * @param args
     */
    public static void main(String[] args) {
        TestWireframeState app = new TestWireframeState();
        app.useDialogAlways(true);
        app.start();
    }

    /**
     * Not used in this test.
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {

    }

    /** 
     * clears the buffers and then draws the TriMesh.
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        display.getRenderer().clearBuffers();

        display.getRenderer().draw(scene);

    }

    /**
     * creates the displays and sets up the viewport.
     * @see com.jme.app.AbstractGame#initSystem()
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
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

    }

    /** 
     * builds the trimesh.
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        Vector3f[] verts = new Vector3f[3];
        ColorRGBA[] color = new ColorRGBA[3];

        verts[0] = new Vector3f();
        verts[0].x = -50;
        verts[0].y = 0;
        verts[0].z = 0;
        verts[1] = new Vector3f();
        verts[1].x = -50;
        verts[1].y = 25;
        verts[1].z = 25;
        verts[2] = new Vector3f();
        verts[2].x = -50;
        verts[2].y = 25;
        verts[2].z = 0;

        color[0] = new ColorRGBA();
        color[0].r = 1;
        color[0].g = 0;
        color[0].b = 0;
        color[0].a = 1;
        color[1] = new ColorRGBA();
        color[1].r = 0;
        color[1].g = 1;
        color[1].b = 0;
        color[1].a = 1;
        color[2] = new ColorRGBA();
        color[2].r = 0;
        color[2].g = 0;
        color[2].b = 1;
        color[2].a = 1;
        int[] indices = { 0, 1, 2 };

        t = new TriMesh(verts, null, color, null, indices);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();
        
        Vector3f[] verts2 = new Vector3f[3];
        ColorRGBA[] color2 = new ColorRGBA[3];

        verts2[0] = new Vector3f();
        verts2[0].x = -40;
        verts2[0].y = 10;
        verts2[0].z = 0;
        verts2[1] = new Vector3f();
        verts2[1].x = -40;
        verts2[1].y = 35;
        verts2[1].z = 15;
        verts2[2] = new Vector3f();
        verts2[2].x = -10;
        verts2[2].y = 35;
        verts2[2].z = -10;

        color2[0] = new ColorRGBA();
        color2[0].r = 1;
        color2[0].g = 0;
        color2[0].b = 0;
        color2[0].a = 1;
        color2[1] = new ColorRGBA();
        color2[1].r = 0;
        color2[1].g = 1;
        color2[1].b = 0;
        color2[1].a = 1;
        color2[2] = new ColorRGBA();
        color2[2].r = 0;
        color2[2].g = 0;
        color2[2].b = 1;
        color2[2].a = 1;
        int[] indices2 = { 0, 1, 2 };

        t2 = new TriMesh(verts2, null, color2, null, indices2);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();
        System.out.println(t.getModelBound());
        cam.update();

        scene = new Node();
        scene.attachChild(t);
        scene.attachChild(t2);
        scene.setLocalTranslation(new Vector3f(0,-25,0));
        
        WireframeState ws = display.getRenderer().getWireframeState();
        ws.setEnabled(true);
        
        t2.setRenderState(ws);
        
        cam.update();

        scene.updateGeometricState(0.0f, true);

    }
    /**
     * not used.
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {

    }

    /** 
     * Not used.
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {

    }

}

