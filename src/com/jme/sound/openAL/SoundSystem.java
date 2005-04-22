/**
 * Created on Apr 22, 2005
 */
package com.jme.sound.openAL;

import java.util.logging.Level;

import org.lwjgl.openal.AL;


import com.jme.renderer.Camera;
import com.jme.sound.fmod.objects.Listener;
import com.jme.util.LoggingSystem;

public class SoundSystem {
    
    private static Listener listener;
    private static Camera camera;
    
    
    private static int OS_DETECTED;
    private static final int OS_LINUX=1;
    private static final int OS_WINDOWS=2;
    private static final int OS_MAC = 3;
    
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

}
