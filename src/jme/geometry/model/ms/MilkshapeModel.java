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

package jme.geometry.model.ms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import jme.geometry.model.Joint;
import jme.geometry.model.Keyframe;
import jme.geometry.model.Material;
import jme.geometry.model.Mesh;
import jme.geometry.model.Model;
import jme.geometry.model.Triangle;
import jme.geometry.model.Vertex;
import jme.math.Matrix;
import jme.math.Quaternion;
import jme.math.Vector;
import jme.texture.TextureManager;

import org.lwjgl.opengl.GL;

/**
 * A MilkshapeModel represents a Milkshape 3D model.  Currently, it can only
 * load the exported ascii text version of an MD3D model.  In the future, the
 * binary version may be supported, but there really is no need for it because
 * it does not matter how long it takes to load the animation.  Loading should
 * take place at game startup.  If speed becomes extremely important in the
 * future, the binary loader may be an option, or serialized models is another
 * option.  Unless someone contributes a binary loader or add the nehe lwjgl
 * port of the binary loader that actually loads multiple texture correctly :)
 *
 * The model can also draw itself, fully textured, to OpenGL through lwjgl.
 *
 * Bone animation is also supported.
 *
 * EDIT: Mark Powell 9/5/03 - heavily updated class to make calls to exisiting
 * jme math (Matrix, Vector, etc). Altered update to take a time interval to 
 * allow for frame rate independant animation.
 *
 * SPECIAL THANKS:
 * Animation method was ported by naj from a MSVC++ Model Viewer tutorial
 * written by Mete Ciragan (creator of Milkshape).
 *
 * @author naj
 * @author Mark Powell
 * @version $Id: MilkshapeModel.java,v 1.10 2003-09-09 14:13:09 mojomonkey Exp $
 */
public class MilkshapeModel implements Model {

    /**
     * Debugging variable to toggle animations.
     */
    private boolean animated;

    /**
     * The total number of frames in the animation.
     */
    private int totalFrames;

    /**
     * The current frame of the animation.
     */
    private float currentFrame;

    /**
     * The number of meshes in the model.
     */
    private int numberMeshes;

    /**
     * The number of materials or textures in the model.
     */
    private int numberMaterials;

    /**
     * The number of joints or bones in the model.
     */
    private int numberJoints;

    /**
     * The meshes in the model.
     */
    private Mesh[] meshes;

    /**
     * The materials in the model.
     */
    private Material[] materials;

    /**
     * The joints in the model.
     */
    private Joint[] joints;
    
    private ArrayList points;

    /**
     * The absolute path to the directory containing the model file.  Used
     * to load the texture files.
     */
    private String absoluteFilePath;

    //Color attributes
    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private float a = 1.0f;
    
    private String filename;

    public MilkshapeModel(String filename) {
        this.filename = filename;
        this.animated = false;
        initialize();
    }

    public MilkshapeModel(String filename, boolean animated) {
        this.filename = filename;
        this.animated = animated;
        initialize();
    }

    /**
     * Draws the model to OpenGL via lwjgl.  Also advances the animation
     * frames along if there are animations for the model.
     */
    public void render() {

        boolean isTextureEnabled = GL.glIsEnabled(GL.GL_TEXTURE_2D);

        for (int meshIndex = 0; meshIndex < numberMeshes; meshIndex++) {
            int materialIndex = meshes[meshIndex].materialIndex;

            if (materialIndex >= 0) {
                ByteBuffer buffer =
                    ByteBuffer.allocateDirect(16).order(
                        ByteOrder.nativeOrder());

                GL.glMaterial(
                    GL.GL_FRONT,
                    GL.GL_AMBIENT,
                    buffer.asFloatBuffer().put(
                        materials[materialIndex].ambient));
                GL.glMaterial(
                    GL.GL_FRONT,
                    GL.GL_DIFFUSE,
                    buffer.asFloatBuffer().put(
                        materials[materialIndex].diffuse));
                GL.glMaterial(
                    GL.GL_FRONT,
                    GL.GL_SPECULAR,
                    buffer.asFloatBuffer().put(
                        materials[materialIndex].specular));
                GL.glMaterial(
                    GL.GL_FRONT,
                    GL.GL_EMISSION,
                    buffer.asFloatBuffer().put(
                        materials[materialIndex].emissive));
                GL.glMaterialf(
                    GL.GL_FRONT,
                    GL.GL_SHININESS,
                    materials[materialIndex].shininess);

                if (materials[materialIndex].glTextureAddress > 0) {
                    TextureManager.getTextureManager().bind(
                        materials[materialIndex].glTextureAddress);
                    GL.glEnable(GL.GL_TEXTURE_2D);
                } else {
                    GL.glDisable(GL.GL_TEXTURE_2D);
                }
            } else {
                GL.glDisable(GL.GL_TEXTURE_2D);
            }

            int triangleCount = meshes[meshIndex].numberTriangles;
            Vertex[] vertices = meshes[meshIndex].vertices;

            GL.glBegin(GL.GL_TRIANGLES);
            GL.glColor4f(r,g,b,a);
            for (int triangleIndex = 0;
                triangleIndex < triangleCount;
                triangleIndex++) {
                Triangle triangle =
                    (meshes[meshIndex].triangles)[triangleIndex];

                Vertex vertex = vertices[triangle.vertexIndex1];
                float[] normals =
                    (meshes[meshIndex].normals)[triangle.normalIndex1];
                if (!animated || vertex.boneIndex == -1) {
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(vertex.x, vertex.y, vertex.z);
                } else {
                    Vector animationVector =
                        new Vector(vertex.x, vertex.y, vertex.z).rotate(
                            joints[vertex.boneIndex].finalMatrix);
                    animationVector.x
                        += joints[vertex.boneIndex].finalMatrix.matrix[0][3];
                    animationVector.y
                        += joints[vertex.boneIndex].finalMatrix.matrix[1][3];
                    animationVector.z
                        += joints[vertex.boneIndex].finalMatrix.matrix[2][3];
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(
                        animationVector.x,
                        animationVector.y,
                        animationVector.z);
                }

                vertex = vertices[triangle.vertexIndex2];
                normals = (meshes[meshIndex].normals)[triangle.normalIndex2];
                if (!animated || vertex.boneIndex == -1) {
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(vertex.x, vertex.y, vertex.z);
                } else {
                    Vector animationVector =
                        new Vector(vertex.x, vertex.y, vertex.z).rotate(
                            joints[vertex.boneIndex].finalMatrix);
                    animationVector.x
                        += joints[vertex.boneIndex].finalMatrix.matrix[0][3];
                    animationVector.y
                        += joints[vertex.boneIndex].finalMatrix.matrix[1][3];
                    animationVector.z
                        += joints[vertex.boneIndex].finalMatrix.matrix[2][3];
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(
                        animationVector.x,
                        animationVector.y,
                        animationVector.z);
                }

                vertex = vertices[triangle.vertexIndex3];
                normals = (meshes[meshIndex].normals)[triangle.normalIndex3];
                if (!animated || vertex.boneIndex == -1) {
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(vertex.x, vertex.y, vertex.z);
                } else {
                    Vector animationVector =
                        new Vector(vertex.x, vertex.y, vertex.z).rotate(
                            joints[vertex.boneIndex].finalMatrix);
                    animationVector.x
                        += joints[vertex.boneIndex].finalMatrix.matrix[0][3];
                    animationVector.y
                        += joints[vertex.boneIndex].finalMatrix.matrix[1][3];
                    animationVector.z
                        += joints[vertex.boneIndex].finalMatrix.matrix[2][3];
                    GL.glNormal3f(normals[0], normals[1], normals[2]);
                    GL.glTexCoord2f(vertex.u, vertex.v);
                    GL.glVertex3f(
                        animationVector.x,
                        animationVector.y,
                        animationVector.z);
                }
            }
            GL.glEnd();
        }

        if (isTextureEnabled) {
            GL.glEnable(GL.GL_TEXTURE_2D);
        } else {
            GL.glDisable(GL.GL_TEXTURE_2D);
        }

    }

    /**
     * Set the final matrix of all of the joints to be part way between the
     * previous keyframe and the next keyframe, depending on how much time
     * has passed since the last keyframe.
     */
    public void update(float time) {
        if (!animated) {
            return;
        }
        currentFrame += time;
        if (currentFrame > totalFrames) {
            currentFrame = 0.0f;
        }

        for (int meshIndex = 0; meshIndex < numberJoints; meshIndex++) {
            Joint joint = joints[meshIndex];
            int positionKeyframeCount = joint.numberPosistionKeyframes;
            int rotationKeyframeCount = joint.numberRotationKeyframes;
            if (positionKeyframeCount == 0 && rotationKeyframeCount == 0) {
                joints[meshIndex].finalMatrix.copy(
                    joints[meshIndex].absoluteMatrix);
            } else {
                Vector positionVector = new Vector();
                Quaternion rotationVector = new Quaternion();
                Keyframe lastPositionKeyframe = null;
                Keyframe currentPositionKeyframe = null;
                for (int keyframeIndex = 0;
                    keyframeIndex < positionKeyframeCount;
                    keyframeIndex++) {
                    Keyframe positionKeyframe =
                        joint.positionKeys[keyframeIndex];
                    if (positionKeyframe.time >= currentFrame) {
                        currentPositionKeyframe = positionKeyframe;
                        break;
                    }
                    lastPositionKeyframe = positionKeyframe;
                }
                if (lastPositionKeyframe != null
                    && currentPositionKeyframe != null) {
                    float d =
                        currentPositionKeyframe.time
                            - lastPositionKeyframe.time;
                    float s = (currentFrame - lastPositionKeyframe.time) / d;
                    positionVector.x =
                        lastPositionKeyframe.x
                            + (currentPositionKeyframe.x
                                - lastPositionKeyframe.x)
                                * s;
                    positionVector.y =
                        lastPositionKeyframe.y
                            + (currentPositionKeyframe.y
                                - lastPositionKeyframe.y)
                                * s;
                    positionVector.z =
                        lastPositionKeyframe.z
                            + (currentPositionKeyframe.z
                                - lastPositionKeyframe.z)
                                * s;
                } else if (lastPositionKeyframe == null) {
                    currentPositionKeyframe.x = positionVector.x;
                    currentPositionKeyframe.y = positionVector.y;
                    currentPositionKeyframe.z = positionVector.z;
                } else if (currentPositionKeyframe == null) {
                    lastPositionKeyframe.x = positionVector.x;
                    lastPositionKeyframe.y = positionVector.y;
                    lastPositionKeyframe.z = positionVector.z;
                }
                Matrix slerpedMatrix = new Matrix();
                Keyframe lastRotationKeyframe = null;
                Keyframe currentRotationKeyframe = null;
                for (int keyframeIndex = 0;
                    keyframeIndex < rotationKeyframeCount;
                    keyframeIndex++) {
                    Keyframe rotationKeyframe =
                        joint.rotationKeys[keyframeIndex];
                    if (rotationKeyframe.time >= currentFrame) {
                        currentRotationKeyframe = rotationKeyframe;
                        break;
                    }
                    lastRotationKeyframe = rotationKeyframe;
                }
                if (lastRotationKeyframe != null
                    && currentRotationKeyframe != null) {
                    float d =
                        currentRotationKeyframe.time
                            - lastRotationKeyframe.time;
                    float s = (currentFrame - lastRotationKeyframe.time) / d;
                    Quaternion slerpedQuaternion = new Quaternion();
                    Quaternion lastRotationQuaternion = new Quaternion();
                    Quaternion currentRotationQuaternion = new Quaternion();
                    lastRotationQuaternion.fromAngles(
                        new float[] {
                            lastRotationKeyframe.x,
                            lastRotationKeyframe.y,
                            lastRotationKeyframe.z });
                    currentRotationQuaternion.fromAngles(
                        new float[] {
                            currentRotationKeyframe.x,
                            currentRotationKeyframe.y,
                            currentRotationKeyframe.z });
                    slerpedQuaternion =
                        slerpedQuaternion.slerp(
                            lastRotationQuaternion,
                            currentRotationQuaternion,
                            s);
                    slerpedMatrix.set(slerpedQuaternion);
                } else if (lastRotationKeyframe == null) {
                    rotationVector.x =
                        currentRotationKeyframe.x * 180 / (float) Math.PI;
                    rotationVector.y =
                        currentRotationKeyframe.y * 180 / (float) Math.PI;
                    rotationVector.z =
                        currentRotationKeyframe.z * 180 / (float) Math.PI;
                    slerpedMatrix.angleRotationDegrees(
                        new Vector(
                            rotationVector.x,
                            rotationVector.y,
                            rotationVector.z));
                } else if (currentRotationKeyframe == null) {
                    rotationVector.x =
                        lastRotationKeyframe.x * 180 / (float) Math.PI;
                    rotationVector.y =
                        lastRotationKeyframe.y * 180 / (float) Math.PI;
                    rotationVector.z =
                        lastRotationKeyframe.z * 180 / (float) Math.PI;
                    slerpedMatrix.angleRotationDegrees(
                        new Vector(
                            rotationVector.x,
                            rotationVector.y,
                            rotationVector.z));
                }
                slerpedMatrix.matrix[0][3] = positionVector.x;
                slerpedMatrix.matrix[1][3] = positionVector.y;
                slerpedMatrix.matrix[2][3] = positionVector.z;
                joints[meshIndex].relativeFinalMatrix =
                    joints[meshIndex].relativeMatrix.multiply(slerpedMatrix);
                if (joint.parentIndex == -1) {
                    joints[meshIndex].finalMatrix.copy(
                        joints[meshIndex].relativeFinalMatrix);
                } else {
                    joints[meshIndex].finalMatrix =
                        joints[joint.parentIndex].finalMatrix.multiply(
                            joints[meshIndex].relativeFinalMatrix);
                }
            }
        }

    }

    /**
     * Loads an ascii text model exported from MS3D.
     * @param filename the file to load.
     */
    public void initialize() {
        points = new ArrayList();
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
                    meshes = new Mesh[numberMeshes];
                    parseMeshes(reader);
                }
                if (line.startsWith("Materials: ")) {
                    numberMaterials = Integer.parseInt(line.substring(11));
                    materials = new Material[numberMaterials];
                    parseMaterials(reader);
                }
                if (line.startsWith("Bones: ")) {
                    numberJoints = Integer.parseInt(line.substring(7));
                    joints = new Joint[numberJoints];
                    parseJoints(reader);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        reloadTextures();
    }
    
    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    
    public void setTexture(String texture) {
        //not used.
    }
    
    public Vector[] getPoints() {
        Vector v[] = new Vector[points.size()];
        return (Vector[])points.toArray(v);
    }
    
    /**
     * Simple parser to extract the mesh information from the text file.
     */
    private void parseMeshes(BufferedReader reader) throws Exception {
        for (int i = 0; i < numberMeshes; i++) {
            Mesh mesh = new Mesh();
            String line = getNextLine(reader);
            mesh.name = line.substring(1, line.lastIndexOf("\""));
            mesh.flags =
                Integer.parseInt(
                    line.substring(
                        line.lastIndexOf("\"") + 2,
                        line.lastIndexOf(" ")));
            mesh.materialIndex =
                Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));

            line = getNextLine(reader);
            mesh.numberVertices = Integer.parseInt(line);
            Vertex[] vertices = new Vertex[mesh.numberVertices];
            for (int j = 0; j < mesh.numberVertices; j++) {
                line = getNextLine(reader);
                String[] values = line.split(" ");
                vertices[j] =
                    new Vertex(
                        Integer.parseInt(values[0]),
                        Float.parseFloat(values[1]),
                        Float.parseFloat(values[2]),
                        Float.parseFloat(values[3]),
                        Float.parseFloat(values[4]),
                        Float.parseFloat(values[5]),
                        Integer.parseInt(values[6]));
                points.add(new Vector(vertices[j].x, vertices[j].y, vertices[j].z));
            }
            mesh.vertices = vertices;

            line = getNextLine(reader);
            mesh.numberNormals = Integer.parseInt(line);
            float[][] normals = new float[mesh.numberNormals][3];
            for (int j = 0; j < mesh.numberNormals; j++) {
                line = getNextLine(reader);
                String[] values = line.split(" ");
                normals[j] =
                    new float[] {
                        Float.parseFloat(values[0]),
                        Float.parseFloat(values[1]),
                        Float.parseFloat(values[2])};
            }
            mesh.normals = normals;

            line = getNextLine(reader);
            mesh.numberTriangles = Integer.parseInt(line);
            Triangle[] triangles = new Triangle[mesh.numberTriangles];
            for (int j = 0; j < mesh.numberTriangles; j++) {
                line = getNextLine(reader);
                String[] values = line.split(" ");
                triangles[j] =
                    new Triangle(
                        Integer.parseInt(values[0]),
                        Integer.parseInt(values[1]),
                        Integer.parseInt(values[2]),
                        Integer.parseInt(values[3]),
                        Integer.parseInt(values[4]),
                        Integer.parseInt(values[5]),
                        Integer.parseInt(values[6]),
                        Integer.parseInt(values[7]));
            }
            mesh.triangles = triangles;
            meshes[i] = mesh;
        }
    }

    /**
     * Simple parser to extract the material information from the text file.
     */
    private void parseMaterials(BufferedReader reader) throws Exception {
        for (int i = 0; i < numberMaterials; i++) {
            String line = getNextLine(reader);
            Material material = new Material();
            material.name = line.substring(1, line.length() - 1);
            line = getNextLine(reader);
            String[] values = line.split(" ");
            material.ambient =
                new float[] {
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2]),
                    Float.parseFloat(values[3])};
            line = getNextLine(reader);
            values = line.split(" ");
            material.diffuse =
                new float[] {
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2]),
                    Float.parseFloat(values[3])};
            line = getNextLine(reader);
            values = line.split(" ");
            material.specular =
                new float[] {
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2]),
                    Float.parseFloat(values[3])};
            line = getNextLine(reader);
            values = line.split(" ");
            material.emissive =
                new float[] {
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2]),
                    Float.parseFloat(values[3])};
            line = getNextLine(reader);
            material.shininess = Float.parseFloat(line);
            line = getNextLine(reader);
            material.transparency = Float.parseFloat(line);
            line = getNextLine(reader);
            material.colorMap = line.substring(1, line.length() - 1);
            line = getNextLine(reader);
            material.alphaMap = line.substring(1, line.length() - 1);
            materials[i] = material;
        }
    }

    /**
     * Simple parser to extract the joint information from the text file.
     */
    private void parseJoints(BufferedReader reader) throws Exception {
        for (int i = 0; i < numberJoints; i++) {
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
                    System.out.println("CRAP!");
                    System.exit(1);
                }
            }
            joints[i].parentIndex = parentIndex;
        }
        if (animated) {
            setupJointAnimations();
        }
    }

    /**
     * Calculate the initial absolute and relative matrices for the joints.
     */
    private void setupJointAnimations() {
        for (int jointIndex = 0; jointIndex < numberJoints; jointIndex++) {
            Joint joint = joints[jointIndex];
            Vector rotationVector = new Vector();
            rotationVector.x = joint.rotx * 180 / (float) Math.PI;
            rotationVector.y = joint.roty * 180 / (float) Math.PI;
            rotationVector.z = joint.rotz * 180 / (float) Math.PI;
            joints[jointIndex].relativeMatrix.angleRotationDegrees(
                rotationVector);
            joints[jointIndex].relativeMatrix.matrix[0][3] = joint.posx;
            joints[jointIndex].relativeMatrix.matrix[1][3] = joint.posy;
            joints[jointIndex].relativeMatrix.matrix[2][3] = joint.posz;
            if (joint.parentIndex != -1) {
                joints[jointIndex].absoluteMatrix =
                    joints[joint.parentIndex].absoluteMatrix.multiply(
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

        for (int meshIndex = 0; meshIndex < numberMeshes; meshIndex++) {
            Mesh pMesh = meshes[meshIndex];
            for (int j = 0; j < pMesh.numberVertices; j++) {
                Vertex vertex = pMesh.vertices[j];
                if (vertex.boneIndex != -1) {
                    vertex.x
                        -= joints[vertex.boneIndex].absoluteMatrix.matrix[0][3];
                    vertex.y
                        -= joints[vertex.boneIndex].absoluteMatrix.matrix[1][3];
                    vertex.z
                        -= joints[vertex.boneIndex].absoluteMatrix.matrix[2][3];
                    Vector inverseRotationVector = new Vector();
                    inverseRotationVector =
                        new Vector(vertex.x, vertex.y, vertex.z).inverseRotate(
                            joints[vertex.boneIndex].absoluteMatrix);
                    vertex.x = inverseRotationVector.x;
                    vertex.y = inverseRotationVector.y;
                    vertex.z = inverseRotationVector.z;
                }
            }
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
     * Reloads the textures.
     */
    private final void reloadTextures() {
        for (int i = 0; i < numberMaterials; i++) {
            if (materials[i].name.length() > 0) {
                try {
                    materials[i].glTextureAddress =
                        loadTexture(absoluteFilePath + materials[i].colorMap);
                } catch (Exception e) {
                    materials[i].glTextureAddress = 0;
                }
            } else {
                materials[i].glTextureAddress = 0;
            }
        }
    }

    /**
     * Loads a bitmap (*.bmp image file) texture in opengl memory
     * @param the relative filename to the bmp
     * @return the image address in memory
     */
    private final int loadTexture(String file) throws Exception {
        System.out.println(file);
        return TextureManager.getTextureManager().loadTexture(
            file,
            GL.GL_LINEAR_MIPMAP_LINEAR,
            GL.GL_LINEAR,
            true,
            false);

    }

    /**
     * Determine is the model is going to run animations, if it has them.
     * @return the animation mode.
     */
    public boolean isAnimated() {
        return animated;
    }

    /**
     * Set the new animation mode.
     * @param animated the new animation mode.
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
}