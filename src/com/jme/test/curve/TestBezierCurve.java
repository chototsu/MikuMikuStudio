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

import com.jme.app.AbstractGame;
import com.jme.curve.BezierCurve;
import com.jme.image.Texture;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * <code>TestBezierCurve</code>
 * @author Mark Powell
 * @version $Id: TestBezierCurve.java,v 1.4 2004-01-07 03:52:53 mojomonkey Exp $
 */
public class TestBezierCurve extends AbstractGame {
    private TriMesh t, t2, t3, t4;

    private Text text;
    private TriMesh box;
    private Node scene, root;
    private static final float MAX_STEPS = 25;
    private Camera cam;
    private InputController input;
    private BezierCurve curve;
    
    private float step = 0;
    private float mod = 0.0005f;

    private Vector3f up = new Vector3f(0,1,0);

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
        box.setLocalTranslation(curve.getPoint(step));
        box.setLocalRotation(curve.getOrientation(step, 0.1f, up));
        scene.updateWorldData(0.1f);
        step += mod;
        
        text.print("STEP: " + step);
        
        if(step > 1) {
            mod = mod*-1;
            step = 1+mod;
        } else if(step < 0) {
            mod = mod*-1;
            step = mod;
        }

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        Vector3f point;
        display.getRenderer().clearBuffers();
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
        text = new Text("Timer");
        text.setLocalTranslation(new Vector3f(1,60,0));
        TextureState textImage = display.getRenderer().getTextureState();
        textImage.setEnabled(true);
        textImage.setTexture(
            TextureManager.loadTexture(
                "data/Font/font.png",
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        text.setRenderState(textImage);
        AlphaState as1 = display.getRenderer().getAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);
        text.setRenderState(as1);
        //create control Points
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f(-4, 0, 0);
        points[1] = new Vector3f(-2, 3, 2);
        points[2] = new Vector3f(2, -3, -2);
        points[3] = new Vector3f(4, 0, 0);

        curve = new BezierCurve(points);
        ColorRGBA[] colors = new ColorRGBA[4];
        colors[0] = new ColorRGBA(0,1,0,1);
        colors[1] = new ColorRGBA(1,0,0,1);
        colors[2] = new ColorRGBA(1,1,0,1);
        colors[3] = new ColorRGBA(0,0,1,1);
        curve.setColors(colors);

        Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
        Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

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

        box = new Box(min.mult(5), max.mult(5));
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();

        box.setLocalTranslation(points[0]);
        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                "data/Images/Monkey.jpg",
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        box.setRenderState(ts);

        scene = new Node();
        scene.setRenderState(buf);
        scene.attachChild(t);
        scene.attachChild(t2);
        scene.attachChild(t3);
        scene.attachChild(t4);
        scene.attachChild(box);
        scene.attachChild(text);
        scene.attachChild(curve);
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
