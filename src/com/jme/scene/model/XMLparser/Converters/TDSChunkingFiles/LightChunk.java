package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

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
    ColorRGBA lightColor;
    float outterRange;
    float innerRange;
    float mult;
    SpotLightChunk spotInfo;

    protected void initializeVariables() throws IOException {
        myLoc=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        decrHeaderLen(4*3);
        mult=1;
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case COLOR_FLOAT:
                lightColor=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                if (DEBUG) System.out.println("Light color:"+lightColor);
                return true;
            case LIGHT_OUT_RANGE:
                readOuterLightRange();
                return true;
            case LIGHT_IN_RANGE:
                readInnerLightRange();
                return true;
            case LIGHT_MULTIPLIER:
                readLightMultiplier();
                return true;
            case LIGHT_SPOTLIGHT:
                if (spotInfo!=null)
                    throw new IOException("logic error... spotInfo not null");
                spotInfo = new SpotLightChunk(myIn,i);
                return true;
            default:
                return false;
            }
    }

    private void readLightMultiplier() throws IOException {
        mult=myIn.readFloat();
    }

    private void readInnerLightRange() throws IOException {
        innerRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Inner range:" + innerRange);
    }

    private void readOuterLightRange() throws IOException {
        outterRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Outter range:" + outterRange);
    }
}
