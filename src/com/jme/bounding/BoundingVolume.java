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
package com.jme.bounding;

import com.jme.math.Quaternion;
import com.jme.math.Plane;
import com.jme.math.Vector3f;

/**
 * <code>BoundingVolume</code> defines an interface for dealing with containment
 * of a collection of points.
 * @author Mark Powell
 * @version $Id: BoundingVolume.java,v 1.1 2004-04-02 15:51:52 mojomonkey Exp $
 */
public interface BoundingVolume {

    /**
     *
     * <code>transform</code> alters the location of the bounding volume by
     * a rotation, translation and a scalar.
     * @param rotate the rotation to affect the bound.
     * @param translate the translation to affect the bound.
     * @param scale the scale to resize the bound.
     * @return the new bounding volume.
     */
    public BoundingVolume transform(Quaternion rotate,
            Vector3f translate, float scale);
    /**
     *
     * <code>transform</code> alters the location of the bounding volume by
     * a rotation, translation and a scalar.
     * @param rotate the rotation to affect the bound.
     * @param translate the translation to affect the bound.
     * @param scale the scale to resize the bound.
     * @param store sphere to store result in
     * @return the new bounding volume.
     */
    public BoundingVolume transform(
        Quaternion rotate,
        Vector3f translate,
        float scale,
        BoundingVolume store);

    /**
     *
     * <code>whichSide</code> returns the side on which the bounding volume
     * lies on a plane. Possible values are POSITIVE_SIDE, NEGATIVE_SIDE, and
     * NO_SIDE.
     * @see com.jme.scene.Point
     * @param plane the plane to check against this bounding volume.
     * @return the side on which this bounding volume lies.
     */
    public int whichSide(Plane plane);

    /**
     *
     * <code>computeFromPoints</code> generates a bounding volume that
     * encompasses a collection of points.
     * @param points the points to contain.
     */
    public void computeFromPoints(Vector3f[] points);

    /**
     * <code>merge</code> combines two bounding volumes into a single bounding
     * volume that contains both this bounding volume and the parameter volume.
     * @param volume the volume to combine.
     * @return the new merged bounding volume.
     */
    public BoundingVolume merge(BoundingVolume volume);

    /**
     * <code>mergeLocal</code> combines two bounding volumes into a single bounding
     * volume that contains both this bounding volume and the parameter volume.
     * The result is stored locally.
     * @param volume the volume to combine.
     * @return this
     */
    public BoundingVolume mergeLocal(BoundingVolume volume);

    /**
     * <code>clone</code> creates a new BoundingVolume object containing the same
     * data as this one.
     * @param store where to store the cloned information.  if null or wrong class, a new store is created.
     * @return the new BoundingVolume
     */
    public Object clone(BoundingVolume store);

    /**
     * <code>initCheckPlanes</code> resets the checkplanes to their standard order.
     */
    public void initCheckPlanes();

    /**
     * get the value for a given index in the checkplanes
     * @param index
     * @return
     */
    public int getCheckPlane(int index);

    /**
     * set the value for a given index in the checkplanes
     * @param index
     * @param value
     */
    public void setCheckPlane(int index, int value);

    /**
     * Reconstruct a visible mesh for the bound.
     */
    public void recomputeMesh();

}
