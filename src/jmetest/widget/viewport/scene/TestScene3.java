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

import com.jme.widget.WidgetAbstractContainer;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetFillType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.layout.WidgetBorderLayout;
import com.jme.widget.layout.WidgetBorderLayoutConstraint;
import com.jme.widget.layout.WidgetFlowLayout;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.panel.WidgetPanel;
import com.jme.widget.panel.WidgetScrollPanel;
import com.jme.widget.panel.rollout.WidgetRolloutPanel;
import com.jme.widget.panel.rollout.WidgetRolloutPanelContainer;
import com.jme.widget.text.WidgetText;
import com.jme.widget.viewport.WidgetViewport;
import com.jme.widget.viewport.WidgetViewportCameraController;

/**
 * <code>TestScene3</code>
 * @author Gregg Patton
 * @version $Id: TestScene3.java,v 1.1 2004-02-14 22:19:55 ericthered Exp $
 */
public class TestScene3 extends TestAbstractScene {

    static final int TOTAL_BUTTONS = 30;
    static final int TOTAL_BUNNIES = 2;
    static final int TOTAL_ROLLOUTS = 1;

    static final boolean ADD_CENTER = true;
    static final boolean ADD_NORTH = true;
    static final boolean ADD_SOUTH = true;
    static final boolean ADD_EAST = true;
    static final boolean ADD_WEST = true;

    static final boolean PANEL = false;
    static final boolean SCROLL_PANEL = false;
    static final boolean SCROLL_PANEL_FILL_HORIZONTAL = false;
    static final boolean ROLLOUT_CONTAINER = true;

    private WidgetText fps;

    /**
     * 
     */
    public TestScene3() {
        super();
    }

    public void init(WidgetViewport vp, WidgetViewportCameraController cameraController) {
        vp.detachAllChildren();
        initGui(vp);
    }

    public void initGui(WidgetViewport vp) {
        vp.setLayout(new WidgetBorderLayout());

        vp.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.FLAT));
        vp.setInsets(new WidgetInsets(1, 1, 1, 1));

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
            vp.add(northPanel, WidgetBorderLayoutConstraint.NORTH);

        WidgetButton south = new WidgetButton("South", WidgetAlignmentType.ALIGN_CENTER);
        if (ADD_SOUTH)
            vp.add(south, WidgetBorderLayoutConstraint.SOUTH);

        WidgetButton east = new WidgetButton("East                East", WidgetAlignmentType.ALIGN_CENTER);
        if (ADD_EAST)
            vp.add(east, WidgetBorderLayoutConstraint.EAST);

        WidgetButton west = new WidgetButton("West                West", WidgetAlignmentType.ALIGN_CENTER);
        if (ADD_WEST)
            vp.add(west, WidgetBorderLayoutConstraint.WEST);

        if (ADD_CENTER) {

            WidgetPanel centerPanel = new WidgetPanel();

            centerPanel.setInsets(new WidgetInsets(5, 5, 5, 5));

            int xSize = 3;
            int ySize = 5;

            centerPanel.setLayout(new WidgetGridLayout(xSize, ySize, 5, 5));

            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {

                    WidgetButton button =
                        new WidgetButton("" + (x + 1) + "," + (y + 1), WidgetAlignmentType.ALIGN_CENTER);
                    button.setInsets(new WidgetInsets(0, 3, 2, 2));

                    centerPanel.add(button);
                }
            }

            vp.add(centerPanel, WidgetBorderLayoutConstraint.CENTER);

        } else {

            if (SCROLL_PANEL || SCROLL_PANEL_FILL_HORIZONTAL) {

                WidgetScrollPanel scrollPanel = new WidgetScrollPanel();

                if (SCROLL_PANEL_FILL_HORIZONTAL)
                    scrollPanel.setLayout(
                        new WidgetFlowLayout(WidgetAlignmentType.ALIGN_WEST, WidgetFillType.HORIZONTAL));

                vp.add(scrollPanel, WidgetBorderLayoutConstraint.CENTER);
                addWidgets(scrollPanel);

            } else if (PANEL) {

                WidgetPanel panel = new WidgetPanel();

                vp.add(northPanel, WidgetBorderLayoutConstraint.CENTER);
                addWidgets(northPanel);

            } else if (ROLLOUT_CONTAINER) {

                WidgetRolloutPanelContainer rpc = new WidgetRolloutPanelContainer();

                for (int i = 0; i < TOTAL_ROLLOUTS; i++) {
                    addWidgets(rpc, "Rollout " + (i + 1));
                }

                vp.add(rpc, WidgetBorderLayoutConstraint.CENTER);
            }
        }

        vp.doLayout();
    }

    private void addWidgets(WidgetAbstractContainer c) {
        addWidgets(c, null);
    }

    private void addWidgets(WidgetAbstractContainer c, String t) {
        String title;

        if (t == null)
            title = "";
        else
            title = t + ":  ";

        WidgetRolloutPanel rollout = null;

        if (ROLLOUT_CONTAINER == true) {
            rollout = new WidgetRolloutPanel(title);
            rollout.setLayout(new WidgetFlowLayout(WidgetAlignmentType.ALIGN_CENTER, 0, 0));
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
                rollout.add(button);
            else
                c.add(button);

        }

        if (rollout != null)
            c.add(rollout);
    }

    /** <code>setFps</code> 
     * @param fps
     * @see jmetest.widget.viewport.scene.TestAbstractScene#setFps(java.lang.String)
     */
    public void setFps(String fps) {
        if (this.fps != null)
            this.fps.setText("FPS:  " + fps);
    }

    /** <code>update</code> 
     * 
     * @see jmetest.widget.viewport.scene.TestAbstractScene#update()
     */
    public void update() {
        // TODO Auto-generated method stub

    }

}
