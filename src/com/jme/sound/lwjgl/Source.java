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
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.jme.math.Vector3f;
import com.jme.sound.IBuffer;
import com.jme.sound.IBufferFilter;
import com.jme.sound.ISource;

/**
 * @author Arman Ozcelik
 *  
 */
public class Source implements ISource {

	private int sourceNumber;
	private Vector3f position = new Vector3f();
	private FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(3); //ByteBuffer.allocateDirect(4
																		   // *
																		   // 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
	private Vector3f velocity = new Vector3f();
	private FloatBuffer velocityBuffer = BufferUtils.createFloatBuffer(3);//ByteBuffer.allocateDirect(4
																		  // *
																		  // 3).order(ByteOrder.nativeOrder()).asFloatBuffer();

	private Vector3f direction = new Vector3f();
	private FloatBuffer directionBuffer = BufferUtils.createFloatBuffer(3);//ByteBuffer.allocateDirect(4
																		   // *
																		   // 3).order(ByteOrder.nativeOrder()).asFloatBuffer();

	private IBuffer buffer;

	public Source(int sourceNumber) {
		this.sourceNumber = sourceNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#play()
	 */
	public void play() {

		AL10.alSourcePlay(sourceNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#pause()
	 */
	public void pause() {
		AL10.alSourcePause(sourceNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#stop()
	 */
	public void stop() {
		AL10.alSourceStop(sourceNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#rewind()
	 */
	public void rewind() {
		AL10.alSourceRewind(sourceNumber);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#delete()
	 */
	public void delete() {
		IntBuffer alSource = BufferUtils.createIntBuffer(1);//ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		alSource.put(sourceNumber);
		AL10.alDeleteSources(alSource);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setPitch(float)
	 */
	public void setPitch(float pitch) {
		AL10.alSourcef(sourceNumber, AL10.AL_PITCH, pitch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getPitch()
	 */
	public float getPitch() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_PITCH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setGain(float)
	 */
	public void setGain(float gain) {
		AL10.alSourcef(sourceNumber, AL10.AL_GAIN, gain);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getGain()
	 */
	public float getGain() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_GAIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setMaxDistance(float)
	 */
	public void setMaxDistance(float maxDistance) {
		AL10.alSourcef(sourceNumber, AL10.AL_MAX_DISTANCE, maxDistance);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getMaxDistance()
	 */
	public float getMaxDistance() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_MAX_DISTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setRolloffFactor(float)
	 */
	public void setRolloffFactor(float rolloffFactor) {
		AL10.alSourcef(sourceNumber, AL10.AL_ROLLOFF_FACTOR, rolloffFactor);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getRolloffFactor()
	 */
	public float getRolloffFactor() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_ROLLOFF_FACTOR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setReferenceDistance(float)
	 */
	public void setReferenceDistance(float referenceDistance) {
		AL10.alSourcef(sourceNumber, AL10.AL_REFERENCE_DISTANCE,
				referenceDistance);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getReferenceDistance()
	 */
	public float getReferenceDistance() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_REFERENCE_DISTANCE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setMinGain(float)
	 */
	public void setMinGain(float minGain) {
		AL10.alSourcef(sourceNumber, AL10.AL_MIN_GAIN, minGain);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getMinGain()
	 */
	public float getMinGain() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_MIN_GAIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setMaxGain(float)
	 */
	public void setMaxGain(float maxGain) {
		AL10.alSourcef(sourceNumber, AL10.AL_MAX_GAIN, maxGain);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getMaxGain()
	 */
	public float getMaxGain() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_MAX_GAIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setConeOuterGain(float)
	 */
	public void setConeOuterGain(float coneOuterGain) {
		AL10.alSourcef(sourceNumber, AL10.AL_CONE_OUTER_GAIN, coneOuterGain);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getConeOuterGain()
	 */
	public float getConeOuterGain() {
		return AL10.alGetSourcef(sourceNumber, AL10.AL_CONE_OUTER_GAIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setPosition(com.jme.math.Vector3f)
	 */
	public void setPosition(Vector3f position) {
		AL10.alSource3f(sourceNumber, AL10.AL_POSITION, position.x, position.y,
				position.z);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setPosition(float, float, float)
	 */
	public void setPosition(float x, float y, float z) {
		AL10.alSource3f(sourceNumber, AL10.AL_POSITION, x, y, z);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getPosition()
	 */
	public Vector3f getPosition() {
		AL10.alGetSource(sourceNumber, AL10.AL_POSITION, positionBuffer);
		position.x = positionBuffer.get(0);
		position.y = positionBuffer.get(1);
		position.z = positionBuffer.get(2);
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setVelocity(com.jme.math.Vector3f)
	 */
	public void setVelocity(Vector3f veloc) {
		AL10.alSource3f(sourceNumber, AL10.AL_VELOCITY, veloc.x, veloc.y,
				veloc.z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setVelocity(float, float, float)
	 */
	public void setVelocity(float x, float y, float z) {
		AL10.alSource3f(sourceNumber, AL10.AL_VELOCITY, x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getVelocity()
	 */
	public Vector3f getVelocity() {
		AL10.alGetSource(sourceNumber, AL10.AL_VELOCITY, velocityBuffer);
		velocity.x = velocityBuffer.get(0);
		velocity.y = velocityBuffer.get(1);
		velocity.z = velocityBuffer.get(2);
		return velocity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setDirection(com.jme.math.Vector3f)
	 */
	public void setDirection(Vector3f direct) {
		AL10.alSource3f(sourceNumber, AL10.AL_DIRECTION, direct.x, direct.y,
				direct.z);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setDirection(float, float, float)
	 */
	public void setDirection(float x, float y, float z) {
		AL10.alSource3f(sourceNumber, AL10.AL_DIRECTION, x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getDirection()
	 */
	public Vector3f getDirection() {
		AL10.alGetSource(sourceNumber, AL10.AL_DIRECTION, directionBuffer);
		direction.x = directionBuffer.get(0);
		direction.y = directionBuffer.get(1);
		direction.z = directionBuffer.get(2);
		return direction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setSourceRelative(boolean)
	 */
	public void setSourceRelative(boolean isRelative) {
		AL10.alSourcei(sourceNumber, AL10.AL_SOURCE_RELATIVE, isRelative
				? AL10.AL_TRUE
				: AL10.AL_FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#isSourceRelative()
	 */
	public boolean isSourceRelative() {
		return (AL10.alGetSourcei(sourceNumber, AL10.AL_SOURCE_RELATIVE) == AL10.AL_TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setLooping(boolean)
	 */
	public void setLooping(boolean isLooping) {
		AL10.alSourcei(sourceNumber, AL10.AL_LOOPING, isLooping
				? AL10.AL_TRUE
				: AL10.AL_FALSE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getLooping()
	 */
	public boolean getLooping() {
		return (AL10.alGetSourcei(sourceNumber, AL10.AL_LOOPING) == AL10.AL_TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getBuffersQueued()
	 */
	public int getBuffersQueued() {
		return AL10.alGetSourcei(sourceNumber, AL10.AL_BUFFERS_QUEUED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getBuffersProcessed()
	 */
	public int getBuffersProcessed() {
		return AL10.alGetSourcei(sourceNumber, AL10.AL_BUFFERS_PROCESSED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#setBuffer(com.jme.sound.IBuffer)
	 */
	public void setBuffer(IBuffer buffer) {
		this.buffer = buffer;
		AL10.alSourcei(sourceNumber, AL10.AL_BUFFER, ((Buffer) buffer)
				.getBufferNumber());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#getBuffer()
	 */
	public IBuffer getBuffer() {
		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#queueBuffers(com.jme.sound.IBuffer[])
	 */
	public void queueBuffers(IBuffer[] buffers) {
		IntBuffer alBuffer = BufferUtils.createIntBuffer(buffers.length);//ByteBuffer.allocateDirect(4
																		 // *
																		 // buffers.length).order(ByteOrder.nativeOrder()).asIntBuffer();
		for (int i = 0; i < buffers.length; i++) {
			alBuffer.put(((Buffer) buffers[i]).getBufferNumber());
		}
		alBuffer.rewind();
		AL10.alSourceQueueBuffers(sourceNumber, alBuffer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#queueBuffers(com.jme.sound.IBuffer[])
	 */
	public void queueBuffer(IBuffer buffer) {
		IntBuffer alBuffer = BufferUtils.createIntBuffer(1);
		alBuffer.put(((Buffer) buffer).getBufferNumber());
		alBuffer.rewind();
		AL10.alSourceQueueBuffers(sourceNumber, alBuffer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#unqueueBuffers(com.jme.sound.IBuffer[])
	 */
	public void unqueueBuffers(IBuffer[] buffers) {
		IntBuffer alBuffer = BufferUtils.createIntBuffer(buffers.length);//ByteBuffer.allocateDirect(4
																		 // *
																		 // buffers.length).order(ByteOrder.nativeOrder()).asIntBuffer();
		for (int i = 0; i < buffers.length; i++) {
			alBuffer.put(((Buffer) buffers[i]).getBufferNumber());
		}
		alBuffer.rewind();
		AL10.alSourceUnqueueBuffers(sourceNumber, alBuffer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.ISource#unqueueBuffers(com.jme.sound.IBuffer[])
	 */
	public void unqueueBuffer(IBuffer buffer) {
		IntBuffer alBuffer = BufferUtils.createIntBuffer(1);

		alBuffer.put(((Buffer) buffer).getBufferNumber());
		alBuffer.rewind();
		AL10.alSourceUnqueueBuffers(sourceNumber, alBuffer);

	}

	public boolean isPlaying() {
		return (AL10.alGetSourcei(sourceNumber, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.sound.second.ISource#setFilter(com.jme.sound.second.IBufferFilter)
	 */
	public void setFilter(IBufferFilter filter) {
		filter.applyOnSource(sourceNumber);
	}

}