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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;


/**
 * <code>TerrainPage</code>
 * @author Mark Powell
 * @version $id$
 */
public class TerrainPage extends Node {
    
    private Vector2f offset;
    private int totalSize;
    
    public TerrainPage(String name, int blockSize, int size, float stepScale,
            int[] heightMap, boolean clod) {
        this(name, blockSize, size, stepScale, heightMap, clod, size, new Vector2f());
    }
    
    public TerrainPage(String name, int blockSize, int size, float stepScale,
            int[] heightMap, boolean clod, int totalSize, Vector2f offset) {
        super(name);
        this.offset = offset;
        this.totalSize = totalSize;
        split(size, blockSize, stepScale, heightMap, clod);
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
    
    //XXX this is just sending a copy of the same bounding volume. Each child
    //needs it's own. Fix.
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
    private void split(int size, int blockSize, float stepScale, int[] heightMap, boolean clod) {
        if(size/2 + 1 <= blockSize) {
            createQuadBlock(size, stepScale, heightMap, clod);
        } else {
            createQuadPage(size, blockSize, stepScale, heightMap, clod);
        }
        
    }

    /**
     * <code>createQuadPage</code> 
     *
     * 
     */
    private void createQuadPage(int size, int blockSize, float stepScale, int[] heightMap, boolean clod) {
//      create 4 terrain pages
        Vector2f tempOffset = new Vector2f();
        
        int split = (size + 1) / 2;
        int newBlockSize = split * split;
        
        //1 upper left
        int[] heightBlock1 = new int[newBlockSize];
        int count = 0;
        for(int i = 0; i < split; i++) {
            for(int j = 0; j < split; j++) {
                heightBlock1[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin1 = new Vector3f(-size/4 * stepScale, 0, -size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x;
        tempOffset.y += origin1.z;
        
        TerrainPage page1 = new TerrainPage(name+"Page1", blockSize, split, stepScale, heightBlock1, clod, totalSize, tempOffset);
        page1.setLocalTranslation(origin1);
        this.attachChild(page1);
        
        //2 lower left
        int[] heightBlock2 = new int[newBlockSize];
        count = 0;
        for(int i = split-1; i < size; i++) {
            for(int j = 0; j < split; j++) {
                heightBlock2[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin2 = new Vector3f(-size/4  * stepScale, 0, size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin2.x;
        tempOffset.y += origin2.z;
        
        TerrainPage page2 = new TerrainPage(name+"Page2", blockSize, split, stepScale, heightBlock2, clod, totalSize, tempOffset);
        page2.setLocalTranslation(origin2);
        this.attachChild(page2);
        
        //3 lower right
        int[] heightBlock3 = new int[newBlockSize];
        count = 0;
        for(int i = 0; i < split; i++) {
            for(int j = split-1; j < size; j++) {
                heightBlock3[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin3 = new Vector3f(size/4  * stepScale, 0, -size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin3.x;
        tempOffset.y += origin3.z;
        
        TerrainPage page3 = new TerrainPage(name+"Page3", blockSize, split, stepScale, heightBlock3, clod, totalSize, tempOffset);
        page3.setLocalTranslation(origin3);
        this.attachChild(page3);
////        
        //4 upper right
        int[] heightBlock4 = new int[newBlockSize];
        count = 0;
        for(int i = split-1; i < size; i++) {
            for(int j = split-1; j < size; j++) {
                heightBlock4[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin4 = new Vector3f(size/4  * stepScale, 0, size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin4.x;
        tempOffset.y += origin4.z;
        
        TerrainPage page4 = new TerrainPage(name+"Page4", blockSize, split, stepScale, heightBlock4, clod, totalSize, tempOffset);
        page4.setLocalTranslation(origin4);
        this.attachChild(page4);
        
    }

    /**
     * <code>createQuadBlock</code> 
     *
     * 
     */
    private void createQuadBlock(int size, float stepScale, int[] heightMap, boolean clod) {
        Vector2f tempOffset = new Vector2f();
        //create 4 terrain blocks
        int split = (size + 1) / 2;
        int newBlockSize = split * split;
        int[] heightBlock1 = new int[newBlockSize];
        int count = 0;
        for(int i = 0; i < split; i++) {
            for(int j = 0; j < split; j++) {
                heightBlock1[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin1 = new Vector3f(-size/4 * stepScale, 0, -size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x;
        tempOffset.y += origin1.z;
        
        TerrainBlock block1 = new TerrainBlock(name+"Block1", split, stepScale, heightBlock1, origin1, clod, totalSize, tempOffset);
        this.attachChild(block1);
        block1.setModelBound(new BoundingBox());
        block1.updateModelBound();
        
        //2 lower left
        int[] heightBlock2 = new int[newBlockSize];
        count = 0;
        for(int i = split - 1; i < size; i++) {
            for(int j = 0; j < split; j++) {
                heightBlock2[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin2 = new Vector3f(-size/4  * stepScale, 0, size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin2.x;
        tempOffset.y += origin2.z;
        
        TerrainBlock block2 = new TerrainBlock(name+"Block2", split, stepScale, heightBlock2, origin2, clod, totalSize, tempOffset);
        this.attachChild(block2);
        block2.setModelBound(new BoundingBox());
        block2.updateModelBound();
        
        //3 lower right
        int[] heightBlock3 = new int[newBlockSize];
        count = 0;
        for(int i = 0; i < split; i++) {
            for(int j = split - 1; j < size; j++) {
                heightBlock3[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin3 = new Vector3f(size/4  * stepScale, 0, -size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin3.x;
        tempOffset.y += origin3.z;
        
        TerrainBlock block3 = new TerrainBlock(name+"Block3", split, stepScale, heightBlock3, origin3, clod, totalSize, tempOffset);
        this.attachChild(block3);
        block3.setModelBound(new BoundingBox());
        block3.updateModelBound();
        
        //4 upper right
        int[] heightBlock4 = new int[newBlockSize];
        count = 0;
        for(int i = split - 1; i < size; i++) {
            for(int j = split - 1; j < size; j++) {
                heightBlock4[count++] = heightMap[j + (i*size)];
            }
        }
        Vector3f origin4 = new Vector3f(size/4  * stepScale, 0, size/4  * stepScale);
        
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin4.x;
        tempOffset.y += origin4.z;
        
        TerrainBlock block4 = new TerrainBlock(name+"Block4", split, stepScale, heightBlock4, origin4, clod, totalSize, tempOffset);
        this.attachChild(block4);
        block4.setModelBound(new BoundingBox());
        block4.updateModelBound();
    
    }
    
    
}
