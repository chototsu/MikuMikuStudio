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
 * <code>Point</code> defines a collection of vertices that are rendered as
 * single points. 
 * @author Mark Powell
 * @version $Id: Point.java,v 1.1.1.1 2003-10-29 10:56:35 Anakan Exp $
 */
public class Point extends Geometry {

    /**
     * Constructor instantiates a new <code>Point</code> object with a given
     * set of data. Any data may be null, except the vertex array. If this
     * is null an exception is thrown.
     * @param vertex the vertices or points.
     * @param normal the normals of the points.
     * @param color the color of the points.
     * @param texture the texture coordinates of the points.
     */
    public Point(
        Vector3f[] vertex,
        Vector3f[] normal,
        ColorRGBA[] color,
        Vector2f[] texture) {

        super(vertex, normal, color, texture);
        LoggingSystem.getLogger().log(Level.INFO,"Point created.");
    }

    /**
     * <code>draw</code> calls super to set the render state. After this
     * state is set, the points are sent to the renderer for display.
     * @param r the renderer used for displaying the data.
     */
    public void draw(Renderer r) {
        super.draw(r);
        r.draw(this);
    }
}
