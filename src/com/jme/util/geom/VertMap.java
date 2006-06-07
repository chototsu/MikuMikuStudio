package com.jme.util.geom;

import com.jme.scene.batch.TriangleBatch;

public class VertMap {

    private int[] lookupTable;

    public VertMap(TriangleBatch batch) {
        setupTable(batch);
    }
    
    private void setupTable(TriangleBatch batch) {
        lookupTable = new int[batch.getVertexCount()];
        for (int x = 0; x < lookupTable.length; x++)
            lookupTable[x] = x;
    }
    
    public int getNewIndex(int oldIndex) {
        return lookupTable[oldIndex];
    }
    
    public void replaceIndex(int oldIndex, int newIndex) {
        for (int x = 0; x < lookupTable.length; x++)
            if (lookupTable[x] == oldIndex)
                lookupTable[x] = newIndex;
    }

    public void decrementIndices(int above) {
        for (int x = lookupTable.length; --x >= 0; )
            if (lookupTable[x] >= above) lookupTable[x]--;
    }
    
}
