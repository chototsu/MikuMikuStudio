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
package test.general; 

import jme.utility.Timer;
import jme.controller.BasicController;
import jme.geometry.hud.text.Font2D;
import org.lwjgl.opengl.GL; 

import jme.AbstractGame; 
import jme.system.DisplaySystem;
import jme.entity.camera.Camera;

public class Tutorial1 extends AbstractGame { 

    // Initiate a timer
    Timer timer;
    
    // Initiate a font
    Font2D font;
    
    // Initiate a camera
    Camera camera;
    
    // Initiate a base controller
    BasicController controller;
    
   protected void update() {
    if(!controller.update(timer.getFrameRate())) { 
        finish(); 
    }
    timer.update();
   }

   protected void render() { 
      gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT); 
      gl.loadIdentity();
      
      controller.render();

      // move to the left and into the screen
      gl.translatef(-1.5f,0.0f,-6.0f);
      
      // Rotate 10 degrees about the y-axis
      // gl.rotatef(10.0f, 0.0f, 1.0f, 0.0f);
      
      // begin the triangle drawing code
      gl.begin(GL.TRIANGLES);
            // Set the color to red
            gl.color3f(1.0f, 0.0f, 0.0f);
            gl.vertex3f(0.0f, 1.0f, 0.0f);
            
            // Set the color to green
            gl.color3f(0.0f, 1.0f, 0.0f);
            gl.vertex3f(-1.0f, -1.0f, 0.0f);
            
            // Set the color to Blue
            gl.color3f(0.0f, 0.0f, 1.0f);
            gl.vertex3f(1.0f, -1.0f, 0.0f);
      gl.end();
      
      // move right 3 units
      gl.translatef(3.0f, 0.0f, 0.0f);
      
      // Set the color to Red one time only
      gl.color3f(0.75f, 0.0f, 0.0f);
      
      // begin the square drawing code
      gl.begin(GL.QUADS);
            gl.vertex3f(-1.0f, 1.0f, 0.0f);
            gl.vertex3f(1.0f, 1.0f, 0.0f);
            gl.vertex3f(1.0f, -1.0f, 0.0f);
            gl.vertex3f(-1.0f, -1.0f, 0.0f);
      gl.end();
    
    // Print out for FPS diagnostics
    font.print(1,1,"Frame Rate: " + timer.getFrameRate(), 0);
    // font.print(1,1,"Timer: " + timer.toString(), 0);
       
   } 

   protected void initSystem() { 
      //create the window 
      DisplaySystem.createDisplaySystem("test","jme/data/Monkey.jpg", true);

      //create the OpenGL bindings 
      gl = DisplaySystem.getDisplaySystem().getGL(); 
      glu = DisplaySystem.getDisplaySystem().getGLU(); 
      //Define what the clear color will be (black) 
      gl.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
      gl.matrixMode(GL.PROJECTION);
      gl.loadIdentity();
      
      // Calculate The Aspect Ratio Of The Window
      glu.perspective(
        45.0f,
        (float)gl.getWidth() / (float)gl.getHeight(),
        0.01f,
        750.0f);
        gl.matrixMode(GL.MODELVIEW);
        gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);
        
        // instantiate timer
        timer = Timer.getTimer();
        
        // instantiate font
        font = new Font2D("jme/data/font.png");
        
        // blend font together so it doesn't chop the letters off
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE);
   }
   
   protected void initGame() {
        
        // instantiate a new camera
        camera = new Camera(1,0,0,10,0,0,0,0,1,0);
        
        // instantiate a new controller
        controller = new BasicController(camera);
   } 

   protected void reinit() { 
      //nothing here... YET! 
   } 

   protected void cleanup() { 
      //clean up the OpenGL resources 
      gl.destroy(); 
   } 
    
   public static void main(String[] args) { 
      Tutorial1 app = new Tutorial1();
      app.start(); 
   } 

}
