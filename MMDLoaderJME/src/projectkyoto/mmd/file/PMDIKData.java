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

/**
 *
 * @author kobayasi
 */
public class PMDIKData implements Serializable {

    private int ikBoneIndex;
    private int ikTargetBoneIndex;
    private int ikChainLength;
    private int iterations;
    private float controlWeight;
    private int[] ikChildBoneIndex;

    public PMDIKData(DataInputStreamLittleEndian is) throws IOException {
        ikBoneIndex = is.readUnsignedShort();
        ikTargetBoneIndex = is.readUnsignedShort();
        ikChainLength = is.readByte() & 0xff;
        iterations = is.readShort();
        controlWeight = is.readFloat();
        ikChildBoneIndex = new int[ikChainLength];
        for (int i = 0; i < ikChainLength; i++) {
            ikChildBoneIndex[i] = is.readUnsignedShort();
        }
    }

    public void writeToStream(DataOutput os) throws IOException {
        os.writeShort(ikBoneIndex);
        os.writeShort(ikTargetBoneIndex);
        os.writeByte(ikChainLength);
        os.writeShort(iterations);
        os.writeFloat(controlWeight);
        for (int s : ikChildBoneIndex) {
            os.writeShort(s);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{ikBoneIndex = " + ikBoneIndex);
        sb.append("\n").append("ikTargetBoneIndex = " + ikTargetBoneIndex);
        sb.append(" ikChainLength = " + ikChainLength);
        sb.append(" \niterations = " + iterations);
        sb.append("\ncontrolWeight = " + controlWeight);
        sb.append("\n{");
        for (int i = 0; i < ikChainLength; i++) {
            sb.append("ikChildBoneIndex = " + ikChildBoneIndex[i]);
        }
        sb.append("}");
        return sb.toString();
    }

    public float getControlWeight() {
        return controlWeight;
    }

    public void setControlWeight(float controlWeight) {
        this.controlWeight = controlWeight;
    }

    public int getIkBoneIndex() {
        return ikBoneIndex;
    }

    public void setIkBoneIndex(int ikBoneIndex) {
        this.ikBoneIndex = ikBoneIndex;
    }

    public int getIkChainLength() {
        return ikChainLength;
    }

    public void setIkChainLength(int ikChainLength) {
        this.ikChainLength = ikChainLength;
    }

    public int[] getIkChildBoneIndex() {
        return ikChildBoneIndex;
    }

    public void setIkChildBoneIndex(int[] ikChildBoneIndex) {
        this.ikChildBoneIndex = ikChildBoneIndex;
    }

    public int getIkTargetBoneIndex() {
        return ikTargetBoneIndex;
    }

    public void setIkTargetBoneIndex(int ikTargetBoneIndex) {
        this.ikTargetBoneIndex = ikTargetBoneIndex;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
