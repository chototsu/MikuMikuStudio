/*
 * Copyright (c) 2003-2007 jMonkeyEngine
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

package jmetest.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.concurrent.Callable;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.input.KeyInput;
import com.jme.input.controls.GameControl;
import com.jme.input.controls.GameControlManager;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.MultiFaceBox;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;

public class TestMultiFaceBox extends SimpleGame {

    private static final Random RANDOM = new Random();
    private static final Font FONT = new Font("Arial Unicode MS", Font.PLAIN,
            30);
    private Color[] colors = new Color[] { Color.red, Color.green, Color.blue,
            Color.yellow, Color.white, Color.orange };
    private MultiFaceBox box;
    private int[] keys = new int[] { KeyInput.KEY_0, KeyInput.KEY_1,
            KeyInput.KEY_2, KeyInput.KEY_3, KeyInput.KEY_4, KeyInput.KEY_5 };

    private GameControl[] control = new GameControl[6];

    protected void simpleInitGame() {
        box = new MultiFaceBox("box", new Vector3f(), 10, 10, 10);
        GameControlManager manager = new GameControlManager();
        for (int i = 0; i < 6; i++) {
            control[i] = manager.addControl("control" + 1);
            control[i].addBinding(new KeyboardBinding(keys[i]));
        }
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                .createMaterialState();
        ms.setEmissive(ColorRGBA.white.clone());
        box.setRenderState(ms);
        setTexture(box);
        rootNode.attachChild(box);
    }

    public static void main(String... args) {
        TestMultiFaceBox app = new TestMultiFaceBox();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    private void setTexture(final Spatial s) {
        final BufferedImage bi = new BufferedImage(64, 512,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = (Graphics2D) bi.getGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        bg.setFont(FONT);
        for (int i = 0; i < 6; i++) {
            bg.setColor(colors[i]);
            bg.fillRect(0, i * 64, 64, (i + 1) * 64);
            bg.setColor(Color.black);
            bg.drawString("" + i, 28, 64 * i + 38);
        }
        bg.dispose();
        GameTaskQueueManager.getManager().update(new Callable<Object>() {
            public Object call() throws Exception {
                try {
                    TextureState ts = DisplaySystem.getDisplaySystem()
                            .getRenderer().createTextureState();
                    Texture t = TextureManager.loadTexture(bi,
                            Texture.MM_LINEAR, Texture.MM_LINEAR, 1, false);
                    ts.setTexture(t);
                    TextureState oldTs = (TextureState) s
                            .getRenderState(RenderState.RS_TEXTURE);
                    if (oldTs != null) {
                        TextureManager.releaseTexture(oldTs.getTexture());
                        oldTs.deleteAll(true);
                    }
                    s.setRenderState(ts);
                    s.updateRenderState();
                } catch (ConcurrentModificationException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
    }

    @Override
    public void simpleUpdate() {
        for (int i = 0; i < 6; i++) {
            if (control[i].getValue() != 0) {
                System.out.println(i);
                colors[i] = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256),
                        RANDOM.nextInt(256));
                setTexture(box);
            }
        }
    }
}