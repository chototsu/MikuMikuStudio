/*
 * Copyright (c) 2003-2005 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.math;

/**
 * <code>SpringNode</code> defines a single node in a SpringSystem.
 * @author Joshua Slack
 * @version $Id: SpringNode.java,v 1.2 2005-09-15 17:13:45 renanse Exp $
 */
public class SpringNode {

	/**
	 * index of this node in the system.  Needs to be set by the programmer.
	 * can be useful for derivatives of SpringNodeForce that may apply force
	 * differently to different nodes based on location in the matrix.
	 */
	public int index = 0;
	/** Mass of this node. */
	public float mass = 1;
	/** Inverse Mass of this node. */
	public float invMass = 1;
	/** Position of this node in space. */
	public Vector3f position;
	/** Previous Position of this node in space. */
	public Vector3f oldPos;
	/** Acceleration vector, zeroed and recalculated on each SpringSystem.calcForces(float). */
	public Vector3f acceleration;

	/**
	 * Public constructor.
	 * @param pos Vertex position of this node.
	 */
	public SpringNode(Vector3f pos) {
		position = pos;
		oldPos = new Vector3f(pos);
		acceleration = new Vector3f(0, 0, 0);
	}

	/**
	 * Set the mass for this node.  Also calculates and stores the inverse
	 * mass to invMass field for future use.
	 * @param m float
	 */
	public void setMass(float m) {
		mass = m;
		if (m == Float.POSITIVE_INFINITY || m == Float.NEGATIVE_INFINITY)
			invMass = 0;
		else if (m == 0)
			invMass = Float.POSITIVE_INFINITY;
		else
			invMass = 1f / m;
	}

	/**
	 * Verlet update of node location.  Pretty stable.  Updates position
	 * by using implied velocity derived from the distance travled since
	 * last update.  Thus velocity and position do not get out of sync.
	 * @param dt float - change in time since last update.
	 */
	public void update(float dt) {
		float dtSquared = dt * dt;
		if (invMass == 0) return;
		float x = position.x, y = position.y, z = position.z;
		position.set(
				2*position.x - oldPos.x + acceleration.x * dtSquared,
				2*position.y - oldPos.y + acceleration.y * dtSquared,
				2*position.z - oldPos.z + acceleration.z * dtSquared);
		oldPos.set(x, y, z);
	}
}
