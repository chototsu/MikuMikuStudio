/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme.scene;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.batch.LineBatch;
import com.jme.util.LoggingSystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Line</code> subclasses geometry and defines a collection of lines.
 * For every two points, a line is created. If mode is set to CONNECTED, these
 * lines as connected as one big line.  If it is set to LOOP, it is also rendered
 * connected but the last point is connected to the first point.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @version $Id: Line.java,v 1.22 2006-06-23 22:31:55 nca Exp $
 */
public class Line extends Geometry {

	private static final long serialVersionUID = 1L;
	
	public static final int SEGMENTS = 0;
	public static final int CONNECTED = 1;
	public static final int LOOP = 2;

    public Line() {
        
    }
    
    /**
	 * Constructs a new line with the given name. By default, the line has no
	 * information.
	 * 
	 * @param name
	 *            The name of the line.
	 */
	public Line(String name) {
		super(name);
	}

	/**
	 * Constructor instantiates a new <code>Line</code> object with a given
	 * set of data. Any data can be null except for the vertex list. If vertices
	 * are null an exception will be thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertex
	 *            the vertices that make up the lines.
	 * @param normal
	 *            the normals of the lines.
	 * @param color
	 *            the color of each point of the lines.
	 * @param texture
	 *            the texture coordinates of the lines.
	 */
	public Line(String name, FloatBuffer vertex, FloatBuffer normal,
			FloatBuffer color, FloatBuffer texture) {
		super(name, vertex, normal, color, texture);
        generateIndices(0);
		LoggingSystem.getLogger().log(Level.INFO, "Line created.");
	}

	/**
	 * Constructor instantiates a new <code>Line</code> object with a given
	 * set of data. Any data can be null except for the vertex list. If vertices
	 * are null an exception will be thrown.
	 * 
	 * @param name
	 *            the name of the scene element. This is required for
	 *            identification and comparision purposes.
	 * @param vertex
	 *            the vertices that make up the lines.
	 * @param normal
	 *            the normals of the lines.
	 * @param color
	 *            the color of each point of the lines.
	 * @param texture
	 *            the texture coordinates of the lines.
	 */
	public Line(String name, Vector3f[] vertex, Vector3f[] normal,
			ColorRGBA[] color, Vector2f[] texture) {
		super(name, 
		        BufferUtils.createFloatBuffer(vertex), 
		        BufferUtils.createFloatBuffer(normal), 
		        BufferUtils.createFloatBuffer(color), 
		        BufferUtils.createFloatBuffer(texture));
        generateIndices(0);
		LoggingSystem.getLogger().log(Level.INFO, "Line created.");
	}

    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, FloatBuffer textureCoords) {
        super.reconstruct(vertices, normals, colors, textureCoords);
        generateIndices(0);
    }
    
    @Override
    public void reconstruct(FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, FloatBuffer textureCoords, int batchIndex) {
        super.reconstruct(vertices, normals, colors, textureCoords, batchIndex);
        generateIndices(batchIndex);
    }
    
    protected void setupBatchList() {
        batchList = new ArrayList<GeomBatch>(1);
        LineBatch batch = new LineBatch();
        batch.setParentGeom(this);
        batchList.add(batch);
    }

    public LineBatch getBatch(int index) {
        return (LineBatch) batchList.get(index);
    }
    
    public void generateIndices(int batchIndex) {
        LineBatch batch = getBatch(batchIndex);
        if (batch.getIndexBuffer() == null || batch.getIndexBuffer().capacity() != batch.getVertexCount()) {
            batch.setIndexBuffer(BufferUtils.createIntBuffer(batch.getVertexCount()));
        } else
            batch.getIndexBuffer().rewind();

        for (int x = 0; x < batch.getVertexCount(); x++)
            batch.getIndexBuffer().put(x);
    }

    /**
     * 
     * <code>getIndexBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer() {
        return getBatch(0).getIndexBuffer();
    }

    /**
     * 
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>Line</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices) {
        getBatch(0).setIndexBuffer(indices);
    }

    /**
     * @return true if lines are to be antialiased
     */
    public boolean isAntialiased() {
        return getBatch(0).isAntialiased();
    }
    
    /**
     * Sets whether the line should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SB_SRC_ALPHA and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antiAliased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased) {
        getBatch(0).setAntialiased(antialiased);
    }
    
    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public int getMode() {
        return getBatch(0).getMode();
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(int mode) {
        getBatch(0).setMode(mode);
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return getBatch(0).getLineWidth();
    }

    /**
     * Sets the width of the line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        getBatch(0).setLineWidth(lineWidth);
    }
    
    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return getBatch(0).getStipplePattern();
    }

    /**
     * The stipple or pattern to use when drawing this line. 0xFFFF is a solid
     * line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern) {
        getBatch(0).setStipplePattern(stipplePattern);
    }
    
    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return getBatch(0).getStippleFactor();
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        getBatch(0).setStippleFactor(stippleFactor);
    }

    /**
     * 
     * <code>getIndexAsBuffer</code> retrieves the indices array as an
     * <code>IntBuffer</code>.
     * 
     * @return the indices array as an <code>IntBuffer</code>.
     */
    public IntBuffer getIndexBuffer(int batchIndex) {
        return getBatch(batchIndex).getIndexBuffer();
    }

    /**
     * 
     * <code>setIndexBuffer</code> sets the index array for this
     * <code>Line</code>.
     * 
     * @param indices
     *            the index array as an IntBuffer.
     */
    public void setIndexBuffer(IntBuffer indices, int batchIndex) {
        getBatch(batchIndex).setIndexBuffer(indices);
    }

    /**
     * @return true if lines are to be antialiased
     */
    public boolean isAntialiased(int batchIndex) {
        return getBatch(batchIndex).isAntialiased();
    }
    
    /**
     * Sets whether the line should be antialiased. May decrease performance. If
     * you want to enabled antialiasing, you should also use an alphastate with
     * a source of SB_SRC_ALPHA and a destination of DB_ONE_MINUS_SRC_ALPHA or
     * DB_ONE.
     * 
     * @param antiAliased
     *            true if the line should be antialiased.
     */
    public void setAntialiased(boolean antialiased, int batchIndex) {
        getBatch(batchIndex).setAntialiased(antialiased);
    }
    
    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public int getMode(int batchIndex) {
        return getBatch(batchIndex).getMode();
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(int mode, int batchIndex) {
        getBatch(batchIndex).setMode(mode);
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth(int batchIndex) {
        return getBatch(batchIndex).getLineWidth();
    }

    /**
     * Sets the width of the line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth, int batchIndex) {
        getBatch(batchIndex).setLineWidth(lineWidth);
    }
    
    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern(int batchIndex) {
        return getBatch(batchIndex).getStipplePattern();
    }

    /**
     * The stipple or pattern to use when drawing this line. 0xFFFF is a solid
     * line.
     * 
     * @param stipplePattern
     *            a 16bit short whose bits describe the pattern to use when
     *            drawing this line
     */
    public void setStipplePattern(short stipplePattern, int batchIndex) {
        getBatch(batchIndex).setStipplePattern(stipplePattern);
    }
    
    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor(int batchIndex) {
        return getBatch(batchIndex).getStippleFactor();
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor, int batchIndex) {
        getBatch(batchIndex).setStippleFactor(stippleFactor);
    }

    /**
     * <code>draw</code> calls the onDraw method for each batch maintained by
     * this Line.
     * 
     * @param r
     *            the renderer to display
     */
    public void draw(Renderer r) {
        LineBatch batch;
        if (getBatchCount() == 1) {
            batch = getBatch(0);
            if (batch != null && batch.isEnabled()) {
                batch.setLastFrustumIntersection(frustrumIntersects);
                batch.draw(r);
                return;
            }
        }

        for (int i = 0, cSize = getBatchCount(); i < cSize; i++) {
            batch = getBatch(i);
            if (batch != null && batch.isEnabled())
                batch.onDraw(r);
        }
    }

    /*
     * unsupported
     * 
     * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
     *      com.jme.intersection.CollisionResults)
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        ; // unsupported
    }
    
    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        return false;
    }
}