package com.jmex.model.XMLparser.Converters.TDSChunkingFiles;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 *
 * parent == 4d4d == MAIN_3DS
 * type == 3d3d == EDIT_3DS
 *
 * @author Jack Lindamood
 *
 */
class EditableObjectChunk extends ChunkerClass{


    HashMap materialBlocks;
    HashMap namedObjects;
    float masterScale;
    float shadowMapRange;
    float rayTraceBias;
    Vector3f oConstPlanes;
    ColorRGBA genAmbientColor;
    ColorRGBA backGroundColor;
    String backGroundBigMap;
    boolean useBackColor;
    float shadowBias;
    short shadowMapSize;
    LayeredFogChunk fogOptions;
    FogChunk myFog;
    DistanceQueueChunk distanceQueue;

    public EditableObjectChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn,header);
    }

    protected void initializeVariables(){
        materialBlocks=new HashMap();
        namedObjects=new HashMap();
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
            switch (i.type){
                case MESH_VERSION:
                    readMeshVersion();
                    return true;
                case MAT_BLOCK:
                    MaterialBlock tempMat=new MaterialBlock(myIn,i);
                    materialBlocks.put(tempMat.name,tempMat);
                    return true;

                case MASTER_SCALE:
                    readMasterScale();
                    return true;

                case NAMED_OBJECT:
                    NamedObjectChunk tempOb=new NamedObjectChunk(myIn,i);
                    namedObjects.put(tempOb.name,tempOb);
                    return true;
                case KEY_VIEWPORT:  // Viewport layout is unneeded so is ignored
                    skipSize(i.length);
//                    readViewLayout(i.length);
                    return true;

                case SHADOW_MAP_RANGE:
                    readShadowRange();
                    return true;

                case RAYTRACE_BIAS:
                    readRayTraceBias();
                    return true;

                case O_CONSTS:
                    readOConst();
                    return true;

                case GEN_AMB_COLOR:
                    genAmbientColor=new ColorChunk(myIn,i).getBestColor();
                    return true;

                case BACKGRD_COLOR:
                    backGroundColor=new ColorChunk(myIn,i).getBestColor();
                    return true;
                case BACKGRD_BITMAP:
                    backGroundBigMap=readcStr();
                    return true;
                case V_GRADIENT:
                    skipSize(i.length); // ignored/unneeded
                    return true;
                case USE_BCK_COLOR:
                    useBackColor=true;
                    return true;
                case FOG_FLAG:
                    myFog=new FogChunk(myIn,i);
                    return true;
                case SHADOW_BIAS:
                    readShadowBias();
                    return true;
                case SHADOW_MAP_SIZE:
                    readShadowMapSize();
                    return true;
                case LAYERED_FOG_OPT:
                    fogOptions=new LayeredFogChunk(myIn,i);
                    return true;
                case DISTANCE_QUEUE:
                    distanceQueue=new DistanceQueueChunk(myIn,i);
                    return true;
                case DEFAULT_VIEW:
                    skipSize(i.length); // view ignored
                    return true;
                case UNKNOWN1:
                    skipSize(i.length);   // Unknown
                    return true;
                default:
                    return false;
            }
    }

    private void readOConst() throws IOException{
        oConstPlanes=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        if (DEBUG || DEBUG_LIGHT) System.out.println("Planes:" + oConstPlanes);
    }

    private void readRayTraceBias() throws IOException{
        rayTraceBias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Raytrace bias:" + rayTraceBias);
    }

    private void readShadowRange() throws IOException {
        shadowMapRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Shadow map range:" + shadowMapRange);
    }

    private void readMasterScale() throws IOException{
        masterScale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Master scale:" + masterScale);
    }

    private void readMeshVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Mesh version:" + i);
    }

    private void readShadowBias() throws IOException {
        shadowBias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Bias:" + shadowBias);
    }

    private void readShadowMapSize() throws IOException{
        shadowMapSize=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Shadow map siz:" + shadowMapSize);
    }

}
