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
package jme.math;

/**
 * <code>LineApproximation</code>
 * @author Mark Powell
 *
 */
public class LineApproximation {
	
	/**
	 * <code>orthogonalLineFit</code> creates a line that uses 
	 * least squares and measures errors orthogonally rather than
	 * linearly. 
	 * @param points the points to fit a line to.
	 * @return the line that best fits the points.
	 */
	public static Line orthogonalLineFit(Vector[] points) {
			
		Vector origin = new Vector();
		Vector direction = new Vector();
		//		compute average of points
		origin = points[0];
		int i;
		for (i = 1; i < points.length; i++)
			origin = origin.add(points[i]);
		float inverseQuantity = 1.0f / points.length;
		origin = origin.mult(inverseQuantity);

		// compute sums of products
		float fSumXX = 0.0f, fSumXY = 0.0f, fSumXZ = 0.0f;
		float fSumYY = 0.0f, fSumYZ = 0.0f, fSumZZ = 0.0f;
		for (i = 0; i < points.length; i++) {
			Vector kDiff = points[i].subtract(origin);
			fSumXX += kDiff.x * kDiff.x;
			fSumXY += kDiff.x * kDiff.y;
			fSumXZ += kDiff.x * kDiff.z;
			fSumYY += kDiff.y * kDiff.y;
			fSumYZ += kDiff.y * kDiff.z;
			fSumZZ += kDiff.z * kDiff.z;
		}

		// setup the eigensolver
		Eigen kES = new Eigen(3);
		kES.setMatrixValue(0, 0, fSumYY + fSumZZ);
		kES.setMatrixValue(0, 1, -fSumXY);
		kES.setMatrixValue(0, 2, -fSumXZ);
		kES.setMatrixValue(1, 0, kES.getMatrixValue(0, 1));
		kES.setMatrixValue(1, 1, fSumXX + fSumZZ);
		kES.setMatrixValue(1, 2, -fSumYZ);
		kES.setMatrixValue(2, 0, kES.getMatrixValue(0, 2));
		kES.setMatrixValue(2, 1, kES.getMatrixValue(1, 2));
		kES.setMatrixValue(2, 2, fSumXX + fSumYY);

		// compute eigenstuff, smallest eigenvalue is in last position
		kES.decrementSort3();

		// unit-length direction for best-fit line
		direction.x = kES.getEigenvector(0, 2);
		direction.y = kES.getEigenvector(1, 2);
		direction.z = kES.getEigenvector(2, 2);
		
		return new Line(origin, direction);
	}
}
