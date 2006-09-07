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

package com.jme.util;

import java.io.IOException;
import java.net.URL;

import com.jme.image.Image;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * 
 * <code>TextureKey</code> provides a way for the TextureManager to cache and
 * retrieve <code>Texture</code> objects.
 * 
 * @author Joshua Slack
 * @version $Id: TextureKey.java,v 1.22 2006-09-07 15:35:55 irrisor Exp $
 */
final public class TextureKey implements Savable {

    public interface LocationOverride {
        public URL getLocation(String file);
    }
    
    protected URL m_location = null;
    protected int m_minFilter, m_maxFilter;
    protected float m_anisoLevel;
    protected boolean m_flipped;
    protected int code = Integer.MAX_VALUE;
    protected int imageType = Image.GUESS_FORMAT;
    protected String fileType;
    
    private static LocationOverride locationOverride;
    
    public TextureKey() {
        
    }

    public TextureKey(URL location, int minFilter, int maxFilter,
            float anisoLevel, boolean flipped, int imageType) {
        m_location = location;
        m_minFilter = minFilter;
        m_maxFilter = maxFilter;
        m_flipped = flipped;
        m_anisoLevel = anisoLevel;
        this.imageType = imageType;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TextureKey)) {
            return false;
        }
        
        TextureKey that = (TextureKey) other;
        if (this.m_location == null) {
			if (that.m_location != null)
				return false;
        }
		else if (!this.m_location.equals(that.m_location))
			return false;
        
        if (this.m_minFilter != that.m_minFilter)
            return false;
        if (this.m_maxFilter != that.m_maxFilter)
            return false;
        if (this.m_anisoLevel != that.m_anisoLevel)
            return false;
        if (this.m_flipped != that.m_flipped)
            return false;
        if (this.imageType != that.imageType)
            return false;
        if (this.fileType == null && that.fileType != null) return false;
        else if (this.fileType != null && !this.fileType.equals(that.fileType))
            return false;

        return true;
    }

    // TODO: make this better?
    public int hashCode() {
        if (code == Integer.MAX_VALUE) {
            code = 37;
            if(m_location != null) {
                code += 37 * m_location.hashCode();
            }
            if(fileType != null) {
                code += 37 * fileType.hashCode();
            }
            code += 37 * (int) (m_anisoLevel * 100);
            code += 37 * m_maxFilter;
            code += 37 * m_minFilter;
            code += 37 * imageType;
            code += 37 * (m_flipped ? 1 : 0);
        }
        return code;
    }
    
    public void resetHashCode() {
        code = Integer.MAX_VALUE;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        if ( m_location != null ) {
            capsule.write(m_location.getProtocol(), "protocol", null);
            capsule.write(m_location.getHost(), "host", null);
            capsule.write(m_location.getFile(), "file", null);
        }
        capsule.write(m_minFilter, "minFilter", 0);
        capsule.write(m_maxFilter, "maxFilter", 0);
        capsule.write(m_anisoLevel, "anisoLevel", 0);
        capsule.write(m_flipped, "flipped", false);
        capsule.write(imageType, "imageType", Image.GUESS_FORMAT);
        capsule.write(fileType, "fileType", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        String protocol = capsule.readString("protocol", null);
        String host = capsule.readString("host", null);
        String file = capsule.readString("file", null);
        if(locationOverride != null && "file".equals(protocol)) {
            int index = file.lastIndexOf('/');
            if(index == -1) {
                index = file.lastIndexOf('\\');
            }
            index+=1;
            
            file = file.substring(index);
            m_location = new URL(locationOverride.getLocation(file), file);
        } else {
            m_location = new URL(protocol, host, file);
        }
        
        m_minFilter = capsule.readInt("minFilter", 0);
        m_maxFilter = capsule.readInt("maxFilter", 0);
        m_anisoLevel = capsule.readFloat("anisoLevel", 0);
        m_flipped = capsule.readBoolean("flipped", false);
        imageType = capsule.readInt("imageType", Image.GUESS_FORMAT);
        fileType = capsule.readString("fileType", null);
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public static LocationOverride getLocationOverride() {
        return locationOverride;
    }
    
    public static void setLocationOverride(LocationOverride locationOverride) {
        TextureKey.locationOverride = locationOverride;
    }
    
    /** This method is to be used with setOverridingLocation. */
    public static URL getOverridingLocation() {
        if (locationOverride == null) return null;
        else return locationOverride.getLocation(null);
    }
    
    public static void setOverridingLocation(final URL overridingLocation) {
        setLocationOverride(new LocationOverride() {
            public URL getLocation(String file) {
                return overridingLocation;
            }
        });
    }
    
    public Class getClassTag() {
        return this.getClass();
    }

    /**
     * @return Returns the anisoLevel.
     */
    public float getAnisoLevel() {
        return m_anisoLevel;
    }

    /**
     * @param level The anisoLevel to set.
     */
    public void setAnisoLevel(float level) {
        m_anisoLevel = level;
    }

    /**
     * @return Returns the flipped.
     */
    public boolean isFlipped() {
        return m_flipped;
    }

    /**
     * @param flipped The flipped to set.
     */
    public void setFlipped(boolean flipped) {
        this.m_flipped = flipped;
    }

    /**
     * @return Returns the location.
     */
    public URL getLocation() {
        return m_location;
    }

    /**
     * @param location The location to set.
     */
    public void setLocation(URL location) {
        this.m_location = location;
    }

    /**
     * @return Returns the maxFilter.
     */
    public int getMaxFilter() {
        return m_maxFilter;
    }

    /**
     * @param filter The maxFilter to set.
     */
    public void setMaxFilter(int filter) {
        m_maxFilter = filter;
    }

    /**
     * @return Returns the minFilter.
     */
    public int getMinFilter() {
        return m_minFilter;
    }

    /**
     * @param filter The minFilter to set.
     */
    public void setMinFilter(int filter) {
        m_minFilter = filter;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
