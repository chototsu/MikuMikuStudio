/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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
import java.util.logging.Level;

import com.jme.intersection.CollisionResults;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
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
 * @version $Id: Line.java,v 1.16 2005-09-20 16:46:36 renanse Exp $
 */
public class Line extends Geometry {

	private static final long serialVersionUID = 1L;
	
	public static final int SEGMENTS = 0;
	public static final int CONNECTED = 1;
	public static final int LOOP = 2;

	private float lineWidth = 1.0f;
	private int mode = SEGMENTS;
	private short stipplePattern = (short)0xFFFF;
	private int stippleFactor = 1;
	private boolean antialiased = false;

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
		LoggingSystem.getLogger().log(Level.INFO, "Line created.");
	}

	/**
	 * <code>draw</code> calls super to set the render state then calls the
	 * renderer to display the collection of lines.
	 * 
	 * @param r
	 *            the renderer used to display the lines.
	 */
	public void draw(Renderer r) {
		if (!r.isProcessingQueue()) {
			if (r.checkAndAdd(this))
				return;
		}
		super.draw(r);
		r.draw(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
	 *      com.jme.intersection.CollisionResults)
	 */
	public void findCollisions(Spatial scene, CollisionResults results) {
		// TODO Auto-generated method stub

	}
	
	public boolean hasCollision(Spatial scene, boolean checkTriangles) {
		return false;
	}
	
    /**
     * @return true if lines are to be antialiased
     */
    public boolean isAntialiased() {
        return antialiased;
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
        this.antialiased = antialiased;
    }
    
    /**
     * @return either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode
     *            either SEGMENTS, CONNECTED or LOOP. See class description.
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * @return the width of this line.
     */
    public float getLineWidth() {
        return lineWidth;
    }

    /**
     * Sets the width of the line when drawn. Non anti-aliased line widths are
     * rounded to the nearest whole number by opengl.
     * 
     * @param lineWidth
     *            The lineWidth to set.
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    /**
     * @return the set stipplePattern. 0xFFFF means no stipple.
     */
    public short getStipplePattern() {
        return stipplePattern;
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
        this.stipplePattern = stipplePattern;
    }
    
    /**
     * @return the set stippleFactor.
     */
    public int getStippleFactor() {
        return stippleFactor;
    }

    /**
     * @param stippleFactor
     *            magnification factor to apply to the stipple pattern.
     */
    public void setStippleFactor(int stippleFactor) {
        this.stippleFactor = stippleFactor;
    }
}