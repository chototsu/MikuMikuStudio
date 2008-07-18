/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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

package com.jmex.swt.lwjgl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.input.InputSystem;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.JMECanvasImplementor;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * <code>LWJGLCanvas</code>
 * 
 * @author Joshua Slack
 */
public class LWJGLSWTCanvas extends GLCanvas implements JMECanvas {
    private static final Logger logger = Logger.getLogger(LWJGLSWTCanvas.class
            .getName());

    private static final long serialVersionUID = 1L;

    private JMECanvasImplementor impl;

    private boolean updateInput = false;

	private boolean vysnc = false;
	
	private boolean inited = false;

	private Runnable renderRunner;

    public LWJGLSWTCanvas(Composite parent, int style, GLData data) throws LWJGLException {
        super(parent, style, data);
        renderRunner = new Runnable() {
        	public void run() {
        		if (!inited) init();
        		render();
        	}
        };
    }

    public void setVSync(boolean sync) {
        this.vysnc  = sync;
    }

    public void setImplementor(JMECanvasImplementor impl) {
        this.impl = impl;
    }

    public void init() {
        try {
            ((LWJGLDisplaySystem)DisplaySystem.getDisplaySystem()).switchContext(this);
            setCurrent();
			GLContext.useContext(this);
    
            impl.doSetup();
    
            if (DisplaySystem.getDisplaySystem().getMinSamples() != 0
                    && GLContext.getCapabilities().GL_ARB_multisample) {
                GL11.glEnable(ARBMultisample.GL_MULTISAMPLE_ARB);
            }
            
            inited = true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in initGL()", e);
        }
    }
    
    public void render() {
		if (!isDisposed()) {
			setCurrent();
			try {
				((LWJGLDisplaySystem) DisplaySystem.getDisplaySystem())
						.switchContext(this);
				GLContext.useContext(this);

				if (updateInput)
					InputSystem.update();

				GameTaskQueueManager.getManager()
						.getQueue(GameTaskQueue.UPDATE).execute();

				impl.doUpdate();

				GameTaskQueueManager.getManager()
						.getQueue(GameTaskQueue.RENDER).execute();

				impl.doRender();

				swapBuffers();
				if (vysnc) {
					getDisplay().timerExec(0, renderRunner);
				} else {
					getDisplay().asyncExec(renderRunner);
				}
			} catch (LWJGLException e) {
				logger.log(Level.SEVERE, "Exception in paintGL()", e);
			}
		}
	}

    public boolean doUpdateInput() {
        return updateInput;
    }

    public void setUpdateInput(boolean doUpdate) {
        updateInput = doUpdate;
    }
}
