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
public class UICheck extends UIObject {

	protected boolean _selected = false;

	public UICheck(String name, InputHandler inputHandler,
			String upfile, String overfile, String downfile, String selectedfile,
			int x, int y, float scale) {
		super(name, inputHandler, x, y, scale);

		_textureStates = new TextureState[4];

		for (int i = 0; i < 4; i++) {
			_textureStates[i] = DisplaySystem.getDisplaySystem().getRenderer()
					.getTextureState();
			_textureStates[i].setEnabled(true);
		}

		_textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(upfile),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		_textureStates[1].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(overfile),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		_textureStates[2].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(downfile),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		_textureStates[3].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource(selectedfile),
				Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true));
		_textureStates[0].apply();
		_textureStates[1].apply();
		_textureStates[2].apply();
		_textureStates[3].apply();
		this.setRenderState(_textureStates[0]);

		setup();
	}

	public boolean update() {

		boolean retval = false;
		
		if (hitTest()) {

			if ( _inputHandler.getMouse().getMouseInput().getButtonState()
					.equals(MouseButtonStateType.MOUSE_BUTTON_1)) {

				// button is down
				if (_state != DOWN) {
					if (!_selected) {
						this.setRenderState(_textureStates[DOWN]);
						this.updateRenderState();
					}
					_state = DOWN;
				}
			} else {
				// button is up
				if (_state == DOWN) {
					// Fire action. here
					if (!_selected) {
						this.setRenderState(_textureStates[3]);
						this.updateRenderState();
						_selected = true;
						_state = OVER;
						retval = true;
					} else {
						_selected = false;
					}
				}
				if (!_selected && _state != OVER) {
					this.setRenderState(_textureStates[OVER]);
					this.updateRenderState();
					_state = OVER;
				}
			}
		} else {
			if (!_selected && _state != UP) {
				this.setRenderState(_textureStates[UP]);
				this.updateRenderState();
				_state = UP;
			}
		}
		return retval;
	}

	public int getState() {
		if( _selected) {
			return SELECTED;
		} else {
			return _state;
		}
	}
}