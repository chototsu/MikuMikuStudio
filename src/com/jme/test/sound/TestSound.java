/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.test.sound;

import com.jme.sound.IEffectPlayer;
import com.jme.sound.IRenderer;
import com.jme.sound.SoundSystem;
import com.jme.sound.utils.EffectRepository;



/**
 * @author Arman Ozcelik
 *
 */
public class TestSound{
	
	
	public static void main(String[] args) throws InterruptedException {
		SoundSystem system=SoundSystem.getSoundEffectSystem("LWJGL");
		IRenderer renderer=system.getRenderer();
		
		renderer.addSoundPlayer("NPC");
		
		renderer.loadSoundAs("music", "../data/sound/walk.mp3");
		
		
		
		//renderer.getSoundPlayer("MONSTER").play("music");
		renderer.getSoundPlayer("NPC").play(EffectRepository.getRepository().getSource("music"));
		
		
		while(renderer.getSoundPlayer("NPC").getStatus()==IEffectPlayer.PLAYING){
			Thread.sleep(1000);
		}
		
		
	}
}
