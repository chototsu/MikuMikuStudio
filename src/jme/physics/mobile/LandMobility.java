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
package jme.physics.mobile;

import java.util.logging.Level;

import jme.physics.PhysicsModule;
import jme.utility.LoggingSystem;

import org.lwjgl.vector.Vector3f;

/**
 * <code>LandMobility</code> handles the acceleration and velocity of a 
 * land vehicle. It is set such that to move forward/backward the entity is
 * accelerated/decelerated. The velocity of entity is determined by:
 * V1 = V0 + a(t). Turning is also determined by a set turning velocity. 
 * 
 * Future enhancements should include using the normal of the geometry moving
 * over to for friction, gravity changes. 
 * 
 * @author Mark Powell
 */
public class LandMobility implements PhysicsModule {
	private float currentTurningVel;
    private float maxVelocity;
	private float minVelocity;
	
    private float baseAcceleration;
    private float coastDeceleration;
    private float currentAcceleration;
	private float currentVelocity;
	private float prevVelocity;
	
	private float turningVelocity;
	private float currentAngle;
	private float distance;

	private boolean moving;
	
    /**
     * Constuctor instantiates a new <code>LandMobility</code> object. 
     *
     */
	public LandMobility() {
          LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO, 
            "LandMobility physics module created.");
	}

    /**
     * <code>update</code> calculates the current velocity of the entity,
     * as well as the current angle the entity is facing. This is scaled by
     * the current time frame sent as a parameter. This insures independance
     * from the framerate.
     * 
     * @param time the time between updates.
     */
	public void update(float time) {
		//if we are moving, update the velocity otherwise coast to a stop.
		if(moving) {
			currentVelocity = prevVelocity + currentAcceleration * time;
			if(currentVelocity > maxVelocity) {
				currentVelocity = maxVelocity;
			} else if(currentVelocity < minVelocity) {
                currentVelocity = minVelocity;
			}
		} else {
            if(currentVelocity > 0) {
                currentVelocity = currentVelocity - coastDeceleration * time;
			    if(currentVelocity < 0) {
                    currentVelocity = 0;
			    }
            } else if(currentVelocity < 0) {
				currentVelocity = currentVelocity + coastDeceleration * time;
                if(currentVelocity > 0) {
                    currentVelocity = 0;
                }
			}
		}
		prevVelocity = currentVelocity;
		
        //only update if time is valid. This insures that the initial time of
        //infinity does not result in a bad result.
		if(time < Float.MAX_VALUE) {
			distance += currentVelocity * time;
            currentAngle += currentTurningVel * time;
        
        }
        //set moving to false to allow for release of the movement key.
        moving = false;
	}
	
    /**
     * <code>updatePosition</code> sets the entities new position based on 
     * it's current velocity and angle. 
     * @param position the position to update.
     */
	public void updatePosition(Vector3f position) {
		float sin = (float)Math.sin(Math.toRadians(currentAngle));
		float cos = (float)Math.cos(Math.toRadians(currentAngle));
		
		position.x += sin * distance;
		position.z += cos * distance;
		
		distance = 0;

	}
	
    /**
     * <code>turn</code> affects the current turning velocity, where negative
     * one turns right by the set turning velocity and positive one turns left
     * by the set turning velocity. For increases of turning speed increase the
     * scalar and vice versa for decreasing.
     * @param turnScalar the amount to multiply the turning velocity by.
     */
	public void turn(float turnScalar){
		currentTurningVel = turningVelocity * turnScalar;
	}
    
    /**
     * <code>move</code> sets the current acceleration to the base acceleration
     * to the acceleration scalar. Therefore, if you wanted to move forward
     * at full speed the accelerationScalar would be set to 1. If you wanted
     * to decelerate by half the speed (-0.5).
     * @param accelerationScalar the scalar to multiply the baseAcceleration and
     *      set the current acceleration.
     *      
     */
    public void move(float accelerationScalar) {
        currentAcceleration = baseAcceleration * accelerationScalar;
        moving = true;
    }
    
    /**
     * <code>strafe</code> sets the current acceleration to the base strafe
     * acceleration to the acceleration scalar. Therefore, if you want to strafe
     * to the right, you would set it to -1. To the left would be 1.
     * @param strafeScalar is the scalar to multiply the strafe acceleration
     *      by.
     */
    public void strafe(float strafeScalar) {
    	//TODO set up strafe
    }
    
    /**
     * <code>setCoastDeceleration</code> sets the deceleration rate of the
     * entity. This value is used when the entity is not actively moving.
     * @param coastDeceleration the deceleration rate of the entity.
     */
    public void setCoastDeceleration(float coastDeceleration) {
    	this.coastDeceleration = coastDeceleration;
    }
    
    /**
     * <code>setMaxVelocity</code> sets the maximum velocity the entity is
     * allowed to obtain.
     * @param maxVelocity the maximum velocity of the entity.
     */
    public void setMaxVelocity(float maxVelocity) {
		this.maxVelocity = maxVelocity;
	}
    
    /**
     * <code>setMinVelocity</code> sets the minimum velocity (backward movement)
     * the entity is allowed to obtain.
     * @param minVelocity the minimum velocity of the entity.
     */
	public void setMinVelocity(float minVelocity) {
		this.minVelocity = minVelocity;
	}
    
    /**
     * <code>setTurningVelocity</code> sets the rate at which the entity can
     * turn.
     * @param turningVelocity the rate of turn of the entity.
     */
	public void setTurningVelocity(float f) {
		turningVelocity = f;
	}
    
    /**
     * <code>setBaseAcceleration</code> sets the acceleration of the entity. 
     * This base is used to determine the velocity of the entity.
     * @param baseAcceleration the base (unmodified) acceleration rate of the
     *      entity.
     */
    public void setBaseAcceleration(float baseAcceleration) {
        this.baseAcceleration = baseAcceleration;
    }

    /**
     * <code>setCurrentAngle</code> sets the current angle of the entity. 
     * Effectively rotating the entity instantly.
     * @param currentAngle the angle to set the entity to.
     */
    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }

    /**
     * <code>getCurrentAngle</code> returns the current angle the entity is 
     * facing.
     * @return the current angle of the entity.
     */
	public float getCurrentAngle() {
		return currentAngle;
	}
    
    /**
     * <code>getCurrentVelocity</code> returns the current velocity of the 
     * entity.
     * @return the current velocity of the entity.
     */
	public float getCurrentVelocity() {
		return currentVelocity;
	}
    
    /**
     * <code>getCurrentTurningVel</code> returns the current velocity of turning.
     * @return the current turning velocity.
     */
	public float getCurrentTurningVel() {
        return currentTurningVel;
    }
    

}