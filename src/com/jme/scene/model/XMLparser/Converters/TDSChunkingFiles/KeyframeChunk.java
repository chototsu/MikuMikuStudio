package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class KeyframeChunk extends ChunkerClass{
    public KeyframeChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
/*
            case KEYFRAME_HEAD:
                readKeyframeHeader();
                return true;
            case KEYFRAME_OBJ:
                readKeyframeObj(i.length);
                return true;
            case KEY_SEGMENT:
                readSegment();
                return true;
            case KEY_CURTIME:
                readCurTime();
                return true;
            case VIEWPORT_LAYOUT:
                readViewLayout(i.length);
                return true;
            case CAMERA_TARG_INF_TAG:
                readCamTargetInfoTag(i.length);
                return true;
            case CAMERA_INFO_TAG:
                readCamInfoTag(i.length);
                return true;
            case KEY_OMNI_LI_INFO:
                readOmniLightKeyframeInfo(i.length);
                return true;
            case KEY_AMBIENT_NODE:
                readAmbientNodeKeyframeInfo(i.length);
                return true;
            case KEY_SPOT_TARGET:
                readSpotLightTarget(i.length);
                return true;
            case KEY_SPOT_INFO:
                readKeySpotLightInfo(i.length);
                return true;
*/
            default:
                return false;
        }
    }
}
