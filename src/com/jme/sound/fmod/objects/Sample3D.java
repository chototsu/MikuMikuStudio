/*
 * Created on 10 avr. 2005
 */
package com.jme.sound.fmod.objects;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.fmod3.FSound;
import org.lwjgl.fmod3.FSoundSample;

import com.jme.sound.fmod.scene.Configuration;
import com.jme.sound.fmod.scene.SoundSpatial;
import com.jme.util.LoggingSystem;


/**
 * @author Arman
 */
public class Sample3D extends SoundSpatial{

    
    private FSoundSample fmodSample;
    private int ray;
    private int min=1;private FloatBuffer position=BufferUtils.createFloatBuffer(3);
    private FloatBuffer velocity=BufferUtils.createFloatBuffer(3);
    private boolean handlesEvent;
    private int actualVolume=-1;
    
    public static final int METHOD_PAUSE=1;
    public static final int METHOD_STOP=2;
    public static final int METHOD_MUTE=3;
    
    float posx=0;
    private int method;
    
    public Sample3D(String file){     
        fmodSample=FSound.FSOUND_Sample_Load(FSound.FSOUND_UNMANAGED, file, FSound.FSOUND_HW3D |FSound.FSOUND_FORCEMONO | FSound.FSOUND_ENABLEFX, 0, 0);
        LoggingSystem.getLogger().log(Level.INFO,"Load file:"+file+ " Success="+(fmodSample !=null));
    }
    
    public Sample3D(Listener listener, String file, int renderMethod){        
        this(file);
        this.listener=listener;
        this.method=renderMethod;
    }
    
    public void setConfiguration(Configuration conf){
        configuration=conf;
    }
    
    public void setRenderMethod(int method){
        this.method=method;
    }
    
    public void draw() {  
        
        if(handlesEvent) return;
        
        if (distance(listener.getPosition().x,
                listener.getPosition().y, 
                listener.getPosition().z,
                position.get(0),position.get(1), position.get(2)) > ray) {
            if(method==METHOD_PAUSE){
                pause();
            }else
            if(method==METHOD_STOP){
                stop();
            }else 
            if(method==METHOD_MUTE){
                mute();
            }
        } else {
            if (!isPlaying()) {                
                play();
            }else if(method==METHOD_PAUSE && isPlaying() && isPaused()){
                pause();
            }else if(method==METHOD_MUTE && isPlaying()){
                if(actualVolume !=-1){
                    setVolume(actualVolume);
                }
            }
        }   
        
        
    }
    
    public boolean play(){
        if(fmodSample==null){
            return false;
        }
        if((playingChannel=FSound.nFSOUND_PlaySoundEx(FSound.FSOUND_FREE, fmodSample, FSound.FSOUND_DSP_GetSFXUnit(), true)) !=-1){
            configure();
            FSound.FSOUND_SetPriority(playingChannel, 255); 
            FSound.FSOUND_3D_SetDistanceFactor(1);
            FSound.FSOUND_3D_SetMinMaxDistance(playingChannel, min, ray);
            FSound.FSOUND_3D_SetAttributes(playingChannel, position, velocity);
            FSound.FSOUND_SetPaused(playingChannel, false); 
            return true;
        }
       
        return false;
    }
    
    public boolean pause(){
        return FSound.FSOUND_SetPaused(playingChannel, !FSound.FSOUND_GetPaused(playingChannel));
    }
    
    public boolean isPaused(){
        return FSound.FSOUND_GetPaused(playingChannel);
    }
    
    public boolean stop(){
        return FSound.FSOUND_StopSound(playingChannel);
    }
    
    public boolean isPlaying(){
        return FSound.FSOUND_IsPlaying(playingChannel);
    }
    
    public void mute(){
        if(actualVolume==-1){
            actualVolume=FSound.FSOUND_GetVolume(playingChannel);
        }
        setVolume(0);
    }
    

    
    public void setPosition(float x, float y, float z){
        position.clear();
        position.put(x);
        position.put(y);
        position.put(z);   
        position.position(0);
        
    }
    
    public void setVelocity(float x, float y, float z){
        velocity.clear();
        velocity.put(x);
        velocity.put(y);
        velocity.put(z);
        
    }
    
    public void setMinDistance(int min){
        this.min=min;
        FSound.FSOUND_3D_SetMinMaxDistance(playingChannel, min, ray);
        
    }
    
    public void setVolume(int volume){
        FSound.FSOUND_SetVolume(playingChannel, volume);
    }
    
    public void setMaxAudibleDistance(int max){
        ray=max;
    }
    
    private float distance(float ax, float ay, float az, float bx, float by, float bz){
        return (float)Math.sqrt((float)distanceSquared(ax, ay, az, bx,by, bz));
    }
    

    /**
     * <code>distanceSquared</code> returns the distance between two points,
     * with the distance squared. This allows for faster comparisons if relation
     * is important but actual distance is not.
     * @return the distance squared between two points.
     */
    private static float distanceSquared(float ax, float ay, float az, float bx, float by, float bz) {
        return ((ax - bx) * (ax - bx))
                + ((ay - by) * (ay - by))
                + ((az - bz) * (az - bz));
    }
    
    private void configure(){
        if(configuration !=null && !configured){
            if(configuration.isFxEnabled()){
                FSound.FSOUND_SetPaused(playingChannel, true);
                float params[]=null;
                if(configuration.isChorusEnabled()){
                    setFxChorusID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_CHORUS));
                    if(getFxChorusID() !=-1){
                        params=configuration.getChorusParams();
                        FSound.FSOUND_FX_SetChorus(getFxChorusID(), params[0], params[1], params[2], params[3], (int)params[4], params[5],(int)params[6]);
                    }
                }
                if(configuration.isCompressorEnabled()){
                    setFxCompressorID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_COMPRESSOR));
                    if(getFxCompressorID() !=-1){
                        params=configuration.getCompressorParams();
                        FSound.FSOUND_FX_SetCompressor(getFxCompressorID(), params[0], params[1], params[2], params[3], params[4], params[5]);
                    }
                }
                if(configuration.isDistorsionEnabled()){
                    setFxDistorsionID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_DISTORTION));
                    if(getFxDistorsionID() !=-1){
                        params=configuration.getDistorsionParams();
                        FSound.FSOUND_FX_SetDistortion(getFxDistorsionID(), params[0], params[1], params[2],params[3], params[4]);
                    }
                }
                if(configuration.isEchoEnabled()){
                    setFxEchoID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_ECHO));
                    if(getFxEchoID() !=-1){
                        params=configuration.getEchoParams(); 
                        FSound.FSOUND_FX_SetEcho(getFxEchoID(), params[0], params[1], params[2],params[3], (int)params[4]);
                    }
                }
                if(configuration.isEqEnabled()){
                    setFxParamEqID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_PARAMEQ));
                    if(getFxParamEqID() !=-1){
                        params=configuration.getEqParams(); 
                        FSound.FSOUND_FX_SetParamEQ(getFxParamEqID(), params[0], params[1], params[2]);
                    }
                }
                if(configuration.isFlangerEnabled()){
                    setFxFlangerID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_FLANGER));
                    if(getFxFlangerID() !=-1){
                        params=configuration.getFlangerParams();
                        FSound.FSOUND_FX_SetFlanger(getFxFlangerID(), params[0], params[1],params[2],params[3], (int)params[4], params[5],(int)params[6]);
                    }
                }
                if(configuration.isGargleEnabled()){
                    setFxGargleID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_GARGLE));
                    if(getFxGargleID() !=-1){
                        params=configuration.getGargleParams();
                        FSound.FSOUND_FX_SetGargle(getFxGargleID(), (int)params[0], (int)params[0]);
                        
                    }
                }
                if(configuration.isI3DL2ReverbEnabled()){
                    setFxI3DL2ReverbID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_I3DL2REVERB));
                    if(getFxI3DL2ReverbID() !=-1){
                        params=configuration.getI3DLParams();
                        FSound.FSOUND_FX_SetI3DL2Reverb(getFxI3DL2ReverbID(), (int)params[0], (int)params[0], params[0], params[0], params[0], (int)params[0], params[0], (int)params[0],params[0], params[0], params[0], params[0]);
                        
                    }
                }
                if(configuration.isReverbEnabled()){
                    setFxWavesReverbID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_WAVES_REVERB));
                    if(getFxWavesReverbID() !=-1){
                        params=configuration.getReverbParams();
                        FSound.FSOUND_FX_SetWavesReverb(getFxWavesReverbID(), params[0],params[0],params[0],params[0]);
                    }
                }
                if(configuration.isMaxEnabled()){
                    setFxMaxID(FSound.FSOUND_FX_Enable(playingChannel, Configuration.FX_MAX));
                }
            }
            configured=true;
            
        }
    }

    private int[] event;
    private int[] program;
    
    public void bindEvent(int eventNumber) {
        if (event == null) {
            event = new int[1];
            event[0] = eventNumber;
            handlesEvent=true;
            return;
        }
        int[] tmp = new int[event.length + 1];
        System.arraycopy(event, 0, tmp, 0, event.length);
        tmp[event.length] = eventNumber;
        event = tmp;        
    }
    
    /**
     * Used internally for firing an event on this sound playing object.
     * @return true if the event has been fired
     * and false if this sound does not "know" the event
     */
    public boolean fireEvent(int eventNumber) {
        if (event != null && (allowInterrupt || !isPlaying())) {
            for (int i = 0; i < event.length; i++) {
                if (event[i] == eventNumber) {
                    stop();
                    play();
                    return true;
                }
            }
        }
        return false;
    }
    
}
