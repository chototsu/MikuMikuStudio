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

package com.jme.util;

import java.util.logging.Level;

import org.lwjgl.Sys;

/**
 * <code>Timer</code> handles the system's time related functionality. This
 * allows the calculation of the framerate. To keep the framerate calculation
 * accurate, a call to update each frame is required. <code>Timer</code> is 
 * a singleton object and must be created via the <code>getTimer</code> 
 * method.
 * 
 * @author Mark Powell
 * @version $Id: LWJGLTimer.java,v 1.1 2003-11-14 19:37:28 mojomonkey Exp $
 */
public class LWJGLTimer extends Timer {
    private long frameDiff;
    private static LWJGLTimer instance = null;
    //frame rate parameters.
    private long oldTime = 0;
    private long newTime = 0;
    private float fps = 0;
    
    /**
     * Constructor builds a <code>Timer</code> object. All values will be
     * initialized to it's default values.
     */
    public LWJGLTimer() {
        //reset time
        Sys.setTime(0);

        //set priority of this process
        Sys.setProcessPriority(Sys.LOW_PRIORITY);
        
        //print timer resolution info
        LoggingSystem.getLogger().log(
            Level.INFO,
            "Timer resolution: "
                + Sys.getTimerResolution()
                + " ticks per second");
    }

    /**
     * <code>getFrameRate</code> returns the current frame rate since the
     * last call to <code>update</code>.
     * @return the current frame rate.
     */
    public float getFrameRate() {
        return fps;
    }
    
    /**
     * 
     */
    public float getTimePerFrame() {
        return 1f/fps;
    }
    
    /**
     * <code>setProcessPriority</code> sets the priority of this application.
     * 
     * @param priority the application's priority level.
     */
    public void setProcessPriority(int priority) {
        Sys.setProcessPriority(priority);
    }

    /**
     * <code>setTime</code> sets the time of the timer.
     * 
     * @param time the new time of the timer.
     */
    public void setTime(long time) {
        Sys.setTime(time);
    }

    /**
     * <code>update</code> recalulates the frame rate based on the previous
     * call to update. It is assumed that update is called each frame.
     */
    public void update() {
        newTime = Sys.getTime();
        frameDiff = newTime - oldTime;
        fps =
            (float) Sys.getTimerResolution()
                / (float)frameDiff;
        oldTime = newTime;
    }
    
    
    
    
    /**
     * <code>toString</code> returns the string representation of this timer
     * in the format:<br><br>
     * jme.utility.Timer@1db699b<br>
     * Time: {LONG}<br>
     * FPS: {FLOAT}<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nTime: " + newTime;
        string += "\nFPS: " + fps;
        return string;
    }
}
