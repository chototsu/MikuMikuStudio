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

package com.jme.scene.model.msascii;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.model.*;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.util.TextureManager;
import com.jme.animation.*;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * <code>MilkshapeASCIIModel</code> defines a model using the data defined
 * in a Milkshape ASCII text file. This model loader builds the mesh of the
 * model as well as the animation controller. The model's meshes are added
 * to this model as children. The animation controller is set to clamp by
 * default, with frequency of 1. If a faster animation or different repeat
 * type is desired, the controller can be obtained via the
 * <code>getAnimationController</code> method.
 * @author Mark Powell
 * @version $Id: MilkshapeASCIIModel.java,v 1.16 2004-04-22 22:26:52 renanse Exp $
 */
public class MilkshapeASCIIModel extends Model {
	//the meshes that make up this model.
	private JointMesh[] meshes;
	//the controller that handles this model's animations.
	private DeformationJointController jointController;
	//the path to the file.
	private String textureDirectory = "";

	private ColorRGBA color = new ColorRGBA(1,1,1,1);

	/**
	 * Constructor creates a new <code>MilkshapeASCIIModel</code> object.
	 * No data is loaded during this construction.
	 * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
	 */
	public MilkshapeASCIIModel(String name) {
		super(name);
	}

	/**
	 * Constructor creates a new <code>MilkshapeASCIIModel</code> loading
	 * the provided file.
	 * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
	 * @param filename the name of the file that contains the milkshape
	 * data.
	 */
	public MilkshapeASCIIModel(String name, String filename) {
		super(name);
		load(filename);
	}

	/**
	 * Loads an ascii text model exported from MS3D. The corresponding
	 * <code>JointMesh</code> objects are created and attached to the
	 * model. Materials are then assigned to each mesh. Lastly, the
	 * joints and joint controller are loaded and initialized.
	 * @param filename the file to load.
	 */
	public void load(String filename) {
		try {
			URL file = new URL("file:"+filename);
			load(file);
		} catch (MalformedURLException e) {
			LoggingSystem.getLogger().log(Level.WARNING, "Could not load " +
					filename);
		}
	}

    public void load(String filename, String textureDirectory) {
    	this.textureDirectory = textureDirectory;
        try {
            URL file = new URL("file:"+filename);
            load(file);
        } catch (MalformedURLException e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Could not load " +
                    filename);
        }


    }

    public void load(URL filename, String textureDirectory) {
        this.textureDirectory = textureDirectory;
        load(filename);
    }

	/**
	 * Loads an ascii text model exported from MS3D. The corresponding
	 * <code>JointMesh</code> objects are created and attached to the
	 * model. Materials are then assigned to each mesh. Lastly, the
	 * joints and joint controller are loaded and initialized.
	 * @param filename the url of the file to load.
	 */
	public void load(URL filename) {
		if(null == filename) {
			throw new JmeException("Null data. Cannot load.");
		}
		//attempt to load and parse the data.
		try {
			//add a controller for animations.
			jointController = new DeformationJointController();

			BufferedReader reader = new BufferedReader(new InputStreamReader(filename.openStream()));

			String line;
			while ((line = getNextLine(reader)) != null) {
				if (line.startsWith("Frames: ")) {
					jointController.setTotalFrames(
						Integer.parseInt(line.substring(8)));
				}
				if (line.startsWith("Frame: ")) {
					jointController.setCurrentFrame(
						Integer.parseInt(line.substring(7)));
				}
				if (line.startsWith("Meshes: ")) {
					int numberMeshes = Integer.parseInt(line.substring(8));
					meshes = new JointMesh[numberMeshes];

					parseMeshes(reader);
					jointController.setMeshes(meshes);
				}
				if (line.startsWith("Materials: ")) {
					parseMaterials(
						reader,
						Integer.parseInt(line.substring(11)));
				}
				if (line.startsWith("Bones: ")) {
					int numberJoints = Integer.parseInt(line.substring(7));
					jointController.setJoints(new DeformationJoint[numberJoints]);
					parseJoints(reader);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JmeException("Error loading Milkshape ASCII " + filename);
		}

		jointController.setupJointAnimations();
		this.addController(jointController);
	}

	/**
	 * <code>getAnimationController</code> returns the controller assigned
	 * to this model. This controller handles updating the models joints.
	 */
	public Controller getAnimationController() {
		return jointController;
	}

	/**
	 * <code>parseMeshes</code> reads the information about the meshes.
	 * This includes vertices, texture coordinates, normals and triangle
	 * indices.
	 * @param reader the file reader.
	 * @throws Exception throws an exception if there was an error reading
	 * 		any of the mesh data.
	 */
	private void parseMeshes(BufferedReader reader) throws Exception {
		//load in each mesh individually.
		for (int i = 0; i < meshes.length; i++) {
			String line = getNextLine(reader);
			JointMesh mesh = new JointMesh(line.substring(1, line.lastIndexOf("\"")));

			mesh.setMaterialsIndex(
				Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1)));

			line = getNextLine(reader);

			int numberVertices = Integer.parseInt(line);
			Vector3f[] vertices = new Vector3f[numberVertices];
			Vector2f[] tex = new Vector2f[numberVertices];
			int[] jointIndices = new int[numberVertices];
			//load the vertices, textures and joint
			for (int j = 0; j < numberVertices; j++) {
				line = getNextLine(reader);
				String[] values = line.split(" ");
				vertices[j] =
					new Vector3f(
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]),
						Float.parseFloat(values[3]));
				tex[j] =
					new Vector2f(
						Float.parseFloat(values[4]),
						1 - Float.parseFloat(values[5]));
				jointIndices[j] = Integer.parseInt(values[6]);
			}

			line = getNextLine(reader);
			//read in the normals
			int numberNormals = Integer.parseInt(line);
			float[][] normals = new float[numberNormals][3];
			for (int j = 0; j < numberNormals; j++) {
				line = getNextLine(reader);
				String[] values = line.split(" ");
				normals[j] =
					new float[] {
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2])};
			}

			Vector3f[] norms = new Vector3f[numberVertices];

			line = getNextLine(reader);
			int numberTriangles = Integer.parseInt(line);
			int[] ind = new int[numberTriangles * 3];
			int count = 0;

			//read in triangle information and set the corresponding normals.
			//Milkshape does not assign normals to each vertex, so this is
			//done to play nice with OpenGL.
			for (int j = 0; j < numberTriangles; j++) {
				line = getNextLine(reader);
				String[] values = line.split(" ");
				float[] temp = normals[Integer.parseInt(values[4])];
				norms[Integer.parseInt(values[1])] =
					new Vector3f(temp[0], temp[1], temp[2]);
				temp = normals[Integer.parseInt(values[5])];
				norms[Integer.parseInt(values[2])] =
					new Vector3f(temp[0], temp[1], temp[2]);
				temp = normals[Integer.parseInt(values[6])];
				norms[Integer.parseInt(values[3])] =
					new Vector3f(temp[0], temp[1], temp[2]);

				ind[count] = Integer.parseInt(values[1]);
				count++;
				ind[count] = Integer.parseInt(values[2]);
				count++;
				ind[count] = Integer.parseInt(values[3]);
				count++;
			}

			//create a copy of the original vertices so we don't
			//affect the originals during animation.
			Vector3f[] vertex =
				new Vector3f[vertices.length];
			for (int j = 0; j < numberVertices; j++) {
				vertex[j] = vertices[j];
			}

			ColorRGBA[] setColors = new ColorRGBA[vertices.length];
			for(int j = 0; j < numberVertices; j++) {
				setColors[j] = color;
			}

			//assign all the mesh information to be rendered as a TriMesh.
			mesh.setOriginalVertices(vertices);
			mesh.setVertices(vertex);
			mesh.setJointIndices(jointIndices);
			mesh.setTextures(tex);
			mesh.setNormals(norms);
			mesh.setColors(setColors);
			mesh.setIndices(ind);

			//set the model bound and attach it to this node.
			meshes[i] = mesh;
			meshes[i].setModelBound(new BoundingBox());
			meshes[i].updateModelBound();
			this.attachChild(meshes[i]);


		}

	}

	/**
	 * <code>parseMaterials</code> reads the material section of the
	 * model data and assigns the material state and texture state to
	 * the corresponding mesh.
	 */
	private void parseMaterials(BufferedReader reader, int numberMaterials)
		throws Exception {
		MaterialState[] materials = new MaterialState[numberMaterials];
		TextureState[] textures = new TextureState[numberMaterials];

		for (int i = 0; i < materials.length; i++) {
			//load material
			MaterialState ms =
				DisplaySystem
					.getDisplaySystem()
					.getRenderer()
					.getMaterialState();
			String line = getNextLine(reader);
			line = getNextLine(reader);
			String[] values = line.split(" ");
			ms.setAmbient(
				new ColorRGBA(
					Float.parseFloat(values[0]),
					Float.parseFloat(values[1]),
					Float.parseFloat(values[2]),
					Float.parseFloat(values[3])));
			line = getNextLine(reader);
			values = line.split(" ");
			ms.setDiffuse(
				new ColorRGBA(
					Float.parseFloat(values[0]),
					Float.parseFloat(values[1]),
					Float.parseFloat(values[2]),
					Float.parseFloat(values[3])));
			line = getNextLine(reader);
			values = line.split(" ");
			ms.setSpecular(
				new ColorRGBA(
					Float.parseFloat(values[0]),
					Float.parseFloat(values[1]),
					Float.parseFloat(values[2]),
					Float.parseFloat(values[3])));
			line = getNextLine(reader);
			values = line.split(" ");
			ms.setEmissive(
				new ColorRGBA(
					Float.parseFloat(values[0]),
					Float.parseFloat(values[1]),
					Float.parseFloat(values[2]),
					Float.parseFloat(values[3])));
			line = getNextLine(reader);
			ms.setShininess(Float.parseFloat(line));
			ms.setEnabled(true);

			//load texture
			line = getNextLine(reader);
			ms.setAlpha(Float.parseFloat(line));
			line = getNextLine(reader);
			String colorMap = line.substring(1, line.length() - 1);
			String texture = line.substring(1, line.length() - 1);
			line = getNextLine(reader);
			textures[i] = loadTexture(colorMap);
			materials[i] = ms;


			//if the mesh is assigned to this material, set it.
			for (int j = 0; j < meshes.length; j++) {
				if (meshes[j].getMaterialsIndex() == i) {
					if(materials[i] != null) {
						meshes[j].setRenderState(materials[i]);
					}
					if(textures[i] != null) {
						meshes[j].setRenderState(textures[i]);
					}
				}
			}
		}
	}

	/**
	 * <code>parseJoints</code> reads the model data and initialize
	 * the joint information. This includes setting the keyframes and
	 * position of the joint. The joint controller is then set up.
	 */
	private void parseJoints(BufferedReader reader) throws Exception {
		for (int i = 0; i < jointController.getNumberOfJoints(); i++) {
			String line = getNextLine(reader);
			DeformationJoint joint = new DeformationJoint();
			joint.name = line.substring(1, line.length() - 1);
			line = getNextLine(reader);

			joint.parentName = line.substring(1, line.length() - 1);
			line = getNextLine(reader);
			String[] values = line.split(" ");

			joint.pos.x = Float.parseFloat(values[1]);
			joint.pos.y = Float.parseFloat(values[2]);
			joint.pos.z = Float.parseFloat(values[3]);
			joint.rot.x = Float.parseFloat(values[4]);
			joint.rot.y = Float.parseFloat(values[5]);
			joint.rot.z = Float.parseFloat(values[6]);

			line = getNextLine(reader);
			joint.numberPosistionKeyframes = Integer.parseInt(line);
			Keyframe[] positionKeyframes =
				new Keyframe[joint.numberPosistionKeyframes];

			for (int j = 0; j < joint.numberPosistionKeyframes; j++) {
				line = getNextLine(reader);
				values = line.split(" ");
				positionKeyframes[j] =
					new Keyframe(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]),
						Float.parseFloat(values[3]));
			}
			joint.positionKeys = positionKeyframes;

			line = getNextLine(reader);
			joint.numberRotationKeyframes = Integer.parseInt(line);
			Keyframe[] rotationKeyframes =
				new Keyframe[joint.numberRotationKeyframes];
			for (int j = 0; j < joint.numberRotationKeyframes; j++) {
				line = getNextLine(reader);
				values = line.split(" ");
				rotationKeyframes[j] =
					new Keyframe(
						Float.parseFloat(values[0]),
						Float.parseFloat(values[1]),
						Float.parseFloat(values[2]),
						Float.parseFloat(values[3]));
			}
			joint.rotationKeys = rotationKeyframes;
			jointController.setJoint(i,joint);

			int parentIndex = -1;
			if (jointController.getJoint(i).parentName.length() > 0) {
				for (int j = 0; j < jointController.getNumberOfJoints(); j++) {
					if (jointController
						.getJoint(j)
						.name
						.equalsIgnoreCase(
							jointController.getJoint(i).parentName)) {
						parentIndex = j;
						break;
					}
				}
				if (parentIndex == -1) {
					throw new JmeException("Joints invalid.");
				}
			}
			jointController.getJoint(i).parentIndex = parentIndex;
		}
	}

	/**
	 * Returns the next line from the text file being parsed.  Removes
	 * comments and trims the line of whitespace.
	 */
	private String getNextLine(BufferedReader reader) throws Exception {
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("//") || "".equals(line)) {
				continue;
			}
			break;
		}
		return line;
	}

	/**
	 * Loads the assigned image texture as a texture state for the mesh.
	 * @param the relative filename to the bmp
	 * @return the image address in memory
	 */
	private TextureState loadTexture(String file) {
		URL fileURL = null;
		fileURL = MilkshapeASCIIModel.class.getClassLoader().getResource(textureDirectory + file);
		if(!textureDirectory.endsWith("/")) {
            textureDirectory += "/";
        }
        if(fileURL == null) {
			try {
                fileURL = new URL("file:"+textureDirectory + file);
        	} catch(MalformedURLException e) {
				LoggingSystem.getLogger().log(Level.WARNING, "Could not load: "
                        + textureDirectory + file);
				return null;
			}
		}

        Texture tex = TextureManager.loadTexture(
        		fileURL,
				Texture.MM_LINEAR,
				Texture.FM_LINEAR,
				true);

        if(tex != null) {

			TextureState ts =
				DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
			ts.setEnabled(true);
			ts.setTexture(tex);

			return ts;
        } else {
        	return null;
        }
	}



}
