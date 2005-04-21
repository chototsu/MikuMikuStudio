/*
 * Created on 21 avr. 2005
 */
package com.jme.sound.fmod.scene;

import com.jme.sound.fmod.objects.Listener;

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
    
    protected float[] getChorusConfig(){
        return configuration !=null ? configuration.getChorusParams() : null;
    }
    
    

}
