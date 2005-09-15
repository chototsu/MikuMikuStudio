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
 * <code>SpringNodeForce</code> is an abstract class defining an external
 * force to be used with the SpringSystem class.
 *
 * @author Joshua Slack
 * @version $Id: SpringNodeForce.java,v 1.2 2005-09-15 17:13:46 renanse Exp $
 */
public abstract class SpringNodeForce {

	/**
	 * Is this force enabled?  ie, should it be used when calculating forces
	 * in a system it is added to.
	 */
	private boolean enabled = true;

	/**
	 * Set this force enabled or not.
	 * @param enabled boolean
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Return whether or not this system is enabled.
	 * @return boolean
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Apply the force defined by this class on a given node.
	 * Should do this by making a call to
	 * <i>node.acceleration.addLocal(....);</i>
	 * @param dt amount of time since last apply call in ms.
	 * @param node the node to apply the force to.
	 */
	public abstract void apply(float dt, SpringNode node);
}
