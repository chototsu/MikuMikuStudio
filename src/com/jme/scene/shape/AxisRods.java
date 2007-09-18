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

package com.jme.scene.shape;

import java.io.IOException;

import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>AxisRods</code> is a convenience shape representing three axis in
 * space.
 * 
 * @author Joshua Slack
 * @version $Revision: 1.4 $
 */
public class AxisRods extends Node {
    private static final long serialVersionUID = 1L;

    protected static final ColorRGBA xAxisColor = new ColorRGBA(1, 0, 0, .4f);
    protected static final ColorRGBA yAxisColor = new ColorRGBA(0, 1, 0, .25f);
    protected static final ColorRGBA zAxisColor = new ColorRGBA(0, 0, 1, .4f);

    protected float length;
    protected float width;
    protected boolean rightHanded;

    protected Arrow xAxis;
    protected Arrow yAxis;
    protected Arrow zAxis;

    public AxisRods() {}
    
    public AxisRods(String name) {
        this(name, true, 1);
    }
    
    public AxisRods(String name, boolean rightHanded, float baseScale) {
        this(name, rightHanded, baseScale, baseScale * 0.125f);
    }

    public AxisRods(String name, boolean rightHanded, float length, float width) {
        super(name);
        this.length = length;
        this.width = width;
        this.rightHanded = rightHanded;
        setLightCombineMode(LightState.OFF);
        setTextureCombineMode(TextureState.OFF);
        
        buildAxis();
    }

    protected void buildAxis() {
        xAxis = new Arrow("xAxis", length, width);
        xAxis.setSolidColor(xAxisColor);
        xAxis.getLocalRotation().fromAngles(0,0,-90*FastMath.DEG_TO_RAD);
        xAxis.getLocalTranslation().addLocal(length*.5f, 0, 0);
        attachChild(xAxis);

        yAxis = new Arrow("yAxis", length, width);
        yAxis.setSolidColor(yAxisColor);
        yAxis.getLocalTranslation().addLocal(0, length*.5f, 0);
        attachChild(yAxis);
        
        zAxis = new Arrow("zAxis", length, width);
        zAxis.setSolidColor(zAxisColor);
        if (rightHanded) {
            zAxis.getLocalRotation().fromAngles(90*FastMath.DEG_TO_RAD,0,0);
            zAxis.getLocalTranslation().addLocal(0, 0, length*.5f);
        } else {
            zAxis.getLocalRotation().fromAngles(-90*FastMath.DEG_TO_RAD,0,0);
            zAxis.getLocalTranslation().addLocal(0, 0, -length*.5f);
        }
        attachChild(zAxis);
    }

    public void setModelBound(BoundingVolume bound) {
        xAxis.setModelBound(bound);
        yAxis.setModelBound(bound);
        zAxis.setModelBound(bound);
    }

    public void updateModelBound() {
        xAxis.updateModelBound();
        yAxis.updateModelBound();
        zAxis.updateModelBound();
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "length", 1);
        capsule.write(width, "width", 0.125f);
        capsule.write(rightHanded, "rightHanded", true);
        
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        length = capsule.readFloat("length", 1);
        width = capsule.readFloat("width", 0.125f);
        rightHanded = capsule.readBoolean("rightHanded", true);
        buildAxis();
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
