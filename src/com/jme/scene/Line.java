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
package com.jme.scene;

import java.util.logging.Level;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.util.LoggingSystem;

/**
 * <code>Line</code> subclasses geometry and defines a collection of lines.
 * For each two points, a line is created. The last point of the previous line
 * is the first point of the next line. Therefore, for N points there are
 * N-1 lines.
 * @author Mark Powell
 * @version $Id: Line.java,v 1.6 2004-03-13 03:07:38 renanse Exp $
 */
public class Line extends Geometry {

    public Line(String name) {
        super(name);
    }

    /**
     * Constructor instantiates a new <code>Line</code> object with a given
     * set of data. Any data can be null except for the vertex list. If
     * vertices are null an exception will be thrown.
     * @param name the name of the scene element. This is required for identification and
     * 		comparision purposes.
     * @param vertex the vertices that make up the lines.
     * @param normal the normals of the lines.
     * @param color the color of each point of the lines.
     * @param texture the texture coordinates of the lines.
     */
    public Line(
    	String name,
        Vector3f[] vertex,
        Vector3f[] normal,
        ColorRGBA[] color,
        Vector2f[] texture) {
        super(name, vertex, normal, color, texture);
        LoggingSystem.getLogger().log(Level.INFO, "Line created.");
    }

    /**
     * <code>draw</code> calls super to set the render state then calls the
     * renderer to display the collection of lines.
     * @param r the renderer used to display the lines.
     */
    public void draw(Renderer r) {
        super.draw(r);
        r.draw(this);
    }

    /**
     * <code>drawBounds</code> calls super to set the render state then passes itself
     * to the renderer.
     * @param r the renderer to display
     */
    public void drawBounds(Renderer r) {
        r.drawBounds(this);
    }
}
