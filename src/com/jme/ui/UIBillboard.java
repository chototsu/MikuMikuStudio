/*
 * Created on Jun 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.jme.ui;

import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.image.Texture;
import com.jme.util.TextureManager;
import com.jme.math.Vector3f;
/**
 * @author schustej
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UIBillboard extends UIObject {

	public UIBillboard( String name, String imgfile, int x, int y, float scale) {
		super(name, null, x, y, scale);
		
		_textureStates = new TextureState[1];
		
		_textureStates[0] = DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
		_textureStates[0].setEnabled(true);
		
		_textureStates[0].setTexture(TextureManager.loadTexture(UIObject.class
				.getClassLoader().getResource( imgfile),
				Texture.MM_NEAREST, Texture.FM_NEAREST, true));
		_textureStates[0].apply();
		this.setRenderState(_textureStates[0]);

		setup();

	}
	
	public void center() {
		_x = DisplaySystem.getDisplaySystem().getWidth() / 2 - _width / 2;
		_y = DisplaySystem.getDisplaySystem().getHeight() / 2 - _height / 2;
		setLocalScale(_scale);
		setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2,
				0.0f));
	}
	
	public void setSize( int w, int h) {
		_width = w;
		_height = h;
		initialize(_width, _height);
		setLocalScale(_scale);
		setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2,
				0.0f));
	}
	
	public void setLocation( int x, int y) {
	    _x = x;
	    _y = y;
		setLocalTranslation(new Vector3f(_x + _width / 2, _y + _height / 2,
				0.0f));
	}
	
	public void setWrap( int wrap) {
	    _textureStates[0].getTexture().setWrap( wrap);
	}
	
	/* (non-Javadoc)
	 * @see org.resonus.finis.ui.UIObject#update()
	 */
	public boolean update() {
		return false;
	}

}
