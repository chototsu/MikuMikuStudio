/*
 * Created on 18 oct. 2003
 *
 */
package com.jme.sound;

import org.lwjgl.openal.AL;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * $Id: WaveData.java,v 1.1 2003-10-18 15:50:08 Anakan Exp $
 *
 * Utitlity class for loading wavefiles.
 *
 * @author Brian Matzon <brian@matzon.dk>
 * @version $Revision: 1.1 $
 */

public class WaveData {
	/** actual wave data */
	public final ByteBuffer data;

	/** format type of data */
	public final int format;

	/** sample rate of data */
	public final int samplerate;

	/**
	 * Creates a new WaveData
	 * 
	 * @param data actual wavedata
	 * @param format format of wave data
	 * @param samplerate sample rate of data
	 */
	private WaveData(ByteBuffer data, int format, int samplerate) {
		this.data = data;
		this.format = format;
		this.samplerate = samplerate;
	}

	/**
	 * Disposes the wavedata
	 */
	public void dispose() {
		data.clear();
	}

	/**
	 * Creates a WaveData container from the specified filename
	 * 
	 * @param filepath path to file (relative, and in classpath) 
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(String filepath) {
		try {
			return create(
				AudioSystem.getAudioInputStream(
					new BufferedInputStream(new FileInputStream(filepath))));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a WaveData container from the specified bytes
	 *
	 * @param buffer array of bytes containing the complete wave file
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(byte[] buffer) {
		try {
			return create(
				AudioSystem.getAudioInputStream(
					new BufferedInputStream(new ByteArrayInputStream(buffer))));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a WaveData container from the specified stream
	 * 
	 * @param ais AudioInputStream to read from
	 * @return WaveData containing data, or null if a failure occured
	 */
	public static WaveData create(AudioInputStream ais) {
		//get format of data
		AudioFormat audioformat = ais.getFormat();

		// get channels
		int channels = 0;
		if (audioformat.getChannels() == 1) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL.AL_FORMAT_MONO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL.AL_FORMAT_MONO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else if (audioformat.getChannels() == 2) {
			if (audioformat.getSampleSizeInBits() == 8) {
				channels = AL.AL_FORMAT_STEREO8;
			} else if (audioformat.getSampleSizeInBits() == 16) {
				channels = AL.AL_FORMAT_STEREO16;
			} else {
				assert false : "Illegal sample size";
			}
		} else {
			assert false : "Only mono or stereo is supported";
		}

		//read data into buffer
		byte[] buf =
			new byte[audioformat.getChannels()
				* (int) ais.getFrameLength()
				* audioformat.getSampleSizeInBits()
				/ 8];
		int read = 0, total = 0;
		try {
			while ((read = ais.read(buf, total, buf.length - total)) != -1 && total < buf.length) {
				total += read;
			}
		} catch (IOException ioe) {
			return null;
		}

		//insert data into bytebuffer
		ByteBuffer buffer = ByteBuffer.allocateDirect(buf.length);
		buffer.put(buf);
		buffer.rewind();

		//create our result
		WaveData wavedata = new WaveData(buffer, channels, (int) audioformat.getSampleRate());

		//close stream
		try {
			ais.close();
		} catch (IOException ioe) {
		}

		return wavedata;
	}
}
