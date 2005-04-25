/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
 * Created on 10 avr. 2005
 */
package com.jme.sound.openAL.objects;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.jme.math.Vector3f;

/**
 * @author Arman
 */
public class Listener {
    
    private final float[] orientation={0, 0, -1, 0,1,0};
    private final float[] position={0, 0, 0};
    private FloatBuffer orientationBuffer=BufferUtils.createFloatBuffer(6);
    
    private final FloatBuffer velocity=BufferUtils.createFloatBuffer(3);
    private final Vector3f vpos=new Vector3f();

    /**
     * Get the 6 coordinate listener's orientation
     * @return an array of six floats
     */
    public float[] getOrientation(){
        return orientation;
    }
    
    public void update(){
        AL10.alListener3f(AL10.AL_POSITION, position[0],position[1], position[2]);  
        if(orientation !=null){
            for(int a=0; a<orientation.length; a++){
                orientationBuffer.put(a, orientation[a]);
            }
        }
        AL10.alListener(AL10.AL_ORIENTATION, orientationBuffer);
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(Vector3f v) {        
        position[0]=v.x;
        position[1]=v.y;
        position[2]=v.z;
    }
    
    public Vector3f getPosition(){
        vpos.x=position[0];
        vpos.y=position[1];
        vpos.z=position[2];
        return vpos;
    }

}
