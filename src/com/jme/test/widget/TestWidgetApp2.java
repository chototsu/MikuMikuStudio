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
import java.util.Random;

import com.jme.app.SimpleGame;
import com.jme.input.AbstractInputController;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractFrame;
import com.jme.widget.WidgetInsets;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.input.mouse.WidgetMouseTestControllerBasic;
import com.jme.widget.layout.WidgetAbsoluteLayout;

/**
 * <code>TestWidgetApp2</code>
 * @author Gregg Patton
 * @version
 */
public class TestWidgetApp2 extends SimpleGame implements Observer {
    class TestFrame extends WidgetAbstractFrame {

        WidgetButton shuffleButton;
        Random random = new Random();

        TestFrame(AbstractInputController ic) {
            super(ic);

            setLayout(new WidgetAbsoluteLayout());

            shuffleButton = new WidgetButton("Shuffle Buttons", WidgetAlignmentType.ALIGN_CENTER);
            shuffleButton.setInsets(new WidgetInsets(5, 5, 5, 5));
            add(shuffleButton);

            for (int cnt = 0; cnt < 20; cnt++) {

                WidgetButton button = new WidgetButton("Button " + (cnt + 1), WidgetAlignmentType.ALIGN_CENTER);
                //button.setBgColor(null);
                //button.setBorder(new WidgetBorder());
                button.setInsets(new WidgetInsets(10, 10, 10, 10));
                add(button);

            }

            doLayout();
        }

        void shuffle() {

            int w = shuffleButton.getWidth();
            int h = shuffleButton.getHeight();

            for (int i = 1; i < getQuantity(); i++) {
                Widget widget = (Widget) getChild(i);

                int x = random.nextInt((int) (display.getWidth() - widget.getPreferredSize().x));
                int y = random.nextInt((int) (display.getHeight() - widget.getPreferredSize().y));

                if (x < w && y < h) {
                    x += w + 1;
                    y += h + 1;
                }

                widget.setLocation(x, y);

            }
        }

    }

    private TestFrame frame;
    private Node scene;
    private Camera cam;
    private AbstractInputController input;

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

        display.getRenderer().draw(scene);
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

        cam = display.getRenderer().getCamera(display.getWidth(), display.getHeight());

        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);
        display.getRenderer().setCamera(cam);

        input = new WidgetMouseTestControllerBasic(this);

    }

    /* (non-Javadoc)
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void initGame() {
        scene = new Node();

        initTriMesh();

        frame = new TestFrame(input);

        frame.shuffle();

        //Add the app as an observer for shuffleButton mouse down
        frame.shuffleButton.addMouseButtonDownObserver(this);

        scene.attachChild(frame);

        cam.update();

        scene.updateGeometricState(0.0f, true);
    }

    private void initTriMesh() {
        Vector3f[] verts = new Vector3f[3];
        ColorRGBA[] color = new ColorRGBA[3];

        verts[0] = new Vector3f();
        verts[0].x = -50;
        verts[0].y = 0;
        verts[0].z = 0;
        verts[1] = new Vector3f();
        verts[1].x = -50;
        verts[1].y = 25;
        verts[1].z = 25;
        verts[2] = new Vector3f();
        verts[2].x = -50;
        verts[2].y = 25;
        verts[2].z = 0;

        color[0] = new ColorRGBA();
        color[0].r = 1;
        color[0].g = 0;
        color[0].b = 0;
        color[0].a = 1;
        color[1] = new ColorRGBA();
        color[1].r = 0;
        color[1].g = 1;
        color[1].b = 0;
        color[1].a = 1;
        color[2] = new ColorRGBA();
        color[2].r = 0;
        color[2].g = 0;
        color[2].b = 1;
        color[2].a = 1;
        int[] indices = { 0, 1, 2 };

        TriMesh t = new TriMesh(verts, null, color, null, indices);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();
        System.out.println(t.getModelBound());
        cam.update();

        scene.attachChild(t);

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
        TestWidgetApp2 app = new TestWidgetApp2();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    public void update(Observable o, Object arg) {
        frame.shuffle();
    }

}
