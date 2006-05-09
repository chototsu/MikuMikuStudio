/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.intersection;

import java.util.ArrayList;

import com.jme.math.FastMath;
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

    public TrianglePickData( Ray ray, Geometry targetMesh, int index, ArrayList targetTris, boolean checkDistance ) {
        super( ray, targetMesh, index, targetTris, checkDistance );
    }

    protected float calculateDistance() {
        ArrayList tris = getTargetTris();
        if ( tris.isEmpty() ) {
            return Float.POSITIVE_INFINITY;
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
        } else distance = FastMath.sqrt(distance);
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
