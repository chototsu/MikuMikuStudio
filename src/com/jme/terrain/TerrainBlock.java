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

package com.jme.terrain;

import com.jme.renderer.Renderer;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;

/**
 * <code>TerrainBlock</code> defines the lowest level of the terrain system. 
 * <code>TerrainBlock</code> is the actual part of the terrain system that
 * renders to the screen. The terrain is built from a heightmap defined by
 * a one dimenensional int array. The step scale is used to define the
 * amount of units each block line will extend. Clod can be used to allow for 
 * level of detail control.
 * 
 * By directly creating a <code>TerrainBlock</code> yourself, you can generate
 * a brute force terrain. This is many times sufficient for small terrains on
 * modern hardware. If terrain is to be large, it is recommended that you make
 * use of the <code>TerrainPage</code> class.
 * 
 * @author Mark Powell
 * @version $Id: TerrainBlock.java,v 1.19 2004-04-30 17:29:45 mojomonkey Exp $
 */
public class TerrainBlock extends AreaClodMesh {
    //size of the block, totalSize is the total size of the heightmap if this
    //block is just a small section of it.
    private int size;
    private int totalSize;

    //x/z step
    private Vector3f stepScale;
    //use lod or not
    private boolean useClod;
    
    //center of the block in relation to (0,0,0)
    private Vector2f offset;
    //amount the block has been shifted.
    private int offsetAmount;

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The 
     * parameters and heightmap data are then processed to generate a 
     * <code>TriMesh</code> object for renderering.
     * @param name the name of the terrain block.
     * @param size the size of the heightmap.
     * @param stepScale the scale for the axes.
     * @param heightMap the height data.
     * @param origin the origin offset of the block.
     * @param clod true will use level of detail, false will not.
     */
    public TerrainBlock(String name, int size, Vector3f stepScale,
            int[] heightMap, Vector3f origin, boolean clod) {
        this(name, size, stepScale, heightMap, origin, clod, size,
                new Vector2f(), 0);
    }

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a 
     * <code>TriMesh</code> object for renderering.
     * @param name the name of the terrain block.
     * @param size the size of the block.
     * @param stepScale the scale for the axes.
     * @param heightMap the height data.
     * @param origin the origin offset of the block.
     * @param clod true will use level of detail, false will not.
     * @param totalSize the total size of the terrain. (Higher if the block
     * 		is part of a <code>TerrainPage</code> tree.
     * @param offset the offset for texture coordinates.
     * @param offsetAmount the total offset amount.
     */
    protected TerrainBlock(String name, int size, Vector3f stepScale,
            int[] heightMap, Vector3f origin, boolean clod, int totalSize,
            Vector2f offset, int offsetAmount) {
        super(name);
        this.useClod = clod;
        this.size = size;
        this.stepScale = stepScale;
        this.totalSize = totalSize;
        this.offsetAmount = offsetAmount;
        this.offset = offset;

        setLocalTranslation(origin);
        buildVertices(heightMap);
        buildTextureCoordinates();
        buildNormals();

        if (useClod) {
            this.create(null);
            this.setTrisPerPixel(0.02f);
        }
    }

    /**
     * <code>chooseTargetRecord</code> determines which level of detail to use.
     * If CLOD is not used, the index 0 is always returned.
     * 
     * @param r the renderer to use for determining the LOD record.
     * @return the index of the record to use.
     */
    public int chooseTargetRecord(Renderer r) {
        if (useClod) {
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
    
    public float getHeight(Vector2f position) {
        return getHeight(position.x, position.y);
    }
    
    public float getHeight(Vector3f position) {
        return getHeight(position.x, position.y);
    }
    
    public float getHeight(float x, float z) {
        x /= stepScale.x;
        z /= stepScale.z;
        
        if(x + z*size < 0 || x + z*size > vertex.length) {
            return Float.NaN;
        }
        
        float low, highX, highZ;
        float intX, intY;
        float interpolation;

        low = vertex[(int)x + (int) z*size].y;
        
        if (x + 1 > size) {
            return low;
        } else {
            highX = vertex[(int)x + (int) (z+1)*size].y;
        }

        interpolation = x - (int) x;
        intX = ((highX - low) * interpolation) + low;

        if (z + 1 > size) {
            return low;
        } else {
            highZ = vertex[(int)(x + 1) + (int) z*size].y;
        }

        interpolation = z - (int) z;
        intY = ((highZ - low) * interpolation) + low;

        return ((intX + intY) / 2);
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
                vertex[x + (y * size)] = new Vector3f(x * stepScale.x,
                        heightMap[x + (y * size)] * stepScale.y, y * stepScale.z);
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
        offset.x += (int) (offsetAmount * stepScale.x);
        offset.y += (int) (offsetAmount * stepScale.z);

        texture[0] = new Vector2f[vertex.length];

        for (int i = 0; i < texture[0].length; i++) {
            texture[0][i] = new Vector2f((vertex[i].x + offset.x)
                    / (stepScale.x * (totalSize - 1)), (vertex[i].z + offset.y)
                    / (stepScale.z * (totalSize - 1)));
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

        int normalIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row == size - 1) {
                    if (col == size - 1) { // last row, last col
                        // up cross left
                        normal[normalIndex] = vertex[normalIndex - size]
                                .subtract(vertex[normalIndex]).cross(
                                        vertex[normalIndex - 1]
                                                .subtract(vertex[normalIndex]))
                                .normalize();
                    } else { // last row, except for last col
                        // right cross up
                        normal[normalIndex] = vertex[normalIndex + 1].subtract(
                                vertex[normalIndex]).cross(
                                vertex[normalIndex - size]
                                        .subtract(vertex[normalIndex]))
                                .normalize();
                    }
                } else {
                    if (col == size - 1) { // last column except for last row
                        // left cross down
                        normal[normalIndex] = vertex[normalIndex - 1].subtract(
                                vertex[normalIndex]).cross(
                                vertex[normalIndex + size]
                                        .subtract(vertex[normalIndex]))
                                .normalize();
                    } else { // most cases
                        // down cross right
                        normal[normalIndex] = vertex[normalIndex + size]
                                .subtract(vertex[normalIndex]).cross(
                                        vertex[normalIndex + 1]
                                                .subtract(vertex[normalIndex]))
                                .normalizeLocal();
                    }
                }
                normalIndex++;
            }
        }

        setNormals(normal);
    }
}