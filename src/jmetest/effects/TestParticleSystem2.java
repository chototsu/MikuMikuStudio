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

import com.jme.app.SimpleGame;
import com.jme.effects.ParticleController;
import com.jme.effects.ParticleSystem;
import com.jme.image.Texture;
import com.jme.input.InputController;
import com.jme.input.NodeController;
import com.jme.intersection.CollisionDetection;
import com.jme.intersection.CollisionResults;
import com.jme.math.Rectangle;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Box;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * @author Ahmed
 */
public class TestParticleSystem2 extends SimpleGame {

	private ParticleSystem ps;
	private ParticleController pc;

	private Node root, scene;

	private Camera cam;
	private CameraNode camNode;

	private Timer timer;
	private InputController input;

	private Text fps, col;

	private TriMesh box;

	public static void main(String[] args) {
		TestParticleSystem2 app = new TestParticleSystem2();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void update(float interpolation) {
		timer.update();
		input.update(timer.getTimePerFrame() * 10);

		fps.print("FPS: " + (int) timer.getFrameRate());
		root.updateWorldData(timer.getTimePerFrame() * 10);

		CollisionResults cs = new CollisionResults();
		CollisionDetection.hasCollision(ps, root, cs);

		if (cs.getNumber() > 0) {
			col.print("Collided:  YES " + cs.getNumber());
		} else if (cs.getNumber() <= 0) {
			col.print("Collided:  NO");
		}

	}

	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(root);
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

			cam =
				display.getRenderer().getCamera(
					properties.getWidth(),
					properties.getHeight());
		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}

		display.getRenderer().setBackgroundColor(new ColorRGBA(0, 0, 0, 1));

		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);

		Vector3f loc = new Vector3f(10, 0, 0);
		Vector3f left = new Vector3f(0, -1, 0);
		Vector3f up = new Vector3f(0, 0, 1f);
		Vector3f dir = new Vector3f(-1, 0, 0);
		cam.setFrame(loc, left, up, dir);

		display.getRenderer().setCamera(cam);

		camNode = new CameraNode("Camera node", cam);

		timer = Timer.getTimer(properties.getRenderer());
		input = new NodeController(this, camNode, properties.getRenderer());
        display.setTitle("Snow");
	}

	protected void initGame() {
		root = new Node("Scene graph Root");
		scene = new Node("3D Scene");
		
		FogState fog = display.getRenderer().getFogState();
		fog.setColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 0.5f));
		fog.setDensity(0.3f);
		fog.setDensityFunction(FogState.DF_LINEAR);
		fog.setApplyFunction(FogState.AF_PER_PIXEL);
		fog.setStart(5f);
		fog.setEnd(25f);
		fog.setEnabled(true);

		ZBufferState zEnabled = display.getRenderer().getZBufferState();
		zEnabled.setEnabled(true);

		AlphaState as1 = display.getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		as1.setEnabled(true);

		TextureState ts = display.getRenderer().getTextureState();
		ts.setTexture(
			TextureManager.loadTexture(
				TestParticleSystem2.class.getClassLoader().getResource(
					"jmetest/data/texture/snowflake.png"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		ts.setEnabled(true);

		TextureState font = display.getRenderer().getTextureState();
		font.setTexture(
			TextureManager.loadTexture(
				TestParticleSystem2.class.getClassLoader().getResource(
					"jmetest/data/Font/font.png"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		font.setEnabled(true);

		TextureState mojo = display.getRenderer().getTextureState();
		mojo.setTexture(
			TextureManager.loadTexture(
				TestParticleSystem2.class.getClassLoader().getResource(
					"jmetest/data/images/Monkey.jpg"),
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		mojo.setEnabled(true);

		ps = new ParticleSystem("Particle System",1000);
		ps.setStartColor(new ColorRGBA(0f, 1f, 0f, 0.9f));
		ps.setEndColor(new ColorRGBA(0f, 1f, 0f, 0.0f));
		ps.setSize(0.2f, 0.1f);
		ps.setGravity(new Vector3f(0, -25, 0));
		ps.setSpeed(1f);
		ps.setFriction(3f);
		ps.setFade(0.01f);
		ps.setGeometry(
			new Rectangle(
				new Vector3f(-25, 0, 0),
				new Vector3f(25, 0, 0),
				new Vector3f(25, 0, 5)));
		ps.setLocalTranslation(new Vector3f(0, 8, 10));

		pc = new ParticleController(ps);
		pc.setRepeatType(Controller.RT_WRAP);
		ps.addController(pc);
		ps.setRenderState(as1);
		ps.setRenderState(ts);
		
		fps = new Text("FPS counter","");
		fps.setRenderState(as1);
		fps.setRenderState(font);
		
		col = new Text("Collisions","");
		col.setLocalTranslation(new Vector3f(0, 20, 0));
		col.setRenderState(as1);
		col.setRenderState(font);
		
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);
		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		box = new Box("Box",min.mult(10), max.mult(10));
		box.setLocalTranslation(new Vector3f(-0.5f, 0, 0));
		box.setRenderState(mojo);
		
		camNode.attachChild(ps);
		camNode.setLocalTranslation(new Vector3f(0, 0, -25));
		
		scene.setRenderState(zEnabled);
		scene.setRenderState(fog);
		scene.attachChild(box);
		scene.attachChild(camNode);
		root.attachChild(scene);
		root.attachChild(fps);
		root.attachChild(col);
		root.updateGeometricState(0.0f, true);

	}

	protected void reinit() {
	}

	protected void cleanup() {
	}
}
