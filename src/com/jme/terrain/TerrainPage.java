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

import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.scene.Node;


/**
 * <code>TerrainPage</code>
 * @author Mark Powell
 * @version $id$
 */
public class TerrainPage extends Node {
    
    public TerrainPage(String name, int blockSize, int size, float stepScale,
            int[] heightMap, boolean clod) {
        super(name);
        
        split(blockSize, size, stepScale, heightMap, clod);
    }
    
    public void setDetailTexture(int unit, int repeat) {
        for(int i = 0; i < this.getQuantity(); i++) {
            if(this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage)getChild(i)).setDetailTexture(unit, repeat);
            } else if( this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock)getChild(i)).setDetailTexture(unit, repeat);
                    
            }
        }
    }
    
    public void setModelBound(BoundingVolume v) {
        for(int i = 0; i < this.getQuantity(); i++) {
            if(this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage)getChild(i)).setModelBound(v);
            } else if( this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock)getChild(i)).setModelBound(v);
                    
            }
        }
    }
    
    public void updateModelBound() {
        for(int i = 0; i < this.getQuantity(); i++) {
            if(this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage)getChild(i)).updateModelBound();
            } else if( this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock)getChild(i)).updateModelBound();
                    
            }
        }
    }

    /**
     * <code>split</code> 
     *
     * @param blockSize
     * @param size
     * @param stepScale
     * @param heightMap
     * @param clod
     */
    private void split(int blockSize, int size, float stepScale, int[] heightMap, boolean clod) {
        System.out.println(size + " " +  size / 4 + " " + blockSize);
        //if(size/2 <= blockSize) {
            createQuadBlock(size, stepScale, heightMap, clod);
        //} else {
        //    createQuadPage(blockSize, size, stepScale, heightMap, clod);
        //}
        
    }

    /**
     * <code>createQuadPage</code> 
     *
     * 
     */
    private void createQuadPage(int blockSize, int size, float stepScale, int[] heightMap, boolean clod) {
//      create 4 terrain pages
        
        //1 upper left
        int[] heightBlock1 = new int[heightMap.length/4];
        int count = 0;
        for(int i = 0; i < size/2; i++) {
            for(int j = 0; j < size/2; j++) {
                heightBlock1[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin1 = new Vector3f(-size/4 * stepScale, 0, size/4  * stepScale);
        TerrainPage page1 = new TerrainPage(name+"Page1", blockSize, size/2, stepScale, heightBlock1, clod);
        page1.setLocalTranslation(origin1);
        this.attachChild(page1);
        
        //2 lower left
        int[] heightBlock2 = new int[heightMap.length/4];
        count = 0;
        for(int i = size/2; i < size; i++) {
            for(int j = 0; j < size/2; j++) {
                heightBlock2[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin2 = new Vector3f(-size/4  * stepScale, 0, -size/4  * stepScale);
        TerrainPage page2 = new TerrainPage(name+"Page2", blockSize, size/2, stepScale, heightBlock2, clod);
        page2.setLocalTranslation(origin2);
        this.attachChild(page2);
        
        //3 lower right
        int[] heightBlock3 = new int[heightMap.length/4];
        count = 0;
        for(int i = 0; i < size/2; i++) {
            for(int j = size/2; j < size; j++) {
                heightBlock3[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin3 = new Vector3f(size/4  * stepScale, 0, size/4  * stepScale);
        TerrainPage page3 = new TerrainPage(name+"Page3", blockSize, size/2, stepScale, heightBlock3, clod);
        page3.setLocalTranslation(origin3);
        this.attachChild(page3);
        
        //4 upper right
        int[] heightBlock4 = new int[heightMap.length/4];
        count = 0;
        for(int i = size/2; i < size; i++) {
            for(int j = size/2; j < size; j++) {
                heightBlock4[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin4 = new Vector3f(size/4  * stepScale, 0, -size/4  * stepScale);
        TerrainPage page4 = new TerrainPage(name+"Page4", blockSize, size/2, stepScale, heightBlock4, clod);
        page4.setLocalTranslation(origin4);
        this.attachChild(page4);
        
    }

    /**
     * <code>createQuadBlock</code> 
     *
     * 
     */
    private void createQuadBlock(int size, float stepScale, int[] heightMap, boolean clod) {
        //create 4 terrain blocks
        
        //1 upper left
        int[] heightBlock1 = new int[heightMap.length/4];
        int count = 0;
        for(int i = 0; i < size/2; i++) {
            for(int j = 0; j < size/2; j++) {
                heightBlock1[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin1 = new Vector3f(-size/4 * stepScale + 1, 0, size/4  * stepScale - 1);
        TerrainBlock block1 = new TerrainBlock(name+"Block1", size/2, stepScale, heightBlock1, origin1, clod);
        this.attachChild(block1);
        
        //2 lower left
        int[] heightBlock2 = new int[heightMap.length/4];
        count = 0;
        for(int i = size/2; i < size; i++) {
            for(int j = 0; j < size/2; j++) {
                heightBlock2[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin2 = new Vector3f(-size/4  * stepScale + 1, 0, -size/4  * stepScale - 1);
        TerrainBlock block2 = new TerrainBlock(name+"Block2", size/2, stepScale, heightBlock2, origin2, clod);
        this.attachChild(block2);
        
        //3 lower right
        int[] heightBlock3 = new int[heightMap.length/4];
        count = 0;
        for(int i = 0; i < size/2; i++) {
            for(int j = size/2; j < size; j++) {
                heightBlock3[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin3 = new Vector3f(size/4  * stepScale - 1, 0, size/4  * stepScale - 1);
        TerrainBlock block3 = new TerrainBlock(name+"Block3", size/2, stepScale, heightBlock3, origin3, clod);
        this.attachChild(block3);
        
        //4 upper right
        int[] heightBlock4 = new int[heightMap.length/4];
        count = 0;
        for(int i = size/2; i < size; i++) {
            for(int j = size/2; j < size; j++) {
                heightBlock4[count++] = heightMap[i + (j*size)];
            }
        }
        Vector3f origin4 = new Vector3f(size/4  * stepScale - 1, 0, -size/4  * stepScale + 1);
        TerrainBlock block4 = new TerrainBlock(name+"Block4", size/2, stepScale, heightBlock4, origin4, clod);
        this.attachChild(block4);
    
    }
    
    
}
