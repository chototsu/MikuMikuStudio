package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.IOException;
import java.io.DataInput;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * parent == 3d3d == EDIT_3DS
 * type == 2300 == DISTANCE_QUEUE
 *
 * @author Jack Lindamood
 */
class DistanceQueueChunk extends ChunkerClass{
    boolean activeDistanceQueue;
    float nearPlane;
    float nearDensity;
    float farPlane;
    float farDensity;

    public DistanceQueueChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables() throws IOException{
        nearPlane=myIn.readFloat();
        nearDensity=myIn.readFloat();
        farPlane=myIn.readFloat();
        farDensity=myIn.readFloat();
        if (DEBUG)
            System.out.println("@distanceQueue nearPlane:"+nearPlane+" nearDensity:"+
                    nearDensity+" farPlane"+farPlane+" farDensity"+farDensity);
        decrHeaderLen(4*4);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case DQUEUE_BACKGRND:
                activeDistanceQueue=true;
                if (DEBUG) System.out.println("Use distanceQueue true");
                return true;
            default:
                return false;
        }
    }
}
