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
package jme.geometry.model.md3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import jme.math.Quaternion;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;
import jme.utility.Conversion;
import jme.utility.LoggingSystem;
import jme.utility.StringUtils;
import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.geometry.Geometry;
import jme.geometry.bounding.BoundingBox;
import jme.geometry.bounding.BoundingSphere;
import jme.geometry.model.Triangle;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;

/**
 * <code>Md3Model</code> handles loading and rendering a Quake 3 MD3 format
 * model. Animation is fully supported, where <code>setTorsoAnimation</code>
 * and <code>setLegsAnimation</code> determine the animation used for the
 * two major parts of the model. These animations are defined as:
 * <br><br>
 * BOTH_DEATH1<br>
 * BOTH_DEAD1<br>
 * BOTH_DEATH2<br>
 * BOTH_DEAD2<br>
 * BOTH_DEATH3<br>
 * BOTH_DEAD3<br>
 * <br>
 * TORSO_GESTURE<br>
 * TORSO_ATTACK<br>
 * TORSO_ATTACK2<br>
 * TORSO_DROP<br>
 * TORSO_RAISE<br>
 * TORSO_STAND<br>
 * TORSO_STAND2<br>
 * <br>
 * LEGS_WALKCR<br>
 * LEGS_WALK<br>
 * LEGS_RUN<br>
 * LEGS_BACK<br>
 * LEGS_SWIM<br>
 * LEGS_JUMP<br>
 * LEGS_LAND<br>
 * LEGS_JUMPB<br>
 * LEGS_LANDB<br>
 * LEGS_IDLECR<br>
 * LEGS_IDLE<br>
 * LEGS_TURN<br>
 * <br>
 * 
 * 
 * @author Mark Powell
 */
public class Md3Model implements Geometry {
    /**
     * LOWER defines the legs section of the model.
     */
    public static final int LOWER = 0;
    /**
     * UPPER defines the torso section of the model.
     */
    public static final int UPPER = 1;
    /**
     * HEAD defines the head section of the model.
     */
    public static final int HEAD = 2;
    /**
     * WEAPON defines the weapon of the model.
     */
    public static final int WEAPON = 3;

    //buffer for model data and structures to hold it once loaded.
    private ByteBuffer buffer;
    private byte[] fileContents;
    private Md3Header header;
    private Md3Skin[] skins;
    private Md3TexCoord[] texCoords;
    //private Md3Face[] triangles;
    private Triangle[] triangles;
    private Md3Triangle[] vertices;
    private Md3Bone[] bones;

    //model names
    private String path;
    private String model;
    private String weapon;

    //model structure
    private Model3D head;
    private Model3D upper;
    private Model3D lower;
    private Model3D weaponModel;
    
    FloatBuffer buf;

    //model rendering attributes.
    private Vector3f scale;
    private float r;
    private float g;
    private float b;
    private float a;

    //bounding information.
    private BoundingSphere boundingSphere;
    private BoundingBox boundingBox;

    //OpenGL context.
    private GL gl;

    //Model constants
    private static final int START_TORSO_ANIMATION = 6;
    private static final int START_LEGS_ANIMATION = 13;
    private static final int MAX_ANIMATIONS = 25;
    private final static int MAX_TEXTURES = 100;

    /**
     * Constructor instantiates a new <code>Md3Model</code> object. During
     * creation, the model is loaded and initialized.
     * @param path the path to the base level of the model directory.
     * @param model the model file.
     * @param weapon the current weapon file. May be null.
     * @throws MonkeyGLException if the OpenGL context has not been created.
     * @throws MonkeyRuntimeException if path or model is null.
     */
    public Md3Model(String path, String model, String weapon) {
        if (null == path || null == model) {
            throw new MonkeyRuntimeException("Path and model cannot be null.");
        }
        gl = DisplaySystem.getDisplaySystem().getGL();
        if (null == gl) {
            throw new MonkeyGLException("OpenGL context must be created first.");
        }
        
        buf =
        ByteBuffer
            .allocateDirect(64)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

        scale = new Vector3f(1.0f, 1.0f, 1.0f);
        r = 1.0f;
        g = 1.0f;
        b = 1.0f;
        a = 1.0f;

        this.path = path;
        this.model = model;
        this.weapon = weapon;

        head = new Model3D();
        upper = new Model3D();
        lower = new Model3D();
        weaponModel = new Model3D();

        initialize();
        LoggingSystem.getLoggingSystem().getLogger().log(Level.INFO, 
                "Successfully loaded MD3 model " + model);
    }

    /**
     * <code>setModelString</code> sets the new model to be used. This should
     * be followed by a call to <code>loadModel</code> to complete the change
     * of the model data.
     * @param model the new model to use.
     */
    public void setModelString(String model) {
        this.model = model;
    }

    /**
     * <code>setWeaponString</code> sets the new model to be used for the weapon.
     * This should be followed by a call to <code>loadWeapon</code> to complete
     * te change of the model data.
     * @param weapon the new weapon model to use.
     */
    public void setWeaponString(String weapon) {
        this.weapon = weapon;
    }

    /**
     * <code>setPath</code> sets the path to the model.
     * @param path the new path to the models.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * <code>getModel</code> returns a <code>Model3D</code> object of the
     * specified model part. Either LOWER, UPPER, HEAD or WEAPON.
     * @param part the part requested.
     * @return the model information for the model part.
     */
    public Model3D getModel(int part) {
        if (part == LOWER) {
            return lower;
        } else if (part == UPPER) {
            return upper;
        } else if (part == HEAD) {
            return head;
        } else {
            return weaponModel;
        }
    }

    /**
     * <code>initialize</code> loads the model and weapon then sets up the
     * bounding volumes for the model.
     */
    public void initialize() {
        loadModel();
        loadWeapon();
        boundingSphere = new BoundingSphere(10);
        boundingBox = new BoundingBox(10);
    }
    
    /**
     * <code>render</code> handles rendering the model to the screen. This 
     * occurs recursively starting with the legs, then torso and lastly
     * the head. Due to the make up of the MD3 vertices, culling is turned to
     * front.
     */
    public void render() {
        DisplaySystem.getDisplaySystem().cullMode(GL.FRONT, true);
        
        //MD3 has Z up, so remedy this.
		gl.rotatef(-90,0,1,0);
        gl.rotatef(-90, 1, 0, 0);
        
        //scale by a desired factor
        gl.scalef(scale.x, scale.y, scale.z);
        //set the desired color
        gl.color4f(r, g, b, a);

        //Update the leg and torso animations
        updateModel(lower);
        updateModel(upper);

        //start rendering with the legs first.
        drawLink(lower);
        //set culling back to GL.BACK
        DisplaySystem.getDisplaySystem().cullMode(GL.BACK, true);
    }

    /**
     * <code>setTorsoAnimation</code> sets the current animation of the 
     * upper model. The string that denotes the animation is defined and
     * denoted at the top of this javadoc.
     * @param animationName the animation to set for the torso.
     */
    public void setTorsoAnimation(String animationName) {
        for (int i = 0; i < upper.numOfAnimations; i++) {
            if (((AnimationInfo)upper.animations.get(i))
                .name
                .equals(animationName)) {
                
                upper.currentAnim = i;
                upper.currentFrame = ((AnimationInfo)upper.animations.get(
                            upper.currentAnim)).startFrame;
                return;
            }
        }
    }

    /**
     * <code>setLegsAnimation</code> sets the current animation of the 
     * lower model. The string that denotes the animation is defined and
     * denoted at the top of this javadoc.
     * @param animationName the animation to set for the legs.
     */
    public void setLegsAnimation(String animationName) {
        for (int i = 0; i < lower.numOfAnimations; i++) {
            if (((AnimationInfo)lower.animations.get(i))
                .name
                .equals(animationName)) {
            
                lower.currentAnim = i;
                lower.currentFrame = ((AnimationInfo)lower.animations.get(
                            lower.currentAnim)).startFrame;
                return;
            }
        }

    }

    /**
     * <code>setTexture</code> is not used as the MD3 Model's texture is
     * defined in the skin data of the model. It is here to provide 
     * continuity with the <code>Geometry</code> interface.
     * @param texture not used.
     */
    public void setTexture(String texture) {
        //nothing
    }

    /**
     * <code>setColor</code> sets the overall shade of the model.
     * @param r the red component of the color.
     * @param g the green component of the color.
     * @param b the blue component of the color.
     * @param a the alpha component of the color.
     */
    public void setColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * <code>getBoundingBox</code> returns the bounding box that contains this
     * model.
     * @return the bounding box for the model.
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * <code>getBoundingSphere</code> returns the bounding sphere that contains
     * this model.
     * @return the bounding sphere for the model.
     */
    public BoundingSphere getBoundingSphere() {
        return boundingSphere;
    }

    /**
     * <code>setScale</code> sets the scale factor for the model.
     * @param scale the scale of the model.
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    /**
     * <code>loadModel</code> loads the currently defined model file and 
     * sets the appropriate data structures for rendering and animating.
     * @return true if the model loaded correctly, false otherwise.
     */
    public boolean loadModel() {
        if(null == path || null == model) {
            return false;
        }
        //model file names
        String lowerModel;
        String upperModel;
        String headModel;
        //skin file names
        String lowerSkin;
        String upperSkin;
        String headSkin;

        //build file names.
        lowerModel = path + "/" + model + "_lower.md3";
        upperModel = path + "/" + model + "_upper.md3";
        headModel = path + "/" + model + "_head.md3";
        lowerSkin = path + "/" + model + "_lower.skin";
        upperSkin = path + "/" + model + "_upper.skin";
        headSkin = path + "/" + model + "_head.skin";

        // Load the head mesh
        if (!importMD3(head, headModel)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the HEAD model!");
            return false;
        }

        // Load the upper mesh
        if (!importMD3(upper, upperModel)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the UPPER model!");
            return false;
        }

        // Load the lower mesh
        if (!importMD3(lower, lowerModel)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the LOWER model!");
            return false;
        }

        // Load the lower skin
        if (!loadSkin(lower, lowerSkin)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the LOWER skin!");
            return false;
        }

        // Load the upper skin
        if (!loadSkin(upper, upperSkin)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the UPPER skin!");
            return false;
        }

        // Load the head skin
        if (!loadSkin(head, headSkin)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the HEAD skin!");
            return false;
        }

        //load the lower, upper and head textures.  
        loadModelTextures(lower, path);
        loadModelTextures(upper, path);
        loadModelTextures(head, path);

        // Add the path and file name prefix to the animation.cfg file
        String configFile = path + "/" + model + "_animation.cfg";

        // Load the animation config file
        if (!loadAnimations(configFile)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the Animation Config File!");
            return false;
        }

        // Link the lower body to the upper body
        linkModel(lower, upper, "tag_torso");

        // Link the upper body to the head
        linkModel(upper, head, "tag_head");

        return true;
    }

    /**
     * <code>loadWeapon</code> loads the currently defined weapon file and 
     * sets the appropriate data structures for rendering and animating.
     * @return true if the weapon loaded correctly, false otherwise.
     */
    public boolean loadWeapon() {
        if(null == path || null == weapon) {
            return false;
        }
        String weaponFile;
        String shaderFile;
        
        weaponFile = path + "/" + weapon + ".md3";

        // Load the weapon mesh
        if (!importMD3(weaponModel, weaponFile)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the WEAPON model!");
            return false;
        }

        // Add the path, file name and .shader extension
        shaderFile = path + "/" + weapon + ".shader";

        // Load our textures associated with the gun from the weapon shader file
        if (!loadShader(weaponModel, shaderFile)) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING,
                "Unable to load the SHADER file!");
            return false;
        }

        loadModelTextures(weaponModel, path);

        // Link the weapon to the model's hand that has the weapon tag
        linkModel(upper, weaponModel, "tag_weapon");

        return true;
    }

    //  /////////////////////////////// IMPORT MD3 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////   This is called by the client to open the .Md3 file, read it, then clean up
    /////
    ///////////////////////////////// IMPORT MD3 \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

    private boolean importMD3(Model3D pModel, String file) {
        System.out.println("LOADING: " + file);
        InputStream is = null;
        int fileSize = 0;
        File f = new File(file);
        try {
            FileInputStream fis = new FileInputStream(f);
            // wrap a buffer to make reading more efficient (faster)
            BufferedInputStream bis = new BufferedInputStream(fis);
            fileSize = (int)f.length();
            fileContents = new byte[fileSize];

            System.out.println(fileContents.length);

            // Read the entire file into memory
            bis.read(fileContents, 0, fileSize);

            // Close the .md3 file that we opened
            bis.close();
        } catch (IOException ioe) {
            throw new MonkeyRuntimeException("Could not open MD3 Model." + file);
        }
        // Open the MD3 file in binary
        //filePointer = 0;

        buffer = ByteBuffer.wrap(fileContents).order(ByteOrder.nativeOrder());

        // Now that we know the file was found and it's all cool, let's read in
        // the header of the file.  If it has the correct 4 character ID and version number,
        // we can continue to load the rest of the data, otherwise we need to print an error.

        // Read the header data and store it in our header member variable
        header = new Md3Header();
        byte idBuffer[] = new byte[4];
        for (int i = 0; i < 4; i++) {
            idBuffer[i] = buffer.get();
        }
        header.fileID = Conversion.byte2String(idBuffer);

        header.version = buffer.getInt();

        byte fileBuffer[] = new byte[68];
        for (int i = 0; i < 68; i++) {
            fileBuffer[i] = buffer.get();
        }
        header.file = Conversion.byte2String(fileBuffer);

        header.numFrames = buffer.getInt();
        header.numTags = buffer.getInt();
        header.numMeshes = buffer.getInt();
        header.numMaxSkins = buffer.getInt();
        header.headerSize = buffer.getInt();
        header.tagStart = buffer.getInt();
        header.tagEnd = buffer.getInt();
        header.fileSize = buffer.getInt();

        // Get the 4 character ID
        String ID = header.fileID;

        // The ID MUST equal "IDP3" and the version MUST be 15, or else it isn't a valid
        // .MD3 file.  This is just the numbers ID Software chose.

        // Make sure the ID == IDP3 and the version is this crazy number '15' or else it's a bad egg
        if (!ID.equals("IDP3") || header.version != 15) {
            // Display an error message for bad file format, then stop loading
            System.err.println(
                "Invalid file format (Version not 15): " + file + "!");
            return false;
        }

        // Read in the model and animation data
        readMD3Data(pModel);

        // Return a success
        return true;
    }

    ///////////////////////////////// READ MD3 DATA \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////   This function reads in all of the model's data, except the animation frames
    /////
    ///////////////////////////////// READ MD3 DATA \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

    private void readMD3Data(Model3D pModel) {
        int i = 0;

        // This member function is the BEEF of our whole file.  This is where the
        // main data is loaded.  The frustrating part is that once the data is loaded,
        // you need to do a billion little things just to get the model loaded to the screen
        // in a correct manner.

        // Here we allocate memory for the bone information and read the bones in.
        bones = new Md3Bone[header.numFrames];
        for (i = 0; i < header.numFrames; i++) {
            bones[i] = new Md3Bone();
            bones[i].mins[0] = buffer.getFloat();
            bones[i].mins[1] = buffer.getFloat();
            bones[i].mins[2] = buffer.getFloat();
            bones[i].maxs[0] = buffer.getFloat();
            bones[i].maxs[1] = buffer.getFloat();
            bones[i].maxs[2] = buffer.getFloat();
            bones[i].position[0] = buffer.getFloat();
            bones[i].position[1] = buffer.getFloat();
            bones[i].position[2] = buffer.getFloat();
            bones[i].scale = buffer.getFloat();

            byte[] creatorBuff = new byte[16];
            for (int j = 0; j < 16; j++) {
                creatorBuff[j] = buffer.get();
            }
            bones[i].creator = Conversion.byte2String(creatorBuff);
        }

        // Since we don't care about the bone positions, we just free it immediately.
        // It might be cool to display them so you could get a visual of them with the model.

        // Free the unused bones
        bones = null;

        // Next, after the bones are read in, we need to read in the tags.  Below we allocate
        // memory for the tags and then read them in.  For every frame of animation there is
        // an array of tags.
        pModel.tags = new Md3Tag[header.numFrames * header.numTags];
        for (i = 0; i < header.numFrames * header.numTags; i++) {
            pModel.tags[i] = new Md3Tag();
            byte[] nameBuffer = new byte[64];
            for (int j = 0; j < 64; j++) {
                nameBuffer[j] = buffer.get();
            }
            pModel.tags[i].name = Conversion.byte2String(nameBuffer);
            pModel.tags[i].position.x = buffer.getFloat();
            pModel.tags[i].position.y = buffer.getFloat();
            pModel.tags[i].position.z = buffer.getFloat();
            for (int j = 0; j < 9; j++) {
                pModel.tags[i].rotation[j] = buffer.getFloat();
            }

        }

        // Assign the number of tags to our model
        pModel.numOfTags = header.numTags;

        // Now we want to initialize our links.  Links are not read in from the .MD3 file, so
        // we need to create them all ourselves.  We use a double array so that we can have an
        // array of pointers.  We don't want to store any information, just pointers to t3DModels.
        pModel.links = new Model3D[header.numTags];

        // Initilialize our link pointers to NULL
        for (i = 0; i < header.numTags; i++)
            pModel.links[i] = null;

        // Now comes the loading of the mesh data.  We want to use ftell() to get the current
        // position in the file.  This is then used to seek to the starting position of each of
        // the mesh data arrays.

        // Get the current offset into the file
        int meshOffset = buffer.position();

        // Create a local meshHeader that stores the info about the mesh
        Md3MeshInfo meshHeader = new Md3MeshInfo();

        // Go through all of the sub-objects in this mesh
        for (int j = 0; j < header.numMeshes; j++) {
            // Seek to the start of this mesh and read in it's header
            buffer.position(meshOffset);
            meshHeader = new Md3MeshInfo();
            byte[] meshBuffer = new byte[4];
            for (int k = 0; k < 4; k++) {
                meshBuffer[k] = buffer.get();
            }
            meshHeader.meshID = Conversion.byte2String(meshBuffer);

            byte[] nameBuffer = new byte[68];
            for (int k = 0; k < 68; k++) {
                nameBuffer[k] = buffer.get();
            }
            meshHeader.name = Conversion.byte2String(nameBuffer);
            meshHeader.numMeshFrames = buffer.getInt();
            meshHeader.numSkins = buffer.getInt();
            meshHeader.numVertices = buffer.getInt();
            meshHeader.numTriangles = buffer.getInt();
            meshHeader.triStart = buffer.getInt();
            meshHeader.headerSize = buffer.getInt();
            meshHeader.uvStart = buffer.getInt();
            meshHeader.vertexStart = buffer.getInt();
            meshHeader.meshSize = buffer.getInt();

            // Here we allocate all of our memory from the header's information
            skins = new Md3Skin[meshHeader.numSkins];

            texCoords = new Md3TexCoord[meshHeader.numVertices];

            triangles = new Triangle[meshHeader.numTriangles];
            vertices =
                new Md3Triangle[meshHeader.numVertices
                    * meshHeader.numMeshFrames];

            // Read in the skin information
            for (i = 0; i < meshHeader.numSkins; i++) {
                skins[i] = new Md3Skin();
                byte[] skinBuffer = new byte[68];
                for (int k = 0; k < 68; k++) {
                    skinBuffer[k] = buffer.get();
                }
                skins[i].name = Conversion.byte2String(skinBuffer);
            }

            // Seek to the start of the triangle/face data, then read it in
            buffer.position(meshOffset + meshHeader.triStart);
            for (i = 0; i < meshHeader.numTriangles; i++) {
                triangles[i] = new Triangle();
                triangles[i].vertexIndices[0] = buffer.getInt();
                triangles[i].vertexIndices[1] = buffer.getInt();
                triangles[i].vertexIndices[2] = buffer.getInt();
            }

            // Seek to the start of the UV coordinate data, then read it in
            buffer.position(meshOffset + meshHeader.uvStart);
            for (i = 0; i < meshHeader.numVertices; i++) {
                texCoords[i] = new Md3TexCoord();
                texCoords[i].textureCoord[0] = buffer.getFloat();
                texCoords[i].textureCoord[1] = buffer.getFloat();
            }

            // Seek to the start of the vertex/face index information, then read it in.
            buffer.position(meshOffset + meshHeader.vertexStart);
            for (i = 0;
                i < meshHeader.numMeshFrames * meshHeader.numVertices;
                i++) {

                vertices[i] = new Md3Triangle();
                vertices[i].vertex[0] = buffer.getShort();
                vertices[i].vertex[1] = buffer.getShort();
                vertices[i].vertex[2] = buffer.getShort();
                vertices[i].normal[0] = buffer.get();
                vertices[i].normal[1] = buffer.get();
            }

            // Now that we have the data loaded into the Quake3 structures, let's convert them to
            // our data types like Model3D and Object3D.  That way the rest of our model loading
            // code will be mostly the same as the other model loading tutorials.
            convertDataStructures(pModel, meshHeader);

            // Free all the memory for this mesh since we just converted it to our structures
            skins = null;
            texCoords = null;
            triangles = null;
            vertices = null;

            // Increase the offset into the file
            meshOffset += meshHeader.meshSize;
        }
    }

    ///////////////////////////////// CONVERT DATA STRUCTURES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////   This function converts the .md3 structures to our own model and object structures
    /////
    ///////////////////////////////// CONVERT DATA STRUCTURES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

    private void convertDataStructures(
        Model3D pModel,
        Md3MeshInfo meshHeader) {
        int i = 0;

        // This is function takes care of converting all of the Quake3 structures to our
        // structures that we have been using in all of our mode loading tutorials.  You
        // do not need this function if you are going to be using the Quake3 structures.
        // I just wanted to make it modular with the rest of the tutorials so you (me really) 
        // can make a engine out of them with an abstract base class.  Of course, each model
        // has some different data variables inside of the, depending on each format, but that
        // is perfect for some cool inheritance.  Just like in the .MD2 tutorials, we only
        // need to load in the texture coordinates and face information for one frame
        // of the animation (eventually in the next tutorial).  Where, the vertex information
        // needs to be loaded for every new frame, since it's vertex key frame animation 
        // used in .MD3 models.  Half-life models do NOT do this I believe.  It's just
        // pure bone/skeletal animation.  That will be a cool tutorial if the time ever comes.

        // Increase the number of objects (sub-objects) in our model since we are loading a new one
        pModel.numOfObjects++;

        // Create a empty object structure to store the object's info before we add it to our list
        Object3D currentMesh = new Object3D();

        // Copy the name of the object to our object structure
        currentMesh.name = meshHeader.name;
        //debug(".. .. .. name = " + meshHeader.name);

        // Assign the vertex, texture coord and face count to our new structure
        currentMesh.numOfVerts = meshHeader.numVertices;
        currentMesh.numTexVertex = meshHeader.numVertices;
        currentMesh.numOfFaces = meshHeader.numTriangles;

        // Allocate memory for the vertices, texture coordinates and face data.
        // Notice that we multiply the number of vertices to be allocated by the
        // number of frames in the mesh.  This is because each frame of animation has a 
        // totally new set of vertices.  This will be used in the next animation tutorial.
        currentMesh.verts =
            new Vector3f[currentMesh.numOfVerts * meshHeader.numMeshFrames];
        currentMesh.texVerts = new Vector3f[currentMesh.numOfVerts];
        currentMesh.faces = new Face[currentMesh.numOfFaces];

        // Go through all of the vertices and assign them over to our structure
        for (i = 0;
            i < currentMesh.numOfVerts * meshHeader.numMeshFrames;
            i++) {
            // For some reason, the ratio 64 is what we need to divide the vertices by,
            // otherwise the model is gargantuanly huge!  If you use another ratio, it
            // screws up the model's body part position.  I found this out by just
            // testing different numbers, and I came up with 65.  I looked at someone
            // else's code and noticed they had 64, so I changed it to that.  I have never
            // read any documentation on the model format that justifies this number, but
            // I can't get it to work without it.  Who knows....  Maybe it's different for
            // 3D Studio Max files verses other software?  You be the judge.  I just work here.. :)
            currentMesh.verts[i] = new Vector3f();
            currentMesh.verts[i].x = vertices[i].vertex[0] / 64.0f;
            currentMesh.verts[i].y = vertices[i].vertex[1] / 64.0f;
            currentMesh.verts[i].z = vertices[i].vertex[2] / 64.0f;
        }

        // Go through all of the uv coords and assign them over to our structure
        for (i = 0; i < currentMesh.numTexVertex; i++) {
            // Since I changed the images to bitmaps, we need to negate the V ( or y) value.
            // This is because I believe that TARGA (.tga) files, which were originally used
            // with this model, have the pixels flipped horizontally.  If you use other image
            // files and your texture mapping is crazy looking, try deleting this negative.
            currentMesh.texVerts[i] = new Vector3f();
            currentMesh.texVerts[i].x = texCoords[i].textureCoord[0];
            currentMesh.texVerts[i].y = -texCoords[i].textureCoord[1];
        }

        // Go through all of the face data and assign it over to OUR structure
        for (i = 0; i < currentMesh.numOfFaces; i++) {
            // Assign the vertex indices to our face data
            currentMesh.faces[i] = new Face();
            currentMesh.faces[i].vertIndex[0] = triangles[i].vertexIndices[0];
            currentMesh.faces[i].vertIndex[1] = triangles[i].vertexIndices[1];
            currentMesh.faces[i].vertIndex[2] = triangles[i].vertexIndices[2];

            // Assign the texture coord indices to our face data (same as the vertex indices)
            currentMesh.faces[i].coordIndex[0] = triangles[i].vertexIndices[0];
            currentMesh.faces[i].coordIndex[1] = triangles[i].vertexIndices[1];
            currentMesh.faces[i].coordIndex[2] = triangles[i].vertexIndices[2];
        }

        // Here we add the current object to our list object list
        pModel.object.add(currentMesh);
    }

    ///////////////////////////////// LOAD SKIN \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
    /////
    /////   This loads the texture information for the model from the *.skin file
    /////
    ///////////////////////////////// LOAD SKIN \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

    private boolean loadSkin(Model3D pModel, String strSkin) {
        System.out.println(strSkin);
        // Make sure valid data was passed in
        if (pModel == null || strSkin == null)
            return false;

        // This function is used to load a .skin file for the .md3 model associated
        // with it.  The .skin file stores the textures that need to go with each
        // object and subject in the .md3 files.  For instance, in our Lara Croft model,
        // her upper body model links to 2 texture; one for her body and the other for
        // her face/head.  The .skin file for the lara_upper.md3 model has 2 textures:
        //
        // u_torso,models/players/laracroft/default.bmp
        // u_head,models/players/laracroft/default_h.bmp
        //
        // Notice the first word, then a comma.  This word is the name of the object
        // in the .md3 file.  Remember, each .md3 file can have many sub-objects.
        // The next bit of text is the Quake3 path into the .pk3 file where the 
        // texture for that model is stored  Since we don't use the Quake3 path
        // because we aren't making Quake, I just grab the texture name at the
        // end of the string and disregard the rest.  of course, later this is
        // concatenated to the original MODEL_PATH that we passed into load our character.
        // So, for the torso object it's clear that default.bmp is assigned to it, where
        // as the head model with the pony tail, is assigned to default_h.bmp.  Simple enough.
        // What this function does is go through all the lines of the .skin file, and then
        // goes through all of the sub-objects in the .md3 file to see if their name is
        // in that line as a sub string.  We use our cool IsInString() function for that.
        // If it IS in that line, then we know that we need to grab it's texture file at
        // the end of the line.  I just parse backwards until I find the last '/' character,
        // then copy all the characters from that index + 1 on (I.E. "default.bmp").
        // Remember, it's important to note that I changed the texture files from .tga
        // files to .bmp files because that is what all of our tutorials use.  That way
        // you don't have to sift through tons of image loading code.  You can write or
        // get your own if you really want to use the .tga format.

        // Open the skin file
        InputStream is = null;
        int fileSize = 0;
        try {
            is = new FileInputStream(strSkin);
            // wrap a buffer to make reading more efficient (faster)
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));

            // These 2 variables are for reading in each line from the file, then storing
            // the index of where the bitmap name starts after the last '/' character.
            String strLine;
            int textureNameStart = 0;

            // Go through every line in the .skin file
            while ((strLine = reader.readLine()) != null) {
                // Loop through all of our objects to test if their name is in this line
                for (int i = 0; i < pModel.numOfObjects; i++) {
                    // Check if the name of this object appears in this line from the skin file
                    if (StringUtils
                        .isInString(
                            strLine,
                            ((Object3D)pModel.object.get(i)).name)) {
                        // To extract the texture name, we loop through the string, starting
                        // at the end of it until we find a '/' character, then save that index + 1.
                        textureNameStart = strLine.lastIndexOf("/") + 1;

                        // Create a local material info structure
                        MaterialInfo texture = new MaterialInfo();

                        // Copy the name of the file into our texture file name variable.
                        // Notice that with string we can pass in the address of an index
                        // and it will only pass in the characters from that point on. Cool huh?
                        // So now the file name should hold something like ("bitmap_name.bmp")
                        texture.file = strLine.substring(textureNameStart);

                        // The tile or scale for the UV's is 1 to 1 
                        texture.uTile = texture.uTile = 1;

                        // Store the material ID for this object and set the texture boolean to true
                        ((Object3D)pModel.object.get(i)).materialID =
                            pModel.numOfMaterials;
                        ((Object3D)pModel.object.get(i)).hasTexture = true;

                        // Here we increase the number of materials for the model
                        pModel.numOfMaterials++;

                        // Add the local material info structure to our model's material list
                        pModel.materials.add(texture);
                    }
                }
            }
            reader.close();

        } catch (IOException e) {
            throw new MonkeyRuntimeException("Could not load skin.");
        }
        // Close the file and return a success
        return true;
    }

    /**
     * <code>loadShader</code> loads the shader file for the weapon. This
     * is not a standard .shader file, but one which has been modified to
     * only include the texture information.
     * @param weaponModel the model to apply the shader to.
     * @param shaderFile the name of the .shader file.
     * @return true if the shader was loaded correctly. False otherwise.
     */
    private boolean loadShader(Model3D weaponModel, String shaderFile) {
        if (null == weaponModel || null == shaderFile) {
            return false;
        }
        
        InputStream is = null;
        int fileSize = 0;
        try {
            is = new FileInputStream(shaderFile);
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));

            String line;
            int currentIndex = 0;
            //read the file line by line
            while ((line = reader.readLine()) != null) {
                // Create a local material info structure
                MaterialInfo texture = new MaterialInfo();

                // Copy the name of the file into our texture file name variable
                texture.file = line;

                // The tile or scale for the UV's is 1 to 1 
                texture.uTile = texture.vTile = 1;

                // Store the\ ID for this object and set the texture to true
                ((Object3D)weaponModel.object.get(currentIndex)).materialID =
                    weaponModel.numOfMaterials;
                ((Object3D)weaponModel.object.get(currentIndex)).hasTexture = 
                    true;

                // Here we increase the number of materials for the model
                weaponModel.numOfMaterials++;

                // Add the local material info structure to our model's material list
                weaponModel.materials.add(texture);

                // Here we increase the material index for the next texture (if any)
                currentIndex++;
            }

            // Close the file and return a success
            reader.close();
        } catch (IOException e) {
            throw new MonkeyRuntimeException("Could not load shader " 
                    + shaderFile);
        }

        return true;
    }

    /**
     * <code>loadModelTextures</code> loads the textures for the current 
     * model. Each model has a number of materials which in turn could have
     * a number of textures.
     * @param model the model to load the texture's for.
     * @param texPath the path of the texture.
     */
    private void loadModelTextures(Model3D model, String texPath) {
        //Load the textures for each material
        for (int i = 0; i < model.numOfMaterials; i++) {
            if (((MaterialInfo)model.materials.get(i)).file != null) {
                String fullPath;

                //find the path of the texture.
                fullPath = texPath + "/"
                        + ((MaterialInfo)model.materials.get(i)).file;

                //use TextureManager to supply the texture id and store it.
                ((MaterialInfo)model.materials.get(i)).texureId =
                    TextureManager.getTextureManager().loadTexture(
                        fullPath,
                        GL.LINEAR_MIPMAP_LINEAR,
                        GL.LINEAR,
                        true);
            }
        }
    }

    /**
     * <code>loadAnimations</code> loads the animation.cfg file defining the
     * animation name and the frame count.
     * @param configFile the animation file to parse.
     * @return true if the loading was a success, false otherwise.
     */
    private boolean loadAnimations(String configFile) {
        AnimationInfo[] animations = new AnimationInfo[MAX_ANIMATIONS];

        InputStream is = null;
        int fileSize = 0;
        try {
            is = new FileInputStream(configFile);
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
            
            String word = "";
            String line = "";
            int currentAnim = 0;
            int torsoOffset = 0;
            
            StringTokenizer tokenizer;

            while ((line = reader.readLine()) != null) {
                // skip blank lines
                if (line.length() == 0) {
                    continue;
                }
                //continue until a number is found
                if (!Character.isDigit(line.charAt(0))) {
                    continue;
                }

                //start parsing the animation information
                tokenizer = new StringTokenizer(line);

                int startFrame = Integer.parseInt(tokenizer.nextToken());
                int numOfFrames = Integer.parseInt(tokenizer.nextToken());
                int loopingFrames = Integer.parseInt(tokenizer.nextToken());
                int framesPerSecond = Integer.parseInt(tokenizer.nextToken());

                //set the animation information
                animations[currentAnim] = new AnimationInfo();
                animations[currentAnim].startFrame = startFrame;
                animations[currentAnim].endFrame = startFrame + numOfFrames;
                animations[currentAnim].loopingFrames = loopingFrames;
                animations[currentAnim].framesPerSecond = framesPerSecond;
                //set the animation name
                tokenizer.nextToken();
                animations[currentAnim].name = tokenizer.nextToken();

                //add the animation to the appropriate model.
                if (StringUtils.isInString(line, "BOTH")) {
                    upper.animations.add(animations[currentAnim]);
                    lower.animations.add(animations[currentAnim]);
                } else if (StringUtils.isInString(line, "TORSO")) {
                    upper.animations.add(animations[currentAnim]);
                } else if (StringUtils.isInString(line, "LEGS")) {
                    //If the torso offset hasn't been set, set it
                    if (torsoOffset == 0)
                        torsoOffset =
                            animations[START_LEGS_ANIMATION].startFrame
                                - animations[START_TORSO_ANIMATION].startFrame;

                    animations[currentAnim].startFrame -= torsoOffset;
                    animations[currentAnim].endFrame -= torsoOffset;

                    lower.animations.add(animations[currentAnim]);
                }

                // Increase the current animation count
                currentAnim++;
            }
        } catch (IOException e) {
            throw new MonkeyRuntimeException("Could not load animations.");
        }

        lower.numOfAnimations = lower.animations.size();
        upper.numOfAnimations = upper.animations.size();
        head.numOfAnimations = head.animations.size();
        weaponModel.numOfAnimations = head.animations.size();

        return true;
    }

    /**
     * <code>linkModel</code> links two models together. In this case, links
     * the legs to the torso and the torso to the head, etc. 
     * @param parent the parent model (all rotations/translation effect child).
     * @param child the child model that is controlled by the parent.
     * @param tagName the name of the link.
     */
    private void linkModel(Model3D parent, Model3D child, String tagName) {
        if(null == parent || null == child || null == tagName) {
            return;
        }
        
        //find the tag and then set the link
        for (int i = 0; i < parent.numOfTags; i++) {
            if (parent.tags[i].name.equals(tagName)) {
                parent.links[i] = child;
                return;
            }
        }
    }

    /**
     * <code>updateModel</code> sets the animation frame. The frames are 
     * interpolated, so the frame rate for application is used to 
     * determine how much to interpolate for frame rate independant 
     * animations.
     * @param model the model to update.
     */
    private void updateModel(Model3D model) {
        int startFrame = 0;
        int endFrame = 1;

        //get the current animation information
        AnimationInfo animInfo =
            (AnimationInfo)model.animations.get(model.currentAnim);

        if (model.numOfAnimations != 0) {
            startFrame = animInfo.startFrame;
            endFrame = animInfo.endFrame;
        }

        //set the next frame.
        model.nextFrame = (model.currentFrame + 1) % endFrame;

        //if needed, loop the animation.
        if (model.nextFrame == 0) {
            model.nextFrame = startFrame;
        }
        //interpolate based on the frame rate.
        setCurrentTime(model);
    }

    /**
     * <code>drawLink</code> draws a link of the model. As the model is rendered
     * it's link or child is then rendered, which contains any matrix 
     * translation and/or rotations so the child correctly "sits" on the parent.
     * @param model the mesh to render.
     */
    private void drawLink(Model3D model) {
        renderModel(model);

        // Create some local variables to store all this crazy interpolation data
        Quaternion quat = new Quaternion();
        Quaternion nextQuat = new Quaternion();
        Quaternion interpolatedQuat = new Quaternion();
        float[] matrix;
        float[] nextMatrix;
        float[] finalMatrix = new float[16];

        //render each tag
        for (int i = 0; i < model.numOfTags; i++) {
            
            if (model.links[i] != null) {
                //interpolated between the two positions.
                Vector3f oldPosition =
                    model.tags[model.currentFrame*model.numOfTags+i].position;

                Vector3f nextPosition =
                    model.tags[model.nextFrame*model.numOfTags+i].position;

                //interpolate via p(t) = p0 + t(p1 - p0)
                Vector3f position = new Vector3f();
                position.x = oldPosition.x + model.t * (nextPosition.x - 
                    oldPosition.x);
                position.y = oldPosition.y + model.t * (nextPosition.y - 
                    oldPosition.y);
                position.z = oldPosition.z + model.t * (nextPosition.z - 
                    oldPosition.z);

                //interpolate the rotations
                matrix = model.tags[model.currentFrame * model.numOfTags
                        + i].rotation;

                nextMatrix = model.tags[model.nextFrame * model.numOfTags
                        + i].rotation;

                
                quat.fromMatrix(matrix, 3);
                nextQuat.fromMatrix(nextMatrix, 3);

                //slerp to interpolate
                interpolatedQuat = quat.slerp(quat, nextQuat, model.t);
                finalMatrix = interpolatedQuat.toMatrix();

                finalMatrix[12] = position.x;
                finalMatrix[13] = position.y;
                finalMatrix[14] = position.z;

                //render the model
                gl.pushMatrix();

                buf.clear();
                buf.put(finalMatrix);
                int ptr = Sys.getDirectBufferAddress(buf);
                gl.multMatrixf(ptr);
                //render the children
                drawLink(model.links[i]);

                gl.popMatrix();
            }
        }

    }

    /**
     * <code>setCurrentTime</code> uses the system clock to set the
     * time for animation interpolation.
     * @param model the model to set the time for.
     */
    private void setCurrentTime(Model3D model) {
        if (model.animations.size() == 0) {
            return;
        }
        
        long time = System.currentTimeMillis();
        //the number of milliseconds between the current time and the last time.
        long elapsedTime = time - model.lastTime;
        
        int animationSpeed = ((AnimationInfo)model.animations.get(
                    model.currentAnim)).framesPerSecond;

        //find the ratio between the first and second frame.
        float t = elapsedTime / (1000f / animationSpeed);

        //check to see if we should go to the next frame
        if (elapsedTime >= (1000.0f / animationSpeed)) {
            model.currentFrame = model.nextFrame;
            model.lastTime = time;
        }

        model.t = t;
    }

    /**
     * <code>renderModel</code> actually does the rendering to the 
     * OpenGL context. 
     * @param model the model to render.
     */
    private void renderModel(Model3D model) {
        if (null == model.object) {
            return;
        }
        
        for (int i = 0; i < model.numOfObjects; i++) {
            Object3D object3d = (Object3D)model.object.get(i);

            int currentIndex = model.currentFrame * object3d.numOfVerts;
            int nextIndex = model.nextFrame * object3d.numOfVerts;
            
            //if there is a texture assigned to the model, use it.
            if (object3d.hasTexture) {
                gl.enable(GL.TEXTURE_2D);

                int textureID = ((MaterialInfo)model.materials.get(
                            object3d.materialID)).texureId;

                TextureManager.getTextureManager().bind(textureID);
            } else {
                gl.disable(GL.TEXTURE_2D);
            }

            //render the model as triangles
            gl.begin(GL.TRIANGLES);

            for (int j = 0; j < object3d.numOfFaces; j++) {
                for (int whichVertex = 0; whichVertex < 3; whichVertex++) {
                    int index = object3d.faces[j].vertIndex[whichVertex];

                    if (object3d.texVerts != null) {
                        // Assign the texture coordinate to this vertex
                        gl.texCoord2f(
                            object3d.texVerts[index].x,
                            object3d.texVerts[index].y);
                    }

                    Vector3f point1 = object3d.verts[currentIndex + index];
                    Vector3f point2 = object3d.verts[nextIndex + index];
                    
                    //interpolate
                    gl.vertex3f(
                        point1.x + model.t * (point2.x - point1.x),
                        point1.y + model.t * (point2.y - point1.y),
                        point1.z + model.t * (point2.z - point1.z));

                }
            }
            gl.end();
        }
    }
    
    /**
     * <code>Md3Header</code> maintains the header information for the 
     * MD3 file.
     */
    private class Md3Header {
        /**
         * the file ID should be "IDP3"
         */
        String fileID;
        /**
         * the version of the file should be 15.
         */
        int version;
        /**
         * the name of the file.
         */
        String file;
        /**
         * the number of animation frames.
         */
        int numFrames;
        /**
         * the number of tags.
         */
        int numTags;
        /**
         * the number of meshes.
         */
        int numMeshes;
        /**
         * the number of skins for the mesh.
         */
        int numMaxSkins;
        /**
         * the size of the header.
         */
        int headerSize;
        /**
         * the offset into the file for tags.
         */
        int tagStart;
        /**
         * the offset into the file for the last of the tags.
         */
        int tagEnd;
        /**
         * the size of the MD3 file.
         */
        int fileSize;
    };

    /**
     * <code>Md3MeshInfo</code> stores information about each mesh of the 
     * model.
     */
    private class Md3MeshInfo {
        /**
         * the id of the mesh.
         */
        String meshID;
        /**
         * the name of the mesh.
         */
        String name;
        /**
         * the number of frames this mesh has.
         */
        int numMeshFrames;
        /**
         * the number of skins the mesh has.
         */
        int numSkins;
        /**
         * the number of vertices the mesh has.
         */
        int numVertices;
        /**
         * the number of triangles the mesh has.
         */
        int numTriangles;
        /**
         * the offset for the triangles.
         */
        int triStart;
        /**
         * the size of the header for the mesh.
         */
        int headerSize;
        /**
         * the offset of the texture coordinates.
         */
        int uvStart;
        /**
         * the offset for the vertex indices.
         */
        int vertexStart;
        /**
         * the total size of the mesh.
         */
        int meshSize;
    };

    /**
     * <code>Md3Skin</code> contains the skin name.
     */
    private class Md3Skin {
        /**
         * the name of the skin.
         */
        String name;
    };

    /**
     * 
     * <code>Md3TexCoord</code> keeps track of the texture coordinates.
     */
    private class Md3TexCoord {
        float[] textureCoord = new float[2];
    };

    /**
     * 
     * <code>Md3Tag</code> is used to link model meshes together.
     */
    private class Md3Tag {
        /**
         * the name of the tag.
         */
        String name;
        /**
         * the translation of the tag.
         */
        Vector3f position = new Vector3f();
        /**
         * the rotation of the tag.
         */
        float[] rotation = new float[9];
    };

    /**
     * 
     * <code>Md3Bone</code> stores all the bone information. 
     */
    private class Md3Bone {
        /**
         * the minimum (x,y,z) value for the bone.
         */
        float[] mins = new float[3];
        /**
         * the maximum (x,y,z) value for the bone.
         */
        float[] maxs = new float[3];
        /**
         * the position of the bone.
         */
        float[] position = new float[3];
        /**
         * the scale of the bone.
         */
        float scale;
        /**
         * the modeler used to create the bone.
         */
        String creator;
    };

    /**
     * 
     * <code>Md3Triangle</code> stores indices to the vertices and the normals.
     */
    private class Md3Triangle {
        /**
         * the vertex indices.
         */
        short[] vertex = new short[3];
        /**
         * the normal values for the triangle.
         */
        int[] normal = new int[2];
    };

    /**
     * 
     * <code>Face</code> contains all information for a single face of the
     * mesh.
     */
    private class Face {
        /**
         * the vertices that make up the face.
         */
        int[] vertIndex = new int[3];
        /**
         * the texture coordinates for the face.
         */
        int[] coordIndex = new int[3];
    };

    /**
     * 
     * <code>MaterialInfo</code> maintains material information for a mesh.
     * This includes the texture and color.
     * 
     */
    private class MaterialInfo {
        /**
         * the name of the material.
         */
        String name;
        /**
         * the texture file name.
         */
        String file;
        /**
         * the color of the mesh.
         */
        byte[] color = new byte[3];
        /**
         * the id of the texture.
         */
        int texureId;
        /**
         * u tiling of the texture.
         */
        float uTile;
        /**
         * v tiling of the texture.
         */
        float vTile;
        /**
         * u offset of the texture.
         */
        float uOffset;
        /**
         * v offset of the texture.
         */
        float vOffset;
    };

    /**
     * 
     * <code>Object3D</code> contains information for rendering the mesh.
     * 
     */
    private class Object3D {
        /**
         * number of vertices of the model.
         */
        int numOfVerts;
        /**
         * number of faces of the model.
         */
        int numOfFaces;
        /**
         * number of texture coordinates of the model.
         */
        int numTexVertex;
        /**
         * the material this object uses.
         */
        int materialID;
        /**
         * whether the object has a texture or not.
         */
        boolean hasTexture;
        /**
         * the name of the object.
         */
        String name;
        /**
         * the vertices of the object.
         */
        Vector3f[] verts;
        /**
         * the object's normals.
         */
        Vector3f[] normals;
        /**
         * the texture coordinates.
         */
        Vector3f[] texVerts;
        /**
         * the faces of the object.
         */
        Face[] faces;
    };

    /**
     * 
     * <code>AnimationInfo</code> contains information about the animation.
     * 
     */
    private class AnimationInfo {
        /**
         * the name of the animation.
         */
        String name;
        /**
         * the start frame of the animation.
         */
        int startFrame;
        /**
         * the last frame of the animation.
         */
        int endFrame;
        /**
         * the frames used for looping.
         */
        int loopingFrames;
        /**
         * how many frames per second used for the animations.
         */
        int framesPerSecond;
    };

    /**
     * 
     * <code>Model3D</code> contains information about the MD3 model.
     * 
     */
    private class Model3D {
        /**
         * the number of objects that the model contains.
         */
        int numOfObjects;
        /**
         * the number of materials the model contains.
         */
        int numOfMaterials;
        /**
         * The materials that make up the model.
         */
        ArrayList materials = new ArrayList();
        /**
         * the list of objects that make up the model.
         */
        ArrayList object = new ArrayList();
        /**
         * the number of animations in the model.
         */
        int numOfAnimations;
        /**
         * the current animation used.
         */
        int currentAnim;
        /**
         * the current frame of the current animation.
         */
        int currentFrame;
        /**
         * the next frame of the current animation.
         */
        int nextFrame;
        /**
         * interpolation values between the two animation frames.
         */
        float t;
        /**
         * the last time of update.
         */
        long lastTime;
        /**
         * list of animations.
         */
        ArrayList animations = new ArrayList();
        /**
         * the number of tags for the model.
         */
        int numOfTags;
        /**
         * the links to children models.
         */
        Model3D[] links;
        /**
         * model tags for animation.
         */
        Md3Tag[] tags;
    };
}