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

import java.util.Observable;
import java.util.Observer;

import com.jme.app.SimpleGame;
import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.image.Texture;
import com.jme.input.AbstractInputController;
import com.jme.light.AmbientLight;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Box;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.sound.ISource;
import com.jme.sound.SoundAPIController;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.TextureManager;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.input.mouse.WidgetMouseButtonType;
import com.jme.widget.input.mouse.WidgetMouseTestControllerFirstPerson;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.text.WidgetLabel;

/**
 * <code>TestWidgetButtonLightSwitch</code>
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: TestWidgetButtonLightSwitch.java,v 1.6 2004-02-09 12:18:55 greggpatton Exp $
 */
public class TestWidgetButtonLightSwitch extends SimpleGame {
    private ISource clickSource;
	static String STARTED_STATE_STRING = " Stop ";
    static String STOPPED_STATE_STRING = "Start";

    static String ON_STATE_STRING = "Off";
    static String OFF_STATE_STRING = "On";
    
    

    class TestFrame extends WidgetAbstractFrame implements Observer {

        WidgetButton startStopButton;
        WidgetButton onOffButton;
        WidgetLabel msgLabel;
        WidgetLabel fps;
        WidgetPanel westPanel;
        WidgetPanel eastPanel;
        WidgetPanel buttonPanel;
        WidgetPanel msgPanel;

        TestFrame(AbstractInputController ic) {
            super(ic);

            setLayout(new WidgetBorderLayout());

            eastPanel = new WidgetPanel();
            eastPanel.setLayout(new WidgetBorderLayout());
            eastPanel.setBgColor(null);

            westPanel = new WidgetPanel();
            westPanel.setLayout(new WidgetBorderLayout());
            westPanel.setBgColor(null);

            buttonPanel = new WidgetPanel();
            buttonPanel.setLayout(new WidgetFlowLayout(WidgetFillType.HORIZONTAL));
            buttonPanel.setBgColor(null);

            msgPanel = new WidgetPanel();
            msgPanel.setLayout(new WidgetFlowLayout(WidgetFillType.HORIZONTAL));
            msgPanel.setBgColor(null);
            msgPanel.setInsets(new WidgetInsets(0, 0, 0, 25));

            fps = new WidgetLabel("         ", WidgetAlignmentType.ALIGN_CENTER);
            fps.setFgColor(new ColorRGBA());
            fps.setBgColor(null);
            fps.setInsets(new WidgetInsets(0, 0, 5, 0));
            add(fps, WidgetBorderLayoutConstraint.SOUTH);

            msgLabel = new WidgetLabel("'W' to move forward", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("'S' to move backward", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("'A' to strafe left", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("'D' to strafe right", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Up Arrow to look up", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Down Arrow to look down", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Right Arrow to turn right", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Left Arrow to turn left", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Esc to exit", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("", WidgetAlignmentType.ALIGN_EAST);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("Right click mouse to toggle cursor", WidgetAlignmentType.ALIGN_EAST);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgPanel.add(msgLabel);

            eastPanel.add(msgPanel, WidgetBorderLayoutConstraint.SOUTH);
            add(eastPanel, WidgetBorderLayoutConstraint.EAST);

            startStopButton = new WidgetButton(STARTED_STATE_STRING, WidgetAlignmentType.ALIGN_CENTER);
            startStopButton.setBorder(new WidgetBorder(3, 3, 3, 3));
            startStopButton.setInsets(new WidgetInsets(2, 4, 5, 2));

            onOffButton = new WidgetButton(ON_STATE_STRING, WidgetAlignmentType.ALIGN_CENTER);
            onOffButton.setBorder(new WidgetBorder(3, 3, 3, 3));
            onOffButton.setInsets(new WidgetInsets(2, 4, 5, 2));

            buttonPanel.add(startStopButton);
            buttonPanel.add(onOffButton);

            westPanel.add(buttonPanel, WidgetBorderLayoutConstraint.SOUTH);
            add(westPanel, WidgetBorderLayoutConstraint.WEST);

            msgLabel = new WidgetLabel("Test WidgetButton Light Switch", WidgetAlignmentType.ALIGN_CENTER);
            msgLabel.setFgColor(new ColorRGBA());
            msgLabel.setBgColor(null);
            msgLabel.setInsets(new WidgetInsets(10, 0, 0, 0));
            add(msgLabel, WidgetBorderLayoutConstraint.NORTH);

            //Add the frame as an observer for startStopButton mouse down
            startStopButton.addMouseButtonDownObserver(this);

            //Add the frame as an observer for onOffButton mouse down
            onOffButton.addMouseButtonDownObserver(this);

            doLayout();
        }

        void toggleStartStop() {
            if (startStopButton.getTitle() == STARTED_STATE_STRING) {
                startStopButton.setTitle(STOPPED_STATE_STRING);
            } else {
                startStopButton.setTitle(STARTED_STATE_STRING);
            }
        }

        void toggleOnOff() {
            if (onOffButton.getTitle() == ON_STATE_STRING) {

                onOffButton.setTitle(OFF_STATE_STRING);

                spotlight1.setEnabled(false);
                spotlight2.setEnabled(true);
                dr.setEnabled(false);

            } else {

                onOffButton.setTitle(ON_STATE_STRING);

                spotlight1.setEnabled(true);
                spotlight2.setEnabled(true);
                dr.setEnabled(true);

            }
        }

        public void handleMouseButtonUp() {
            super.handleMouseButtonUp();

            if (getMouseInput().getPreviousButtonState() == WidgetMouseButtonType.MOUSE_BUTTON_2) {
                if (getMouseInput().isCursorVisible()) {
                    getMouseInput().setCursorVisible(false);
                } else {
                    getMouseInput().setCursorVisible(true);
                }
            }
        }

        public void handleMouseButtonDown() {

            if (frame.isMouseCursorOn() && getMouseInput().getButtonState() != WidgetMouseButtonType.MOUSE_BUTTON_2) {
                super.handleMouseButtonDown();
                
            }
        }

        public boolean isMouseCursorOn() {
            return getMouseInput().isCursorVisible();
        }

        /* (non-Javadoc)
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        public void update(Observable o, Object arg) {
            if (arg == startStopButton) {
                toggleStartStop();
				clickSource.play();
            } else if (arg == onOffButton) {
                toggleOnOff();
				clickSource.play();
            }

        }

    }

    private TestFrame frame;

    private TriMesh t, t2, t3, t4;

    private TriMesh box;
    private Node scene, root;
    private static final float MAX_STEPS = 25;
    private Camera cam;
    private AbstractInputController input;
    private BezierCurve curve;

    private float step = 0;
    private float mod = 0.0005f;

    private Vector3f up = new Vector3f(0, 1, 0);
    SpotLight spotlight1;
    SpotLight spotlight2;
    DirectionalLight dr;

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {

        frame.fps.setTitle("FPS:  " + WidgetAbstractFrame.getFrameRate().toString());

        input.setUpdateMouseActionsEnabled(!frame.isMouseCursorOn());

        frame.handleInput(0.1f);
        //input.update(0.1f);

        if (frame.startStopButton.getTitle().equals(STARTED_STATE_STRING))
            scene.updateWorldData(0.0005f);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        Vector3f point;
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(root);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initSystem()
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
            cam = display.getRenderer().getCamera(properties.getWidth(), properties.getHeight());

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA blackColor = new ColorRGBA(0, 0, 0, 1);
        display.getRenderer().setBackgroundColor(blackColor);

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);

        Vector3f loc = new Vector3f(0.0f, 0.0f, 15.0f);
        Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);

        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

        input = new WidgetMouseTestControllerFirstPerson(this, cam);

    	SoundAPIController.getSoundSystem(properties.getRenderer());
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {

        //create control Points
        Vector3f[] points = new Vector3f[4];
        points[0] = new Vector3f(-4, 0, 0);
        points[1] = new Vector3f(-2, 3, 2);
        points[2] = new Vector3f(2, -3, -2);
        points[3] = new Vector3f(4, 0, 0);

        curve = new BezierCurve(points);
        ColorRGBA[] colors = new ColorRGBA[4];
        colors[0] = new ColorRGBA(0, 1, 0, 1);
        colors[1] = new ColorRGBA(1, 0, 0, 1);
        colors[2] = new ColorRGBA(1, 1, 0, 1);
        colors[3] = new ColorRGBA(0, 0, 1, 1);
        curve.setColors(colors);

        Vector3f min = new Vector3f(-0.1f, -0.1f, -0.1f);
        Vector3f max = new Vector3f(0.1f, 0.1f, 0.1f);

        ZBufferState buf = display.getRenderer().getZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.CF_LEQUAL);

        t = new Box(min, max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(points[0]);

        t2 = new Box(min, max);
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        t2.setLocalTranslation(points[1]);

        t3 = new Box(min, max);
        t3.setModelBound(new BoundingSphere());
        t3.updateModelBound();

        t3.setLocalTranslation(points[2]);

        t4 = new Box(min, max);
        t4.setModelBound(new BoundingSphere());
        t4.updateModelBound();

        t4.setLocalTranslation(points[3]);

        box = new Box(min.mult(5), max.mult(5));
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();

        box.setLocalTranslation(points[0]);

        CurveController cc = new CurveController(curve, box);
        box.addController(cc);
        cc.setRepeatType(Controller.RT_CYCLE);
        cc.setUpVector(up);

        TextureState ts = display.getRenderer().getTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture("data/Images/Monkey.jpg", Texture.MM_LINEAR, Texture.FM_LINEAR, true));
        box.setRenderState(ts);

        AmbientLight am = new AmbientLight();
        am.setDiffuse(new ColorRGBA(0.05f, 0.05f, 0.05f, 1.0f));
        am.setAmbient(new ColorRGBA(0.05f, 0.05f, 0.05f, 1.0f));

        spotlight1 = new SpotLight();
        spotlight1.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        spotlight1.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        spotlight1.setDirection(new Vector3f(0, 0, 0));
        spotlight1.setLocation(new Vector3f(25, 10, 0));
        spotlight1.setAngle(15);

        spotlight2 = new SpotLight();
        spotlight2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        spotlight2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        spotlight2.setDirection(new Vector3f(0, 0, 0));
        spotlight2.setLocation(new Vector3f(-25, 10, 0));
        spotlight2.setAngle(15);

        dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        //dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        dr.setDirection(new Vector3f(0, 0, -1));

        LightState lightState = display.getRenderer().getLightState();
        lightState.attach(am);
        lightState.attach(dr);
        lightState.attach(spotlight1);
        lightState.attach(spotlight2);

        am.setEnabled(true);
        spotlight1.setEnabled(true);
        spotlight2.setEnabled(true);
        dr.setEnabled(true);

        scene = new Node();

        scene.setRenderState(lightState);
        scene.setRenderState(buf);

        scene.attachChild(t);
        scene.attachChild(t2);
        scene.attachChild(t3);
        scene.attachChild(t4);
        scene.attachChild(box);

        scene.attachChild(curve);
        root = new Node();

        root.attachChild(scene);

        scene.updateGeometricState(0.0f, true);

        frame = new TestFrame(input);

        root.attachChild(frame);
        
        clickSource=SoundAPIController.getSoundSystem().loadSource("data/sound/click.mp3");

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {}

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {}

    public static void main(String[] args) {
        TestWidgetButtonLightSwitch app = new TestWidgetButtonLightSwitch();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

}
