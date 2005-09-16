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

package com.jme.util.geom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;

/**
 * <code>NormalDebugger</code> is a simple utility allowing you to quickly see
 * the normals on a given section of the scenegraph. Lines are added for every
 * normal in the selected section of the scenegraph. These lines are added to
 * the node that contains the geometry for which they represent the normals,
 * thus allowing them to follow any transformation of any of the nodes between
 * the geometry and the root.
 *
 * <p>
 * To add lines for a section of the scenegraph (either a node or a geometry):
 * <pre>
 *   NormalDebugger.getInstance().register(section);
 * </pre>
 *
 * To remove the lines for the section:
 * <pre>
 *   NormalDebugger.getInstance().unregister(section);
 * </pre>
 * 
 * @author Emond Papegaaij
 * @version $Id: NormalDebugger.java,v 1.2 2005-09-16 21:36:26 Mojomonkey Exp $
 */
public class NormalDebugger {
	private static NormalDebugger INSTANCE;

	private Map lineNodes = new HashMap();
	private Map children = new HashMap();

	/**
	 * Hide the default constructor as <code>NormalDebugger</code> is singleton.
	 */
	private NormalDebugger() {
	}

	/**
	 * Returns the singleton instance of the <code>NormalDebugger</code>. It is
	 * safe to get the instance before the initialization of the display system.
	 * @return The singleton instance of the <code>NormalDebugger</code>, which
	 * will be created on the first call.
	 */
	public static NormalDebugger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NormalDebugger();
		}
		return INSTANCE;
	}

	/**
	 * Internal method that adds the normal lines to the given Geometry. The lines
	 * will be added to a node, which will be added to the parent node of the
	 * Geometry.
	 */
	private void addNormalsToParent(Geometry g) {
		Node lineNode = new Node("Normals for "+g.getName());
		g.getParent().attachChild(lineNode);
		Vector3f[] vertices = BufferUtils.getVector3Array(g.getVertexBuffer());
		Vector3f[] normals = BufferUtils.getVector3Array(g.getNormalBuffer());
		if (normals == null || normals.length != vertices.length) {
			return;
		}

		for (int count=0; count<vertices.length; count++) {
			Vector3f curVertex = vertices[count];
			Vector3f curNormal = normals[count];

			Line line = new Line("Normal "+count, new Vector3f[] {
				new Vector3f(curVertex), new Vector3f(curVertex).addLocal(curNormal)},
				null, null, null);
			line.setLightCombineMode(LightState.OFF);
			line.setSolidColor(ColorRGBA.white);
			line.setLocalTranslation(g.getLocalTranslation());
			line.setLocalScale(g.getLocalScale());
			line.setLocalRotation(g.getLocalRotation());
			lineNode.attachChild(line);
			line.updateRenderState();
		}
		lineNodes.put(g, lineNode);
	}

	/**
	 * Internal method that recursivelly processes a node by adding the normal
	 * lines to all children of the node.
	 */
	private void processNode(Node nodeToProcess) {
		List nodeChildren = new ArrayList();
		children.put(nodeToProcess, nodeChildren);

		Iterator it = new ArrayList(nodeToProcess.getChildren()).iterator();
		while (it.hasNext()) {
			Spatial curChild = (Spatial) it.next();
			nodeChildren.add(curChild);
			if (curChild instanceof Node) {
				processNode((Node) curChild);
			} else if (curChild instanceof Geometry) {
				addNormalsToParent((Geometry) curChild);
			}
		}
	}

	/**
	 * Registers the given <code>Geometry</code> for normal debugging. The normal
	 * lines will be drawn immediately. Because the lines will be added to the
	 * parent node of the <code>Geometry</code>, it is an error to call this
	 * method with a <code>Geometry</code> that is not yet attached.
	 * @param g The <code>Geometry</code> to add the normals to.
	 */
	public void register(Geometry g) {
		addNormalsToParent(g);
	}

	/**
	 * Registers the given <code>Node</code> for normal debugging. Registering a
	 * <code>Node</code> will result in all children of the <code>Node</code>
	 * being registered. The normal lines will be drawn immediately.
	 * @param n The <code>Node</code> that contains the geometries to add the
	 * normals to.
	 */
	public void register(Node n) {
		processNode(n);
	}

	/**
	 * Registers the given <code>Spatial</code> for normal debugging. This method
	 * is provided for convenience and will simply call the register method for
	 * either a <code>Geometry</code> or a <code>Node</code>.
	 * @param s The <code>Spatial</code> to register.
	 */
	public void register(Spatial s) {
		if (s instanceof Geometry) {
			register((Geometry) s);
		} else if (s instanceof Node) {
			register((Node) s);
		}
	}

	/**
	 * Unregisters the given <code>Geometry</code> for normal debugging. This will
	 * remove all normal lines immediately. When the given <code>Geometry</code>
	 * is currently not registered, nothing is done.
	 * @param g The <code>Geometry</code> to unregister.
	 */
	public void unregister(Geometry g) {
		Node lines = (Node)lineNodes.remove(g);
		if (lines != null) {
			lines.removeFromParent();
		}
	}

	/**
	 * Unregisters the given <code>Node</code> for normal debugging. This will
	 * recursively unregister all children of the <code>Node</code>. When the
	 * given <code>Node</code> is currently not registered, nothing is done.
	 * @param n The <code>Node</code> to unregister.
	 */
	public void unregister(Node n) {
		List nodeChildren = (List)children.remove(n);
		if (nodeChildren != null) {
			for (int x = 0; x < nodeChildren.size(); x++) {
			    Spatial curSpatial = (Spatial)nodeChildren.get(x);
				unregister(curSpatial);
			}
		}
	}

	/**
	 * Unregisters the given <code>Spatial</code> for normal debugging. This
	 * method is provided for convenience and will simply call the unregister
	 * method for either a <code>Geometry</code> or a <code>Node</code>.
	 * @param s The <code>Spatial</code> to unregister.
	 */
	public void unregister(Spatial s) {
		if (s instanceof Geometry) {
			unregister((Geometry) s);
		} else if (s instanceof Node) {
			unregister((Node) s);
		}
	}

	/**
	 * Updates the normals for the given <code>Spatial</code>. Currently this will
	 * result in all normals being removed and recreated. Calling this method
	 * should only be needed when one of the local transformations of a
	 * <code>Geometry</code> have changed and/or new objects were added/removed.
	 * @param s The <code>Spatial</code> to update.
	 */
	public void update(Spatial s) {
		unregister(s);
		register(s);
	}
}
