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
