package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;


/**
 * Started Date: Jul 2, 2004<br><br>
 * 
 * @author Jack Lindamood
 */
public class ChunkHeader{
    public ChunkHeader(){}
    public int type;
    public int length;

//    public ChunkHeader(int type, int length) {
//        this.type = type;
//        this.length = length;
//    }

    public ChunkHeader(ChunkHeader i) {
        this.type=i.type;
        this.length=i.length;
    }

    public ChunkHeader(DataInput myIn) throws IOException {
        this.type=myIn.readUnsignedShort();
        this.length=myIn.readInt();
    }
}