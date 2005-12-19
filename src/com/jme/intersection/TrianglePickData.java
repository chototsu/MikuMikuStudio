package com.jme.intersection;

import java.util.ArrayList;

import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.util.LoggingSystem;

/**
 * Pick data for triangle accuracy picking including sort by distance to intersection point.
 */
public class TrianglePickData extends PickData {
    private Vector3f[] worldTriangle;
    private Vector3f intersectionPoint;
    private Quaternion worldRotation;
    private Vector3f worldScale;
    private Vector3f worldTranslation;

    public TrianglePickData( Ray ray, Geometry targetMesh, ArrayList targetTris, boolean checkDistance ) {
        super( ray, targetMesh, targetTris, checkDistance );
    }

    protected float calculateDistance() {
        ArrayList tris = getTargetTris();
        if ( tris.isEmpty() ) {
            return super.calculateDistance();
        }

        TriMesh mesh = (TriMesh) getTargetMesh();

        mesh.updateWorldVectors();
        worldRotation = mesh.getWorldRotation();
        worldScale = mesh.getWorldScale();
        worldTranslation = mesh.getWorldTranslation();

        worldTriangle = new Vector3f[3];
        int i;
        for ( i = 0; i < 3; i++ ) {
            worldTriangle[i] = new Vector3f();
        }
        intersectionPoint = new Vector3f();

        Vector3f[] vertices = new Vector3f[3];
        float distance = Float.MAX_VALUE;
        for ( i = 0; i < tris.size(); i++ ) {
            int triIndex = ( (Integer) tris.get( i ) ).intValue();
            mesh.getTriangle( triIndex, vertices );
            float triDistance = getDistanceToTriangle( vertices );
            if ( triDistance > 0 && triDistance < distance ) {
                distance = triDistance;
            }
        }
        if ( distance == Float.MAX_VALUE ) {
            LoggingSystem.getLogger().warning( "Couldn't detect nearest triangle intersection!" );
        }
        return distance;
    }

    private float getDistanceToTriangle( Vector3f[] triangle ) {
        // Transform triangle to world space
        for ( int i = 0; i < 3; i++ ) {
            worldRotation.mult( triangle[i], worldTriangle[i] ).multLocal( worldScale ).addLocal( worldTranslation );
        }
        // Intersection test
        Ray ray = getRay();
        if ( ray.intersectWhere( worldTriangle[0], worldTriangle[1], worldTriangle[2], intersectionPoint ) ) {
            return ray.getOrigin().distanceSquared( intersectionPoint );
        }
        else {
            // Should not happen
            return Float.MAX_VALUE;
        }
    }
}
