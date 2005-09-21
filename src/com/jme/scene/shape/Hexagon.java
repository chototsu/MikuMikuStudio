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

package com.jme.scene.shape;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Hexagon</code> provides an extension of <code>TriMesh</code>. A
 * <code>Hexagon</code> provides a regular hexagon with each triangle having
 * side length that is given in the constructor.
 * 
 * @author Joel Schuster
 * @version $Id: Hexagon.java,v 1.6 2005-09-21 17:52:55 renanse Exp $
 */
public class Hexagon extends TriMesh {
	private static final long serialVersionUID = 1L;

	private static final int NUM_POINTS = 7;

	private static final int NUM_TRIS = 6;

	private float sideLength;

	/**
	 * Hexagon Constructor instantiates a new Hexagon. This element is center on
	 * 0,0,0 with all normals pointing up. The user must move and rotate for
	 * positioning.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param sideLength
	 *            The length of all the sides of the tiangles
	 */
	public Hexagon(String name, float sideLength) {
		super(name);
		this.sideLength = sideLength;

		// allocate vertices
		vertQuantity = NUM_POINTS;
		vertBuf = BufferUtils.createVector3Buffer(vertQuantity);
		normBuf = BufferUtils.createVector3Buffer(vertQuantity);
		texBuf[0] = BufferUtils.createVector2Buffer(vertQuantity);
		
		triangleQuantity = NUM_TRIS;
		indexBuffer = BufferUtils.createIntBuffer(3 * triangleQuantity);

		setVertexData();
		setIndexData();
		setTextureData();
		setNormalData();
	    setDefaultColor(ColorRGBA.white);

	}

	/**
	 * Vertexes are set up like this: 0__1 / \ / \ 5/__\6/__\2 \ / \ / \ /___\ /
	 * 4 3
	 * 
	 * All lines on this diagram are sideLength long. Therefore, the width of
	 * the hexagon is sideLength * 2, and the height is 2 * the height of one
	 * equalateral triangle with all side = sideLength which is .866
	 *  
	 */
	private void setVertexData() {
	    vertBuf.put(-(sideLength / 2)).put(sideLength * 0.866f).put(0.0f);
		vertBuf.put(sideLength / 2).put(sideLength * 0.866f).put(0.0f);
		vertBuf.put(sideLength).put(0.0f).put(0.0f);
		vertBuf.put(sideLength / 2).put(-sideLength * 0.866f).put(0.0f);
		vertBuf.put(-(sideLength / 2)).put(-sideLength * 0.866f).put(0.0f);
		vertBuf.put(-sideLength).put(0.0f).put(0.0f);
		vertBuf.put(0.0f).put(0.0f).put(0.0f);
	}

	/**
	 * Sets up the indexes of the mesh. These go in a clockwise fassion and thus
	 * only the 'up' side of the hex is visible. If you wish to have to either
	 * set two sided lighting or create two hexes back-to-back
	 *  
	 */

	private void setIndexData() {
	    indexBuffer.rewind();
		// tri 1
		indexBuffer.put(0);
		indexBuffer.put(1);
		indexBuffer.put(6);
		// tri 2
		indexBuffer.put(1);
		indexBuffer.put(2);
		indexBuffer.put(6);
		// tri 3
		indexBuffer.put(2);
		indexBuffer.put(3);
		indexBuffer.put(6);
		// tri 4
		indexBuffer.put(3);
		indexBuffer.put(4);
		indexBuffer.put(6);
		// tri 5
		indexBuffer.put(4);
		indexBuffer.put(5);
		indexBuffer.put(6);
		// tri 6
		indexBuffer.put(5);
		indexBuffer.put(0);
		indexBuffer.put(6);
	}

	private void setTextureData() {
	    texBuf[0].put(0.25f).put(0);
	    texBuf[0].put(0.75f).put(0);
	    texBuf[0].put(1.0f).put(0.5f);
	    texBuf[0].put(0.75f).put(1.0f);
	    texBuf[0].put(0.25f).put(1.0f);
	    texBuf[0].put(0.0f).put(0.5f);
	    texBuf[0].put(0.5f).put(0.5f);
	}

	/**
	 * Sets all the default vertex normals to 'up', +1 in the Z direction.
	 *  
	 */
	private void setNormalData() {
	    Vector3f zAxis = new Vector3f(0, 0, 1); 
		for (int i = 0; i < NUM_POINTS; i++)
		    BufferUtils.setInBuffer(zAxis, normBuf, i);
	}

}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2005/09/15 17:13:42  renanse
 * Removed Geometry and Spatial object arrays, fixed resulting errors, cleaned up license comments and imports and fixed all tests.  Also removed widget and ui packages.
 *
 * Revision 1.4  2004/09/14 21:52:21  mojomonkey
 * Clean Up:
 * 1. Added serialVersionUID to those classes that needed it.
 * 2. Formatted a significant number of classes.
 * Revision 1.3 2004/05/27 02:28:26 guurk Corrected shape
 * for height.
 * 
 * Revision 1.2 2004/05/27 02:06:31 guurk Added some CVS keyword replacements.
 *  
 */
