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
package com.jme.test.curve;

import org.lwjgl.opengl.GL;

import com.jme.app.AbstractGame;
import com.jme.curve.BezierCurve;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;

/**
 * <code>TestBezierCurve</code>
 * @author Mark Powell
 * @version $Id: TestBezierCurve.java,v 1.1 2004-01-06 15:20:26 mojomonkey Exp $
 */
public class TestBezierCurve extends AbstractGame {
    private TriMesh t,t2,t3,t4;
    private Node scene, root;
    private static final float MAX_STEPS = 25;
    private Camera cam;
    private InputController input;
    private BezierCurve curve;

    public static void main(String[] args) {
        TestBezierCurve app = new TestBezierCurve();
        app.useDialogAlways(true);
        app.start();
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {
        input.update(0.2f);

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        Vector3f point;
        display.getRenderer().clearBuffers();

        GL.glLineWidth(1.5f);
        GL.glColor3f(1, 0, 0);
        GL.glBegin(GL.GL_LINE_STRIP);
        for (float t = 0;
            t <= (1 + (1.0f / MAX_STEPS));
            t += 1.0f / MAX_STEPS) {
            point = curve.getPoint(t);
            GL.glVertex3f(point.x, point.y, point.z);
        }
        GL.glEnd();

        display.getRenderer().draw(root);

    }

    /* (non-Javadoc)
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

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        //create control Points
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f(-4, 0, 0);
        points[1] = new Vector3f(-2, 3, 2);
        points[2] = new Vector3f(2, -3, -2);
        points[3] = new Vector3f(4, 0, 0);

        curve = new BezierCurve(points);

        Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
        Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

        t = new Box(min, max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(points[0]);

        t2 = new Box(min, max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(points[1]);

        t3 = new Box(min, max);
        t3.setModelBound(new BoundingSphere());
        t3.updateModelBound();

        t3.setLocalTranslation(points[2]);

        t4 = new Box(min, max);
        t4.setModelBound(new BoundingSphere());
        t4.updateModelBound();

        t4.setLocalTranslation(points[3]);

        scene = new Node();
        scene.attachChild(t);
        scene.attachChild(t2);
        scene.attachChild(t3);
        scene.attachChild(t4);
        root = new Node();
        root.attachChild(scene);
        
        scene.updateGeometricState(0.0f, true);

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {
        // TODO Auto-generated method stub

    }

}
