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
 * Created on 15 juin 2004
 */
package jmetest.sound;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.sound.SoundAPIController;
import com.jme.sound.SoundPool;
import com.jme.sound.scene.ProgrammableSound;
import com.jme.sound.scene.SoundNode;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * @author Arman
 *  
 */
public class TestProgrammableSound extends SimpleGame {

    private SoundNode snode;
    private ProgrammableSound psound;
    static int EVENT_TO_FIRE=5;//random number
    Box box;

    private int[] programs = new int[2];

    Timer timer;
    float time;
    

    

    protected void simpleInitGame() {
        timer = Timer.getTimer("LWJGL");
        display.setTitle("Test Sound Graph");
        SoundAPIController.getSoundSystem(properties.getRenderer());
        SoundAPIController.getRenderer().setCamera(cam);

        TextureState tst = display.getRenderer().createTextureState();
        tst.setEnabled(true);
        tst.setTexture(TextureManager.loadTexture(
                TestSequencedSound.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
                Texture.FM_LINEAR, true));
        rootNode.setRenderState(tst);

        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);
        box = new Box("Box", min, max);
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -25));
        rootNode.attachChild(box);
        URL[] urls = new URL[1];
        
        urls[0] = TestProgrammableSound.class.getClassLoader().getResource(
                "jmetest/data/sound/ccday02.ogg");

        URL[] urls2 = new URL[1];
        
        urls2[0] = TestProgrammableSound.class.getClassLoader().getResource(
                "jmetest/data/sound/CHAR_CRE_1.ogg");

        programs[0] = SoundPool.compile(urls);
        programs[1] = SoundPool.compile(urls2);
        snode = new SoundNode();
        psound = new ProgrammableSound();
        psound.setNextProgram(programs[0]);
        psound.setLoopingEnabled(true);
        psound.setGain(1f);
        psound.bindEvent(EVENT_TO_FIRE, programs[1]);
        //psound.setQueueCheckPercentage(10);
        
        snode.attachChild(psound);

    }
    
    protected void simpleUpdate() {
        time= timer.getTimeInSeconds();
        if(Math.round(time)%10==0 ){
            fps.print("Fired event");
            snode.onEvent(EVENT_TO_FIRE);
        }
            
            
        
        snode.updateGeometricState(time, true);
    }
    
    protected void simpleRender() {
        SoundAPIController.getRenderer().draw(snode);
    }

    public static void main(String[] args) {
        TestProgrammableSound app = new TestProgrammableSound();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

}