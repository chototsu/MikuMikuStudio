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

package jme.geometry.primitive;

import org.lwjgl.opengl.GL;

import jme.geometry.Geometry;
import jme.geometry.bounding.BoundingBox;
import jme.geometry.bounding.BoundingSphere;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;

/**
 * <code>Primitive</code> defines a basic geometry shape. This typically 
 * denotes objects such as: Spheres, Cubes, Disks, Cylinders and the like. 
 * 
 * @author Mark Powell
 * @version 1
 */
public abstract class Primitive implements Geometry {
    /**
     * the bounding sphere of the primitive.
     */    
    protected BoundingSphere boundingSphere;
    
    /**
     * the bounding box of the primitive.
     */
    protected BoundingBox boundingBox;

   /**
     * the red component of the object's color.
     */
    protected float red = 1.0f;

    /**
     * the green component of the object's color.
     */
    protected float green = 1.0f;

    /**
     * the blue component of the object's color.
     */
    protected float blue = 1.0f;

    /**
     * the transparency of the object.
     */
    protected float alpha = 1.0f;

    //holds the texture id.
    private int texID = -1;

    /**
     * <code>setTexture</code> takes an image file, and adds it to the 
     * texture system. The <code>TextureManager</code> handles the
     * creation of the texture.
     * 
     * @param filename the image file to use as a texture.
     */
    public void setTexture(String filename) {
        texID = TextureManager.getTextureManager().loadTexture(
                filename,
                GL.LINEAR_MIPMAP_LINEAR,
                GL.LINEAR,
                true);
    }
    /**
     * <code>getTextureId</code> returns the texture id associated with
     * this object.
     * @return the texture id of this object.
     */
    public int getTextureId() {
    	return texID;
    }
    /**
    * <code>setColor</code> sets the RGBA values for this object.
    * 
    * @param red the red component of the color.
    * @param green the green component of the color.
    * @param blue the blue component of the color.
    * @param alpha the transparency component of the color.
    */
    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    
    /**
     * <code>render</code> is an abstract method that should handle the
     * displaying of the geometry data.
     */
    public abstract void render();

    /**
     * <code>clean</code> should be called after any <code>render</code> call.
     * This disables the texture 2d state if appropriate.
     */
    public void clean() {
        if (-1 != texID) {
            DisplaySystem.getDisplaySystem().getGL().disable(GL.TEXTURE_2D);
        }
    }

    /**
     * <code>getBoundingSphere</code> returns the sphere that contains the
     * Primitive. Null is possibly returned if the implementing class does 
     * not define the sphere.
     * @return the sphere that bounds the primitive.
     */
    public BoundingSphere getBoundingSphere() {
         return boundingSphere;
    }
     
    /**
     * <code>getBoundingBox</code> returns the box that contains the
     * Primitive. Null is possibly returned if the implmenenting class does
     * not define the box.
     * @return the box the bounds the primitive.
     */
    public BoundingBox getBoundingBox() {
         return boundingBox;
    }

    /**
     * <code>toString</code> returns the string representation of this
     * geometry object in the format: <br><br>
     * Geometry: jme.geometry.primitive.Sphere@10e3293<br>
     * Color: {RGBA VALUE}<br>
     * TextureID: {VALUE}<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nColor: " + red + " " + green + " " + blue + " " + alpha;
        string += "\nTextureID: " + texID;
        return string;
    }
}
