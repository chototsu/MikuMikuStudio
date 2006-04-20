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

package com.jme.scene;

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Stack;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.state.RenderState;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf node
 * contains the geometric data for rendering objects. It manages all rendering
 * information such as a collection of states and the data for a model.
 * Subclasses define what the model data is.
 *
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Geometry.java,v 1.101 2006-04-20 17:59:00 nca Exp $
 */
public abstract class Geometry extends Spatial implements Serializable {

	/** The local bounds of this Geometry object. */
	protected BoundingVolume bound;

	protected ArrayList batchList;
	protected GeomBatch batch;
	protected int batchCount = 0;
	
    /**
     * The compiled list of renderstates for this geometry, taking into account
     * ancestors' states - updated with updateRenderStates()
     */
	public RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

	/** Non -1 values signal this geometry is a clone of grouping "cloneID". */
	private int cloneID = -1;
    
    protected ColorRGBA defaultColor = new ColorRGBA(ColorRGBA.white);

    protected boolean hasDirtyVertices = false;

    protected boolean normalizedNormals = true;

    /** Static computation field */
    protected static Vector3f compVect = new Vector3f();

	/**
	 * Empty Constructor to be used internally only.
	 */
	public Geometry() {
		batchList = new ArrayList();
		batch = new GeomBatch();
        batch.parentGeom = this;
		batchList.add(batch);
		batchCount = 1;
		
	}

	/**
	 * Constructor instantiates a new <code>Geometry</code> object. This is
	 * the default object which has an empty vertex array. All other data is
	 * null.
	 *
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 *
	 */
	public Geometry(String name) {
		super(name);
		batchList = new ArrayList();
		batch = new GeomBatch();
        batch.parentGeom = this;
		batchList.add(batch);
		batchCount = 1;
		reconstruct(null, null, null, null);
	}
	
	/**
	 * Constructor creates a new <code>Geometry</code> object. During
	 * instantiation the geometry is set including vertex, normal, color and
	 * texture information. 
	 *
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertex
	 *            the points that make up the geometry.
	 * @param normal
	 *            the normals of the geometry.
	 * @param color
	 *            the color of each point of the geometry.
	 * @param texture
	 *            the texture coordinates of the geometry (position 0.)
	 */
	public Geometry(String name, FloatBuffer vertex, FloatBuffer normal,
			FloatBuffer color, FloatBuffer texture) {
		super(name);
		batchList = new ArrayList();
		batch = new GeomBatch();
        batch.parentGeom = this;
		batchList.add(batch);
		batchCount = 1;
		reconstruct(vertex, normal, color, texture);
	}
	
	/**
	 * adds a batch to the batch list of the geometry. 
	 * @param batch the batch to add.
	 */
	public void addBatch(GeomBatch batch) {
        if (batchCount == 0) this.batch = batch;
        batch.parentGeom = this;
		batchList.add(batch);
		batchCount = batchList.size();
	}
	
	/**
	 * removes the batch supplied. If the 
	 * currently active batch is the one supplied, the
	 * active batch is reset to the first batch in the list. If the
	 * last batch is removed, then the active batch is set to null.
	 * @param batch the batch to remove from the list.
	 */
	public void removeBatch(GeomBatch batch) {
		batchList.remove(batch);
        batch.parentGeom = null;
		batchCount = batchList.size();
		if(this.batch == batch) {
			if(batchCount != 0) {
				this.batch = (GeomBatch)batchList.get(0);
			} else {
				this.batch = null;
			}
		}
	}
	
	/**
	 * removes the batch defined by the index supplied. If the 
	 * currently active batch is the one defined by the index, the
	 * active batch is reset to the first batch in the list. If the
	 * last batch is removed, then the active batch is set to null.
	 * @param index the index to the batch to remove from the list.
	 */
	public void removeBatch(int index) {
		if(this.batch == batchList.get(index)) {
            batch.parentGeom = null;
			batchList.remove(index);
			batchCount = batchList.size();
			if(batchCount != 0) {
				this.batch = (GeomBatch)batchList.get(0);
			} else {
				this.batch = null;
			}
		} else {
			GeomBatch b = (GeomBatch)batchList.remove(index);
            b.parentGeom = null;
			batchCount = batchList.size();
		}
	}
	
	/**
	 * Retrieves the currently active batch.
	 * @return the currently active batch
	 */
	public GeomBatch getBatch() {
		return batch;
	}
    
	/**
	 * Retrieves the batch at the supplied index. If the index is invalid,
	 * this could throw an exception.
	 * @param index the index to retrieve the batch from.
	 * @return the selected batch.
	 */
    public GeomBatch getBatch(int index) {
        return (GeomBatch)batchList.get(index);
    }
    
	/**
	 * clearBatches removes all batches from this geometry. Effectively making
	 * the geometry contain no render data.
	 *
	 */
    public void clearBatches() {
        batchList.clear();
    }
	
    /**
     * returns the number of batches contained in this geometry.
     * @return the number of batches in this geometry.
     */
	public int getBatchCount() {
		return batchCount;
	}
	
	/**
	 * sets the active batch to the supplied index. If this index is invalid,
	 * an exception may be thrown.
	 * @param i the index of the batch to set.
	 */
	public void setActiveBatch(int i) {
		batch = getBatch(i);
	}
	
	/**
	 * sets the active batch to the supplied batch. There are no checks to confirm
	 * that this supplied batch is a valid member of the batch list, so client care
	 * should be taken.
	 * @param batch the batch to set as the active batch.
	 */
	public void setActiveBatch(GeomBatch batch) {
		this.batch = batch;
	}
    
    /**
	 * returns the number of vertices contained in this geometry. This is
	 * a summation of the vertex count for each batch that is contained in 
	 * this geometry.
	 * 
	 */
    public int getVertexCount() {
        int count = 0;
        
        for(int i = 0; i < getBatchCount(); i++) {
            count += getBatch(i).getVertQuantity();
        }
        
        return count;
    }

    /**
	 * <code>reconstruct</code> reinitializes the geometry with new data. This
	 * will reuse the geometry object.
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
        reconstruct(vertices, normals, colors, textureCoords, 0);
    }
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, FloatBuffer textureCoords, int batchIndex) {
		if (vertices == null)
		    getBatch(batchIndex).setVertQuantity(0);
		else
            getBatch(batchIndex).setVertQuantity(vertices.capacity() / 3);

        getBatch(batchIndex).setVertBuf(vertices);
        getBatch(batchIndex).setNormBuf(normals);
        getBatch(batchIndex).setColorBuf(colors);
        if(getBatch(batchIndex).getTexBuf() == null) {
            getBatch(batchIndex).setTexBuf(new ArrayList(1));
        }
        
        getBatch(batchIndex).clearTexBuffer();
        getBatch(batchIndex).addTexCoordinates(textureCoords);
        
        
        if (getBatch(batchIndex).getVboInfo() != null)
            getBatch(batchIndex).resizeTextureIds(1);
	}
	
	public void setVBOInfo(VBOInfo info) {
		
	    batch.setVBOInfo(info);
	}
	
	public VBOInfo getVBOInfo() {
	    return batch.getVboInfo();
	}
	
	/**
	 *
	 * <code>setSolidColor</code> sets the color array of this geometry to a
	 * single color.  For greater efficiency, try setting the the ColorBuffer
     * to null and using DefaultColor instead.
	 *
	 * @param color
	 *            the color to set.
	 */
	public void setSolidColor(ColorRGBA color) {
        for(int i = 0; i < getBatchCount(); i++) {
            getBatch(i).setSolidColor(color);
        }
	}

	/**
	 * Sets every color of this geometry's color array to a random color.
	 */
	public void setRandomColors() {
	    batch.setRandomColors();
	}

	/**
	 * <code>getVertexBuffer</code> returns the float buffer that
	 * contains this geometry's vertex information.
	 *
	 * @return the float buffer that contains this geometry's vertex
	 *         information.
	 */
	public FloatBuffer getVertexBuffer() {
		return batch.getVertBuf();
	}

	/**
	 * <code>setVertexBuffer</code> sets this geometry's vertices via a
	 * float buffer consisting of groups of three floats: x,y and z.
	 *
	 * @param buff
	 *            the new vertex buffer.
	 */
	public void setVertexBuffer(FloatBuffer buff) {
		batch.setVertBuf(buff);
		
	}

	/**
	 * Returns the number of vertices defined in this Geometry object.
	 *
	 * @return The number of vertices in this Geometry object.
	 */
	public int getVertQuantity() {
		return batch.getVertQuantity();
	}

	/**
     * 
     * @param quantity
     *            the value to override the quantity with. This is overridden by
     *            setVertexBuffer().
     */
	public void setVertQuantity(int quantity) {
		batch.setVertQuantity(quantity);
	}

	/**
	 * <code>getNormalBuffer</code> retrieves this geometry's normal
	 * information as a float buffer.
	 *
	 * @return the float buffer containing the geometry information.
	 */
	public FloatBuffer getNormalBuffer() {
		return batch.getNormBuf();
	}

	/**
	 * <code>setNormalBuffer</code> sets this geometry's normals via a
	 * float buffer consisting of groups of three floats: x,y and z.
	 *
	 * @param buff
	 *            the new normal buffer.
	 */
	public void setNormalBuffer(FloatBuffer buff) {
		batch.setNormBuf(buff);
	}

	/**
	 * <code>getColorBuffer</code> retrieves the float buffer that
	 * contains this geometry's color information.
	 *
	 * @return the buffer that contains this geometry's color information.
	 */
	public FloatBuffer getColorBuffer() {
		return batch.getColorBuf();
	}

	/**
	 * <code>setColorBuffer</code> sets this geometry's colors via a
	 * float buffer consisting of groups of four floats: r,g,b and a.
	 *
	 * @param buff
	 *            the new color buffer.
	 */
	public void setColorBuffer(FloatBuffer buff) {
		batch.setColorBuf(buff);
	}

    /**
     *
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored.
     *
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     */
    public void copyTextureCoords(int fromIndex, int toIndex) {
        copyTextureCoords(fromIndex, toIndex, 1.0f);
    }

    /**
     * 
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored.  Coords are multiplied by the given factor.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     * @param factor
     *            a multiple to apply when copying
     */
    public void copyTextureCoords(int fromIndex, int toIndex, float factor) {
        batch.copyTextureCoordinates(fromIndex, toIndex, factor);
        
    }

	/**
	 * <code>getTextureBuffer</code> retrieves this geometry's texture
	 * information contained within a float buffer.
	 *
	 * @return the float buffer that contains this geometry's texture
	 *         information.
	 */
	public FloatBuffer getTextureBuffer() {
		if (batch.getTexBuf().size() > 0) {
            return (FloatBuffer)batch.getTexBuf().get(0);
        }
        return null;
	}

	/**
	 * <code>getTextureBuffers</code> retrieves this geometry's texture
	 * information contained within a float buffer array.
	 *
	 * @return the float buffers that contain this geometry's texture
	 *         information.
	 */
	public FloatBuffer[] getTextureBuffers() {
		return (FloatBuffer[])batch.getTexBuf().toArray(new FloatBuffer[batch.getTexBuf().size()]);
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
		return batch.getTexBuf(textureUnit);
        
	}

	/**
     * <code>setTextureBuffer</code> sets this geometry's textures (position
     * 0) via a float buffer consisting of groups of two floats: x and y.
     * 
     * @param buff
     *            the new vertex buffer.
     */
	public void setTextureBuffer(FloatBuffer buff) {
        setTextureBuffer(buff, 0);
	}

	/**
     * <code>setTextureBuffer</code> sets this geometry's textures st the
     * position given via a float buffer consisting of groups of two floats: x
     * and y.
     * 
     * @param buff
     *            the new vertex buffer.
     */
	public void setTextureBuffer(FloatBuffer buff, int position) {
		batch.setTexBuf(buff, position);
        
	}

	/**
	 *
	 * <code>getNumberOfUnits</code> returns the number of texture units this
	 * geometry supports.
	 *
	 * @return the number of texture units supported by the geometry.
	 */
	public int getNumberOfUnits() {
	    if (batch.getTexBuf() == null) return 0;
		return batch.getTexBuf().size();
	}
	
	public int getType() {
		return Spatial.GEOMETRY;
	}

	/**
	 * Clears all vertex, normal, texture, and color buffers by setting them to
	 * null.
	 */
	public void clearBuffers() {
	    reconstruct(null, null, null, null);
	}

	/**
	 * <code>updateBound</code> recalculates the bounding object assigned to
	 * the geometry. This resets it parameters to adjust for any changes to the
	 * vertex information.
	 *
	 */
	public void updateModelBound() {
		if (bound != null) {
			bound.computeFromBatches(batchList);
			updateWorldBound();
		}
	}

	/**
	 *
	 * <code>getModelBound</code> retrieves the bounding object that contains
	 * the geometry node's vertices.
	 *
	 * @return the bounding object for this geometry.
	 */
	public BoundingVolume getModelBound() {
		return bound;
	}

	/**
	 *
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
	 *
	 * <code>setStates</code> applies all the render states for this
	 * particular geometry.
	 *
	 */
	public void applyStates() {
		RenderState tempState;
		for (int i = 0; i < states.length; i++) {
			tempState = enforcedStateList[i] != null ? enforcedStateList[i] : states[i];
                
			if (tempState != null) {
				if (tempState != currentStates[i]) {
					tempState.apply();
					currentStates[i] = tempState;
				}
			} else {
				currentStates[i] = null;
			}
		}
	}

	/**
	 * <code>draw</code> prepares the geometry for rendering to the display.
	 * The renderstate is set and the subclass is responsible for rendering the
	 * actual data.
	 *
	 * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
	 * @param r
	 *            the renderer that displays to the context.
	 */
	public void draw(Renderer r) {
		applyStates();
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
	 * <code>applyRenderState</code> determines if a particular render state
	 * is set for this Geometry. If not, the default state will be used.
	 */
	protected void applyRenderState(Stack[] states) {
		for (int x = 0; x < states.length; x++) {
			if (states[x].size() > 0) {
				this.states[x] = ((RenderState) states[x].peek()).extract(
						states[x], this);
			} else {
				this.states[x] = defaultStateList[x];
			}
		}
	}

	/**
     * <code>randomVertice</code> returns a random vertex from the list of
     * vertices set to this geometry. If there are no vertices set, null is
     * returned.
     * 
     * @param fill
     *            a Vector3f to fill with the results. If null, one is created.
     *            It is more efficient to pass in a nonnull vector.
     * @return Vector3f a random vertex from the vertex list. Null is returned
     *         if the vertex list is not set.
     */
	public Vector3f randomVertice(Vector3f fill) {
		if (batch.getVertBuf() == null)
			return null;
		int i = (int) (FastMath.nextRandomFloat() * batch.getVertQuantity());
		
		if (fill == null) fill = new Vector3f();
		BufferUtils.populateFromBuffer(fill, batch.getVertBuf(), i);
		
		worldRotation.multLocal(fill).addLocal(worldTranslation);

		return fill;
	}

	public void findPick(Ray ray, PickResults results) {
		if (getWorldBound() == null || !isCollidable) {
			return;
		}
		if (getWorldBound().intersects(ray)) {
			//find the triangle that is being hit.
			//add this node and the triangle to the PickResults list.
			results.addPick(ray, this);
		}
	}

	public Spatial putClone(Spatial store, CloneCreator properties) {
		if (store == null)
			return null;
		super.putClone(store, properties);
		Geometry toStore = (Geometry) store; // This should never throw a class
		// cast exception if
		// the clone is written correctly.

		// Create a CloneID if none exist for this mesh.
		if (!properties.CloneIDExist(this))
			properties.createCloneID(this);

		toStore.cloneID = properties.getCloneID(this);

		if (properties.isSet("vertices")) {
			toStore.setVertexBuffer(batch.getVertBuf());
		} else {
		    if (batch.getVertBuf() != null) {
			    toStore.setVertexBuffer(BufferUtils.createFloatBuffer(batch.getVertBuf().capacity()));
			    batch.getVertBuf().rewind();
			    toStore.getVertexBuffer().put(batch.getVertBuf());
			    toStore.setVertexBuffer(toStore.getVertexBuffer()); // pick up vertQuantity
		    } else toStore.setVertexBuffer(null);
		}

		if (properties.isSet("colors")) { // if I should shallow copy colors
		    toStore.setColorBuffer(batch.getColorBuf());
		} else  // If I should deep copy colors
		    if (batch.getColorBuf() != null) {
			    toStore.setColorBuffer(BufferUtils.createFloatBuffer(batch.getColorBuf().capacity()));
			    batch.getColorBuf().rewind();
			    toStore.getColorBuffer().put(batch.getColorBuf());
		    } else toStore.setColorBuffer(null);

		if (properties.isSet("normals")) {
		    toStore.setNormalBuffer(batch.getNormBuf());
		} else
		    if (batch.getNormBuf() != null) {
			    toStore.setNormalBuffer(BufferUtils.createFloatBuffer(batch.getNormBuf().capacity()));
			    batch.getNormBuf().rewind();
			    toStore.getNormalBuffer().put(batch.getNormBuf());
			} else toStore.setNormalBuffer(null);

		if (properties.isSet("texcoords")) {
			toStore.getBatch().setTexBuf(batch.getTexBuf()); // pick up all array positions
		} else {
		    if (batch.getTexBuf() != null) {
				for (int i = 0; i < batch.getTexBuf().size(); i++) {
                    FloatBuffer src = (FloatBuffer)batch.getTexBuf().get(i);
				    if (src != null) {
					    toStore.getBatch().getTexBuf().set(i,BufferUtils.createFloatBuffer(src.capacity()));
                        src.rewind();
					    ((FloatBuffer)toStore.getBatch().getTexBuf().get(i)).put(src);
				    } else toStore.getBatch().getTexBuf().set(i,null);
				}
		    } else toStore.getBatch().setTexBuf(null);
		}

		if (bound != null)
			toStore.setModelBound((BoundingVolume) bound.clone(null));

		if (properties.isSet("vboinfo")) {
		    toStore.setVBOInfo(batch.getVboInfo());
		} else {
		    if (toStore.getVBOInfo() != null) {
		        toStore.setVBOInfo(batch.getVboInfo().copy());
		    } else toStore.setVBOInfo(null);
		}

		return toStore;
	}

	/**
	 * Returns the ID number that identifies this Geometry's clone ID.
	 *
	 * @return The assigned clone ID of this geometry. Non clones are -1.
	 */
	public int getCloneID() {
		return cloneID;
	}

    

    /**
     * <code>getDefaultColor</code> returns the color used if no
     * per vertex colors are specified.
     * 
     * @return default color
     */
    public ColorRGBA getDefaultColor() {
        return defaultColor;
    }

    /**
     * <code>setDefaultColor</code> sets the color to be used
     * if no per vertex color buffer is set.
     * 
     * @param color
     */
    public void setDefaultColor(ColorRGBA color) {
        defaultColor = color;
    }

    /**
     * <code>getWorldCoords</code> translates/rotates and scales the
     * coordinates of this Geometry (from the first batch) to world coordinates
     * based on its world settings. The results are stored in the given
     * FloatBuffer. If given FloatBuffer is null, one is created.
     * 
     * @param store
     *            the FloatBuffer to store the results in, or null if you want
     *            one created.
     * @return store or new FloatBuffer if store == null.
     */
    public FloatBuffer getWorldCoords(FloatBuffer store) {
        return getWorldCoords(store, 0);
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
    public FloatBuffer getWorldCoords(FloatBuffer store, int batchIndex) {
        GeomBatch gBatch = getBatch(batchIndex);
        if (store == null || store.capacity() != gBatch.getVertBuf().capacity())
            store = BufferUtils.clone(gBatch.getVertBuf());
        for (int v = 0, vSize = store.capacity() / 3; v < vSize; v++) {
            BufferUtils.populateFromBuffer(compVect, store, v);
            worldRotation.multLocal(compVect).multLocal(worldScale).addLocal(worldTranslation);
            BufferUtils.setInBuffer(compVect, store, v);
        }
        store.clear();
        return store;
    }
    

    // inherited documentation
    public void lockMeshes(Renderer r) {
        super.lockMeshes(r);
        
        batch.setDisplayListID(r.createDisplayList(this));
    }

    // inherited documentation
    public void unlockMeshes(Renderer r) {
        super.unlockMeshes(r);

        if (batch.getDisplayListID() != -1) {
            r.releaseDisplayList(batch.getDisplayListID());
            batch.setDisplayListID(-1);
        }
    }
    
    public int getDisplayListID() {
        return batch.getDisplayListID();
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        // go through children and set parent to this node
        for (int x = 0, cSize = batchList.size(); x < cSize; x++) {
            GeomBatch batch = getBatch(x);
            batch.parentGeom = this;
        }
    }

    public boolean hasDirtyVertices() {
        return hasDirtyVertices ;
    }
    
    public void setHasDirtyVertices(boolean flag) {
        hasDirtyVertices = flag;
    }

    public int getBatchIndex(GeomBatch bat) {
        if (bat == null) return -1;
        for (int x = 0, cSize = getBatchCount(); x < cSize; x++) {
            GeomBatch batch = getBatch(x);
            if (bat.equals(batch)) return x;
        }
        return -1;
    }

    public void swapBatches(int index1, int index2) {
        GeomBatch b2 = (GeomBatch) batchList.get(index2);
        GeomBatch b1 = (GeomBatch) batchList.remove(index1);
        batchList.add(index1, b2);
        batchList.remove(index2);
        batchList.add(index2, b1);
        
        if(parent != null) {
            parent.batchChange(this, index1, index2);
        }
    }

    public boolean hasNormalizedNormals() {
        return normalizedNormals ;
    }

    public void setNormalizedNormals(boolean normalized) {
        normalizedNormals = normalized;
    }
}
