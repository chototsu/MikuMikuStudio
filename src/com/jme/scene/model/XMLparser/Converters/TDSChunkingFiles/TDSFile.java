package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;



import com.jme.scene.Node;

import java.io.IOException;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.HashMap;

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

    public Node buildScene() {
        buildObject();
        return null;
    }

    private void buildObject() {
/*
        spatialNodes=new ArrayList();
        Object[] parts=objects.namedObjects.toArray();
        for (int i=0;i<parts.length;i++){   // for each named object
            Node temp=new Node(((NamedObjectChunk)parts[i]).name);
            Object[] TriMeshChunks=((NamedObjectChunk)parts[i]).meshList.toArray();
            HashMap q;
//            q.put(key,value);
            for (int j=0;j<TriMeshChunks.length;j++){   // go thru each meshList in that named object

            }
        }*/
    }

}