/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
package jmetest.intersection;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Text;
import com.jme.scene.model.Model;
import com.jme.scene.model.msascii.MilkshapeASCIIModel;
import com.jme.math.FastMath;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.LightState;

/**
 * <code>TestPick</code>
 * @author Mark Powell
 * @version $Id: TestPick.java,v 1.19 2004-06-23 19:15:59 renanse Exp $
 */
public class TestPick extends SimpleGame {

  private Model model;

  /**
   * Entry point for the test,
   * @param args
   */
  public static void main(String[] args) {
    TestPick app = new TestPick();
    app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  /**
   * builds the trimesh.
   * @see com.jme.app.SimpleGame#initGame()
   */
  protected void simpleInitGame() {
    display.setTitle("Mouse Pick");
    cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
    cam.update();
    Text text = new Text("Test Label", "Hits: 0 Shots: 0");
    Text cross = new Text("Crosshairs", "+");
    text.setLocalTranslation(new Vector3f(1, 60, 0));
    cross.setLocalTranslation(new Vector3f( (float) (display.getWidth() / 2f) -
                                           8f,  // 8 is half the width of a font char
                                           (float) (display.getHeight() / 2f) -
                                           8f, 0));

    fpsNode.attachChild(text);
    fpsNode.attachChild(cross);

    model = new MilkshapeASCIIModel("Milkshape Model");
    URL modelURL = TestPick.class.getClassLoader().getResource(
        "jmetest/data/model/msascii/run.txt");
    model.load(modelURL, "jmetest/data/model/msascii/");
    model.getAnimationController().setActive(false);

    Vector3f[] vertex = new Vector3f[1000];
    ColorRGBA[] color = new ColorRGBA[1000];
    for (int i = 0; i < 1000; i++) {
      vertex[i] = new Vector3f();
      vertex[i].x = FastMath.nextRandomFloat() * -100 - 50;
      vertex[i].y = FastMath.nextRandomFloat() * 50 - 25;
      vertex[i].z = FastMath.nextRandomFloat() * 50 - 25;
      color[i] = ColorRGBA.randomColor();
    }

    Line l = new Line("Line Group", vertex, null, color, null);
    l.setModelBound(new BoundingBox());
    l.updateModelBound();
    l.setLightCombineMode(LightState.OFF);

    rootNode.attachChild(l);
    rootNode.attachChild(model);

    MousePick pick = new MousePick(cam, rootNode, text);
    pick.setMouse(input.getMouse());
    input.addAction(pick);
  }

}
