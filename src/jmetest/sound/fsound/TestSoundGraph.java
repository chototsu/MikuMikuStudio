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

    int footsteps;

    int background;

    Box box;

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
        TextureState tst = display.getRenderer().createTextureState();
        tst.setEnabled(true);
        tst.setTexture(TextureManager.loadTexture(
                TestSoundGraph.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
                Texture.FM_LINEAR));
        rootNode.setRenderState(tst);
        rootNode.attachChild(box);
        snode = SoundSystem.createSoundNode();
        footsteps = SoundSystem
                .create3DSample("jmetest/data/sound/Footsteps.wav");
        background = SoundSystem
                .create3DSample("jmetest/data/sound/test.ogg");
        SoundSystem.setSampleMaxAudibleDistance(footsteps, 100);
        SoundSystem.setSampleMaxAudibleDistance(background, 1000);
        SoundSystem.addSampleToNode(footsteps, snode);
        SoundSystem.addSampleToNode(background, snode);
        SoundSystem.setSamplePosition(footsteps, box.getLocalTranslation().x,
                box.getLocalTranslation().y, box.getLocalTranslation().z);
        SoundSystem.setSampleMinAudibleDistance(footsteps, 4);

        Configuration config = new Configuration();
        config.setDistortion(-10, 70, 6000, 5000, 7000);

        SoundSystem.setSampleConfig(footsteps, config);

    }
}
