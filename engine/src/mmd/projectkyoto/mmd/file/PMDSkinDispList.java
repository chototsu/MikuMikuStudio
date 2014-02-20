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

package projectkyoto.mmd.file;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author kobayasi
 */
public class PMDSkinDispList implements Serializable{
    private int skinDispCount;
    private short skinIndexArray[];
    public PMDSkinDispList(DataInputStreamLittleEndian is) throws IOException {
        skinDispCount = is.readUnsignedByte();
        skinIndexArray = new short[skinDispCount];
        for(int i=0;i<skinDispCount;i++) {
            skinIndexArray[i] = is.readShort();
        }
    }
    public void writeToStream(DataOutput os) throws IOException {
        os.writeByte(skinDispCount);
        for(short s : skinIndexArray) {
            os.writeShort(s);
        }
    }

    @Override
    public String toString() {
        return "PMDSkinDispList{" + "skinDispCount=" + skinDispCount + ", skinIndexArray=" + skinIndexArray + '}';
    }

    public int getSkinDispCount() {
        return skinDispCount;
    }

    public void setSkinDispCount(int skinDispCount) {
        this.skinDispCount = skinDispCount;
    }

    public short[] getSkinIndexArray() {
        return skinIndexArray;
    }

    public void setSkinIndexArray(short[] skinIndexArray) {
        this.skinIndexArray = skinIndexArray;
    }
    
}
