/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

import com.jme.animation.VertexKeyframeController;
import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.model.md2.Md2KeyframeSelector;
import com.jme.scene.model.md2.Md2Model;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestBackwardAction</code>
 * 
 * @author Mark Powell
 * @version $Id: TestMd2.java,v 1.4 2004-02-20 20:51:16 mojomonkey Exp $
 */
public class TestMd2 extends SimpleGame {
	LightState state;
	ZBufferState zstate;
	TextureState ts = null;
	private Camera cam;
	private InputController input;
	private Timer timer;
	private Md2Model model;
	private String FILE_NAME = "data/model/drfreak.md2";
	private String TEXTURE_NAME = "data/model/drfreak.jpg";
	private float rotate;
	private Quaternion quat = new Quaternion();
	private Vector3f up = new Vector3f(0,1,0);
	private Md2KeyframeSelector keyframeSelector;
	private int animCounter = 0;
	private float lastTime = 10;
	/**
	 * Nothing to update.
	 * 
	 * @see com.jme.app.AbstractGame#update()
	 */
	protected void update(float f) {
		timer.update();
		input.update(timer.getTimePerFrame() * 100);
		model.updateWorldData(timer.getTimePerFrame()*10);
		lastTime += timer.getTimePerFrame();
		if(lastTime > 0.25) {
			if (KeyBindingManager
				.getKeyBindingManager()
				.isValidCommand("selectAnimation")) {
				animCounter++;
				if(animCounter >= keyframeSelector.getNumberOfAnimations()) {
					animCounter = 0;
				}
				keyframeSelector.setAnimation(animCounter);
			}
			lastTime = 0;	
		}	
	}

	/**
	 * Render the scene
	 * 
	 * @see com.jme.app.AbstractGame#render()
	 */
	protected void render(float f) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(model);

	}

	/**
	 * set up the display system and camera.
	 * 
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
			display.setTitle("MD2 Animation");
			cam =
				display.getRenderer().getCamera(
					properties.getWidth(),
					properties.getHeight());

		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ColorRGBA blackColor = new ColorRGBA();
		blackColor.r = 1;
		blackColor.g = 1;
		blackColor.b = 1;
		display.getRenderer().setBackgroundColor(blackColor);
		cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0.0f, 0.0f, 200.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		cam.setFrame(loc, left, up, dir);

		display.getRenderer().setCamera(cam);

		input = new FirstPersonController(this, cam, properties.getRenderer());
		timer = Timer.getTimer(properties.getRenderer());
		KeyBindingManager.getKeyBindingManager().set(
					"selectAnimation",
					KeyInput.KEY_F1);
		display.setTitle("MD2 Animation");
	}

	/**
	 * set up the scene
	 * 
	 * @see com.jme.app.AbstractGame#initGame()
	 */
	protected void initGame() {  
		model = new Md2Model("Dr Freak");
		model.load(TestMd2.class.getClassLoader().getResource("jmetest/"+FILE_NAME));
		
		ts = display.getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(
			TextureManager.loadTexture(
			    TestMd2.class.getClassLoader().getResource("jmetest/"+TEXTURE_NAME),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		
		SpotLight am = new SpotLight();
		am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
		am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am.setDirection(new Vector3f(0, 0, 0));
		am.setLocation(new Vector3f(25, 10, 10));
		am.setAngle(5);

		SpotLight am2 = new SpotLight();
		am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
		am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		am2.setDirection(new Vector3f(0, 0, 0));
		am2.setLocation(new Vector3f(-25, 10, 0));
		am2.setAngle(5);

		DirectionalLight dr = new DirectionalLight();
		dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		dr.setAmbient(new ColorRGBA(0.25f, 0.25f, 0.25f, 1.0f));
		dr.setDirection(new Vector3f(0, 0, -150));

		state = display.getRenderer().getLightState();
		state.setEnabled(true);
		state.attach(am);
		state.attach(dr);
		state.attach(am2);
		am.setEnabled(true);
		am2.setEnabled(true);
		dr.setEnabled(true);

		zstate = display.getRenderer().getZBufferState();
		zstate.setEnabled(true);
		
		WireframeState ws = display.getRenderer().getWireframeState();
		ws.setEnabled(false);
		
		model.setRenderState(state);
		model.setRenderState(ts);
		model.setRenderState(zstate);
		model.setRenderState(ws);
		
		model.getAnimationController().setRepeatType(Controller.RT_WRAP);
		model.updateGeometricState(0, true);
		
		keyframeSelector = new Md2KeyframeSelector((VertexKeyframeController)model.getAnimationController());
	}

	/**
	 * not used.
	 * 
	 * @see com.jme.app.AbstractGame#reinit()
	 */
	protected void reinit() {
	}

	/**
	 * not used.
	 * 
	 * @see com.jme.app.AbstractGame#cleanup()
	 */
	protected void cleanup() {
	}

	public static void main(String[] args) {
		TestMd2 app = new TestMd2();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}
