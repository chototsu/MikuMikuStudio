/*
 * Created on Jan 20, 2004
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
package com.jme.test.effects;

import com.jme.app.SimpleGame;
import com.jme.effects.ParticleController;
import com.jme.effects.ParticleSystem;
import com.jme.image.Texture;
import com.jme.input.InputController;
import com.jme.input.InputSystem;
import com.jme.input.NodeController;
import com.jme.intersection.CollisionDetection;
import com.jme.intersection.CollisionResults;
import com.jme.math.Line;
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

	private Node root;

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

		fps.print("FPS: " + (int)timer.getFrameRate());
		root.updateWorldData(timer.getTimePerFrame() * 10);

		CollisionResults cs = new CollisionResults();
		CollisionDetection.hasCollision(ps, root, cs);

		if (cs.getNumber() > 0) {
			col.print("Collided:  YES");
		}else if (cs.getNumber() <= 0) {
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

		display.getRenderer().setBackgroundColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1));

		cam.setFrustum(1f, 1000f, -0.55f, 0.55f, 0.4125f, -0.4125f);

		Vector3f loc = new Vector3f(10, 0, 0);
		Vector3f left = new Vector3f(0, -1, 0);
		Vector3f up = new Vector3f(0, 0, 1f);
		Vector3f dir = new Vector3f(-1, 0, 0);
		cam.setFrame(loc, left, up, dir);

		display.getRenderer().setCamera(cam);
		
		camNode = new CameraNode(cam);

		timer = Timer.getTimer(properties.getRenderer());
		input = new NodeController(this, camNode, properties.getRenderer());
		input.setMouseSpeed(0.2f);
		input.setKeySpeed(1f);

		InputSystem.createInputSystem(properties.getRenderer());
	}

	protected void initGame() {
		root = new Node();
		
		ZBufferState zEnabled = display.getRenderer().getZBufferState();
		zEnabled.setEnabled(true);
		
		FogState fog = display.getRenderer().getFogState();
		fog.setEnabled(true);
		fog.setColor(display.getRenderer().getBackgroundColor());
		fog.setDensity(1f);
		fog.setStart(1f);
		fog.setEnd(20f);
		fog.setApplyFunction(FogState.AF_PER_PIXEL);

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
				"data/texture/snowflake.png",
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		ts.setEnabled(true);

		TextureState font = display.getRenderer().getTextureState();
		font.setTexture(
			TextureManager.loadTexture(
				"data/Font/font.png",
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		font.setEnabled(true);
		
		TextureState monk = display.getRenderer().getTextureState();
		monk.setTexture(
				TextureManager.loadTexture(
						"data/Images/Monkey.jpg",
						Texture.MM_LINEAR,
						Texture.FM_LINEAR,
						true));
		monk.setEnabled(true);

		ps = new ParticleSystem(1000);
		ps.setStartColor(new ColorRGBA(1f, 1f, 1f, 0.9f));
		ps.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.1f));
		ps.setStartSize(0.2f);
		ps.setEndSize(0.1f);
		ps.setGravity(new Vector3f(0, -50, 0));
		ps.setSpeed(1f);
		ps.setFriction(3f);
		ps.setFade(0.01f);
		ps.useGeometry(true);
		ps.setGeometry(new Line(new Vector3f(-25, 0, 0), new Vector3f(25, 0, 0)));
		ps.setLocalTranslation(new Vector3f(0, 5, 10));

		pc = new ParticleController(ps);
		pc.setRepeatType(Controller.RT_WRAP);
		ps.addController(pc);
		ps.setRenderState(as1);
		ps.setRenderState(ts);
		ps.setName("Particle System");

		fps = new Text("");
		fps.setRenderState(as1);
		fps.setRenderState(font);
		fps.setName("FPS Counter");
		
		col = new Text("");
		col.setLocalTranslation(new Vector3f(0, 20, 0));
		col.setRenderState(as1);
		col.setRenderState(font);
		col.setName("Collisions");
		
		Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);
		Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
		box = new Box(min.mult(10), max.mult(10));
		box.setRenderState(monk);
		box.setLocalTranslation(new Vector3f(-0.5f, 0, 0));
		box.setName("Box");
		
		camNode.attachChild(ps);
		camNode.setLocalTranslation(new Vector3f(0, 0, -10));
		
		root.setRenderState(zEnabled);
		root.setRenderState(fog);
		root.attachChild(fps);
		root.attachChild(col);
		root.attachChild(box);
		root.attachChild(camNode);
		root.updateGeometricState(0.0f, true);

	}

	protected void reinit() {
	}

	protected void cleanup() {
	}
}
