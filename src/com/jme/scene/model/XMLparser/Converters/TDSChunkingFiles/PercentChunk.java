package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.IOException;
import java.io.DataInput;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * type=0030,0031=PRCT_INT_FRMT,PRCT_FLT_FRMT
 * parent=global
 *
 * @author Jack Lindamood
 */
class PercentChunk extends ChunkerClass{
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
