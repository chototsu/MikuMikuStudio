/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved. Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of the Mojo Monkey Coding, jME,
 * jMonkey Engine, nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jmetest.effects;

import com.jme.app.VariableTimestepGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * @author Ahmed
 * @version $Id: TestMotionBlur.java, Jul 1, 2004 11:56:16 AM
 */
public class TestMotionBlur extends VariableTimestepGame {

	private Camera cam;
	private InputHandler input;

	private Node rootNode, sceneNode, blurNode;

	private TextureRenderer tRenderer;
	private Texture fakeTex;

	private boolean firstFrame = true;

	protected void update(float timeD) {
		float time = timeD * 10;
		input.update(time);

		if (firstFrame == false) {
			tRenderer.updateCamera();
			tRenderer.render(rootNode, fakeTex);
		} else {
			firstFrame = false;
		}

		rootNode.updateGeometricState(time, false);
	}

	protected void render(float timeD) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(rootNode);
	}

	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(properties.getWidth(),
					properties.getHeight(),
					properties.getDepth(),
					properties.getFreq(),
					properties.getFullscreen());
			cam = display.getRenderer().createCamera(properties.getWidth(),
					properties.getHeight());
		} catch (JmeException je) {
			je.printStackTrace();
			System.exit(1);
		}

		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0, 0, 25);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f dir = new Vector3f(0, 0, -1);
		cam.setFrame(loc, left, up, dir);
		cam.update();
		display.getRenderer().setCamera(cam);

		input = new FirstPersonHandler(this, cam, properties.getRenderer());
		input.setMouseSpeed(0.5f);
		input.setKeySpeed(2f);
	}
	protected void initGame() {
		// init nodes
		rootNode = new Node("Root Node");
		sceneNode = new Node("SceneNode");
		blurNode = new Node("Blur Node");

		// set render queues
		//blurNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		// alpha state for the quad
		AlphaState as = display.getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setEnabled(true);

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(TextureManager
				.loadTexture(TestMotionBlur.class.getClassLoader()
						.getResource("jmetest/data/images/Monkey.jpg"),
						Texture.MM_LINEAR,
						Texture.FM_LINEAR,
						true));

		tRenderer = display.createTextureRenderer(512,
				512,
				false,
				true,
				false,
				false,
				TextureRenderer.RENDER_TEXTURE_2D,
				0);
		tRenderer.setBackgroundColor(ColorRGBA.black);
		tRenderer.setCamera(cam);
		tRenderer.updateCamera();
		fakeTex = tRenderer.setupTexture();

		TextureState blurTS = display.getRenderer().createTextureState();
		blurTS.setTexture(fakeTex);
		blurTS.setEnabled(true);

		Quad quad = new Quad("Blur Quad", 10, 10);
		quad.setSolidColor(new ColorRGBA(1, 1, 1, 0.99f));
		quad.setRenderState(blurTS);
		quad.setRenderState(as);

		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);
		Box box = new Box("Scene", min.mult(5), max.mult(5));
		box.setRenderState(ts);

		blurNode.attachChild(quad);
		sceneNode.attachChild(box);

		ZBufferState zEnabled = display.getRenderer().createZBufferState();
		zEnabled.setEnabled(true);

		rootNode.setRenderState(zEnabled);
		rootNode.attachChild(sceneNode);
		rootNode.attachChild(blurNode);
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
	}

	protected void cleanup() {

	}

	protected void reinit() {

	}

	public static void main(String[] args) {
		TestMotionBlur app = new TestMotionBlur();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}