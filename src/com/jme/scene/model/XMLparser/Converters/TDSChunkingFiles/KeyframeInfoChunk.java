package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * @author Jack Lindamood
 */
class KeyframeInfoChunk extends ChunkerClass{
    public KeyframeInfoChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }
    String name;
    short parent;
    short myID;
    Vector3f pivot;
    ArrayList track;
    float morphSmoothAngle;
    Vector3f BBoxMin;
    Vector3f BBoxMax;

    protected void initializeVariables() throws IOException {
        track=new ArrayList();
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case NODE_ID:
                readNodeID();
                return true;
            case TRACK_HEADER:
                readTrackHeader();
                return true;
            case TRACK_PIVOT:
                readTrackPivot();
                return true;
            case TRACK_POS_TAG:
                readPosTrackTag();
                return true;
            case TRACK_ROT_TAG:
                readRotTrackTag();
                return true;
            case TRACK_SCL_TAG:
                readScaleTrackTag();
                return true;
            case MORPH_SMOOTH:
                readSmoothMorph();
                return true;
            case KEY_FOV_TRACK:
                readFOVTrack();
                return true;
            case KEY_ROLL_TRACK:
                readRollTrack();
                return true;
            case KEY_COLOR_TRACK:
                readColorTrack();
                return true;
            case KEY_HOTSPOT_TRACK:
                readHotspotTrack();
                return true;
            case KEY_FALLOFF_TRACK:
                readFalloffTrack();
                return true;
            case BOUNDING_BOX:
                readBoundingBox();
                return true;
            default:
                return false;
        }
    }

    private void readBoundingBox() throws IOException {
        if (BBoxMin!=null)
            throw new IOException("logic error, BBoxMin not null:" + BBoxMin);
        BBoxMin=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        BBoxMax=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private void readFalloffTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).fallOff=myIn.readFloat();
        }
    }

    private void readHotspotTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).hotSpot=myIn.readFloat();
        }
    }

    private void readColorTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).colorTrack=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
        }
    }

    private void readRollTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).roll=myIn.readFloat();
        }
    }

    private void readFOVTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).FOV=myIn.readFloat();
        }
    }

    private void readSmoothMorph() throws IOException {
        morphSmoothAngle=myIn.readFloat();
    }

    private void readScaleTrackTag() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).scale=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        }
    }

    private void readRotTrackTag() throws IOException{
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            Quaternion tempQ=new Quaternion();
            tempQ.w =myIn.readFloat();
            tempQ.x =myIn.readFloat();
            tempQ.y =myIn.readFloat();
            tempQ.z =myIn.readFloat();
            locateTrack(trackPosition).rot=new Quaternion(tempQ);
        }
    }

    private void readPosTrackTag() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).position=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("trackPos#"+trackPosition+"Pos#i"+locateTrack(trackPosition).position);
        }
    }

    private KeyPointInTime locateTrack(int trackPosition) {
        if (track.size()==0){
            KeyPointInTime temp=new KeyPointInTime();
            temp.frame=trackPosition;
            track.add(temp);
            return temp;
        }
        Object[] parts=track.toArray();
        int i;
        for (i=0;i<parts.length;i++){
            if (((KeyPointInTime)parts[i]).frame>trackPosition){
                KeyPointInTime temp=new KeyPointInTime();
                temp.frame=trackPosition;
                track.add(i,temp);
                return temp;
            } else if (((KeyPointInTime)parts[i]).frame==trackPosition){
                return (KeyPointInTime) track.get(i);
            }
        }
        KeyPointInTime temp=new KeyPointInTime();
        temp.frame=trackPosition;
        track.add(temp);
        return temp;
    }

    private void readTrackPivot() throws IOException {
        pivot=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("Pivot of:" + pivot);
    }

    private void readTrackHeader() throws IOException {
        name=readcStr();
        short flag1=myIn.readShort();   // ignored
        short flag2=myIn.readShort();   // ignored
        short parent=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Name:" + name + " with parent:"+ parent);
    }

    private void readNodeID() throws IOException {
        myID=myIn.readShort();
    }


    public class KeyPointInTime{
        // acc data ignored
        public int frame;
        public Vector3f position;
        public Quaternion rot;
        public Vector3f scale;
        public float FOV;
        public float roll;
        public String morphName;
        public float hotSpot;
        public float fallOff;
        public ColorRGBA colorTrack;
    }
}
