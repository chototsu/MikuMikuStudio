/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.sound.fsound;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.sound.fmod.SoundSystem;
import com.jmex.sound.fmod.scene.Configuration;

/**
 * @author Arman Ozcelik
 * @version $Id: TestSoundGraph.java,v 1.14 2005/02/10 21:48:31 renanse Exp $
 */
public class TestSoundGraph extends SimpleGame {
    private int snode;

    int boxCenter;
    int boxRight;
    int boxLeft;

    int background;

    Box box;
    Box box2;
    Box box3;

    public static void main(String[] args) {
        TestSoundGraph app = new TestSoundGraph();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {
        SoundSystem.update(0.0f);
    }

    protected void simpleRender() {
        SoundSystem.draw(snode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        display.setTitle("Test Sound Graph");
        SoundSystem.init(cam, SoundSystem.OUTPUT_DEFAULT);
        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);
        box = new Box("Box", min, max);
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -50));
        
        box2 = new Box("Box2", (Vector3f)min.clone(), (Vector3f)max.clone());
        box2.setModelBound(new BoundingSphere());
        box2.updateModelBound();
        box2.setLocalTranslation(new Vector3f(100, 10, -50));
        
        box3 = new Box("Box3", (Vector3f)min.clone(), (Vector3f)max.clone());
        box3.setModelBound(new BoundingSphere());
        box3.updateModelBound();
        box3.setLocalTranslation(new Vector3f(-100, 10, -50));
        
        
        TextureState tst = display.getRenderer().createTextureState();
        tst.setEnabled(true);
        tst.setTexture(TextureManager.loadTexture(
                TestSoundGraph.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
                Texture.FM_LINEAR));
        rootNode.setRenderState(tst);
        rootNode.attachChild(box);
        rootNode.attachChild(box2);
        rootNode.attachChild(box3);
        snode = SoundSystem.createSoundNode();
        boxCenter = SoundSystem
                .create3DSample("jmetest/data/sound/CHAR_CRE_1.ogg");
        
        boxRight = SoundSystem.cloneSample(boxCenter);
        boxLeft = SoundSystem.cloneSample(boxCenter);
        background = SoundSystem
                .createStream("jmetest/data/sound/test.ogg", false);
        
        //SoundSystem.setSampleMaxAudibleDistance(background, 1000);
        
        //SoundSystem.addSampleToNode(background, snode);
        
        SoundSystem.setSampleMaxAudibleDistance(boxLeft, 100);
        
        SoundSystem.setSamplePosition(boxLeft, box3.getLocalTranslation().x,
                box3.getLocalTranslation().y, box3.getLocalTranslation().z);
        SoundSystem.setSampleMinAudibleDistance(boxLeft, 4);
        SoundSystem.addSampleToNode(boxLeft, snode);
        
        SoundSystem.setSampleMaxAudibleDistance(boxRight, 100);
        
        SoundSystem.setSamplePosition(boxRight, box2.getLocalTranslation().x,
                box2.getLocalTranslation().y, box2.getLocalTranslation().z);
        SoundSystem.setSampleMinAudibleDistance(boxRight, 4);
        
        SoundSystem.addSampleToNode(boxRight, snode);
       
        Configuration config = new Configuration();
        config.setDistortion(-1, 60, 6000, 1000, 3000);

        //SoundSystem.setSampleConfig(footsteps, config);

    }
}
