/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.effects;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;

/**
 * <code>ParticleSystem</code>
 * 
 * @author Ahmed
 * @version $Id: ParticleSystem.java,v 1.4 2004-02-03 22:44:13 darkprophet Exp $
 */
public class ParticleSystem extends Node {

	private int numOfParticles;
	private float startSize, endSize, fade, speed, friction;
	private ColorRGBA startColor, endColor;
	private Vector3f gravity, position;

	private Particle[] particles;
	private ParticleController pc;

	public ParticleSystem(int num) {
		super();
		numOfParticles = num;
		particles = new Particle[numOfParticles];

		startSize = endSize = 1.0f;
		fade = speed = friction = 0.0f;
		startColor = endColor = new ColorRGBA(0, 0, 0, 0);
		gravity = position = new Vector3f(0, 0, 0);

		Vector3f[] vertices = new Vector3f[4];
		vertices[0] = new Vector3f(0, 0, 0);
		vertices[1] = new Vector3f(0, 1, 0);
		vertices[2] = new Vector3f(1, 1, 0);
		vertices[3] = new Vector3f(1, 0, 0);

		ColorRGBA[] colors = new ColorRGBA[4];
		colors[0] = new ColorRGBA(0, 0, 0, 0);
		colors[1] = new ColorRGBA(0, 0, 0, 0);
		colors[2] = new ColorRGBA(0, 0, 0, 0);
		colors[3] = new ColorRGBA(0, 0, 0, 0);

		Vector2f[] tex = new Vector2f[4];
		tex[0] = new Vector2f(0, 0);
		tex[1] = new Vector2f(0, 1);
		tex[2] = new Vector2f(1, 1);
		tex[3] = new Vector2f(1, 0);

		int[] indices = { 0, 1, 3, 2, 3, 1 };

		for (int i = 0; i < numOfParticles; i++) {
			particles[i] = new Particle(vertices, null, colors, tex, indices);
			particles[i].setName("Particle " + (i + 1));
			attachChild(particles[i]);
		}
	}
	
	//----
	// Getter Methods
	//----
	public float getStartSize() {
		return startSize;
	}
	public float getEndSize() {
		return endSize;
	}
	public float getFade() {
		return fade;
	}
	public float getSpeed() {
		return speed;
	}
	public float getFriction() {
		return friction;
	}
	public ColorRGBA getStartColor() {
		return startColor;
	}
	public ColorRGBA getEndColor() {
		return endColor;
	}
	public Vector3f getGravity() {
		return gravity;
	}
	public Vector3f getStartPosition() {
		return position;
	}
	
	//----
	// Setter Methods
	//----
	public void setStartSize(float s) {
		startSize = s;
	}
	public void setEndSize(float s) {
		endSize = s;
	}
	public void setFade(float f) {
		fade = f;
	}
	public void setSpeed(float s) {
		speed = s;
	}
	public void setFriction(float f) {
		friction = f;
	}
	public void setStartColor(ColorRGBA c) {
		startColor = c;
	}
	public void setEndColor(ColorRGBA c) {
		endColor = c;
	}
	public void setGravity(Vector3f g) {
		gravity = g;
	}
	public void setStartPosition(Vector3f p) {
		position = p;
	}
	
	//----
	// misc methods
	//----
	public void update(float t) {
		pc.update(t);
	}
	public void addController(ParticleController p) {
		pc = p;
	}
}
