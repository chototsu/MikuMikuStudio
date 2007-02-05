package jmetest.unit.com.jme.math;

import static org.junit.Assert.assertTrue;

import java.nio.FloatBuffer;

import org.junit.Test;

import com.jme.math.Line;
import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;


public class TestLine {

	/**
	 */
	@Test
	public void testOrthogonalLineFit() {
		Line ls = new Line();
		FloatBuffer points = BufferUtils.createFloatBuffer(18);
		points.put(6).put(3).put(9);
		points.put(16).put(-3).put(0);
		points.put(4.5f).put(8).put(32);
		points.put(0).put(0).put(1);
		points.put(10).put(3).put(76);
		points.put(-8).put(-45).put(-1.5f);
		ls.orthogonalLineFit(points);
		assertTrue(ls.getDirection().equals(new Vector3f(0.12209535f, 0.3713143f, 0.92044467f)));
		assertTrue(ls.getOrigin().equals(new Vector3f(4.75f, -5.666667f, 19.416668f)));
	}
	
	@Test
	public void testDistanceSquared() {
		Line l = new Line();
		l.setDirection(new Vector3f(-1,0,1));
		l.setOrigin(new Vector3f(1,2,3));
		Vector3f point = new Vector3f(10,34,9);
		float distance = l.distanceSquared(point);
		assertTrue(distance == 1141);
	}
}
