/*
 * Copyright (c) 2004, jMonkeyEngine - Mojo Monkey Coding
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
package jmetest.widget.viewport;

import com.jme.app.SimpleGame;
import com.jme.input.AbstractInputController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;

/**
 * <code>TestWidgetViewportApp</code> is a test application for WidgetViewport.
 * It demonstrates the use of WidgetViewport for displaying different parts of a
 * scene graph from different viewing perspectives.  Some of the viewports can capture the mouse
 * input.  To activate/deactivate capturing mouse input right-click on the viewport.
 * Not all the viewports will capture mouse input.
 * @author Gregg Patton
 * @version $Id: TestWidgetViewportApp.java,v 1.2 2004-02-25 16:36:46 mojomonkey Exp $
 */
public class TestWidgetViewportApp extends SimpleGame {

    private TestWidgetViewportFrame frame;
    private AbstractInputController input;

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#update()
     */
    protected void update(float interpolation) {
        frame.handleInput(0.2f);
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#render()
     */
    protected void render(float interpolation) {
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
        
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        
        Camera cam = display.getRenderer().getCamera(display.getWidth(), display.getHeight());

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);
        display.setTitle("Test Multiple Viewports");
        input = new WidgetMouseTestControllerBasic(this);

    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#initGame()
     */
    protected void initGame() {
        frame = new TestWidgetViewportFrame(input);
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#reinit()
     */
    protected void reinit() {
        WidgetAbstractFrame.destroy();
        frame.init();
    }

    /* (non-Javadoc)
     * @see com.jme.app.AbstractGame#cleanup()
     */
    protected void cleanup() {
        WidgetAbstractFrame.destroy();
    }

    public static void main(String[] args) {
        TestWidgetViewportApp app = new TestWidgetViewportApp();
        //app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

}
