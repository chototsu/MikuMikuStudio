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

package jme.locale;

import java.util.logging.Level;

import jme.exception.MonkeyRuntimeException;
import jme.system.DisplaySystem;
import jme.texture.TextureManager;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;

/**
 * <code>SimpleLocale</code> defines a simple Locale. This locale is a single
 * quad with a defined center, a defined length for each side, and a normal
 * of (0, 1, 0).
 * 
 * A texture can be set to the <code>SimpleLocale</code> to improve it's 
 * appearence, as well can you change the color.
 * 
 * @author Mark Powell
 * @version 1
 */
public class SimpleLocale implements Locale {

    //Coordinates of locale.
    private Vector3f center;
    private float halfLength;

    //material properties of the locale.
    private int textureID;
    private float red = 1.0f;
    private float blue = 1.0f;
    private float green = 1.0f;
    private float alpha = 1.0f;

    //gl object.
    private GL gl;

    /**
     * Constructor builds a new <code>SimpleLocale</code> with the defined,
     * center and length of each side. 
     * @param center the center point of the locale.
     * @param length the length of the locale's sides.
     * 
     * @throws MonkeyRuntimeException if center is null or length is less than
     *      or equal to zero.
     */
    public SimpleLocale(Vector3f center, float length) {

        if (null == center || length <= 0) {
            throw new MonkeyRuntimeException(
                "Center must be defined, and "
                    + "length must be greater than 0.");
        }

        this.center = center;
        this.halfLength = length / 2;
        gl = DisplaySystem.getDisplaySystem().getGL();

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "SimpleLocale created.");
    }

    /**
     * <code>update</code> does not perform any action for 
     * <code>SimpleLocale</code>. 
     */
    public void update(float time) {
    	//nothing to do
    }

    /**
     * <code>render</code> renders the <code>SimpleLocale</code>. The
     * locale is rendered as a simple quad, with a constant height of
     * <code>center.y</code>.  
     */
    public void render() {

        //bind texture only if one has been set.
        if (textureID > 0) {
            gl.enable(GL.TEXTURE_2D);
            TextureManager.getTextureManager().bind(textureID);
        }

        gl.color4f(red, green, blue, alpha);
        gl.begin(GL.QUADS);

        gl.texCoord2f(0f, 1f);
        gl.vertex3f(center.x - halfLength, center.y, center.z + halfLength);

        gl.texCoord2f(1f, 1f);
        gl.vertex3f(center.x + halfLength, center.y, center.z + halfLength);

        gl.texCoord2f(1f, 0f);
        gl.vertex3f(center.x + halfLength, center.y, center.z - halfLength);

        gl.texCoord2f(0f, 0f);
        gl.vertex3f(center.x - halfLength, center.y, center.z - halfLength);

        gl.end();

        if (textureID > 0) {
            gl.disable(GL.TEXTURE_2D);
        }
    }

    /**
     * <code>setTexture</code> sets the image used by the locale.
     * @param filename the path and filename of the image file.
     */
    public void setTexture(String filename) {
        textureID = TextureManager.getTextureManager().loadTexture(
            filename,
            GL.LINEAR_MIPMAP_LINEAR,
            GL.LINEAR,
            true);
    }

    /**
     * <code>setColor</code> sets the color to set the locale to.
     * @param red the red component of the color.
     * @param green the green component of the color.
     * @param blue the blue component of the color.
     * @param alpha the transparency of the color.
     */
    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    /**
     * <code>getCenter</code> returns the center of the locale.
     * @return the center of the locale.
     */
    public Vector3f getCenter() {
        return center;
    }

    /**
     * <code>getLength</code> returns the length of the locale's sides.
     * @return the length of the locale's sides.
     */
    public float getLength() {
        return halfLength * 2;
    }

    /**
     * <code>setCenter</code> sets the center of the locale.
     * @param center the new center of the locale.
     */
    public void setCenter(Vector3f center) {
        this.center = center;
    }

    /**
     * <code>setLength</code> sets the length of the locale's sides.
     * @param length the length of the locale's sides.
     */
    public void setLength(float length) {
        halfLength = length / 2;
    }

    /**
     * <code>toString</code> returns the string representation of this 
     * object in the format:<br><br>
     * 
     * jme.locale.SimpleLocale@1c282a1<br>
     * Center: {VECTOR}<br>
     * Side Length: {FLOAT}<br>
     * Color: {RGBA VALUE}<br>
     * Texture Name: {IMAGE FILE}<br>
     * 
     * @return string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nCenter: " + center.toString();
        string += "\nSide Length: " + halfLength * 2;
        string += "\nColor: " + red + " " + green + " " + blue + " " + alpha;
        string += "\nTexture Name: " + textureID;
        return string;
    }

	/**
	 * <code>useDistanceFog</code> returns false for the simple locale.
	 * @return false always.
	 */
	public boolean useDistanceFog() {
		return false;
	}

	/**
	 * <code>useVolumetricFog</code> returns false for the simple locale.
	 * @return false always.
	 */
	public boolean useVolumetricFog() {
		return false;
	}
}
