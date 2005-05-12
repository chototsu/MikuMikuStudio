package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * Parent == TDSFile == 0x4d4d<br>
 * type ==  KEYFRAMES == 0xb000<br>
 *
 * @author Jack Lindamood
 */
class KeyframeChunk extends ChunkerClass{
    public KeyframeChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    int animationLen;
    int begin;
    int end;
    //ArrayList objKeyframes;
    HashMap objKeyframes;
    ArrayList cameraKeyframes;
    ArrayList lightKeyframes;

    protected void initializeVariables() throws IOException {
        objKeyframes=new HashMap ();
        cameraKeyframes=new ArrayList();
        lightKeyframes=new ArrayList();
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case KEY_HEADER:
                readKeyframeHeader();
                return true;
            case KEY_SEGMENT:
                readSegment();
                return true;
            case KEY_CURTIME:
                readCurTime();
                return true;
            case KEY_VIEWPORT:
                skipSize(i.length); // Ignore changing viewports, not relevant
                return true;
            case KEY_OBJECT:
                KeyframeInfoChunk it=new KeyframeInfoChunk(myIn,i);
                objKeyframes.put(it.name,it);
//                objKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            case KEY_CAM_TARGET:
            case KEY_CAMERA_OBJECT:
                cameraKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            case KEY_OMNI_LI_INFO:
            case KEY_AMB_LI_INFO:
            case KEY_SPOT_TARGET:
            case KEY_SPOT_OBJECT:
                lightKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            default:
                return false;
        }
    }

    private void readSegment() throws IOException {
        begin=myIn.readInt();
        end=myIn.readInt();
        if (DEBUG_LIGHT) System.out.println("Reading segment");
        if (DEBUG) System.out.println("Segment begins at " + begin + " and ends at " + end);
    }

    private void readCurTime() throws IOException {
        int curFrame=myIn.readInt();
        if (DEBUG) System.out.println("Current frame is " + curFrame);
    }

    private void readKeyframeHeader() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading keyframeHeader");
        short revision=myIn.readShort();
        String flname=readcStr();
        animationLen=myIn.readInt();
        if (DEBUG) System.out.println("Revision #" + revision + " with filename " + flname + " and animation len " + animationLen);
    }
}
