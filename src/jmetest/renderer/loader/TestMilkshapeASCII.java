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
package jmetest.renderer.loader;

import java.net.URL;

import com.jme.animation.DeformationJointController;
import com.jme.app.SimpleGame;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;

/**
 * <code>TestBackwardAction</code>
 * @author Mark Powell
 * @version 
 */
public class TestMilkshapeASCII extends SimpleGame {
    private Camera cam;
    private Node scene;
    private InputController input;
    private Model model;
    private Timer timer;

    /**
     * Nothing to update.
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update(float interpolation) {
    	timer.update();
        input.update(timer.getTimePerFrame() * 100);
        //System.out.println(timer.getFrameRate());
        scene.updateWorldData(timer.getTimePerFrame());
        System.out.println("Scene bound: " + scene.getWorldBound());
        System.out.println("Model bound: " + model.getWorldBound());
    }

    /**
     * Render the scene
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(scene);
    }

    /**
     * set up the display system and camera.
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
			display.setTitle("Joint Animation");
            cam =
                display.getRenderer().getCamera(
                    properties.getWidth(),
                    properties.getHeight());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA();
        blackColor.r = 0;
        blackColor.g = 0;
        blackColor.b = 0;
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(0.0f, 0.0f, 200.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);
        
        input = new FirstPersonController(this, cam, "LWJGL");
        timer = Timer.getTimer("LWJGL");
        
        

    }

    /**
     * set up the scene
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        URL modelURL = null;
    	ZBufferState zstate = display.getRenderer().getZBufferState();
    	zstate.setEnabled(true);
    	scene = new Node("Model");
    	scene.setRenderState(zstate);
    	model = new MilkshapeASCIIModel("Milkshape Model");
        modelURL = TestMilkshapeASCII.class.getClassLoader().getResource("jmetest/data/model/msascii/run.txt");
        model.load(modelURL, "jmetest/data/model/msascii/");
    	model.getAnimationController().setFrequency(10.0f);
    	model.getAnimationController().setRepeatType(Controller.RT_CYCLE);
    	((DeformationJointController)model.getAnimationController()).setUpdateModelBounds(true);
        scene.attachChild(model);
		SpotLight am = new SpotLight();
		am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am.setDirection(new Vector3f(0, 0, 0));
		am.setLocation(new Vector3f(25, 10, 0));
		am.setAngle(15);

		SpotLight am2 = new SpotLight();
		am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am2.setDirection(new Vector3f(0, 0, 0));
		am2.setLocation(new Vector3f(-25, 10, 0));
		am2.setAngle(15);


		DirectionalLight dr = new DirectionalLight();
		dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		dr.setDirection(new Vector3f(0, 0 , -150));

		LightState state = display.getRenderer().getLightState();
		state.setEnabled(true);
		state.attach(am);
		state.attach(dr);
		state.attach(am2);
		am.setEnabled(true);
		am2.setEnabled(true);
		dr.setEnabled(true);
		scene.setRenderState(state);
        cam.update();
        scene.updateGeometricState(0,true);

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
		TestMilkshapeASCII app = new TestMilkshapeASCII();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }
}
