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
package com.jme.test.renderer;

import com.jme.app.AbstractGame;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Box;
import com.jme.scene.LightNode;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.model.MilkshapeASCIIModel;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestMilkshapeASCII.java,v 1.2 2004-01-29 20:52:01 mojomonkey Exp $
 */
public class TestMilkshapeASCII extends AbstractGame {
    private TriMesh t;
    private Camera cam;
    private Node root;
    private Node scene;
    private InputController input;
    private Thread thread;
    private LightState lightstate;
    private PointLight pl;
    private LightNode lightNode;
    private Vector3f currentPos;
    private Vector3f newPos;
    private Timer timer;
    private TriMesh[] other;

    /**
     * Entry point for the test, 
     * @param args
     */
    public static void main(String[] args) {
        TestMilkshapeASCII app = new TestMilkshapeASCII();
        app.useDialogAlways(true);
        app.start();

    }

    /**
     * Not used in this test.
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {
//      update world data
        timer.update();
        input.update(timer.getTimePerFrame() * 100);
        
        
        //update individual sprites
         if ((int) currentPos.x == (int) newPos.x
             && (int) currentPos.y == (int) newPos.y
             && (int) currentPos.z == (int) newPos.z) {
             newPos.x = (float) Math.random() * 1000 - 500;
             newPos.y = (float) Math.random() * 1000 - 500;
             newPos.z = (float) Math.random() * 1000 - 500;
         }

         currentPos.x -= (currentPos.x - newPos.x)
             / (timer.getFrameRate() / 2);
         currentPos.y -= (currentPos.y - newPos.y)
             / (timer.getFrameRate() / 2);
         currentPos.z -= (currentPos.z - newPos.z)
             / (timer.getFrameRate() / 2);
             
         lightNode.setLocalTranslation(currentPos);
         scene.updateWorldData(timer.getTimePerFrame());
         //System.out.println(other.getWorldBound());
         
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
        currentPos = new Vector3f();
        newPos = new Vector3f();
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
        ColorRGBA blackColor = new ColorRGBA(1, 1, 1, 1);
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 100.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new FirstPersonController(this, cam, "LWJGL");
        timer = Timer.getTimer("LWJGL");
    }

    /** 
     * builds the trimesh.
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {

        scene = new Node();

        scene = new Node();
        root = new Node();
        root.attachChild(scene);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        scene.setRenderState(buf);
        cam.update();

        Node body2 = MilkshapeASCIIModel.load("data/model/msascii/run.txt");
        scene.attachChild(body2);

        pl = new PointLight();
        pl.setAmbient(new ColorRGBA(0, 0, 0, 1));
        pl.setDiffuse(new ColorRGBA(1, 1, 1, 1));
        pl.setSpecular(new ColorRGBA(0, 0, 1, 1));
        pl.setEnabled(true);

        lightstate = display.getRenderer().getLightState();
        lightstate.setEnabled(true);
        
        lightNode = new LightNode(lightstate);
        lightNode.setLight(pl);
        lightNode.setTarget(scene);
        
        Vector3f min = new Vector3f(-0.15f, -0.15f, -0.15f);
        Vector3f max = new Vector3f(0.15f,0.15f,0.15f);
        Box lightBox = new Box(min,max);
        
        
        lightNode.attachChild(lightBox);
        lightNode.setForceView(true);
        
        scene.attachChild(lightNode);

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
