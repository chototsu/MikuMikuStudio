/*
 * Copyright (c) 2010-2014, Kazuhiko Kobayashi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.asset.gdx;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author kobayasi
 */
public class MMSFilterInputStream extends FilterInputStream {

    int size;

    public MMSFilterInputStream(InputStream in, int size) {
        super(in);
        this.size = size;
    }

    @Override
    public int available() throws IOException {
        return size;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public synchronized void mark(int i) {
        super.mark(i);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        if (size == 0) {
            return -1;
        }
        int i = super.read();
        if (i >= 0) {
            size--;
        }
        return i;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        int i = read(bytes, 0, bytes.length);
        return i;
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        if (size == 0) {
            return -1;
        }
        int readSize = 0;
        if (i1 > size) {
            i1 = size;
        }
        while(size > 0 && readSize != i1) {
            int i2 = super.read(bytes, i+readSize, i1 - readSize);
            if (i2 >= 0) {
                size = size - i2;
                readSize += i2;
            } else {
                break;
            }
        }
        return readSize;
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
    }

    @Override
    public long skip(long n) throws IOException {
//        long totalBytesSkipped = 0L;
//        while (totalBytesSkipped < n && size > 0) {
//            long bytesSkipped = in.skip(n - totalBytesSkipped);
//            size = size - (int) bytesSkipped;
//            if (bytesSkipped == 0L) {
//                int b = read();
//                if (b < 0) {
//                    break;  // we reached EOF
//                } else {
//                    bytesSkipped = 1; // we read one byte
////                    size = size - 1;
//                }
//            }
//            totalBytesSkipped += bytesSkipped;
//        }
        for(long i = 0;i<n;i++) {
            if (read() < 0) {
                return i-1;
            }
        }
        return n;
    }
}

