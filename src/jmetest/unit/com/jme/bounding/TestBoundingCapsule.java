package jmetest.unit.com.jme.bounding;

import static org.junit.Assert.assertTrue;

import java.nio.FloatBuffer;

import org.junit.Test;

import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.math.LineSegment;
import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;


public class TestBoundingCapsule {

	@Test
	public void testOrthogonalLineFit() {
		FloatBuffer points = BufferUtils.createFloatBuffer(18);
		points.put(6).put(3).put(9);
		points.put(16).put(-3).put(0);
		points.put(4.5f).put(8).put(32);
		points.put(0).put(0).put(1);
		points.put(10).put(3).put(76);
		points.put(-8).put(-45).put(-1.5f);
		
		BoundingCapsule bc = new BoundingCapsule();
		bc.computeFromPoints(points);
		
		assertTrue(bc.getRadius() == 29.883142f);
		assertTrue(bc.getLineSegment().getOrigin().equals(new Vector3f(4.367725f, -6.829235f, 16.534798f)));
		assertTrue(bc.getLineSegment().getDirection().equals(new Vector3f(0.12209535f, 0.3713143f, 0.92044467f)));
		assertTrue(bc.getLineSegment().getExtent() == 32.27561f);
	}
	
	@Test
	public void testContainsSphere() {
		FloatBuffer points = BufferUtils.createFloatBuffer(18);
		points.put(6).put(3).put(9);
		points.put(16).put(-3).put(0);
		points.put(4.5f).put(8).put(32);
		points.put(0).put(0).put(1);
		points.put(10).put(3).put(76);
		points.put(-8).put(-45).put(-1.5f);
		
		BoundingCapsule bc = new BoundingCapsule();
		bc.computeFromPoints(points);
		
		BoundingSphere sphere = new BoundingSphere();
		sphere.setCenter(new Vector3f(4,0,10));
		sphere.setRadius(15);
		
		assertTrue(bc.contains(sphere));
	}
	
	@Test
	public void testContainsCapsule() {
		FloatBuffer points = BufferUtils.createFloatBuffer(18);
		points.put(6).put(3).put(9);
		points.put(16).put(-3).put(0);
		points.put(4.5f).put(8).put(32);
		points.put(0).put(0).put(1);
		points.put(10).put(3).put(76);
		points.put(-8).put(-45).put(-1.5f);
		
		BoundingCapsule bc = new BoundingCapsule();
		bc.computeFromPoints(points);
		
		BoundingCapsule test = new BoundingCapsule();
		LineSegment ls = new LineSegment();
		ls.setOrigin(new Vector3f(4,0,10));
		ls.setDirection(new Vector3f(1,0,0));
		ls.setExtent(5);
		test.setRadius(15);
		
		assertTrue(bc.contains(test));
	}
	
	@Test
	public void testMerge() {
		FloatBuffer points = BufferUtils.createFloatBuffer(18);
		points.put(6).put(3).put(9);
		points.put(16).put(-3).put(0);
		points.put(4.5f).put(8).put(32);
		points.put(0).put(0).put(1);
		points.put(10).put(3).put(76);
		points.put(-8).put(-45).put(-1.5f);
		
		BoundingCapsule bc = new BoundingCapsule();
		bc.computeFromPoints(points);
		
		FloatBuffer points2 = BufferUtils.createFloatBuffer(18);
		points2.put(1).put(1).put(1);
		points2.put(-9).put(-3).put(10);
		points2.put(2.3f).put(-8).put(-32);
		points2.put(-100).put(0).put(1);
		points2.put(40).put(-32).put(-76);
		points2.put(-18).put(45).put(1.5f);
		
		BoundingCapsule bc2 = new BoundingCapsule();
		bc2.computeFromPoints(points2);
		
		BoundingCapsule bc3 = bc.mergeCapsule(bc2, new BoundingCapsule());
		
//		assertTrue(bc3.getRadius() == 87.8737);
//		System.out.println(bc3.getLineSegment().getOrigin());
//		System.out.println(bc3.getLineSegment().getDirection());
//		System.out.println(bc3.getLineSegment().getExtent());
	}
	
	@Test
	public void testMerge2() {
		BoundingCapsule bc = new BoundingCapsule();
		LineSegment ls = new LineSegment();
		ls.setExtent(1.1928054f);
		ls.setDirection(new Vector3f(0.4086107f, -0.21022895f, 0.88816714f));
		ls.setOrigin(new Vector3f(9.92369f, 3.9846199f, 0.9370832f));
		bc.setLineSegment(ls);
		bc.setRadius(2.5043542f);
		
		BoundingCapsule bc2 = new BoundingCapsule();
		LineSegment ls2 = new LineSegment();
		ls2.setExtent(10);
		ls2.setDirection(new Vector3f(-1,0,0));
		ls2.setOrigin(new Vector3f(25,10,1));
		bc2.setLineSegment(ls2);
		bc2.setRadius(2.828427f);
		
		BoundingCapsule bc3 = bc2.mergeCapsule(bc, new BoundingCapsule());
		
		System.out.println(bc3.getRadius());
		System.out.println(bc3.getLineSegment().getOrigin());
		System.out.println(bc3.getLineSegment().getDirection());
		System.out.println(bc3.getLineSegment().getExtent());
	}
}
