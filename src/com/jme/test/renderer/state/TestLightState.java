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
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestLightState.java,v 1.2 2003-10-31 22:02:54 mojomonkey Exp $
 */
public class TestLightState extends AbstractGame {
    private TriMesh t;
    private Camera cam;
    private Node scene;
    private InputController input;

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
        input.update(0.25f);
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
        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 4.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonController(this, cam, "LWJGL");

    }

    /** 
     * builds the trimesh.
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        Vector3f max = new Vector3f(10,10,-20);
        Vector3f min = new Vector3f(0,0,-10);
        Vector3f[] verts = new Vector3f[24];
        Vector3f vert0 = min;
        Vector3f vert1 = new Vector3f(max.x, min.y, min.z);
        Vector3f vert2 = new Vector3f(max.x, max.y, min.z);
        Vector3f vert3 = new Vector3f(min.x, max.y, min.z);
        Vector3f vert4 = new Vector3f(max.x, min.y, max.z);
        Vector3f vert5 = new Vector3f(min.x, min.y, max.z);
        Vector3f vert6 = max;
        Vector3f vert7 = new Vector3f(min.x, max.y, max.z);

        //Front
        verts[0] = vert0;
        verts[1] = vert1;
        verts[2] = vert2;
        verts[3] = vert3;

        //Right
        verts[4] = vert1;
        verts[5] = vert4;
        verts[6] = vert6;
        verts[7] = vert2;

        //Back
        verts[8] = vert4;
        verts[9] = vert5;
        verts[10] = vert7;
        verts[11] = vert6;

        //Left
        verts[12] = vert5;
        verts[13] = vert0;
        verts[14] = vert3;
        verts[15] = vert7;

        //Top
        verts[16] = vert2;
        verts[17] = vert6;
        verts[18] = vert7;
        verts[19] = vert3;

        //Bottom
        verts[20] = vert0;
        verts[21] = vert5;
        verts[22] = vert4;
        verts[23] = vert1;

        
        Vector3f[] normals = new Vector3f[24];
        //Vector3f front = new Vector3f(0, 0, -1);
        //Vector3f right = new Vector3f(1, 0, 0);
        //Vector3f back = new Vector3f(0, 0, 1);
        Vector3f left = new Vector3f(-1, 0, 0);
        Vector3f top = new Vector3f(0, 1, 0);
        Vector3f bottom = new Vector3f(0, -1, 0);
//        Vector3f front = vert0.cross(vert3).normalize();
//        Vector3f right = vert3.cross(vert4).normalize();
//        Vector3f back = vert6.cross(vert5).normalize();
//        Vector3f left = vert0.cross(vert7).normalize();
       // System.out.println(back);
        System.out.println(left);

        //front
        normals[0] = vert1.cross(vert3).normalize();
        normals[1] = vert2.cross(vert0).normalize();
        normals[2] = vert3.cross(vert1).normalize();
        normals[3] = vert0.cross(vert2).normalize();

        //right
        normals[4] = vert4.cross(vert2).normalize();
        normals[5] = vert6.cross(vert1).normalize();
        normals[6] = vert2.cross(vert4).normalize();
        normals[7] = vert1.cross(vert6).normalize();

        //back
        normals[8] = vert5.cross(vert6).normalize();
        normals[9] = vert7.cross(vert4).normalize();
        normals[10] = vert6.cross(vert5).normalize();
        normals[11] = vert4.cross(vert7).normalize();

        //left
        normals[12] = vert0.cross(vert7).normalize();
        normals[13] = vert3.cross(vert5).normalize();
        normals[14] = vert7.cross(vert0).normalize();
        normals[15] = vert5.cross(vert3).normalize();

        //top
        normals[16] = vert6.cross(vert3).normalize();
        normals[17] = vert7.cross(vert2).normalize();
        normals[18] = vert3.cross(vert6).normalize();
        normals[19] = vert2.cross(vert7).normalize();

        //bottom
        normals[20] = vert5.cross(vert1).normalize();
        normals[21] = vert4.cross(vert0).normalize();
        normals[22] = vert1.cross(vert5).normalize();
        normals[23] = vert0.cross(vert4).normalize();

        
        int[] indices = {
                    0,1,2,
                    0,2,3,
                    4,5,6,
                    4,6,7,
                    8,9,10,
                    8,10,11,
                    12,13,14,
                    12,14,15,
                    16,17,18,
                    16,18,19,
                    20,21,22,
                    20,22,23
                    
                };

        
        ColorRGBA[] color = new ColorRGBA[24];
        for (int i = 0; i < color.length; i++) {
            color[i] = new ColorRGBA(1, 1, 1, 1);
        }

        t = new TriMesh(verts, normals, color, null, indices);
        //t = new Box(new Vector3f(-10, 0, 0), new Vector3f(-20, 10, 10));
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        
        scene = new Node();
        scene.attachChild(t);
        
        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);
        
        SpotLight am = new SpotLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.0f, 0.5f, 1.0f, 1.0f));
        am.setSpecular(new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
        am.setDirection(new Vector3f(-40, -50, 0));
        am.setLocation(new Vector3f(-20, -100, 0));
        am.setAngle(90);

        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(1.0f, 0.5f, 0.0f, 1.0f));
        dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        dr.setDirection(new Vector3f(-40, -25, 0));

        LightState state = display.getRenderer().getLightState();
        state.attach(am);
        state.attach(dr);
        am.setEnabled(true);
        dr.setEnabled(true);
        scene.setRenderState(state);
        scene.setRenderState(buf);
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
