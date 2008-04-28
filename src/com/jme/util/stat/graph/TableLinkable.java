package com.jme.util.stat.graph;

import com.jme.scene.Line;
import com.jme.util.stat.StatType;

public interface TableLinkable {

    public Line updateLineKey(StatType type, Line lineKey);

}
