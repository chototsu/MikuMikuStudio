package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Matrix3f;
import com.jme.math.TransformMatrix;

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
class TriMeshChunk extends ChunkerClass {

    Vector3f[] vertexes;
    Vector2f[] texCoords;


    Matrix3f rotation;
    Vector3f origin;
    private byte color;
    FacesChunk face;
    TransformMatrix coordSystem;

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
        coordSystem=new TransformMatrix();
        rotation=new Matrix3f();
        for (int i=0;i<9;i++)
            parts[i]=myIn.readFloat();
        rotation.set(parts);
        rotation.transposeLocal();
        coordSystem.setRotation(rotation);
        origin=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        coordSystem.setTranslation(origin);
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