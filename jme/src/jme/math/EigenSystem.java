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

import java.util.logging.Level;

import jme.utility.LoggingSystem;

/**
 * <code>EigenSystem</code> defines a system of eigen values and associated
 * eigen vectors that solves the case AX = (alpha)X where (A - alpha(I)) = 0.
 * The numerical approach used is to apply orthogonal transformations 
 * (Householder transformations) to reduce A to a tridiagonal matrix. The
 * QL algorithm than shifts the matrix to a diagonal one. 
 * 
 * <br><br>
 * <b>NOTE:</b> See 3D Game Engine Design. David H. Eberly. Also, JAMA.
 * 
 * @author Mark Powell
 * @version $Id: EigenSystem.java,v 1.1.1.1 2003-10-29 10:58:49 Anakan Exp $
 */
public class EigenSystem {
    private float[][] matrix;
    private float[][] eigenVectors;
    private float[][] hessenberg;
    private float[] realEigenValues;
    private float[] imaginaryEigenValues;
    private int size;

    /**
     * Constructor instantiates a new <code>EigenSystem</code> object from
     * a supplied matrix. Where the matrix is A in the equation: 
     * AX = (alpha)X. It is assumed that the supplied matrix is symmetric
     * and a check for such will occur during instantiation. If the matrix
     * is not symmetric, the eigenvalues and eigen vectors will not be 
     * calculated.
     * 
     * @param matrix the square matrix to create the eigen system from.
     */
    public EigenSystem(float[][] matrix) {
        this.matrix = matrix;
        //insure the matrix is square.
        if (matrix[0].length != matrix.length) {
            LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING, 
                "Matrix is not square.");
            return;
        }
        size = matrix.length;

        //insure the matrix is symmetric.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.matrix[i][j] != this.matrix[j][i]) {
                    LoggingSystem.getLoggingSystem().getLogger().log(Level.WARNING, 
                        "Matrix is not symmetric.");
                    return;
                }
            }
        }

        //initialize data and calculate eigen system
        realEigenValues = new float[size];
        imaginaryEigenValues = new float[size];
        eigenVectors = new float[size][size];
        hessenberg = new float[size][size];

        //initialize vectors as initial matrix
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                eigenVectors[i][j] = matrix[i][j];
            }
        }
    }

    /**
     * <code>decreasingSort</code> sorts eigenvalues and their associated
     * eigenvectors in order of largest to smallest. Such that
     * e[0] >= ... >= e[size - 1].
     */
    public void decreasingSort() {
        for (int currentValue = 0; currentValue <= size - 2; currentValue++) {
            // locate maximum eigenvalue
            int placeHolder = currentValue;
            float max = realEigenValues[placeHolder];
            for (int swapValue = currentValue + 1;
                swapValue < size;
                swapValue++) {
                if (realEigenValues[swapValue] > max) {
                    placeHolder = swapValue;
                    max = realEigenValues[placeHolder];
                }
            }

            if (placeHolder != currentValue) {
                // swap eigenvalues
                realEigenValues[placeHolder] = realEigenValues[currentValue];
                realEigenValues[currentValue] = max;

                // swap eigenvectors
                for (int swapValue = 0; swapValue < size; swapValue++) {
                    float tmp = eigenVectors[swapValue][currentValue];
                    eigenVectors[swapValue][currentValue] =
                        eigenVectors[swapValue][placeHolder];
                    eigenVectors[swapValue][placeHolder] = tmp;
                }
            }
        }
    }

    /**
     * <code>increasingSort</code> sorts eigenvalues and their associated
     * eigenvectors in order of smallest to largest. Such that
     * e[0] <= ... <= e[size - 1].
     *
     */
    public void increasingSort() {
        // sort eigenvalues in increasing order, e[0] <= ... <= e[size-1]
        for (int currentValue = 0; currentValue <= size - 2; currentValue++) {
            // locate minimum eigenvalue
            int placeHolder = currentValue;
            float min = realEigenValues[placeHolder];
            for (int swapValue = currentValue + 1;
                swapValue < size;
                swapValue++) {
                if (realEigenValues[swapValue] < min) {
                    placeHolder = swapValue;
                    min = realEigenValues[placeHolder];
                }
            }

            if (placeHolder != currentValue) {
                // swap eigenvalues
                realEigenValues[placeHolder] = realEigenValues[currentValue];
                realEigenValues[currentValue] = min;

                // swap eigenvectors
                for (int swapValue = 0; swapValue < size; swapValue++) {
                    float tmp = eigenVectors[swapValue][currentValue];
                    eigenVectors[swapValue][currentValue] =
                        eigenVectors[placeHolder][swapValue];
                    eigenVectors[swapValue][placeHolder] = tmp;
                }
            }
        }
    }

    /**
     * <code>tridiagonalReduction</code> reduces the matrix to a tridiagonal
     * matrix using the Householder transformations. 
     *
     */
    public void tridiagonalReduction() {
        //initialize the eigenvalues to the last row of eigenVectors.
        for (int j = 0; j < size; j++) {
            realEigenValues[j] = eigenVectors[size - 1][j];
        }

        //Householder transformation
        for (int i = size - 1; i > 0; i--) {

            // Scale to avoid under/overflow.
            float scale = 0;
            float h = 0;
            for (int k = 0; k < i; k++) {
                scale = scale + Math.abs(realEigenValues[k]);
            }
            
            if (scale == 0) {
                imaginaryEigenValues[i] = realEigenValues[i - 1];
                for (int j = 0; j < i; j++) {
                    realEigenValues[j] = eigenVectors[i - 1][j];
                    eigenVectors[i][j] = 0;
                    eigenVectors[j][i] = 0;
                }
            } else {

                // Generate Householder vector.
                for (int k = 0; k < i; k++) {
                    realEigenValues[k] /= scale;
                    h += realEigenValues[k] * realEigenValues[k];
                }
                float f = realEigenValues[i - 1];
                float g = (float) Math.sqrt(h);
                if (f > 0) {
                    g = -g;
                }
                imaginaryEigenValues[i] = scale * g;
                h = h - f * g;
                realEigenValues[i - 1] = f - g;
                for (int j = 0; j < i; j++) {
                    imaginaryEigenValues[j] = 0;
                }

                // Apply similarity transformation to remaining columns.
                for (int j = 0; j < i; j++) {
                    f = realEigenValues[j];
                    eigenVectors[j][i] = f;
                    g = imaginaryEigenValues[j] + eigenVectors[j][j] * f;
                    for (int k = j + 1; k <= i - 1; k++) {
                        g += eigenVectors[k][j] * realEigenValues[k];
                        imaginaryEigenValues[k] += eigenVectors[k][j] * f;
                    }
                    imaginaryEigenValues[j] = g;
                }
                f = 0;
                for (int j = 0; j < i; j++) {
                    imaginaryEigenValues[j] /= h;
                    f += imaginaryEigenValues[j] * realEigenValues[j];
                }
                float hh = f / (h + h);
                for (int j = 0; j < i; j++) {
                    imaginaryEigenValues[j] -= hh * realEigenValues[j];
                }
                for (int j = 0; j < i; j++) {
                    f = realEigenValues[j];
                    g = imaginaryEigenValues[j];
                    for (int k = j; k <= i - 1; k++) {
                        eigenVectors[k][j]
                            -= (f * imaginaryEigenValues[k]
                                + g * realEigenValues[k]);
                    }
                    realEigenValues[j] = eigenVectors[i - 1][j];
                    eigenVectors[i][j] = 0;
                }
            }
            realEigenValues[i] = h;
        }

        // Accumulate transformations.

        for (int i = 0; i < size - 1; i++) {
            eigenVectors[size - 1][i] = eigenVectors[i][i];
            eigenVectors[i][i] = 1;
            float h = realEigenValues[i + 1];
            if (h != 0) {
                for (int k = 0; k <= i; k++) {
                    realEigenValues[k] = eigenVectors[k][i + 1] / h;
                }
                for (int j = 0; j <= i; j++) {
                    float g = 0;
                    for (int k = 0; k <= i; k++) {
                        g += eigenVectors[k][i + 1] * eigenVectors[k][j];
                    }
                    for (int k = 0; k <= i; k++) {
                        eigenVectors[k][j] -= g * realEigenValues[k];
                    }
                }
            }
            for (int k = 0; k <= i; k++) {
                eigenVectors[k][i + 1] = 0;
            }
        }
        for (int j = 0; j < size; j++) {
            realEigenValues[j] = eigenVectors[size - 1][j];
            eigenVectors[size - 1][j] = 0;
        }
        eigenVectors[size - 1][size - 1] = 1;
        imaginaryEigenValues[0] = 0;
    }

    /**
     * <code>tridiagonalQL</code> reduces the tridiagonal matrix to a diagonal
     * one using the QL algorithm.
     *
     */
    public void tridiagonalQL() {
        
        for (int i = 1; i < size; i++) {
            imaginaryEigenValues[i - 1] = imaginaryEigenValues[i];
        }
        imaginaryEigenValues[size - 1] = 0;

        float f = 0;
        float tst1 = 0;
        float eps = (float) Math.pow(2.0, -52.0);
        for (int l = 0; l < size; l++) {

            // Find small subdiagonal element

            tst1 =
                Math.max(
                    tst1,
                    Math.abs(realEigenValues[l])
                        + Math.abs(imaginaryEigenValues[l]));
            int m = l;
            while (m < size) {
                if (Math.abs(imaginaryEigenValues[m]) <= eps * tst1) {
                    break;
                }
                m++;
            }

            // If m == l, realEigenValues[l] is an eigenvalue,
            // otherwise, iterate.

            if (m > l) {
                int iter = 0;
                do {
                    iter = iter + 1; // (Could check iteration count here.)

                    // Compute implicit shift

                    float g = realEigenValues[l];
                    float p =
                        (realEigenValues[l + 1] - g)
                            / (2 * imaginaryEigenValues[l]);
                    float r = MathUtils.hypot(p, 1);
                    if (p < 0) {
                        r = -r;
                    }
                    realEigenValues[l] = imaginaryEigenValues[l] / (p + r);
                    realEigenValues[l + 1] = imaginaryEigenValues[l] * (p + r);
                    float dl1 = realEigenValues[l + 1];
                    float h = g - realEigenValues[l];
                    for (int i = l + 2; i < size; i++) {
                        realEigenValues[i] -= h;
                    }
                    f = f + h;

                    // Implicit QL transformation.

                    p = realEigenValues[m];
                    float c = 1;
                    float c2 = c;
                    float c3 = c;
                    float el1 = imaginaryEigenValues[l + 1];
                    float s = 0;
                    float s2 = 0;
                    for (int i = m - 1; i >= l; i--) {
                        c3 = c2;
                        c2 = c;
                        s2 = s;
                        g = c * imaginaryEigenValues[i];
                        h = c * p;
                        r = MathUtils.hypot(p, imaginaryEigenValues[i]);
                        imaginaryEigenValues[i + 1] = s * r;
                        s = imaginaryEigenValues[i] / r;
                        c = p / r;
                        p = c * realEigenValues[i] - s * g;
                        realEigenValues[i + 1] =
                            h + s * (c * g + s * realEigenValues[i]);

                        // Accumulate transformation.

                        for (int k = 0; k < size; k++) {
                            h = eigenVectors[k][i + 1];
                            eigenVectors[k][i + 1] =
                                s * eigenVectors[k][i] + c * h;
                            eigenVectors[k][i] = c * eigenVectors[k][i] - s * h;
                        }
                    }
                    p = -s * s2 * c3 * el1 * imaginaryEigenValues[l] / dl1;
                    imaginaryEigenValues[l] = s * p;
                    realEigenValues[l] = c * p;

                    // Check for convergence.

                } while (Math.abs(imaginaryEigenValues[l]) > eps * tst1);
            }
            realEigenValues[l] = realEigenValues[l] + f;
            imaginaryEigenValues[l] = 0;
        }

        // Sort eigenvalues and corresponding vectors.

        for (int i = 0; i < size - 1; i++) {
            int k = i;
            float p = realEigenValues[i];
            for (int j = i + 1; j < size; j++) {
                if (realEigenValues[j] < p) {
                    k = j;
                    p = realEigenValues[j];
                }
            }
            if (k != i) {
                realEigenValues[k] = realEigenValues[i];
                realEigenValues[i] = p;
                for (int j = 0; j < size; j++) {
                    p = eigenVectors[j][i];
                    eigenVectors[j][i] = eigenVectors[j][k];
                    eigenVectors[j][k] = p;
                }
            }
        }
    }

    /**
     * <code>getEigenVector</code> returns a single element of an eigenvector.
     * The element is defined by the row and column requested.
     * @param row the row of the eigenvectors
     * @param col the column of the eigenvectors
     * @return the eigenvector value.
     */
    public float getEigenvector(int row, int col) {
        return eigenVectors[row][col];
    }
    
    /**
     * <code>getEigenvectors</code> returns the complete matrix of eigenvectors.
     * 
     * @return the complete matrix of eigenvectors.
     */
    public float[][] getEigenvectors() {
        return eigenVectors;
    }

    /**
     * <code>getRealEigenvalue</code> returns the real components of the
     * eigenvalues.
     * @return the real components of the eigenvalues.
     */
    public float[] getRealEigenvalue() {
        return realEigenValues;
    }

    /**
     * <code>getRealEigenvalue</code> returns a single eigenvalue defined by
     * the parameter index.
     * @param i the eigenvalue desired.
     * @return the eigenvalue referenced by the parameter index.
     */
    public float getRealEigenvalue(int i) {
        return realEigenValues[i];
    }

    /**
     * <code>getImaginaryEigenvalue</code> returns the imaginary components of 
     * the eigenvalues.
     * @return the imaginary components of the eigenvalues.
     */
    public float[] getImaginaryEigenValue() {
        return imaginaryEigenValues;
    }

    /**
     * <code>getImaginaryEigenvalue</code> returns a single eigenvalue defined by
     * the parameter index.
     * @param i the eigenvalue desired.
     * @return the eigenvalue referenced by the parameter index.
     */
    public float getImaginaryEigenValue(int i) {
        return imaginaryEigenValues[i];
    }

    /**
     * Main method used for testing. Creates a simple matrix and calculates the
     * eigenvalues and eigenvectors.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        float[][] matrix = new float[3][3];
        matrix[0][0] = 2;
        matrix[0][1] = 1;
        matrix[0][2] = 1;
        matrix[1][0] = 1;
        matrix[1][1] = 2;
        matrix[1][2] = 1;
        matrix[2][0] = 1;
        matrix[2][1] = 1;
        matrix[2][2] = 2;

        EigenSystem kES = new EigenSystem(matrix);

        kES.tridiagonalReduction();
        kES.tridiagonalQL();
        kES.increasingSort();
        //kES.decreasingSort();
        System.out.println("eigenvalues = ");
        int iRow;
        for (iRow = 0; iRow < 3; iRow++)
            System.out.print(kES.getRealEigenvalue(iRow) + " ");
        System.out.println();

        System.out.println("eigenvectors = ");
        for (iRow = 0; iRow < 3; iRow++) {
            for (int iCol = 0; iCol < 3; iCol++)
                System.out.print(kES.getEigenvector(iRow, iCol) + " ");
            System.out.println();
        }
    }

}
