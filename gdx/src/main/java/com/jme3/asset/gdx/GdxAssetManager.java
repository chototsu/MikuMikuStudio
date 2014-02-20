/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.asset.gdx;

import com.jme3.asset.*;
import com.jme3.material.Material;
import com.jme3.system.gdx.GdxAudioLoader;
import com.jme3.texture.Texture;
import com.jme3.texture.plugins.gdx.GdxTGALoader;
import com.jme3.texture.plugins.gdx.GdxImageLoader;

/**
 * Created with IntelliJ IDEA.
 * User: kobayasi
 * Date: 13/10/08
 * Time: 0:56
 * To change this template use File | Settings | File Templates.
 */
public class GdxAssetManager extends DesktopAssetManager {
    public GdxAssetManager() {
        super(true);
        registerLoader(GdxTGALoader.class, "tga");
        this.registerLoader(GdxImageLoader.class, "jpg", "bmp", "gif", "png", "jpeg","spa","sph");
        this.registerLoader(GdxAudioLoader.class, "wav", "mp3", "ogg");
    }

    @Override
    public <T> T loadAsset(AssetKey<T> key) {
        return super.loadAsset(key);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Object loadAsset(String name) {
        return super.loadAsset(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Texture loadTexture(TextureKey key) {
        return super.loadTexture(key);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Material loadMaterial(String name) {
        return super.loadMaterial(name);    //To change body of overridden methods use File | Settings | File Templates.
    }



}
