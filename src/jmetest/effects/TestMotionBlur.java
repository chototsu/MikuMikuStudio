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
//import com.jme.effects.MotionBlur;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
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
	private TextureState blurTS;

	protected void update(float timeD) {
		float time = timeD * 10;
		input.update(time);
		
		tRenderer.updateCamera();
		tRenderer.render(sceneNode, fakeTex);

		rootNode.updateGeometricState(time, false);
	}

	protected void render(float timeD) {
		display.getRenderer().clearBuffers();

		display.getRenderer().draw(sceneNode);

		display.getRenderer().setOrthoCenter();
		display.getRenderer().draw(blurNode);
		display.getRenderer().unsetOrtho();
	}

	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(properties.getWidth(),
					properties.getHeight(),
					properties.getDepth(),
					properties.getFreq(),
					properties.getFullscreen());
			cam = display.getRenderer().getCamera(properties.getWidth(),
					properties.getHeight());
		} catch (JmeException je) {
			je.printStackTrace();
			System.exit(1);
		}

		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0, 0, 75);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f dir = new Vector3f(0, 0, -1);
		cam.setFrame(loc, left, up, dir);
		cam.update();
		display.getRenderer().setCamera(cam);

		input = new FirstPersonHandler(this, cam, properties.getRenderer());
		input.setMouseSpeed(0.5f);

	}
	protected void initGame() {
		// init nodes
		rootNode = new Node("Root Node");
		sceneNode = new Node("Scene Node");
		blurNode = new Node("BlurNode");

		// init states
		TextureState ts = display.getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(TextureManager
				.loadTexture(TestMotionBlur.class.getClassLoader()
						.getResource("jmetest/data/images/Monkey.jpg"),
						Texture.MM_LINEAR,
						Texture.FM_LINEAR,
						true));

		AlphaState as = display.getRenderer().getAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
		
		ZBufferState zEnabled = display.getRenderer().getZBufferState();
		zEnabled.setEnabled(true);
		zEnabled.setFunction(ZBufferState.CF_LEQUAL);

		tRenderer = display.createTextureRenderer(512,
				512,
				false,
				true,
				false,
				false,
				TextureRenderer.RENDER_TEXTURE_2D,
				0);
		tRenderer.setBackgroundColor(new ColorRGBA(1, 1, 1, 0.5f));
		tRenderer.setCamera(cam);
		tRenderer.updateCamera();
		fakeTex = tRenderer.setupTexture();
		/*
		 * MotionBlur mb = new MotionBlur(); mb.setBlurValue(0.5f); fakeTex =
		 * new Texture(); fakeTex.setApply(Texture.AM_MODULATE);
		 * fakeTex.setBlendColor(new ColorRGBA(1, 1, 1, 1));
		 * fakeTex.setCorrection(Texture.CM_PERSPECTIVE);
		 * fakeTex.setFilter(Texture.MM_LINEAR); fakeTex.setImage(mb);
		 * fakeTex.setMipmapState(Texture.FM_LINEAR); fakeTex.setTextureId(0);
		 */

		blurTS = display.getRenderer().getTextureState();
		blurTS.setEnabled(true);
		blurTS.setTexture(fakeTex);

		// init scene
		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

		Box b = new Box("Box", min.mult(5), max.mult(5));
		b.setRenderState(ts);

		// init blurnode
		Quad q = new Quad("Quad", display.getWidth(), display.getHeight());
		q.setRenderState(as);
		q.setRenderState(blurTS);

		// attach to nodes
		rootNode.setRenderState(zEnabled);
		sceneNode.attachChild(b);
		blurNode.attachChild(q);
		rootNode.attachChild(sceneNode);
		rootNode.attachChild(blurNode);

		rootNode.updateGeometricState(0, true);
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