/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * <code>ImageUtils</code> is used to for various helper methods based on com.jme.image.Image.
 * 
 * @author Tijl Houtbeckers
 * @version $Id: ImageUtils.java,v 1.1 2006-05-12 17:56:59 llama Exp $
 */

package com.jme.util;

import java.nio.ByteBuffer;

import com.jme.image.Image;
import com.jme.system.JmeException;
import com.jme.util.geom.BufferUtils;

public class ImageUtils {

	/**
	 * This method converts between different Image formats. Right now it only
	 * converts RGB888 to RBGA8888, but in the future it might handle more
	 * formats, such as RGBA8888 to RGBA8888_DXT5.
	 * 
	 * @param source
	 *            The source Image to convert from.
	 * @param convertTo
	 *            The type of Image to convert to. Eg. Image.RGBA8888
	 * @return The newly created converted Image.
	 * 
	 * @throws JmeException
	 *             thrown is the conversion can not be done.
	 */
	public static Image convert(Image source, int convertTo) throws JmeException {

		switch (convertTo) {
		case Image.RGBA8888:
			if (source.getType() == Image.RGB888)
				return _RGB888_to_RGBA8888(source);
			break;
		}
		throw new JmeException("Can not convert to this image format yet (" 
				+ source.getType() + " to " + convertTo	+ ")");
	}

	// conversion code by Tony Vera (Tora)
	private static Image _RGB888_to_RGBA8888(Image rgb888) {
		int size = rgb888.getWidth() * rgb888.getHeight() * 4;

		ByteBuffer rgb = rgb888.getData();

		ByteBuffer rgba8888 = BufferUtils.createByteBuffer(size);
		rgb.rewind();
		for (int j = 0; j < size; j++) {
			if ((j + 1) % 4 == 0) {
				rgba8888.put((byte) 0xFF);
			} else {
				rgba8888.put(rgb.get());
			}
		}
		return new Image(Image.RGBA8888, rgb888.getWidth(), rgb888.getHeight(), rgba8888);
	}
}
