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
package com.jme.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.jme.system.JmeException;

/**
 * <code>BinaryFileReader</code>
 * @author Mark Powell
 * @version $Id: BinaryFileReader.java,v 1.1 2004-02-05 22:41:37 mojomonkey Exp $
 */
public class BinaryFileReader {
	private int fileSize;
	private byte[] fileContents;
	private int m_FilePointer = 0;
	
	public BinaryFileReader(File f) {
		try {
			FileInputStream is = new FileInputStream(f);
			fileSize = (int) f.length();
			
			// wrap a buffer to make reading more efficient (faster)
			DataInputStream bis = new DataInputStream(is);
			
			fileContents = new byte[fileSize];
			
			// Read the entire file into memory
			bis.read(fileContents, 0, fileSize);
			bis.close();
		} catch (IOException ioe) {
			throw new JmeException("Could not read: " + f.getName());
		}
	}
	
	public int readByte() {
		int b1 = (fileContents[m_FilePointer] & 0xFF);
		m_FilePointer += 1;
		return (b1);
	}

	public int readShort() {
		int s1 = (fileContents[m_FilePointer] & 0xFF);
		int s2 = (fileContents[m_FilePointer + 1] & 0xFF) << 8;
		m_FilePointer += 2;
		return (s1 | s2);
	}

	public int readInt() {
		int i1 = (fileContents[m_FilePointer] & 0xFF);
		int i2 = (fileContents[m_FilePointer + 1] & 0xFF) << 8;
		int i3 = (fileContents[m_FilePointer + 2] & 0xFF) << 16;
		int i4 = (fileContents[m_FilePointer + 3] & 0xFF) << 24;
		m_FilePointer += 4;
		return (i1 | i2 | i3 | i4);
	}

	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	public String readString(int size) {
		//Look for zero terminated string from byte array
		for (int i = m_FilePointer; i < m_FilePointer + size; i++) {
			if (fileContents[i] == (byte) 0) {
				String s = new String(
						fileContents,
						m_FilePointer,
						i - m_FilePointer);
				m_FilePointer += size;
				return s;
			}
		}
		
		String s = new String(fileContents, m_FilePointer, size);
		m_FilePointer += size;
		return s;
	}
	
	public void setOffset(int offset) {
		m_FilePointer = offset;
	}
}
