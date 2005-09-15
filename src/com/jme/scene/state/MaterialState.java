/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.scene.state;

import com.jme.renderer.ColorRGBA;

/**
 * <code>MaterialState</code> defines a state to define an objects material
 * settings. Material is defined by the emissive quality of the object, the
 * ambient color, diffuse color and specular color. The material also defines
 * the shininess of the object and the alpha value of the object.
 * @author Mark Powell
 * @version $Id: MaterialState.java,v 1.7 2005-09-15 17:13:14 renanse Exp $
 */
public abstract class MaterialState extends RenderState {
    //attributes of the material
    private ColorRGBA ambient;
    private ColorRGBA diffuse;
    private ColorRGBA specular;
    private ColorRGBA emissive;
    private float shininess;
    private float alpha;  // IS THIS PARAM USED??

    protected static ColorRGBA currentAmbient = new ColorRGBA(-1,-1,-1,-1);
    protected static ColorRGBA currentDiffuse = new ColorRGBA(-1,-1,-1,-1);
    protected static ColorRGBA currentSpecular = new ColorRGBA(-1,-1,-1,-1);
    protected static ColorRGBA currentEmissive = new ColorRGBA(-1,-1,-1,-1);
    protected static float currentShininess = -1;

    /** Default ambient color for all material states. */
    public static final ColorRGBA defaultAmbient  = new ColorRGBA(0.2f,0.2f,0.2f,1.0f);
    /** Default diffuse color for all material states. */
    public static final ColorRGBA defaultDiffuse  = new ColorRGBA(0.8f,0.8f,0.8f,1.0f);
    /** Default specular color for all material states. */
    public static final ColorRGBA defaultSpecular = new ColorRGBA(0.0f,0.0f,0.0f,1.0f);
    /** Default emissive color for all material states. */
    public static final ColorRGBA defaultEmissive = new ColorRGBA(0.0f,0.0f,0.0f,1.0f);
    /** Default shininess for all material states. */
    public static final float defaultShininess = 0.0f;


    /**
     * <code>getAlpha</code> returns the alpha value for this material state.  This
     * value isn't actually used directly by jME.  Alpha value is more for a user refrence.
     * @return The current alpha value.
     * @see com.jme.scene.state.AlphaState
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * <code>setAlpha</code> sets the alpha value for this material state.  This
     * value isn't actually used directly by jME.  Alpha value is more for a user
     * refrence.
     * @param alpha The new alpha value of this material state.
     * @see com.jme.scene.state.AlphaState
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Constructor instantiates a new <code>MaterialState</code> object.
     *
     */
    public MaterialState() {
        emissive = (ColorRGBA)defaultEmissive.clone();
        ambient = (ColorRGBA)defaultAmbient.clone();
        diffuse = (ColorRGBA)defaultDiffuse.clone();
        specular = (ColorRGBA)defaultSpecular.clone();
        shininess = defaultShininess;
    }

    /**
     * <code>getAmbient</code> retreives the ambient color of the material.
     * @return the color of the ambient value.
     */
    public ColorRGBA getAmbient() {
        return ambient;
    }

    /**
     * <code>setAmbient</code> sets the ambient color of the material.
     * @param ambient the ambient color of the material.
     */
    public void setAmbient(ColorRGBA ambient) {
        this.ambient = ambient;
    }

    /**
     * <code>getDiffuse</code> retrieves the diffuse color of the material.
     * @return the color of the diffuse value.
     */
    public ColorRGBA getDiffuse() {
        return diffuse;
    }

    /**
     * <code>setDiffuse</code> sets the diffuse color of the material.
     * @param diffuse the diffuse color of the material.
     */
    public void setDiffuse(ColorRGBA diffuse) {
        this.diffuse = diffuse;
    }

    /**
     * <code>getEmissive</code> retrieves the emissive color of the material.
     * @return the color of the emissive value.
     */
    public ColorRGBA getEmissive() {
        return emissive;
    }

    /**
     * <code>setEmissive</code> sets the emissive color of the material.
     * @param emissive the emissive color of the material.
     */
    public void setEmissive(ColorRGBA emissive) {
        this.emissive = emissive;
    }

    /**
     * <code>getShininess</code> retrieves the shininess value of the material.
     * @return the shininess value of the material.
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * <code>setShininess</code> sets the shininess of the material.
     * @param shininess the shininess of the material.
     */
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    /**
     * <code>getSpecular</code> retrieves the specular color of the material.
     * @return the specular color of the material.
     */
    public ColorRGBA getSpecular() {
        return specular;
    }

    /**
     * <code>setSpecular</code> sets the specular color of the material.
     * @param specular the specular color of the material.
     */
    public void setSpecular(ColorRGBA specular) {
        this.specular = specular;
    }

    /**
     * <code>getType</code> returns the render state type of this.
     * (RS_MATERIAL).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_MATERIAL;
    }
}
