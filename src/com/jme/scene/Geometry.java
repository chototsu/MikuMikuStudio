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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;
import java.util.logging.Level;

import com.jme.bounding.BoundingVolume;
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
 * @version $Id: Geometry.java,v 1.41 2004-05-19 16:16:25 renanse Exp $
 */
public abstract class Geometry extends Spatial implements Serializable {

    protected BoundingVolume bound;

    //data that specifies how to render this leaf.
    protected Vector3f[] vertex;

    protected Vector3f[] normal;

    protected ColorRGBA[] color;

    protected Vector2f[][] texture;

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
                .getTextureState().getNumberOfUnits();
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
            throw new JmeException("Geometry must include vertex information.");
        }

        int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
                .getTextureState().getNumberOfUnits();
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
            throw new JmeException("Geometry must include vertex information.");
        }

        int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
                .getTextureState().getNumberOfUnits();
        this.texture = new Vector2f[textureUnits][0];
        this.texBuf = new FloatBuffer[textureUnits];
        this.vertex = vertices;
        this.normal = normal;
        this.color = color;
        this.texture[0] = texture;

        updateColorBuffer();
        updateNormalBuffer();
        updateVertexBuffer();
        updateTextureBuffer();
    }

    public boolean isVBOVertexEnabled() {
        return useVBOVertex;
    }

    public boolean isVBOTextureEnabled() {
        return useVBOTexture;
    }

    public boolean isVBONormalEnabled() {
        return useVBONormal;
    }

    public boolean isVBOColorEnabled() {
        return useVBOColor;
    }

    public void setVBOVertexEnabled(boolean enabled) {
        useVBOVertex = enabled;
    }

    public void setVBOTextureEnabled(boolean enabled) {
        useVBOTexture = enabled;
    }

    public void setVBONormalEnabled(boolean enabled) {
        useVBONormal = enabled;
    }

    public void setVBOColorEnabled(boolean enabled) {
        useVBOColor = enabled;
    }

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
            if (this.color.length != color.length) {
                colorBuf = null;
            }
        }
        this.color = color;
        updateColorBuffer();
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
        if (vertex == null) { throw new JmeException(
                "Geometry must include vertex information."); }
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
            if (this.normal.length != normal.length) {
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
            if (this.texture[0].length != texture.length) {
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
        if (textureUnit < 0 || textureUnit >= this.texture.length) { return; }
        if (this.texture != null) {
            if (this.texture[textureUnit].length != texture.length) {
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
        if (fromIndex < 0 || fromIndex >= this.texture.length) { return; }
        if (toIndex < 0 || toIndex >= this.texture.length) { return; }
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

    public int getVertQuantity() {
        return vertQuantity;
    }

    public void setAllTextures(Vector2f[][] texture) {
        this.texture = texture;
    }

    public Vector2f[][] getAllTextures() {
        return texture;
    }

    public void clearBuffers() {
        int textureUnits = DisplaySystem.getDisplaySystem().getRenderer()
                .getTextureState().getNumberOfUnits();
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
        this.bound = modelBound;
    }

    /**
     *
     * <code>setStates</code> applies all the render states for this
     * particular geometry.
     *
     */
    public void applyStates() {
        if (parent != null) Spatial.clearCurrentStates();
        for (int i = 0; i < states.length; i++) {
            if (states[i] != currentStates[i]) {
                states[i].apply();
                currentStates[i] = states[i];
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
        if (color == null) { return; }

        int bufferLength = vertex.length * 4;

        if (colorBuf == null) {
            colorBuf = ByteBuffer.allocateDirect(4 * bufferLength).order(
                    ByteOrder.nativeOrder()).asFloatBuffer();
        }

        colorBuf.clear();

        ColorRGBA tempColor;
        for (int i = 0; i < bufferLength; i++) {
          tempColor = color[i];
          if (tempColor != null) {
            vertBuf.put(tempColor.r).put(tempColor.g)
                   .put(tempColor.b).put(tempColor.a);
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
      if (vertex == null) { return; }
      int bufferLength;
      if (vertQuantity >= 0)
        bufferLength = vertQuantity * 3;
      else
        bufferLength = vertex.length * 3;
      if (vertBuf == null || vertBuf.capacity() < (4 * bufferLength)) {
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
        if (normal == null) { return; }
        int bufferLength;
        if (vertQuantity >= 0)
          bufferLength = vertQuantity * 3;
        else
          bufferLength = vertex.length * 3;
        if (normBuf == null) {
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
     * this geometry's texture information.  Updates textureUnit 0.
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
        if (texture == null) { return; }
        if (texture[textureUnit] == null) { return; }
        int bufferLength;
        if (vertQuantity >= 0)
          bufferLength = vertQuantity * 2;
        else
          bufferLength = vertex.length * 2;

        if (texBuf[textureUnit] == null) {
            texBuf[textureUnit] = ByteBuffer.allocateDirect(4 * bufferLength)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        }

        texBuf[textureUnit].clear();

        Vector2f tempVect;
        for (int i = 0, max = bufferLength>>1; i < max; i++) {
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
        if (vertex == null) return null;
        int i = (int) (FastMath.nextRandomFloat() * vertQuantity);
        return vertex[i].add(worldTranslation);
    }

}
