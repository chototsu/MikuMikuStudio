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
package com.jme.sound.fmod.objects;

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
    
    protected int getFxChorusID() {
        return fxChorusID;
    }
    

    protected void setFxChorusID(int fxChorusID) {
        this.fxChorusID = fxChorusID;
    }
    

    protected int getFxCompressorID() {
        return fxCompressorID;
    }
    

    protected void setFxCompressorID(int fxCompressorID) {
        this.fxCompressorID = fxCompressorID;
    }
    

    protected int getFxDistorsionID() {
        return fxDistorsionID;
    }
    

    protected void setFxDistorsionID(int fxDistorsionID) {
        this.fxDistorsionID = fxDistorsionID;
    }
    

    protected int getFxEchoID() {
        return fxEchoID;
    }
    

    protected void setFxEchoID(int fxEchoID) {
        this.fxEchoID = fxEchoID;
    }
    

    protected int getFxFlangerID() {
        return fxFlangerID;
    }
    

    protected void setFxFlangerID(int fxFlangerID) {
        this.fxFlangerID = fxFlangerID;
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
    

   
    

    public Configuration(){
        
    }
    
    public void enableFX(){
        fxEnabled=true;
    }

    public boolean isFxEnabled() {
        return fxEnabled;
    }


    protected boolean isChorusEnabled() {
        return chorusEnabled;
    }
    


    public void enableChorus() {
        this.chorusEnabled = true;
    }
    


    protected boolean isCompressorEnabled() {
        return compressorEnabled;
    }
    


    public void enableCompressor() {
        this.compressorEnabled = true;
    }
    


    protected boolean isDistorsionEnabled() {
        return distorsionEnabled;
    }
    


    public void enableDistorsion() {
        this.distorsionEnabled = true;
    }
    


    protected boolean isEchoEnabled() {
        return echoEnabled;
    }
    


    public void enableEcho() {
        this.echoEnabled = true;
    }
    


    protected boolean isEqEnabled() {
        return eqEnabled;
    }
    


    public void enableEq() {
        this.eqEnabled = true;
    }
    


    protected boolean isFlangerEnabled() {
        return flangerEnabled;
    }
    


    public void enableFlanger() {
        this.flangerEnabled = true;
    }
    


    protected boolean isGargleEnabled() {
        return gargleEnabled;
    }
    


    public void enableGargle( ) {
        this.gargleEnabled = true;
    }
    


    protected boolean isI3DL2ReverbEnabled() {
        return I3DL2ReverbEnabled;
    }
    


    public void enableI3DL2Reverb() {
        I3DL2ReverbEnabled = true;
    }
    


    protected boolean isMaxEnabled() {
        return maxEnabled;
    }
    


    public void enableMax() {
        this.maxEnabled = true;
    }
    


    protected boolean isReverbEnabled() {
        return reverbEnabled;
    }
    


    public void enableReverb() {
        this.reverbEnabled = true;
    }
    
    
    
    

}
