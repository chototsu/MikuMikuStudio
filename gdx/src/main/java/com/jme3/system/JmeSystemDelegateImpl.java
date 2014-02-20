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

package com.jme3.system;

import com.jme3.app.Application;
import com.jme3.asset.*;
import com.jme3.asset.gdx.GdxAssetManager;
import com.jme3.audio.*;
import com.jme3.system.gdx.GdxAudioRenderer;
import com.jme3.system.gdx.GdxContext;
//import com.jme3.audio.DummyAudioRenderer;
import com.jme3.system.JmeContext.Type;
import com.jme3.util.JmeFormatter;

import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.URL;

public class JmeSystemDelegateImpl implements JmeSystemDelegate {

    private static final Logger logger = Logger.getLogger(JmeSystemDelegateImpl.class.getName());
    private boolean initialized = false;
    private boolean lowPermissions = false;
    private static ThreadLocal<Application> app = new ThreadLocal<Application>();

    public void initialize(AppSettings settings) {
        if (initialized) {
            return;
        }

        initialized = true;
        try {
            JmeFormatter formatter = new JmeFormatter();

//            Handler consoleHandler = new AndroidLogHandler();
//            consoleHandler.setFormatter(formatter);
        } catch (SecurityException ex) {
            logger.log(Level.SEVERE, "Security error in creating log file", ex);
        }
        logger.log(Level.INFO, "Running on {0}", getFullName());
    }

    public String getFullName() {
        return "MikuMikuStudio gdx 1.0.0";
    }

    public void setLowPermissions(boolean lowPerm) {
        lowPermissions = lowPerm;
    }

    public boolean isLowPermissions() {
        return lowPermissions;
    }

    public JmeContext newContext(AppSettings settings, Type contextType) {
        initialize(settings);
        if (settings.getRenderer().startsWith("LiveWallpaper")) {
            
        }
        return new GdxContext();
    }

    // TODO
    public AudioRenderer newAudioRenderer(AppSettings settings) {
        return new GdxAudioRenderer();
    }

//    public static void setResources(Resources res) {
//        JmeSystem.res = res;
//    }

//    public static Resources getResources() {
//        return res;
//    }

//    public static void setActivity(Context activity) {
//        JmeSystem.activity = activity;
//
//    }
    public void setApplication(Application app) {
        JmeSystem.app.set(app);
    }
    public Application getApplication() {
        return app.get();
    }

//    public static Context getActivity() {
//        return activity;
//    }

    public AssetManager newAssetManager() {
        logger.log(Level.INFO, "newAssetManager()");
        AssetManager am = new GdxAssetManager();

        return am;
    }

    public AssetManager newAssetManager(URL url) {
        logger.log(Level.INFO, "newAssetManager({0})", url);
        AssetManager am = new GdxAssetManager();

        return am;
    }

    public boolean showSettingsDialog(AppSettings settings, boolean loadSettings) {
        return true;
    }

    public Platform getPlatform() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("arm")){
            if (arch.contains("v5")){
                return Platform.Android_ARM5;
            }else if (arch.contains("v6")){
                return Platform.Android_ARM6;
            }else if (arch.contains("v7")){
                return Platform.Android_ARM7;
            }else{
                return Platform.Android_ARM5; // unknown ARM
            }
        }else{
            throw new UnsupportedOperationException("Unsupported Android Platform");
        }
    }
    public InputStream getResourceAsStream(String name) {
        return JmeSystem.class.getResourceAsStream(name);
    }

    public URL getResource(String name) {
        return JmeSystem.class.getResource(name);
    }
}
