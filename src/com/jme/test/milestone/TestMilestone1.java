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
package com.jme.test.milestone;

import com.jme.app.AbstractGame;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.LWJGLCamera;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.TriMesh;
import com.jme.system.JmeException;
import com.jme.system.LWJGLDisplaySystem;

/**
 * <code>TestMilestone1</code> tests all features of Milestone 1:
 * 
 * Properties Dialog
 * Game loop
 * Scene graph elements
 * renderer
 * Culling
 * 
 * @author Mark Powell
 * @version $Id: TestMilestone1.java,v 1.1 2003-10-02 15:01:17 mojomonkey Exp $
 */
public class TestMilestone1 extends AbstractGame {
    private Node scene;
    private Camera cam;
    private Line l;
    private Point p;
    private TriMesh t;
    private TriMesh t2;

    /**
     * Nothing to update.
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {
    }

    /**
     * Render the scene
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(scene);
    }

    /**
     * set up the display system and camera.
     * @see com.jme.app.AbstractGame#initSystem()
     */
    protected void initSystem() {
        try {
            if("LWJGL".equalsIgnoreCase(properties.getRenderer())) {
                display = new LWJGLDisplaySystem();
                display.createWindow(properties.getWidth(), properties.getHeight(), 
                                properties.getDepth(), properties.getFreq(), 
                                properties.getFullscreen());
                cam = new LWJGLCamera(properties.getWidth(),properties.getHeight());
            }
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA();
        blackColor.r = 0;
        blackColor.g = 0;
        blackColor.b = 0;
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f,1000.0f,-0.55f,0.55f,0.4125f,-0.4125f);
        Vector3f loc = new Vector3f(4.0f,0.0f,0.0f);
        Vector3f left = new Vector3f(0.0f,-1.0f,0.0f);
        Vector3f up = new Vector3f(0.0f,0.0f,1.0f);
        Vector3f dir = new Vector3f(-1.0f,0f,0.0f);
        cam.setFrame(loc,left,up,dir);

        display.getRenderer().setCamera(cam);

    }

    /**
     * set up the scene
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        Vector3f[] vertex = new Vector3f[1000];
        ColorRGBA[] color = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex[i] = new Vector3f();
            vertex[i].x = (float) Math.random() * 50;
            vertex[i].y = (float) Math.random() * 50;
            vertex[i].z = (float) Math.random() * 50;
            color[i] = new ColorRGBA();
            color[i].r = (float) Math.random();
            color[i].g = (float) Math.random();
            color[i].b = (float) Math.random();
            color[i].a = 1.0f;
        }

        l = new Line(vertex, null, color, null);
        l.setLocalTranslation(new Vector3f(-200.0f,-25,-25));
        l.setModelBound(new BoundingSphere());
        l.updateModelBound();

        Vector3f[] vertex2 = new Vector3f[1000];
        ColorRGBA[] color2 = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex2[i] = new Vector3f();
            vertex2[i].x = (float) Math.random()* -100 - 50;
            vertex2[i].y = (float) Math.random() * 50 - 25;
            vertex2[i].z = (float) Math.random() * 50 - 25;
    
            color2[i] = new ColorRGBA();
            color2[i].r = (float) Math.random();
            color2[i].g = (float) Math.random();
            color2[i].b = (float) Math.random();
            color2[i].a = 1.0f;
        }

        p = new Point(vertex2, null, color2, null);
        p.setLocalTranslation(new Vector3f(0.0f, 25,0));
        p.setModelBound(new BoundingSphere());
        p.updateModelBound();
        Node pointNode = new Node();
        pointNode.attachChild(p);
        
        Vector3f[] verts = new Vector3f[3];
        ColorRGBA[] color3 = new ColorRGBA[3];

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

        color3[0] = new ColorRGBA();
        color3[0].r = 1;
        color3[0].g = 0;
        color3[0].b = 0;
        color3[0].a = 1;
        color3[1] = new ColorRGBA();
        color3[1].r = 0;
        color3[1].g = 1;
        color3[1].b = 0;
        color3[1].a = 1;
        color3[2] = new ColorRGBA();
        color3[2].r = 0;
        color3[2].g = 0;
        color3[2].b = 1;
        color3[2].a = 1;
        int[] indices = { 0, 1, 2 };

        t = new TriMesh(verts, null, color3, null, indices);
        t.setLocalTranslation(new Vector3f(-150, 0, 0));
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();
        
        pointNode.attachChild(t);
        pointNode.setLocalTranslation(new Vector3f(0,-50,0));
        
        //should be culled:
        
        Vector3f[] verts2 = new Vector3f[3];
        ColorRGBA[] color4 = new ColorRGBA[3];

        verts2[0] = new Vector3f();
        verts2[0].x = -50;
        verts2[0].y = 0;
        verts2[0].z = 0;
        verts2[1] = new Vector3f();
        verts2[1].x = -50;
        verts2[1].y = 25;
        verts2[1].z = 25;
        verts2[2] = new Vector3f();
        verts2[2].x = -50;
        verts2[2].y = 25;
        verts2[2].z = 0;

        color4[0] = new ColorRGBA();
        color4[0].r = 1;
        color4[0].g = 0;
        color4[0].b = 0;
        color4[0].a = 1;
        color4[1] = new ColorRGBA();
        color4[1].r = 0;
        color4[1].g = 1;
        color4[1].b = 0;
        color4[1].a = 1;
        color4[2] = new ColorRGBA();
        color4[2].r = 0;
        color4[2].g = 0;
        color4[2].b = 1;
        color4[2].a = 1;
        int[] indices2 = { 0, 1, 2 };

        t2 = new TriMesh(verts2, null, color4, null, indices2);
        t2.setLocalTranslation(new Vector3f(150, 0, 0));
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();
        
        scene = new Node();
        scene.attachChild(l);
        scene.attachChild(pointNode);
        scene.attachChild(t2);
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
     * not used.
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {
    }

    public static void main(String[] args) {
       TestMilestone1 app = new TestMilestone1();
       app.useDialogAlways(true);
       app.start();
    }
}
