/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound;

import org.lwjgl.openal.AL;

/**
 * @author Arman Ozcelik
 *
 */
public class LWJGLSoundSystem extends SoundSystem {

	private LWJGLSoundRenderer renderer;
	private int maxSources;
	private int maxBuffers;
	private boolean created;
	/**
	 * TODO Comment
	 */
	public void createSoundSystem(int maxSources, int maxBuffers) {
		this.maxSources = maxSources;
		this.maxBuffers = maxBuffers;
		initOpenAL();
		renderer = new LWJGLSoundRenderer(maxBuffers, maxSources);
		created = true;

	}

	/**
	 * TODO Comment
	 */
	public SoundRenderer getRenderer() {
		return renderer;
	}

	/**
	 * TODO Comment
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * TODO Comment
	 */

	private void initOpenAL() {
		try {
			AL.create();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

}
