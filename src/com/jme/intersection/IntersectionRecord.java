package com.jme.intersection;

import com.jme.math.Vector3f;
import com.jme.system.JmeException;

public class IntersectionRecord {

    private float[] distances;
    private Vector3f[] points;

    public IntersectionRecord() { }
    
    public IntersectionRecord(float[] distances, Vector3f[] points) {
        if (distances.length != points.length) throw new JmeException("The distances and points variables must have an equal number of elements.");
        this.distances = distances;
        this.points = points;
    }

    public int getQuantity() {
        return points.length;
    }
    
    public Vector3f getIntersectionPoint(int index) {
        return points[index];
    }
    
    public float getIntersectionDistance(int index) {
        return distances[index];
    }
    
    public float getClosestDistance() {
        float min = Float.MAX_VALUE;
        if (distances != null)
            for (float val : distances)
                if (val < min)
                    min = val;
        return min;
    }

}
