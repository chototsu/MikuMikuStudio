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
import java.nio.IntBuffer;
import java.util.logging.Level;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.JmeException;
import com.jme.util.LoggingSystem;

/**
 * <code>TriMesh</code> defines a geometry mesh. This mesh defines a three
 * dimensional object via a collection of points, colors, normals and textures.
 * The points are referenced via a indices array. This array instructs the 
 * renderer the order in which to draw the points, creating triangles on
 * every three points. 
 * @author Mark Powell
 * @version $Id: TriMesh.java,v 1.4 2004-02-20 20:17:49 mojomonkey Exp $
 */
public class TriMesh extends Geometry implements Serializable {
    private int[] indices;
    private IntBuffer indexBuffer;
    
    /**
     * Constructor instantiates a new <code>TriMesh</code> object.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     */
    public TriMesh(String name) {
    	super(name);
        
    }

    /**
     * Constructor instantiates a new <code>TriMesh</code> object. Provided
     * are the attributes that make up the mesh all attributes may be null,
     * except for vertices and indices.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param vertices the vertices of the geometry.
     * @param normal the normals of the geometry.
     * @param color the colors of the geometry.
     * @param texture the texture coordinates of the mesh.
     * @param indices the indices of the vertex array.
     */
    public TriMesh(
    	String name,
        Vector3f[] vertices,
        Vector3f[] normal,
        ColorRGBA[] color,
        Vector2f[] texture,
        int[] indices) {

        super(name, vertices, normal, color, texture);

        if(null == indices) {
            LoggingSystem.getLogger().log(Level.WARNING, "Indices may not be" +                " null.");
            throw new JmeException("Indices may not be null.");
        }
        this.indices = indices;

        setIndexBuffers();
        LoggingSystem.getLogger().log(Level.INFO, "TriMesh created.");
    }

    /**
     * 
     * <code>getIndices</code> retrieves the indices into the vertex array.
     * @return the indices into the vertex array.
     */
    public int[] getIndices() {
        return indices;
    }

    /**
     * 
     * <code>getIndexAsBuffer</code> retrieves the indices array as an 
     * <code>IntBuffer</code>.
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexAsBuffer() {
        return indexBuffer;
    }

    /**
     * 
     * <code>setIndices</code> sets the index array for this <code>TriMesh</code>.
     * @param indices the index array.
     */
    public void setIndices(int[] indices) {
        this.indices = indices;
        setIndexBuffers();
    }
    
    /**
     * <code>draw</code> calls super to set the render state then passes itself
     * to the renderer.
     * @param r the renderer to display
     */
    public void draw(Renderer r) {
        super.draw(r);
        r.draw(this);
    }

    /**
     * 
     * <code>setIndexBuffers</code> creates the <code>IntBuffer</code> that
     * contains the indices array.
     *
     */
    private void setIndexBuffers() {
        indexBuffer =
            ByteBuffer
                .allocateDirect(4 * indices.length)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        indexBuffer.clear();
        indexBuffer.put(indices);
        indexBuffer.flip();
    }


}
