/*
 * Created on Jul 26, 2004
 *
 */
package com.jme.ui;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

/**
 * @author schustej
 *  
 */
public class UICharacter extends UIObject {

    UICharacter(String name, TextureState ts, float tx, float ty, float tx2, float ty2, float scale) {
        super(name, null, 0, 0, scale);

        _textureStates = new TextureState[1];
        _textureStates[0] = ts;

        setup();

        Vector2f[] texCoords = new Vector2f[4];

        texCoords[0] = new Vector2f(tx, ty2);
        texCoords[1] = new Vector2f(tx, ty);
        texCoords[2] = new Vector2f(tx2, ty);
        texCoords[3] = new Vector2f(tx2, ty2);

        setTextures(texCoords);

        _scale = scale;
    }

    UICharacter(UICharacter tmp) {
        super(tmp.getName(), null, 0, 0, tmp._scale);

        this._textureStates = tmp._textureStates;
        this._width = tmp._width;
        this._height = tmp._height;
        this._x = tmp._x;
        this._y = tmp._y;
        this.renderStateList = tmp.renderStateList;

        setVertices(tmp.getVertices());
        setNormals(tmp.getNormals());
        setColors(tmp.getColors());
        setTextures(tmp.getTextures());
        setIndices(tmp.getIndices());

        setLocalScale(_scale);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    protected void setup() {

        _width = ((TextureState) _textureStates[0]).getTexture().getImage().getWidth() / 16;
        _height = ((TextureState) _textureStates[0]).getTexture().getImage().getHeight() / 16;

        initialize(_width, _height);

        setLocalScale(_scale);

        /*
         * doesn't seem to work right. It ends up being in the wrong place.
         */
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    public boolean update() {
        return false;
    }
}