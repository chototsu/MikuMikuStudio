/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * <code>MilkshapeASCIIModel</code> generates a <code>Node</code> containing
 * a model generated from a Milkshape ASCII file. The node contains 
 * children making up the scene. For example, if a model consists of 
 * two meshes (head and body) the body <code>TriMesh</code> will be 
 * created, attached to the main node, a second node for the head is 
 * created, attached to the main node, the head <code>TriMesh</code> is
 * then attached to this second node.
 * @author Mark Powell
 * @version $Id: MilkshapeASCIIModel.java,v 1.2 2004-01-23 03:32:09 mojomonkey Exp $
 */
public class MilkshapeASCIIModel {
    //contains data structures for the resulting model scene.
    private static TriMesh[] meshArray;
    private static Node modelNode;

    //keeps a reference to the path to the model and textures.
    private static String absoluteFilePath;

    //keeps the number of mesh groups, materials and joints
    // that will be created.
    private static int numberMeshes;
    private static int numberMaterials;
    private static int numberJoints;

    //keep joint information. Will be changed when jME animation is
    //implemented.
    private static Joint[] joints;
    private static int totalFrames;
    private static int currentFrame;

    /**
     * <code>load</code> parses a Milkshape 3D ASCII file and generates
     * a scenegraph node based on the mesh information. If the ASCII
     * file is not valid or there are problems reading the file a null
     * object is returned. 
     * 
     * @param filename the file name of the model to be loaded.
     * @return the scenegraph node.
     */
    public static Node load(String filename) {
        if(!filename.endsWith(".txt")) {
            return null;
        }
        try {
            File file = new File(filename);
            absoluteFilePath = file.getAbsolutePath();
            absoluteFilePath =
                absoluteFilePath.substring(
                    0,
                    absoluteFilePath.lastIndexOf(File.separator) + 1);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = getNextLine(reader)) != null) {
                if (line.startsWith("Frames: ")) {
                    totalFrames = Integer.parseInt(line.substring(8));
                }
                if (line.startsWith("Frame: ")) {
                    currentFrame = Integer.parseInt(line.substring(7));
                }
                if (line.startsWith("Meshes: ")) {
                    numberMeshes = Integer.parseInt(line.substring(8));
                    parseMeshes(reader);
                }
                if (line.startsWith("Materials: ")) {
                    numberMaterials = Integer.parseInt(line.substring(11));
                    parseMaterials(reader);
                }
                if (line.startsWith("Bones: ")) {
                    numberJoints = Integer.parseInt(line.substring(7));
                    joints = new Joint[numberJoints];
                    parseJoints(reader);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Node oldNode = null;
        modelNode = new Node();
        for (int i = 0; i < meshArray.length; i++) {
            meshArray[i].setModelBound(new BoundingSphere());
            meshArray[i].updateModelBound();

            if (i == 0) {
                modelNode.attachChild(meshArray[0]);
                oldNode = modelNode;
            } else {
                Node newNode = new Node();
                newNode.attachChild(meshArray[1]);
                oldNode.attachChild(newNode);
                oldNode = newNode;
            }
        }

        return modelNode;

    }
    /**
         * Simple parser to extract the mesh information from the text file.
         */
    private static void parseMeshes(BufferedReader reader) {
        meshArray = new TriMesh[numberMeshes];
        for (int i = 0; i < numberMeshes; i++) {
            String line;
            try {
                line = getNextLine(reader);

                line = getNextLine(reader);
                int numberVertices = Integer.parseInt(line);
                Vector3f[] vectors = new Vector3f[numberVertices];
                Vector2f[] textures = new Vector2f[numberVertices];
                for (int j = 0; j < numberVertices; j++) {
                    line = getNextLine(reader);
                    String[] values = line.split(" ");
                    vectors[j] =
                        new Vector3f(
                            Float.parseFloat(values[1]),
                            Float.parseFloat(values[2]),
                            Float.parseFloat(values[3]));
                    textures[j] =
                        new Vector2f(
                            Float.parseFloat(values[4]),
                            1 - Float.parseFloat(values[5]));
                }

                line = getNextLine(reader);
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

                meshArray[i] = new TriMesh();
                meshArray[i].setVertices(vectors);
                meshArray[i].setTextures(textures);
                meshArray[i].setNormals(norms);
                meshArray[i].setIndices(ind);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Simple parser to extract the material information from the text file.
     */
    private static void parseMaterials(BufferedReader reader) {
        for (int i = 0; i < numberMaterials; i++) {
            try {
                String line = getNextLine(reader);
                line = getNextLine(reader);
                String[] values = line.split(" ");
                MaterialState ms =
                    DisplaySystem
                        .getDisplaySystem()
                        .getRenderer()
                        .getMaterialState();
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
                line = getNextLine(reader);
                ms.setAlpha(Float.parseFloat(line));
                line = getNextLine(reader);
                meshArray[i].setRenderState(
                    loadTexture(
                        absoluteFilePath
                            + line.substring(1, line.length() - 1)));
                line = getNextLine(reader);

                meshArray[i].setRenderState(ms);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Simple parser to extract the joint information from the text file.
     */
    private static void parseJoints(BufferedReader reader) {
        for (int i = 0; i < numberJoints; i++) {
            try {
                String line = getNextLine(reader);
                Joint joint = new Joint();
                joint.name = line.substring(1, line.length() - 1);
                line = getNextLine(reader);
                joint.parentName = line.substring(1, line.length() - 1);
                line = getNextLine(reader);
                String[] values = line.split(" ");
                joint.flags = Integer.parseInt(values[0]);
                joint.posx = Float.parseFloat(values[1]);
                joint.posy = Float.parseFloat(values[2]);
                joint.posz = Float.parseFloat(values[3]);
                joint.rotx = Float.parseFloat(values[4]);
                joint.roty = Float.parseFloat(values[5]);
                joint.rotz = Float.parseFloat(values[6]);
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
                joints[i] = joint;

                int parentIndex = -1;
                if (joints[i].parentName.length() > 0) {
                    for (int j = 0; j < numberJoints; j++) {
                        if (joints[j]
                            .name
                            .equalsIgnoreCase(joints[i].parentName)) {
                            parentIndex = j;
                            break;
                        }
                    }
                    if (parentIndex == -1) {
                        return;
                    }
                }
                joints[i].parentIndex = parentIndex;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    /**
     * Calculate the initial absolute and relative matrices for the joints.
     */
    private static void setupJointAnimations() {
        for (int jointIndex = 0; jointIndex < numberJoints; jointIndex++) {
            Joint joint = joints[jointIndex];
            Vector3f rotationVector = new Vector3f();
            rotationVector.x = joint.rotx * 180 / (float) Math.PI;
            rotationVector.y = joint.roty * 180 / (float) Math.PI;
            rotationVector.z = joint.rotz * 180 / (float) Math.PI;
            joints[jointIndex].relativeMatrix.fromAngles(rotationVector);
            joints[jointIndex].relativeMatrix.set(0, 3, joint.posx);
            joints[jointIndex].relativeMatrix.set(1, 3, joint.posy);
            joints[jointIndex].relativeMatrix.set(2, 3, joint.posz);
            if (joint.parentIndex != -1) {
                joints[jointIndex].absoluteMatrix =
                    joints[joint.parentIndex].absoluteMatrix.mult(
                        joints[jointIndex].relativeMatrix);
                joints[jointIndex].finalMatrix.copy(
                    joints[jointIndex].absoluteMatrix);
            } else {
                joints[jointIndex].absoluteMatrix.copy(
                    joints[jointIndex].relativeMatrix);
                joints[jointIndex].finalMatrix.copy(
                    joints[jointIndex].relativeMatrix);
            }
        }
    }

    /**
     * Returns the next line from the text file being parsed.  Removes
     * comments and trims the line of whitespace.
     */
    private static String getNextLine(BufferedReader reader)
        throws IOException {
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
     * Loads a bitmap (*.bmp image file) texture in opengl memory
     * @param the relative filename to the bmp
     * @return the image address in memory
     */
    private static TextureState loadTexture(String file) {
        System.out.println(file);
        TextureState texture =
            DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        texture.setTexture(
            TextureManager.loadTexture(
                file,
                Texture.MM_LINEAR,
                Texture.FM_LINEAR,
                true));
        texture.setEnabled(true);
        return texture;

    }
}
