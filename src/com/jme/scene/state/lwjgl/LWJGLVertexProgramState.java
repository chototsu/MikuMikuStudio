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
 */
package com.jme.scene.state.lwjgl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.util.Vector;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import com.jme.scene.state.VertexProgramState;
import com.jme.util.LoggingSystem;


/**
 * <code>LWJGLVertexProgramState</code>
 * 
 * @author Eric Woroshow
 * @version $Id: LWJGLVertexProgramState.java,v 1.1 2004-04-02 23:29:02 mojomonkey Exp $
 */
public class LWJGLVertexProgramState extends VertexProgramState {

    private byte[] program;
    private int programID = -1;

    /**
     * Determines if the current OpenGL context supports the
     * GL_ARB_vertex_program extension.
     * @see com.jme.scene.state.VertexProgramState#isSupported()
     */
    public boolean isSupported(){
        return GLContext.GL_ARB_vertex_program;
    }
    
    /**
     * Loads the vertex program into a byte array. Note that a 
     * @see com.jme.scene.state.VertexProgramState#load(java.net.URL)
     */
	public void load(java.net.URL file){
		int next;
		Vector bytes = new Vector();
		program = new byte[0];
		
		try {
		    
            InputStream is = new BufferedInputStream(file.openStream());
        	while((next = is.read()) != -1)
        		bytes.add(new Byte((byte)next));
        	is.close();
        	program = new byte[bytes.size()];
        	for (int i = 0; i < program.length; i++)
                program[i] = ((Byte)bytes.get(i)).byteValue();
            
        } catch (IOException e) {
            LoggingSystem.getLogger().log(Level.WARNING, "Could not load vertex program");
        }
	}
	
    private void create() {
        IntBuffer buf = BufferUtils.createIntBuffer(1);
        
        ByteBuffer pbuf = BufferUtils.createByteBuffer(program.length);
        pbuf.put(program);
        pbuf.rewind();
        
        ARBVertexProgram.glGenProgramsARB(buf);
        ARBVertexProgram.glBindProgramARB(ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, buf.get(0));
        ARBVertexProgram.glProgramStringARB(ARBVertexProgram.GL_VERTEX_PROGRAM_ARB,
                							ARBVertexProgram.GL_PROGRAM_FORMAT_ASCII_ARB,
                							pbuf);
        /*NVVertexProgram.glGenProgramsNV(buf);
        NVVertexProgram.glBindProgramNV(NVVertexProgram.GL_VERTEX_PROGRAM_NV, buf.get(0));
        NVVertexProgram.glLoadProgramNV(NVVertexProgram.GL_VERTEX_PROGRAM_NV, buf.get(0), pbuf);
        NVVertexProgram.glTrackMatrixNV(
                NVVertexProgram.GL_VERTEX_PROGRAM_NV, 0,
                NVVertexProgram.GL_MODELVIEW_PROJECTION_NV, NVVertexProgram.GL_IDENTITY_NV);
        NVVertexProgram.glTrackMatrixNV(
                NVVertexProgram.GL_VERTEX_PROGRAM_NV, 4,
                GL11.GL_MODELVIEW, NVVertexProgram.GL_INVERSE_TRANSPOSE_NV);*/

        programID = buf.get(0);
    }

    public void set() {
        if (isEnabled()) {
            
            //Vertex program not yet loaded
            if (programID == -1)
                create();
            
            ARBVertexProgram.glBindProgramARB(ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, programID);
            GL11.glEnable(ARBVertexProgram.GL_VERTEX_PROGRAM_ARB);
            //GL11.glEnable(NVVertexProgram.GL_VERTEX_PROGRAM_NV);
            //NVVertexProgram.glBindProgramNV(NVVertexProgram.GL_VERTEX_PROGRAM_NV, programID);
            
            //load local parameters...
            if (usingParameters) //No sense checking array if we are sure no parameters are used
	            for (int i = 0; i < parameters.length; i++)
	                if (parameters[i] != null)
	                    ARBVertexProgram.glProgramLocalParameter4fARB(
	                            ARBVertexProgram.GL_VERTEX_PROGRAM_ARB, i,
	                            parameters[i][0], parameters[i][1], parameters[i][2], parameters[i][3]);
	                    //NVVertexProgram.glProgramParameter4fNV(
	                    //        NVVertexProgram.GL_VERTEX_PROGRAM_NV, i,
	                    //        parameters[i][0], parameters[i][1], parameters[i][2], parameters[i][3]);
        }
    }

    public void unset() {
        if (isEnabled())
            GL11.glDisable(ARBVertexProgram.GL_VERTEX_PROGRAM_ARB);
    }
}
