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
 * Created on 22 janv. 2004
 *
 */
package com.jme.sound;

import com.jme.math.Vector3f;


/**
 * @author Arman Ozcelik
 *
 */
public interface ISource {

	public void play();

	public void pause();

	public void stop();

	public void rewind();

	public void delete();

	public void setPitch(float pitch);

	public float getPitch();

	public void setGain(float gain);

	public float getGain();

	public void setMaxDistance(float maxDistance);

	public float getMaxDistance();

	public void setRolloffFactor(float rolloffFactor);

	public float getRolloffFactor();

	public void setReferenceDistance(float referenceDistance);

	public float getReferenceDistance();

	public void setMinGain(float minGain);

	public float getMinGain();

	public void setMaxGain(float maxGain);

	public float getMaxGain();

	public void setConeOuterGain(float coneOuterGain);

	public float getConeOuterGain();

	public void setPosition(Vector3f position);

	public void setPosition(float x, float y, float z);

	public Vector3f getPosition();

	public void setVelocity(Vector3f velocity);

	public void setVelocity(float x, float y, float z);

	public Vector3f getVelocity();

	public void setDirection(Vector3f direction);

	public void setDirection(float x, float y, float z);

	public Vector3f getDirection();

	public void setSourceRelative(boolean isRelative);

	public boolean isSourceRelative();

	public void setLooping(boolean isLooping);

	public boolean getLooping();

	public int getBuffersQueued();

	public int getBuffersProcessed();

	public void setBuffer(IBuffer buffer);

	public IBuffer getBuffer();

	public void queueBuffers(IBuffer[] buffers);
	
	public void queueBuffer(IBuffer buffers);

	public void unqueueBuffers(IBuffer[] buffer);

	public void unqueueBuffer(IBuffer buffer);
	
	public boolean isPlaying();

	public void setFilter(IBufferFilter filter);

}
