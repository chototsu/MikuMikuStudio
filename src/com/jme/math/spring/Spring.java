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

package com.jme.math.spring;

import com.jme.math.Vector3f;

/**
 * <code>Spring</code> defines a single spring connecting two SpringNodes
 * in a SpringSystem.
 *
 * @author Joshua Slack
 * @version $Id: Spring.java,v 1.1 2005-10-12 16:56:13 Mojomonkey Exp $
 */
public class Spring {

	/** First node connected by this Spring. */
	public SpringPoint node1;
	/** Second node connected by this Spring. */
	public SpringPoint node2;
	/** Rest length of this Spring. */
	private float restLength = 1;
	/** The squared rest length of this spring */
	private float rlSquared = 1;
	/** The total mass of this spring */
	private float tMass = 1;
	/** Private vector used by Spring in update() method to avoid object creation. */
	private Vector3f delta = new Vector3f();

	/**
	 * Public constructor.
	 * @param node1 SpringNode
	 * @param node2 SpringNode
	 * @param restLength float
	 */
	public Spring(SpringPoint node1, SpringPoint node2, float restLength) {
		this.node1 = node1;
		this.node2 = node2;
		setRestLength(restLength);
		updateTotalMassFromNodes();
	}

	/**
	 * Set the rest length of this Spring.  Also, calculates and sets the
	 * squared rest length field.
	 *
	 * @param restLength float
	 */
	public void setRestLength(float restLength) {
		this.restLength = restLength;
		this.rlSquared = restLength * restLength;
	}

	/**
	 * Return the rest length of this Spring.
	 *
	 * @return float
	 */
	public float getRestLength() {
		return restLength;
	}

	/**
	 * Computes the spring collective mass from the node using inverted masses
	 * for stability
	 */
	public void updateTotalMassFromNodes() {
		tMass = 1f / (node1.invMass+node2.invMass);
	}

	/**
	 * Updates the positions of the nodes connected by this spring based on spring
	 * force calculations.  Relaxation method idea came from paper on physics system
	 * of Hitman game.
	 */
	public void update() {
		delta.set(node2.position).subtractLocal(node1.position);
		delta.multLocal(tMass-(2*rlSquared*tMass/(delta.lengthSquared()+rlSquared)));
		node1.position.addLocal(
				delta.x * node1.invMass,
				delta.y * node1.invMass,
				delta.z * node1.invMass);
		node2.position.subtractLocal(
				delta.x * node2.invMass,
				delta.y * node2.invMass,
				delta.z * node2.invMass);
	}
}
