/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights reserved.
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
package jmetest.renderer.loader;

import java.net.URL;

import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.model.ase.ASEModel;

/**
 * <code>TestBackwardAction</code>
 *
 * @author Mark Powell
 * @version $Id: TestAse.java,v 1.9 2004-04-25 03:03:31 mojomonkey Exp $
 */
public class TestAse extends SimpleGame {

  private ASEModel model;

  public static void main(String[] args) {
    TestAse app = new TestAse();
    app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
    app.start();
  }

  protected void simpleInitGame() {
    display.setTitle("ASE Model");
    cam.setLocation(new Vector3f(0.0f, 0.0f, 30.0f));
    cam.update();

    model = new ASEModel("Statue of Liberty");
    URL data = TestAse.class.getClassLoader().getResource(
        "jmetest/data/model/Statue.ase");
    model.load(data, "jmetest/data/model/");

    rootNode.attachChild(model);
  }
}
