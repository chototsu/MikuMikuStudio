/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
 * Created on 25 janv. 2004
 *
 */
package jmetest.sound;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.sound.scene.SoundNode;
import com.jme.sound.scene.SphericalSound;
import com.jme.sound.SoundAPIController;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;

/**
 * @author Arman Ozcelik
 *
 */
public class TestSoundGraph extends BaseGame {

	private Node scene;
	private SoundNode snode;
	private Camera cam;
	SphericalSound footsteps;
	Box box;

	private InputHandler input;

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#update()
	 */
	protected void update(float interpolation) {
		input.update(1);
		snode.updateGeometricState(0.0f, true);

	}

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#render()
	 */
	protected void render(float interpolation) {
		display.getRenderer().clearBuffers();
		display.getRenderer().draw(scene);
		SoundAPIController.getRenderer().draw(snode);

	}

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#initSystem()
	 */
	protected void initSystem() {
		try {
			display= DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(
				properties.getWidth(),
				properties.getHeight(),
				properties.getDepth(),
				properties.getFreq(),
				properties.getFullscreen());
			cam= display.getRenderer().getCamera(properties.getWidth(), properties.getHeight());

		} catch (JmeException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
		input= new FirstPersonHandler(this, cam, "LWJGL");
		input.setMouseSpeed(0.2f);
		input.setKeySpeed(.1f);
		SoundAPIController.getSoundSystem("LWJGL");
		SoundAPIController.getRenderer().setCamera(cam);


	}

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void initGame() {
		scene= new Node("3D Scene Node");
		Vector3f max= new Vector3f(5, 5, 5);
		Vector3f min= new Vector3f(-5, -5, -5);
		box= new Box("Box", min, max);
		box.setModelBound(new BoundingSphere());
		box.updateModelBound();
		box.setLocalTranslation(new Vector3f(0, 0, -50));
		TextureState tst= display.getRenderer().getTextureState();
		tst.setEnabled(true);
		tst.setTexture(
			TextureManager.loadTexture(
                TestSoundGraph.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
		scene.setRenderState(tst);
		ZBufferState buf= display.getRenderer().getZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);
		scene.setRenderState(buf);
		scene.attachChild(box);
		cam.update();

		snode=new SoundNode();
		footsteps=new SphericalSound(TestSoundGraph.class.getClassLoader().getResource("jmetest/data/sound/Footsteps.wav"));
		footsteps.getSource().setMaxDistance(100);
		footsteps.getSource().setRolloffFactor(.1f);
		footsteps.getSource().setPosition(box.getLocalTranslation());
		footsteps.getSource().setGain(1.0f);
		snode.attachChild(footsteps);



		scene.updateGeometricState(0.0f, true);
		snode.updateGeometricState(0.0f, true);
                scene.updateRenderState();

	}

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#reinit()
	 */
	protected void reinit() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.jme.app.SimpleGame#cleanup()
	 */
	protected void cleanup() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		TestSoundGraph app= new TestSoundGraph();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

}
