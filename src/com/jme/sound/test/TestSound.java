/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound.test;

import com.jme.sound.SoundSystem;

/**
 * @author Arman Ozcelik
 *
 */
public class TestSound {
	
	public static void main(String[] args) throws Exception {
		SoundSystem system=SoundSystem.getSoundSystem("LWJGL");
		system.createSoundSystem(1, 1);
		system.getRenderer().loadWaveAs("test", "Battle.wav", 0);
		system.getRenderer().play("test");
		Thread.sleep(5000);
	}

}
