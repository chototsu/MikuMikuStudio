/*
 * Created on Jan 20, 2004
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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * @author Ahmed
 */
public class Particle {

	// how much to fade by johny?
	public float fade;

	// johny son, im dying, i only have a maximum of 1.0f!!
	public float life;

	// johny dude...calm down, you can squash me with your current
	// size
	public Vector3f size;

	// johny, please dude, dont sit on the bird shit!!
	public Vector3f position;

	// speed on the highway!!
	public Vector3f velocity;

	// ewwww, blue bird shit!
	public ColorRGBA color;

	public Particle() {
		// Its all coming up millhouse!!
		// yes, 0s are coming...ruuuunnnn!!
		position = new Vector3f(0, 0, 0);
		fade = 0.0f;
		life = 0.0f;
		velocity = new Vector3f(0, 0, 0);
		size = new Vector3f(0, 0, 0);
		color = new ColorRGBA(0, 0, 0, 0);
	}

}
