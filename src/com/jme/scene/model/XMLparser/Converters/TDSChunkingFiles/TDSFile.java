package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;



import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;

import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Started Date: Jul 2, 2004<br><br>
 * 
 * type=4d4d=MAIN_3DS
 * parent=nothing
 * @author Jack Lindamood
 */
public class TDSFile extends ChunkerClass{
    EditableObjectChunk objects=null;
    KeyframeChunk keyframes=null;
    ArrayList spatialNodes;

    public TDSFile(DataInput myIn) throws IOException {
        super(myIn);
        ChunkHeader c=new ChunkHeader(myIn);
        if (c.type!=MAIN_3DS)
            throw new IOException("Header doesn't match 0x4D4D; Header=" + Integer.toHexString(c.type));
        c.length-=6;
        setHeader(c);

        chunk();
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch(i.type){
            case TDS_VERSION:
                readVersion();
                return true;
            case EDIT_3DS:
                objects=new EditableObjectChunk(myIn,i);
                return true;
            case KEYFRAMES:
                keyframes=new KeyframeChunk(myIn,i);
                return true;
            default:
                return false;
            }
    }


    private void readVersion() throws IOException{
        int version=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Version:" + version);
    }

    public Node buildScene() throws IOException {
        buildObject();
        Node uberNode=new Node("TDS Scene");
        for (int i=0;i<spatialNodes.size();i++)
            uberNode.attachChild((Spatial) spatialNodes.get(i));
        return uberNode;
    }


    private void buildObject() throws IOException {
        spatialNodes=new ArrayList();   // An ArrayList of Nodes
        Iterator i=objects.namedObjects.keySet().iterator();
        while (i.hasNext()){
            String objectKey=(String) i.next();
            Node parentNode=new Node(objectKey);
            NamedObjectChunk noc=(NamedObjectChunk) objects.namedObjects.get(objectKey);
            if (noc.whatIAm instanceof TriMeshChunk){
                putChildMeshes(parentNode,(TriMeshChunk) noc.whatIAm);
            }
            spatialNodes.add(parentNode);
        }

    }

    private void putChildMeshes(Node parentNode, TriMeshChunk whatIAm) throws IOException {
        FacesChunk myFace=whatIAm.face;
        boolean[] faceHasMaterial=new boolean[myFace.nFaces];
        int noMaterialCount=myFace.nFaces;
        for (int i=0;i<myFace.materialIndexes.size();i++){
            int[] faceIndexes=(int[])myFace.materialIndexes.get(i);
            String matName=(String) myFace.materialNames.get(i);
            if (faceIndexes.length!=0){
                TriMesh part=new TriMesh(parentNode.getName()+i);
                part.setVertices(whatIAm.vertexes);
                part.setNormals(whatIAm.normals);
                part.setTextures(whatIAm.texCoords);
                int[] newIndexArray=new int[faceIndexes.length*3];
                for (int k=0;k<faceIndexes.length;k++){
                    if (false==faceHasMaterial[k]) noMaterialCount--;
                    faceHasMaterial[k]=true;

                    newIndexArray[k*3+0]=myFace.faces[faceIndexes[k]][0];
                    newIndexArray[k*3+1]=myFace.faces[faceIndexes[k]][1];
                    newIndexArray[k*3+2]=myFace.faces[faceIndexes[k]][2];
                }
                part.setIndices(newIndexArray);
                MaterialBlock myMaterials=(MaterialBlock) objects.materialBlocks.get(matName);
                if (matName==null)
                    throw new IOException("Couldn't find the correct name of " + myMaterials);
                if (myMaterials.myMatState.isEnabled())
                    part.setRenderState(myMaterials.myMatState);
                if (myMaterials.myTexState.isEnabled())
                    part.setRenderState(myMaterials.myTexState);
                if (myMaterials.myWireState.isEnabled())
                    part.setRenderState(myMaterials.myWireState);
                parentNode.attachChild(part);
            }
        }
        if (noMaterialCount!=0){    // attach materialless parts
            int[] noMaterialIndexes=new int[noMaterialCount*3];
            int partCount=0;
            for (int i=0;i<whatIAm.face.nFaces;i++){
                if (!faceHasMaterial[i]){
                    noMaterialIndexes[partCount++]=myFace.faces[i][0];
                    noMaterialIndexes[partCount++]=myFace.faces[i][1];
                    noMaterialIndexes[partCount++]=myFace.faces[i][2];
                }
            }
            TriMesh noMaterials=new TriMesh(parentNode.getName()+"-1");
            noMaterials.setVertices(whatIAm.vertexes);
            noMaterials.setIndices(noMaterialIndexes);
            parentNode.attachChild(noMaterials);
        }
    }
}