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
package test.bounding;

import org.lwjgl.Display;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

import jme.AbstractGame;
import jme.entity.camera.Camera;
import jme.geometry.bounding.BoundingCylinder;
import jme.geometry.hud.text.Font2D;
import jme.math.Vector;
import jme.system.DisplaySystem;
import jme.utility.Timer;

/**
 * @author Mark Powell
 */
public class TestBoundingCylinder extends AbstractGame {
    private Timer timer;
    private Camera camera;
    private KeyController controller;
    private Vector[] points;
    private BoundingCylinder bc;
    private Font2D font;
    
    /* (non-Javadoc)
     * @see jme.AbstractGame#update()
     */
    protected void update() {
        if(!controller.update(timer.getFrameRate())) { 
          finish();
        }
        timer.update();
    }

    /* (non-Javadoc)
     * @see jme.AbstractGame#render()
     */
    protected void render() {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        GL.glLoadIdentity();
        controller.render();
        
        GL.glBegin(GL.GL_LINES);
        GL.glColor3f(0.0f, 1.0f, 0.0f);
        for(int i = 0; i < points.length; i++) {
            GL.glVertex3f(points[i].x, points[i].y, points[i].z);
        }
        GL.glEnd();
        
        GL.glBegin(GL.GL_LINES);
        GL.glColor3f(1.0f, 0.0f, 0.0f);
        GL.glVertex3f(bc.getCenter().x, bc.getCenter().y, bc.getCenter().z);
        GL.glVertex3f(bc.getDirection().x, bc.getDirection().y, bc.getDirection().z);
        GL.glEnd();
        
        font.print(1, 45, "Height - " + bc.getHeight(), 0);
        font.print(1, 30,"Capsule - " + bc.getCenter(), 0);
        font.print(1, 15, " to " + bc.getDirection(),0);
        font.print(1, 1, "radius " + bc.getRadius(), 0);
        

    }

    /* (non-Javadoc)
     * @see jme.AbstractGame#initSystem()
     */
    protected void initSystem() {
        DisplaySystem.createDisplaySystem(
            "TestApplication",
            "data/Images/Monkey.jpg",
            true);
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
        GL.glDepthFunc(GL.GL_LEQUAL);
        GL.glEnable(GL.GL_DEPTH_TEST);
        GL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        
    }

    /* (non-Javadoc)
     * @see jme.AbstractGame#initGame()
     */
    protected void initGame() {
        timer = Timer.getTimer();
        camera = new Camera(1,0,0,10,0,0,0,0,1,0);
        controller = new KeyController(camera);
        font = new Font2D("data/Font/font.png");
        
        points = new Vector[100];
        for(int i = 0; i < points.length; i++) {
            points[i] = new Vector((float)Math.random() * 20 - 10,
                (float)Math.random() * 40 - 10,
                (float)Math.random() * 20 - 10);
        }
        
        bc = new BoundingCylinder();
        bc.leastSquaresFit(points);
    }

    /* (non-Javadoc)
     * @see jme.AbstractGame#reinit()
     */
    protected void reinit() {
        //not needed

    }

    /* (non-Javadoc)
     * @see jme.AbstractGame#cleanup()
     */
    protected void cleanup() {
        Window.destroy();

    }
    
    /**
     * <code>main</code> entry point for application.
     * @param args comman line arguments, none used.
     */ 
    public static void main(String[] args) { 
       TestBoundingCylinder testApp = new TestBoundingCylinder(); 
       testApp.start(); 
    }   

}

