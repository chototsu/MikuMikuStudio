package com.jme.scene.model.XMLparser.Converters;

import com.jme.util.LittleEndien;
import com.jme.util.TextureManager;
import com.jme.math.Vector3f;
import com.jme.math.Vector2f;
import com.jme.math.Quaternion;
import com.jme.math.Matrix3f;
import com.jme.scene.TriMesh;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.model.XMLparser.Converters.MaxChunkIDs;
import com.jme.scene.model.XMLparser.JmeBinaryWriter;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.image.Texture;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.util.BitSet;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * Converts .3ds files into jME binary
 *
 * @author Jack Lindamood
 */
public class MaxToJme implements MaxChunkIDs{
    private LittleEndien myIn;
    private static boolean DEBUG=true;
    private static boolean DEBUG_SEVERE=true;
    private static boolean DEBUG_LIGHT=false;

    private Stack s=new Stack();
    private HashMap readObject=new HashMap();

    /**
     * Converts a .3ds file (represented by the InputStream) to jME format.
     * @param max The .3ds file as an InputStream
     * @param bin The place to put the jME format
     * @throws IOException If read/write goes wrong.
     */
    public void convert(InputStream max,OutputStream bin) throws IOException {
        s.clear();
        myIn=new LittleEndien(max);
        Chunk mainPart=readChunk();
        if (mainPart.type!=MAIN_3DS){
            throw new IOException("Header doesn't match.  Probably not a 3ds file");
        }
        mainPart.length-=6;
        s.push(new Node("3ds scene"));
        readFile(mainPart.length);
        Node totalScene=(Node) s.pop();
        new JmeBinaryWriter().writeScene(totalScene,bin);
    }

    private void readFile(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading file");
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readFile chunk ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch(i.type){
                case TDS_VERSION:
                    readVersion();
                    break;
                case EDIT_3DS:
                    readEditableObject(i.length);
                    break;
                case KEYFRAMES:
                    readKeyframes(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readFile");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readFile:" + length);
        }
    }

    private void readKeyframes(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading Keyframes");
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in keyframer ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case KEYFRAME_HEAD:
                    readKeyframeHeader();
                    break;
                case KEYFRAME_OBJ:
                    readKeyframeObj(i.length);
                    break;
                case KEY_SEGMENT:
                    readSegment();
                    break;
                case KEY_CURTIME:
                    readCurTime();
                    break;
                case VIEWPORT_LAYOUT:
                    readViewLayout(i.length);
                    break;
                case CAMERA_TARG_INF_TAG:
                    readCamTargetInfoTag(i.length);
                    break;
                case CAMERA_INFO_TAG:
                    readCamInfoTag(i.length);
                    break;
                case KEY_OMNI_LI_INFO:
                    readOmniLightKeyframeInfo(i.length);
                    break;
                case KEY_AMBIENT_NODE:
                    readAmbientNodeKeyframeInfo(i.length);
                    break;
                case KEY_SPOT_TARGET:
                    readSpotLightTarget(i.length);
                    break;
                case KEY_SPOT_INFO:
                    readKeySpotLightInfo(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readKeyframes");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readKeyframes:" + length);
        }

    }

    private void readKeySpotLightInfo(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading keyspotlightinfo");
        if (DEBUG) System.out.println("Reading info about spot light");
        readKeyframeObj(length);
    }

    private void readSpotLightTarget(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading spotlighttarget");
        if (DEBUG) System.out.println("Reading info about spot light target");
        readKeyframeObj(length);
    }

    private void readAmbientNodeKeyframeInfo(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading ambientnodekeyframe");
        if (DEBUG) System.out.println("Reading info about Ambient light");
        readKeyframeObj(length);
    }

    private void readOmniLightKeyframeInfo(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading omnilightkeyframe");
        if (DEBUG) System.out.println("Reading info about omni light");
        readKeyframeObj(length);
    }

    private void readCamTargetInfoTag(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading camtargetinfotag");
        if (DEBUG) System.out.println("Reading info about camera target");
        readKeyframeObj(length);
    }

    private void readCamInfoTag(int length) throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading caminfotag");
        if (DEBUG) System.out.println("Reading info about camera");
        readKeyframeObj(length);
    }

    private void readCurTime() throws IOException{
        int curFrame=myIn.readInt();
        if (DEBUG_LIGHT) System.out.println("Reading curTime");
        if (DEBUG) System.out.println("Current frame is " + curFrame);
    }

    private void readSegment() throws IOException {
        int begin=myIn.readInt();
        int end=myIn.readInt();
        if (DEBUG_LIGHT) System.out.println("Reading segment");
        if (DEBUG) System.out.println("Segment begins at " + begin + " and ends at " + end);
    }

    private void readKeyframeObj(int length) throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading readingkeyframeobject");
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in KeyframeObj ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case NODE_ID:
                    readNodeID();
                    break;
                case TRACK_HEADER:
                    readTrackHeader();
                    break;
                case TRACK_PIVOT:
                    readTrackPivot();
                    break;
                case TRACK_POS_TAG:
                    readPosTrackTag();
                    break;
                case TRACK_ROT_TAG:
                    readRotTrackTag();
                    break;
                case TRACK_SCL_TAG:
                    readScaleTrackTag();
                    break;
                case MORPH_SMOOTH:
                    readSmoothMorph();
                    break;
                case KEY_FOV_TRACK:
                    readFOVTrack();
                    break;
                case KEY_ROLL_TRACK:
                    readRollTrack();
                    break;
                case KEY_COLOR_TRACK:
                    readColorTrack();
                    break;
                case KEY_HOTSPOT_TRACK:
                    readHotspotTrack();
                    break;
                case KEY_FALLOFF_TRACK:
                    readFalloffTrack();
                    break;
                case BOUNDING_BOX:
                    readBoundingBox();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readKeyframeObj");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in KeyframeObj:" + length);

        }
    }

    private void readBoundingBox() throws IOException {
        Vector3f boxMin=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        Vector3f boxMax=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        
    }

    private void readFalloffTrack() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading FalloffTrack");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        float[] falloff=new float[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            falloff[i]=myIn.readFloat();
            if (DEBUG) System.out.println("Track " + trackRot + " has falloff angle " + falloff[i]);
        }
    }

    private void readHotspotTrack() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading Hotspot track");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        float[] hotspot=new float[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            hotspot[i]=myIn.readFloat();
            if (DEBUG) System.out.println("Track " + trackRot + " has hotspot angle " + hotspot[i]);
        }
    }

    private void readColorTrack() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading Colortrack");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        ColorRGBA[] trackColors=new ColorRGBA[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackColors[i]=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
            if (DEBUG) System.out.println("Track for color " + trackRot + " is angle " + trackColors[i]);
        }
    }

    private void readRollTrack() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading roll track");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        float[] trackRoll=new float[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackRoll[i]=myIn.readFloat();
            if (DEBUG) System.out.println("Track for roll " + trackRot + " is angle " + trackRoll[i]);
        }
    }

    private void readFOVTrack() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading FOV track");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        float[] trackFOV=new float[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackFOV[i]=myIn.readFloat();
            if (DEBUG) System.out.println("Track for FOV " + trackRot + " is angle " + trackFOV[i]);
        }
    }

    private void readSmoothMorph() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading smoothmorph");
        float smoothAngle=myIn.readFloat();
        if (DEBUG) System.out.println("Smooth morph angle:" + smoothAngle);
    }

    private void readScaleTrackTag() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading scaletrack");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        Vector3f[] trackRots=new Vector3f[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackRots[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("Track for scale " + trackRot + " is " + trackRots[i]);
        }
    }

    private void readRotTrackTag() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading rottrack");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        Quaternion[] trackRots=new Quaternion[keys];
        for (int i=0;i<keys;i++){
            int trackRot=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackRots[i]=new Quaternion();
            trackRots[i].w=myIn.readFloat();
            trackRots[i].x=myIn.readFloat();
            trackRots[i].y=myIn.readFloat();
            trackRots[i].z=myIn.readFloat();
            if (DEBUG) System.out.println("Track for rotation " + trackRot + " is " + trackRots[i]);
        }
    }

    private void readPosTrackTag() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading Postrack");
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        Vector3f[] trackPositions=new Vector3f[keys];
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            trackPositions[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) System.out.println("Track for position " + trackPosition + " is " + trackPositions[i]);
        }
    }

    private void readTrackPivot() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading trackpivot");
        Vector3f pivot=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) System.out.println("Pivot of:" + pivot);
    }

    private void readTrackHeader() throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading trackheader");
        String name=readcStr();
        short flag1=myIn.readShort();
        short flag2=myIn.readShort();
        short parent=myIn.readShort();
        if (DEBUG) System.out.println("Name:" + name + " with parent:"+ parent);
    }

    private void readNodeID() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading NodeID");
        short ID=myIn.readShort();
        if (DEBUG) System.out.println("Reading node id#" + ID);
    }

    private void readKeyframeHeader() throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading keyframeHeader");
        short revision=myIn.readShort();
        String flname=readcStr();
        int animLen=myIn.readInt();
        if (DEBUG) System.out.println("Revision #" + revision + " with filename " + flname + " and animation len " + animLen);
    }

    // Parent readFile() This=3d3d
    private void readEditableObject(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading editableObject");
        s.push(new Node("3ds editable object"));
        while (length>0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in editable object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case MESH_VERSION:
                    readMeshVersion();
                    break;
                case MASTER_SCALE:
                    readMasterScale();
                    break;
                case NAMED_OBJECT:
                    readNamedObject(i.length);
                    break;
                case MAT_BLOCK:
                    readMatBlock(i.length);
                    break;
                case VIEWPORT_LAYOUT:
                    readViewLayout(i.length);
                    break;
                case SHADOW_MAP_RANGE:
                    readShadowRange();
                    break;
                case RAYTRACE_BIAS:
                    readRayTraceBias();
                    break;
                case O_CONSTS:
                    readOConst();
                    break;
                case GEN_AMB_COLOR:
                    readGenAmbColor(i.length);
                    break;
                case BACKGRD_COLOR:
                    readBackGroundColor(i.length);
                    break;
                case BACKGRD_BITMAP:
                    readBackGroundBitMap();
                    break;
                case V_GRADIENT:
                    readGradient(i.length);
                    break;
                case USE_BCK_COLOR:
                    useBackColor();
                    break;
                case FOG_FLAG:
                    readFog(i.length);
                    break;
                case SHADOW_BIAS:
                    readShadowBias();
                    break;
                case SHADOW_MAP_SIZE:
                    readShadowMapSize();
                    break;
                case LAYERED_FOG_OPT:
                    readLayeredFogOptions(i.length);
                    break;
                case DISTANCE_QUEUE:
                    readDistanceQueue(i.length);
                    break;
                case DEFAULT_VIEW:
                    readDefaultView(i.length);
                    break;
                case UNKNOWN1:
                    myIn.readFloat();   // Unknown
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readEditableObject");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readEditableObject:" + length);
        }
        Node finishedNode=(Node) s.pop();
        Node parentNode=(Node) s.pop();
        parentNode.attachChild(finishedNode);
        s.push(parentNode);
        if (DEBUG_LIGHT) System.out.println("Finished Editable object");
    }

    private void readFog(int length) throws IOException{
        if (DEBUG_LIGHT) System.out.println("Reading Fog");
        float nearPlane=myIn.readFloat();
        float nearDensity=myIn.readFloat();
        float farPlane=myIn.readFloat();
        float farDensity=myIn.readFloat();
        if (DEBUG){
            System.out.println("Near plane:" + nearPlane + " Near Density:" + nearDensity + " Far Plane:" + farPlane + " Far Density:"+ farDensity);
        }
        length-=4*4;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readFog ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case COLOR_FLOAT:
                    ColorRGBA background=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                    if (DEBUG) System.out.println("Background Color:" + background);
                    break;
                case FOG_BACKGROUND:
                    if (DEBUG) System.out.println("use background true");
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type*" + Integer.toHexString(i.type) + "* in readFog");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readFog:" + length);
        }
    }

    private void readLayeredFogOptions(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading LayeredFogOptions");
        float nearZ=myIn.readFloat();
        float farZ=myIn.readFloat();
        float density=myIn.readFloat();
        int type=myIn.readInt();
        length-=4*4;
        if (DEBUG) System.out.println("nearZ:"+nearZ+" farZ:"+farZ+" density:"+density+" type:"+type);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in layeredFogOptions ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case COLOR_FLOAT:
                    ColorRGBA fogColor=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                    if (DEBUG) System.out.println("Fog color:" + fogColor);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type*" + Integer.toHexString(i.type) + "* in layeredFogOptions");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in layeredFogOptions:" + length);
        }
    }

    private void readDistanceQueue(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading distanceQueue");
        float nearPlane=myIn.readFloat();
        float nearDensity=myIn.readFloat();
        float farPlane=myIn.readFloat();
        float farDensity=myIn.readFloat();
        if (DEBUG) System.out.println("@distanceQueue nearPlane:"+nearPlane+" nearDensity:"+nearDensity+" farPlane"+farPlane+" farDensity"+farDensity);
        length-=4*4;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readDistanceQueue ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case DQUEUE_BACKGRND:
                    if (DEBUG) System.out.println("Use distanceQueue true");
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type*" + Integer.toHexString(i.type) + "* in readDistanceQueue");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readDistanceQueue:" + length);
        }
    }

    private void readDefaultView(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading defaultView");
        if (DEBUG) System.out.println("Reading default view");
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readDefaultView ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case VIEW_CAMERA:
                    readCamera();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type*" + Integer.toHexString(i.type) + "* in readDefaultView");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readDefaultView:" + length);
        }
    }

    private void readCamera() throws IOException {
        String s=readcStr();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading camera for view with name " + s);

    }

    private void useBackColor() {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Background color use flag located");
    }

    private void readGradient(int length) throws IOException{
        float midpoint=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading gradient with midpoint of " + midpoint);
        readColors(length-4);
    }

    private void readBackGroundBitMap() throws IOException {
        String bit=readcStr();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Bitmap name:" + bit);

    }

    private void readBackGroundColor(int length) throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading background colors");
        readColors(length);
    }

    private void readGenAmbColor(int length) throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading general ambient color");
        readColors(length);
    }

    private void readOConst() throws IOException{
        Vector3f planes=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        if (DEBUG || DEBUG_LIGHT) System.out.println("Planes:" + planes);
    }

    private void readRayTraceBias() throws IOException{
        float bias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Raytrace bias:" + bias);
    }

    private void readShadowRange() throws IOException{
        float range=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Shadow map range:" + range);
    }

    private void readViewLayout(int length) throws IOException {
        short style=myIn.readShort();
        int active=myIn.readShort();
        myIn.readShort(); // Unknown
        int swap=myIn.readShort();
        myIn.readShort(); // Uknown
        int swapPrior=myIn.readShort();
        int swapView=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT){
            System.out.println("ViewLayout style:" + style+":active:"+active+":swap:"+swap+":swapPrior:"+swapPrior+":swapView"+swapView);
        }
        length-=14;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in ViewLayout ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case VIEWPORT_SIZE:
                    readViewPortSize();
                    break;
                case VIEWPORT_DATA3:
                case VIEWPORT_DATA:
                    readViewPortData();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type*" + Integer.toHexString(i.type) + "* in readViewLayout");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readViewLayout:" + length);
        }
    }

    private void readShadowMapSize() throws IOException{
        short size=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Shadow map siz:" + size);
    }

    private void readShadowBias() throws IOException {
        float bias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Bias:" + bias);
    }

    private void readViewPortData() throws IOException{
        short flags=myIn.readShort();
        short axisLockout=myIn.readShort();
        short X=myIn.readShort();
        short Y=myIn.readShort();
        short width=myIn.readShort();
        short height=myIn.readShort();
        short type=myIn.readShort();
        float zoom=myIn.readFloat();
        Vector3f cent=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        float horizAng=myIn.readFloat();
        float vertAng=myIn.readFloat();
        byte[] name=new byte[11];
        myIn.readFully(name);
        String camName=new String(name);
        if (DEBUG || DEBUG_LIGHT){
            System.out.println("ViewportData flags:" + flags + " axisLockout:" + axisLockout + " X:" + X + " Y:" + Y + " width:" + width +
                    " height:" + height + " type:" + type + " zoom:" + zoom + " center:" + cent +
                    " horizAng:" + horizAng + " vertAng:" + vertAng + " Name:" + camName);
        }
    }

    private void readViewPortSize() throws IOException{
        short x=myIn.readShort();
        short y=myIn.readShort();
        short width=myIn.readShort();
        short height=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("X:" + x + " Y:" + y + " Width:" + width + " Height:" + height);

    }

    private void readMatBlock(int length) throws IOException{
        s.push(new MaterialBlock());
        if (DEBUG_LIGHT) System.out.println("reading MatBlock");
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in Material Block ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case MAT_NAME:
                    readMatName();
                    break;
                case MAT_AMB_COLOR:
                    readMatAmbient(i.length);
                    break;
                case MAT_DIF_COLOR:
                    readMatDiffuse(i.length);
                    break;
                case MAT_SPEC_CLR:
                    readMatSpec(i.length);
                    break;
                case MAT_SHINE:
                    readMatShine();
                    break;
                case MAT_SHINE_STR:
                    readMatStrShine();
                    break;
                case MAT_ALPHA:
                    readMatAlpha();
                    break;
                case MAT_ALPHA_FAL:
                    readMatAlphaFallout();
                    break;
                case MAT_REF_BLUR:
                    readMatRefBlur();
                    break;
                case MAT_SHADING:
                    readMatShading();
                    break;
                case MAT_SELF_ILUM:
                    readMatSelfIlum();
                    break;
                case MAT_WIRE_SIZE:
                    readMatWireSize();
                    break;
                case IN_TRANC_FLAG:
                    if (DEBUG) System.out.println("In tranc (???) flag is true");
                    break;
                case TEXMAP_ONE:
                    readTextureMapOne(i.length);
                    break;
                case MAT_TEX_BUMPMAP:
                    readTextureBumpMap(i.length);
                    break;
                case MAT_SOFTEN:
                    if (DEBUG) System.out.println("Material soften is true");
                    break;
                case MAT_SXP_TEXT_DATA:
                    myIn.skipBytes(i.length);   // unknown
                    break;
                case MAT_REFL_BLUR:
                    if (DEBUG) System.out.println("Material blur present");
                    break;
                case MAT_WIRE_ABS:
                    if (DEBUG) System.out.println("Using absolute wire in units");
                    break;
                case MAT_REFLECT_MAP:
                    readReflectMap(i.length);
                    break;
                case MAT_SXP_BUMP_DATA:
                    myIn.skipBytes(i.length);   // unknown
                    break;
                case MAT_TWO_SIDED:
                    if (DEBUG) System.out.println("Material two sided indicated");
                    break;
                case MAT_FALLOFF:
                    if (DEBUG) System.out.println("Using material falloff");
                    break;
                case MAT_WIREFRAME_ON:
                    if (DEBUG) System.out.println("Material wireframe is active");
                    break;
                case MAT_TEX2MAP:
                    readTextureMapTwo(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readMatBlock");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readNamedObject:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished Mat block");
        MaterialBlock mb=(MaterialBlock) s.pop();
        mb.mat.setEnabled(false);
        mb.tex.setEnabled(true);
        readObject.put(mb.name,mb);
    }

    private void readTextureMapTwo(int length) throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading texture map TWO info");
        readMapInfo(length);
    }

    private void readReflectMap(int length) throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Reflection info");
        readMapInfo(length);
    }

    private void readTextureBumpMap(int length) throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading texture bumpMap info");
        readMapInfo(length);
    }

    private Texture readMapInfo(int length)throws IOException{
        Texture t=null;
        s.push(t);
        if (DEBUG_LIGHT) System.out.println("Reading map info");
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            float value;
            if (DEBUG) System.out.println("Read in readMapInfo object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case PRCT_INT_FRMT:
                    value=myIn.readShort()/100f;
                    if (DEBUG) System.out.println("Texture percent:"+value);
                    break;
                case MAT_TEXNAME:
                    readMatFileName();
                    break;
                case MAT_TEX_FLAGS:
                    readMatMapFlags();
                    break;
                case MAT_TEX_BLUR:
                    readTextureBlur();
                    break;
                case MAT_TEX_BUMP_PER:
                    value=myIn.readShort()/100f;
                    if (DEBUG) System.out.println("Texture bump percent:"+value);
                    break;
                case TEXTURE_V_SCALE:
                    readVScale();
                    break;
                case TEXTURE_U_SCALE:
                    readUScale();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readMapInfo");
                    return (Texture) s.pop();
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readMapInfo:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished mapInfo");
        return (Texture) s.pop();
    }

    private void readUScale() throws IOException {
        float uScale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("U Scale:" + uScale);
        Texture temp=(Texture) s.pop();
        MaterialBlock mb=(MaterialBlock) s.pop();
        mb.uScale=uScale;
        s.push(mb);
        s.push(temp);
    }

    private void readVScale() throws IOException {
        float vScale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("V scale:" + vScale);
        Texture temp=(Texture) s.pop();
        MaterialBlock mb=(MaterialBlock) s.pop();
        mb.vScale=vScale;
        s.push(mb);
        s.push(temp);
    }

    private void readTextureMapOne(int length) throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading texture map one info");
        Texture t=readMapInfo(length);
        if (t!=null){
            MaterialBlock mb=(MaterialBlock) s.pop();
            mb.tex.setTexture(t);
            s.push(mb);
        }
    }

    private void readTextureBlur() throws IOException {
        float blur=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Texture blur is " + blur);
    }

    private void readMatMapFlags() throws IOException {
        short flags=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Material Map flags are " + flags);
    }

    private void readMatFileName() throws IOException {
        String name=readcStr();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Texture filename:"+name);
        Texture t=(Texture) s.pop();    // ignore old texture
        s.push(TextureManager.loadTexture(name,Texture.FM_LINEAR,Texture.MM_NEAREST,false));
    }

    private void readMatWireSize() throws IOException {
        float wireSize=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material wiresize " + wireSize);
    }

    private void readMatSelfIlum() throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material self Illumination");
        readPercent();
    }

    private void readMatShading() throws IOException{
        short matShadeVal=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material shading value:" + matShadeVal);
    }

    private void readMatRefBlur() throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material reflective blur");
        readPercent();
    }

    private void readMatAlphaFallout() throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material alpha Fallout");
        readPercent();
    }

    private void readMatAlpha() throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Material alpha");
        float alpha=readPercent();
        MaterialBlock m=(MaterialBlock) s.pop();
        m.mat.setAlpha(alpha);
        s.push(m);
    }

    private void readMatStrShine() throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading shinniness strength");
        readPercent();
    }

    private void readMatShine() throws IOException {
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading shinniness");
        float percent=readPercent();
        MaterialBlock m=(MaterialBlock) s.pop();
        m.mat.setShininess(percent*128f);
        s.push(m);
    }

    private float readPercent() throws IOException{
        Chunk i=readChunk();
        float value=0;
        if (DEBUG) System.out.println("Read in readPercent ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
        switch (i.type){
            case PRCT_INT_FRMT:
                value=myIn.readShort()/100f;
                break;
            case PRCT_FLT_FRMT:
                value=myIn.readFloat();
                break;
            default:
                if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readPercent");
                return -1;
        }
        if (DEBUG || DEBUG_LIGHT) System.out.println("Have read percent " + value);
        return value;
    }

    private void readMatSpec(int length) throws IOException {
        ColorRGBA color=readColors(length);
        MaterialBlock m=(MaterialBlock) s.pop();
        m.mat.setAmbient(color);
        s.push(m);
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Specular color " + color);
    }

    private void readMatDiffuse(int length) throws IOException {
        ColorRGBA color=readColors(length);
        MaterialBlock m=(MaterialBlock) s.pop();
        m.mat.setDiffuse(color);
        s.push(m);
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Diffuse color " + color);
    }

    private void readMatAmbient(int length) throws IOException {
        ColorRGBA color=readColors(length);
        MaterialBlock m=(MaterialBlock) s.pop();
        m.mat.setAmbient(color);
        s.push(m);
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading ambient color " + color);
    }

    private ColorRGBA readColors(int length) throws IOException {
        ColorRGBA color1=null;
        ColorRGBA color2=null;
        while (length > 0){
            Chunk i=readChunk();
            if (DEBUG) System.out.println("Read in readColor ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case COLOR_BYTE:
                    if (color1==null) color1=new ColorRGBA(myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,1);
                    break;
                case CLR_BYTE_GAMA:
                    if (color2==null) color2=new ColorRGBA(myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,myIn.readUnsignedByte()/255f,1);
                    break;
                case COLOR_FLOAT:
                    if (color1==null) color1=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
                    break;
                case CLR_FLOAT_GAMA:
                    if (color2==null) color2=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readMatAmb");
                    myIn.skipBytes(i.length);
                    return null;
            }
            length-=i.length;
            if (DEBUG) System.out.println("Have read colors and length left " + length);
        }
        return (color1==null) ? color2 : color1;
    }


    private void readMatName() throws IOException{
        String name=readcStr();
        if (DEBUG || DEBUG_LIGHT) System.out.println("read material name:" + name);
        MaterialBlock i=(MaterialBlock) s.pop();
        i.name=name;
        s.push(i);
    }

    private void readNamedObject(int length) throws IOException {
        String name=readcStr();
        length-=name.length()+1;
        s.push(new Node(name));
        if (DEBUG_LIGHT) System.out.println("Reading named object " + name);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in named object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case OBJ_TRIMESH:
                    readTriMesh(i.length);
                    break;
                case CAMERA_FLAG:
                    readCameraFlag(i.length);
                    break;
                case LIGHT_OBJ:
                    readLightObject(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readNamedObject");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readNamedObject:" + length);
        }
        Node finishedNode=(Node) s.pop();
        Node parentNode=(Node) s.pop();
        parentNode.attachChild(finishedNode);
        s.push(parentNode);
        if (DEBUG_LIGHT) System.out.println("Finished named object " + name);
    }

    private void readLightObject(int length) throws IOException{
        Vector3f lightPos=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Light with position " + lightPos);
        length-=4*3;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readLightObject object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case COLOR_FLOAT:
                    ColorRGBA lightColor=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                    if (DEBUG) System.out.println("Light color:"+lightColor);
                    break;
                case LIGHT_OUT_RANGE:
                    readOuterLightRange();
                    break;
                case LIGHT_IN_RANGE:
                    readInnerLightRange();
                    break;
                case LIGHT_MULTIPLIER:
                    readLightMultiplier();
                    break;
                case LIGHT_SPOTLIGHT:
                    readSpotLight(i.length);
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readLightObject");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readLightObject:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished light");
    }

    private void readSpotLight(int length) throws IOException{
        Vector3f targetPos=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        float hotSpot=myIn.readFloat();
        float fallOff=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT){
            System.out.println("Spotlight pointed at " + targetPos + " with hotspot " + hotSpot + " and falloff " + fallOff);
        }
        length-=4*5;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readSpotLight object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case LIGHT_SPOT_ROLL:
                    readSpotlightRollAngles();
                    break;
                case LIGHT_SPOT_SHADOWED:
                    if (DEBUG) System.out.println("Spotlight is now shadowed");
                    break;
                case LIGHT_SPOT_BIAS:
                    readLightBias();
                    break;
                case LIGHT_LOC_SHADOW:
                    readLightShadow();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readSpotLight");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readSpotLight:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished spotlight");
    }

    private void readLightShadow() throws IOException {
        float bias=myIn.readFloat();
        float filter=myIn.readFloat();
        short shadowSize=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Light shadow bias " + bias +" filter " + filter +" shadowSize " + shadowSize);
    }

    private void readLightBias() throws IOException {
        float bias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Light bias is " + bias);
    }

    private void readSpotlightRollAngles() throws IOException{
        float roll=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Spotlight rollangle:"+ roll);
    }

    private void readLightMultiplier() throws IOException {
        float mult=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Light multiplier is " + mult);
    }

    private void readInnerLightRange() throws IOException {
        float range=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Light inner range is " + range);
    }

    private void readOuterLightRange() throws IOException {
        float range=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Light outter range is " + range);
    }

    private void readCameraFlag(int length) throws IOException {
        Vector3f camPos=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        Vector3f targetLoc=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        float bankAngle=myIn.readFloat();
        float focus=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT)
            System.out.println("Camera Position:" + camPos+" TargetLoc:"+targetLoc+" bankAngle:"+bankAngle+" focus:"+focus);
        length-=8*4;
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in readCameraFlag object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case CAMERA_RANGES:
                    readRanges();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readCameraFlag");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readCameraFlag:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished camera flags");
    }

    private void readRanges() throws IOException {
        float nearRange=myIn.readFloat();
        float farRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Near range:"+ nearRange + " Far range:" + farRange);
    }

    private void readTriMesh(int length) throws IOException {
        if (DEBUG_LIGHT) System.out.println("Reading triMesh");
        TriMesh me=new TriMesh("Mesh Object");
        s.push(me);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in TriMesh object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case VERTEX_LIST:
                    readVerts();
                    break;
                case TEXT_COORDS:
                    readTexCoords();
                    break;
                case COORD_SYS:
                    readCoordSystem();
                    break;
                case FACES_ARRAY:
                    readFaces(i.length);
                    break;
                case VERTEX_OPTIONS:
                    readOptions();
                    break;
                case MESH_COLOR:
                    readMeshColor();
                    break;
                case MESH_TEXTURE_INFO:
                    readMeshTextureInfo();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readTriMesh");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readTriMesh:" + length);
        }
        TriMesh finishedMesh=(TriMesh) s.pop();
        if (finishedMesh.getVertices()==null || finishedMesh.getVertices().length==0){
            if (DEBUG_LIGHT) System.out.println("Not attaching a TriMesh");
            return;
        }
        finishedMesh.setModelBound(new BoundingSphere());
        finishedMesh.updateModelBound();
//        finishedMesh.setSolidColor(ColorRGBA.randomColor());
        Node parentNode=(Node) s.pop();
        parentNode.attachChild(finishedMesh);


        s.push(parentNode);
        if (DEBUG_LIGHT) System.out.println("Finished reading trimesh");
    }

    private void readMeshTextureInfo() throws IOException{
        short type=myIn.readShort();
        float xTiling=myIn.readFloat();
        float yTiling=myIn.readFloat();;
        float Xicon=myIn.readFloat();
        float Yicon=myIn.readFloat();
        float Zicon=myIn.readFloat();
        float matrix[][]=new float[4][3];
        for (int i=0;i<4;i++)
            for (int j=0;j<3;j++)
                matrix[i][j]=myIn.readFloat();
        float scaling=myIn.readFloat();
        float planIconW=myIn.readFloat();
        float planIconH=myIn.readFloat();
        float cylIconH=myIn.readFloat();;
    }

    private void readMeshColor() throws IOException {
        byte color=myIn.readByte();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Mesh color read as " + color);
    }

    private void readOptions() throws IOException {
        int numVert=myIn.readShort();
        for (int i=0;i<numVert;i++){
            short option=myIn.readShort();
        }
        if (DEBUG || DEBUG_LIGHT) System.out.println("Options read");
    }

    private void readMeshVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Mesh version:" + i);

    }

    private void readSmoothing(short nFaces,Vector3f[] faceNormals) throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading smoothing");
        TriMesh parentMesh=(TriMesh) s.pop();
        Vector3f[] norms=new Vector3f[parentMesh.getVertices().length];
        int [] parts=new int[nFaces];
        Vector3f[] smoothingGroupNormalCalculation=new Vector3f[33];
        int []totalNormalsInASmoothingGroup=new int[33];
        for (int i=0;i<totalNormalsInASmoothingGroup.length;i++){
            smoothingGroupNormalCalculation[i]=new Vector3f();
            totalNormalsInASmoothingGroup[i]=0;
        }

        for (int i=0;i<nFaces;i++){
            parts[i]=myIn.readInt();
            for (int j=0;j<totalNormalsInASmoothingGroup.length;j++){
                if ((parts[i]& (1<<j))!=0){
                    smoothingGroupNormalCalculation[j].addLocal(faceNormals[i]);
                    totalNormalsInASmoothingGroup[j]++;
                }
            }
        }
        for (int i=0;i<totalNormalsInASmoothingGroup.length;i++){
            smoothingGroupNormalCalculation[i].normalizeLocal();
        }


        Vector3f temp=new Vector3f();
        // Change each face's normal to a new normal dependant upon Smoothing Groups
        for (int i=0;i<nFaces;i++){
            temp.set(0,0,0);
            int totalSmoothingGroups=0;
            for (int j=0;j<totalNormalsInASmoothingGroup.length;j++){
                if ((parts[i] & (1<<j))!=0){
                    totalSmoothingGroups++;
                    temp.addLocal(smoothingGroupNormalCalculation[j]);
                }
            }
            if (totalSmoothingGroups!=0){   // If it's not in a smoothing group, then the face normal doesn't change
                faceNormals[i].set(temp);
                faceNormals[i].normalizeLocal();
            }
        }

        // Now set the normal at a vertex to the average of the normals of every face
        //  that contains that vertex
        // For every vertex in the mesh
        int[] indexes=parentMesh.getIndices();
        for (int i=0;i<norms.length;i++){
            int count=0;
            temp.set(0,0,0);
            for (int j=0;j<indexes.length;j++){  // for every vertex that is shown (IE every face)
                if (indexes[j]==i){
                    temp.addLocal(faceNormals[j/3]);
                    count++;
                }
            }
            if (count!=0)
                norms[i]=temp.normalize();
        }

        parentMesh.setNormals(norms);
        s.push(parentMesh);

    }

    private void readFaces(int length) throws IOException{

        short nFaces=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading faces #=" + nFaces);
        int[] indexes=new int[nFaces*3];
        Vector3f[]faceNormal=new Vector3f[nFaces];
        TriMesh parentMesh=(TriMesh) s.pop();
        Vector3f[] vertexes=parentMesh.getVertices();
        Vector3f tempA=new Vector3f();
        Vector3f tempB=new Vector3f();

        for (int i=0;i<nFaces;i++){
            short[] parts=new short[3];
            for (int j=0;j<3;j++){
                parts[j]=myIn.readShort();
                indexes[i*3+j]=parts[j];
            }
            short flag=myIn.readShort();
            if (DEBUG || DEBUG_LIGHT) System.out.println("Read vertex indexes:" + parts[0] + " , " + parts[1] + " , " + parts[2] + ": with flag:" + flag);
            vertexes[parts[1]].subtract(vertexes[parts[0]],tempA);
            vertexes[parts[2]].subtract(vertexes[parts[0]],tempB);
            faceNormal[i]=tempA.cross(tempB);
            faceNormal[i].normalizeLocal();
            if (DEBUG || DEBUG_LIGHT) System.out.println("Normal for face " + i + " is " + faceNormal[i]
            );

        }
        length -= 2 + nFaces*(3*2+2);
        parentMesh.setIndices(indexes);
        s.push(parentMesh);
        while (length > 0){
            Chunk i=readChunk();
            i.length-=6;
            length-=6;
            if (DEBUG) System.out.println("Read in faces object ID:" + Integer.toHexString(i.type) + "* with known length " + i.length);
            switch (i.type){
                case SMOOTH_GROUP:
                    readSmoothing(nFaces,faceNormal);
                    break;
                case MESH_MAT_GROUP:
                    readMeshMaterialGroup();
                    break;
                default:
                    if (DEBUG) System.out.println("Unknown type***:" + Integer.toHexString(i.type) + "***");
                    if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) + ": in readFaces");
                    return;
            }
            length-=i.length;
            if (DEBUG) System.out.println("length left in readNamedObject:" + length);
        }
        if (DEBUG_LIGHT) System.out.println("Finished reading faces");
    }

    private void readMeshMaterialGroup() throws IOException {
        String name=readcStr();
        short numFace=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Material " + name + " is applied to " + numFace + " faces");
        for (int i=0;i<numFace;i++){
            int faceApplied=myIn.readShort();
        }
        // Wrongly assume only one texture can be applied to one TriMesh
        MaterialBlock mb=(MaterialBlock) readObject.get(name);
        TriMesh tm=(TriMesh) s.pop();
        tm.setRenderState(mb.mat);
        tm.setRenderState(mb.tex);
//        Vector2f[] texCoords=tm.getTextures();
//        for (int i=0;i<texCoords.length;i++)
//            if (texCoords[i]!=null)
//                texCoords[i].set(texCoords[i].x*mb.uScale,texCoords[i].y*mb.vScale);
//        tm.setTextures(texCoords);
        s.push(tm);
    }

    private void readCoordSystem() throws IOException{
        if (DEBUG || DEBUG_LIGHT) System.out.println("reading local coords");
        float[] parts=new float[9];
        Matrix3f rot=new Matrix3f();
        for (int i=0;i<9;i++)
            parts[i]=myIn.readFloat();
        rot.set(parts);
        Vector3f origin=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
//        if (DEBUG) System.out.println("Origin:" + origin);
        Spatial parent=(Spatial) s.pop();
//        parent.setLocalRotation(rot);
//        parent.setLocalTranslation(origin);
        s.push(parent);

    }

    private void readTexCoords() throws IOException{
        short nPoints=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading texCoords #="+ nPoints);
        Vector2f[] verts=new Vector2f[nPoints];
        for (int i=0;i<nPoints;i++){
            verts[i]=new Vector2f(myIn.readFloat(),myIn.readFloat());
//            if (DEBUG) System.out.println("Reading vert:" + verts[i]);
        }
        TriMesh parentMesh=(TriMesh) s.pop();
        parentMesh.setTextures(verts);
        s.push(parentMesh);
    }

    private void readVerts() throws IOException{
        short nPoints=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Reading Verts #="+ nPoints);
        Vector3f[] verts=new Vector3f[nPoints];
        for (int i=0;i<nPoints;i++){
            verts[i]=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
//            if (DEBUG) System.out.println("Reading vert:" + verts[i]);
        }
        TriMesh parentMesh=(TriMesh) s.pop();
        parentMesh.setVertices(verts);
        s.push(parentMesh);
    }

    private String readcStr() throws IOException {
        ArrayList byteArray=new ArrayList(16);
        byte inByte=myIn.readByte();
        while (inByte!=0){
            byteArray.add(new Byte(inByte));
            inByte=myIn.readByte();
        }
        Object [] parts=byteArray.toArray();
        byte[] name=new byte[parts.length];
        for (int i=0;i<parts.length;i++){
            name[i]=((Byte)parts[i]).byteValue();
        }
        String theName=new String(name);
        if (DEBUG) System.out.println("read string:" + theName + "*");
        return theName;
    }

    private void readMasterScale() throws IOException {
        float scale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Master scale:" + scale);

    }

    private void readVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Version:" + i);
    }

    private Chunk readChunk() throws IOException {
        return new Chunk(myIn.readUnsignedShort(),myIn.readInt());
    }

    private static class Chunk{
        Chunk(int t,int l){
            type=t;
            length=l;
        }
        int type;
        int length;
    }

    private static class MaterialBlock{
        public MaterialState mat;
        public TextureState tex;
        float uScale=1;
        float vScale=1;
        public String name;
        public MaterialBlock(){
            mat=DisplaySystem.getDisplaySystem().getRenderer().getMaterialState();
            tex=DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        }
    }
}