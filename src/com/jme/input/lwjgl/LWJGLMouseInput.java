/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package com.jme.input.lwjgl;

import java.net.URL;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.system.lwjgl.LWJGLStandardCursor;
import com.jme.util.TextureManager;

/**
 * <code>LWJGLMouseInput</code> handles mouse input via the LWJGL Input API.
 *
 * @author Mark Powell
 * @version $Id: LWJGLMouseInput.java,v 1.23 2007-08-02 21:41:09 nca Exp $
 */
public class LWJGLMouseInput extends MouseInput {
    private static final Logger logger = Logger.getLogger(LWJGLMouseInput.class.getName());

    private static Hashtable<URL, Cursor> loadedCursors;

	private int dx, dy;
	private int lastX, lastY;
	private boolean virgin = true;
	private int dWheel;
	private int wheelRotation;

	/**
	 * Constructor creates a new <code>LWJGLMouseInput</code> object. A call
	 * to the LWJGL creation method is made, if any problems occur during
	 * this creation, it is logged.
	 *
	 */
	protected LWJGLMouseInput() {
		try {
			Mouse.create();
			setCursorVisible(false);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Problem during creation of Mouse.", e);
		}
	}

	/**
	 * <code>destroy</code> cleans up the native mouse reference.
	 * @see com.jme.input.MouseInput#destroy()
	 */
	public void destroy() {
		setCursorVisible(false);
		Mouse.destroy();

	}

	/**
	 * <code>getButtonIndex</code> returns the index of a given button name.
	 * @see com.jme.input.MouseInput#getButtonIndex(java.lang.String)
	 */
	public int getButtonIndex(String buttonName) {
		return Mouse.getButtonIndex(buttonName);
	}

	/**
	 * <code>getButtonName</code> returns the name of a given button index.
	 * @see com.jme.input.MouseInput#getButtonName(int)
	 */
	public String getButtonName(int buttonIndex) {
		return Mouse.getButtonName(buttonIndex);
	}

	/**
	 * <code>isButtonDown</code> tests if a given button is pressed or not.
	 * @see com.jme.input.MouseInput#isButtonDown(int)
	 */
	public boolean isButtonDown(int buttonCode) {
		return Mouse.isButtonDown(buttonCode);
	}

	/**
	 * <code>getWheelDelta</code> retrieves the change of the mouse wheel,
	 * if any.
	 * @see com.jme.input.MouseInput#getWheelDelta()
	 */
	public int getWheelDelta() {
		return dWheel;
	}
	/**
	 * <code>getXDelta</code> retrieves the change of the x position, if any.
	 * @see com.jme.input.MouseInput#getXDelta()
	 */
	public int getXDelta() {
		return dx;
	}
	/**
	 * <code>getYDelta</code> retrieves the change of the y position, if any.
	 * @see com.jme.input.MouseInput#getYDelta()
	 */
	public int getYDelta() {
		return dy;
	}

	/**
	 * <code>getXAbsolute</code> gets the absolute x axis value.
	 * @see com.jme.input.MouseInput#getXAbsolute()
	 */
	public int getXAbsolute() {
		return Mouse.getX();
	}

	/**
	 * <code>getYAbsolute</code> gets the absolute y axis value.
	 * @see com.jme.input.MouseInput#getYAbsolute()
	 */
	public int getYAbsolute() {
		return Mouse.getY();
	}

	/**
	 * <code>updateState</code> updates the mouse state.
	 * @see com.jme.input.MouseInput#update()
	 */
	public void update() {
		/**Actual polling is done in {@link org.lwjgl.opengl.Display#update()} */

		boolean grabbed = Mouse.isGrabbed();
		int x;
		int y;
		if ( grabbed ) {
			dx = Mouse.getDX();
			dy = Mouse.getDY();
			x = Mouse.getX();
			y = Mouse.getY();
		} else {
			x = Mouse.getEventX();
			y = Mouse.getEventY();
			dx = x - lastX;
			dy = y - lastY;
			lastX = x;
			lastY = y;
		}
		if (virgin && (dx != 0 || dy != 0)) {
			dx = dy = 0;
			wheelRotation = 0;
			virgin = false;
		}
		dWheel = Mouse.getDWheel();
		wheelRotation += dWheel;


		if ( listeners != null && listeners.size() > 0 ) {
			while ( Mouse.next() ) {
				int button = Mouse.getEventButton();
				boolean pressed = button >= 0 && Mouse.getEventButtonState();

				int wheelDelta = Mouse.getEventDWheel();

				int xDelta = Mouse.getEventDX();
				int yDelta = Mouse.getEventDY();

				for ( int i = 0; i < listeners.size(); i++ ) {
					MouseInputListener listener = listeners.get( i );
					if ( button >= 0 )
					{
						listener.onButton( button,  pressed, x, y );
					}
					if ( wheelDelta != 0 )
					{
						listener.onWheel( wheelDelta, x, y );
					}
					if ( xDelta != 0 || yDelta != 0 )
					{
						listener.onMove( xDelta, yDelta, x, y );
					}
				}
			}
		}
		else {
			// clear events - could use a faster method in lwjgl here...
			while ( Mouse.next() ) {
				//nothing
			}
		}
	}


	/**
	 * <code>setCursorVisible</code> sets the visiblity of the hardware cursor.
	 * @see com.jme.input.MouseInput#setCursorVisible(boolean)
	 */
	public void setCursorVisible(boolean v) {
	  Mouse.setGrabbed(!v);
		try {

			if (v) {
				Mouse.setNativeCursor(LWJGLStandardCursor.cursor);
			} else {
				Mouse.setNativeCursor(null);
			}

		} catch (Exception e) {
			logger.warning("Problem showing mouse cursor.");
		}
	}

	/**
	 * <code>isCursorVisible</code> Returns true if a cursor is currently bound.
	 * @see com.jme.input.MouseInput#isCursorVisible()
	 */
	public boolean isCursorVisible() {
		return Mouse.getNativeCursor() != null;
	}

	public void setHardwareCursor(URL file) {
		setHardwareCursor(file, -1, -1);
	}

	public void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
		Mouse.setGrabbed(false);

		if (loadedCursors == null) {
			loadedCursors = new Hashtable<URL, Cursor>();
		}

		org.lwjgl.input.Cursor cursor = null;
		if (loadedCursors.containsKey(file)) {
			cursor = loadedCursors.get(file);
		}
		else {
			com.jme.image.Image image = TextureManager.loadImage(file, true);
			IntBuffer imageData = image.getData().asIntBuffer();
			IntBuffer imageDataCopy = BufferUtils.createIntBuffer(imageData.remaining());

			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			for (int y = 0; y < imageHeight; y++) {
				for (int x = 0; x < imageWidth; x++) {
					int index = y * imageWidth + x;

					int pixel = imageData.get(index);
					int a = (pixel >> 24) & 0xff;
					if (a < 0x7f) {
						a = 0x00;
					}
					else {
						a = 0xff;
					}
					int b = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int r = (pixel) & 0xff;

					imageDataCopy.put(index, (a << 24) | (r << 16) | (g << 8) | b);
				}
			}

			if (xHotspot < 0 || yHotspot < 0 || xHotspot >= imageWidth || yHotspot >= imageHeight) {
				//Revert to a hotspot position of top-left
				xHotspot = 0;
				yHotspot = imageHeight - 1;

				logger.warning("Hotspot positions are outside image bounds!");
			}

			try {
				cursor = new Cursor(imageWidth, imageHeight, xHotspot, yHotspot, 1, imageDataCopy, null);
			} catch (LWJGLException e) {
				logger.log(Level.WARNING, "Failed creating native cursor!", e);
			}

			loadedCursors.put(file, cursor);
		}
		try {
			org.lwjgl.input.Mouse.setNativeCursor(cursor);
		} catch (LWJGLException e) {
			logger.log(Level.WARNING, "Failed setting native cursor!", e);
		}
	}

	public int getWheelRotation() {
		return wheelRotation;
	}

	public int getButtonCount() {
		return Mouse.getButtonCount();
	}

	public void setCursorPosition( int x, int y) {
		Mouse.setCursorPosition( x, y);
	}
}
