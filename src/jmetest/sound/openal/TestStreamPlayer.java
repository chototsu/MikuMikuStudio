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
package jmetest.sound.openal;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.jme.sound.openAL.SoundSystem;
import com.jme.util.LoggingSystem;

/**
 * @author Arman
 */
public class TestStreamPlayer {
    
    public static void main(String[] args) throws Exception{
        SoundSystem.init(null, SoundSystem.OUTPUT_DEFAULT);
        final JFrame frame=new JFrame();
        final JButton open=new JButton("Select directory");
        frame.getContentPane().add(open);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        open.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){   
                final JFileChooser fileChooser=new JFileChooser();;
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showOpenDialog(null);
                ((JButton)e.getSource()).setEnabled(false);
                try {
                    new Thread(new Runnable(){public void run(){try {
                        startPlayer(frame, open,fileChooser.getSelectedFile());
                    } catch (Exception e) {
                       System.exit(-1);
                    }}}).start();
                    
                } catch (Exception e1) {
                    System.exit(-1);
                    
                }
            }
        });
        frame.setSize(300, 100);
        frame.show();        
        
    }

    protected static void startPlayer(JFrame frame, JButton button, File dir) throws Exception{
        String[] list=null;
        int[] clip=null;
        if(dir !=null && dir.isDirectory()){
           list=dir.list();
        }else{
            
            System.exit(-1);
        }
        ArrayList valid=null;
        ArrayList songs=null;
        if(list !=null && list.length>0){
            valid=new ArrayList();
            songs=new ArrayList();
        }
        else{
            LoggingSystem.getLogger().log(Level.INFO,"The path entered does not contain any file");
            LoggingSystem.getLogger().log(Level.INFO,dir.getAbsolutePath());
            System.exit(-1);
        }
        for(int a=0; a<list.length; a++){
                int nb=SoundSystem.createStream(dir.getAbsolutePath()+File.separator+list[a], false); 
                if(SoundSystem.isStreamOpened(nb)){
                    valid.add(new Integer(nb));
                    songs.add(list[a]);
                }
        }
        int nbStream=valid.size();
        if(nbStream>0){
            System.out.print("Found "+nbStream+" playable songs in this directory");
            for(int a=0; a<nbStream; a++){
                int music=((Integer)valid.get(a)).intValue();
                int lgth=(int)SoundSystem.getStreamLength(music);
                SoundSystem.playStream(music);
                //SoundSystem.setStreamLooping(music, true); 
                while(!(lgth <=0)){
                    button.setText("Playing "+(String)songs.get(a)+" "+(lgth/1000/60)+" m "+(lgth/1000%60)+"s");
                    frame.repaint();
                    button.repaint();
                    frame.pack();
                    Thread.sleep(1000);
                    lgth-=1000;
                    
                }                
            }
        }
        
    }

}
