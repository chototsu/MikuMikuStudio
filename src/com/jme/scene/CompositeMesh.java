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

package com.jme.scene;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Composite</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the
 * renderer the order in which to draw the points, with exact meaning of indices
 * being defined by IndexRange collection. Index ranges are interpreted one
 * after another, consuming their 'count' indices each time. Every range use
 * same vertex data, so it is perfectly possible to reference already used
 * indices from different kind of range.
 * 
 * @author Artur Biesiadowski
 */
public class CompositeMesh extends TriMesh implements Serializable {

	protected IndexRange[] ranges;

	private int[] cachedTriangleIndices;

	/**
	 * Constructor instantiates a new <code>CompositeMesh</code> object.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 */
	public CompositeMesh(String name) {
		super(name);
	}

	/**
	 * Constructor instantiates a new <code>CompositeMesh</code> object.
	 * Provided are the attributes that make up the mesh all attributes may be
	 * null, except for vertices,indices and ranges
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
	 * @param ranges
	 *            the list of index ranges to be used in rendering
	 */
	public CompositeMesh(String name, FloatBuffer vertices, FloatBuffer normal,
	        FloatBuffer color, FloatBuffer texture, IntBuffer indices,
			IndexRange[] ranges) {
		super(name);
		this.reconstruct(vertices, normal, color, texture, indices, ranges);
		LoggingSystem.getLogger().log(Level.INFO, "CompositeMesh created.");
	}

	/**
	 * Recreates the geometric information of this CompositeMesh from scratch.
	 * The index,vertex and ranges array must not be null, but the others may
	 * be.
	 * 
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
	 * @param ranges
	 *            the list of index ranges to be used in rendering
	 */
	public void reconstruct(FloatBuffer vertices, FloatBuffer normal,
			FloatBuffer color, FloatBuffer texture, IntBuffer indices,
			IndexRange[] ranges) {
		super.reconstruct(vertices, normal, color, texture, indices);

		if (ranges == null) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Index ranges may not be null.");
			throw new JmeException("Index ranges may not be null.");
		}
		this.ranges = ranges;
	}

	/**
	 * 
	 * @return currently set index ranges
	 */
	public IndexRange[] getIndexRanges() {
		return ranges;
	}

	/**
	 * Sets new index ranges - be sure to match it with updates to indices array
	 * if needed
	 * 
	 * @param ranges
	 */
	public void setIndexRanges(IndexRange[] ranges) {
		this.ranges = ranges;
		cachedTriangleIndices = null;
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
		applyStates();
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
	 * @return equivalent number of triangles - each quad counts as two
	 *         triangles
	 */
	public int getTriangleQuantity() {
		if (cachedTriangleIndices != null) {
			return cachedTriangleIndices.length / 3;
		}
		int quantity = 0;
		for (int i = 0; i < ranges.length; i++) {
			quantity += ranges[i].getTriangleQuantityEquivalent();
		}
		return quantity;
	}

	/**
	 * Create index range representing free, unconnected triangles.
	 * 
	 * @param count
	 *            number of indexes to be put in this range
	 * @return new IndexRange for unconnected triangles
	 */
	public static IndexRange createTriangleRange(int count) {
		if (count % 3 != 0) {
			throw new IllegalArgumentException(
					"Triangle range has to be multiple of 3 vertices");
		}
		return new IndexRange(IndexRange.TRIANGLES, count);
	}

	/**
	 * Create index range representing triangle strip
	 * 
	 * @param count
	 *            number of indexes to be put in this range
	 * @return new IndexRange for triangle strip
	 */
	public static IndexRange createTriangleStrip(int count) {
		if (count < 3) {
			throw new IllegalArgumentException(
					"Triangle strip cannot be shorter than 3 vertices");
		}
		return new IndexRange(IndexRange.TRIANGLE_STRIP, count);
	}

	/**
	 * Create index range representing triangle fan
	 * 
	 * @param count
	 *            number of indexes to be put in this range
	 * @return new IndexRange for triangle fan
	 */
	public static IndexRange createTriangleFan(int count) {
		if (count < 3) {
			throw new IllegalArgumentException(
					"Triangle fan cannot be shorter than 3 vertices");
		}
		return new IndexRange(IndexRange.TRIANGLE_FAN, count);
	}

	/**
	 * Create index range representing free, unconnected quads.
	 * 
	 * @param count
	 *            number of indexes to be put in this range
	 * @return new IndexRange for unconnected quads
	 */
	public static IndexRange createQuadRange(int count) {
		if (count % 4 != 0) {
			throw new IllegalArgumentException(
					"Quad range has to be multiple of 4 vertices");
		}
		return new IndexRange(IndexRange.QUADS, count);
	}

	/**
	 * Create index range representing quad strip
	 * 
	 * @param count
	 *            number of indexes to be put in this range
	 * @return new IndexRange for quad strip
	 */
	public static IndexRange createQuadStrip(int count) {
		if (count < 4) {
			throw new IllegalArgumentException(
					"Quad strip range cannot be shorter than 4 vertices");
		}
		if (count % 2 != 0) {
			throw new IllegalArgumentException(
					"Quad strip range has to be multiple of 2 vertices");
		}
		return new IndexRange(IndexRange.QUAD_STRIP, count);
	}

	/**
	 * Recreate view of this composite mesh as collection of triangles.
	 * Unconditionally updates cachedTriangleIndices field with new data.
	 */
	protected void recreateTriangleIndices() {
		cachedTriangleIndices = new int[getTriangleQuantity() * 3];
		int index = 0;
		int ctIdx = 0;
		for (int i = 0; i < ranges.length; i++) {
			IndexRange rng = ranges[i];
			switch (rng.getKind()) {
			case CompositeMesh.IndexRange.TRIANGLES:
				for (int ri = 0; ri < rng.getCount(); ri++) {
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(ri + index);
				}
				break;
			case CompositeMesh.IndexRange.TRIANGLE_STRIP:
				for (int ri = 2; ri < rng.getCount(); ri++) {
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + ri - 2);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + ri - 1);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + ri);
				}
				break;
			case CompositeMesh.IndexRange.TRIANGLE_FAN:
				for (int ri = 2; ri < rng.getCount(); ri++) {
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + ri - 1);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + ri);
				}
				break;
			case CompositeMesh.IndexRange.QUADS:
				for (int q = 0; q < rng.getCount(); q += 4) {
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q + 1);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q + 2);

					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q + 2);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q + 3);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q);
				}
				break;
			case CompositeMesh.IndexRange.QUAD_STRIP:
				for (int q = 2; q < rng.getCount(); q += 2) {
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q - 2);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q - 1);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q);

					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q + 1);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q);
					cachedTriangleIndices[ctIdx++] = indexBuffer.get(index + q - 1);
				}
				break;
			default:
				throw new JmeException("Unknown index range type "
						+ ranges[i].getKind());
			}
			index += rng.getCount();
		}
	}

	/**
	 * Return this mesh object as triangles. Every 3 vertices returned compose
	 * single triangle. Vertices are returned by reference for efficiency, so it
	 * is required that they won't be modified by caller.
	 * 
	 * @return view of current mesh as group of triangle vertices
	 */
	public Vector3f[] getMeshAsTriangles() {

		if (cachedTriangleIndices == null) {
			recreateTriangleIndices();
		}
        Vector3f[] vertex = BufferUtils.getVector3Array(vertBuf); // FIXME: UGLY!
		Vector3f[] triangleData = new Vector3f[cachedTriangleIndices.length];
		for (int i = 0; i < triangleData.length; i++) {
			triangleData[i] = vertex[cachedTriangleIndices[i]];
		}
		return triangleData;
	}

	/**
	 * Stores in the <code>storage</code> array the indices of triangle
	 * <code>i</code>. If <code>i</code> is an invalid index, or if
	 * <code>storage.length<3</code>, then nothing happens For composite
	 * mesh, this operation is more costly than for Trimesh.
	 * 
	 * @param i
	 *            The index of the triangle to get.
	 * @param storage
	 *            The array that will hold the i's indexes.
	 */
	public void getTriangle(int i, int[] storage) {
		int iOffset = i * 3;

		if (cachedTriangleIndices == null) {
			recreateTriangleIndices();
		}
		if (i < 0 || iOffset >= cachedTriangleIndices.length) {
			return;
		}
		storage[0] = cachedTriangleIndices[iOffset + 0];
		storage[1] = cachedTriangleIndices[iOffset + 1];
		storage[2] = cachedTriangleIndices[iOffset + 2];
	}

	/**
	 * Stores in the <code>vertices</code> array the vertex values of triangle
	 * <code>i</code>. If <code>i</code> is an invalid triangle index,
	 * nothing happens.
	 * 
	 * @param i
	 * @param vertices
	 */
	public void getTriangle(int i, Vector3f[] vertices) {
		int iOffset = i * 3;
		if (cachedTriangleIndices == null) {
			recreateTriangleIndices();
		}
		if (i < 0 || iOffset >= cachedTriangleIndices.length) {
			return;
		}
        for (int x = 0; x < 3; x++) {
            vertices[x] = new Vector3f();   // we could reuse existing, but it may affect current users.
            BufferUtils.populateFromBuffer(vertices[x], vertBuf, cachedTriangleIndices[iOffset++]);
        }
	}

	private static final long serialVersionUID = 1;

	/**
	 * This class represents range of indexes to be interpreted in a way
	 * depending on 'kind' attribute. To create instances of this class, please
	 * check CompositeMesh static methods.
	 */
	public static class IndexRange implements java.io.Serializable {

		public static final int TRIANGLES = 1;

		public static final int TRIANGLE_STRIP = 2;

		public static final int TRIANGLE_FAN = 3;

		public static final int QUADS = 4;

		public static final int QUAD_STRIP = 5;

		private int kind;

		private int count;

		IndexRange(int aKind, int aCount) {
			kind = aKind;
			count = aCount;

		}

		public int getCount() {
			return count;
		}

		public int getKind() {
			return kind;
		}

		/**
		 * @return equivalent in triangles of elements drawn (1 quad counts as
		 *         two triangles)
		 */
		public long getTriangleQuantityEquivalent() {
			switch (kind) {
			case TRIANGLES:
				return count / 3;
			case TRIANGLE_STRIP:
				return count - 2;
			case TRIANGLE_FAN:
				return count - 2;
			case QUADS:
				return (count / 4) * 2;
			case QUAD_STRIP:
				return ((count - 2) / 2) * 2;
			default:
				throw new JmeException("Unknown kind of index range");
			}
		}

		public String toString() {
			return "IndexRange kind=" + KIND_NAMES[getKind()] + " count="
					+ getCount();
		}

		private String[] KIND_NAMES = { null, "TRIANGLES", "TRIANGLE_STRIP",
				"TRIANGLE_FAN", "QUADS", "QUAD_STRIP" };

		private static final long serialVersionUID = 1;

	}

}