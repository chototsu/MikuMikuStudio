package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;



import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;
import com.jme.scene.model.Face;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

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
        ArrayList normals=new ArrayList();
        ArrayList indexes=new ArrayList();
        ArrayList vertexes=new ArrayList();
        Vector3f tempNormal=new Vector3f();
        ArrayList texCoords=new ArrayList();
        for (int i=0;i<myFace.materialIndexes.size();i++){  // For every original material
            String matName=(String) myFace.materialNames.get(i);
            int[] appliedFacesIndexes=(int[])myFace.materialIndexes.get(i);
            if (appliedFacesIndexes.length!=0){ // If it's got something make a new trimesh for it
                TriMesh part=new TriMesh(parentNode.getName()+i);
                normals.clear();
                indexes.clear();
                vertexes.clear();
                texCoords.clear();
                for (int j=0;j<appliedFacesIndexes.length;j++){ // Look thru every face in that new TriMesh
                    int actuallFace=appliedFacesIndexes[j];
                    if (faceHasMaterial[actuallFace]==false){
                        faceHasMaterial[actuallFace]=true;
                        noMaterialCount--;
                    }
                    for (int k=0;k<3;k++){                      //   and every vertex in that face
                        // what faces contain this vertex index? If they do and are in the same SG, average
                        int vertexIndex=myFace.faces[actuallFace][k];
                        tempNormal.set(whatIAm.faceNormals[actuallFace]);
                        calcFacesWithVertexAndSmoothGroup(whatIAm.faceNormals,myFace,tempNormal,vertexIndex,actuallFace);
                        // Now can I just index this Vertex/tempNormal combination?
                        int l=0;
                        Vector3f vertexValue=whatIAm.vertexes[vertexIndex];
                        for (l=0;l<normals.size();l++)
                            if (normals.get(l).equals(tempNormal) && vertexes.get(l).equals(vertexValue))
                                break;
                        if (l==normals.size()){ // if new
                            normals.add(new Vector3f(tempNormal));
                            vertexes.add(whatIAm.vertexes[vertexIndex]);
                            texCoords.add(whatIAm.texCoords[vertexIndex]);
                            indexes.add(new Integer(l));
                        } else{ // if old
                            indexes.add(new Integer(l));
                        }
                    }
                }
                part.setVertices((Vector3f[]) vertexes.toArray(new Vector3f[]{}));
                part.setNormals((Vector3f[]) normals.toArray(new Vector3f[]{}));
                part.setTextures((Vector2f[]) texCoords.toArray(new Vector2f[]{}));
                int[] intIndexes=new int[indexes.size()];
                for (int val=0;val<intIndexes.length;val++)
                    intIndexes[val]=((Integer)indexes.get(val)).intValue();
                part.setIndices(intIndexes);

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

    // Find all face normals for faces that contain that vertex AND are in that smoothing group.
    private void calcFacesWithVertexAndSmoothGroup(Vector3f[] faceNormals,FacesChunk myFace, Vector3f tempNormal, int vertexIndex, int faceIndex) {
        // tempNormal starts out with the face normal value
        int smoothingGroupValue=myFace.smoothingGroups[faceIndex];
        if (smoothingGroupValue==0) return; // 0 smoothing group values don't have smooth edges anywhere
        for (int i=0;i<myFace.nFaces;i++){
            if (i == faceIndex || (myFace.smoothingGroups[i]&smoothingGroupValue)==0)
                continue;
            for (int j=0;j<3;j++)
                if (myFace.faces[i][j]==vertexIndex){
                    tempNormal.addLocal(faceNormals[i]);
                    break;
                }
        }
        tempNormal.normalizeLocal();
    }
}