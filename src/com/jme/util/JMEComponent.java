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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.jme.system.DisplaySystem;
import java.awt.image.DataBufferInt;

/**
 * <code>JMEComponent</code> is an integration class allowing jME generated
 * graphics to be displayed in a AWT/Swing interface.
 *
 * @author Joshua Slack
 * @version $Id: JMEComponent.java,v 1.5 2004-11-10 01:27:45 renanse Exp $
 */

public class JMEComponent extends Component {

/** The buffer that holds our render results in anticipation of converting it to an image */
	private IntBuffer buf;

/** The image currently displayed in the Component. */
	private BufferedImage img;

/** Temporary storage used when converting the IntBuffer to the BufferedImage. */
	private int[] ibuf;

/** Rate in MS the component refreshes itself.  50ms by default. */
	private int refresh = 50;

/** Width and Height of the OpenGL context.  Note: Not neccesarily the width
	 * and height of the component itself. */
	private int width, height;

/** Whether to scale the OpenGL image to the current dimensions of the Component.
	 * true by default. */
	private boolean scaled = true;

	/**
	 * Main Constructor.  Must be set with the width and height of the underlying
	 * GL context or bad things will happen... (Buffer Over/Underflow exceptions)
	 *
	 * Generally, you create this component after creating the context to allow
	 * dynamic sizing.  eg:
	 *
	 *   frame.comp = new JMEComponent(display.getWidth(), display.getHeight());
	 *
	 * After adding your component to a Swing/AWT container, make sure you either
	 * pack, revalidate, or whatnot.
	 *
	 * @param width gl context width
	 * @param height gl context height
	 */
	public JMEComponent(int width, int height) {
		this.width = width;
		this.height = height;
		buf = ByteBuffer.allocateDirect(width * height * 4)
				.order(ByteOrder.nativeOrder()).asIntBuffer();
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		ibuf = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();

		try {
			new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(refresh);
							repaint();
						} catch (InterruptedException ex) {}
					}
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Get this component's IntBuffer.  Used when OpenGL needs to copy it's
	 * contents to the JMEComponent.
	 * @return IntBuffer
	 */
	public IntBuffer getBuffer() {
		return buf;
	}

	/**
	 * Return true if the Component will paint the OpenGL scene on itself scaled
	 * to it's current size.  False means it will maintain the size as returned
	 * by opengl either cutting it off if the component is too small or showing
	 * the background color on the right and bottom if the component is too big.
	 * @return boolean
	 */
	public boolean isScaled() {
		return scaled;
	}

	/**
	 * Sets the scale param.  See <code>isScaled()</code>
	 * @param shouldScale boolean
	 */
	public void setScaled(boolean shouldScale) {
		scaled = shouldScale;
	}

	/**
	 * Return the refresh rate of this component.  In other words, how much time
	 * (in ms) should pass between forced repaints of the component.
	 * @return refresh rate
	 */
	public int getRefreshRate() {
		return refresh;
	}

	/**
	 * Set the refresh rate.  See <code>getRefreshRate()</code>
	 * @param rate int
	 */
	public void setRefreshRate(int rate) {
		refresh = rate;
	}

	/**
	 * Overriden paint(Graphics) method.  Does not call super method.
	 * Clears the component to the current background color (getBackground()).
	 * It then copies the image data in the IntBuffer to the BufferedImage and
	 * paints it to the component in the upper left, scaled if set to do so.
	 * @param g Graphics
	 */
	public void paint(Graphics g) {
		synchronized(this) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			if (DisplaySystem.getDisplaySystem() != null &&
					DisplaySystem.getDisplaySystem().getRenderer() != null) {
				//Grab pixel information and set it to the BufferedImage info.
				buf.clear();
				for (int x = height; --x >= 0; ) {
					buf.get(ibuf, x * width, width);
				}
				buf.clear();
				if (scaled)
					g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(),
											getBackground(), null);
				else
					g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), getBackground(), null);
			}

			// tell the delegate we want new contents from GL on the next pass.
			HeadlessDelegate.setNeedsRender(this, true);
		}
	}
}
