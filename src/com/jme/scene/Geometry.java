/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.jme.scene;

import java.io.Serializable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;
import java.util.logging.Level;

import com.jme.bounding.BoundingVolume;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;
import com.jme.math.FastMath;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf node
 * contains the geometric data for rendering objects. It manages all rendering
 * information such as a collection of states and the data for a model.
 * Subclasses define what the model data is.
 *
 * @author Mark Powell
 * @version $Id: Geometry.java,v 1.68 2004-11-04 23:53:01 renanse Exp $
 */
public abstract class Geometry extends Spatial implements Serializable {

	/** The local bounds of this Geometry object. */
	protected BoundingVolume bound;

	/** The geometry's vertex information. */
	protected Vector3f[] vertex;

	/** The geometry's per vertex normal information. */
	protected Vector3f[] normal;

	/** The geometry's per vertex color information. */
	protected ColorRGBA[] color;

	/** The geometry's per Texture per vertex texture coordinate information. */
	protected Vector2f[][] texture;

	/** The number of vertexes in this geometry. */
	protected int vertQuantity = -1;

	//buffers that allow for faster data processing.
	protected transient FloatBuffer colorBuf;

	protected transient FloatBuffer normBuf;

	protected transient FloatBuffer vertBuf;

	protected transient FloatBuffer[] texBuf;

	private RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

	private boolean useVBOVertex = false;

	private boolean useVBOTexture = false;

	private boolean useVBOColor = false;

	private boolean useVBONormal = false;

	private int vboVertexID = -1;

	private int vboColorID = -1;

	private int vboNormalID = -1;

	/** Non -1 values signal this geometry is a clone of grouping "cloneID". */
	private int cloneID = -1;

	private int[] vboTextureIDs;

	/**
	 * Empty Constructor to be used internally only.
	 */
	public Geometry() {
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
		vertex = new Vector3f[0];
		int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState().getNumberOfUnits();
		texture = new Vector2f[textureUnits][0];
		texBuf = new FloatBuffer[textureUnits];
		vboTextureIDs = new int[textureUnits];
	}

	/**
	 * Constructor creates a new <code>Geometry</code> object. During
	 * instantiation the geometry is set including vertex, normal, color and
	 * texture information. Any part may be null except for the vertex
	 * information. If this is null, an exception will be thrown.
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
	 *            the texture coordinates of the geometry.
	 */
	public Geometry(String name, Vector3f[] vertex, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture) {

		super(name);

		if (vertex == null) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Geometry must" + " include vertex information.");
			throw new JmeException(
					"Geometry must include vertex information. (100)");
		}

		int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState().getNumberOfUnits();
		this.texture = new Vector2f[textureUnits][0];
		this.texBuf = new FloatBuffer[textureUnits];
		this.vboTextureIDs = new int[textureUnits];
		this.vertex = vertex;
		this.vertQuantity = vertex.length;
		this.normal = normal;
		this.color = color;
		this.texture[0] = texture;

		updateColorBuffer();
		updateNormalBuffer();
		updateVertexBuffer();
		updateTextureBuffer();
	}

	/**
	 * <code>reconstruct</code> reinitializes the geometry with new data. This
	 * will reuse the geometry object.
	 *
	 * @param vertices
	 *            the new vertices to use.
	 * @param normal
	 *            the new normals to use.
	 * @param color
	 *            the new colors to use.
	 * @param texture
	 *            the new texture coordinates to use.
	 */
	public void reconstruct(Vector3f[] vertices, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture) {

		if (vertex == null) {
			LoggingSystem.getLogger().log(Level.WARNING,
					"Geometry must" + " include vertex information.");
			throw new JmeException(
					"Geometry must include vertex information. (101)");
		}

		int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState().getNumberOfUnits();
		this.texture = new Vector2f[textureUnits][0];
		this.texBuf = new FloatBuffer[textureUnits];
		this.vertex = vertices;
		this.normal = normal;
		this.color = color;
		this.texture[0] = texture;
		this.vertQuantity = vertex.length;

		updateColorBuffer();
		updateNormalBuffer();
		updateVertexBuffer();
		updateTextureBuffer();
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for vertex information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for vertexes.
	 */
	public boolean isVBOVertexEnabled() {
		return useVBOVertex;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for texture information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for textures.
	 */
	public boolean isVBOTextureEnabled() {
		return useVBOTexture;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for normal information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for normals.
	 */
	public boolean isVBONormalEnabled() {
		return useVBONormal;
	}

	/**
	 * Returns true if VBO (Vertex Buffer) is enabled for color information.
	 * This is used during rendering.
	 *
	 * @return If VBO is enabled for colors.
	 */
	public boolean isVBOColorEnabled() {
		return useVBOColor;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for vertex information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for vertexes.
	 */
	public void setVBOVertexEnabled(boolean enabled) {
		useVBOVertex = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for texture coordinate
	 * information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for texture coordinates.
	 */
	public void setVBOTextureEnabled(boolean enabled) {
		useVBOTexture = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for normal information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for normals
	 */
	public void setVBONormalEnabled(boolean enabled) {
		useVBONormal = enabled;
	}

	/**
	 * Enables or disables Vertex Buffer Objects for color information.
	 *
	 * @param enabled
	 *            If true, VBO enabled for colors
	 */
	public void setVBOColorEnabled(boolean enabled) {
		useVBOColor = enabled;
	}

	// TODO: Finish javadoc for VBO information.
	public int getVBOVertexID() {
		return vboVertexID;
	}

	public int getVBOTextureID(int index) {
		return vboTextureIDs[index];
	}

	public int getVBONormalID() {
		return vboNormalID;
	}

	public int getVBOColorID() {
		return vboColorID;
	}

	public void setVBOVertexID(int id) {
		vboVertexID = id;
	}

	public void setVBOTextureID(int index, int id) {
		vboTextureIDs[index] = id;
	}

	public void setVBONormalID(int id) {
		vboNormalID = id;
	}

	public void setVBOColorID(int id) {
		vboColorID = id;
	}

	/**
	 * <code>getColors</code> returns the color information of the geometry.
	 * This may be null and should be check for such a case.
	 *
	 * @return the color array.
	 */
	public ColorRGBA[] getColors() {
		return color;
	}

	/**
	 * <code>setColors</code> sets the color array of this geometry.
	 *
	 * @param color
	 *            the new color array.
	 */
	public void setColors(ColorRGBA[] color) {
		if (this.color != null) {
			if (color == null || this.color.length != color.length) {
				colorBuf = null;
			}
		}
		this.color = color;
		updateColorBuffer();
	}

	/**
	 *
	 * <code>setColor</code> sets a single colorRGBA into the color array. The
	 * index to set it is given, and due to speed considerations, no bounds
	 * checking is done. Therefore, if an invalid index is given, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param index
	 *            the index of the color to set.
	 * @param value
	 *            the color to set.
	 */

	public void setColor(int index, ColorRGBA value) {
		color[index] = value;
		colorBuf.put(index * 4, value.r).put(index * 4 + 1, value.g).put(
				index * 4 + 2, value.b).put(index * 4 + 3, value.a);
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
		ColorRGBA colors[] = new ColorRGBA[vertex.length];
		for (int x = 0; x < colors.length; x++)
			colors[x] = (ColorRGBA) color.clone();
		setColors(colors);
	}

	/**
	 * Sets every color of this geometry's color array to a random color.
	 */
	public void setRandomColors() {
		ColorRGBA colors[] = new ColorRGBA[vertex.length];
		for (int x = 0; x < colors.length; x++)
			colors[x] = ColorRGBA.randomColor();
		setColors(colors);
	}

	/**
	 * <code>getColorAsFloatBuffer</code> retrieves the float buffer that
	 * contains this geometry's color information.
	 *
	 * @return the buffer that contains this geometry's color information.
	 */
	public FloatBuffer getColorAsFloatBuffer() {
		return colorBuf;
	}

	/**
	 * <code>getVertices</code> returns the vertex array for this geometry.
	 *
	 * @return the array of vertices for this geometry.
	 */
	public Vector3f[] getVertices() {
		return vertex;
	}

	/**
	 * <code>setVertices</code> sets the vertices of this geometry. The
	 * vertices may not be null and will throw an exception if so.
	 *
	 * @param vertex
	 *            the new vertices of this geometry.
	 */
	public void setVertices(Vector3f[] vertex) {
		if (vertex == null) {
			throw new JmeException(
					"Geometry must include vertex information. (102)");
		}
		if (this.vertex.length != vertex.length) {
			vertBuf = null;
		}
		this.vertex = vertex;
		this.vertQuantity = vertex.length;

		updateVertexBuffer();
	}

	/**
	 *
	 * <code>setVertex</code> sets a single vertex into the vertex array. The
	 * index to set it is given, and due to speed considerations, no bounds
	 * checking is done. Therefore, if an invalid index is given, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param index
	 *            the index of the vertex to set.
	 * @param value
	 *            the vertex to set.
	 */
	public void setVertex(int index, Vector3f value) {
		vertex[index] = value;
		vertBuf.put(index * 3, value.x);
		vertBuf.put(index * 3 + 1, value.y);
		vertBuf.put(index * 3 + 2, value.z);
	}

	/**
	 *
	 * <code>setTextureCoord</code> sets a single coord into the texture
	 * array. The index to set it is given, and due to speed considerations, no
	 * bounds checking is done. Therefore, if an invalid index is given, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param textureUnit
	 *            the textureUnit to set on.
	 * @param index
	 *            the index of the coord to set.
	 * @param value
	 *            the vertex to set.
	 */
	public void setTextureCoord(int textureUnit, int index, Vector2f value) {
		this.texture[textureUnit][index] = value;
	}

	/**
	 * <code>getVerticeAsFloatBuffer</code> returns the float buffer that
	 * contains this geometry's vertex information.
	 *
	 * @return the float buffer that contains this geometry's vertex
	 *         information.
	 */
	public FloatBuffer getVerticeAsFloatBuffer() {
		return vertBuf;
	}

	/**
	 * <code>getNormals</code> returns the array that contains this geometry's
	 * normal information.
	 *
	 * @return the normal array for this geometry.
	 */
	public Vector3f[] getNormals() {
		return normal;
	}

	/**
	 * <code>setNormals</code> sets this geometry's normals to a new array of
	 * normal values.
	 *
	 * @param normal
	 *            the new normal values.
	 */
	public void setNormals(Vector3f[] normal) {
		if (this.normal != null) {
			if (normal == null || this.normal.length != normal.length) {
				normBuf = null;
			}
		}
		this.normal = normal;
		updateNormalBuffer();
	}

	/**
	 *
	 * <code>setNormal</code> sets a single normal into the normal array. The
	 * index to set it is given, and due to speed considerations, no bounds
	 * checking is done. Therefore, if an invalid index is given, an
	 * ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param index
	 *            the index of the normal to set.
	 * @param value
	 *            the normal to set.
	 */
	public void setNormal(int index, Vector3f value) {
		normal[index] = value;
		normBuf.put(index * 3, value.x);
		normBuf.put(index * 3 + 1, value.y);
		normBuf.put(index * 3 + 2, value.z);
	}

	/**
	 * <code>getNormalAsFloatBuffer</code> retrieves this geometry's normal
	 * information as a float buffer.
	 *
	 * @return the float buffer containing the geometry information.
	 */
	public FloatBuffer getNormalAsFloatBuffer() {
		return normBuf;
	}

	/**
	 * <code>getTextures</code> retrieves the texture array that contains this
	 * geometry's texture information. The texture coordinates are those of the
	 * first texture unit.
	 *
	 * @return the array that contains the geometry's texture information.
	 */
	public Vector2f[] getTextures() {
		return texture[0];
	}

	/**
	 *
	 * <code>getTextures</code> retrieves the texture array that contains this
	 * geometry's texture information for a given texture unit. If the texture
	 * unit is invalid, or no texture coordinates are set for the texture unit,
	 * null is returned.
	 *
	 * @param textureUnit
	 *            the texture unit to retrieve the coordinates for.
	 * @return the texture coordinates of a given texture unit. Null is returned
	 *         if the texture unit is not valid, or no coordinates are set for
	 *         the given unit.
	 */
	public Vector2f[] getTextures(int textureUnit) {
		if (textureUnit >= 0 && textureUnit < texture.length) {
			return texture[textureUnit];
		} else {
			return null;
		}
	}

	/**
	 * <code>setTextures</code> sets this geometry's texture array to a new
	 * array.
	 *
	 * @param texture
	 *            the new texture information for this geometry.
	 */
	public void setTextures(Vector2f[] texture) {
		if (this.texture != null) {
			if (texture == null || this.texture[0].length != texture.length) {
				texBuf[0] = null;
			}
		}
		this.texture[0] = texture;
		updateTextureBuffer();
	}

	/**
	 *
	 * <code>setTextures</code> sets the texture coordinates of a given
	 * texture unit. If the texture unit is not valid, then the coordinates are
	 * ignored.
	 *
	 * @param textures
	 *            the coordinates to set.
	 * @param textureUnit
	 *            the texture unit to set them to.
	 */
	public void setTextures(Vector2f[] textures, int textureUnit) {
		if (textureUnit < 0 || textureUnit >= this.texture.length) {
			return;
		}
		if (this.texture != null && textures != null) {
			if (this.texture[textureUnit].length != textures.length) {
				texBuf[textureUnit] = null;
			}
		}
		this.texture[textureUnit] = textures;
		updateTextureBuffer(textureUnit);
	}

	/**
	 *
	 * <code>setTexture</code> sets a single texture coordinate into the
	 * texture array. The index to set it is given, and due to speed
	 * considerations, no bounds checking is done. Therefore, if an invalid
	 * index is given, an ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param index
	 *            the index of the texture coordinate to set.
	 * @param value
	 *            the texture coordinate to set.
	 */
	public void setTexture(int index, Vector2f value) {
		texture[0][index] = value;
		texBuf[0].put(index * 2, value.x);
		texBuf[0].put(index * 2 + 1, value.y);
	}

	/**
	 *
	 * <code>setTexture</code> sets a single texture coordinate into the
	 * texture array. The index to set it is given, and due to speed
	 * considerations, no bounds checking is done. Therefore, if an invalid
	 * index is given, an ArrayIndexOutOfBoundsException will be thrown.
	 *
	 * @param index
	 *            the index of the texture coordinate to set.
	 * @param value
	 *            the texture coordinate to set.
	 * @param textureUnit
	 *            the texture unit to alter.
	 */
	public void setTexture(int index, Vector2f value, int textureUnit) {
		texture[textureUnit][index] = value;
		texBuf[textureUnit].put(index * 2, value.x);
		texBuf[textureUnit].put(index * 2 + 1, value.y);
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
		if (fromIndex < 0 || fromIndex >= this.texture.length) {
			return;
		}
		if (toIndex < 0 || toIndex >= this.texture.length) {
			return;
		}
		if (this.texture != null) {
			if (this.texture[fromIndex].length != texture.length) {
				texBuf[toIndex] = null;
			}
		}
		this.texture[toIndex] = (Vector2f[]) texture[fromIndex].clone();
		updateTextureBuffer(toIndex);
	}

	/**
	 * <code>getTextureAsFloatBuffer</code> retrieves this geometry's texture
	 * information contained within a float buffer.
	 *
	 * @return the float buffer that contains this geometry's texture
	 *         information.
	 */
	public FloatBuffer getTextureAsFloatBuffer() {
		return texBuf[0];
	}

	/**
	 *
	 * <code>getTextureAsFloatBuffer</code> retrieves the texture buffer of a
	 * given texture unit. If the texture unit is not valid, null is returned.
	 *
	 * @param textureUnit
	 *            the texture unit to check.
	 * @return the texture coordinates at the given texture unit.
	 */
	public FloatBuffer getTextureAsFloatBuffer(int textureUnit) {
		return texBuf[textureUnit];
	}

	/**
	 *
	 * <code>getNumberOfUnits</code> returns the number of texture units this
	 * geometry supports.
	 *
	 * @return the number of texture units supported by the geometry.
	 */
	public int getNumberOfUnits() {
		return texBuf.length;
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
	 * Sets all texture coordinates to those defined in the given array of
	 * arrays.
	 *
	 * @param texture
	 *            The new texture coordinates.
	 */
	public void setAllTextures(Vector2f[][] texture) {
		this.texture = texture;
	}

	/**
	 * Returns the geometry's texture coordinate information.
	 *
	 * @return The geometry's texture coordinate information.
	 */
	public Vector2f[][] getAllTextures() {
		return texture;
	}

	/**
	 * Clears all vertex, normal, texture, and color buffers by setting them to
	 * null.
	 */
	public void clearBuffers() {
		int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState().getNumberOfUnits();
		vertBuf = null;
		normBuf = null;
		this.texBuf = new FloatBuffer[textureUnits];
		colorBuf = null;
	}

	/**
	 * <code>updateBound</code> recalculates the bounding object assigned to
	 * the geometry. This resets it parameters to adjust for any changes to the
	 * vertex information.
	 *
	 */
	public void updateModelBound() {
		if (bound != null) {
			bound.computeFromPoints(vertex);
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
	 * <code>setColorBuffer</code> calculates the <code>FloatBuffer</code>
	 * that contains all the color information of this geometry.
	 *
	 */
	public void updateColorBuffer() {
		if (color == null) {
			return;
		}
		int bufferLength;
		if (vertQuantity >= 0)
			bufferLength = vertQuantity * 4;
		else
			bufferLength = vertex.length * 4;

		if (colorBuf == null || colorBuf.capacity() < (bufferLength)) {
			colorBuf = ByteBuffer.allocateDirect(4 * bufferLength).order(
					ByteOrder.nativeOrder()).asFloatBuffer();
		}

		colorBuf.clear();

		ColorRGBA tempColor;
		for (int i = 0, max = bufferLength >> 2; i < max; i++) {
			tempColor = color[i];
			if (tempColor != null) {
				colorBuf.put(tempColor.r).put(tempColor.g).put(tempColor.b)
						.put(tempColor.a);
			}
		}

		colorBuf.flip();

	}

	/**
	 * <code>updateVertexBuffer</code> sets the float buffer that contains
	 * this geometry's vertex information.
	 *
	 */
	public void updateVertexBuffer() {
		if (vertex == null) {
			return;
		}
		int bufferLength;
		if (vertQuantity >= 0)
			bufferLength = vertQuantity * 3;
		else
			bufferLength = vertex.length * 3;
		if (vertBuf == null || vertBuf.capacity() < (bufferLength)) {
			vertBuf = ByteBuffer.allocateDirect(4 * bufferLength).order(
					ByteOrder.nativeOrder()).asFloatBuffer();
		}

		vertBuf.clear();

		Vector3f tempVect;
		for (int i = 0, endPoint = bufferLength / 3; i < endPoint; i++) {
			tempVect = vertex[i];
			if (tempVect != null) {
				vertBuf.put(tempVect.x).put(tempVect.y).put(tempVect.z);
			}
		}

		vertBuf.flip();
	}

	/**
	 * <code>updateNormalBuffer</code> sets the float buffer that contains
	 * this geometry's normal information.
	 *
	 */
	public void updateNormalBuffer() {
		if (normal == null) {
			return;
		}
		int bufferLength;
		if (vertQuantity >= 0)
			bufferLength = vertQuantity * 3;
		else
			bufferLength = vertex.length * 3;
		if (normBuf == null || normBuf.capacity() < (bufferLength)) {
			normBuf = ByteBuffer.allocateDirect(4 * bufferLength).order(
					ByteOrder.nativeOrder()).asFloatBuffer();
		}

		normBuf.clear();

		Vector3f tempVect;
		for (int i = 0, endPoint = bufferLength / 3; i < endPoint; i++) {
			tempVect = normal[i];
			if (tempVect != null) {
				normBuf.put(tempVect.x).put(tempVect.y).put(tempVect.z);
			}
		}

		normBuf.flip();

	}

	/**
	 * <code>updateTextureBuffer</code> sets the float buffer that contains
	 * this geometry's texture information. Updates textureUnit 0.
	 *
	 */
	public void updateTextureBuffer() {
		updateTextureBuffer(0);
	}

	/**
	 * <code>updateTextureBuffer</code> sets the float buffer that contains
	 * this geometry's texture information.
	 *
	 */
	public void updateTextureBuffer(int textureUnit) {
		if (texture == null) {
			return;
		}
		if (texture[textureUnit] == null) {
			texBuf[textureUnit] = null;
			return;
		}
		int bufferLength;
		if (vertQuantity >= 0)
			bufferLength = vertQuantity * 2;
		else
			bufferLength = vertex.length * 2;

		if (texBuf[textureUnit] == null
				|| texBuf[textureUnit].capacity() < (bufferLength)) {
			texBuf[textureUnit] = ByteBuffer.allocateDirect(4 * bufferLength)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
		}

		texBuf[textureUnit].clear();

		Vector2f tempVect;
		for (int i = 0, max = bufferLength >> 1; i < max; i++) {
			tempVect = texture[textureUnit][i];
			if (tempVect != null) {
				texBuf[textureUnit].put(tempVect.x).put(tempVect.y);
			}
		}

		texBuf[textureUnit].flip();

	}

	/**
	 * <code>randomVertice</code> returns a random vertex from the list of
	 * vertices set to this geometry. If there are no vertices set, null is
	 * returned.
	 *
	 * @return Vector3f a random vertex from the vertex list. Null is returned
	 *         if the vertex list is not set.
	 */
	public Vector3f randomVertice() {
		if (vertex == null)
			return null;
		int i = (int) (FastMath.nextRandomFloat() * vertQuantity);

		return worldRotation.mult(vertex[i]).addLocal(worldTranslation);
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

	/**
	 * Sets this geometry's first texture buffer as a refrence to the passed
	 * <code>FloatBuffer</code>. Incorrectly built FloatBuffers can have
	 * undefined results. Use with care.
	 *
	 * @param toSet
	 *            The <code>FloatBuffer</code> to set this geometry's first
	 *            texture buffer to
	 */
	protected void setTextureBuffer(FloatBuffer toSet) {
		this.texBuf[0] = toSet;
	}

	/**
	 * Sets this geometry's normal buffer as a refrence to the passed
	 * <code>FloatBuffer</code>. Incorrectly built FloatBuffers can have
	 * undefined results. Use with care.
	 *
	 * @param toSet
	 *            The <code>FloatBuffer</code> to set this geometry's normal
	 *            buffer to
	 */
	protected void setNormalBuffer(FloatBuffer toSet) {
		normBuf = toSet;
	}

	/**
	 * Sets this geometry's vertex buffer as a refrence to the passed
	 * <code>FloatBuffer</code>. Incorrectly built FloatBuffers can have
	 * undefined results. Use with care.
	 *
	 * @param toSet
	 *            The <code>FloatBuffer</code> to set this geometry's vertex
	 *            buffer to
	 */
	protected void setVertexBuffer(FloatBuffer toSet) {
		vertBuf = toSet;
	}

	/**
	 * Sets this geometry's color buffer as a refrence to the passed
	 * <code>FloatBuffer</code>. Incorrectly built FloatBuffers can have
	 * undefined results. Use with care.
	 *
	 * @param toSet
	 *            The <code>FloatBuffer</code> to set this geometry's color
	 *            buffer to
	 */
	protected void setFloatBuffer(FloatBuffer toSet) {
		colorBuf = toSet;
	}

	/**
	 * Used with Serialization. Not to be called manually.
	 *
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @see java.io.Serializable
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState().getNumberOfUnits();
		texBuf = new FloatBuffer[textureUnits];
		if (color != null)
			updateColorBuffer();
		if (normal != null)
			updateNormalBuffer();
		if (vertex != null)
			updateVertexBuffer();
		if (texture != null) {
			for (int i = 0; i < texture.length; i++)
				if (texture[i] != null && texture[i].length != 0)
					updateTextureBuffer(i);
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
			toStore.vertBuf = this.vertBuf;
			toStore.vertex = this.vertex;
			toStore.vertQuantity = this.vertQuantity;
		} else {
			Vector3f[] temp = new Vector3f[this.vertex.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = new Vector3f(this.vertex[i]);
			}
			toStore.setVertices(temp);
		}

		if (properties.isSet("colors")) { // if I should shallow copy colors
			toStore.colorBuf = this.colorBuf;
			toStore.color = this.color;
		} else if (color != null) { // If I should deep copy colors

			ColorRGBA[] temp = new ColorRGBA[this.color.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = new ColorRGBA(this.color[i]);
			}
			toStore.setColors(temp);
		}

		if (properties.isSet("normals")) {
			toStore.normBuf = this.normBuf;
			toStore.normal = this.normal;
		} else if (normal != null) {
			Vector3f[] temp = new Vector3f[this.normal.length];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = new Vector3f(this.normal[i]);
			}
			toStore.setNormals(temp);
		}

		if (properties.isSet("texcoords")) {
			toStore.texBuf = this.texBuf;
			toStore.texture = this.texture;
		} else {
			Vector2f[][] temp = new Vector2f[this.texture.length][];
			for (int i = 0; i < temp.length; i++) {
				if (this.texBuf[i] == null)
					continue;
				temp[i] = new Vector2f[this.texture[i].length];
				for (int j = 0; j < temp[i].length; j++) {
					temp[i][j] = new Vector2f(this.texture[i][j]);
				}
				toStore.setTextures(temp[i], i);
			}
		}

		if (bound != null)
			toStore.setModelBound((BoundingVolume) bound.clone(null));

		toStore.useVBOVertex = this.useVBOVertex;
		toStore.useVBOColor = this.useVBOColor;
		toStore.useVBONormal = this.useVBONormal;
		toStore.useVBOTexture = this.useVBOTexture;

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
}
