/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.intersection;

import java.util.ArrayList;

import com.jme.math.Ray;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;

/**
 * TrianglePickResults creates a PickResults object that calculates picking to
 * the triangle accuracy. PickData objects are added to the pick list as they
 * happen, these data objects refer to the two meshes, as well as their triangle
 * lists. While TrianglePickResults defines a processPick method, it is empty
 * and should be further defined by the user if so desired.
 * 
 * NOTE: Only TriMesh objects may obtain triangle accuracy, all others will
 * result in Bounding accuracy.
 * 
 * @author Mark Powell
 * @version $Id: TrianglePickResults.java,v 1.2 2004/10/14 01:23:12 mojomonkey
 *          Exp $
 */
public class TrianglePickResults extends PickResults {

	/**
	 * <code>addPick</code> adds a geometry object to the pick list. If the
	 * Geometry object is not a TriMesh, the process stops here. However, if the
	 * Geometry is a TriMesh, further processing occurs to obtain the triangle
	 * lists that the ray passes through.
	 * 
	 * @param ray the ray that is doing the picking.
	 * @param s the geometry to add to the pick list.
	 * 
	 * @see com.jme.intersection.PickResults#addPick(com.jme.math.Ray,
	 *      com.jme.scene.Geometry)
	 */
	public void addPick(Ray ray, Geometry s) {
		ArrayList a = new ArrayList();
		//find the triangle that is being hit.
		//add this node and the triangle to the CollisionResults
		// list.
		if (!(s instanceof TriMesh)) {
			PickData data = new PickData(ray, s);
			addPickData(data);
		} else {
			((TriMesh) s).findTrianglePick(ray, a);
			PickData data = new PickData(ray, s, a);
			addPickData(data);
		}
	}

	/**
	 * <code>processPick</code> will handle processing of the pick list. This
	 * is very application specific and therefore left as an empty method. 
	 * Applications wanting an automated picking system should extend 
	 * TrianglePickResults and override this method.
	 * 
	 * @see com.jme.intersection.PickResults#processPick()
	 */
	public void processPick() {

	}

}