/**
 * Created on Apr 22, 2005
 */
package com.jme.sound.openAL;

import java.util.logging.Level;

import org.lwjgl.openal.AL;

import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.sound.openAL.objects.Listener;
import com.jme.sound.openAL.objects.MusicStream;
import com.jme.sound.openAL.objects.Sample3D;
import com.jme.sound.openAL.scene.Configuration;
import com.jme.sound.openAL.scene.SoundNode;
import com.jme.util.LoggingSystem;

public class SoundSystem {
    
    private static Listener listener;
    private static Camera camera;
    
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
    
    private static int OS_DETECTED;
    private static final int OS_LINUX=1;
    private static final int OS_WINDOWS=2;
    private static final int OS_MAC = 3;
    
    private static SoundNode[] nodes;
    private static Sample3D[] sample3D;
    private static MusicStream[] stream;
    
    static{
        
            LoggingSystem.getLogger().log(Level.INFO,"DETECT OPERATING SYSTEM");
            detectOS();
            LoggingSystem.getLogger().log(Level.INFO,"CREATE OPENAL");
            initializeOpenAL();            
            LoggingSystem.getLogger().log(Level.INFO,"CREATE LISTENER");
            listener=new Listener();
    }
    
    /**
     *
     */
    private static void initializeOpenAL() {
        try {
            AL.create();
            LoggingSystem.getLogger().log(Level.INFO, "OpenAL initalized!");
        } catch (Exception e) {
            LoggingSystem.getLogger().log(Level.SEVERE,
                    "Failed to Initialize OpenAL...");
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
            case OS_LINUX : 
                break;
            case OS_WINDOWS : 
                break;
            case OS_MAC : 
                break;
            
        }
        
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
            up=new Vector3f(0, 1, 0);
        }
        orientation[0] = -dir.x;
        orientation[1] = dir.y;
        orientation[2] = dir.z;
        orientation[3] = up.x;
        orientation[4] = up.y;
        orientation[5] = up.z;
        listener.update();
        
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
    public static int create3DSample(String file){
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
     * Creates a Music stream and returns an identifier for it
     * @param file streaming file name
     * @param loadIntoMemory
     * @return the stream identifier
     */
    public static int createStream(String file, boolean loadIntoMemory){
        if(stream==null){
            stream=new MusicStream[1];
            stream[0]=new MusicStream(file, loadIntoMemory);
            return 0;
        }else{
            MusicStream[] tmp=new MusicStream[stream.length];
            System.arraycopy(stream, 0, tmp, 0, tmp.length);
            stream=new MusicStream[tmp.length+1];
            System.arraycopy(tmp, 0, stream, 0, tmp.length);
            stream[tmp.length]=new MusicStream(file, loadIntoMemory);
            return tmp.length;
        }
    }
   
    /**
     * Checks if a stream is opened. If it is, this can be used to know
     * that the file is really a audio file  
     * @param streamName
     * @return true if the stream is opened
     */
    public static boolean isStreamOpened(int streamName){
        if(stream==null){
            return false;
        }else if(streamName<0 || streamName>=stream.length){
            return false; 
        }else{
            return stream[streamName].isOpened();
        }
    }
    
    
    /**
     * Get the length of the given stream in milliseconds
     * @param streamName
     * @return the stream length in millis
     */
    public static int getStreamLength(int streamName){
        if(stream==null){
            return -1;
        }else if(streamName<0 || streamName>=stream.length){
            return -1; 
        }else{
            return stream[streamName].length();
        }
    }
    
    public static boolean playStream(int streamName){
        if(stream==null){
            return false;
        }else if(streamName<0 || streamName>=stream.length){
            return false; 
        }else{
            return stream[streamName].play();
        }
    }
    
    
    
    
    public static boolean pauseStream(int streamName){
        if(stream==null){
            return false;
        }else if(streamName<0 || streamName>=stream.length){
            return false; 
        }else{
            return stream[streamName].pause();
        }
    }
    
    public static void stopStream(int streamName){
        if(stream==null){
            return ;
        }else if(streamName<0 || streamName>=stream.length){
            return ; 
        }else{
            stream[streamName].stop();
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
     * Set the FX configuration of the given sample
     * @param sample sample the sample identifier
     * @param conf the config
     */
    public static void setSampleConfig(int sample, Configuration conf){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setConfiguration(conf); 
        }
    }
    
    
    /**
     * Set the FX configuration of the given stream
     * @param sample stream the sample identifier
     * @param conf the config
     */
    public static void setStreamConfig(int streamName, Configuration conf){
        if(stream==null){
            return;
        }else if(streamName<0 || streamName>=stream.length){
            return; 
        }else{
            stream[streamName].setConfiguration(conf);
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
    } 
    
    /**
     * Set the volume of the given sample
     * @param sample
     * @param volume
     */
    public static void setSampleVolume(int sample, float volume){
        if(sample3D==null){
            return;
        }else if(sample<0 || sample>=sample3D.length){
            return; 
        }else{
            sample3D[sample].setVolume(volume);
        }
    }

    
    
    
    
    


}
