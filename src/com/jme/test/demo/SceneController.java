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
 * Created on 20 déc. 2003
 *
 */
package com.jme.test.demo;

/**
 * @author Arman Ozcelik
 *
 */
public class SceneController {

	private SceneEnabledGame game;
	private String initialScene;
	private Scene playingScene;
	private Scene nextScene;

	private boolean preloadStarted;

	public SceneController(SceneEnabledGame game) {
		this.game= game;
	}

	public void init(String startingSceneClassName) {
		initialScene= startingSceneClassName;
		try {
			playingScene= (Scene)Class.forName(initialScene).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		playingScene.init(game);

	}

	public void update() {
		game.getInput().update(1);
		if (playingScene.getStatus() == Scene.READY
			|| playingScene.getStatus() == Scene.LOAD_NEXT_SCENE
			|| playingScene.getStatus() == Scene.LOADING_NEXT_SCENE) {
			playingScene.update();
		}
		if (playingScene.getStatus() == Scene.LOAD_NEXT_SCENE) {
			playingScene.setStatus(Scene.LOADING_NEXT_SCENE);
			preloadNextScene();
		}
		if (nextScene != null) {
			nextScene.update();
			if (nextScene.getStatus() == Scene.READY) {
				playingScene= nextScene;
				nextScene= null;
			}
		}

	}

	public void render() {
		playingScene.render();
	}

	public void preloadNextScene() {
		if (!preloadStarted) {
			preloadStarted= true;
			Runnable r= new Runnable() {
				public void run() {
					try {
						nextScene= (Scene)Class.forName(playingScene.getLinkedSceneClassName()).newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					nextScene.init(game);

				}
			};
			Thread t= new Thread(r);
			t.start();
		}

	}

}
