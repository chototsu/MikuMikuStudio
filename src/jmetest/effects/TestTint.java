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
package jmetest.effects;

import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.effects.Tint;
import com.jme.image.Texture;
import com.jme.input.FirstPersonController;
import com.jme.input.InputController;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Box;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * <code>TestTint</code>
 * 
 * @author Ahmed
 * @version $Id: TestTint.java,v 1.2 2004-03-03 17:30:22 darkprophet Exp $
 */
public class TestTint extends SimpleGame {

	private Camera cam;
	private Node tintNode, scene;
	private InputController input;
	private Timer timer;

	private Tint tint;
	private TriMesh box;

	private float alpha;

	private Text instructions;

	protected void update(float interpolation) {
		timer.update();
		input.update(timer.getTimePerFrame() * 35);

		if (KeyBindingManager
			.getKeyBindingManager()
			.isValidCommand("Alpha+")) {
			alpha += 0.01f;
			tint.getTintColor().a = alpha;
		} else if (
			KeyBindingManager.getKeyBindingManager().isValidCommand(
				"Alpha-")) {
			alpha -= 0.01f;
			tint.getTintColor().a = alpha;
		}

		scene.updateWorldData(timer.getTimePerFrame() * 10);
		tintNode.updateWorldData(timer.getTimePerFrame() * 10);
	}

	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(scene);
		display.getRenderer().draw(tintNode);
	}

	protected void initSystem() {
		try {
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(
				properties.getWidth(),
				properties.getHeight(),
				properties.getDepth(),
				properties.getFreq(),
				properties.getFullscreen());

			display.setTitle("Tint Test");

			cam =
				display.getRenderer().getCamera(
					properties.getWidth(),
					properties.getHeight());
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
		display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 0));
		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);
		Vector3f loc = new Vector3f(0, 0, 3);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f dir = new Vector3f(0, 0, -1);
		cam.setFrame(loc, left, up, dir);
		display.getRenderer().setCamera(cam);

		input = new FirstPersonController(this, cam, properties.getRenderer());
		timer = Timer.getTimer(properties.getRenderer());

		input.getKeyBindingManager().set("Alpha+", KeyInput.KEY_PERIOD);
		input.getKeyBindingManager().set("Alpha-", KeyInput.KEY_COMMA);
	}

	protected void initGame() {
		tintNode = new Node("tintNode");
		scene = new Node("scene");
		alpha = 0.8f;

		AlphaState as1 = display.getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		as1.setEnabled(true);

		TextureState ts1 = display.getRenderer().getTextureState();
		ts1.setEnabled(true);
		ts1.setTexture(
			TextureManager.loadTexture(
				TestTint.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.jpg"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));

		TextureState font = display.getRenderer().getTextureState();
		font.setEnabled(true);
		font.setTexture(
			TextureManager.loadTexture(
				TestTint.class.getClassLoader().getResource(
					"jmetest/data/Font/font.png"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));

		ZBufferState zEnabled = display.getRenderer().getZBufferState();
		zEnabled.setEnabled(true);

		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

		box = new Box("Box", min.mult(5), max.mult(5));
		box.setRenderState(ts1);
		box.setLocalTranslation(new Vector3f(0, 0, 0));

		tint = new Tint("tint", new ColorRGBA(1, 0, 0, alpha));
		tint.setRenderState(as1);

		instructions =
			new Text("Instructions", "WASD to move, < and > to change alpha");
		instructions.setRenderState(font);
		instructions.setRenderState(as1);

		scene.setRenderState(zEnabled);
		scene.attachChild(box);
		scene.attachChild(instructions);

		tintNode.attachChild(tint);
	}

	protected void reinit() {
	}

	protected void cleanup() {
	}

	public static void main(String[] args) {
		LoggingSystem.getLogger().setLevel(Level.ALL);
		TestTint app = new TestTint();
		app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
}
