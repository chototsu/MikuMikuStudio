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

import java.util.ArrayList;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.BoundingVolume;
import com.jme.scene.Geometry;

/**
 * @author Ahmed
 */
public class ParticleSystem extends Geometry {

	// the television set
	private Particle[] particles;

	// the remote control 
	private ParticleController partController;

	// johny? keep it steady now!
	private float speed;

	// johny? your fired! you are a disgrace!
	// captain friction can do better!
	private float friction;

	// friction, set me the fade
	private float fade;

	// dont like your name friction, change it to johny!
	// oh and while your at it, change all the particle colours to
	// the start colour
	private ColorRGBA startColor;
	private ColorRGBA endColor;

	// wow johny, its huge!
	private Vector3f startSize;
	private Vector3f endSize;

	// where's sir isaac when you need him?
	private Vector3f gravity;

	// where are we going to place that infamouse particlesystem
	private Vector3f position;

	// do we need randomneessesss?
	private boolean random;
	private Vector3f randomness;

	// culling stuff...yes, cull em alll...mwwaaaahahaaaaa
	private Vector3f positionsOfParticles[];
	
	public ParticleSystem(int numOfParticles) {
		speed = 0;
		friction = 0;
		startColor = new ColorRGBA(0, 0, 0, 0);
		endColor = new ColorRGBA(0, 0, 0, 0);
		startSize = new Vector3f(0, 0, 0);
		endSize = new Vector3f(0, 0, 0);
		gravity = new Vector3f(0, 0, 0);
		position = new Vector3f(0, 0, 0);
		random = false;
		randomness = new Vector3f();

		particles = new Particle[numOfParticles];
		positionsOfParticles = new Vector3f[numOfParticles];

		for (int i = 0; i < numOfParticles; i++) {
			particles[i]= new Particle();
			positionsOfParticles[i] = new Vector3f();
		}
	}

	public void draw(Renderer r) {
		super.draw(r);
		r.draw(this);
	}

	public void update(float time) {
		partController.update(time);
	}

	public void addController(ParticleController pc) {
		partController = pc;
	}

	// Getter Methods
	public float getSpeed() {
		return speed;
	}
	public float getFriction() {
		return friction;
	}
	public float getFade() {
		return fade;
	}
	public ColorRGBA getStartColor() {
		return startColor;
	}
	public ColorRGBA getEndColor() {
		return endColor;
	}
	public Vector3f getStartSize() {
		return startSize;
	}
	public Vector3f getEndSize() {
		return endSize;
	}
	public Vector3f getGravity() {
		return gravity;
	}
	public Vector3f getPosition() {
		return position;
	}
	public Particle[] getParticles() {
		return particles;
	}
	public boolean getRandom() {
		return random;
	}
	public Vector3f getPlane() {
		return randomness;
	}
	public Vector3f[] getParticlePosition() {
		return positionsOfParticles;
	}
	public BoundingVolume getModelBound() {
		return bound;
	}

	// setter methods
	public void setSpeed(float s) {
		speed = s;
	}
	public void setFriction(float f) {
		friction = f;
	}
	public void setFade(float f) {
		fade = f;
	}
	public void setStartColor(ColorRGBA c) {
		startColor.r = c.r;
		startColor.g = c.g;
		startColor.b = c.b;
		startColor.a = c.a;
	}
	public void setEndColor(ColorRGBA c) {
		endColor.r = c.r;
		endColor.g = c.g;
		endColor.b = c.b;
		endColor.a = c.a;
	}
	public void setStartSize(Vector3f s) {
		startSize.x = s.x;
		startSize.y = s.y;
		startSize.z = s.z;
	}
	public void setEndSize(Vector3f s) {
		endSize.x = s.x;
		endSize.y = s.y;
		endSize.z = s.z;
	}
	public void setGravity(Vector3f g) {
		gravity.x = g.x;
		gravity.y = g.y;
		gravity.z = g.z;
	}
	public void setPosition(Vector3f p) {
		position.x = p.x;
		position.y = p.y;
		position.z = p.z;
	}
	public void setPlane(boolean r, Vector3f i) {
		random = r;
		randomness.x = i.x + 0.0000001f;
		randomness.y = i.y + 0.0000001f;
		randomness.z = i.z + 0.0000001f;
	}
	public void setParticlePosition(Vector3f p, int i) {
		positionsOfParticles[i] = p;
	}

	public void setModelBound(BoundingVolume b) {
		bound = b;
	}
    
    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location
     * of all this node's parents. 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        if(bound != null) {
            updateModelBound();
            worldBound =
                bound.transform(worldRotation, worldTranslation, worldScale);
        }  
    } 

	public void updateModelBound() {
        if(bound != null) {
		  bound.computeFromPoints(positionsOfParticles);
        }
	}
}
