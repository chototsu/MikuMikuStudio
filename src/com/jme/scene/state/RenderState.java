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

import com.jme.scene.Controller;

/**
 * <code>RenderState</code>
 * @author Mark Powell
 * @version $Id: RenderState.java,v 1.4 2004-03-20 20:44:25 ericthered Exp $
 */
public abstract class RenderState {

    public final static int RS_ALPHA = 0;
    public final static int RS_DITHER = 1;
    public final static int RS_FOG = 2;
    public final static int RS_LIGHT = 3;
    public final static int RS_MATERIAL = 4;
    public final static int RS_SHADE = 5;
    public final static int RS_TEXTURE = 6;
    public final static int RS_VERTEXCOLOR = 7;
    public final static int RS_WIREFRAME = 8;
    public final static int RS_ZBUFFER = 9;
    public final static int RS_CULL = 10;
    public final static int RS_SHOW_BOUNDINGS = 11;
    public final static int RS_VERTEX_PROGRAM = 12;
    public final static int RS_MAX_STATE = 13;
    
    public final static int MAX_CONTROLLERS = 10;
    
    private Controller[] controllers;
    private boolean enabled;
    
    public RenderState() {
        controllers = new Controller[MAX_CONTROLLERS];
    }
    
    public abstract int getType();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean value) {
        this.enabled = value;
    }
    
    public Controller[] getControllers() {
        return controllers;
    }
    
    public void addController(int index, Controller c) {
        controllers[index] = c;
    }
    
    
    
    public abstract void set();
    public abstract void unset();
}
