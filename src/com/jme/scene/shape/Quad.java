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
 * <code>Quad</code> defines a four sided, two dimensional shape. The local
 * height of the <code>Quad</code> defines it's size about the y-axis, while
 * the width defines the x-axis. The z-axis will always be 0.
 * 
 * @author Mark Powell
 * @version $Id: Quad.java,v 1.5 2004-09-14 21:52:22 mojomonkey Exp $
 */
public class Quad extends TriMesh {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor creates a new <code>Quad</code> object. That data for the
	 * <code>Quad</code> is not set until a call to <code>initialize</code>
	 * is made.
	 * 
	 * @param name
	 *            the name of this <code>Quad</code>.
	 */
	public Quad(String name) {
		super(name);
	}

	/**
	 * Constructor creates a new <code>Quade</code> object with the provided
	 * width and height.
	 * 
	 * @param name
	 *            the name of the <code>Quad</code>.
	 * @param width
	 *            the width of the <code>Quad</code>.
	 * @param height
	 *            the height of the <code>Quad</code>.
	 */
	public Quad(String name, float width, float height) {
		super(name);
		initialize(width, height);
	}

	/**
	 * <code>resize</code> changes the width and height of the given quad by
	 * altering its vertices.
	 * 
	 * @param width
	 *            the new width of the <code>Quad</code>.
	 * @param height
	 *            the new height of the <code>Quad</code>.
	 */
	public void resize(float width, float height) {
		Vector3f[] verts = new Vector3f[4];

		verts[0] = new Vector3f(-width / 2f, height / 2f, 0);
		verts[1] = new Vector3f(-width / 2f, -height / 2f, 0);
		verts[2] = new Vector3f(width / 2f, -height / 2f, 0);
		verts[3] = new Vector3f(width / 2f, height / 2f, 0);

		setVertices(verts);
	}

	/**
	 * 
	 * <code>initialize</code> builds the data for the <code>Quad</code>
	 * object.
	 * 
	 * 
	 * @param width
	 *            the width of the <code>Quad</code>.
	 * @param height
	 *            the height of the <code>Quad</code>.
	 */
	public void initialize(float width, float height) {
		Vector3f[] verts = new Vector3f[4];
		Vector3f[] norms = new Vector3f[4];
		Vector2f[] texCoords = new Vector2f[4];
		ColorRGBA[] colors = new ColorRGBA[4];
		int[] indices = new int[6];

		verts[0] = new Vector3f(-width / 2f, height / 2f, 0);
		verts[1] = new Vector3f(-width / 2f, -height / 2f, 0);
		verts[2] = new Vector3f(width / 2f, -height / 2f, 0);
		verts[3] = new Vector3f(width / 2f, height / 2f, 0);

		norms[0] = new Vector3f(0, 0, 1);
		norms[1] = new Vector3f(0, 0, 1);
		norms[2] = new Vector3f(0, 0, 1);
		norms[3] = new Vector3f(0, 0, 1);

		texCoords[0] = new Vector2f(0, 1);
		texCoords[1] = new Vector2f(0, 0);
		texCoords[2] = new Vector2f(1, 0);
		texCoords[3] = new Vector2f(1, 1);

		colors[0] = new ColorRGBA();
		colors[1] = new ColorRGBA();
		colors[2] = new ColorRGBA();
		colors[3] = new ColorRGBA();

		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 0;
		indices[4] = 2;
		indices[5] = 3;

		setVertices(verts);
		setNormals(norms);
		setColors(colors);
		setTextures(texCoords);
		setIndices(indices);
	}

	/**
	 * <code>getCenter</code> returns the center of the <code>Quad</code>.
	 * 
	 * @return Vector3f the center of the <code>Quad</code>.
	 */
	public Vector3f getCenter() {
		return worldTranslation;
	}
}