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

import com.jme.scene.Controller;

/**
 * @author Ahmed
 */
public class ParticleController extends Controller {

	private ParticleSystem ps;
	private Particle currentP;

	private boolean isFirst;

	public ParticleController(ParticleSystem ps) {
		this.ps = ps;
		currentP = new Particle();
		isFirst = true;
	}

	public void update(float time) {
		float timeF = time * ps.getSpeed();

		for (int i = 0; i < ps.getParticles().size(); i++) {
			// check life
			currentP = (Particle) ps.getParticles().get(i);
			if (isFirst || (this.getRepeatType() == RT_WRAP)) {
				if (currentP.life <= 0.0f) {
					regenerateParticle();
				}

				// update life depending on fade
				currentP.life -= currentP.fade * time;

				// update position by velocity;
				currentP.position.x += currentP.velocity.x
					/ (ps.getFriction() * 1000)
					* timeF;
				currentP.position.y += currentP.velocity.y
					/ (ps.getFriction() * 1000)
					* timeF;
				currentP.position.z += currentP.velocity.z
					/ (ps.getFriction() * 1000)
					* timeF;

				// update velocity depending on gravity;
				currentP.velocity.x += ps.getGravity().x * timeF;
				currentP.velocity.y += ps.getGravity().y * timeF;
				currentP.velocity.z += ps.getGravity().z * timeF;

				// update colourrrssss
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

				// im going to start using big words now, INTERPOLATE
				// the size
				currentP.size.x =
					(ps.getStartSize().x * currentP.life)
						+ (ps.getEndSize().x * (1 - currentP.life));
				currentP.size.y =
					(ps.getStartSize().y * currentP.life)
						+ (ps.getEndSize().y * (1 - currentP.life));
				currentP.size.z =
					(ps.getStartSize().z * currentP.life)
						+ (ps.getEndSize().z * (1 - currentP.life));

				// set the new Particle
				ps.getParticles().set(i, currentP);
				ps.setParticlePosition(currentP.position, i);
			} else if (this.getRepeatType() == RT_CLAMP) {
				currentP = (Particle) ps.getParticles().get(i);
				if (isFirst == true) {
					regenerateParticle();
					continue;
				} else if (currentP.life <= 0.0f) {
					// kill em alll....and make sure no one lives!
					killParticle();
				}

				//	update life depending on fade again!
				currentP.life -= currentP.fade * timeF;

				// update position by velocity...again! How boring!
				currentP.position.x += currentP.velocity.x
					/ (ps.getFriction() * 1000)
					* timeF;
				currentP.position.y += currentP.velocity.y
					/ (ps.getFriction() * 1000)
					* timeF;
				currentP.position.z += currentP.velocity.z
					/ (ps.getFriction() * 1000)
					* timeF;

				// i aint gonna mention what im doing here, cause
				// ive already mentioned it! *walks off in a strop*
				currentP.velocity.x += ps.getGravity().x * timeF;
				currentP.velocity.y += ps.getGravity().y * timeF;
				currentP.velocity.z += ps.getGravity().z * timeF;

				// update colours...*moans from having to say "again"*
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

				// big words are for big people, and im still little,
				// so i wont use long words...yet!
				currentP.size.x =
					(ps.getStartSize().x * currentP.life)
						+ (ps.getEndSize().x * (1 - currentP.life));
				currentP.size.y =
					(ps.getStartSize().y * currentP.life)
						+ (ps.getEndSize().y * (1 - currentP.life));
				currentP.size.z =
					(ps.getStartSize().z * currentP.life)
						+ (ps.getEndSize().z * (1 - currentP.life));

				// set the new Particle...again!
				ps.getParticles().set(i, currentP);
				ps.setParticlePosition(currentP.position, i);
			} else if (getRepeatType() == RT_CYCLE) {
				// if the repeat type is cycle, change it to wrap!
				// because cycle isn't implemented yet!
				setRepeatType(RT_WRAP);
			}
		}
		isFirst = false;
	}

	private void killParticle() {
		currentP.life = 0.0f;
		currentP.fade = 0.0f;

		currentP.position.x = ps.getPosition().x;
		currentP.position.y = ps.getPosition().y;
		currentP.position.z = ps.getPosition().z;

		currentP.color.r = ps.getEndColor().r;
		currentP.color.g = ps.getEndColor().g;
		currentP.color.b = ps.getEndColor().b;
		currentP.color.a = ps.getEndColor().a;

		currentP.size.x = 0;
		currentP.size.y = 0;
		currentP.size.z = 0;

		currentP.velocity.x = 0;
		currentP.velocity.y = 0;
		currentP.velocity.z = 0;
	}

	private void regenerateParticle() {
		currentP.life = 1.0f;
		currentP.fade = (float) (ps.getFade() * Math.random() + ps.getFade());

		if (ps.getRandom() == true) {
			currentP.position.x =
				ps.getPosition().x
					+ (float) (ps.getPlane().x * Math.random()
						- (ps.getPlane().x/2));
			currentP.position.y =
				ps.getPosition().y
					+ (float) (ps.getPlane().y * Math.random()
						- (ps.getPlane().y/2));
			currentP.position.z =
				ps.getPosition().z
					+ (float) (ps.getPlane().z * Math.random()
						- (ps.getPlane().z/2));
		} else {
			currentP.position.x = ps.getPosition().x;
			currentP.position.y = ps.getPosition().y;
			currentP.position.z = ps.getPosition().z;
		}

		currentP.color.r = ps.getStartColor().r;
		currentP.color.g = ps.getStartColor().g;
		currentP.color.b = ps.getStartColor().b;
		currentP.color.a = 0;

		currentP.size.x = ps.getStartSize().x;
		currentP.size.y = ps.getStartSize().y;
		currentP.size.z = ps.getStartSize().z;

		currentP.velocity.x =
			(((float) (Math.random() * 32767) % 50) - 26.0f) * 10.0f;
		currentP.velocity.y =
			(((float) (Math.random() * 32767) % 50) - 26.0f) * 10.0f;
		currentP.velocity.z =
			(((float) (Math.random() * 32767) % 50) - 26.0f) * 10.0f;
	}
}
