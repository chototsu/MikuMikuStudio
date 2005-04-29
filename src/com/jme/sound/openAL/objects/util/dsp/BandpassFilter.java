/*
 * Copyright (c) 2003-2004, jMonkeyEngine - Mojo Monkey Coding
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
/**
 * Created on Apr 28, 2005
 */
package com.jme.sound.openAL.objects.util.dsp;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.ShortBuffer;

public class BandpassFilter extends Filter {

    private float qParam = 1.4f;

    private double alpha;

    private double beta;

    private double gamma;

    public BandpassFilter(int frequency, int rate) {
        super(frequency, rate);
        resetABC(qParam);
    }

    public void resetABC(double q) {
        double tan = Math.tan(theta / (2.0 * q));
        beta = 0.5 * ((1.0 - tan) / (1.0 + tan));
        alpha = (0.5 - beta) / 2.0;
        gamma = (0.5 + beta) * Math.cos(theta);
    }

    public byte[] filter(byte[] input) {
        ShortBuffer buffer = ByteBuffer.wrap(input).asShortBuffer();
        double[] d=new double[buffer.capacity()];
        for(int a=0; a<d.length; a++){
            d[a]=(double)buffer.get(a);
        }
        DoubleBuffer outputBuffer = DoubleBuffer.wrap(d);
        double[] inputArray = new double[3];
        double[] outputArray = new double[3];
        int i = 0, j = 0, k = 0;
        for (int a = 0; a < buffer.capacity(); a++) {
            // Fetch sample
            inputArray[i] = (double) buffer.get(a);
            
            // Do indices maintainance
            j = i - 2;
            if (j < 0)
                j += 3;
            k = i - 1;
            if (k < 0)
                k += 3;
            
            // Run the difference equation
            double out = outputArray[i] = 2 *(alpha * (inputArray[i] - inputArray[j]) 
                    + gamma * outputArray[k] 
                                          - beta * outputArray[j]);
            double val = outputBuffer.get(a);
            val += adjust * out;
            outputBuffer.put(a, val);
            i = (i + 1) % 3;
        }
        double[] darray = outputBuffer.array();
        short[] outShort = new short[darray.length];
        for (int a = 0; a < outputBuffer.capacity(); a++) {
            double dSample = darray[a];
            if (dSample > 32767.0)
                dSample = 32767.0;
            else if (dSample < -32768.0)
                dSample = -32768.0;
            // Convert sample and store
            outShort[a] = (short) dSample;
        }
        return toByte(outShort, false);
    }
    
    
    public byte[] toByte(short[] array, boolean flag){
        byte[] outBuf=new byte[array.length*2];
        for(int a=0, b=0; a<array.length; a++, b+=2){
            byte[] ret=toByte(array[a], flag);
            outBuf[b]=ret[0];
            outBuf[b+1]=ret[1];
        }
        return outBuf;
    }
    

    public static final byte[] toByte(short value, boolean flag) {
        byte abyte0[] = new byte[2];
        for (byte byte0 = 0; byte0 <= 1; byte0++)
            abyte0[byte0] = (byte) (value >>> (1 - byte0) * 8);

        if (flag)
            abyte0 = reverse_order(abyte0, 2);
        return abyte0;
    }

    public static final byte[] toByte(short word0) {
        return toByte(word0, false);
    }

    private static final byte[] reverse_order(byte array[], int i) {
        byte abyte1[] = new byte[i];
        for (byte byte0 = 0; byte0 <= i - 1; byte0++)
            abyte1[byte0] = array[i - 1 - byte0];

        return abyte1;
    }

}