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

import jme.entity.camera.Frustum;
import jme.math.Vector;

/**
 * <code>BoundingBox</code> defines a bounding volume that contains
 * all vertices that make up the geometry.
 * 
 * @author Mark Powell
 * @version $Id: BoundingBox.java,v 1.6 2003-09-08 20:29:28 mojomonkey Exp $
 */
public class BoundingBox implements BoundingVolume {
	private Vector center;
	private Vector minPoint;
	private Vector maxPoint;

	/**
	 * Default constructor instantiates a new <code>BoundingBox</code> 
	 * object with default (0,0,0) vectors.
	 *
	 */
	public BoundingBox() {
		center = new Vector();
		minPoint = new Vector();
		maxPoint = new Vector();
	}

	/**
	 * Constructor creates a new <code>BoundingBox</code> object
	 * with the defined attributes.
	 * @param center the center of the box.
	 * @param minPoint the minimum point of the box.
	 * @param maxPoint the maximum point of the box.
	 */
	public BoundingBox(Vector center, Vector minPoint, Vector maxPoint) {
		if (null == center) {
			center = new Vector();
		} else {
			this.center = center;
		}

		if (null == minPoint) {
			this.minPoint = new Vector();
		} else {
			this.minPoint = minPoint;
		}

		if (null == maxPoint) {
			this.maxPoint = new Vector();
		} else {
			this.maxPoint = maxPoint;
		}
	}

	/**
	 * <code>axisAligned</code> creates a minimal box around all
	 * supplied points. The orientation is always aligned with the
	 * local entity's coordinate system and therefore is axis 
	 * aligned.
	 * @param points the list of points to contain.
	 */
	public void axisAligned(Vector[] points) {
		minPoint = points[0];
		maxPoint = minPoint;

		for (int i = 1; i < points.length; i++) {
			if (points[i].x < minPoint.x)
				minPoint.x = points[i].x;
			else if (points[i].x > maxPoint.x)
				maxPoint.x = points[i].x;

			if (points[i].y < minPoint.y)
				minPoint.y = points[i].y;
			else if (points[i].y > maxPoint.y)
				maxPoint.y = points[i].y;

			if (points[i].z < minPoint.z)
				minPoint.z = points[i].z;
			else if (points[i].z > maxPoint.z)
				maxPoint.z = points[i].z;
		}
	}

	/**
	 * <code>getMinPoint</code> returns the minPoint or minimum point of 
	 * the bounding box.
	 * @return the minPoint point of the box.
	 */
	public Vector getMinPoint() {
		return minPoint;
	}

	/**
	 * <code>setMinPoint</code> sets the minPoint or maximum point of the 
	 * bounding box.
	 * @param minPoint the new minPoint point of the box.
	 */
	public void setMinPoint(Vector minPoint) {
		this.minPoint = minPoint;
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
	 * <code>getMaxPoint</code> returns the maxPoint of maximum point of 
	 * the box.
	 * @return the maxPoint of the box.
	 */
	public Vector getMaxPoint() {
		return maxPoint;
	}

	/**
	 * <code>setMaxPoint</code> sets the new maximum point of the box.
	 * @param maxPoint the new maxPoint of the box.
	 */
	public void setMaxPoint(Vector maxPoint) {
		this.maxPoint = maxPoint;
	}
    
    public boolean hasCollision(BoundingVolume volume) {
        return false;
    }
    
    public float distance(BoundingVolume volume) {
        return -1.0f;
    }
    
    public boolean isVisible(Frustum frustum) {
        return true;
    }

}
