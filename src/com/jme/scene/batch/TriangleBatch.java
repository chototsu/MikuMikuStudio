package com.jme.scene.batch;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.bounding.OBBTree;
import com.jme.math.Vector3f;
import com.jme.scene.CompositeMesh;

/**
 * TriangleBatch provides an extension of GeomBatch adding the capabilities for
 * triangle indices and OBBTree collisions. These batch elements are usually contained
 * in the TriMesh object, while Geometry contains GeomBatch elements.
 * @author Mark Powell
 *
 */
public class TriangleBatch extends GeomBatch {
	public static final int TRIANGLES = 0;
	
	protected transient IntBuffer indexBuffer;
	

	protected int triangleQuantity = -1;
    
    private OBBTree collisionTree;

    private static Vector3f[] triangles;

    private int mode = TRIANGLES;
    
    /**
     * Default constructor that creates a new TriangleBatch.
     *
     */
    public TriangleBatch() {
    	super();
    }
    
    public void setMode(int mode) {
    	this.mode = mode;
    }
    
    public int getMode() {
    	return mode;
    }

	public static Vector3f[] getTriangles() {
		return triangles;
	}

	public static void setTriangles(Vector3f[] triangles) {
		TriangleBatch.triangles = triangles;
	}

	public OBBTree getCollisionTree() {
		return collisionTree;
	}

	public void setCollisionTree(OBBTree collisionTree) {
		this.collisionTree = collisionTree;
	}

	public IntBuffer getIndexBuffer() {
		return indexBuffer;
	}

	public void setIndexBuffer(IntBuffer indices) {
		this.indexBuffer = indices;
        if (indices == null) triangleQuantity = 0;
        else triangleQuantity = indices.capacity() / 3;
	}

	public int getTriangleQuantity() {
		return triangleQuantity;
	}

	public void setTriangleQuantity(int triangleQuantity) {
		this.triangleQuantity = triangleQuantity;
	}
    
    
}
