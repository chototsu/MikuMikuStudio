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
 
package test.polygon;
 
import org.lwjgl.Display; 
import org.lwjgl.opengl.GL; 

import jme.AbstractGame; 
import jme.system.DisplaySystem;
import jme.utility.Timer;
import jme.math.Vector;
import jme.geometry.hud.text.Font2D;
import jme.geometry.primitive.Triangle;

/**
 * <code>TestPolygon.java</code> provides a very basic jME OpenGL construct
 * This code generates a basic OpenGL window with a black background.
 * You will can use this basic construct to build more complex jME applications
 *  
 * @author Samuel Wasson
 * @version 0.1.0
 */

public class TestPolygon extends AbstractGame {
	
	private Timer timer;
	private Font2D font;
	private Triangle triangle;
	private Vector[] points;

	/**
	 * This is where we'll do any updating.
	 */
	protected void update() {
	// Update each frame to calculate the framerate
	timer.getFrameRate();
	timer.update();
	}
	
	/**
	 * Render is called once per frame to display the data.
	 */
	protected void render() {
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		gl.loadIdentity();
		

		// Enable texture mapping
		gl.enable(GL.TEXTURE_2D);

		triangle.render();
		
		/*
		 * Print out the frame rate.
		 * The print method takes 4 parameters 
		 * (int, int, string, int). The first two 
		 * ints are the coordinates to start the text. 
		 * These are screen coordinates in pixels where 
		 * (0, 0) is the lower left hand of the screen. 
		 * The String is whatever you want to display. 
		 * The last parameter is the font type where 0 
		 * is normal and 1 is italics
		 */
		font.print(1,1,"Frame Rate - " + timer.getFrameRate(), 0);

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
		

		 // Define the clear color to be black
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

	protected void initSystem() {
		initDisplay();
		initGL();
	}

	protected void initGame() {

		 // Blend the font together so it doesn't chop the letters off 
		gl.blendFunc(GL.SRC_ALPHA, GL.ONE);
		
		 //Instantiate a font object
		font = new Font2D("jme/data/Font/font.png");
		
		 // Instantiate a new timer object
		timer = Timer.getTimer();
		
		 // Initiate a new Vector class
		points = new Vector[3];
		
		 // Initialize the Vector array
		for (int i = 0; i < 3; i++) {
			points[i] = new Vector();
		}
		
		 // Add points to the Vector array
		points[0].x = 0.0f; points[0].y = 1.0f; points[0].z = -5.0f;
		points[1].x = -1.0f; points[1].y = -1.0f; points[1].z = -5.0f;
		points[2].x = 1.0f; points[2].y = -1.0f; points[2].z = -5.0f;
		
		 // Instantiate a new Triangle object
		triangle = new Triangle(points);
	}

	protected void reinit() {
		 // Nothing here yet.
	}

	/**
	 * Clean up the OpenGL resources
	 */
	protected void cleanup() {
		gl.destroy();
	}
	
	public static void main(String[] args) { 
	   TestPolygon testPoly = new TestPolygon(); 
	   testPoly.start(); 
	}	
}