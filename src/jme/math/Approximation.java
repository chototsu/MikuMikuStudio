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
 * <code>Approximation</code> is a static class that will create a
 * piece of geometry (line, rectangle, etc) that best fits a collection of 
 * points.
 * <br><br>
 * <b>NOTE:</b> See 3D Game Engine Design. David H. Eberly.
 * @author Mark Powell
 * @version $Id: Approximation.java,v 1.2 2003-08-27 21:05:42 mojomonkey Exp $
 *
 */
public class Approximation {

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
        //compute average of points
        origin = points[0];
        for (int i = 1; i < points.length; i++) {
            origin = origin.add(points[i]);
        }

        float inverseQuantity = 1.0f / points.length;
        origin = origin.mult(inverseQuantity);

        // compute sums of products
        float sumXX = 0.0f, sumXY = 0.0f, sumXZ = 0.0f;
        float sumYY = 0.0f, sumYZ = 0.0f, sumZZ = 0.0f;
        for (int i = 0; i < points.length; i++) {
            Vector diff = points[i].subtract(origin);
            sumXX += diff.x * diff.x;
            sumXY += diff.x * diff.y;
            sumXZ += diff.x * diff.z;
            sumYY += diff.y * diff.y;
            sumYZ += diff.y * diff.z;
            sumZZ += diff.z * diff.z;
        }

        float[][] matrix = new float[3][3];
        // setup the eigensolver
        matrix[0][0] = sumYY + sumZZ;
        matrix[0][1] = -sumXY;
        matrix[0][2] = -sumXZ;
        matrix[1][0] = matrix[0][1];
        matrix[1][1] = sumXX + sumZZ;
        matrix[1][2] = -sumYZ;
        matrix[2][0] = matrix[0][2];
        matrix[2][1] = matrix[1][2];
        matrix[2][2] = sumXX + sumYY;
        EigenSystem eigen = new EigenSystem(matrix);

        // compute eigenstuff, smallest eigenvalue is in last position
        eigen.tridiagonalReduction();
        eigen.tridiagonalQL();
        eigen.decreasingSort();

        // unit-length direction for best-fit line
        direction.x = eigen.getEigenvector(0, 2);
        direction.y = eigen.getEigenvector(1, 2);
        direction.z = eigen.getEigenvector(2, 2);

        return new Line(origin, direction);
    }

    /**
     * <code>gaussPointsFit</code> generates a rectangle based on supplied
     * points such that the center is calculated as the average of points and
     * the extents are determined by the eigenvectors.
     * @param points the collection of points to generate the rectangle.
     * @param center storage for the center point of the points.
     * @param axis storage for the orientation of the rectangle.
     * @param extent storage for the length of the rectangle.
     */
    public static void gaussPointsFit(
        Vector[] points,
        Vector center,
        Vector[] axis,
        float[] extent) {
            
        // compute mean of points
        center = points[0];
        
        for (int i = 1; i < points.length; i++) {
            center = center.add(points[i]);
        }
        
        float inverseQuantity = 1.0f / points.length;
        center = center.mult(inverseQuantity);

        // compute covariances of points
        float sumXX = 0.0f;
        float sumXY = 0.0f;
        float sumXZ = 0.0f;
        float sumYY = 0.0f;
        float sumYZ = 0.0f;
        float sumZZ = 0.0f;
        
        for (int i = 0; i < points.length; i++) {
            Vector diff = points[i].subtract(center);
            sumXX += diff.x * diff.x;
            sumXY += diff.x * diff.y;
            sumXZ += diff.x * diff.z;
            sumYY += diff.y * diff.y;
            sumYZ += diff.y * diff.z;
            sumZZ += diff.z * diff.z;
        }
        sumXX *= inverseQuantity;
        sumXY *= inverseQuantity;
        sumXZ *= inverseQuantity;
        sumYY *= inverseQuantity;
        sumYZ *= inverseQuantity;
        sumZZ *= inverseQuantity;
        float[][] matrix = new float[3][3];
        // compute eigenvectors for covariance matrix
       
        matrix[0][0] = sumXX;
        matrix[0][1] = sumXY;
        matrix[0][2] = sumXZ;
        matrix[1][0] = sumXY;
        matrix[1][1] = sumYY;
        matrix[1][2] = sumYZ;
        matrix[2][0] = sumXZ;
        matrix[2][1] = sumYZ;
        matrix[2][2] = sumZZ;
        
        EigenSystem eigen = new EigenSystem(matrix);
        eigen.tridiagonalReduction();
        eigen.tridiagonalQL();
        eigen.increasingSort();
        
        axis[0].x = eigen.getEigenvector(0, 0);
        axis[0].y = eigen.getEigenvector(1, 0);
        axis[0].z = eigen.getEigenvector(2, 0);
        axis[1].x = eigen.getEigenvector(0, 1);
        axis[1].y = eigen.getEigenvector(1, 1);
        axis[1].z = eigen.getEigenvector(2, 1);
        axis[2].x = eigen.getEigenvector(0, 2);
        axis[2].y = eigen.getEigenvector(1, 2);
        axis[2].z = eigen.getEigenvector(2, 2);

        extent[0] = eigen.getRealEigenvalue(0);
        extent[1] = eigen.getRealEigenvalue(1);
        extent[2] = eigen.getRealEigenvalue(2);
    }
}