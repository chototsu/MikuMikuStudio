/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
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
package com.jme.scene;

/**
 * <code>Controller</code> provides a base class for creation of controllers to
 * modify nodes and render states over time. The base controller provides a 
 * repeat type, min and max time, as well as a phase and frequency. Subclasses
 * of this will provide the update method that takes the current time and
 * modifies an object in a application specific way.
 * @author Mark Powell
 * @version $Id: Controller.java,v 1.2 2003-10-17 20:45:27 mojomonkey Exp $
 */
public abstract class Controller {
    public static final int RT_CLAMP = 0;
    public static final int RT_WRAP = 1;
    public static final int RT_CYCLE = 2;
    
    private int repeatType;
    private float minTime;
    private float maxTime;
    private float phase;
    private float frequency;
    private boolean active;
    
    /**
     * <code>getFrequency</code>
     * @return
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * <code>setFrequency</code>
     * @param frequency
     */
    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    /**
     * <code>getMaxTime</code>
     * @return
     */
    public float getMaxTime() {
        return maxTime;
    }

    /**
     * <code>setMaxTime</code>
     * @param maxTime
     */
    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * <code>getMinTime</code>
     * @return
     */
    public float getMinTime() {
        return minTime;
    }

    /**
     * <code>setMinTime</code>
     * @param minTime
     */
    public void setMinTime(float minTime) {
        this.minTime = minTime;
    }

    /**
     * <code>getPhase</code>
     * @return
     */
    public float getPhase() {
        return phase;
    }

    /**
     * <code>setPhase</code>
     * @param phase
     */
    public void setPhase(float phase) {
        this.phase = phase;
    }

    /**
     * <code>getRepeatType</code>
     * @return
     */
    public int getRepeatType() {
        return repeatType;
    }

    /**
     * <code>setRepeatType</code>
     * @param repeatType
     */
    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public abstract void update(float time);
}
