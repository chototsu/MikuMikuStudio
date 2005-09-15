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

package com.jme.bounding;

import java.io.Serializable;
import java.nio.FloatBuffer;

import com.jme.math.Plane;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;

/**
 * <code>BoundingVolume</code> defines an interface for dealing with
 * containment of a collection of points.
 * 
 * @author Mark Powell
 * @version $Id: BoundingVolume.java,v 1.9 2005-09-15 23:01:27 Mojomonkey Exp $
 */
public interface BoundingVolume extends Serializable {
	
	public static final int BOUNDING_SPHERE = 0;
	public static final int BOUNDING_BOX = 1;
	public static final int BOUNDING_OBB = 2;
	public static final int BOUNDING_OBB2 = 3;
	
	/**
	 * getType returns the type of bounding volume this is.
	 */
	public int getType();

	/**
	 * 
	 * <code>transform</code> alters the location of the bounding volume by a
	 * rotation, translation and a scalar.
	 * 
	 * @param rotate
	 *            the rotation to affect the bound.
	 * @param translate
	 *            the translation to affect the bound.
	 * @param scale
	 *            the scale to resize the bound.
	 * @return the new bounding volume.
	 */
	public BoundingVolume transform(Quaternion rotate, Vector3f translate,
			Vector3f scale);

	/**
	 * 
	 * <code>transform</code> alters the location of the bounding volume by a
	 * rotation, translation and a scalar.
	 * 
	 * @param rotate
	 *            the rotation to affect the bound.
	 * @param translate
	 *            the translation to affect the bound.
	 * @param scale
	 *            the scale to resize the bound.
	 * @param store
	 *            sphere to store result in
	 * @return the new bounding volume.
	 */
	public BoundingVolume transform(Quaternion rotate, Vector3f translate,
			Vector3f scale, BoundingVolume store);

	/**
	 * 
	 * <code>whichSide</code> returns the side on which the bounding volume
	 * lies on a plane. Possible values are POSITIVE_SIDE, NEGATIVE_SIDE, and
	 * NO_SIDE.
	 * 
	 * @see com.jme.scene.Point
	 * @param plane
	 *            the plane to check against this bounding volume.
	 * @return the side on which this bounding volume lies.
	 */
	public int whichSide(Plane plane);

	/**
	 * 
	 * <code>computeFromPoints</code> generates a bounding volume that
	 * encompasses a collection of points.
	 * 
	 * @param points
	 *            the points to contain.
	 */
	public void computeFromPoints(FloatBuffer points);

	/**
	 * <code>merge</code> combines two bounding volumes into a single bounding
	 * volume that contains both this bounding volume and the parameter volume.
	 * 
	 * @param volume
	 *            the volume to combine.
	 * @return the new merged bounding volume.
	 */
	public BoundingVolume merge(BoundingVolume volume);

	/**
	 * <code>mergeLocal</code> combines two bounding volumes into a single
	 * bounding volume that contains both this bounding volume and the parameter
	 * volume. The result is stored locally.
	 * 
	 * @param volume
	 *            the volume to combine.
	 * @return this
	 */
	public BoundingVolume mergeLocal(BoundingVolume volume);

	/**
	 * <code>clone</code> creates a new BoundingVolume object containing the
	 * same data as this one.
	 * 
	 * @param store
	 *            where to store the cloned information. if null or wrong class,
	 *            a new store is created.
	 * @return the new BoundingVolume
	 */
	public Object clone(BoundingVolume store);

	/**
	 * <code>initCheckPlanes</code> resets the checkplanes to their standard
	 * order.
	 */
	public void initCheckPlanes();

	/**
	 * get the value for a given index in the checkplanes
	 * 
	 * @param index
	 * @return
	 */
	public int getCheckPlane(int index);

	/**
	 * set the value for a given index in the checkplanes
	 * 
	 * @param index
	 * @param value
	 */
	public void setCheckPlane(int index, int value);

	/**
	 * Reconstruct a visible mesh for the bound.
	 */
	public void recomputeMesh();

	/**
	 * Find the distance from the center of this Bounding Volume to the given
	 * point.
	 * 
	 * @param point
	 *            The point to get the distance to
	 * @return distance
	 */
	public float distanceTo(Vector3f point);

	/**
	 * This function stores the approximate center of the bounding volume into
	 * the store vector. For easy function usage, the store vector should be
	 * returned when the function is complete.
	 * 
	 * @param store
	 *            The vector to store the center in.
	 */
	public Vector3f getCenter(Vector3f store);

	/**
	 * determines if this bounding volume and a second given volume are
	 * intersecting. Intersecting being: one volume contains another, one volume
	 * overlaps another or one volume touches another.
	 * 
	 * @param bv
	 *            the second volume to test against.
	 * @return true if this volume intersects the given volume.
	 */
	public boolean intersects(BoundingVolume bv);

	/**
	 * determines if a ray intersects this bounding volume.
	 * 
	 * @param ray
	 *            the ray to test.
	 * @return true if this volume is intersected by a given ray.
	 */
	public boolean intersects(Ray ray);

	/**
	 * determines if this bounding volume and a given bounding sphere are
	 * intersecting.
	 * 
	 * @param bs
	 *            the bounding sphere to test against.
	 * @return true if this volume intersects the given bounding sphere.
	 */
	public boolean intersectsSphere(BoundingSphere bs);

	/**
	 * determines if this bounding volume and a given bounding box are
	 * intersecting.
	 * 
	 * @param bb
	 *            the bounding box to test against.
	 * @return true if this volume intersects the given bounding box.
	 */
	public boolean intersectsBoundingBox(BoundingBox bb);

	/**
	 * determines if this bounding volume and a given oriented bounding box are
	 * intersecting.
	 * 
	 * @param obb
	 *            the bounding box to test against.
	 * @return true if this volume intersects the given oriented bounding box.
	 */
	public boolean intersectsOrientedBoundingBox(OrientedBoundingBox obb);

	/**
	 * determines if this bounding volume and a given oriented bounding box are
	 * intersecting.
	 * 
	 * @param obb
	 *            the bounding box to test against.
	 * @return true if this volume intersects the given oriented bounding box.
	 */
	public boolean intersectsOBB2(OBB2 obb);

}