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
 * Created on 17 déc. 2003
 *
 */
package com.jme.test.demo;

	
	
/**
 * @author Arman Ozcelik
 *
 */
public interface Scene {
	
	public static final int READY=1;
	public static final int END=2;
	public static final int LOAD_NEXT_SCENE=3;
	public static final int LOADING_NEXT_SCENE=4;
	
	/**
	 * Initializes the game
	 * @param game
	 */
	public void init(SceneEnabledGame game);
	
	/**
	 * Used to know if the scene has been initialized or the scene ended 
	 * @return the scene status 
	 */
	
	
	public int getStatus();
	
	public void setStatus(int status);
	
	/**
	 * Updates the scene. 
	 * @returns false if the scene is not ready
	 *
	 */
	public boolean update();
	
	/**
	 * Renders the scene
	 * @returns false if the scene is not ready
	 */
	public boolean render();
	
	/**
	 * Cleans up the scene by freeing memory
	 *
	 */
	public void cleanup();
	
	
	public String getSceneClassName();
	
	public String getLinkedSceneClassName();
	
	

}
