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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.system.JmeException;
import com.jme.scene.Spatial;

/**
 * <code>TerrainPage</code> is used to build a quad tree of terrain blocks.
 * The <code>TerrainPage</code> will have four children, either four pages or
 * four blocks. The size of the page must be (2^N + 1), to allow for even
 * splitting of the blocks. Organization of the page into a quad tree allows for
 * very fast culling of the terrain. In some instances, using Clod will also
 * improve rendering speeds. The total size of the heightmap is provided, as
 * well as the desired end size for a block. Appropriate values for the end
 * block size is completely dependant on the application. In some cases, a large
 * size will give performance gains, in others, a small size is the best option.
 * It is recommended that different combinations are tried.
 *
 * @author Mark Powell
 * @version $Id: TerrainPage.java,v 1.16 2004-05-07 22:03:27 renanse Exp $
 */
public class TerrainPage extends Node {

    private Vector2f offset;

    private int totalSize;

    private int size;

    private Vector3f stepScale;

    private int offsetAmount;

    /**
     * Empty Constructor to be used internally only.
     */
    public TerrainPage() {}

    /**
     * Constructor instantiates a new <code>TerrainPage</code> object. The
     * data is then split into either 4 new <code>TerrainPages</code> or 4 new
     * <code>TerrainBlock</code>.
     *
     * @param name
     *            the name of the page.
     * @param blockSize
     *            the size of the leaf nodes. This is used to determine if four
     *            new <code>TerrainPage</code> objects should be the child or
     *            four new <code>TerrainBlock</code> objects.
     * @param size
     *            the size of the heightmap for this page.
     * @param stepScale
     *            the scale of the axes.
     * @param heightMap
     *            the height data.
     * @param clod
     *            true will use level of detail, false will not.
     */
    public TerrainPage(String name, int blockSize, int size,
            Vector3f stepScale, int[] heightMap, boolean clod) {
        this(name, blockSize, size, stepScale, heightMap, clod, size,
                new Vector2f(), 0);
    }

    /**
     * Constructor instantiates a new <code>TerrainPage</code> object. The
     * data is then split into either 4 new <code>TerrainPages</code> or 4 new
     * <code>TerrainBlock</code>.
     *
     * @param name
     *            the name of the page.
     * @param blockSize
     *            the size of the leaf nodes. This is used to determine if four
     *            new <code>TerrainPage</code> objects should be the child or
     *            four new <code>TerrainBlock</code> objects.
     * @param size
     *            the size of the heightmap for this page.
     * @param stepScale
     *            the scale of the axes.
     * @param heightMap
     *            the height data.
     * @param clod
     *            true will use level of detail, false will not.
     * @param totalSize
     *            the total terrain size, used if the page is an internal node
     *            of a terrain system.
     * @param offset
     *            the texture offset for the page.
     * @param offsetAmount
     *            the amount of the offset.
     */
    protected TerrainPage(String name, int blockSize, int size,
            Vector3f stepScale, int[] heightMap, boolean clod, int totalSize,
            Vector2f offset, int offsetAmount) {
        super(name);
        if (!FastMath.isPowerOfTwo(size - 1)) { throw new JmeException(
                "size given: "+size+"  Terrain page sizes may only be (2^N + 1)"); }

        this.offset = offset;
        this.offsetAmount = offsetAmount;
        this.totalSize = totalSize;
        this.size = size;
        this.stepScale = stepScale;
        split(size, blockSize, stepScale, heightMap, clod);
    }

    /**
     *
     * <code>setDetailTexture</code> sets the detail texture coordinates to be
     * applied on top of the normal terrain texture.
     *
     * @param unit
     *            the texture unit to set the coordinates.
     * @param repeat
     *            the number of tiling for the texture.
     */
    public void setDetailTexture(int unit, int repeat) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).setDetailTexture(unit, repeat);
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).setDetailTexture(unit, repeat);

            }
        }
    }

    /**
     *
     * <code>setModelBound</code> sets the model bounds for the terrain
     * blocks.
     *
     * @param v
     *            the bounding volume to set for the terrain blocks.
     */
    public void setModelBound(BoundingVolume v) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).setModelBound((BoundingVolume) v
                        .clone(null));
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).setModelBound((BoundingVolume) v
                        .clone(null));

            }
        }
    }

    /**
     *
     * <code>updateModelBound</code> updates the model bounds (generates the
     * bounds from the current vertices).
     *
     *
     */
    public void updateModelBound() {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainPage) {
                ((TerrainPage) getChild(i)).updateModelBound();
            } else if (this.getChild(i) instanceof TerrainBlock) {
                ((TerrainBlock) getChild(i)).updateModelBound();

            }
        }
    }

    /**
     *
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN
     * float value is returned (Float.NaN).
     *
     * @param position the vector representing the height location to check.
     * @return the height at the provided location.
     */
    public float getHeight(Vector2f position) {
        return getHeight(position.x, position.y);
    }

    /**
     *
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN
     * float value is returned (Float.NaN).
     *
     * @param position the vector representing the height location to check.
     * 		Only the x and z values are used.
     * @return the height at the provided location.
     */
    public float getHeight(Vector3f position) {
        return getHeight(position.x, position.z);
    }

    /**
     *
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN
     * float value is returned (Float.NaN).
     *
     * @param x the x coordinate to check.
     * @param y the y coordinate to check.
     * @return the height at the provided location.
     */
    public float getHeight(float x, float z) {
        //determine which quadrant this is in.
        Spatial child = null;
        int split = (size - 1) >> 1;
        float halfmapx = split * stepScale.x, halfmapz = split * stepScale.z;
        float newX = 0, newZ = 0;
        if (x > 0) {
            if (z > 0) {
                // upper right
                child = getChild(3);
                newX = x;
                newZ = z;
            } else {
                // lower right
                child = getChild(2);
                newX = x;
                newZ = z + halfmapz;
            }
        } else {
            if (z > 0) {
                // upper left
                child = getChild(1);
                newX = x + halfmapx;
                newZ = z;
            } else {
                // lower left...
                child = getChild(0);
                newX = x + halfmapx;
                newZ = z + halfmapz;
            }
        }
        if (child instanceof TerrainBlock)
            return ((TerrainBlock) child).getHeight(newX, newZ);
        else if (child instanceof TerrainPage)
                return ((TerrainPage) child).getHeight(x
                        - ((TerrainPage) child).getLocalTranslation().x, z
                        - ((TerrainPage) child).getLocalTranslation().z);
        return Float.NaN;
    }

    /**
     * <code>split</code> divides the heightmap data for four children. The
     * children are either pages or blocks. This is dependent on the size of the
     * children. If the child's size is less than or equal to the set block
     * size, then blocks are created, otherwise, pages are created.
     *
     * @param blockSize
     *            the blocks size to test against.
     * @param size
     *            the size of this page.
     * @param stepScale
     *            the scale of the x/z axes.
     * @param heightMap
     *            the height data.
     * @param clod
     *            true if level of detail is used, false otherwise.
     */
    private void split(int size, int blockSize, Vector3f stepScale,
            int[] heightMap, boolean clod) {
        if (size >> 1 + 1 <= blockSize) {
            createQuadBlock(size, stepScale, heightMap, clod);
        } else {
            createQuadPage(size, blockSize, stepScale, heightMap, clod);
        }

    }

    /**
     * <code>createQuadPage</code> generates four new pages from this page.
     *
     *
     */
    private void createQuadPage(int size, int blockSize, Vector3f stepScale,
            int[] heightMap, boolean clod) {
        System.err.println("CREATING PAGES!");
        //      create 4 terrain pages
        Vector2f tempOffset = new Vector2f();
        int quarterSize = size >> 2;
        offsetAmount += quarterSize;

        int split = (size + 1) / 2;
        int newBlockSize = split * split;

        //1 upper left
        int[] heightBlock1 = new int[newBlockSize];
        int count = 0;
        for (int i = 0; i < split; i++) {
            for (int j = 0; j < split; j++) {
                heightBlock1[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin1 = new Vector3f(-quarterSize * stepScale.x, 0,
                -quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x;
        tempOffset.y += origin1.z;

        TerrainPage page1 = new TerrainPage(name + "Page1", blockSize, split,
                stepScale, heightBlock1, clod, totalSize, tempOffset,
                offsetAmount);
        page1.setLocalTranslation(origin1);
        this.attachChild(page1);

        //2 lower left
        int[] heightBlock2 = new int[newBlockSize];
        count = 0;
        for (int i = split - 1; i < size; i++) {
            for (int j = 0; j < split; j++) {
                heightBlock2[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin2 = new Vector3f(-quarterSize * stepScale.x, 0,
                quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin2.x;
        tempOffset.y += origin2.z;

        TerrainPage page2 = new TerrainPage(name + "Page2", blockSize, split,
                stepScale, heightBlock2, clod, totalSize, tempOffset,
                offsetAmount);
        page2.setLocalTranslation(origin2);
        this.attachChild(page2);

        //3 lower right
        int[] heightBlock3 = new int[newBlockSize];
        count = 0;
        for (int i = 0; i < split; i++) {
            for (int j = split - 1; j < size; j++) {
                heightBlock3[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin3 = new Vector3f(quarterSize * stepScale.x, 0,
                -quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin3.x;
        tempOffset.y += origin3.z;

        TerrainPage page3 = new TerrainPage(name + "Page3", blockSize, split,
                stepScale, heightBlock3, clod, totalSize, tempOffset,
                offsetAmount);
        page3.setLocalTranslation(origin3);
        this.attachChild(page3);
        ////
        //4 upper right
        int[] heightBlock4 = new int[newBlockSize];
        count = 0;
        for (int i = split - 1; i < size; i++) {
            for (int j = split - 1; j < size; j++) {
                heightBlock4[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin4 = new Vector3f(quarterSize * stepScale.x, 0,
                quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin4.x;
        tempOffset.y += origin4.z;

        TerrainPage page4 = new TerrainPage(name + "Page4", blockSize, split,
                stepScale, heightBlock4, clod, totalSize, tempOffset,
                offsetAmount);
        page4.setLocalTranslation(origin4);
        this.attachChild(page4);

    }

    /**
     * <code>createQuadBlock</code> creates four child blocks from this page.
     *
     *
     */
    private void createQuadBlock(int size, Vector3f stepScale, int[] heightMap,
            boolean clod) {
        int quarterSize = size >> 2;
        int halfSize = size >> 1;

        Vector2f tempOffset = new Vector2f();
        offsetAmount += quarterSize;
        //create 4 terrain blocks
        int split = (size + 1) >> 1;
        int newBlockSize = split * split;
        int[] heightBlock1 = new int[newBlockSize];
        int count = 0;
        for (int i = 0; i < split; i++) {
            for (int j = 0; j < split; j++) {
                heightBlock1[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin1 = new Vector3f(-halfSize * stepScale.x, 0, -halfSize
                * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x / 2;
        tempOffset.y += origin1.z / 2;

        TerrainBlock block1 = new TerrainBlock(name + "Block1", split,
                stepScale, heightBlock1, origin1, clod, totalSize, tempOffset,
                offsetAmount);
        this.attachChild(block1);
        block1.setModelBound(new BoundingBox());
        block1.updateModelBound();

        //2 lower left
        int[] heightBlock2 = new int[newBlockSize];
        count = 0;
        for (int i = split - 1; i < size; i++) {
            for (int j = 0; j < split; j++) {
                heightBlock2[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin2 = new Vector3f(-halfSize * stepScale.x, 0, 0);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x / 2;
        tempOffset.y += quarterSize * stepScale.z;

        TerrainBlock block2 = new TerrainBlock(name + "Block2", split,
                stepScale, heightBlock2, origin2, clod, totalSize, tempOffset,
                offsetAmount);
        this.attachChild(block2);
        block2.setModelBound(new BoundingBox());
        block2.updateModelBound();

        //3 lower right
        int[] heightBlock3 = new int[newBlockSize];
        count = 0;
        for (int i = 0; i < split; i++) {
            for (int j = split - 1; j < size; j++) {
                heightBlock3[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin3 = new Vector3f(0, 0, -halfSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += quarterSize * stepScale.x;
        tempOffset.y += origin3.z / 2;

        TerrainBlock block3 = new TerrainBlock(name + "Block3", split,
                stepScale, heightBlock3, origin3, clod, totalSize, tempOffset,
                offsetAmount);
        this.attachChild(block3);
        block3.setModelBound(new BoundingBox());
        block3.updateModelBound();

        //4 upper right
        int[] heightBlock4 = new int[newBlockSize];
        count = 0;
        for (int i = split - 1; i < size; i++) {
            for (int j = split - 1; j < size; j++) {
                heightBlock4[count++] = heightMap[j + (i * size)];
            }
        }
        Vector3f origin4 = new Vector3f(0, 0, 0);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += quarterSize * stepScale.x;
        tempOffset.y += quarterSize * stepScale.z;

        TerrainBlock block4 = new TerrainBlock(name + "Block4", split,
                stepScale, heightBlock4, origin4, clod, totalSize, tempOffset,
                offsetAmount);
        this.attachChild(block4);
        block4.setModelBound(new BoundingBox());
        block4.updateModelBound();

    }

}
