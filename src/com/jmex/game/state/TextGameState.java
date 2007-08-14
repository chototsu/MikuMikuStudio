/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jmex.game.state;

import com.jme.image.Texture;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.game.StandardGame;

/**
 * <code>TextGameState</code> provides a GameState that can be used to display simple text.
 * This is similar to the typical FPS counter seen in SimpleGame but can be used for any text.
 * 
 * @author Matthew D. Hicks
 */
public class TextGameState extends BasicGameState {
	private static final String FONT_LOCATION = "/com/jme/app/defaultfont.tga";
	
	private Text textObject;
	private Node textNode;
	private String text;

	public TextGameState(String text) {
		super("TextGameState");
		this.text = text;
		
		AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		as.setEnabled(true);
		
		TextureState font = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		font.setTexture(TextureManager.loadTexture(StandardGame.class.getResource(FONT_LOCATION),
						Texture.MM_LINEAR, Texture.FM_LINEAR));
		font.setEnabled(true);
		
		textObject = new Text("Text", "");
		textObject.setTextureCombineMode(TextureState.REPLACE);
		textNode = new Node("TextNode");
		textNode.attachChild(textObject);
		textNode.setRenderState(font);
		textNode.setRenderState(as);
		textNode.updateGeometricState(0.0f, true);
		textNode.updateRenderState();
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public void update(float tpf) {
		textObject.print(text);
	}
	
	public void render(float tpf) {
		DisplaySystem.getDisplaySystem().getRenderer().draw(textNode);
	}
	
	public void cleanup() {
	}
}
