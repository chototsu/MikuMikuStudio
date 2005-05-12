package com.jmex.model;

import com.jme.renderer.CloneCreator;
import com.jme.scene.TriMesh;
import com.jme.scene.Spatial;
import com.jme.math.Vector3f;

/**
 * Started Date: Jun 11, 2004 JointMesh is the same as a TriMesh, but extends to
 * include an index array of joints and to store the original Vertex and Normal
 * information
 * 
 * 
 * @author Jack Lindamood
 */
public class JointMesh extends TriMesh {

    private static final long serialVersionUID = 1L;

    public int[] jointIndex;

    public Vector3f[] originalVertex;

    public Vector3f[] originalNormal;

    public JointMesh(String name) {
        super(name);
    }

    public Spatial putClone(Spatial store, CloneCreator properties) {
        JointMesh toReturn;
        if (store == null)
            toReturn = new JointMesh(getName() + "copy");
        else
            toReturn = (JointMesh) store;
        super.putClone(toReturn, properties);
        toReturn.jointIndex = jointIndex;
        toReturn.originalNormal = originalNormal;
        toReturn.originalVertex = originalVertex;

        return toReturn;
    }
}