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

/*
 * Created on 21 avr. 2005
 */
package com.jmex.sound.fmod.scene;

import com.jmex.sound.fmod.objects.Listener;

/**
 * @author Arman
 */
public abstract class Playable {
    
    protected int playingChannel=-2;
    protected Listener listener;
    protected Configuration configuration;
    protected boolean configured;
    
    private int fxEchoID=-1;
    private int fxChorusID=-1;
    private int fxCompressorID=-1;
    private int fxDistorsionID=-1;
    private int fxFlangerID=-1;
    private int fxGargleID=-1;
    private int fxI3DL2ReverbID=-1;
    private int fxParamEqID=-1;
    private int fxWavesReverbID=-1;
    private int fxMaxID=-1;
    
    
    
    protected int getFxChorusID() {
        return fxChorusID;
    }
    

    protected void setFxChorusID(int fxChorusID) {
        this.fxChorusID = fxChorusID;
    }
    

    protected int getFxCompressorID() {
        return fxCompressorID;
    }
    

    protected void setFxCompressorID(int fxCompressor) {
        this.fxCompressorID = fxCompressor;
    }
    

    protected int getFxDistorsionID() {
        return fxDistorsionID;
    }
    

    protected void setFxDistorsionID(int fxDistorsion) {
        this.fxDistorsionID = fxDistorsion;
    }
    

    protected int getFxEchoID() {
        return fxEchoID;
    }
    

    protected void setFxEchoID(int fxEcho) {
        this.fxEchoID = fxEcho;
    }
    

    protected int getFxFlangerID() {
        return fxFlangerID;
    }
    

    protected void setFxFlangerID(int fxFlanger) {
        this.fxFlangerID = fxFlanger;
    }
    

    protected int getFxGargleID() {
        return fxGargleID;
    }
    

    protected void setFxGargleID(int fxGargleID) {
        this.fxGargleID = fxGargleID;
    }
    

    protected int getFxI3DL2ReverbID() {
        return fxI3DL2ReverbID;
    }
    

    protected void setFxI3DL2ReverbID(int fxI3DL2ReverbID) {
        this.fxI3DL2ReverbID = fxI3DL2ReverbID;
    }
    

    protected int getFxMaxID() {
        return fxMaxID;
    }
    

    protected void setFxMaxID(int fxMaxID) {
        this.fxMaxID = fxMaxID;
    }
    

    protected int getFxParamEqID() {
        return fxParamEqID;
    }
    

    protected void setFxParamEqID(int fxParamEqID) {
        this.fxParamEqID = fxParamEqID;
    }
    

    protected int getFxWavesReverbID() {
        return fxWavesReverbID;
    }
    

    protected void setFxWavesReverbID(int fxWavesReverbID) {
        this.fxWavesReverbID = fxWavesReverbID;
    }
    
    public void resetConfig(){
        configured=false;
    }
    
    
    
    

}
