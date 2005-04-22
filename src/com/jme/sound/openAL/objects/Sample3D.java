/*
 * Created on 10 avr. 2005
 */
package com.jme.sound.openAL.objects;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.jcraft.jorbis.Info;
import com.jme.intersection.Distance;
import com.jme.math.Vector3f;

import com.jme.sound.IBuffer;
import com.jme.sound.lwjgl.Source;
import com.jme.sound.openAL.objects.util.Buffer;
import com.jme.sound.openAL.objects.util.SampleLoader;
import com.jme.sound.openAL.scene.Configuration;
import com.jme.sound.openAL.scene.SoundSpatial;
import com.jme.system.JmeException;
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
    
    
    float posx=0;
    
    public Sample3D(String file){     
        LoggingSystem.getLogger().log(Level.INFO,"Load file:"+file);
        buffer=SampleLoader.loadBuffer(file);
        
    }
    
    public Sample3D(Listener listener, String file){        
        this(file);
        this.listener=listener;
    }
    
    public void setConfiguration(Configuration conf){
        configuration=conf;
    }
    
    public void draw() {        
        
        if (distance(listener.getPosition().x,
                listener.getPosition().y, 
                listener.getPosition().z,
                position.get(0),position.get(1), position.get(2)) > ray) {
            if(sourceNumber>=0){
                stop();
                delete();
            }
        } else {
            if (!isPlaying()) {    
                generateSource();
                AL10.alSourcef(sourceNumber, AL10.AL_MAX_DISTANCE, ray);
                play();
            }
        }   
        
    }
    
    public boolean play(){
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
        AL10.alSource3f(sourceNumber, AL10.AL_POSITION, x, y, z);
        
    }
    
    public void setVelocity(float x, float y, float z){
        AL10.alSource3f(sourceNumber, AL10.AL_VELOCITY, x, y, z);
        
    }
    
    public void setMinDistance(int min){
        this.min=min;
        
    }
    
    public void setVolume(int volume){
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
    


    
}
