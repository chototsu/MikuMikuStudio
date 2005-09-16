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

import java.io.IOException;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.Stack;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.CloneCreator;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf node
 * contains the geometric data for rendering objects. It manages all rendering
 * information such as a collection of states and the data for a model.
 * Subclasses define what the model data is.
 *
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Geometry.java,v 1.75 2005-09-16 19:33:40 Mojomonkey Exp $
 */
public abstract class Geometry extends Spatial implements Serializable {

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
	protected transient FloatBuffer[] texBuf;

	/** The geometry's VBO information. **/
	protected VBOInfo vboInfo;
	
	public RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

	/** Non -1 values signal this geometry is a clone of grouping "cloneID". */
	private int cloneID = -1;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public Geometry() {
		texBuf = new FloatBuffer[1];
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
		reconstruct(vertex, normal, color, texture);
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
		int textureUnits = TextureState.getNumberOfUnits();
		if (textureUnits == -1) {
		    DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		    textureUnits = TextureState.getNumberOfUnits();
		}
		texBuf = new FloatBuffer[textureUnits];
		if (vertices == null)
		    vertQuantity = 0;
		else
		    vertQuantity = vertices.capacity() / 3;

		vertBuf = vertices;
		normBuf = normals;
		colorBuf = colors;
		texBuf[0] = textureCoords;
	}
	
	public void setVBOInfo(VBOInfo info) {
	    this.vboInfo = info;
	}
	
	public VBOInfo getVBOInfo() {
	    return vboInfo;
	}
	
	/**
	 *
	 * <code>setSolidColor</code> sets the color array of this geometry to a
	 * single color.
	 *
	 * @param color
	 *            the color to set.
	 */
	public void setSolidColor(ColorRGBA color) {
	    if (colorBuf == null) 
	        colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		for (int x = 0, cLength = colorBuf.capacity(); x < cLength; x+=4) {
		    colorBuf.put(color.r);
		    colorBuf.put(color.g);
		    colorBuf.put(color.b);
		    colorBuf.put(color.a);
		}
		colorBuf.flip();
	}

	/**
	 * Sets every color of this geometry's color array to a random color.
	 */
	public void setRandomColors() {
	    if (colorBuf == null) 
	        colorBuf = BufferUtils.createColorBuffer(vertQuantity);

		for (int x = 0, cLength = colorBuf.capacity(); x < cLength; x+=4) {
		    colorBuf.put(FastMath.nextRandomFloat());
		    colorBuf.put(FastMath.nextRandomFloat());
		    colorBuf.put(FastMath.nextRandomFloat());
		    colorBuf.put(1);
		}
		colorBuf.flip();
	}

	/**
	 * <code>getVertexBuffer</code> returns the float buffer that
	 * contains this geometry's vertex information.
	 *
	 * @return the float buffer that contains this geometry's vertex
	 *         information.
	 */
	public FloatBuffer getVertexBuffer() {
		return vertBuf;
	}

	/**
	 * <code>setVertexBuffer</code> sets this geometry's vertices via a
	 * float buffer consisting of groups of three floats: x,y and z.
	 *
	 * @param buff
	 *            the new vertex buffer.
	 */
	public void setVertexBuffer(FloatBuffer buff) {
		this.vertBuf = buff;
		if (vertBuf != null)
		    vertQuantity = vertBuf.capacity() / 3;
		else vertQuantity = 0;
	}

	/**
	 * Returns the number of vertexes defined in this Geometry object. Basicly,
	 * it is vertex.length.
	 *
	 * @return The number of vertexes in this Geometry object.
	 */
	public int getVertQuantity() {
		return vertQuantity;
	}

	/**
	 * <code>getNormalBuffer</code> retrieves this geometry's normal
	 * information as a float buffer.
	 *
	 * @return the float buffer containing the geometry information.
	 */
	public FloatBuffer getNormalBuffer() {
		return normBuf;
	}

	/**
	 * <code>setNormalBuffer</code> sets this geometry's normals via a
	 * float buffer consisting of groups of three floats: x,y and z.
	 *
	 * @param buff
	 *            the new normal buffer.
	 */
	public void setNormalBuffer(FloatBuffer buff) {
		this.normBuf = buff;
	}

	/**
	 * <code>getColorBuffer</code> retrieves the float buffer that
	 * contains this geometry's color information.
	 *
	 * @return the buffer that contains this geometry's color information.
	 */
	public FloatBuffer getColorBuffer() {
		return colorBuf;
	}

	/**
	 * <code>setColorBuffer</code> sets this geometry's colors via a
	 * float buffer consisting of groups of four floats: r,g,b and a.
	 *
	 * @param buff
	 *            the new color buffer.
	 */
	public void setColorBuffer(FloatBuffer buff) {
		this.colorBuf = buff;
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
	    
	    if (texBuf == null) return;
	    
		if (fromIndex < 0 || fromIndex >= texBuf.length || texBuf[fromIndex] == null) {
			return;
		}
		if (toIndex < 0 || toIndex >= texBuf.length || toIndex == fromIndex) {
			return;
		}
		
		if (texBuf[toIndex] == null
                || texBuf[toIndex].capacity() != texBuf[fromIndex].capacity())
            texBuf[toIndex] = BufferUtils.createFloatBuffer(texBuf[fromIndex]
                    .capacity());
		else
		    texBuf[toIndex].clear();
		texBuf[fromIndex].rewind();
		texBuf[toIndex].put(texBuf[fromIndex]);
	}

	/**
	 * <code>getTextureBuffer</code> retrieves this geometry's texture
	 * information contained within a float buffer.
	 *
	 * @return the float buffer that contains this geometry's texture
	 *         information.
	 */
	public FloatBuffer getTextureBuffer() {
		return texBuf[0];
	}

	/**
	 * <code>getTextureBuffers</code> retrieves this geometry's texture
	 * information contained within a float buffer array.
	 *
	 * @return the float buffers that contain this geometry's texture
	 *         information.
	 */
	public FloatBuffer[] getTextureBuffers() {
		return texBuf;
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
		return texBuf[textureUnit];
	}

	/**
     * <code>setTextureBuffer</code> sets this geometry's textures (position
     * 0) via a float buffer consisting of groups of two floats: x and y.
     * 
     * @param buff
     *            the new vertex buffer.
     */
	public void setTextureBuffer(FloatBuffer buff) {
		this.texBuf[0] = buff;
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
		this.texBuf[position] = buff;
	}

	/**
	 *
	 * <code>getNumberOfUnits</code> returns the number of texture units this
	 * geometry supports.
	 *
	 * @return the number of texture units supported by the geometry.
	 */
	public int getNumberOfUnits() {
	    if (texBuf == null) return 0;
		return texBuf.length;
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
			bound.computeFromPoints(vertBuf);
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
	 * When true, this geometry object will always be rendered as long as its
	 * parent is rendered.
	 *
	 * @param value
	 *            The new forced view flag for this object.
	 */
	public void setForceView(boolean value) {
		forceView = value;
	}

	/**
	 *
	 * <code>setStates</code> applies all the render states for this
	 * particular geometry.
	 *
	 */
	public void applyStates() {
		RenderState tempState = null;
		for (int i = 0; i < states.length; i++) {
			tempState = states[i];
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
	 * <code>drawBounds</code> calls super to set the render state then passes
	 * itself to the renderer.
	 *
	 * @param r
	 *            the renderer to display
	 */
	public void drawBounds(Renderer r) {
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
				this.states[x] = (RenderState) defaultStateList[x];
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
		if (vertBuf == null)
			return null;
		int i = (int) (FastMath.nextRandomFloat() * vertQuantity);
		
		if (fill == null) fill = new Vector3f();
		BufferUtils.populateFromBuffer(fill, vertBuf, i);
		
		worldRotation.multLocal(fill).addLocal(worldTranslation);

		return fill;
	}

	public void findPick(Ray ray, PickResults results) {
		if (getWorldBound() == null) {
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
			toStore.setVertexBuffer(vertBuf);
		} else {
		    if (vertBuf != null) {
			    toStore.setVertexBuffer(BufferUtils.createFloatBuffer(vertBuf.capacity()));
			    vertBuf.rewind();
			    toStore.vertBuf.put(vertBuf);
			    toStore.setVertexBuffer(toStore.vertBuf); // pick up vertQuantity
		    } else toStore.setVertexBuffer(null);
		}

		if (properties.isSet("colors")) { // if I should shallow copy colors
		    toStore.setColorBuffer(colorBuf);
		} else if (colorBuf != null) { // If I should deep copy colors
		    if (colorBuf != null) {
			    toStore.colorBuf = BufferUtils.createFloatBuffer(colorBuf.capacity());
			    colorBuf.rewind();
			    toStore.colorBuf.put(colorBuf);
		    } else toStore.setColorBuffer(null);
		}

		if (properties.isSet("normals")) {
		    toStore.setNormalBuffer(normBuf);
		} else if (normBuf != null) {
		    if (normBuf != null) {
			    toStore.normBuf = BufferUtils.createFloatBuffer(normBuf.capacity());
			    normBuf.rewind();
			    toStore.normBuf.put(normBuf);
			} else toStore.setNormalBuffer(null);
		}

		if (properties.isSet("texcoords")) {
			toStore.texBuf = this.texBuf; // pick up all array positions
		} else {
		    if (texBuf != null) {
				for (int i = 0; i < texBuf.length; i++) {
				    if (texBuf[i] != null) {
					    toStore.texBuf[i] = BufferUtils.createFloatBuffer(texBuf[i].capacity());
					    texBuf[i].rewind();
					    toStore.texBuf[i].put(texBuf[i]);
				    } else toStore.texBuf[i] = null;
				}
		    } else toStore.texBuf = null;
		}

		if (bound != null)
			toStore.setModelBound((BoundingVolume) bound.clone(null));

		if (properties.isSet("vboinfo")) {
		    toStore.vboInfo = this.vboInfo;
		} else {
		    if (toStore.vboInfo != null) {
		        toStore.setVBOInfo((VBOInfo)vboInfo.copy());
		    } else toStore.vboInfo = null;
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
     * Used with Serialization. Do not call this directly.
     * 
     * @param s
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // vert buffer
        if (vertBuf == null)
            s.writeInt(0);
        else {
            s.writeInt(vertBuf.capacity());
            vertBuf.rewind();
            for (int x = 0, len = vertBuf.capacity(); x < len; x++)
                s.writeFloat(vertBuf.get());
        }

        // norm buffer
        if (normBuf == null)
            s.writeInt(0);
        else {
            s.writeInt(normBuf.capacity());
            normBuf.rewind();
            for (int x = 0, len = normBuf.capacity(); x < len; x++)
                s.writeFloat(normBuf.get());
        }

        // color buffer
        if (colorBuf == null)
            s.writeInt(0);
        else {
            s.writeInt(colorBuf.capacity());
            colorBuf.rewind();
            for (int x = 0, len = colorBuf.capacity(); x < len; x++)
                s.writeFloat(colorBuf.get());
        }

        // tex buffer
        if (texBuf == null || texBuf.length == 0)
            s.writeInt(0);
        else {
            s.writeInt(texBuf.length);
            for (int i = 0; i < texBuf.length; i++) {
                if (texBuf[i] == null)
                    s.writeInt(0);
                else {
                    s.writeInt(texBuf[i].capacity());
                    texBuf[i].rewind();
                    for (int x = 0, len = texBuf[i].capacity(); x < len; x++)
                        s.writeFloat(texBuf[i].get());
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
            texBuf = null;
        } else {
            texBuf = new FloatBuffer[len];
            for (int i = 0; i < texBuf.length; i++) {
                len = s.readInt();
                if (len == 0) {
                    setTextureBuffer(null, i);
                } else {
                    FloatBuffer buf = BufferUtils.createFloatBuffer(len);
                    for (int x = 0; x < len; x++)
                        buf.put(s.readFloat());
                    setTextureBuffer(buf, i);            
                }
            }
        }
    }
}
