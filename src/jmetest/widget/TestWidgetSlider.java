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
package jmetest.widget;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import com.jme.app.BaseGame;
import com.jme.input.AbstractInputHandler;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.WidgetOrientationType;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.slider.WidgetHSlider;
import com.jme.widget.slider.WidgetVSlider;
import com.jme.widget.text.WidgetLabel;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestWidgetSlider extends BaseGame {

    class TestFrame extends WidgetAbstractFrame implements Observer {
        WidgetLabel northSliderValue = new WidgetLabel("", WidgetAlignmentType.ALIGN_CENTER);
        WidgetLabel southSliderValue = new WidgetLabel("", WidgetAlignmentType.ALIGN_CENTER);
        WidgetLabel eastSliderValue = new WidgetLabel("", WidgetAlignmentType.ALIGN_EAST);
        WidgetLabel westSliderValue = new WidgetLabel("", WidgetAlignmentType.ALIGN_WEST);
        WidgetHSlider hNorthSlider;
        WidgetHSlider hSouthSlider;
        WidgetVSlider vEastSlider;
        WidgetVSlider vWestSlider;

        WidgetPanel centerPanel;

        TestFrame(AbstractInputHandler ic) {
            super(ic);

            setLayout(new WidgetBorderLayout());

            hNorthSlider = new WidgetHSlider(WidgetOrientationType.DOWN);
            hNorthSlider.setBorder(new WidgetBorder(1, 1, 1, 1));
            hNorthSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            hNorthSlider.setMaximum(200);
            add(hNorthSlider, WidgetBorderLayoutConstraint.NORTH);

            hSouthSlider = new WidgetHSlider(WidgetOrientationType.UP);
            hSouthSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            hSouthSlider.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.LOWERED));
            hSouthSlider.setMinimum(-100);
            hSouthSlider.setMaximum(0);
            add(hSouthSlider, WidgetBorderLayoutConstraint.SOUTH);

            vWestSlider = new WidgetVSlider(WidgetOrientationType.RIGHT);
            vWestSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            add(vWestSlider, WidgetBorderLayoutConstraint.WEST);

            vEastSlider = new WidgetVSlider(WidgetOrientationType.LEFT);
            vEastSlider.setInsets(new WidgetInsets(5, 5, 5, 5));
            add(vEastSlider, WidgetBorderLayoutConstraint.EAST);

            centerPanel = new WidgetPanel();
            centerPanel.setLayout(new WidgetBorderLayout());

            add(centerPanel, WidgetBorderLayoutConstraint.CENTER);

            northSliderValue.setInsets(new WidgetInsets(5, 5, 5, 5));
            centerPanel.add(northSliderValue, WidgetBorderLayoutConstraint.NORTH);

            southSliderValue.setInsets(new WidgetInsets(5, 5, 5, 5));
            centerPanel.add(southSliderValue, WidgetBorderLayoutConstraint.SOUTH);

            eastSliderValue.setInsets(new WidgetInsets(5, 5, 5, 5));
            centerPanel.add(eastSliderValue, WidgetBorderLayoutConstraint.EAST);

            westSliderValue.setInsets(new WidgetInsets(5, 5, 5, 5));
            centerPanel.add(westSliderValue, WidgetBorderLayoutConstraint.WEST);

            WidgetPanel msgPanel = new WidgetPanel();
            msgPanel.setLayout(new WidgetFlowLayout(WidgetFillType.HORIZONTAL));

            WidgetLabel msgLabel = new WidgetLabel("The top slider has a range of 0 to 200.", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("The bottom slider has a range of -100 to 0.", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("The bottom slider gets updated with the negative", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("value of the top slider.  The update is triggered by", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("an Observer notification.", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("The bottom slider's value is constrained to it's range", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("when the top slider's value goes outside of the bottom", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            msgLabel = new WidgetLabel("slider's range.", WidgetAlignmentType.ALIGN_WEST);
            msgPanel.add(msgLabel);

            centerPanel.add(msgPanel, WidgetBorderLayoutConstraint.CENTER);

            hNorthSlider.addValueChangeObserver(this);
            hSouthSlider.addValueChangeObserver(this);
            vEastSlider.addValueChangeObserver(this);
            vWestSlider.addValueChangeObserver(this);

            update(null, hNorthSlider);

            doLayout();

            update(null, hNorthSlider);

        }

        /** <code>update</code>
         * @param o
         * @param arg
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        public void update(Observable o, Object obj) {

            DecimalFormat df = new DecimalFormat("0.00");

            northSliderValue.setTitle("" + df.format(hNorthSlider.getValue()));
            southSliderValue.setTitle("" + df.format(hSouthSlider.getValue()));
            eastSliderValue.setTitle("" + df.format(vEastSlider.getValue()));
            westSliderValue.setTitle("" + df.format(vWestSlider.getValue()));

            if (obj == hNorthSlider)
                hSouthSlider.setValue(-hNorthSlider.getValue());

            centerPanel.doLayout();
        }

    }

    private TestFrame frame;
    private Node scene;
    private Camera cam;
    private AbstractInputHandler input;

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#update()
     */
    protected void update(float interpolation) {
        frame.handleInput();
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#render()
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(frame);
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

        } catch (JmeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        ColorRGBA background = new ColorRGBA(.5f, .5f, .5f, 1);
        display.getRenderer().setBackgroundColor(background);

        cam = display.getRenderer().createCamera(display.getWidth(), display.getHeight());

        display.getRenderer().setCamera(cam);

        display.setTitle("GUI Slider Interaction Test");

        input = new WidgetMouseTestControllerBasic(this);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        scene = new Node("");

        frame = new TestFrame(input);

        frame.updateGeometricState(0.0f, true);

        scene.attachChild(frame);

        cam.update();

        scene.updateGeometricState(0.0f, true);
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {
        WidgetAbstractFrame.destroy();
        frame.init();
    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {
        WidgetAbstractFrame.destroy();
    }

    public static void main(String[] args) {
        TestWidgetSlider app = new TestWidgetSlider();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        //app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

}
