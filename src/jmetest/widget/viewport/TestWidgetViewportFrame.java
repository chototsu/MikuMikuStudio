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

import com.jme.input.AbstractInputHandler;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import jmetest.widget.viewport.scene.TestAbstractScene;
import jmetest.widget.viewport.scene.TestScene1;
import jmetest.widget.viewport.scene.TestScene2;
import jmetest.widget.viewport.scene.TestScene3;
import jmetest.widget.viewport.scene.TestScene4;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.viewport.WidgetViewport;
import com.jme.widget.viewport.WidgetViewportCameraController;

/**
 * <code>TestWidgetViewportFrame</code>
 * @author Gregg Patton
 * @version $Id: TestWidgetViewportFrame.java,v 1.5 2004-03-27 17:45:04 greggpatton Exp $
 */
public class TestWidgetViewportFrame extends WidgetAbstractFrame {

    private WidgetViewport vp1;
    private WidgetViewport vp2;
    private WidgetViewport vp3;
    private WidgetViewport vp4;

    private TestScene1 scene1;
    private TestScene2 scene2;
    private TestScene3 scene3;
    private TestScene4 scene4;

    private WidgetViewport curVp;
    private TestAbstractScene curScene;

    /**
     * @param ic
     */
    public TestWidgetViewportFrame(AbstractInputHandler ic) {
        super(ic);

        //SoundAPIController.getSoundSystem(DisplaySystem.getDisplaySystem().getRendererType().getName());

        scene1 = new TestScene1();
        scene2 = new TestScene2();
        scene3 = new TestScene3();
        scene4 = new TestScene4();

        vp1 = new WidgetViewport();
        vp2 = new WidgetViewport();
        vp3 = new WidgetViewport();
        vp4 = new WidgetViewport();

        int xSize = 2;
        int ySize = 2;

        setLayout(new WidgetGridLayout(xSize, ySize));

        scene2.init(vp1, scene2.getCameraController1());
        scene2.initGui(vp1);
        add(vp1);

        scene4.init(vp2, scene4.getCameraController2());
        add(vp2);

        scene3.init(vp3, scene4.getCameraController3());
        add(vp3);

        scene1.init(vp4, scene1.getCameraController4());
        add(vp4);

        doLayout();

        scene1.shuffle();
    }

    /** <code>onDraw</code> 
     * @param r
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {

        String fps = WidgetAbstractFrame.getFrameRate().toString();

        this.scene1.setFps(fps);
        this.scene2.setFps(fps);
        this.scene3.setFps(fps);
        this.scene4.setFps(fps);

        super.onDraw(r);
    }

    public void doMouseButtonUp() {

        super.doMouseButtonUp();
        
        if (getMouseInput().getPreviousButtonState() == WidgetMouseButtonType.MOUSE_BUTTON_2) {
            if (getMouseInput().isCursorVisible()) {

                Spatial scene = null;

                if (vp1.isMouseInWidget()) {
                    scene = vp1.getChild();
                    curVp = vp1;
                } else if (vp2.isMouseInWidget()) {
                    scene = vp2.getChild();
                    curVp = vp2;
                } else if (vp3.isMouseInWidget()) {
                    scene = vp3.getChild();
                    curVp = vp3;
                } else if (vp4.isMouseInWidget()) {
                    scene = vp4.getChild();
                    curVp = vp4;
                } else {
                    curVp = null;
                }

                if (scene == scene1.getScene()) {
                    curScene = scene1;
                } else if (scene == scene2.getScene()) {
                    curScene = scene2;
                    getMouseInput().setCursorVisible(false);
                } else if (scene == scene3.getScene()) {
                    curScene = scene3;
                } else if (scene == scene4.getScene()) {
                    curScene = scene4;
                    getMouseInput().setCursorVisible(false);
                }

            } else {
                getMouseInput().setCursorVisible(true);
                curVp = null;
                curScene = null;
            }
        }
    }

    public void doMouseButtonDown() {

        if (isMouseCursorOn() && getMouseInput().getButtonState() != WidgetMouseButtonType.MOUSE_BUTTON_2) {
            super.doMouseButtonDown();
        }
    }

    public void handleInput(boolean updateController, float time) {
        super.handleInput(updateController, time);

        if (curVp != null && updateController) {
            WidgetViewportCameraController cameraController = curVp.getCameraController();

            if (cameraController != null && cameraController.getInputHandler() != null) {
                cameraController.getInputHandler().update(false, false, time);
            }
            
        }

        scene1.update();
        scene2.update();
        scene3.update();
        scene4.update();

    }

    public boolean isMouseCursorOn() {
        return getMouseInput().isCursorVisible();
    }
}
