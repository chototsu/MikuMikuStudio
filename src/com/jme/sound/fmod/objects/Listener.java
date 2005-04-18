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
package com.jme.sound.fmod.objects;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.fmod3.FSound;

import com.jme.math.Vector3f;

/**
 * @author Arman
 */
public class Listener {
    
    private final float[] orientation={0, 0, -1, 0,1,0};
    private final FloatBuffer position=BufferUtils.createFloatBuffer(3);
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
        FSound.FSOUND_3D_Listener_SetAttributes(position, velocity, orientation[0], orientation[1],orientation[2],orientation[3],orientation[4],orientation[5]);
        
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    public void setPosition(Vector3f v) {        
        position.clear();
        position.put(-v.x);
        position.put(v.y);
        position.put(v.z);
        position.rewind();
    }
    
    public Vector3f getPosition(){
        vpos.x=position.get(0);
        vpos.y=position.get(1);
        vpos.z=position.get(2);
        return vpos;
    }

}
