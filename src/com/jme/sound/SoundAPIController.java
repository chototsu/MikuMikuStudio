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
 * Created on 24 janv. 2004
 *
 */
package com.jme.sound;



/**
 * @author Arman Ozcelik
 *
 */
public class SoundAPIController {

	private static ISoundSystem soundSystem;
	private static String api;
	private static ISoundRenderer renderer;

	public SoundAPIController() {

	}

	public static ISoundSystem getSoundSystem(String apiName) {
		api= apiName;
		if (api.equals("LWJGL")) {
			soundSystem= new com.jme.sound.lwjgl.SoundSystem();
			renderer= new com.jme.sound.lwjgl.SoundRenderer();

		}
		if (api.equals("JOAL")) {
			soundSystem= new com.jme.sound.joal.SoundSystem();
			renderer= new com.jme.sound.joal.SoundRenderer();
		}
		return soundSystem;

	}

	public static ISoundRenderer getRenderer() {
		return renderer;
	}

	public static ISoundSystem getSoundSystem() {
		return soundSystem;
	}

	public static void plugExternal(ISoundSystem externalAPI, ISoundRenderer externalRenderer) {
		if (externalAPI != null) {
			soundSystem= externalAPI;
			renderer= externalRenderer;
			api= soundSystem.getAPIName();
		}
	}

}
