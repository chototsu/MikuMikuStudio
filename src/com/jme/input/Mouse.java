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

/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a
 *                      WidgetViewport to update an AbstractInputHandler
 *                      without polling the mouse.  GOP
 */

package com.jme.input;

import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

/**
 * <code>Mouse</code> defines a node that handles the rendering and updating
 * of a mouse input device. If a cursor is set, this cursor is desplayed in
 * the position defined by the device.
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: Mouse.java,v 1.8 2004-06-04 00:41:42 renanse Exp $
 */
public abstract class Mouse extends Geometry {
	/**
	 * the input device.
	 */
	protected MouseInput mouse;
	/**
	 * the cursor's texture.
	 */
	protected TextureState cursor;

	protected boolean hasCursor = false;

	protected int imageWidth, imageHeight;

	protected float _speed = 1.0f;

	/**
	 * Constructor creates a new <code>Mouse</code> object.
	 * @param name the name of the scene element. This is required for identification and
	 * 		comparision purposes.
	 */
	public Mouse(String name) {
		super(name);
		setForceView(true);
	}

	/**
	 *
	 * <code>setRenderState</code> sets a render state for this node. Note,
	 * there can only be one render state per type per node. That is, there
	 * can only be a single AlphaState a single TextureState, etc. If there
	 * is already a render state for a type set the old render state will
	 * be rendered. Otherwise, null is returned.
	 * @param rs the render state to add.
	 * @return the old render state.
	 */
	public RenderState setRenderState(RenderState rs) {
		if (rs.getType() == RenderState.RS_TEXTURE) {
			hasCursor = true;
			imageHeight =
				((TextureState) rs).getTexture().getImage().getHeight();
			imageWidth = ((TextureState) rs).getTexture().getImage().getWidth();
		}
                return super.setRenderState(rs);
	}

	/**
	 *
	 * <code>getImageHeight</code> retrieves the height of the cursor image.
	 * @return the height of the cursor image.
	 */
	public int getImageHeight() {
		return imageHeight;
	}

	/**
	 *
	 * <code>getImageWidth</code> retrieves the width of the cursor image.
	 * @return the width of the cursor image.
	 */
	public int getImageWidth() {
		return imageWidth;
	}

	/**
	 *
	 * <code>hasCursor</code> returns true if there is a texture associated
	 * with the mouse.
	 * @return true if there is a texture for the mouse, false otherwise.
	 */
	public boolean hasCursor() {
		return hasCursor;
	}

	/**
	 *
	 * <code>setMouseInput</code> sets the input device for the mouse.
	 * @param mouse the input device for the mouse.
	 */
	public void setMouseInput(MouseInput mouse) {
		this.mouse = mouse;
	}

	/**
	 *
	 * <code>getMouseInput</code> retrieves the input device for the mouse.
	 * @return the input device for the mouse.
	 */
	public MouseInput getMouseInput() {
		return mouse;
	}

	/**
	 * <code>draw</code> renders the mouse cursor using the supplied
	 * renderer.
	 * @param r the renderer to use to display the mouse.
	 */
	public void draw(Renderer r) {
		super.draw(r);
		r.draw(this);
	}

	/**
	 * <code>drawBounds</code> calls super to set the render state then passes itself
	 * to the renderer.
	 * @param r the renderer to display
	 */
	public void drawBounds(Renderer r) {
		r.drawBounds(this);
	}

	/**
	 *
	 * <code>update</code> updates the mouse input object.
	 *
	 */
	public abstract void update();

	/**
	 * <code>update</code> updates the mouse input object.
	 * @param updateState indicates if the mouse's state should be updated
	 */
	public abstract void update(boolean updateState);

	/**
	 * Sets the speed multiplier for updating the cursor position
	 * @param speed
	 */
	public void setSpeed(float speed) {
		_speed = speed;
	}
}
