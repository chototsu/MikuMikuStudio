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
 * Created on 15 juin 2004
 */
package com.jme.sound.scene;

import com.jme.math.Vector3f;
import com.jme.sound.IBuffer;
import com.jme.sound.IBufferFilter;
import com.jme.sound.ISoundRenderer;
import com.jme.sound.ISource;
import com.jme.sound.SoundAPIController;
import com.jme.sound.SoundPool;

/**
 * @author Arman
 *  
 */
public class ProgrammableSound extends SoundSpatial {

    private ISource source;

    private IBuffer[] playingSequence;

    private boolean changed = false;

    private int nextProgram=-1;

    private int cullMode;

    private float sequenceDuration;

    private boolean sequenced;

    private float sequenceStartTime;

    private boolean loopingEnabled;

    private float queueCheckPercentage;

    public ProgrammableSound() {
        source = SoundAPIController.getSoundSystem().generateSources(1)[0];
        cullMode = SoundSpatial.CULL_DISTANCE;
    }

    public void setNextProgram(int programNumber) {
        changed = true;
        nextProgram = programNumber;
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
     * Gets the number of samples
     * 
     * @return the number of sequences; 0 if no sequence has been programmed.
     */
    public int getSequenceSize() {
        if (playingSequence != null) { return playingSequence.length; }
        return 0;
    }

    public void updateWorldData(float timeInSeconds) {
        super.updateWorldData(timeInSeconds);
        if (changed) {
            if (playingSequence != null) {
                //if (source.getBuffersProcessed() != playingSequence.length)
                //        return;
                unqueueBuffers(playingSequence);
            }
            playingSequence = SoundPool.getProgram(nextProgram);
            sequenceDuration = SoundPool.getProgramDuration(nextProgram);
            if(playingSequence.length > 1) sequenced=true;
            setQueueCheckPercentage(queueCheckPercentage);
            
            changed = false;
        }
        if (loopingEnabled) {
            //first time check
            if (sequenceStartTime == 0) sequenceStartTime = timeInSeconds;
            if ((timeInSeconds - sequenceStartTime) >= sequenceDuration) {
            	if (playingSequence != null) {
                    queueBuffers(playingSequence);
                }
                //reset timer
                sequenceStartTime = timeInSeconds;
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

        if (source.isPlaying()) { return; }
        if (playingSequence != null) {
        	if(playingSequence.length>1) sequenced=true;
            source.queueBuffers(playingSequence);
            source.play();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#pause()
     */
    public void pause() {

        source.pause();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#stop()
     */
    public void stop() {

        source.stop();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#rewind()
     */
    public void rewind() {
        source.rewind();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPitch(float)
     */
    public void setPitch(float pitch) {

        source.setPitch(pitch);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getPitch()
     */
    public float getPitch() {
        return source.getPitch();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setGain(float)
     */
    public void setGain(float gain) {

        source.setGain(gain);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getGain()
     */
    public float getGain() {
        return source.getGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMaxDistance(float)
     */
    public void setMaxDistance(float maxDistance) {

        source.setMaxDistance(maxDistance);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMaxDistance()
     */
    public float getMaxDistance() {
        return source.getMaxDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setRolloffFactor(float)
     */
    public void setRolloffFactor(float rolloffFactor) {

        source.setRolloffFactor(rolloffFactor);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getRolloffFactor()
     */
    public float getRolloffFactor() {
        return source.getRolloffFactor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setReferenceDistance(float)
     */
    public void setReferenceDistance(float referenceDistance) {

        source.setReferenceDistance(referenceDistance);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getReferenceDistance()
     */
    public float getReferenceDistance() {
        return source.getReferenceDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMinGain(float)
     */
    public void setMinGain(float minGain) {

        source.setMinGain(minGain);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMinGain()
     */
    public float getMinGain() {
        return source.getMinGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setMaxGain(float)
     */
    public void setMaxGain(float maxGain) {

        source.setMaxGain(maxGain);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getMaxGain()
     */
    public float getMaxGain() {
        return source.getMaxGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setConeOuterGain(float)
     */
    public void setConeOuterGain(float coneOuterGain) {

        source.setConeOuterGain(coneOuterGain);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getConeOuterGain()
     */
    public float getConeOuterGain() {
        return source.getConeOuterGain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPosition(com.jme.math.Vector3f)
     */
    public void setPosition(Vector3f position) {

        source.setPosition(position);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setPosition(float, float, float)
     */
    public void setPosition(float x, float y, float z) {

        source.setPosition(x, y, z);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getPosition()
     */
    public Vector3f getPosition() {
        return source.getPosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setVelocity(com.jme.math.Vector3f)
     */
    public void setVelocity(Vector3f velocity) {

        source.setVelocity(velocity);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setVelocity(float, float, float)
     */
    public void setVelocity(float x, float y, float z) {

        source.setVelocity(x, y, z);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getVelocity()
     */
    public Vector3f getVelocity() {
        return source.getVelocity();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setDirection(com.jme.math.Vector3f)
     */
    public void setDirection(Vector3f direction) {

        source.setDirection(direction);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setDirection(float, float, float)
     */
    public void setDirection(float x, float y, float z) {

        source.setDirection(x, y, z);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getDirection()
     */
    public Vector3f getDirection() {
        return source.getDirection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setSourceRelative(boolean)
     */
    public void setSourceRelative(boolean isRelative) {

        source.setSourceRelative(isRelative);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#isSourceRelative()
     */
    public boolean isSourceRelative() {
        return source.isSourceRelative();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setLooping(boolean)
     */
    public void setLooping(boolean isLooping) {
        if (!sequenced) {
            source.setLooping(isLooping);
            
        }
        loopingEnabled = isLooping;

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
        return source.getBuffersQueued();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#getBuffersProcessed()
     */
    public int getBuffersProcessed() {
        return source.getBuffersProcessed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#queueBuffers(com.jme.sound.IBuffer[])
     */
    public void queueBuffers(IBuffer[] buffers) {

        source.queueBuffers(buffers);

    }

    public void queueBuffer(IBuffer buffer) {

        source.queueBuffer(buffer);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#unqueueBuffers(com.jme.sound.IBuffer[])
     */
    public void unqueueBuffers(IBuffer[] buffers) {

        source.unqueueBuffers(buffers);

    }

    public void unqueueBuffer(IBuffer buffer) {

        source.unqueueBuffer(buffer);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#isPlaying()
     */
    public boolean isPlaying() {
        return source.isPlaying();

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.sound.ISource#setFilter(com.jme.sound.IBufferFilter)
     */
    public void setFilter(IBufferFilter filter) {
        if (playingSequence != null) {
            for (int a = 0; a < playingSequence.length; a++) {
                source.setFilter(filter);
            }
        }
    }

    /**
     * The playing time the playing sample
     * 
     * @return the sample duration in seconds
     */
    public float getPlayingTime() {

        return sequenceDuration;

    }

    /**
     * @return Returns the queueCheckPercentage.
     */
    public float getQueueCheckPercentage() {
        return queueCheckPercentage;
    }

    /**
     * @param queueCheckPercentage
     *            The queueCheckPercentage to set.
     */
    public void setQueueCheckPercentage(float queueCheckPercentage) {
        this.queueCheckPercentage = queueCheckPercentage;

        sequenceDuration -= (sequenceDuration * queueCheckPercentage) / 100;

    }

    public void draw(ISoundRenderer r) {
        r.draw(this);

    }

    private int[] event;

    private int[] program;

    public void bindEvent(int eventNumber, int programNumber) {
        if (event == null) {
            event = new int[2];
            event[0] = eventNumber;
            event[1] = programNumber;
            return;
        }
        int[] tmp = new int[event.length + 2];
        System.arraycopy(event, 0, tmp, 0, event.length);
        tmp[event.length] = eventNumber;
        tmp[event.length + 1] = programNumber;
        event = tmp;
    }
    
    
    public void fireEvent(int eventNumber) {
        if (event != null) {
            for (int i = 0; i < event.length; i+=2) {
                if (event[i] == eventNumber) {
                    setNextProgram(event[i + 1]);
                    source.stop();
                    updateWorldData(-1);
                    play();
                    return;
                }
            }
        }
    }

    
    /**
     * Get the current running sequence program number
     * @return the program number in the SoundPool; -1 if no program has been attached
     */
    public int getCurrentProgram(){
    	return nextProgram;
    }
}