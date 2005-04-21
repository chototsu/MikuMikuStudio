/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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
/**
 * Created on Apr 20, 2005
 */
package com.jme.sound.fmod.scene;

public class Configuration {
    
    private boolean fxEnabled;
    private boolean echoEnabled;
    private boolean chorusEnabled;
    private boolean compressorEnabled;
    private boolean distorsionEnabled;
    private boolean flangerEnabled;
    private boolean gargleEnabled;
    private boolean I3DL2ReverbEnabled;
    private boolean eqEnabled;
    private boolean reverbEnabled;
    private boolean maxEnabled;
    private static final int MAX_FX=16;
    
    
    
    //FX Config    
    public static final int FX_CHORUS=0;
    public static final int FX_COMPRESSOR=1;
    public static final int FX_DISTORTION=2;
    public static final int FX_ECHO=3;
    public static final int FX_FLANGER=4;
    public static final int FX_GARGLE=5;
    public static final int FX_I3DL2REVERB=6;
    public static final int FX_PARAMEQ=7;
    public static final int FX_WAVES_REVERB=8;
    public static final int FX_MAX=9;
    


   
    

    public Configuration(){
        
    }
    
    public void enableFX(){
        fxEnabled=true;
    }

    public boolean isFxEnabled() {
        return fxEnabled;
    }


    public boolean isChorusEnabled() {
        return chorusEnabled;
    }
    


    public void enableChorus() {
        this.chorusEnabled = true;
    }
    


    public boolean isCompressorEnabled() {
        return compressorEnabled;
    }
    


    public void enableCompressor() {
        this.compressorEnabled = true;
    }
    


    public boolean isDistorsionEnabled() {
        return distorsionEnabled;
    }
    


    public void enableDistorsion() {
        this.distorsionEnabled = true;
    }
    


    public boolean isEchoEnabled() {
        return echoEnabled;
    }
    


    public void enableEcho() {
        this.echoEnabled = true;
    }
    


    public boolean isEqEnabled() {
        return eqEnabled;
    }
    


    public void enableEq() {
        this.eqEnabled = true;
    }
    


    public boolean isFlangerEnabled() {
        return flangerEnabled;
    }
    


    public void enableFlanger() {
        this.flangerEnabled = true;
    }
    


    public boolean isGargleEnabled() {
        return gargleEnabled;
    }
    


    public void enableGargle( ) {
        this.gargleEnabled = true;
    }
    


    public boolean isI3DL2ReverbEnabled() {
        return I3DL2ReverbEnabled;
    }
    


    public void enableI3DL2Reverb() {
        I3DL2ReverbEnabled = true;
    }
    


    public boolean isMaxEnabled() {
        return maxEnabled;
    }
    


    public void enableMax() {
        this.maxEnabled = true;
    }
    


    public boolean isReverbEnabled() {
        return reverbEnabled;
    }
    


    public void enableReverb() {
        this.reverbEnabled = true;
    }
    
    
    
    

}
