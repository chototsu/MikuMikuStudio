package com.jme.scene.geometryinstancing.instance;

import com.jme.scene.batch.TriangleBatch;
import java.util.ArrayList;

/**
 * <code>GeometryBatchCreator</code> is a container class for
 * <code>GeometryInstances</code>.
 *
 * @author Patrik Lindegrén
 */
public class GeometryBatchCreator {
    protected ArrayList<GeometryInstance> instances;
    private int nVerts;
    private int nIndices;

    public GeometryBatchCreator() {
        instances = new ArrayList<GeometryInstance>(1);
        nVerts = 0;
        nIndices = 0;
    }

    public void clearInstances() {
        instances.clear();
        nVerts = 0;
        nIndices = 0;
    }

    public void addInstance(GeometryInstance geometryInstance) {
        if (geometryInstance == null) {
            return;
        }
        instances.add(geometryInstance);
        nIndices += geometryInstance.getNumIndices();
        nVerts += geometryInstance.getNumVerts();
    }

    public void removeInstance(GeometryInstance geometryInstance) {
        if (instances.remove(geometryInstance)) {
            nIndices -= geometryInstance.getNumIndices();
            nVerts -= geometryInstance.getNumVerts();
        }
    }

    public int getNumVertices() {
        return nVerts;
    }

    public int getNumIndices() {
        return nIndices;
    }

    public ArrayList<GeometryInstance> getInstances() {
        return instances;
    }

    public void commit(TriangleBatch batch) {
        for (GeometryInstance instance : instances) {
            instance.commit(batch);
        }
    }
}