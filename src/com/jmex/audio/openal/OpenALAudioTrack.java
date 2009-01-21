/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package com.jmex.audio.openal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jcraft.jorbis.JOrbisException;
import com.jcraft.jorbis.VorbisFile;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.stream.AudioInputStream;
import com.jmex.audio.stream.OggInputStream;
import com.jmex.audio.stream.WavInputStream;
import com.jmex.audio.util.AudioLoader;

/**
 * @see AudioTrack
 * @author Joshua Slack
 * @version $Id: OpenALAudioTrack.java,v 1.7 2007/12/03 17:59:28 nca Exp $
 */
public class OpenALAudioTrack extends AudioTrack {
    private static final Logger logger = Logger
            .getLogger(OpenALAudioTrack.class.getName());

    public OpenALAudioTrack(URL resource, boolean stream) {
        super(resource, stream);
        if (resource != null) {
            if (stream) {
                try {
                    Format type = AudioInputStream.sniffFormat(resource.openStream());
                    if (Format.WAV.equals(type)) {
                        WavInputStream inputStream = new WavInputStream(resource);
                        setPlayer(new OpenALStreamedAudioPlayer(inputStream, this));
                    } else if (Format.OGG.equals(type)) {
                        float length = -1; 
                        try {
                            VorbisFile vf;
                            if (!resource.getProtocol().equals("file")) {
                                vf = new VorbisFile(resource.openStream(),
                                        null, 0);
                            } else {
                                vf = new VorbisFile(
                                        URLDecoder.decode(new File(resource.getFile()).getPath(), "UTF-8"));
                            }
                            length = vf.time_total(-1);
                        } catch (JOrbisException e) {
                            logger.log(Level.WARNING, "Error creating VorbisFile", e);
                        }
                        OggInputStream inputStream = new OggInputStream(resource, length);
                        setPlayer(new OpenALStreamedAudioPlayer(inputStream, this));
                    } else {
                        throw new IllegalArgumentException("Given url is not a recognized audio type. Must be OGG or RIFF/WAV: "+resource);
                    }
                    getPlayer().init();
                } catch (IOException e) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "OpenALAudioTrack(URL resource, boolean stream)", "Exception", e);
                }
            } else {
                OpenALAudioBuffer buffer = OpenALAudioBuffer.generateBuffer();
                try {
                    AudioLoader.fillBuffer(buffer, resource);
                } catch (IOException e) {
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "OpenALAudioTrack(URL resource, boolean stream)", "Exception", e);
                    return;
                }
                setPlayer(new OpenALMemoryAudioPlayer(buffer, this));
            }
        }
    }

    public OpenALAudioTrack(URL resource, OpenALAudioBuffer buffer) {
        super(resource, false);
        setPlayer(new OpenALMemoryAudioPlayer(buffer, this));
    }

    public OpenALAudioTrack(URL resource, AudioInputStream inputStream) {
        super(resource, true);
        setPlayer(new OpenALStreamedAudioPlayer(inputStream, this));
    }

}
