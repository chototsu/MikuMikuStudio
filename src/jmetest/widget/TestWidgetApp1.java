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
package jmetest.widget;

import com.jme.app.BaseGame;
import com.jme.input.AbstractInputHandler;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.panel.WidgetScrollPanel;
import com.jme.widget.panel.rollout.WidgetRolloutPanel;
import com.jme.widget.panel.rollout.WidgetRolloutPanelContainer;
import com.jme.widget.text.WidgetText;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestWidgetApp1 extends BaseGame {

    class TestFrame extends WidgetAbstractFrame {

        static final int TOTAL_BUTTONS = 30;
        static final int TOTAL_BUNNIES = 2;
        static final int TOTAL_ROLLOUTS = 3;

        static final boolean ADD_CENTER = false;
        static final boolean ADD_NORTH = true;
        static final boolean ADD_SOUTH = true;
        static final boolean ADD_EAST = true;
        static final boolean ADD_WEST = true;

        static final boolean PANEL = false;
        static final boolean SCROLL_PANEL = false;
        static final boolean SCROLL_PANEL_FILL_HORIZONTAL = false;
        static final boolean GRID_TEST = false;
        static final boolean ROLLOUT_CONTAINER = true;

        WidgetText fps;

        TestFrame(AbstractInputHandler ic) {
            super(ic);

            setLayout(new WidgetBorderLayout());
            setInsets(new WidgetInsets());

            WidgetPanel northPanel = new WidgetPanel();
            //northPanel.setBgColor(null);
            northPanel.setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_EAST, 5, 0));
            northPanel.setInsets(new WidgetInsets(5, 5, 5, 10));
            northPanel.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.LOWERED));

            WidgetButton northButton = new WidgetButton("North", WidgetAlignmentType.ALIGN_CENTER);
            northButton.setBorder(new WidgetBorder(3, 3, 3, 3));
            northButton.setInsets(new WidgetInsets(5, 5, 5, 5));

            fps = new WidgetText("FPS:  000");

            northPanel.add(northButton);
            northPanel.add(fps);

            if (ADD_NORTH)
                add(northPanel, WidgetBorderLayoutConstraint.NORTH);
                //add(northButton, WidgetBorderLayoutConstraint.NORTH);

            WidgetButton south = new WidgetButton("South", WidgetAlignmentType.ALIGN_CENTER);
            if (ADD_SOUTH)
                add(south, WidgetBorderLayoutConstraint.SOUTH);

            WidgetButton east = new WidgetButton("East                      East", WidgetAlignmentType.ALIGN_CENTER);
            if (ADD_EAST)
                add(east, WidgetBorderLayoutConstraint.EAST);

            WidgetButton west = new WidgetButton("West                      West", WidgetAlignmentType.ALIGN_CENTER);
            if (ADD_WEST)
                add(west, WidgetBorderLayoutConstraint.WEST);

            if (ADD_CENTER) {

                WidgetButton centerButton = new WidgetButton("Center", WidgetAlignmentType.ALIGN_CENTER);
                add(centerButton, WidgetBorderLayoutConstraint.CENTER);

            } else {

                if (SCROLL_PANEL || SCROLL_PANEL_FILL_HORIZONTAL) {

                    WidgetScrollPanel scrollPanel = new WidgetScrollPanel();

                    if (SCROLL_PANEL_FILL_HORIZONTAL)
                        scrollPanel.setLayout(
                            new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST, WidgetFillType.HORIZONTAL));

                    add(scrollPanel, WidgetBorderLayoutConstraint.CENTER);
                    addWidgets(scrollPanel);

                } else if (PANEL) {

                    WidgetPanel panel = new WidgetPanel();

                    panel.setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_CENTER, WidgetFillType.HORIZONTAL));

                    add(panel, WidgetBorderLayoutConstraint.CENTER);

                    for (int i = 0; i < TOTAL_ROLLOUTS; i++) {
                        addWidgets(panel, "Rollout " + (i + 1));
                    }

                } else if (GRID_TEST) {
                  WidgetPanel centerPanel = new WidgetPanel();

                  centerPanel.setInsets(new WidgetInsets(5, 5, 5, 5));

                  int xSize = 4;
                  int ySize = 10;

                  centerPanel.setLayout(new WidgetGridLayout(xSize, ySize));

                  for (int y = 0; y < ySize; y++) {
                      for (int x = 0; x < xSize; x++) {

                          WidgetButton button =
                              new WidgetButton("" + (x + 1) + "," + (y + 1), WidgetAlignmentType.ALIGN_CENTER);
                          button.setInsets(new WidgetInsets(0, 3, 2, 2));

                          centerPanel.add(button);
                      }
                  }

                  add(centerPanel, WidgetBorderLayoutConstraint.CENTER);
                  
                } else if (ROLLOUT_CONTAINER) {

                    WidgetRolloutPanelContainer rpc = new WidgetRolloutPanelContainer();

                    for (int i = 0; i < TOTAL_ROLLOUTS; i++) {
                        addWidgets(rpc, "Rollout " + (i + 1));
                    }

                    add(rpc, WidgetBorderLayoutConstraint.CENTER);
                }
            }

            doLayout();
        }

        void addWidgets(WidgetAbstractContainer c) {
            addWidgets(c, null);
        }

        void addWidgets(WidgetAbstractContainer c, String t) {
            String title;

            if (t == null)
                title = "";
            else
                title = t + ":  ";

            WidgetRolloutPanel rollout = null;

            if (ROLLOUT_CONTAINER == true) {
                rollout = new WidgetRolloutPanel(title);
                rollout.setPanelLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_CENTER, 0, 0));
                //rollout.setPanelInsets(new WidgetInsets(5, 5, 0, 5));
            }

            StringBuffer buf = new StringBuffer();

            for (int cnt = 0; cnt < TOTAL_BUNNIES; cnt++) {
                buf.append("(-:=");
            }

            for (int cnt = 0; cnt < TOTAL_BUTTONS; cnt++) {

                WidgetButton button = new WidgetButton(title + "Happy Bunny Button " + (cnt + 1) + " " + buf);
                button.setInsets(new WidgetInsets(10, 3, 12, 2));

                if (rollout != null)
                    rollout.addPanelWidget(button);
                else
                    c.add(button);

            }

            if (rollout != null)
                c.add(rollout);
        }

        public void init() {
            super.init();

            setForceView(true);
        }

        public void onDraw(Renderer r) {
            super.onDraw(r);

            fps.setText("FPS:  " + getFrameRate().toString());
        }

    }

    private TestFrame frame;
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

        display.getRenderer().setCamera(display.getRenderer().getCamera(display.getWidth(), display.getHeight()));

        input = new WidgetMouseTestControllerBasic(this);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        frame = new TestFrame(input);

        frame.updateGeometricState(0.0f, true);
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
        TestWidgetApp1 app = new TestWidgetApp1();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        //app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
        app.start();
    }

}
