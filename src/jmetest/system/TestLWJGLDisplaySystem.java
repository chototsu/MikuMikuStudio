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
package jmetest.system;

import com.jme.app.SimpleGame;
import com.jme.renderer.ColorRGBA;
import com.jme.system.JmeException;
import com.jme.system.lwjgl.LWJGLDisplaySystem;

/**
 * <code>TestLWJGLDisplaySystem</code> tests the display system for creation
 * of a LWJGL window.
 * @author Mark Powell
 */
public class TestLWJGLDisplaySystem extends SimpleGame {
    public static void main(String[] args) {
        TestLWJGLDisplaySystem app = new TestLWJGLDisplaySystem();
        app.start();
    }

    /**
    `* update is not used for this test.
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
    }

    /**
     * render simply clears the buffer.
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
    }

    /**
     * creates the display sytem and sets the background color to blue.
     * @see com.jme.app.SimpleGame#initSystem()
     */
    protected void initSystem() {
        try {
            display = new LWJGLDisplaySystem();
            display.createWindow(640, 480, 16, 60, false);
        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }
        ColorRGBA blueColor = new ColorRGBA();
        blueColor.r = 0;
        blueColor.g = 0;
        display.getRenderer().setBackgroundColor(blueColor);
    }

    /**
     * init game is not used.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
    }

    /**
     * reinit is not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {
    }

    /**
     * cleanup not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {
    }
}
