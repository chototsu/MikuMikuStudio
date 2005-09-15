/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.scene.state.lwjgl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFragmentProgram;
import org.lwjgl.opengl.ARBProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.jme.scene.state.FragmentProgramState;
import com.jme.util.LoggingSystem;

/**
 * @author Eric Woroshow
 * @version $Id: LWJGLFragmentProgramState.java,v 1.1 2004/08/20 23:21:20
 *          ericthered Exp $
 */
public class LWJGLFragmentProgramState extends FragmentProgramState {

	private static final long serialVersionUID = 1L;

	private byte[] program;

	private int programID = -1;

	/**
	 * Determines if the current OpenGL context supports the
	 * GL_ARB_fragment_program extension.
	 * 
	 * @see com.jme.scene.state.FragmentProgramState#isSupported()
	 */
	public boolean isSupported() {
		return GLContext.getCapabilities().GL_ARB_fragment_program;
	}

	/**
	 * Loads the fragment program into a byte array. Note that a
	 * 
	 * @see com.jme.scene.state.VertexProgramState#load(java.net.URL)
	 */
	public void load(java.net.URL file) {
		int next;
		ArrayList bytes = new ArrayList();
		program = new byte[0];

		try {

			InputStream is = new BufferedInputStream(file.openStream());
			while ((next = is.read()) != -1)
				bytes.add(new Byte((byte) next));
			is.close();
			program = new byte[bytes.size()];
			for (int i = 0; i < program.length; i++)
				program[i] = ((Byte) bytes.get(i)).byteValue();

		} catch (Exception e) {
			LoggingSystem.getLogger().log(Level.SEVERE,
					"Could not load fragment program: " + e);
			LoggingSystem.getLogger().throwing(getClass().getName(),
					"load(URL)", e);
		}
	}

	private void create() {
		IntBuffer buf = BufferUtils.createIntBuffer(1);

		ByteBuffer pbuf = BufferUtils.createByteBuffer(program.length);
		pbuf.put(program);
		pbuf.rewind();

		ARBFragmentProgram.glGenProgramsARB(buf);
		ARBFragmentProgram.glBindProgramARB(
				ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, buf.get(0));
		ARBFragmentProgram.glProgramStringARB(
				ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB,
				ARBProgram.GL_PROGRAM_FORMAT_ASCII_ARB, pbuf);

		checkProgramError();

		programID = buf.get(0);
	}

	/**
	 * Queries OpenGL for errors in the fragment program. Errors are logged as
	 * SEVERE, noting both the line number and message.
	 */
	private void checkProgramError() {
		if (GL11.glGetError() == GL11.GL_INVALID_OPERATION) {
			//retrieve the error position
			IntBuffer errorloc = BufferUtils.createIntBuffer(16);
			GL11.glGetInteger(ARBProgram.GL_PROGRAM_ERROR_POSITION_ARB,
					errorloc);

			LoggingSystem
					.getLogger()
					.log(
							Level.SEVERE,
							"Error "
									+ GL11
											.glGetString(ARBProgram.GL_PROGRAM_ERROR_STRING_ARB)
									+ " in fragment program on line "
									+ errorloc.get(0));
		}
	}

	public void apply() {
		if (isSupported()) {
			if (isEnabled()) {

				//Fragment program not yet loaded
				if (programID == -1)
					create();

				GL11.glEnable(ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB);
				ARBProgram.glBindProgramARB(
						ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, programID);

				//load environmental parameters...
				//TODO: Reevaluate how this is done.
				/*
				 * for (int i = 0; i < envparameters.length; i++) if
				 * (envparameters[i] != null)
				 * ARBFragmentProgram.glProgramEnvParameter4fARB(
				 * ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB, i,
				 * envparameters[i][0], envparameters[i][1],
				 * envparameters[i][2], envparameters[i][3]);
				 */

				//load local parameters...
				if (usingParameters) //No sense checking array if we are sure
									 // no parameters are used
					for (int i = 0; i < parameters.length; i++)
						if (parameters[i] != null)
							ARBFragmentProgram.glProgramLocalParameter4fARB(
									ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB,
									i, parameters[i][0], parameters[i][1],
									parameters[i][2], parameters[i][3]);

			} else {
				GL11.glDisable(ARBFragmentProgram.GL_FRAGMENT_PROGRAM_ARB);
			}
		}
	}

}