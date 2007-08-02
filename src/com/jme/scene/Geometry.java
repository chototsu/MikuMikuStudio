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
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf node
 * contains the geometric data for rendering objects. It manages all rendering
 * information such as a collection of states and the data for a model.
 * Subclasses define what the model data is.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Geometry.java,v 1.112 2007-08-02 22:00:10 nca Exp $
 */
public abstract class Geometry extends Spatial implements Serializable,
        Savable {
    
    private static final long serialVersionUID = 1;
    
    protected ArrayList<GeomBatch> batchList;
    
    /**
     * Empty Constructor to be used internally only.
     */
    public Geometry() {
        super();
        setupBatchList();
    }

    /**
     * Constructor instantiates a new <code>Geometry</code> object. This is
     * the default object which has an empty vertex array. All other data is
     * null.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     */
    public Geometry(String name) {
        super(name);
        setupBatchList();
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
        setupBatchList();
        reconstruct(vertex, normal, color, texture);
    }

    protected void setupBatchList() {
        batchList = new ArrayList<GeomBatch>(1);
        GeomBatch batch = new GeomBatch();
        batch.setParentGeom(this);
        batchList.add(batch);
    }
    
    /**
     * Note: this method also sets zorder on batches
     */
    @Override
    public void setZOrder(int zOrder) {
        super.setZOrder(zOrder);
        for (int i = getBatchCount(); --i >= 0;) {
            getBatch(i).setZOrder(zOrder);
        }
    }

    /**
     * adds a batch to the batch list of the geometry.
     * 
     * @param batch
     *            the batch to add.
     */
    public void addBatch(GeomBatch batch) {
        batch.setParentGeom(this);
        batchList.add(batch);
    }

    /**
     * removes the batch supplied. If the currently active batch is the one
     * supplied, the active batch is reset to the first batch in the list. If
     * the last batch is removed, then the active batch is set to null.
     * 
     * @param batch
     *            the batch to remove from the list.
     */
    public boolean removeBatch(GeomBatch batch) {
        return batchList.remove(batch);
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
    public GeomBatch removeBatch(int index) {
        GeomBatch b = batchList.remove(index);
        b.setParentGeom(null);
        return b;
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
        return batchList.get(index);
    }

    /**
     * clearBatches removes all batches from this geometry. Effectively making
     * the geometry contain no render data.
     */
    public void clearBatches() {
        batchList.clear();
    }

    /**
     * returns the number of batches contained in this geometry.
     * 
     * @return the number of batches in this geometry.
     */
    public int getBatchCount() {
        return batchList.size();
    }

    /**
     * returns the number of vertices contained in this geometry. This is a
     * summation of the vertex count for each enabled batch that is contained in
     * this geometry.
     */
    @Override
    public int getVertexCount() {
        int count = 0;

        for (int i = 0; i < getBatchCount(); i++) {
            GeomBatch gb = getBatch(i); 
            if (gb.isEnabled())
                count += gb.getVertexCount();
        }

        return count;
    }
    
    @Override
    public int getTriangleCount() {
    	return 0;
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

    /**
     * 
     * @param vertices
     * @param normals
     * @param colors
     * @param textureCoords
     * @param batchIndex
     */
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals,
            FloatBuffer colors, FloatBuffer textureCoords, int batchIndex) {
        if (vertices == null)
            getBatch(batchIndex).setVertexCount(0);
        else
            getBatch(batchIndex).setVertexCount(vertices.limit() / 3);

        getBatch(batchIndex).setVertexBuffer(vertices);
        getBatch(batchIndex).setNormalBuffer(normals);
        getBatch(batchIndex).setColorBuffer(colors);
        if (getBatch(batchIndex).getTextureBuffers() == null) {
            getBatch(batchIndex).setTextureBuffers(new ArrayList<FloatBuffer>(1));
        }

        getBatch(batchIndex).clearTextureBuffers();
        getBatch(batchIndex).addTextureCoordinates(textureCoords);

        if (getBatch(batchIndex).getVBOInfo() != null)
            getBatch(batchIndex).resizeTextureIds(1);
    }

    /**
     * Sets VBO info on the 0th batch contained in this Geometry.
     * @param info the VBO info to set
     * @see VBOInfo
     */
    public void setVBOInfo(VBOInfo info) {
        setVBOInfo(0, info);
    }

    /**
     * 
     * @param batchIndex The index of the batch to set VBO info on
     * @param info the VBO info to set
     * @see VBOInfo
     */
    public void setVBOInfo(int batchIndex, VBOInfo info) {
        getBatch(batchIndex).setVBOInfo(info);
    }
    
    /**
     * 
     * @param batchIndex The index of the batch to retrieve VBO info from
     * @return VBO info object for the given batch
     * @see VBOInfo
     */
    public VBOInfo getVBOInfo(int batchIndex) {
        return getBatch(batchIndex).getVBOInfo();
    }

    /**
     * <code>setSolidColor</code> sets the color array of this geometry to a
     * single color. For greater efficiency, try setting the the ColorBuffer to
     * null and using DefaultColor instead.
     * 
     * @param color
     *            the color to set.
     */
    public void setSolidColor(ColorRGBA color) {
        for (int i = 0; i < getBatchCount(); i++) {
            GeomBatch gb = getBatch(i); 
            if (gb.isEnabled())
                gb.setSolidColor(color);
        }
    }

    /**
     * Sets every color of this geometry's color array to a random color.
     */
    public void setRandomColors() {
        for (int i = 0; i < getBatchCount(); i++) {
            GeomBatch gb = getBatch(i); 
            if (gb.isEnabled())
                gb.setRandomColors();
        }
    }

    /**
     * <code>getVertexBuffer</code> returns the float buffer that contains
     * this geometry's vertex information.
     * 
     * @return the float buffer that contains this geometry's vertex
     *         information.
     */
    public FloatBuffer getVertexBuffer(int batchIndex) {
        return getBatch(batchIndex).getVertexBuffer();
    }

    /**
     * <code>setVertexBuffer</code> sets this geometry's vertices via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param buff
     *            the new vertex buffer.
     */
    public void setVertexBuffer(int batchIndex, FloatBuffer buff) {
        getBatch(batchIndex).setVertexBuffer(buff);

    }

    /**
     * <code>getNormalBuffer</code> retrieves this geometry's normal
     * information as a float buffer.
     * 
     * @return the float buffer containing the geometry information.
     */
    public FloatBuffer getNormalBuffer(int batchIndex) {
        return getBatch(batchIndex).getNormalBuffer();
    }

    /**
     * <code>setNormalBuffer</code> sets this geometry's normals via a float
     * buffer consisting of groups of three floats: x,y and z.
     * 
     * @param buff
     *            the new normal buffer.
     */
    public void setNormalBuffer(int batchIndex, FloatBuffer buff) {
        getBatch(batchIndex).setNormalBuffer(buff);
    }

    /**
     * <code>getColorBufferfer</code> retrieves the float buffer that contains
     * this geometry's color information.
     * 
     * @return the buffer that contains this geometry's color information.
     */
    public FloatBuffer getColorBuffer(int batchIndex) {
        return getBatch(batchIndex).getColorBuffer();
    }

    /**
     * <code>setColorBuffer</code> sets this geometry's colors via a float
     * buffer consisting of groups of four floats: r,g,b and a.
     * 
     * @param buff
     *            the new color buffer.
     */
    public void setColorBuffer(int batchIndex, FloatBuffer buff) {
        getBatch(batchIndex).setColorBuffer(buff);
    }

    /**
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     */
    public void copyTextureCoords(int batchIndex, int fromIndex, int toIndex) {
        copyTextureCoords(batchIndex, fromIndex, toIndex, 1.0f);
    }

    /**
     * <code>copyTextureCoords</code> copys the texture coordinates of a given
     * texture unit to another location. If the texture unit is not valid, then
     * the coordinates are ignored. Coords are multiplied by the given factor.
     * 
     * @param fromIndex
     *            the coordinates to copy.
     * @param toIndex
     *            the texture unit to set them to.
     * @param factor
     *            a multiple to apply when copying
     */
    public void copyTextureCoords(int batchIndex, int fromIndex, int toIndex, float factor) {
        getBatch(batchIndex).copyTextureCoordinates(fromIndex, toIndex, factor);

    }

    /**
     * <code>getTextureBuffers</code> retrieves this geometry's texture
     * information contained within a float buffer array.
     * 
     * @return the float buffers that contain this geometry's texture
     *         information.
     */
    public FloatBuffer[] getTextureBuffers(int batchIndex) {
        return getBatch(batchIndex).getTextureBuffers().toArray(
                new FloatBuffer[getBatch(batchIndex).getTextureBuffers().size()]);
    }

    /**
     * <code>getTextureAsFloatBuffer</code> retrieves the texture buffer of a
     * given texture unit.
     * 
     * @param textureUnit
     *            the texture unit to check.
     * @return the texture coordinates at the given texture unit.
     */
    public FloatBuffer getTextureBuffer(int batchIndex, int textureUnit) {
        return getBatch(batchIndex).getTextureBuffer(textureUnit);

    }

    /**
     * <code>setTextureBuffer</code> sets this geometry's textures (position
     * 0) via a float buffer consisting of groups of two floats: x and y.
     * 
     * @param buff
     *            the new vertex buffer.
     */
    public void setTextureBuffer(int batchIndex, FloatBuffer buff) {
        setTextureBuffer(batchIndex, buff, 0);
    }

    /**
     * <code>setTextureBuffer</code> sets this geometry's textures st the
     * position given via a float buffer consisting of groups of two floats: x
     * and y.
     * 
     * @param buff
     *            the new vertex buffer.
     */
    public void setTextureBuffer(int batchIndex, FloatBuffer buff, int position) {
        getBatch(batchIndex).setTextureBuffer(buff, position);
    }
    
    /**
     *
     * <code>getNumberOfUnits</code> returns the number of texture units this
     * geometry supports at a given batch.
     *
     * @return the number of texture units supported by the geometry batch.
     */
    public int getNumberOfUnits(int batchIndex) {
        if (getBatch(batchIndex).getTextureBuffers() == null) return 0;
        return getBatch(batchIndex).getTextureBuffers().size();
    }
    
    @Override
    public int getType() {
        return SceneElement.GEOMETRY;
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
     */
    @Override
    public void updateModelBound() {
        for (int i = 0; i < getBatchCount(); i++) {
            GeomBatch batch =  batchList.get(i);
            if (batch != null && batch.isEnabled()) {
                batch.updateModelBound();
            }
        }
        updateWorldBound();
    }

    /**
     * <code>setModelBound</code> sets the bounding object for this geometry.
     * 
     * @param modelBound
     *            the bounding object for this geometry.
     */
    @Override
    public void setModelBound(BoundingVolume modelBound) {
        this.worldBound = null;
        if (batchList != null)
            for (int i = 0; i < getBatchCount(); i++) {
                GeomBatch batch = batchList.get(i);
                if (batch != null && batch.isEnabled()) {
                    batch.setModelBound(modelBound != null ? modelBound.clone(null) : null);
                }
            }
    }


    /**
     * <code>updateWorldData</code> updates all the children maintained by
     * this node.
     * 
     * @param time
     *            the frame time.
     */
    @Override
    public void updateWorldData(float time) {
        super.updateWorldData(time);

        if (batchList != null)
            for (int i = 0; i < getBatchCount(); i++) {
                GeomBatch batch =  batchList.get(i);
                if (batch != null && batch.isEnabled()) {
                    batch.updateGeometricState(time, false);
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
    @Override
    public void draw(Renderer r) {
    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location of
     * all this node's parents.
     * 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    @Override
    public void updateWorldBound() {
        if ((lockedMode & SceneElement.LOCKED_BOUNDS) != 0) return;

        boolean foundFirstBound = false;
        for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
            GeomBatch child =  getBatch(i);
            if (child != null && child.isEnabled()) {
                if (foundFirstBound) {
                    // merge current world bound with child world bound
                    worldBound.mergeLocal(child.getWorldBound());

                } else {
                    // set world bound to first non-null child world bound
                    if (child.getWorldBound() != null) {
                        worldBound = child.getWorldBound()
                                .clone(worldBound);
                        foundFirstBound = true;
                    }
                }
            }
        }

    }

    /**
     * <code>applyRenderState</code> determines if a particular render state
     * is set for this Geometry. If not, the default state will be used.
     */
    @Override
    protected void applyRenderState(Stack[] states) {
        if (batchList != null)
            for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
                GeomBatch batch = getBatch(i);
                if (batch != null && batch.isEnabled())
                    batch.updateRenderState(states);
            }
    }

    /**
     * <code>randomVertex</code> returns a random vertex from the list of
     * vertices set to this geometry. If there are no vertices set, null is
     * returned.
     * 
     * @param fill
     *            a Vector3f to fill with the results. If null, one is created.
     *            It is more efficient to pass in a nonnull vector.
     * @return Vector3f a random vertex from the vertex list. Null is returned
     *         if the vertex list is not set.
     */
    public Vector3f randomVertex(Vector3f fill) {
        int batchIndex = (int)(FastMath.nextRandomFloat() * getBatchCount());
        return getBatch(batchIndex).randomVertex(fill);
    }

    @Override
    public void findPick(Ray ray, PickResults results) {
        if (getWorldBound() != null && isCollidable) {
            if (getWorldBound().intersects(ray)) {
                // further checking needed.
                for (int i = 0; i < getBatchCount(); i++) {
                    GeomBatch gb = getBatch(i); 
                    if (gb.isEnabled())
                        gb.findPick(ray, results);
                }
            }
        }
    }

    /**
     * <code>setDefaultColor</code> sets the color to be used if no per vertex
     * color buffer is set.
     * 
     * @param color
     */
    public void setDefaultColor(ColorRGBA color) {
        for (int i = 0; i < getBatchCount(); i++) {
            GeomBatch gb = getBatch(i); 
            if (gb.isEnabled())
                gb.setDefaultColor(color);
        }
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
        return getBatch(batchIndex).getWorldCoords(store);
    }

	/**
	 * <code>getWorldNormals</code> rotates the
	 * normals of this Geometry (from the first batch) to world normals
	 * based on its world settings. The results are stored in the given
	 * FloatBuffer. If given FloatBuffer is null, one is created.
	 *
	 * @param store
	 *            the FloatBuffer to store the results in, or null if you want
	 *            one created.
	 * @return store or new FloatBuffer if store == null.
	 */
	public FloatBuffer getWorldNormals(FloatBuffer store) {
		return getWorldNormals(store, 0);
	}

	/**
	 * <code>getWorldNormals</code> rotates the
	 * normals of this Geometry (from the given batch index) to world
	 * normals based on its world settings. The results are stored in the
	 * given FloatBuffer. If given FloatBuffer is null, one is created.
	 *
	 * @param store
	 *            the FloatBuffer to store the results in, or null if you want
	 *            one created.
	 * @param batchIndex
	 *            the batch to process
	 * @return store or new FloatBuffer if store == null.
	 */
	public FloatBuffer getWorldNormals(FloatBuffer store, int batchIndex) {
		return getBatch(batchIndex).getWorldNormals(store);
	}

    @Override
    public void lockBounds() {
        super.lockBounds();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).lockBounds();
    }

    @Override
    public void lockShadows() {
        super.lockShadows();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).lockShadows();
    }
    
    @Override
    public void lockTransforms() {
        super.lockTransforms();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).lockTransforms();
    }

    @Override
    public void lockMeshes(Renderer r) {
        super.lockMeshes(r);

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).lockMeshes(r);
    }
    
    @Override
    public void unlockBounds() {
        super.unlockBounds();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).unlockBounds();
    }
    
    @Override
    public void unlockShadows() {
        super.unlockShadows();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).unlockShadows();
    }
    
    @Override
    public void unlockTransforms() {
        super.unlockTransforms();

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).unlockTransforms();
    }

    @Override
    public void unlockMeshes(Renderer r) {
        super.unlockMeshes(r);

        for (int x = 0; x < getBatchCount(); x++)
            getBatch(x).unlockMeshes(r);
    }

    private void readObject(java.io.ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        // go through children and set parent to this node
        for (int x = 0, cSize = getBatchCount(); x < cSize; x++) {
            GeomBatch batch = getBatch(x);
            batch.setParentGeom(this);
        }
    }

	/**
	 * Returns the index of the batch object provided or -1 if it doesn't exist
	 * in this geometry.
	 *
	 * @param bat the batch object to retrieve index for
	 * @return index of the batch object provided or -1 if it doesn't exist
	 */
	public int getBatchIndex(GeomBatch bat) {
        if (bat == null)
            return -1;
        for (int x = 0, cSize = getBatchCount(); x < cSize; x++) {
            GeomBatch batch = getBatch(x);
            if (bat.equals(batch))
                return x;
        }
        return -1;
    }

    /**
     * Swap the places of two batches in this Geometry
     * @param index1 the first batch index
     * @param index2 the second batch index
     */
    public void swapBatches(int index1, int index2) {
        GeomBatch b2 =  batchList.get(index2);
        GeomBatch b1 =  batchList.remove(index1);
        batchList.add(index1, b2);
        batchList.remove(index2);
        batchList.add(index2, b1);

        if (parent != null) {
            parent.batchChange(this, index1, index2);
        }
    }

    @Override
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(batchList, "batchList", new ArrayList<GeomBatch>(1));
    }

    @Override
    @SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        batchList = capsule.readSavableArrayList("batchList", new ArrayList<GeomBatch>(1));
        
        if (batchList != null)
            for (int x = 0, cSize = getBatchCount(); x < cSize; x++) {
                GeomBatch batch = getBatch(x);
                batch.setParentGeom(this);
            }
    }

    public void setTangentBuffer(int batchIndex, FloatBuffer tangentBuf) {
        getBatch(batchIndex).setTangentBuffer(tangentBuf);
    }

    public FloatBuffer getTangentBuffer(int batchIndex) {
        return getBatch(batchIndex).getTangentBuffer();
    }

    public void setBinormalBuffer(int batchIndex, FloatBuffer binormalBuf) {
        getBatch(batchIndex).setBinormalBuffer(binormalBuf);
    }

    public FloatBuffer getBinormalBuffer(int batchIndex) {
        return getBatch(batchIndex).getBinormalBuffer();
    }
}