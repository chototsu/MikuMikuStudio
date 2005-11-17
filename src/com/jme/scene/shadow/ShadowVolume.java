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

package com.jme.scene.shadow;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * <code>ShadowVolume</code>
 * Represents the shadow volume mesh for a light and an occluder model
 * 
 * @author Mike Talbot (some code from a shadow implementation written Jan 2005)
 * @author Joshua Slack
 * @version $Id: ShadowVolume.java,v 1.1 2005-11-17 23:55:41 renanse Exp $
 */
public class ShadowVolume extends TriMesh {
    private static final long serialVersionUID = 1L;

    protected Light light = null;
    protected Vector3f position = new Vector3f();
    protected Vector3f direction = new Vector3f();
    protected boolean update = true;
    protected static int _ordinal = 0;

    /**
     * Constructor for <code>ShadowVolume</code>
     * @param light the light for which a volume should be created
     */
    public ShadowVolume(Light light) {
        super("LV" + _ordinal++);
        
        this.light = light;

        // Initialise the location and direction of the light
        if (light.getType() == Light.LT_POINT) {
            position = ((PointLight) light).getLocation();
        } else if (light.getType() == Light.LT_DIRECTIONAL) {
            direction = ((DirectionalLight) light).getDirection();
        }
        
        // It will change so make sure VBO is off
        setVBOInfo(new VBOInfo(false));

        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        ms.setAmbient(new ColorRGBA(0.5f,0.7f,0.7f,0.2f));
        ms.setDiffuse(new ColorRGBA(0.5f,0.7f,0.7f,0.2f));
        ms.setEmissive(new ColorRGBA(0.9f,0.9f,0.7f,0.6f));
        ms.setAmbient(ColorRGBA.white);
        ms.setDiffuse(ColorRGBA.white);
        ms.setSpecular(ColorRGBA.white);
        ms.setEmissive(ColorRGBA.white);
        ms.setEnabled(true);
        setRenderState(ms);
        
        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as.setBlendEnabled(true);
        as.setEnabled(true);
        setRenderState(as);
    }

    /**
     * @return Returns the direction.
     */
    public Vector3f getDirection() {
        return direction;
    }

    /**
     * @param direction The direction to set.
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /**
     * @return Returns the position.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * @param position The position to set.
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * @return Returns whether this volume needs updating.
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * @param update sets whether this volume needs updating.
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * @return Returns the light.
     */
    public Light getLight() {
        return light;
    }

    /**
     * @param light The light to set.
     */
    public void setLight(Light light) {
        this.light = light;
    }

}
