/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package com.jmex.model.collada;

import java.util.ArrayList;

import com.jme.image.Texture;
import com.jme.scene.Controller;
import com.jme.scene.state.RenderState;

/**
 * ColladaMaterial is designed to hold all the material attributes of a Collada
 * object. This may include many RenderState objects. ColladaMaterial is a
 * container object for jME RenderStates needed.
 * 
 * @author Mark Powell
 */
public class ColladaMaterial {
    public String minFilter;
    public String magFilter;
    
    RenderState[] stateList;
    ArrayList<Controller> controllerList;
    

    public ColladaMaterial() {
        stateList = new RenderState[RenderState.RS_MAX_STATE];
    }
    
    public void addController(Controller c) {
    	if(controllerList == null) {
    		controllerList = new ArrayList<Controller>();
    	}
    	
    	controllerList.add(c);
    }
    
    public ArrayList<Controller> getControllerList() {
    	return controllerList;
    }

    public void setState(RenderState ss) {
    	if(ss == null) return;
        stateList[ss.getType()] = ss;
    }

    public RenderState getState(int index) {
        return stateList[index];
    }
    
    public int getMagFilterConstant() {
        if(magFilter == null) {
            return Texture.FM_LINEAR;
        }
        
        if(magFilter.equals("NEAREST")) {
            return Texture.FM_NEAREST;
        }
        
        if(magFilter.equals("LINEAR")) {
            return Texture.FM_LINEAR;
        }
        
        return Texture.FM_LINEAR;
    }
    
    public int getMinFilterConstant() {
        if(minFilter == null) {
            return Texture.MM_LINEAR_LINEAR;
        }
        
        if(minFilter.equals("NEAREST")) {
            return Texture.MM_NEAREST;
        }
        
        if(minFilter.equals("LINEAR")) {
            return Texture.MM_LINEAR;
        }
        
        if(minFilter.equals("NEAREST_MIPMAP_NEAREST")) {
            return Texture.MM_NEAREST_NEAREST;
        }
        
        if(minFilter.equals("NEAREST_MIPMAP_LINEAR")) {
            return Texture.MM_NEAREST_LINEAR;
        }
        
        if(minFilter.equals("LINEAR_MIPMAP_NEAREST")) {
            return Texture.MM_LINEAR_NEAREST;
        }
        
        if(minFilter.equals("LINEAR_MIPMAP_LINEAR")) {
            return Texture.MM_LINEAR_LINEAR;
        }
        
        return Texture.MM_LINEAR_LINEAR;
    }
}
