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
package com.jme.widget.text;


import com.jme.math.Vector2f;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.widget.WidgetAlignmentType;
import com.jme.widget.WidgetAbstractImpl;
import com.jme.widget.font.WidgetFont;
import com.jme.widget.font.WidgetFontManager;
import com.jme.widget.renderer.WidgetRendererFactory;
import java.util.Stack;
import com.jme.scene.state.RenderState;
import com.jme.scene.Spatial;


/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WidgetText extends WidgetAbstractImpl {

    private final static String FONT_NAME = "Default";

    protected TextureState textureState;
    protected AlphaState alphaState;

    private String text = "";
    private float scale = 1f;

    private WidgetFont font;

    RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

    public WidgetText() {
        this("", WidgetAlignmentType.ALIGN_NONE);
    }

    public WidgetText(String text) {
        this(text, WidgetAlignmentType.ALIGN_NONE);
    }

    public WidgetText(String text, WidgetAlignmentType alignment) {
        super();

        textureState = DisplaySystem.getDisplaySystem().getRenderer().getTextureState();
        alphaState = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();

        setText(text);
        setAlignment(alignment);

        alphaState.setBlendEnabled(true);
        alphaState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        alphaState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);

        setRenderState(alphaState);
        setRenderState(textureState);
    }

    public String toString() {
        return "[" + text + super.toString() + "]";
    }

    public String getText() {
        return text;
    }

    public void setText(String string) {
        text = string;

        if (text != null) {

            textureState.setEnabled(true);
            alphaState.setEnabled(true);

            font = WidgetFontManager.getFont(getFontName());

            Vector2f size = font.getStringSize(text);

            localBound.setSize(size);

            preferredSize.x = size.x;
            preferredSize.y = size.y;

            textureState.setTexture(font.getTexture());

        } else {
            textureState.setEnabled(false);
            alphaState.setEnabled(false);
            setSize(0, 0);
        }
    }

    /* (non-Javadoc)
     * @see com.jme.scene.Spatial#onDraw(com.jme.renderer.Renderer)
     */
    public void onDraw(Renderer r) {

        font = WidgetFontManager.getFont(getFontName());
        textureState.setTexture(font.getTexture());

        super.onDraw(r);
    }

    /**
     *
     * <code>setStates</code> applies all the render states for this
     * particular geometry.
     *
     */
    public void applyStates() {
      if (parent != null)
        Spatial.clearCurrentStates();
      for (int i = 0; i < states.length; i++) {
        if (states[i] != currentStates[i]) {
          states[i].apply();
          currentStates[i] = states[i];
        }
      }
    }

    public void draw(Renderer r) {
      applyStates();
        r.draw(getWidgetRenderer());
    }

    public void drawBounds(Renderer r) {
        //ignore
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float f) {
        scale = f;
    }

    public void doMouseButtonDown() {
    }

    public void doMouseButtonUp() {
    }

    public void setSize(Vector2f size) {
        updateWorldBound();
    }

    public void setSize(int width, int height) {
        updateWorldBound();
    }

    public void setPreferredSize(Vector2f size) {
    }

    public String getFontName() {
        return FONT_NAME;
    }

    public void setLocation(int x, int y) {
        float w = localBound.getWidth();
        float h = localBound.getHeight();

        super.setLocation(x, y);

        localBound.setWidthHeight(w, h);
        updateWorldBound();
    }

    public void setLocation(Vector2f at) {
        float w = localBound.getWidth();
        float h = localBound.getHeight();

        super.setLocation(at);

        localBound.setWidthHeight(w, h);
        updateWorldBound();
    }

    public void setX(int x) {
        float w = localBound.getWidth();
        super.setX(x);
        localBound.setWidth(w);
        updateWorldBound();
    }

    public void setY(int y) {
        float h = localBound.getHeight();
        super.setY(y);
        localBound.setHeight(h);
        updateWorldBound();
    }

    public WidgetFont getFont() {
        return font;
    }

    public void setFont(WidgetFont font) {
        this.font = font;
    }

    /** <code>initWidgetRenderer</code>
     *
     * @see com.jme.widget.Widget#initWidgetRenderer()
     */
    public void initWidgetRenderer() {
        setWidgetRenderer(WidgetRendererFactory.getFactory().getRenderer(this));
    }

    protected void applyRenderState(Stack[] states) {
      for (int x = 0; x < states.length; x++) {
        if (states[x].size() > 0) {
          this.states[x] = ((RenderState) states[x].peek()).extract(states[x], this);
        } else {
          this.states[x] = (RenderState) defaultStateList[x];
        }
      }
    }

}
