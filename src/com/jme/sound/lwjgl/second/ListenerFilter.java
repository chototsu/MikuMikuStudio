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
package com.jme.sound.lwjgl.second;

import org.lwjgl.openal.eax.EAXListenerProperties;

import com.jme.sound.IListenerFilter;

/**
 * @author Arman Ozcelik
 *
 */
public class ListenerFilter implements IListenerFilter {

	protected int room;
	protected int roomHF;
	protected float roomRolloffFactor;
	protected float decayTime;
	protected float decayTimeHFRatio;
	protected int reflections;
	protected float reflectionsDelay;
	protected int reverb;
	protected float reverbDelay;
	protected float airAbsorptionHF;
	protected float airAbsorptionFactor;
	protected EAXListenerProperties listenerProperties;
	protected int environment;
	
	public ListenerFilter() {
		listenerProperties= new EAXListenerProperties();
		setRoom(EAXListenerProperties.EAXLISTENER_DEFAULTROOM);
		setRoomHF(EAXListenerProperties.EAXLISTENER_DEFAULTROOMHF);
		setRoomRolloffFactor(EAXListenerProperties.EAXLISTENER_DEFAULTROOMROLLOFFFACTOR);
		setDecayTime(EAXListenerProperties.EAXLISTENER_DEFAULTDECAYTIME);
		setDecayTimeHFRatio(EAXListenerProperties.EAXLISTENER_DEFAULTDECAYHFRATIO);
		setReflections(EAXListenerProperties.EAXLISTENER_DEFAULTREFLECTIONS);
		setReflectionsDelay(EAXListenerProperties.EAXLISTENER_DEFAULTREFLECTIONSDELAY);
		setReverb(EAXListenerProperties.EAXLISTENER_DEFAULTREVERB);
		setReverbDelay(EAXListenerProperties.EAXLISTENER_DEFAULTREVERBDELAY);
		listenerProperties.setEnvironment(EAXListenerProperties.EAXLISTENER_DEFAULTENVIRONMENT);
		listenerProperties.setEnvironmentSize(EAXListenerProperties.EAXLISTENER_DEFAULTENVIRONMENTSIZE);
		listenerProperties.setEnvironmentDiffusion(EAXListenerProperties.EAXLISTENER_DEFAULTENVIRONMENTDIFFUSION);
		setAirAbsorptionFactor(EAXListenerProperties.EAXLISTENER_DEFAULTAIRABSORPTIONHF);
		listenerProperties.setFlags(EAXListenerProperties.EAXLISTENER_DEFAULTFLAGS);
		listenerProperties.setAutoCommit(false);	
	}



	/**
	 * @param sourceNumber
	 */
	public void enable() {
		listenerProperties.setAutoCommit(true);		
		listenerProperties.setEnvironment(environment);
		listenerProperties.setAutoCommit(false);	
		
	}

	/**
	 * @return
	 */
	public float getAirAbsorptionHF() {
		return airAbsorptionHF;
	}

	/**
	 * @return
	 */
	public float getDecayTimeHFRatio() {
		return decayTimeHFRatio;
	}

	/**
	 * @return
	 */
	public float getDecayTime() {
		return decayTime;
	}

	/**
	 * @return
	 */
	public EAXListenerProperties getListenerProperties() {
		return listenerProperties;
	}

	/**
	 * @return
	 */
	public int getReflections() {
		return reflections;
	}

	/**
	 * @return
	 */
	public float getReflectionsDelay() {
		return reflectionsDelay;
	}

	/**
	 * @return
	 */
	public int getReverb() {
		return reverb;
	}

	/**
	 * @return
	 */
	public float getReverbDelay() {
		return reverbDelay;
	}

	/**
	 * @return
	 */
	public int getRoom() {
		return room;
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
	public float getRoomRolloffFactor() {
		return roomRolloffFactor;
	}

	/**
	 * @param i
	 */
	public void setAirAbsorptionHF(float i) {
		airAbsorptionHF= i;
		listenerProperties.setAirAbsorptionFactor(i);
	}

	/**
	 * @param i
	 */
	public void setDecayTimeHFRatio(float i) {
		decayTimeHFRatio= i;
		listenerProperties.setDecayTimeHFRatio(i);
	}

	/**
	 * @param i
	 */
	public void setDecayTime(float i) {
		decayTime= i;
		listenerProperties.setDecayTime(i);
	}

	
	/**
	 * @param i
	 */
	public void setReflections(int i) {
		reflections= i;
		listenerProperties.setReflections(i);
	}

	/**
	 * @param i
	 */
	public void setReflectionsDelay(float i) {
		reflectionsDelay= i;
		listenerProperties.setReflectionsDelay(i);
	}

	/**
	 * @param i
	 */
	public void setReverb(int i) {
		reverb= i;
		listenerProperties.setReverb(i);
	}

	/**
	 * @param i
	 */
	public void setReverbDelay(float i) {
		reverbDelay= i;
		listenerProperties.setReverbDelay(i);
	}

	/**
	 * @param i
	 */
	public void setRoom(int i) {
		room= i;
		listenerProperties.setRoom(i);
	}

	/**
	 * @param i
	 */
	public void setRoomHF(int i) {
		roomHF= i;
		listenerProperties.setRoomHF(i);
	}

	/**
	 * @param f
	 */
	public void setRoomRolloffFactor(float f) {
		roomRolloffFactor= f;
		listenerProperties.setRoomRolloffFactor(f);
	}



	
	/**
	 * @return
	 */
	public float getAirAbsorptionFactor() {
		return airAbsorptionFactor;
	}

	/**
	 * @param f
	 */
	public void setAirAbsorptionFactor(float f) {
		airAbsorptionFactor= f;
		listenerProperties.setAirAbsorptionFactor(f);
	}



	/* (non-Javadoc)
	 * @see com.jme.sound.filter.ListenerFilter#getPredefinedFilter(int)
	 */
	public IListenerFilter getPredefinedFilter(int filterName) {
		environment=filterName;
		return this;
	}

}
