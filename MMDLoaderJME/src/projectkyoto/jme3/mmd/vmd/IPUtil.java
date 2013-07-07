/*  Copyright (c) 2009-2011  Nagoya Institute of Technology          */
/*                           Department of Computer Science          */
/*                2011       Kazuhiko Kobayashi                      */
/*                                                                   */
/* All rights reserved.                                              */
/*                                                                   */
/* Redistribution and use in source and binary forms, with or        */
/* without modification, are permitted provided that the following   */
/* conditions are met:                                               */
/*                                                                   */
/* - Redistributions of source code must retain the above copyright  */
/*   notice, this list of conditions and the following disclaimer.   */
/* - Redistributions in binary form must reproduce the above         */
/*   copyright notice, this list of conditions and the following     */
/*   disclaimer in the documentation and/or other materials provided */
/*   with the distribution.                                          */
/* - Neither the name of the MMDLoaderJME3 project team nor the names of  */
/*   its contributors may be used to endorse or promote products     */
/*   derived from this software without specific prior written       */
/*   permission.                                                     */
/*                                                                   */
/* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND            */
/* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,       */
/* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF          */
/* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE          */
/* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS */
/* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,          */
/* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED   */
/* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,     */
/* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON */
/* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,   */
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY    */
/* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE           */
/* POSSIBILITY OF SUCH DAMAGE.                                       */
/* ----------------------------------------------------------------- */

package projectkyoto.jme3.mmd.vmd;

import com.jme3.math.FastMath;

/**
 *
 * @author kobayasi
 */
public class IPUtil {
    static float ipfunc(float t, float p1, float p2) {
        return ((1 + 3 * p1 - 3 * p2) * t * t * t + (3 * p2 - 6 * p1) * t * t + 3 * p1 * t);
    }

    /* ipfuncd: derivation of ipfunc */
    static float ipfuncd(float t, float p1, float p2) {
        return ((3 + 9 * p1 - 9 * p2) * t * t + (6 * p2 - 12 * p1) * t + 3 * p1);
    }
    /* VMD::setInterpolationTable: set up motion interpolation parameter */

    static float calcIp(BoneMotionList bml, float x, int offset) {
        if (x <= 0) {
            return 0f;
        }
        if (x >=1) {
            return 1f;
        }
        int i = (int)((float)BoneMotionList.IPTABLESIZE * x);
        if (i >= BoneMotionList.IPTABLESIZE) {
            return 1f;
        }
        float f1,f2;
        f1 = bml.ipTable[offset][i];
        if (i < BoneMotionList.IPTABLESIZE-1) {
            f2 = bml.ipTable[offset][i+1];
        } else {
            f2 = 1f;
        }
        return f1 + (f2 - f1) * (x * (float)BoneMotionList.IPTABLESIZE - (float)i);
    }
    static float calcIp(byte ip[], float x, int offset) {
        short i, d;
        float x1, x2, y1, y2;
        float inval, t, v, tt;

        /*
         * check if they are just a linear function
         */
        if (ip[0 + offset] == ip[4 + offset] && ip[8 + offset] == ip[12 + offset]) {
            // linear
//            return x;
        }

        /*
         * xの近似解を求める。
         */
        //for (i = 0; i < 4; i++) {
        x1 = (float) ip[offset] / 127.0f;
        y1 = (float) ip[ 4 + offset] / 127.0f;
        x2 = (float) ip[ 8 + offset] / 127.0f;
        y2 = (float) ip[12 + offset] / 127.0f;
        // for (d = 0; d < kInterpolationTableSize; d++) {
        inval = x; //((float) d + 0.5f) / (float) kInterpolationTableSize;
         /*
         * get Y value for given inval
         */
        t = inval;
        for (int i2 = 0; i2 < 1000; i2++) {
            v = ipfunc(t, x1, x2) - inval;
            if (Math.abs(v) < 0.0001f) {
                break;
            }
            tt = ipfuncd(t, x1, x2);
            if (tt == 0.0f) {
                break;
            }
            t -= v / tt;
        }
        return ipfunc(t, y1, y2);
    }

    static void createInterpolationTable(byte ip[], int ipTableSize, float ipTable[][]) {
        short i, d;
        float x1, x2, y1, y2;
        float inval, t, v, tt;

        /*
         * check if they are just a linear function
         */
        for (i = 0; i < 4; i++) {
            if (ip[0 + i] == ip[4 + i] && ip[8 + i] == ip[12 + i]) {
                // linear
                for (d = 0; d < ipTableSize; d++) {
                    ipTable[i][d] = (float) d / ipTableSize;
                }
            } else {
                x1 = ip[   i] / 127.0f;
                y1 = ip[ 4 + i] / 127.0f;
                x2 = ip[ 8 + i] / 127.0f;
                y2 = ip[12 + i] / 127.0f;
                for (d = 0; d < ipTableSize; d++) {
                    inval = ((float) d + 0.5f) / (float) ipTableSize;
                    /*
                     * get Y value for given inval
                     */
                    t = inval;
                    for (int i2=0;i2<1000;i2++) {
                        v = ipfunc(t, x1, x2) - inval;
                        if (FastMath.abs(v) < 0.0001f) {
                            break;
                        }
                        tt = ipfuncd(t, x1, x2);
                        if (tt == 0.0f) {
                            break;
                        }
                        t -= v / tt;
                    }
                    ipTable[i][d] = ipfunc(t, y1, y2);
                }
            }
        }
    }
}
