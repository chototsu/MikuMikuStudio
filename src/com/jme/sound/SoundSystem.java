/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound;

/**
 * @author Arman Ozcelik
 *
 */
public abstract class SoundSystem {
	/**
		* The list of current implemented rendering APIs that subclass SoundSystem.
		*/
	public static final String[] rendererNames = { "LWJGL" };

	/**
		 * 
		 * <code>getSoundSystem</code> is a factory method that creates the
		 * appropriate sound system specified by the key parameter. If the
		 * key given is not a valid identifier for a specific display system,
		 * null is returned. For valid sound systems see the
		 * <code>rendererNames</code> array.
		 * @param key the display system to use.
		 * @return the appropriate display system specified by the key.
		 */
	public static SoundSystem getSoundSystem(String key) {
		if ("LWJGL".equalsIgnoreCase(key)) {
			return new LWJGLSoundSystem();
		}
		return null;
	}

	/**
	 * 
	 * @param maxSouces
	 * @param maxBuffers
	 */

	public abstract void createSoundSystem(int maxSouces, int maxBuffers);

	/**
		* <code>getRenderer</code> returns the <code>SoundRenderer</code> implementation
		* that is compatible with the chosen <code>SoundSystem</code>. For 
		* example, if <code>LWJGLSoundSystem</code> is used, the returned 
		* <code>SoundRenderer</code> will be </code>LWJGLSoundRenderer</code>.
		* @see com.jme.sound.SoundRenderer
		* @return the appropriate <code>SoundRenderer</code> implementation that is
		*      compatible with the used <code>SoundSystem</code>.
		*/

	public abstract SoundRenderer getRenderer();

	/**
		* <code>isCreated</code> returns the current status of the sound
		* system. If the sound system and  the sound renderer are created, true is returned,
		* otherwise false.
		* 
		* @return whether the sound system is created.
		*/

	public abstract boolean isCreated();
}
