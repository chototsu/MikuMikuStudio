/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package com.jme.scene.state;

import com.jme.renderer.ColorRGBA;

/**
 * <code>MaterialState</code> defines a state to define an objects material
 * settings. Material is defined by the emissive quality of the object, the
 * ambient color, diffuse color and specular color. The material also defines
 * the shininess of the object and the alpha value of the object.
 * @author Mark Powell
 * @version $Id: MaterialState.java,v 1.1.1.1 2003-10-29 10:56:40 Anakan Exp $
 */
public abstract class MaterialState extends RenderState {
    //attributes of the material
    private ColorRGBA emissive;
    private ColorRGBA ambient;
    private ColorRGBA diffuse;
    private ColorRGBA specular;
    private float shininess;
    private float alpha;
    
    /**
     * Constructor instantiates a new <code>MaterialState</code> object.
     *
     */
    public MaterialState() {
        emissive = new ColorRGBA();
        ambient = new ColorRGBA();
        diffuse = new ColorRGBA();
        specular = new ColorRGBA();
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
