/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
import java.util.logging.Logger;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.SceneElement;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;

public class GeomBatch extends SceneElement implements Serializable, Savable {
    private static final Logger logger = Logger.getLogger(GeomBatch.class.getName());

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

    /** The geometry's per vertex color information. */
    protected transient FloatBuffer tangentBuf;

    /** The geometry's per vertex normal information. */
    protected transient FloatBuffer binormalBuf;

	/** The geometry's VBO information. */
	protected transient VBOInfo vboInfo;

	protected boolean enabled = true;
    
    protected transient Geometry parentGeom = null;

    protected boolean castsShadows = true;

    protected boolean hasDirtyVertices = false;

    /**
     * The compiled list of renderstates for this geometry, taking into account
     * ancestors' states - updated with updateRenderStates()
     */
    public RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

    protected ColorRGBA defaultColor = new ColorRGBA(ColorRGBA.white);

	/**
	 * Non -1 values signal that drawing this scene should use the provided
	 * display list instead of drawing from the buffers.
	 */
	protected int displayListID = -1;
    
    /** Static computation field */
    protected static Vector3f compVect = new Vector3f();

	public GeomBatch() {
        super();
		texBuf = new ArrayList<FloatBuffer>(1);
        texBuf.add(null);
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
		checkTextureCoordinates();
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
			vertQuantity = vertBuf.limit() / 3;
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
        checkTextureCoordinates();
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

		for (int x = 0, cLength = colorBuf.limit(); x < cLength; x += 4) {
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(FastMath.nextRandomFloat());
			colorBuf.put(1);
		}
		colorBuf.flip();
	}
	
	protected void checkTextureCoordinates() {
		int max = TextureState.getNumberOfFragmentTexCoordUnits();
		if (max == -1)
			return; // No texture state created yet.
		if (texBuf.size() > max) {
			for (int i = max; i < texBuf.size(); i++) {
				if (texBuf.get(i) != null) {
					 logger.warning("Texture coordinates set for unit " + i
                            + ". Only " + max + " units are available.");
				}
			}
		}
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
        if (buf == null || buf.capacity() != src.limit()) {
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
        
        checkTextureCoordinates();
    }

    public void scaleTextureCoordinates(int index, float factor) {
        scaleTextureCoordinates(index, new Vector2f(factor, factor));
    }

    public void scaleTextureCoordinates(int index, Vector2f factor) {
        if (texBuf == null)
            return;

        if (index < 0 || index >= texBuf.size()
                || texBuf.get(index) == null) {
            return;
        }

        FloatBuffer buf = texBuf.get(index);
        
        for (int i = 0, len = buf.limit()/2; i < len; i++) {
            BufferUtils.multInBuffer(factor, buf, i);
        }

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
		checkTextureCoordinates();
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
                s.writeInt(getVertexBuffer().limit());
                getVertexBuffer().rewind();
                for (int x = 0, len = getVertexBuffer().limit(); x < len; x++)
                    s.writeFloat(getVertexBuffer().get());
            }
    
            // norm buffer
            if (getNormalBuffer() == null)
                s.writeInt(0);
            else {
                s.writeInt(getNormalBuffer().limit());
                getNormalBuffer().rewind();
                for (int x = 0, len = getNormalBuffer().limit(); x < len; x++)
                    s.writeFloat(getNormalBuffer().get());
            }
    
            // color buffer
            if (getColorBuffer() == null)
                s.writeInt(0);
            else {
                s.writeInt(getColorBuffer().limit());
                getColorBuffer().rewind();
                for (int x = 0, len = getColorBuffer().limit(); x < len; x++)
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
                        s.writeInt(src.limit());
                        src.rewind();
                        for (int x = 0, len = src.limit(); x < len; x++)
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
        return parentGeom.removeBatch(this);
    }

    public boolean isCastsShadows() {
        return castsShadows;
    }
    
    public String toString() {
    	if (parentGeom != null)
    		return parentGeom.getName() + ": Batch "+parentGeom.getBatchIndex(this);
    	
        return "orphaned batch";
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
		
		parentGeom.localToWorld(fill, fill);

		return fill;
	}

    /**
     * Check if this geom intersects the ray if yes add it to the results.
     * @param ray  ray to check intersection with. The direction of the ray must be normalized (length 1).
     * @param results result list
     */
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

    public int getType() {
        return SceneElement.GEOMBATCH;
    }

    public int getNumberOfUnits() {
        if (texBuf == null) return 0;
        return texBuf.size();
    }
    
    @Override
    public void lockMeshes(Renderer r) {
        if (getDisplayListID() != -1) {
			logger.warning("This GeomBatch already has locked meshes."
                    + "(Use unlockMeshes to clear)");
			return;
		}
    	
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
        if (bound != null && getVertexBuffer() != null) {
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
        if (bound != null && parentGeom != null) {
            worldBound = bound.transform(parentGeom.getWorldRotation(), parentGeom.getWorldTranslation(),
                    parentGeom.getWorldScale(), worldBound);
        }
    }
    
    public void updateGeometricState(float time, boolean initiator) {
        if ((lockedMode & SceneElement.LOCKED_BOUNDS) == 0) {
            updateWorldBound();
            if (initiator) {
                propagateBoundToRoot();
            }
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
    
    /**
     * Called just before renderer starts drawing this batch. If it returns
     * false, we'll skip rendering.
     */
    public boolean predraw(Renderer r) { return true; }

    /**
     * Called after renderer finishes drawing this batch.
     */
    public void postdraw(Renderer r) { }
    
    public void onDraw(Renderer r) {
        int cm = getCullMode();
        if (cm == SceneElement.CULL_ALWAYS) {
            frustrumIntersects = Camera.OUTSIDE_FRUSTUM;
            return;
        } else if (cm == SceneElement.CULL_NEVER) {
            frustrumIntersects = Camera.INTERSECTS_FRUSTUM;
            draw(r);
            return;
        }

        Camera camera = r.getCamera();
        int state = camera.getPlaneState();

        // check to see if we can cull this node
        frustrumIntersects = (parentGeom != null ? parentGeom.getLastFrustumIntersection()
                : Camera.INTERSECTS_FRUSTUM);
        if (cm == SceneElement.CULL_DYNAMIC && frustrumIntersects == Camera.INTERSECTS_FRUSTUM) {
            frustrumIntersects = camera.contains(worldBound);
        }

        if (frustrumIntersects != Camera.OUTSIDE_FRUSTUM) {
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
    
    public FloatBuffer getWorldCoords(FloatBuffer store) {
        if (store == null || store.capacity() != getVertexBuffer().limit())
            store = BufferUtils.clone(getVertexBuffer());
        for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
            BufferUtils.populateFromBuffer(compVect, store, v);
            parentGeom.getWorldRotation().multLocal(compVect).multLocal(parentGeom.getWorldScale()).addLocal(
                    parentGeom.getWorldTranslation());
            BufferUtils.setInBuffer(compVect, store, v);
        }
        store.clear();
        return store;
    }

	public FloatBuffer getWorldNormals(FloatBuffer store) {
		if (store == null || store.capacity() != getNormalBuffer().limit())
			store = BufferUtils.clone(getNormalBuffer());
		for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
			BufferUtils.populateFromBuffer(compVect, store, v);
			parentGeom.getWorldRotation().multLocal(compVect);
			BufferUtils.setInBuffer(compVect, store, v);
		}
		store.clear();
		return store;
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

    public void translatePoints(float x, float y, float z) {
        translatePoints(new Vector3f(x,y,z));
    }

    public void translatePoints(Vector3f amount) {
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.addInBuffer(amount, vertBuf, x);
        }
    }

    public void rotatePoints(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, vertBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, vertBuf, x);
        }
    }
    
    public void rotateNormals(Quaternion rotate) {
        Vector3f store = new Vector3f();
        for (int x = 0; x < vertQuantity; x++) {
            BufferUtils.populateFromBuffer(store, normBuf, x);
            rotate.mult(store, store);
            BufferUtils.setInBuffer(store, normBuf, x);
        }
    }

    /**
     * <code>getDefaultColor</code> returns the color used if no per vertex
     * colors are specified.
     * 
     * @return default color
     */
    public ColorRGBA getDefaultColor() {
        return defaultColor;
    }

    /**
     * <code>setDefaultColor</code> sets the color to be used if no per vertex
     * color buffer is set.
     * 
     * @param color
     */
    public void setDefaultColor(ColorRGBA color) {
        defaultColor = color;
    }

    public void draw(Renderer r) {
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(colorBuf, "colorBuf", null);
        capsule.write(normBuf, "normBuf", null);
        capsule.write(vertBuf, "vertBuf", null);
        capsule.writeFloatBufferArrayList(texBuf, "texBuf", new ArrayList<FloatBuffer>(1));
        capsule.write(enabled, "enabled", true);
        capsule.write(castsShadows, "castsShadows", true);
        capsule.write(bound, "bound", null);
        capsule.write(defaultColor, "defaultColor", ColorRGBA.white);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        
        colorBuf = capsule.readFloatBuffer("colorBuf", null);
        normBuf = capsule.readFloatBuffer("normBuf", null);
        vertBuf = capsule.readFloatBuffer("vertBuf", null);
        if (vertBuf != null)
            vertQuantity = vertBuf.limit() / 3;
        else
            vertQuantity = 0;
        texBuf = capsule.readFloatBufferArrayList("texBuf", new ArrayList<FloatBuffer>(1));
        checkTextureCoordinates();

        enabled = capsule.readBoolean("enabled", true);
        castsShadows = capsule.readBoolean("castsShadows", true);
        bound = (BoundingVolume) capsule.readSavable("bound", null);
        if (bound != null) worldBound = bound.clone(null);
        defaultColor = (ColorRGBA) capsule.readSavable("defaultColor", ColorRGBA.white.clone());
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

    public Geometry getParentGeom() {
        return parentGeom;
    }

    public void setParentGeom(Geometry parentGeom) {
        this.parentGeom = parentGeom;
    }
    
    public void setTangentBuffer(FloatBuffer tangentBuf) {
        this.tangentBuf = tangentBuf;
    }

    public FloatBuffer getTangentBuffer() {
        return this.tangentBuf;
    }

    public void setBinormalBuffer(FloatBuffer binormalBuf) {
        this.binormalBuf = binormalBuf;
    }

    public FloatBuffer getBinormalBuffer() {
        return binormalBuf;
    }
}