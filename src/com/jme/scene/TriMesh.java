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
package com.jme.scene;

import java.io.Serializable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Level;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, creating triangles on every
 * three points.
 *
 * @author Mark Powell
 * @version $Id: TriMesh.java,v 1.23 2004-08-26 23:53:20 cep21 Exp $
 */
public class TriMesh extends Geometry implements Serializable {
	protected int[] indices;
	private transient IntBuffer indexBuffer;
	protected int triangleQuantity = -1;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public TriMesh() {
	}

	/**
	 * Constructor instantiates a new <code>TriMesh</code> object.
	 *
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public TriMesh(String name) {
		super(name);

	}

	/**
	 * Constructor instantiates a new <code>TriMesh</code> object. Provided
	 * are the attributes that make up the mesh all attributes may be null,
	 * except for vertices and indices.
	 *
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertices
	 *            the vertices of the geometry.
	 * @param normal
	 *            the normals of the geometry.
	 * @param color
	 *            the colors of the geometry.
	 * @param texture
	 *            the texture coordinates of the mesh.
	 * @param indices
	 *            the indices of the vertex array.
	 */
	public TriMesh(String name, Vector3f[] vertices, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture, int[] indices) {

		super(name, vertices, normal, color, texture);

		if (null == indices) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Indices may not be" + " null.");
			throw new JmeException("Indices may not be null.");
		}
		this.indices = indices;
		triangleQuantity = indices.length / 3;
		updateIndexBuffer();
		LoggingSystem.getLogger().log(Level.INFO, "TriMesh created.");
	}

	/**
	 * Recreates the geometric information of this TriMesh from scratch.  The index and
     * vertex array must not be null, but the others may be.  Every 3 indices define an
     * index in the <code>vertices</code> array that refrences a vertex of a triangle.
	 * @param vertices The vertex information for this TriMesh.
	 * @param normal The normal information for this TriMesh.
	 * @param color The color information for this TriMesh.
	 * @param texture The texture information for this TriMesh.
	 * @param indices The index information for this TriMesh.
     * @see #reconstruct(com.jme.math.Vector3f[], com.jme.math.Vector3f[], com.jme.renderer.ColorRGBA[], com.jme.math.Vector2f[])
	 */
	public void reconstruct(Vector3f[] vertices, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture, int[] indices) {

		super.reconstruct(vertices, normal, color, texture);

		if (null == indices) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Indices may not be" + " null.");
			throw new JmeException("Indices may not be null.");
		}
		this.indices = indices;

		updateIndexBuffer();
		LoggingSystem.getLogger().log(Level.INFO, "TriMesh reconstructed.");
	}

	/**
	 *
	 * <code>getIndices</code> retrieves the indices into the vertex array.
	 *
	 * @return the indices into the vertex array.
	 */
	public int[] getIndices() {
		return indices;
	}

	/**
	 *
	 * <code>getIndexAsBuffer</code> retrieves the indices array as an
	 * <code>IntBuffer</code>.
	 *
	 * @return the indices array as an <code>IntBuffer</code>.
	 */
	public IntBuffer getIndexAsBuffer() {
		return indexBuffer;
	}

	/**
     * Stores in the <code>storage</code> array the indices of triangle <code>i</code>.  If
     * <code>i</code> is an invalid index, or if <code>storage.length!=3</code>, then nothing happens
     * @param i The index of the triangle to get.
     * @param storage The array that will hold the i's indexes.
     */
    public void getTriangle(int i, int[] storage) {
		if (i < triangleQuantity && storage.length == 3) {

			int iBase = 3 * i;
			storage[0] = indices[iBase++];
			storage[1] = indices[iBase++];
			storage[2] = indices[iBase];
		}
	}

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle <code>i</code>.
     * If <code>i</code> is an invalid triangle index, nothing happens.
     * @param i
     * @param vertices
     */
	public void getTriangle(int i, Vector3f[] vertices) {
	    //System.out.println(i + ", " + triangleQuantity);
		if (i < triangleQuantity && i>=0) {
		    int iBase = 3 * i;
		    vertices[0] = vertex[indices[iBase++]];
		    vertices[1] = vertex[indices[iBase++]];
		    vertices[2] = vertex[indices[iBase]];
		}
	}

	/**
     * Returns the number of triangles this TriMesh contains.  Is basicly indices.length/3
     * @return The current number of triangles.
     */
    public int getTriangleQuantity() {
		return indices.length / 3;
	}

	/**
	 *
	 * <code>setIndices</code> sets the index array for this
	 * <code>TriMesh</code>.
	 *
	 * @param indices
	 *            the index array.
	 */
	public void setIndices(int[] indices) {
		this.indices = indices;
		triangleQuantity = indices.length / 3;
		updateIndexBuffer();
	}

	/**
	 * <code>draw</code> calls super to set the render state then passes
	 * itself to the renderer.
	 *
	 * @param r
	 *            the renderer to display
	 */
        public void draw(Renderer r) {
          if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this))
              return;
          }
          super.draw(r);
          r.draw(this);
        }

	/**
	 * <code>drawBounds</code> calls super to set the render state then passes
	 * itself to the renderer.
	 *
	 * @param r
	 *            the renderer to display
	 */
	public void drawBounds(Renderer r) {
		r.drawBounds(this);
	}

	/**
	 *
	 * <code>setIndexBuffers</code> creates the <code>IntBuffer</code> that
	 * contains the indices array.
	 *
	 */
	public void updateIndexBuffer() {
		if (indices == null) {
			return;
		}
        if (indexBuffer==null || indexBuffer.capacity()<indices.length){
            indexBuffer = ByteBuffer.allocateDirect(
                    4 * (triangleQuantity >= 0
                            ? triangleQuantity * 3
                            : indices.length)).order(ByteOrder.nativeOrder())
                    .asIntBuffer();
        }

		indexBuffer.clear();
		indexBuffer.put(indices, 0, triangleQuantity >= 0
				? triangleQuantity * 3
				: indices.length);
		indexBuffer.flip();
	}

    /**
     * Clears the buffers of this TriMesh.  The buffers include its indexBuffer, and all Geometry
     * buffers.
     */
	public void clearBuffers() {
		super.clearBuffers();
		indexBuffer = null;
	}

    /**
     * Sets this geometry's index buffer as a refrence to
     * the passed <code>IntBuffer</code>.  Incorrectly built IntBuffers can
     * have undefined results.  Use with care.
     * @param toSet The <code>IntBuffer</code> to set this
     * geometry's index buffer to
     */
    public void setIndexBuffer(IntBuffer toSet){
        indexBuffer=toSet;
    }

    /**
     * Used with Serialization.  Do not call this directly.
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
		updateIndexBuffer();
    }


}
