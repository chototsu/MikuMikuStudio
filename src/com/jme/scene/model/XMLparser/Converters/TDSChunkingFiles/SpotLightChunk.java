package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * type == LIGHT_SPOTLIGHT == 0x4610<br>
 * parent == LIGHT_OBJ == 0x4600<br>
 *
 * @author Jack Lindamood
 */
class SpotLightChunk extends ChunkerClass{
    Vector3f target;
    float hotSpot;
    float fallOff;
    boolean shadowed;
    float roll;
    short shadowSize;
    float lightBias;
    float filter;
    float shadowBias;

    public SpotLightChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() throws IOException {
        target=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        hotSpot=myIn.readFloat();
        fallOff=myIn.readFloat();
        decrHeaderLen(4*5);
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case LIGHT_SPOT_ROLL:
                readSpotlightRollAngles();
                return true;
            case LIGHT_SPOT_SHADOWED:
                shadowed=true;
                return true;
            case LIGHT_SPOT_BIAS:
                readLightBias();
                return true;
            case LIGHT_LOC_SHADOW:
                readLightShadow();
                return true;
            case LIGHT_SEE_CONE:
                return true;    // A visable cone is ignored
            case LIGHT_SPOT_OVERSHOOT:
                return true;    // Overshoot ignored
            default:
                return false;
        }
    }

    private void readLightShadow() throws IOException {
        shadowBias=myIn.readFloat();
        filter=myIn.readFloat();
        shadowSize=myIn.readShort();
    }

    private void readLightBias() throws IOException {
        lightBias=myIn.readFloat();
    }

    private void readSpotlightRollAngles() throws IOException {
        roll=myIn.readFloat();
    }
}
