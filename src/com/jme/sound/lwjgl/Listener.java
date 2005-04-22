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
 * Created on 24 janv. 2004
 *
 */
package com.jme.sound.lwjgl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import com.jme.math.Vector3f;
import com.jme.sound.IListener;
import com.jme.sound.IListenerFilter;

/**
 * @author Arman Ozcelik
 * @deprecated Use the new sound system implementation please
 */
public class Listener implements IListener {

    private Vector3f position= new Vector3f();
    private FloatBuffer positionBuffer=
        BufferUtils.createFloatBuffer(3);//ByteBuffer.allocateDirect(4 * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private Vector3f velocity= new Vector3f();
    private FloatBuffer velocityBuffer=
        BufferUtils.createFloatBuffer(3);//ByteBuffer.allocateDirect(4 * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private float[] orientation= new float[6];
    private FloatBuffer orientationBuffer=
        BufferUtils.createFloatBuffer(6);//ByteBuffer.allocateDirect(4 * 6).order(ByteOrder.nativeOrder()).asFloatBuffer();

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#setGain(float)
     */
    public void setGain(float gain) {
        AL10.alListenerf(AL10.AL_GAIN, gain);

    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#getGain()
     */
    public float getGain() {
        return AL10.alGetListenerf(AL10.AL_GAIN);
    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#setPosition(float, float, float)
     */
    public void setPosition(float x, float y, float z) {
        AL10.alListener3f(AL10.AL_POSITION, x, y, z);

    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#setPosition(com.jme.math.Vector3f)
     */
    public void setPosition(Vector3f position) {
        AL10.alListener3f(AL10.AL_POSITION, position.x, position.y, position.z);

    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#getPosition()
     */
    public Vector3f getPosition() {

        AL10.alGetListener(AL10.AL_POSITION, positionBuffer);
        position.x= positionBuffer.get(0);
        position.y= positionBuffer.get(1);
        position.z= positionBuffer.get(2);
        return position;
    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#setVelocity(com.jme.math.Vector3f)
     */
    public void setVelocity(Vector3f velocity) {
        AL10.alListener3f(AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#getVelocity()
     */
    public Vector3f getVelocity() {
        AL10.alGetListener(AL10.AL_VELOCITY, velocityBuffer);
        velocity.x= velocityBuffer.get(0);
        velocity.y= velocityBuffer.get(1);
        velocity.z= velocityBuffer.get(2);
        return velocity;
    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#setOrientation(float[])
     */
    public void setOrientation(float[] orient) {
        if(orient !=null){
            for(int a=0; a<orient.length; a++){
                orientationBuffer.put(a, orient[a]);
            }
        }
        AL10.alListener(AL10.AL_ORIENTATION, orientationBuffer);

    }

    /* (non-Javadoc)
     * @see com.jme.sound.IListener#getOrientation()
     */
    public float[] getOrientation() {
        AL10.alGetListener(AL10.AL_ORIENTATION, orientationBuffer);
        for(int a=0; a<6; a++){
            orientation[a]=orientationBuffer.get(a);
        }
        return orientation;
    }

    /* (non-Javadoc)
     * @see com.jme.sound.second.IListener#setFilter(com.jme.sound.second.IListenerFilter)
     */
    public void setFilter(IListenerFilter filter) {
        filter.enable();
    }

}
