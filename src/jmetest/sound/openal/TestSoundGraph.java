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

package jmetest.sound.openal;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.sound.openAL.SoundSystem;
import com.jmex.sound.openAL.scene.Configuration;

/**
 * @author Arman Ozcelik
 * @version $Id: TestSoundGraph.java,v 1.12 2006-01-26 21:27:12 Anakan Exp $
 */
public class TestSoundGraph extends SimpleGame {
    private int snode;

    int boxCenter;
    int boxRight;
    int boxLeft;

    int background;

    Box box;
    Box leftBox;
    Box rightBox;

	private float volume;

    public static void main(String[] args) {

        TestSoundGraph app = new TestSoundGraph();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleUpdate() {
    	leftBox.getLocalTranslation().x+=1/leftBox.getLocalTranslation().y;
    	leftBox.getLocalTranslation().y+=1/leftBox.getLocalTranslation().y;
    	
    	if(rightBox.getLocalTranslation().y>0 && rightBox.getLocalTranslation().y<50)
    		rightBox.getLocalTranslation().y-=0.01;
    	if(rightBox.getLocalTranslation().y<0 && rightBox.getLocalTranslation().y>-50)
    		rightBox.getLocalTranslation().y+=0.01;
    	
    	float coord=(float)(Math.pow(-rightBox.getLocalTranslation().y, 2)+25);
    	if(coord>= 0) coord=(float)Math.sqrt(coord);
    	if(coord<0) coord=(float)Math.sqrt(-coord);
    		rightBox.getLocalTranslation().x=coord;
    		
    	
    	
    	
        
    	SoundSystem.setSamplePosition(boxLeft, leftBox.getLocalTranslation().x,
                leftBox.getLocalTranslation().y, leftBox.getLocalTranslation().z);
        SoundSystem.setSamplePosition(boxLeft, rightBox.getLocalTranslation().x,
                rightBox.getLocalTranslation().y, rightBox.getLocalTranslation().z);
        SoundSystem.setStreamVolume(background, volume+=0.0001);
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
        SoundSystem.init(display.getRenderer().getCamera(),
                SoundSystem.OUTPUT_DEFAULT);
        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);
        box = new Box("Box", min, max);
        box.setModelBound(new BoundingSphere());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 10, -50));
        
        leftBox = new Box("Box2", (Vector3f)min.clone(), (Vector3f)max.clone());
        leftBox.setModelBound(new BoundingSphere());
        leftBox.updateModelBound();
        leftBox.setLocalTranslation(new Vector3f(-100, 10, -50));
        
        rightBox = new Box("Box3", (Vector3f)min.clone(), (Vector3f)max.clone());
        rightBox.setModelBound(new BoundingSphere());
        rightBox.updateModelBound();
        rightBox.setLocalTranslation(new Vector3f(100, 10, -50));
        
        TextureState tst = display.getRenderer().createTextureState();
        tst.setEnabled(true);
        tst.setTexture(TextureManager.loadTexture(
                TestSoundGraph.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MM_LINEAR,
                Texture.FM_LINEAR));
        rootNode.setRenderState(tst);
        rootNode.attachChild(box);
        
        rootNode.attachChild(leftBox);
        
        rootNode.attachChild(rightBox);
        snode = SoundSystem.createSoundNode();
        try {

            background = SoundSystem.createStream(TestSoundGraph.class.getClassLoader()
                    .getResource("jmetest/data/sound/test.ogg"));
            boxCenter = SoundSystem.create3DSample(TestSoundGraph.class.getClassLoader()
                    .getResource("jmetest/data/sound/CHAR_CRE_1.ogg"));
            boxRight = SoundSystem.cloneSample(boxCenter);
            boxLeft = SoundSystem.cloneSample(boxCenter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SoundSystem.setStreamLooping(background, true);
        SoundSystem.playStream(background);
        SoundSystem.setStreamVolume(background, 0);
        
        //SoundSystem.setSampleMaxAudibleDistance(background, 1000);
        //SoundSystem.setSampleVolume(background, 0.1f);
        //SoundSystem.addSampleToNode(background, snode);
        
        SoundSystem.setSampleMaxAudibleDistance(boxLeft, 100);
        
        SoundSystem.setSamplePosition(boxLeft, rightBox.getLocalTranslation().x,
                rightBox.getLocalTranslation().y, rightBox.getLocalTranslation().z);
        SoundSystem.setSampleMinAudibleDistance(boxLeft, 4);
        SoundSystem.addSampleToNode(boxLeft, snode);
        
        SoundSystem.setSampleMaxAudibleDistance(boxRight, 100);
        SoundSystem.setSamplePosition(boxRight, leftBox.getLocalTranslation().x,
                leftBox.getLocalTranslation().y, leftBox.getLocalTranslation().z);
        SoundSystem.setSampleMinAudibleDistance(boxRight, 4);
        SoundSystem.addSampleToNode(boxRight, snode);
        
        Configuration config = new Configuration();
        config.setDistortion(-10, 70, 6000, 5000, 7000);

        SoundSystem.setSampleConfig(boxCenter, config);

    }
}
