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

/*
 * Created on 17 janv. 2004
 *
 */
package com.jme.sound;

/**
 * @author Arman Ozcelik
 *
 */
public interface IBufferFilter {

	/**
		 * @return
		 */
	public float getAirAbsorptionFactor();

	/**
	 * @return
	 */
	public int getObstruction();

	/**
	 * @return
	 */
	public float getObstructionLFRatio();

	/**
	 * @return
	 */
	public int getOcclusion();

	/**
	 * @return
	 */
	public int getOcclusionHF();

	/**
	 * @return
	 */
	public int getOcclusionLF();

	/**
	 * @return
	 */
	public float getOcclusionLFRatio();

	/**
	 * @return
	 */
	public float getOcclusionRoomRatio();

	/**
	 * @return
	 */
	public int getOutsideVolumeHF();

	/**
	 * @return
	 */
	public int getRoomHF();

	/**
	 * @return
	 */
	public int getRoomLF();

	/**
	 * @return
	 */
	public float getRoomRolloffFactor();
	/**
	 * @param f
	 */
	public void setAirAbsorptionFactor(float f);

	/**
	 * @param i
	 */
	public void setObstruction(int i);

	/**
	 * @param f
	 */
	public void setObstructionLFRatio(float f);

	/**
	 * @param i
	 */
	public void setOcclusion(int i);

	/**
	 * @param i
	 */
	public void setOcclusionHF(int i);

	/**
	 * @param i
	 */
	public void setOcclusionLF(int i);

	/**
	 * @param f
	 */
	public void setOcclusionLFRatio(float f);

	/**
	 * @param f
	 */
	public void setOcclusionRoomRatio(float f);

	/**
	 * @param i
	 */
	public void setOutsideVolumeHF(int i);

	/**
	 * @param i
	 */
	public void setRoomHF(int i);

	/**
	 * @param i
	 */
	public void setRoomLF(int i);

	/**
	 * @param f
	 */
	public void setRoomRolloffFactor(float f);

	/**
	 * @return
	 */
	public int getDirect();

	/**
	 * @return
	 */
	public int getDirectHF();

	/**
	 * @param i
	 */
	public void setDirect(int i);

	/**
	 * @param i
	 */
	public void setDirectHF(int i);

	/**
	 * @return
	 */
	public int getRoom();

	/**
	 * @param i
	 */
	public void setRoom(int i);
	
	public void applyOnSource(int source);

}
