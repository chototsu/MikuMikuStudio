package com.jme.scene.model.XMLparser;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * List of 3ds Chunk ID #'s
 * @author Jack Lindamood
 */
public interface MaxChunkIDs {

    // These must all be diffrent values
    static final int MAIN_3DS     =0x4D4D;
    static final int M3D_VERSION  =0x0002;
    static final int NULL_CHUNK   =0x0000;
    static final int MASTER_SCALE =0x0100;
    static final int NAMED_OBJECT =0x4000;
    static final int OBJ_TRIMESH  =0x4100;
    static final int VERTEX_LIST  =0x4110;
    static final int TEXT_COORDS  =0x4140;
    static final int COORD_SYS    =0x4160;
    static final int FACES_ARRAY  =0x4120;
    static final int SMOOTH_GROUP =0x4150;
    static final int KEYFRAMES    =0xb000;
    static final int KEYFRAME_HEAD=0xb00a;
    static final int XDATA_SECTION=0x8000;
    static final int ROT_TRACK_TAG=0xb021;
    static final int SCALE_TRACK  =0xb022;
    static final int EDIT_3DS     =0x3d3d;
    static final int MESH_VERSION =0x3d3e;
}
