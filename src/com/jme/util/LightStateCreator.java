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

package com.jme.util;

import java.util.ArrayList;
import com.jme.light.*;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.renderer.ColorRGBA;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Plane;

/**
 * The <code> LightStateCreator </code> class is used to sort lights in a scene.
 * The utility allows the user to place multiple lights in a single container and
 * the best eight lights (those lights that most directly affect a Spatial) will be
 * applied.
 * 
 * @author Badmi
 */
public class LightStateCreator {

    ArrayList lightList;

    /** Creates a new instance of LightStateCreator */
    public LightStateCreator() {
        lightList = new ArrayList();
    }

    /**
     * Adds a light for the controller to sort into a spatial. All the lights
     * must be added before the lights could be sorted.
     */
    public void addLight(Light l) {
        lightList.add(l);
    }

    /**
     * Gets the Ith light from the creator. The placement of the light is
     * subject to change.
     */
    public Light get(int i) {
        return (Light) lightList.get(i);
    }

    /** Returns the number of lights in the creator. */
    public int numberOfLights() {
        return lightList.size();
    }

    /**
     * Creates a new LightState for a spatial placing the "best" eight lights
     * currently maintained by the LightStateCreator.
     */
    public LightState createLightState(Spatial sp) {
        LightState l = com.jme.system.DisplaySystem.getDisplaySystem()
                .getRenderer().createLightState();
        resortLightsFor(l, sp);
        return l;
    }

    /**
     * Gives the LightState the best possible lights for the Spatial. The spatial
     * must be using bounding volumes for this process to work properly.
     */
    public void resortLightsFor(LightState ls, Spatial sp) {

        ls.detachAll();
        quickSort(0, lightList.size() - 1, sp);

        for (int i = 0; i < LightState.MAX_LIGHTS_ALLOWED; i++) {
            ls.attach(get(i));
        }
    }

    protected void quickSort(int minindex, int maxindex, Spatial sp) {

        if (maxindex - minindex <= 1) return;
        int chose = (maxindex - minindex) / 2 + minindex;
        float valofchos = getValueFor(get(chose), sp.getWorldBound());
        swap(chose, minindex);

        int setmin = minindex + 1;
        int setmax = maxindex;
        while (setmax - setmin >= 1) {
            while (setmax - setmin > 1
                    && getValueFor(get(setmin), sp.getWorldBound()) > valofchos)
                setmin++;

            while (setmax - setmin > 1
                    && getValueFor(get(setmax), sp.getWorldBound()) <= valofchos)
                setmax--;

            if (setmax - setmin < 1) break;
            swap(setmin, setmax);
            setmin++;
            setmax--;

        }
        swap(minindex, setmin);

        if (setmin == LightState.MAX_LIGHTS_ALLOWED)
            return;
        else if (setmin < LightState.MAX_LIGHTS_ALLOWED && setmin != minindex)
            quickSort(setmin, maxindex, sp);
        else if (setmin != maxindex) quickSort(minindex, setmin, sp);

    }

    /**
     * Swaps point a and b in the list
     */
    protected void swap(int a, int b) {
        Object temp = lightList.get(a);
        lightList.set(a, lightList.get(b));
        lightList.set(b, temp);
    }

    protected float max(ColorRGBA a) {
        return Math.max(Math.max(a.r, a.g), a.b);
    }

    protected float getColorValue(Light l) {
        return Math.max(Math.max(max(l.getAmbient()), max(l.getDiffuse())),
                max(l.getSpecular()));
    }

    protected float getValueFor(Light l, BoundingVolume val) {
        if (!l.isEnabled())
            return 0;
        else if (l.getType() == Light.LT_AMBIENT) {
            return max(l.getAmbient());
        } else if (l.getType() == Light.LT_DIRECTIONAL) {
            return getColorValue(l);
        } else if (l.getType() == Light.LT_POINT) {
            return getValueFor((PointLight) l, val);
        } else if (l.getType() == Light.LT_SPOT) { return getValueFor(
                (SpotLight) l, val); }
        //If a new tipe of light was aded and this was not updated return .3
        return .3f;
    }

    float getValueFor(PointLight l, BoundingVolume val) {
        if (l.isAttenuate()) {
            float dist = val.distanceTo(l.getLocation());

            float colar = getColorValue(l);
            float amlat = 1;
            if (l.getConstant() != 0)
                amlat = /* 1 */l.getConstant() + l.getLinear() * dist
                        + l.getQuadratic() * dist * dist;
            else
                amlat = l.getLinear() * dist + l.getQuadratic() * dist * dist;

            return colar / amlat;
        } else {
            return getColorValue(l);
        }
    }

    float getValueFor(SpotLight l, BoundingVolume val) {
        Plane p = new Plane(l.getDirection(), l.getDirection().dot(
                l.getLocation()));
        if (val.whichSide(p) != Plane.NEGATIVE_SIDE)
                return getValueFor((PointLight) l, val);

        return 0;
    }

    LightStateCreator makeCopy() {
        LightStateCreator newtool = new LightStateCreator();

        for (int i = 0; i < this.numberOfLights(); i++)
            newtool.addLight(this.get(i));

        return newtool;
    }
}