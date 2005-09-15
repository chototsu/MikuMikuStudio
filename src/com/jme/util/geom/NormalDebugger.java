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
 * the normals on a given section of the scenegraph.
 * 
 * @author Emond Papegaaij
 * @version $Id: NormalDebugger.java,v 1.1 2005-09-15 21:29:58 renanse Exp $
 */
public class NormalDebugger {
	private static NormalDebugger INSTANCE;

	private Map lineNodes = new HashMap();
	private Map children = new HashMap();

	private NormalDebugger() {
	}

	public static NormalDebugger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NormalDebugger();
		}
		return INSTANCE;
	}

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

	public void register(Geometry g) {
		addNormalsToParent(g);
	}

	public void register(Node n) {
		processNode(n);
	}

	public void register(Spatial s) {
		if (s instanceof Geometry) {
			register((Geometry) s);
		} else if (s instanceof Node) {
			register((Node) s);
		}
	}

	public void unregister(Geometry g) {
		Node lines = (Node)lineNodes.remove(g);
		if (lines != null) {
			lines.removeFromParent();
		}
	}

	public void unregister(Node n) {
		List nodeChildren = (List)children.remove(n);
		if (nodeChildren != null) {
			for (int x = 0; x < nodeChildren.size(); x++) {
			    Spatial curSpatial = (Spatial)nodeChildren.get(x);
				unregister(curSpatial);
			}
		}
	}

	public void unregister(Spatial s) {
		if (s instanceof Geometry) {
			unregister((Geometry) s);
		} else if (s instanceof Node) {
			unregister((Node) s);
		}
	}

	public void update(Spatial s) {
		unregister(s);
		register(s);
	}
}
