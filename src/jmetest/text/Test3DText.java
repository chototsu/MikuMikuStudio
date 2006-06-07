/*
 * Copyright (c) 2003-2006 jMonkeyEngine All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: * Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. * Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution. *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.text;

import java.awt.Font;

import com.jme.app.SimpleGame;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.CullState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.font2d.Font2D;
import com.jmex.font2d.Text2D;
import com.jmex.font3d.Font3D;
import com.jmex.font3d.Text3D;

/**
 * <code>TestSimpleGame</code>
 * 
 * @author Joshua Slack
 * @version $Id: Test3DText.java,v 1.1 2006-06-07 21:26:50 nca Exp $
 */
public class Test3DText extends SimpleGame {

    public static void main(String[] args) {
        Test3DText app = new Test3DText();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    @Override
    protected void simpleInitGame() {
        display.setTitle("Test Text3D");

        // And some text
        Font3D myfont = new Font3D(new Font("Arial", Font.PLAIN, 2), 0.1, true,
                true, true);
        for (int i = -20; i < 20; i++) {
            Text3D mytext = myfont.createText(
                    "Test Text is good TEXT3D is nice", 2, 0);
            mytext.setFontColor(new ColorRGBA(1, 0, 0, 1));
            mytext.setLocalTranslation(new Vector3f(0, i, 0));
            mytext.setLocalRotation(new Quaternion().fromAngleNormalAxis(
                    FastMath.TWO_PI * (i / 20f), Vector3f.UNIT_Y));
            rootNode.attachChild(mytext);
        }

        // And to make sure text is OK we add some backface culling
        CullState bfculling = DisplaySystem.getDisplaySystem().getRenderer()
                .createCullState();
        bfculling.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(bfculling);

        // Now some 2D text
        {
            Font2D my2dfont = new Font2D();
            Text2D my2dtext = my2dfont.createText(
                    "Here is the 2D text at position (100,100)", 10, 0);
            my2dtext.setLocalTranslation(new Vector3f(100, 100, 0));
            my2dtext.setRenderQueueMode(Renderer.QUEUE_ORTHO);
            ZBufferState zbs = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.CF_ALWAYS);
            my2dtext.setRenderState(zbs);
            rootNode.attachChild(my2dtext);
        }

    }
}