/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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
import java.util.logging.Level;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>Geometry</code> defines a leaf node of the scene graph. The leaf 
 * node contains the geometric data for rendering objects. It manages all
 * rendering information such as a collection of states and the data for a 
 * model. Subclasses define what the model data is.
 * @author Mark Powell
 * @version $Id: Geometry.java,v 1.3 2003-10-17 20:45:04 mojomonkey Exp $
 */
public class Geometry extends Spatial implements Serializable {
    protected BoundingVolume bound;
    //data that specifies how to render this leaf.
    protected Vector3f[] vertex;
    protected Vector3f[] normal;
    protected ColorRGBA[] color;
    protected Vector2f[] texture;

    //buffers that allow for faster data processing.
    private FloatBuffer colorBuf;
    private FloatBuffer normBuf;
    private FloatBuffer vertBuf;
    private FloatBuffer texBuf;
    
    /**
     * Constructor instantiates a new <code>Geometry</code> object. This
     * is the default object which has an empty vertex array. All other
     * data is null.
     *
     */
    public Geometry() {
        vertex = new Vector3f[0];
    }

    /**
     * Constructor creates a new <code>Geometry</code> object. During
     * instantiation the geometry is set including vertex, normal, color and
     * texture information. Any part may be null except for the vertex 
     * information. If this is null, an exception will be thrown.
     * @param vertex the points that make up the geometry.
     * @param normal the normals of the geometry.
     * @param color the color of each point of the geometry.
     * @param texture the texture coordinates of the geometry.
     */
    public Geometry(
        Vector3f[] vertex,
        Vector3f[] normal,
        ColorRGBA[] color,
        Vector2f[] texture) {

        super();

        if (vertex == null) {
            LoggingSystem.getLogger().log(Level.WARNING, "Geometry must" +                " include vertex information.");
            throw new JmeException("Geometry must include vertex information.");
        }

        this.vertex = vertex;
        this.normal = normal;
        this.color = color;
        this.texture = texture;

        setColorBuffer();
        setNormalBuffer();
        setVertexBuffer();
        setTextureBuffer();
    }

    /**
     * <code>getColors</code> returns the color information of the geometry.
     * This may be null and should be check for such a case.
     * @return the color array.
     */
    public ColorRGBA[] getColors() {
        return color;
    }

    /**
     * <code>setColors</code> sets the color array of this geometry.
     * @param color the new color array.
     */
    public void setColors(ColorRGBA[] color) {
        this.color = color;
        setColorBuffer();
    }

    /**
     * <code>getColorAsFloatBuffer</code> retrieves the float buffer that
     * contains this geometry's color information.
     * @return the buffer that contains this geometry's color information.
     */
    public FloatBuffer getColorAsFloatBuffer() {
        return colorBuf;
    }

    /**
     * <code>getVertices</code> returns the vertex array for this geometry.
     * @return the array of vertices for this geometry.
     */
    public Vector3f[] getVertices() {
        return vertex;
    }

    /**
     * <code>setVertices</code> sets the vertices of this geometry. The
     * vertices may not be null and will throw an exception if so.
     * @param vertex the new vertices of this geometry.
     */
    public void setVertices(Vector3f[] vertex) {
        if (vertex == null) {
            throw new JmeException("Geometry must include vertex information.");
        }
        this.vertex = vertex;
        setVertexBuffer();
    }

    /**
     * <code>getVerticeAsFloatBuffer</code> returns the float buffer that 
     * contains this geometry's vertex information.
     * @return the float buffer that contains this geometry's vertex information.
     */
    public FloatBuffer getVerticeAsFloatBuffer() {
        return vertBuf;
    }

    /**
     * <code>getNormals</code> returns the array that contains this 
     * geometry's normal information.
     * @return the normal array for this geometry.
     */
    public Vector3f[] getNormals() {
        return normal;
    }

    /**
     * <code>setNormals</code> sets this geometry's normals to a new array of
     * normal values.
     * @param normal the new normal values.
     */
    public void setNormals(Vector3f[] normal) {
        this.normal = normal;
        setNormalBuffer();
    }

    /**
     * <code>getNormalAsFloatBuffer</code> retrieves this geometry's normal
     * information as a float buffer.
     * @return the float buffer containing the geometry information.
     */
    public FloatBuffer getNormalAsFloatBuffer() {
        return normBuf;
    }

    /**
     * <code>getTextures</code> retrieves the texture array that contains this
     * geometry's texture information.
     * @return the array that contains the geometry's texture information.
     */
    public Vector2f[] getTextures() {
        return texture;
    }

    /**
     * <code>setTextures</code> sets this geometry's texture array to a new
     * array.
     * @param texture the new texture information for this geometry.
     */
    public void setTextures(Vector2f[] texture) {
        this.texture = texture;
        setTextureBuffer();
    }

    /**
     * <code>getTextureAsFloatBuffer</code> retrieves this geometry's texture
     * information contained within a float buffer.
     * @return the float buffer that contains this geometry's texture 
     *      information.
     */
    public FloatBuffer getTextureAsFloatBuffer() {
        return texBuf;
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
        }
    }

    /**
     * 
     * <code>getModelBound</code> retrieves the bounding object that contains
     * the geometry node's vertices.
     * @return the bounding object for this geometry.
     */
    public BoundingVolume getModelBound() {
        return bound;
    }

    /**
     * 
     * <code>setModelBound</code> sets the bounding object for this geometry.
     * @param modelBound the bounding object for this geometry.
     */
    public void setModelBound(BoundingVolume modelBound) {
        this.bound = modelBound;
    }
    
    /**
     * <code>draw</code> prepares the geometry for rendering to the display. 
     * The renderstate is set and the subclass is responsible for rendering
     * the actual data.
     * @see com.jme.scene.Spatial#draw(com.jme.renderer.Renderer)
     * @param r the renderer that displays to the context.
     */
    public void draw(Renderer r) {
        //set state

    }

    /**
     * <code>updateWorldBound</code> updates the bounding volume that contains
     * this geometry. The location of the geometry is based on the location
     * of all this node's parents. 
     * @see com.jme.scene.Spatial#updateWorldBound()
     */
    public void updateWorldBound() {
        if(bound != null) {
            worldBound =
                bound.transform(worldRotation, worldTranslation, worldScale);
        }  
    } 
    
    /**
     * <code>setColorBuffer</code> calculates the <code>FloatBuffer</code>
     * that contains all the color information of this geometry.
     *
     */
    private void setColorBuffer() {
        if (color == null) {
            return;
        }
        float[] buffer = new float[vertex.length * 4];

        colorBuf =
            ByteBuffer
                .allocateDirect(4 * buffer.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for (int i = 0; i < vertex.length; i++) {
            buffer[i * 4] = color[i].r;
            buffer[i * 4 + 1] = color[i].g;
            buffer[i * 4 + 2] = color[i].b;
            buffer[i * 4 + 3] = color[i].a;
        }

        colorBuf.clear();
        colorBuf.put(buffer);
        colorBuf.flip();

    }

    /**
     * <code>setVertexBuffer</code> sets the float buffer that contains this
     * geometry's vertex information.
     *
     */
    private void setVertexBuffer() {
        if (vertex == null) {
            return;
        }
        float[] buffer = new float[vertex.length * 3];
        vertBuf =
            ByteBuffer
                .allocateDirect(4 * buffer.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for (int i = 0; i < vertex.length; i++) {
            buffer[i * 3] = vertex[i].x;
            buffer[i * 3 + 1] = vertex[i].y;
            buffer[i * 3 + 2] = vertex[i].z;
        }

        vertBuf.clear();
        vertBuf.put(buffer);
        vertBuf.flip();

    }

    /**
     * <code>setNormalBuffer</code> sets the float buffer that contains
     * this geometry's normal information.
     *
     */
    private void setNormalBuffer() {
        if (normal == null) {
            return;
        }
        float[] buffer = new float[vertex.length * 3];
        normBuf =
            ByteBuffer
                .allocateDirect(4 * buffer.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for (int i = 0; i < vertex.length; i++) {
            buffer[i * 3] = normal[i].x;
            buffer[i * 3 + 1] = normal[i].y;
            buffer[i * 3 + 2] = normal[i].z;
        }

        normBuf.clear();
        normBuf.put(buffer);
        normBuf.flip();

    }

    /**
     * <code>setTextureBuffer</code> sets the float buffer that contains
     * this geometry's texture information.
     *
     */
    private void setTextureBuffer() {
        if (texture == null) {
            return;
        }
        float[] buffer = new float[vertex.length * 2];
        texBuf =
            ByteBuffer
                .allocateDirect(4 * buffer.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for (int i = 0; i < vertex.length; i++) {
            buffer[i * 2] = texture[i].x;
            buffer[i * 2 + 1] = texture[i].y;
        }

        texBuf.clear();
        texBuf.put(buffer);
        texBuf.flip();

    }

    
}
