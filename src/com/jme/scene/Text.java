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

package com.jme.scene;

import java.io.IOException;
import java.util.Stack;

import com.jme.app.SimpleGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.intersection.CollisionResults;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * 
 * <code>Text</code> allows text to be displayed on the screen. The
 * renderstate of this Geometry must be a valid font texture.
 * 
 * @author Mark Powell
 * @version $Id: Text.java,v 1.28 2007/08/02 21:51:11 nca Exp $
 */
public class Text extends Geometry {

    private static final long serialVersionUID = 1L;

    private StringBuffer text;

    private ColorRGBA textColor = new ColorRGBA();

    /**
     * The compiled list of renderstates for this geometry, taking into account
     * ancestors' states - updated with updateRenderStates()
     */
    public RenderState[] states = new RenderState[RenderState.RS_MAX_STATE];

    public Text() {}
    
    /**
     * Creates a texture object that starts with the given text.
     * 
     * @see com.jme.util.TextureManager
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparision purposes.
     * @param text
     *            The text to show.
     */
    public Text(String name, String text) {
        super(name);
        setCullMode(SceneElement.CULL_NEVER);
        this.text = new StringBuffer(text);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * 
     * <code>print</code> sets the text to be rendered on the next render
     * pass.
     * 
     * @param text
     *            the text to display.
     */
    public void print(String text) {
        this.text.replace(0, this.text.length(), text);
    }

    /**
     * Sets the text to be rendered on the next render. This function is a more
     * efficient version of print(String).
     * 
     * @param text
     *            The text to display.
     */
    public void print(StringBuffer text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    /**
     * 
     * <code>getText</code> retrieves the text string of this
     * <code>Text</code> object.
     * 
     * @return the text string of this object.
     */
    public StringBuffer getText() {
        return text;
    }

    /**
     * <code>draw</code> calls super to set the render state then calls the
     * renderer to display the text string.
     * 
     * @param r
     *            the renderer used to display the text.
     */
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this)) return;
        }
        super.draw(r);
        r.draw(this);
    }

    /**
     * Sets the color of the text.
     * 
     * @param color
     *            Color to set.
     */
    public void setTextColor(ColorRGBA color) {
    	textColor = color;
    }

    /**
     * Returns the current text color.
     * 
     * @return Current text color.
     */
    public ColorRGBA getTextColor() {
        return textColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
     *      com.jme.intersection.CollisionResults)
     */
    public void findCollisions(Spatial scene, CollisionResults results) {
        //Do nothing.
    }

    public boolean hasCollision(Spatial scene, boolean checkTriangles) {
        return false;
    }

    public float getWidth() {
        float rVal = 10f * text.length() * worldScale.x;
        return rVal;
    }

    public float getHeight() {
        float rVal = 16f * worldScale.y;
        return rVal;
    }

    /**
     * @return a Text with {@link #DEFAULT_FONT} and correct alpha state
     * @param name name of the spatial
     */
    public static Text createDefaultTextLabel( String name ) {
        return createDefaultTextLabel( name, "" );
    }

    /**
     * @return a Text with {@link #DEFAULT_FONT} and correct alpha state
     * @param name name of the spatial
     */
    public static Text createDefaultTextLabel( String name, String initialText ) {
        Text text = new Text( name, initialText );
        text.setCullMode( SceneElement.CULL_NEVER );
        text.setRenderState( getDefaultFontTextureState() );
        text.setRenderState( getFontAlpha() );
        return text;
    }

    /*
    * @return an alpha state for doing alpha transparency
    */
    private static AlphaState getFontAlpha() {
        AlphaState as1 = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as1.setBlendEnabled( true );
        as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
        as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
        return as1;
    }

    /**
     * texture state for the default font.
     */
    private static TextureState defaultFontTextureState;

    public static final void resetFontTexture() {
        defaultFontTextureState = null;
    }
    
    /**
     * A default font cantained in the jME library.
     */
    public static final String DEFAULT_FONT = "com/jme/app/defaultfont.tga";
    
    protected void applyRenderState(Stack[] states) {
        for (int x = 0; x < states.length; x++) {
            if (states[x].size() > 0) {
                this.states[x] = ((RenderState) states[x].peek()).extract(
                        states[x], this);
            } else {
                this.states[x] = Renderer.defaultStateList[x];
            }
        }
    }

    /**
     * Creates the texture state if not created before.
     * @return texture state for the default font
     */
    private static TextureState getDefaultFontTextureState() {
        if ( defaultFontTextureState == null ) {
            defaultFontTextureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
            defaultFontTextureState.setTexture( TextureManager.loadTexture( SimpleGame.class
                    .getClassLoader().getResource( DEFAULT_FONT ), Texture.MM_LINEAR_LINEAR,
                    Texture.FM_LINEAR, Image.GUESS_FORMAT_NO_S3TC, 1.0f, true ) );
            defaultFontTextureState.setEnabled( true );
        }
        return defaultFontTextureState;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(text.toString(), "textString", "");
        capsule.write(textColor, "textColor", new ColorRGBA());
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        text = new StringBuffer(capsule.readString("textString", ""));
        textColor = (ColorRGBA)capsule.readSavable("textColor", new ColorRGBA());
        
    }
}