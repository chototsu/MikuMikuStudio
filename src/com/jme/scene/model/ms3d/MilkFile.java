package com.jme.scene.model.ms3d;



import java.io.IOException;

import java.net.URL;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.system.JmeException;
import com.jme.renderer.ColorRGBA;
import com.jme.util.LittleEndien;

/**
 * Helper class for MilkLoader to hold loaded .ms3d files.
 *
 * @author Jack Lindamood
 */
class MilkFile {

    private LittleEndien file;
    private int nNumVertices;
    MilkshapeTriangle[] myTri;
    private int nNumTriangles;
    byte[] boneID;
    Vector3f[] vertexes;
    private int[] vertexIndextoGroup;
    int nNumGroups;
    MilkshapeGroup[] myGroups;
    private int nNumMaterials;
    private MilkshapeMaterial[] myMats;
    int iTotalFrames;
    int nNumJoints;
    MilkJoint[] myJoints;
    private URL baseURL;
    public float speed;


    private void invertWithParents() {
        for (int i=0;i<nNumJoints;i++){
            myJoints[i].inverseChainMatrix=new TransformMatrix(myJoints[i].localRefMatrix);
            myJoints[i].inverseChainMatrix.inverse();
            if (myJoints[i].parentIndex!=-1)
                myJoints[i].inverseChainMatrix.multLocal(myJoints[myJoints[i].parentIndex].inverseChainMatrix);
        }
    }

    private MilkFile(){ // used to create copies
    }

    MilkFile(URL baseURL,LittleEndien file) {
        this.file=file;
        this.baseURL=baseURL;
        loadInfo();
        processFile();
    }


    private void processFile() {

        for (int i=0;i<nNumGroups;i++){
            myGroups[i].buildGeo(myTri,vertexes);
            if (myGroups[i].materialIndex!=-1) myMats[myGroups[i].materialIndex].setApperance(myGroups[i],baseURL);
            myGroups[i].updateRenderState();
        }

        for (int i=0;i<nNumJoints;i++)
            myJoints[i].processMe();

        invertWithParents();
    }

    private void loadInfo() {
        try {
            loadHeader();
            loadVertexes();
            loadTriangles();
            loadGroups();
            loadMaterials();
            loadFrames();
            // Should be done loading file here
        } catch (IOException e) {
            throw new JmeException("File blocked while loading" + e.getMessage());
        }
    }

    private void loadFrames() throws IOException {
        float multiplier=file.readFloat();
        speed=file.readFloat();   // Time till first frame is complete
        iTotalFrames=file.readInt();
        nNumJoints=file.readUnsignedShort();
        byte[] inName=new byte[32];
        myJoints=new MilkJoint[nNumJoints];
        Vector3f eulerVec=new Vector3f();
        Vector3f translation=new Vector3f();

        for (int i=0;i<nNumJoints;i++){
            myJoints[i]=new MilkJoint();
            myJoints[i].keyframeRot=new Quaternion[iTotalFrames];
            myJoints[i].keyframePos=new Vector3f[iTotalFrames];

            file.readByte();    //flags
            file.readFully(inName);
            myJoints[i].name = cutAtNull(inName);

            file.readFully(inName);
            String compare=cutAtNull(inName);

            myJoints[i].parentIndex=-1;
            for (int j=0;j<i;j++)
                if (myJoints[j].name.equals(compare)) myJoints[i].parentIndex=j;

            eulerVec.set(file.readFloat(),file.readFloat(),file.readFloat());
            translation.set(file.readFloat(),file.readFloat(),file.readFloat());
            myJoints[i].localRefMatrix.setEulerRot(eulerVec);
            myJoints[i].localRefMatrix.setTranslation(translation);
            int numRot=file.readUnsignedShort();
            int numPos=file.readUnsignedShort();
            int myTime;
            for (int j=0;j<numRot;j++){
                myTime=(int)(multiplier*file.readFloat()-1);
                eulerVec.set(file.readFloat(),file.readFloat(),file.readFloat());
                if (myTime >= myJoints[i].keyframeRot.length) continue;
                myJoints[i].keyframeRot[myTime]=new Quaternion();
                myJoints[i].keyframeRot[myTime].fromAngles(new float[]{eulerVec.x,eulerVec.y,eulerVec.z});
            }
            for (int j=0;j<numPos;j++){
                myTime=(int)(multiplier*file.readFloat()-1);
                if (myTime >= myJoints[i].keyframeRot.length){
                    file.skipBytes(12);
                    continue;
                }
                myJoints[i].keyframePos[myTime]=new Vector3f(
                        file.readFloat(),
                        file.readFloat(),
                        file.readFloat()
                );
            }
        }
    }

    private void loadMaterials() throws IOException {
        nNumMaterials=file.readUnsignedShort();
        myMats=new MilkshapeMaterial[nNumMaterials];
        byte[] inString=new byte[128];
        for (int i=0;i<nNumMaterials;i++){
            myMats[i]=new MilkshapeMaterial();
            file.readFully(inString,0,32);
            myMats[i].name=cutAtNull(inString);
            myMats[i].ambColor=new ColorRGBA(
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat()
                    );
            myMats[i].difColor=new ColorRGBA(
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat()
                    );
            myMats[i].specColor=new ColorRGBA(
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat()
                    );
            myMats[i].emisColor=new ColorRGBA(
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat(),
                    file.readFloat()
                    );
            myMats[i].shininess=file.readFloat();
            myMats[i].transparency=file.readFloat();
            file.readByte();    // Unknown mode flag
            file.readFully(inString);
            myMats[i].texture=cutAtNull(inString);
            file.readFully(inString);
            myMats[i].alphaMap=cutAtNull(inString);
        }

    }

    private static String cutAtNull(byte[] inString) {
        for (int i=0;i<inString.length;i++)
            if (inString[i]==0) return new String(inString,0,i);
        return new String(inString);
    }

    private void loadGroups() throws IOException {
        nNumGroups=file.readUnsignedShort();
        myGroups=new MilkshapeGroup[nNumGroups];
        byte[] inName=new byte[32];
        for (int i=0;i<nNumGroups;i++){
            file.readByte();
            file.readFully(inName);
            myGroups[i]=new MilkshapeGroup(cutAtNull(inName));
            myGroups[i].numTriangles=file.readUnsignedShort();
            myGroups[i].triangleIndices=new int[myGroups[i].numTriangles];
            for (int j=0;j<myGroups[i].numTriangles;j++){
                myGroups[i].triangleIndices[j]=file.readUnsignedShort();
            }
            myGroups[i].materialIndex=file.readByte();
        }
    }

    private void loadTriangles() throws IOException {
        nNumTriangles=file.readUnsignedShort();
        myTri=new MilkshapeTriangle[nNumTriangles];
        vertexIndextoGroup=new int[nNumTriangles];

        for (int i=0;i<nNumTriangles;i++){
            myTri[i]=new MilkshapeTriangle();
            file.readUnsignedShort(); // flags
            myTri[i].vertexIndices[0]=file.readUnsignedShort();
            myTri[i].vertexIndices[1]=file.readUnsignedShort();
            myTri[i].vertexIndices[2]=file.readUnsignedShort();
            for (int j=0;j<3;j++){
                myTri[i].vertexNormals[j].x=file.readFloat();
                myTri[i].vertexNormals[j].y=file.readFloat();
                myTri[i].vertexNormals[j].z=file.readFloat();
            }
            for (int j=0;j<3;j++)
                myTri[i].texCoords[j].x=file.readFloat();
            for (int j=0;j<3;j++)
                myTri[i].texCoords[j].y=1-file.readFloat();
            file.readUnsignedByte(); // Smoothing group
            vertexIndextoGroup[i]=file.readUnsignedByte();
        }
    }

    private void loadVertexes() throws IOException{
        nNumVertices=file.readUnsignedShort();
        vertexes=new Vector3f[nNumVertices];
        boneID=new byte[nNumVertices];

        for (int i=0;i<nNumVertices;i++){
            file.readByte();    // flags
            vertexes[i]=new Vector3f(
                file.readFloat(),
                file.readFloat(),
                file.readFloat()
            );
            boneID[i]=file.readByte();
            file.readUnsignedByte();   // unknown use
        }

    }

    private void loadHeader() throws IOException {
        byte [] b=new byte[10];
        file.readFully(b);
        if (!new String(b).equals("MS3D000000")) throw new JmeException("Not MS format");
        if (file.readInt()!=4) throw new JmeException("I only know milkshape format 4");
    }

    public MilkFile spawnCopy() {
        MilkFile toReturn=new MilkFile();
        toReturn.file=null;
        toReturn.nNumVertices=this.nNumVertices;
        toReturn.myTri=this.myTri;
        toReturn.nNumTriangles=this.nNumTriangles;
        toReturn.boneID=this.boneID;
        toReturn.vertexes=this.vertexes;
        toReturn.vertexIndextoGroup=this.vertexIndextoGroup;
        toReturn.nNumMaterials=this.nNumMaterials;
        toReturn.myMats=this.myMats;
        toReturn.iTotalFrames=this.iTotalFrames;
        toReturn.nNumJoints=this.nNumJoints;
        toReturn.myJoints=this.myJoints;
        toReturn.speed=this.speed;

        toReturn.nNumGroups=this.nNumGroups;

//        toReturn.myGroups != this.myGroups; // Groups are diffrent!!
        toReturn.myGroups=new MilkshapeGroup[nNumGroups];
        for (int i=0;i<toReturn.nNumGroups;i++){
            toReturn.myGroups[i]=new MilkshapeGroup(myGroups[i]);
            if (myGroups[i].materialIndex!=-1) myMats[myGroups[i].materialIndex].setApperance(toReturn.myGroups[i],baseURL);
            toReturn.myGroups[i].setModelBound(myGroups[i].getModelBound());
        }
        return toReturn;
    }
}