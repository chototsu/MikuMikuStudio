/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.util.lwjgl;

import java.util.logging.Level;

import org.lwjgl.Sys;

import com.jme.math.FastMath;
import com.jme.util.LoggingSystem;
import com.jme.util.Timer;

/**
 * <code>Timer</code> handles the system's time related functionality. This
 * allows the calculation of the framerate. To keep the framerate calculation
 * accurate, a call to update each frame is required. <code>Timer</code> is a
 * singleton object and must be created via the <code>getTimer</code> method.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLTimer.java,v 1.11 2005-11-14 05:37:51 renanse Exp $
 */
public class LWJGLTimer extends Timer {

    private long frameDiff;

    //frame rate parameters.
    private long oldTime = 0;

    private long newTime = 0;
    
    private float lastTPF = 0, lastFPS = 0;

    private final static int TIMER_SMOOTHNESS = 32;

    private long[] tpf = new long[TIMER_SMOOTHNESS];

    private int smoothIndex = TIMER_SMOOTHNESS - 1;

    private final static long timerRez = Sys.getTimerResolution();
    private final static float avgPlusInvTimerRez = (1f / timerRez) / TIMER_SMOOTHNESS;

    private final long startTime; // this is only final to allow compiler optimizations

    /**
     * Constructor builds a <code>Timer</code> object. All values will be
     * initialized to it's default values.
     */
    public LWJGLTimer() {
        //reset time
        oldTime = Sys.getTime();
        startTime = Sys.getTime();

        // set tpf... Using 0 fps to begin with...
        for (int i = tpf.length; --i >= 0;)
            tpf[i] = 0;

        //print timer resolution info
        LoggingSystem.getLogger().log(Level.INFO,
                "Timer resolution: " + timerRez + " ticks per second");
    }

    /**
     * @see com.jme.util.Timer#getTime()
     */
    public long getTime() {
        return Sys.getTime() - startTime;
    }

    /**
     * @see com.jme.util.Timer#getResolution()
     */
    public long getResolution() {
        return timerRez;
    }

    /**
     * <code>getFrameRate</code> returns the current frame rate since the last
     * call to <code>update</code>.
     * 
     * @return the current frame rate.
     */
    public float getFrameRate() {
        return lastFPS;
    }

    public float getTimePerFrame() {
        return lastTPF;
    }

    /**
     * <code>update</code> recalulates the frame rate based on the previous
     * call to update. It is assumed that update is called each frame.
     */
    public void update() {
        newTime = Sys.getTime();
        frameDiff = newTime - oldTime;
        tpf[smoothIndex] = frameDiff;
        oldTime = newTime;
        smoothIndex--;
        if (smoothIndex < 0) smoothIndex = tpf.length - 1;
        
        lastTPF = 0.0f;
        for (int i = tpf.length; --i >= 0;)
            lastTPF += tpf[i];
        lastTPF *= avgPlusInvTimerRez;
        if (lastTPF < FastMath.FLT_EPSILON) lastTPF = FastMath.FLT_EPSILON;
        
        lastFPS = 1f / lastTPF;
    }

    /**
     * <code>toString</code> returns the string representation of this timer
     * in the format: <br>
     * <br>
     * jme.utility.Timer@1db699b <br>
     * Time: {LONG} <br>
     * FPS: {LONG} <br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nTime: " + newTime;
        string += "\nFPS: " + getFrameRate();
        return string;
    }
}