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
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

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
 * @version $Id: TestPolygon.java,v 1.5 2003-09-05 15:43:12 mojomonkey Exp $
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
        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        GL.glLoadIdentity();

        // Enable texture mapping
        GL.glEnable(GL.GL_TEXTURE_2D);

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
        font.print(1, 1, "Frame Rate - " + timer.getFrameRate(), 0);

    }

    /** 
     * This is where we create and initialize the window.
     */
    protected void initDisplay() {
        DisplaySystem.createDisplaySystem(
            "TestPolygon",
            "data/Images/Monkey.jpg",
            true);
    }

    protected void initGL() {

        // Define the clear color to be black
        GL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();

        // Calculate the aspect ratio
        GLU.gluPerspective(
            45.0f,
            (float) Display.getWidth() / (float) Display.getHeight(),
            0.01f,
            750.0f);

        GL.glMatrixMode(GL.GL_MODELVIEW);
        GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    }

    protected void initSystem() {
        initDisplay();
        initGL();
    }

    protected void initGame() {

        // Blend the font together so it doesn't chop the letters off 
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);

        //Instantiate a font object
        font = new Font2D("data/Font/font.png");

        // Instantiate a new timer object
        timer = Timer.getTimer();

        // Initiate a new Vector class
        points = new Vector[3];

        // Initialize the Vector array
        for (int i = 0; i < 3; i++) {
            points[i] = new Vector();
        }

        // Add points to the Vector array
        points[0].x = 0.0f;
        points[0].y = 1.0f;
        points[0].z = -5.0f;
        points[1].x = -1.0f;
        points[1].y = -1.0f;
        points[1].z = -5.0f;
        points[2].x = 1.0f;
        points[2].y = -1.0f;
        points[2].z = -5.0f;

        // Instantiate a new Triangle object
        triangle = new Triangle(points);
        triangle.setTexture("data/texture/Wood.bmp");
    }

    protected void reinit() {
        // Nothing here yet.
    }

    /**
     * Clean up the OpenGL resources
     */
    protected void cleanup() {
        Window.destroy();
    }

    public static void main(String[] args) {
        TestPolygon testPoly = new TestPolygon();
        testPoly.start();
    }
}