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

package test.general;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL;

import jme.controller.TrackingController;
import jme.system.DisplaySystem;
import jme.entity.Entity;
import jme.entity.camera.Camera;

/**
 * <code>TestController.java</code>
 * 
 * @author Mark Powell
 * @version 0.1
 */
public class TestController extends TrackingController {
	private TestMain main;
	private int smode;

	public TestController(Camera camera, Entity entity, TestMain main) {
		super(entity, camera);
		this.main = main;
		key.set("640x480", Keyboard.KEY_1);
		key.set("1024x768", Keyboard.KEY_2);
		key.set("1024x768fs", Keyboard.KEY_3);
		key.set("linemode", Keyboard.KEY_7);
		key.set("fillmode", Keyboard.KEY_8);
		key.set("screenshot", Keyboard.KEY_0);
		int[] ss = { Keyboard.KEY_RCONTROL, Keyboard.KEY_P };
		key.add("screenshot", ss);
	}
	protected boolean checkAdditionalKeys() {
		if (isKeyDown("exit")) {
			return false;
		}

		if (isKeyDown("640x480")) {
			if (smode != 0) {
				smode = 0;
				main.resetDisplay(640, 480, 32, 60,false, "Terrain");
			}
		}

		if (isKeyDown("1024x768")) {
			if (smode != 1) {
				smode = 1;
				main.resetDisplay(1024, 768, 32, 60,false, "Terrain");
			}
		}

		if (isKeyDown("1024x768fs")) {
			if (smode != 2) {
				smode = 2;
				main.resetDisplay(1024, 768, 32, 60,true, "Terrain");
			}
		}

		if (isKeyDown("linemode")) {
			DisplaySystem.getDisplaySystem().getGL().polygonMode(
				GL.FRONT_AND_BACK,
				GL.LINE);
		}

		if (isKeyDown("fillmode")) {
			DisplaySystem.getDisplaySystem().getGL().polygonMode(
				GL.FRONT_AND_BACK,
				GL.FILL);
		}

		if (isKeyDown("screenshot")) {
			DisplaySystem.getDisplaySystem().takeScreenShot("test");
		}
		return true;
	}
}
