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
import com.jme.scene.TriMesh;

/**
 * <code>Particle</code>
 * 
 * @author Ahmed
 * @version $Id: Particle.java,v 1.3 2004-02-13 23:24:19 darkprophet Exp $
 */
public class Particle extends TriMesh {

	public float fade, life, size;
	public Vector3f velocity;
	public ColorRGBA color;
	
	private ColorRGBA[] cornerColors;

	public Particle() {
		super();
	}

	public Particle(
		Vector3f[] vertices,
		Vector3f[] normal,
		ColorRGBA[] color,
		Vector2f[] texture,
		int[] indices) {
		super(vertices, normal, color, texture, indices);
		
		cornerColors = new ColorRGBA[color.length];
		size = fade = life = 0.0f;
		velocity = new Vector3f(0, 0, 0);
		this.color = new ColorRGBA(0, 0, 0, 0);
	}

	public void updateColor() {
		for (int i = 0; i < cornerColors.length; i++) {
			cornerColors[i] = this.color;
		}
		super.setColors(cornerColors);
	}

}
