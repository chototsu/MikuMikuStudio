package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent ==  NAMED_OBJECT == 0x4000<br>
 * type == CAMERA_FLAG == 4700 <br>
 * 
 * @author Jack Lindamood
 */
public class CameraChunk extends ChunkerClass {
    public CameraChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        return false;
    }
}
