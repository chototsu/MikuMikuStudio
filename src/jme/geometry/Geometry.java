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

package jme.geometry;

import jme.geometry.bounding.BoundingBox;
import jme.geometry.bounding.BoundingSphere;

/**
 * <code>Geometry</code> defines an interface to maintain and render a 
 * three dimensional object.
 * 
 * It is intended that all graphical "entities" be derived from 
 * <code>Geometry</code> 
 * 
 * @author Mark Powell
 * @version 0.1.0
 */
public interface Geometry {

    /**
     * <code>initialize</code> sets up any required attributes of the 
     * geometry object. Typically, this includes retrieving the valid
     * GL and GLU objects.
     */
    public void initialize();
    
    /**
     * <code>render</code> is responsible to presenting the geometry to the
     * OpenGL context. 
     */
    public void render();
    
    /**
     * <code>setTexture</code> sets the associated texture of this 
     * geometry to the passed texture file.
     * 
     * @param filename the image file to use as the texture for this 
     *      geometry object.
     */
    public void setTexture(String filename);
    
    /**
     * <code>setColor</code> sets the color of the geometry. This uses a
     * RGBA color. Where alpha of 1 is opaque and 0 is clear.
     * 
     * @param red the red color of the geometry.
     * @param green the green color of the geometry;
     * @param blue the green color of the geometry.
     * @param alpha the transparency of the geometry.
     */
    public void setColor(float red, float green, float blue, float alpha);
    
    /**
     * <code>getBoundingSphere</code> returns the sphere that surrounds all vertices
     * of the geometry object.
     * @return the bounding sphere of the object.
     */
    public BoundingSphere getBoundingSphere();
    
    /**
     * <code>getBoundingBox</code> returns the box that surrounds all vertices
     * of the geometry object.
     * 
     * @return the bounding box of the object.
     */
    public BoundingBox getBoundingBox();
}
