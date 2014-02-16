package com.jme3.system;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by kobayasi on 2014/02/17.
 */
public interface JmeSystemDelegate {
    public void setLowPermissions(boolean lowPerm);

    public boolean isLowPermissions();

    public AssetManager newAssetManager(URL configFile);

    public AssetManager newAssetManager();

    public boolean showSettingsDialog(AppSettings sourceSettings, final boolean loadFromRegistry);

    public Platform getPlatform();

    public JmeContext newContext(AppSettings settings, JmeContext.Type contextType);

    public AudioRenderer newAudioRenderer(AppSettings settings);

    public void initialize(AppSettings settings);

    public String getFullName();

    public InputStream getResourceAsStream(String name);

    public URL getResource(String name);
}