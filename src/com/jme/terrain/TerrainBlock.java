/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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

package com.jme.terrain;

import com.jme.renderer.Renderer;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * @author Mark Powell
 */
public class TerrainBlock extends AreaClodMesh {

    private int size;

    private float stepScale;
    
    private boolean useClod;
    
    private int totalSize;
    private Vector2f offset;

    /** Creates a new instance of TerrainBlock */
    public TerrainBlock(String name, int size, float stepScale,
            int[] heightMap, Vector3f origin, boolean clod) {
        this(name, size, stepScale, heightMap, origin, clod, size, new Vector2f());
    }
    
    public TerrainBlock(String name, int size, float stepScale,
            int[] heightMap, Vector3f origin, boolean clod, int totalSize, Vector2f offset) {
        super(name);
        this.useClod = clod;
        this.size = size;
        this.stepScale = stepScale;
        this.totalSize = totalSize;
        this.offset = offset;
        
        setLocalTranslation(origin);
        buildVertices(heightMap);
        buildTextureCoordinates();
        buildNormals();
        
        if(useClod) {
            this.create(null);
            this.setTrisPerPixel(0.02f);
        }
    }

    public int chooseTargetRecord(Renderer r) {
    	if(useClod) {
    	    return super.chooseTargetRecord(r);
    	} else {
    	    return 0;
    	}
    }
    
    /**
     * 
     * <code>setDetailTexture</code> sets the detail texture unit's repeat
     * value.
     *
     * @param unit
     * @param repeat
     */
    public void setDetailTexture(int unit, int repeat) {
        texture[unit] = new Vector2f[texture[0].length];
        for (int i = 0; i < texture[0].length; i++) {
            texture[unit][i] = texture[0][i].mult(repeat);
        }

        setTextures(texture[unit], unit);
    }

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     * 
     * @param heightMap
     *            the raw data.
     */
    private void buildVertices(int[] heightMap) {
        vertex = new Vector3f[heightMap.length];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                vertex[x + (y * size)] = new Vector3f(x * stepScale,
                        heightMap[x + (y * size)], y * stepScale);
            }
        }

        //set up the indices
        int value = ((size - 1) * (size - 1)) * 6;
        indices = new int[value];

        int count = 0;

        //go through entire array up to the second to last column.
        for (int i = 0; i < (size * (size - 1)); i++) {
            //we want to skip the top row.
            if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
                continue;
            }
            //set the top left corner.
            indices[count++] = i;
            //set the bottom right corner.
            indices[count++] = ((1 + size) + i);
            //set the top right corner.
            indices[count++] = (1 + i);
            //set the top left corner
            indices[count++] = i;
            //set the bottom left corner
            indices[count++] = size + i;
            //set the bottom right corner
            indices[count++] = ((1 + size) + i);

        }

        setVertices(vertex);
        setIndices(indices);
    }

    /**
     * <code>buildTextureCoordinates</code> calculates the texture coordinates
     * of the terrain.
     *  
     */
    private void buildTextureCoordinates() {
        offset.x += (int)(size/2 * stepScale);
        offset.y += (int)(size/2 * stepScale);
        
        texture[0] = new Vector2f[vertex.length];

        for (int i = 0; i < texture[0].length; i++) {
            texture[0][i] = new Vector2f(
                    (vertex[i].x + offset.x) / (stepScale * (totalSize - 1)),
                    (vertex[i].z + offset.y) / (stepScale * (totalSize - 1)));
        }

        setTextures(texture[0]);
    }

    /**
     * 
     * <code>buildNormals</code> calculates the normals of each vertex that
     * makes up the block of terrain.
     * 
     *  
     */
    private void buildNormals() {
        Vector3f[] normal = new Vector3f[vertex.length];

        //get the first and last normals taken care of.
        normal[0] = vertex[size].cross(vertex[1]).normalize();
        normal[normal.length - 1] = vertex[normal.length - 1 - size].cross(
                vertex[normal.length - 2]).normalize();

        for (int i = 1; i < normal.length - 1; i++) {
            if (i % ((size * (i / size + 1)) - 1) == 0) {
                //right hand normal
                normal[i] = new Vector3f();
            } else if (i >= size * (size - 1)) {
                //bottom row
                normal[i] = new Vector3f(0,1,0);
            } else {
                //interior
                normal[i] = vertex[i + size].cross(vertex[i + 1]).normalize();
            }
        }

        setNormals(normal);
    }
}