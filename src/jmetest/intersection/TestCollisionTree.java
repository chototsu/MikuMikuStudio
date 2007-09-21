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

package jmetest.intersection;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

import com.jme.animation.SpatialTransformer;
import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.intersection.CollisionData;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.shape.Sphere;
import com.jme.util.geom.BufferUtils;

/**
 * Started Date: Sep 6, 2004 <br>
 * <br>
 * 
 * @author Jack Lindamood
 */
public class TestCollisionTree extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestCollisionTree.class.getName());
    
	ColorRGBA[] colorSpread = { ColorRGBA.white.clone(), ColorRGBA.green.clone(),
			ColorRGBA.gray.clone() };

	TriMesh s, r;

	Node n, m;

	CollisionResults results;

	CollisionData oldData;

	int count = 0;

	public static void main(String[] args) {
		TestCollisionTree app = new TestCollisionTree();
		app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void simpleInitGame() {
		CollisionTreeManager.getInstance().setTreeType(CollisionTree.AABB_TREE);
		
		results = new TriangleCollisionResults();
		s = new Sphere("sphere", 10, 10, 1);
		
		s.setSolidColor(ColorRGBA.white.clone());
        s.setModelBound(new BoundingBox());
		s.updateModelBound();
		
		n = new Node("sphere node");

		r = new PQTorus("tort", 5, 4, 2f, .5f, 128, 16);
		r.setLocalTranslation(new Vector3f(0, 0, 0));
        r.setSolidColor(ColorRGBA.white.clone());
		r.setModelBound(new BoundingBox());
		r.updateModelBound();

		m = new Node("tort node");

		SpatialTransformer st = new SpatialTransformer(1);
        st.setRepeatType(Controller.RT_CYCLE);
		st.setObject(m, 0, -1);
		st.setPosition(0, 0, new Vector3f(10, 10, 0));
		st.setPosition(0, 4, new Vector3f(-10, -10, 0));
		st.interpolateMissing();
		r.addController(st);

		for(int j = 0; j < r.getBatchCount(); j++) {
			FloatBuffer color1 = r.getBatch(j).getColorBuffer();
			color1.clear();
			for (int i = 0, bLength = color1.capacity(); i < bLength; i+=4) {
			    ColorRGBA c = colorSpread[i % 3];
			    color1.put(c.r).put(c.g).put(c.b).put(c.a);
			}
			color1.flip();
		}
		for(int j = 0; j < s.getBatchCount(); j++) {
			FloatBuffer color2 = s.getBatch(j).getColorBuffer();
			color2.clear();
			for (int i = 0, bLength = color2.capacity(); i < bLength; i+=4) {
			    ColorRGBA c = colorSpread[i % 3];
			    color2.put(c.r).put(c.g).put(c.b).put(c.a);
			}
			color2.flip();
		}

		n.attachChild(r);
		m.attachChild(s);

		rootNode.attachChild(n);
		rootNode.attachChild(m);

		lightState.detachAll();
        lightState.setEnabled(false);
	}

	protected void simpleUpdate() {
		count++;
		if (count < 5)
			return;
		count = 0;
		int[] indexBuffer = new int[3];
		if (oldData != null) {
			if(oldData.getSourceBatchId() == 1) {
				logger.info("1");
			}
			for (int j = 0; j < oldData.getSourceTris().size();j++) {
				int triIndex = oldData.getSourceTris().get(j);
				s.getTriangle(oldData.getSourceBatchId(), triIndex, indexBuffer);
                FloatBuffer color1 = s.getColorBuffer(oldData.getSourceBatchId());
				BufferUtils.setInBuffer(colorSpread[indexBuffer[0] % 3], color1, indexBuffer[0]);
				BufferUtils.setInBuffer(colorSpread[indexBuffer[1] % 3], color1, indexBuffer[1]);
				BufferUtils.setInBuffer(colorSpread[indexBuffer[2] % 3], color1, indexBuffer[2]);
			}

			for (int j = 0; j < oldData.getTargetTris().size();j++) {
				int triIndex = oldData.getTargetTris().get(j);
				r.getTriangle(oldData.getTargetBatchId(), triIndex, indexBuffer);
                FloatBuffer color2 = r.getColorBuffer(oldData.getTargetBatchId());
				BufferUtils.setInBuffer(colorSpread[indexBuffer[0] % 3], color2, indexBuffer[0]);
				BufferUtils.setInBuffer(colorSpread[indexBuffer[1] % 3], color2, indexBuffer[1]);
				BufferUtils.setInBuffer(colorSpread[indexBuffer[2] % 3], color2, indexBuffer[2]);
			}
		}
		

		results.clear();
		m.findCollisions(n, results);

		if (results.getNumber() > 0) {
			oldData = results.getCollisionData(0);
            for (int i = 0; i < oldData.getSourceTris().size(); i++) {
                FloatBuffer color1 = s.getColorBuffer(oldData.getSourceBatchId());
                int triIndex = oldData.getSourceTris().get(i);
                s.getTriangle(oldData.getSourceBatchId(), triIndex, indexBuffer);
                BufferUtils.setInBuffer(ColorRGBA.red, color1, indexBuffer[0]);
                BufferUtils.setInBuffer(ColorRGBA.red, color1, indexBuffer[1]);
                BufferUtils.setInBuffer(ColorRGBA.red, color1, indexBuffer[2]);
            }

            for (int i = 0; i < oldData.getTargetTris().size(); i++) {
                int triIndex = oldData.getTargetTris().get(i);
                FloatBuffer color2 = r.getColorBuffer(oldData.getTargetBatchId());
                r.getTriangle(oldData.getTargetBatchId(), triIndex, indexBuffer);
                BufferUtils.setInBuffer(ColorRGBA.blue, color2, indexBuffer[0]);
                BufferUtils.setInBuffer(ColorRGBA.blue, color2, indexBuffer[1]);
                BufferUtils.setInBuffer(ColorRGBA.blue, color2, indexBuffer[2]);
            }
        }
	}
}