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
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author kobayasi
 */
public class PMNSkinMesh {
    FloatBuffer pointBuffer;
    FloatBuffer normalBuffer;
    FloatBuffer texCoordBuffer;
    ShortBuffer boneIndexBuffer;
    FloatBuffer boneWeightBuffer;
    short[] boneIndexArray;
    int[] materialIndexArray;
    ShortBuffer[] indexBufferArray;
    public PMNSkinMesh() {
    }

    public short[] getBoneIndexArray() {
        return boneIndexArray;
    }

    public void setBoneIndexArray(short[] boneIndexArray) {
        this.boneIndexArray = boneIndexArray;
    }

    public ShortBuffer getBoneIndexBuffer() {
        return boneIndexBuffer;
    }

    public void setBoneIndexBuffer(ShortBuffer boneIndexBuffer) {
        this.boneIndexBuffer = boneIndexBuffer;
    }

    public FloatBuffer getBoneWeightBuffer() {
        return boneWeightBuffer;
    }

    public void setBoneWeightBuffer(FloatBuffer boneWeightBuffer) {
        this.boneWeightBuffer = boneWeightBuffer;
    }

    public ShortBuffer[] getIndexBufferArray() {
        return indexBufferArray;
    }

    public void setIndexBufferArray(ShortBuffer[] indexBufferArray) {
        this.indexBufferArray = indexBufferArray;
    }

    public int[] getMaterialIndexArray() {
        return materialIndexArray;
    }

    public void setMaterialIndexArray(int[] materialIndexArray) {
        this.materialIndexArray = materialIndexArray;
    }

    public FloatBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public void setNormalBuffer(FloatBuffer normalBuffer) {
        this.normalBuffer = normalBuffer;
    }

    public FloatBuffer getPointBuffer() {
        return pointBuffer;
    }

    public void setPointBuffer(FloatBuffer pointBuffer) {
        this.pointBuffer = pointBuffer;
    }

    public FloatBuffer getTexCoordBuffer() {
        return texCoordBuffer;
    }

    public void setTexCoordBuffer(FloatBuffer texCoordBuffer) {
        this.texCoordBuffer = texCoordBuffer;
    }
}
