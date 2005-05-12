/*
 * Created on 10 avr. 2005
 */
package com.jmex.sound.openAL.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.jmex.sound.openAL.objects.util.Buffer;
import com.jmex.sound.openAL.objects.util.SampleLoader;
import com.jmex.sound.openAL.scene.Configuration;
import com.jmex.sound.openAL.scene.SoundSpatial;
import com.jme.util.LoggingSystem;


/**
 * @author Arman
 */
public class Sample3D extends SoundSpatial{

    
    
    private int ray;
    private int min=1;
    private FloatBuffer position=BufferUtils.createFloatBuffer(3);
    private FloatBuffer velocity=BufferUtils.createFloatBuffer(3);
    private Buffer buffer=null;
    private boolean handlesEvent;
    
    
    
    float posx=0;
    private boolean positionChanged=true;
    
    public Sample3D(String file){     
        LoggingSystem.getLogger().log(Level.INFO,"Load file:"+file);
        buffer=SampleLoader.loadBuffer(file);
        generateSource();
    }
    
    public Sample3D(Listener listener, String file){        
        this(file);
        this.listener=listener;
    }
    
    public void setConfiguration(Configuration conf){
        configuration=conf;
    }
    
    public void draw() {  
       if(handlesEvent) return;
       if (distance(listener.getPosition().x,
                listener.getPosition().y, 
                listener.getPosition().z,
                position.get(0),position.get(1), position.get(2)) > ray) {
            if(sourceNumber>=0){
                stop();
            }
        } else {
            if (!isPlaying()) {
                AL10.alSourcef(sourceNumber, AL10.AL_MAX_DISTANCE, ray);
                play();
            }
            
        }
    }
    
    public boolean play(){
        if(positionChanged){
            if(sourceNumber>=0){
                AL10.alSource3f(sourceNumber, AL10.AL_POSITION, position.get(0), position.get(1), position.get(2));
            }
            positionChanged=false;
        }
        AL10.alSourcei(sourceNumber, AL10.AL_BUFFER, buffer.getBufferNumber());
        AL10.alSourcePlay(sourceNumber);
        return true;
    }
    
    public boolean pause(){
        AL10.alSourcePause(sourceNumber);
        return true;
    }
    
    public boolean stop(){
        AL10.alSourceStop(sourceNumber);
        return true;
    }
    
    public boolean isPlaying(){
        return sourceNumber >=0 && (AL10.alGetSourcei(sourceNumber, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING);
    }
    
    public void delete() {
        IntBuffer alSource = BufferUtils.createIntBuffer(1);
        alSource.put(sourceNumber);
        AL10.alDeleteSources(alSource);

    }
    
    
    public void setPosition(float x, float y, float z){
        position.clear();
        position.put(x);
        position.put(y);
        position.put(z);
        positionChanged=true;
        
    }
    
    public void setVelocity(float x, float y, float z){
        AL10.alSource3f(sourceNumber, AL10.AL_VELOCITY, x, y, z);
        
    }
    
    public void setMinDistance(int min){
        this.min=min;
        
    }
    
    public void setVolume(float volume){
        AL10.alSourcef(sourceNumber, AL10.AL_GAIN, volume);
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
        
    }
    
    
    private void generateSource() {
        IntBuffer alSources = BufferUtils.createIntBuffer(1);
        AL10.alGenSources(alSources);
        sourceNumber=alSources.get(0);
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
