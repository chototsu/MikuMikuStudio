/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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
package com.jme.scene.state.lwjgl.records;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashMap;

import org.lwjgl.BufferUtils;

import com.jme.math.Vector3f;

public class TextureStateRecord extends StateRecord {
    
    public FloatBuffer eyePlaneS = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneT = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneR = BufferUtils.createFloatBuffer(4);
    public FloatBuffer eyePlaneQ = BufferUtils.createFloatBuffer(4);

    public HashMap<Integer, TextureRecord> textures;
    public TextureUnitRecord[] units;
    public int hint = -1;
    public int currentUnit = -1;

    /**
     * temporary rotation axis vector to flatline memory usage.
     */
    public final Vector3f tmp_rotation1 = new Vector3f();

    /**
     * temporary matrix buffer to flatline memory usage.
     */
    public final FloatBuffer tmp_matrixBuffer = BufferUtils.createFloatBuffer(16);

    public TextureStateRecord(int maxUnits) {
        textures = new HashMap<Integer, TextureRecord>();
        units = new TextureUnitRecord[maxUnits];
        for (int i = 0; i < maxUnits; i++) {
            units[i] = new TextureUnitRecord();
        }

        eyePlaneS.put(1.0f).put(0.0f).put(0.0f).put(0.0f);
        eyePlaneT.put(0.0f).put(1.0f).put(0.0f).put(0.0f);
        eyePlaneR.put(0.0f).put(0.0f).put(1.0f).put(0.0f);
        eyePlaneQ.put(0.0f).put(0.0f).put(0.0f).put(1.0f);
    }

    public TextureRecord getTextureRecord(int textureId) {
        TextureRecord tr = textures.get(textureId);
        if (tr == null) {
            tr = new TextureRecord();
            textures.put(textureId, tr);
        }
        return tr;
    }
    
    public void removeTextureRecord(int textureId) {
        textures.remove(textureId);
        for (int i = 0; i < units.length; i++) {
            if (units[i].boundTexture == textureId)
                units[i].boundTexture = -1;
        }
    }
    
    @Override
    public void invalidate() {
        super.invalidate();
        currentUnit = -1;
        Collection<TextureRecord> texs = textures.values();
        for (TextureRecord tr : texs) {
            tr.invalidate();
        }
        for (int i = 0; i < units.length; i++) {
            units[i].invalidate();
        }
    }
    
    @Override
    public void validate() {
        super.validate();
        Collection<TextureRecord> texs = textures.values();
        for (TextureRecord tr : texs) {
            tr.validate();
        }
        for (int i = 0; i < units.length; i++) {
            units[i].validate();
        }
    }
}