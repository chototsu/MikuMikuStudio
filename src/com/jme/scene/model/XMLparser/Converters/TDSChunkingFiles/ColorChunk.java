package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * type == 0010-0014 == various<br>
 * Parent == Global<br>
 *
 * @author Jack Lindamood
 */
class ColorChunk extends ChunkerClass{

    ColorRGBA gamaColor;
    ColorRGBA regColor;

    public ColorChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case COLOR_BYTE:
                if (regColor==null) regColor=new ColorRGBA(myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,1);
                return true;
            case CLR_BYTE_GAMA:
                if (gamaColor==null) gamaColor=new ColorRGBA(myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,1);
                return true;
            case COLOR_FLOAT:
                if (regColor==null) regColor=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
                return true;
            case CLR_FLOAT_GAMA:
                if (gamaColor==null) gamaColor=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
                return true;
            default:
                return false;
        }
    }

    public ColorRGBA getBestColor() {
        if (regColor==null) return gamaColor; else return regColor;
    }
}
