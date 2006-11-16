package com.jme.scene.state.lwjgl.records;

import java.nio.DoubleBuffer;

import com.jme.scene.state.ClipState;
import com.jme.util.geom.BufferUtils;

public class ClipStateRecord extends StateRecord {

    public boolean[] planeEnabled = new boolean[ClipState.MAX_CLIP_PLANES];
    public DoubleBuffer buf = BufferUtils.createDoubleBuffer(4);

}
