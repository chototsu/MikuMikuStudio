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

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.intersection.CollisionDetection;
import com.jme.intersection.CollisionResults;
import com.jme.light.DirectionalLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestCollision</code>
 * @author Mark Powell
 * @version $Id: TestCollision.java,v 1.4 2004-02-24 16:46:13 mojomonkey Exp $
 */
public class TestCollision extends SimpleGame {

    private TriMesh t;
    private TriMesh t2;
    private Camera cam;
    private Text text;
    private Node root;
    private Node scene;
    private InputController input;
    private Thread thread;
    private Timer timer;
    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;

    private float tInc = -40.0f;
    private float t2Inc = -10.0f;

    /**
     * Entry point for the test, 
     * @param args
     */
    public static void main(String[] args) {
        TestCollision app = new TestCollision();
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
        if (timer.getTimePerFrame() < 1) {
            angle = angle + (timer.getTimePerFrame() * 1);
            if (angle > 360) {
                angle = 0;
            }
        }

        rotQuat.fromAngleAxis(angle, axis);
        timer.update();
        input.update(timer.getTimePerFrame());

        t.setLocalRotation(rotQuat);
        
        t.getLocalTranslation().y += tInc * timer.getTimePerFrame();
        t2.getLocalTranslation().x += t2Inc * timer.getTimePerFrame();
        
        if(t.getLocalTranslation().y > 40) {
            t.getLocalTranslation().y = 40;
            tInc *= -1;
        } else if(t.getLocalTranslation().y < -40) {
            t.getLocalTranslation().y = -40;
            tInc *= -1;
        }
        
        if(t2.getLocalTranslation().x > 40) {
            t2.getLocalTranslation().x = 40;
            t2Inc *= -1;
        } else if(t2.getLocalTranslation().x < -40) {
            t2.getLocalTranslation().x = -40;
            t2Inc *= -1;
        }
        
        CollisionResults results = new CollisionResults();
        
        CollisionDetection.hasCollision(t, scene, results);
        
        if(results.getNumber() > 0) {
            text.print("Collision: YES");
        } else {
            text.print("Collision: NO");
        }
        
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

        input = new FirstPersonController(this, cam, properties.getRenderer());
        input.setKeySpeed(15f);
        input.setMouseSpeed(1);

        timer = Timer.getTimer(properties.getRenderer());

        display.getRenderer().setCullingMode(Renderer.CULL_BACK);
        rotQuat = new Quaternion();
        axis = new Vector3f(1, 0, 0);
        display.setTitle("Collision Detection");

    }

    /** 
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        text = new Text("Text Label","Collision: No");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        TextureState textImage = display.getRenderer().getTextureState();
        textImage.setEnabled(true);
        textImage.setTexture(
            TextureManager.loadTexture(
                TestCollision.class.getClassLoader().getResource("jmetest/data/font/font.png"),
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
        as1.setEnabled(true);
        text.setRenderState(as1);
        scene = new Node("3D Scene Root");
        root = new Node("Scene Root");
        root.attachChild(text);

        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);

        t = new Box("Box 1", min, max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(new Vector3f(0, 30, 0));
        
        t2 = new Box("Box 2", min, max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(new Vector3f(30, 0, 0));

        scene.attachChild(t);
        scene.attachChild(t2);
        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        DirectionalLight am = new DirectionalLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, 75));

        scene.setRenderState(buf);
        scene.setWorldBound(new BoundingSphere());
        cam.update();

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(
            TextureManager.loadTexture(
                TestCollision.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));

        scene.setRenderState(ts);

        root.attachChild(text);

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
