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

import com.jme3.system.Timer;

/**
 * <code>GdxTimer</code> is a System.nanoTime implementation of <code>Timer</code>.
 */
public class GdxTimer extends Timer {
    
    //private static final long TIMER_RESOLUTION = 1000L;
    //private static final float INVERSE_TIMER_RESOLUTION = 1f/1000L;
    private static final long TIMER_RESOLUTION = 1000000000L;
    private static final float INVERSE_TIMER_RESOLUTION = 1f/1000000000L;
    
    private long startTime;
    private float previousTime;
    private float tpf;
    private float fps;
    
    public GdxTimer() {
        //startTime = System.currentTimeMillis();
        startTime = System.nanoTime();
    }

    /**
     * Returns the time in seconds. The timer starts
     * at 0.0 seconds.
     *
     * @return the current time in seconds
     */
    @Override
    public float getTimeInSeconds() {
        return getTime() * INVERSE_TIMER_RESOLUTION;
    }

    public long getTime() {
        //return System.currentTimeMillis() - startTime;
        return System.nanoTime() - startTime;
    }

    public long getResolution() {
        return TIMER_RESOLUTION;
    }

    public float getFrameRate() {
        return fps;
    }

    public float getTimePerFrame() {
        return tpf;
    }

    public void update() {
        long timeNow = getTime();
        tpf = (timeNow) * (1.0f / TIMER_RESOLUTION) - previousTime;
        fps = 1.0f / tpf;
        previousTime = previousTime += tpf;
    }
    
    public void reset() {
        //startTime = System.currentTimeMillis();
        startTime = System.nanoTime();
        previousTime = 0;//getTime();
    }
}
