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
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestLightState.java,v 1.1 2003-10-13 18:30:09 mojomonkey Exp $
 */
public class TestLightState extends AbstractGame {
    private TriMesh t;
    private Camera cam;
    private Node scene;

    /**
     * Entry point for the test, 
     * @param args
     */
    public static void main(String[] args) {
        TestLightState app = new TestLightState();
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
        ColorRGBA blackColor = new ColorRGBA(0,0,0,1);
        display.getRenderer().setBackgroundColor(blackColor);
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
        Vector3f[] verts = new Vector3f[4];
        ColorRGBA[] color = new ColorRGBA[4];
        Vector3f[] normal = new Vector3f[4];

        verts[0] = new Vector3f();
        normal[0] = new Vector3f();
        normal[0].x = 1;
        normal[0].y = 0;
        normal[0].z = 0;
        verts[0].x = -50;
        verts[0].y = 0;
        verts[0].z = 0;
        verts[1] = new Vector3f();
        normal[1] = new Vector3f();
        normal[1].x = 1f;
        normal[1].y = 0;
        normal[1].z = 0;
        verts[1].x = -50;
        verts[1].y = 25;
        verts[1].z = 25;
        verts[2] = new Vector3f();
        normal[2] = new Vector3f();
        normal[2].x = 1f;
        normal[2].y = 0;
        normal[2].z = 0;
        verts[2].x = -50;
        verts[2].y = 25;
        verts[2].z = 0;
        verts[3] = new Vector3f();
        normal[3] = new Vector3f();
        normal[3].x = 0.25f;
        normal[3].y = -0.25f;
        normal[3].z = -0.25f;
        verts[3].x = -30;
        verts[3].y = 35;
        verts[3].z = 25;
        

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
        color[3] = new ColorRGBA();
        int[] indices = { 0, 1, 2, 1, 2, 3};

        t = new TriMesh(verts, normal, color, null, indices);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();
        
        cam.update();

        scene = new Node();
        scene.attachChild(t);
        scene.setLocalTranslation(new Vector3f(0,-25,-10));
        
        SpotLight am = new SpotLight();
        am.setDiffuse(new ColorRGBA(0.0f,1.0f,0.0f,1.0f));
        am.setAmbient(new ColorRGBA(0.0f,0.5f, 1.0f, 1.0f));
        am.setSpecular(new ColorRGBA(0.0f,0.0f,1.0f,1.0f));
        am.setDirection(new Vector3f(-40, -50, 0));
        am.setLocation(new Vector3f(-20, -100, 0));
        am.setAngle(90);
        
        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(0.0f,0.0f,1.0f,1.0f));
        dr.setAmbient(new ColorRGBA(1.0f,0.5f, 0.0f, 1.0f));
        dr.setSpecular(new ColorRGBA(1.0f,0.0f,0.0f,1.0f));
        dr.setDirection(new Vector3f(-40, -25, 0));
        
        LightState state = display.getRenderer().getLightState();
        state.attach(am);
        state.attach(dr);
        scene.setRenderState(state);
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
