package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;



import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
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
        ArrayList normals=new ArrayList(myFace.nFaces);
        ArrayList vertexes=new ArrayList(myFace.nFaces);
        Vector3f tempNormal=new Vector3f();
        ArrayList texCoords=new ArrayList(myFace.nFaces);

        // Precalculate nextTo[vertex][0...i] <--->
        // whatIAm.vertexes[vertex] is next to face nextTo[vertex][0] & nextTo[vertex][i]
        if (DEBUG || DEBUG_LIGHT) System.out.println("Precaching");
        int[] vertexCount=new int[whatIAm.vertexes.length];
        int vertexIndex;
        for (int i=0;i<myFace.nFaces;i++){
            for (int j=0;j<3;j++){
                vertexCount[myFace.faces[i][j]]++;
            }
        }
        int[][] realNextFaces=new int[whatIAm.vertexes.length][];
        for (int i=0;i<realNextFaces.length;i++)
            realNextFaces[i]=new int[vertexCount[i]];
        for (int i=0;i<myFace.nFaces;i++){
            for (int j=0;j<3;j++){
                vertexIndex=myFace.faces[i][j];
                realNextFaces[vertexIndex][--vertexCount[vertexIndex]]=i;
            }
        }


        if (DEBUG || DEBUG_LIGHT) System.out.println("Precaching done");



        int[] indexes=new int[myFace.nFaces*3];
        int curPosition;

        for (int i=0;i<myFace.materialIndexes.size();i++){  // For every original material
            String matName=(String) myFace.materialNames.get(i);
            int[] appliedFacesIndexes=(int[])myFace.materialIndexes.get(i);
            if (DEBUG_LIGHT || DEBUG) System.out.println("On material " + matName + " with " + appliedFacesIndexes.length + " faces.");
            if (appliedFacesIndexes.length!=0){ // If it's got something make a new trimesh for it
                TriMesh part=new TriMesh(parentNode.getName()+i);
                normals.clear();
                curPosition=0;
                vertexes.clear();
                texCoords.clear();
                for (int j=0;j<appliedFacesIndexes.length;j++){ // Look thru every face in that new TriMesh
                    if (DEBUG) if (j%500==0) System.out.println("Face:" + j);
                    int actuallFace=appliedFacesIndexes[j];
                    if (faceHasMaterial[actuallFace]==false){
                        faceHasMaterial[actuallFace]=true;
                        noMaterialCount--;
                    }
                    for (int k=0;k<3;k++){                      //   and every vertex in that face
                        // what faces contain this vertex index? If they do and are in the same SG, average
                        vertexIndex=myFace.faces[actuallFace][k];
                        tempNormal.set(whatIAm.faceNormals[actuallFace]);
                        calcFacesWithVertexAndSmoothGroup(realNextFaces[vertexIndex],whatIAm.faceNormals,myFace,tempNormal,actuallFace);
                        // Now can I just index this Vertex/tempNormal combination?
                        int l=0;
                        Vector3f vertexValue=whatIAm.vertexes[vertexIndex];
                        for (l=0;l<normals.size();l++)
                            if (normals.get(l).equals(tempNormal) && vertexes.get(l).equals(vertexValue))
                                break;
                        if (l==normals.size()){ // if new
                            normals.add(new Vector3f(tempNormal));
                            vertexes.add(whatIAm.vertexes[vertexIndex]);
                            if (whatIAm.texCoords!=null)
                                texCoords.add(whatIAm.texCoords[vertexIndex]);
                            indexes[curPosition++]=l;
                        } else { // if old
                            indexes[curPosition++]=l;
                        }
                    }
                }
                Vector3f[] newVerts=new Vector3f[vertexes.size()];
                for (int indexV=0;indexV<newVerts.length;indexV++)
                    newVerts[indexV]=(Vector3f) vertexes.get(indexV);
                part.setVertices(newVerts);
                part.setNormals((Vector3f[]) normals.toArray(new Vector3f[]{}));
                if (whatIAm.texCoords!=null) part.setTextures((Vector2f[]) texCoords.toArray(new Vector2f[]{}));
                int[] intIndexes=new int[curPosition];
                System.arraycopy(indexes,0,intIndexes,0,curPosition);
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
    private void calcFacesWithVertexAndSmoothGroup(int[] thisVertexTable,Vector3f[] faceNormals,FacesChunk myFace, Vector3f tempNormal, int faceIndex) {
        // tempNormal starts out with the face normal value
        int smoothingGroupValue=myFace.smoothingGroups[faceIndex];
        if (smoothingGroupValue==0)
            return; // 0 smoothing group values don't have smooth edges anywhere
        int arrayFace;
        for (int i=0;i<thisVertexTable.length;i++){
            arrayFace=thisVertexTable[i];
            if (arrayFace==faceIndex) continue;
            if ((myFace.smoothingGroups[arrayFace] & smoothingGroupValue)!=0 )
                tempNormal.addLocal(faceNormals[arrayFace]);
        }
        tempNormal.normalizeLocal();
    }
}