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
import org.lwjgl.vector.Vector3f;

import jme.AbstractGame;
import jme.entity.effects.ParticleEmitter;
import jme.entity.effects.ParticleSystem;
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
		gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);
		gl.loadIdentity();
		ps.render();
	}

	protected void initSystem() {
		//create the window
		DisplaySystem.createDisplaySystem("Particles", "jme/data/Images/Monkey.jpg", true);
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
			(float) Display.getWidth() / (float) Display.getHeight(),
			0.01f,
			750.0f);
		gl.matrixMode(GL.MODELVIEW);
		gl.hint(GL.PERSPECTIVE_CORRECTION_HINT, GL.NICEST);
		gl.disable(GL.DEPTH_TEST);
	}

	protected void initGame() {
		timer = Timer.getTimer();
		
		ParticleEmitter pe = new ParticleEmitter(1000);
		pe.setStartColor(new Vector3f(1.0f,1.0f,1.0f));
		pe.setEndColor(new Vector3f(1.0f,1.0f,1.0f));
		pe.setStartSize(new Vector3f(0.25f,1.0f,0.25f));
		pe.setEndSize(new Vector3f(0.25f,1.0f,0.25f));
		pe.setFade(0.01f);
		pe.setSpeed(1);
		pe.setGravity(new Vector3f(0.0f,-100.0f,10.0f));
		pe.setFriction(1);
		pe.setTexture("jme/data/texture/star.png");
		pe.loopAnimation(true);
		
		ps = new ParticleSystem();
		ps.addEmitter(pe);
		ps.setPosition(new Vector3f(0,100,-10));
	}

	protected void reinit() {
		//nothing here... YET!
	}

	protected void cleanup() {
		//clean up the OpenGL resources
		gl.destroy();
	}

	public static void main(String[] args) {
		TestParticleSystem app = new TestParticleSystem();
		app.start();
	}

}
