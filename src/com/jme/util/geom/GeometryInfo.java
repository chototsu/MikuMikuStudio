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
package com.jme.util.geom;

import java.util.Arrays;
import java.util.TreeSet;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CompositeMesh;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.system.JmeException;
import com.jme.util.geom.nvtristrip.PrimitiveGroup;
import com.jme.util.geom.nvtristrip.TriStrip;

/**
 * This class allows for optimalization of indexed geometry. In most cases it
 * will be filled with GeometryCreator.fillGeometryInfo, processed and result
 * geometry will be retrieved.
 */

public class GeometryInfo {

	protected VertexData[] vertices;

	protected int[] triangles;

	protected int[] smoothGroups;

	private int vertexCacheSize = TriStrip.CACHESIZE_GEFORCE1_2;

	protected int state = STATE_UNKNOWN;

	protected static final int STATE_UNKNOWN = 0;

	protected static final int STATE_SPLIT = 1;

	protected static final int STATE_MERGED = 2;

	/**
	 * Constructs new GeometryInfo
	 */
	public GeometryInfo() {
		super();
	}

	public GeometryInfo(TriMesh mesh) {
		GeometryCreator creator = new GeometryCreator(mesh);
		creator.fillGeometryInfo(this);
		weldVertices();
	}

	/**
	 * This method computes normal for each face in flat mode - every vertex is
	 * duplicated for each face and assigned separate normal belonging to this
	 * face. As side effect of this method, indices are 'split'. Merging them
	 * will probably not do much, as vertices with same coords will have
	 * different normals because of flat shading.
	 *  
	 */
	public GeometryInfo recalculateFlatNormals() {

		unweldVertices();

		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f faceNormal = new Vector3f();

		int numFaces = triangles.length / 3;

		// step through all the faces
		for (int face = 0; face < numFaces; face++) {

			Vector3f a = vertices[triangles[face * 3]].coord;
			Vector3f b = vertices[triangles[face * 3 + 1]].coord;
			Vector3f c = vertices[triangles[face * 3 + 2]].coord;

			b.subtract(a, v1);
			c.subtract(a, v2);
			v1.cross(v2, faceNormal);
			faceNormal.normalizeLocal();

			vertices[triangles[face * 3]].normal = new Vector3f(faceNormal);
			vertices[triangles[face * 3 + 1]].normal = new Vector3f(faceNormal);
			vertices[triangles[face * 3 + 2]].normal = new Vector3f(faceNormal);
		}

		return this;
	}

	/**
	 * This method computes smoothened normals for each vertex. Normal of each
	 * face from same smoothing group is averaged to compute value of vertex.
	 * This means that vertex will be duplicated if it belongs to faces
	 * belonging to more than one smooth group. After this operation, vertices
	 * will be mostly welded - unless there is a case where two neighbour faces
	 * from different smoothing group have same normal.
	 */
	public GeometryInfo recalculateSmoothGroupNormals() {
		if (smoothGroups == null) {
			smoothGroups = new int[triangles.length / 3];
			Arrays.fill(smoothGroups, 1);
		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].normal = null;
		}

		unweldVertices();

		for (int i = 0; i < smoothGroups.length; i++) {
			vertices[triangles[i * 3]].smoothGroup = smoothGroups[i];
			vertices[triangles[i * 3 + 1]].smoothGroup = smoothGroups[i];
			vertices[triangles[i * 3 + 2]].smoothGroup = smoothGroups[i];
		}
		weldVertices();

		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f faceNormal = new Vector3f();

		int numFaces = triangles.length / 3;

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].normal = new Vector3f();
		}

		// step through all the faces
		for (int face = 0; face < numFaces; face++) {

			Vector3f a = vertices[triangles[face * 3]].coord;
			Vector3f b = vertices[triangles[face * 3 + 1]].coord;
			Vector3f c = vertices[triangles[face * 3 + 2]].coord;

			b.subtract(a, v1);
			c.subtract(a, v2);
			v1.cross(v2, faceNormal);

			vertices[triangles[face * 3]].normal.addLocal(faceNormal);
			vertices[triangles[face * 3 + 1]].normal.addLocal(faceNormal);
			vertices[triangles[face * 3 + 2]].normal.addLocal(faceNormal);
		}

		for (int i = 0; i < vertices.length; i++) {
			vertices[i].normal.normalizeLocal();
			vertices[i].smoothGroup = -1;
		}

		state = STATE_UNKNOWN;

		return this;
	}

	/**
	 * Find vertices with same parameters and merge them into one. Allows
	 * sharing of data between faces, which is a requirement for performance
	 * benefit from GPU vertex cache.
	 * 
	 * @see #unweldVertices
	 */
	public GeometryInfo weldVertices() {
		if (state == STATE_MERGED)
			return this;
		TreeSet nverts = new TreeSet();
		for (int i = 0; i < vertices.length; i++) {
			nverts.add(vertices[i]);
		}
		VertexData[] ndata = (VertexData[]) nverts
				.toArray(new VertexData[nverts.size()]);
		int[] nTriangles = new int[triangles.length];
		for (int i = 0; i < nTriangles.length; i++) {
			nTriangles[i] = Arrays.binarySearch(ndata, vertices[triangles[i]]);
		}
		vertices = ndata;
		triangles = nTriangles;
		state = STATE_MERGED;

		return this;
	}

	/**
	 * Duplicate vertex data for each face, so it is not shared between them.
	 * Allows for allocating per-face normal (flat shading).
	 * 
	 * @see #weldVertices
	 */
	public GeometryInfo unweldVertices() {
		if (state == STATE_SPLIT)
			return this;
		VertexData[] ndata = new VertexData[triangles.length];
		for (int i = 0; i < triangles.length; i++) {
			ndata[i] = new VertexData(vertices[triangles[i]]);
			triangles[i] = i;
		}
		vertices = ndata;
		state = STATE_SPLIT;

		return this;
	}

	/**
	 * This method reorders face indices to fit well into vertex cache of GPU.
	 * This operation is meaningful if you plan to use non-strip triangle array
	 * later - in other case, use one of strip generation methods, which are
	 * cache-aware by default. Note: this method destroys smoothGroups info and
	 * it cannot be recovered, as order of faces is changed. If you want to use
	 * smoothing group info for generating normals, you need to do it _before_
	 * you call this method.
	 * 
	 * @see #setVertexCacheSize
	 */
	public GeometryInfo optimizeTrianglesForCache() {
		TriStrip ts = new TriStrip();
		ts.setCacheSize(getVertexCacheSize());
		ts.setListsOnly(true);
		PrimitiveGroup[] pg = ts.generateStrips(triangles);

		assert (pg.length == 1);
		assert (pg[0].type == PrimitiveGroup.PT_LIST);
		smoothGroups = null;
		triangles = pg[0].getTrimmedIndices();

		return this;
	}

	/**
	 * Create continous triangle strip with separate substrips connected by
	 * degenerate triangles. Please note that unless vertices are welded before
	 * (explicitly by weldVertices or implictly by
	 * recalculateSmoothGroupNormals) stripification will not help with
	 * perfomance, as same vertex will have different index for different face.
	 * 
	 * @return indices of triangle strip
	 */
	public int[] createContinousStrip() {
		TriStrip ts = new TriStrip();
		ts.setCacheSize(getVertexCacheSize());
		ts.setListsOnly(false);
		ts.setMinStripSize(0);
		ts.setStitchStrips(true);
		PrimitiveGroup[] pg = ts.generateStrips(triangles);

		assert (pg.length == 1);
		assert (pg[0].type == PrimitiveGroup.PT_STRIP);

		return pg[0].getTrimmedIndices();
	}

	/**
	 * @return Returns the vertexCacheSize.
	 */
	public int getVertexCacheSize() {
		return vertexCacheSize;
	}

	/**
	 * This method sets size of vertex cache on gpu for which strips/triangle
	 * lists should be optimized. This is the "actual" cache size, so 24 for
	 * GeForce3 and 16 for GeForce1/2 You may want to play around with this
	 * number to tweak performance. Default value: 16 In case of doubt it is
	 * better to underestimate size of cache. If you don't care about vertex
	 * cache and want strips as long as possible, put very high value here - but
	 * be warned, because stripification algorithm is O(n^2*m) [doublecheck - is
	 * it true?], where n is number of indices, and m is size of
	 * vertexCacheSize. On the other hand, with too small value, you will get
	 * too many small strips and cost of degenerate triangles will rise.
	 * 
	 * @param vertexCacheSize
	 *            The vertexCacheSize to set.
	 */
	public void setVertexCacheSize(int vertexCacheSize) {
		this.vertexCacheSize = vertexCacheSize;
	}

	private void fillData(Geometry geom) {
		int count = vertices.length;

		Vector3f[] verts = new Vector3f[count];
		for (int i = 0; i < count; i++) {
			verts[i] = vertices[i].coord;
		}
		geom.setVertices(verts);

		if (vertices[0].normal != null) {
			Vector3f[] normals = new Vector3f[count];
			for (int i = 0; i < count; i++) {
				normals[i] = vertices[i].normal;
			}
			geom.setNormals(normals);
		}

		if (vertices[0].color4 != null) {
			ColorRGBA[] colors = new ColorRGBA[count];
			for (int i = 0; i < count; i++) {
				colors[i] = vertices[i].color4;
			}
			geom.setColors(colors);
		}

		if (vertices[0].texCoords != null) {
			for (int tc = 0; tc < vertices[0].texCoords.length; tc++) {
				Vector2f[] tex = new Vector2f[count];
				for (int i = 0; i < count; i++) {
					tex[i] = vertices[i].texCoords[tc];
				}
				geom.setTextures(tex, tc);
			}
		}
	}

	/**
	 * Create indexed triangle array with geometry contained in this object. It
	 * is best to call weldVertices (if needed) and then
	 * optimizeTrianglesForCache before calling this method.
	 * 
	 * @param vertexFormat
	 * @return
	 */

	public TriMesh createTrimesh(String name) {
		TriMesh tri = new TriMesh(name);
		fillData(tri);
		tri.setIndices(triangles);
		return tri;
	}

	/**
	 * Create indexed triangle strip array with geometry contained in this
	 * object. It is best to call weldVertices (if needed) before calling this
	 * method. Strip array will have one long strip, with substrips connected by
	 * degenerate triangles. It uses createContinousStrip method.
	 * 
	 * @param vertexFormat
	 * @return
	 * @see GeometryInfo#createContinousStrip
	 */
	public CompositeMesh createContinousStripMesh(String name) {

		CompositeMesh mesh = new CompositeMesh(name);
		int[] flow = createContinousStrip();
		fillData(mesh);
		mesh.setIndices(flow);
		mesh.setIndexRanges(new CompositeMesh.IndexRange[] { CompositeMesh
				.createTriangleStrip(flow.length) });
		return mesh;
	}

	/**
	 * Create indexed triangle strip array with geometry contained in this
	 * object. It is best to call weldVertices (if needed) before calling this
	 * method. Strip array will have multiple short strips, possibly even
	 * single-triangle, so it is not very fast solution. It is better to create
	 * continous strip array or mixed array (when it will be implemented).
	 * 
	 * @param vertexFormat
	 * @return
	 * @see GeometryInfo#createContinousStrip
	 */
	public CompositeMesh createChunkedStripArray(String name) {
		TriStrip ts = new TriStrip();
		ts.setCacheSize(getVertexCacheSize());
		ts.setListsOnly(false);
		ts.setMinStripSize(0);
		ts.setStitchStrips(false);
		PrimitiveGroup[] pg = ts.generateStrips(triangles);

		CompositeMesh.IndexRange[] strips = new CompositeMesh.IndexRange[pg.length];
		int totalCount = 0;
		for (int i = 0; i < strips.length; i++) {
			assert (pg[i].type == PrimitiveGroup.PT_STRIP);
			strips[i] = CompositeMesh.createTriangleStrip(pg[i].numIndices);
			totalCount += pg[i].numIndices;
		}

		int[] flow = new int[totalCount];
		int current = 0;
		for (int i = 0; i < strips.length; i++) {
			System.arraycopy(pg[i].indices, 0, flow, current, pg[i].numIndices);
			current += pg[i].numIndices;
		}

		assert (current == totalCount);

		CompositeMesh mesh = new CompositeMesh(name);
		fillData(mesh);
		mesh.setIndices(flow);
		mesh.setIndexRanges(strips);
		return mesh;

	}

	public CompositeMesh createMixedArray(String name, int minStripLen,
			boolean stitchStrips) {
		TriStrip ts = new TriStrip();
		ts.setCacheSize(getVertexCacheSize());
		ts.setListsOnly(false);
		ts.setMinStripSize(minStripLen);
		ts.setStitchStrips(stitchStrips);
		PrimitiveGroup[] pg = ts.generateStrips(triangles);

		CompositeMesh.IndexRange[] strips = new CompositeMesh.IndexRange[pg.length];
		int totalCount = 0;
		for (int i = 0; i < strips.length; i++) {
			switch (pg[i].type) {
			case PrimitiveGroup.PT_LIST:
				strips[i] = CompositeMesh.createTriangleRange(pg[i].numIndices);
				break;
			case PrimitiveGroup.PT_FAN:
				strips[i] = CompositeMesh.createTriangleFan(pg[i].numIndices);
				break;
			case PrimitiveGroup.PT_STRIP:
				strips[i] = CompositeMesh.createTriangleStrip(pg[i].numIndices);
				break;
			default:
				throw new JmeException("Unknown PrimitiveGroup type");
			}
			totalCount += pg[i].numIndices;
		}

		int[] flow = new int[totalCount];
		int current = 0;
		for (int i = 0; i < strips.length; i++) {
			System.arraycopy(pg[i].indices, 0, flow, current, pg[i].numIndices);
			current += pg[i].numIndices;
		}

		assert (current == totalCount);

		CompositeMesh mesh = new CompositeMesh(name);
		fillData(mesh);
		mesh.setIndices(flow);
		mesh.setIndexRanges(strips);
		return mesh;
	}

}