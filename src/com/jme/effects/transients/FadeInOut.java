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
/*
 * Created on Apr 6, 2004
 */
package com.jme.effects.transients;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.state.AlphaState;
import com.jme.system.DisplaySystem;

/**
 * @author Ahmed
 */
public class FadeInOut extends Transient {

    private Geometry fadeQ;
    private Node fadeInNode, fadeOutNode;
    private ColorRGBA fadeColor;
    private float speed;

    public FadeInOut(String name, Geometry fade, Node out, Node in, ColorRGBA c) {
        super(name);
        initialise(fade, out, in, c, 0.01f);
    }

    public FadeInOut(String name, Geometry fade, Node out, Node in, ColorRGBA c, float s) {
        super(name);
        initialise(fade, out, in, c, s);
    }

    private void initialise(Geometry fade, Node out, Node in, ColorRGBA c, float speed) {
        setMaxNumOfStages(2);
        setCurrentStage(0);
        setSpeed(speed);

        fadeColor = (ColorRGBA)c.clone();
        fadeColor.a = 0;
        
        fadeInNode = in;
        fadeOutNode = out;
        fadeQ = fade;

        AlphaState fadeAS = DisplaySystem.getDisplaySystem().getRenderer().getAlphaState();
		fadeAS.setBlendEnabled(true);
		fadeAS.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		fadeAS.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		fadeAS.setTestEnabled(false);
		fadeAS.setTestFunction(AlphaState.TF_GEQUAL);
		fadeAS.setEnabled(true);
		
		fadeQ.setRenderState(fadeAS);
        
        this.attachChild(fadeOutNode);
    }
    
    // getters/setters for the fadeQuad
    public Geometry getFadeQuad() {
        return fadeQ;
    }
    public void setFadeQuad(Geometry f) {
        fadeQ = f;
    }
    
    // getters/setters for fadeInNode
    public Node getFadeInNode() {
    	return fadeInNode;
    }
    public void setFadeInNode(Node fade) {
    	fadeInNode = fade;
    }
    
    // getters/setters for fadeOutNode
    public Node getFadeOutNode() {
    	return fadeOutNode;
    }
    public void setFadeOutNode(Node fade) {
    	fadeOutNode = fade;
    }

    public ColorRGBA getFadeColor() {
        return fadeColor;
    }

    public void setFadeColor(ColorRGBA c) {
    	fadeColor = (ColorRGBA)c.clone();
    	fadeQ.setSolidColor(fadeColor);
    }

    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float s) {
        speed = s;
    }

    public void updateWorldData(float time) {
        if (getControllers().size() != 0) {
        	for (int i = 0; i < getControllers().size(); i++) {
        		(getController(i)).update(time);
        	}
        }
    }

}
