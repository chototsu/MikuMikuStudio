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

import com.jme.math.Matrix3f;
import com.jme.scene.Controller;
import com.jme.system.DisplaySystem;

/*
 * NOTE: 2/15/04 - Fixed random point using line. MP
 * 		 2/24/04 - Removed initialization of particles and place in particle 
 *                  constructor. MP
 *       2/26/04 - Documentation. - MP
 */

/**
 * <code>ParticleController</code> updates the particles from a given 
 * particle system. 
 * 
 * @author Ahmed
 * @version $Id: ParticleController.java,v 1.3 2004/02/03 22:44:13 darkprophet
 *          Exp $
 */
public class ParticleController extends Controller {

	private ParticleSystem ps;
	private float time;
	private Particle currentP;

	private Matrix3f rotationalM;

	public ParticleController(ParticleSystem p) {
		super();
		ps = p;
		time = 0.0f;
		rotationalM = new Matrix3f();
	}

	public void update(float timeF) {
		time = timeF * ps.getSpeed();
		for (int i = 0; i < ps.getParticles().length; i++) {
			currentP = ps.getParticles()[i];

			if (getRepeatType() == RT_WRAP) {
				// check if dead
				if (currentP.life <= 0.0f) {
					regenerateParticle();
				}

				// update the particle
				updateParticle();

			} else if (getRepeatType() == RT_CLAMP) {
				// if its the first time, generate
					if (currentP.life <= 0.0f) {
					// if particle is dead, bury it
					killParticle();
				}

				// update the particle
				updateParticle();

			} else if (getRepeatType() == RT_CYCLE) {
				setRepeatType(RT_WRAP);
			}
		}
	}

	public void updateParticle() {
		// update life
		currentP.life -= currentP.fade * time;

		// update position by velocity;
		currentP.getLocalTranslation().x
			+= (currentP.velocity.x / (ps.getFriction() * 1000))
			* time;
		currentP.getLocalTranslation().y
			+= (currentP.velocity.y / (ps.getFriction() * 1000))
			* time;
		currentP.getLocalTranslation().z
			+= (currentP.velocity.z / (ps.getFriction() * 1000))
			* time;
		//currentP.setLocalTranslation(currentP.position);

		// update velocity by gravity;
		currentP.velocity.x += ps.getGravity().x * time;
		currentP.velocity.y += ps.getGravity().y * time;
		currentP.velocity.z += ps.getGravity().z * time;

		// interpolate colors
		currentP.color.r =
			(ps.getStartColor().r * currentP.life)
				+ (ps.getEndColor().r * (1 - currentP.life));
		currentP.color.g =
			(ps.getStartColor().g * currentP.life)
				+ (ps.getEndColor().g * (1 - currentP.life));
		currentP.color.b =
			(ps.getStartColor().b * currentP.life)
				+ (ps.getEndColor().b * (1 - currentP.life));
		currentP.color.a =
			(ps.getStartColor().a * currentP.life)
				+ (ps.getEndColor().a * (1 - currentP.life));
		currentP.updateColor();

		// update the size, currently, the size
		// updates both the x and y values. So you always
		// get a square
		currentP.size =
			(ps.getStartSize() * currentP.life)
				+ (ps.getEndSize() * (1 - currentP.life));
		currentP.setLocalScale(currentP.size);

		rotationalM.setColumn(
			0,
			DisplaySystem
				.getDisplaySystem()
				.getRenderer()
				.getCamera()
				.getLeft());
		rotationalM.setColumn(
			1,
			DisplaySystem.getDisplaySystem().getRenderer().getCamera().getUp());
		rotationalM.setColumn(
			2,
			DisplaySystem
				.getDisplaySystem()
				.getRenderer()
				.getCamera()
				.getDirection());
		currentP.setLocalRotation(rotationalM);

	}

	private void killParticle() {
		currentP.life = 0f;
		currentP.fade = 0f;

		currentP.color.r = 0;
		currentP.color.g = 0;
		currentP.color.b = 0;
		currentP.color.a = 0;

		currentP.size = 0;

		currentP.velocity.x = 1;
		currentP.velocity.y = 1;
		currentP.velocity.z = 1;
	}

	private void regenerateParticle() {
		currentP.life = 1.0f;
		currentP.fade = (float) (ps.getFade() * Math.random() + ps.getFade());

		switch (ps.getGeometry()) {
			case 1 :
				if (ps == ps.getParticleParent()) {
					currentP.setLocalTranslation(ps.getLine().random());
					break;
				} else {
					currentP.setLocalTranslation(ps.getLine().random());
					currentP.getLocalTranslation().x
						+= ps.getLocalTranslation().x;
					currentP.getLocalTranslation().y
						+= ps.getLocalTranslation().y;
					currentP.getLocalTranslation().z
						+= ps.getLocalTranslation().z;
					break;
				}
			case 2 :
				if (ps == ps.getParticleParent()) {
					currentP.setLocalTranslation(ps.getRectangle().random());
					break;
				} else {
					currentP.setLocalTranslation(ps.getRectangle().random());
					currentP.getLocalTranslation().x
						+= ps.getLocalTranslation().x;
					currentP.getLocalTranslation().y
						+= ps.getLocalTranslation().y;
					currentP.getLocalTranslation().z
						+= ps.getLocalTranslation().z;
					break;
				}
			default :
				if (ps == ps.getParticleParent()) {
					currentP.getLocalTranslation().x = ps.getStartPosition().x;
					currentP.getLocalTranslation().y = ps.getStartPosition().y;
					currentP.getLocalTranslation().z = ps.getStartPosition().z;
					break;
				} else {
					currentP.getLocalTranslation().x =
						ps.getStartPosition().x + ps.getLocalTranslation().x;
					currentP.getLocalTranslation().y =
						ps.getStartPosition().y + ps.getLocalTranslation().y;
					currentP.getLocalTranslation().z =
						ps.getStartPosition().z + ps.getLocalTranslation().z;
					break;
				}

		}

		currentP.color.r = ps.getStartColor().r;
		currentP.color.g = ps.getStartColor().g;
		currentP.color.b = ps.getStartColor().b;
		currentP.color.a = ps.getStartColor().a;

		currentP.size = ps.getStartSize();

		currentP.velocity.x =
			(float) (((Math.random() * 32767) % 50) - 26) * 10;
		currentP.velocity.y =
			(float) (((Math.random() * 32767) % 50) - 26) * 10;
		currentP.velocity.z =
			(float) (((Math.random() * 32767) % 50) - 26) * 10;

	}
}
