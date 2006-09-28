package com.jmex.model.collada;

import com.jme.image.Texture;
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

    public ColladaMaterial() {
        stateList = new RenderState[RenderState.RS_MAX_STATE];
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
