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

import java.util.logging.Level;

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * @author Arman Ozcelik
 *
 */
public class LoadingScene implements Scene {

	private int status;
	private float timeElapsed;
	private Text text;
	private Timer timer;
	private SceneEnabledGame demo;
	private Node loadingNode;
	private static boolean firstTime= true;

	public void init(SceneEnabledGame game) {
		this.demo= game;
		timer= game.getTimer();
		loadingNode= new Node();
		loadingNode.updateGeometricState(0.0f, true);
		text= new Text("Loading Scene");
		text.setLocalTranslation(new Vector3f(1, 60, 0));
		TextureState ts= demo.getDisplaySystem().getRenderer().getTextureState();
		ts.setEnabled(true);
		ts.setTexture(
			TextureManager.loadTexture(
				"C:/eclipse/workspace/JavaMonkeyEngine/jme/data/Font/font.png",
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true));
		text.setRenderState(ts);
		AlphaState as1= demo.getDisplaySystem().getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);
		text.setRenderState(as1);
		loadingNode.attachChild(text);
		status= READY;

	}

	public int getStatus() {
		return status;
	}

	public boolean update() {
		timeElapsed += timer.getTimePerFrame();
		if (timeElapsed > 1) {
			timeElapsed= 0;
			text.print(text.getText().toString() + ".");

		}
		timer.update();
		return true;
	}

	public boolean render() {
		if (status == READY) {
			status= LOAD_NEXT_SCENE;
		}
		demo.getDisplaySystem().getRenderer().clearBuffers();
		demo.getDisplaySystem().getRenderer().draw(loadingNode);
		return true;
	}

	public void cleanup() {
		loadingNode= null;
	}

	/* (non-Javadoc)
	 * @see com.jme.test.demo.Scene#getSceneClassName()
	 */
	public String getSceneClassName() {
		return "com.jme.test.demo.LoadingScene";
	}

	/* (non-Javadoc)
	 * @see com.jme.test.demo.Scene#getLinkedSceneClassName()
	 */
	public String getLinkedSceneClassName() {
		return "com.jme.test.demo.SoundPlayingScene";
	}

	/* (non-Javadoc)
	 * @see com.jme.test.demo.Scene#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status= status;
	}

	public void finalize() {
		LoggingSystem.getLogger().log(Level.INFO, "Finalizing " + getClass().getName());
	}

}
