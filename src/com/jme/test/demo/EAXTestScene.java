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
 * Created on 18 janv. 2004
 *
 */
package com.jme.test.demo;

/**
 * @author Arman Ozcelik
 *
 */

import com.jme.entity.Entity;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.sound.IPlayer;
import com.jme.sound.SoundSystem;
import com.jme.sound.filter.LWJGLListenerFilter;
import com.jme.sound.filter.ListenerFilter;
import com.jme.sound.utils.EffectRepository;
import com.jme.sound.utils.OnDemandSoundLoader;

import com.jme.util.TextureManager;
import com.jme.util.Timer;
public class EAXTestScene implements Scene {

	private Node soundNode;
	private Text text;
	private float timeElapsed;
	private Timer timer;
	private SoundSystem soundRenderer;
	private Entity backgroundMusic, e;
	private SceneEnabledGame game;
	private OnDemandSoundLoader soundLoader;
	private int status;
	private TriMesh t;
	private ListenerFilter filter;

	public void init(SceneEnabledGame game) {
		this.game= game;
		timer= game.getTimer();
		text= new Text("Playing sound");
		filter= new LWJGLListenerFilter();

		TextureState ts= game.getDisplaySystem().getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(
			TextureManager.loadTexture("data/Font/font.png", Texture.MM_LINEAR, Texture.FM_LINEAR, true));
		text.setRenderState(ts);

		AlphaState as1= game.getDisplaySystem().getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		text.setRenderState(as1);

		soundRenderer= game.getSoundSystem();
		backgroundMusic= new Entity("BACKGROUND");
		soundRenderer.addSource(backgroundMusic);

		soundRenderer.getPlayer(backgroundMusic).setMaxDistance(30.0f);
		soundLoader= new OnDemandSoundLoader(10);
		soundLoader.start();
		soundLoader.queueSound(backgroundMusic.getId(), "data/sound/Footsteps.wav");
		soundNode= new Node();
		soundNode.attachChild(text);
		status= Scene.LOADING_NEXT_SCENE;
		text.print("No Filter");
		text.setLocalTranslation(new Vector3f(1, 60, 0));
		soundNode.updateGeometricState(0.0f, true);
		text.getLocalTranslation().x= game.getDisplaySystem().getWidth()/2;
		text.getLocalTranslation().y= game.getDisplaySystem().getHeight()/2;

	}

	public int getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see com.jme.test.demo.Scene#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status= status;

	}

	public boolean update() {
		if (soundNode == null) {
			return false;
		}
		if (EffectRepository.getRepository().getSource(backgroundMusic.getId()) != null) {
			status= READY;
		} else {
			return false;
		}
		timer.update();
		timeElapsed += timer.getTimePerFrame();
		
		if (soundRenderer.getPlayer(backgroundMusic).getStatus() != IPlayer.LOOPING) {
			soundRenderer.getPlayer(backgroundMusic).loop(backgroundMusic.getId());
		}
		if (timeElapsed > 5 && timeElapsed < 10) {
			text.print("ROOM FILTER");
			soundRenderer.getPlayer(backgroundMusic).applyFilter(
				filter.getPredefinedFilter(ListenerFilter.ROOM));
		}
		if (timeElapsed > 10 && timeElapsed < 15) {
					text.print("ALLEY FILTER");
					soundRenderer.getPlayer(backgroundMusic).applyFilter(
						filter.getPredefinedFilter(ListenerFilter.ALLEY));
		}

		soundNode.updateGeometricState(0.0f, true);
		return true;
	}

	public boolean render() {
		game.getDisplaySystem().getRenderer().clearBuffers();
		game.getDisplaySystem().getRenderer().draw(soundNode);
		return false;
	}

	public void cleanup() {
		soundRenderer.getPlayer(backgroundMusic).stop();
		EffectRepository.getRepository().remove(backgroundMusic.getId());
		soundNode= null;

	}

	public String getSceneClassName() {
		return "com.jme.test.demo.EAXTestScene";
	}

	public String getLinkedSceneClassName() {
		return "com.jme.test.demo.LoadingScene";
	}

}
