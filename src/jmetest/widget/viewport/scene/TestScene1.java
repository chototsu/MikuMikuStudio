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

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.BoundingSphere;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.widget.Widget;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetInsets;
import com.jme.widget.border.WidgetBorder;
import com.jme.widget.border.WidgetBorderType;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.button.WidgetButton;
import com.jme.widget.layout.WidgetAbsoluteLayout;
import com.jme.widget.viewport.WidgetViewport;
import com.jme.widget.viewport.WidgetViewportCameraController;

/**
 * <code>TestScene1</code>
 * @author Gregg Patton
 * @version $Id: TestScene1.java,v 1.2 2004-02-20 20:17:50 mojomonkey Exp $
 */
public class TestScene1 extends TestAbstractScene implements Observer {
    private WidgetButton shuffleButton;
    private Random random = new Random();

    /**
     * 
     */
    public TestScene1() {
        super();
    }

    /** <code>update</code> 
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (arg == shuffleButton) {
            shuffle();
        }
    }

    public void init(WidgetViewport vp, WidgetViewportCameraController cameraController) {

        vp.detachAllChildren();

        if (scene == null) {
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

            TriMesh t = new TriMesh("Triangle", verts, null, color, null, indices);
            t.setModelBound(new BoundingSphere());
            t.updateModelBound();

            scene = new Node("Scene Node");

            scene.setWorldBound(new BoundingSphere());

            scene.attachChild(t);

            t.updateGeometricState(0.0f, true);
        }

        vp.attachChild(scene);

        vp.setCameraController(cameraController);

        initGui(vp);

    }

    public void initGui(WidgetViewport vp) {
        vp.setBorder(new WidgetBorder(1, 1, 1, 1, WidgetBorderType.FLAT));
        vp.setInsets(new WidgetInsets(1, 1, 1, 1));
        vp.setBgColor(new ColorRGBA(.5f, .5f, .5f, 1));

        vp.setLayout(new WidgetAbsoluteLayout());

        shuffleButton = new WidgetButton("Shuffle Buttons", WidgetAlignmentType.ALIGN_CENTER);
        shuffleButton.setInsets(new WidgetInsets(5, 5, 5, 5));
        shuffleButton.setLocation(vp.getInsets().getLeft(), vp.getInsets().getBottom());
        vp.add(shuffleButton);

        for (int cnt = 0; cnt < 20; cnt++) {

            WidgetButton button = new WidgetButton("Button " + (cnt + 1), WidgetAlignmentType.ALIGN_CENTER);
            //button.setBgColor(null);
            //button.setBorder(new WidgetBorder());
            button.setInsets(new WidgetInsets(5, 5, 5, 5));
            vp.add(button);

        }

        vp.doLayout();

        shuffleButton.deleteMouseButtonDownObservers();
        shuffleButton.addMouseButtonDownObserver(this);
    }

    public void shuffle() {
        if (shuffleButton == null)
            return;
            
        WidgetViewport vp = (WidgetViewport) shuffleButton.getParent();

        WidgetViewRectangle r = vp.getViewRectangle();

        float width = r.getWidth();
        float height = r.getHeight();

        int w = shuffleButton.getWidth();
        int h = shuffleButton.getHeight();

        WidgetInsets insets = vp.getInsets();

        if (width > 0 && height > 0) {
            for (int i = 1; i < vp.getWidgetCount(); i++) {
                Widget widget = vp.getWidget(i);

                int x =
                    random.nextInt(
                        (int) (width - (widget.getPreferredSize().x + insets.getLeft() + insets.getRight())));
                int y =
                    random.nextInt(
                        (int) (height - (widget.getPreferredSize().y + insets.getTop() + insets.getBottom())));

                if (x < w && y < h) {
                    x += w + 1;
                    y += h + 1;
                }

                widget.setLocation(x, y);

            }
        }
    }

    /** <code>setFps</code> 
     * @param fps
     * @see jmetest.widget.viewport.scene.TestAbstractScene#setFps(java.lang.String)
     */
    public void setFps(String fps) {
        // TODO Auto-generated method stub

    }

    /** <code>update</code> 
     * 
     * @see jmetest.widget.viewport.scene.TestAbstractScene#update()
     */
    public void update() {
        // TODO Auto-generated method stub
        
    }

}
