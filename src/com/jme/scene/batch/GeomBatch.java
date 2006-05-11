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

package com.jme.scene.batch;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.LoggingSystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

public class GeomBatch extends Geometry implements Serializable, Savable {

    private static final long serialVersionUID = -6361186042554187448L;
    
    /** The local bounds of this Geometry object. */
    protected BoundingVolume bound;

    /** The number of vertexes in this geometry. */
	protected int vertQuantity = 0;

	/** The geometry's per vertex color information. */
	protected transient FloatBuffer colorBuf;

	/** The geometry's per vertex normal information. */
	protected transient FloatBuffer normBuf;

	/** The geometry's vertex information. */
	protected transient FloatBuffer vertBuf;

	/** The geometry's per Texture per vertex texture coordinate information. */
	protected transient ArrayList<FloatBuffer> texBuf;

	/** The geometry's VBO information. */
	protected VBOInfo vboInfo;

	protected boolean enabled = true;
    
    public transient Geometry parentGeom = null;

    protected boolean castsShadows = true;

    protected boolean hasDirtyVertices = false;

	/**
	 * Non -1 values signal that drawing this scene should use the provided
	 * display list instead of drawing from the buffers.
	 */
	protected int displayListID = -1;

	public GeomBatch() {
        super();
		texBuf = new ArrayList<FloatBuffer>(1);
        texBuf.add(null);
	}
    
    @Override
    protected void setupBatchList() {
        ; // do nothing
    }

    /**
     * adds a batch to the batch list of the geometry.
     * 
     * @param batch
     *            the batch to add.
     */
    public void addBatch(GeomBatch batch) {
        ;
    }

    /**
     * removes the batch supplied. If the currently active batch is the one
     * supplied, the active batch is reset to the first batch in the list. If
     * the last batch is removed, then the active batch is set to null.
     * 
     * @param batch
     *            the batch to remove from the list.
     */
    public void removeBatch(GeomBatch batch) {
        ;
    }

    /**
     * removes the batch defined by the index supplied. If the currently active
     * batch is the one defined by the index, the active batch is reset to the
     * first batch in the list. If the last batch is removed, then the active
     * batch is set to null.
     * 
     * @param index
     *            the index to the batch to remove from the list.
     */
    public void removeBatch(int index) {
        ;
    }

    /**
     * Retrieves the batch at the supplied index. If the index is invalid, this
     * could throw an exception.
     * 
     * @param index
     *            the index to retrieve the batch from.
     * @return the selected batch.
     */
    public GeomBatch getBatch(int index) {
        return null;
    }

    /**
     * clearBatches removes all batches from this geometry. Effectively making
     * the geometry contain no render data.
     */
    public void clearBatches() {
        ;
    }

    /**
     * returns the number of batches contained in this geometry.
     * 
     * @return the number of batches in this geometry.
     */
    public int getBatchCount() {
        return 0;
    }

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public FloatBuffer getColorBuffer() {
		return colorBuf;
	}

	public void setColorBuffer(FloatBuffer colorBuf) {
		this.colorBuf = colorBuf;
	}

	public int getDisplayListID() {
		return displayListID;
	}

	public void setDisplayListID(int displayListID) {
		this.displayListID = displayListID;
	}

	public FloatBuffer getNormalBuffer() {
		return normBuf;
	}

	public void setNormalBuffer(FloatBuffer normBuf) {
		this.normBuf = normBuf;
	}

	public ArrayList<FloatBuffer> getTextureBuffers() {
		return texBuf;
	}

	public void setTextureBuffers(ArrayList<FloatBuffer> texBuf) {
		this.texBuf = texBuf;
	}

	public VBOInfo getVBOInfo() {
		return vboInfo;
	}

    public void setVBOInfo(VBOInfo info) {
        vboInfo = info;
        if (vboInfo != null) {
            vboInfo.resizeTextureIds(texBuf.size());
        }
    }

	public FloatBuffer getVertexBuffer() {
		return vertBuf;
	}

	public void setVertexBuffer(FloatBuffer vertBuf) {
		this.vertBuf = vertBuf;
		if (vertBuf != null)
			vertQuantity = vertBuf.capacity() / 3;
		else
			vertQuantity = 0;
	}

	public int getVertexCount() {
		return vertQuantity;
	}

	public void setVertexCount(int vertQuantity) {
		this.vertQuantity = vertQuantity;
	}

	public void clearTextureBuffers() {
        if(texBuf != null) {
            texBuf.clear();
        }
	}

	public void addTextureCoordinates(FloatBuffer textureCoords) {
        if(texBuf != null) {
            texBuf.add(textureCoords);
        }
	}

	public void resizeTextureIds(int i) {
		vboInfo.resizeTextureIds(i);
	}

	public void setSolidColor(ColorRGBA color) {
		if (colorBuf == null)
			colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		colorBuf.rewind();
		for (int x = 0, cLength = colorBuf.remaining(); x < cLength; x += 4) {
			colorBuf.put(color.r);
			colorBuf.put(color.g);
			colorBuf.put(color.b);
			colorBuf.put(color.a);
		}
		colorBuf.flip();
	}

	public void setRandomColors() {
		if (colorBuf == null)
			colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		for (int x = 0, cLength = colorBuf.capacity(); x < cLength; x += 4) {
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(1);
		}
		colorBuf.flip();
	}

	public void copyTextureCoordinates(int fromIndex, int toIndex, float factor) {
		if (texBuf == null)
			return;

		if (fromIndex < 0 || fromIndex >= texBuf.size()
				|| texBuf.get(fromIndex) == null) {
			return;
		}

		if (toIndex < 0 || toIndex == fromIndex) {
			return;
		}

		if (toIndex >= texBuf.size()) {
			while (toIndex >= texBuf.size()) {
				texBuf.add(null);
			}
		}

		FloatBuffer buf = texBuf.get(toIndex);
		FloatBuffer src = texBuf.get(fromIndex);
		if (buf == null || buf.capacity() != src.capacity()) {
			buf = BufferUtils.createFloatBuffer(src.capacity());
			texBuf.set(toIndex, buf);
		}
		buf.clear();
		int oldLimit = src.limit();
		src.clear();
		for (int i = 0, len = buf.capacity(); i < len; i++) {
			buf.put(factor * src.get());
		}
		src.limit(oldLimit);
		buf.limit(oldLimit);

		if (vboInfo != null) {
			vboInfo.resizeTextureIds(this.texBuf.size());
		}
	}

	public FloatBuffer getTextureBuffer(int textureUnit) {
		if (texBuf == null)
			return null;
		if (textureUnit >= texBuf.size())
			return null;
		return  texBuf.get(textureUnit);
	}

	public void setTextureBuffer(FloatBuffer buff, int position) {
		if (position >= texBuf.size()) {
			while (position >= texBuf.size()) {
				texBuf.add(null);
			}
		}

		texBuf.set(position, buff);
		if (vboInfo != null) {
			vboInfo.resizeTextureIds(texBuf.size());
		}
	}
    
	/**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
            // vert buffer
            if (getVertexBuffer() == null)
                s.writeInt(0);
            else {
                s.writeInt(getVertexBuffer().capacity());
                getVertexBuffer().rewind();
                for (int x = 0, len = getVertexBuffer().capacity(); x < len; x++)
                    s.writeFloat(getVertexBuffer().get());
            }
    
            // norm buffer
            if (getNormalBuffer() == null)
                s.writeInt(0);
            else {
                s.writeInt(getNormalBuffer().capacity());
                getNormalBuffer().rewind();
                for (int x = 0, len = getNormalBuffer().capacity(); x < len; x++)
                    s.writeFloat(getNormalBuffer().get());
            }
    
            // color buffer
            if (getColorBuffer() == null)
                s.writeInt(0);
            else {
                s.writeInt(getColorBuffer().capacity());
                getColorBuffer().rewind();
                for (int x = 0, len = getColorBuffer().capacity(); x < len; x++)
                    s.writeFloat(getColorBuffer().get());
            }
    
            // tex buffer
            if (getTextureBuffers() == null || getTextureBuffers().size() == 0)
                s.writeInt(0);
            else {
                s.writeInt(getTextureBuffers().size());
                for (int i = 0; i < getTextureBuffers().size(); i++) {
                    if (getTextureBuffers().get(i) == null)
                        s.writeInt(0);
                    else {
                        FloatBuffer src = getTextureBuffers().get(i);
                        s.writeInt(src.capacity());
                        src.rewind();
                        for (int x = 0, len = src.capacity(); x < len; x++)
                            s.writeFloat(src.get());
                    }
                }
            }
        
    }

    /**
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        // vert buffer
        int len = s.readInt();
        if (len == 0) {
            setVertexBuffer(null);
        } else {
            FloatBuffer buf = BufferUtils.createFloatBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readFloat());
            setVertexBuffer(buf);            
        }
        // norm buffer
        len = s.readInt();
        if (len == 0) {
            setNormalBuffer(null);
        } else {
            FloatBuffer buf = BufferUtils.createFloatBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readFloat());
            setNormalBuffer(buf);            
        }
        // color buffer
        len = s.readInt();
        if (len == 0) {
            setColorBuffer(null);
        } else {
            FloatBuffer buf = BufferUtils.createFloatBuffer(len);
            for (int x = 0; x < len; x++)
                buf.put(s.readFloat());
            setColorBuffer(buf);            
        }
        // tex buffers
        len = s.readInt();
        if (len == 0) {
            setTextureBuffers(null);
        } else {
           setTextureBuffers(new ArrayList<FloatBuffer>(1));
            for (int i = 0; i < len; i++) {
                int len2 = s.readInt();
                if (len2 == 0) {
                    setTextureBuffer(null, i);
                } else {
                    FloatBuffer buf = BufferUtils.createFloatBuffer(len2);
                    for (int x = 0; x < len2; x++)
                        buf.put(s.readFloat());
                    setTextureBuffer(buf, i);            
                }
            }
        }
    }

    /**
     * Removes this batch from the batchlist of it's containing parent.
     */
    public boolean removeFromParent() {
        parentGeom.removeBatch(this);
        return super.removeFromParent();
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }
    
    public String toString() {
    	if (parentGeom != null)
    		return parentGeom.getName() + ": Batch "+parentGeom.getBatchIndex(this);
    	else return "orphaned batch";
    }

    public void setCastsShadows(boolean castsShadows) {
        this.castsShadows = castsShadows;
    }

	public Vector3f randomVertex(Vector3f fill) {
		if (getVertexBuffer() == null)
			return null;
		int i = (int) (FastMath.nextRandomFloat() * getVertexCount());
		
		if (fill == null) fill = new Vector3f();
		BufferUtils.populateFromBuffer(fill, getVertexBuffer(), i);
		
		localToWorld(fill, fill);

		return fill;
	}

    @Override
    public void findCollisions(Spatial scene, CollisionResults results) {
        // TODO Auto-generated method stub
        
    }

    public void findPick(Ray ray, PickResults results) {
        if (getWorldBound() == null || !isCollidable) {
            return;
        }
        if (getWorldBound().intersects(ray)) {
            // find the triangle that is being hit.
            // add this node and the triangle to the PickResults list.
            results.addPick(ray, this);
        }
    }
    
    @Override
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        // TODO Auto-generated method stub
        return false;
    }

    public int getType() {
        return (Spatial.GEOMETRY | Spatial.GEOMBATCH);
    }

    public int getNumberOfUnits() {
        if (texBuf == null) return 0;
        return texBuf.size();
    }
    
    @Override
    public void lockMeshes(Renderer r) {
        updateRenderState();
        lockedMode |= LOCKED_MESH_DATA;
        
        setDisplayListID(r.createDisplayList(this));
    }
    
    @Override
    public void unlockMeshes(Renderer r) {
        lockedMode &= ~LOCKED_MESH_DATA;
        
        if (getDisplayListID() != -1) {
            r.releaseDisplayList(getDisplayListID());
            setDisplayListID(-1);
        }
    }


    protected void updateWorldTranslation() {
        if (parentGeom != null) {
            worldTranslation = parentGeom.localToWorld( localTranslation, worldTranslation );
        } else {
            worldTranslation.set(localTranslation);
        }
    }
    

    protected void updateWorldRotation() {
        if (parentGeom != null) {
            parentGeom.getWorldRotation().mult(localRotation, worldRotation);
        } else {
            worldRotation.set(localRotation);
        }
    }

    protected void updateWorldScale() {
        if (parentGeom != null) {
            worldScale.set(parentGeom.getWorldScale()).multLocal(localScale);
        } else {
            worldScale.set(localScale);
        }
    }

    /**
     * <code>setModelBound</code> sets the bounding object for this geometry.
     * 
     * @param modelBound
     *            the bounding object for this geometry.
     */
    public void setModelBound(BoundingVolume modelBound) {
        this.worldBound = null;
        this.bound = modelBound;
    }

    /**
     * <code>updateBound</code> recalculates the bounding object assigned to
     * the geometry. This resets it parameters to adjust for any changes to the
     * vertex information.
     */
    public void updateModelBound() {
        if (bound != null) {
            bound.computeFromPoints(getVertexBuffer());
            updateWorldBound();
        }
    }
    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location of
     * all this node's parents.
     *
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        if (bound != null) {
            worldBound = bound.transform(worldRotation, worldTranslation,
                    worldScale, worldBound);
        }
    }
    
    /**
     *
     * <code>propagateBoundToRoot</code> passes the new world bound up the
     * tree to the root.
     *
     */
    public void propagateBoundToRoot() {
        if (parentGeom != null) {
            parentGeom.updateWorldBound();
            parentGeom.propagateBoundToRoot();
        }
    }
    
    public void onDraw(Renderer r) {
        int cm = getCullMode();
        if (cm == CULL_ALWAYS) {
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();

        // check to see if we can cull this node
        frustrumIntersects = (parentGeom != null ? parentGeom.getLastFrustumIntersection()
                : Camera.INTERSECTS_FRUSTUM);
        if (cm == CULL_DYNAMIC && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
            frustrumIntersects = camera.contains(worldBound);
        }

        if (cm == CULL_NEVER || frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
            draw(r);
        }
        camera.setPlaneState(state);
    }

    public int getRenderQueueMode() {
        if (renderQueueMode != Renderer.QUEUE_INHERIT)
            return renderQueueMode;
        else if (parentGeom != null)
            return parentGeom.getRenderQueueMode();
        else
            return Renderer.QUEUE_SKIP;
    }

    /**
     * Returns this spatial's light combine mode. If the mode is set to inherit,
     * then the spatial gets its combine mode from its parent.
     *
     * @return The spatial's light current combine mode.
     */
    public int getLightCombineMode() {
        if (lightCombineMode != LightState.INHERIT)
            return lightCombineMode;
        else if (parentGeom != null)
            return parentGeom.getLightCombineMode();
        else
            return LightState.COMBINE_FIRST;
    }

    /**
     * Returns this spatial's texture combine mode. If the mode is set to
     * inherit, then the spatial gets its combine mode from its parent.
     *
     * @return The spatial's texture current combine mode.
     */
    public int getTextureCombineMode() {
        if (textureCombineMode != TextureState.INHERIT)
            return textureCombineMode;
        else if (parentGeom != null)
            return parentGeom.getTextureCombineMode();
        else
            return TextureState.COMBINE_CLOSEST;
    }


    /**
     * @see #setCullMode(int)
     *
     * @return the cull mode of this spatial
     */
    public int getCullMode() {
        if (cullMode != CULL_INHERIT)
            return cullMode;
        else if (parentGeom != null)
            return parentGeom.getCullMode();
        else return CULL_DYNAMIC;
    }

    public int getNormalsMode() {
        if (normalsMode != NM_INHERIT)
            return normalsMode;
        else if (parentGeom != null)
            return parentGeom.getNormalsMode();
        else
            return NM_GL_NORMALIZE_IF_SCALED;
    }

    /**
     * Called during updateRenderState(Stack[]), this function goes up the scene
     * graph tree until the parent is null and pushes RenderStates onto the
     * states Stack array.
     *
     * @param states
     *            The Stack[] to push states onto.
     */
    @SuppressWarnings("unchecked")
    public void propagateStatesFromRoot(Stack[] states) {
        // traverse to root to allow downward state propagation
        if (parentGeom != null)
            parentGeom.propagateStatesFromRoot(states);

        // push states onto current render state stack
        for (int x = 0; x < RenderState.RS_MAX_STATE; x++)
            if (getRenderState(x) != null)
                states[x].push(getRenderState(x));
    }
    
    protected void applyRenderState(Stack[] states) {
        for (int x = 0; x < states.length; x++) {
            if (states[x].size() > 0) {
                this.states[x] = ((RenderState) states[x].peek()).extract(
                        states[x], this);
            } else {
                this.states[x] = Renderer.defaultStateList[x];
            }
        }
    }

    /**
     * <code>getWorldCoords</code> translates/rotates and scales the
     * coordinates of this Geometry (from the given batch index) to world
     * coordinates based on its world settings. The results are stored in the
     * given FloatBuffer. If given FloatBuffer is null, one is created.
     * 
     * @param store
     *            the FloatBuffer to store the results in, or null if you want
     *            one created.
     * @param batchIndex
     *            the batch to process
     * @return store or new FloatBuffer if store == null.
     */
    public FloatBuffer getWorldCoords(FloatBuffer store) {
        if (store == null || store.capacity() != getVertexBuffer().capacity())
            store = BufferUtils.clone(getVertexBuffer());
        for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
            BufferUtils.populateFromBuffer(compVect, store, v);
            worldRotation.multLocal(compVect).multLocal(worldScale).addLocal(
                    worldTranslation);
            BufferUtils.setInBuffer(compVect, store, v);
        }
        store.clear();
        return store;
    }

    public void draw(Renderer r) {
        super.draw(r);
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(colorBuf, "colorBuf", null);
        capsule.write(normBuf, "normBuf", null);
        capsule.write(vertBuf, "vertBuf", null);
        capsule.writeFloatBufferArrayList(texBuf, "texBuf", new ArrayList<FloatBuffer>(1));
        capsule.write(vboInfo, "vboInfo", null);
        capsule.write(enabled, "enabled", true);
        capsule.write(castsShadows, "castsShadows", true);
        capsule.write(bound, "bound", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        colorBuf = capsule.readFloatBuffer("colorBuf", null);
        normBuf = capsule.readFloatBuffer("normBuf", null);
        vertBuf = capsule.readFloatBuffer("vertBuf", null);
        if (vertBuf != null)
            vertQuantity = vertBuf.capacity() / 3;
        else
            vertQuantity = 0;
        texBuf = capsule.readFloatBufferArrayList("texBuf", new ArrayList<FloatBuffer>(1));
        vboInfo = (VBOInfo)capsule.readSavable("vboInfo", null);

        enabled = capsule.readBoolean("enabled", true);
        castsShadows = capsule.readBoolean("castsShadows", true);
        bound = (BoundingVolume) capsule.readSavable("bound", null);
        if (bound != null) worldBound = bound.clone(null);
    }

    /**
     * <code>getModelBound</code> retrieves the bounding object that contains
     * the batch vertices.
     * 
     * @return the bounding object for this geometry.
     */
    public BoundingVolume getModelBound() {
        return bound;
    }

    public boolean hasDirtyVertices() {
        return hasDirtyVertices;
    }

    public void setHasDirtyVertices(boolean flag) {
        hasDirtyVertices = flag;
    }
    
    // methods we don't care about... Tried to refactor to avoid inheriting
    // these methods, but could not find a clean solution.  -- Josh

    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, FloatBuffer textureCoords, int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public void setVBOInfo(int batchIndex, VBOInfo info) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }
    
    public VBOInfo getVBOInfo(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public FloatBuffer getVertexBuffer(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public void setVertexBuffer(int batchIndex, FloatBuffer buff) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public FloatBuffer getNormalBuffer(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public void setNormalBuffer(int batchIndex, FloatBuffer buff) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public FloatBuffer getColorBuffer(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public void setColorBuffer(int batchIndex, FloatBuffer buff) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public void copyTextureCoords(int batchIndex, int fromIndex, int toIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public void copyTextureCoords(int batchIndex, int fromIndex, int toIndex, float factor) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public FloatBuffer[] getTextureBuffers(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public FloatBuffer getTextureBuffer(int batchIndex, int textureUnit) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public void setTextureBuffer(int batchIndex, FloatBuffer buff) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }

    public void setTextureBuffer(int batchIndex, FloatBuffer buff, int position) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
    }
    
    public int getNumberOfUnits(int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return -1;
    }

    public FloatBuffer getWorldCoords(FloatBuffer store, int batchIndex) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return null;
    }

    public int getBatchIndex(GeomBatch bat) {
        LoggingSystem.getLogger().log(Level.WARNING, "This method should not be called on a batch.");
        return -1;
    }

}
