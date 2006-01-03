/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jme.math;

import java.util.Random;

/**
 * <code>FastMath</code> provides 'fast' math approximations and float equivalents of Math
 * functions.  These are all used as static values and functions.
 *
 * @author Various
 * @version $Id: FastMath.java,v 1.28 2006-01-03 18:06:09 renanse Exp $
 */

final public class FastMath {

    private FastMath(){}

    /** A "close to zero" double epsilon value for use*/
    public static final double DBL_EPSILON = 2.220446049250313E-16d;

    /** A "close to zero" float epsilon value for use*/
    public static final float FLT_EPSILON = 1.1920928955078125E-7f;
    
    public static final float ONE_THIRD = 1f/3f;

    /** The value PI as a float. */
    public static final float PI = (float) (4.0 * atan(1.0f));

    /** The value 2PI as a float. */
    public static final float TWO_PI = 2.0f * PI;

    /** The value PI/2 as a float. */
    public static final float HALF_PI = 0.5f * PI;

    /** The value 1/PI as a float. */
    public static final float INV_PI = 1.0f / PI;

    /** The value 1/(2PI) as a float. */
    public static final float INV_TWO_PI = 1.0f / TWO_PI;

    /** A value to multiply a degree value by, to convert it to radians. */
    public static final float DEG_TO_RAD = PI / 180.0f;

    /** A value to multiply a radian value by, to convert it to degrees. */
    public static final float RAD_TO_DEG = 180.0f / PI;

    /** A precreated random object for random numbers. */
    public static final Random rand = new Random(System.currentTimeMillis());


    /**
     * Returns true if the number is a power of 2 (2,4,8,16...)
     * 
     * A good implementation found on the Java boards. note: a number is a power
     * of two if and only if it is the smallest number with that number of
     * significant bits. Therefore, if you subtract 1, you know that the new
     * number will have fewer bits, so ANDing the original number with anything
     * less than it will give 0.
     * 
     * @param number
     *            The number to test.
     * @return True if it is a power of two.
     */
    public static boolean isPowerOfTwo(int number) {
        return (number > 0) && (number & (number - 1)) == 0;
    }

    /**
     * Linear interpolation from v0 to v1 by f percent.  IE (1-f) * V0 + f * V1
     * @param f Percent value to use.
     * @param v0 Begining value. 0% of f
     * @param v1 ending value.  100% of f
     * @return An interpolation between v0 and v1.
     */
    public static float LERP(float f, float v0, float v1) {
      return ( (1 - (f)) * (v0) + (f) * (v1));
    }


     /**
     * Returns the arc cosine of an angle given in radians.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is PI.
     * <li>If the argument is greater than 1, then the result is 0.</ul>
     * @param fValue The angle, in radians.
     * @return fValue's acos
     * @see java.lang.Math#acos(double)
     */
    public static float acos(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f)
                return (float) Math.acos((double) fValue);
            else
                return 0.0f;
        } else {
            return PI;
        }
    }

     /**
     * Returns the arc sine of an angle given in radians.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is -HALF_PI.
     * <li>If the argument is greater than 1, then the result is HALF_PI.</ul>
     * @param fValue The angle, in radians.
     * @return fValue's asin
     * @see java.lang.Math#asin(double)
     */
    public static float asin(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f)
                return (float) Math.asin((double) fValue);
            else
                return HALF_PI;
        } else {
            return -HALF_PI;
        }
    }

     /**
     * Returns the arc tangent of an angle given in radians.<br>
     * @param fValue The angle, in radians.
     * @return fValue's asin
     * @see java.lang.Math#atan(double)
     */
    public static float atan(float fValue) {
        return (float) Math.atan((double) fValue);
    }

    /**
     * A direct call to Math.atan2.
     * @param fY
     * @param fX
     * @return Math.atan2(fY,fX)
     * @see java.lang.Math#atan2(double, double)
     */
    public static float atan2(float fY, float fX) {
        return (float) Math.atan2((double) fY, (double) fX);
    }

    /**
     * Rounds a fValue up.  A call to Math.ceil
     * @param fValue The value.
     * @return The fValue rounded up
     * @see java.lang.Math#ceil(double)
     */
    public static float ceil(float fValue) {
        return (float) Math.ceil((double) fValue);
    }
    
    /**
     * Fast Trig functions for x86. This forces the trig functiosn to stay
     * within the safe area on the x86 processor (-45 degrees to +45 degrees)
     * The results may be very slightly off from what the Math and StrictMath
     * trig functions give due to rounding in the angle reduction but it will be
     * very very close. 
     * 
     * note: code from wiki posting on java.net by jeffpk
     */
    public static float reduceSinAngle(float radians) {
        radians %= TWO_PI; // put us in -2PI to +2PI space
        if (Math.abs(radians) > PI) { // put us in -PI to +PI space
            radians = radians - (TWO_PI);
        }
        if (Math.abs(radians) > HALF_PI) {// put us in -PI/2 to +PI/2 space
            radians = PI - radians;
        }

        return radians;
    }

    /**
     * Returns sine of a value. 
     * 
     * note: code from wiki posting on java.net by jeffpk
     * 
     * @param fValue
     *            The value to sine, in raidans.
     * @return The sine of fValue.
     * @see java.lang.Math#sin(double)
     */
    public static float sin(float fValue) {
        fValue = reduceSinAngle(fValue); // limits angle to between -PI/2 and +PI/2
        if (Math.abs(fValue)<=Math.PI/4){
           return (float)Math.sin(fValue);
        } else {
           return (float)Math.cos(Math.PI/2-fValue);
        }
    }

    /**
     * Returns cos of a value.
     * 
     * @param fValue
     *            The value to cosine, in raidans.
     * @return The cosine of fValue.
     * @see java.lang.Math#cos(double)
     */
    public static float cos(float fValue) {
        return sin(fValue+HALF_PI);
    }

    /**
     * Returns E^fValue
     * @param fValue Value to raise to a power.
     * @return The value E^fValue
     * @see java.lang.Math#exp(double)
     */
    public static float exp(float fValue) {
        return (float) Math.exp((double) fValue);
    }

    /**
     * Returns Absolute value of a float.
     * @param fValue The value to abs.
     * @return The abs of the value.
     * @see java.lang.Math#abs(float)
     */
    public static float abs(float fValue) {
        if (fValue < 0) return -fValue;
        else return fValue;
    }

    /**
     * Returns a number rounded down.
     * @param fValue The value to round
     * @return The given number rounded down
     * @see java.lang.Math#floor(double)
     */
    public static float floor(float fValue) {
        return (float) Math.floor((double) fValue);
    }

    /**
     * Returns 1/sqrt(fValue)
     * @param fValue The value to process.
     * @return 1/sqrt(fValue)
     * @see java.lang.Math#sqrt(double)
     */
    public static float invSqrt(float fValue) {
        return (float) (1.0 / Math.sqrt((double) fValue));
    }

    /**
     * Returns the log base E of a value.
     * @param fValue The value to log.
     * @return The log of fValue base E
     * @see java.lang.Math#log(double)
     */
    public static float log(float fValue) {
        return (float) Math.log((double) fValue);
    }

    /**
     * Returns a number raised to an exponent power.  fBase^fExponent
     * @param fBase The base value (IE 2)
     * @param fExponent The exponent value (IE 3)
     * @return base raised to exponent (IE 8)
     * @see java.lang.Math#pow(double, double)
     */
    public static float pow(float fBase, float fExponent) {
        return (float) Math.pow((double) fBase, (double) fExponent);
    }

    /**
     * Returns the value squared.  fValue ^ 2
     * @param fValue The vaule to square.
     * @return The square of the given value.
     */
    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    /**
     * Returns the square root of a given value.
     * @param fValue The value to sqrt.
     * @return The square root of the given value.
     * @see java.lang.Math#sqrt(double)
     */
    public static float sqrt(float fValue) {
        return (float) Math.sqrt((double) fValue);
    }

    /**
     * Returns the tangent of a value.  If USE_FAST_TRIG is enabled, an approximate value
     * is returned.  Otherwise, a direct value is used.
     * @param fValue The value to tangent, in raidans.
     * @return The tangent of fValue.
     * @see java.lang.Math#tan(double)
     */
    public static float tan(float fValue) {
        return (float) Math.tan((double) fValue);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
     * @param iValue The integer to examine.
     * @return The integer's sign.
     */
    public static int sign(int iValue) {
        if (iValue > 0) return 1;

        if (iValue < 0) return -1;

        return 0;
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0 otherwise
     * @param fValue The float to examine.
     * @return The float's sign.
     */
    public static float sign(float fValue) {
        if (fValue > 0.0f) return 1.0f;

        if (fValue < 0.0f) return -1.0f;

        return 0.0f;
    }

    /**
     * Given 3 points in a 2d plane, this function computes if the points going from A-B-C
     * are moving counter clock wise.
     * @param p0 Point 0.
     * @param p1 Point 1.
     * @param p2 Point 2.
     * @return 1 If they are CCW, -1 if they are not CCW, 0 if p2 is between p0 and p1.
     */
    public static int counterClockwise(Vector2f p0,Vector2f p1,Vector2f p2){
        float dx1,dx2,dy1,dy2;
        dx1=p1.x-p0.x;
        dy1=p1.y-p0.y;
        dx2=p2.x-p0.x;
        dy2=p2.y-p0.y;
        if (dx1*dy2>dy1*dx2) return 1;
        if (dx1*dy2<dy1*dx2) return -1;
        if ((dx1*dx2 < 0) || (dy1*dy2 <0)) return -1;
        if ((dx1*dx1+dy1*dy1) < (dx2*dx2+dy2*dy2)) return 1;
        return 0;
    }

    /**
     * Test if a point is inside a triangle.  1 if the point is on the ccw side,
     * -1 if the point is on the cw side, and 0 if it is on neither.
     * @param t0 First point of the triangle.
     * @param t1 Second point of the triangle.
     * @param t2 Third point of the triangle.
     * @param p The point to test.
     * @return Value 1 or -1 if inside triangle, 0 otherwise.
     */
    public static int pointInsideTriangle(Vector2f t0,Vector2f t1,Vector2f t2,Vector2f p){
        int val1=counterClockwise(t0,t1,p);
        if (val1==0) return 1;
        int val2=counterClockwise(t1,t2,p);
        if (val2==0) return 1;
        if (val2!=val1) return 0;
        int val3=counterClockwise(t2,t0,p);
        if (val3==0) return 1;
        if (val3!=val1) return 0;
        return val3;
    }


    /**
     * Returns the determinant of a 4x4 matrix.
     */ 
    public static float determinant(double m00,double m01,double m02,double m03, double m10,double m11,double m12,double m13,
                                    double m20,double m21,double m22,double m23,double m30,double m31,double m32,double m33) {
        double value;
        value =
            m03 * m12 * m21 * m30-m02 * m13 * m21 * m30-m03 * m11 * m22 * m30+m01 * m13 * m22 * m30+
            m02 * m11 * m23 * m30-m01 * m12 * m23 * m30-m03 * m12 * m20 * m31+m02 * m13 * m20 * m31+
            m03 * m10 * m22 * m31-m00 * m13 * m22 * m31-m02 * m10 * m23 * m31+m00 * m12 * m23 * m31+
            m03 * m11 * m20 * m32-m01 * m13 * m20 * m32-m03 * m10 * m21 * m32+m00 * m13 * m21 * m32+
            m01 * m10 * m23 * m32-m00 * m11 * m23 * m32-m02 * m11 * m20 * m33+m01 * m12 * m20 * m33+
            m02 * m10 * m21 * m33-m00 * m12 * m21 * m33-m01 * m10 * m22 * m33+m00 * m11 * m22 * m33;
        return (float) value;
  }

    /**
     * Returns a random float between 0 and 1.
     * @return A random float between 0 and 1.
     */
    public static float nextRandomFloat() {
        return rand.nextFloat();
    }

    /**
     * Converts a point from spherical coordinates to cartesian and stores the
     * results in the store var.
     */
    public static Vector3f sphericalToCartesian(Vector3f sphereCoords,
            Vector3f store) {
        store.y = sphereCoords.x * FastMath.sin(sphereCoords.z);
        float a = sphereCoords.x * FastMath.cos(sphereCoords.z);
        store.x = a * FastMath.cos(sphereCoords.y);
        store.z = a * FastMath.sin(sphereCoords.y);

        return store;
    }

    /**
     * Converts a point from cartesian coordinates to spherical and stores the
     * results in the store var. (Radius, Azimuth, Polar)
     */
    public static Vector3f cartesianToSpherical(Vector3f cartCoords,
            Vector3f store) {
        if (cartCoords.x == 0)
            cartCoords.x = FastMath.FLT_EPSILON;
        store.x = FastMath
                .sqrt((cartCoords.x * cartCoords.x)
                        + (cartCoords.y * cartCoords.y)
                        + (cartCoords.z * cartCoords.z));
        store.y = FastMath.atan(cartCoords.z / cartCoords.x);
        if (cartCoords.x < 0)
            store.y += FastMath.PI;
        store.z = FastMath.asin(cartCoords.y / store.x);
        return store;
    }

    /**
     * Takes an value and expresses it in terms of min to max.
     * 
     * @param r -
     *            the angle to normalize (in radians)
     * @return the normalized angle (also in radians)
     */
    public static float normalize(float val, float min, float max) {
        if (Float.isInfinite(val) || Float.isNaN(val))
            return 0f;
        float range = max-min;
        while (val > max)
            val -= range;
        while (val < min)
            val += range;
        return val;
    }
}
