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

package com.jme.scene.state;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>TextureState</code> maintains a texture state for a given node and
 * it's children. The number of states that a TextureState can maintain at
 * one time is equal to the number of texture units available on the GPU. It is
 * not within the scope of this class to generate the texture, and is
 * recommended that <code>TextureManager</code> be used to create the
 * Texture objects.
 * @see com.jme.util.TextureManager
 * @author Mark Powell
 * @author Tijl Houtbeckers - Added a TextureID cache.
 * @version $Id: TextureState.java,v 1.29 2006-05-18 15:49:34 nca Exp $
 */
public abstract class TextureState extends RenderState {

    /** Ignore textures. */
    public static final int OFF = 0;

    /** Combine texture states starting from the root node and working towards the given SceneElement. Ignore disabled states. */
    public static final int COMBINE_FIRST = 1;

    /** Combine texture states starting from the given Spatial and working towards the root. Ignore disabled states. */
    public static final int COMBINE_CLOSEST = 2;

    /** Similar to COMBINE_CLOSEST, but if a disabled state is encountered, it will stop combining at that point. */
    public static final int COMBINE_RECENT_ENABLED = 3;

    /** Inherit mode from parent. */
    public static final int INHERIT = 4;

    /** Do not combine texture states, just use the most recent one. */
    public static final int REPLACE = 5;

    /** The texture(s). */
    protected transient ArrayList<Texture> texture;

    /** The current number of used texture units. */
    protected static int numTexUnits = -1;

    protected static float maxAnisotropic = -1.0f;

    /** True if multitexturing is supported. */
    protected static boolean supportsMultiTexture = false;

    /** True if anisofiltering is supported. */
    protected static boolean supportsAniso = false;

    /** True if non pow 2 texture sizes are supported. */
    protected static boolean supportsNonPowerTwo = false;

    /** True if S3TC compression is supported. */
    protected static boolean supportsS3TCCompression = false;

    protected transient int firstTexture = 0;
    protected transient int lastTexture = 0;
    
    /** offset is used to denote where to begin access of texture coordinates. 0 default */
    protected int offset = 0;

    protected transient int[] idCache = new int[0];

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
        if(this.texture.size() == 0) {
            this.texture.add(texture);
        } else {
            this.texture.set(0, texture);
        }

        resetFirstLast();
    }

    /**
     *
     * <code>getTexture</code> gets the texture that is assigned to the
     * first texture unit.
     * @return the texture in the first texture unit.
     */
    public Texture getTexture() {
        return texture.get(0);
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
            while(textureUnit >= this.texture.size()) {
                this.texture.add(null);
            }
            this.texture.set(textureUnit, texture);
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
        if(textureUnit >= 0 && textureUnit < numTexUnits && textureUnit < texture.size()) {
            return texture.get(textureUnit);
        } else {
            return null;
        }
    }

    public boolean removeTexture(Texture tex) {
        if (!texture.contains(tex)) return false;
        
        int index = texture.indexOf(tex);
        if (index == numTexUnits-1) {
            numTexUnits--;
            return texture.remove(tex);
        }
        
        texture.set(index, null);
        idCache[index] = 0;
        return true;
    }

    public boolean removeTexture(int textureUnit) {
        if(textureUnit >= 0 && textureUnit < numTexUnits && textureUnit < texture.size())
            return false;
        else {
            Texture t = getTexture(textureUnit);
            if (t == null)
                return false;
            else
                return removeTexture(t);
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
     * Returns the number of textures this texture manager is maintaining.
     * @return the number of textures.
     */
    public int getNumberOfSetTextures() {
        return texture.size();
    }
    
    /**
	 * Fast access for retrieving a Texture ID. A return is guaranteed when
	 * <code>textureUnit</code> is any number under or equal to the highest
	 * textureunit currently in use. This value can be retrieved with
	 * <code>getNumberOfSetTextures</code>. A higher value might result in 
	 * unexpected behaviour such as an exception being thrown.
	 * 
	 * @param textureUnit
	 *            The texture unit from which to retrieve the ID.
	 * @return the textureID, or 0 if there is none.
	 */    
    public final int getTextureID(int textureUnit) {
        if(textureUnit >= 0 && textureUnit < numTexUnits && textureUnit < idCache.length) {
            return idCache[textureUnit];
        } else {
            return 0;
        }
    }
    
    /**
     * <code>setTextureCoordinateOffset</code> sets the offset value used to determine
     * which coordinates to use for texturing Geometry.
     * @param offset the offset (default 0).
     */
    public void setTextureCoordinateOffset(int offset) {
        this.offset = offset;
    }
    
    /**
    * <code>setTextureCoordinateOffset</code> gets the offset value used to determine
     * which coordinates to use for texturing Geometry.
     * @return the offset (default 0).
     */
    public int getTextureCoordinateOffset() {
        return this.offset;
    }

    /**
     * Loads our textures into the underlying rendering system, generating mip
     * maps if appropriate.
     */
    public void load() {
        for (int unit = 0; unit < numTexUnits; unit++) {
            if (getTexture(unit) != null) {
                load(unit);
            }
        }
    }

    /**
     * Loads the texture for the given unit into the underlying rendering
     * system, generating mip maps if appropriate.
     */
    public abstract void load(int unit);

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
      for (int x = 0; x < texture.size(); x++) {
        if (texture.get(x) != null) {
          if (!foundFirst) {
            firstTexture = x;
            foundFirst = true;
          }
          lastTexture = x;
        }
      }
      if (idCache.length <= lastTexture) {
          if(idCache.length == 0) {
              idCache = new int[lastTexture+2];
          } else {
              int[] tempCache = new int[lastTexture+2];
              System.arraycopy(idCache, 0, tempCache, 0, lastTexture);
              idCache = tempCache;
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
        texture=new ArrayList<Texture>();
        for (int i=0;i<ii;i++){
            if ( in.readBoolean() ) {
                texture.add(TextureManager.loadTexture( new URL( in.readUTF() ), in.readInt(), in.readInt() ));
            }
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
        out.writeShort(texture.size());
        for (int i=0;i<texture.size();i++){
            if ( texture.get(i) == null ) {
                out.writeBoolean( false );
            }
            else {
                out.writeBoolean( true );
                out.writeUTF( ((Texture)texture.get(i)).getImageLocation() );
                out.writeInt( ((Texture)texture.get(i)).getMipmapState() );
                out.writeInt( ((Texture)texture.get(i)).getFilter() );
            }
        }
    }

    /**
     * @return true if non pow 2 texture sizes are supported
     */
    public static boolean isSupportingNonPowerOfTwoTextureSize() {
        return supportsNonPowerTwo;
    }

    /**
     * Call to force use of specified textures even if they are not power of 2 sized.
     */
    public static void forceNonPowerOfTwoTextureSizeUsage() {
        supportsNonPowerTwo = true;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.writeSavableArrayList(texture, "texture", new ArrayList<Texture>());
        capsule.write(offset, "offset", 0);
        
    }

    @SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        texture = capsule.readSavableArrayList("texture", new ArrayList<Texture>());
        offset = capsule.readInt("offset", 0);
        resetFirstLast();
    }
}
