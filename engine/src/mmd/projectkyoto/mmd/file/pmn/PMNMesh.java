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

package projectkyoto.mmd.file.pmn;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kobayasi
 */
public class PMNMesh {
    ByteBuffer interleavedBuffer;
    ShortBuffer indexBuffer;
    /**
     * 0 point
     * 1 normal
     * 2 texcoord
     * 3 boneIndex
     * 4 boneWeight
     */
    byte[] offsetArray;
    int stride;
    int materialIndex;
    short[] boneIndexArray;
    public PMNMesh() {
    }

    public short[] getBoneIndexArray() {
        return boneIndexArray;
    }

    public void setBoneIndexArray(short[] boneIndexArray) {
        this.boneIndexArray = boneIndexArray;
    }

    public ShortBuffer getIndexBuffer() {
        return indexBuffer;
    }

    public void setIndexBuffer(ShortBuffer indexBuffer) {
        this.indexBuffer = indexBuffer;
    }

    public ByteBuffer getInterleavedBuffer() {
        return interleavedBuffer;
    }

    public void setInterleavedBuffer(ByteBuffer interleavedBuffer) {
        this.interleavedBuffer = interleavedBuffer;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public byte[] getOffsetArray() {
        return offsetArray;
    }

    public void setOffsetArray(byte[] offsetArray) {
        this.offsetArray = offsetArray;
    }

    public int getStride() {
        return stride;
    }

    public void setStride(int stride) {
        this.stride = stride;
    }
    
}
