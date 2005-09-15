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

package com.jme.intersection;

import java.util.ArrayList;

import com.jme.math.Ray;
import com.jme.scene.Geometry;

/**
 * <code>PickResults</code> contains information resulting from a pick test.
 * The results will contain a list of every node that was "struck" during a
 * pick test.
 *
 * @author Mark Powell
 * @version $Id: PickResults.java,v 1.7 2005-09-15 17:14:29 renanse Exp $
 */
public abstract class PickResults {

    private ArrayList nodeList;

    /**
     * Constructor instantiates a new <code>PickResults</code> object.
     */
    public PickResults() {
        nodeList = new ArrayList();
    }

    /**
     * <code>addGeometry</code> places a new <code>Geometry</code> spatial into the
     * results list.
     *
     * @param node the geometry to be placed in the results list.
     */
    public void addPickData(PickData data) {
        nodeList.add(data);
    }

    /**
     * <code>getNumber</code> retrieves the number of geometries that have been
     * placed in the results.
     *
     * @return the number of Geometry objects in the list.
     */
    public int getNumber() {
        return nodeList.size();
    }

    /**
     * <code>getGeometry</code> retrieves a Geometry from a specific index.
     *
     * @param i the index requested.
     * @return the Geometry at the specified index.
     */
    public PickData getPickData(int i) {
        return (PickData) nodeList.get(i);
    }

    /**
     * <code>clear</code> clears the list of all Geometry objects.
     */
    public void clear() {
        nodeList.clear();
    }
    
    public abstract void addPick(Ray ray,Geometry s);
	
	public abstract void processPick();
}
