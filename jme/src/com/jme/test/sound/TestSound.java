/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.test.sound;

import com.jme.sound.SoundSystem;
import com.jme.sound.SoundRenderer;



/**
 * @author Arman Ozcelik
 *
 */
public class TestSound{
	
	
	public static void main(String[] args) throws InterruptedException {
		SoundSystem system=SoundSystem.getSoundSystem("LWJGL");
		SoundRenderer renderer=system.getRenderer();
		renderer.addSoundPlayer("MONSTER");
		renderer.addSoundPlayer("NPC");
		
		renderer.loadSoundAs("music", "cu.wav");
		renderer.loadSoundAs("bored", "01.mp3");
		
		renderer.getSoundPlayer("MONSTER").setNumberOfBuffers(1);
		renderer.getSoundPlayer("NPC").setNumberOfBuffers(128);
		
		renderer.getSoundPlayer("MONSTER").updatePosition(1, 0, 0);
		renderer.getSoundPlayer("NPC").updatePosition(0, 0, 0);
		renderer.getSoundPlayer("NPC").updateVelocity(0, 0, 0);
		
		//renderer.getSoundPlayer("MONSTER").play("music");
		renderer.getSoundPlayer("NPC").play("bored");
		
		
		//while(renderer.getSoundPlayer("MONSTER").isPlaying()){
		//	Thread.sleep(1000);
		//}
		
		
	}
}
