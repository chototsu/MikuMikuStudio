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

import com.jme.math.Ray;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

/**
 * <code>Pick</code> provides functionality for determining if a ray has
 * intersected an object. This is useful for object selection, determining if a
 * weapon has hit it's target, calculating line of sight, etc. The methods of
 * <code>Pick</code> are static allowing for easy usage of the class.
 * 
 * @author Mark Powell
 * @version $Id: Pick.java,v 1.8 2004-09-10 22:36:08 mojomonkey Exp $
 */
public class Pick {

	private Pick() {
	}

	/**
	 * <code>doPick</code> takes a ray object and a scene graph node. The
	 * graph is traveled until it determines any leaf nodes have been hit, and
	 * those leafs that have been hit are added to the results. Using the scene
	 * graph allows for quick removal of branches, allowing for a quick and
	 * efficient test.
	 * 
	 * @param node
	 *            the scene graph.
	 * @param ray
	 *            the ray to test.
	 * @param results
	 *            contains the nodes that were hit by the ray.
	 */
	public static void doPick(Spatial node, Ray ray, PickResults results) {
		if (node.getWorldBound().intersects(ray)) {
			if ((node instanceof Node)) {
				Node parent = (Node) node;
				for (int i = 0; i < parent.getQuantity(); i++) {
					doPick(parent.getChild(i), ray, results);
				}
			} else {
				//find the triangle that is being hit.
				//add this node and the triangle to the PickResults list.
				results.addGeometry((Geometry) node);
			}
		}
	}

}