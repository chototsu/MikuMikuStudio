package com.jme.scene.model;

import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;

/**
 * Started Date: Jun 11, 2004
 * JointMesh is the same as a TriMesh, but extends to include an index array of joints and to store the original
 * Vertex and Normal information
 *
 *
 * @author Jack Lindamood
 */
public class JointMesh extends TriMesh{
    public int[] jointIndex;
    public Vector3f[] originalVertex;
    public Vector3f[] originalNormal;
    public JointMesh(String name){
        super(name);
    }
}
