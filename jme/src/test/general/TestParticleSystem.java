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



import org.lwjgl.Display;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLU;
import org.lwjgl.opengl.Window;

import jme.AbstractGame;
import jme.entity.effects.ParticleEmitter;
import jme.entity.effects.ParticleSystem;
import jme.math.Vector;
import jme.system.DisplaySystem;
import jme.utility.Timer;

/**
 * @author Mark Powell
 */
public class TestParticleSystem extends AbstractGame {

	private ParticleSystem ps;
	private Timer timer;

	protected void update() {
		timer.update();
		ps.update(10/timer.getFrameRate());
		//nothing to update... YET!
	}

	protected void render() {
		GL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		GL.glLoadIdentity();
		ps.render();
	}

	protected void initSystem() {
		//create the window
		DisplaySystem.createDisplaySystem("Particles", "data/Images/Monkey.jpg", true);
		
		//Define what the clear color will be (black)
		GL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL.glMatrixMode(GL.GL_PROJECTION);
		GL.glLoadIdentity();
		// Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
			45.0f,
			(float) Display.getWidth() / (float) Display.getHeight(),
			0.01f,
			750.0f);
		GL.glMatrixMode(GL.GL_MODELVIEW);
		GL.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		GL.glDisable(GL.GL_DEPTH_TEST);
	}

	protected void initGame() {
		timer = Timer.getTimer();
		
		ParticleEmitter pe = new ParticleEmitter(1000);
		pe.setStartColor(new Vector(1.0f,1.0f,1.0f));
		pe.setEndColor(new Vector(1.0f,1.0f,1.0f));
		pe.setStartSize(new Vector(0.25f,1.0f,0.25f));
		pe.setEndSize(new Vector(0.25f,1.0f,0.25f));
		pe.setFade(0.01f);
		pe.setSpeed(1);
		pe.setGravity(new Vector(0.0f,-100.0f,10.0f));
		pe.setFriction(1);
		pe.setTexture("data/texture/star.png");
		pe.loopAnimation(true);
		
		ps = new ParticleSystem();
		ps.addEmitter(pe);
		ps.setPosition(new Vector(0,100,-10));
	}

	protected void reinit() {
		//nothing here... YET!
	}
    

	protected void cleanup() {
		//clean up the OpenGL resources
		Window.destroy();
	}

	public static void main(String[] args) {
		TestParticleSystem app = new TestParticleSystem();
		app.start();
	}

}
