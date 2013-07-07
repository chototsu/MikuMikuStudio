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
/* - Neither the name of the MMDLoaderJME project team nor the names of  */
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

    static float calcIp(final VMDControl.BoneMotionList bml, float x, int offset) {
        if (x <= 0) {
            return 0f;
        }
        if (x >=1) {
            return 1f;
        }
        final float ipTable[][][] = bml.ipTable;
        int ipTableSize = ipTable[offset].length;
        for(int i=bml.ipTableIndex;i<ipTableSize;i++) {
            if (bml.ipTable[offset][i][0] == x) {
                bml.ipTableIndex = i;
                return bml.ipTable[offset][i][1];
            }
            if (ipTable[offset][i][0] > x) {
                float x1,x2,y1,y2;
                if (i == 0) {
                    x1 = 0;
                    y1 = 0;
                } else {
                    x1 = ipTable[offset][i-1][0];
                    y1 = ipTable[offset][i-1][1];
                }
                x2 = ipTable[offset][i][0];
                y2 = ipTable[offset][i][1];
                bml.ipTableIndex = i;
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        bml.ipTableIndex = ipTableSize;
        return 1f;
    }
    static void createInterpolationTable(final byte ip[], final float ipTable[][][]) {
        int i, d;
        float x1, x2, y1, y2;
        float inval, t, v, tt;
        /*
         * check if they are just a linear function
         */
        for (i = 0; i < 4; i++) {
            int ipTableSize = ipTable[i].length;
            if (ip[0 + i] == ip[4 + i] && ip[8 + i] == ip[12 + i]) {
                // linear
                for (d = 0; d < ipTableSize; d++) {
                    ipTable[i][d][0] = (float) d / ipTableSize;
                    ipTable[i][d][1] = (float) d / ipTableSize;
                }
            } else {
                x1 = ip[   i] / 127.0f;
                y1 = ip[ 4 + i] / 127.0f;
                x2 = ip[ 8 + i] / 127.0f;
                y2 = ip[12 + i] / 127.0f;
                for (d = 0; d < ipTableSize; d++) {
                    inval = ((float) d ) / (float) ipTableSize;
                    /*
                     * get Y value for given inval
                     */
                    t = inval;
                    ipTable[i][d][0] = ipfunc(t, x1, x2);
                    ipTable[i][d][1] = ipfunc(t, y1, y2);
                }
            }
        }
    }

}
