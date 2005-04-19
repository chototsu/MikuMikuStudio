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

import com.jme.sound.fmod.SoundSystem;

/**
 * @author Arman
 */
public class TestSteamPlayer {
    
    public static void main(String[] args) throws Exception{
        SoundSystem.init(null, SoundSystem.OUTPUT_DEFAULT);
        int clip=SoundSystem.createStream("C:\\Documents and Settings\\Arman\\Mes documents\\Noir Désir-666.667 Club\\09-Noir_Désir-L'homme_pressé.mp3", false);
        int clip2=SoundSystem.createStream("C:\\Documents and Settings\\Arman\\Mes documents\\Noir Désir-666.667 Club\\10-Noir_Désir-Lazy.mp3", false);
        int lgth=SoundSystem.getStreamLength(clip);
        int lgth2=SoundSystem.getStreamLength(clip2);
        System.out.println("Length "+(lgth/1000/60)+" m "+(lgth/1000%60)+"s");
        SoundSystem.playStream(clip);        
        Thread.sleep(lgth);
        System.out.println("Length "+(lgth2/1000/60)+" m "+(lgth2/1000%60)+"s");
        SoundSystem.playStream(clip2);
    }

}
