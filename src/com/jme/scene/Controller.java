/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
 * repeat type, min and max time, as well as speed. Subclasses
 * of this will provide the update method that takes the current time and
 * modifies an object in a application specific way.
 * @author Mark Powell
 * @version $Id: Controller.java,v 1.6 2004-04-22 22:26:44 renanse Exp $
 */
public abstract class Controller {
    public static final int RT_CLAMP = 0;
    public static final int RT_WRAP = 1;
    public static final int RT_CYCLE = 2;

    private int repeatType;
    private float minTime;
    private float maxTime;
    private float speed = 1;
    private boolean active = true;

    /**
     * <code>getFrequency</code>
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * <code>setFrequency</code>
     * @param speed
     */
    public void setSpeed(float frequency) {
        this.speed = frequency;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public abstract void update(float time);
}
