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

package com.jme.util;

import java.awt.Graphics;

import com.jme.system.DisplaySystem;
import com.jme.util.HeadlessDelegate;
import com.jme.util.JMEComponent;

/**
 * <code>JMEHiddenComponent</code> is an integration class allowing jME generated
 * graphics to be rendered and stored in this component for retrieval by other 
 * processes.
 *
 * @author Joshua Slack
 * @version $Id: JMEHiddenComponent.java,v 1.2 2005-03-30 20:18:05 renanse Exp $
 */

public class JMEHiddenComponent extends JMEComponent {
    private static final long serialVersionUID = 1L;

	/**
	 * Main Constructor.  Must be set with the width and height of the underlying
	 * GL context or bad things will happen... (Buffer Over/Underflow exceptions)
	 *
	 * @param width gl context width
	 * @param height gl context height
	 */
	public JMEHiddenComponent(int width, int height) {
		super(width, height);
	}
	
    protected void setupPaintThread() {
        try {
			new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(refresh);
							paint(null);
						} catch (InterruptedException ex) {}
					}
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

	/**
	 * Overriden paint(Graphics) method.  Does not call super method.
	 * Copies the image data in the IntBuffer to the BufferedImage.
	 * @param g Graphics
	 */
	public void paint(Graphics g) {
		synchronized(this) {
			if (DisplaySystem.getDisplaySystem() != null &&
					DisplaySystem.getDisplaySystem().getRenderer() != null) {
				buf.clear(); // Note: clear() resets marks and positions, 
							 //       but not data in buffer.
				//Grab pixel information and set it to the BufferedImage info.
				for (int x = height; --x >= 0; ) {
					buf.get(ibuf, x * width, width);
				}
				buf.clear();
			}

			// tell the delegate we want new contents from GL on the next pass.
			HeadlessDelegate.setNeedsRender(this, true);
		}
	}
}
