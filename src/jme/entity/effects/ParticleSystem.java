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
import jme.entity.camera.Frustum;
import jme.geometry.bounding.BoundingVolume;

/**
 * <code>ParticleSystem</code> maintains a collection of
 * particle emitters. 
 * 
 * @author Mark Powell
 * @version $Id: ParticleSystem.java,v 1.4 2003-09-03 16:20:52 mojomonkey Exp $
 */
public class ParticleSystem implements EntityInterface {
    private ArrayList emitters;
    private Vector3f position;
    private BoundingVolume boundingVolume;
    
    public ParticleSystem() {
        emitters = new ArrayList();
        position = new Vector3f();
    }

    /* (non-Javadoc)
     * @see jme.entity.EntityInterface#render()
     */
    public void render() {
        GL.glPushMatrix();
        GL.glTranslatef(position.x, position.y, position.z);
        for(int i = 0; i < emitters.size(); i++) {
            ((ParticleEmitter)emitters.get(i)).render();
        }
        GL.glPopMatrix();
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
    
    /**
     * <code>setBoundingVolume</code> sets the volume that contains this
     * entity.
     * @param volume the volume that contains this entity.
     */
    public void setBoundingVolume(BoundingVolume volume) {
        this.boundingVolume = volume;
    }

    /**
     * <code>getBoundingVolume</code> returns the volume that contains this
     * entity.
     * @return the volume that contains this entity.
     */
    public BoundingVolume getBoundingVolume() {
        return boundingVolume;
    }
}
