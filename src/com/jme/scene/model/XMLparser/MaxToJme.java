package com.jme.scene.model.XMLparser;

import com.jme.util.LittleEndien;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * Converts .3ds files into jME binary
 *
 * @author Jack Lindamood
 */
public class MaxToJme implements MaxChunkIDs{
    LittleEndien myIn;
    private static boolean DEBUG=true;

    public void convert(InputStream max,OutputStream bin) throws IOException {
        myIn=new LittleEndien(max);
        readHeader();
        while (anotherChunk());
    }

    private void readHeader() throws IOException {
        short i=myIn.readShort();
        if (i!=MAIN_3DS){
            throw new IOException("probably not a max file chunk was:" + i);
        }
        int length=myIn.readInt();
        if (DEBUG) System.out.println("Lenght:" +  length);
    }

    private boolean anotherChunk() throws IOException {
        short i=myIn.readShort();
        long length=myIn.readInt();
        if (DEBUG) System.out.println("Read chunk ID:" + Integer.toHexString(i) + "* with known length " + length);
        switch (i){
            case M3D_VERSION:
                readVersion();
                return true;
            case NULL_CHUNK:
                return true;
            case MASTER_SCALE:
                readScale();
                return true;
            case NAMED_OBJECT:
                readcStr();
                return true;
            case OBJ_TRIMESH:
                readMesh();
                return true;
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
                readFaces();
                return true;
        }
        return false;
    }

    private void readFaces() throws IOException{
        if (DEBUG) System.out.println("Reading faces");
        short nFaces=myIn.readShort();
        if (DEBUG) System.out.println("nFaces:" + nFaces);
        for (int i=0;i<nFaces;i++){
            short[] parts=new short[3];
            for (int j=0;j<3;j++){
                parts[j]=myIn.readShort();
            }
            short flag=myIn.readShort();
            System.out.println("Read vertex indexes:" + parts[0] + " , " + parts[1] + " , " + parts[2] + ": with flag:" + flag);
        }
    }

    private void readCoordSystem() throws IOException{
        if (DEBUG) System.out.println("reading local coords");
        Vector3f[] parts=new Vector3f[4];
        parts[0]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("X:" + parts[0]);
        parts[1]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("Y:" + parts[1]);
        parts[2]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("Z:" + parts[2]);
        parts[3]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("Origin:" + parts[3]);
    }

    private void readTexCoords() throws IOException{
        if (DEBUG) System.out.println("Reading texCoords");
        short nPoints=myIn.readShort();
        if (DEBUG) System.out.println("NumPoints:"+ nPoints);
        Vector2f[] verts=new Vector2f[nPoints];
        for (int i=0;i<nPoints;i++){
            verts[i]=new Vector2f(myIn.readFloat(),myIn.readFloat());
            System.out.println("Reading vert:" + verts[i]);
        }

    }

    private void readVerts() throws IOException{
        if (DEBUG) System.out.println("Verts read");
        short nPoints=myIn.readShort();
        if (DEBUG) System.out.println("NumPoints:"+ nPoints);
        Vector3f[] verts=new Vector3f[nPoints];
        for (int i=0;i<nPoints;i++){
            verts[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            System.out.println("Reading vert:" + verts[i]);
        }
    }

    private void readMesh() throws IOException{
        if (DEBUG) System.out.println("Mesh read");
    }

    private void readcStr() throws IOException {
        ArrayList byteArray=new ArrayList(16);
        byte inByte=myIn.readByte();
        while (inByte!=0){
            byteArray.add(new Byte(inByte));
            inByte=myIn.readByte();
        }
        Object [] parts=byteArray.toArray();
        byte[] name=new byte[parts.length];
        for (int i=0;i<parts.length;i++){
            name[i]=((Byte)parts[i]).byteValue();
        }
        String s=new String(name);
        if (DEBUG) System.out.println("read string:" + s + "*");
    }

    private void readScale() throws IOException {
        float scale=myIn.readFloat();
        if (DEBUG) System.out.println("Master scale:" + scale);

    }

    private void readVersion() throws IOException {
        short i=myIn.readShort();
        if (DEBUG) System.out.println("Version:" + i);
    }
}