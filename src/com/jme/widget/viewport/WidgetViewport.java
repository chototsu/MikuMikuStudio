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
package com.jme.widget.viewport;

import com.jme.renderer.Camera;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.widget.WidgetInsets;
import com.jme.widget.bounds.WidgetViewRectangle;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.panel.WidgetPanel;

/**
 * <code>WidgetViewport</code>
 * @author Gregg Patton
 * @version $Id: WidgetViewport.java,v 1.3 2004-09-14 21:52:17 mojomonkey Exp $
 */
public class WidgetViewport extends WidgetPanel {

    private static final long serialVersionUID = 1L;
	protected WidgetViewportCameraController cameraController;
    private Spatial child;

    public WidgetViewport() {
        super();
    }

    public WidgetViewport(WidgetViewportCameraController cameraController) {
        super();

        setCameraController(cameraController);

        init();

    }

    private void init() {

        setLayout(new WidgetGridLayout(1, 1));

        setBgColor(null);

    }

    protected void setCameraFrustum(WidgetViewRectangle v) {
        WidgetInsets insets = getInsets();

        float aspect =
            (v.getWidth() - (insets.getLeft() + insets.getRight()))
                / (v.getHeight() - (insets.getBottom() + insets.getTop()));

        float top = 1f;
        float bottom = -top;
        float right = top * aspect;
        float left = -right;

        cameraController.camera.setFrustum(1.0f, 1000.0f, left, right, top, bottom);
    }

    /** <code>setViewRectangle</code>
     * @param viewport
     * @see com.jme.widget.Widget#setViewRectangle(com.jme.widget.bounds.WidgetViewRectangle)
     */
    public void setViewRectangle(WidgetViewRectangle v) {

        WidgetInsets insets = getInsets();

        if (cameraController != null) {
            cameraController.camera.setViewPort(
                v.getMinX() + insets.getLeft(),
                v.getMaxX() - insets.getRight(),
                v.getMinY() + insets.getBottom(),
                v.getMaxY() - insets.getTop());

            setCameraFrustum(v);
        }

        super.setViewRectangle(v);
    }

    /** <code>onDraw</code>
     * @param r
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {

        Camera holdCamera = r.getCamera();

        if (cameraController != null) {
            r.setCamera(cameraController.camera);
            cameraController.camera.update();
        }

        super.onDraw(r);

        if (cameraController != null) {

            r.setCamera(holdCamera);

            if (holdCamera != null) {
                holdCamera.update();
            }

        }
    }

    /** <code>attachChild</code>
     * @param child
     * @return
     * @see com.jme.scene.Node#attachChild(com.jme.scene.Spatial)
     */
    public int attachChild(Spatial child) {

        if (this.child != null) {
            detachChild(this.child);
        }

        int ret = super.attachChild(child);

        this.child = child;

        return ret;
    }

    /**
     * <code>getChild</code>
     * @return
     */
    public Spatial getChild() {
        return child;
    }

    public void doMouseMove() {
        super.doMouseMove();
    }

    /**
     * <code>getCameraController</code>
     * @return
     */
    public WidgetViewportCameraController getCameraController() {
        return cameraController;
    }

    /**
     * <code>setCameraController</code>
     * @param controller
     */
    public void setCameraController(WidgetViewportCameraController controller) {
        cameraController = controller;
    }

}
