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

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.scene.model.Model;
import com.jme.system.JmeException;
import com.jme.util.BinaryFileReader;

/**
 * <code>Md2Model</code>
 * 
 * @author Mark Powell
 * @version $Id: Md2Model.java,v 1.1 2004-02-05 22:41:37 mojomonkey Exp $
 */
public class Md2Model extends Model {
	private BinaryFileReader bis = null;
	private int numOfObjects;
	private ModelObject[] objects;
	private Header header; // The header data
	private String[] skins; // The skin data
	private Vector2f[] texCoords; // The texture coordinates
	private Md2Face[] triangles; // Face index information
	private Md2Frame[] frames; // The frames of animation (vertices)

	private TriMesh[] triMesh;

	// This holds the header information that is read in at the beginning of
	// the file
	class Header {
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

	// This is used to store the vertices that are read in for the current
	// frame
//	class FrameTriangle {
//		int[] vertex; //byte
//		int lightNormalIndex; //byte
//
//		FrameTriangle() {
//			vertex =
//				new int[] { bis.readByte(), bis.readByte(), bis.readByte()};
//			lightNormalIndex = bis.readByte();
//		}
//	};

	// This stores the normals and vertices for the frames
	class Triangle {
		Vector3f vertex = new Vector3f();
		Vector3f normal = new Vector3f();
	};

	// This stores the indices into the vertex and texture coordinate arrays
	class Md2Face {
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
	public class VectorKeyframe {
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
	class Md2Frame {
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
	public class Face {
		public int[] vertIndex = new int[3];
		// indicies for the verts that make up this triangle
		public int[] coordIndex = new int[3];
		// indicies for the tex coords to texture this face
	};

	// This holds all the information for our model/scene.
	public class ModelObject {
		public int numOfVerts; // The number of verts in the model
		public int numOfFaces; // The number of faces in the model
		public int numTexVertex; // The number of texture coordinates

		public String strName; // The name of the object
		public Vector3f[] pVerts; // The object's vertices
		public Vector3f[] pNormals; // The object's normals
		public Vector2f[] pTexVerts; // The texture's UV coordinates
		public Face[] pFaces; // The faces information of the object
	};

	private void parseMesh() {
		// Here we allocate all of our memory from the header's information
		skins = new String[header.numSkins];
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

		// Assign our alias frame to our buffer memory
		VectorKeyframe pFrame = new VectorKeyframe();

		// Allocate the memory for the first frame of animation's vertices
		frames[0] = new Md2Frame();

		frames[0].pVertices = new Triangle[header.numVertices];
		Vector3f[] aliasVertices = new Vector3f[header.numVertices];
		int[] aliasLightNormals = new int[header.numVertices];

		// Read in the first frame of animation
		for (int j = 0; j < header.numVertices; j++) {
			aliasVertices[j] = new Vector3f(bis.readByte(), bis.readByte(), bis.readByte());
			aliasLightNormals[j] = bis.readByte();
		}

		// Copy the name of the animation to our frames array
		frames[0].strName = pFrame.name;

		Triangle[] pVertices = frames[0].pVertices;

		// Go through all of the number of vertices and assign the scale and
		// translations.
		// Store the vertices in our current frame's vertex list array, while
		// swapping Y and Z.
		// Notice we also negate the Z axis as well to make the swap correctly.
		for (int j = 0; j < header.numVertices; j++) {
			pVertices[j] = new Triangle();
			pVertices[j].vertex.x =
				aliasVertices[j].x * pFrame.scale.x
					+ pFrame.translate.x;
			pVertices[j].vertex.z =
				-1
					* (aliasVertices[j].y * pFrame.scale.y
						+ pFrame.translate.y);
			pVertices[j].vertex.y =
				aliasVertices[j].z * pFrame.scale.z
					+ pFrame.translate.z;
		}
	}

	private void convertDataStructures() {
		// Assign the number of objects, which is 1 since we only want 1 frame
		// of animation. In the next tutorial each object will be a key frame
		// to interpolate between.
		numOfObjects = 1;
		objects = new ModelObject[numOfObjects];
		triMesh = new TriMesh[numOfObjects];

		for (int i = 0; i < numOfObjects; i++) {
			// Create a local object to store the first frame of animation's
			// data
			ModelObject currentFrame = new ModelObject();
			objects[0] = currentFrame;
			// Assign the vertex, texture coord and face count to our new
			// structure
			currentFrame.numOfVerts = header.numVertices;
			currentFrame.numTexVertex = header.numTexCoords;
			currentFrame.numOfFaces = header.numTriangles;

			// Allocate memory for the vertices, texture coordinates and face
			// data.
			currentFrame.pVerts = new Vector3f[currentFrame.numOfVerts];
			currentFrame.pTexVerts = new Vector2f[currentFrame.numTexVertex];
			currentFrame.pFaces = new Face[currentFrame.numOfFaces];

			// Go through all of the vertices and assign them over to our
			// structure
			for (int j = 0; j < currentFrame.numOfVerts; j++) {
				currentFrame.pVerts[j] = new Vector3f();
				currentFrame.pVerts[j].x = frames[0].pVertices[j].vertex.x;
				currentFrame.pVerts[j].y = frames[0].pVertices[j].vertex.y;
				currentFrame.pVerts[j].z = frames[0].pVertices[j].vertex.z;
			}

			// Go through all of the face data and assign it over to OUR
			// structure
			for (int j = 0; j < currentFrame.numOfFaces; j++) {
				currentFrame.pFaces[j] = new Face();
				// Assign the vertex indices to our face data
				currentFrame.pFaces[j].vertIndex[0] =
					triangles[j].vertexIndices[0];
				currentFrame.pFaces[j].vertIndex[1] =
					triangles[j].vertexIndices[1];
				currentFrame.pFaces[j].vertIndex[2] =
					triangles[j].vertexIndices[2];

				// Assign the texture coord indices to our face data
				currentFrame.pFaces[j].coordIndex[0] =
					triangles[j].textureIndices[0];
				currentFrame.pFaces[j].coordIndex[1] =
					triangles[j].textureIndices[1];
				currentFrame.pFaces[j].coordIndex[2] =
					triangles[j].textureIndices[2];
			}

			for (int j = 0; j < currentFrame.numTexVertex; j++) {
				currentFrame.pTexVerts[j] = new Vector2f();
				currentFrame.pTexVerts[j].x =
					texCoords[j].x / (float) (header.skinWidth);
				currentFrame.pTexVerts[j].y =
					1 - texCoords[j].y / (float) (header.skinHeight);
			}

			Vector2f[] texCoords2 = new Vector2f[currentFrame.pVerts.length];

			for (int j = 0; j < currentFrame.numOfFaces; j++) {
				int index = currentFrame.pFaces[j].vertIndex[0];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					currentFrame
						.pTexVerts[currentFrame
						.pFaces[j]
						.coordIndex[0]];

				index = currentFrame.pFaces[j].vertIndex[1];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					currentFrame
						.pTexVerts[currentFrame
						.pFaces[j]
						.coordIndex[1]];

				index = currentFrame.pFaces[j].vertIndex[2];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					currentFrame
						.pTexVerts[currentFrame
						.pFaces[j]
						.coordIndex[2]];
			}

			currentFrame.pTexVerts = texCoords2;
			// Here we add the current object (or frame) to our list object
			// list

			int[] indices = new int[currentFrame.numOfFaces * 3];
			int count = 0;
			for (int j = 0; j < currentFrame.numOfFaces; j++) {
				indices[count] = currentFrame.pFaces[j].vertIndex[0];
				count++;
				indices[count] = currentFrame.pFaces[j].vertIndex[1];
				count++;
				indices[count] = currentFrame.pFaces[j].vertIndex[2];
				count++;
			}

			computeNormals();

			
			triMesh[i] = new TriMesh();
			triMesh[i].setVertices(currentFrame.pVerts);
			triMesh[i].setTextures(texCoords2);
			triMesh[i].setNormals(currentFrame.pNormals);
			triMesh[i].setIndices(indices);
			triMesh[i].setName(currentFrame.strName);
			this.attachChild(triMesh[i]);
			triMesh[i].setModelBound(new BoundingSphere());
			triMesh[i].updateModelBound();
		}
	}

	private void computeNormals() {
		Vector3f vVector1 = new Vector3f();
		Vector3f vVector2 = new Vector3f();
		Vector3f vNormal = new Vector3f();
		Vector3f[] vPoly = new Vector3f[3];

		// If there are no objects, we can skip this part
		if (numOfObjects <= 0)
			return;

		// Go through each of the objects to calculate their normals
		for (int index = 0; index < numOfObjects; index++) {
			// Get the current object
			ModelObject object = objects[index];

			// Here we allocate all the memory we need to calculate the normals
			Vector3f[] pNormals = new Vector3f[object.numOfFaces];
			Vector3f[] pTempNormals = new Vector3f[object.numOfFaces];
			object.pNormals = new Vector3f[object.numOfVerts];

			// Go though all of the faces of this object
			for (int i = 0; i < object.numOfFaces; i++) {
				// To cut down LARGE code, we extract the 3 points of this face
				vPoly[0] = object.pVerts[object.pFaces[i].vertIndex[0]];
				vPoly[1] = object.pVerts[object.pFaces[i].vertIndex[1]];
				vPoly[2] = object.pVerts[object.pFaces[i].vertIndex[2]];

				// Now let's calculate the face normals (Get 2 vectors and find
				// the cross product of those 2)

				vVector1 = vPoly[0].subtract(vPoly[2]);
				// Get the vector of the polygon (we just need 2 sides for the
				// normal)
				vVector2 = vPoly[2].subtract(vPoly[1]);
				// Get a second vector of the polygon

				vNormal = vVector1.cross(vVector2);
				// Return the cross product of the 2 vectors (normalize vector,
				// but not a unit vector)
				pTempNormals[i] = vNormal;
				// Save the un-normalized normal for the vertex normals
				vNormal = vNormal.normalize();
				// Normalize the cross product to give us the polygons normal
				pNormals[i] = new Vector3f();
				pNormals[i].x = -vNormal.x;
				pNormals[i].y = -vNormal.y;
				pNormals[i].z = -vNormal.z;
				// Assign the normal to the list of normals
			}

			//////////////// Now Get The Vertex Normals /////////////////

			Vector3f vSum = new Vector3f();
			Vector3f vZero = vSum;
			int shared = 0;

			for (int i = 0;
				i < object.numOfVerts;
				i++) // Go through all of the vertices
				{
				for (int j = 0;
					j < object.numOfFaces;
					j++) // Go through all of the triangles
					{ // Check if the vertex is shared by another face
					if (object.pFaces[j].vertIndex[0] == i
						|| object.pFaces[j].vertIndex[1] == i
						|| object.pFaces[j].vertIndex[2] == i) {
						vSum = vSum.add(pTempNormals[j]);
						// Add the un-normalized normal of the shared face
						shared++; // Increase the number of shared triangles
					}
				}

				// Get the normal by dividing the sum by the shared. We negate
				// the shared so it has the normals pointing out.
				object.pNormals[i] = vSum.divide(-shared);

				// Normalize the normal for the final vertex normal
				object.pNormals[i] = object.pNormals[i].normalize().negate();
				vSum = vZero; // Reset the sum
				shared = 0; // Reset the shared
			}
		}
	}

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
	}

	/**
	 * <code>getAnimationController</code>
	 * 
	 * @return @see com.jme.scene.model.Model#getAnimationController()
	 */
	public Controller getAnimationController() {
		// TODO Auto-generated method stub
		return null;
	}
}
