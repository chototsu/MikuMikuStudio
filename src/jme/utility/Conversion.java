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
package jme.utility;

/**
 * <code>Conversion</code> provides static methods for unit conversion.
 * 
 * Special thanks to Chman's <a href="http://chman-area.tuxfamily.org">site</a> for help.
 * @author Mark Powell
 *
 */
public class Conversion {
	/**
	 * <code>byte2short</code> converts two bytes to a short. The 
	 * bytes are contained in an array with the starting index provided.
	 * Two bytes are read (index and index+1). 
	 * 
	 * @param bytes the bytes to convert.
	 * @param index the pointer to the start of the bytes.
	 * @return the short value defined by the two bytes.
	 */
	public final static short byte2short(byte[] bytes, int index) {
		int s1 = (bytes[index] & 0xFF);
		int s2 = (bytes[index + 1] & 0xFF) << 8;
		return (short)(s1 | s2);
	}

	/**
	 * <code>byte2int</code> converts four bytes to an int. The
	 * bytes are contained in an array with the starting index
	 * provided. Four bytes are read (index, index+1, index+2, and 
	 * index+3).
	 * @param bytes the bytes to convert.
	 * @param index the pointer to the start of the bytes.
	 * @return the int value defined by the four bytes.
	 */
	public final static int byte2int(byte[] bytes, int index) {
		int i1 = (bytes[index] & 0xFF);
		int i2 = (bytes[index + 1] & 0xFF) << 8;
		int i3 = (bytes[index + 2] & 0xFF) << 16;
		int i4 = (bytes[index + 3] & 0xFF) << 24;
		return (i1 | i2 | i3 | i4);
	}

	/**
	 * <code>byte2float</code> converts four bytes to a float by
	 * first converting the bytes into and int and then converting
	 * that into a float using <code>Float.intBitsToFloat</code>.
	 * @param bytes the bytes to convert.
	 * @param index the pointer to the start of the bytes.
	 * @return the int value defined by the four bytes.
	 */
	public final static float byte2float(byte[] bytes, int index) {
		return Float.intBitsToFloat(byte2int(bytes, index));
	}
	
	/**
	 * <code>byte2String</code> converts an array of bytes into
	 * a string. The bytes are read until a trailing zero is 
	 * encountered. Leading zeros are ignored.
	 * @param b the bytes to convert to a String.
	 * @return the String value of the given bytes.
	 */
	public final static String byte2String(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			if (b[i] == (byte) 0) {
				return new String(b, 0, i);
			}
		}
		return new String(b);
		
	}
}
