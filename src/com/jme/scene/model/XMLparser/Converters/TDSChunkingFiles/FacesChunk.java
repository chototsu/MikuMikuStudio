package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent == OBJ_TRIMESH == 4100<br>
 * type == FACES_ARRAY == 4120<br>
 *
 * @author Jack Lindamood
 */
public class FacesChunk extends ChunkerClass{
    int nFaces;
    int[] indexes;
    int [] smoothingGroups;
    HashMap materialNamesToIndexes;

    public FacesChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() throws IOException {
        nFaces=myIn.readUnsignedShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading faces #=" + nFaces);
        indexes=new int[nFaces*3];
        smoothingGroups=new int[nFaces];
        materialNamesToIndexes=new HashMap();


        for (int i=0;i<nFaces;i++){
            for (int j=0;j<3;j++)
                indexes[i*3+j]=myIn.readShort();
            short flag=myIn.readShort();
        }
        decrHeaderLen(2 + nFaces*(3*2+2));
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case SMOOTH_GROUP:
                readSmoothing();
                return true;
            case MESH_MAT_GROUP:
                readMeshMaterialGroup();
                return true;
            default:
                return false;
            }
    }

    private void readMeshMaterialGroup() throws IOException {
        String name=readcStr();
        short numFace=myIn.readShort();
        int[] appliedFacesIndexes=new int[numFace];
        if (DEBUG || DEBUG_LIGHT) System.out.println("Material " + name + " is applied to " + numFace + " faces");
        for (int i=0;i<numFace;i++){
            appliedFacesIndexes[i]=myIn.readShort();
        }
        materialNamesToIndexes.put(name,appliedFacesIndexes);
    }

    private void readSmoothing() throws IOException {
        for (int i=0;i<nFaces;i++)
            smoothingGroups[i]=myIn.readInt();
    }
}
