/*
 * Created on Jun 9, 2004
 *
 */
package com.jme.ui;

import com.jme.image.Texture;
import com.jme.input.MouseButtonStateType;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.input.*;

/**
 * @author schustej
 *  
 */
public class UIButton extends UIObject {

	public UIButton(String name, InputHandler inputHandler,
			String upfile, String overfile, String downfile, int x, int y,
			float scale) {
		super(name, inputHandler, x, y, scale);

		_textureStates = new TextureState[3];

		for (int i = 0; i < 3; i++) {
			_textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer()
					.getTextureState();
			_textureStates[i].setEnabled(true);
		}

		_textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(upfile), Texture.MM_NEAREST,
				Texture.FM_NEAREST, true));
		_textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(overfile), Texture.MM_NEAREST,
				Texture.FM_NEAREST, true));
		_textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(downfile), Texture.MM_NEAREST,
				Texture.FM_NEAREST, true));

		_textureStates[0].apply();
		_textureStates[1].apply();
		_textureStates[2].apply();

		this.setRenderState(_textureStates[0]);

		setup();
	}

	public boolean update() {

		boolean retval = false;

		if( hitTest()) {
		    
			if ( _inputHandler.getMouse().getMouseInput().getButtonState()
					.equals(MouseButtonStateType.MOUSE_BUTTON_1)) {
				// button is down
				if (_state != DOWN) {
					this.setRenderState(_textureStates[DOWN]);
					this.updateRenderState();
					_state = DOWN;
				}
			} else {
				// button is up
				if (_state == DOWN) {
					// Fire action. here
					retval = true;
				}
				if (_state != OVER) {
					this.setRenderState(_textureStates[OVER]);
					this.updateRenderState();
					_state = OVER;
				}
			}
		} else {
			if (_state != UP) {
				this.setRenderState(_textureStates[UP]);
				this.updateRenderState();
				_state = UP;
			}
		}
		return retval;
	}
}