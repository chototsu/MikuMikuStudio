/*
 * Created on Jun 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.general;

/**
 * Nehe32.java
 *
 * Author: Thomas Hourdel (thomas.hourdel@libertysurf.fr)
 * Date: 16/01/2003
 *
 * Port of the NeHe OpenGL Tutorial (Lesson 32: "MilkshapeModel Loading")
 * to Java using the LWJGL interface to OpenGL.
 */


/** Standard includes
 */

import jme.geometry.hud.text.Font2D;
import jme.geometry.model.md3.Md3Model;
import jme.geometry.model.ms.MilkshapeModel;
import jme.geometry.primitive.Pyramid;
import jme.system.DisplaySystem;
import jme.utility.Timer;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.vector.Vector3f;
import org.lwjgl.input.*;


/** Main class
 */

public final class MilkshapeTest
{

	private Pyramid p;

    public Md3Model model;

		  // GL variables...
	public GL gl;						// The Opengl Context
	public GLU glu;

	public Font2D font;
	public Timer timer;


	/** Global variable declaration
	 */

	private boolean finished;

	private  float yrot;									// Y Rotation
	private MilkshapeModel pModel;						// Memory To Hold The MilkshapeModel




	/** Constructor (unused here)
	 */

	private MilkshapeTest()
	{
		DisplaySystem.createDisplaySystem(640,480,32,false,"water");
		gl = DisplaySystem.getDisplaySystem().getGL();
		glu = DisplaySystem.getDisplaySystem().getGLU();
		
		try
				{
					init();												// Init Opengl

					while(!finished)
					{
						Keyboard.poll();								// Poll The Keyboard
						mainLoop();										// Launch The Main Loop
						render();										// Render To Screen
						gl.paint();
							gl.tick();						// Swap Opengl Buffers
					}   
				}
				catch(Throwable t)
				{
				   t.printStackTrace();
				}
				finally
				{
					cleanup();
				}
	}



	/** Main method
	 */

	public static void main(String args[])
	{
		MilkshapeTest mst = new MilkshapeTest();
	}



	/** Init opengl
	 */

	private void init() throws Exception
	{
		Keyboard.create();										// Create The Keyboard
		font = new Font2D("jme/data/font.png");
		timer = Timer.getTimer();
		pModel = new MilkshapeModel("jme/data/tris.ms3d");					// Create new MilkshapeModel
        model = new Md3Model("jme/data/Paladin","Paladin","railgun");
        model.setScale(new Vector3f(1.0f,1.0f,1.0f));
        model.setTorsoAnimation("TORSO_STAND");
        model.setLegsAnimation("LEGS_WALK");

        p = new Pyramid(40,50);
        p.setTexture("jme/data/lara/default.bmp");

        gl.enable(GL.TEXTURE_2D);								// Enable Texture Mapping
		gl.shadeModel(GL.SMOOTH);								// Enable Smooth Shading
		gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);					// Black Background
		gl.clearDepth(1.0);										// Depth Buffer Setup
		gl.enable(GL.DEPTH_TEST);								// Enables Depth Testing
		gl.depthFunc(GL.LEQUAL);								// The Type Of Depth Testing To Do

		gl.matrixMode(GL.PROJECTION);							// Select The Projection Matrix
		gl.loadIdentity();										// Reset The Projection Matrix
		  // Calculate The Aspect Ratio Of The Window
		glu.perspective(45.0f, (float)Display.getWidth() / (float)Display.getHeight(), 0.1f, 500.0f);
		gl.matrixMode(GL.MODELVIEW);							// Select The Modelview Matrix

		gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);		// Really Nice Perspective Calculations

		GL.wglSwapIntervalEXT(0);
	}



	/** Main loop
	 */

	private void mainLoop()
	{
		timer.update();
		processKeyboard();										// Get Keyboard Events
	}



	/** Rendering method
	 */

	private void render()
	{
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);	// Clear Screen And Depth Buffer
		gl.loadIdentity();										// Reset The Current Modelview Matrix

		glu.lookAt( 75, 75, 125, 0, 55, 0, 0, 1, 0 );				// (3) Eye Postion (3) Center Point (3) Y-Axis Up Vector

		
		//pModel.render();											// Draw The MilkshapeModel
		model.render();
        //p.render();
        font.print(10,10,"FPS - " + timer.getFrameRate(), 0);
		
	}



	/** Process keyboard events
	 */
	
	private void processKeyboard()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
	}



	/** Cleanup
	 */

	private void cleanup()
	{
		Keyboard.destroy();										// Destroy The Keyboard
		gl.destroy();											// Destroy The Opengl Context
	}
}