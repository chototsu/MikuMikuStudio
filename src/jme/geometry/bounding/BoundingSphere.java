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

package jme.geometry.bounding;

import jme.math.Vector;

/**
 * <code>BoundingSphere.java</code> defines a sphere that defines a container 
 * for a group of vertices of a particular piece of geometry. This sphere 
 * defines a radius and a center. This origin is translated from the containing 
 * entity's position.
 * 
 * @author Mark Powell
 * @version 1
 */
public class BoundingSphere {
    private float radius;
    private Vector center;
    
    /**
     * Default contstructor instantiates a new <code>BoundingSphere</code>
     * object. 
     */
    public BoundingSphere() {
    	center = new Vector();
    }
    
    /**
     * Constructor instantiates a new <code>BoundingSphere</code> object.
     * @param radius the radius of the sphere.
     * @param center the center of the sphere.
     */
    public BoundingSphere(float radius, Vector center) {
    	if(null == center) {
    		this.center = new Vector();
    	} else {
    		this.center = center;
    	}
        this.radius = radius;
    }
    
    /**
     * <code>getRadius</code> returns the radius of the bounding sphere.
     * @return the radius of the bounding sphere.
     */
    public float getRadius() {
        return radius;
    }
    
    /**
     * <code>getCenter</code> returns the center of the bounding sphere.
     * @return the center of the bounding sphere.
     */
    public Vector getCenter() {
    	return center;
    }
    
    /**
     * <code>setRadius</code> sets the radius of this bounding sphere.
     * @param radius the new radius of the bounding sphere.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

	/**
	 * <code>setCenter</code> sets the center of the bounding sphere.
	 * @param center the new center of the bounding sphere.
	 */
	public void setCenter(Vector center) {
		this.center = center;
	}
}
