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
package com.jme.scene.state.lwjgl.records;

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Represents a texture unit in opengl
 */
public class TextureUnitRecord {
    public boolean enabled = false;
    public Matrix4f texMatrix = new Matrix4f();
    public Vector3f texScale = new Vector3f();
    public int boundTexture = -1;
    public int envMode = -1;
    public float envRGBScale;
    public float envAlphaScale;
    public ColorRGBA blendColor;
    public boolean textureGenQ = false, textureGenR = false, textureGenS = false, textureGenT = false;
    public int textureGenQMode = -1, textureGenRMode = -1, textureGenSMode = -1, textureGenTMode = -1;
    public int rgbCombineFunc = -1, alphaCombineFunc = -1;
    public int combSrcRGB0 = -1, combSrcRGB1 = -1, combSrcRGB2 = -1;
    public int combOpRGB0 = -1, combOpRGB1 = -1, combOpRGB2 = -1;
    public int combSrcAlpha0 = -1, combSrcAlpha1 = -1, combSrcAlpha2 = -1;
    public int combOpAlpha0 = -1, combOpAlpha1 = -1, combOpAlpha2 = -1;

    public TextureUnitRecord() {
        blendColor = new ColorRGBA(1,1,1,1);
    }
}
