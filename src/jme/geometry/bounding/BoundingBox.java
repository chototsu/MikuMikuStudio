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

import jme.math.MathUtils;
import jme.math.Vector;

/**
 * <code>BoundingBox</code> defines a bounding volume that contains
 * all vertices that make up the geometry.
 * 
 * @author Mark Powell
 * @version $Id: BoundingBox.java,v 1.2 2003-08-07 21:24:37 mojomonkey Exp $
 */
public class BoundingBox {
	private Vector center;
	private Vector axis;
	private Vector extent;
	
	

	/**
	 * Default constructor instantiates a new <code>BoundingBox</code> 
	 * object with default (0,0,0) vectors.
	 *
	 */
	public BoundingBox() {
		center = new Vector();
		axis = new Vector();
		extent = new Vector();
	}
	
	/**
	 * Constructor creates a new <code>BoundingBox</code> object
	 * with the defined attributes.
	 * @param center the center of the box.
	 * @param axis the minimum point of the box.
	 * @param extent the maximum point of the box.
	 */
	public BoundingBox(Vector center, Vector axis, Vector extent) {
		if(null == center) {
			center = new Vector();
		} else {
			this.center = center;
		}
		
		if(null == axis) {
			this.axis = new Vector();
		} else {
			this.axis = axis;
		}
		
		if(null == extent) {
			this.extent = new Vector();
		} else {
			this.extent = extent;
		}
	}
	
	/**
	 * <code>getRadius</code> calculates the distance between
	 * the center point and the axis point.
	 * @return the distance between the center of the box and
	 * 		the axis point.
	 */
	public double getRadius() {
		return MathUtils.distance(center, axis);
	}
	
	/**
	 * <code>getAxis</code> returns the axis or minimum point of 
	 * the bounding box.
	 * @return the axis point of the box.
	 */
	public Vector getAxis() {
		return axis;
	}

	/**
	 * <code>setAxis</code> sets the axis or maximum point of the 
	 * bounding box.
	 * @param axis the new axis point of the box.
	 */
	public void setAxis(Vector axis) {
		this.axis = axis;
	}

	/**
	 * <code>getCenter</code> returns the center (in relation to the
	 * parent entity) of the bounding box.
	 * @return the center of the bounding box.
	 */
	public Vector getCenter() {
		return center;
	}

	/**
	 * <code>setCenter</code> sets the center of the bounding box.
	 * @param center the new center of the bounding box.
	 */
	public void setCenter(Vector center) {
		this.center = center;
	}

	/**
	 * <code>getExtent</code> returns the extent of maximum point of 
	 * the box.
	 * @return the extent of the box.
	 */
	public Vector getExtent() {
		return extent;
	}

	/**
	 * <code>setExtent</code> sets the new maximum point of the box.
	 * @param extent the new extent of the box.
	 */
	public void setExtent(Vector extent) {
		this.extent = extent;
	}
	
}
