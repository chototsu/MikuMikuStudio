/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the
 * names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *  
 */
/*
 * Created on 25 janv. 2004
 *  
 */
package com.jme.sound.scene;

import java.net.URL;

import com.jme.math.Vector3f;
import com.jme.sound.IBuffer;
import com.jme.sound.IBufferFilter;
import com.jme.sound.ISoundRenderer;
import com.jme.sound.ISource;
import com.jme.sound.SoundAPIController;
import com.jme.sound.scene.SoundSpatial;

/**
 * @author Arman Ozcelik
 *  
 */
public class SphericalSound extends SoundSpatial implements ISource {

    private int cullMode;

    private ISource[] sequence;

    private int[] playTime;

    private int activeSource = 0;

    private boolean sequenced;

    private float sequenceStartTime;

    private boolean loopingEnabled;

    public SphericalSound(String file) {
        sequence = new ISource[1];
        sequence[0] = SoundAPIController.getSoundSystem().loadSource(file);
        cullMode = SoundSpatial.CULL_DISTANCE;
    }

    public SphericalSound(URL url) {
        sequence = new ISource[1];
        sequence[0] = SoundAPIController.getSoundSystem().loadSource(url);
        cullMode = SoundSpatial.CULL_DISTANCE;
    }

    public SphericalSound(String[] files, int[] playingTimeInMillis) {
        sequence = new ISource[files.length];
        for (int a = 0; a < files.length; a++) {
            sequence[a] = SoundAPIController.getSoundSystem().loadSource(
                    files[a]);
        }
        if (files.length > 1) {
            sequenced = true;
        }
        playTime = playingTimeInMillis;
        cullMode = SoundSpatial.CULL_DISTANCE;
    }

    public SphericalSound(URL[] urls, int[] playingTimeInMillis) {
        sequence = new ISource[urls.length];
        for (int a = 0; a < urls.length; a++) {
            sequence[a] = SoundAPIController.getSoundSystem().loadSource(
                    urls[a]);
        }
        if (urls.length > 1) {
            sequenced = true;
        }
        playTime = playingTimeInMillis;
        cullMode = SoundSpatial.CULL_DISTANCE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.scene.SoundSpatial#draw(com.jme.sound.scene.SoundRenderer)
     */
    public void draw(ISoundRenderer r) {
        r.draw(this);
    }

    /**
     * @deprecated Use methods provided in <code>SphericalSound</code>
     * @return
     */
    public ISource getSource() {
        return sequence[activeSource];
    }

    /**
     * @deprecated Use the Constructors provided
     * @param source
     */
    public void setSource(ISource source) {
        sequence[activeSource] = source;
    }

    /**
     * @return
     */
    public int getCullMode() {
        return cullMode;
    }

    /**
     * @param i
     */
    public void setCullMode(int i) {
        cullMode = i;
    }

    /**
     * @return Returns the activeSource.
     */
    public int getActiveSequence() {
        return activeSource;
    }

    /**
     * Used to override the actual playing sound sequence.
     * using a number higher than the number of samples-1 will have no effect
     * @param number The sequence to activate. Remark that the sample numbering starts at 0
     */
    public void setActiveSource(int number) {
        if(number < sequence.length)
            activeSource = number;
    }

    /**
     * Detects if this SphericalSound is sequence playing enabled.
     * @return Returns true if there are more than 2 samples inside this SphericalSound.
     */
    public boolean isSequenced() {
        return sequenced;
    }

    
    /**
     * Gets the number of samples 
     * @return the number of sequences
     */
    public int getSequenceSize() {
        return sequence.length;
    }

    public void updateWorldData(float timeInSeconds) {
        super.updateWorldData(timeInSeconds);
        if (playTime != null) {
            if (activeSource < sequence.length) {
                if (sequenceStartTime == 0) sequenceStartTime = timeInSeconds;
                if ((timeInSeconds - sequenceStartTime) > playTime[activeSource]/1000) {
                    stop(activeSource);
                    sequenceStartTime = timeInSeconds;
                    activeSource++;
                }
            } else {
                if (loopingEnabled) activeSource = 0;
            }
        }
    }

    /**
     * @return Returns the loopingEnabled.
     */
    public boolean isLoopingEnabled() {
        return loopingEnabled;
    }

    /**
     * @param loopingEnabled
     *            The loopingEnabled to set.
     */
    public void setLoopingEnabled(boolean loopingEnabled) {
        this.loopingEnabled = loopingEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#play()
     */
    public void play() {
        if (activeSource < sequence.length) sequence[activeSource].play();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#pause()
     */
    public void pause() {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].pause();
        }
    }

    private void pause(int seqNumber) {
        sequence[seqNumber].pause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#stop()
     */
    public void stop() {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].stop();
        }
    }

    private void stop(int seqNumber) {

        sequence[seqNumber].stop();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#rewind()
     */
    public void rewind() {
        activeSource = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#delete()
     */
    public void delete() {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].delete();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPitch(float)
     */
    public void setPitch(float pitch) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setPitch(pitch);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getPitch()
     */
    public float getPitch() {
        return sequence[0].getPitch();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setGain(float)
     */
    public void setGain(float gain) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setGain(gain);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getGain()
     */
    public float getGain() {
        return sequence[0].getGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMaxDistance(float)
     */
    public void setMaxDistance(float maxDistance) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setMaxDistance(maxDistance);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMaxDistance()
     */
    public float getMaxDistance() {
        return sequence[0].getMaxDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setRolloffFactor(float)
     */
    public void setRolloffFactor(float rolloffFactor) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setRolloffFactor(rolloffFactor);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getRolloffFactor()
     */
    public float getRolloffFactor() {
        return sequence[0].getRolloffFactor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setReferenceDistance(float)
     */
    public void setReferenceDistance(float referenceDistance) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setReferenceDistance(referenceDistance);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getReferenceDistance()
     */
    public float getReferenceDistance() {
        return sequence[0].getReferenceDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMinGain(float)
     */
    public void setMinGain(float minGain) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setMinGain(minGain);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMinGain()
     */
    public float getMinGain() {
        return sequence[0].getMinGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMaxGain(float)
     */
    public void setMaxGain(float maxGain) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setMaxGain(maxGain);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMaxGain()
     */
    public float getMaxGain() {
        return sequence[0].getMaxGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setConeOuterGain(float)
     */
    public void setConeOuterGain(float coneOuterGain) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setConeOuterGain(coneOuterGain);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getConeOuterGain()
     */
    public float getConeOuterGain() {
        return sequence[0].getConeOuterGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPosition(com.jme.math.Vector3f)
     */
    public void setPosition(Vector3f position) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setPosition(position);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPosition(float, float, float)
     */
    public void setPosition(float x, float y, float z) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setPosition(x, y, z);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getPosition()
     */
    public Vector3f getPosition() {
        return sequence[0].getPosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setVelocity(com.jme.math.Vector3f)
     */
    public void setVelocity(Vector3f velocity) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setVelocity(velocity);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setVelocity(float, float, float)
     */
    public void setVelocity(float x, float y, float z) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setVelocity(x, y, z);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getVelocity()
     */
    public Vector3f getVelocity() {
        return sequence[0].getVelocity();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setDirection(com.jme.math.Vector3f)
     */
    public void setDirection(Vector3f direction) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setDirection(direction);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setDirection(float, float, float)
     */
    public void setDirection(float x, float y, float z) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setDirection(x, y, z);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getDirection()
     */
    public Vector3f getDirection() {
        return sequence[0].getDirection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setSourceRelative(boolean)
     */
    public void setSourceRelative(boolean isRelative) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setSourceRelative(isRelative);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#isSourceRelative()
     */
    public boolean isSourceRelative() {
        return sequence[0].isSourceRelative();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setLooping(boolean)
     */
    public void setLooping(boolean isLooping) {
        if (!sequenced) {
            sequence[0].setLooping(isLooping);
            loopingEnabled = isLooping;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getLooping()
     */
    public boolean getLooping() {

        return loopingEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getBuffersQueued()
     */
    public int getBuffersQueued() {
        return sequence[0].getBuffersQueued();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getBuffersProcessed()
     */
    public int getBuffersProcessed() {
        return sequence[0].getBuffersProcessed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setBuffer(com.jme.sound.IBuffer)
     */
    public void setBuffer(IBuffer buffer) {
        if (activeSource < sequence.length) {
            sequence[activeSource].setBuffer(buffer);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getBuffer()
     */
    public IBuffer getBuffer() {
        if (activeSource < sequence.length)
                return sequence[activeSource].getBuffer();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#queueBuffers(com.jme.sound.IBuffer[])
     */
    public void queueBuffers(IBuffer[] buffers) {
        if (activeSource < sequence.length)
                sequence[activeSource].queueBuffers(buffers);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#unqueueBuffers(com.jme.sound.IBuffer[])
     */
    public void unqueueBuffers(IBuffer[] buffers) {
        if (activeSource < sequence.length)
                sequence[activeSource].unqueueBuffers(buffers);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#isPlaying()
     */
    public boolean isPlaying() {
        
       return activeSource < sequence.length ? sequence[activeSource].isPlaying() : false;
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setFilter(com.jme.sound.IBufferFilter)
     */
    public void setFilter(IBufferFilter filter) {
        for (int a = 0; a < sequence.length; a++) {
            sequence[a].setFilter(filter);
        }
    }
    
    public int getPlayingTime(){
        if(playTime !=null){
            if(activeSource < sequence.length){
                return playTime[activeSource];
            }
        }
        return 0;
    }
}