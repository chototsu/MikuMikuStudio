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
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author Kazuhiko Kobayashi
 */
public class PMDBone implements Serializable {

    private String boneName;
    private int parentBoneIndex;
    private int tailPosBoneIndex;
    private int boneType;   // 0:回転のみ 1:回転と移動 2:IK 3:不明 4:IK影響下 5:回転影響下
    //6:IK接続先 7:非表示 8:捻り 9:回転運動
    private int targetBone;
    private Vector3f boneHeadPos;
    private boolean hiza;

    @Override
    public String toString() {
        return "{boneName = " + boneName
                + " parentBoneIndex = " + parentBoneIndex
                + " tailPosBoneIndex = " + tailPosBoneIndex
                + " boneType = " + boneType
                + " targetBone = " + targetBone
                + " boneHeadPos = {" + (boneHeadPos == null ? "null" : "{"
                + boneHeadPos.x + " " + boneHeadPos.y + " " + boneHeadPos.z)
                + "}"
                + "}\n";
    }
    public PMDBone() {
    }
    public PMDBone(DataInputStreamLittleEndian is) throws IOException {
        readFromStream(is);
    }
    public PMDBone readFromStream(DataInputStreamLittleEndian is) throws IOException {
        boneName = is.readString(20);
        parentBoneIndex = is.readUnsignedShort();
        tailPosBoneIndex = is.readUnsignedShort();
        boneType = is.readByte();
        targetBone = is.readShort();
        boneHeadPos = new Vector3f(is.readFloat(), is.readFloat(),
                -is.readFloat());
        if (boneName.indexOf("ひざ") >= 0) {
            hiza = true;
        } else {
            hiza = false;
        }
        return this;
    }
    public void writeToStream(DataOutput os) throws IOException {
        PMDUtil.writeString(os, boneName, 20);
        os.writeShort(parentBoneIndex);
        os.writeShort(tailPosBoneIndex);
        os.writeByte(boneType);
        os.writeShort(targetBone);
        os.writeFloat(boneHeadPos.x);
        os.writeFloat(boneHeadPos.y);
        os.writeFloat(-boneHeadPos.z);
    }

    public void readFromBuffer(ByteBuffer bb) {
        boneName = BufferUtil.readString(bb, 20);
        parentBoneIndex = bb.getShort();
        tailPosBoneIndex = bb.getShort();
        boneType = bb.get();
        targetBone = bb.getShort();
        boneHeadPos = new Vector3f(bb.getFloat(), bb.getFloat(), bb.getFloat());
        if (boneName.indexOf("ひざ") >= 0) {
            hiza = true;
        } else {
            hiza = false;
        }
    }

    public void writeToBuffer(ByteBuffer bb) {
        BufferUtil.writeString(bb, boneName, 20);
        bb.putShort((short) parentBoneIndex);
        bb.putShort((short) tailPosBoneIndex);
        bb.put((byte) boneType);
        bb.putShort((short) targetBone);
        bb.putFloat(boneHeadPos.x).putFloat(boneHeadPos.y).putFloat(boneHeadPos.z);
    }

    public Vector3f getBoneHeadPos() {
        return boneHeadPos;
    }

    public void setBoneHeadPos(Vector3f boneHeadPos) {
        this.boneHeadPos = boneHeadPos;
    }

    public String getBoneName() {
        return boneName;
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public int getBoneType() {
        return boneType;
    }

    public void setBoneType(int boneType) {
        this.boneType = boneType;
    }

    public int getTargetBone() {
        return targetBone;
    }

    public void setTargetBone(int targetBone) {
        this.targetBone = targetBone;
    }

    public int getParentBoneIndex() {
        return parentBoneIndex;
    }

    public void setParentBoneIndex(int parentBoneIndex) {
        this.parentBoneIndex = parentBoneIndex;
    }

    public int getTailPosBoneIndex() {
        return tailPosBoneIndex;
    }

    public void setTailPosBoneIndex(int tailPosBoneIndex) {
        this.tailPosBoneIndex = tailPosBoneIndex;
    }

    public boolean isHiza() {
        return hiza;
    }

    public void setHiza(boolean hiza) {
        this.hiza = hiza;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PMDBone other = (PMDBone) obj;
        if ((this.boneName == null) ? (other.boneName != null) : !this.boneName.equals(other.boneName)) {
            return false;
        }
        if (this.parentBoneIndex != other.parentBoneIndex) {
            return false;
        }
        if (this.tailPosBoneIndex != other.tailPosBoneIndex) {
            return false;
        }
        if (this.boneType != other.boneType) {
            return false;
        }
        if (this.targetBone != other.targetBone) {
            return false;
        }
        if (this.boneHeadPos != other.boneHeadPos && (this.boneHeadPos == null || !this.boneHeadPos.equals(other.boneHeadPos))) {
            return false;
        }
        if (this.hiza != other.hiza) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.boneName != null ? this.boneName.hashCode() : 0);
        return hash;
    }
    
}
