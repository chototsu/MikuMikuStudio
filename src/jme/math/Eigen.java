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
 * <code>Eigen</code>
 * <br><br>
 * <b>NOTE:</b> See 3D Game Engine Design. David H. Eberly.
 * @author Mark Powell
 */
public class Eigen {

	private int size;
	private float[][] matrix;
	private float[] diagonal;
	private float[] subd;

	public Eigen(int size) {
		this.size = size;
		matrix = new float[size][size];
		diagonal = new float[size];
		subd = new float[size];
	}

	public void setMatrixValue(int x, int y, float value) {
		matrix[x][y] = value;
	}

	public float getMatrixValue(int x, int y) {
		return matrix[x][y];
	}

	public void decrementSort3() {
		tridiagonal3();
		qLAlgorithm();
		decreasingSort();
	}

	public void tridiagonal3() {
		float fM00 = matrix[0][0];
		float fM01 = matrix[0][1];
		float fM02 = matrix[0][2];
		float fM11 = matrix[1][1];
		float fM12 = matrix[1][2];
		float fM22 = matrix[2][2];

		diagonal[0] = fM00;
		subd[2] = 0.0f;
		if (fM02 != 0.0f) {
			float fLength = (float) Math.sqrt(fM01 * fM01 + fM02 * fM02);
			float fInvLength = 1.0f / fLength;
			fM01 *= fInvLength;
			fM02 *= fInvLength;
			float fQ = 2.0f * fM01 * fM12 + fM02 * (fM22 - fM11);
			diagonal[1] = fM11 + fM02 * fQ;
			diagonal[2] = fM22 - fM02 * fQ;
			subd[0] = fLength;
			subd[1] = fM12 - fM01 * fQ;
			matrix[0][0] = 1.0f;
			matrix[0][1] = 0.0f;
			matrix[0][2] = 0.0f;
			matrix[1][0] = 0.0f;
			matrix[1][1] = fM01;
			matrix[1][2] = fM02;
			matrix[2][0] = 0.0f;
			matrix[2][1] = fM02;
			matrix[2][2] = -fM01;
		} else {
			diagonal[1] = fM11;
			diagonal[2] = fM22;
			subd[0] = fM01;
			subd[1] = fM12;
			matrix[0][0] = 1.0f;
			matrix[0][1] = 0.0f;
			matrix[0][2] = 0.0f;
			matrix[1][0] = 0.0f;
			matrix[1][1] = 1.0f;
			matrix[1][2] = 0.0f;
			matrix[2][0] = 0.0f;
			matrix[2][1] = 0.0f;
			matrix[2][2] = 1.0f;
		}
	}

	public boolean qLAlgorithm() {
		int iMaxIter = 32;

		for (int i0 = 0; i0 < size; i0++) {
			int i1;
			for (i1 = 0; i1 < iMaxIter; i1++) {
				int i2;
				for (i2 = i0; i2 <= size - 2; i2++) {
					float fTmp =
						Math.abs(diagonal[i2]) + Math.abs(diagonal[i2 + 1]);
					if (Math.abs(subd[i2]) + fTmp == fTmp)
						break;
				}
				if (i2 == i0)
					break;

				float fG =
					(diagonal[i0 + 1] - diagonal[i0]) / (2.0f * subd[i0]);
				float fR = (float) Math.sqrt(fG * fG + 1.0);
				if (fG < 0.0f)
					fG = diagonal[i2] - diagonal[i0] + subd[i0] / (fG - fR);
				else
					fG = diagonal[i2] - diagonal[i0] + subd[i0] / (fG + fR);
				float fSin = 1.0f, fCos = 1.0f, fP = 0.0f;
				for (int i3 = i2 - 1; i3 >= i0; i3--) {
					float fF = fSin * subd[i3];
					float fB = fCos * subd[i3];
					if (Math.abs(fF) >= Math.abs(fG)) {
						fCos = fG / fF;
						fR = (float) Math.sqrt(fCos * fCos + 1.0);
						subd[i3 + 1] = fF * fR;
						fSin = 1.0f / fR;
						fCos *= fSin;
					} else {
						fSin = fF / fG;
						fR = (float) Math.sqrt(fSin * fSin + 1.0);
						subd[i3 + 1] = fG * fR;
						fCos = 1.0f / fR;
						fSin *= fCos;
					}
					fG = diagonal[i3 + 1] - fP;
					fR = (diagonal[i3] - fG) * fSin + 2.0f * fB * fCos;
					fP = fSin * fR;
					diagonal[i3 + 1] = fG + fP;
					fG = fCos * fR - fB;

					for (int i4 = 0; i4 < size; i4++) {
						fF = matrix[i4][i3+1];
						matrix[i4][i3 + 1] = fSin * matrix[i4][i3] + fCos * fF;
						matrix[i4][i3] = fCos * matrix[i4][i3] - fSin * fF;
					}
				}
				diagonal[i0] -= fP;
				subd[i0] = fG;
				subd[i2] = 0.0f;
			}
			if (i1 == iMaxIter)
				return false;
		}

		return true;
	}

	public void decreasingSort() {
		// sort eigenvalues in decreasing order, e[0] >= ... >= e[iSize-1]
		for (int i0 = 0, i1; i0 <= size - 2; i0++) {
			// locate maximum eigenvalue
			i1 = i0;
			float fMax = diagonal[i1];
			int i2;
			for (i2 = i0 + 1; i2 < size; i2++) {
				if (diagonal[i2] > fMax) {
					i1 = i2;
					fMax = diagonal[i1];
				}
			}

			if (i1 != i0) {
				// swap eigenvalues
				diagonal[i1] = diagonal[i0];
				diagonal[i0] = fMax;

				// swap eigenvectors
				for (i2 = 0; i2 < size; i2++) {
					float fTmp = matrix[i2][i0];
					matrix[i2][i0] = matrix[i2][i1];
					matrix[i2][i1] = fTmp;
				}
			}
		}
	}

	public float getEigenvector(int iRow, int iCol) {
		return matrix[iRow][iCol];
	}

	public float[] getEigenvalue() {
		return diagonal;
	}

	public float getEigenvalue(int i) {
		return diagonal[i];
	}

	public void IncrSortEigenStuff3() {
		tridiagonal3();
		qLAlgorithm();
		increasingSort();
	}

	public void increasingSort() {
		// sort eigenvalues in increasing order, e[0] <= ... <= e[size-1]
		for (int i0 = 0, i1; i0 <= size - 2; i0++) {
			// locate minimum eigenvalue
			i1 = i0;
			float fMin = diagonal[i1];
			int i2;
			for (i2 = i0 + 1; i2 < size; i2++) {
				if (diagonal[i2] < fMin) {
					i1 = i2;
					fMin = diagonal[i1];
				}
			}

			if (i1 != i0) {
				// swap eigenvalues
				diagonal[i1] = diagonal[i0];
				diagonal[i0] = fMin;

				// swap eigenvectors
				for (i2 = 0; i2 < size; i2++) {
					float fTmp = matrix[i2][i0];
					matrix[i2][i0] = matrix[i1][i2];
					matrix[i2][i1] = fTmp;
				}
			}
		}
	}

	public static void main(String[] args) {

		Eigen kES = new Eigen(3);

		kES.setMatrixValue(0, 0, 2.0f);
		kES.setMatrixValue(0, 1, 1.0f);
		kES.setMatrixValue(0, 2, 1.0f);
		kES.setMatrixValue(1, 0, 1.0f);
		kES.setMatrixValue(1, 1, 2.0f);
		kES.setMatrixValue(1, 2, 1.0f);
		kES.setMatrixValue(2, 0, 1.0f);
		kES.setMatrixValue(2, 1, 1.0f);
		kES.setMatrixValue(2, 2, 2.0f);

		kES.IncrSortEigenStuff3();

		System.out.println("eigenvalues = ");
		int iRow;
		for (iRow = 0; iRow < 3; iRow++)
			System.out.print(kES.getEigenvalue(iRow) + " ");
		System.out.println();

		System.out.println("eigenvectors = ");
		for (iRow = 0; iRow < 3; iRow++) {
			for (int iCol = 0; iCol < 3; iCol++)
				System.out.print(kES.getEigenvector(iRow, iCol) + " ");
			System.out.println();
		}

		// eigenvalues =
		//    1.000000 1.000000 4.000000
		// eigenvectors =
		//    0.411953  0.704955 0.577350
		//    0.404533 -0.709239 0.577350
		//   -0.816485  0.004284 0.577350

	}
}
