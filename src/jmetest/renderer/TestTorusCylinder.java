/*
* Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
* All rights reserved.
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
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS &quot;AS IS&quot;
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
package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.ZBufferState;

/**
 * <code>TestTorusCylinder</code>
 * @author Administrator
 * @version $Id: TestTorusCylinder.java,v 1.1 2005-04-19 19:59:13 renanse Exp $
 */
public class TestTorusCylinder extends SimpleGame {

    public static void main(String[] args) {
        TestTorusCylinder app = new TestTorusCylinder();
        app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    protected void simpleInitGame() {
        rootNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        CullState cs = display.getRenderer().createCullState();
        cs.setCullMode(CullState.CS_BACK);
        rootNode.setRenderState(cs);

        Cylinder cylinder = new Cylinder("Cylinder", 20, 30, 4.75f, 20f);
        MaterialState ms1 = display.getRenderer().createMaterialState();
        ms1.setEnabled(true);
        ms1.setDiffuse(new ColorRGBA(0,1,0,.8f));
        ms1.setShininess(128);
        cylinder.setRenderState(ms1);
        rootNode.attachChild(cylinder);

        Torus torus = new Torus("Torus", 20, 50, 5, 10);
        rootNode.attachChild(torus);
        MaterialState ms3 = display.getRenderer().createMaterialState();
        ms3.setEnabled(true);
        ms3.setDiffuse(new ColorRGBA(1,0,0,.8f));
        ms3.setShininess(128);
        torus.setRenderState(ms3);

        AlphaState as = display.getRenderer().createAlphaState();
        as.setEnabled(true);
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_DST_ALPHA);
        rootNode.setRenderState(as);

        ZBufferState zb = display.getRenderer().createZBufferState();
        zb.setWritable(false);
        zb.setEnabled(false);
        rootNode.setRenderState(zb);
    }

}
