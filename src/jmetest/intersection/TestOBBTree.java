package jmetest.intersection;


import com.jme.app.SimpleGame;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.intersection.CollisionData;
import com.jme.intersection.CollisionResults;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.animation.SpatialTransformer;
import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Sep 6, 2004 <br>
 * <br>
 * 
 * @author Jack Lindamood
 */
public class TestOBBTree extends SimpleGame {
	ColorRGBA[] colorSpread = { ColorRGBA.white, ColorRGBA.green,
			ColorRGBA.gray };

	TriMesh s, r;
	Node n, m;

	CollisionResults results;
	CollisionData oldData;

	int count = 0;

	public static void main(String[] args) {
		TestOBBTree app = new TestOBBTree();
		app.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}

	protected void simpleInitGame() {
		results = new TriangleCollisionResults();
		s = new Sphere("sphere", 10, 10, 1);
		s.updateCollisionTree();
		s.setModelBound(new BoundingBox());
		s.updateModelBound();
		
		n = new Node("sphere node");

		r = new PQTorus("tort", 5, 4, 2f, .5f, 128, 16);
		r.updateCollisionTree();
		r.setLocalTranslation(new Vector3f(0, 0, 0));
		r.setModelBound(new BoundingBox());
		r.updateModelBound();
		
		m = new Node("tort node");

		SpatialTransformer st = new SpatialTransformer(1);
		st.setObject(m, 0, -1);
		st.setPosition(0, 0, new Vector3f(10, 10, 0));
		st.setPosition(0, 4, new Vector3f(-10, -10, 0));
		st.setPosition(0, 8, new Vector3f(10, 10, 0));
		st.interpolateMissing();
		r.addController(st);

		ColorRGBA[] color1 = r.getColors();
		for (int i = 0; i < color1.length; i++) {
			color1[i] = colorSpread[i % 3];
		}
		r.setColors(color1);
		ColorRGBA[] color2 = s.getColors();
		for (int i = 0; i < color2.length; i++) {
			color2[i] = colorSpread[i % 3];
		}
		s.setColors(color2);

		n.attachChild(r);
		m.attachChild(s);
		
		rootNode.attachChild(n);
		rootNode.attachChild(m);

		lightState.detachAll();
	}

	protected void simpleUpdate() {
		count++;
		if (count < 3)
			return;
		count = 0;

		ColorRGBA[] color1 = s.getColors();
		ColorRGBA[] color2 = r.getColors();
		int[] index1 = s.getIndices();
		int[] index2 = r.getIndices();

		
		if (oldData != null) {

			for (int i = 0; i < oldData.getSourceTris().size(); i++) {
				int triIndex = ((Integer) oldData
						.getSourceTris().get(i)).intValue();
				color1[index1[triIndex * 3 + 0]] = colorSpread[index1[triIndex * 3 + 0] % 3];
				color1[index1[triIndex * 3 + 1]] = colorSpread[index1[triIndex * 3 + 1] % 3];
				color1[index1[triIndex * 3 + 2]] = colorSpread[index1[triIndex * 3 + 2] % 3];
			}
			for (int i = 0; i < oldData.getTargetTris().size(); i++) {
				int triIndex = ((Integer) oldData
						.getTargetTris().get(i)).intValue();
				color2[index2[triIndex * 3 + 0]] = colorSpread[index2[triIndex * 3 + 0] % 3];
				color2[index2[triIndex * 3 + 1]] = colorSpread[index2[triIndex * 3 + 1] % 3];
				color2[index2[triIndex * 3 + 2]] = colorSpread[index2[triIndex * 3 + 2] % 3];
			}
		}

		results.clear();
		m.findCollisions(n, results);

		if (results.getNumber() > 0) {
			oldData = results.getCollisionData(0);
			for (int i = 0; i < results.getCollisionData(0).getSourceTris().size(); i++) {
				int triIndex = ((Integer) results.getCollisionData(0)
						.getSourceTris().get(i)).intValue();
				color1[index1[triIndex * 3 + 0]] = ColorRGBA.red;
				color1[index1[triIndex * 3 + 1]] = ColorRGBA.red;
				color1[index1[triIndex * 3 + 2]] = ColorRGBA.red;
			}
			s.setColors(color1);
			for (int i = 0; i < results.getCollisionData(0).getTargetTris().size(); i++) {
				int triIndex = ((Integer) results.getCollisionData(0)
						.getTargetTris().get(i)).intValue();
				color2[index2[triIndex * 3 + 0]] = ColorRGBA.blue;
				color2[index2[triIndex * 3 + 1]] = ColorRGBA.blue;
				color2[index2[triIndex * 3 + 2]] = ColorRGBA.blue;
			}
			r.setColors(color2);
		}
	}
}