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

package jme.entity.effects;

import java.util.ArrayList;

import org.lwjgl.opengl.GL;
import org.lwjgl.vector.Vector3f;

import jme.entity.EntityInterface;
import jme.system.DisplaySystem;
import jme.entity.camera.Frustum;

/**
 * <code>ParticleSystem</code> maintains a collection of
 * particle emitters. 
 * 
 * @author Mark Powell
 * @version 1
 */
public class ParticleSystem implements EntityInterface {
    private ArrayList emitters;
    private Vector3f position;
    private GL gl;
    
    public ParticleSystem() {
        emitters = new ArrayList();
        gl = DisplaySystem.getDisplaySystem().getGL();
        position = new Vector3f();
    }

    /* (non-Javadoc)
     * @see jme.entity.EntityInterface#render()
     */
    public void render() {
    	gl.pushMatrix();
    	gl.translatef(position.x, position.y, position.z);
        for(int i = 0; i < emitters.size(); i++) {
            ((ParticleEmitter)emitters.get(i)).render();
        }
        gl.popMatrix();
    }

    /**
     * @see jme.entity.EntityInterface#update(float)
     */
    public void update(float time) {
        for(int i = 0; i < emitters.size(); i++) {
            ((ParticleEmitter)emitters.get(i)).update(time);
        }
    }
    
    public boolean isVisible() {
        return true;
    }
    
    public void checkVisibility(Frustum frustum) {
    }

    public void addEmitter(ParticleEmitter emitter) {
        emitters.add(emitter);
    }
    
    public void setPosition(Vector3f position) {
    	this.position = position;
    }
}
