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

package jme.entity;

import jme.entity.camera.Frustum;

/**
 * <code>EntityInterface</code> defines an interface for handling entities. 
 * Where an entity is any game element. This will handle anything from a tree
 * to a player to a camera.
 * 
 * @author Mark Powell
 * @version 1
 */
public interface EntityInterface {
    /**
     * <code>render</code> handles the making the required calls to the
     * rendering framework (OpenGL) to display the entity to the screen.
     */
    public void render();
    
    /**
     * <code>update</code> is used to alter the entity in any way to reflect
     * the passage of time. This could be position changes, animation, etc.
     * @param time the amount of time between frames.
     */
    public void update(float time);
    /**
     * <code>isVisible</code> returns true if the entity is visible and false
     * if it is not.
     * @return true if the entity is visible and false otherwise.
     */
    public boolean isVisible();
    
    /**
     * <code>checkVisibility</code> makes the appropriate checks to test if
     * the entity is currently visible or not.
     * @param frustum the view frustum to check against.
     */
    public void checkVisibility(Frustum frustum);
    
}
