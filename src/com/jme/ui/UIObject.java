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
import com.jme.input.action.*;
import java.util.*;

/**
 * @author schustej
 *
 */
public abstract class UIObject extends Quad {

    /*
     * Base states for a UIObject, extentions may add to these or
     * change, or even not even use
     */
    public static final int UP = 0;
    public static final int OVER = 1;
    public static final int DOWN = 2;
    public static final int SELECTED = 3;

    /*
     * just some defaults
     */
    protected int _x = 200;
    protected int _y = 200;
    protected int _width = 0;
    protected int _height = 0;

    protected float _xscale = 1.0f;
    protected float _yscale = 1.0f;

    protected int _state = UP;

    protected TextureState[] _textureStates = null;

    protected InputHandler _inputHandler = null;

    protected UIActiveArea _hitArea = null;

    protected Vector _actions = null;

    /**
     * Constructor.
     * @param name
     * @param inputHandler
     * @param x
     * @param y
     * @param scale
     */
    public UIObject(String name, InputHandler inputHandler, int x, int y, float scale) {
        super(name);
        _inputHandler = inputHandler;
        _x = x;
        _y = y;
        _xscale = scale;
        _yscale = scale;
        _actions = new Vector();
    }

    /**
     * Constructor.
     * @param name
     * @param inputHandler
     * @param x
     * @param y
     * @param scale
     */
    public UIObject(String name, InputHandler inputHandler, int x, int y, float xscale, float yscale) {
        super(name);
        _inputHandler = inputHandler;
        _x = x;
        _y = y;
        _xscale = xscale;
        _yscale = yscale;
        _actions = new Vector();
    }

    /**
     * This is used in all extention constructors.
     *
     * Note that a Quad's vertexes are based on the center of the quad, so we have to
     * account for that in the translations. However, the hitarea is from the lower left corner.
     *
     */
    protected void setup() {

        _width = ((TextureState) _textureStates[0]).getTexture().getImage().getWidth();
        _height = ((TextureState) _textureStates[0]).getTexture().getImage().getHeight();

        AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as1.setBlendEnabled(true);
        as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as1.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        as1.setTestEnabled(true);
        as1.setTestFunction(AlphaState.TF_GREATER);

        this.setRenderState(as1);

        initialize(_width, _height);
        setLocalTranslation(new Vector3f(_x + (_width * _xscale * .5f), _y + (_height * _yscale * .5f), 0.0f));
        getLocalScale().x = _xscale;
        getLocalScale().y = _yscale;

        _hitArea = new UIActiveArea(_x, _y, (int) (_width * _xscale), (int) (_height * _yscale), _inputHandler);

        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * Abstract, must be implemented in the extentions
     * @return
     */
    public abstract boolean update();

    /**
     * Checks the hitarea for the mouse location
     * @return
     */
    protected boolean hitTest() {
        return _hitArea.hitTest();
    }

    /**
     * returns the current state of the control
     * @return
     */
    public int getState() {
        return _state;
    }

    /**
     * gets the width of the control, before scaling
     * @return
     */
    public int getWidth() {
        return _width;
    }

    /**
     * gets the height of the control, before scaling
     * @return
     */
    public int getHeight() {
        return _height;
    }

    /**
     * Places the center of the control at the given screen location
     * @return
     */
    public void centerAt(int x, int y) {
        this.localTranslation.set(x, y, 0);

        _x = (int) (x - _width / 2);
        _y = (int) (y - _height / 2);

        _hitArea._x = _x;
        _hitArea._y = _y;
    }

    /**
     * Add an inputaction that will be fired when
     * the control changes state
     * @param action
     */
    public void addAction( UIInputAction action) {
        _actions.add( action);
    }

    /**
     * removes a given input action
     * @param action
     */
    public void removeAction( UIInputAction action) {
        _actions.remove( action);
    }

    protected void fireActions() {
        Iterator actionIter = _actions.iterator();
		while (actionIter.hasNext()) {
			((UIInputAction) actionIter.next()).performAction( this);
		}
    }

    /**
     * After loading the image this method may be called to resize the image to
     * any on-screen size. Note that this WILL cause distortion of the images.
     *
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        _width = w;
        _height = h;
        initialize(_width, _height);
        getLocalScale().x = _xscale;
        getLocalScale().y = _yscale;
        setLocalTranslation(new Vector3f(_x + _width>>1, _y + _height>>1, 0.0f));
    }


    /**
     * After loading the image this method may be called to rescale the image.
     * Note that this WILL cause distortion of the image.
     *
     * @param xscale
     * @param yscale
     */
    public void setScale(float xscale, float yscale) {
        _xscale = xscale;
        _yscale = yscale;
        getLocalScale().x = _xscale;
        getLocalScale().y = _yscale;
        setLocalTranslation(new Vector3f(_x + _width>>1, _y + _height>>1, 0.0f));
    }

    /**
     * Moves the quad around the screen, properly setting local params at the
     * same time.
     */
    public void setLocation(int x, int y) {
      _x = x;
      _y = y;
      setLocalTranslation(new Vector3f(_x + _width>>1, _y + _height>>1,
                                       0.0f));
      _hitArea._x = _x;
      _hitArea._y = _y;
    }


}
