/*
 * Copyright (c) 2009-2010 jMonkeyEngine
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
package com.jme3.system;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class JmeSystem {

    private static final Logger logger = Logger.getLogger(JmeSystem.class.getName());
    private static boolean initialized = false;

    private static JmeSystemDelegate delegate;

    static {
        try {
            delegate = (JmeSystemDelegate) Class.forName("com.jme3.system.JmeSystemDelegateImpl").newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("initialize failed.", ex);
        }
    }

    public static boolean trackDirectMemory() {
        return false;
    }

    public static JmeSystemDelegate getDelegate() {
        return delegate;
    }

    public static void setDelegate(JmeSystemDelegate delegate) {
        JmeSystem.delegate = delegate;
    }

    public static void setLowPermissions(boolean lowPerm) {
        delegate.setLowPermissions(lowPerm);
    }

    public static boolean isLowPermissions() {
        return delegate.isLowPermissions();
    }

    public static AssetManager newAssetManager(URL configFile) {
        return delegate.newAssetManager(configFile);
    }

    public static AssetManager newAssetManager() {
        return delegate.newAssetManager(null);
    }

    public static boolean showSettingsDialog(AppSettings sourceSettings, final boolean loadFromRegistry) {
        return delegate.showSettingsDialog(sourceSettings, loadFromRegistry);
    }

    public static Platform getPlatform() {
        return delegate.getPlatform();
    }

    public static JmeContext newContext(AppSettings settings, JmeContext.Type contextType) {
        return delegate.newContext(settings, contextType);
    }

    public static AudioRenderer newAudioRenderer(AppSettings settings) {
        return delegate.newAudioRenderer(settings);
    }

    public static void initialize(AppSettings settings) {
        delegate.initialize(settings);
        initialized = true;
    }

    public static String getFullName() {
        return delegate.getFullName();
    }

    public static InputStream getResourceAsStream(String name) {
        return delegate.getResourceAsStream(name);
    }

    public static URL getResource(String name) {
        return delegate.getResource(name);
    }
}
