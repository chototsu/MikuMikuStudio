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
package com.jme.scene.shape;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;

/**
 * <code>Pyramid</code> provides an extension of <code>TriMesh</code>. A 
 * pyramid is defined by a width at the base and a height. The pyramid is a
 * four sided pyramid with the center at (0,0). The pyramid will be axis aligned
 * with the peak being on the positive y axis and the base being in the x-z
 * plane.
 * @author Mark Powell
 * @version $Id: Pyramid.java,v 1.1 2004-04-02 15:52:07 mojomonkey Exp $
 */
public class Pyramid extends TriMesh {
    private float height;
    private float width;
    
    /**
     * Constructor instantiates a new <code>Pyramid</code> object. The base
     * width and the height are provided.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param width the base width of the pyramid.
     * @param height the height of the pyramid from the base to the peak.
     */
    public Pyramid(String name, float width, float height) {
    	super(name);
        this.width = width;
        this.height = height;
        
        setVertexData();
        setNormalData();
        setTextureData();
        setColorData();
        setIndexData();
    }
    
    /**
     * 
     * <code>setVertexData</code> sets the vertices that make the pyramid. Where
     * the center of the box is the origin and the base and height are set
     * during construction.
     *
     */
    private void setVertexData() {
        Vector3f peak = new Vector3f(0, height/2, 0);
        Vector3f vert0 = new Vector3f(-width/2, -height/2, -width/2);
        Vector3f vert1 = new Vector3f(width/2, -height/2, -width/2);
        Vector3f vert2 = new Vector3f(width/2, -height/2, width/2);
        Vector3f vert3 = new Vector3f(-width/2, -height/2, width/2);
        
        Vector3f[] vertices = new Vector3f[16];
        
        //base
        vertices[0] = vert3;
        vertices[1] = vert2;
        vertices[2] = vert1;
        vertices[3] = vert0;
        
        //side 1
        vertices[4] = vert0;
        vertices[5] = vert1;
        vertices[6] = peak;
        
        //side 2
        vertices[7] = vert1;
        vertices[8] = vert2;
        vertices[9] = peak;
        
        //side 3
        vertices[10] = vert2;
        vertices[11] = vert3;
        vertices[12] = peak;
        
        //side 4
        vertices[13] = vert3;
        vertices[14] = vert0;
        vertices[15] = peak;
        
        setVertices(vertices);
    }

    /**
     * 
     * <code>setNormalData</code> defines the normals of each face of the 
     * pyramid.
     *
     */
    private void setNormalData() {
        Vector3f[] normals = new Vector3f[24];
        Vector3f front = new Vector3f(0, 1, 1);
        Vector3f right = new Vector3f(1, 1, 0);
        Vector3f back = new Vector3f(0, 1, -1);
        Vector3f left = new Vector3f(-1, 1, 0);
        Vector3f bottom = new Vector3f(0, -1, 0);
        
        normals[0] = bottom;
        normals[1] = bottom;
        normals[2] = bottom;
        normals[3] = bottom;
        
        normals[4] = back;
        normals[5] = back;
        normals[6] = back;
        
        normals[7] = right;
        normals[8] = right;
        normals[9] = right;
        
        normals[10] = front;
        normals[11] = front;
        normals[12] = front;
        
        normals[13] = left;
        normals[14] = left;
        normals[15] = left;
        
        setNormals(normals);
        
        
    }
    
    /**
     * 
     * <code>setTextureData</code> sets the texture that defines the look
     * of the pyramid. The top point of the pyramid is the top center of
     * the texture, with the remaining texture wrapping around it.
     *
     */
    private void setTextureData() {
        Vector2f[] textures = new Vector2f[16];
        Vector2f br = new Vector2f(0, 0);
        Vector2f bl = new Vector2f(1, 0);
        Vector2f tl = new Vector2f(1, 1);
        Vector2f tr = new Vector2f(0, 1);
        Vector2f tc = new Vector2f(0.5f,1);
        Vector2f q1 = new Vector2f(0.75f, 0);
        Vector2f q2 = new Vector2f(0.5f, 0);
        Vector2f q3 = new Vector2f(0.25f,0);
        
        textures[0] = bl;
        textures[1] = br;
        textures[2] = tr;
        textures[3] = tl;
        
        textures[4] = bl;
        textures[5] = q1;
        textures[6] = tc;
        
        textures[7] = q1;
        textures[8] = q2;
        textures[9] = tc;
        
        textures[10] = q2;
        textures[11] = q3;
        textures[12] = tc;
        
        textures[13] = q3;
        textures[14] = br;
        textures[15] = tc;
        
        setTextures(textures);
    }
    
    /**
     * 
     * <code>setColorData</code> sets the color of all vertices to white.
     *
     */
    private void setColorData() {
        ColorRGBA[] color = new ColorRGBA[16];
        for (int i = 0; i < color.length; i++) {
            color[i] = new ColorRGBA(1, 1, 1, 1);
        }
        setColors(color);
    }
    
    /**
     * 
     * <code>setIndexData</code> sets the indices into the list of vertices,
     * defining all triangles that constitute the pyramid.
     *
     */
    private void setIndexData() {
        int[] index = {
          3,2,1,
          3,1,0,
          6,5,4,
          9,8,7,
          12,11,10,
          15,14,13  
        };
        
        setIndices(index);
    }
}
