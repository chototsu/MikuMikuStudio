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
package com.jme.test.widget;

import com.jme.app.AbstractGame;
import com.jme.input.InputControllerAbstract;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.LWJGLCamera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetFrameAbstract;
import com.jme.widget.WidgetInsets;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetFlowLayout;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestWidgetAppTemplate extends AbstractGame {
    private class TestFrame extends WidgetFrameAbstract {
        public TestFrame(DisplaySystem ds, InputControllerAbstract ic, Timer timer) {
            super(ds, ic, timer);

            setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_CENTER));

            WidgetButton north = new WidgetButton("Button", WidgetAlignmentType.ALIGN_CENTER);
            //WidgetButton north = new WidgetButton("Button");
            north.setInsets(new WidgetInsets(0, 3, 2, 2));
            add(north);

            doLayout();
        }
    }

    private TestFrame frame;
    private Node scene;
    private LWJGLCamera cam;
    private InputControllerAbstract input;

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update() {
        frame.handleInput();
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render() {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(frame);
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#initSystem()
     */
    protected void initSystem() {
        try {

            display = DisplaySystem.getDisplaySystem(properties.getRenderer());
            display.createWindow(
                properties.getWidth(),
                properties.getHeight(),
                properties.getDepth(),
                properties.getFreq(),
                properties.getFullscreen());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA background = new ColorRGBA(.5f, .5f, .5f, 1);
        display.getRenderer().setBackgroundColor(background);

        cam = new LWJGLCamera(display.getWidth(), display.getHeight());

        display.getRenderer().setCamera(cam);


        input = new WidgetMouseTestControllerBasic(this, display.getRenderer().getCamera(), properties.getRenderer());

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        scene = new Node();

        frame = new TestFrame(display, input, Timer.getTimer(properties.getRenderer()));

        frame.updateGeometricState(0.0f, true);

        scene.attachChild(frame);

        cam.update();

        scene.updateGeometricState(0.0f, true);
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {
        WidgetFrameAbstract.destroy();
        frame.init();
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {
        WidgetFrameAbstract.destroy();
    }

    public static void main(String[] args) {
        TestWidgetAppTemplate app = new TestWidgetAppTemplate();
        app.useDialogAlways(true);
        app.start();
    }

}
