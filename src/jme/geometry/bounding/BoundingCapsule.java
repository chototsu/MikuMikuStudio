/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
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
package jme.geometry.bounding;

import jme.math.Line;
import jme.math.LineApproximation;
import jme.math.Distance;
import jme.math.Segment;
import jme.math.Vector;

/**
 * <code>BoundingCapsule</code> defines a bounding volume in the shape of
 * a capsule, where a capsule is an extension of sphere. A capsule can be
 * thought of as a cylinder with domed caps.
 * 
 * @author Mark Powell
 * @version $Id: BoundingCapsule.java,v 1.1 2003-08-18 22:25:02 mojomonkey Exp $
 */
public class BoundingCapsule {
	private Segment lineSegment;
	private float radius;

	public BoundingCapsule() {
		lineSegment = new Segment();
	}

	public BoundingCapsule(Segment lineSegment, float radius) {
		this.lineSegment = lineSegment;
		this.radius = radius;
	}

	public Vector getOrigin() {
		return lineSegment.getOrigin();
	}

	public Vector getDirection() {
		return lineSegment.getDirection();
	}

	public float getRadius() {
		return radius;
	}

	/**
	 * <code>leastSquaresFit</code> selects a parameterized equation
	 * that represents a discrete set of points in a continuous manner.
	 * The radius is then found to be the maximum distance from the
	 * data points to the line.
	 * @param points the points to contain.
	 */
	public void leastSquaresFit(Vector[] points) {

		Line line = LineApproximation.orthogonalLineFit(points);

		float maxRadiusSqr = 0.0f;

		for (int i = 0; i < points.length; i++) {
			float radiusSquared = Distance.distanceSquared(points[i], line);
			if (radiusSquared > maxRadiusSqr) {
				maxRadiusSqr = radiusSquared;
			}
		}

		Vector u = new Vector();
		Vector v = new Vector();
		Vector w = line.getDirection();
		Vector.generateOrthonormalBasis(u, v, w, true);

		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		for (int i = 0; i < points.length; i++) {
			Vector diff = points[i].subtract(line.getOrigin());
			float fU = u.dot(diff);
			float fV = v.dot(diff);
			float fW = w.dot(diff);
			float fDiscr = maxRadiusSqr - (fU * fU + fV * fV);
			float radical = (float) Math.sqrt(Math.abs(fDiscr));

			float fTest = fW + radical;
			if (fTest < min)
				min = fTest;

			fTest = fW - radical;
			if (fTest > max)
				max = fTest;
		}

		if (min < max) {
			lineSegment.setOrigin(
				line.getOrigin().add(line.getDirection().mult(min)));
			lineSegment.setDirection(line.getDirection().mult(max - min));
		} else {
			// enclosing capsule is really a sphere
			lineSegment.setOrigin(
				line.getOrigin().add(
					line.getDirection().mult((0.5f * (min + max)))));
			lineSegment.setDirection(new Vector());
		}

		radius = (float) Math.sqrt(maxRadiusSqr);
	}
}
