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

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme.sound.openAL.SoundSystem;
import com.jme.sound.openAL.objects.util.dsp.Equalizer;
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
                final JFileChooser fileChooser=new JFileChooser("C:/SAVE/Apps/ogg");
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
        frame.setVisible(true);        
        
    }

    protected static void startPlayer(JFrame frame, JButton button, File dir) throws Exception{
        Equalizer e=new Equalizer(new int[]{50, 200, 800, 3200, 12800}, -12, 12);
        SoundSystem.setEqualizer(e);
        frame.getContentPane().remove(button);
        JPanel panel=new JPanel();
        JPanel southPanel=new JPanel();
        panel.setLayout(new BorderLayout());
        
        JButton next=new JButton(">>");
        JButton previous=new JButton("<<");
        JSlider sl50=new JSlider(-12, 12, 0);
        sl50.setOrientation(JSlider.VERTICAL);
        sl50.setPaintTicks(true);
        sl50.setMajorTickSpacing(1);
        
        
        JSlider sl200=new JSlider(-12, 12, 0);
        sl200.setOrientation(JSlider.VERTICAL);
        sl200.setPaintTicks(true);
        sl200.setMajorTickSpacing(1);
        
        JSlider sl800=new JSlider(-12, 12, 0);
        sl800.setOrientation(JSlider.VERTICAL);
        sl800.setPaintTicks(true);
        sl800.setMajorTickSpacing(1);
        
        JSlider sl3200=new JSlider(-12, 12, 0);
        sl3200.setOrientation(JSlider.VERTICAL);
        sl3200.setPaintTicks(true);
        sl3200.setMajorTickSpacing(1);
        
        JSlider sl12800=new JSlider(-12, 12, 0);
        sl12800.setOrientation(JSlider.VERTICAL);
        sl12800.setPaintTicks(true);
        sl12800.setMajorTickSpacing(1);
        
        GridLayout southPanelLayout=new GridLayout(1, 5);
        southPanel.setLayout(southPanelLayout);
        southPanel.add(sl50);
        southPanel.add(sl200);
        southPanel.add(sl800);
        southPanel.add(sl3200);
        southPanel.add(sl12800);
        
        NextButtonHandler nextHandler=new NextButtonHandler();
        NextButtonHandler previousHandler=new NextButtonHandler();
        next.addActionListener(nextHandler);
        previous.addActionListener(previousHandler);
        panel.add(next, "East");
        panel.add(previous, "West");
        panel.add(button, "North");       
        panel.add(southPanel, "South");
        frame.getContentPane().add(panel);
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
                    sl50.addChangeListener(new SliderChangeListener(e, nb, 50));
                    sl200.addChangeListener(new SliderChangeListener(e, nb, 200));
                    sl800.addChangeListener(new SliderChangeListener(e, nb, 800));
                    sl3200.addChangeListener(new SliderChangeListener(e, nb, 3200));
                    sl12800.addChangeListener(new SliderChangeListener(e, nb, 12800));
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
                    if(nextHandler.isPressed()){
                        lgth=0;
                        nextHandler.setPressed(false);
                        SoundSystem.stopStream(music);
                    }
                    if(previousHandler.isPressed()){
                        previousHandler.setPressed(false);
                        SoundSystem.stopStream(music);
                        if(a==0) a=-1;
                        else a-=2;
                        lgth=0;
                    }
                    lgth-=1000;
                    
                }                
            }
        }
        
    }
    
    


}

class NextButtonHandler implements ActionListener{
    private boolean pressed;
    
    public void actionPerformed(ActionEvent arg0) {
        pressed=true;
        
    }

    public boolean isPressed() {
        return pressed;
    }
    

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
    
    
}


class SliderChangeListener implements ChangeListener{

    private int stream;
    private int freq;
    private Equalizer eq;
    
    public SliderChangeListener(Equalizer e, int streamNumber, int frequency){
        this.stream=streamNumber;
        this.freq=frequency;
        this.eq=e;
    }

    public void stateChanged(ChangeEvent evt) {
        JSlider slider = (JSlider)evt.getSource();

        if (!slider.getValueIsAdjusting()) {
            // Get new value
            eq.setDBValue(stream, freq, slider.getValue());
        }
    }
    
}
