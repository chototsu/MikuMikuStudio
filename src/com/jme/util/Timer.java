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
package com.jme.util;

import com.jme.util.lwjgl.*;
import com.jme.system.DisplaySystem;

/**
 * <code>Timer</code> is the base class for a high resolultion timer. It is
 * created from getTimer("display system")
 * 
 * @author Mark Powell
 * @version $Id: Timer.java,v 1.9 2005-05-24 22:47:36 Mojomonkey Exp $
 */
public abstract class Timer {
    private static Timer instance;

    /**
     * Returns the current time in ticks. A tick is an arbitrary measure of time
     * defined by the timer implementation. The number of ticks per second is
     * given by <code>getResolution()</code>.
     * 
     * @return a long value representing the current time
     */
    public abstract long getTime();

    /**
     * Returns the time in seconds. There is no guarantee that the timer starts
     * at 0.0 seconds.
     * 
     * @return the current time in seconds
     */
    public float getTimeInSeconds() {
        return getTime() / (float) getResolution();
    }

    /**
     * Returns the resolution of the timer.
     * 
     * @return the number of timer ticks per second
     */
    public abstract long getResolution();

    /**
     * Returns the "calls per second". If this is called every frame, then it
     * will return the "frames per second".
     * 
     * @return The "calls per second".
     */
    public abstract float getFrameRate();

    /**
     * Returns the time, in seconds, between the last call and the current one.
     * 
     * @return Time between this call and the last one.
     */
    public abstract float getTimePerFrame();

    /**
     * <code>update</code> recalulates the frame rate based on the previous
     * call to update. It is assumed that update is called each frame.
     */
    public abstract void update();

    /**
     * Returns the high resolution timer. Timer is a singleton class so only one
     * instance of Timer is allowed.
     * 
     * @param version
     *            The version of the rendering enviroment.
     * @return The high resolution timer.
     */
    public static Timer getTimer(String version) {
        if (DisplaySystem.DISPLAY_SYSTEM_JWJGL.equalsIgnoreCase(version)) {
            if (instance == null || !(instance instanceof LWJGLTimer)) {
                instance = new LWJGLTimer();
            }

            return instance;
        } else {
            return null;
        }
    }
}
