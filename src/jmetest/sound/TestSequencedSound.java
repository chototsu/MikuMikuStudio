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
 * Created on 25 jan. 2004
 *  
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
import com.jme.sound.scene.SoundNode;
import com.jme.sound.scene.SphericalSound;
import com.jme.util.TextureManager;
import com.jme.util.Timer;

/**
 * @author Arman Ozcelik
 * @version $Id: TestSequencedSound.java,v 1.3 2004-06-14 23:02:15 anakan Exp $
 */
public class TestSequencedSound extends SimpleGame {

    private SoundNode snode;

    SphericalSound sequenced;

    SphericalSound sequenced2;
    
    SphericalSound sequenced3;
    
    SphericalSound sequenced4;
    

    SphericalSound background;

    Box box, box2, box3, box4;

    Timer timer;

    public static void main(String[] args) {
        TestSequencedSound app = new TestSequencedSound();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {
        float time = timer.getTimeInSeconds();
        snode.updateGeometricState(time, true);
        //fps.print("Playing seqence: Duration: " + sequenced.getPlayingTime()
        //        + " millis.(Please go near the box)");

    }

    protected void simpleRender() {

        SoundAPIController.getRenderer().draw(snode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        timer = Timer.getTimer("LWJGL");
        
        
        URL[] urls = new URL[2];
        urls[0] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/foot1.wav");
        urls[1] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/foot2.wav");

        URL[] urls2 = new URL[2];
        urls2[0] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/iagree.wav");
        urls2[1] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/medical.wav");

        URL[] urls3 = new URL[4];
        urls3[0] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/ccday01.ogg");
        urls3[1] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/ccday02.ogg");
        urls3[2] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/ccday03.ogg");
        urls3[3] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/ccday04.ogg");
        
        
        URL[] urls4 = new URL[1];
        urls4[0] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/CHAR_CRE_1.ogg");
        
        URL[] urls5 = new URL[1];
        urls5[0] = TestSequencedSound.class.getClassLoader().getResource(
                "jmetest/data/sound/CHAR_CRE_11.ogg");
        

        display.setTitle("Test Sound Graph");
        SoundAPIController.getSoundSystem(properties.getRenderer());
        SoundAPIController.getRenderer().setCamera(cam);
        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);
        box = new Box("Box", min, max);
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -100));

        box2 = new Box("Box", min, max);
        box2.setModelBound(new BoundingSphere());
        box2.updateModelBound();
        box2.setLocalTranslation(new Vector3f(20, 0, -25));
        
        box3 = new Box("Box", min, max);
        box3.setModelBound(new BoundingSphere());
        box3.updateModelBound();
        box3.setLocalTranslation(new Vector3f(10, 0, -150));
        
        box4 = new Box("Box", min, max);
        box4.setModelBound(new BoundingSphere());
        box4.updateModelBound();
        box4.setLocalTranslation(new Vector3f(50, 0, -200));
        

        TextureState tst = display.getRenderer().getTextureState();
        tst.setEnabled(true);
        tst.setTexture(TextureManager.loadTexture(
                TestSequencedSound.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
                Texture.FM_LINEAR, true));
        rootNode.setRenderState(tst);
        rootNode.attachChild(box);
        rootNode.attachChild(box2);
        rootNode.attachChild(box3);
        rootNode.attachChild(box4);
        snode = new SoundNode();
        

        sequenced = new SphericalSound(urls4);
        sequenced.setQueueCheckPercentage(1);
        sequenced.setMaxDistance(10);
        sequenced.setRolloffFactor(.1f);
        sequenced.setPosition(box.getLocalTranslation());
        sequenced.setGain(1.0f);
        sequenced.setLoopingEnabled(true);
        
        
        sequenced3 = new SphericalSound(urls5);
        sequenced3.setQueueCheckPercentage(1);
        sequenced3.setMaxDistance(20);
        sequenced3.setRolloffFactor(.1f);
        sequenced3.setPosition(box3.getLocalTranslation());
        sequenced3.setGain(1.0f);
        sequenced3.setLoopingEnabled(false);
        
        sequenced4 = new SphericalSound(urls);
        sequenced4.setQueueCheckPercentage(1);
        sequenced4.setMaxDistance(25);
        sequenced4.setRolloffFactor(.1f);
        sequenced4.setPosition(box4.getLocalTranslation());
        sequenced4.setGain(1.0f);
        sequenced4.setLoopingEnabled(false);
        
        
        //sequenced.setLooping(true);
        snode.attachChild(sequenced);
        sequenced2 = new SphericalSound(urls2);
        sequenced2.setQueueCheckPercentage(1);
        sequenced2.setMaxDistance(10);
        sequenced2.setRolloffFactor(.1f);
        sequenced2.setPosition(box2.getLocalTranslation());
        sequenced2.setGain(1.0f);
        sequenced2.setLoopingEnabled(true);

        background = new SphericalSound(urls3);
        background.setLoopingEnabled(true);
        background.setGain(0.5f);
        snode.attachChild(sequenced2);
        snode.attachChild(sequenced3);
        snode.attachChild(sequenced4);
        snode.attachChild(background);

    }
}