/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.scene;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.util.LoggingSystem;

/**
 * <code>SharedMesh</code> allows the sharing of data between multiple nodes.
 * A provided TriMesh is used as the model for this node. This allows the user
 * to place multiple copies of the same object throughout the scene without
 * having to duplicate data. It should be known that any change to the provided
 * target mesh will affect the appearance of this mesh, including animations.
 * Secondly, the SharedMesh is read only. Any attempt to write to the mesh data
 * via set* methods, will result in a warning being logged and nothing else. Any
 * changes to the mesh should happened to the target mesh being shared.
 * <br>
 * <b>Important:</b> It is highly recommended that the Target mesh is NOT
 * placed into the scenegraph, as it's translation, rotation and scale are
 * replaced by the shared meshes using it before they are rendered. <br>
 * <b>Note:</b> Special thanks to Kevin Glass.
 * 
 * @author Mark Powell
 * @version $id$
 */
public class SharedMesh extends TriMesh {
	private static final long serialVersionUID = 1L;

	private TriMesh target;

	/**
	 * Constructor creates a new <code>SharedMesh</code> object.
	 * 
	 * @param name
	 *            the name of this shared mesh.
	 * @param target
	 *            the TriMesh to share the data.
	 */
	public SharedMesh(String name, TriMesh target) {
		super(name);
		
		if((target.getType() & Spatial.SHARED_MESH) != 0) {
			setTarget(((SharedMesh)target).getTarget());
		} else {
			setTarget(target);
		}
	}
	
	public int getType() {
		return (Spatial.GEOMETRY | Spatial.TRIMESH | Spatial.SHARED_MESH);
	}

	/**
	 * <code>setTarget</code> sets the shared data mesh.
	 * 
	 * @param target
	 *            the TriMesh to share the data.
	 */
	public void setTarget(TriMesh target) {
		this.target = target;

		for (int i = 0; i < RenderState.RS_MAX_STATE; i++) {
            RenderState renderState = target.getRenderState( i );
            if (renderState != null) {
                setRenderState(renderState );
            }
		}
		
		this.localRotation.set(target.getLocalRotation());
		this.localScale.set(target.getLocalScale());
		this.localTranslation.set(target.getLocalTranslation());
	}
	
	/**
	 * <code>getTarget</code> returns the mesh that is being shared by
	 * this object.
	 * @return the mesh being shared.
	 */
	public TriMesh getTarget() {
		return target;
	}
	
/**
	 * <code>reconstruct</code> is not supported in SharedMesh.
	 *
	 * @param vertices
	 *            the new vertices to use.
	 * @param normals
	 *            the new normals to use.
	 * @param colors
	 *            the new colors to use.
	 * @param textureCoords
	 *            the new texture coordinates to use (position 0).
	 */
	public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
			FloatBuffer colors, FloatBuffer textureCoords) {
		LoggingSystem.getLogger().log(Level.INFO, "SharedMesh will ignore reconstruct.");
	}
	
	/**
	 * <code>setVBOInfo</code> is not supported in SharedMesh.
	 */
	public void setVBOInfo(VBOInfo info) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}
	
	/**
	 * <code>getVBOInfo</code> returns the target mesh's vbo info.
	 */
	public VBOInfo getVBOInfo() {
	    return target.getVBOInfo();
	}
	
	/**
	 *
	 * <code>setSolidColor</code> is not supported by SharedMesh.
	 *
	 * @param color
	 *            the color to set.
	 */
	public void setSolidColor(ColorRGBA color) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * <code>setRandomColors</code> is not supported by SharedMesh.
	 */
	public void setRandomColors() {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * <code>getVertexBuffer</code> returns the float buffer that
	 * contains the target geometry's vertex information.
	 *
	 * @return the float buffer that contains the target geometry's vertex
	 *         information.
	 */
	public FloatBuffer getVertexBuffer() {
		return target.getVertexBuffer();
	}

	/**
	 * <code>setVertexBuffer</code> is not supported by SharedMesh.
	 *
	 * @param buff
	 *            the new vertex buffer.
	 */
	public void setVertexBuffer(FloatBuffer buff) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * Returns the number of vertexes defined in the target's Geometry object.
	 *
	 * @return The number of vertexes in the target Geometry object.
	 */
	public int getVertQuantity() {
		return target.getVertQuantity();
	}

	/**
	 * <code>getNormalBuffer</code> retrieves the target geometry's normal
	 * information as a float buffer.
	 *
	 * @return the float buffer containing the target geometry information.
	 */
	public FloatBuffer getNormalBuffer() {
		return target.getNormalBuffer();
	}

	/**
	 * <code>setNormalBuffer</code> is not supported by SharedMesh.
	 *
	 * @param buff
	 *            the new normal buffer.
	 */
	public void setNormalBuffer(FloatBuffer buff) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * <code>getColorBuffer</code> retrieves the float buffer that
	 * contains the target geometry's color information.
	 *
	 * @return the buffer that contains the target geometry's color information.
	 */
	public FloatBuffer getColorBuffer() {
		return target.getColorBuffer();
	}

	/**
	 * <code>setColorBuffer</code> is not supported by SharedMesh.
	 *
	 * @param buff
	 *            the new color buffer.
	 */
	public void setColorBuffer(FloatBuffer buff) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}
	
	/**
     * 
     * <code>getIndexAsBuffer</code> retrieves the target's indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer() {
        return target.getIndexBuffer();
    }

    /**
     * 
     * <code>setIndexBuffer</code> is not supported by SharedMesh.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices) {
    	LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
    }

    /**
     * Stores in the <code>storage</code> array the indices of triangle
     * <code>i</code>. If <code>i</code> is an invalid index, or if
     * <code>storage.length<3</code>, then nothing happens
     * 
     * @param i
     *            The index of the triangle to get.
     * @param storage
     *            The array that will hold the i's indexes.
     */
    public void getTriangle(int i, int[] storage) {
        target.getTriangle(i, storage);
    }

    /**
     * Stores in the <code>vertices</code> array the vertex values of triangle
     * <code>i</code>. If <code>i</code> is an invalid triangle index,
     * nothing happens.
     * 
     * @param i
     * @param vertices
     */
    public void getTriangle(int i, Vector3f[] vertices) {
        getTriangle(i, vertices);
    }

    /**
     * Returns the number of triangles the target TriMesh contains.
     * 
     * @return The current number of triangles.
     */
    public int getTriangleQuantity() {
        return target.getTriangleQuantity();
    }

	/**
	 *
	 * <code>copyTextureCoords</code> is not supported by SharedMesh.
	 *
	 * @param fromIndex
	 *            the coordinates to copy.
	 * @param toIndex
	 *            the texture unit to set them to.
	 */
	public void copyTextureCoords(int fromIndex, int toIndex) {
	    
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * <code>getTextureBuffer</code> retrieves the target geometry's texture
	 * information contained within a float buffer.
	 *
	 * @return the float buffer that contains the target geometry's texture
	 *         information.
	 */
	public FloatBuffer getTextureBuffer() {
		return target.getTextureBuffer();
	}

	/**
	 * <code>getTextureBuffers</code> retrieves the target geometry's texture
	 * information contained within a float buffer array.
	 *
	 * @return the float buffers that contain the target geometry's texture
	 *         information.
	 */
	public FloatBuffer[] getTextureBuffers() {
		return target.getTextureBuffers();
	}

	/**
	 *
	 * <code>getTextureAsFloatBuffer</code> retrieves the texture buffer of a
	 * given texture unit.
	 *
	 * @param textureUnit
	 *            the texture unit to check.
	 * @return the texture coordinates at the given texture unit.
	 */
	public FloatBuffer getTextureBuffer(int textureUnit) {
		return target.getTextureBuffer(textureUnit);
	}

	/**
     * <code>setTextureBuffer</code> is not supported by SharedMesh.
     * 
     * @param buff
     *            the new vertex buffer.
     */
	public void setTextureBuffer(FloatBuffer buff) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
     * <code>setTextureBuffer</code> not supported by SharedMesh
     * 
     * @param buff
     *            the new vertex buffer.
     */
	public void setTextureBuffer(FloatBuffer buff, int position) {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 *
	 * <code>getNumberOfUnits</code> returns the number of texture units the target
	 * geometry supports.
	 *
	 * @return the number of texture units supported by the target geometry.
	 */
	public int getNumberOfUnits() {
	    return target.getNumberOfUnits();
	}

	/**
	 * clearBuffers is not supported by SharedMesh
	 */
	public void clearBuffers() {
		LoggingSystem.getLogger().log(Level.WARNING, "SharedMesh does not allow the manipulation" +
		"of the the mesh data.");
	}

	/**
	 * <code>updateWorldBound</code> updates the bounding volume that contains
	 * this geometry. The location of the geometry is based on the location of
	 * all this node's parents.
	 * 
	 * @see com.jme.scene.Spatial#updateWorldBound()
	 */
	public void updateWorldBound() {
		if (target.getModelBound() != null) {
			worldBound = target.getModelBound().transform(worldRotation,
					worldTranslation, worldScale, worldBound);
		}
	}

	/**
	 * <code>updateBound</code> recalculates the bounding object assigned to
	 * the geometry. This resets it parameters to adjust for any changes to the
	 * vertex information.
	 * 
	 */
	public void updateModelBound() {
		if (target.getModelBound() != null) {
			target.getModelBound().computeFromPoints(vertBuf);
			updateWorldBound();
		}
	}

	/**
	 * returns the model bound of the target object.
	 */
	public BoundingVolume getModelBound() {
		return target.getModelBound();
	}

	/**
	 * draw renders the target mesh, at the translation, rotation and scale of
	 * this shared mesh.
	 * 
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 */
	public void draw(Renderer r) {
		applyStates();

		if (!r.isProcessingQueue()) {
			if (r.checkAndAdd(this))
				return;
		}
		target.setLocalTranslation(worldTranslation);
		target.setLocalRotation(worldRotation);
		target.setLocalScale(worldScale);
		r.draw(target);
	}
	
	/**
     * This function checks for intersection between the target trimesh and the given
     * one. On the first intersection, true is returned.
     * 
     * @param toCheck
     *            The intersection testing mesh.
     * @return True if they intersect.
     */
    public boolean hasTriangleCollision(TriMesh toCheck) {
    	
    	target.setLocalTranslation(worldTranslation);
		target.setLocalRotation(worldRotation);
		target.setLocalScale(worldScale);
		
		return target.hasTriangleCollision(toCheck);
    }

    /**
     * This function finds all intersections between this trimesh and the
     * checking one. The intersections are stored as Integer objects of Triangle
     * indexes in each of the parameters.
     * 
     * @param toCheck
     *            The TriMesh to check.
     * @param thisIndex
     *            The array of triangle indexes intersecting in this mesh.
     * @param otherIndex
     *            The array of triangle indexes intersecting in the given mesh.
     */
    public void findTriangleCollision(TriMesh toCheck, ArrayList thisIndex,
            ArrayList otherIndex) {
    	target.setLocalTranslation(worldTranslation);
		target.setLocalRotation(worldRotation);
		target.setLocalScale(worldScale);
		
		target.findTriangleCollision(toCheck, thisIndex, otherIndex);
    }

    /**
     * 
     * <code>findTrianglePick</code> determines the triangles of the target trimesh
     * that are being touched by the ray. The indices of the triangles are
     * stored in the provided ArrayList.
     * 
     * @param toTest
     *            the ray to test.
     * @param results
     *            the indices to the triangles.
     */
    public void findTrianglePick(Ray toTest, ArrayList results) {
    	target.setLocalTranslation(worldTranslation);
		target.setLocalRotation(worldRotation);
		target.setLocalScale(worldScale);
		
		target.findTrianglePick(toTest, results);
    }

}
