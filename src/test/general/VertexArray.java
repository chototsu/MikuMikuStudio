/*
 * Created on Jun 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package test.general;

/**
 * @author mpowell
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * VertexArray.java
 *
 * Author: Thomas Hourdel (thomas.hourdel@libertysurf.fr)
 * Date: 09/01/2003
 *
 * Show how to use vertex & colors arrays.
 */


/** Standard includes
 */

import java.nio.*;

import jme.system.DisplaySystem;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.input.*;


/** Main class
 */

public final class VertexArray
{

	  // Display mode setup...
	static
		{
			DisplaySystem.createDisplaySystem(640,480,32,false,"water");
		}


		  // GL variables...
		public static final GL gl = DisplaySystem.getDisplaySystem().getGL();						// The Opengl Context
		public static final GLU glu =DisplaySystem.getDisplaySystem().getGLU();					// The Opengl Utility Context

	

	/** Global variable declaration
	 */

	private static boolean finished;

	private static float rot = 0.0f;							// Cube Rotation Angle

	private final static float cube_vertices[] =				// An Array Of Vertices
	{
				-2.0f,	2.0f, 2.0f,								// Front Face Top Left.		0
				 2.0f,	2.0f, 2.0f,								// Front Face Top Right.		1
				 2.0f, -2.0f, 2.0f,								// Front Face Bottom Right.	2
				-2.0f, -2.0f, 2.0f,								// Front Face Bottom Left.	3

				-2.0f,	2.0f, -2.0f,							// Back Face Top Left.		4
				 2.0f,	2.0f, -2.0f,							// Back Face Top Right.		5
				 2.0f, -2.0f, -2.0f,							// Back Face Bottom Right.	6
				-2.0f, -2.0f, -2.0f,							// Back Face Bottom Left		7
	};

	private final static float cube_colors[] =					// An Array Of Colors
	{
				 1.0f,	0.0f,   0.0f,
				 0.0f,	1.0f,   0.0f,
				 0.0f,	0.0f,	1.0f,
				 1.0f,	1.0f,	0.0f,

				 1.0f,	0.0f,	1.0f,
				 0.0f,	1.0f,	1.0f,
				 0.0f,	0.0f,	0.0f,
				 1.0f,	1.0f,	1.0f,
	};

	private final static int cube_indexes[] =
	{
				 0, 3, 2, 1,									// Front Face
				 5, 6, 7, 4,									// Back Face
				 4, 0, 1, 5,									// Top Face
				 3, 7, 6, 2,									// Bottom Face
				 4, 0, 3, 7,									// Left Face
				 1, 2, 6, 5,									// Right Face
	};

	private static FloatBuffer fBuffer_ver = ByteBuffer.allocateDirect(24 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	private static FloatBuffer fBuffer_col = ByteBuffer.allocateDirect(24 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	private static IntBuffer iBuffer_ind = ByteBuffer.allocateDirect(24 * 4).order(ByteOrder.nativeOrder()).asIntBuffer();



	/** Constructor (unused here)
	 */

	private VertexArray()
	{
	}



	/** Main method
	 */

	public static void main(String args[])
	{
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



	/** Init opengl
	 */

	private final static void init() throws Exception
	{
		Keyboard.create();										// Create The Keyboard

		gl.shadeModel(GL.SMOOTH);								// Enable Smooth Shading
		gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);					// Black Background
		gl.clearDepth(1.0);										// Depth Buffer Setup
		gl.enable(GL.DEPTH_TEST);								// Enables Depth Testing
		gl.depthFunc(GL.LEQUAL);								// The Type Of Depth Testing To Do

		gl.matrixMode(GL.PROJECTION);							// Select The Projection Matrix
		gl.loadIdentity();										// Reset The Projection Matrix
		  // Calculate The Aspect Ratio Of The Window
		glu.perspective(45.0f, (float)Display.getWidth() / (float)Display.getHeight(), 0.1f, 100.0f);
		gl.matrixMode(GL.MODELVIEW);							// Select The Modelview Matrix

		gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);		// Really Nice Perspective Calculations

		gl.enableClientState(GL.VERTEX_ARRAY);					// Enable The Vertex Array Usage
		gl.enableClientState(GL.COLOR_ARRAY);					// Enable The Color Array Usage

		fBuffer_ver.put(cube_vertices);							// Put Vertex Array In Buffer
		fBuffer_col.put(cube_colors);							// Put Color Array In Buffer
		iBuffer_ind.put(cube_indexes);							// Put Index Array In Buffer

		GL.wglSwapIntervalEXT(1);
	}



	/** Main loop
	 */

	private final static void mainLoop()
	{
		processKeyboard();										// Get Keyboard Events
	}



	/** Rendering method
	 */

	private final static void render()
	{
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);	// Clear Screen And Depth Buffer
		gl.loadIdentity();										// Reset The Current Modelview Matrix

		gl.translatef(0.0f, 0.0f, -12.0f);
		gl.rotatef(rot, 1.0f, 0.75f, 0.30f);

		gl.vertexPointer(3, GL.FLOAT, 0, Sys.getDirectBufferAddress(fBuffer_ver));	// 3 Floats Per Vertex, 0 Packing, And Address
		gl.colorPointer(3, GL.FLOAT, 0, Sys.getDirectBufferAddress(fBuffer_col));	// 3 Floats Per Color, 0 Packing, And Address

		gl.drawElements(GL.QUADS, 24, GL.UNSIGNED_INT, Sys.getDirectBufferAddress(iBuffer_ind));	// Draw The Cube With The Indexes

		rot += 0.4f;											// Increase Rotation Angle
	}



	/** Process keyboard events
	 */
	
	private final static void processKeyboard()
	{
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
	}



	/** Cleanup
	 */

	private final static void cleanup()
	{
		Keyboard.destroy();										// Destroy The Keyboard
		gl.destroy();											// Destroy The Opengl Context
	}
}
