package com.jme.scene.model.XMLparser.Converters.TDSChunkingFiles;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * @author Jack Lindamood
 */
public class EditableObjectChunk extends ChunkerClass{
    // Parent == TDSFile == 0x4d4d

    ArrayList materialBlocks;
    HashMap namedObjects;
    float masterScale;
    public EditableObjectChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn,header);
    }

    protected void initializeVariables(){
        materialBlocks=new ArrayList();
        namedObjects=new HashMap();
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
            switch (i.type){
                case MESH_VERSION:
                    readMeshVersion();
                    return true;
                case MAT_BLOCK:
                    materialBlocks.add(new MaterialBlock(myIn,i));
                    return true;

                case MASTER_SCALE:
                    readMasterScale();
                    return true;

                case NAMED_OBJECT:
                    NamedObjectChunk temp=new NamedObjectChunk(myIn,i);
                    namedObjects.put(temp.name,temp);
                    return true;
/*
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
*/
                default:
                    return false;
            }
    }

    private void readMasterScale() throws IOException{
        masterScale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Master scale:" + masterScale);
    }

    private void readMeshVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) System.out.println("Mesh version:" + i);
    }
}
