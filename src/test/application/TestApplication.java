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
 
package test.application;
 
import org.lwjgl.Display; 
import org.lwjgl.opengl.GL; 

import jme.AbstractGame; 
import jme.system.DisplaySystem;

/**
 * <code>TestApplication</code> provides a very basic jME OpenGL construct
 * This code generates a basic OpenGL window with a black background.
 * You will can use this basic construct to build more complex jME applications
 *  
 * @author Samuel Wasson
 * @version $Id: TestApplication.java,v 1.2 2003-08-08 02:16:56 mojomonkey Exp $
 */

//Edit: Mark Powell - altered comments slightly. 8/7/03

public class TestApplication extends AbstractGame {
    /**
     * This is where we'll do any updating
     */
    protected void update() {
	}
	
    /**
     * Render is called once per frame to display the data.
     */
	protected void render() {
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		gl.loadIdentity();
	}
	
    /** 
     * This is where we create and initialize the window.
     */
	protected void initDisplay() {
		
		DisplaySystem.createDisplaySystem(
			"TestApplication", 
			"jme/data/Images/Monkey.jpg",
			true
		);
	}
	
	protected void initGL() {

		 // Here we create the OpenGL bindings.
		 
		gl = DisplaySystem.getDisplaySystem().getGL();
		glu = DisplaySystem.getDisplaySystem().getGLU();
		
		
		//Define the clear color to be black
				
		gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);

		gl.matrixMode(GL.PROJECTION);
		gl.loadIdentity();
		
		
		 // Calculate the aspect ratio
		glu.perspective(
			45.0f,
			(float)Display.getWidth() / (float)Display.getHeight(),
			0.01f,
			750.0f);
		
		gl.matrixMode(GL.MODELVIEW);
		gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);		
	}

    /**
     * Called first to initialize system attributes.
     */
	protected void initSystem() {
		initDisplay();
		initGL();
	}
    /** 
     * Nothing here yet.
     */
	protected void initGame() {
	}

    /** 
     * Nothing here yet.
     */
	protected void reinit() {
	}

    /**
     * Clean up the OpenGL resources
     */
	protected void cleanup() {
		gl.destroy();
	}

    /**
     * <code>main</code> entry point for application.
     * @param args comman line arguments, none used.
     */	
	public static void main(String[] args) { 
	   TestApplication testApp = new TestApplication(); 
	   testApp.start(); 
	}	
}
	