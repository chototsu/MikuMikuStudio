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
 * Created on 17 janv. 2004
 *
 */
package com.jme.sound.lwjgl;

import org.lwjgl.openal.eax.EAXBufferProperties;
import com.jme.sound.IBufferFilter;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class BufferFilter implements IBufferFilter{

	protected EAXBufferProperties bufferProperties;
	protected int occlusion;
	protected int occlusionHF;
	protected int occlusionLF;
	protected int room;
	protected int roomHF;
	protected int roomLF;
	protected float roomRolloffFactor;
	protected int obstruction;
	protected float obstructionLFRatio;
	protected float occlusionLFRatio;
	protected float occlusionRoomRatio;
	protected int outsideVolumeHF;
	protected float airAbsorptionFactor;
	protected int direct;
	protected int directHF;


	protected BufferFilter() {
		super();
		bufferProperties= new EAXBufferProperties();
		setDirect(EAXBufferProperties.EAXBUFFER_DEFAULTDIRECT);
		setDirectHF(EAXBufferProperties.EAXBUFFER_DEFAULTDIRECTHF);
		setRoom(EAXBufferProperties.EAXBUFFER_DEFAULTROOM);
		setRoomHF(EAXBufferProperties.EAXBUFFER_DEFAULTROOMHF);
		setRoomRolloffFactor(EAXBufferProperties.EAXBUFFER_DEFAULTROOMROLLOFFFACTOR);
		setObstruction(EAXBufferProperties.EAXBUFFER_DEFAULTOBSTRUCTION);
		setObstructionLFRatio(EAXBufferProperties.EAXBUFFER_DEFAULTOBSTRUCTIONLFRATIO);
		setOcclusion(EAXBufferProperties.EAXBUFFER_DEFAULTOCCLUSION);
		setOcclusionLFRatio(EAXBufferProperties.EAXBUFFER_DEFAULTOCCLUSIONLFRATIO);
		setOcclusionRoomRatio(EAXBufferProperties.EAXBUFFER_DEFAULTOCCLUSIONROOMRATIO);
		setOutsideVolumeHF(EAXBufferProperties.EAXBUFFER_DEFAULTOUTSIDEVOLUMEHF);
		setAirAbsorptionFactor(EAXBufferProperties.EAXBUFFER_DEFAULTAIRABSORPTIONFACTOR);
		bufferProperties.setFlags(EAXBufferProperties.EAXBUFFER_DEFAULTFLAGS);
		//bufferProperties.setAutoCommit(true);
	}

	/**
	 * @return
	 */
	public float getAirAbsorptionFactor() {
		return airAbsorptionFactor;
	}

	/**
	 * @return
	 */
	public EAXBufferProperties getBufferProperties() {
		return bufferProperties;
	}

	/**
	 * @return
	 */
	public int getObstruction() {
		return obstruction;
	}

	/**
	 * @return
	 */
	public float getObstructionLFRatio() {
		return obstructionLFRatio;
	}

	/**
	 * @return
	 */
	public int getOcclusion() {
		return occlusion;
	}

	/**
	 * @return
	 */
	public int getOcclusionHF() {
		return occlusionHF;
	}

	/**
	 * @return
	 */
	public int getOcclusionLF() {
		return occlusionLF;
	}

	/**
	 * @return
	 */
	public float getOcclusionLFRatio() {
		return occlusionLFRatio;
	}

	/**
	 * @return
	 */
	public float getOcclusionRoomRatio() {
		return occlusionRoomRatio;
	}

	/**
	 * @return
	 */
	public int getOutsideVolumeHF() {
		return outsideVolumeHF;
	}

	/**
	 * @return
	 */
	public int getRoomHF() {
		return roomHF;
	}

	/**
	 * @return
	 */
	public int getRoomLF() {
		return roomLF;
	}

	/**
	 * @return
	 */
	public float getRoomRolloffFactor() {
		return roomRolloffFactor;
	}

	/**
	 * @param f
	 */
	public void setAirAbsorptionFactor(float f) {
		airAbsorptionFactor= f;
		bufferProperties.setAirAbsorptionFactor(f);

	}

	/**
	 * @param i
	 */
	public void setObstruction(int i) {
		obstruction= i;
		bufferProperties.setObstruction(i);

	}

	/**
	 * @param f
	 */
	public void setObstructionLFRatio(float f) {
		obstructionLFRatio= f;
		bufferProperties.setObstructionLFRatio(f);

	}

	/**
	 * @param i
	 */
	public void setOcclusion(int i) {
		occlusion= i;
		bufferProperties.setOcclusion(i);

	}

	/**
	 * @param i
	 */
	public void setOcclusionHF(int i) {
		occlusionHF= i;

	}

	/**
	 * @param i
	 */
	public void setOcclusionLF(int i) {
		occlusionLF= i;
		bufferProperties.setOcclusion(i);

	}

	/**
	 * @param f
	 */
	public void setOcclusionLFRatio(float f) {
		occlusionLFRatio= f;
		bufferProperties.setOcclusionLFRatio(f);

	}

	/**
	 * @param f
	 */
	public void setOcclusionRoomRatio(float f) {
		occlusionRoomRatio= f;
		bufferProperties.setOcclusionRoomRatio(f);

	}

	/**
	 * @param i
	 */
	public void setOutsideVolumeHF(int i) {
		outsideVolumeHF= i;
		bufferProperties.setOutsideVolumeHF(i);
	}

	/**
	 * @param i
	 */
	public void setRoomHF(int i) {
		roomHF= i;
		bufferProperties.setRoomHF(i);
	}

	/**
	 * @param i
	 */
	public void setRoomLF(int i) {
		roomLF= i;
		bufferProperties.setRoom(i);
	}

	/**
	 * @param f
	 */
	public void setRoomRolloffFactor(float f) {
		roomRolloffFactor= f;
		bufferProperties.setRoomRolloffFactor(f);
	}

	/**
	 * @return
	 */
	public int getDirect() {
		return direct;
	}

	/**
	 * @return
	 */
	public int getDirectHF() {
		return directHF;
	}

	/**
	 * @return
	 */
	public int getRoom() {
		return room;
	}

	/**
	 * @param i
	 */
	public void setDirect(int i) {
		direct= i;
		bufferProperties.setDirect(i);
	}

	/**
	 * @param i
	 */
	public void setDirectHF(int i) {
		directHF= i;
		bufferProperties.setDirectHF(i);

	}

	/**
	 * @param i
	 */
	public void setRoom(int i) {
		room= i;
		bufferProperties.setRoom(i);
	}

	public void applyOnSource(int sourceNumber){
		bufferProperties.setCurrentSource(sourceNumber);
	}



}
