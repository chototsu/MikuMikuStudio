/*
 * Created on Jun 8, 2004
 *
 */
package com.jme.ui;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.input.*;

/**
 * @author schustej
 *
 */
public abstract class UIObject extends Quad {

	public static final int UP = 0;
	public static final int OVER = 1;
	public static final int DOWN = 2;
	public static final int SELECTED = 3;

	protected int _x = 200;
	protected int _y = 200;
	protected int _width = 0;
	protected int _height = 0;

	protected float _scale = 1.0f;

	protected TextureState[] _textureStates = null;

	protected int _state = UP;

	protected InputHandler _inputHandler = null;

  public UIObject(String name, InputHandler inputHandler, int x, int y, float scale) {
		super(name);
		_inputHandler = inputHandler;
		_x = x;
		_y = y;
		_scale = scale;
	}

	protected void setup() {

		_width = ((TextureState) _textureStates[0]).getTexture().getImage()
				.getWidth();
		_height = ((TextureState) _textureStates[0]).getTexture().getImage()
				.getHeight();

		AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();
		as1.setBlendEnabled(true);
		as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as1.setTestEnabled(true);
		as1.setTestFunction(AlphaState.TF_GREATER);

		this.setRenderState(as1);

		initialize(_width, _height);
		setLocalTranslation(new Vector3f(_x + ( _width * _scale / 2), _y + ( _height * _scale / 2), 0.0f));
		setLocalScale(_scale);

		/*
		 * doesn't seem to work right. It ends up being in the wrong place.
		 */
		setRenderQueueMode(Renderer.QUEUE_ORTHO);
	}

	public abstract boolean update();

	protected boolean hitTest() {
	    Vector3f mouseloc = _inputHandler.getMouse().getHotSpotPosition();
		if (mouseloc.x >= _x
				&& mouseloc.x <= _x + ( _width * _scale)
				&& mouseloc.y + ( _inputHandler.getMouse().getImageHeight()) >= _y
				&& mouseloc.y + ( _inputHandler.getMouse().getImageHeight()) <= _y
						+ ( _height * _scale)) {
			return true;
		} else
			return false;
	}

	public int getState() {
		return _state;
	}

        public int getWidth() {
          return _width;
        }

        public int getHeight() {
          return _height;
        }

        public void centerAt(int x, int y) {
          this.localTranslation.set(x,y,0);
          _x = (int)(x - _width/2);
          _y = (int)(y - _height/2);
        }
}
