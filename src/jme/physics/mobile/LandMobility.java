/*
 * Created on Jul 10, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package jme.physics.mobile;

import org.lwjgl.vector.Vector3f;

/**
 * @author mpowell
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LandMobility {
	private boolean movingBack;
	private boolean leftTurn;
	private boolean rightTurn;
	float maxVelocity;
	float minVelocity;
	float acceleration;
	float deceleration;
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
	}

	public void update(float time) {
		if(movingBack) {
			currentVelocity = prevVelocity - acceleration * time;
			if(currentVelocity < minVelocity) {
				currentVelocity = minVelocity;
			}
		}else if(moving) {
			currentVelocity = prevVelocity + acceleration * time;
			if(currentVelocity > maxVelocity) {
				currentVelocity = maxVelocity;
			}
		} else {
			currentVelocity = currentVelocity - deceleration * time;
			if(currentVelocity < 0) {
				currentVelocity = 0;
			}
		}
		
		if(rightTurn) {
			currentAngle -= turningVelocity * time;
		} else if(leftTurn) {
			currentAngle += turningVelocity * time;
		}
		prevVelocity = currentVelocity;
		
		if(time < Float.MAX_VALUE) {
			distance += currentVelocity * time;
		}
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
	
	public void turnRight() {
		rightTurn = true;
		leftTurn = false;
	}
	
	public void turnLeft() {
		rightTurn = false;
		leftTurn = true;
	}
	
	public void noTurn() {
		rightTurn = false;
		leftTurn = false;
	}
	
	public void accelerate() {
		moving = true;
	}
	
	public void decelerate() {
		moving = false;
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

	/**
	 * @return
	 */
	public boolean isLeftTurn() {
		return leftTurn;
	}

	/**
	 * @return
	 */
	public boolean isRightTurn() {
		return rightTurn;
	}

	/**
	 * @param b
	 */
	public void setLeftTurn(boolean b) {
		leftTurn = b;
	}

	/**
	 * @param b
	 */
	public void setRightTurn(boolean b) {
		rightTurn = b;
	}

	/**
	 * 
	 */
	public void backward() {
		movingBack = true;
		
	}

	/**
	 * 
	 */
	public void stop() {
		moving = false;
		movingBack = false;
		currentVelocity = 0;
		
	}

}