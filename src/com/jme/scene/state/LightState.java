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

import java.util.ArrayList;

import com.jme.light.Light;

/**
 * <code>LightState</code> maintains a collection of lights up to the set
 * number of maximum lights allowed. Any subclass of <code>Light</code> can
 * be added to the light state. Each light is processed and used to modify
 * the color of the scene.
 * @author Mark Powell
 * @version $Id: LightState.java,v 1.2 2004-02-27 00:18:09 mojomonkey Exp $
 */
public abstract class LightState extends RenderState {
    /**
     * defines the maximum number of lights that are allowed to be 
     * maintained at one time.
     */
    public static final int MAX_LIGHTS_ALLOWED = 8;
    
    //holds the lights
    private ArrayList lightList;
    protected boolean twoSidedOn;

    /**
     * Constructor instantiates a new <code>LightState</code> object. Initially
     * there are no lights set.
     *
     */
    public LightState() {
        lightList = new ArrayList();
    }
    /**
     * <code>getType</code> returns the type of render state this is. 
     * (RS_LIGHT).
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        return RS_LIGHT;
    }
    
    /**
     * 
     * <code>attach</code> places a light in the queue to be processed. If
     * there are already eight lights placed in the queue, the light is 
     * ignored and false ir returned. Otherwise, true is returned to indicate
     * success.
     * @param light the light to add to the queue.
     * @return true if the light was added successfully, false if there are
     *      already eight lights in the queue.
     */
    public boolean attach(Light light) {
        if(lightList.size() < MAX_LIGHTS_ALLOWED) {
            lightList.add(light);
            return true;
        }
        return false;
    }

    /**
     * 
     * <code>detach</code> removes a light from the queue for processing. 
     * @param light the light to be removed.
     */
    public void detach(Light light) {
        lightList.remove(light);
    }
    
    /**
     * 
     * <code>detachAll</code> clears the queue of all lights to be processed.
     *
     */
    public void detachAll() {
        lightList.clear();
    }
    
    /**
     * 
     * <code>get</code> retrieves a particular light defined by an index.
     * If there exists no light at a particular index, null is returned.
     * @param i the index to retrieve the light from the queue.
     * @return the light at the given index, null if no light exists at this
     *      index.
     */
    public Light get(int i) {
        return (Light)lightList.get(i);
    }
    
    /**
     * 
     * <code>getQuantity</code> returns the number of lights currently in the
     * queue.
     * @return the number of lights currently in the queue.
     */
    public int getQuantity() {
        return lightList.size();
    }
    
    public void setTwoSidedLighting(boolean twoSidedOn) {
        this.twoSidedOn = twoSidedOn;
    }
}
