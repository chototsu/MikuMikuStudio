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
package com.jme.scene;

import com.jme.math.Matrix3f;
import com.jme.math.Plane;
import com.jme.math.Vector3f;

/**
 * <code>BoundingBox</code> defines a box that contains a collection of 
 * vertices. The box is defined by a center and a minimum and maximum point. The
 * box will always be axis aligned. 
 * @author Mark Powell
 * @version $Id: BoundingBox.java,v 1.2 2004-02-19 21:23:24 mojomonkey Exp $
 */
public class BoundingBox implements BoundingVolume {
    private Vector3f center;
    private Vector3f min;
    private Vector3f max;
    
    /**
     * Constructor instantiates a new <code>BoundingBox</code> object.
     *
     */
    public BoundingBox() {
        center = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
    }

    /**
     * Constructor instantiates a new <code>BoundingBox</code> object. The
     * box is defined by a minimum and maximum point.
     * @param center the center of the box.
     * @param min the minimum point of the box.
     * @param max the maximum point of the box.
     */
    public BoundingBox(Vector3f center, Vector3f min, Vector3f max) {
        this.center = center;
        this.min = min;
        this.max = max;
    }
    
   
    public Vector3f getMin() {
        return min;
    }

    public Vector3f getMax() {
        return max;
    }
    
    public Vector3f getCenter() {
        return center;
    }

    /**
     * <code>transform</code> alters the bounding box to correspond to the 
     * geometry it contains.
     * @see com.jme.scene.BoundingVolume#transform(com.jme.math.Matrix3f, com.jme.math.Vector3f, float)
     */
    public BoundingVolume transform(
        Matrix3f rotate,
        Vector3f translate,
        float scale) {

        Vector3f newCenter = ((rotate.mult(center)).mult(scale).add(translate));
        Vector3f newMin = ((rotate.mult(min)).mult(scale)).add(translate);
        Vector3f newMax = ((rotate.mult(max)).mult(scale)).add(translate);

        return new BoundingBox(newCenter, newMin, newMax);
    }
    
    /**
     * <code>transform</code> alters the bounding box to correspond to the 
     * geometry it contains.
     * @see com.jme.scene.BoundingVolume#transform(com.jme.math.Matrix3f, com.jme.math.Vector3f, float)
     */
    public BoundingVolume transform(
    		Matrix3f rotate,
			Vector3f translate,
			float scale,
			BoundingVolume bv) {
    	
    	if(bv instanceof BoundingBox) {

	    	Vector3f newCenter = ((rotate.mult(center)).mult(scale).add(translate));
	    	Vector3f newMin = ((rotate.mult(min)).mult(scale)).add(translate);
	    	Vector3f newMax = ((rotate.mult(max)).mult(scale)).add(translate);
	    	
	    	((BoundingBox)bv).setCenter(newCenter);
	    	((BoundingBox)bv).setMin(newMin);
	    	((BoundingBox)bv).setMax(newMax);
    	}

    	return bv;
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#whichSide(com.jme.math.Plane)
     */
    public int whichSide(Plane plane) {
        float distance = plane.pseudoDistance(center);
        
        float radius = max.subtract(center).length();
        
        if (distance <= -radius) {
            return Plane.NEGATIVE_SIDE;
        } else if (distance >= radius) {
            return Plane.POSITIVE_SIDE;
        } else {
            return Plane.NO_SIDE;
        }
    }

    /**
     * <code>computeFromPoints</code> creates a new BoundingBox from
     * a given set of points. It uses the <code>axisAligned</code> method
     * as default.
     * @param points the points to contain.
     */
    public void computeFromPoints(Vector3f[] points) {
        axisAligned(points);
    }

    /**
     * <code>axisAligned</code> creates a minimal box around all
     * supplied points. The orientation is always aligned with the
     * local entity's coordinate system and therefore is axis 
     * aligned.
     * @param points the list of points to contain.
     */
    public void axisAligned(Vector3f[] points) {
        min.x = points[0].x;
        min.y = points[0].y;
        min.z = points[0].z;
        max.x = min.x;
        max.y = min.y;
        max.z = min.z;

        for (int i = 1; i < points.length; i++) {
            if (points[i].x < min.x)
                min.x = points[i].x;
            else if (points[i].x > max.x)
                max.x = points[i].x;

            if (points[i].y < min.y)
                min.y = points[i].y;
            else if (points[i].y > max.y)
                max.y = points[i].y;

            if (points[i].z < min.z)
                min.z = points[i].z;
            else if (points[i].z > max.z)
                max.z = points[i].z;
        }

        center.x = (max.x + min.x) / 2;
        center.y = (max.y + min.y) / 2;
        center.z = (max.z + min.z) / 2;
    }

    public boolean contains(BoundingBox box) {
        //if this min is less than or equal to box's min and
        //if this max is greater than or equal to box's max
        if ((min.x <= box.getMin().x
            && min.y <= box.getMin().x
            && min.z <= box.getMin().z)
            && (max.x >= box.getMax().x
                && max.y >= box.getMax().y
                && max.z >= box.getMax().z)) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see com.jme.scene.BoundingVolume#merge(com.jme.scene.BoundingVolume)
     */
    public BoundingVolume merge(BoundingVolume volume) {
        if (!(volume instanceof BoundingBox)) {
            return this;
        }

        BoundingBox testBox = (BoundingBox) volume;
        if (contains(testBox)) {
            return this;
        } else if (testBox.contains(this)) {
            return testBox;
        }

        Vector3f newMin = new Vector3f();
        Vector3f newMax = new Vector3f();
        //otherwise:
        //new sphere is the average of the two centers
        //smallest min and largest max.
        if (min.x < testBox.getMin().x) {
            newMin.x = min.x;
        } else {
            newMin.x = testBox.getMin().x;
        }

        //find min point
        if (min.y < testBox.getMin().y) {
            newMin.y = min.y;
        } else {
            newMin.y = testBox.getMin().y;
        }

        if (min.z < testBox.getMin().z) {
            newMin.z = min.z;
        } else {
            newMin.z = testBox.getMin().z;
        }

        if (max.x > testBox.getMax().x) {
            newMax.x = max.x;
        } else {
            newMax.x = testBox.getMax().x;
        }

        //find max point
        if (max.y > testBox.getMax().y) {
            newMax.y = max.y;
        } else {
            newMax.y = testBox.getMax().y;
        }

        if (max.z > testBox.getMax().z) {
            newMax.z = max.z;
        } else {
            newMax.z = testBox.getMax().z;
        }

        Vector3f newCenter = new Vector3f();
        newCenter.x = (newMax.x + newMin.x) / 2;
        newCenter.y = (newMax.y + newMin.y) / 2;
        newCenter.z = (newMax.z + newMin.z) / 2;

        return new BoundingBox(newCenter, newMin, newMax);
    }

	/**
	 * @param center The center to set.
	 */
	public void setCenter(Vector3f center) {
		this.center = center;
	}

	/**
	 * @param max The max to set.
	 */
	public void setMax(Vector3f max) {
		this.max = max;
	}

	/**
	 * @param min The min to set.
	 */
	public void setMin(Vector3f min) {
		this.min = min;
	}

}
