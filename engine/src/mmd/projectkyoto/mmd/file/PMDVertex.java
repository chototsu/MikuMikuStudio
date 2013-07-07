/*
 * Copyright (c) 2011 Kazuhiko Kobayashi
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
 * * Neither the name of 'MMDLoaderJME' nor the names of its contributors
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

package projectkyoto.mmd.file;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import javax.vecmath.Vector3f;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDVertex implements Serializable{

    private Vector3f pos; // 位置
    private Vector3f normal; // 法線
    private Coords2d uv; // uv座標
    private int boneNum1; // Bone番号1
    private int boneNum2; // Bone番号2
    private byte boneWeight; // Bone重み係数
    private byte edgeFlag; // 0:通常 1:エッジ無効

    @Override
    public String toString() {
        return "{pos = "+pos
                + " normal = "+normal
                + " uv = "+ uv
                + " boneNum1 = "+boneNum1
                + " boneNum2 = "+boneNum2
                +" boneWeight = "+boneWeight
                +" edgeFlag = "+edgeFlag
                +"}";
    }


    public PMDVertex() {
        pos = new Vector3f();
        normal = new Vector3f();
        uv = new Coords2d();
    }
    public PMDVertex readFromStream(DataInputStreamLittleEndian is) throws IOException {
        PMDUtil.readVector3f(is, pos);
        PMDUtil.readVector3f(is, normal);
        uv.readFromStream(is);
        boneNum1 = is.readUnsignedShort();
        boneNum2 = is.readUnsignedShort();
        boneWeight = is.readByte();
        edgeFlag = is.readByte();
        return this;
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeVector3f(os, pos);
        PMDUtil.writeVector3f(os, normal);
        uv.writeToStream(os);
        os.writeShort(boneNum1);
        os.writeShort(boneNum2);
        os.writeByte(boneWeight);
        os.writeByte(edgeFlag);
    }    
    public PMDVertex readFromBuffer(ByteBuffer bb) {
        PMDUtil.readVector3f(bb, pos);
        PMDUtil.readVector3f(bb, normal);
        uv.readFromBuffer(bb);
        boneNum1 = bb.getShort();
        boneNum2 = bb.getShort();
        boneWeight = bb.get();
        edgeFlag = bb.get();
        return this;
    }
    public PMDVertex writeToBuffer(ByteBuffer bb) {
        PMDUtil.writeVector3f(bb, pos);
        PMDUtil.writeVector3f(bb, normal);
        uv.writeToBuffer(bb);
        bb.putShort((short)boneNum1);
        bb.putShort((short)boneNum2);
        bb.put(boneWeight);
        bb.put(edgeFlag);
        return this;
    }

    public int getBoneNum1() {
        return boneNum1;
    }

    public void setBoneNum1(int boneNum1) {
        this.boneNum1 = boneNum1;
    }

    public int getBoneNum2() {
        return boneNum2;
    }

    public void setBoneNum2(int boneNum2) {
        this.boneNum2 = boneNum2;
    }


    public byte getBoneWeight() {
        return boneWeight;
    }

    public void setBoneWeight(byte boneWeight) {
        this.boneWeight = boneWeight;
    }

    public byte getEdgeFlag() {
        return edgeFlag;
    }

    public void setEdgeFlag(byte edgeFlag) {
        this.edgeFlag = edgeFlag;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Coords2d getUv() {
        return uv;
    }

    public void setUv(Coords2d uv) {
        this.uv = uv;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PMDVertex other = (PMDVertex) obj;
        if (this.pos != other.pos && (this.pos == null || !this.pos.equals(other.pos))) {
            return false;
        }
        if (this.normal != other.normal && (this.normal == null || !this.normal.equals(other.normal))) {
            return false;
        }
        if (this.uv != other.uv && (this.uv == null || !this.uv.equals(other.uv))) {
            return false;
        }
        if (this.boneNum1 != other.boneNum1) {
            return false;
        }
        if (this.boneNum2 != other.boneNum2) {
            return false;
        }
        if (this.boneWeight != other.boneWeight) {
            return false;
        }
        if (this.edgeFlag != other.edgeFlag) {
            return false;
        }
        return true;
    }

    public boolean equals2(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final PMDVertex other = (PMDVertex) obj;
//        if (this.pos != other.pos && (this.pos == null || !this.pos.equals(other.pos))) {
//            return false;
//        }
//        if (this.normal != other.normal && (this.normal == null || !this.normal.equals(other.normal))) {
//            return false;
//        }
//        if (this.uv != other.uv && (this.uv == null || !this.uv.equals(other.uv))) {
//            return false;
//        }
//        if (this.boneNum1 != other.boneNum1) {
//            return false;
//        }
//        if (this.boneNum2 != other.boneNum2) {
//            return false;
//        }
//        if (this.boneWeight != other.boneWeight) {
//            return false;
//        }
//        if (this.edgeFlag != other.edgeFlag) {
//            return false;
//        }
//        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        return hash;
    }
    public static int size() {
        return 38;
    }
}
