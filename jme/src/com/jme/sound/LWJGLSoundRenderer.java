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
package com.jme.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.openal.AL;


import com.jme.sound.utils.StreamRepository;
import com.jme.sound.utils.SourceRepository;



/**
 * @author Arman Ozcelik
 * @version $Id: LWJGLSoundRenderer.java,v 1.1.1.1 2003-10-29 10:56:42 Anakan Exp $
 */
public class LWJGLSoundRenderer implements SoundRenderer {
	
	private float[] listenerPos = { 0.0f, 0.0f, 0.0f };
	//Velocity of the listener.
	private float[] listenerVel = { 0.0f, 0.0f, 0.0f };
	//Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	private float[] listenerOri = { 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f };

	public LWJGLSoundRenderer() {
		setListenerValues();
	}

	private void setListenerValues() {
		AL.alListener3f(AL.AL_POSITION, listenerPos[0], listenerPos[1], listenerPos[2]);
		AL.alListener3f(AL.AL_VELOCITY, listenerVel[0], listenerVel[1], listenerVel[2]);
		AL.alListener3f(AL.AL_ORIENTATION, listenerOri[0], listenerOri[1], listenerOri[2]);
	}

	

	public void addSoundPlayer(Object name){
		IntBuffer source=ByteBuffer.allocateDirect(4 ).order(ByteOrder.nativeOrder()).asIntBuffer();
		AL.alGenSources(source);
		SourceRepository.getRepository().bind(name, new LWJGLSource(source.get(0)));
	}
		
	
	
	public void loadSoundAs(String name, String file){
		StreamRepository.getInstance().bind(name, file);
	}

	
	public SoundSource getSoundPlayer(Object name) {
		return SourceRepository.getRepository().getSource(name);
	}
	

}
