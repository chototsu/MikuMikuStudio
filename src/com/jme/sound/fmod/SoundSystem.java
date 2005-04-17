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

import java.util.logging.Level;

import org.lwjgl.fmod3.FMOD;
import org.lwjgl.fmod3.FMODException;
import org.lwjgl.fmod3.FSound;

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
    
    private static Listener listener;
    private static Camera camera;
    private static SoundNode[] nodes;
    private static Sample3D[] sample3D;
    

  

    static{
        try {
            LoggingSystem.getLogger().log(Level.INFO,"CREATE FMOD");
            FMOD.create();
            LoggingSystem.getLogger().log(Level.INFO,"INIT FSOUND 44100 32 0");
            FSound.FSOUND_Init(44100, 32, 0);
            LoggingSystem.getLogger().log(Level.INFO,"CREATE LISTENER");
            listener=new Listener();
            
        } catch (FMODException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * init the sound system by setting it's listener's position to the cameras position
     * @param cam
     */
    public static void init(Camera cam){
        camera=cam;
    }
    
    /**
     * Updates the geometric states of all nodes in the scene
     * @param time currently not used 
     */
    public static void update(float time){
        if(nodes==null) return;
        updateListener();
        for(int a=0; a<nodes.length; a++){
            nodes[a].updateWorldData(time);
        }        
    }
    
    /**
     * Updates the geometric states of the given node in the scene
     * @param nodeName the node to update
     * @param time currently not used 
     */
    public static void update(int nodeName, float time){
        if(nodes==null) return;
        if(nodeName<0 || nodeName>=nodes.length) return;
        updateListener();
        nodes[nodeName].updateWorldData(time);
    }
    
    
    private static void updateListener(){
        if(camera==null) return;
        listener.setPosition(camera.getLocation());
        float[] orientation = listener.getOrientation();
        Vector3f dir = camera.getDirection();
        orientation[0] = dir.x;
        orientation[1] = dir.y;
        orientation[2] = dir.z;
        listener.update();
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
    
    public static void setSamplePosition(int sample, float x, float y, float z){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setPosition(x, y, z);
        }
    }
    
    public static void setSampleVelocity(int sample, float x, float y, float z){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setVelocity(x, y, z);
        }
    }
    
    public static void setSampleMaxAudibleDistance(int sample, float dist){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setMaxAudibleDistance(dist);
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
    
    public static void main(String[] args) throws Exception{
        int sampleHandle=SoundSystem.create3DSample("D:/eclipse/workspace/JMonkeyEngine/data/sound/CHAR_CRE_1.ogg", false);
        
        int sampleHandle1=SoundSystem.create3DSample("D:/eclipse/workspace/JMonkeyEngine/data/sound/CHAR_CRE_11.ogg", false);
        
        int nodeHandle=SoundSystem.createSoundNode();
        
        
        
        int nodeHandle1=SoundSystem.createSoundNode();
        
        SoundSystem.addSampleToNode(sampleHandle1, nodeHandle1);
        SoundSystem.addSampleToNode(sampleHandle, nodeHandle);
        
        float y=0;
        SoundSystem.setSampleMaxAudibleDistance(sampleHandle, 100);
            while(true){
                
                SoundSystem.setSamplePosition(sampleHandle, 0,y, 0);
                SoundSystem.update(nodeHandle, 0);
                Thread.sleep(1000);
                SoundSystem.update(nodeHandle1, 0);
                
                
            }
           
    }

    
    

}
