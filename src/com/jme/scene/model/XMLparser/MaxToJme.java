package com.jme.scene.model.XMLparser;

import com.jme.util.LittleEndien;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.scene.TriMesh;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Stack;

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
    private long totalSize;
    Stack s=new Stack();

    public void convert(InputStream max,OutputStream bin) throws IOException {
        s.clear();
        myIn=new LittleEndien(max);
        Chunk mainPart=readChunk();
        if (mainPart.type!=MAIN_3DS){
            throw new IOException("Header doesn't match.  Probably not a 3ds file");
        }
        mainPart.length-=6;
        s.push(new Node("3ds scene"));
        readFile(mainPart.length);
        Node totalScene=(Node) s.pop();
    }

    private void readFile(int length) throws IOException {
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readFile chunk ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch(i.type){
                case M3D_VERSION:
                    readVersion();
                    break;
                case EDIT_3DS:
                    readEditableObject(i.length);
                    break;
                case KEYFRAMES:
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readFile:" + length);
        }
    }

    private void readEditableObject(int length) throws IOException {
        s.push(new Node("3ds editable object"));
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in editable object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case MESH_VERSION:
                    readMeshVersion();
                    break;
                case MASTER_SCALE:
                    readMasterScale();
                    break;
                case NAMED_OBJECT:
                    readNamedObject(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readEditableObject:" + length);
        }
        Node parentNode=(Node) s.pop();
        Node finishedNode=(Node) s.pop();
        parentNode.attachChild(finishedNode);
        s.push(parentNode);
    }

    private void readNamedObject(int length) throws IOException {
        String name=readcStr();
        length-=name.length()+1;
        s.push(new Node(name));
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in named object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case OBJ_TRIMESH:
                    readTriMesh(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readNamedObject:" + length);
        }
        Node finishedNode=(Node) s.pop();
        Node parentNode=(Node) s.pop();
        parentNode.attachChild(finishedNode);
        s.push(parentNode);
    }

    private void readTriMesh(int length) throws IOException {
        TriMesh me=new TriMesh("Mesh Object");
        s.push(me);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in TriMesh object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case VERTEX_LIST:
                    readVerts();
                    break;
                case TEXT_COORDS:
                    readTexCoords();
                    break;
                case COORD_SYS:
                    readCoordSystem();
                    break;
                case FACES_ARRAY:
                    readFaces(length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readTriMesh:" + length);
        }
        TriMesh finishedMesh=(TriMesh) s.pop();
        Node parentNode=(Node) s.pop();
        parentNode.attachChild(finishedMesh);
        s.push(parentNode);
    }

    private void readMeshVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG) System.out.println("Mesh version:" + i);

    }

    private void readScaleTrack() throws IOException {
        if (DEBUG) System.out.println("Reading scale track tags");
        short flags=myIn.readShort();
        if (DEBUG) System.out.println("flags:" + flags);
        long unknown=myIn.readLong();
        if (DEBUG) System.out.println("Unknown:" + unknown);
        int numKeys=myIn.readInt(); // todo: Conflicting reports here
        if (DEBUG) System.out.println("Number of keys:" + numKeys);
        for (int i=0;i<numKeys;i++){
            short frameNum=myIn.readShort();
            Vector3f totScale=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("Scale for frame " + frameNum + " is " + totScale);
        }
    }

    private void readRotTrackTag() throws IOException{
        if (DEBUG) System.out.println("Reading rotation track tags");
        short flags=myIn.readShort();
        if (DEBUG) System.out.println("flags:" + flags);
        long unknown=myIn.readLong();
        if (DEBUG) System.out.println("Unknown:" + unknown);
        int numKeys=myIn.readInt(); // todo: Conflicting reports here
        if (DEBUG) System.out.println("Number of keys:" + numKeys);
        for (int i=0;i<numKeys;i++){
            short frameNum=myIn.readShort();
            if (DEBUG) System.out.println("Unknown:" + myIn.readInt());
            float rot=myIn.readFloat();
            Vector3f axis=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("Rotation for frame " + frameNum + " about " + axis + " of rad " + rot);
        }
    }

    private void readKeyframeHeader() throws IOException {
        if (DEBUG) System.out.println("Reading keyframe header");
        short revision=myIn.readShort();
        if (DEBUG) System.out.println("Revision:" + revision);
        String fileName=readcStr();
        if (DEBUG) System.out.println("Filename:"+ fileName);
        short animLen=myIn.readShort();
        if (DEBUG) System.out.println("Animation length:"+ animLen);

    }

    private void readKeyframeStart() {
        if (DEBUG) System.out.println("Starting to read keyframe information");
    }

    private void readSmoothing(short nFaces) throws IOException{
        if (DEBUG) System.out.println("Reading smoothing");
        for (int i=0;i<nFaces;i++){
            short part=myIn.readShort();
            if (DEBUG) System.out.println("Smoothing group for face " + i + " is " + part);
            part=myIn.readShort();
            if (DEBUG) System.out.println("Smoothing group for face " + i + " is " + part);

        }
    }

    private void readFaces(int length) throws IOException{

        if (DEBUG) System.out.println("Reading faces");
        short nFaces=myIn.readShort();
        if (DEBUG) System.out.println("nFaces:" + nFaces);
        int[] indexes=new int[nFaces*3];
        for (int i=0;i<nFaces;i++){
            short[] parts=new short[3];
            for (int j=0;j<3;j++){
                parts[j]=myIn.readShort();
                indexes[i*3+j]=parts[j];
            }
            short flag=myIn.readShort();
            System.out.println("Read vertex indexes:" + parts[0] + " , " + parts[1] + " , " + parts[2] + ": with flag:" + flag);
        }
        length -= 2 + nFaces*(3*2+2);
        TriMesh parentMesh=(TriMesh) s.pop();
        parentMesh.setIndices(indexes);
        s.push(parentMesh);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in faces object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case SMOOTH_GROUP:
                    readSmoothing(nFaces);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readNamedObject:" + length);
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
        TriMesh parentMesh=(TriMesh) s.pop();
        parentMesh.setTextures(verts);
        s.push(parentMesh);
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
        TriMesh parentMesh=(TriMesh) s.pop();
        parentMesh.setVertices(verts);
        s.push(parentMesh);
    }

    private void readMesh() throws IOException{
        s.push(new TriMesh((String) s.pop()));
        if (DEBUG) System.out.println("Mesh read");
    }

    private String readcStr() throws IOException {
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
        String theName=new String(name);
        if (DEBUG) System.out.println("read string:" + theName + "*");
        return theName;
    }

    private void readMasterScale() throws IOException {
        float scale=myIn.readFloat();
        if (DEBUG) System.out.println("Master scale:" + scale);

    }

    private void readVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG) System.out.println("Version:" + i);
    }

    private Chunk readChunk() throws IOException {
        return new Chunk(myIn.readUnsignedShort(),myIn.readInt());
    }

    class Chunk{
        Chunk(int t,int l){
            type=t;
            length=l;
        }
        int type;
        int length;
    }
}