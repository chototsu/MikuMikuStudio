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

import org.lwjgl.vector.Vector3f;

/**
 * <code>LandMobility</code>
 * 
 * @author Mark Powell
 */
public class LandMobility {
	private float currentTurningVel;
    float maxVelocity;
	float minVelocity;
	float acceleration;
	float deceleration;
    float currentAcceleration;
	float currentVelocity;
	float prevVelocity;
	
	float turningVelocity;
	float currentAngle;
	float distance;

	boolean moving;
	
	public LandMobility() {
		currentVelocity = 0;
		distance = 0;
		deceleration = 0.01f;
        currentTurningVel = 0;
        currentAngle = 0;
        
	}

	public void update(float time) {
		if(moving) {
			currentVelocity = prevVelocity + currentAcceleration * time;
			if(currentVelocity > maxVelocity) {
				currentVelocity = maxVelocity;
			} else if(currentVelocity < minVelocity) {
                currentVelocity = minVelocity;
			}
		} else {
            if(currentVelocity > 0) {
                currentVelocity = currentVelocity + deceleration * time;
			    if(currentVelocity < 0) {
                    currentVelocity = 0;
			    }
            } else if(currentVelocity < 0) {
				currentVelocity = currentVelocity + acceleration * time;
                if(currentVelocity > 0) {
                    currentVelocity = 0;
                }
			}
		}
		prevVelocity = currentVelocity;
		
		if(time < Float.MAX_VALUE) {
			distance += currentVelocity * time;
            currentAngle += currentTurningVel * time;
        
        }
        
        moving = false;
	}
	
	public void updatePosition(Vector3f position) {
		float x, z;
		float sin = (float)Math.sin(Math.toRadians(currentAngle));
		float cos = (float)Math.cos(Math.toRadians(currentAngle));
		x = sin * distance;
		z = cos * distance;
		
		position.x += x;
		position.z += z;
		
		distance = 0;

	}
	
	public void turn(float turningVelocity){
		currentTurningVel = turningVelocity;
	}
    
    public void move(float acceleration) {
        currentAcceleration = acceleration;
        moving = true;
    }
	
	
	/**
	 * @return
	 */
	public float getMaxVelocity() {
		return maxVelocity;
	}

	/**
	 * @return
	 */
	public float getMinVelocity() {
		return minVelocity;
	}

	/**
	 * @return
	 */
	public boolean isMoving() {
		return moving;
	}

	/**
	 * @return
	 */
	public float getPrevVelocity() {
		return prevVelocity;
	}

	/**
	 * @return
	 */
	public float getTurningVelocity() {
		return turningVelocity;
	}

	/**
	 * @param f
	 */
	public void setMaxVelocity(float f) {
		maxVelocity = f;
	}

	/**
	 * @param f
	 */
	public void setMinVelocity(float f) {
		minVelocity = f;
	}

	/**
	 * @param b
	 */
	public void setMoving(boolean b) {
		moving = b;
	}

	/**
	 * @param f
	 */
	public void setPrevVelocity(float f) {
		prevVelocity = f;
	}

	/**
	 * @param f
	 */
	public void setTurningVelocity(float f) {
		turningVelocity = f;
	}

	/**
	 * @return
	 */
	public float getAcceleration() {
		return acceleration;
	}

	/**
	 * @return
	 */
	public float getCurrentAngle() {
		return currentAngle;
	}

	/**
	 * @return
	 */
	public float getCurrentVelocity() {
		return currentVelocity;
	}

	/**
	 * @return
	 */
	public float getDeceleration() {
		return deceleration;
	}

	/**
	 * @return
	 */
	public float getDistance() {
		return distance;
	}

	/**
	 * @param f
	 */
	public void setAcceleration(float f) {
		acceleration = f;
	}

	/**
	 * @param f
	 */
	public void setCurrentAngle(float f) {
		currentAngle = f;
	}

	/**
	 * @param f
	 */
	public void setCurrentVelocity(float f) {
		currentVelocity = f;
	}

	/**
	 * @param f
	 */
	public void setDeceleration(float f) {
		deceleration = f;
	}

	/**
	 * @param f
	 */
	public void setDistance(float f) {
		distance = f;
	}
    
    public float getCurrentTurningVel() {
        return currentTurningVel;
    }
}