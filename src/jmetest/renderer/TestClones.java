/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.CloneCreator;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;

/**
 * Started Date: Sep 16, 2004 <br>
 * <br>
 * 
 * @author Jack Lindamood
 */
public class TestClones extends SimpleGame {

    public static void main(String[] args) {
        TestClones app = new TestClones();
        app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
        app.start();
    }

    CloneCreator cc1;

    CloneCreator cc2;

    protected void simpleInitGame() {
        //        Box b=new Box("box",new Vector3f(0,0,0),new Vector3f(1,1,1));
        Sphere b = new Sphere("my sphere", 25, 25, 2);
        b.setRandomColors();
        b.setModelBound(new BoundingSphere());
        b.updateModelBound();
        rootNode.attachChild(b);

        cc1 = new CloneCreator(b);
        cc1.addProperty("vertices");
        cc1.addProperty("normals");
        cc1.addProperty("colors");
        cc1.addProperty("texcoords");
        cc1.addProperty("indices");
        cc1.addProperty("vboinfo");

        Box c = new Box("my box", new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
        c.setRandomColors();
        c.setModelBound(new BoundingBox());
        c.updateModelBound();
        cc2 = new CloneCreator(c);
        cc2.addProperty("vertices");
        cc2.addProperty("normals");
        cc2.addProperty("colors");
        cc2.addProperty("texcoords");
        cc2.addProperty("indices");
        cc2.addProperty("vboinfo");
        for (int i = 0; i < 45; i++) {
            addRandom();
        }
    }

    private void addRandom() {
        Spatial s1 = cc1.createCopy();
        s1.setLocalTranslation(new Vector3f(
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25));
        rootNode.attachChild(s1);

        Spatial s2 = cc2.createCopy();
        s2.setLocalTranslation(new Vector3f(
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25,
                FastMath.rand.nextFloat() * 50 - 25));
        rootNode.attachChild(s2);
    }
}