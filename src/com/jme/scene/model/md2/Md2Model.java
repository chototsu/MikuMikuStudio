/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package com.jme.scene.model.md2;

import java.io.File;
import java.io.InputStream;

import com.jme.animation.VertexKeyframeController;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.scene.model.Model;
import com.jme.system.JmeException;
import com.jme.util.BinaryFileReader;

/**
 * <code>Md2Model</code> defines a model using the MD2 model format made
 * common by Quake 2. This loader builds the mesh of each frame of animation
 * then builds the animation controller that allows the shown mesh to be
 * displayed at any given time. The memory footprint may be quite large
 * depending on how many key frames exist, and how many vertices within the
 * mesh.
 * 
 * @author Mark Powell
 * @version $Id: Md2Model.java,v 1.3 2004-02-06 21:14:24 mojomonkey Exp $
 */
public class Md2Model extends Model {
	private BinaryFileReader bis = null;

	private Header header; // The header data

	private Vector2f[] texCoords; // The texture coordinates
	private Md2Face[] triangles; // Face index information
	private Md2Frame[] frames; // The frames of animation (vertices)

	//holds each keyframe.
	private TriMesh[] triMesh;

	private VertexKeyframeController controller;

	/**
	 * <code>load</code>
	 * 
	 * @param filename
	 * @see com.jme.scene.model.Model#load(java.lang.String)
	 */
	public void load(String filename) {
		InputStream is = null;
		int fileSize = 0;
		File file = new File(filename);
		bis = new BinaryFileReader(file);

		header = new Header();

		if (header.version != 8) {
			throw new JmeException(
				"Invalid file format (Version not 8): " + filename + "!");
		}

		parseMesh();
		convertDataStructures();

		triangles = null;
		texCoords = null;
		frames = null;
	}

	/**
	 * <code>getAnimationController</code>
	 * 
	 * @return @see com.jme.scene.model.Model#getAnimationController()
	 */
	public Controller getAnimationController() {
		return controller;
	}

	private void parseMesh() {
		// Here we allocate all of our memory from the header's information
		String[] skins = new String[header.numSkins];
		texCoords = new Vector2f[header.numTexCoords];
		triangles = new Md2Face[header.numTriangles];
		frames = new Md2Frame[header.numFrames];

		// Next, we start reading in the data by seeking to our skin names
		// offset
		bis.setOffset(header.offsetSkins);

		// Depending on the skin count, we read in each skin for this model
		for (int j = 0; j < header.numSkins; j++) {
			skins[j] = bis.readString(64);
		}

		// Move the file pointer to the position in the file for texture
		// coordinates
		bis.setOffset(header.offsetTexCoords);

		// Read in all the texture coordinates in one fell swoop
		for (int j = 0; j < header.numTexCoords; j++) {
			texCoords[j] = new Vector2f();
			texCoords[j].x = bis.readShort();
			texCoords[j].y = bis.readShort();
		}

		// Move the file pointer to the triangles/face data offset
		bis.setOffset(header.offsetTriangles);

		// Read in the face data for each triangle (vertex and texCoord
		// indices)
		for (int j = 0; j < header.numTriangles; j++) {
			triangles[j] = new Md2Face();
		}

		// Move the file pointer to the vertices (frames)
		bis.setOffset(header.offsetFrames);

		for (int i = 0; i < header.numFrames; i++) {
			// Assign our alias frame to our buffer memory
			VectorKeyframe pFrame = new VectorKeyframe();

			// Allocate the memory for the first frame of animation's vertices
			frames[i] = new Md2Frame();

			frames[i].pVertices = new Triangle[header.numVertices];
			Vector3f[] aliasVertices = new Vector3f[header.numVertices];
			int[] aliasLightNormals = new int[header.numVertices];

			// Read in the first frame of animation
			for (int j = 0; j < header.numVertices; j++) {
				aliasVertices[j] =
					new Vector3f(
						bis.readByte(),
						bis.readByte(),
						bis.readByte());
				aliasLightNormals[j] = bis.readByte();
			}

			// Copy the name of the animation to our frames array
			frames[i].strName = pFrame.name;

			Triangle[] pVertices = frames[i].pVertices;

			for (int j = 0; j < header.numVertices; j++) {
				pVertices[j] = new Triangle();
				pVertices[j].vertex.x =
					aliasVertices[j].x * pFrame.scale.x + pFrame.translate.x;
				pVertices[j].vertex.z =
					-1
						* (aliasVertices[j].y * pFrame.scale.y
							+ pFrame.translate.y);
				pVertices[j].vertex.y =
					aliasVertices[j].z * pFrame.scale.z + pFrame.translate.z;
			}
		}
	}

	private void convertDataStructures() {
		triMesh = new TriMesh[header.numFrames];
		Vector2f[] texCoords2 = new Vector2f[header.numVertices];

		for (int i = 0; i < header.numFrames; i++) {
			int numOfVerts = header.numVertices;
			int numTexVertex = header.numTexCoords;
			int numOfFaces = header.numTriangles;
			triMesh[i] = new TriMesh();
			Vector3f[] verts = new Vector3f[numOfVerts];
			Vector2f[] texVerts = new Vector2f[numTexVertex];
			
			Face[] faces = new Face[numOfFaces];

			// Go through all of the vertices and assign them over to our
			// structure
			for (int j = 0; j < numOfVerts; j++) {
				verts[j] = new Vector3f();
				verts[j].x = frames[i].pVertices[j].vertex.x;
				verts[j].y = frames[i].pVertices[j].vertex.y;
				verts[j].z = frames[i].pVertices[j].vertex.z;
			}

			// Go through all of the face data and assign it over to OUR
			// structure
			for (int j = 0; j < numOfFaces; j++) {
				faces[j] = new Face();
				// Assign the vertex indices to our face data
				faces[j].vertIndex[0] = triangles[j].vertexIndices[0];
				faces[j].vertIndex[1] = triangles[j].vertexIndices[1];
				faces[j].vertIndex[2] = triangles[j].vertexIndices[2];

				// Assign the texture coord indices to our face data
				faces[j].coordIndex[0] = triangles[j].textureIndices[0];
				faces[j].coordIndex[1] = triangles[j].textureIndices[1];
				faces[j].coordIndex[2] = triangles[j].textureIndices[2];
			}

			if (i == 0) {
				for (int j = 0; j < numTexVertex; j++) {
					texVerts[j] = new Vector2f();
					texVerts[j].x = texCoords[j].x / (float) (header.skinWidth);
					texVerts[j].y =
						1 - texCoords[j].y / (float) (header.skinHeight);
				}

				for (int j = 0; j < numOfFaces; j++) {
					int index = faces[j].vertIndex[0];
					texCoords2[index] = new Vector2f();
					texCoords2[index] = texVerts[faces[j].coordIndex[0]];

					index = faces[j].vertIndex[1];
					texCoords2[index] = new Vector2f();
					texCoords2[index] = texVerts[faces[j].coordIndex[1]];

					index = faces[j].vertIndex[2];
					texCoords2[index] = new Vector2f();
					texCoords2[index] = texVerts[faces[j].coordIndex[2]];
				}
			
				int[] indices = new int[numOfFaces * 3];
				int count = 0;
				for (int j = 0; j < numOfFaces; j++) {
					indices[count] = faces[j].vertIndex[0];
					count++;
					indices[count] = faces[j].vertIndex[1];
					count++;
					indices[count] = faces[j].vertIndex[2];
					count++;
				}
				triMesh[i].setIndices(indices);
			}

			triMesh[i].setVertices(verts);
			triMesh[i].setNormals(computeNormals(faces, verts));
			triMesh[i].setName(frames[i].strName);
			
			if (i == 0) {
				triMesh[i].setTextures(texCoords2);
				triMesh[i].setModelBound(new BoundingSphere());
				triMesh[i].updateModelBound();
			}
		}

		controller = new VertexKeyframeController();
		controller.setKeyframes(triMesh);
		controller.setDisplayedMesh(triMesh[0]);
		this.attachChild(triMesh[0]);
		this.addController(controller);

	}

	private Vector3f[] computeNormals(Face[] faces, Vector3f[] verts) {
		Vector3f[] returnNormals = new Vector3f[verts.length];

		Vector3f[] normals = new Vector3f[faces.length];
		Vector3f[] tempNormals = new Vector3f[faces.length];

		for (int i = 0; i < faces.length; i++) {
			tempNormals[i] =
				verts[faces[i].vertIndex[0]].subtract(
					verts[faces[i].vertIndex[2]]).cross(
					verts[faces[i].vertIndex[2]].subtract(
						verts[faces[i].vertIndex[1]]));
			normals[i] = tempNormals[i].normalize();
		}

		Vector3f sum = new Vector3f();
		Vector3f zero = sum;
		int shared = 0;

		for (int i = 0; i < verts.length; i++) {
			for (int j = 0; j < faces.length; j++) {
				if (faces[j].vertIndex[0] == i
					|| faces[j].vertIndex[1] == i
					|| faces[j].vertIndex[2] == i) {
					sum = sum.add(tempNormals[j]);
					shared++;
				}
			}

			returnNormals[i] = sum.divide(-shared);
			returnNormals[i] = returnNormals[i].normalize().negate();

			sum = zero;
			shared = 0;
		}

		return returnNormals;
	}

	// This holds the header information that is read in at the beginning of
	// the file
	private class Header {
		int magic; // This is used to identify the file
		int version; // The version number of the file (Must be 8)
		int skinWidth; // The skin width in pixels
		int skinHeight; // The skin height in pixels
		int frameSize; // The size in bytes the frames are
		int numSkins; // The number of skins associated with the model
		int numVertices; // The number of vertices (constant for each frame)
		int numTexCoords; // The number of texture coordinates
		int numTriangles; // The number of faces (polygons)
		int numGlCommands; // The number of gl commands
		int numFrames; // The number of animation frames
		int offsetSkins; // The offset in the file for the skin data
		int offsetTexCoords; // The offset in the file for the texture data
		int offsetTriangles; // The offset in the file for the face data
		int offsetFrames; // The offset in the file for the frames data
		int offsetGlCommands;
		// The offset in the file for the gl commands data
		int offsetEnd; // The end of the file offset

		Header() {
			magic = bis.readInt();
			version = bis.readInt();
			skinWidth = bis.readInt();
			skinHeight = bis.readInt();
			frameSize = bis.readInt();
			numSkins = bis.readInt();
			numVertices = bis.readInt();
			numTexCoords = bis.readInt();
			numTriangles = bis.readInt();
			numGlCommands = bis.readInt();
			numFrames = bis.readInt();
			offsetSkins = bis.readInt();
			offsetTexCoords = bis.readInt();
			offsetTriangles = bis.readInt();
			offsetFrames = bis.readInt();
			offsetGlCommands = bis.readInt();
			offsetEnd = bis.readInt();
		}

	};

	// This stores the normals and vertices for the frames
	private class Triangle {
		Vector3f vertex = new Vector3f();
		Vector3f normal = new Vector3f();
	};

	// This stores the indices into the vertex and texture coordinate arrays
	private class Md2Face {
		int[] vertexIndices = new int[3]; // short
		int[] textureIndices = new int[3]; // short

		Md2Face() {
			vertexIndices =
				new int[] { bis.readShort(), bis.readShort(), bis.readShort()};
			textureIndices =
				new int[] { bis.readShort(), bis.readShort(), bis.readShort()};
		}
	};

	// This stores the animation scale, translation and name information for a
	// frame, plus verts
	private class VectorKeyframe {
		private Vector3f scale = new Vector3f();
		private Vector3f translate = new Vector3f();
		private String name;

		VectorKeyframe() {
			scale.x = bis.readFloat();
			scale.y = bis.readFloat();
			scale.z = bis.readFloat();

			translate.x = bis.readFloat();
			translate.y = bis.readFloat();
			translate.z = bis.readFloat();
			name = bis.readString(16);
		}
	};

	// This stores the frames vertices after they have been transformed
	private class Md2Frame {
		String strName; // char [16]
		Triangle[] pVertices;

		Md2Frame() {
		}
	};

	// This is our face structure. This is is used for indexing into the vertex
	// and texture coordinate arrays. From this information we know which
	// vertices
	// from our vertex array go to which face, along with the correct texture
	// coordinates.
	private class Face {
		public int[] vertIndex = new int[3];
		public int[] coordIndex = new int[3];
	};
}
