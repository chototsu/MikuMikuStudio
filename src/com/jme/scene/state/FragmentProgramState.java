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

package com.jme.scene.state;

import java.net.URL;


/**
 * <code>FragmentProgramState</code>
 * @author MASTER
 * @version $Id: FragmentProgramState.java,v 1.1 2004-08-20 23:21:19 ericthered Exp $
 */
public abstract class FragmentProgramState extends RenderState {

    protected static float[][] envparameters = new float[96][4];
    
    protected boolean usingParameters = false;
    protected float[][] parameters;
    
    /**
     * <code>setEnvParameter</code> sets an environmental fragment program
     * parameter that is accessable by all fragment programs in memory.
     * @param param four-element array of floating point numbers
     * @param paramID identity number of the parameter, ranging from 0 to 95
     */
    //TODO: Reevaluate how this is done.
    /*public static void setEnvParameter(float[] param, int paramID){
        if (paramID < 0 || paramID > 95)
            throw new IllegalArgumentException("Invalid parameter ID");
        if (param != null && param.length != 4)
            throw new IllegalArgumentException("Vertex program parameters must be of type float[4]");

        envparameters[paramID] = param;
    }*/
    
    public FragmentProgramState() {
        parameters = new float[24][4];
    }
    
    /**
     * <code>setParameter</code> sets a parameter for this fragment program.
     * @param paramID identity number of the parameter, ranging from 0 to 23
     * @param param four-element array of floating point numbers
     */
    public void setParameter(float[] param, int paramID) {
        if (paramID < 0 || paramID > 23)
                throw new IllegalArgumentException("Invalid parameter ID");
        if (param != null && param.length != 4)
                throw new IllegalArgumentException("Fragment program parameters must be of type float[4]");

        usingParameters = true;
        parameters[paramID] = param;
    }
    
    /**
     * <code>isSupported</code> determines if the ARB_fragment_program extension
     * is supported by current graphics configuration.
     * @return if ARB fragment programs are supported
     */
    public abstract boolean isSupported();

    /**
     * @return com.jme.scene.state.RenderState.RS_FRAGMENT_PROGRAM
     * @see com.jme.scene.state.RenderState#getType()
     */
    public int getType() {
        // TODO Auto-generated method stub
        return RS_FRAGMENT_PROGRAM;
    }
    
    /**
     * <code>load</code> loads the fragment program from the specified file.
     * The program must be in ASCII format. We delegate the loading to each
     * implementation because we do not know in what format the underlying API
     * wants the data.
     * @param file text file containing the fragment program
     */
    public abstract void load(URL file);
}
