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
package jme.controller;

import java.util.logging.Level;

import jme.entity.Entity;
import jme.entity.camera.Camera;
import jme.exception.MonkeyRuntimeException;
import jme.math.MathUtils;
import jme.physics.PhysicsModule;
import jme.system.KeyBindingManager;
import jme.utility.LoggingSystem;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.vector.Vector3f;

/**
 * <code>TrackingController</code> defines a controller that uses a camera
 * to "follow" a given entity. It is intended to be used such that the user is
 * directly controlling an entity. As that entity moves away, the camera trackings
 * it by updating it's view. If the entity is getting too far away, the camera
 * moves to maintain a minimal distance. 
 * 
 * Movement of the entity is handled by the entity's <code>PhysicsModule</code>.
 * If no such module exists, the entity will not be moved via 
 * <code>TrackingController</code>.
 * 
 * @author Mark Powell
 */
public class TrackingController extends AbstractGameController {
	//the minimal distance the camera can be away from the entity.
	private double trackingDistance;
	private float trackingSpeed;

	//Camera system
	private Camera camera;

	//Entity's physics module for movement. Can be null.
	private PhysicsModule physics;

	//keybindings
	protected KeyBindingManager key;

	/**
	 * Constructor instantiates a new <code>TrackingController</code>
	 * object. During instantiation, it sets the input devices for
	 * use and the key bindings are set.
	 * @param entity the entity to track.
	 * @param camera the camera to use for tracking.
	 * @throws MonkeyRuntimeException if entity or camera are null.
	 */
	public TrackingController(Entity entity, Camera camera) {
		if (null == entity || null == camera) {
			throw new MonkeyRuntimeException(
				"Entity and camera " + "may not be null.");
		}
		this.entity = entity;
		this.camera = camera;
		//physics MIGHT be null
		this.physics = entity.getPhysics();

		try {
			Mouse.create();
			Keyboard.create();
		} catch (Exception e) {
			e.printStackTrace();
			LoggingSystem.getLoggingSystem().getLogger().log(
				Level.WARNING,
				"Error creating Mouse and/or Keyboard");

		}

		setDefaultKeyBindings();
	}

	/**
	 * <code>render</code> calls the render method for the camera
	 * updating the camera's viewport.
	 */
	public void render() {
		camera.render();
	}

	/**
	 * <code>update</code> refreshes the camera and checks the tracking
	 * distance. Key presses are then checked and the entity is updated 
	 * if needed.
	 * 
	 * @param time the time between updates.
	 */
	public boolean update(float time) {
		Keyboard.poll();

		camera.update(time);
		camera.updateFrustum();
		camera.setView(entity.getPosition());

		double distance =
			MathUtils.distance(camera.getPosition(), entity.getPosition());
		if (distance > trackingDistance) {
			Vector3f vec =
				(
					Vector3f.sub(
						camera.getView(),
						camera.getPosition(),
						null)).normalise(
					null);

			camera.getPosition().x += vec.x * trackingSpeed;
			camera.getPosition().z += vec.z * trackingSpeed;

			camera.getPosition().y = entity.getPosition().y;
		}

		if (null != physics) {
			if (isKeyDown("forward")) {
				physics.move(1);
			}

			if (isKeyDown("back")) {
				physics.move(-0.5f);
			}

			if (isKeyDown("right")) {
				physics.turn(-1);
				entity.setYaw(physics.getCurrentAngle());
			} else {
				if (isKeyDown("left")) {
					physics.turn(1);
					entity.setYaw(physics.getCurrentAngle());
				} else {
					physics.turn(0);
				}
			}
		}
		return checkAdditionalKeys();
	}

	/**
	 * <code>setTrackingDistance</code> sets the distance to follow
	 * behind the entity. If the camera is further than this
	 * tracking distance, the camera will move towards the entity. 
	 * The speed at which the camera moves is dependant on the 
	 * tracking speed.
	 * 
	 * @param trackingDistance the minimal distance the camera
	 * 		is allowed to follow behind. 
	 */
	public void setTrackingDistance(float trackingDistance) {
		this.trackingDistance = trackingDistance;
	}

	/**
	 * <code>setTrackingSpeed</code> sets the speed at which the camera
	 * follows the entity. 
	 * @param trackingSpeed the speed at which the camera follows the
	 * 		entity.
	 */
	public void setTrackingSpeed(float trackingSpeed) {
		this.trackingSpeed = trackingSpeed;
	}

	/**
	 * <code>checkAdditionalKeys</code> tests the keyboard for any additional
	 * key bindings. It is recommended that this be overridden by any 
	 * subclass to add additional key bindings. The method returns a boolean. 
	 * This boolean notifies if the game should be stopped or not. Default false
	 * is generated by hitting the escape key.
	 * 
	 * @param boolean true continue for another frame, false stop.
	 */
	protected boolean checkAdditionalKeys() {
		if (isKeyDown("exit")) {
			System.out.println("EXIT");
			return false;
		}

		return true;
	}

	/**
	 * <code>setDefaultKeyBindings</code> sets the default bindings for
	 * forward, backward, strafe left/right, turn left/right, rise and lower.
	 */
	private void setDefaultKeyBindings() {
		key = KeyBindingManager.getKeyBindingManager();
		key.set("exit", Keyboard.KEY_ESCAPE);
		key.set("forward", Keyboard.KEY_W);
		key.set("right", Keyboard.KEY_D);
		key.set("left", Keyboard.KEY_A);
		key.set("back", Keyboard.KEY_S);
		key.add("forward", Keyboard.KEY_UP);
		key.add("right", Keyboard.KEY_RIGHT);
		key.add("left", Keyboard.KEY_LEFT);
	}

}
