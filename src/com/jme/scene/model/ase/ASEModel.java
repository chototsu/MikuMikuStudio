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
package com.jme.scene.model.ase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.TriMesh;
import com.jme.scene.model.Face;
import com.jme.scene.model.Model;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;

/**
 * <code>ASEModel</code>
 * 
 * @author Mark Powell
 * @version $Id: ASEModel.java,v 1.1 2004-02-12 23:07:07 mojomonkey Exp $
 */
public class ASEModel extends Model {

	//ASE file tags.
	private static final String OBJECT = "*GEOMOBJECT";
	private static final String NUM_VERTEX = "*MESH_NUMVERTEX";
	private static final String NUM_FACES = "*MESH_NUMFACES";
	private static final String NUM_TVERTEX = "*MESH_NUMTVERTEX";
	private static final String VERTEX = "*MESH_VERTEX";
	private static final String FACE = "*MESH_FACE";
	private static final String NORMALS = "*MESH_NORMALS";
	private static final String FACE_NORMAL = "*MESH_FACENORMAL";
	private static final String NVERTEX = "*MESH_VERTEXNORMAL";
	private static final String TVERTEX = "*MESH_TVERT";
	private static final String TFACE = "*MESH_TFACE";
	private static final String TEXTURE = "*BITMAP";
	private static final String UTILE = "*UVW_U_TILING";
	private static final String VTILE = "*UVW_V_TILING";
	private static final String UOFFSET = "*UVW_U_OFFSET";
	private static final String VOFFSET = "*UVW_V_OFFSET";
	private static final String MATERIAL_ID = "*MATERIAL_REF";
	private static final String MATERIAL_COUNT = "*MATERIAL_COUNT";
	private static final String MATERIAL = "*MATERIAL";
	private static final String MATERIAL_NAME = "*MATERIAL_NAME";
	private static final String MATERIAL_DIFFUSE = "*MATERIAL_DIFFUSE";
	private static final String MATERIAL_AMBIENT = "*MATERIAL_AMBIENT";
	private static final String MATERIAL_SPECULAR = "*MATERIAL_SPECULAR";
	private static final String MATERIAL_SHINE = "*MATERIAL_SHINE";

	//path to the model and texture file.
	private String absoluteFilePath;
	private BufferedReader reader = null;
	private StringTokenizer tokenizer;
	private String fileContents;

	private int numOfObjects; // The number of objects in the model
	private int numOfMaterials; // The number of materials for the model
	private ArrayList materials = new ArrayList();
	private ArrayList objectList = new ArrayList();
	// The object list for our model

	private class ASEMaterialInfo {
		String name; // The texture name
		public String file;
		// The texture file name (If this is set it's a texture map)
		public float[] diffuse = new float[3];
		public float[] ambient = new float[3];
		public float[] specular = new float[3];
		public float shine;
		// The color of the object (R, G, B)
		float uTile; // u tiling of texture (Currently not used)
		float vTile; // v tiling of texture (Currently not used)
		float uOffset; // u offset of texture (Currently not used)
		float vOffset; // v offset of texture (Currently not used)
	};

	// This holds all the information for our model/scene.
	// You should eventually turn into a robust class that
	// has loading/drawing/querying functions like:
	// LoadModel(...); DrawObject(...); DrawModel(...); DestroyModel(...);
	public class ASEObject extends TriMesh {
		//int numOfVerts; // The number of verts in the model
		//public int numOfFaces; // The number of faces in the model
		//int numTexVertex; // The number of texture coordinates
		public int materialID;
		// This is TRUE if there is a texture map for this object
		public String strName; // The name of the object
		public Vector3f[] tempVertices; // The object's vertices
		public Vector3f[] tempNormals; // The object's normals
		public Vector2f[] tempTexVerts; // The texture's UV coordinates
		public Face[] faces; // The faces information of the object
	};

	public void load(String file) {
		InputStream is = null;
		int fileSize = 0;
		try {
			File f = new File(file);
			absoluteFilePath = f.getAbsolutePath();
			absoluteFilePath =
				absoluteFilePath.substring(
					0,
					absoluteFilePath.lastIndexOf(File.separator) + 1);
			is = new FileInputStream(f);
			fileSize = (int) f.length();
		
			reader = new BufferedReader(new InputStreamReader(is));

			StringBuffer fc = new StringBuffer();
		
			String line;
			while ((line = reader.readLine()) != null) {
				fc.append(line + "\n");
			}

			fileContents = fc.toString();

			// Close the .ase file that we opened
			reader.close();

			parseFile();
			computeNormals();
			convertToTriMesh();
		} catch (IOException e) {
			LoggingSystem.getLogger().log(
				Level.WARNING,
				"Could not load " + file);
		}
	}
	
	/**
	 * <code>getAnimationController</code>
	 * 
	 * @return @see com.jme.scene.model.Model#getAnimationController()
	 */
	public Controller getAnimationController() {
		return null;
	}

	private void parseFile() {
		ASEMaterialInfo textureInfo = new ASEMaterialInfo();
		ASEObject mesh = new ASEObject();

		numOfObjects = getObjectCount();
		numOfMaterials = getMaterialCount();

		//Build texture list (not sure if this makes since, there can only be
		//one texture per mesh, and the are reading it in for the entire
		//object, not on a per object basis.
		for (int i = 0; i < numOfMaterials; i++) {
			materials.add(textureInfo);

			getMaterialInfo((ASEMaterialInfo) materials.get(i), i + 1);
		}

		for (int i = 0; i < numOfObjects; i++) {
			mesh.materialID = -1;
			moveToObject(i + 1);
			readObjectInfo(mesh, i + 1);
			readObjectData(mesh, i + 1);
			objectList.add(mesh);
		}

	}

	private void convertToTriMesh() {

		for (int i = 0; i < numOfObjects; i++) {
			ASEObject object = (ASEObject) objectList.get(i);
			Vector2f[] texCoords2 = new Vector2f[object.tempVertices.length];
			for (int j = 0; j < object.faces.length; j++) {
				int index = object.faces[j].vertIndex[0];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					object.tempTexVerts[object.faces[j].coordIndex[0]];

				index = object.faces[j].vertIndex[1];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					object.tempTexVerts[object.faces[j].coordIndex[1]];

				index = object.faces[j].vertIndex[2];
				texCoords2[index] = new Vector2f();
				texCoords2[index] =
					object.tempTexVerts[object.faces[j].coordIndex[2]];
			}

			object.tempTexVerts = texCoords2;

			int[] indices = new int[object.faces.length * 3];
			int count = 0;
			for (int j = 0; j < object.faces.length; j++) {
				indices[count] = object.faces[j].vertIndex[0];
				count++;
				indices[count] = object.faces[j].vertIndex[1];
				count++;
				indices[count] = object.faces[j].vertIndex[2];
				count++;
			}

			object.setIndices(indices);
			object.setVertices(object.tempVertices);
			object.setNormals(object.tempNormals);
			object.setTextures(object.tempTexVerts);
			this.attachChild(object);

		}
		
		for(int j = 0; j < numOfMaterials; j++) {
			ASEModel.ASEMaterialInfo mat = (ASEModel.ASEMaterialInfo) materials.get(j);
			if(mat.file.length() > 0) {
				MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().getMaterialState();
				ms.setEnabled(true);
				ms.setAmbient(new ColorRGBA(mat.ambient[0], mat.ambient[1], mat.ambient[2], 1));
				ms.setDiffuse(new ColorRGBA(mat.diffuse[0], mat.diffuse[1], mat.diffuse[2], 1));
				ms.setSpecular(new ColorRGBA(mat.specular[0], mat.specular[1], mat.specular[2], 1));
				ms.setEmissive(new ColorRGBA(0,0,0,1));
				ms.setShininess(mat.shine);
				this.setRenderState(ms);
			}
		}

		for (int j = 0; j < numOfMaterials; j++) {
			// Check if the current material has a file name
			if (((ASEModel.ASEMaterialInfo) materials.get(j)).file.length()
				> 0) {
				TextureState ts =
					DisplaySystem
						.getDisplaySystem()
						.getRenderer()
						.getTextureState();
				ts.setEnabled(true);
				ts.setTexture(
					TextureManager.loadTexture(
						((ASEModel.ASEMaterialInfo) materials.get(j)).file,
						Texture.MM_LINEAR,
						Texture.FM_LINEAR,
						true));
				this.setRenderState(ts);
			}

		}
	}

	private int getObjectCount() {
		int objectCount = 0;
		tokenizer = new StringTokenizer(fileContents);

		while (tokenizer.hasMoreTokens()) {
			// Check if we hit the start of an object
			if (OBJECT.equals(tokenizer.nextToken())) {
				objectCount++;
			}
		}

		return objectCount;
	}

	/**
	 * 
	 * <code>getMaterialCount</code> retrieves the number of materials in the
	 * ASE file. The file is read until the *MATERIAL flag is encountered. Once
	 * this flag is found, the value is read.
	 * 
	 * @return the number of materials as defined in the ASE file.
	 */
	private int getMaterialCount() {
		int materialCount = 0;

		// Go to the beginning of the file
		tokenizer = new StringTokenizer(fileContents);

		// GO through the whole file until we hit the end
		while (tokenizer.hasMoreTokens()) {
			if (MATERIAL_COUNT.equals(tokenizer.nextToken())) {
				materialCount = Integer.parseInt(tokenizer.nextToken());
				return materialCount;
			}
		}

		//Material tag never found
		return 0;
	}

	
	private void getMaterialInfo(ASEMaterialInfo material, int desiredMaterial) {
		String strWord;
		int materialCount = 0;

		// Go to the beginning of the file
		tokenizer = new StringTokenizer(fileContents);

		//read through the file until the correct material entry is found.
		while (tokenizer.hasMoreTokens()) {
			if (MATERIAL.equals(tokenizer.nextToken())) {
				materialCount++;

				// Check if it's the one we want to stop at, if so break
				if (materialCount == desiredMaterial)
					break;
			}
		}

		while (tokenizer.hasMoreTokens()) {
			strWord = tokenizer.nextToken();

			if (strWord.equals(MATERIAL)) {
				return;
			}

			//read material properites.
			if(strWord.equals(MATERIAL_AMBIENT)) {
				material.ambient[0] = Float.parseFloat(tokenizer.nextToken());
				material.ambient[1] = Float.parseFloat(tokenizer.nextToken());
				material.ambient[2] = Float.parseFloat(tokenizer.nextToken());
			}else if (strWord.equals(MATERIAL_DIFFUSE)) {
				material.diffuse[0] = Float.parseFloat(tokenizer.nextToken());
				material.diffuse[1] = Float.parseFloat(tokenizer.nextToken());
				material.diffuse[2] = Float.parseFloat(tokenizer.nextToken());
			} else if (strWord.equals(MATERIAL_SPECULAR)) {
				material.specular[0] = Float.parseFloat(tokenizer.nextToken());
				material.specular[1] = Float.parseFloat(tokenizer.nextToken());
				material.specular[2] = Float.parseFloat(tokenizer.nextToken());
			} else if(strWord.equals(MATERIAL_SHINE)) {
				material.shine = Float.parseFloat(tokenizer.nextToken());
			}
			
			//read texture information.
			if (strWord.equals(TEXTURE)) {
				material.file =
					absoluteFilePath
						+ tokenizer.nextToken().replace('"', ' ').trim();
			} else if (strWord.equals(MATERIAL_NAME)) {
				material.name = tokenizer.nextToken();
			} else if (strWord.equals(UTILE)) {
				material.uTile = Float.parseFloat(tokenizer.nextToken());
			} else if (strWord.equals(VTILE)) {
				material.vTile = Float.parseFloat(tokenizer.nextToken());
			}
		}
	}

	private void moveToObject(int desiredObject) {
		int objectCount = 0;

		tokenizer = new StringTokenizer(fileContents);

		while (tokenizer.hasMoreTokens()) {
			if (OBJECT.equals(tokenizer.nextToken())) {
				objectCount++;

				if (objectCount == desiredObject)
					return;
			}
		}
	}

	private void readObjectInfo(ASEObject currentObject, int desiredObject) {
		String word;

		moveToObject(desiredObject);

		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();

			if (word.equals("*NODE_NAME")) {
				currentObject.setName(tokenizer.nextToken());
			}

			if (word.equals(NUM_VERTEX)) {
				int numOfVerts = Integer.parseInt(tokenizer.nextToken());
				currentObject.tempVertices = new Vector3f[numOfVerts];
			} else if (word.equals(NUM_FACES)) {
				int numOfFaces = Integer.parseInt(tokenizer.nextToken());
				currentObject.faces = new Face[numOfFaces];
			} else if (word.equals(NUM_TVERTEX)) {
				int numTexVertex = Integer.parseInt(tokenizer.nextToken());

				currentObject.tempTexVerts = new Vector2f[numTexVertex];
			} else if (word.equals(OBJECT)) {
				return;
			}
		}
	}

	private void readObjectData(ASEObject currentObject, int desiredObject) {
		// Load the material ID for this object
		getData(currentObject, MATERIAL_ID, desiredObject);

		// Load the vertices for this object
		getData(currentObject, VERTEX, desiredObject);

		// Load the texture coordinates for this object
		getData(currentObject, TVERTEX, desiredObject);

		// Load the vertex faces list for this object
		getData(currentObject, FACE, desiredObject);

		// Load the texture face list for this object
		getData(currentObject, TFACE, desiredObject);

		// Load the texture for this object
		getData(currentObject, TEXTURE, desiredObject);

		// Load the U tile for this object
		getData(currentObject, UTILE, desiredObject);

		// Load the V tile for this object
		getData(currentObject, VTILE, desiredObject);
	}

	private void getData( ASEObject currentObject, String desiredData, int desiredObject) {
		String word;

		moveToObject(desiredObject);

		// Go through the file until we reach the end
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();

			// If we reached an object tag, stop read because we went to far
			if (word.equals(OBJECT)) {
				// Stop reading because we are done with the current object
				return;
			}
			// If we hit a vertex tag
			else if (word.equals(VERTEX)) {
				// Make sure that is the data that we want to read in
				if (desiredData.equals(VERTEX)) {
					// Read in a vertex
					readVertex(currentObject);
				}
			}
			// If we hit a texture vertex
			else if (word.equals(TVERTEX)) {
				// Make sure that is the data that we want to read in
				if (desiredData.equals(TVERTEX)) {
					// Read in a texture vertex
					readTextureVertex(
						currentObject,
						(ASEMaterialInfo) materials.get(currentObject.materialID));
				}
			}
			// If we hit a vertice index to a face
			else if (word.equals(FACE)) {
				// Make sure that is the data that we want to read in
				if (desiredData.equals(FACE)) {
					// Read in a face
					readFace(currentObject);
				}
			}
			// If we hit a texture index to a face
			else if (word.equals(TFACE)) {
				// Make sure that is the data that we want to read in
				if (desiredData.equals(TFACE)) {
					// Read in a texture indice for a face
					readTextureFace(currentObject);
				}
			}
			// If we hit the material ID to the object
			else if (word.equals(MATERIAL_ID)) {
				// Make sure that is the data that we want to read in
				if (desiredData.equals(MATERIAL_ID)) {
					// Read in the material ID assigned to this object
					currentObject.materialID =
						(int) Float.parseFloat(tokenizer.nextToken());
					return;
				}
			}
		}
	}

	private void readVertex(ASEObject currentObject) {
		int index = 0;

		// Read past the vertex index
		index = Integer.parseInt(tokenizer.nextToken());
		currentObject.tempVertices[index] = new Vector3f();

		//convert to standard coordinate axis.
		currentObject.tempVertices[index].x = Float.parseFloat(tokenizer.nextToken());
		currentObject.tempVertices[index].z = -Float.parseFloat(tokenizer.nextToken());
		currentObject.tempVertices[index].y = Float.parseFloat(tokenizer.nextToken());

	}

	private void readTextureVertex(ASEObject currentObject, ASEMaterialInfo texture) {
		int index = 0;

		// Here we read past the index of the texture coordinate
		index = Integer.parseInt(tokenizer.nextToken());
		currentObject.tempTexVerts[index] = new Vector2f();

		// Next, we read in the (U, V) texture coordinates.
		currentObject.tempTexVerts[index].x = Float.parseFloat(tokenizer.nextToken());
		currentObject.tempTexVerts[index].y = Float.parseFloat(tokenizer.nextToken());

		currentObject.tempTexVerts[index].x *= texture.uTile;
		currentObject.tempTexVerts[index].y *= texture.vTile;

	}

	private void readFace(ASEObject currentObject) {
		int index = 0;

		// Read past the index of this Face
		String temp = tokenizer.nextToken();
		if (temp.indexOf(":") > 0) {
			temp = temp.substring(0, temp.length() - 1);
		}
		index = Integer.parseInt(temp);
		currentObject.faces[index] = new Face();

		tokenizer.nextToken(); // "A:"
		currentObject.faces[index].vertIndex[0] =
			Integer.parseInt(tokenizer.nextToken());
		tokenizer.nextToken(); // "B:"
		currentObject.faces[index].vertIndex[1] =
			Integer.parseInt(tokenizer.nextToken());
		tokenizer.nextToken(); // "C:"
		currentObject.faces[index].vertIndex[2] =
			Integer.parseInt(tokenizer.nextToken());
	}

	private void readTextureFace(ASEObject currentObject) {
		int index = 0;

		// Read past the index for this texture coordinate
		index = Integer.parseInt(tokenizer.nextToken());

		// Now we read in the UV coordinate index for the current face.
		// This will be an index into pTexCoords[] for each point in the face.
		currentObject.faces[index].coordIndex[0] =
			Integer.parseInt(tokenizer.nextToken());
		currentObject.faces[index].coordIndex[1] =
			Integer.parseInt(tokenizer.nextToken());
		currentObject.faces[index].coordIndex[2] =
			Integer.parseInt(tokenizer.nextToken());
	}

	private void computeNormals() {
		Vector3f vector1 = new Vector3f();
		Vector3f vector2 = new Vector3f();
		Vector3f normal = new Vector3f();
		Vector3f[] triangle = new Vector3f[3];

		// If there are no objects, we can skip this part
		if (numOfObjects <= 0)
			return;

		// Go through each of the objects to calculate their normals
		for (int index = 0; index < numOfObjects; index++) {
			// Get the current object
			ASEObject object = (ASEObject) objectList.get(index);

			// Here we allocate all the memory we need to calculate the normals
			Vector3f[] pNormals = new Vector3f[object.faces.length];
			Vector3f[] pTempNormals = new Vector3f[object.faces.length];
			object.tempNormals = new Vector3f[object.tempVertices.length];

			// Go though all of the faces of this object
			for (int i = 0; i < object.faces.length; i++) {
				// To cut down LARGE code, we extract the 3 points of this face
				triangle[0] = object.tempVertices[object.faces[i].vertIndex[0]];
				triangle[1] = object.tempVertices[object.faces[i].vertIndex[1]];
				triangle[2] = object.tempVertices[object.faces[i].vertIndex[2]];

				// Now let's calculate the face normals (Get 2 vectors and find
				// the cross product of those 2)

				vector1 = triangle[0].subtract(triangle[2]);
				// Get the vector of the polygon (we just need 2 sides for the
				// normal)
				vector2 = triangle[2].subtract(triangle[1]);
				// Get a second vector of the polygon

				normal = vector1.cross(vector2);
				pTempNormals[i] = normal;
				normal = normal.normalize();
				pNormals[i] = normal;
			}

			Vector3f sum = new Vector3f();
			Vector3f zero = sum;
			int shared = 0;

			for (int i = 0;
				i < object.tempVertices.length;
				i++) // Go through all of the vertices
				{
				for (int j = 0;
					j < object.faces.length;
					j++) // Go through all of the triangles
					{ // Check if the vertex is shared by another face
					if (object.faces[j].vertIndex[0] == i
						|| object.faces[j].vertIndex[1] == i
						|| object.faces[j].vertIndex[2] == i) {
						sum = sum.add(pTempNormals[j]);
						// Add the un-normalized normal of the shared face
						shared++; // Increase the number of shared triangles
					}
				}

				// Get the normal by dividing the sum by the shared. We negate
				// the shared so it has the normals pointing out.
				object.tempNormals[i] = sum.divide((float) (-shared));

				// Normalize the normal for the final vertex normal
				object.tempNormals[i] = object.tempNormals[i].normalize();

				sum = zero; // Reset the sum
				shared = 0; // Reset the shared
			}
		}
	}

	

}
