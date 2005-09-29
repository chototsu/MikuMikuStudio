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

package com.jme.scene.state;

import java.io.IOException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.util.TextureManager;

/**
 * <code>TextureState</code> maintains a texture state for a given node and
 * it's children. The number of states that a TextureState can maintain at
 * one time is equal to the number of texture units available on the GPU. It is
 * not within the scope of this class to generate the texture, and is
 * recommended that <code>TextureManager</code> be used to create the
 * Texture objects.
 * @see com.jme.util.TextureManager
 * @author Mark Powell
 * @version $Id: TextureState.java,v 1.18 2005-09-29 20:24:06 Mojomonkey Exp $
 */
public abstract class TextureState extends RenderState {

    /** Ignore textures. */
    public static final int OFF = -1;

    /** Combine texture states starting from the root node and working towards the given Spatial. Ignore disabled states. */
    public static final int COMBINE_FIRST = 0;

    /** Combine texture states starting from the given Spatial and working towards the root. Ignore disabled states. */
    public static final int COMBINE_CLOSEST = 1;

    /** Similar to COMBINE_CLOSEST, but if a disabled state is encountered, it will stop combining at that point. */
    public static final int COMBINE_RECENT_ENABLED = 2;

    /** Inherit mode from parent. */
    public static final int INHERIT = 4;

    /** Do not combine texture states, just use the most recent one. */
    public static final int REPLACE = 5;

    /** The texture(s). */
    protected transient Texture[] texture;

    /** The current number of used texture units. */
    protected static int numTexUnits = -1;

    protected static float maxAnisotropic = -1.0f;

    /** True if multitexturing is supported. */
    protected static boolean supportsMultiTexture = false;

    /** True if S3TC compression is supported. */
    protected static boolean supportsS3TCCompression = false;

    protected transient int firstTexture = 0;
    protected transient int lastTexture = 0;

    /**
     * Constructor instantiates a new <code>TextureState</code> object.
     *
     */
    public TextureState() {
    }

    /**
     * <code>getType</code> returns this type of render state.
     * (RS_TEXTURE).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_TEXTURE;
    }

    /**
     *
     * <code>setTexture</code> sets a single texture to the first
     * texture unit.
     * @param texture the texture to set.
     */
    public void setTexture(Texture texture) {
        this.texture[0] = texture;
        resetFirstLast();
    }

    /**
     *
     * <code>getTexture</code> gets the texture that is assigned to the
     * first texture unit.
     * @return the texture in the first texture unit.
     */
    public Texture getTexture() {
        return texture[0];
    }


    /**
     *
     * <code>setTexture</code> sets the texture object to be used by the
     * state. The texture unit that this texture uses is set, if the
     * unit is not valid, i.e. less than zero or greater than the
     * number of texture units supported by the graphics card, it is
     * ignored.
     * @param texture the texture to be used by the state.
     * @param textureUnit the texture unit this texture will fill.
     */
    public void setTexture(Texture texture, int textureUnit) {
        if(textureUnit >= 0 && textureUnit < numTexUnits) {
            this.texture[textureUnit] = texture;
            resetFirstLast();
        }
    }

    /**
     *
     * <code>getTexture</code> retrieves the texture being used by the
     * state in a particular texture unit.
     * @param textureUnit the texture unit to retrieve the texture from.
     * @return the texture being used by the state. If the texture unit
     *      is invalid, null is returned.
     */
    public Texture getTexture(int textureUnit) {
        if(textureUnit >= 0 && textureUnit < numTexUnits) {
            return texture[textureUnit];
        } else {
            return null;
        }
    }

    /**
     *
     * <code>getNumberOfUnits</code> returns the number of texture units
     * the computer's graphics card supports.
     * @return the number of texture units supported by the graphics card.
     */
    public static int getNumberOfUnits() {
        return numTexUnits;
    }

    /**
     * Removes the texture of the given unit.
     * @param unit The unit of the Texture to remove.
     */
    public abstract void delete(int unit);

    /**
     * Removes all Texture set in this TextureState.
     */
    public abstract void deleteAll();

    /**
     * Returns the maximum anisotropic filter.
     * @return The maximum anisotropic filter.
     */
    public float getMaxAnisotropic() {
      return maxAnisotropic;
    }

    /**
     * Returns if S3TC compression is available for textures.
     * @return true if S3TC is available.
     */
    public boolean isS3TCAvailable() {
      return supportsS3TCCompression;
    }


    /**
     * Updates firstTexture to be the first non-null Texture, and lastTexture to be the last
     * non-null texture.
     */
    protected void resetFirstLast() {
      boolean foundFirst = false;
      for (int x = 0; x < numTexUnits; x++) {
        if (texture[x] != null) {
          if (!foundFirst) {
            firstTexture = x;
            foundFirst = true;
          }
          lastTexture = x;
        }
      }
    }


    /**
     * Used with serialization.  Do not call this manually.
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Serializable
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int ii=in.readShort();
        texture=new Texture[ii];
        for (int i=0;i<texture.length;i++){
            if (in.readBoolean())
                texture[i]=TextureManager.loadTexture(new URL(in.readUTF()),in.readInt(),in.readInt());
        }
        resetFirstLast();
    }

  /**
     * Used with serialization.  Do not call this manually.
     * @param out
     * @throws IOException
     * @see java.io.Serializable
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeShort(texture.length);
        for (int i=0;i<texture.length;i++){
            if (texture[i]==null)
                out.writeBoolean(false);
            else{
                out.writeBoolean(true);
                out.writeUTF(texture[i].getImageLocation());
                out.writeInt(texture[i].getMipmapState());
                out.writeInt(texture[i].getFilter());
            }
        }
    }

}
