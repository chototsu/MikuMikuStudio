/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
 * Created on 25 jan. 2004
 *  
 */
package jmetest.sound;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.sound.SoundAPIController;
import com.jme.sound.scene.SoundNode;
import com.jme.sound.scene.SphericalSound;
import com.jme.util.TextureManager;
/**
 * @author Arman Ozcelik
 * @version $Id: TestSoundGraph.java,v 1.11 2004-05-14 23:35:49 anakan Exp $
 */
public class TestSoundGraph extends SimpleGame {
	private SoundNode snode;
	SphericalSound footsteps;
	SphericalSound background;
	Box box;
	public static void main(String[] args) {
		TestSoundGraph app = new TestSoundGraph();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
	protected void simpleUpdate() {
		snode.updateGeometricState(0.0f, true);
	}
	protected void simpleRender() {
		SoundAPIController.getRenderer().draw(snode);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("Test Sound Graph");
		SoundAPIController.getSoundSystem(properties.getRenderer());
		SoundAPIController.getRenderer().setCamera(cam);
		Vector3f max = new Vector3f(5, 5, 5);
		Vector3f min = new Vector3f(-5, -5, -5);
		box = new Box("Box", min, max);
		box.setModelBound(new BoundingSphere());
		box.updateModelBound();
		box.setLocalTranslation(new Vector3f(0, 0, -50));
		TextureState tst = display.getRenderer().getTextureState();
		tst.setEnabled(true);
		tst.setTexture(TextureManager.loadTexture(
				TestSoundGraph.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
				Texture.FM_LINEAR, true));
		rootNode.setRenderState(tst);
		rootNode.attachChild(box);
		snode = new SoundNode();
		footsteps = new SphericalSound(TestSoundGraph.class.getClassLoader()
				.getResource("jmetest/data/sound/Footsteps.wav"));
		
		footsteps.getSource().setMaxDistance(100);
		footsteps.getSource().setRolloffFactor(.1f);
		footsteps.getSource().setPosition(box.getLocalTranslation());
		footsteps.getSource().setGain(1.0f);
		
		
		background = new SphericalSound(TestSoundGraph.class.getClassLoader()
				.getResource("jmetest/data/sound/test.ogg"));
		background.getSource().setGain(.06f);
		
		snode.attachChild(footsteps);
		snode.attachChild(background);
		snode.updateGeometricState(0.0f, true);
	}
}