/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound;

/**
 * @author Arman Ozcelik
 *
 */
public interface SoundRenderer {

	/**
	 * TODO Comment
	 * @param bindingName
	 * @param file
	 * @param source
	 */
	public void loadWaveAs(String bindingName, String file, int source);

	/**
	 * TODO Comment
	 * @param sound
	 */
	public void loop(String sound);

	/**
	 * TODO Comment
	 * @param sound
	 */
	public void play(String sound);

	/**
	 * Comment
	 * @param sound
	 * @param xPosition
	 * @param yPosition
	 * @param zPosition
	 * @param xVelocity
	 * @param yVelocity
	 * @param zVelocity
	 */

	public void playFaded(
		String sound,
		float xPosition,
		float yPosition,
		float zPosition,
		float xVelocity,
		float yVelocity,
		float zVelocity);

	/**
	 * TODO Comment
	 * @param sound
	 * @param xPosition
	 * @param yPosition
	 * @param zPosition
	 */

	public void playLocalized(String sound, float xPosition, float yPosition, float zPosition);
}