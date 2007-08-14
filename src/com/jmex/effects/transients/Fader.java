/*
 * Copyright (c) 2003-2006 jMonkeyEngine
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

/*
 * Created: Jun 12, 2006
 */
package com.jmex.effects.transients;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

/**
 * A <code>Fader</code> can be added to a scene directly as it extends Quad
 * and simply does what it says, provides the ability to fade in and out via
 * a color and opacity.
 * 
 * @author Matthew D. Hicks
 */
public class Fader extends Quad {
	private static final long serialVersionUID = 49342555401922808L;
	
	public static final int DISABLED = 0;
    public static final int FADE_IN = 1;
    public static final int FADE_OUT = 2;
	
	private float fadeTimeInSeconds;
	private ColorRGBA color;
	private AlphaState alphaState;
	private Controller fadeController;
	private boolean ignoreUntilStable;
    private int mode;
    private float alpha;
	
    /**
     * If width and height both equal 0 or less the width and height will be defined to take up the entire screen.
     * 
     * @param name
     * @param width
     * @param height
     * @param color
     * @param fadeTimeInSeconds
     */
	public Fader(String name, float width, float height, ColorRGBA color, float fadeTimeInSeconds) {
		super(
				name,
				width <= 0.0f ? DisplaySystem.getDisplaySystem().getWidth() : width,
				height <= 0.0f ? DisplaySystem.getDisplaySystem().getHeight() : height
			  );
		this.color = color;
		this.fadeTimeInSeconds = fadeTimeInSeconds;
		initQuad();
		initAlphaState();
		initController();
	}
	
	private void initQuad() {
        getLocalRotation().set(0.0f, 0.0f, 0.0f, 1.0f);
        getLocalTranslation().set(DisplaySystem.getDisplaySystem().getWidth() / 2, DisplaySystem.getDisplaySystem().getHeight() / 2, 0.0f);
        getLocalScale().set(1.0f, 1.0f, 1.0f);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setLightCombineMode(LightState.OFF);
        setColorBuffer(0, null);
        if (color == null) color = new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f);
        setDefaultColor(color);
    }
	
	private void initAlphaState() {
        alphaState = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        alphaState.setBlendEnabled(true);
        alphaState.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        alphaState.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        alphaState.setTestEnabled(true);
        alphaState.setTestFunction(AlphaState.TF_GREATER);
        alphaState.setEnabled(true);
        setRenderState(alphaState);
    }
	
	private void initController() {
        fadeController = new Controller() {
            private static final long serialVersionUID = 1L;

            public void update(float time) {
                // Fix for lagged startups
                if (ignoreUntilStable) {
                    if (time < 0.1f) {
                        ignoreUntilStable = false;
                    }
                    return;
                }
                if ((mode == FADE_IN) && (alpha > 0.0f)) {
                    alpha -= 1 / (fadeTimeInSeconds / time);
                    if (alpha < 0.0f) alpha = 0.0f;
                    color.a = alpha;
                } else if ((mode == FADE_OUT) && (alpha < 1.0f)) {
                    alpha += 1 / (fadeTimeInSeconds / time);
                    if (alpha > 1.0f) alpha = 1.0f;
                    color.a = alpha;
                } else if ((mode == DISABLED) && (color.a != 0.0f)) {
                    color.a = 0.0f;
                }
            }
        };
        addController(fadeController);
    }
	
	/**
	 * Sets the current mode of operation of this Fader. Valid modes are:
	 * 		DISABLED
	 *		FADE_IN
	 *		FADE_OUT
	 *
	 * The alpha should be changed previous to the mode change if the mode is
	 * being changed from DISABLED.
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
        this.mode = mode;
        ignoreUntilStable = true;
    }
    
	/**
	 * Gets the current mode of operation for this Fader.
	 * 
	 * @return
	 * 		Valid modes are as follows:
	 * 			DISABLED
	 * 			FADE_IN
	 * 			FADE_OUT
	 */
    public int getMode() {
        return mode;
    }
    
    /**
     * Sets the alpha where an alpha of 1.0f is completely opaque
     * and 0.0f is transparent.
     * 
     * @param alpha
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        color.a = alpha;
    }
    
    /**
     * Gets the current alpha of this Quad.
     * 
     * @return
     * 		The current alpha where 1.0f is opaque and 0.0f is transparent
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * @return Returns the fadeTimeInSeconds.
     */
    public float getFadeTimeInSeconds() {
        return fadeTimeInSeconds;
    }

    /**
     * @param fadeTimeInSeconds The fadeTimeInSeconds to set.
     */
    public void setFadeTimeInSeconds(float fadeTimeInSeconds) {
        this.fadeTimeInSeconds = fadeTimeInSeconds;
    }

    /**
     * @return Returns the base color of the fader.
     */
    public ColorRGBA getColor() {
        return color;
    }

    /**
     * @param color The color to set as the base color of the fader.
     */
    public void setColor(ColorRGBA color) {
        this.color = color;
        if (color == null) 
            color = new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f);
        setDefaultColor(color);
    }
}
