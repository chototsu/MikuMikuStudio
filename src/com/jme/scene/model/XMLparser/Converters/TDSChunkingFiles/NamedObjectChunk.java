package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class NamedObjectChunk extends ChunkerClass{
    String name;
    public NamedObjectChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected void initializeVariables() throws IOException {
        name=readcStrAndDecrHeader();
        if (DEBUG) System.out.println("Editable object name="+name);
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        return false;
    }
}
