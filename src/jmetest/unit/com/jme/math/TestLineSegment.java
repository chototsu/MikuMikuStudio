/**
 * 
 */
package jmetest.unit.com.jme.math;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jme.math.Line;
import com.jme.math.LineSegment;
import com.jme.math.Vector3f;

/**
 * All values tested against are provided via WildMagic 4.0 and David Eberly's 
 * examples.
 * @author mpowell
 *
 */
public class TestLineSegment {

	/**
	 * Test method for {@link com.jme.math.LineSegment#getPositiveEnd(com.jme.math.Vector3f)}.
	 */
	@Test
	public void testGetPositiveEnd() {
		LineSegment ls = new LineSegment();
		ls.setDirection(new Vector3f(1,1,0));
		ls.setOrigin(new Vector3f(0.5f, 1, 9));
		ls.setExtent(4.5f);
		Vector3f store = new Vector3f();
		ls.getPositiveEnd(store);
		assertTrue(store.equals(new Vector3f(5,5.5f,9)));
	}

	/**
	 * Test method for {@link com.jme.math.LineSegment#getNegativeEnd(com.jme.math.Vector3f)}.
	 */
	@Test
	public void testGetNegativeEnd() {
		LineSegment ls = new LineSegment();
		ls.setDirection(new Vector3f(1,1,0));
		ls.setOrigin(new Vector3f(0.5f, 1, 9));
		ls.setExtent(4.5f);
		Vector3f store = new Vector3f();
		ls.getNegativeEnd(store);
		assertTrue(store.equals(new Vector3f(-4,-3.5f,9)));
	}
	
	@Test
	public void testDistanceSquaredPoint() {
		LineSegment ls = new LineSegment();
		ls.setDirection(new Vector3f(-1,0,1));
		ls.setOrigin(new Vector3f(1,2,3));
		ls.setExtent(2);
		Vector3f point = new Vector3f(10,34,9);
		float distance = ls.distanceSquared(point);
		assertTrue(distance == 1137);
	}
	
	@Test
	public void testDistanceSquaredSegment() {
		LineSegment ls = new LineSegment();
		ls.setDirection(new Vector3f(-1,0,1));
		ls.setOrigin(new Vector3f(1,2,3));
		ls.setExtent(2);
		
		LineSegment ls2 = new LineSegment();
		ls2.setDirection(new Vector3f(0,-0.5f,0.345f));
		ls2.setOrigin(new Vector3f(-100,0,-1));
		ls2.setExtent(16);
		
		float distance = ls.distanceSquared(ls2);
		assertTrue(distance == 9835.855f);
	}

}
