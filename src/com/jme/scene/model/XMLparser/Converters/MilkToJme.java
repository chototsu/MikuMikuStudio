package com.jme.scene.model.XMLparser.Converters;

import com.jme.util.LittleEndien;
import com.jme.system.JmeException;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.scene.*;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.animation.JointController;
import com.jme.scene.model.JointMesh2;
import com.jme.scene.state.*;
import com.jme.renderer.*;
import com.jme.image.Texture;


import java.io.*;

/**
 * Started Date: Jun 8, 2004
 * This class converts a .ms3d file to jME's binary format.  The way it converts is by first
 * building the .ms3d scenegraph object, then saving that object to binary format via JmeBinaryWriter
 * 
 * @author Jack Lindamood
 */
public class MilkToJme extends FormatConverter{
    private DataInput inFile;
    private byte[] tempChar=new byte[128];
    private int nNumVertices;
    private MilkVertex[] myVerts;
    private int nNumTriangles;
    private MilkTriangle[] myTris;
    private int[] materialIndexes;

    /**
     * Converts a MS3D file to jME format.  The syntax is: MilkToJme runner.ms3d out.jme
     * @param args
     */
    public static void main(String[] args){
        new DummyDisplaySystem();
        new MilkToJme().attemptFileConvert(args);
    }


    /**
     * The node that represents the .ms3d file.  It's chidren are MS meshes
     */
    private Node finalNode;

    /**
     * This class's only public function.  It creates a node from a .ms3d stream and then writes that node to the given
     * OutputStream in binary format
     * @param MSFile An inputStream that is the .ms3d file
     * @param o The Stream to write it's jME binary equivalent to
     * @throws IOException If anything funky goes wrong with reading information
     */
    public void convert(InputStream MSFile,OutputStream o) throws IOException {
        inFile=new LittleEndien(MSFile);
        finalNode=new Node("ms3d file");
        checkHeader();
        readVerts();
        readTriangles();
        readGroups();
        readMats();
        readJoints();
        JmeBinaryWriter jbw=new JmeBinaryWriter();
        jbw.writeScene(finalNode,o);
    }

    private void readJoints() throws IOException {
        float fAnimationFPS=inFile.readFloat();
        float curTime=inFile.readFloat();     // Ignore currentTime
        int iTotalFrames=inFile.readInt();      // Ignore total Frames
        int nNumJoints=inFile.readUnsignedShort();
        String[] jointNames=new String[nNumJoints];
        String[] parentNames=new String[nNumJoints];
        JointController jc=new JointController(nNumJoints);
        jc.FPS=fAnimationFPS;

        for (int i=0;i<nNumJoints;i++){
            inFile.readByte();  // Ignore flags
            inFile.readFully(tempChar,0,32);
            jointNames[i]=cutAtNull(tempChar);
            inFile.readFully(tempChar,0,32);
            parentNames[i]=cutAtNull(tempChar);
            jc.localRefMatrix[i].setEulerRot(inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            jc.localRefMatrix[i].setTranslation(inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            int numKeyFramesRot=inFile.readUnsignedShort();
            int numKeyFramesTrans=inFile.readUnsignedShort();
            for (int j=0;j<numKeyFramesRot;j++)
                jc.setRotation(i,inFile.readFloat(),inFile.readFloat(),inFile.readFloat(),inFile.readFloat());
            for (int j=0;j<numKeyFramesTrans;j++)
                jc.setTranslation(i,inFile.readFloat(),inFile.readFloat(),inFile.readFloat(),inFile.readFloat());

        }
        for (int i=0;i<nNumJoints;i++){
            jc.parentIndex[i]=-1;
            for (int j=0;j<nNumJoints;j++){
                if (parentNames[i].equals(jointNames[j])) jc.parentIndex[i]=j;
            }
        }
        jc.processController();

        finalNode.addController(jc);
    }

    private void readMats() throws IOException {
        int nNumMaterials=inFile.readUnsignedShort();
        for (int i=0;i<nNumMaterials;i++){
            inFile.skipBytes(32);   // Skip the name, unused
//            if (!inArray(materialIndexes,i)){ //TODO: Make this work
//                inFile.skipBytes(329);
//                continue;
//            }
            MaterialState matState=new MaterialState(){
                public void apply() {throw new JmeException("I am not to be used in a real graph");}
            };
            matState.setAmbient(getNextColor());
            matState.setDiffuse(getNextColor());
            matState.setSpecular(getNextColor());
            matState.setEmissive(getNextColor());
            matState.setShininess(inFile.readFloat());
            matState.setAlpha(inFile.readFloat());
            inFile.readByte();      // Mode is ignored

            inFile.readFully(tempChar,0,128);
            TextureState texState=null;
            String texFile=cutAtNull(tempChar);
            if (texFile.length()!=0){
                texState=new TextureState(){
                    public void setTexture(Texture t){
                        if (texture.length==0) texture=new Texture[1];
                        texture[0] = t;
                    }
                    public void delete(int unit) {throw new JmeException("I am not to be used in a real graph");}
                    public void deleteAll() {throw new JmeException("I am not to be used in a real graph");}
                    public void apply() {throw new JmeException("I am not to be used in a real graph");}

                };
                Texture tempTex=new Texture();
//                tempTex.setImageLocation(new URL("file:///./"+texFile));
                tempTex.setImageLocation("file:/"+texFile);
                // TODO: Work on proper image locaion
                texState.setTexture(tempTex);
            }
            inFile.readFully(tempChar,0,128);   // Alpha map, but it is ignored
            //TODO: Implement Alpha Maps

            applyStates(matState,texState,i);
        }
    }

    /**
     * Every child of finalNode (IE the .ms3d file) whos materialIndex is the given index, gets the two RenderStates applied
     * @param matState A Material state to apply
     * @param texState A TextureState to apply
     * @param index The index that a TriMesh should have from <code>materialIndex</code> to get the given
     * states
     */
    private void applyStates(MaterialState matState, TextureState texState,int index) {
        for (int i=0;i<finalNode.getQuantity();i++){
            if (materialIndexes[i]==index){
                if (matState!=null) ((TriMesh)finalNode.getChild(i)).setRenderState(matState);
                if (texState!=null) ((TriMesh)finalNode.getChild(i)).setRenderState(texState);
            }
        }
    }

    private ColorRGBA getNextColor() throws IOException {
        return new ColorRGBA(
                inFile.readFloat(),
                inFile.readFloat(),
                inFile.readFloat(),
                inFile.readFloat()
        );
    }

    private void readGroups() throws IOException {
        int nNumGroups=inFile.readUnsignedShort();
        materialIndexes=new int[nNumGroups];
        for (int i=0;i<nNumGroups;i++){
            inFile.readByte();      // Ignore flags
            inFile.readFully(tempChar,0,32);    // Name
            int numTriLocal=inFile.readUnsignedShort();
            Vector3f[] meshVerts=new Vector3f[numTriLocal*3],meshNormals=new Vector3f[numTriLocal*3];
            Vector3f[] origVerts=new Vector3f[numTriLocal*3],origNormals=new Vector3f[numTriLocal*3];
            Vector2f[] meshTexCoords=new Vector2f[numTriLocal*3];
            int[] meshIndex=new int[numTriLocal*3];
            int[] jointIndex=new int[numTriLocal*3];
            JointMesh2 theMesh=new JointMesh2(cutAtNull(tempChar));

            for (int j=0;j<numTriLocal;j++){
                int triIndex=inFile.readUnsignedShort();
                for (int k=0;k<3;k++){
                    meshVerts       [j*3+k]=myVerts[myTris[triIndex].vertIndicies[k]].vertex;
                    meshNormals     [j*3+k]=myTris[triIndex].vertNormals[k];
                    meshTexCoords   [j*3+k]=myTris[triIndex].vertTexCoords[k];
                    meshIndex       [j*3+k]=j*3+k;
                    origVerts       [j*3+k]=meshVerts[j*3+k];
                    origNormals     [j*3+k]=meshNormals[j*3+k];
                    jointIndex      [j*3+k]=myVerts[myTris[triIndex].vertIndicies[k]].boneID;
                }
            }
            theMesh.reconstruct(meshVerts,meshNormals,null,meshTexCoords,meshIndex);
            theMesh.originalNormal=origNormals;
            theMesh.originalVertex=origVerts;
            theMesh.jointIndex=jointIndex;
            finalNode.attachChild(theMesh);
            materialIndexes[i]=inFile.readByte();
        }
    }

    private void readTriangles() throws IOException {
        nNumTriangles=inFile.readUnsignedShort();
        myTris=new MilkTriangle[nNumTriangles];
        for (int i=0;i<nNumTriangles;i++){
            int j;
            myTris[i]=new MilkTriangle();
            inFile.readUnsignedShort(); // Ignore flags
            for (j=0;j<3;j++)
                myTris[i].vertIndicies[j]=inFile.readUnsignedShort();
            for (j=0;j<3;j++){
                myTris[i].vertNormals[j]=new Vector3f(
                        inFile.readFloat(),
                        inFile.readFloat(),
                        inFile.readFloat()
                );
            }
            for (j=0;j<3;j++){
                myTris[i].vertTexCoords[j]=new Vector2f();
                myTris[i].vertTexCoords[j].x=inFile.readFloat();
            }
            for (j=0;j<3;j++)
                myTris[i].vertTexCoords[j].y=1-inFile.readFloat();
            inFile.readByte();      // Ignore smoothingGroup
            inFile.readByte();      // Ignore groupIndex
        }
    }

    private void readVerts() throws IOException {
        nNumVertices=inFile.readUnsignedShort();
        myVerts=new MilkVertex[nNumVertices];
        for (int i=0;i<nNumVertices;i++){
            myVerts[i]=new MilkVertex();
            inFile.readByte(); // Ignore flags
            myVerts[i].vertex=new Vector3f(
                    inFile.readFloat(),
                    inFile.readFloat(),
                    inFile.readFloat()
            );
            myVerts[i].boneID=inFile.readByte();
            inFile.readByte();  // Ignore referenceCount
        }
    }

    private void checkHeader() throws IOException {
        inFile.readFully(tempChar,0,10);
        if (!"MS3D000000".equals(new String(tempChar,0,10))) throw new JmeException("Wrong File type: not Milkshape file??");
        if (inFile.readInt()!=4) throw new JmeException("Wrong file version: Not 4");   // version
    }

    private static class MilkVertex{
        Vector3f vertex;
        byte boneID;
    }
    private static class MilkTriangle{
        int[] vertIndicies=new int[3];              // 3 ints
        Vector3f[] vertNormals=new Vector3f[3];     // 3 Vector3fs
        Vector2f[] vertTexCoords=new Vector2f[3];   // 3 Texture Coords
    }
    private static String cutAtNull(byte[] inString) {
        for (int i=0;i<inString.length;i++)
            if (inString[i]==0) return new String(inString,0,i);
        return new String(inString);
    }
}