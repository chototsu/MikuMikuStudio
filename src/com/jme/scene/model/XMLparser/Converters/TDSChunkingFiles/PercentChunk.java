package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.IOException;
import java.io.DataInput;

/**
 * Started Date: Jul 2, 2004<br><br>
 * 
 * @author Jack Lindamood
 */
public class PercentChunk extends ChunkerClass{
    public PercentChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    float percent;

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case PRCT_INT_FRMT:
                percent=myIn.readShort()/100f;
                return true;
            case PRCT_FLT_FRMT:
                percent=myIn.readFloat();
                return true;
            default:
                return false;
        }
    }
}
