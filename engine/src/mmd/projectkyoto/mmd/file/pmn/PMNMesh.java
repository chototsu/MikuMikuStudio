/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
