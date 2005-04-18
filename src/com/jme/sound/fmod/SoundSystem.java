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
/*
 * Created on 9 avr. 2005
 */
package com.jme.sound.fmod;

import java.nio.FloatBuffer;
import java.util.logging.Level;

import org.lwjgl.BufferUtils;
import org.lwjgl.fmod3.FMOD;
import org.lwjgl.fmod3.FMODException;
import org.lwjgl.fmod3.FSound;
import org.lwjgl.fmod3.FSoundSample;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.sound.fmod.objects.Listener;
import com.jme.sound.fmod.objects.Sample3D;
import com.jme.sound.fmod.scene.SoundNode;
import com.jme.util.LoggingSystem;

/**
 * @author Arman
 */
public class SoundSystem {
    
    public static final int FREE_NODE_INDEX = -1;
    public static final int RENDER_MEHOD_PAUSE=1;
    public static final int RENDER_MEHOD_STOP=2;
    
    public static final int OUTPUT_DEFAULT=0;
    //WINDOZE
    public static final int OUTPUT_DSOUND =1;
    public static final int OUTPUT_WINMM =2;
    public static final int OUTPUT_ASIO =3;
    //LINUZ
    public static final int OUTPUT_OSS =5;
    public static final int OUTPUT_ESD =6;
    public static final int OUTPUT_ALSA =7;
    //MAC
    public static final int OUTPUT_MAC = 8;
    
    private static Listener listener;
    private static Camera camera;
    private static SoundNode[] nodes;
    private static Sample3D[] sample3D;
    private static int OS_DETECTED;
    
    private static final int OS_LINUX=1;
    private static final int OS_WINDOWS=2;
    private static final int OS_MAC = 3;
    
  

    static{
        try {
            LoggingSystem.getLogger().log(Level.INFO,"DETECT OPERATING SYSTEM");
            detectOS();
            LoggingSystem.getLogger().log(Level.INFO,"CREATE FMOD");
            FMOD.create();
            LoggingSystem.getLogger().log(Level.INFO,"CREATE LISTENER");
            listener=new Listener();
            detectOS();
            
        } catch (FMODException e) {
            e.printStackTrace();
        }
    }
    
    private static void detectOS() {
        String osName=System.getProperty("os.name");
        osName=osName.toUpperCase();
        if(osName.startsWith("LINUX")) OS_DETECTED=OS_LINUX;
        if(osName.startsWith("WINDOWS")) OS_DETECTED=OS_WINDOWS;
        if(osName.startsWith("MAC")) OS_DETECTED=OS_MAC;        
    }
    
    /**
     * init the sound system by setting it's listener's position to the cameras position
     * 
     * @param cam
     * @param outputMethod
     */
    public static void init(Camera cam, int outputMethod){
        camera=cam;
        if(outputMethod==OUTPUT_DEFAULT){
            outputMethod=OS_DETECTED;
        }
        switch(outputMethod){
            case OS_LINUX : FSound.FSOUND_SetOutput(FSound.FSOUND_OUTPUT_ALSA);
                break;
            case OS_WINDOWS : FSound.FSOUND_SetOutput(FSound.FSOUND_OUTPUT_DSOUND);
                break;
            case OS_MAC : FSound.FSOUND_SetOutput(FSound.FSOUND_OUTPUT_MAC);
                break;
            
        }
        FSound.FSOUND_SetDriver(0);
        FSound.FSOUND_SetMixer(FSound.FSOUND_MIXER_AUTODETECT);
        LoggingSystem.getLogger().log(Level.INFO,"INIT FSOUND 44100 32 0");
        FSound.FSOUND_Init(44100, 32, 0);
        FSound.FSOUND_3D_SetDistanceFactor(1.0f); 
    }
    


    /**
     * Updates the geometric states of all nodes in the scene
     * @param time currently not used 
     */
    public static void update(float time){
        if(nodes==null) return;
        
        for(int a=0; a<nodes.length; a++){
            nodes[a].updateWorldData(time);
        } 
        updateListener();
        
    }
    
    /**
     * Updates the geometric states of the given node in the scene
     * @param nodeName the node to update
     * @param time currently not used 
     */
    public static void update(int nodeName, float time){
        if(nodes==null) return;
        if(nodeName<0 || nodeName>=nodes.length) return;
        nodes[nodeName].updateWorldData(time);
        updateListener();
        
    }
    
    
    /**
     * Draws all nodes in the scene
     * @param time currently not used 
     */
    public static void draw(){
        if(nodes==null) return;
        for(int a=0; a<nodes.length; a++){
            nodes[a].draw();
        }   
        FSound.FSOUND_Update();
    }
    
    /**
     * Draws the given node in the scene
     * @param nodeName the node to update
     * @param time currently not used 
     */
    public static void draw(int nodeName){
        if(nodes==null) return;
        if(nodeName<0 || nodeName>=nodes.length) return;
        nodes[nodeName].draw();
        FSound.FSOUND_Update();
    }
    
    
    private static void updateListener(){     
        if(camera !=null){
            listener.setPosition(camera.getLocation());            
        }
        float[] orientation = listener.getOrientation();
        Vector3f dir=null;
        Vector3f up=null;
        if(camera !=null){
         dir = camera.getDirection();
         up = camera.getUp();
        }else if(dir==null){
            dir=new Vector3f(0, 0, -1);
        }
        orientation[0] = -dir.x;
        orientation[1] = dir.y;
        orientation[2] = dir.z;
        orientation[3] = up.x;
        orientation[4] = up.y;
        orientation[5] = up.z;
        listener.update();
        //FSound.FSOUND_Update();
    }

    /**
     * Creates a node ans return an integer as it's identifier.
     * @return the node identifier
     */
    public static int createSoundNode(){
        if(nodes==null){
            nodes=new SoundNode[1];
            nodes[0]=new SoundNode();
            return 0;
        }else{
            SoundNode[] tmp=new SoundNode[nodes.length];
            System.arraycopy(nodes, 0, tmp, 0, tmp.length);
            nodes=new SoundNode[tmp.length+1];
            System.arraycopy(tmp, 0, nodes, 0, tmp.length);
            nodes[tmp.length]=new SoundNode();
            return tmp.length;
        }
    }
    
    /**
     * Creates a 3D sample and returns an identifier for it
     * @param file the sample file name
     * @return the 3D sample identifier
     */
    public static int create3DSample(String file, boolean asStream){//as stream is not handled yet
        if(sample3D==null){
            sample3D=new Sample3D[1];
            sample3D[0]=new Sample3D(listener, file);
            return 0;
        }else{
            Sample3D[] tmp=new Sample3D[sample3D.length];
            System.arraycopy(sample3D, 0, tmp, 0, tmp.length);
            sample3D=new Sample3D[tmp.length+1];
            System.arraycopy(tmp, 0, sample3D, 0, tmp.length);
            sample3D[tmp.length]=new Sample3D(listener, file);
            return tmp.length;
        }
    }
    /**
     * Sets the spatial position of a given sample
     * @param sample the sample identifier
     * @param x the x position of the sample
     * @param y the y position of the sample
     * @param z the z position of the sample
     */
    public static void setSamplePosition(int sample, float x, float y, float z){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setPosition(x, y, z);
        }
    }
    
    /**
     * Sets the velocity of a given sample
     * @param sample the sample identifier
     * @param x the x velocity of the sample
     * @param y the y velocity of the sample
     * @param z the z velocity of the sample
     */    
    public static void setSampleVelocity(int sample, float x, float y, float z){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setVelocity(x, y, z);
        }
    }
    
    

    
    /**
     * Sets the units from which the sample will stop playing
     * @param sample the sample identifier
     * @param dist the distance unit from which the sample will stop playing
     */
    public static void setSampleMaxAudibleDistance(int sample, int dist){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setMaxAudibleDistance(dist);
        }
    }
    
    
    public static void setSampleMinAudibleDistance(int sample, int dist){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setMinDistance(dist);
        }
    }
    
    
    /**
     * Adds a sample to the given node identifier
     * @param destNode
     * @param sample
     */
    public static void addSampleToNode(int sample, int destNode){
        if(nodes==null){
            return;
        }else if(sample3D==null){
            return;
        }else if(destNode<0 || destNode>=nodes.length){
            return;            
        }else if(sample<0 || sample>=sample3D.length){
            return;            
        }else{
            nodes[destNode].attachChild(sample3D[sample]);
        }        
    }

    public static void setRolloffFactor(float rolloff){
        FSound.FSOUND_3D_SetRolloffFactor(rolloff);
    } 
    
    /**
     * Set the volume of the given sample
     * @param sample
     * @param volume
     */
    public void setSampleVolume(int sample, int volume){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setVolume(volume);
        }
    }
    
    public static void main(String[] args) throws Exception{
       
        /*
        int sampleHandle=SoundSystem.create3DSample("C:/Evol/eclipse/workspace/FrontJHEAD/data/sound/foot1.wav", false);
        
        int sampleHandle1=SoundSystem.create3DSample("C:/Evol/eclipse/workspace/FrontJHEAD/data/sound/CHAR_CRE_11.ogg", false);
        
        int nodeHandle=SoundSystem.createSoundNode();
        
        
        
        int nodeHandle1=SoundSystem.createSoundNode();
        
        SoundSystem.addSampleToNode(sampleHandle1, nodeHandle1);
        SoundSystem.addSampleToNode(sampleHandle, nodeHandle);
       
        float y=0;
        SoundSystem.setSampleMaxAudibleDistance(sampleHandle, 1000);
            while(true){
                y+=0.05;
                SoundSystem.setSamplePosition(sampleHandle, y,0, 0);
                SoundSystem.update(nodeHandle, 0);
                SoundSystem.draw(nodeHandle);
                //Thread.sleep(1000);
               // SoundSystem.update(nodeHandle1, 0);
               // SoundSystem.draw(nodeHandle1);
                
            }
            */
        
        FSound.FSOUND_SetOutput(FSound.FSOUND_OUTPUT_DSOUND); 
        FSound.FSOUND_SetDriver(0); 
         
        FSound.FSOUND_SetMixer(FSound.FSOUND_MIXER_AUTODETECT); 
        FSound.FSOUND_Init(44100, 32, 0); 
         
        FSoundSample temp  = FSound.FSOUND_Sample_Load( FSound.FSOUND_UNMANAGED, "D:/eclipse/workspace/JMonkeyEngine/data/sound/foot1.wav",  FSound.FSOUND_HW3D | FSound.FSOUND_FORCEMONO, 0, 0); 
        FSound.FSOUND_Sample_SetMinMaxDistance(temp, 4.0f, 100.0f); 
        FSound.FSOUND_Sample_SetMode(temp, FSound.FSOUND_LOOP_NORMAL); 
             
        if (temp==null) 
        { 
             System.out.println("No sound\n"); 
           //MessageBox(0, "no sound", "no sound", MB_OK); 
        } 

        FSound.FSOUND_3D_SetDistanceFactor(1.0f); 
        FSound.FSOUND_SetVolume(FSound.FSOUND_ALL, 255);      // go to max volume 
         
        FloatBuffer pos=BufferUtils.createFloatBuffer(3); 
        FloatBuffer vel=BufferUtils.createFloatBuffer(3); 
        pos.put(0); 
        pos.put(0);  
        pos.put(0);  
         
        int channel = FSound.FSOUND_PlaySound(FSound.FSOUND_FREE ,temp); 
        FSound.FSOUND_SetPriority(channel, 255); 
        FSound.FSOUND_3D_SetAttributes(channel, pos, vel);    
        FSound.FSOUND_SetPaused(channel, false); 
        FSound.FSOUND_SetSurround(channel, true); 
        float y=0;
        while(true){
            pos.clear();
            pos.put(y++);
            pos.rewind();
            FSound.FSOUND_3D_Listener_SetAttributes(pos, vel, 0,0,1,0,1,0);
            FSound.FSOUND_Update();
            Thread.sleep(500);
            System.out.print("\ry="+y);
        }
           
        
    }

    
    

}
