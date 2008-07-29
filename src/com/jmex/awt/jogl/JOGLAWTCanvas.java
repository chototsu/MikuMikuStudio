/*
 * Copyright (c) 2008 SRA International, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.awt.jogl;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import com.jme.input.InputSystem;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.jogl.JOGLRenderer;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.JMECanvasImplementor;
import com.jme.system.jogl.JOGLDisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * @author Steve Vaughan
 */
public class JOGLAWTCanvas extends GLCanvas implements JMECanvas,
        GLEventListener {

    /**
     * Generated serial version UID.
     */
    private static final long serialVersionUID = 8527577398527578928L;

    /**
     * The name of this class, used for logging.
     */
    private static final String CLASSNAME = JOGLAWTCanvas.class.getName();

    /**
     * Instance logger.
     */
    private static final Logger logger = Logger.getLogger(CLASSNAME);

    private JOGLRenderer renderer;

    // TODO Remove the public modifier, making canvas requests go through the
    // Canvas Constructor.
    public JOGLAWTCanvas(final GLCapabilities caps) {
        super(caps);
    }

    /* Canvas ------------------------------------------------------------- */

    @Override
    public void setBackground(final Color c) {
        // TODO Fix logging
        logger.info("setBackground " + c);

        // FIXME Background color may be null.
        if (renderer == null) {
            super.setBackground(c);
        } else {
            final ColorRGBA color = new ColorRGBA(c.getRed(), c.getGreen(), c
                    .getBlue(), c.getAlpha());
            renderer.setBackgroundColor(color);
        }
    }

    @Override
    public Color getBackground() {
        // TODO Fix logging
        logger.info("getBackground");

        if (renderer == null) {
            return super.getBackground();
        } else {
            final ColorRGBA color = renderer.getBackgroundColor();
            return new Color(color.r, color.g, color.b, color.a);
        }
    }

    /* JMECanvas ---------------------------------------------------------- */

    /**
     * @see #setImplementor(JMECanvasImplementor)
     */
    private JMECanvasImplementor impl;

    public void setImplementor(final JMECanvasImplementor impl) {
        // TODO Fix logging
        logger.info("setImplementor " + impl);

        this.impl = impl;
    }

    public void setVSync(boolean sync) {
        // TODO Auto-generated method stub
        logger.info("setVSync " + sync);
    }

    public void setUpdateInput(boolean doUpdate) {
        // TODO Auto-generated method stub
        logger.info("setUpdateInput " + doUpdate);
    }

    public boolean doUpdateInput() {
        // TODO Auto-generated method stub
        logger.info("doUpdateInput");
        return false;
    }

    /* GLEventListener Methods -------------------------------------------- */

    public void init(final GLAutoDrawable drawable) {
        logger.info("init " + drawable);

        // Switching the context is not necessary, since this is handled by the
        // GLEventListener.

        // Complete canvas initialization.
        JOGLDisplaySystem display = (JOGLDisplaySystem) DisplaySystem
                .getDisplaySystem();
        display.initForCanvas(display.getWidth(), display.getHeight());
        // FIXME Either use the DisplaySystem, or use the canvas for renderer.
        renderer = (JOGLRenderer) DisplaySystem.getDisplaySystem()
                .getRenderer();

        // Perform game initialization.
        impl.doSetup();
    }

    public void display(final GLAutoDrawable drawable) {
        if (logger.isLoggable(Level.FINER))
            logger.entering(CLASSNAME, "display", drawable);

        // Switching the context is not necessary, since this is handled by the
        // GLEventListener.

        // FIXME Check with the input system.
        // if (updateInput)
        InputSystem.update();

        // Perform updates, queued updates first.
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE)
                .execute();
        impl.doUpdate();

        // Perform rendering, queued rendering first.
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER)
                .execute();
        impl.doRender();
    }

    public void displayChanged(final GLAutoDrawable drawable,
            final boolean modeChanged, final boolean deviceChanged) {
        // FIXME Currently just logs the changes.
        logger.info("displayChanged " + drawable + ", " + modeChanged + ", "
                + deviceChanged);
    }

    public void reshape(final GLAutoDrawable drawable, final int x,
            final int y, final int width, final int height) {
        logger.info("reshape " + drawable + ", " + x + ", " + y + ", " + width
                + ", " + height);

        renderer.reinit(width, height);
    }

}
