/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding All rights
 * reserved.
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

package com.jme.math;

import java.util.Random;

/**
 * <code>FastMath</code>
 *
 * @author Various
 * @version $Id: FastMath.java,v 1.10 2004-05-01 03:48:47 renanse Exp $
 */

public class FastMath {

    public static final double DBL_EPSILON = 2.220446049250313E-16d;

    public static final float FLT_EPSILON = 1.1920928955078125E-7f;

    public static final float PI = (float) (4.0 * atan(1.0f));

    public static final float TWO_PI = 2.0f * PI;

    public static final float HALF_PI = 0.5f * PI;

    public static final float INV_PI = 1.0f / PI;

    public static final float INV_TWO_PI = 1.0f / PI;

    public static final float DEG_TO_RAD = PI / 180.0f;

    public static final float RAD_TO_DEG = 180.0f / PI;

    public static final Random rand = new Random();

    // A good implementation found on the Java boards.
    // note: a number is a power of two if and only if it is the smallest number
    //       with that number of significant bits. Therefore, if you subtract 1,
    //       you know that the new number will have fewer bits, so ANDing the original
    // number
    //       with anything less than it will give 0.
    public static boolean isPowerOfTwo(int number) {
        return (number > 0) && (number & (number - 1)) == 0;
    }

    public static float LERP(float f, float v0, float v1) {
      return ( (1 - (f)) * (v0) + (f) * (v1));
    }


    /** */
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

    /** */
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

    /** */
    public static float atan(float fValue) {
        return (float) Math.atan((double) fValue);
    }

    /** */
    public static float atan2(float fY, float fX) {
        return (float) Math.atan2((double) fY, (double) fX);
    }

    /** */
    public static float ceil(float fValue) {
        return (float) Math.ceil((double) fValue);
    }

    /** */
    public static float cos(float fValue) {
        return (float) Math.cos((double) fValue);
    }

    /** */
    public static float exp(float fValue) {
        return (float) Math.exp((double) fValue);
    }

    /** */
    public static float abs(float fValue) {
        return (float) Math.abs(fValue);
    }

    /** */
    public static float floor(float fValue) {
        return (float) Math.floor((double) fValue);
    }

    /** */
    public static float invSqrt(float fValue) {
        return (float) (1.0 / Math.sqrt((double) fValue));
    }

    /** */
    public static float log(float fValue) {
        return (float) Math.log((double) fValue);
    }

    /** */
    public static float pow(float fBase, float fExponent) {
        return (float) Math.pow((double) fBase, (double) fExponent);
    }

    /** */
    public static float sin(float fValue) {
        return (float) Math.sin((double) fValue);
    }

    /** */
    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    /** */
    public static float sqrt(float fValue) {
        return (float) Math.sqrt((double) fValue);
    }

    /** */
    public static float tan(float fValue) {
        return (float) Math.tan((double) fValue);
    }

    /** */
    public static int sign(int iValue) {
        if (iValue > 0) return 1;

        if (iValue < 0) return -1;

        return 0;
    }

    /** */
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

    public static float nextRandomFloat() {
        return rand.nextFloat();
    }

    /**
     * FastTrig
     *
     * @Author Erikd
     */
    static public class FastTrig {

        public static int PRECISION = 0x100000;

        private static float RAD_SLICE = TWO_PI / PRECISION, sinTable[] = null,
                cosTable[] = null, tanTable[] = null;

        static {

            RAD_SLICE = TWO_PI / PRECISION;
            sinTable = new float[PRECISION];
            cosTable = new float[PRECISION];
            tanTable = new float[PRECISION];
            float rad = 0;

            for (int i = 0; i < PRECISION; i++) {
                rad = (float) i * RAD_SLICE;
                sinTable[i] = (float) java.lang.Math.sin(rad);
                cosTable[i] = (float) java.lang.Math.cos(rad);
                tanTable[i] = (float) java.lang.Math.tan(rad);
            }
        }

        private static final int radToIndex(float radians) {
            return (int) ((radians / TWO_PI) * (float) PRECISION)
                    & (PRECISION - 1);
        }

        public static float sin(float radians) {
            return sinTable[radToIndex(radians)];
        }

        public static float cos(float radians) {
            return cosTable[radToIndex(radians)];
        }

        public static float tan(float radians) {
            return tanTable[radToIndex(radians)];
        }
    }
}
