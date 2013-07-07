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

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import projectkyoto.mmd.file.util2.BufferUtil;

/**
 *
 * @author kobayasi
 */
public class VMDMotion implements Serializable{
    protected VMDFile vmdFile;
    private String boneName; // char[15]
    private short boneIndex;
    private int frameNo;
    private Point3f location;
    private Quat4f rotation;
    private byte[] interpolation = new byte[64];
    public VMDMotion() {
        location = new Point3f();
        rotation = new Quat4f();
    }
    public VMDMotion(VMDFile vmdFile, DataInputStreamLittleEndian is) throws IOException {
        this.vmdFile = vmdFile;
        readFromStream(is);
    }
    public final void readFromStream(DataInputStreamLittleEndian is) throws IOException {
        boneName = is.readString(15);
        boneIndex = -1;
//        for(int i=0;i<vmdFile.boneNames.size();i++) {
//            if (boneName.equals(vmdFile.boneNames.get(i))) {
//                boneIndex = (short)i;
//                break;
//            }
//        }
        boneIndex = (short)vmdFile.boneNames.indexOf(boneName);
        if (boneIndex < 0) {
            vmdFile.boneNames.add(boneName);
//            boneIndex = (short)(vmdFile.boneNames.size() - 1);
            boneIndex = (short)vmdFile.boneNames.indexOf(boneName);
        }
        frameNo = is.readInt();
        location = new Point3f();
        location.x = is.readFloat();
        location.y = is.readFloat();
        location.z = -is.readFloat();
        rotation = new Quat4f(is.readFloat(), is.readFloat(), -is.readFloat(), -is.readFloat());
        int pos = 0;
        while(pos < 64) {
            pos += is.read(interpolation, pos, 64 - pos);
        }
    }
    public VMDMotion readFromBuffer(ByteBuffer bb) {
//        boneName = BufferUtil.readString(bb, 15);
        boneIndex = bb.getShort();
        frameNo = bb.getInt();
        BufferUtil.readPoint3f(bb, location);
        BufferUtil.readQuat4f(bb, rotation);
        bb.get(interpolation);
        return this;
    }
    public VMDMotion writeToBuffer(ByteBuffer bb) {
        int startPos = bb.position();
//        BufferUtil.writeString(bb, boneName, 15);
        bb.putShort(boneIndex);
        bb.putInt(frameNo);
        BufferUtil.writePoint3f(bb, location);
        BufferUtil.writeQuat4f(bb, rotation);
        bb.put(interpolation);
        int endPos = bb.position();
        if (endPos - startPos != 98) {
            throw new RuntimeException("size = "+(endPos - startPos));
        }
        return this;
    }
    public VMDMotion set(VMDMotion m) {
        boneIndex = m.boneIndex;
        frameNo = m.frameNo;
        location.set(m.location);
        rotation.set(m.rotation);
        System.arraycopy(m.interpolation, 0, interpolation, 0, 64);
        vmdFile = m.vmdFile;
        return m;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{boneName = " + boneName
                + " frameNo = " + frameNo
                + " location = " + location
                + " rotation = " + rotation
                + " interpolation = {");
        for (int i = 0; i < 64; i++) {
            sb.append(interpolation[i]).append(',');
        }
        sb.append("}}\n");

        return sb.toString();
    }

    public String getBoneName() {
        return vmdFile.boneNames.get(boneIndex);
    }

    public void setBoneName(String boneName) {
        this.boneName = boneName;
    }

    public int getFrameNo() {
        return frameNo;
    }

    public void setFlameNo(int frameNo) {
        this.frameNo = frameNo;
    }

    public byte[] getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(byte[] interpolation) {
        this.interpolation = interpolation;
    }

    public Point3f getLocation() {
        return location;
    }

    public void setLocation(Point3f location) {
        this.location = location;
    }

    public Quat4f getRotation() {
        return rotation;
    }

    public void setRotation(Quat4f rotation) {
        this.rotation = rotation;
    }
    
}
