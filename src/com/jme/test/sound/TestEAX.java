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
 * Created on 18 janv. 2004
 *
 */
package com.jme.test.sound;




import com.jme.entity.Entity;
import com.jme.sound.SoundSystem;
import com.jme.sound.filter.LWJGLListenerFilter;
import com.jme.sound.filter.ListenerFilter;

/**
 * @author Arman Ozcelik
 *
 */
public class TestEAX {

	public static void main(String[] args) throws Exception {
		SoundSystem system= SoundSystem.getSoundEffectSystem("LWJGL", true);
		Entity monster=new Entity("footsteps");
		system.addSource(monster);
		system.load("data/sound/Footsteps.wav", monster.getId());
		
		System.out.println("No filter");
		system.getPlayer(monster).loop(monster.getId());
		Thread.sleep(5000);
		
		ListenerFilter filter= new LWJGLListenerFilter();
		System.out.println("Room filter");
		system.getPlayer(monster).applyFilter(filter.getPredefinedFilter(ListenerFilter.ROOM));
		Thread.sleep(5000);

		System.out.println("Concer hall filter");
		system.getPlayer(monster).applyFilter(filter.getPredefinedFilter(ListenerFilter.CONCERTHALL));
		Thread.sleep(5000);

		System.out.println("Hangar filter");
		system.getPlayer(monster).applyFilter(filter.getPredefinedFilter(ListenerFilter.HANGAR));
		Thread.sleep(5000);

	}

}
