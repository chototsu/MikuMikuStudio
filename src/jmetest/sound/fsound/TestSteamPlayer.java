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
 * Created on 20 avr. 2005
 */
package jmetest.sound.fsound;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import com.jme.sound.fmod.SoundSystem;
import com.jme.util.LoggingSystem;

/**
 * @author Arman
 */
public class TestSteamPlayer {
    
    public static void main(String[] args) throws Exception{
        SoundSystem.init(null, SoundSystem.OUTPUT_DEFAULT);
        //replace the path to your files
        String path=null;
        if(args.length==0){
            path="C:\\Evol\\JAVA\\CLAPTON";
        }else{
            path=args[0];
        }
        File dir=new File(path);
        String[] list=null;
        int[] clip=null;
        if(dir.isDirectory()){
           list=dir.list();
        }else{
            LoggingSystem.getLogger().log(Level.INFO,"The path entered is not a directory");
            LoggingSystem.getLogger().log(Level.INFO,path);
            System.exit(-1);
        }
        ArrayList valid=null;
        if(list !=null && list.length>0){
            valid=new ArrayList();
        }
        else{
            LoggingSystem.getLogger().log(Level.INFO,"The path entered does not contain any file");
            LoggingSystem.getLogger().log(Level.INFO,path);
            System.exit(-1);
        }
        for(int a=0; a<list.length; a++){
                int nb=SoundSystem.createStream(path+"\\"+list[a], false); 
                if(SoundSystem.isStreamOpened(nb)){
                    valid.add(new Integer(nb));
                }
        }
        int nbStream=valid.size();
        if(nbStream>0){
            
            for(int a=0; a<nbStream; a++){
                int music=((Integer)valid.get(a)).intValue();
                int lgth=SoundSystem.getStreamLength(music);
                LoggingSystem.getLogger().log(Level.INFO,"Length "+(lgth/1000/60)+" m "+(lgth/1000%60)+"s");
                SoundSystem.playStream(music);
                while(!(lgth <=0)){
                    Thread.sleep(1000);
                    lgth-=1000;
                    System.out.print("\rRemaining "+(lgth/1000/60)+" m "+(lgth/1000%60)+"s");
                }                
            }
        }
        
        
    }

}
