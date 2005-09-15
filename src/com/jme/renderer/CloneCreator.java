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

package com.jme.renderer;

import java.util.HashMap;
import java.util.Stack;

import com.jme.animation.SpatialTransformer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.system.JmeException;

/**
 * Started Date: Sep 16, 2004 <br>
 * <br>
 * This class controlls how a Spatial should be cloned. Users add properties to
 * a CloneCreator object, along with the Spatial they want to clone. Then, calls
 * to createCopy() return a spatial that is the clone of the spatial passed to
 * this object's Constructor. Spatial objects must define Spatial.putClone() in
 * order to be cloned.
 * 
 * @author Jack Lindamood
 */
public class CloneCreator {

    /** Contains properties that control how things are cloned. */
    protected HashMap props = new HashMap();

    /** Static count of total number of Cloned Geometry objects */
    protected static int count = 0;

    /** Maps Geometry objects to a clone ID for this CloneCreator object. */
    protected HashMap meshToCopyID = new HashMap();

    /**
     * Maps original Spatials to their Copy. Usefull for reasigning copied
     * Controllers.
     */
    public HashMap originalToCopy = new HashMap();

    /** The spatial that is getting copied. */
    protected Spatial toCopy;

    /** True to signal that a copy command has been issued by this object. */
    protected boolean setCreator = false;

    /** Stack of to-be-processed Spatial Controllers. */
    protected Stack sTrans = new Stack();

    

    /**
     * Creates a new CloneCreator that will make clones of the given spatial.
     * 
     * @param toCopy
     *            The Spatial to copy.
     */
    public CloneCreator(Spatial toCopy) {
        this.toCopy = toCopy;
    }

    /**
     * Adds a Clone property that the method createCopy() should watch for when
     * making copies of the original spatial and its children.
     * 
     * @param property
     *            The property to watch for.
     */
    public void addProperty(String property) {
        if (setCreator)
                throw new JmeException(
                        "Cannot add a property to a CloneCreator that has already created a Clone");
        props.put(property, Boolean.TRUE);
    }

    /**
     * Removes a set Clone property for this CloneCreator.
     * 
     * @param property
     *            The property to remove.
     */
    public void removeProperty(String property) {
        if (setCreator)
                throw new JmeException(
                        "Cannot remove a property from a CloneCreator that has already created a Clone");
        props.remove(property);
    }

    /**
     * Returns true if the given property is set.
     * 
     * @param prop
     *            The property to check for.
     * @return True if it is set.
     */
    public boolean isSet(String prop) {
        return props.containsKey(prop);
    }

    /**
     * Creates a copy of the original and returns the copy.
     * 
     * @return
     */
    public Spatial createCopy() {
        setCreator = true;
        originalToCopy.clear();
        Spatial toReturn = toCopy.putClone(null, this);
        //processJointStack();
        processSpatialStack();
        //processKeyframeStack();
        return toReturn;
    }

    

    /**
     * Reasigns the original Spatial toCopy's SpatialController to now animate
     * the copy.
     */
    protected void processSpatialStack() {
        while (!sTrans.empty()) {
            SpatialTransformer st = (SpatialTransformer) sTrans.pop();
            for (int i = 0; i < st.toChange.length; i++) {
                Spatial original = (Spatial) st.toChange[i];
                Spatial copy = (Spatial) originalToCopy.get(original);
                if (copy == null)
                        throw new JmeException(
                                "Unable to match up copy for Spatial "
                                        + original.getName());

                st.toChange[i] = copy;
            }
        }
    }

    /**
     * Signals that a SpatialTransformer needs to be processed after the clone
     * happens. This should not be called by users directly. It is called by the
     * cloned SpatialTransformer automatically.
     * 
     * @param st
     *            The SpatialTransformer that will later be processed.
     */
    public void queueSpatialTransformer(SpatialTransformer st) {
        sTrans.add(st);
    }

    

    /**
     * Creates a Clone ID for the given geometry object. This ID is applied to
     * any Geometry that are clones of this geometry from this CloneCreator.
     * Users should have little use for calling this directly. Instead, let it
     * be called automatically by the Geometry object durring Clone calls.
     * 
     * @param geometry
     *            The geometry object that needs to be cloned.
     */
    public void createCloneID(Geometry geometry) {
        if (CloneIDExist(geometry)) return;
        meshToCopyID.put(geometry, new Integer(CloneCreator.count));
        CloneCreator.count++;
    }

    /**
     * Returns the Clone ID of a geometry original. Users should have little use
     * for calling this directly. Instead, let it be called automatically by the
     * Geometry object durring Clone calls.
     * 
     * @param geometry
     *            The original.
     * @return The original's ID.
     */
    public int getCloneID(Geometry geometry) {
        return ((Integer) meshToCopyID.get(geometry)).intValue();
    }

    /**
     * Returns true if a CloneID exist for the given geometry. Users should have
     * little use for calling this directly. Instead, let it be called
     * automatically by the Geometry object durring Clone calls.
     * 
     * @param geometry
     *            The geometry original to check.
     * @return True if a Clone ID exist for that geometry.
     */
    public boolean CloneIDExist(Geometry geometry) {
        return meshToCopyID.containsKey(geometry);
    }
}