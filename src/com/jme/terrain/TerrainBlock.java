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
import com.jme.renderer.ColorRGBA;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.FastMath;

/**
 * <code>TerrainBlock</code> defines the lowest level of the terrain system.
 * <code>TerrainBlock</code> is the actual part of the terrain system that
 * renders to the screen. The terrain is built from a heightmap defined by a one
 * dimenensional int array. The step scale is used to define the amount of units
 * each block line will extend. Clod can be used to allow for level of detail
 * control.
 *
 * By directly creating a <code>TerrainBlock</code> yourself, you can generate
 * a brute force terrain. This is many times sufficient for small terrains on
 * modern hardware. If terrain is to be large, it is recommended that you make
 * use of the <code>TerrainPage</code> class.
 *
 * @author Mark Powell
 * @version $Id: TerrainBlock.java,v 1.31 2004-09-05 16:18:28 cep21 Exp $
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

    // heightmap values used to create this block
    private int[] heightMap;

  /**
     * Empty Constructor to be used internally only.
     */
    public TerrainBlock() {}

    /**
     * For internal use only.  Creates a new Terrainblock with the given name by simply calling
     * super(name)
     * @param name The name.
     * @see com.jme.scene.lod.AreaClodMesh#AreaClodMesh(java.lang.String)
     */
    public TerrainBlock(String name){
        super(name);
    }

    /**
     * Constructor instantiates a new <code>TerrainBlock</code> object. The
     * parameters and heightmap data are then processed to generate a
     * <code>TriMesh</code> object for renderering.
     *
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the heightmap.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param origin
     *            the origin offset of the block.
     * @param clod
     *            true will use level of detail, false will not.
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
     *
     * @param name
     *            the name of the terrain block.
     * @param size
     *            the size of the block.
     * @param stepScale
     *            the scale for the axes.
     * @param heightMap
     *            the height data.
     * @param origin
     *            the origin offset of the block.
     * @param clod
     *            true will use level of detail, false will not.
     * @param totalSize
     *            the total size of the terrain. (Higher if the block is part of
     *            a <code>TerrainPage</code> tree.
     * @param offset
     *            the offset for texture coordinates.
     * @param offsetAmount
     *            the total offset amount.  Used for texture coordinates.
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
        this.heightMap = heightMap;

        setLocalTranslation(origin);

        buildVertices();
        buildTextureCoordinates();
        buildNormals();
        buildColors();

        setVBOVertexEnabled(true);
        setVBONormalEnabled(true);
        setVBOTextureEnabled(true);
        setVBOColorEnabled(true);

        if (useClod) {
            this.create(null);
            this.setTrisPerPixel(0.02f);
        }
    }

  /**
     * <code>chooseTargetRecord</code> determines which level of detail to
     * use. If CLOD is not used, the index 0 is always returned.
     *
     * @param r
     *            the renderer to use for determining the LOD record.
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
     *            int
     * @param repeat
     *            int
     */
    public void setDetailTexture(int unit, int repeat) {
        Vector2f[] texs = new Vector2f[texture[0].length];
        for (int i = 0; i < texture[0].length; i++) {
            texs[i] = texture[0][i].mult(repeat);
        }

        setTextures(texs, unit);
    }

    /**
     *
     * <code>getHeight</code> returns the height of an arbitrary point on the
     * terrain. If the point is between height point values, the height is
     * linearly interpolated. This provides smooth height calculations. If the
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     *
     * @param position
     *            the vector representing the height location to check.
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
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     *
     * @param position
     *            the vector representing the height location to check. Only the
     *            x and z values are used.
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
     * point provided is not within the bounds of the height map, the NaN float
     * value is returned (Float.NaN).
     *
     * @param x
     *            the x coordinate to check.
     * @param z
     *            the z coordinate to check.
     * @return the height at the provided location.
     */
    public float getHeight(float x, float z) {
        x /= stepScale.x;
        z /= stepScale.z;
        float col = FastMath.floor(x);
        float row = FastMath.floor(z);

        if (col < 0 || row < 0 || col >= size - 1 || row >= size - 1) { return Float.NaN; }
        float intOnX = x - col, intOnZ = z - row;

        float topLeft, topRight, bottomLeft, bottomRight;

        int focalSpot = (int) (col + row * size);

        // find the heightmap point closest to this position (but will always
        // be to the left ( < x) and above (< z) of the spot.
        topLeft = heightMap[focalSpot] * stepScale.y;

        // now find the next point to the right of topLeft's position...
        topRight = heightMap[focalSpot + 1] * stepScale.y;

        // now find the next point below topLeft's position...
        bottomLeft = heightMap[focalSpot + size] * stepScale.y;

        // now find the next point below and to the right of topLeft's
        // position...
        bottomRight = heightMap[focalSpot + size + 1] * stepScale.y;

        // Use linear interpolation to find the height.
        return FastMath.LERP(intOnZ, FastMath.LERP(intOnX, topLeft, topRight),
                FastMath.LERP(intOnX, bottomLeft, bottomRight));
    }

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     */
    private void buildVertices() {
        vertex = new Vector3f[heightMap.length];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                vertex[x + (y * size)] = new Vector3f(x * stepScale.x,
                        heightMap[x + (y * size)] * stepScale.y, y
                                * stepScale.z);
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

        for (int i = 0; i < vertex.length; i++) {
            texture[0][i] = new Vector2f(
                (vertex[i].x + offset.x) / (stepScale.x * (totalSize - 1)),
                (vertex[i].z + offset.y) / (stepScale.z * (totalSize - 1)));
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
        Vector3f oppositeVector = new Vector3f();
        int normalIndex = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
              normal[normalIndex] = new Vector3f();
                if (row == size - 1) {
                    if (col == size - 1) { // last row, last col
                        // up cross left
                        normal[normalIndex].set(vertex[normalIndex - size])
                                .subtractLocal(vertex[normalIndex])
                                .crossLocal(
                                    oppositeVector.set(vertex[normalIndex - 1])
                                    .subtractLocal(vertex[normalIndex]))
                                .normalizeLocal();
                    } else { // last row, except for last col
                        // right cross up
                        normal[normalIndex].set(vertex[normalIndex + 1])
                                .subtractLocal(vertex[normalIndex])
                                .crossLocal(
                                    oppositeVector.set(vertex[normalIndex - size])
                                    .subtractLocal(vertex[normalIndex]))
                                .normalizeLocal();
                    }
                } else {
                    if (col == size - 1) { // last column except for last row
                        // left cross down
                        normal[normalIndex].set(vertex[normalIndex - 1])
                                .subtractLocal(vertex[normalIndex])
                                .crossLocal(
                                    oppositeVector.set(vertex[normalIndex + size])
                                    .subtractLocal(vertex[normalIndex]))
                                .normalizeLocal();
                    } else { // most cases
                        // down cross right
                        normal[normalIndex].set(vertex[normalIndex + size])
                                .subtractLocal(vertex[normalIndex])
                                .crossLocal(
                                    oppositeVector.set(vertex[normalIndex + 1])
                                    .subtractLocal(vertex[normalIndex]))
                                .normalizeLocal();
                    }
                }
                normalIndex++;
            }
        }

        setNormals(normal);
    }

    /**
     * Sets the colors for each vertex to the color white.
     */
    private void buildColors()
    {
        color = new ColorRGBA[vertex.length];
        //initialize colors to white
        for (int x = 0; x < vertex.length; x++) {
            color[x] = new ColorRGBA();
        }

        setColors(color);
    }

    /**
     * Returns the height map this terrain block is using.
     * @return This terrain block's height map.
     */
    public int[] getHeightMap() {
        return heightMap;
    }

    /**
     * Returns the offset amount this terrain block uses for textures.
     * @return The current offset amount.
     */
    public int getOffsetAmount() {
        return offsetAmount;
    }

    /**
     * Returns the step scale that stretches the height map.
     * @return The current step scale.
     */
    public Vector3f getStepScale() {
        return stepScale;
    }

    /**
     * Returns the total size of the terrain.
     * @return The terrain's total size.
     */
    public int getTotalSize() {
        return totalSize;
    }

    /**
     * Returns the size of this terrain block.
     * @return The current block size.
     */
    public int getSize() {
        return size;
    }

    /**
     * If true, the terrain is created as a ClodMesh.  This is only usefull as a call after the
     *  default constructor.
     * @param useClod
     */
    public void setUseClod(boolean useClod) {
      this.useClod = useClod;
    }

    /**
     * Returns the current offset amount.  This is used when building texture coordinates.
     * @return The current offset amount.
     */
    public Vector2f getOffset() {
        return offset;
    }

    /**
     * Sets the value for the current offset amount to use when building texture coordinates.
     * Note that this does <b>NOT</b> rebuild the terrain at all.  This is mostly used for
     * outside constructors of terrain blocks.
     * @param offset The new texture offset.
     */
    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }

    /**
     * Returns true if this TerrainBlock was created as a clod.
     * @return True if this terrain block is a clod.  False otherwise.
     */
    public boolean isUseClod() {
      return useClod;
    }

    /**
     * Sets the size of this terrain block.  Note that this does <b>NOT</b> rebuild the terrain
     * at all.  This is mostly used for outside constructors of terrain blocks.
     * @param size The new size.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Sets the total size of the terrain .  Note that this does <b>NOT</b> rebuild the terrain
     * at all.  This is mostly used for outside constructors of terrain blocks.
     * @param totalSize The new total size.
     */
    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * Sets the step scale of this terrain block's height map.  Note that this does <b>NOT</b> rebuild
     * the terrain at all.  This is mostly used for outside constructors of terrain blocks.
     * @param stepScale The new step scale.
     */
    public void setStepScale(Vector3f stepScale) {
        this.stepScale = stepScale;
    }

    /**
     * Sets the offset of this terrain texture map.  Note that this does <b>NOT</b> rebuild
     * the terrain at all.  This is mostly used for outside constructors of terrain blocks.
     * @param offsetAmount The new texture offset.
     */
    public void setOffsetAmount(int offsetAmount) {
        this.offsetAmount = offsetAmount;
    }

    /**
     * Sets the terrain's height map.  Note that this does <b>NOT</b> rebuild
     * the terrain at all.  This is mostly used for outside constructors of terrain blocks.
     * @param heightMap The new height map.
     */
    public void setHeightMap(int[] heightMap) {
        this.heightMap = heightMap;
    }
}