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
package test.model.ms3dAscii;


import org.lwjgl.Display;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

import jme.AbstractGame;
import jme.controller.BaseFPSController;
import jme.entity.camera.Camera;
import jme.geometry.model.Model;
import jme.geometry.model.ms.MilkshapeModel;
import jme.system.DisplaySystem;
import jme.utility.Timer;

/**
 * @author Mark Powell
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestMilkshape extends AbstractGame {
    
    private Model model;
    private float yrot;
    private BaseFPSController cc;
    private Timer timer;
    private Camera camera;
    
    /**
     * This is where we'll do any updating
     */
    protected void update() {
        if(!cc.update(timer.getFrameRate())) { 
          finish();
        }
        timer.update();
    }
    
    /**
     * Render is called once per frame to display the data.
     */
    protected void render() {
        GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        GL.glLoadIdentity();
        cc.render();
        model.render();
    }
    
    /** 
     * This is where we create and initialize the window.
     */
    protected void initDisplay() {
        
        DisplaySystem.createDisplaySystem(
            "TestMilkshape", 
            "data/Images/Monkey.jpg",
            true
        );
    }
    
    protected void initGL() {

         // Here we create the OpenGL bindings.
         
        //Define the clear color to be black
                
        GL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL.glClearDepth(1.0);
        GL.glEnable(GL.GL_DEPTH_TEST);
        GL.glDepthFunc(GL.GL_LEQUAL);

        GL.glMatrixMode(GL.GL_PROJECTION);
        GL.glLoadIdentity();

        GLU.gluPerspective(45.0f, (float) Display.getWidth() / (float) Display.getHeight(), 100.0f, 2000.0f);
        GL.glMatrixMode(GL.GL_MODELVIEW);

        GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    }

    /**
     * Called first to initialize system attributes.
     */
    protected void initSystem() {
        initDisplay();
        initGL();
        camera = new Camera(1,50, 0, 300, 0, 0, 0, 0, 1, 0);
        cc = new BaseFPSController(camera);
        timer = Timer.getTimer();
    }
    /** 
     * Nothing here yet.
     */
    protected void initGame() {
        model = new MilkshapeModel(true);
        model.load("data/model/msascii/run.txt");
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
        Window.destroy();
    }

    /**
     * <code>main</code> entry point for application.
     * @param args comman line arguments, none used.
     */ 
    public static void main(String[] args) { 
       TestMilkshape testApp = new TestMilkshape(); 
       testApp.start(); 
    }   
}
    
