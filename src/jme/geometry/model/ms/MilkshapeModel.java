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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;

import jme.exception.MonkeyGLException;
import jme.geometry.*;
import jme.geometry.bounding.BoundingBox;
import jme.geometry.bounding.BoundingSphere;
import jme.geometry.model.Joint;
import jme.geometry.model.Keyframe;
import jme.geometry.model.Material;
import jme.geometry.model.Mesh;
import jme.geometry.model.Triangle;
import jme.geometry.model.Vertex;
import jme.math.Matrix;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;
import jme.utility.Conversion;
import jme.utility.LoggingSystem;

/**
 * 
 * <code>MilkshapeModel</code> defines a model created by the Milkshape 3D 
 * modeling package. The loader handles all aspects of the Milkshape model up
 * to version 3. Animation is not currently supported, but planned.
 * 
 * @author Mark Powell
 */
public class MilkshapeModel implements Geometry {
    //defines the bounding volumes of the model.
    private BoundingSphere boundingSphere;
    private BoundingBox boundingBox;

    //animation attributes
    private float animationFPS;
    private float currentTime;
    private int totalFrames;
    
    //model data information
    private String modelFile;
    private ByteBuffer buffer;
    private String id;
    private String path;
    private int version;
    /**
     * the OpenGL context object.
     */
    private GL gl;
    /**
     * the color of the model. This color will be applied as a whole to the
     * model and may be trumped by the material level.
     */
    private float red, blue, green, alpha;
    /**
     * the scale of the model, where 1.0 is the standard size of the model.
     */
    private Vector3f scale;
    /**
     * the number of meshes that makes up the model.
     */
    private int numMeshes = 0;
    /**
     * the number of materials that makes up the model.
     */
    private int numMaterials = 0;
    /**
     * the number of triangles that makes up the model.
     */
    private int numTriangles = 0;
    /**
     * the number of vertices that makes up the model.
     */
    private int numVertices = 0;
    /**
     * the number of joints that make up the model.
     */
    private int numJoints = 0;
    /**
     * the total animation time.
     */
    private float totalTime = 0;
    /**
     * the array of meshes that build the model.
     */
    private Mesh meshes[] = null;
    /**
     * the array of materials that build the model.
     */
    private Material materials[] = null;
    /**
     * the array of triangles that build the model.
     */
    private Triangle triangles[] = null;
    /**
     * the array of vertices that build the model.
     */
    private Vertex vertices[] = null;
    /**
     * the array of Joints that build the model.
     */
    private Joint joints[] = null;

    /**
     * Constructor instantiates a new <code>MilkshapeModel</code> object. 
     * @param modelFile the Milkshape file.
     * @throws MonkeyGLException if the OpenGL context has not been created.
     */
    public MilkshapeModel(String modelFile) {
        gl = DisplaySystem.getDisplaySystem().getGL();
        if (null == gl) {
            throw new MonkeyGLException(
                "OpenGL context must be " + "created before MilkshapeModel.");
        }

        this.modelFile = modelFile;

        red = 1.0f;
        blue = 1.0f;
        green = 1.0f;
        alpha = 1.0f;
        scale = new Vector3f(1.0f, 1.0f, 1.0f);

        initialize();
        setBoundingVolumes();
    }

    /**
     * <code>initialize</code> reads the Milkshape model and sets the 
     * structure for rendering.
     */
    public void initialize() {
        //read the byte data
        byte data[] = null;
        File file = new File(modelFile);
        path =
            file.getAbsolutePath().substring(
                0,
                file.getAbsolutePath().length() - file.getName().length());
        int length = (int)file.length();
        data = new byte[length];
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            fis.read(data);
            fis.close();
        } catch (FileNotFoundException e) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Could not find model file " + modelFile);
            return;
        } catch (IOException e) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Could not read model file " + modelFile);
            return;
        }

        buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());

        //read header information
        //the ID is 10 bytes
        byte idBuffer[] = new byte[10];
        for (int i = 0; i < 10; i++) {
            idBuffer[i] = buffer.get();
        }
        id = Conversion.byte2String(idBuffer);
        version = buffer.getInt();

        if (!id.equals("MS3D000000")) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                modelFile + " is not a valid Milkshape3D model file.");
            return;
        }

        if (version < 3) {
            LoggingSystem.getLoggingSystem().getLogger().log(
                Level.WARNING,
                "Bad " + modelFile + " version.");
            return;
        }

        //Read the vertices
        numVertices = buffer.getShort();
        vertices = new Vertex[numVertices];
        for (int i = 0; i < numVertices; i++) {
            vertices[i] = new Vertex();
            vertices[i].flags = buffer.get();
            vertices[i].point[0] = buffer.getFloat();
            vertices[i].point[1] = buffer.getFloat();
            vertices[i].point[2] = buffer.getFloat();
            vertices[i].boneId = buffer.get();
            vertices[i].refCount = buffer.get();
        }

        //Read the Triangles
        numTriangles = buffer.getShort();
        triangles = new Triangle[numTriangles];
        for (int i = 0; i < numTriangles; i++) {
            triangles[i] = new Triangle();
            triangles[i].flags = buffer.getShort();
            for (int j = 0; j < 3; j++) {
                triangles[i].vertexIndices[j] = buffer.getShort();
            }

            for (int j = 0; j < 3; j++) {
                triangles[i].vertexNormals[j][0] = buffer.getFloat();
                triangles[i].vertexNormals[j][1] = buffer.getFloat();
                triangles[i].vertexNormals[j][2] = buffer.getFloat();
            }

            for (int j = 0; j < 3; j++) {
                triangles[i].s[j] = buffer.getFloat();
            }

            for (int j = 0; j < 3; j++) {
                triangles[i].t[j] = 1.0f - buffer.getFloat();
            }

            triangles[i].smoothingGroup = buffer.get();
            triangles[i].groupIndex = buffer.get();
        }

        //Read the meshes
        numMeshes = buffer.getShort();
        meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            meshes[i] = new Mesh();
            meshes[i].flags = buffer.get();
            //get the name 32 bytes
            byte nameBuffer[] = new byte[32];
            for (int j = 0; j < 32; j++) {
                nameBuffer[j] = buffer.get();
            }
            meshes[i].name = Conversion.byte2String(nameBuffer);
            meshes[i].numTriangles = buffer.getShort();
            meshes[i].triangleIndices = new int[meshes[i].numTriangles];
            for (int j = 0; j < meshes[i].numTriangles; j++) {
                meshes[i].triangleIndices[j] = buffer.getShort();
            }
            meshes[i].materialIndex = buffer.get();

        }

        //read materials
        numMaterials = buffer.getShort();
        materials = new Material[numMaterials];
        for (int i = 0; i < numMaterials; i++) {
            materials[i] = new Material();

            //read the material name 32 bytes
            byte nameBuffer[] = new byte[32];
            for (int j = 0; j < 32; j++) {
                nameBuffer[j] = buffer.get();
            }
            materials[i].name = Conversion.byte2String(nameBuffer);

            materials[i].ambient[0] = buffer.getFloat();
            materials[i].ambient[1] = buffer.getFloat();
            materials[i].ambient[2] = buffer.getFloat();
            materials[i].ambient[3] = buffer.getFloat();

            materials[i].diffuse[0] = buffer.getFloat();
            materials[i].diffuse[1] = buffer.getFloat();
            materials[i].diffuse[2] = buffer.getFloat();
            materials[i].diffuse[3] = buffer.getFloat();

            materials[i].specular[0] = buffer.getFloat();
            materials[i].specular[1] = buffer.getFloat();
            materials[i].specular[2] = buffer.getFloat();
            materials[i].specular[3] = buffer.getFloat();

            materials[i].emissive[0] = buffer.getFloat();
            materials[i].emissive[1] = buffer.getFloat();
            materials[i].emissive[2] = buffer.getFloat();
            materials[i].emissive[3] = buffer.getFloat();

            materials[i].shininess = buffer.getFloat();
            materials[i].transparency = buffer.getFloat();
            materials[i].mode = buffer.get();

            //get texture
            byte texBuffer[] = new byte[128];
            for (int j = 0; j < 128; j++) {
                texBuffer[j] = buffer.get();
            }
            materials[i].textureFilename = Conversion.byte2String(texBuffer);
            byte alphaBuffer[] = new byte[128];
            for (int j = 0; j < 128; j++) {
                alphaBuffer[j] = buffer.get();
            }
            materials[i].alphaFilename = Conversion.byte2String(texBuffer);
        }
        loadTextures();

        //Read key frames
        animationFPS = buffer.getFloat();
        currentTime = (buffer.getFloat() * 1000);
        totalFrames = buffer.getInt();

        numJoints = buffer.getShort();
        joints = new Joint[numJoints];

        for (int i = 0; i < numJoints; i++) {
            joints[i] = new Joint();
            joints[i].flags = buffer.get();
            //get name 32 bytes
            byte[] nameBuffer = new byte[32];
            for (int j = 0; j < 32; j++) {
                nameBuffer[j] = buffer.get();
            }

            joints[i].name = Conversion.byte2String(nameBuffer);
            //get parent name 32 bytes
            byte[] parentBuffer = new byte[32];
            for (int j = 0; j < 32; j++) {
                parentBuffer[j] = buffer.get();
            }
            joints[i].parentName = Conversion.byte2String(parentBuffer);

            int parentIndex = -1;
            if (joints[i].parentName.length() > 0) {
                for (int j = 0; j < numJoints; j++) {
                    if (joints[j]
                        .name
                        .equalsIgnoreCase(joints[i].parentName)) {
                        parentIndex = j;
                        break;
                    }
                }
                if (parentIndex == -1) {
                    LoggingSystem.getLoggingSystem().getLogger().log(
                        Level.WARNING,
                        "Milkshape does not have a parent joint.");
                    return;
                }
            }
            joints[i].parent = parentIndex;
            joints[i].rotation[0] = buffer.getFloat();
            joints[i].rotation[1] = buffer.getFloat();
            joints[i].rotation[2] = buffer.getFloat();

            joints[i].translation[0] = buffer.getFloat();
            joints[i].translation[1] = buffer.getFloat();
            joints[i].translation[2] = buffer.getFloat();

            joints[i].numRotationKeyframes = buffer.getShort();
            joints[i].rotationKeyframes =
                new Keyframe[joints[i].numRotationKeyframes];

            joints[i].numTranslationKeyframes = buffer.getShort();
            joints[i].translationKeyframes =
                new Keyframe[joints[i].numTranslationKeyframes];

            for (int j = 0; j < joints[i].numRotationKeyframes; j++) {
                joints[i].rotationKeyframes[j] = new Keyframe();
                joints[i].rotationKeyframes[j].time =
                    (buffer.getFloat() * 1000);
                joints[i].rotationKeyframes[j].parameter[0] = buffer.getFloat();
                joints[i].rotationKeyframes[j].parameter[1] = buffer.getFloat();
                joints[i].rotationKeyframes[j].parameter[2] = buffer.getFloat();
            }

            for (int j = 0; j < joints[i].numTranslationKeyframes; j++) {
                joints[i].translationKeyframes[j] = new Keyframe();
                joints[i].translationKeyframes[j].time =
                    (buffer.getFloat() * 1000);
                joints[i].translationKeyframes[j].parameter[0] =
                    buffer.getFloat();
                joints[i].translationKeyframes[j].parameter[1] =
                    buffer.getFloat();
                joints[i].translationKeyframes[j].parameter[2] =
                    buffer.getFloat();
            }
        }

        //setupJoints();
        //restart();
    }

    /**
     * <code>render</code> using the current mesh and material information to
     * display the model to the screen.
     */
    public void render() {
        Triangle currentTri;
        int triangleIndex;
        int index;
        ByteBuffer temp = ByteBuffer.allocateDirect(16);
        temp.order(ByteOrder.nativeOrder());
        gl.color4f(red, green, blue, alpha);
        gl.pushMatrix();
        gl.scalef(scale.x, scale.y, scale.z);

        //go through each mesh and render them.
        for (int i = 0; i < numMeshes; i++) {
            int materialIndex = meshes[i].materialIndex;
            //if the material is set, use it to set the texture and lighting.
            if (materialIndex >= 0) {

                gl.materialfv(
                    GL.FRONT,
                    GL.AMBIENT,
                    Sys.getDirectBufferAddress(
                        temp.asFloatBuffer().put(
                            materials[materialIndex].ambient)));
                gl.materialfv(
                    GL.FRONT,
                    GL.DIFFUSE,
                    Sys.getDirectBufferAddress(
                        temp.asFloatBuffer().put(
                            materials[materialIndex].diffuse)));
                gl.materialfv(
                    GL.FRONT,
                    GL.SPECULAR,
                    Sys.getDirectBufferAddress(
                        temp.asFloatBuffer().put(
                            materials[materialIndex].specular)));
                gl.materialfv(
                    GL.FRONT,
                    GL.EMISSION,
                    Sys.getDirectBufferAddress(
                        temp.asFloatBuffer().put(
                            materials[materialIndex].emissive)));
                gl.materialf(
                    GL.FRONT,
                    GL.SHININESS,
                    materials[materialIndex].shininess);

                if (materials[materialIndex].texture > 0) {
                    TextureManager.getTextureManager().bind(
                        materials[materialIndex].texture);
                    gl.enable(GL.TEXTURE_2D);
                } else
                    gl.disable(GL.TEXTURE_2D);
            } else {
                gl.disable(GL.TEXTURE_2D);
            }

            gl.begin(GL.TRIANGLES);
            int m = meshes[i].numTriangles;

            //render all triangles defined for the current mesh.
            for (int j = 0; j < m; j++) {
                triangleIndex = meshes[i].triangleIndices[j];
                currentTri = (Triangle) (triangles[triangleIndex]);

                for (int k = 0; k < 3; k++) {
                    index = currentTri.vertexIndices[k];

                    gl.normal3f(
                        currentTri.vertexNormals[k][0],
                        currentTri.vertexNormals[k][1],
                        currentTri.vertexNormals[k][2]);
                    gl.texCoord2f(currentTri.s[k], currentTri.t[k]);
                    gl.vertex3f(
                        vertices[index].point[0],
                        vertices[index].point[1],
                        vertices[index].point[2]);
                }
            }
            gl.end();
        }
        gl.popMatrix();
        gl.disable(GL.TEXTURE_2D);
    }

    /**
     * <code>setTexture</code> is not used for the model. Instead, the 
     * material set up is responsible for defining the texture.
     * @param filename not used.
     */
    public void setTexture(String filename) {
        //do nothing as textures are defined within the Milkshape MilkshapeModel.
    }

    /**
     * <code>setColor</code> will define the color of the model. This overall
     * color may be overridden by the defined material information of the
     * model.
     * @param red the red component of the color.
     * @param green the green component of the color.
     * @param blue the blue component of the color.
     * @param alpha the transparency of the color.
     */
    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * <code>setScale</code> sets the overall size of the model. The scale is
     * used to decrease or increase the size of the model, where 1.0 is the 
     * normal size of the model.
     * @param scale the multiplier of the model's size.
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    /**
     * <code>getBoundingSphere</code> returns the bounding sphere that 
     * contains the model.
     * @return the bounding sphere of the model.
     */
    public BoundingSphere getBoundingSphere() {
        return boundingSphere;
    }

    /**
     * <code>getBoundingBox</code> returns the bounding box that contains the
     * model.
     * @return the bounding box of the model.
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * <code>setBoundingVolumes</code> intializes the bounding sphere and box
     * of the model.
     *
     */
    private void setBoundingVolumes() {
        float distanceSqr = 0;
        float tempValue;
        for (int i = 0; i < numVertices; i++) {
            tempValue =
                vertices[i].point[0] * vertices[i].point[0]
                    + vertices[i].point[1] * vertices[i].point[1]
                    + vertices[i].point[2] * vertices[i].point[2];
            if (tempValue > distanceSqr) {
                distanceSqr = tempValue;
            }
        }
        distanceSqr *= scale.x;
        boundingSphere = new BoundingSphere((float)Math.sqrt(distanceSqr));
        boundingBox = new BoundingBox((float)Math.sqrt(distanceSqr));
    }

    /**
     * <code>setupJoints</code> initializes the joint information for
     * animation.
     */
    private void setupJoints() {
        for (int i = 0; i < numJoints; i++) {
            joints[i].relative = new Matrix();
            joints[i].absolute = new Matrix();
            joints[i].finalMatrix = new Matrix();

            joints[i].relative.setRotationRadians(joints[i].rotation);
            joints[i].relative.setTranslation(joints[i].translation);

            if (joints[i].parent != -1) {
                joints[i].absolute.set(
                    joints[joints[i].parent].absolute.getMatrix());
                joints[i].absolute.multiply(joints[i].relative);
            } else {
                joints[i].absolute.set(joints[i].relative.getMatrix());
            }
        }

        for (int i = 0; i < numVertices; i++) {
            if (vertices[i].boneId != -1) {
                joints[vertices[i].boneId].absolute.inverseTranslateVect(
                    vertices[i].point);
                joints[vertices[i].boneId].absolute.inverseRotateVect(
                    vertices[i].point);
            }
        }

        for (int i = 0; i < numTriangles; i++) {
            Triangle tri = triangles[i];
            for (int j = 0; j < 3; j++) {
                Vertex vert = vertices[tri.vertexIndices[j]];
                if (vert.boneId != -1) {
                    joints[vert.boneId].absolute.inverseRotateVect(
                        tri.vertexNormals[j]);
                }
            }
        }
    }

    /**
     * 
     * <code>restart</code> sets the animation to the initial key frame.
     *
     */
    private void restart() {
        for (int i = 0; i < numJoints; i++) {
            joints[i].currentRotationKeyframe =
                joints[i].currentTranslationKeyframe = 0;
            joints[i].finalMatrix.set(joints[i].absolute.getMatrix());
        }
    }

    /**
     * <code>loadTextures</code> makes the appropriate call to have
     * OpenGL load all needed textures for the given model.
     *
     */
    private void loadTextures() {
        for (int i = 0; i < numMaterials; i++) {
            if (materials[i].textureFilename.length() > 0) {
                String fullFilename = getPath(materials[i].textureFilename);
                materials[i].texture =
                    TextureManager.getTextureManager().loadTexture(
                        fullFilename,
                        GL.LINEAR_MIPMAP_LINEAR,
                        GL.LINEAR,
                        true);
            } else
                materials[i].texture = 0;
        }
    }

    /**
     * <code>getPath</code> returns the path of the file.
     * @param name the file name.
     * @return the full resolved file path.
     */
    private String getPath(String name) {
        if (name.indexOf(".\\") != -1) {
            name = name.substring(1);
        }
        return path + name;
    }

}
