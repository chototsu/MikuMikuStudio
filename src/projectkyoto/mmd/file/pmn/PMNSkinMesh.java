/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
