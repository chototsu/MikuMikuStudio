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

package com.jme.terrain;

import com.jme.scene.TriMesh;
import com.jme.math.Vector3f;

/**
 *
 * @author  Mark Powell
 */
public class TerrainBlock extends TriMesh {
    private int size;
    private float stepScale;
    /** Creates a new instance of TerrainBlock */
    public TerrainBlock(String name, int size, float stepScale, float[] heightMap, Vector3f origin) {
        super(name);
        this.size = size;
        this.stepScale = stepScale;
        setLocalTranslation(origin);
        buildVertices(heightMap);
    }
    
    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the 
     *TriMesh. 
     *@param heightMap the raw data.
     */
    private void buildVertices(float[] heightMap) {
        vertex = new Vector3f[heightMap.length];
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                vertex[x + (y * size)] = new Vector3f(x*stepScale,heightMap[x + (y * size)],y*stepScale);
            }
        }
        
        //set up the indices
        int value = ((size - 1)*(size - 1)) * 6;
        indices = new int[value];
        
        int count = 0;
        
        //go through entire array up to the second to last column.
        for(int i = 0; i < (size * (size-1)); i++) {
            //we want to skip the top row.
            if(i % ((size*(i / size + 1)) - 1) == 0 && i != 0) {
                continue;
            }
            //set the bottom left corner.
            indices[count++] = i;
            //set the top right corner.
            indices[count++] = ((1 + size) + i);
            //set the top left corner.
            indices[count++] = (size + i);
            //set the bottom left corner
            indices[count++] = i;
            //set the bottom right corner
            indices[count++] = 1 + i;
            //set the upper right corner
            indices[count++] = ((1 + size) + i);
            
        }
        
        setVertices(vertex);
        setIndices(indices);
    }
    
}
