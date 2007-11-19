/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.TriangleBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Pyramid</code> provides an extension of <code>TriMesh</code>. A
 * pyramid is defined by a width at the base and a height. The pyramid is a four
 * sided pyramid with the center at (0,0). The pyramid will be axis aligned with
 * the peak being on the positive y axis and the base being in the x-z plane.
 * 
 * @author Mark Powell
 * @version $Id: Pyramid.java,v 1.12 2007/09/21 15:45:27 nca Exp $
 */
public class Pyramid extends TriMesh {
	private static final long serialVersionUID = 1L;

	private float height;

	private float width;

    public Pyramid() {}
    
	/**
	 * Constructor instantiates a new <code>Pyramid</code> object. The base
	 * width and the height are provided.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param width
	 *            the base width of the pyramid.
	 * @param height
	 *            the height of the pyramid from the base to the peak.
	 */
	public Pyramid(String name, float width, float height) {
		super(name);
		this.width = width;
		this.height = height;

		setVertexData();
		setNormalData();
		setTextureData();
		setIndexData();
	}

	/**
	 * 
	 * <code>setVertexData</code> sets the vertices that make the pyramid.
	 * Where the center of the box is the origin and the base and height are set
	 * during construction.
	 *  
	 */
	private void setVertexData() {
		Vector3f peak = new Vector3f(0, height / 2, 0);
		Vector3f vert0 = new Vector3f(-width / 2, -height / 2, -width / 2);
		Vector3f vert1 = new Vector3f(width / 2, -height / 2, -width / 2);
		Vector3f vert2 = new Vector3f(width / 2, -height / 2, width / 2);
		Vector3f vert3 = new Vector3f(-width / 2, -height / 2, width / 2);

        FloatBuffer verts = BufferUtils.createVector3Buffer(16);

		//base
	    verts.put(vert3.x).put(vert3.y).put(vert3.z);
	    verts.put(vert2.x).put(vert2.y).put(vert2.z);
	    verts.put(vert1.x).put(vert1.y).put(vert1.z);
	    verts.put(vert0.x).put(vert0.y).put(vert0.z);

		//side 1
	    verts.put(vert0.x).put(vert0.y).put(vert0.z);
	    verts.put(vert1.x).put(vert1.y).put(vert1.z);
	    verts.put(peak.x).put(peak.y).put(peak.z);

		//side 2
	    verts.put(vert1.x).put(vert1.y).put(vert1.z);
	    verts.put(vert2.x).put(vert2.y).put(vert2.z);
	    verts.put(peak.x).put(peak.y).put(peak.z);

		//side 3
	    verts.put(vert2.x).put(vert2.y).put(vert2.z);
	    verts.put(vert3.x).put(vert3.y).put(vert3.z);
	    verts.put(peak.x).put(peak.y).put(peak.z);

		//side 4
	    verts.put(vert3.x).put(vert3.y).put(vert3.z);
	    verts.put(vert0.x).put(vert0.y).put(vert0.z);
	    verts.put(peak.x).put(peak.y).put(peak.z);

        verts.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setVertexBuffer(verts);
	}

	/**
	 * 
	 * <code>setNormalData</code> defines the normals of each face of the
	 * pyramid.
	 *  
	 */
	private void setNormalData() {
        FloatBuffer norms = BufferUtils.createVector3Buffer(16);

		// bottom
		norms.put(0).put(-1).put(0);
  		norms.put(0).put(-1).put(0);
		norms.put(0).put(-1).put(0);
		norms.put(0).put(-1).put(0);

		// back
		norms.put(0).put(0.70710677f).put(-0.70710677f);
		norms.put(0).put(0.70710677f).put(-0.70710677f);
		norms.put(0).put(0.70710677f).put(-0.70710677f);

		// right
		norms.put(0.70710677f).put(0.70710677f).put(0);
		norms.put(0.70710677f).put(0.70710677f).put(0);
		norms.put(0.70710677f).put(0.70710677f).put(0);

		// front
		norms.put(0).put(0.70710677f).put(0.70710677f);
		norms.put(0).put(0.70710677f).put(0.70710677f);
		norms.put(0).put(0.70710677f).put(0.70710677f);

		// left
		norms.put(-0.70710677f).put(0.70710677f).put(0);
		norms.put(-0.70710677f).put(0.70710677f).put(0);
		norms.put(-0.70710677f).put(0.70710677f).put(0);
        
        norms.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setNormalBuffer(norms);
	}

	/**
	 * 
	 * <code>setTextureData</code> sets the texture that defines the look of
	 * the pyramid. The top point of the pyramid is the top center of the
	 * texture, with the remaining texture wrapping around it.
	 *  
	 */
	private void setTextureData() {
        FloatBuffer texCoords = BufferUtils.createVector2Buffer(16);

		texCoords.put(1).put(0);
		texCoords.put(0).put(0);
		texCoords.put(0).put(1);
		texCoords.put(1).put(1);

		texCoords.put(1).put(0);
		texCoords.put(0.75f).put(0);
		texCoords.put(0.5f).put(1);

		texCoords.put(0.75f).put(0);
		texCoords.put(0.5f).put(0);
		texCoords.put(0.5f).put(1);

		texCoords.put(0.5f).put(0);
		texCoords.put(0.25f).put(0);
		texCoords.put(0.5f).put(1);

		texCoords.put(0.25f).put(0);
		texCoords.put(0).put(0);
		texCoords.put(0.5f).put(1);
        
        texCoords.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setTextureBuffer(texCoords, 0);
	}

	/**
	 * 
	 * <code>setIndexData</code> sets the indices into the list of vertices,
	 * defining all triangles that constitute the pyramid.
	 *  
	 */
	private void setIndexData() {
        IntBuffer indices = BufferUtils.createIntBuffer(18);
	    indices.put(3).put(2).put(1);
	    indices.put(3).put(1).put(0);
	    indices.put(6).put(5).put(4);
	    indices.put(9).put(8).put(7);
	    indices.put(12).put(11).put(10);
	    indices.put(15).put(14).put(13);

        indices.rewind();
        TriangleBatch batch = getBatch(0);
        batch.setIndexBuffer(indices);
	}
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(height, "height", 0);
        capsule.write(width, "width", 0);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        height = capsule.readFloat("height", 0);
        width = capsule.readFloat("width", 0);
        
    }
}