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

/*
 * Created on 16 déc. 2003
 *
 */
package com.jme.test.demo;

import com.jme.input.InputController;
import com.jme.input.InputSystem;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyExitAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.sound.SoundSystem;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;

/**
 * @author Arman Ozcelik
 *
 */
public class JmeDemo extends SceneEnabledGame {

	private SoundSystem soundSystem;
	private SceneController demoScene;
	private Camera cam;
	private InputController input;
	private Timer timer;
	protected void update() {
		demoScene.update();
	}

	protected void render() {
		demoScene.render();

	}

	protected void initSystem() {

		timer= Timer.getTimer("LWJGL");
		demoScene= new SceneController(this);

		/*****************************************************************
		 * 						INIT DISPLAY							 *
		 *****************************************************************/

		try {
			display= DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(
				properties.getWidth(),
				properties.getHeight(),
				properties.getDepth(),
				properties.getFreq(),
				properties.getFullscreen());
			cam= display.getRenderer().getCamera(properties.getWidth(), properties.getHeight());
			ColorRGBA blackColor= new ColorRGBA();
			blackColor.r= 0;
			blackColor.g= 0;
			blackColor.b= 0;
			display.getRenderer().setBackgroundColor(blackColor);
			cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);

			Vector3f loc= new Vector3f(0.0f, 0.0f, 20.0f);
			Vector3f left= new Vector3f(-1.0f, 0.0f, 0.0f);
			Vector3f up= new Vector3f(0.0f, 1.0f, 0.0f);
			Vector3f dir= new Vector3f(0.0f, 0f, -1.0f);
			cam.setFrame(loc, left, up, dir);
			display.getRenderer().setCamera(cam);
			/*****************************************************************
			* 						INIT SOUND SYSTEM						 *
			 *****************************************************************/
			soundSystem= SoundSystem.getSoundEffectSystem("LWJGL");
			input= new InputController();
			/*****************************************************************
			* 						INIT INPUT SYSTEM						 *
			*****************************************************************/

			input= new InputController();
			KeyBindingManager keyboard= KeyBindingManager.getKeyBindingManager();
			InputSystem.createInputSystem("LWJGL");
			keyboard.setKeyInput(InputSystem.getKeyInput());
			keyboard.set("exit", KeyInput.KEY_ESCAPE);
			input.setKeyBindingManager(keyboard);
			KeyExitAction exitAction= new KeyExitAction(this);
			exitAction.setKey("exit");
			input.addAction(exitAction);
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	protected void initGame() {
		if (demoScene != null) {
			demoScene.init("com.jme.test.demo.LoadingScene");
		}
	}

	protected void reinit() {
		// TODO Auto-generated method stub

	}

	protected void cleanup() {
		// TODO Auto-generated method stub

	}

	public Camera getCamera() {
		return cam;
	}

	public SoundSystem getSoundSystem() {
		return soundSystem;
	}

	public DisplaySystem getDisplay() {
		return display;
	}

	public static void main(String[] args) {
		JmeDemo app= new JmeDemo();

		app.useDialogAlways(true);
		app.start();
	}

	/**
	 * @return
	 */
	public InputController getInput() {
		return input;
	}

	/* (non-Javadoc)
	 * @see com.jme.test.demo.SceneEnabledGame#getTimer()
	 */
	public Timer getTimer() {

		return timer;
	}

}
