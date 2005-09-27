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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import jmetest.renderer.loader.TestMilkJmeWrite;

import com.jme.app.SimpleGame;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.util.LoggingSystem;
import com.jmex.model.XMLparser.JmeBinaryReader;
import com.jmex.model.XMLparser.Converters.MilkToJme;

/**
 * <code>TestSharedMesh</code>
 * 
 * @author Mark Powell
 * @version $Id: TestSharedNode.java,v 1.5 2005-09-27 19:10:26 renanse Exp $
 */
public class TestSharedNode extends SimpleGame {

	Node file = null;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LoggingSystem.getLogger().setLevel(java.util.logging.Level.OFF);
		TestSharedNode app = new TestSharedNode();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void simpleUpdate() {
		file.updateGeometricState(tpf, true);
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("jME - Sphere");
		display.getRenderer().setBackgroundColor(ColorRGBA.white);
		display.setMinSamples(4);
		URL MSFile = TestMilkJmeWrite.class.getClassLoader().getResource(
				"jmetest/data/model/msascii/run.ms3d");
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		if (MSFile == null) {
			System.out
					.println("Unable to find milkshape file, did you include jme-test.jar in classpath?");
			System.exit(0);
		}
		MilkToJme convert = new MilkToJme();
		// ByteArrayOutputStream BO=new ByteArrayOutputStream();

		try {
			convert.convert(MSFile.openStream(), BO);
			JmeBinaryReader jbr = new JmeBinaryReader();
			URL TEXdir = TestMilkJmeWrite.class.getClassLoader().getResource(
					"jmetest/data/model/msascii/");
			jbr.setProperty("texurl", TEXdir);
			file = jbr.loadBinaryFormat(new ByteArrayInputStream(BO
					.toByteArray()));
		} catch (IOException e) {
			System.out.println("Damn exceptions:" + e);
		}

		Node n1 = new Node("n1");

		for (int i = 0; i < 100; i++) {
			SharedNode sm = new SharedNode("Share" + i, file);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 200 - 100,
					(float) Math.random() * 200 - 100,
					(float) Math.random() * 200 - 100));
			n1.attachChild(sm);
		}

		n1.setLocalTranslation(new Vector3f(500, 0, 0));
		rootNode.attachChild(n1);

		SharedNode sm1 = new SharedNode("Share1", n1);
		sm1.setLocalTranslation(new Vector3f(200, 100, 200));
		sm1.setLocalScale(new Vector3f(0.5f, 0.5f, 0.5f));
		Matrix3f m = new Matrix3f();
		m.fromAngleAxis(2f, new Vector3f(1, 1, 0));
		sm1.setLocalRotation(m);
		rootNode.attachChild(sm1);

		SharedNode sm2 = new SharedNode("Share1", n1);
		sm2.setLocalTranslation(new Vector3f(-200, 100, 200));
		rootNode.attachChild(sm2);

		SharedNode sm3 = new SharedNode("Share1", n1);
		sm3.setLocalTranslation(new Vector3f(200, 100, -200));
		rootNode.attachChild(sm3);

		SharedNode sm4 = new SharedNode("Share1", n1);
		sm4.setLocalTranslation(new Vector3f(-200, -100, -200));
		rootNode.attachChild(sm4);

	}
}
