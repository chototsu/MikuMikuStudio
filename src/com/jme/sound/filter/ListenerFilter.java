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
package com.jme.sound.filter;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class ListenerFilter {

	public static final int GENERIC= 0;
	public static final int PADDEDCELL= 1;
	public static final int ROOM= 2;
	public static final int BATHROOM= 3;
	public static final int LIVINGROOM= 4;
	public static final int STONEROOM= 5;
	public static final int AUDITORIUM= 6;
	public static final int CONCERTHALL= 7;
	public static final int CAVE= 8;
	public static final int ARENA= 9;
	public static final int HANGAR= 10;
	public static final int CARPETEDHALLWAY= 11;
	public static final int HALLWAY= 12;
	public static final int STONECORRIDOR= 13;
	public static final int ALLEY= 14;
	public static final int FOREST= 15;
	public static final int CITY= 16;
	public static final int MOUNTAINS= 17;
	public static final int QUARRY= 18;
	public static final int PLAIN= 19;
	public static final int PARKINGLOT= 20;
	public static final int SEWERPIPE= 21;
	public static final int UNDERWATER= 22;
	public static final int DRUGGED= 23;
	public static final int DIZZY= 24;
	public static final int PSYCHOTIC= 25;
	public static final int COUNT= 26;

	/**
	 * @return
	 */
	public abstract float getAirAbsorptionHF();

	/**
	 * @return
	 */
	public abstract float getDecayTimeHFRatio();

	/**
	 * @return
	 */
	public abstract float getDecayTime();

	/**
	 * @return
	 */
	public abstract int getReflections();
	/**
	 * @return
	 */
	public abstract float getReflectionsDelay();
	/**
	 * @return
	 */
	public abstract int getReverb();

	/**
	 * @return
	 */
	public abstract float getReverbDelay();

	/**
	 * @return
	 */
	public abstract int getRoom();

	/**
	 * @return
	 */
	public abstract int getRoomHF();

	/**
	 * @return
	 */
	public abstract float getRoomRolloffFactor();

	/**
	 * 
	 * @param f
	 */
	public abstract void setAirAbsorptionHF(float f);

	/**
	 * @param f
	 */
	public abstract void setDecayTimeHFRatio(float f);

	/**
	 * @param i
	 */
	public abstract void setDecayTime(float i);

	/**
	 * @param i
	 */
	public abstract void setReflections(int i);

	/**
	 * @param i
	 */
	public abstract void setReflectionsDelay(float i);

	/**
	 * @param i
	 */
	public abstract void setReverb(int i);

	/**
	 * @param i
	 */
	public abstract void setReverbDelay(float i);

	/**
	 * @param i
	 */
	public abstract void setRoom(int i);

	/**
	 * @param i
	 */
	public abstract void setRoomHF(int i);

	/**
	 * @param i
	 */
	public abstract void setRoomRolloffFactor(float i);
	
	public abstract ListenerFilter getPredefinedFilter(int filterName);

}
