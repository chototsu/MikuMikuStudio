package com.jme.scene.model.XMLparser;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * List of 3ds Chunk ID #'s
 * @author Jack Lindamood
 */
public interface MaxChunkIDs {

    // These must all be diffrent values
    static final short MAIN_3DS     =0x4D4D;
    static final short M3D_VERSION  =0x0002;
    static final short NULL_CHUNK   =0x0000;
    static final short MASTER_SCALE =0x0100;
    static final short NAMED_OBJECT =0x4000;
    static final short OBJ_TRIMESH  =0x4100;
    static final short VERTEX_LIST  =0x4110;
    static final short TEXT_COORDS  =0x4140;
    static final short COORD_SYS    =0x4160;
    static final short FACES_ARRAY  =0x4120;
}
