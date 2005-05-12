package com.jmex.model.XMLparser.Converters;

import com.jme.util.BinaryFileReader;
import com.jme.scene.*;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.system.JmeException;
import com.jmex.model.EmptyTriMesh;
import com.jmex.model.Face;
import com.jmex.model.XMLparser.JmeBinaryWriter;
import com.jmex.model.animation.KeyframeController;

import java.io.*;
import java.util.Random;


/**
 * Started Date: Jun 14, 2004<br><br>
 *
 * This class converts a .md2 file to jME's binary format.
 * 
 * @author Jack Lindamood
 */
public class Md2ToJme extends FormatConverter{

    /**
     * Converts an Md2 file to jME format.  The syntax is: "Md2ToJme drfreak.md2 outfile.jme".
     * @param args The array of parameters
     */
    public static void main(String[] args){
        new DummyDisplaySystem();
        new Md2ToJme().attemptFileConvert(args);
    }


    /**
     * It creates a node from a .md2 stream and then writes that
     * node to the given OutputStream in jME's binary format.
     * @param Md2Stream A stream representing the .md2 file
     * @param o The stream to write it's binary equivalent to
     * @throws java.io.IOException If anything funky goes wrong with reading information
     */
    public void convert(InputStream Md2Stream,OutputStream o) throws IOException {
        if (Md2Stream==null)
            throw new NullPointerException("Unable to load null streams");
        JmeBinaryWriter i=new JmeBinaryWriter();
        i.setProperty("q3norm","true");
        i.setProperty("q3vert","true");
        i.writeScene(new Md2ConverterCopy(Md2Stream),o);
    }

    /**
     * 95% a Copy/paste of the .md2 loader by Mark Powell modifyed for efficiency (use of empty TriMesh) and
     * VertexController as well as a few tiny adjustments here and there on memory.
     *
     * @author Mark Powell
     * @author Jack Lindamood
     */
    private static class Md2ConverterCopy extends TriMesh{
        private static final long serialVersionUID = 1L;

		private BinaryFileReader bis = null;

        private Header header;

        private Vector2f[] texCoords;
        private Md2Face[] triangles;
        private Md2Frame[] frames;

        //holds each keyframe.
        private TriMesh[] triMesh;
        //controller responsible for handling keyframe morphing.
        private KeyframeController controller;


        /**
         * Loads an MD2 model. The corresponding
         * <code>TriMesh</code> objects are created and attached to the
         * model. Each keyframe is then loaded and assigned to a
         * <code>KeyframeController</code>. MD2 does not keep track
         * of it's own texture or material settings, so the user is
         * responsible for setting these.
         * @param Md2 the InputStream of the file to load.
         */
        public Md2ConverterCopy(InputStream Md2) {
            super("MD2 mesh"+new Random().nextInt());
            if(null == Md2) {
                throw new JmeException("Null data. Cannot load.");
            }
//            bis = new BinaryFileReader(filename);
            bis=new BinaryFileReader(Md2);

            header = new Header();

            if (header.version != 8) {
                throw new JmeException(
                    "Invalid file format (Version not 8)!");
            }

            parseMesh();
            convertDataStructures();

            triangles = null;
            texCoords = null;
            frames = null;
        }

        /**
         * <code>getAnimationController</code> returns the animation controller
         * used for MD2 animation (VertexKeyframeController).
         *
         * @return @see com.jmex.model.Model#getAnimationController()
         */
        public Controller getAnimationController() {
            return controller;
        }

        /**
         *
         * <code>parseMesh</code> reads the MD2 file and builds the
         * necessary data structures. These structures are specific to
         * MD2 and therefore require later conversion to jME data structures.
         *
         */
        private void parseMesh() {
            String[] skins = new String[header.numSkins];
            texCoords = new Vector2f[header.numTexCoords];
            triangles = new Md2Face[header.numTriangles];
            frames = new Md2Frame[header.numFrames];

            //start with skins. Move the file pointer to the correct position.
            bis.setOffset(header.offsetSkins);

            // Read in each skin for this model
            for (int j = 0; j < header.numSkins; j++) {
                skins[j] = bis.readString(64);
            }

            //Now read in texture coordinates.
            bis.setOffset(header.offsetTexCoords);
            for (int j = 0; j < header.numTexCoords; j++) {
                texCoords[j] = new Vector2f();
                texCoords[j].x = bis.readShort();
                texCoords[j].y = bis.readShort();
            }

            //read the vertex data.
            bis.setOffset(header.offsetTriangles);
            for (int j = 0; j < header.numTriangles; j++) {
                triangles[j] = new Md2Face();
            }
            bis.setOffset(header.offsetFrames);

            //Each keyframe has the same type of data, so read each
            //keyframe one at a time.
            for (int i = 0; i < header.numFrames; i++) {
                VectorKeyframe frame = new VectorKeyframe();
                frames[i] = new Md2Frame();

                frames[i].vertices = new Triangle[header.numVertices];
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
                frames[i].name = frame.name;
                Triangle[] verices = frames[i].vertices;

                for (int j = 0; j < header.numVertices; j++) {
                    verices[j] = new Triangle();
                    verices[j].vertex.x =
                        aliasVertices[j].x * frame.scale.x + frame.translate.x;
                    verices[j].vertex.z =
                        -1
                            * (aliasVertices[j].y * frame.scale.y
                                + frame.translate.y);
                    verices[j].vertex.y =
                        aliasVertices[j].z * frame.scale.z + frame.translate.z;
                }
            }
        }

        /**
         *
         * <code>convertDataStructures</code> takes the loaded MD2 data and
         * converts it into jME data.
         *
         */
        private void convertDataStructures() {
            triMesh = new TriMesh[header.numFrames];
            Vector2f[] texCoords2 = new Vector2f[header.numVertices];
            controller = new KeyframeController();
            for (int i = 0; i < header.numFrames; i++) {
                int numOfVerts = header.numVertices;
                int numTexVertex = header.numTexCoords;
                int numOfFaces = header.numTriangles;
                if (i!=0)
                    triMesh[i] = new EmptyTriMesh();
                else
                    triMesh[i] = this;
                Vector3f[] verts = new Vector3f[numOfVerts];
                Vector2f[] texVerts = new Vector2f[numTexVertex];

                Face[] faces = new Face[numOfFaces];

                //assign a vector array for the trimesh.
                for (int j = 0; j < numOfVerts; j++) {
//                    verts[j] = new Vector3f();
//                    verts[j].x = frames[i].vertices[j].vertex.x;
//                    verts[j].y = frames[i].vertices[j].vertex.y;
//                    verts[j].z = frames[i].vertices[j].vertex.z;
                    if (i!=0)
                        verts[j]=frames[i].vertices[j].vertex;
                    else
                        verts[j]=new Vector3f(frames[i].vertices[j].vertex);
                }

                //set up the initial indices array.
                for (int j = 0; j < numOfFaces; j++) {
                    faces[j] = new Face();
                    faces[j].vertIndex[0] = triangles[j].vertexIndices[0];
                    faces[j].vertIndex[1] = triangles[j].vertexIndices[1];
                    faces[j].vertIndex[2] = triangles[j].vertexIndices[2];

                    faces[j].coordIndex[0] = triangles[j].textureIndices[0];
                    faces[j].coordIndex[1] = triangles[j].textureIndices[1];
                    faces[j].coordIndex[2] = triangles[j].textureIndices[2];
                }

                if (i == 0) {
                    //texture coordinates.
                    for (int j = 0; j < numTexVertex; j++) {
                        texVerts[j] = new Vector2f();
                        texVerts[j].x = texCoords[j].x / (float) (header.skinWidth);
                        texVerts[j].y =
                            1 - texCoords[j].y / (float) (header.skinHeight);
                    }

                    //reorginize coordinates to match the vertex index.
                    if (numTexVertex != 0) {
                      for (int j = 0; j < numOfFaces; j++) {
                        int index = faces[j].vertIndex[0];
//                        texCoords2[index] = new Vector2f();   ??? Why was this here???
                        texCoords2[index] = texVerts[faces[j].coordIndex[0]];

                        index = faces[j].vertIndex[1];
//                        texCoords2[index] = new Vector2f();
                        texCoords2[index] = texVerts[faces[j].coordIndex[1]];

                        index = faces[j].vertIndex[2];
//                        texCoords2[index] = new Vector2f();   ??
                        texCoords2[index] = texVerts[faces[j].coordIndex[2]];
                      }
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
                    triMesh[i].setTextures(texCoords2);
                    controller.setMorphingMesh(triMesh[i]);

                }   // End if (i==0)

                triMesh[i].setVertices(verts);
                triMesh[i].setNormals(computeNormals(faces, verts));
                if (i!=0) controller.setKeyframe(i-1,triMesh[i]);
            }
            //build controller. Attach everything.
//            this.attachChild(triMesh[0]);
//            triMesh[0].addController(controller);
            this.addController(controller);
        }

        /**
         *
         * <code>computeNormals</code> calculates the normals of
         * the model.
         * @param faces the faces of the model.
         * @param verts the vertices of the model.
         * @return the array of normals.
         */
        private Vector3f[] computeNormals(Face[] faces, Vector3f[] verts) {
            Vector3f[] returnNormals = new Vector3f[verts.length];

//            Vector3f[] normals = new Vector3f[faces.length];      // Why is this here?
            Vector3f[] tempNormals = new Vector3f[faces.length];

            for (int i = 0; i < faces.length; i++) {
                tempNormals[i] =
                    verts[faces[i].vertIndex[0]].subtract(
                        verts[faces[i].vertIndex[2]]).cross(
                        verts[faces[i].vertIndex[2]].subtract(
                            verts[faces[i].vertIndex[1]]));
//                normals[i] = tempNormals[i].normalize();  ??? Why is this here
            }

            Vector3f sum = new Vector3f();
            int shared = 0;

            for (int i = 0; i < verts.length; i++) {
                for (int j = 0; j < faces.length; j++) {
                    if (faces[j].vertIndex[0] == i
                        || faces[j].vertIndex[1] == i
                        || faces[j].vertIndex[2] == i) {
                        sum.addLocal(tempNormals[j]);
                        shared++;
                    }
                }

                returnNormals[i] = sum.divide(-shared);
//                returnNormals[i] = returnNormals[i].normalizeLocal().negateLocal();
                returnNormals[i].normalizeLocal().negateLocal();

                sum.zero();
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
            String name; // char [16]
            Triangle[] vertices;

            Md2Frame() {
            }
        };
    }

    /**
     * This function returns the KeyframeController that animates an MD2 converted mesh.
     * Null is returned if a KeyframeController cannot be found.
     * @param model The MD2 mesh.
     * @return This mesh's controller.
     */
    public static KeyframeController findController(Node model) {
        if (model.getQuantity()==0 ||
                model.getChild(0).getControllers().size()==0 ||
                !(model.getChild(0).getController(0) instanceof KeyframeController))
            return null;
        return (KeyframeController) model.getChild(0).getController(0);
    }
}
