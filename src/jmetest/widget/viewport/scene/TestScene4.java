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
package jmetest.widget.viewport.scene;

import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.input.mouse.WidgetMouseTestControllerFirstPerson;
import com.jme.widget.viewport.WidgetViewport;
import com.jme.widget.viewport.WidgetViewportCameraController;

/**
 * <code>TestScene4</code>
 * @author Gregg Patton
 * @version $Id: TestScene4.java,v 1.3 2004-02-24 22:03:13 mojomonkey Exp $
 */
public class TestScene4 extends TestAbstractScene {

    /**
     * 
     */
    public TestScene4() {
        super();

    }

    public void init(WidgetViewport vp, WidgetViewportCameraController cameraController) {
        vp.detachAllChildren();
        
        
        if (scene == null) {
            DisplaySystem display = DisplaySystem.getDisplaySystem();

            timer = Timer.getTimer(display.getRendererType().getName());


            ZBufferState zstate = display.getRenderer().getZBufferState();
            zstate.setEnabled(true);
            scene = new Node("Scene Node");
            scene.setRenderState(zstate);
            Model model = new MilkshapeASCIIModel("Milkshape Model");
            model.load("data/model/msascii/run.txt", "data/model/msascii/");
            model.getAnimationController().setFrequency(10.0f);
            model.getAnimationController().setRepeatType(Controller.RT_CYCLE);
            scene.attachChild(model);
            SpotLight am = new SpotLight();
            am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
            am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            am.setDirection(new Vector3f(0, 0, 0));
            am.setLocation(new Vector3f(25, 10, 0));
            am.setAngle(15);

            SpotLight am2 = new SpotLight();
            am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
            am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            am2.setDirection(new Vector3f(0, 0, 0));
            am2.setLocation(new Vector3f(-25, 10, 0));
            am2.setAngle(15);

            DirectionalLight dr = new DirectionalLight();
            dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
            dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            dr.setDirection(new Vector3f(0, 0, -150));

            LightState state = display.getRenderer().getLightState();
            state.setEnabled(true);
            state.attach(am);
            state.attach(dr);
            state.attach(am2);
            am.setEnabled(true);
            am2.setEnabled(true);
            dr.setEnabled(true);
            scene.setRenderState(state);

            scene.updateGeometricState(0, true);

        }

        vp.setCameraController(cameraController);

        vp.attachChild(scene);

        initGui(vp);

    }
    
    public void initGui(WidgetViewport vp) {
        vp.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.FLAT));
        vp.setInsets(new WidgetInsets(2, 2, 2, 2));
        vp.setBgColor(new ColorRGBA(0, 0, 0, 1));
    }

    /** <code>setFps</code> 
     * @param fps
     * @see jmetest.widget.viewport.scene.TestAbstractScene#setFps(java.lang.String)
     */
    public void setFps(String fps) {
    }
    
    /** <code>initNewCameraController</code> 
     * @return
     * @see jmetest.widget.viewport.scene.TestAbstractScene#initNewCameraController()
     */
    protected WidgetViewportCameraController initNewCameraController() {
        Camera camera = DisplaySystem.getDisplaySystem().getRenderer().getCamera(1, 1);

        Vector3f loc = new Vector3f(20.0f, 20.0f, 200.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(-.50f, -.25f, -1.0f);


        camera.setFrame(loc, left, up, dir);

        WidgetViewportCameraController cameraController =
            new WidgetViewportCameraController(camera, new WidgetMouseTestControllerFirstPerson(camera));

        return cameraController;
    }
    
    /** <code>update</code> 
     * 
     * @see jmetest.widget.viewport.scene.TestAbstractScene#update()
     */
    public void update() {
        if (timer.getFrameRate() > 0)
            scene.updateWorldData(timer.getTimePerFrame());
    }
    
}
