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
 
package test.keycontroller;
 
import org.lwjgl.Display; 
import org.lwjgl.opengl.GL; 

import jme.AbstractGame; 
import jme.system.DisplaySystem;
import jme.entity.camera.Camera;
import jme.entity.Entity;
import jme.utility.Timer;
import jme.math.Vector;
import jme.geometry.hud.text.Font2D;
import jme.geometry.primitive.Triangle;



/**
 * <code>TestKeyController.java</code> provides a very basic jME OpenGL construct
 * This code generates a basic OpenGL window with a black background. This contruct
 * also takes keyboard input. By pressing different keys you can invoke different
 * function. By pressing the 'ESC' key, you will exit the program.
 *  
 * @author Samuel Wasson
 * @version $Id
 */

public class TestKeyController extends AbstractGame {
	
	private Timer timer;
	private Entity entity1;
	private Camera camera;
	private Font2D font;
	private Triangle triangle;
	private Vector[] points;
	int Flag, entity_1 = 1;
	private KeyController controller;

	/**
	 * This is where we'll do any updating.
	 */
	protected void update() {
		if(!controller.update(timer.getFrameRate())) { 
		finish();
		}
		timer.update();
	}
	
	/**
	 * Set the Flag with the value for what object will be rendered
	 */
	protected void setRenderObject(int value) {
	 Flag = value;
	}
	
	/**
	 * Render is called once per frame to display the data.
	 */
	protected void render() {
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		gl.loadIdentity();
		
		controller.render();
		
		// Enable texture mapping
		gl.enable(GL.TEXTURE_2D);

		if(Flag == 1) {
		  gl.pushMatrix();
		  entity1.render();
		  gl.popMatrix();
		}
		
		gl.end();
		
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
			"data/Images/Monkey.jpg",
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
		font = new Font2D("data/Font/font.png");
		
		 // Instantiate a new timer object
		timer = Timer.getTimer();
		
		// Instantiate a new camera
		camera = new Camera(1,0,0,10,0,0,0,0,1,0);
		
		// Instantiate a new controller
		controller = new KeyController(camera, this);
		
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
		
		// initialize the pyramid entity
		entity1 = new Entity(entity_1);
		entity1.setVisibilityType(Entity.VISIBILITY_CUBE);
		entity1.setPosition(new Vector(0.0f, 0.0f, 0.0f));
		entity1.setGeometry(triangle);
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
	   TestKeyController testPoly = new TestKeyController(); 
	   testPoly.start(); 
	}	
}