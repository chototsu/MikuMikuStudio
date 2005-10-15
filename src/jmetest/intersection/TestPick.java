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

package jmetest.intersection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import jmetest.renderer.loader.TestMilkJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.state.LightState;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MilkToJme;
import com.jmex.model.animation.JointController;

/**
 * <code>TestPick</code>
 * 
 * @author Mark Powell
 * @version $Id: TestPick.java,v 1.25 2005-10-15 18:04:46 irrisor Exp $
 */
public class TestPick extends SimpleGame {

	private Node model;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestPick app = new TestPick();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("Mouse Pick");
		cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
		cam.update();
		Text text = new Text("Test Label", "Hits: 0 Shots: 0");
		Text cross = new Text("Crosshairs", "+");
		text.setLocalTranslation(new Vector3f(1, 60, 0));
		cross.setLocalTranslation(new Vector3f(
				(float) (display.getWidth() / 2f) - 8f, // 8 is half the width
														// of a font char
				(float) (display.getHeight() / 2f) - 8f, 0));

		fpsNode.attachChild(text);
		fpsNode.attachChild(cross);

		MilkToJme converter = new MilkToJme();
		URL MSFile = TestMilkJmeWrite.class.getClassLoader().getResource(
				"jmetest/data/model/msascii/run.ms3d");
		ByteArrayOutputStream BO = new ByteArrayOutputStream();

		try {
			converter.convert(MSFile.openStream(), BO);
		} catch (IOException e) {
			System.out.println("IO problem writting the file!!!");
			System.out.println(e.getMessage());
			System.exit(0);
		}
		JmeBinaryReader jbr = new JmeBinaryReader();
		URL TEXdir = TestMilkJmeWrite.class.getClassLoader().getResource(
				"jmetest/data/model/msascii/");
		jbr.setProperty("texurl", TEXdir);
		model = null;
		try {
			model = jbr.loadBinaryFormat(new ByteArrayInputStream(BO
					.toByteArray()));
		} catch (IOException e) {
			System.out.println("darn exceptions:" + e.getMessage());
		}
		((JointController) model.getChild(0).getController(0)).setActive(false);

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