/*
 * Created on 10 avr. 2005
 */
package com.jme.sound.fmod.objects;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.fmod3.FSound;
import org.lwjgl.fmod3.FSoundSample;

import com.jme.intersection.Distance;
import com.jme.math.Vector3f;
import com.jme.sound.fmod.scene.SoundSpatial;
import com.jme.util.LoggingSystem;


/**
 * @author Arman
 */
public class Sample3D extends SoundSpatial{

    
    private FSoundSample fmodSample;
    private float ray;
    private float min;
    private Listener listener;
    private int playingChannel=-2;
    private FloatBuffer position=BufferUtils.createFloatBuffer(3);
    private FloatBuffer velocity=BufferUtils.createFloatBuffer(3);
    
    float posx=0;
    
    public Sample3D(String file){     
        LoggingSystem.getLogger().log(Level.INFO,"Load file:"+file);
        fmodSample=FSound.FSOUND_Sample_Load(FSound.FSOUND_FREE, file, FSound.FSOUND_HW3D , 0, 0);
        
    }
    
    public Sample3D(Listener listener, String file){        
        this(file);
        this.listener=listener;
        
        
    }
    
    public void draw() {
        FSound.FSOUND_3D_SetAttributes(playingChannel, position, velocity);
        System.out.println("sx="+position.get(0)+" sy="+position.get(1)+" sz="+position.get(2));
        //FSound.FSOUND_3D_SetMinMaxDistance(playingChannel, (int)min, (int)ray);
        if (distance(listener.getPosition().x,
                listener.getPosition().y, 
                listener.getPosition().z,
                position.get(0),position.get(1), position.get(2)) > ray) {
            stop();
        } else {
            if (!isPlaying()) {                
                play();
            }
        }        
    }
    
    public boolean play(){
        if(fmodSample==null){
            return false;
        }
        if((playingChannel=FSound.FSOUND_PlaySound(FSound.FSOUND_FREE, fmodSample)) !=1){
            //FSound.FSOUND_3D_SetMinMaxDistance(playingChannel, 4, 10000);
            return true;
        }
        return false;
    }
    
    public boolean pause(){
        return FSound.FSOUND_SetPaused(playingChannel, !FSound.FSOUND_GetPaused(playingChannel));
    }
    
    public boolean stop(){
        return FSound.FSOUND_StopSound(playingChannel);
    }
    
    public boolean isPlaying(){
        return FSound.FSOUND_IsPlaying(playingChannel);
    }
    

    
    public void setPosition(float x, float y, float z){
        position.clear();
        position.put(x);
        position.put(y);
        position.put(z);        
        
    }
    
    public void setVelocity(float x, float y, float z){
        velocity.clear();
        velocity.put(x);
        velocity.put(y);
        velocity.put(z);
        
    }
    
    public void setMinDistance(float min){
        this.min=min;
        
    }
    
    public void setMaxAudibleDistance(float max){
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
    

    
}
