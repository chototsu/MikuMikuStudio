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
 
package com.jme.scene.shape;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;

/**
 * <code>Hexagon</code> provides an extension of <code>TriMesh</code>. A
 * <code>Hexagon</code> provides a regular hexagon with each triangle having side
 * length that is given in the constructor.
 * @author Joel Schuster
 * @version $Id: Hexagon.java,v 1.3 2004-05-27 02:28:26 guurk Exp $
 */
public class Hexagon extends TriMesh {
	private static final int NUM_POINTS = 7;
	private static final int NUM_TRIS = 6;
	private float sideLength;
	/**
	 * Hexagon Constructor instantiates a new Hexagon. This element is center on 0,0,0 with all
	 * normals pointing up. The user must move and rotate for positioning.
	 * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
	 * @param sideLength The length of all the sides of the tiangles 
	 */
	public Hexagon(String name, float sideLength) {
		super(name);
		this.sideLength = sideLength;

		// allocate vertices
		vertex = new Vector3f[NUM_POINTS];
		normal = new Vector3f[NUM_POINTS];
		color = new ColorRGBA[NUM_POINTS];
		texture[0] = new Vector2f[NUM_POINTS];
		indices = new int[3 * NUM_TRIS];

		setVertexData();
		setIndexData();
		setTextureData();
		setNormalData();
		setColorData();

	}
	/**
	 * Vertexes are set up like this:
	 *         0__1
	 *        / \    /  \
	 *     5/__\6/__\2
	 *       \   /   \    /
	 *        \ /___\ /
	 *         4       3
	 * 
	 *     All lines on this diagram are sideLength long.
	 *    Therefore, the width of the hexagon is sideLength * 2,
	 *     and the height is 2 * the height of one equalateral triangle with
	 *    all side = sideLength which is .866
	 * 
	 */
	private void setVertexData() {
		vertex[0] = new Vector3f(- (sideLength / 2), sideLength * 0.866f, 0.0f);
		vertex[1] = new Vector3f(sideLength / 2, sideLength * 0.866f, 0.0f);
		vertex[2] = new Vector3f(sideLength, 0.0f, 0.0f);
		vertex[3] = new Vector3f(sideLength / 2, -sideLength * 0.866f, 0.0f);
		vertex[4] = new Vector3f(- (sideLength / 2), -sideLength * 0.866f, 0.0f);
		vertex[5] = new Vector3f(-sideLength, 0.0f, 0.0f);
		vertex[6] = new Vector3f(0.0f, 0.0f, 0.0f);

		setVertices(vertex);
	}

	/**
	 * Sets up the indexes of the mesh. These go in a clockwise fassion and thus
	 * only the 'up' side of the hex is visible. If you wish to have to either set
	 * two sided lighting or create two hexes back-to-back
	 *
	 */

	private void setIndexData() {
		// tri 1
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 6;
		// tri 2
		indices[3] = 1;
		indices[4] = 2;
		indices[5] = 6;
		// tri 3
		indices[6] = 2;
		indices[7] = 3;
		indices[8] = 6;
		// tri 4
		indices[9] = 3;
		indices[10] = 4;
		indices[11] = 6;
		// tri 5
		indices[12] = 4;
		indices[13] = 5;
		indices[14] = 6;
		// tri 6
		indices[15] = 5;
		indices[16] = 0;
		indices[17] = 6;
		setIndices(indices);
	}

	private void setTextureData() {

		texture[0][0] = new Vector2f();
		texture[0][0].x = 0.25f;
		texture[0][0].y = 0.0f;

		texture[0][1] = new Vector2f();
		texture[0][1].x = 0.75f;
		texture[0][1].y = 0.0f;

		texture[0][2] = new Vector2f();
		texture[0][2].x = 1.0f;
		texture[0][2].y = 0.5f;

		texture[0][3] = new Vector2f();
		texture[0][3].x = 0.75f;
		texture[0][3].y = 1.0f;

		texture[0][4] = new Vector2f();
		texture[0][4].x = 0.25f;
		texture[0][4].y = 1.0f;

		texture[0][5] = new Vector2f();
		texture[0][5].x = 0.0f;
		texture[0][5].y = 0.5f;

		texture[0][6] = new Vector2f();
		texture[0][6].x = 0.5f;
		texture[0][6].y = 0.5f;

		setTextures(texture[0]);
	}

	/**
	 * Sets all the default vertex colors to white.
	 *
	 */

	private void setColorData() {
		for (int x = 0; x < NUM_POINTS; x++)
			color[x] = new ColorRGBA();
		setColors(color);
	}

	/**
	 * Sets all the default vertex normals to 'up', +1 in the Z direction.
	 *
	 */
	
	private void setNormalData() {
		for (int i = 0; i < NUM_POINTS; i++)
			normal[i] = new Vector3f(0, 0, 1);

		setNormals(normal);
	}

}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2004/05/27 02:06:31  guurk
 * Added some CVS keyword replacements.
 *
 */
 