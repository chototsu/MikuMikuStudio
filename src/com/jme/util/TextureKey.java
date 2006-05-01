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
 * @version $Id: TextureKey.java,v 1.6 2006-05-01 14:18:35 nca Exp $
 */
final public class TextureKey {
    protected URL m_location = null;
    protected int m_minFilter, m_maxFilter;
    protected float m_anisoLevel;
    protected boolean m_flipped;
    protected int code = Integer.MAX_VALUE;
    protected int imageType;
    
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
        if(this.m_location == null && !(that.m_location == null)) {
            return false;
        }
        if (this.m_location == null)
            if (that.m_location != null)
                return false;
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

        return true;
    }

    // TODO: make this better?
    public int hashCode() {
        if (code == Integer.MAX_VALUE) {
            if(m_location != null) {
                code = m_location.hashCode();
            }
            code += (int) (m_anisoLevel * 100);
            code += m_maxFilter;
            code += m_minFilter;
            code += imageType;
            code += (m_flipped ? 1 : 0);
        }
        return code;
    }
    
    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }
}
