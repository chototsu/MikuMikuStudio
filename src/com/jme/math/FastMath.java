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
 * @version $Id: FastMath.java,v 1.18 2005-09-20 19:33:37 renanse Exp $
 */

final public class FastMath {

    private FastMath(){}

    /** A "close to zero" double epsilon value for use*/
    public static final double DBL_EPSILON = 2.220446049250313E-16d;

    /** A "close to zero" float epsilon value for use*/
    public static final float FLT_EPSILON = 1.1920928955078125E-7f;

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

    /** If true, fast trig approximations are used for values such as sin/cos/tan. */
    public static boolean USE_FAST_TRIG = false;

    // A good implementation found on the Java boards.
    // note: a number is a power of two if and only if it is the smallest number
    //       with that number of significant bits. Therefore, if you subtract 1,
    //       you know that the new number will have fewer bits, so ANDing the original
    // number
    //       with anything less than it will give 0.

    /**
     * Returns true if the number is a power of 2 (2,4,8,16...)
     * @param number The number to test.
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
     * Returns cos of a value.  If USE_FAST_TRIG is enabled, an approximate value is returned.
     * Otherwise, a direct value is used.
     * @param fValue The value to cosine, in raidans.
     * @return The cosine of fValue.
     * @see java.lang.Math#cos(double)
     */
    public static float cos(float fValue) {
      if (USE_FAST_TRIG)
        return FastTrig.cos(fValue);
      else
        return (float) Math.cos((double) fValue);
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
     * Returns sine of a value.  If USE_FAST_TRIG is enabled, an approximate value is returned.
     * Otherwise, a direct value is used.
     * @param fValue The value to sine, in raidans.
     * @return The sine of fValue.
     * @see java.lang.Math#sin(double)
     */
    public static float sin(float fValue) {
      if (USE_FAST_TRIG)
        return FastTrig.sin(fValue);
      else
        return (float) Math.sin((double) fValue);
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
      if (USE_FAST_TRIG)
        return FastTrig.tan(fValue);
      else
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

    /** */
    public static float logGamma(float fX) {
        float afCoeff[] = { +76.18009173f, -86.50532033f, +24.01409822f,
                -1.231739516f, +(float) 0.120858003e-02,
                -(float) 0.536382000e-05};

        fX -= 1.0f;
        float fTmp = fX + 5.5f;
        fTmp -= (fX + 0.5f) * log(fTmp);
        float fSeries = 1.0f;
        for (int j = 0; j <= 5; j++) {
            fX += 1.0f;
            fSeries += afCoeff[j] / fX;
        }
        return -fTmp + log((2.50662827465f) * fSeries);
    }

    /** */
    public static float gamma(float fX) {
        return exp(logGamma(fX));
    }

    /** */
    public static float incompleteGammaS(float fA, float fX) {
        int iMaxIterations = 100;
        float fTolerance = (float) 3e-07;

        if (fX > 0.0f) {
            float fAp = fA;
            float fSum = (1.0f) / fA, fDel = fSum;
            for (int i = 1; i <= iMaxIterations; i++) {
                fAp += 1.0f;
                fDel *= fX / fAp;
                fSum += fDel;
                if (abs(fDel) < abs(fSum) * fTolerance) {
                    float fArg = -fX + fA * log(fX) - logGamma(fA);
                    return fSum * exp(fArg);
                }
            }
        }

        if (fX == 0.0f) return 0.0f;

        return Float.MAX_VALUE; // LogGamma not defined for x < 0
    }

    /** */
    public static float incompleteGammaCF(float fA, float fX) {
        int iMaxIterations = 100;
        float fTolerance = (float) 3e-07;

        float fA0 = 1.0f, fA1 = fX;
        float fB0 = 0, fB1 = 1.0f;
        float fGold = 0.0f, fFac = 1.0f;

        for (int i = 1; i <= iMaxIterations; i++) {
            float fI = (float) i;
            float fImA = fI - fA;
            fA0 = (fA1 + fA0 * fImA) * fFac;
            fB0 = (fB1 + fB0 * fImA) * fFac;
            float fItF = fI * fFac;
            fA1 = fX * fA0 + fItF * fA1;
            fB1 = fX * fB0 + fItF * fB1;
            if (fA1 != 0.0f) {
                fFac = (1.0f) / fA1;
                float fG = fB1 * fFac;
                if (abs((fG - fGold) / fG) < fTolerance) {
                    float fArg = -fX + fA * log(fX) - logGamma(fA);
                    return fG * exp(fArg);
                }
                fGold = fG;
            }
        }

        return Float.MAX_VALUE; // numerical error if you get here
    }

    /** */
    public static float incompleteGamma(float fA, float fX) {
        if (fX < 1.0f + fA)
            return incompleteGammaS(fA, fX);
        else
            return 1.0f - incompleteGammaCF(fA, fX);
    }

    /** */
    public static float erf(float fX) {
        return 1.0f - erfc(fX);
    }

    /** */
    public static float erfc(float fX) {
        float afCoeff[] = { -1.26551223f, +1.00002368f, +0.37409196f,
                +0.09678418f, -0.18628806f, +0.27886807f, -1.13520398f,
                +1.48851587f, -0.82215223f, +0.17087277f};

        float fZ = abs(fX);
        float fT = (1.0f) / (1.0f + (0.5f) * fZ);
        float fSum = afCoeff[9];

        for (int i = 9; i >= 0; i--)
            fSum = fT * fSum + afCoeff[i];

        float fResult = fT * exp(-fZ * fZ + fSum);

        return (fX >= 0.0f ? fResult : 2.0f - fResult);
    }

    /** */
    public static float modBessel0(float fX) {
        if (fX < 0.0f) // function is even
                fX = -fX;

        float fT, fResult;
        int i;

        if (fX <= 3.75f) {
            float afCoeff[] = { 1.0000000f, 3.5156229f, 3.0899424f, 1.2067492f,
                    0.2659732f, 0.0360768f, 0.0045813f};

            fT = fX / 3.75f;
            float fT2 = fT * fT;
            fResult = afCoeff[6];
            for (i = 5; i >= 0; i--) {
                fResult *= fT2;
                fResult += afCoeff[i];
            }
            // |error| < 1.6e-07
        } else {
            float afCoeff[] = { +0.39894228f, +0.01328592f, +0.00225319f,
                    -0.00157565f, +0.00916281f, -0.02057706f, +0.02635537f,
                    -0.01647633f, +0.00392377f};

            fT = fX / 3.75f;
            float fInvT = (1.0f) / fT;
            fResult = afCoeff[8];
            for (i = 7; i >= 0; i--) {
                fResult *= fInvT;
                fResult += afCoeff[i];
            }
            fResult *= exp(fX);
            fResult /= sqrt(fX);
            // |error| < 1.9e-07
        }

        return fResult;
    }

    /** */
    public static float modBessel1(float fX) {
        int iSign;
        if (fX > 0.0f) {
            iSign = 1;
        } else if (fX < 0.0f) {
            fX = -fX;
            iSign = -1;
        } else {
            return 0.0f;
        }

        float fT, fResult;
        int i;

        if (fX <= 3.75f) {
            float afCoeff[] = { 0.50000000f, 0.87890549f, 0.51498869f,
                    0.15084934f, 0.02658733f, 0.00301532f, 0.00032411f};

            fT = fX / 3.75f;
            float fT2 = fT * fT;
            fResult = afCoeff[6];
            for (i = 5; i >= 0; i--) {
                fResult *= fT2;
                fResult += afCoeff[i];
            }
            fResult *= fX;
            // |error| < 8e-09
        } else {
            float afCoeff[] = { +0.39894228f, -0.03988024f, -0.00362018f,
                    +0.00163801f, -0.01031555f, +0.02282967f, -0.02895312f,
                    +0.01787654f, -0.00420059f};

            fT = fX / 3.75f;
            float fInvT = (1.0f) / fT;
            fResult = afCoeff[8];
            for (i = 7; i >= 0; i--) {
                fResult *= fInvT;
                fResult += afCoeff[i];
            }
            fResult *= exp(fX);
            fResult /= sqrt(fX);
            // |error| < 2.2e-07
        }

        fResult *= iSign;
        return fResult;
    }

    /**
     * Given 3 points in a 2d plane, this function computes if the points going from A-B-C
     * are moving counter clock wise.
     * @param p0 Point 0.
     * @param p1 Point 1.
     * @param p2 Point 2.
     * @return 1 If they are CCW, -1 if they are not CCW, 0 if p2 is between p0 and p1.
     */
    public static int ccw(Vector2f p0,Vector2f p1,Vector2f p2){
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
        int val1=ccw(t0,t1,p);
        if (val1==0) return 1;
        int val2=ccw(t1,t2,p);
        if (val2==0) return 1;
        if (val2!=val1) return 0;
        int val3=ccw(t2,t0,p);
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
     * FastTrig is used to calculate quick trig functions using a lookup table.
     *
     * @author Erikd
     * @author Jack Lindamood (javadoc only)
     */
    static public class FastTrig {

        /** The size of the lookup table.  The bigger, the more accurate. */
        public static int PRECISION = 0x100000;

        private static float RAD_SLICE = TWO_PI / PRECISION, sinTable[] = null,
                tanTable[] = null;

        static {

            RAD_SLICE = TWO_PI / PRECISION;
            sinTable = new float[PRECISION];
            tanTable = new float[PRECISION];
            float rad = 0;

            for (int i = 0; i < PRECISION; i++) {
                rad = (float) i * RAD_SLICE;
                sinTable[i] = (float) java.lang.Math.sin(rad);
                tanTable[i] = (float) java.lang.Math.tan(rad);
            }
        }

        private static final int radToIndex(float radians) {
            return (int) ((radians / TWO_PI) * (float) PRECISION)
                    & (PRECISION - 1);
        }

        /**
         * Returns the sine of a given value, by looking up it's approximation in a
         * precomputed table.
         * @param radians The value to sine.
         * @return The approximation of the value's sine.
         */
        public static float sin(float radians) {
            return sinTable[radToIndex(radians)];
        }

        /**
         * Returns the cosine of a given value, by looking up it's approximation in a
         * precomputed table.
         * @param radians The value to cosine.
         * @return The approximation of the value's cosine.
         */
        public static float cos(float radians) {
            return sinTable[radToIndex(HALF_PI-radians)];
        }

        /**
         * Returns the tangent of a given value, by looking up it's approximation in a
         * precomputed table.
         * @param radians The value to tan.
         * @return The approximation of the value's tangent.
         */
        public static float tan(float radians) {
            return tanTable[radToIndex(radians)];
        }
    }
}
