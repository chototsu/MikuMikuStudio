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

import com.jme.entity.Entity;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.sound.IEffectPlayer;
import com.jme.sound.IRenderer;
import com.jme.sound.utils.EffectRepository;
import com.jme.sound.utils.OnDemandSoundLoader;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * @author Arman Ozcelik
 *
 */
public class DemoScene {

	private int frameCount;
	private int sceneNumber;
	private JmeDemo demo;
	private boolean[] sceneLoaded= new boolean[3];
	private IRenderer soundRenderer;
	private Entity backgroundMusic, e;
	private Node playingScene;
	private Node loadingNode;
	private Node soundNode;

	private Text text, soundText;
	private float timeElapsed;

	private Timer timer;

	private Vector3f soundPosition= new Vector3f(-25, 0, 0);
	private Vector3f soundVelocity= new Vector3f(0.1f, 0, 0.0f);
	public static final int LOADING= 0;
	public static final int SOUND= 1;

	private OnDemandSoundLoader soundLoader;

	public DemoScene(JmeDemo game) {
		demo= game;
		timer= Timer.getTimer("LWJGL");
	}

	public void update(int scene) {
		demo.getInput().update(1);
		switch (scene) {
			case LOADING :
				if (sceneLoaded[LOADING]) {
					updateLoadingScene();
					if(!sceneLoaded[scene+1]){
						loadSoundScene();
					}
				} else {
					loadLoadingScene();
				}
			case SOUND :
				if (sceneLoaded[SOUND]) {
					updateSound();
				}
					
				
		}

	}

	/**
	 * 
	 */
	private void loadSoundScene() {		
			backgroundMusic= new Entity("BACKGROUND");
			soundLoader= new OnDemandSoundLoader(10);
			soundLoader.start();
			soundLoader.queueSound(backgroundMusic.getId(), "../data/sound/0.mp3");
			sceneLoaded[SOUND]= true;
			sceneNumber++;
	}

	private void updateSound() {
		timeElapsed += timer.getTimePerFrame();
		if (timeElapsed > 0.5) {
			timeElapsed= 0;
			if (!sceneLoaded[sceneNumber + 1]){
				text.print(text.getText().toString() + ".");
			}
		}
		timer.update();
		if (!sceneLoaded[sceneNumber + 1]
			&& EffectRepository.getRepository().getSource(backgroundMusic.getId()) != null) {
			soundRenderer= demo.getSoundSystem().getRenderer();
			soundRenderer.addSoundPlayer(backgroundMusic);
			soundRenderer.getSoundPlayer(backgroundMusic).setPosition(soundPosition);
			soundRenderer.getSoundPlayer(backgroundMusic).setVelocity(soundVelocity);
			soundRenderer.getSoundPlayer(backgroundMusic).setMaxDistance(25.0f);
			sceneLoaded[sceneNumber + 1]= true;
			text.print("Sound Scene");
			soundNode=new Node();
			soundNode.attachChild(text);
			soundNode.attachChild(soundText);
			playingScene=soundNode;
		}
		if (sceneLoaded[sceneNumber + 1]) {

			if (soundRenderer.getSoundPlayer(backgroundMusic).getStatus() != IEffectPlayer.LOOPING) {
				soundRenderer.getSoundPlayer(backgroundMusic).loop(
					EffectRepository.getRepository().getSource(backgroundMusic.getId()));
			}
			soundPosition.x += 0.09;
			soundPosition.y += 0.001;
			soundPosition.z += 0.01;
			soundRenderer.getSoundPlayer(backgroundMusic).setPosition(soundPosition);
			soundText.print("Position " + soundPosition);
		}

	}

	private void loadLoadingScene() {
		loadingNode= new Node();
		loadingNode.updateGeometricState(0.0f, true);
		text= new Text("Loading Scene");
		soundText= new Text("");
		text.setLocalTranslation(new Vector3f(1, 60, 0));
		TextureState ts= demo.getDisplay().getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(
			TextureManager.loadTexture("../data/Font/font.png", Texture.MM_LINEAR, Texture.FM_LINEAR, true));
		text.setRenderState(ts);
		AlphaState as1= demo.getDisplay().getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		text.setRenderState(as1);
		soundText.setRenderState(ts);
		soundText.setRenderState(as1);
		soundText.setLocalTranslation(new Vector3f(1, 40, 0));
		loadingNode.attachChild(text);
		loadingNode.attachChild(soundText);
		playingScene= loadingNode;
		sceneLoaded[LOADING]= true;
	}

	public void render() {
		demo.getDisplay().getRenderer().clearBuffers();
		demo.getDisplay().getRenderer().draw(playingScene);
	}

	public int getScene() {
		return sceneNumber;
	}

	private void updateLoadingScene() {
		frameCount++;
		timeElapsed += timer.getTimePerFrame();
		if (timeElapsed > 0.5) {
			timeElapsed= 0;
			text.print(text.getText().toString() + ".");
		}
		timer.update();

	}

}
