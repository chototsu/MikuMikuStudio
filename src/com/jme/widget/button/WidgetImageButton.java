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
package com.jme.widget.button;

import java.io.IOException;
import javax.imageio.ImageIO;

import com.jme.math.Vector2f;
import com.jme.util.TextureManager;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.image.WidgetImage;
import com.jme.widget.layout.WidgetGridLayout;
import com.jme.widget.panel.WidgetPanel;

/**
 * <code>WidgetImageButton</code>
 * @author Joel Schuster
 * @version $Id: WidgetImageButton.java,v 1.2 2004-09-14 21:52:21 mojomonkey Exp $
 */

public class WidgetImageButton extends WidgetPanel {

	private static final long serialVersionUID = 1L;
	protected com.jme.image.Image _imageUp = null;
	protected com.jme.image.Image _imageDown = null;
	protected com.jme.image.Image _imageOver = null;

	protected WidgetImage _image = null;

	protected WidgetButtonStateType _buttonState =
		WidgetButtonStateType.BUTTON_UP;

/**
 * WidgetImageButton contructor. This allows the user to just specify the 
 * up, down and over file locations.  
 * @param upStr the location of the image to be used to the up button state. Based on using the getResource call of ClassLoader
 * @param downStr the location of the image to be used to the down button state. Based on using the getResource call of ClassLoader
 * @param overStr the location of the image to be used to the over button state. Based on using the getResource call of ClassLoader
 * @throws Exception Important: this is the only constructor that throws an IOException,
 *     this is because calling the this constructor with the ImageIO.read within it make it needed since
 *     you can't put a try/catch block around it.
 */
	public WidgetImageButton(String upStr, String downStr, String overStr)
		throws IOException {
		this(
			ImageIO.read(
				WidgetImageButton.class.getClassLoader().getResource(upStr)),
			ImageIO.read(
				WidgetImageButton.class.getClassLoader().getResource(downStr)),
			ImageIO.read(
				WidgetImageButton.class.getClassLoader().getResource(overStr)));
	}

/**
 * Constructor that allows the user to pass in the java.awt.Image reference that need to be used to construct
 * this button. Allows the user more freedom how and were the images are handled.
 * @param imageUp
 * @param imageDown
 * @param imageOver
 */
	public WidgetImageButton(
		java.awt.Image imageUp,
		java.awt.Image imageDown,
		java.awt.Image imageOver) {
		this(
			TextureManager.loadImage(imageUp, true),
			TextureManager.loadImage(imageDown, true),
			TextureManager.loadImage(imageOver, true));
	}

/**
 * Constructor that allows the user to pass in com.jme.image.Image instead of the java.awt.Image reference.
 * @param imageUp
 * @param imageDown
 * @param imageOver
 */

	public WidgetImageButton(
		com.jme.image.Image imageUp,
		com.jme.image.Image imageDown,
		com.jme.image.Image imageOver) {
		super();
		_imageUp = imageUp;
		_imageDown = imageDown;
		_imageOver = imageOver;

		init();
	}
	
	/**
	 * Sets the basic layout and creates the WidgetImage that is going to be used throughout the class
	 *
	 */
	public void init() {
		setLayout(new WidgetGridLayout(1, 1));
		_image =
			new WidgetImage(
				_imageUp,
				WidgetAlignmentType.ALIGN_CENTER,
				WidgetImage.SCALE_MODE_RELATIVE);

		add(_image);
	}

/**
 * Handles when the panel recieves a button up message. If the mouse is still within the widget's boundries then the image will be switched to
 * the over image, otherwise will use the up image.
 */
	public void doMouseButtonUp() {
		if (isMouseInWidget()) {
			_buttonState = WidgetButtonStateType.BUTTON_OVER;
			_image.setImage(_imageOver);
		} else {
			_image.setImage(_imageUp);
		}
		getNotifierMouseButtonUp().notifyObservers(this);
	}

/**
 * Handles the down message and changes the internal WidgeImage's underlying image to the down image.
 */
	public void doMouseButtonDown() {
		_image.setImage(_imageDown);
		_buttonState = WidgetButtonStateType.BUTTON_DOWN;
		getNotifierMouseButtonDown().notifyObservers(this);
	}

/**
 * Checks for the mouse state to be correct when the mouse is in the widget
 * TODO: really need to figure out a better way to handle this, right now because of the overridden handleMouseMove this method
 * get's called for every movement of the mouse.
 * 
 */
	public void doMouseEnter() {
		if (_buttonState != WidgetButtonStateType.BUTTON_OVER) {
			_buttonState = WidgetButtonStateType.BUTTON_OVER;
			_image.setImage(_imageOver);
		}
	}

/**
 * Checks for the mouse to be correct state when mouse is NOT in widget
 * TODO: really need to figure out a better way to handle this, right now because of the overridden handleMouseMove this method
 * get's called for every movement of the mouse.
 */
	public void doMouseExit() {
		if (_buttonState == WidgetButtonStateType.BUTTON_OVER) {
			_buttonState = WidgetButtonStateType.BUTTON_UP;
			_image.setImage(_imageUp);
		}
	}

/**
 * Overridden from WidgetAbstractImpl
 * I don't really know why it's needed, for some reason when using the one in WidgetAbstractImpl the ownership
 * of the mouse is taken by the parent panel or frame and thus the doMouseButtonUp or Down is never called.
 * TODO: need to take a better look at how ownership of the mouse is determined.
 */
	public void handleMouseButtonDown() {
		if (isVisible() == false)
			return;
		boolean b = isMouseInWidget();
		if (b) {
			setMouseOwner(this);
		}
	}

	/**
	 * Overridden from WidgetAbstractImpl
	 * I don't really know why it's needed, for some reason when using the one in WidgetAbstractImpl the ownership
	 * of the mouse is taken by the parent panel or frame and thus the doMouseEnter or Exit is never called.
	 * TODO: need to take a better look at how ownership of the mouse is determined.
	 */
	public void handleMouseMove() {
		if (isVisible() == false)
			return;

		boolean b = isMouseInWidget();

		if (b) {
			setWidgetUnderMouse(this);
			doMouseEnter();
		} else {
			doMouseExit();
		}
	}

/**
 * Needed by the AbsoluteLayout to figure out how big to draw it. As with this entire widget, the image size determines widget size. This
 * required a change in the WidgetImage to be able to get the Preferred size correctly.
 */
	public Vector2f getPreferredSize() {
		return _image.getPreferredSize();
	}
}

/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2004/05/27 02:08:46  guurk
 * New widget that is based on using three interchangeable images to
 * react to the input of a mouse.
 *
 * Initial Checkin
 *
 */
