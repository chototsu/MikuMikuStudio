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
package com.jme.widget.util;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetRepeater {
    public final static long DEFAULT_REPEAT_RATE_PER_SECOND = 100;
    public final static long DEFAULT_DELAY_REPEAT_MILLISECONDS = 500;

    private long repeatDelayMillis;
    private long repeatPerSecond;

    private long initialDelayMillis;
    
    private boolean performedInitialDelay = false;

    private long curTime;

    private boolean started;

    public WidgetRepeater() {
        this(DEFAULT_REPEAT_RATE_PER_SECOND, DEFAULT_DELAY_REPEAT_MILLISECONDS);
    }

    public WidgetRepeater(long repeatPerSecond, long initialDelayMillis) {

        init(repeatPerSecond, initialDelayMillis);

    }

    private void init(long repeatPerSecond, long initialDelayMillis) {
        this.repeatPerSecond = repeatPerSecond;
        
        if (this.repeatPerSecond != 0)
            this.repeatDelayMillis = 1000l / this.repeatPerSecond;
        else
            this.repeatDelayMillis = this.repeatPerSecond;

        this.initialDelayMillis = initialDelayMillis;
    }

    public boolean doRepeat() {

        boolean ret = false;

        if (started == true) {
            if (!performedInitialDelay) {

                if (System.currentTimeMillis() >= curTime + initialDelayMillis) {
                    ret = true;
                    performedInitialDelay = true;

                    curTime = System.currentTimeMillis();
                }

            } else if (repeatDelayMillis != 0) {
                if (System.currentTimeMillis() >= curTime + repeatDelayMillis) {
                    ret = true;
                    curTime = System.currentTimeMillis();
                }
            }
        }

        return ret;
    }

    public void start() {
        started = true;

        curTime = System.currentTimeMillis();
        performedInitialDelay = false;

    }

    public void restart() {
        start();
    }
        
    public long getRepeatPerSecond() {
        return repeatPerSecond;
    }

    public void setRepeatPerSecond(long l) {
        repeatPerSecond = l;
        init(repeatPerSecond, initialDelayMillis);
    }

    public long getInitialDelayMillis() {
        return initialDelayMillis;
    }

    public void setInitialDelayMillis(long l) {
        initialDelayMillis = l;
        init(repeatPerSecond, initialDelayMillis);
    }

}
