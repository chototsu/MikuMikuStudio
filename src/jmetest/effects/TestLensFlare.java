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
package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.effects.LensFlare;
import com.jme.light.LightNode;
import com.jme.light.PointLight;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ProjectionState;
import com.jme.scene.state.RenderState;

/**
 * <code>TestLensFlare</code>
 *  First crack at a lens flare for jme.  Notice that currently,
 *  it doesn't do occlusion culling.
 * @author Joshua Slack
 * @version $Id: TestLensFlare.java,v 1.2 2004-04-30 03:38:05 mojomonkey Exp $
 */
public class TestLensFlare extends SimpleGame {

  private LightNode lightNode;

  public static void main(String[] args) {
    TestLensFlare app = new TestLensFlare();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleInitGame() {

    display.setTitle("Lens Flare!");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 200.0f));
    cam.update();
    input.setKeySpeed(100);
    lightState.detachAll();

    PointLight dr = new PointLight();
    dr.setEnabled(true);
    dr.setDiffuse(ColorRGBA.white);
    dr.setAmbient(ColorRGBA.gray);
    dr.setLocation(new Vector3f(0f, 0f, 0f));
    lightState.setTwoSidedLighting(true);

    lightNode = new LightNode("light", lightState);
    lightNode.setLight(dr);

    Vector3f min2 = new Vector3f( -0.5f, -0.5f, -0.5f);
    Vector3f max2 = new Vector3f(0.5f, 0.5f, 0.5f);
    Box lightBox = new Box("box", min2, max2);
    lightBox.setModelBound(new BoundingBox());
    lightBox.updateModelBound();
    lightNode.attachChild(lightBox);
    lightNode.setTarget(rootNode);
    lightNode.setLocalTranslation(new Vector3f( -14f, 14f, -14f));

    // clear the lights from this child:
    lightBox.setRenderState(Spatial.defaultStateList[RenderState.RS_LIGHT]);
    lightBox.setLightCombineMode(LightState.REPLACE);

    LensFlare flare = new LensFlare("flare");
    flare.setLocalTranslation(lightNode.getLocalTranslation());
    ProjectionState projState = display.getRenderer().getProjectionState();
    projState.setEnabled(true);
    projState.setProjection(ProjectionState.PS_ORTHOGRAPHIC);
    projState.setLeftBottom(new Vector2f(-display.getWidth() / 2, -display.getHeight() / 2));
    projState.setRightTop(new Vector2f(display.getWidth() / 2, display.getHeight() / 2));
    flare.setRenderState(projState);
    //lightNode.attachChild(flare);
    Box box = new Box("my box", new Vector3f(0, 0, 0), 10, 10, 10);
    box.setModelBound(new BoundingBox());
    box.updateModelBound();
    rootNode.attachChild(box);
    rootNode.attachChild(lightNode);

    // notice that it comes at the end
    rootNode.attachChild(flare);

  }

}
