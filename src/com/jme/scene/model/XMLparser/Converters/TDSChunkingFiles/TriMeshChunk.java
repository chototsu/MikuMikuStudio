package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Matrix3f;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * type == OBJ_TRIMESH == 4100<br>
 * parent ==  NAMED_OBJECT == 0x4000<br>
 *
 * @author Jack Lindamood
 */
public class TriMeshChunk extends ChunkerClass {

    Vector3f[] vertexes;
    Vector2f[] texCoords;
    Vector3f[] normals;
    Vector3f[] faceNormals;

    Matrix3f rotation;
    Vector3f origin;
    private byte color;
    FacesChunk face;

    public TriMeshChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case VERTEX_LIST:
                readVerts();
                return true;
            case TEXT_COORDS:
                readTexCoords();
                return true;
            case COORD_SYS:
                readCoordSystem();
                return true;
            case FACES_ARRAY:
                if (face!=null)
                    throw new IOException("Face already non-null... ut ow");
                face=new FacesChunk(myIn,i);
                calculateFaceNormals();
                return true;
            case VERTEX_OPTIONS:
                readOptions();
                return true;
            case MESH_COLOR:
                readMeshColor();
                return true;
            case MESH_TEXTURE_INFO:
                readMeshTextureInfo();
                return true;
            default:
                return false;
            }
    }

    private void calculateFaceNormals() {
        faceNormals=new Vector3f[face.nFaces];
        Vector3f tempa=new Vector3f(),tempb=new Vector3f();
        // Face normals
        for (int i=0;i<face.nFaces;i++){
            tempa.set(vertexes[face.faces[i][0]]);  // tempa=a
            tempa.subtractLocal(vertexes[face.faces[i][1]]);    // tempa-=b (tempa=a-b)
            tempb.set(vertexes[face.faces[i][0]]);  // tempb=a
            tempb.subtractLocal(vertexes[face.faces[i][2]]);    // tempb-=c (tempb=a-c)
            faceNormals[i]=tempa.cross(tempb).normalizeLocal();
        }
        Vector3f sum=new Vector3f();
        Vector3f[] smoothNormals=new Vector3f[face.nFaces];
        for (int i=0;i<face.nFaces;i++){
            smoothNormals[i]=new Vector3f();
            if (face.smoothingGroups[i]==0){
                smoothNormals[i].set(faceNormals[i]);
                continue;
            }
            sum.set(faceNormals[i]);
            for (int j=0;j<face.nFaces;j++){
                if (i==j) continue;
                if ((face.smoothingGroups[i]&face.smoothingGroups[j])==0) continue;
                boolean shareFlag=false;
                for (int k=0;k<3 && shareFlag==false;k++)
                    for (int l=0;l<3 && shareFlag==false;l++){
                        if (face.faces[i][k]==face.faces[j][l])
                            shareFlag=true;
                    }
                if (shareFlag) sum.addLocal(faceNormals[j]);
            }
            smoothNormals[i].set(sum.normalizeLocal());
        }

        normals=new Vector3f[vertexes.length];
        for (int i=0;i<vertexes.length;i++){
            // What faces does this vertex belong to?
            sum.set(0,0,0);
            for (int j=0;j<face.nFaces;j++){
                for (int k=0;k<3;k++)
                    if (face.faces[j][k]==i){
                        sum.addLocal(smoothNormals[j]);
                    }
            }
            normals[i]=sum.normalize();
        }
    }

    private void readMeshTextureInfo() throws IOException {
        // currently no idea what this information means, but its here in case I figure it out
        short type=myIn.readShort();
        float xTiling=myIn.readFloat();
        float yTiling=myIn.readFloat();;
        float Xicon=myIn.readFloat();
        float Yicon=myIn.readFloat();
        float Zicon=myIn.readFloat();
        float matrix[][]=new float[4][3];
        for (int i=0;i<4;i++)
            for (int j=0;j<3;j++)
                matrix[i][j]=myIn.readFloat();
        float scaling=myIn.readFloat();
        float planIconW=myIn.readFloat();
        float planIconH=myIn.readFloat();
        float cylIconH=myIn.readFloat();;
    }

    private void readMeshColor() throws IOException {
        color=myIn.readByte();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Mesh color read as " + color);
    }

    private void readOptions() throws IOException {
        int numOptions=myIn.readUnsignedShort();
        for (int i=0;i<numOptions;i++){
            short option=myIn.readShort();
        }
        if (DEBUG || DEBUG_LIGHT) System.out.println("Options read");
    }

    private void readCoordSystem() throws IOException {
        float[] parts=new float[9];
        rotation=new Matrix3f();
        for (int i=0;i<9;i++)
            parts[i]=myIn.readFloat();
        rotation.set(parts);
        origin=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private void readTexCoords() throws IOException {
        texCoords=new Vector2f[myIn.readUnsignedShort()];
        for (int i=0;i<texCoords.length;i++){
            texCoords[i]=new Vector2f(myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("TX#"+i+'='+texCoords[i]);
        }
    }

    private void readVerts() throws IOException {
        vertexes=new Vector3f[myIn.readUnsignedShort()];
        for (int i=0;i<vertexes.length;i++){
            vertexes[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("V#"+i+'='+vertexes[i]);
        }

    }
}