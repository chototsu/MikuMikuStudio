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

package com.jme3.system.gdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.jme3.audio.*;

import java.util.HashMap;

/**
 * Created by kobayasi on 2013/12/28.
 */
public class GdxAudioRenderer implements AudioRenderer{
    private boolean audioDisabled = false;
    private Listener listener;
    private final HashMap<AudioNode, Music> musicMap = new HashMap<AudioNode, Music>();
    @Override
    public void setListener(Listener listener) {
        Gdx.app.log("GdxAudioRenderer", "setListener");
        if (audioDisabled) {
            return;
        }

        if (this.listener != null) {
            // previous listener no longer associated with current
            // renderer
            this.listener.setRenderer(null);
        }

        this.listener = listener;
        this.listener.setRenderer(this);

    }
    @Override
    public void setEnvironment(Environment environment) {
        Gdx.app.log("GdxAudioRenderer", "setEnvironment");
    }

    @Override
    public void playSourceInstance(AudioNode audioNode) {
        Gdx.app.log("GdxAudioRenderer", "playSourceInstance");
        GdxAudioData audioData;
        audioData = (GdxAudioData) audioNode.getAudioData();
        Music music = musicMap.get(audioNode);
        if (Gdx.app.getType() == Application.ApplicationType.iOS && audioData.getAssetKey().getName().endsWith(".ogg")) {
            return;
        }
        if (music == null) {
            music = Gdx.audio.newMusic(GdxAssetCache.getFileHandle(audioData.getAssetKey().getName()));
            musicMap.put(audioNode, music);
        }
        music.stop();
        music.play();
        audioNode.setStatus(AudioNode.Status.Playing);
    }

    @Override
    public void playSource(AudioNode audioNode) {
        Gdx.app.log("GdxAudioRenderer", "playSource");
        if (audioNode.getStatus() == AudioNode.Status.Playing) {
            stopSource(audioNode);
            playSourceInstance(audioNode);
        } else if (audioNode.getStatus() == AudioNode.Status.Stopped) {
            playSourceInstance(audioNode);
        }
    }

    @Override
    public void pauseSource(AudioNode src) {
        Gdx.app.log("GdxAudioRenderer", "pauseSource");
        if (src.getStatus() == AudioNode.Status.Playing) {
            if (src.getAudioData() instanceof GdxAudioData) {
                GdxAudioData audioData = (GdxAudioData) src.getAudioData();
                if (audioData.getAssetKey() instanceof AudioKey) {
                    AudioKey assetKey = (AudioKey) audioData.getAssetKey();

                    if (assetKey.isStream()) {
                        Music mp;
                        if (musicMap.containsKey(src)) {
                            mp = musicMap.get(src);
                            mp.pause();
                            src.setStatus(AudioNode.Status.Paused);
                        }
                    } else {
                        assert src.getChannel() != -1;

                        if (src.getChannel() > 0) {
//                            soundPool.pause(src.getChannel());
                            src.setStatus(AudioNode.Status.Paused);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void stopSource(AudioNode src) {
        Gdx.app.log("GdxAudioRenderer", "stopSource");
        if (src.getStatus() != AudioNode.Status.Stopped) {
            if (src.getAudioData() instanceof GdxAudioData) {
                GdxAudioData audioData = (GdxAudioData) src.getAudioData();
                if (audioData.getAssetKey() instanceof AudioKey) {
                    AudioKey assetKey = (AudioKey) audioData.getAssetKey();
                    if (assetKey.isStream()) {
                        Music mp;
                        if (musicMap.containsKey(src)) {
                            mp = musicMap.get(src);
                            mp.stop();
                            src.setStatus(AudioNode.Status.Stopped);
                            src.setChannel(-1);
                        }
                    } else {
                        int chan = src.getChannel();
                        assert chan != -1; // if it's not stopped, must have id

                        if (src.getChannel() > 0) {
//                            soundPool.stop(src.getChannel());
                            src.setChannel(-1);
                        }

                        src.setStatus(AudioNode.Status.Stopped);

                        if (audioData.getId() > 0) {
//                            soundPool.unload(audioData.getId());
                        }
                        audioData.setId(-1);



                    }
                }
            }

        }

    }

    @Override
    public void updateSourceParam(AudioNode audioNode, AudioParam audioParam) {
        Gdx.app.log("GdxAudioRenderer", "updateSourceParam");
    }

    @Override
    public void updateListenerParam(Listener listener, ListenerParam listenerParam) {
        Gdx.app.log("GdxAudioRenderer", "updateListenerParam");

    }

    @Override
    public void deleteFilter(Filter filter) {
        Gdx.app.log("GdxAudioRenderer", "deleteFilter");

    }

    @Override
    public void deleteAudioData(AudioData audioData) {
        Gdx.app.log("GdxAudioRenderer", "deleteAudioData");

    }

    @Override
    public void initialize() {
        Gdx.app.log("GdxAudioRenderer", "initialize");

    }

    @Override
    public void update(float v) {
//        Gdx.app.log("GdxAudioRenderer", "update");
        for(AudioNode src : musicMap.keySet()) {
            Music music = musicMap.get(src);
            if (src.getStatus() == AudioNode.Status.Playing) {
                if (!music.isPlaying()) {
                    Gdx.app.log("GdxAudioRenderer","music Stopped");
                    src.setStatus(AudioNode.Status.Stopped);
                } else {
                }
            }
        }

    }

    @Override
    public void cleanup() {
        Gdx.app.log("GdxAudioRenderer", "cleanup");
        for(Music music : musicMap.values()) {
            music.dispose();
        }
        musicMap.clear();
    }
}
