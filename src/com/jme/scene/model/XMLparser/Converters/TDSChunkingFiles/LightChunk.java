package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent ==  NAMED_OBJECT == 0x4000<br>
 * type == LIGHT_OBJ == 0x4600<br>
 *
 * @author Jack Lindamood
 */
public class LightChunk extends ChunkerClass {
    public LightChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    Vector3f myLoc;

    protected void initializeVariables() throws IOException {
        myLoc=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        decrHeaderLen(4*3);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        return false;
    }
}
